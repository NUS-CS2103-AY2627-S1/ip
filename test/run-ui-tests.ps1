[CmdletBinding()]
param(
    [string]$Filter = '',
    [switch]$Quiet
)

$ErrorActionPreference = 'Stop'

$testDir = $PSScriptRoot
$rootDir = Split-Path -Parent $testDir
$srcDir = Join-Path $rootDir 'src\main\java'
$binDir = Join-Path $rootDir 'out'
$planFile = Join-Path $testDir 'ui-test-plan.md'
$workDir = Join-Path ([System.IO.Path]::GetTempPath()) 'ev-ui-test'

function Read-TestPlan([string]$path) {
    $cases = @()
    $current = $null
    $section = ''
    $inFence = $false
    $buffer = @()

    foreach ($line in Get-Content -Path $path) {
        if ($line -match '^###\s+(TC-\d+)\s+(.*)$') {
            if ($null -ne $current) { $cases += $current }
            $current = [pscustomobject]@{
                Id         = $matches[1]
                Name       = $matches[2]
                Aim        = ''
                Input      = @()
                Expected   = @()
                DataBefore = $null
                DataAfter  = $null
            }
            $section = ''
            $inFence = $false
            continue
        }
        if ($null -eq $current) { continue }

        if ($line -match '^\*\*Aim:\*\*\s*(.*)$') {
            $current.Aim = $matches[1]
            $section = 'aim'
            continue
        }
        if ($line -match '^\*\*Input\*\*') { $section = 'input'; continue }
        if ($line -match '^\*\*Expected output\*\*') { $section = 'expected'; continue }
        if ($line -match '^\*\*Data file before\*\*') { $section = 'databefore'; continue }
        if ($line -match '^\*\*Data file after\*\*') { $section = 'dataafter'; continue }

        if ($section -eq 'aim') {
            if ($line.Trim() -eq '') {
                $section = ''
            } else {
                $current.Aim = ($current.Aim + ' ' + $line.Trim()).Trim()
            }
            continue
        }

        if ($line -match '^```') {
            if ($inFence) {
                $inFence = $false
                if ($section -eq 'input') { $current.Input = $buffer }
                elseif ($section -eq 'expected') { $current.Expected = $buffer }
                elseif ($section -eq 'databefore') { $current.DataBefore = @($buffer) }
                elseif ($section -eq 'dataafter') { $current.DataAfter = @($buffer) }
                $section = ''
                $buffer = @()
            } elseif ($section -ne '') {
                $inFence = $true
                $buffer = @()
            }
            continue
        }

        if ($inFence) { $buffer += $line }
    }

    if ($null -ne $current) { $cases += $current }
    return $cases
}

function Get-ReplyBody($outputLines, $caseId) {
    $lines = @($outputLines)
    $separatorIndexes = @()
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match '^_+$') { $separatorIndexes += $i }
    }
    if ($separatorIndexes.Count -lt 4) {
        throw "$caseId : output does not contain the greeting and farewell frames."
    }

    $greetingEnd = $separatorIndexes[1]
    if ($lines[$separatorIndexes[0] + 1] -ne "Hi Peter." -or
        $lines[$separatorIndexes[0] + 2] -ne "EV online. What do you need?") {
        throw "$caseId : greeting does not match the one recorded in the test plan."
    }
    if ($lines[$lines.Count - 2] -ne "Signing off, Peter.") {
        throw "$caseId : farewell does not match the one recorded in the test plan."
    }

    $start = $greetingEnd + 1
    $end = $lines.Count - 4
    if ($start -gt $end) { return @() }
    return @($lines[$start..$end])
}

function Show-Lines($lines, $prefix) {
    foreach ($line in @($lines)) { Write-Host "$prefix$line" }
}

if (-not (Test-Path $planFile)) {
    Write-Host "Cannot find test plan: $planFile" -ForegroundColor Red
    exit 1
}
if (-not (Test-Path $workDir)) {
    New-Item -ItemType Directory -Path $workDir | Out-Null
}

$javac = (Get-Command javac -ErrorAction SilentlyContinue).Source
$java = (Get-Command java -ErrorAction SilentlyContinue).Source
if (-not $javac -or -not $java) {
    Write-Host 'javac or java not found on PATH.' -ForegroundColor Red
    exit 1
}

Write-Host "Compiling $srcDir ..."
# The ev.gui package needs JavaFX, which only the Gradle build puts on the class path.
# These tests drive the text UI, so those classes are left out of this compilation.
$guiDir = Join-Path $srcDir 'ev\gui'
$sources = @(Get-ChildItem -Path $srcDir -Filter '*.java' -Recurse |
    Where-Object { -not $_.FullName.StartsWith($guiDir) } |
    ForEach-Object { $_.FullName })
& $javac -d $binDir $sources
if ($LASTEXITCODE -ne 0) {
    Write-Host 'Compilation failed.' -ForegroundColor Red
    exit 1
}

$cases = Read-TestPlan $planFile
if ($Filter -ne '') {
    $cases = @($cases | Where-Object { $_.Id -like "$Filter*" })
}
if ($cases.Count -eq 0) {
    Write-Host "No test cases matched filter '$Filter'." -ForegroundColor Yellow
    exit 1
}

Write-Host "Running $($cases.Count) test case(s) from $planFile"
Write-Host ''

