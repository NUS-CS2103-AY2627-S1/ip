[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$PlanPath
)

$ErrorActionPreference = 'Stop'

function Normalize-Output {
    param([string]$Text)
    return (($Text -replace "`r`n", "`n") -replace "`r", "`n").TrimEnd("`n")
}

function Get-FencedSection {
    param(
        [string]$Body,
        [string]$Heading
    )

    $pattern = '(?ms)^### ' + [regex]::Escape($Heading) + '\s*\r?\n```[^\r\n]*\r?\n(?<content>.*?)\r?\n```'
    $match = [regex]::Match($Body, $pattern)
    if (-not $match.Success) {
        throw "Test case is missing a fenced '$Heading' section."
    }
    return $match.Groups['content'].Value
}

function Invoke-ConsoleProgram {
    param(
        [string]$ClassPath,
        [string]$ProgramInput
    )

    $startInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $startInfo.FileName = 'java'
    $startInfo.Arguments = "-cp `"$ClassPath`" Judey"
    $startInfo.UseShellExecute = $false
    $startInfo.RedirectStandardInput = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true

    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $startInfo
    [void]$process.Start()
    $process.StandardInput.Write($ProgramInput)
    if (-not $ProgramInput.EndsWith("`n")) {
        $process.StandardInput.WriteLine()
    }
    $process.StandardInput.Close()
    $output = $process.StandardOutput.ReadToEnd()
    $errorOutput = $process.StandardError.ReadToEnd()
    $process.WaitForExit()

    if ($process.ExitCode -ne 0) {
        throw "The program exited with code $($process.ExitCode).`n$errorOutput"
    }
    return $output + $errorOutput
}

$resolvedPlan = (Resolve-Path -LiteralPath $PlanPath).Path
$repositoryRoot = (Resolve-Path (Join-Path (Split-Path -Parent $PSScriptRoot) '..\..\..')).Path
$outDirectory = Join-Path $repositoryRoot 'out'
$javaVersion = (& cmd.exe /c 'java -version 2>&1' | Out-String)
if ($javaVersion -notmatch 'version "25(?:[.\"]|$)') {
    throw "Java 25 is required. Detected:`n$javaVersion"
}

$sourceFiles = Get-ChildItem -Path (Join-Path $repositoryRoot 'src\main\java') -Filter '*.java' -Recurse |
    Select-Object -ExpandProperty FullName
if ($sourceFiles.Count -eq 0) {
    throw 'No Java source files were found under src/main/java.'
}
& javac -d $outDirectory $sourceFiles
if ($LASTEXITCODE -ne 0) {
    throw "Compilation failed with exit code $LASTEXITCODE."
}

$plan = Get-Content -Raw -LiteralPath $resolvedPlan
$cases = [regex]::Matches($plan, '(?ms)^## Test case: (?<name>[^\r\n]+)\r?\n(?<body>.*?)(?=^## Test case:|\z)')
if ($cases.Count -eq 0) {
    throw 'No test cases were found. Use headings in the form: ## Test case: Name'
}

foreach ($case in $cases) {
    $name = $case.Groups['name'].Value.Trim()
    $consoleInput = Get-FencedSection -Body $case.Groups['body'].Value -Heading 'Inputs'
    $expected = Get-FencedSection -Body $case.Groups['body'].Value -Heading 'Expected output'
    $actual = Invoke-ConsoleProgram -ClassPath $outDirectory -ProgramInput $consoleInput

    Write-Host "`n=== Test case: $name ==="
    Write-Host 'Console input:'
    Write-Host $consoleInput
    Write-Host 'Console output:'
    Write-Host $actual

    if ((Normalize-Output $actual) -ne (Normalize-Output $expected)) {
        Write-Host "RESULT: FAILED -- $name"
        Write-Host 'Expected output:'
        Write-Host $expected
        Write-Host 'Actual output:'
        Write-Host $actual
        exit 1
    }
    Write-Host "RESULT: PASSED -- $name"
}

Write-Host "`nAll $($cases.Count) UI test case(s) passed."
