#!/bin/bash

file=$1
output="${file%.*}hist.svg"
GOLD="#FADA5E"
GREEN="#0A7951"
WIDTH="1000"
HEIGHT="400"
echo saving $file histogram as $output

gnuplot -persist << EOF
   # OUTPUT
   #set terminal x11
   #set terminal latex
   set terminal svg size $WIDTH,$HEIGHT
   set output "$output" 

   # SEPARATOR
   set datafile separator ","

   # TITLES
   set title "Number of Pyroprints for each Species in CPLOP"
   set xlabel "Species"
   set ylabel "Number of Pyroprints" 

   # PLOT TYPE
   set style histogram rowstacked

   # BORDERS & EXTRAS
   set key off
   set border 3
   set style fill solid 1.0 border -1 
   set ytic scale 0
   set xtic scale 0
   set xtics rotate by 90 right mirror

   # BOX
   set boxwidth 1

   # PLOT
   plot "$file" using 1:2:xtic(3) with boxes linecolor rgb "$GREEN", \
      "$file" using 1:2:2 with labels rotate by 90 left offset 0,.5
EOF
