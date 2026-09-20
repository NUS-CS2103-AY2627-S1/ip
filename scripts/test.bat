@echo off
rem Runs compilation, JUnit tests, and Checkstyle.
setlocal
cd /d "%~dp0.."
call gradlew.bat check %*
exit /b %ERRORLEVEL%
