# ------------------------------------------------------------------------------
#
# GenAlign: A Gene Alignment Program
# Copyright (c) 2007 Benjamin Gaillard
#
# ------------------------------------------------------------------------------
#
#        File: Makefile
#
# Description: Build Makefile
#
# ------------------------------------------------------------------------------
#
# This program is free software; you can redistribute it and/or modify it
# under the terms of the GNU General Public License as published by the Free
# Software Foundation; either version 2 of the License, or (at your option)
# any later version.
#
# This program is distributed in the hope that it will be useful, but WITHOUT
# ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
# FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
# more details.
#
# You should have received a copy of the GNU General Public License along
# with this program; if not, write to the Free Software Foundation, Inc., 59
# Temple Place - Suite 330, Boston, MA 02111-1307, USA.
#
# ------------------------------------------------------------------------------


# Package information
PACKAGE = genalign
MAIN    = Application
VERSION = 1.0

# Program and flags
JAVAC      = javac
JAVACFLAGS = -encoding UTF-8 -source 1.2 -target 1.2 -Xlint:all -g:none
JAR        = jar
JAVA       = java
JAVADOC    = javadoc -quiet

# Directories and files
SRCDIR   = src
OBJDIR   = classes
DOCDIR   = doc
MANIFEST = manifest.mf
AUX      = AUTHORS COPYING README *.jpx *.jpx.local example

# Auxiliary variables
DISTDIR   = $(PACKAGE)-$(VERSION)
JARCHIVE  = $(DISTDIR).jar
DISTARC   = $(DISTDIR).zip
DOCARC    = $(DISTDIR)-doc.zip
JAVACDIRS = -sourcepath $(SRCDIR) -d $(OBJDIR) -classpath $(OBJDIR)

# Pseudo targets
.SUFFIXES:
.PHONY: default all doc objclean clean dist dist-doc really-all run
.PHONY: $(OBJDIR)-force $(JARCHIVE)-force

# Default rules
all:     $(OBJDIR)-force $(JARCHIVE)-force
default: $(JARCHIVE)

# Java compilation
$(OBJDIR) $(OBJDIR)-force:
	test -d $(OBJDIR) || mkdir $(OBJDIR)
	$(JAVAC) $(JAVACFLAGS) $(JAVACDIRS) $(SRCDIR)/$(PACKAGE)/*.java

# Manifest
$(MANIFEST):
	echo "Main-Class: $(PACKAGE).$(MAIN)" >$@

# JAR archive
$(JARCHIVE) $(JARCHIVE)-force: $(OBJDIR) $(MANIFEST)
	rm -f $(JARCHIVE)
	$(JAR) cfm $(JARCHIVE) $(MANIFEST) -C $(OBJDIR) $(PACKAGE)

# JavaDoc documentation
doc:
	rm -rf $(DOCDIR)
	$(JAVADOC) -d $(DOCDIR) $(SRCDIR)/$(PACKAGE)/*.java

# Remove intermediate files
objclean:
	rm -rf $(OBJDIR) $(MANIFEST)

# Remove all generated files
clean: objclean
	rm -rf $(JARCHIVE) $(DOCDIR)

# Make a distribuable archive
dist:
	rm -rf $(DISTDIR)
	mkdir $(DISTDIR)
	cp -pr $(SRCDIR) $(AUX) Makefile $(DISTDIR)
	rm -f $(DISTARC)
	find $(DISTDIR) | LANG=C sort | zip -q -9 -@ $(DISTARC)
	rm -rf $(DISTDIR)

# Make a distribuable documentation archive
dist-doc: doc
	rm -f $(DOCARC)
	find $(DOCDIR) | LANG=C sort | zip -q -9 -@ $(DOCARC)

# Really make all
really-all: all dist-doc objclean dist

# Running
run: $(JARCHIVE)
	$(JAVA) -jar $(JARCHIVE)

# End of File
