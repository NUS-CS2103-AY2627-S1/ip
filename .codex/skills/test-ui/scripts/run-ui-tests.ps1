[CmdletBinding()]
param(
    [string]$PlanPath = "test/ui-test-plan.md",
    [int]$TimeoutSeconds = 10
)

$ErrorActionPreference = "Stop"
$Utf8NoBom = New-Object System.Text.UTF8Encoding($false)

function Quote-Argument {
    param([string]$Value)
    return '"' + $Value.Replace('"', '\"') + '"'
}

function Invoke-CapturedProcess {
    param(
        [string]$FileName,
        [string]$Arguments,
        [string]$WorkingDirectory,
        [string]$StandardInput,
        [int]$Timeout
    )

    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.FileName = $FileName
    $startInfo.Arguments = $Arguments
    $startInfo.WorkingDirectory = $WorkingDirectory
    $startInfo.UseShellExecute = $false
    $startInfo.CreateNoWindow = $true
    $startInfo.RedirectStandardInput = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true
    $startInfo.StandardOutputEncoding = $Utf8NoBom
    $startInfo.StandardErrorEncoding = $Utf8NoBom

    $process = New-Object System.Diagnostics.Process
    $process.StartInfo = $startInfo
    [void]$process.Start()

    $stdoutTask = $process.StandardOutput.ReadToEndAsync()
    $stderrTask = $process.StandardError.ReadToEndAsync()

    if ($null -ne $StandardInput) {
        $process.StandardInput.Write($StandardInput)
    }
    $process.StandardInput.Close()

    $finished = $process.WaitForExit($Timeout * 1000)
    if (-not $finished) {
        $process.Kill()
        $process.WaitForExit()
    }

    return [pscustomobject]@{
        TimedOut = -not $finished
        ExitCode = if ($finished) { $process.ExitCode } else { $null }
        Stdout = $stdoutTask.Result
        Stderr = $stderrTask.Result
    }
}

function Normalize-Output {
    param([string]$Text)

    $normalized = $Text.Replace("`r`n", "`n").Replace("`r", "`n")
    return $normalized.Trim([char[]]"`n")
}

function Write-TextFile {
    param(
        [string]$Path,
        [string]$Content
    )

    [System.IO.File]::WriteAllText($Path, $Content, $Utf8NoBom)
}

function Show-Block {
    param(
        [string]$Heading,
        [string]$Content
    )

    Write-Output $Heading
    Write-Output ("-" * $Heading.Length)
    Write-Output $Content
}

$repoRoot = [System.IO.Path]::GetFullPath(
    (Join-Path $PSScriptRoot "..\..\..\..")
)

if ([System.IO.Path]::IsPathRooted($PlanPath)) {
    $resolvedPlanPath = [System.IO.Path]::GetFullPath($PlanPath)
} else {
    $resolvedPlanPath = [System.IO.Path]::GetFullPath(
        (Join-Path $repoRoot $PlanPath)
    )
}

if (-not (Test-Path -LiteralPath $resolvedPlanPath -PathType Leaf)) {
    Write-Error "Test plan not found: $resolvedPlanPath"
    exit 1
}

$planContent = Get-Content -Raw -LiteralPath $resolvedPlanPath
$casePattern = '(?ms)^## (?<id>[A-Za-z0-9][A-Za-z0-9-]*): (?<title>[^\r\n]+)\r?\n\r?\n\*\*Aim:\*\* (?<aim>[^\r\n]+)\r?\n\r?\n### Inputs\r?\n\r?\n```console-input\r?\n(?<input>.*?)\r?\n```\r?\n\r?\n### Expected output\r?\n\r?\n```console-output\r?\n(?<expected>.*?)\r?\n```'
$caseMatches = [regex]::Matches($planContent, $casePattern)
$declaredCases = [regex]::Matches(
    $planContent,
    '(?m)^## [A-Za-z0-9][A-Za-z0-9-]*: '
)

if ($caseMatches.Count -eq 0) {
    Write-Error "No test cases found in $resolvedPlanPath"
    exit 1
}

if ($caseMatches.Count -ne $declaredCases.Count) {
    Write-Error "One or more test cases do not match the required Markdown schema."
    exit 1
}

$sourceDirectory = Join-Path $repoRoot "src\main\java"
$consoleEntryPoint = Join-Path $sourceDirectory "tasque\Tasque.java"
if (-not (Test-Path -LiteralPath $consoleEntryPoint -PathType Leaf)) {
    Write-Error "Console entry point not found: $consoleEntryPoint"
    exit 1
}

