JFLAGS=-cp .
JAR=jar
JAVA=java
JAVAC=javac
JAVACC=javacc

PARSEDIR=Parse
MJ_PARSER=MiniJavaParser
MJ_GRAMMAR=MiniJava

DEFAULT_JAVACCGEN = $(PARSEDIR)/SimpleCharStream.java \
	$(PARSEDIR)/ParseException.java \
	$(PARSEDIR)/Token.java \
	$(PARSEDIR)/TokenMgrError.java

MJ_JAVACCGEN = $(DEFAULT_JAVACCGEN) \
	$(PARSEDIR)/$(MJ_PARSER).java \
	$(PARSEDIR)/$(MJ_PARSER)Constants.java \
	$(PARSEDIR)/$(MJ_PARSER)TokenManager.java


# Project 3 - Parser

ASTSRC = $(wildcard Absyn/*.java)

PARSESRC = Parse/Main.java $(ASTSRC)

all:	$(PARSEDIR)/$(MJ_PARSER).java $(PARSESRC:.java=.class)

$(PARSEDIR)/$(MJ_PARSER).java: $(PARSEDIR)/$(MJ_GRAMMAR).jj
	cd $(PARSEDIR); ${JAVACC} $(MJ_GRAMMAR).jj;
	${JAVAC} ${JFLAGS} ${MJ_JAVACCGEN}

%.class : %.java
	${JAVAC} ${JFLAGS} $<

clean:
	rm -f $(PARSESRC:.java=.class) \
		$(MJ_JAVACCGEN) $(MJ_JAVACCGEN:.java=.class)\
                '$(PARSEDIR)/$(MJ_PARSER)$$JJCalls.class' \
                '$(PARSEDIR)/$(MJ_PARSER)$$LookaheadSuccess.class' 

realclean: clean
	rm -f */*.class *~ */*~ *.jar
