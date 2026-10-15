@ECHO OFF

if not exist ..\bin mkdir ..\bin
if exist ACTUAL.TXT del ACTUAL.TXT
if exist LOAD-ACTUAL.TXT del LOAD-ACTUAL.TXT
if exist DATE-ONLY-ACTUAL.TXT del DATE-ONLY-ACTUAL.TXT

REM Collect all Java files into a temporary list
dir /s /b ..\src\main\java\*.java > sources.txt

REM Compile using sources list
javac -cp ..\src\main\java -Xlint:none -d ..\bin @sources.txt
IF ERRORLEVEL 1 (
    echo ********** COMPILATION ERROR **********
    del sources.txt
    exit /b 1
)
del sources.txt

REM Back up the user's saved tasks before using a controlled test data file.
set TEST_FAILED=0
set HAD_SAVED_DATA=0
if not exist ..\data mkdir ..\data
if exist ..\data\bubu.txt (
    copy /y ..\data\bubu.txt ..\data\bubu.txt.test-backup >nul
    set HAD_SAVED_DATA=1
)

REM Verify that a new chatbot session loads tasks saved on disk.
copy /y LOAD-DATA.TXT ..\data\bubu.txt >nul
pushd ..
java -classpath bin bubu.Main < text-ui-test\LOAD-INPUT.TXT > text-ui-test\LOAD-ACTUAL.TXT
popd
FC LOAD-ACTUAL.TXT LOAD-EXPECTED.TXT
if errorlevel 1 set TEST_FAILED=1

REM Start with an empty list, then verify the existing UI and saving tests.
type nul > ..\data\bubu.txt
pushd ..
java -classpath bin bubu.Main < text-ui-test\input.txt > text-ui-test\ACTUAL.TXT
popd
FC ACTUAL.TXT EXPECTED.TXT
if errorlevel 1 set TEST_FAILED=1

REM Confirm that every successful list change saved the final task list.
FC ..\data\bubu.txt PERSISTENCE-EXPECTED.TXT
if errorlevel 1 set TEST_FAILED=1

REM Verify that a date-only deadline receives the documented 23:59 default time.
type nul > ..\data\bubu.txt
pushd ..
java -classpath bin bubu.Main < text-ui-test\DATE-ONLY-INPUT.TXT > text-ui-test\DATE-ONLY-ACTUAL.TXT
popd
FC DATE-ONLY-ACTUAL.TXT DATE-ONLY-EXPECTED.TXT
if errorlevel 1 set TEST_FAILED=1
FC ..\data\bubu.txt DATE-ONLY-PERSISTENCE-EXPECTED.TXT
if errorlevel 1 set TEST_FAILED=1

REM Restore the user's original saved data after all tests complete.
if "%HAD_SAVED_DATA%"=="1" (
    move /y ..\data\bubu.txt.test-backup ..\data\bubu.txt >nul
) else (
    del ..\data\bubu.txt
)

exit /b %TEST_FAILED%
