#! /usr/bin/env python3

# System
import sys
import os
from argparse import ArgumentParser
import json

# Formatting
from pprint import pprint

# Data Structures
from collections import defaultdict
from collections import Counter

# Plotting
from matplotlib import pyplot

DESCRIPTION = "Parses a JSON result file from the Java classification stuffs"
def get_arg_parser():
    parser = ArgumentParser(prog=sys.argv[0], description=DESCRIPTION)
    parser.add_argument("-f", "--filename",
            nargs = 1,
            required = True,
            help = "JSON file with the results")
    return parser

def k_alpha_isoid(results):
    k_alpha_isoid = {}
    for res in results["results"]:
        if res["k"] not in k_alpha_isoid:
            k_alpha_isoid[res["k"]] = defaultdict(dict)
        k_alpha_isoid[res["k"]][res["alpha"]][res["isoId"]] = res["classification"]
    return k_alpha_isoid

def isoid_k_alpha(results):
    isoid_k_alpha = {}
    for res in results["results"]:
        if res["isoId"] not in isoid_k_alpha:
            isoid_k_alpha[res["isoId"]] = defaultdict(dict)
        isoid_k_alpha[res["isoId"]][res["k"]][res["alpha"]] = res["classification"]
    return isoid_k_alpha

params = {'text.usetex' : True,
          'font.size' : 11,
          'font.family' : 'lmodern',
          'text.latex.unicode': True,
          'legend.loc' : 'best'
          }
def pie_k_alpha(k, alpha, k_alpha_isoid, title_fmt, file_fmt):
    pprint(k_alpha_isoid[k][alpha].values())
    counter = Counter(k_alpha_isoid[k][alpha].values())
    labels = []
    counts = []
    explodes = []
    for label, count in sorted(counter.items(), key = lambda x : x[0]):
        labels.append("%s(%d)" % (label, count))
        counts.append(count)
    pprint(labels)
    pprint(counts)
    pyplot.figure(1, figsize=(8,8))
    print("Making pie chart")
    pyplot.pie(counts, labels=labels, autopct='%1.1f%%', shadow=True, startangle=90)
    print("Making title")
    pyplot.title(title_fmt % (k, alpha))
    print("Saving")
    pyplot.savefig(file_fmt % (k, alpha))
    pyplot.close()
    return

def main():
    parser = get_arg_parser()
    args = parser.parse_args()
    filename = args.filename[0]
    basename = os.path.basename(filename)
    basename = basename.split(".")[0]
    study = basename.split("_")[0]
    method = basename.split("_")[1]
    results = None
    with open(filename, "r") as file:
        results = json.load(file)
    isoid_k_alpha_dict = isoid_k_alpha(results)
    k_alpha_isoid_dict = k_alpha_isoid(results)
    for k in k_alpha_isoid_dict:
        for alpha in k_alpha_isoid_dict[k]:
            pie_k_alpha(k, alpha, k_alpha_isoid_dict, 
                    "%s %s " % (study.title(), method.title()) + "k=%d alpha=%.3f", 
                    "pie/%s-%s-" % (study, method) + "%d-%.3f.pdf"
                    )
    return 0

if __name__ == "__main__":
    rtn = main()
    sys.exit(rtn)
