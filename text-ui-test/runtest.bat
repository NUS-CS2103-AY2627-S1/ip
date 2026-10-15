@ECHO OFF

PowerShell -NoProfile -ExecutionPolicy Bypass -File "%~dp0runtest.ps1"
EXIT /B %ERRORLEVEL%
