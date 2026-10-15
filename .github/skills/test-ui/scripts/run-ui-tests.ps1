param(
    [string]$PlanPath = "test\ui-test-plan.md",
    [string]$RepoRoot = (Get-Location).Path
)

$ErrorActionPreference = "Stop"

function Get-Section {
    param(
        [string]$CaseText,
        [string]$SectionName
    )

    $pattern = "(?ms)^###\s+$([regex]::Escape($SectionName))\s*\r?\n(.*?)(?=^###\s+|\z)"
    $match = [regex]::Match($CaseText, $pattern)
    if (-not $match.Success) {
        throw "Missing '$SectionName' section."
    }

    $lines = $match.Groups[1].Value -split "\r?\n"
    $contentLines = @($lines | Where-Object { $_ -notmatch '^\s*`{3}\s*' })
    return ($contentLines -join "`n").TrimEnd("`r", "`n")
}

function Normalize-Output {
    param([string]$Text)

    $normalized = $Text.Replace(([string][char]13 + [char]10), [string][char]10)
    $normalized = $normalized.Replace([string][char]13, "")
    $lines = $normalized -split [char]10 | ForEach-Object { $_.TrimEnd() }
    return ($lines -join [char]10).TrimEnd([char]10)
}

$resolvedPlanPath = [IO.Path]::GetFullPath((Join-Path $RepoRoot $PlanPath))
$plan = Get-Content -Raw -LiteralPath $resolvedPlanPath
$caseMatches = [regex]::Matches($plan, "(?ms)^##\s+Case:\s*(.+?)\s*\r?\n(.*?)(?=^##\s+Case:|\z)")

if ($caseMatches.Count -eq 0) {
    throw "No '## Case:' sections found in $resolvedPlanPath."
}

$passed = 0
foreach ($caseMatch in $caseMatches) {
    $caseName = $caseMatch.Groups[1].Value.Trim()
    $caseText = $caseMatch.Groups[2].Value
    $command = (Get-Section $caseText "Command").Trim()
    $inputText = Get-Section $caseText "Input"
    $expected = Normalize-Output (Get-Section $caseText "Expected output")

    Push-Location $RepoRoot
    try {
        $inputPath = Join-Path $env:TEMP "test-ui-input.txt"
        [IO.File]::WriteAllText($inputPath, $inputText)
        $actual = (Get-Content -Raw $inputPath | & cmd.exe /d /c $command 2>&1 | Out-String)
        $actual = Normalize-Output $actual
    }
    finally {
        Pop-Location
    }

    Write-Output "=== $caseName ==="
    Write-Output "Console input:"
    Write-Output $inputText
    Write-Output "Console output:"
    Write-Output $actual

    if ($actual -ne $expected) {
        Write-Output "FAILED: $caseName"
        Write-Output "Actual output:"
        Write-Output $actual
        Write-Output "Expected output:"
        Write-Output $expected
        exit 1
    }

    $passed++
    Write-Output "PASSED: $caseName"
}

Write-Output "All $passed UI test case(s) passed."