$ErrorActionPreference = "Stop"
$runnerPath = Join-Path $PSScriptRoot "..\.codex\skills\test-ui\scripts\run-ui-test.ps1"
& $runnerPath
exit $LASTEXITCODE
