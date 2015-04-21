#!/bin/bash

file=$1
output="${file%.*}.svg"
GOLD="#FADA5E"
GREEN="#0A7951"
WIDTH=$5
HEIGHT=$6
xCol=$2
yCol=$3
xLabel="$( awk -F "\"*,\"*" "{print \$${xCol}; exit}" ${file})"
yLabel="$( awk -F "\"*,\"*" "{print \$${yCol}; exit}" ${file})"
firstLine=$(head -1 "$file")
alpha=$4
description=$7
species=$8

#output="${file%.*}${description}${species}${alpha}.svg"
output="${file%.*}${description}${species}${alpha}.png"
echo saving $file histogram as $output
echo $output
echo $xLabel
echo $yLabel
echo $species
echo $alpha

#declare -a array=("Human" "Dog" "White Crowned Sparrow")
declare -a speciesArray=(${species})
plotCommand="plot "
for s in "${speciesArray[@]}"; do
   plotCommand=${plotCommand}'"'$file'" using '${xCol}':($2=='$alpha' && strcol(3) eq "'${s}'" && strcol(4) eq "16-23" ?$'${yCol}':NaN) title "'${s}': 16S-23S",'
   plotCommand=${plotCommand}'"'$file'" using '${xCol}':($2=='$alpha' && strcol(3) eq "'${s}'" && strcol(4) eq "23-5" ?$'${yCol}':NaN) title "'${s}': 23S-5S",'
done
echo $plotCommand

gnuplot -persist << EOF
   # OUTPUT
   #set terminal x11
   #set terminal svg size $WIDTH,$HEIGHT
   #set terminal svg 
   set terminal png size $WIDTH,$HEIGHT
   set output "$output" 

   # SEPARATOR
   set datafile separator ","

   # TITLES

   set title "${species} ${description} as \$k\$ Increases with \$\\\alpha\$ Threshold ${alpha}"
   set xlabel "$xLabel"
   set ylabel "$yLabel"

   # PLOT TYPE
   #set style histogram rowstacked

   # BORDERS & EXTRAS
   #set key left bottom 
   set key at 16,87
   set border 3
   set style fill solid 1.0 border -1 
   set format y "%.0f"
   set ytic scale 0 autofreq 10
   set xtic scale 0 autofreq 1
   set autoscale xfixmax
   set xrange [0:17]
   set yrange [0:100]

   # PLOT
   ${plotCommand}
EOF

