#  Copyright (c) 2020-2023 bransonvitz@protonmail.com All Rights Reserved.
#
#  This file is part of jmgpg.
#
#  jmgpg is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as
#  published by the Free Software Foundation, either version 3 of
#  the License, or (at your option) any later version.
#
#  jmgpg is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU Lesser General Public License for more details.
#
#  You should have received a copy of the
#  GNU General Public License along with this software.
#  If not, see <http://www.gnu.org/licenses>.
VER = 0.1.0
JAR = gpg-plugin-$(VER).jar

JMH = $(HOME)/jmeter
LCP = "$(JMH)/lib/slf4j-api-1.7.36.jar:$(JMH)/lib/ext/ApacheJMeter_core.jar:$(JMH)/lib/ext/ApacheJMeter_components.jar:$(JMH)/lib/ext/jorphan.jar:$(JMH)/lib/ext/log4j-core.jar:."

JSD = org/apache/jmeter
SRC = $(JSD)/GnuPGPlugin.java $(JSD)/GnuPGPluginGUI.java

all: $(JAR)
	@echo Build complete

clean:
	@rm -fv $(JAR) $(JSD)/*.class

$(JAR): $(SRC:.java=.class)
	jar cf $(JAR) $(JSD)/*.class

%.class : %.java
	javac -cp $(LCP) $<
