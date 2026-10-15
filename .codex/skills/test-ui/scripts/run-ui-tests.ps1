param(
    [string]$PlanPath = "test/ui-test-plan.md"
)

$ErrorActionPreference = "Stop"

function Get-SectionBlock {
    param([string]$Text, [string]$Heading)
    $pattern = '(?ms)^### ' + [regex]::Escape($Heading) + '\r?\n.*?^```text\r?\n(.*?)^```'
    $match = [regex]::Match($Text, $pattern)
    if (-not $match.Success) {
        throw "Could not find a text block after '$Heading'."
    }
    return $match.Groups[1].Value.TrimEnd("`r", "`n")
}

function Normalize-Output {
    param([string]$Text)
    $lines = (($Text -replace "\r\n", "`n") -replace "\r", "`n").Split("`n")
    return (($lines | ForEach-Object { $_.TrimEnd() }) -join "`n").TrimEnd("`n")
}

function Get-FirstDifference {
    param([string]$Expected, [string]$Actual)
    $sharedLength = [Math]::Min($Expected.Length, $Actual.Length)
    for ($index = 0; $index -lt $sharedLength; $index++) {
        if ($Expected[$index] -cne $Actual[$index]) {
            return ("First difference at character {0}: expected '{1}', actual '{2}'." -f $index, $Expected[$index], $Actual[$index])
        }
    }
    return "Outputs differ in length: expected $($Expected.Length) characters, actual $($Actual.Length) characters."
}

if (-not (Test-Path -LiteralPath $PlanPath)) {
    throw "UI test plan not found: $PlanPath"
}

$plan = Get-Content -Raw -LiteralPath $PlanPath
$caseMatches = [regex]::Matches($plan, '(?m)^## Test Case: (.+)$')
if ($caseMatches.Count -eq 0) {
    throw "The UI test plan contains no test cases."
}

$outputDirectory = Join-Path $PSScriptRoot "..\..\..\..\out\ui-tests"
New-Item -ItemType Directory -Force -Path $outputDirectory | Out-Null

Write-Host "Compiling the console application with Java 25..."
& javac -d $outputDirectory src/main/java/*.java
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed."
}

foreach ($caseMatch in $caseMatches) {
    $caseName = $caseMatch.Groups[1].Value.Trim()
    $caseStart = $caseMatch.Index
    $nextCase = $plan.IndexOf("`n## Test Case:", $caseStart + 1)
    if ($nextCase -lt 0) {
        $caseText = $plan.Substring($caseStart)
    } else {
        $caseText = $plan.Substring($caseStart, $nextCase - $caseStart)
    }

    $input = Get-SectionBlock $caseText "Input"
    $expected = Normalize-Output (Get-SectionBlock $caseText "Expected output")

    # Each planned session is isolated, while the application itself still persists
    # tasks normally between real launches.
    $dataFile = Join-Path (Get-Location) "data\whimsybot.txt"
    if (Test-Path -LiteralPath $dataFile) {
        Remove-Item -LiteralPath $dataFile -Force
    }

    # Input is fed via a temp file with shell redirection rather than Process.StandardInput:
    # merely accessing that property makes .NET write a UTF-8 BOM onto the pipe before any
    # content, which corrupts the first line Java reads.
    $inputFile = Join-Path $outputDirectory "stdin.txt"
    [System.IO.File]::WriteAllText($inputFile, ($input + "`n"), (New-Object System.Text.UTF8Encoding($false)))

    $processInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $processInfo.FileName = "cmd.exe"
    $processInfo.Arguments = "/c java -cp `"$outputDirectory`" WhimsyBot < `"$inputFile`""
    $processInfo.RedirectStandardOutput = $true
    $processInfo.RedirectStandardError = $true
    $processInfo.UseShellExecute = $false

    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $processInfo
    [void]$process.Start()
    $actual = Normalize-Output ($process.StandardOutput.ReadToEnd())
    $errorOutput = Normalize-Output ($process.StandardError.ReadToEnd())
    $process.WaitForExit()
    if ($errorOutput) {
        $actual = "$actual`n[stderr]`n$errorOutput"
    }

    Write-Host "`n=== $caseName ==="
    Write-Host "Console input:"
    Write-Host $input
    Write-Host "Console output:"
    Write-Host $actual

    if ($process.ExitCode -ne 0 -or $actual -cne $expected) {
        Write-Host "`nFAILED: $caseName" -ForegroundColor Red
        Write-Host "Expected output:"
        Write-Host $expected
        Write-Host "Actual output:"
        Write-Host $actual
        Write-Host (Get-FirstDifference $expected $actual)
        exit 1
    }
    Write-Host "PASSED: $caseName" -ForegroundColor Green
}

Write-Host "`nAll UI test cases passed."
