@echo off
rem Starts Duchess's JavaFX interface from any working directory.
setlocal
cd /d "%~dp0.."
call gradlew.bat run %*
exit /b %ERRORLEVEL%
