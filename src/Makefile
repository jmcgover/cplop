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

#Classify
#ISOLS=ES-1199,ES-1200,ES-1203,ES-1206,ES-1207,ES-1208,ES-1211,ES-1215,ES-1216,ES-1217,ES-1218,ES-1219,ES-1221,ES-1225,ES-1233,ES-1234,ES-1236,ES-1240,ES-1241,ES-1242,ES-1244,ES-1247,ES-1249,ES-1208,ES-1234,ES-1236,ES-1240,ES-1241,ES-1242,ES-1244,ES-1247,ES-1249
#ISOLS=PH-1,PH-2,PH-3,PH-4,PH-5,PH-6,PH-007,PH-008,PH-009,PH-010,PH-011,PH-012,PH-013,PH-014,PH-015,PH-016,PH-017,PH-018,PH-019,PH-020,PH-021,PH-022,PH-023,PH-024,PH-025,PH-026,PH-027,PH-028,PH-029,PH-030,PH-031
ISOLS=PH-1,PH-2,PH-3,PH-4,PH-5,PH-6,PH-011,PH-012,PH-013,PH-014,PH-016,PH-017,PH-018,PH-019,PH-020,PH-021,PH-022,PH-023,PH-024,PH-025,PH-026,PH-027,PH-028,PH-029,PH-030,PH-031

classify: classify_union.csv classify_mean.csv classify_winner.csv classify_intersection.csv

classify_mean.csv: tree.phyl $(LIB)Meanwise.class mean/
	java -cp $(CP) Driver -f tree.phyl -o classify_mean.res -m -c $(ISOLS)

classify_winner.csv: tree.phyl $(LIB)Winner.class winner/
	java -cp $(CP) Driver -f tree.phyl -o classify_winner.res -w -c $(ISOLS)

classify_union.csv: tree.phyl $(LIB)Setwise.class union/
	java -cp $(CP) Driver -f tree.phyl -o classify_union.res -s -c $(ISOLS)

classify_intersection.csv: tree.phyl $(LIB)Intersection.class intersection/
	java -cp $(CP) Driver -f tree.phyl -o classify_intersection.res -i -c $(ISOLS)

$(LIB)%.class: %.java
	$(JC) $(FLAGS) -cp $(CP) $^ -d $(LIB)

#Result Folders
results/union/:
	mkdir -p $@

results/mean/:
	mkdir -p $@

results/winner/:
	mkdir -p $@

results/intersection/:
	mkdir -p $@

result_folders: results/union/ results/mean/ results/winner/ results/intersection/

#results/:
#	mkdir -p results


clean: resultclean
	rm -v $(LIB)$(CLSS)


