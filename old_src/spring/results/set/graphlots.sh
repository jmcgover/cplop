#/bin/bash

for a in 0.0 .5 .9 .95 .96 .97 .98 .99 1.0; do 
   echo "./kgraph.sh $1 1 5 $a 0.0 600 400 set $2"
   ./kgraph.sh $1 1 5 $a 600 400 recall $2
done
