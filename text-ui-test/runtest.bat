@ECHO OFF

REM create bin directory if it doesn't exist
if not exist ..\bin mkdir ..\bin

REM delete output from previous run
if exist ACTUAL.TXT del ACTUAL.TXT

REM delete the save file left behind by a previous run, so the first
REM session below always starts with an empty list
if exist data rmdir /s /q data

REM compile the code into the bin folder
REM The sources now sit in package folders under the source root, so they are
REM gathered with dir /s /b rather than a single *.java wildcard. The source
REM root itself is no longer on the classpath: javac locates a class from its
REM package name, and stale .class files there could shadow a fresh one.
dir /s /b ..\src\main\java\*.java > sources.txt
javac -Xlint:none -d ..\bin @sources.txt
REM The errorlevel is checked before sources.txt is deleted, because del would
REM reset it and a build failure would go unnoticed.
IF ERRORLEVEL 1 (
    del sources.txt
    echo ********** BUILD FAILURE **********
    exit /b 1
)
del sources.txt
REM no error here, errorlevel == 0

REM FIRST RUN: feed commands from input.txt and redirect the output to ACTUAL.TXT
java -classpath ..\bin goat.Goat < input.txt > ACTUAL.TXT

REM show what was written to the hard disk, so the saved format is tested too
echo ===== SAVED FILE =====>> ACTUAL.TXT
type data\goat.txt >> ACTUAL.TXT

REM SECOND RUN: start again without adding anything, to prove the tasks reload
echo ===== RESTART =====>> ACTUAL.TXT
java -classpath ..\bin goat.Goat < input-restart.txt >> ACTUAL.TXT

REM THIRD RUN: damage the save file, then start the program again, to check
REM that a hand-edited file with a mistake in it is reported, not crashed on
echo ===== DAMAGED FILE =====>> ACTUAL.TXT
(
echo T ^| 1 ^| read book
echo D ^| 0 ^| return book ^| 2 Dec 2019
) > data\goat.txt
type data\goat.txt >> ACTUAL.TXT
echo ===== RECOVERY =====>> ACTUAL.TXT
java -classpath ..\bin goat.Goat < input-corrupt.txt >> ACTUAL.TXT

REM FOURTH RUN: delete only the save file, keeping the data folder, which is
REM the other first-run situation the requirement names
echo ===== FILE DELETED, FOLDER KEPT =====>> ACTUAL.TXT
del data\goat.txt
java -classpath ..\bin goat.Goat < input-nofile.txt >> ACTUAL.TXT
echo ===== FILE REMADE =====>> ACTUAL.TXT
type data\goat.txt >> ACTUAL.TXT

REM compare the output to the expected output
FC ACTUAL.TXT EXPECTED.TXT
