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
    print("usage: " + sys.argv[0] + " <filename> <species> <k limit> <alpha>")
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
def plot_metrics_per_k(metrics, species, filename):
    alpha = numpy.unique(metrics['alpha'])

    pyplot.scatter(metrics['k'], metrics['recall'],    label='recall',    marker='s',   color='black')
    pyplot.scatter(metrics['k'], metrics['precision'], label='precision', marker = 'p', color='black')
    pyplot.scatter(metrics['k'], metrics['fmeasure'],  label='fmeasure',  marker = 'x', color='black')
    axes = pyplot.gca()
    axes.set_xlim([numpy.min(metrics['k']) - 1, numpy.max(metrics['k']) + 1])
    axes.set_ylim(0, 1.05)
    pyplot.xticks(numpy.arange(numpy.min(metrics['k']) - 1, numpy.max(metrics['k']) + 1))
    pyplot.yticks(numpy.arange(0, 1.04, .05))
    axes.set_title('%s Precision, Recall, and $F$-Measure at $\\alpha=$ %0.2f' % (species, alpha))
    pyplot.ylabel('Accuracy ')
    pyplot.xlabel('$k$')
    legend = pyplot.legend(loc=9, bbox_to_anchor=(0.5, -0.1))
    pyplot.savefig(filename, bbox_extra_artists=(legend,), bbox_inches='tight');
    pyplot.close()

def plot_precision_v_recall(metrics, species, filename):

    alpha = numpy.unique(metrics['alpha'])

    pyplot.scatter(metrics['precision'], metrics['recall'],    label='precision vs. recall',    marker='x',   color='black')
    axes = pyplot.gca()
    axes.set_xlim(0, 1.05)
    axes.set_ylim(0, 1.05)
    axes.set_aspect('equal')
    pyplot.xticks(numpy.arange(0, 1.05, .1))
    pyplot.yticks(numpy.arange(0, 1.05, .1))
    axes.set_title('%s Precision vs. Recall at $\\alpha=$ %0.2f' % (species, alpha))
    pyplot.ylabel('Recall')
    pyplot.xlabel('Precision')
    legend = pyplot.legend(loc=9, bbox_to_anchor=(0.5, -0.1))

    middle = len(metrics['k']) / 2
    middle = numpy.int32(middle)


    first = metrics[0]
    f_x = first['precision']
    f_y = first['recall']
    f_a_y = f_y/2 if f_y > abs(1-f_y)/2 else abs(1+f_y)/2
    last = metrics[-1]
    l_x = last['precision']
    l_y = last['recall']
    l_a_y = l_y/2 if l_y > abs(1-l_y)/2 else abs(1+l_y)/2

    middle_ndx = numpy.int32(len(metrics['k']) / 2 - 1)
    middle = metrics[middle_ndx]
    m_x = middle['precision']
    m_y = middle['recall']
    m_a_y = m_y/2 if m_y > abs(1-m_y) else abs(1+m_y)/2

    arrow = dict(arrowstyle = '->', connectionstyle = 'arc3,rad=0')
    pyplot.annotate( '$k$ = ' + str(first['k']), xy = (f_x,f_y), xytext = (f_x, f_a_y), rotation=90, va = 'center', ha = 'center', arrowprops=arrow)
    pyplot.annotate( '$k$ = ' + str(middle['k']), xy = (m_x,m_y), xytext = (m_x, m_a_y), rotation=90, va = 'center', ha = 'center', arrowprops=arrow)
    pyplot.annotate( '$k$ = ' + str(last['k']), xy = (l_x, l_y), xytext = (l_x, l_a_y), rotation=90, va = 'center', ha = 'center', arrowprops=arrow)
    pyplot.savefig(filename, bbox_extra_artists=(legend,), bbox_inches='tight');
    pyplot.close()



def main():
    if len(sys.argv) < 5:
        print_usage("Not enough arguments!")
    species = numpy.str(sys.argv[1])
    alpha   = numpy.float64(sys.argv[2])
    k_limit = 12

    m_name = 'mean.csv'
    w_name = 'winner.csv'
    u_name = 'set.csv'
    i_name = 'intersection.csv'

    m_data = atpy_csv(m_name)
    w_data = atpy_csv(w_name)
    u_data = atpy_csv(u_name)
    i_data = atpy_csv(i_name)

    m_metr = calc_metrics(m_data, k_limit, alpha, species)
    w_metr = calc_metrics(w_data, k_limit, alpha, species)
    u_metr = calc_metrics(u_data, k_limit, alpha, species)
    i_metr = calc_metrics(i_data, k_limit, alpha, species)

    print(m_metr)
    print(w_metr)
    print(u_metr)
    print(i_metr)
    sys.exit(1)

    metr_fname = "./figures/%s-ALL-metrics-%d-%1.3f.pdf" % (species, filename.replace('.',''), k_limit, alpha)
    plot_metrics_per_k(metrics, species, metr_fname)

    pvr_fname = "./figures/%s-ALL-pvr-%d-%1.3f.pdf" % (species, filename.replace('.',''), k_limit, alpha)
    plot_precision_v_recall(metrics, species, pvr_fname)

    return 0
    
if __name__ == "__main__":
    sys.exit(main())
