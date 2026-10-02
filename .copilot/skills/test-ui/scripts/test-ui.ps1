param(
    [string]$PlanPath = "test\ui-test-plan.md"
)

# Read the plan file
if (-not (Test-Path $PlanPath)) {
    Write-Host "Plan file not found: $PlanPath" -ForegroundColor Red
    exit 2
}

$raw = Get-Content $PlanPath -Raw -ErrorAction Stop

# Regex to find test cases in the markdown. Each test case uses the format:
# ### Test: <id>
# Aim: <aim>
# Command: <command>
# Expected:
# ```
# <expected text>
# ```

$pattern = [regex]::new('(?ms)^###\s*Test:\s*(?<id>[^\r\n]+)\s*\r?\n(?:Aim:\s*(?<aim>[^\r\n]+)\s*\r?\n)?Command:\s*(?<cmd>[^\r\n]+)\s*\r?\nExpected:\s*\r?\n```\r?\n(?<expected>.*?)\r?\n```')
$matches = $pattern.Matches($raw)

if ($matches.Count -eq 0) {
    Write-Host "No test cases found in $PlanPath" -ForegroundColor Yellow
    exit 3
}

$sessionLog = @()
$testIndex = 0
foreach ($m in $matches) {
    $testIndex++
    $id = $m.Groups['id'].Value.Trim()
    $aim = $m.Groups['aim'].Value.Trim()
    $cmd = $m.Groups['cmd'].Value.Trim()
    $expectedRaw = $m.Groups['expected'].Value

    Write-Host "===== Test ${testIndex}: ${id} ====="
    Write-Host "Aim: $aim"
    Write-Host "Command: $cmd"

    $sessionLog += "=> $cmd"

    # Run the command via cmd.exe so command-line parsing is consistent for complex commands.
    # Capture stdout and stderr merged.
    try {
        $outputLines = & cmd.exe /c "$cmd" 2>&1
    } catch {
        $outputLines = $_.Exception.Message
    }

    if ($null -eq $outputLines) { $outputLines = @('') }
    $actual = ($outputLines -join "`n")

    # Normalize newlines and trim trailing whitespace
    $normActual = ($actual -replace "`r`n","`n").Trim() -replace "\s+$",""
    $normExpected = ($expectedRaw -replace "`r`n","`n").Trim() -replace "\s+$",""

    $sessionLog += $actual

    if ($normActual -ne $normExpected) {
        Write-Host "TEST FAILED: $id" -ForegroundColor Red
        Write-Host "--- Expected ---" -ForegroundColor Yellow
        Write-Host $expectedRaw
        Write-Host "--- Actual ---" -ForegroundColor Yellow
        Write-Host $actual
        Write-Host "Terminating test session." -ForegroundColor Red
        exit 1
    } else {
        Write-Host "PASS" -ForegroundColor Green
    }
}

Write-Host "\nAll tests passed. Session transcript:" -ForegroundColor Green
Write-Host "-----------------------------------------"
foreach ($line in $sessionLog) {
    Write-Host $line
}

exit 0
