param(
    [string]$PlanPath = "test/ui-test-plan.md"
)

$ErrorActionPreference = "Stop"
$repositoryRoot = (Get-Location).Path
$planFile = Join-Path $repositoryRoot $PlanPath
$sessionLog = Join-Path $repositoryRoot "test/ui-test-session.log"

if (-not (Test-Path -LiteralPath $planFile)) {
    throw "UI test plan not found: $planFile"
}

$javaVersion = (cmd /c "java -version 2>&1" | Out-String)
if ($javaVersion -notmatch 'version "25') {
    throw "Java 25 is required to run UI tests. Current version: $javaVersion"
}

$plan = Get-Content -LiteralPath $planFile -Raw
$caseMatches = [regex]::Matches(
    $plan,
    '(?ms)^## Test: (?<name>.+?)\r?\n.*?^### Input\r?\n\s*```text\r?\n(?<input>.*?)\r?\n```.*?^### Expected output\r?\n\s*```text\r?\n(?<expected>.*?)\r?\n```'
)
if ($caseMatches.Count -eq 0) {
    throw "No valid test cases found in $PlanPath"
}

$workDirectory = Join-Path ([System.IO.Path]::GetTempPath()) ("ui-test-" + [guid]::NewGuid())
$classesDirectory = Join-Path $workDirectory "classes"
$launcherFile = Join-Path $workDirectory "BootLauncher.java"
New-Item -ItemType Directory -Force -Path $classesDirectory | Out-Null

try {
    @"
/** Starts the application at its functional entry point for UI testing. */
public class BootLauncher {
    public static void main(String[] args) {
        surveyprogram.DeviceG.boot();
    }
}
"@ | Set-Content -LiteralPath $launcherFile

    $sourceFiles = Get-ChildItem -Path (Join-Path $repositoryRoot "src/main/java") -Filter *.java -Recurse |
            Where-Object { $_.FullName -notmatch '[\\/]gui[\\/]|[\\/]Launcher\.java$' } |
            Select-Object -ExpandProperty FullName
    & javac -d $classesDirectory $sourceFiles $launcherFile
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed."
    }

    # Run from the temporary directory so persistence created by a test cannot
    # read or overwrite the user's real data/PERSIST.txt file.
    Set-Location -LiteralPath $workDirectory
    $transcript = [System.Collections.Generic.List[string]]::new()
    foreach ($case in $caseMatches) {
        $name = $case.Groups['name'].Value.Trim()
        $input = $case.Groups['input'].Value.TrimEnd("`r", "`n")
        $expected = $case.Groups['expected'].Value.TrimEnd("`r", "`n").Replace("`r`n", "`n")
        $caseDirectory = Join-Path $workDirectory ("case-" + $transcript.Count)
        New-Item -ItemType Directory -Force -Path $caseDirectory | Out-Null
        Set-Location -LiteralPath $caseDirectory
        $inputFile = Join-Path $caseDirectory "input.txt"
        [System.IO.File]::WriteAllText($inputFile, $input + [Environment]::NewLine)
        $actual = (Get-Content -LiteralPath $inputFile -Raw | & java -cp $classesDirectory BootLauncher 2>&1 | Out-String).TrimEnd("`r", "`n").Replace("`r`n", "`n")

        $transcript.Add("===== $name =====")
        $transcript.Add("Console input:")
        $transcript.Add($input)
        $transcript.Add("Console output:")
        $transcript.Add($actual)

        $expectedFragments = $expected -split "(?m)^---\r?$" | ForEach-Object { $_.Trim("`r", "`n") }
        $searchStart = 0
        $missingFragment = $null
        foreach ($fragment in $expectedFragments) {
            $fragmentIndex = $actual.IndexOf($fragment, $searchStart, [System.StringComparison]::Ordinal)
            if ($fragmentIndex -lt 0) {
                $missingFragment = $fragment
                break
            }
            $searchStart = $fragmentIndex + $fragment.Length
        }

        if ($null -ne $missingFragment) {
            $transcript.Add("RESULT: FAILED")
            $transcript.Add("Expected output:")
            $transcript.Add($expected)
            $transcript.Add("Missing output fragment:")
            $transcript.Add($missingFragment)
            $transcript.Add("Actual output:")
            $transcript.Add($actual)
            $transcript | Set-Content -LiteralPath $sessionLog
            $transcript -join [Environment]::NewLine | Write-Output
            throw "UI test failed: $name. Expected output was not found."
        }

        $transcript.Add("RESULT: PASSED")
    }

    $transcript | Set-Content -LiteralPath $sessionLog
    $transcript -join [Environment]::NewLine | Write-Output
    Write-Output "All $($caseMatches.Count) UI test(s) passed. Transcript: $sessionLog"
} finally {
    Set-Location -LiteralPath $repositoryRoot
    if (Test-Path -LiteralPath $workDirectory) {
        Remove-Item -LiteralPath $workDirectory -Recurse -Force
    }
}
