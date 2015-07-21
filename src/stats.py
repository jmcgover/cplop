#!/usr/bin/python

from astropy.io import ascii
from astropy.table import Table, Column
import matplotlib
from matplotlib import pyplot
import numpy
import pandas
import sys
from prettytable import PrettyTable

def print_usage(msg=None):
    exit_val = 0
    if msg:
        print(msg)
        exit_val = 22
    print("usage: " + sys.argv[0] + " <k> <alpha> <species> ...")
    sys.exit(exit_val)

def atpy_csv(filename):
    data = ascii.read(filename)
    return data
def filter_by_alpha(data, key, value):
    print("Filtering by %s == %s" % (key, str(value)))
    new_data = Table(data[0:0])
    for d in data:
        if d[key] == value:
            new_data.add_row(d)
    return new_data

def get_species_names(data):
    all_names = numpy.empty([0,0])
    for name in data.colnames:
        if name != 'total' and name != 'attempts' and name != 'k' and name != 'alpha' and name != 'species':
            all_names = numpy.append(all_names, name)
    return all_names

def calc_f_measure(recall, precision):
    return 2 / (1 / recall + 1 / precision)

def calc_recall(data, k, alpha, species):
    print("%s recall" % species)
    correct = 0
    total = 0
    for d in data:
        if d['k'] == k and d['alpha'] == alpha and d['species'] == species: 
            correct = d[species]
            total   = d['total']
            isols   = d['attempts']
            return (correct, total, isols)
    return None

def calc_precision(data, k, alpha, species):
    print("%s precision" % species)
    correct = -1
    total = -1
    for d in data:
        if d['k'] == k and d['alpha'] == alpha:
            if d['species'] == species:
                correct = d[species]
            if d['species'] == 'Overall':
                total = d[species]
            if total >=0 and correct >= 0:
                return (correct, total)
    return None

def overall_recall(data, k, alpha):
    print("overall recall")
    correct = 0
    total = 0
    isols = None
    species_seen = False
    overall_seen = False
    for species in get_species_names(data):
        species_seen = False
        for d in data:
            if d['k'] == k and d['alpha'] == alpha:
                if d['species'] == species: 
#                    print(d['k'], d['alpha'], d['species'], d['attempts'])
                    correct += d[species]
                    total   += d['total']
                    if overall_seen:
                        break
                    species_seen = True
                if not overall_seen and d['species'] == 'Overall':
                    overall_seen = True
#                    print(d['k'], d['alpha'], d['species'], d['attempts'])
                    isols   = d['attempts']
                    if species_seen:
                        break
    return (correct, total, isols)

def overall_precision(data, k, alpha):
    print("overall precision")
    total = 0
    correct = 0
    for species in get_species_names(data):
        thisTotal = -1
        thisCorrect = -1
        for d in data:
            if d['k'] == k and d['alpha'] == alpha:
#                print(species + ":",d['k'], d['alpha'], d['species'], d[species], d['attempts'])
                if d['species'] == species:
                    thisCorrect = d[species]
                if d['species'] == 'Overall':
                    thisTotal = d[species]
                if thisTotal >=0 and thisCorrect >= 0:
                    total += thisTotal
                    correct += thisCorrect
                    break
    return (correct, total)

def calc_metrics(data, k_limit, alpha, species):
    name_len = len(species)
    metrics = Table(names = ('species', 'k', 'num', 'alpha', 'recall', 'precision', 'fmeasure'), dtype = ('S' + str(name_len), 'i32', 'i32', 'f64', 'f64', 'f64', 'f64'))
    measured_k = numpy.unique(data['k'])
    if species == 'overall' or species == 'Overall':
        for m_k in measured_k:
            if m_k == k_limit:
                r_corr, r_tot, isols = overall_recall(data, m_k, alpha)
                recall = round(r_corr / r_tot, 3)
#                p_corr, p_tot = overall_precision(data, m_k, alpha)
#                precision = round(p_corr / p_tot, 3)
#                fmeasure =  calc_f_measure(recall, precision)
                metrics.add_row((species, m_k, isols, alpha, recall, recall, recall))
    else:
        for m_k in measured_k:
            if m_k == k_limit:
                r_corr,  r_tot, isols = calc_recall(data, m_k, alpha, species)
                recall = round(r_corr / r_tot, 3)
                p_corr, p_tot = calc_precision(data, m_k, alpha, species)
                precision = round(p_corr / p_tot, 3)
                fmeasure =  calc_f_measure(recall, precision)
                metrics.add_row((species, m_k, isols, alpha, recall, precision, fmeasure))
    return metrics

def print_upper_header():
    print("%s,%s,%s,%s,%s,%s,%s,%s" % ('$|\spec{}|$','$|\isol{}s|$','Meanwise','Meanwise','Meanwise','Winner','Winner','Winner'))
    print("%s,%s,%s,%s,%s,%s,%s,%s" % ('$|\spec{}|$','$|\isol{}s|$','$P$','$R$','$F_1$','$P$','$R$','$F_1$'))

def print_lower_header():
    print("%s,%s,%s,%s,%s,%s,%s,%s" % ('$|\spec{}|$','$|\isol{}s|$','Union','Union','Union','Intersection','Intersection','Intersection'))
    print("%s,%s,%s,%s,%s,%s,%s,%s" % ('$|\spec{}|$','$|\isol{}s|$','$P$','$R$','$F_1$','$P$','$R$','$F_1$'))

def print_row(s,a,b):
    print("%s,%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f" % (s,a['num'],a['precision'],a ['recall'],a['fmeasure'],b['precision'],b['recall'],b['fmeasure']))

def main():
    argc = len(sys.argv)
    if argc < 4:
        print_usage("Not enough arguments!")
    k_limit = numpy.int64(sys.argv[1])
    alpha   = numpy.float64(sys.argv[2])
    species = []
    for i in range(3,argc):
        species = numpy.append(species, numpy.str(sys.argv[i]))

    print(species)

    m_fname = 'mean.csv'
    w_fname = 'winner.csv'
    u_fname = 'union.csv'
    i_fname = 'intersection.csv'

    print("Parsing %s" % m_fname)
    m_data = atpy_csv(m_fname)
    print("Parsing %s" % w_fname)
    w_data = atpy_csv(w_fname)
    print("Parsing %s" % u_fname)
    u_data = atpy_csv(u_fname)
    print("Parsing %s" % i_fname)
    i_data = atpy_csv(i_fname)

    m_metrics = []
    w_metrics = []
    u_metrics = []
    i_metrics = []
    for i, s in enumerate(species):
        print("Metrics for %d, %.3f %s %s" % (k_limit, alpha, "mean", s))
        m_metrics.append(calc_metrics(m_data, k_limit, alpha, s))
        print("Metrics for %d, %.3f %s %s" % (k_limit, alpha, "winner", s))
        w_metrics.append(calc_metrics(w_data, k_limit, alpha, s))
        print("Metrics for %d, %.3f %s %s" % (k_limit, alpha, "union", s))
        u_metrics.append(calc_metrics(u_data, k_limit, alpha, s))
        print("Metrics for %d, %.3f %s %s" % (k_limit, alpha, "intersect", s))
        i_metrics.append(calc_metrics(i_data, k_limit, alpha, s))

    print_upper_header()
    for i, s in enumerate(species):
        print_row(s,m_metrics[i],w_metrics[i])

    print_lower_header()
    for i, s in enumerate(species):
        print_row(s,u_metrics[i],i_metrics[i])
    
    
    return 0
    
if __name__ == "__main__":
    sys.exit(main())
