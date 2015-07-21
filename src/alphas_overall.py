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
    print("usage: " + sys.argv[0] + " <alpha> ... ")
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

def filter_by_keyvalues(data, key, values):
    print("Filtering by %s == %s" % (key, str(values)))
    new_data = Table(data[0:0])
    for d in data:
        for v in values:
            if d[key] == v:
                new_data.add_row(d)
                break
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
    metrics = Table(names = ('species', 'k', 'alpha', 'accuracy'), dtype = ('S' + str(name_len), 'i32', 'f64', 'f64'))
    measured_k = numpy.unique(data['k'])
    if species == 'overall' or species == 'Overall':
        for m_k in measured_k:
            if m_k <= k_limit:
                print("k", m_k)
                accuracy    = overall_recall(data, m_k, alpha)
                metrics.add_row((species, m_k, alpha, accuracy))
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
color_vals = ['purple', 'red', 'green', 'yellow',]
marker_vals = ['o','v','p','H','D','s','8','<','>']
marker_vals = ['o','v','s','D']
alg_keys = ['mean','winner','union', 'intersection']
markers = dict(zip(alg_keys,marker_vals))
colors = dict(zip(alg_keys,['purple', 'red', 'green', 'yellow',]))
def plot_metrics_per_k(k_low, k_high, species, alphas, metrics, leg_title, filename):
    alpha_keys = []
    for alpha in alphas:
        alpha_keys.append("%.3f" % alpha)
        print(alpha_keys)
    colors = dict(zip(alpha_keys, color_vals[:len(alphas)]))
    print(colors)

    print("Plotting metrics...")
    for alg in alg_keys:
        these_metrics = metrics[alg]
        this_marker = markers[alg]
        for alpha in alphas:
            metric = these_metrics["%.3f" % alpha]
            this_color = colors["%.3f" % alpha]
            pyplot.scatter(metric['k'], metric['accuracy'], label='%s: %.2f' % (alg, alpha), marker=this_marker, color=this_color, alpha=1)
    axes = pyplot.gca()
    print("len metrics", len(metrics))
    axes.set_xlim(k_low, k_high)
    axes.set_ylim(0, 1.05)
    pyplot.xticks(numpy.arange(numpy.min(metric['k']) - 1, numpy.max(metric['k']) + 1))
    pyplot.yticks(numpy.arange(0, 1.04, .05))
    axes.set_title('%s Accuracy at $\\alpha=$ %s' % (species, str(numpy.around(alphas, decimals=2)).replace('[','\{').replace(']','\}')))
    pyplot.ylabel('Accuracy ')
    pyplot.xlabel('$k$')
    legend = pyplot.legend(loc=9, bbox_to_anchor=(0.5, -0.1), title=leg_title, ncol=4)

    print("Saving as %s..." % filename)
    pyplot.savefig(filename, bbox_extra_artists=(legend,), bbox_inches='tight');
    pyplot.close()

def plot_precision_v_recall(species, alphas, metrics, leg_title, filename):
    alpha_keys = []
    for alpha in alphas:
        alpha_keys.append("%.3f" % alpha)
        print(alpha_keys)
    colors = dict(zip(alpha_keys, color_vals[:len(alphas)]))
    print(colors)

    print("Plotting metrics...")
    for alg in alg_keys:
        these_metrics = metrics[alg]
        this_marker = markers[alg]
        for alpha in alphas:
            metric = these_metrics["%.3f" % alpha]
            this_color = colors["%.3f" % alpha]
            pyplot.scatter(metric['accuracy'], metric['accuracy'], label='%1.2f' % alpha, marker=this_marker, edgecolors=this_color, facecolors='none', s=80, alpha = 1)
            for label, x, y in zip(metric['k'],metric['accuracy'], metric['accuracy']):
                pyplot.annotate(str(label), xy=(x,y), xytext=(x,y), va='center', ha='center', color=this_color, size='xx-small');

    axes = pyplot.gca()
    axes.set_xlim(0, 1.05)
    axes.set_ylim(0, 1.05)
    axes.set_aspect('equal')
    pyplot.xticks(numpy.arange(0, 1.05, .1))
    pyplot.yticks(numpy.arange(0, 1.05, .1))
    axes.set_title('%s Recall vs. Precision at $\\alpha=$ %s' % (species, str(numpy.around(alphas, decimals=2)).replace('[','{').replace(']','}')))
    pyplot.xlabel('Recall')
    pyplot.ylabel('Precision')
    legend = pyplot.legend(loc=9, bbox_to_anchor=(0.5, -0.1), title=leg_title, ncol=4)

    print("Saving as %s..." % filename)
    pyplot.savefig(filename, bbox_extra_artists=(legend,), bbox_inches='tight');
    pyplot.close()



def main():
    if len(sys.argv) < 2:
        print_usage("Not enough arguments!")
    species = "Overall"
    filename= ""
    k_limit = 12
    alphas = []
    for i, a in enumerate(sys.argv[1:]):
        alphas.append(round(numpy.float64(a), 3))
    print("alphas",str(alphas))

    metr_fname = "./figures/%s-ALL-metrics-%d-%s.pdf" % (species, k_limit, str(numpy.around(alphas, decimals=2)).replace(' ','-').replace('.','_'))
    pvr_fname = "./figures/%s-ALL-pvr-%d-%s.pdf" % (species, k_limit, str(numpy.around(alphas, decimals=2)).replace(' ','-').replace('.','_'))
    print(metr_fname)
    print(pvr_fname)

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
        m_data = filter_by_keyvalues(m_data, 'alpha', alphas)
        w_data = filter_by_keyvalues(w_data, 'alpha', alphas)
        u_data = filter_by_keyvalues(u_data, 'alpha', alphas)
        i_data = filter_by_keyvalues(i_data, 'alpha', alphas)

    print(m_data)
    print(w_data)
    print(u_data)
    print(i_data)

    print("Calculating %s" % 'mean')
    m_metrs = []
    print("Calculating %s" % 'winner')
    w_metrs = []
    print("Calculating %s" % 'union')
    u_metrs = []
    print("Calculating %s" % 'intersection')
    i_metrs = []

    alpha_keys = []
    for alpha in alphas:
        alpha_keys.append("%.3f" % alpha)

    metrs = []
    for alpha in alphas:
        print("Calculating %.2f" %  alpha)
        m_metrs.append(calc_metrics(m_data, k_limit, alpha, species))
        print(m_metrs[-1])
        w_metrs.append(calc_metrics(w_data, k_limit, alpha, species))
        print(w_metrs[-1])
        u_metrs.append(calc_metrics(u_data, k_limit, alpha, species))
        print(u_metrs[-1])
        i_metrs.append(calc_metrics(i_data, k_limit, alpha, species))
        print(i_metrs[-1])


    all_m_metr = dict(zip(alpha_keys, m_metrs))
    all_w_metr = dict(zip(alpha_keys, w_metrs))
    all_u_metr = dict(zip(alpha_keys, u_metrs))
    all_i_metr = dict(zip(alpha_keys, i_metrs))

    all_metrics = dict(zip(['mean', 'winner', 'union', 'intersection', ], [all_m_metr, all_w_metr, all_u_metr, all_i_metr]))

    plot_metrics_per_k(1, k_limit + 1, species, alphas, all_metrics, None, metr_fname)
    plot_precision_v_recall(species, alphas, all_metrics, None, pvr_fname)

    return 0
    
if __name__ == "__main__":
    sys.exit(main())
