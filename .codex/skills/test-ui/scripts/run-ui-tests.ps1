[CmdletBinding()]
param(
    [string]$TestPlan
)

$ErrorActionPreference = 'Stop'
trap {
    Write-Output ("[SETUP ERROR] " + $_.Exception.Message)
    exit 2
}

$projectRoot = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..\..\..\..'))

if ([string]::IsNullOrWhiteSpace($TestPlan)) {
    $TestPlan = Join-Path $projectRoot 'test\ui-test-plan.md'
} elseif (-not [System.IO.Path]::IsPathRooted($TestPlan)) {
    $TestPlan = Join-Path $projectRoot $TestPlan
}

function Normalize-ConsoleOutput {
    param([AllowEmptyString()][string]$Value)

    $normalized = $Value.Replace("`r`n", "`n").Replace("`r", "`n")
    if ($normalized.EndsWith("`n", [System.StringComparison]::Ordinal)) {
        return $normalized.Substring(0, $normalized.Length - 1)
    }
    return $normalized
}

function Show-Block {
    param(
        [string]$Title,
        [AllowEmptyString()][string]$Value
    )

    Write-Output ($Title + ':')
    if ($Value.Length -eq 0) {
        Write-Output '(no output)'
    } else {
        Write-Output $Value
    }
}

function Read-ConfigValue {
    param(
        [string]$Plan,
        [string]$Label
    )

    $escapedLabel = [regex]::Escape($Label)
    $match = [regex]::Match($Plan, "(?m)^- $escapedLabel\s*:\s*``(?<value>[^``]+)``\s*$")
    if (-not $match.Success) {
        throw "The test plan is missing the '$Label' project setting."
    }
    return $match.Groups['value'].Value
}

function Get-JavaVersionText {
    param([string]$Executable)

    $startInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $startInfo.FileName = $Executable
    $startInfo.Arguments = '-version'
    $startInfo.UseShellExecute = $false
    $startInfo.CreateNoWindow = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true

    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $startInfo
    $null = $process.Start()
    $standardOutput = $process.StandardOutput.ReadToEnd()
    $standardError = $process.StandardError.ReadToEnd()
    $process.WaitForExit()

    return ($standardOutput + $standardError).Trim()
}

function Invoke-JavaCompiler {
    param(
        [string]$Executable,
        [string]$OutputDirectory,
        [string[]]$SourceFiles
    )

    $quotedSources = @($SourceFiles | ForEach-Object { '"' + $_ + '"' })
    $startInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $startInfo.FileName = $Executable
    $startInfo.Arguments = '-d "' + $OutputDirectory + '" ' + ($quotedSources -join ' ')
    $startInfo.UseShellExecute = $false
    $startInfo.CreateNoWindow = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true

    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $startInfo
    $null = $process.Start()
    $stdoutTask = $process.StandardOutput.ReadToEndAsync()
    $stderrTask = $process.StandardError.ReadToEndAsync()
    $process.WaitForExit()

    return [pscustomobject]@{
        ExitCode = $process.ExitCode
        Output = Normalize-ConsoleOutput ($stdoutTask.GetAwaiter().GetResult() + $stderrTask.GetAwaiter().GetResult())
    }
}

if (-not (Test-Path -LiteralPath $TestPlan -PathType Leaf)) {
    throw "UI test plan not found: $TestPlan"
}

$plan = Get-Content -LiteralPath $TestPlan -Raw
$mainClass = Read-ConfigValue -Plan $plan -Label 'Main class'
$sourceDirectory = Read-ConfigValue -Plan $plan -Label 'Source directory'
$requiredJavaVersion = Read-ConfigValue -Plan $plan -Label 'Required Java major version'

$casePattern = '(?ms)^## Test case: (?<name>[^\r\n]+)\r?\n\*\*Aim:\*\* (?<aim>[^\r\n]+)\r?\n+### Inputs\r?\n```text\r?\n(?<inputs>.*?)\r?\n```\r?\n+### Expected output\r?\n```text\r?\n(?<expected>.*?)\r?\n```[ \t]*(?:\r?\n|$)'
$testCases = [regex]::Matches($plan, $casePattern)
if ($testCases.Count -eq 0) {
    throw "No test cases matching the required schema were found in $TestPlan"
}

$javac = Get-Command javac -ErrorAction Stop
$java = Get-Command java -ErrorAction Stop
$javacVersionText = Get-JavaVersionText -Executable $javac.Source
$javaVersionText = Get-JavaVersionText -Executable $java.Source
$javacVersionMatch = [regex]::Match($javacVersionText, '^javac\s+(?<major>\d+)')
$javaVersionMatch = [regex]::Match($javaVersionText, 'version\s+"(?<major>\d+)')

