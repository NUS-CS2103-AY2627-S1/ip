@echo off
rem Starts Duchess's command-line interface from any working directory.
setlocal
cd /d "%~dp0.."
call gradlew.bat runCli %*
exit /b %ERRORLEVEL%
