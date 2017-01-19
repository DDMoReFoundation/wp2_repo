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

REM  This Windows Powershell command (built in to Windows 7) is merely a 'nice-to-have' that
REM  sets a large console buffer size in order that the entire output can be scrolled around.
powershell -command "&{$H=get-host;$W=$H.ui.rawui;$B=$W.buffersize;$B.height=2000;$W.buffersize=$B;}" <NUL 1>NUL 2>NUL

REM  Locations without trailing '\'
SET SERVICE_HOME=%~dp0
IF %SERVICE_HOME:~-1%==\ SET SERVICE_HOME=%SERVICE_HOME:~0,-1%

CD "%SERVICE_HOME%"

SET SERVICE_BINARY=${project.build.finalName}.${project.packaging}

REM Converter Toolbox Service requires absolute path of the additional classpath entries
REM if they reside outside its home directory
pushd..
SET CONVERTER_LIBS=%cd%
popd

SET CTS_LIBS_PATH="./lib,%CONVERTER_LIBS%/lib"

java.exe %JAVA_OPTS% -DAPP_HOME="%SERVICE_HOME%" -Dcts.workingDirectory="%SERVICE_HOME%\tmp" -Dloader.path=%CTS_LIBS_PATH% -jar %SERVICE_BINARY%

EXIT
