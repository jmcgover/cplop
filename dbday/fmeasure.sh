#!/bin/bash

file=$1
output="${file%.*}.svg"
GOLD="#FADA5E"
GREEN="#0A7951"
WIDTH=$3
HEIGHT=$4
#xCol=$2
#yCol=$3
#xLabel="$( awk -F "\"*,\"*" "{print \$${xCol}; exit}" ${file})"
xLabel="$( awk -F "\"*,\"*" "{print \$1; exit}" ${file})"
#yLabel="$( awk -F "\"*,\"*" "{print \$${yCol}; exit}" ${file})"
yLabel="F-Measure"
firstLine=$(head -1 "$file")
alpha=$2
#description=$7
species=$5

#output="${file%.*}FMeasure${species}${alpha}.png"
output="${file%.*}FMeasure${species}${alpha}.svg"
echo saving $file histogram as $output
echo $output
echo $xLabel
echo $yLabel
echo Species: $species
echo $alpha

#declare -a array=("Human" "Cow" "Pigeon")
plotCommand="plot "
#for s in ${speciesArray[@]}; do
OLD_IFS=$IFS
IFS=,
declare -a speciesArray=(${species})
for s in ${species}; do
   plotCommand=${plotCommand}'"'$file'" using 1:($2=='$alpha' && strcol(3) eq "'${s}'" && strcol(4) eq "16-23" && $1 <= 11?fMeas($10,$20):NaN) title "'${s}': 16S-23S",'
   plotCommand=${plotCommand}'"'$file'" using 1:($2=='$alpha' && strcol(3) eq "'${s}'" && strcol(4) eq "23-5" && $1 <= 11 ?fMeas($10,$20):NaN) title "'${s}': 23S-5S",'
done
IFS=$OLD_IFS
echo $plotCommand

gnuplot -persist << EOF
   # OUTPUT
   #set terminal x11
   set terminal svg size $WIDTH,$HEIGHT
   #set terminal png size $WIDTH,$HEIGHT
   set output "$output" 

   # FUNCTIONS
   fMeas(p,r)=2*(p*r)/(p+r)

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
   set key at 10,.87
   set border 3
   set style fill solid 1.0 border -1 
   set format y "%.2f"
   set ytic scale 0 autofreq .10
   set xtic scale 0 autofreq 1
   set autoscale xfixmax
   set xrange [0:11]
   set yrange [0:1]

   # PLOT
   ${plotCommand}
EOF

