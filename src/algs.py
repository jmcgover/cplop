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
    print("usage: " + sys.argv[0] + " <species> <alpha>")
    sys.exit(exit_val)

def atpy_csv(filename):
    data = ascii.read(filename)
    return data
def filter_by_keyvalue(data, key, value):
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
    correct = 0
    total = 0
    for d in data:
        if d['k'] == k and d['alpha'] == alpha and d['species'] == species: 
            correct = d[species]
            total   = d['total']
            return round(correct / total, 3)
    return None

def calc_precision(data, k, alpha, species):
    correct = -1
    total = -1
    for d in data:
        if d['k'] == k and d['alpha'] == alpha:
            if d['species'] == species:
                correct = d[species]
            if d['species'] == 'Overall':
                total = d[species]
            if total >=0 and correct >= 0:
                return round(correct / total, 3)
    return None

def overall_recall(data, k, alpha):
    correct = 0
    total = 0
    for species in get_species_names(data):
        for d in data:
            if d['k'] == k and d['alpha'] == alpha and d['species'] == species: 
                correct += d[species]
                total   += d['total']
                break
    return round(correct / total, 3)

def overall_precision(data, k, alpha):
    total = 0
    correct = 0
    for species in get_species_names(data):
        thisTotal = -1
        thisCorrect = -1
        for d in data:
            if d['k'] == k and d['alpha'] == alpha:
                if d['species'] == species:
                    thisCorrect = d[species]
                if d['species'] == 'Overall':
                    thisTotal = d[species]
                if thisTotal >=0 and thisCorrect >= 0:
                    total += thisTotal
                    correct += thisCorrect
                    break
    return round(correct / total, 3)

def calc_metrics(data, k_limit, alpha, species):
    name_len = len(species)
    metrics = Table(names = ('species', 'k', 'alpha', 'recall', 'precision', 'fmeasure'), dtype = ('S' + str(name_len), 'i32', 'f64', 'f64', 'f64', 'f64'))
    measured_k = numpy.unique(data['k'])
    if species == 'overall' or species == 'Overall':
        for m_k in measured_k:
            print("k", m_k)
            if m_k <= k_limit:
                print("recall..")
                recall    = overall_recall(data, m_k, alpha)
                print("precision..")
                precision = overall_precision(data, m_k, alpha)
                fmeasure =  calc_f_measure(recall, precision)
                print("adding..")
                metrics.add_row((species, m_k, alpha, recall, precision, fmeasure))
    else:
        for m_k in measured_k:
            if m_k <= k_limit:
                recall    = calc_recall(data, m_k, alpha, species)
                precision = calc_precision(data, m_k, alpha, species)
                fmeasure =  calc_f_measure(recall, precision)
                metrics.add_row((species, m_k, alpha, recall, precision, fmeasure))
    return metrics

params = {'text.usetex' : True,
          'font.size' : 11,
          'font.family' : 'lmodern',
          'text.latex.unicode': True,
          'legend.loc' : 'best'
          }
matplotlib.rcParams.update(params);
gold='#FADA5E'
green='#0A7951'
alg_keys = ['mean','winner','union', 'intersection']
colors = dict(zip(alg_keys,['purple', 'orange', 'red', green]))
def plot_metrics_per_k(species, alpha, metrics, filename):

    print("Plotting metrics...")
    for key in alg_keys:
        metric = metrics[key]
        this_color = colors[key]
        pyplot.scatter(metric['k'], metric['recall'],    label='%s: $R$'%key,     marker='s',   color=this_color)
        pyplot.scatter(metric['k'], metric['precision'], label='%s: $P$'%key,     marker = 'p', color=this_color)
        pyplot.scatter(metric['k'], metric['fmeasure'],  label='%s: $F_1$'%key, marker = 'x', color=this_color)
    axes = pyplot.gca()
    metric = metrics[list(metrics)[0]]
    axes.set_xlim([numpy.min(metric['k']) - 1, numpy.max(metric['k']) + 1])
    axes.set_ylim(0, 1.05)
    pyplot.xticks(numpy.arange(numpy.min(metric['k']) - 1, numpy.max(metric['k']) + 1))
    pyplot.yticks(numpy.arange(0, 1.04, .05))
    axes.set_title('%s Precision, Recall, and $F$-Measure at $\\alpha=$ %0.2f' % (species, alpha))
    pyplot.ylabel('Accuracy ')
    pyplot.xlabel('$k$')
    legend = pyplot.legend(loc=9, bbox_to_anchor=(0.5, -0.1), ncol=4)

    print("Saving as %s..." % filename)
    pyplot.savefig(filename, bbox_extra_artists=(legend,), bbox_inches='tight');
    pyplot.close()

