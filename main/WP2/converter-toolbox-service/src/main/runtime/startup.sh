#*******************************************************************************
# Copyright (C) 2016 Mango Business Solutions Ltd, http://www.mango-solutions.com
#
# This program is free software: you can redistribute it and/or modify it under
# the terms of the GNU Affero General Public License as published by the
# Free Software Foundation, version 3.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
# or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
# for more details.
#
# You should have received a copy of the GNU Affero General Public License along
# with this program. If not, see <http://www.gnu.org/licenses/agpl-3.0.html>.
#*******************************************************************************
#!/bin/bash

SERVICE_HOME="$( cd "$( dirname "$0" )" && pwd )"

SERVICE_BINARY="${project.build.finalName}.${project.packaging}"

cd $SERVICE_HOME

CTS_LIBS_PATH="./lib,$(cd ..)/lib"
# This is a bit convoluted but this seemed to be the only way to get quoted paths with spaces in
# in the -D parameters treated as part of the same argument to the java call and not split into
# separate arguments on those spaces
eval  `echo  java -DAPP_HOME="$SERVICE_HOME" -Dcts.workingDirectory="$SERVICE_HOME/tmp" -Dloader.path="$CTS_LIBS_PATH" -jar ./$SERVICE_BINARY`
