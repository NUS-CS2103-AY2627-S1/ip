param(
    [string] $PlanPath = 'tests/test-plan.md',
    [ValidateRange(1, 60)]
    [int] $TimeoutSeconds = 15
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'
$projectRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../../..'))
if (-not [IO.Path]::IsPathRooted($PlanPath)) {
    $PlanPath = Join-Path $projectRoot $PlanPath
}

# Keep reports in Gradle's ignored build directory, not beside real task data.
$reportDirectory = Join-Path $projectRoot 'build/reports/ui-tests'
New-Item -ItemType Directory -Force -Path $reportDirectory | Out-Null
$transcriptPath = Join-Path $reportDirectory 'transcript.txt'
$temporaryRoot = [IO.Path]::GetFullPath([IO.Path]::GetTempPath())
$runDirectory = Join-Path $temporaryRoot ('epi-ui-tests-' + [guid]::NewGuid().ToString('N'))

function Get-CodeBlock {
    param([string] $Section, [string] $Heading)
    $fence = ([char]96).ToString() * 3
    $pattern = '(?ms)^### ' + [regex]::Escape($Heading) + '[ \t]*\r?\n(?:[ \t]*\r?\n)*' +
        $fence + 'text[ \t]*\r?\n(.*?)^' + $fence + '[ \t]*\r?$'
    $match = [regex]::Match($Section, $pattern)
    if (-not $match.Success) {
        throw "Missing '$Heading' text code block."
    }
    # Remove only the fence's preceding newline; preserve whitespace in commands.
    return ($match.Groups[1].Value -replace '\r?\n\z', '')
}

function Normalize-Output {
    param([string] $Value)
    $lines = @($Value -split '\r?\n' | ForEach-Object { $_.TrimEnd() })
    return ($lines -join "`n").TrimEnd([char[]]"`r`n")
}

function Invoke-EpiSession {
    param([string] $InputText, [string] $WorkingDirectory, $Runtime)
    $startInfo = [Diagnostics.ProcessStartInfo]::new()
    $startInfo.FileName = $Runtime.javaExecutable
    $startInfo.Arguments = '-ea -Duser.language=en -Duser.country=US -Dfile.encoding=UTF-8 -cp "' +
        $Runtime.classpath + '" epi.Epi'
    $startInfo.WorkingDirectory = $WorkingDirectory
    $startInfo.RedirectStandardInput = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true
    $startInfo.StandardOutputEncoding = [Text.Encoding]::UTF8
    $startInfo.StandardErrorEncoding = [Text.Encoding]::UTF8
    $startInfo.UseShellExecute = $false
    $startInfo.CreateNoWindow = $true
    $process = [Diagnostics.Process]::new()
    $process.StartInfo = $startInfo
    $started = $false
    try {
        $started = $process.Start()
        # Drain both streams concurrently to avoid a full stderr pipe blocking the runner.
        $stdout = $process.StandardOutput.ReadToEndAsync()
        $stderr = $process.StandardError.ReadToEndAsync()
        if ($InputText.Length -gt 0) {
            $process.StandardInput.WriteLine(($InputText -replace '\r?\n', [Environment]::NewLine))
        }
        $process.StandardInput.Close()
        $timedOut = -not $process.WaitForExit($TimeoutSeconds * 1000)
        if ($timedOut) {
            $process.Kill()
            $process.WaitForExit()
        }
        return [pscustomobject]@{
            Output = $stdout.GetAwaiter().GetResult()
            Error = $stderr.GetAwaiter().GetResult()
            ExitCode = $process.ExitCode
            TimedOut = $timedOut
        }
    } finally {
        if ($started -and -not $process.HasExited) {
            $process.Kill()
            $process.WaitForExit()
        }
        $process.Dispose()
    }
}

$exitCode = 0
$transcriptStarted = $false
try {
    Start-Transcript -LiteralPath $transcriptPath -Force | Out-Null
    $transcriptStarted = $true
    $markdown = Get-Content -Raw -Encoding UTF8 -LiteralPath $PlanPath
    $caseMatches = [regex]::Matches($markdown, '(?ms)^## Test case .*?(?=^## |\z)')
    if ($caseMatches.Count -eq 0) {
        throw "No test cases found in $PlanPath"
    }
    $startupOutput = Get-CodeBlock ($markdown -split '(?m)^## Test case ', 2)[0] 'Startup output'
    $cases = @(foreach ($caseMatch in $caseMatches) {
        $section = $caseMatch.Value
        $title = ([regex]::Match($section, '(?m)^## (.*)$')).Groups[1].Value.TrimEnd()
        if ($section -notmatch '(?m)^### Aim\s*\r?\n\s*\S') {
            throw "Missing aim in $title"
        }
        [pscustomobject]@{
            Title = $title
            Input = Get-CodeBlock $section 'Input'
            Expected = Get-CodeBlock $section 'Expected output'
        }
    })

    Push-Location $projectRoot
    try {
        & (Join-Path $projectRoot 'gradlew.bat') prepareUiTests --console=plain --no-daemon
        if ($LASTEXITCODE -ne 0) {
            throw 'Gradle preparation failed; no UI test sessions were started.'
        }
    } finally {
        Pop-Location
    }
    $runtime = Get-Content -Raw -LiteralPath (Join-Path $projectRoot 'build/ui-tests/runtime.json') |
        ConvertFrom-Json
    New-Item -ItemType Directory -Path $runDirectory | Out-Null

    $caseNumber = 0
    foreach ($testCase in $cases) {
        $caseNumber++
        $caseDirectory = Join-Path $runDirectory "case-$caseNumber"
        New-Item -ItemType Directory -Path $caseDirectory | Out-Null
        Write-Output "===== $($testCase.Title) ====="
        Write-Output "Working directory: $caseDirectory"
        Write-Output 'Console input:'
        Write-Output $testCase.Input
        $expected = "$startupOutput`n$($testCase.Expected)"
        $result = Invoke-EpiSession $testCase.Input $caseDirectory $runtime
        Write-Output 'Console output:'
        Write-Output $result.Output.TrimEnd()
        if ($result.Error) {
            Write-Output 'Console stderr:'
            Write-Output $result.Error.TrimEnd()
        }
        if ($result.TimedOut -or $result.ExitCode -ne 0 -or $result.Error -or
            (Normalize-Output $result.Output) -cne (Normalize-Output $expected)) {
            Write-Output 'FAILED'
            Write-Output 'Expected output:'
            Write-Output $expected
            Write-Output "Exit code: $($result.ExitCode); timed out: $($result.TimedOut)"
            throw "Stopped at $($testCase.Title). Later cases were not run."
        }
        Write-Output 'PASSED'
    }
    Write-Output "All $caseNumber UI tests passed."
} catch {
    Write-Output "FAILED: $($_.Exception.Message)"
    $exitCode = 1
} finally {
    if (Test-Path -LiteralPath $runDirectory) {
        if ($exitCode -eq 0) {
            # Only delete the unique temporary directory created by this invocation.
            $resolvedRun = (Resolve-Path -LiteralPath $runDirectory).Path
            $resolvedParent = Split-Path -Parent $resolvedRun
            if ($resolvedParent.TrimEnd('\', '/') -ne $temporaryRoot.TrimEnd('\', '/') -or
                (Split-Path -Leaf $resolvedRun) -notmatch '^epi-ui-tests-[a-f0-9]{32}$') {
                throw "Refusing to clean an unexpected directory: $resolvedRun"
            }
            Remove-Item -LiteralPath $resolvedRun -Recurse -Force
        } else {
            Write-Output "Failed-session files retained at: $runDirectory"
        }
    }
    Write-Output "Transcript: $transcriptPath"
    if ($transcriptStarted) {
        Stop-Transcript | Out-Null
    }
}
exit $exitCode
