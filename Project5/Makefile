# COSC 4400 - Project 5
# Builds the parser, AST reader, type descriptors, and MiniJava type checker.
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


# Project 5 - Type Checker

ASTSRC = $(wildcard Absyn/*.java)
TYPESRC = $(wildcard Types/*.java)
SYMBOLSRC = $(wildcard Symbol/*.java)
SEMANTSRC = $(wildcard Semant/*.java)

PARSESRC = Parse/Main.java Parse/AstNode.java Parse/AstConverter.java \
	$(ASTSRC) $(TYPESRC) $(SYMBOLSRC) $(SEMANTSRC)

.PHONY: all generate test clean realclean

all:	$(PARSESRC:.java=.class)

# Generated parser sources are committed so a normal build only needs a JDK.
# Run this target after changing either JavaCC grammar.
generate:
	cd $(PARSEDIR); ${JAVACC} $(MJ_GRAMMAR).jj
	cd $(PARSEDIR); ${JAVACC} $(AST_GRAMMAR).jj
	${JAVAC} ${JFLAGS} ${MJ_JAVACCGEN} ${AST_JAVACCGEN}

test: all
	@result=`${JAVA} Parse.Main < testcases/ValidTypeCheck.java | \
		${JAVA} Semant.Main 2>&1`; \
	if echo "$$result" | grep -q '^ERROR'; then echo "ValidTypeCheck.java failed:"; \
		echo "$$result"; exit 1; fi
	@${JAVA} Parse.Main < testcases/TypeErrors.java | \
		${JAVA} Semant.Main 2>&1 | diff -u testcases/TypeErrors.expected -
	@${JAVA} Parse.Main < testcases/GraderRegressions.java | \
		${JAVA} Semant.Main 2>&1 | diff -u testcases/GraderRegressions.expected -
	@${JAVA} Parse.Main < testcases/GraderOverrideCascade.java | \
		${JAVA} Semant.Main 2>&1 | diff -u testcases/GraderOverrideCascade.expected -
	@echo "Project 5 type-checker tests passed."

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
		$(MJ_JAVACCGEN:.java=.class) $(AST_JAVACCGEN:.java=.class)\
                '$(PARSEDIR)/$(MJ_PARSER)$$JJCalls.class' \
		'$(PARSEDIR)/$(MJ_PARSER)$$LookaheadSuccess.class' \
		'$(PARSEDIR)/$(AST_PARSER)$$JJCalls.class' \
		'$(PARSEDIR)/$(AST_PARSER)$$LookaheadSuccess.class' \
		'Semant/ClassTable$$State.class' \
		'Semant/TypeChecker$$1.class' \
		'Symbol/Table$$Binding.class'

realclean: clean
	rm -f */*.class *~ */*~ *.jar
