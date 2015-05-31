#!/bin/bash

RESULTS="results"
if [[ ! -d "$RESULTS" ]]; then
   mkdir "$RESULTS"
fi

if [ "$(uname)" == "CYGWIN_NT-6.1" ]; then
   echo found cygwin
   java -Xmx1024m -cp 'mysql-connector-java-5.1.34-bin.jar;build;.' RunTests $@
else
   java -Xmx1024m -cp mysql-connector-java-5.1.34-bin.jar:build:. RunTests $@
fi
