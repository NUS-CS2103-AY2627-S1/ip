<#!
.SYNOPSIS
Runs exact-output console UI tests documented in a Markdown test plan.
#>

param([string]$PlanPath = "test/ui-test-plan.md")

function Normalize-Output {
    param([string]$Text)
    return ($Text -replace "`r`n", "`n" -replace "`r", "`n") -replace "`n$", ""
}

function Invoke-TestCase {
    param([string]$Command, [string]$UserInput, [string]$WorkingDirectory)

    $startInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $startInfo.FileName = "cmd.exe"
    $startInfo.Arguments = "/d /c $Command"
    $startInfo.WorkingDirectory = $WorkingDirectory
    $startInfo.UseShellExecute = $false
    $startInfo.RedirectStandardInput = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true

    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $startInfo
    [void] $process.Start()
    $process.StandardInput.Write($UserInput + "`n")
    $process.StandardInput.Close()
    $standardOutput = $process.StandardOutput.ReadToEnd()
    $standardError = $process.StandardError.ReadToEnd()
    if (-not $process.WaitForExit(15000)) {
        $process.Kill()
        throw "The program timed out after 15 seconds."
    }

    return [PSCustomObject]@{
        ExitCode = $process.ExitCode
        Output = Normalize-Output ($standardOutput + $standardError)
    }
}

try {
    $resolvedPlanPath = (Resolve-Path -LiteralPath $PlanPath -ErrorAction Stop).Path
    $planText = [System.IO.File]::ReadAllText($resolvedPlanPath)
} catch {
    Write-Error "Cannot read test plan: $_"
    exit 2
}

$pattern = '(?ms)^## Test \d+: (?<Name>.+?)\r?\n\r?\n\*\*Aim:\*\* (?<Aim>.+?)\r?\n\r?\n\*\*Run:\*\* `(?<Command>.+?)`\r?\n\r?\n\*\*Input:\*\*\r?\n```text\r?\n(?<Input>.*?)\r?\n```\r?\n\r?\n\*\*Expected output:\*\*\r?\n```text\r?\n(?<Expected>.*?)\r?\n```'
$matches = [regex]::Matches($planText, $pattern)
if ($matches.Count -eq 0) {
    Write-Error "No test cases match the required format."
    exit 2
}

$workingDirectory = Split-Path -Parent (Split-Path -Parent $resolvedPlanPath)
foreach ($match in $matches) {
    $name = $match.Groups['Name'].Value
    $aim = $match.Groups['Aim'].Value
    $command = $match.Groups['Command'].Value
    $caseInput = $match.Groups['Input'].Value
    $expected = Normalize-Output $match.Groups['Expected'].Value

    try {
        $result = Invoke-TestCase $command $caseInput $workingDirectory
    } catch {
        Write-Host "`nTest failed: $name"
        Write-Host $_
        exit 1
    }

    Write-Host "`n=== Test: $name ==="
    Write-Host "Aim: $aim"
    Write-Host "Run: $command"
    Write-Host "Console input:"
    Write-Host $caseInput
    Write-Host "Console output:"
    Write-Host $result.Output

    if ($result.ExitCode -ne 0 -or $result.Output -cne $expected) {
        Write-Host "Test failed. Expected output:"
        Write-Host $expected
        Write-Host "Actual output:"
        Write-Host $result.Output
        exit 1
    }
}

Write-Host "`nAll $($matches.Count) UI test case(s) passed."
