JC=	javac
JE=	java
FLAGS=-Xlint:unchecked
SRCS=	*.java
CLSS=	*.class
CP=	'lib/mysql-connector-java-5.1.34-bin.jar:lib/'
LIB=	lib/

all: 
	$(JC) $(FLAGS) -cp $(CP) $(SRCS) -d $(LIB)

tree.phyl: Phylogeny.java
	$(JE) -cp $(CP) PhylogenyFileMaker -f tree.phyl

tree:
	$(JE) -cp $(CP) PhylogenyFileMaker

Phylogeny.java: Species.java Host.java Isolate.java Pyroprint.java
	touch Phylogeny.java

treeclean:
	rm -v *.phyl

resultclean:
	rm -v *.res

csv: set mean winner

set: set/ set.res
	java -cp $(CP) ResultsPrinter -f set.res > set.csv

mean: mean/ mean.res
	java -cp $(CP) ResultsPrinter -f mean.res > mean.csv

winner: winner/ winner.res
	java -cp $(CP) ResultsPrinter -f winner.res > winner.csv

#Result Files
set.res: tree.phyl
	java -cp $(CP) ApproachSet -f tree.phyl -o set.res

mean.res: tree.phyl
	java -cp $(CP) ApproachMean -f tree.phyl -o mean.res

winner.res: tree.phyl
	java -cp $(CP) ApproachWinner -f tree.phyl -o winner.res

#Result Folders
set/: results/
	mkdir -p $^$@

mean/: results/
	mkdir -p $^$@

winner/: results/
	mkdir -p $^$@

results/:
	mkdir -p results


clean: resultclean
	rm -v $(LIB)$(CLSS)