if (-not $javacVersionMatch.Success -or $javacVersionMatch.Groups['major'].Value -ne $requiredJavaVersion) {
    throw "Java setup error: expected javac $requiredJavaVersion, but found '$javacVersionText'. No test cases were run."
}
if (-not $javaVersionMatch.Success -or $javaVersionMatch.Groups['major'].Value -ne $requiredJavaVersion) {
    throw "Java setup error: expected java $requiredJavaVersion, but found '$javaVersionText'. No test cases were run."
}

$sourcePath = Join-Path $projectRoot $sourceDirectory
$javaFiles = @(Get-ChildItem -LiteralPath $sourcePath -Filter '*.java' -File -Recurse | Where-Object {
    -not (Select-String -LiteralPath $_.FullName -Pattern '^import javafx\.' -Quiet)
})
if ($javaFiles.Count -eq 0) {
    throw "No Java source files found under $sourcePath"
}

$classesDirectory = Join-Path ([System.IO.Path]::GetTempPath()) ("test-ui-" + [guid]::NewGuid().ToString('N'))
[System.IO.Directory]::CreateDirectory($classesDirectory) | Out-Null

try {
    $sourceArguments = @($javaFiles | ForEach-Object { $_.FullName })
    $compileResult = Invoke-JavaCompiler -Executable $javac.Source -OutputDirectory $classesDirectory -SourceFiles $sourceArguments
    if ($compileResult.ExitCode -ne 0) {
        throw "Compilation failed before the UI test session:`n$($compileResult.Output)"
    }

    Write-Output '=== UI test session ==='
    Write-Output "Plan: $TestPlan"
    Write-Output "Java: $requiredJavaVersion"
    Write-Output ''

    $passedCount = 0
    foreach ($testCase in $testCases) {
        $name = $testCase.Groups['name'].Value.Trim()
        $aim = $testCase.Groups['aim'].Value.Trim()
        $inputBlock = Normalize-ConsoleOutput $testCase.Groups['inputs'].Value
        $expected = Normalize-ConsoleOutput $testCase.Groups['expected'].Value
        $commands = if ($inputBlock.Length -eq 0) { @() } else { @($inputBlock -split "`n") }

        Write-Output ("--- " + $name + " ---")
        Write-Output ("Aim: " + $aim)
        Write-Output 'Console input:'
        if ($commands.Count -eq 0) {
            Write-Output '(end of input)'
        } else {
            foreach ($command in $commands) {
                Write-Output ('> ' + $command)
            }
        }

        $startInfo = [System.Diagnostics.ProcessStartInfo]::new()
        $startInfo.FileName = $java.Source
        $startInfo.Arguments = '-cp "' + $classesDirectory + '" ' + $mainClass
        $caseDirectory = Join-Path $classesDirectory ("case-" + [guid]::NewGuid().ToString('N'))
        [System.IO.Directory]::CreateDirectory($caseDirectory) | Out-Null
        $startInfo.WorkingDirectory = $caseDirectory
        $startInfo.UseShellExecute = $false
        $startInfo.CreateNoWindow = $true
        $startInfo.RedirectStandardInput = $true
        $startInfo.RedirectStandardOutput = $true
        $startInfo.RedirectStandardError = $true

        $process = [System.Diagnostics.Process]::new()
        $process.StartInfo = $startInfo
        $null = $process.Start()
        $stdoutTask = $process.StandardOutput.ReadToEndAsync()
        $stderrTask = $process.StandardError.ReadToEndAsync()
        foreach ($command in $commands) {
            $process.StandardInput.WriteLine($command)
        }
        $process.StandardInput.Close()
        $process.WaitForExit()

        $actual = Normalize-ConsoleOutput $stdoutTask.GetAwaiter().GetResult()
        $standardError = Normalize-ConsoleOutput $stderrTask.GetAwaiter().GetResult()
        Show-Block -Title 'Console output' -Value $actual
        if ($standardError.Length -gt 0) {
            Show-Block -Title 'Console error output' -Value $standardError
        }

        $outputMatches = [string]::Equals($actual, $expected, [System.StringComparison]::Ordinal)
        if ($process.ExitCode -ne 0 -or -not $outputMatches -or $standardError.Length -gt 0) {
            Write-Output '[FAIL]'
            Show-Block -Title 'Actual output' -Value $actual
            Show-Block -Title 'Expected output' -Value $expected
            if ($process.ExitCode -ne 0) {
                Write-Output ("Process exit code: " + $process.ExitCode)
            }
            Write-Output 'Test session terminated immediately; later test cases were not run.'
            exit 1
        }

        $passedCount++
        Write-Output '[PASS]'
        Write-Output ''
    }

    Write-Output ("All " + $passedCount + " test case(s) passed.")
} finally {
    if (Test-Path -LiteralPath $classesDirectory) {
        Remove-Item -LiteralPath $classesDirectory -Recurse -Force
    }
}
