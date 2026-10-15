param(
    [string]$PlanPath = "test\ui-test-plan.md"
)

# Read the plan file and extract the JSON block
$absPlan = Join-Path (Get-Location) $PlanPath
if (-not (Test-Path $absPlan)) {
    Write-Error "Plan not found: $absPlan"
    exit 2
}

$content = Get-Content $absPlan -Raw
$match = [regex]::Match($content, '```json\s*(\{[\s\S]*?\})\s*```', [System.Text.RegularExpressions.RegexOptions]::Singleline)
if (-not $match.Success) {
    Write-Error "No JSON test block found in plan ($absPlan). Put a JSON block surrounded by ```json ... ```"
    exit 2
}

$json = $match.Groups[1].Value
try {
    $plan = ConvertFrom-Json $json
} catch {
    Write-Error "Invalid JSON in test plan: $_"
    exit 2
}

$sessionLog = "test\ui-test-session.log"
Remove-Item $sessionLog -ErrorAction SilentlyContinue

foreach ($t in $plan.tests) {
    Write-Output "=== Test: $($t.id) - $($t.aim) ==="
    $cmd = $t.command
    $input = $t.input
    $expected = ($t.expected -replace "\r\n", "\n")

    $psi = New-Object System.Diagnostics.ProcessStartInfo
    $psi.FileName = "cmd.exe"
    $psi.Arguments = "/c $cmd"
    $psi.RedirectStandardInput = $true
    $psi.RedirectStandardOutput = $true
    $psi.RedirectStandardError = $true
    $psi.UseShellExecute = $false

    $proc = New-Object System.Diagnostics.Process
    $proc.StartInfo = $psi
    $proc.Start() | Out-Null

    if ($input -and $input -ne "") {
        $proc.StandardInput.WriteLine($input)
        $proc.StandardInput.Close()
    } else {
        $proc.StandardInput.Close()
    }

    $stdout = $proc.StandardOutput.ReadToEnd()
    $stderr = $proc.StandardError.ReadToEnd()
    $proc.WaitForExit()

    $actual = ($stdout -replace "\r\n", "\n")

    $entry = @()
    $entry += "COMMAND: $cmd"
    $entry += "INPUT: $input"
    $entry += "EXPECTED: $expected"
    $entry += "ACTUAL: $actual"
    $entry += "STDERR: $stderr"
    $entry += "EXITCODE: $($proc.ExitCode)"
    $entry += "---"
    $entryText = ($entry -join "`n") + "`n"

    Add-Content -Path $sessionLog -Value $entryText
    Write-Output $entryText

    if ($actual -ne $expected) {
        Write-Error "Test failed: $($t.id)"
        Write-Error "Expected:`n$expected"
        Write-Error "Actual:`n$actual"
        exit 1
    }
}

Write-Output "All tests passed. Session log: $sessionLog"
exit 0
