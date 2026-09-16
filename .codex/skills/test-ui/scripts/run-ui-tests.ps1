param(
    [string]$PlanPath
)

$ErrorActionPreference = 'Stop'

function Normalize-Newlines([string]$text) {
    return $text -replace "`r`n", "`n" -replace "`r", "`n"
}

$projectRoot = (Resolve-Path (Join-Path $PSScriptRoot '..\..\..\..')).Path
if (-not $PlanPath) {
    $PlanPath = Join-Path $projectRoot 'test\ui-test-plan.md'
}

$javacVersion = (& javac -version 2>&1 | Out-String).Trim()
if ($javacVersion -notmatch '^javac 25(\.|$)') {
    throw "Java 25 is required for UI tests, but found: $javacVersion"
}

$sourceRoot = Join-Path $projectRoot 'src\main\java'
$classOutput = Join-Path $projectRoot 'build\ui-test-classes'
New-Item -ItemType Directory -Path $classOutput -Force | Out-Null
$testWorkingDirectory = Join-Path $projectRoot 'build\ui-test-working-directory'
Remove-Item -LiteralPath $testWorkingDirectory -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $testWorkingDirectory -Force | Out-Null
$sources = @(Get-ChildItem -LiteralPath $sourceRoot -Filter '*.java' -File | ForEach-Object { $_.FullName })
& javac -d $classOutput @sources
if ($LASTEXITCODE -ne 0) {
    throw 'Compilation failed; UI tests were not run.'
}

$plan = Get-Content -LiteralPath $PlanPath -Raw
$testPattern = '(?ms)^## (?<name>[^\r\n]+)\r?\nAim: (?<aim>[^\r\n]+)\r?\n### Input\r?\n```text\r?\n(?<input>.*?)```\r?\n### Expected output\r?\n```text\r?\n(?<expected>.*?)```'
$testCases = [regex]::Matches($plan, $testPattern)
if ($testCases.Count -eq 0) {
    throw "No test cases were found in $PlanPath"
}

foreach ($testCase in $testCases) {
    $name = $testCase.Groups['name'].Value
    $aim = $testCase.Groups['aim'].Value
    $input = $testCase.Groups['input'].Value -replace "\r?\n$", ''
    $expected = Normalize-Newlines $testCase.Groups['expected'].Value

    $process = New-Object System.Diagnostics.Process
    $process.StartInfo.FileName = 'java'
    $process.StartInfo.Arguments = "-cp `"$classOutput`" StanVard"
    $process.StartInfo.UseShellExecute = $false
    $process.StartInfo.RedirectStandardInput = $true
    $process.StartInfo.RedirectStandardOutput = $true
    $process.StartInfo.RedirectStandardError = $true
    $process.StartInfo.WorkingDirectory = $testWorkingDirectory

    [void]$process.Start()
    $process.StandardInput.Write($input + "`n")
    $process.StandardInput.Close()
    $actual = Normalize-Newlines $process.StandardOutput.ReadToEnd()
    $standardError = $process.StandardError.ReadToEnd()
    $process.WaitForExit()

    Write-Host "=== $name ==="
    Write-Host "Aim: $aim"
    Write-Host 'Console input:'
    Write-Host $input
    Write-Host 'Console output:'
    Write-Host $actual

    if ($process.ExitCode -ne 0) {
        throw "Test '$name' ended with exit code $($process.ExitCode): $standardError"
    }
    if ($actual -cne $expected) {
        Write-Host 'Expected output:'
        Write-Host $expected
        throw "Test '$name' failed: actual output differs from expected output."
    }
}

Write-Host "All $($testCases.Count) UI test case(s) passed."
