@echo off
rem Runs the repository's exact console UI regression plan.
setlocal
cd /d "%~dp0.."

where py >nul 2>&1
if %ERRORLEVEL% EQU 0 (
    py -3 .codex\skills\test-ui\scripts\run_ui_tests.py %*
) else (
    python .codex\skills\test-ui\scripts\run_ui_tests.py %*
)
exit /b %ERRORLEVEL%
