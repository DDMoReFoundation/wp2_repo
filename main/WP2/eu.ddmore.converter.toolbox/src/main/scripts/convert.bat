@rem ***************************************************************************
@rem Copyright (C) 2016 Mango Business Solutions Ltd, http://www.mango-solutions.com
@rem
@rem This program is free software: you can redistribute it and/or modify it under
@rem the terms of the GNU Affero General Public License as published by the
@rem Free Software Foundation, version 3.
@rem
@rem This program is distributed in the hope that it will be useful,
@rem but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
@rem or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
@rem for more details.
@rem
@rem You should have received a copy of the GNU Affero General Public License along
@rem with this program. If not, see <http://www.gnu.org/licenses/agpl-3.0.html>.
@rem ***************************************************************************
@echo off

SET HOME=%~dp0
SET CURRENT_DIR=%cd%

REM Extending the classpath with location of directory holding converters and their dependencies
cd "%HOME%"
pushd..
SET PARENT_DIR=%cd%
popd
SET CONVERTER_LIBS="%PARENT_DIR%/lib/*"
cd "%CURRENT_DIR%"

java.exe -cp "%HOME%/lib/*;%CONVERTER_LIBS%" eu.ddmore.convertertoolbox.cli.Main -in %1 -out %2 -sn %3 -sv %4 -tn %5 -tv %6
