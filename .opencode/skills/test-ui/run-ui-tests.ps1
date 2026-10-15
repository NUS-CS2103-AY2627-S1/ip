param(
    [string]$PlanPath = "test/ui-test-plan.md"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

$repoRoot = (Get-Location).Path

function Normalize-Text {
    param([string]$Text)

    if ($null -eq $Text) {
        return ""
    }

    return ($Text -replace "`r`n", "`n" -replace "`r", "`n").Trim()
}

function Format-Block {
    param([string[]]$Lines)

    if ($null -eq $Lines -or $Lines.Count -eq 0) {
        return ""
    }

    return (($Lines | ForEach-Object { [string]$_ }) -join "`n").Trim()
}

function Get-TestCasesFromPlan {
    param([string]$MarkdownPath)

    if (-not (Test-Path -LiteralPath $MarkdownPath)) {
        throw "Test plan not found: $MarkdownPath"
    }

    $content = Get-Content -LiteralPath $MarkdownPath -Raw
    $matches = [regex]::Matches($content, '(?ms)```json\s*(\{.*?\})\s*```')

    if ($matches.Count -eq 0) {
        throw "No JSON test cases found in $MarkdownPath"
    }

    $cases = @()
    foreach ($match in $matches) {
        $case = $match.Groups[1].Value | ConvertFrom-Json

        if ($case.commands.Count -ne $case.expectedOutputs.Count) {
            throw "Test case $($case.id) has mismatched commands and expectedOutputs counts."
        }

        $cases += $case
    }

    return $cases
}

function Build-Transcript {
    param(
        [string]$Prelude,
        [object]$Case,
        [string[]]$Responses
    )

    $lines = @()
    $lines += "=== $($Case.id) ==="
    $lines += "Aim: $($Case.aim)"

    if ((Normalize-Text $Prelude) -ne "") {
        $lines += ""
        $lines += "[Program Startup]"
        $lines += (Normalize-Text $Prelude)
    }

    $lines += ""
    $lines += "[Session]"

    for ($i = 0; $i -lt $Case.commands.Count; $i++) {
        $lines += "> $($Case.commands[$i])"
        $response = Normalize-Text $Responses[$i]
        if ($response -ne "") {
            $lines += $response
        }
    }

    return ($lines -join "`n")
}

function Run-TestCase {
    param([object]$Case)

    $inputText = (($Case.commands | ForEach-Object { [string]$_ }) -join "`n") + "`n"

    # Run each case in a fresh temp folder so the save file cannot leak
    # state between cases or touch the user's real data directory.
    $testDir = Join-Path ([System.IO.Path]::GetTempPath()) ("lenzabot-uitest-" + [guid]::NewGuid().ToString("N"))
    New-Item -ItemType Directory -Path $testDir | Out-Null
    Push-Location $testDir
    try {
        $rawOutput = $inputText | java -cp (Join-Path $repoRoot "src/main/java") lenzabot.LenZaBot 2>&1 | Out-String
    } finally {
        Pop-Location
        Remove-Item -LiteralPath $testDir -Recurse -Force
    }

    $segments = $rawOutput -split [regex]::Escape(">  ")
    $prelude = $segments[0]
    $responses = @()

    if ($segments.Count -gt 1) {
        $responses = @($segments[1..($segments.Count - 1)])
    }

    if ($responses.Count -ne $Case.commands.Count) {
        $transcript = Build-Transcript -Prelude $prelude -Case $Case -Responses $responses
        throw @"
Test case $($Case.id) produced $($responses.Count) response block(s), expected $($Case.commands.Count).

$transcript
"@
    }

    for ($i = 0; $i -lt $Case.commands.Count; $i++) {
        $expected = Format-Block -Lines $Case.expectedOutputs[$i]
        $actual = Normalize-Text $responses[$i]

        if ($expected -ne $actual) {
            $transcript = Build-Transcript -Prelude $prelude -Case $Case -Responses $responses
            throw @"
Test case $($Case.id) failed.
Aim: $($Case.aim)
Command: $($Case.commands[$i])

Expected:
$expected

Actual:
$actual

Transcript:
$transcript
"@
        }
    }

    return Build-Transcript -Prelude $prelude -Case $Case -Responses $responses
}

$cases = Get-TestCasesFromPlan -MarkdownPath $PlanPath

$sources = Get-ChildItem -Recurse -Filter *.java -Path (Join-Path $repoRoot "src/main/java") |
    Where-Object { $_.FullName -notmatch '[\\/]gui[\\/]|[\\/]Launcher\.java$' }
$sources = $sources.FullName
$compileOutput = javac -encoding UTF-8 $sources 2>&1 | Out-String
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed.`n$compileOutput"
}

foreach ($case in $cases) {
    try {
        $transcript = Run-TestCase -Case $case
        $transcript
        ""
    } catch {
        $_.Exception.Message
        exit 1
    }
}
