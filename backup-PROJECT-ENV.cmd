REM ######################################################################################
REM                               <PROJECT> / <ENV>
REM ######################################################################################

set PROJECT=<PROJECT>

set ENVIRONNEMENT=<ENV>

set URL=jdbc:mysql://<SERVER>:<PORT>/<DATABASENAME>
set SCHEMA=SCHEMA
set USERID=USERID
set PASSWD=PASSWD

REM (oracle.thin or mysql)
set DRIVER=mysql

REM ######################################################################################

set PARAMS=-Durl=%URL% -Dschema=%SCHEMA% -Duserid=%USERID% -Dpasswd=%PASSWD% -Ddriver=%DRIVER%
echo ENVIRONNEMENT=%ENVIRONNEMENT%
echo PARAMS=%PARAMS%

cd lib\

java %PARAMS% -Dexport.ddl=true -Dexport.ddl.native=false -Dexport.data=false -jar dbexport.jar > ../_FETCHING_%PROJECT%_%ENVIRONNEMENT%_ddl.sql
@if %ERRORLEVEL% GEQ 1 (
	echo Error : DDL.
	pause
	exit %ERRORLEVEL% 
)
java %PARAMS% -Dexport.ddl=true -Dexport.ddl.native=true  -Dexport.data=false -jar dbexport.jar > ../_FETCHING_%PROJECT%_%ENVIRONNEMENT%_ddl-native.sql
@if %ERRORLEVEL% GEQ 1 (
	echo Error : native DDL.
	pause
	exit %ERRORLEVEL% 
)
java %PARAMS% -Dexport.ddl=false -Dexport.data=true                           -jar dbexport.jar > ../_FETCHING_%PROJECT%_%ENVIRONNEMENT%_data.sql
@if %ERRORLEVEL% GEQ 1 (
	echo Error : Data.
	pause
	exit %ERRORLEVEL% 
)

cd ..

@if not exist "%PROJECT%" mkdir "%PROJECT%"
@if not exist "%PROJECT%\%ENVIRONNEMENT%" mkdir "%PROJECT%\%ENVIRONNEMENT%"

@echo off
For /f "tokens=1-3 delims=/ " %%a in ('date /t') do (set currentdate=%%c-%%b-%%a)

move /Y  _FETCHING_%PROJECT%_%ENVIRONNEMENT%_ddl.sql          "%PROJECT%\%ENVIRONNEMENT%\%PROJECT%-%ENVIRONNEMENT%-%currentdate%@%TIME::=-%-ddl.sql"
move /Y  _FETCHING_%PROJECT%_%ENVIRONNEMENT%_ddl-native.sql   "%PROJECT%\%ENVIRONNEMENT%\%PROJECT%-%ENVIRONNEMENT%-%currentdate%@%TIME::=-%-ddl-native.sql"
move /Y  _FETCHING_%PROJECT%_%ENVIRONNEMENT%_data.sql         "%PROJECT%\%ENVIRONNEMENT%\%PROJECT%-%ENVIRONNEMENT%-%currentdate%@%TIME::=-%-data.sql"

pause