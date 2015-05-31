#!/bin/bash

DIR="build"
#makes build directory
if [[ ! -d "$DIR" ]]; then
   mkdir "$DIR"
fi

if [ "$(uname)" == "CYGWIN_NT-6.1" ]; then
   echo found cygwin
   javac '-Xlint:unchecked' -cp 'mysql-connector-java-5.1.34-bin.jar;build;.' *.java -d "$DIR"
else
   javac -Xlint:unchecked -cp mysql-connector-java-5.1.34-bin.jar:build:. *.java -d "$DIR"
fi
