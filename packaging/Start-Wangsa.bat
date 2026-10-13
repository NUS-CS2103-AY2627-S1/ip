@echo off
setlocal
cd /d "%~dp0"
where java >nul 2>&1
if errorlevel 1 (
    echo Wangsa needs Java 25. Install it, then open this file again.
    echo See QUICK-START.txt for the download link.
    pause
    exit /b 1
)
java -jar "%~dp0Wangsa.jar"
if errorlevel 1 (
    echo.
    echo Wangsa could not start. Check that Java 25 is installed.
    echo Keep Wangsa.jar in this extracted folder. See QUICK-START.txt for help.
    pause
    exit /b 1
)
