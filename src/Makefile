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

col: unionCol meanCol winnerCol intersectionCol
	
meanCol: mean/ mean.res $(LIB)ResultsPrinter.class
	java -cp $(CP) ResultsPrinter -f mean.res -m -c

winnerCol: winner/ winner.res $(LIB)ResultsPrinter.class
	java -cp $(CP) ResultsPrinter -f winner.res -m -c

unionCol: union/ union.res $(LIB)ResultsPrinter.class
	java -cp $(CP) ResultsPrinter -f union.res -m -c

intersectionCol: intersection/ intersection.res $(LIB)ResultsPrinter.class
	java -cp $(CP) ResultsPrinter -f intersection.res -m -c

csv: union mean winner intersection

$(LIB)ResultsPrinter.class: $(LIB)ExperimentResult.class $(LIB)Classification.class $(LIB)SpeciesResult.class ResultsPrinter.java
	$(JC) $(FLAGS) -cp $(CP) ResultsPrinter.java -d $(LIB)


mean: mean/ mean.res $(LIB)ResultsPrinter.class
	java -cp $(CP) ResultsPrinter -f mean.res -m -o mean.csv

winner: winner/ winner.res $(LIB)ResultsPrinter.class
	java -cp $(CP) ResultsPrinter -f winner.res -m -o winner.csv

union: union/ union.res $(LIB)ResultsPrinter.class
	java -cp $(CP) ResultsPrinter -f union.res -m -o union.csv

intersection: intersection/ intersection.res $(LIB)ResultsPrinter.class
	java -cp $(CP) ResultsPrinter -f intersection.res -m -o intersection.csv

#Statistics
stats: $(LIB)Driver.class tree.phyl
	java -cp $(CP) Driver -z -f tree.phyl

#Result Files
results: mean.res winner.res union.res intersection.res 

mean.res: tree.phyl $(LIB)Meanwise.class
	java -cp $(CP) Driver -f tree.phyl -o mean.res -m

winner.res: tree.phyl $(LIB)Winner.class
	java -cp $(CP) Driver -f tree.phyl -o winner.res -w

union.res: tree.phyl $(LIB)Setwise.class
	java -cp $(CP) Driver -f tree.phyl -o union.res -s

intersection.res: tree.phyl $(LIB)Intersection.class
	java -cp $(CP) Driver -f tree.phyl -o intersection.res -i

$(LIB)%.class: %.java
	$(JC) $(FLAGS) -cp $(CP) $^ -d $(LIB)

#Result Folders
union/: results/
	mkdir -p $^$@

mean/: results/
	mkdir -p $^$@

winner/: results/
	mkdir -p $^$@

intersection/: results/
	mkdir -p $^$@

results/:
	mkdir -p results


clean: resultclean
	rm -v $(LIB)$(CLSS)


