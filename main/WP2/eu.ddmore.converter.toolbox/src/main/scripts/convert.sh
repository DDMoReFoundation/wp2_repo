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

# Determine the directory in which this convert.sh script lives
CONVERTER_TOOLBOX_HOME="$( cd "$( dirname "$0" )" && pwd )"

CONVERTER_LIBS_PATH="$(cd $CONVERTER_TOOLBOX_HOME/../ && pwd)/lib/*"

java -cp "$CONVERTER_TOOLBOX_HOME/lib/*:$CONVERTER_LIBS_PATH" eu.ddmore.convertertoolbox.cli.Main -in $1 -out $2 -sn $3 -sv $4 -tn $5 -tv $6

