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

def calc_recall(data, k, alpha, species):
    print("Calculating recall...")
    name_len = len(species)
    recall = Table(names = ('species', 'k', 'alpha', 'recall', 'correct',
        'classifications'), dtype = ('S' + str(name_len), 'i32', 'f64', 'f64',
            'i32', 'i32'))

    for d in data:
        if d['k'] <= k and d['alpha'] == alpha and d['species'] == species: 
            classifications = 0
            for o in data.colnames:
                if o != 'total' and o != 'k' and o != 'alpha' and o != 'species':
                    classifications += d[o]
            accuracy = round(d[species] / classifications * 100, 3)
            recall.add_row((d['species'], d['k'], d['alpha'], accuracy,
                    d[species], classifications))
    return recall

def calc_precision(data, k, alpha, species):
    print("Calculating precision...")
    name_len = len(species)
    precision = Table(names = ('species', 'k', 'alpha', 'precision', 'correct',
        'classifications'), dtype = ('S' + str(name_len), 'i32', 'f64', 'f64',
            'i32', 'i32'))
    measured_k = numpy.unique(data['k'])
    for m_k in measured_k:
        if m_k <= k:
            total = 0
            correct = 0
            for d in data:
                if d['k'] == m_k and d['alpha']:
                    total += d[species]
                    if d['species'] == species:
                        correct += d[species]
            accuracy = round(correct / total * 100, 3)
            precision.add_row((species, m_k, alpha, accuracy, correct, total))
    return precision

def main():
    if len(sys.argv) < 5:
        print_usage("Not enough arguments!")
    filename = sys.argv[1]
    species = numpy.str(sys.argv[2])
    k_limit = numpy.int64(sys.argv[3])
    alpha   = numpy.float64(sys.argv[4])
    print("Parsing " + filename + "...")
    data = atpy_csv(filename)
    recall = calc_recall(data, k_limit, alpha, species)
    precision = calc_precision(data, k_limit, alpha, species)
    print(recall)
    print(precision)


    pyplot.scatter(recall['k'], recall['recall'], label='recall',
            color='#FADA5E')
    pyplot.scatter(precision['k'], precision['precision'], label='precision',
            color='#0A7951')
    axes = pyplot.gca()
    axes.set_xlim([recall[0]['k'] - 1, recall[-1]['k'] + 1])
    axes.set_ylim(0, 100)
    pyplot.yticks(numpy.arange(0, 100, 10))
    axes.set_title('Precision vs. Recall for ' + species + ' at alpha ' +
            str(alpha))
    pyplot.ylabel('Accuracy (%)')
    pyplot.xlabel('k')
    pyplot.legend()
    pyplot.show()
    return 0
    
if __name__ == "__main__":
    sys.exit(main())
