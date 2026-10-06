# COSC 4400 - Project #4
# Builds the parser, AST reader, type descriptors, and semantic analyzer.
# Authors: Nick Raimondi and Payton Canegan
# Instructor: Dr Brylow
# TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu

JFLAGS=-cp .
JAR=jar
JAVA=java
JAVAC=javac
JAVACC=javacc

PARSEDIR=Parse
MJ_PARSER=MiniJavaParser
MJ_GRAMMAR=MiniJava
AST_PARSER=ReadAbsyn
AST_GRAMMAR=ReadAbsyn

DEFAULT_JAVACCGEN = $(PARSEDIR)/SimpleCharStream.java \
	$(PARSEDIR)/ParseException.java \
	$(PARSEDIR)/Token.java \
	$(PARSEDIR)/TokenMgrError.java

MJ_JAVACCGEN = $(DEFAULT_JAVACCGEN) \
	$(PARSEDIR)/$(MJ_PARSER).java \
	$(PARSEDIR)/$(MJ_PARSER)Constants.java \
	$(PARSEDIR)/$(MJ_PARSER)TokenManager.java

AST_JAVACCGEN = $(DEFAULT_JAVACCGEN) \
	$(PARSEDIR)/$(AST_PARSER).java \
	$(PARSEDIR)/$(AST_PARSER)Constants.java \
	$(PARSEDIR)/$(AST_PARSER)TokenManager.java


# Project 4 - Type Checker

ASTSRC = $(wildcard Absyn/*.java)
TYPESRC = $(wildcard Types/*.java)
SYMBOLSRC = $(wildcard Symbol/*.java)
SEMANTSRC = $(wildcard Semant/*.java)

PARSESRC = Parse/Main.java Parse/AstNode.java Parse/AstConverter.java \
	$(ASTSRC) $(TYPESRC) $(SYMBOLSRC) $(SEMANTSRC)

all:	$(PARSEDIR)/$(MJ_PARSER).java $(PARSEDIR)/$(AST_PARSER).java \
	$(PARSESRC:.java=.class)

$(PARSEDIR)/$(MJ_PARSER).java: $(PARSEDIR)/$(MJ_GRAMMAR).jj
	cd $(PARSEDIR); ${JAVACC} $(MJ_GRAMMAR).jj;
	${JAVAC} ${JFLAGS} ${MJ_JAVACCGEN}

$(PARSEDIR)/$(AST_PARSER).java: $(PARSEDIR)/$(AST_GRAMMAR).jj \
	$(PARSEDIR)/$(MJ_PARSER).java
	cd $(PARSEDIR); ${JAVACC} $(AST_GRAMMAR).jj;
	${JAVAC} ${JFLAGS} ${AST_JAVACCGEN}

%.class : %.java
	${JAVAC} ${JFLAGS} $<

clean:
	rm -f $(PARSESRC:.java=.class) \
		$(MJ_JAVACCGEN) $(AST_JAVACCGEN) \
		$(MJ_JAVACCGEN:.java=.class) $(AST_JAVACCGEN:.java=.class)\
                '$(PARSEDIR)/$(MJ_PARSER)$$JJCalls.class' \
		'$(PARSEDIR)/$(MJ_PARSER)$$LookaheadSuccess.class' \
		'$(PARSEDIR)/$(AST_PARSER)$$JJCalls.class' \
		'$(PARSEDIR)/$(AST_PARSER)$$LookaheadSuccess.class'

realclean: clean
	rm -f */*.class *~ */*~ *.jar
