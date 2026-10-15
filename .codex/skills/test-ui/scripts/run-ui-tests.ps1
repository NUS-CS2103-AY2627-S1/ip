param(
    [string]$Plan = "test/ui-test-plan.md",
    [string]$SessionRecord = "test/ui-test-session.md"
)

$ErrorActionPreference = "Stop"

function Normalize-Output {
    param([string]$Text)
    return $Text.Replace("`r`n", "`n").Replace("`r", "`n").TrimEnd("`n")
}

function Get-FencedBlock {
    param([string]$CaseText, [string]$Label)
    $escapedLabel = [regex]::Escape($Label)
    $pattern = '(?ms)^\*\*' + $escapedLabel + ':\*\*[ \t]*\r?\n```text\r?\n(.*?)\r?\n```'
    $match = [regex]::Match($CaseText, $pattern)
    if (-not $match.Success) {
        throw "A test case is missing a '$Label' text block."
    }
    return $match.Groups[1].Value
}

if (-not (Test-Path -LiteralPath $Plan)) {
    throw "UI test plan not found: $Plan"
}

$planText = Get-Content -LiteralPath $Plan -Raw
$runMatch = [regex]::Match($planText, "(?m)^<!--\s*test-ui-run:\s*(.+?)\s*-->$")
if (-not $runMatch.Success) {
    throw "The plan must declare a run command, e.g. <!-- test-ui-run: java -cp build/classes Edith -->."
}
$runCommand = $runMatch.Groups[1].Value.Trim()
$cases = [regex]::Matches($planText, "(?ms)^## Test case[^\r\n]*\r?\n.*?(?=^## Test case|\z)")
if ($cases.Count -eq 0) {
    throw "No test cases were found. Use headings beginning with '## Test case'."
}

$record = [System.Collections.Generic.List[string]]::new()
$record.Add("# UI Test Session")
$record.Add("")
$record.Add("Run command: ``$runCommand``")
$record.Add("")

foreach ($case in $cases) {
    $caseText = $case.Value.TrimEnd()
    $heading = ($caseText -split "\r?\n")[0]
    $inputs = Get-FencedBlock -CaseText $caseText -Label "Inputs"
    $expected = Get-FencedBlock -CaseText $caseText -Label "Expected output"

    $processInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $processInfo.FileName = "cmd.exe"
    $processInfo.Arguments = "/d /c $runCommand"
    $processInfo.UseShellExecute = $false
    $processInfo.RedirectStandardInput = $true
    $processInfo.RedirectStandardOutput = $true
    $processInfo.RedirectStandardError = $true
    $processInfo.CreateNoWindow = $true
    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $processInfo
    [void]$process.Start()
    $process.StandardInput.Write($inputs)
    $process.StandardInput.Close()
    $actual = $process.StandardOutput.ReadToEnd()
    $standardError = $process.StandardError.ReadToEnd()
    $process.WaitForExit()
    if ($standardError) {
        $actual += $standardError
    }

    $passed = (Normalize-Output $actual) -ceq (Normalize-Output $expected)
    $record.Add($heading)
    $record.Add("")
    $record.Add("Result: **$(if ($passed) { 'PASS' } else { 'FAIL' })** (exit code $($process.ExitCode))")
    $record.Add("")
    $record.Add("### Console input")
    $record.Add('```text')
    $record.Add($inputs)
    $record.Add('```')
    $record.Add("")
    $record.Add("### Actual console output")
    $record.Add('```text')
    $record.Add((Normalize-Output $actual))
    $record.Add('```')

    if (-not $passed) {
        $record.Add("")
        $record.Add("### Expected console output")
        $record.Add('```text')
        $record.Add((Normalize-Output $expected))
        $record.Add('```')
        Set-Content -LiteralPath $SessionRecord -Value ($record -join "`n") -NoNewline
        Write-Host "FAILED: $heading. See $SessionRecord for the actual and expected outputs."
        exit 1
    }

    $record.Add("")
}

Set-Content -LiteralPath $SessionRecord -Value ($record -join "`n") -NoNewline
Write-Host "PASSED: $($cases.Count) UI test case(s). See $SessionRecord."
