# COSC 4400 - Project #7
# Builds the MiniJava frontend and intermediate-representation translator.
# Authors: Nick Raimondi and Payton Canegan
# Instructor: Dr Brylow
# TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu

JAVAC = javac
JAVA = java
JFLAGS = -cp .

PACKAGES = Absyn Assem Frame Parse Riscv Semant Symbol Temp Translate Tree Types
SOURCES = $(foreach dir,$(PACKAGES),$(wildcard $(dir)/*.java))
CLASSES = $(SOURCES:.java=.class)

.PHONY: all clean test

all: $(CLASSES)

%.class: %.java
	$(JAVAC) $(JFLAGS) $<

test: all
	@set -e; \
	for test in Hello Precedence ValidDescriptors ValidTypeCheck TranslatorFeatures; do \
		$(JAVA) Parse.Main < testcases/$$test.java | \
			$(JAVA) Semant.Main | $(JAVA) Translate.Main > /dev/null; \
		echo "PASS testcases/$$test.java"; \
	done

clean:
	find $(PACKAGES) -name '*.class' -delete