$passed = 0
foreach ($case in $cases) {
    $inFile = Join-Path $workDir "$($case.Id).in.txt"
    $outFile = Join-Path $workDir "$($case.Id).out.txt"
    $errFile = Join-Path $workDir "$($case.Id).err.txt"

    $caseDir = Join-Path $workDir "$($case.Id).run"
    if (Test-Path $caseDir) { Remove-Item -Path $caseDir -Recurse -Force }
    New-Item -ItemType Directory -Path $caseDir | Out-Null
    $dataFile = Join-Path $caseDir 'data\ev.txt'

    if ($null -ne $case.DataBefore) {
        New-Item -ItemType Directory -Path (Split-Path -Parent $dataFile) | Out-Null
        Set-Content -Path $dataFile -Value @($case.DataBefore) -Encoding ascii
    }

    Set-Content -Path $inFile -Value @($case.Input) -Encoding ascii

    Start-Process -FilePath $java `
        -ArgumentList @('-ea', '-cp', $binDir, 'ev.EV') `
        -WorkingDirectory $caseDir `
        -RedirectStandardInput $inFile `
        -RedirectStandardOutput $outFile `
        -RedirectStandardError $errFile `
        -NoNewWindow -Wait | Out-Null

    $stderr = @(Get-Content -Path $errFile)
    if ($stderr.Count -gt 0) {
        Write-Host "FAILED $($case.Id) $($case.Name)" -ForegroundColor Red
        Write-Host 'The program wrote to standard error:'
        Show-Lines $stderr '  '
        exit 1
    }

    $actual = Get-ReplyBody (Get-Content -Path $outFile) $case.Id
    $expected = @($case.Expected)

    if (-not $Quiet) {
        Write-Host "$($case.Id) $($case.Name)" -ForegroundColor Cyan
        Write-Host "  Aim: $($case.Aim)"
        Show-Lines $case.Input '  > '
        Show-Lines $actual '  '
        Write-Host ''
    }

    $mismatch = -1
    $maxLines = [Math]::Max($expected.Count, $actual.Count)
    for ($i = 0; $i -lt $maxLines; $i++) {
        $e = if ($i -lt $expected.Count) { $expected[$i] } else { $null }
        $a = if ($i -lt $actual.Count) { $actual[$i] } else { $null }
        if ($e -ne $a) { $mismatch = $i; break }
    }

    if ($mismatch -ge 0) {
        Write-Host "FAILED $($case.Id) $($case.Name)" -ForegroundColor Red
        Write-Host "  Aim: $($case.Aim)"
        Write-Host "  First difference at reply line $($mismatch + 1):"
        $e = if ($mismatch -lt $expected.Count) { $expected[$mismatch] } else { '<no more output>' }
        $a = if ($mismatch -lt $actual.Count) { $actual[$mismatch] } else { '<no more output>' }
        Write-Host "    expected: $e" -ForegroundColor Yellow
        Write-Host "    actual  : $a" -ForegroundColor Yellow
        Write-Host ''
        Write-Host "  Full expected output ($($expected.Count) lines):"
        Show-Lines $expected '    '
        Write-Host "  Full actual output ($($actual.Count) lines):"
        Show-Lines $actual '    '
        Write-Host ''
        Write-Host "Stopped at the first failure. $passed case(s) passed before it." -ForegroundColor Red
        exit 1
    }

    if ($null -ne $case.DataAfter) {
        $expectedData = @($case.DataAfter)
        $expectsNoFile = ($expectedData.Count -eq 1 -and $expectedData[0] -eq '(no file)')
        $fileExists = Test-Path $dataFile
        # The @() has to wrap the whole if: PowerShell unrolls a one-element array on its
        # way out of a statement block, and indexing the bare string that is left gives
        # back single characters instead of lines.
        $actualData = @(if ($fileExists) { Get-Content -Path $dataFile } else { @() })

        $dataProblem = ''
        if ($expectsNoFile) {
            if ($fileExists) { $dataProblem = 'the data file exists but the case expects no data file.' }
        } elseif (-not $fileExists) {
            $dataProblem = 'the data file was not created.'
        } else {
            $maxDataLines = [Math]::Max($expectedData.Count, $actualData.Count)
            for ($i = 0; $i -lt $maxDataLines; $i++) {
                $e = if ($i -lt $expectedData.Count) { $expectedData[$i] } else { '<no more lines>' }
                $a = if ($i -lt $actualData.Count) { $actualData[$i] } else { '<no more lines>' }
                if ($e -ne $a) {
                    $dataProblem = "data file line $($i + 1) differs: expected '$e' but found '$a'."
                    break
                }
            }
        }

        if (-not $Quiet -and $dataProblem -eq '') {
            Write-Host "  data file:" -ForegroundColor DarkGray
            Show-Lines $actualData '    | '
            Write-Host ''
        }

        if ($dataProblem -ne '') {
            Write-Host "FAILED $($case.Id) $($case.Name)" -ForegroundColor Red
            Write-Host "  Aim: $($case.Aim)"
            Write-Host "  $dataProblem" -ForegroundColor Yellow
            Write-Host "  Full expected data file ($($expectedData.Count) lines):"
            Show-Lines $expectedData '    '
            Write-Host "  Full actual data file ($($actualData.Count) lines):"
            Show-Lines $actualData '    '
            Write-Host ''
            Write-Host "Stopped at the first failure. $passed case(s) passed before it." -ForegroundColor Red
            exit 1
        }
    }

    $passed++
}

Write-Host "All $passed test case(s) passed." -ForegroundColor Green
exit 0
