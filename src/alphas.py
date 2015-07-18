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
    print("usage: " + sys.argv[0] + " <species> <filename> <alpha> ... ")
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

def filter_by_keyvalue(data, key, values):
    print("Filtering by %s == %s" % (key, str(value)))
    new_data = Table(data[0:0])
    for d in data:
        keep_it = False
        for v in values:
            keep_it  = keep_it and data[key] == v
            if not keep_it:
                break
        if keep_it:
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
color_vals = ['purple', 'orange', 'red', green, 'blue', 'black', 'magenta', 'red']
def plot_metrics_per_k(species, alphas, metrics, leg_title, filename):
    alpha_keys = []
    for alpha in alphas:
        alpha_keys.append("%.3f" % alpha)
        print(alpha_keys)
    colors = dict(zip(alpha_keys, color_vals[:len(alphas)]))
    print(colors)

    print("Plotting metrics...")
    for alpha in alphas:
        metric = metrics["%.3f" % alpha]
        this_color = colors["%.3f" % alpha]
        pyplot.scatter(metric['k'], metric['recall'],    label='%.2f: $R$'%alpha,     marker='s',   color=this_color, alpha=.5)
        pyplot.scatter(metric['k'], metric['precision'], label='%.2f: $P$'%alpha,     marker = 'p', color=this_color, alpha=.5)
        pyplot.scatter(metric['k'], metric['fmeasure'],  label='%.2f: $F_1$'%alpha,   marker = 'x', color=this_color, alpha=.5)
    axes = pyplot.gca()
    print("len metrics", len(metrics))
    metric = metrics[list(metrics)[0]]
    axes.set_xlim([numpy.min(metric['k']) - 1, numpy.max(metric['k']) + 1])
    axes.set_ylim(0, 1.05)
    pyplot.xticks(numpy.arange(numpy.min(metric['k']) - 1, numpy.max(metric['k']) + 1))
    pyplot.yticks(numpy.arange(0, 1.04, .05))
    axes.set_title('%s Precision, Recall, and $F$-Measure at $\\alpha=$ %s' % (species, str(numpy.around(alphas, decimals=2))))
    pyplot.ylabel('Accuracy ')
    pyplot.xlabel('$k$')
    legend = pyplot.legend(loc=9, bbox_to_anchor=(0.5, -0.1), title=leg_title, ncol=len(alphas))

    print("Saving as %s..." % filename)
    pyplot.savefig(filename, bbox_extra_artists=(legend,), bbox_inches='tight');
    pyplot.close()

def plot_precision_v_recall(species, alphas, metrics, leg_title, filename):
    alpha_keys = []
    for alpha in alphas:
        alpha_keys.append("%.3f" % alpha)
    colors = dict(zip(alpha_keys, color_vals[:len(alphas)]))

    print("Plotting p vs r...")
    for alpha in alphas:
        metric = metrics["%.3f" % alpha]
        this_color = colors["%.3f" % alpha]
        pyplot.scatter(metric['recall'], metric['precision'], label='%1.2f' % alpha, marker='o', edgecolors=this_color, facecolors='none', s=80, alpha = .5)
        for label, x, y in zip(metric['k'],metric['recall'], metric['precision']):
            pyplot.annotate(str(label), xy=(x,y), xytext=(x,y), va='center', ha='center', color=this_color, size='xx-small');

    axes = pyplot.gca()
    axes.set_xlim(0, 1.05)
    axes.set_ylim(0, 1.05)
    axes.set_aspect('equal')
    pyplot.xticks(numpy.arange(0, 1.05, .1))
    pyplot.yticks(numpy.arange(0, 1.05, .1))
    axes.set_title('%s Recall vs. Precision at $\\alpha=$ %s' % (species, str(numpy.around(alphas, decimals=2))))
    pyplot.xlabel('Recall')
    pyplot.ylabel('Precision')
    legend = pyplot.legend(loc=9, bbox_to_anchor=(0.5, -0.1), title=leg_title, ncol=4)

    print("Saving as %s..." % filename)
    pyplot.savefig(filename, bbox_extra_artists=(legend,), bbox_inches='tight');
    pyplot.close()



def main():
    if len(sys.argv) < 4:
        print_usage("Not enough arguments!")
    species = numpy.str(sys.argv[1])
    filename= numpy.str(sys.argv[2])
    k_limit = 12
    alphas = []
    for i, a in enumerate(sys.argv[3:]):
        alphas.append(round(numpy.float64(a), 3))
    print("alphas",str(alphas))
    print("Reading %s" % filename)
    data = atpy_csv(filename)

    if species == 'Overall' or species == 'overall':
        data = filter_by_keyvalues(data, 'alpha', alphas)


    metrs = []
    for alpha in alphas:
        print("Calculating %s at %.2f" % ('mean', alpha))
        metrs.append(calc_metrics(data, k_limit, alpha, species))
        print(metrs[-1])

    alpha_keys = []
    for alpha in alphas:
        alpha_keys.append("%.3f" % alpha)

    all_metrics = dict(zip(alpha_keys, metrs))

    metr_fname = "./figures/%s-ALL-metrics-%d-%s.pdf" % (species, k_limit, str(numpy.around(alphas, decimals=2)))
    plot_metrics_per_k(species, alphas, all_metrics, 'Algorithm: ' + filename.split('.')[0], metr_fname)

    pvr_fname = "./figures/%s-ALL-pvr-%d-%s.pdf" % (species, k_limit, str(numpy.around(alphas, decimals=2)))
    print(pvr_fname)
    plot_precision_v_recall(species, alphas, all_metrics, 'Algorithm: ' + filename.split('.')[0], pvr_fname)

    return 0
    
if __name__ == "__main__":
    sys.exit(main())
