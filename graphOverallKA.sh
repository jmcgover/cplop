#!/bin/bash

file=$1
output="${file%.*}.svg"
GOLD="#FADA5E"
GREEN="#0A7951"
WIDTH="1000"
HEIGHT="400"
xCol=$2
yCol=$3
xLabel="$( awk -F "\"*,\"*" "{print \$${xCol}; exit}" ${file})"
yLabel="$( awk -F "\"*,\"*" "{print \$${yCol}; exit}" ${file})"
firstLine=$(head -1 "$file")
#alpha=$4
echo saving $file histogram as $output
echo $xLabel
echo $yLabel
echo $alpha

gnuplot -persist << EOF
   # OUTPUT
   #set terminal x11
   set terminal svg size $WIDTH,$HEIGHT
   set output "$output" 


   # SEPARATOR
   set datafile separator ","

   # TITLES

   set title "Overall Accuracy as \$k\$ Increases with Various \$\\\alpha\$ Thresholds"
   set xlabel "$xLabel"
   set ylabel "$yLabel"

   # PLOT TYPE
   #set style histogram rowstacked

   # BORDERS & EXTRAS
   set key autotitle columnhead
   set border 3
   set style fill solid 1.0 border -1 
   set ytic scale 0
   set xtic scale 0
   set xrange [0:17]
   set yrange [0:1]

   # PLOT
   plot "$file" using ${xCol}:(\$2==0.0 && \$4=="16-23" ?\$${yCol}:NaN) title "16S-23S: \$\\\alpha=0.0\$",\
      "$file" using ${xCol}:(\$2==0.0 && \$4=="23-5" ?\$${yCol}:NaN) title "23S-5S: \$\\\alpha=0.0\$", \
      "$file" using ${xCol}:(\$2==0.5 && \$4=="16-23" ?\$${yCol}:NaN) title "16S-23S: \$\\\alpha=0.5\$",\
      "$file" using ${xCol}:(\$2==0.5 && \$4=="23-5" ?\$${yCol}:NaN) title "23S-5S: \$\\\alpha=0.5\$", \
      "$file" using ${xCol}:(\$2==0.9 && \$4=="16-23" ?\$${yCol}:NaN) title "16S-23S: \$\\\alpha=0.9\$",\
      "$file" using ${xCol}:(\$2==0.9 && \$4=="23-5" ?\$${yCol}:NaN) title "23S-5S: \$\\\alpha=0.9\$", \
      "$file" using ${xCol}:(\$2==0.95 && \$4=="16-23" ?\$${yCol}:NaN) title "16S-23S: \$\\\alpha=0.95\$",\
      "$file" using ${xCol}:(\$2==0.95 && \$4=="23-5" ?\$${yCol}:NaN) title "23S-5S: \$\\\alpha=0.95\$", \
      "$file" using ${xCol}:(\$2==0.98 && \$4=="16-23" ?\$${yCol}:NaN) title "16S-23S: \$\\\alpha=0.98\$",\
      "$file" using ${xCol}:(\$2==0.98 && \$4=="23-5" ?\$${yCol}:NaN) title "23S-5S: \$\\\alpha=0.98\$", \
      "$file" using ${xCol}:(\$2==0.99 && \$4=="16-23" ?\$${yCol}:NaN) title "16S-23S: \$\\\alpha=0.99\$",\
      "$file" using ${xCol}:(\$2==0.99 && \$4=="23-5" ?\$${yCol}:NaN) title "23S-5S: \$\\\alpha=0.99\$", \
      "$file" using ${xCol}:(\$2==1.0 && \$4=="16-23" ?\$${yCol}:NaN) title "16S-23S: \$\\\alpha=1.0\$",\
      "$file" using ${xCol}:(\$2==1.0 && \$4=="23-5" ?\$${yCol}:NaN) title "23S-5S: \$\\\alpha=1.0\$"
EOF