$sessionId = (Get-Date -Format "yyyyMMdd-HHmmss") + "-" + [guid]::NewGuid().ToString("N").Substring(0, 8)
$sessionDirectory = Join-Path $repoRoot ("_temp\test-ui\" + $sessionId)
$classesDirectory = Join-Path $sessionDirectory "classes"
[void](New-Item -ItemType Directory -Path $classesDirectory -Force)

$compileArguments = "-sourcepath " + (Quote-Argument $sourceDirectory) `
    + " -d " + (Quote-Argument $classesDirectory) `
    + " " + (Quote-Argument $consoleEntryPoint)
$compileResult = Invoke-CapturedProcess `
    -FileName "javac" `
    -Arguments $compileArguments `
    -WorkingDirectory $repoRoot `
    -StandardInput "" `
    -Timeout $TimeoutSeconds

if ($compileResult.TimedOut -or $compileResult.ExitCode -ne 0) {
    Write-Output "FAIL: Java compilation"
    Show-Block "COMPILER STDOUT" $compileResult.Stdout
    Show-Block "COMPILER STDERR" $compileResult.Stderr
    Write-Output "Session artifacts: $sessionDirectory"
    exit 1
}

$transcripts = New-Object System.Collections.Generic.List[string]

foreach ($caseMatch in $caseMatches) {
    $caseId = $caseMatch.Groups["id"].Value
    $caseTitle = $caseMatch.Groups["title"].Value
    $caseAim = $caseMatch.Groups["aim"].Value
    $caseInput = $caseMatch.Groups["input"].Value
    $expectedOutput = $caseMatch.Groups["expected"].Value
    $caseDirectory = Join-Path $sessionDirectory $caseId
    [void](New-Item -ItemType Directory -Path $caseDirectory -Force)

    $runArguments = "-cp " + (Quote-Argument $classesDirectory) + " tasque.Tasque"
    $inputSegments = $caseInput -split '(?m)^\s*--- RESTART ---\s*$'
    $stdoutSegments = New-Object System.Collections.Generic.List[string]
    $stderrSegments = New-Object System.Collections.Generic.List[string]
    $timedOut = $false
    $exitCode = 0

    foreach ($inputSegment in $inputSegments) {
        $processInput = (Normalize-Output $inputSegment) + "`n"
        $segmentResult = Invoke-CapturedProcess `
            -FileName "java" `
            -Arguments $runArguments `
            -WorkingDirectory $caseDirectory `
            -StandardInput $processInput `
            -Timeout $TimeoutSeconds
        $stdoutSegments.Add($segmentResult.Stdout)
        $stderrSegments.Add($segmentResult.Stderr)
        if ($segmentResult.TimedOut -or $segmentResult.ExitCode -ne 0) {
            $timedOut = $segmentResult.TimedOut
            $exitCode = $segmentResult.ExitCode
            break
        }
    }

    $runResult = [pscustomobject]@{
        TimedOut = $timedOut
        ExitCode = $exitCode
        Stdout = $stdoutSegments -join "`n"
        Stderr = $stderrSegments -join ""
    }

    Write-TextFile (Join-Path $caseDirectory "input.txt") $caseInput
    Write-TextFile (Join-Path $caseDirectory "expected.txt") $expectedOutput
    Write-TextFile (Join-Path $caseDirectory "actual.txt") $runResult.Stdout
    Write-TextFile (Join-Path $caseDirectory "stderr.txt") $runResult.Stderr

    $normalizedExpected = Normalize-Output $expectedOutput
    $normalizedActual = Normalize-Output $runResult.Stdout
    $failureReason = $null

    if ($runResult.TimedOut) {
        $failureReason = "process timed out after $TimeoutSeconds seconds"
    } elseif ($runResult.ExitCode -ne 0) {
        $failureReason = "process exited with code $($runResult.ExitCode)"
    } elseif (-not [string]::IsNullOrEmpty($runResult.Stderr)) {
        $failureReason = "process wrote to standard error"
    } elseif ($normalizedActual -cne $normalizedExpected) {
        $failureReason = "stdout did not match expected output"
    }

    if ($null -ne $failureReason) {
        Write-Output "FAIL: $caseId - $caseTitle"
        Write-Output "Reason: $failureReason"
        Show-Block "INPUT" $caseInput
        Show-Block "EXPECTED STDOUT" $expectedOutput
        Show-Block "ACTUAL STDOUT" $runResult.Stdout
        if (-not [string]::IsNullOrEmpty($runResult.Stderr)) {
            Show-Block "STANDARD ERROR" $runResult.Stderr
        }
        Write-Output "Session artifacts: $sessionDirectory"
        exit 1
    }

    $transcript = @(
        "TEST: $caseId - $caseTitle",
        "AIM: $caseAim",
        "",
        "INPUT",
        "-----",
        $caseInput,
        "",
        "OUTPUT",
        "------",
        $runResult.Stdout
    ) -join "`n"
    $transcripts.Add($transcript)
    Write-TextFile (Join-Path $caseDirectory "transcript.txt") $transcript
}

$combinedTranscript = $transcripts -join "`n`n"
Write-TextFile (Join-Path $sessionDirectory "transcript.txt") $combinedTranscript

Write-Output "PASS: $($caseMatches.Count) test case(s)"
Write-Output ""
Write-Output $combinedTranscript
Write-Output ""
Write-Output "Session artifacts: $sessionDirectory"
exit 0
