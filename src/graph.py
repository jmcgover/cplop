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
    name_len = len(species)
    recall = Table(names = ('species', 'k', 'alpha', 'recall',
        'classifications'), dtype = ('S' + str(name_len), 'i32', 'f64', 'f64',
            'i32'))

    for d in data:
        if d['k'] <= k and d['alpha'] == alpha and d['species'] == species: 
            classifications = 0
            for o in data.colnames:
                if o != 'total' and o != 'k' and o != 'alpha' and o != 'species':
                    classifications = classifications + d[o]
            accuracy = round(d[species] / classifications * 100, 3)
            recall.add_row((d['species'], d['k'], d['alpha'], accuracy,
                    classifications))
    return recall


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
    print(recall)
    pyplot.scatter(recall['k'], recall['recall'])
    pyplot.show()
    return 0
    
if __name__ == "__main__":
    sys.exit(main())
