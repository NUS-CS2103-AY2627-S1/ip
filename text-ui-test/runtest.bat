@ECHO OFF
SETLOCAL
CD /D "%~dp0"
IF NOT EXIST bin MKDIR bin
javac -d bin ..\src\main\java\*.java || EXIT /B 1
java -cp bin Orbit < input.txt > ACTUAL.TXT
FC EXPECTED.TXT ACTUAL.TXT
IF ERRORLEVEL 1 EXIT /B 1
ECHO Text UI test passed.
