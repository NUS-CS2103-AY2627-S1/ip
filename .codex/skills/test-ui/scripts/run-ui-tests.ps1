param(
    [string]$PlanPath = "test/ui-test-plan.md"
)

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

function Get-CodeBlock {
    param(
        [string]$CaseText,
        [string]$Heading,
        [switch]$Required
    )

    $pattern = '(?ms)^### ' + [regex]::Escape($Heading) + '\s*\r?\n```(?:text)?\r?\n(.*?)\r?\n```'
    $match = [regex]::Match($CaseText, $pattern)
    if (-not $match.Success) {
        if ($Required) {
            throw "Missing '$Heading' code block in a test case."
        }
        return $null
    }
    return $match.Groups[1].Value
}

function Normalize-ConsoleOutput {
    param([string]$Text)

    $lines = ($Text -replace "`r`n", "`n") -split "`n"
    $normalizedLines = foreach ($line in $lines) {
        $line -replace '^ ', ''
    }
    return ($normalizedLines -join "`n").TrimEnd("`n")
}

function Normalize-FileContent {
    param([string]$Text)

    return (($Text -replace "`r`n", "`n")).TrimEnd("`n")
}

if (-not (Test-Path -LiteralPath $PlanPath)) {
    throw "UI test plan not found: $PlanPath"
}

$javacVersion = (& javac -version 2>&1 | Out-String)
if ($javacVersion -notmatch "javac 25\.") {
    throw "Java 25 is required, but found: $javacVersion"
}

$plan = Get-Content -Raw -LiteralPath $PlanPath
$caseMatches = [regex]::Matches($plan, "(?ms)^## Test case: (.+?)\r?$\r?\n(.*?)(?=^## Test case:|\z)")
if ($caseMatches.Count -eq 0) {
    throw "The UI test plan contains no test cases."
}

$classDirectory = Join-Path ([System.IO.Path]::GetTempPath()) ("alzara-ui-classes-" + [guid]::NewGuid())
New-Item -ItemType Directory -Path $classDirectory | Out-Null

try {
    $sources = Get-ChildItem -Path "src/main/java" -Filter "*.java" -File -Recurse | Select-Object -ExpandProperty FullName
    $compilerOutput = & javac -d $classDirectory $sources 2>&1 | Out-String
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed:`n$compilerOutput"
    }

    foreach ($caseMatch in $caseMatches) {
        $name = $caseMatch.Groups[1].Value.Trim()
        $caseText = $caseMatch.Groups[2].Value

        # Each test case gets its own working directory, so its data/alzara.txt
        # save file never touches the real project folder and never leaks into
        # the next test case.
        $workDirectory = Join-Path ([System.IO.Path]::GetTempPath()) ("alzara-ui-run-" + [guid]::NewGuid())
        New-Item -ItemType Directory -Path $workDirectory | Out-Null

        try {
            $saveFileBefore = Get-CodeBlock $caseText "Save file before"
            $saveFileAfter = Get-CodeBlock $caseText "Save file after"
            $saveFilePath = Join-Path $workDirectory "data/alzara.txt"

            if ($null -ne $saveFileBefore) {
                New-Item -ItemType Directory -Path (Join-Path $workDirectory "data") -Force | Out-Null
                [System.IO.File]::WriteAllText($saveFilePath, $saveFileBefore + "`n")
            }

            # A test case is either one legacy Inputs/Expected output pair, or
            # one or more numbered sessions (### Session 1 Inputs / ### Session 1
            # Expected output, ### Session 2 Inputs, ...). Numbered sessions run
            # as separate `java` processes sharing $workDirectory, so they behave
            # like restarting the program: state only carries over through the
            # save file, exactly as it would for a real user.
            $sessions = [System.Collections.Generic.List[object]]::new()
            $sessionIndex = 1
            while ($true) {
                $sessionInputs = Get-CodeBlock $caseText "Session $sessionIndex inputs"
                if ($null -eq $sessionInputs) {
                    break
                }
                $sessionExpected = Get-CodeBlock $caseText "Session $sessionIndex expected output" -Required
                $sessions.Add(@{ Inputs = $sessionInputs; Expected = $sessionExpected })
                $sessionIndex++
            }
            if ($sessions.Count -eq 0) {
                $legacyInputs = Get-CodeBlock $caseText "Inputs" -Required
                $legacyExpected = Get-CodeBlock $caseText "Expected output" -Required
                $sessions.Add(@{ Inputs = $legacyInputs; Expected = $legacyExpected })
            }

            $sessionNumber = 0
            foreach ($session in $sessions) {
                $sessionNumber++
                $sessionLabel = if ($sessions.Count -gt 1) { "$name (session $sessionNumber)" } else { $name }

                $processInfo = [System.Diagnostics.ProcessStartInfo]::new()
                $processInfo.FileName = "java"
                $processInfo.Arguments = "-cp `"$classDirectory`" alzara.Alzara"
                $processInfo.WorkingDirectory = $workDirectory
                $processInfo.UseShellExecute = $false
                $processInfo.RedirectStandardInput = $true
                $processInfo.RedirectStandardOutput = $true
                $processInfo.RedirectStandardError = $true

                $process = [System.Diagnostics.Process]::new()
                $process.StartInfo = $processInfo
                [void]$process.Start()
                $process.StandardInput.Write($session.Inputs + "`n")
                $process.StandardInput.Close()
                $actual = $process.StandardOutput.ReadToEnd()
                $standardError = $process.StandardError.ReadToEnd()
                $process.WaitForExit()

                Write-Output "=== Test case: $sessionLabel ==="
                Write-Output "Console input:"
                Write-Output $session.Inputs
                Write-Output "Console output:"
                Write-Output $actual

                if ($process.ExitCode -ne 0 -or (Normalize-ConsoleOutput $actual) -cne (Normalize-ConsoleOutput $session.Expected)) {
                    Write-Output "FAILED: $sessionLabel"
                    Write-Output "Expected output:"
                    Write-Output $session.Expected
                    Write-Output "Actual output:"
                    Write-Output $actual
                    if ($standardError) {
                        Write-Output "Standard error:"
                        Write-Output $standardError
                    }
                    exit 1
                }

                Write-Output "PASSED: $sessionLabel"
            }

            if ($null -ne $saveFileAfter) {
                $actualSaveFile = if (Test-Path -LiteralPath $saveFilePath) {
                    Get-Content -Raw -LiteralPath $saveFilePath
                } else {
                    ""
                }

                if ((Normalize-FileContent $actualSaveFile) -cne (Normalize-FileContent $saveFileAfter)) {
                    Write-Output "FAILED: $name (save file)"
                    Write-Output "Expected data/alzara.txt:"
                    Write-Output $saveFileAfter
                    Write-Output "Actual data/alzara.txt:"
                    Write-Output $actualSaveFile
                    exit 1
                }

                Write-Output "PASSED: $name (save file)"
            }
        } finally {
            Remove-Item -LiteralPath $workDirectory -Recurse -Force -ErrorAction SilentlyContinue
        }
    }
} finally {
    Remove-Item -LiteralPath $classDirectory -Recurse -Force -ErrorAction SilentlyContinue
}
