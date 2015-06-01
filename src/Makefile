JC=	javac
JE=	java
SRCS=	*.java
CLSS=	*.class
CP=	'lib/mysql-connector-java-5.1.34-bin.jar:lib/'
LIB=	lib/

all: 
	$(JC) -cp $(CP) $(SRCS) -d $(LIB)

tree:
	$(JE) -cp $(CP) PhylogenyFileMaker 

treeclean:
	rm -v *.phyl

clean: 
	rm -v $(LIB)$(CLSS)