def plot_precision_v_recall(species, alpha, metrics, filename):

    print("Plotting p vs r...")
    for key in alg_keys:
        metric = metrics[key]
        this_color = colors[key]
        pyplot.scatter(metric['recall'], metric['precision'], label='%s' % key, marker='o', edgecolors=this_color, facecolors='none', s=80)
        for label, x, y in zip(metric['k'],metric['recall'], metric['precision']):
            pyplot.annotate(str(label), xy=(x,y), xytext=(x,y), va='center', ha='center', color=this_color, size='xx-small');

    axes = pyplot.gca()
    axes.set_xlim(0, 1.05)
    axes.set_ylim(0, 1.05)
    axes.set_aspect('equal')
    pyplot.xticks(numpy.arange(0, 1.05, .1))
    pyplot.yticks(numpy.arange(0, 1.05, .1))
    axes.set_title('%s Recall vs. Precision at $\\alpha=$ %0.2f' % (species, alpha))
    pyplot.xlabel('Recall')
    pyplot.ylabel('Precision')
    legend = pyplot.legend(loc=9, bbox_to_anchor=(0.5, -0.1), ncol=2)

    print("Saving as %s..." % filename)
    pyplot.savefig(filename, bbox_extra_artists=(legend,), bbox_inches='tight');
    pyplot.close()



def main():
    if len(sys.argv) < 3:
        print_usage("Not enough arguments!")
    species = numpy.str(sys.argv[1])
    alpha   = numpy.float64(sys.argv[2])
    k_limit = 12

    m_name = 'mean.csv'
    w_name = 'winner.csv'
    u_name = 'union.csv'
    i_name = 'intersection.csv'

    print("Reading %s" % m_name)
    m_data = atpy_csv(m_name)
    print("Reading %s" % w_name)
    w_data = atpy_csv(w_name)
    print("Reading %s" % u_name)
    u_data = atpy_csv(u_name)
    print("Reading %s" % i_name)
    i_data = atpy_csv(i_name)
    if species == 'Overall' or species == 'overall':
        m_data = filter_by_keyvalue(m_data, 'alpha', alpha)
        w_data = filter_by_keyvalue(w_data, 'alpha', alpha)
        u_data = filter_by_keyvalue(u_data, 'alpha', alpha)
        i_data = filter_by_keyvalue(i_data, 'alpha', alpha)


    print("Calculating %s" % 'mean')
    m_metr = calc_metrics(m_data, k_limit, alpha, species)
    print("Calculating %s" % 'winner')
    w_metr = calc_metrics(w_data, k_limit, alpha, species)
    print("Calculating %s" % 'union')
    u_metr = calc_metrics(u_data, k_limit, alpha, species)
    print("Calculating %s" % 'intersection')
    i_metr = calc_metrics(i_data, k_limit, alpha, species)

    print(m_metr)
    print(w_metr)
    print(u_metr)
    print(i_metr)

    all_metrics = dict(zip(['mean', 'winner', 'union', 'intersection', ], [m_metr, w_metr, u_metr, i_metr]))

    metr_fname = "./figures/%s-ALL-metrics-%d-%s.pdf" % (species, k_limit, str("%.3f" % alpha).replace('.','_'))
    plot_metrics_per_k(species, alpha, all_metrics, metr_fname)

    pvr_fname = "./figures/%s-ALL-pvr-%d-%s.pdf" % (species, k_limit, str("%.3f" % alpha).replace('.','_'))
    plot_precision_v_recall(species, alpha, all_metrics, pvr_fname)

    return 0
    
if __name__ == "__main__":
    sys.exit(main())
