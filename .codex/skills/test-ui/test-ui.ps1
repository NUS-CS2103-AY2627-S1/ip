# Runs the Mochi text UI test suite.
# For each case in test/cases/ (NN-name-input.txt + NN-name-expected.txt):
#   feeds the inputs to Mochi, prints a transcript, and compares the output.
# Stops at the first failure and reports actual vs expected output.

$ErrorActionPreference = 'Stop'

$projectRoot = Resolve-Path (Join-Path $PSScriptRoot '..\..\..')
$src = Join-Path $projectRoot 'src\main\java'
$out = Join-Path $projectRoot 'out'
$casesDir = Join-Path $projectRoot 'test\cases'
$temp = Join-Path $env:TEMP ('mochi-test-' + [Guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory -Path $temp | Out-Null

function Fail([string]$message) {
    Write-Host "FAIL: $message"
    Remove-Item -LiteralPath $temp -Recurse -Force -ErrorAction SilentlyContinue
    exit 1
}

try {
    $javaFiles = Get-ChildItem -LiteralPath $src -Filter '*.java' -Recurse | ForEach-Object { $_.FullName }
    & javac -d $out @javaFiles
    if ($LASTEXITCODE -ne 0) {
        Fail 'Compilation failed'
    }

    $inputFiles = Get-ChildItem -LiteralPath $casesDir -Filter '*-input.txt' | Sort-Object Name
    if ($inputFiles.Count -eq 0) {
        Fail "No test case input files found in $casesDir"
    }

    foreach ($inputFile in $inputFiles) {
        $name = $inputFile.Name -replace '-input\.txt$', ''
        $expectedFile = Join-Path $casesDir ($name + '-expected.txt')
        $actualFile = Join-Path $temp ($name + '-actual.txt')

        # Clean the data directory between test cases so that saved tasks
        # from one test do not leak into the next.
        $dataDir = Join-Path $projectRoot 'data'
        if (Test-Path -LiteralPath $dataDir) {
            Remove-Item -LiteralPath $dataDir -Recurse -Force -ErrorAction SilentlyContinue
        }

        Write-Host ''
        Write-Host "=== Test case: $name ==="

        Get-Content -LiteralPath $inputFile.FullName | & java -cp $out mochi.Mochi |
            Set-Content -LiteralPath $actualFile -Encoding utf8

        $expected = @(Get-Content -LiteralPath $expectedFile)
        $actual = @(Get-Content -LiteralPath $actualFile)

        Write-Host '--- Inputs ---'
        Get-Content -LiteralPath $inputFile.FullName | ForEach-Object { Write-Host "  > $_" }
        Write-Host '--- Actual output ---'
        $actual | ForEach-Object { Write-Host "  $_" }

        if ($actual.Count -ne $expected.Count) {
            Write-Host "--- Expected output ---"
            $expected | ForEach-Object { Write-Host "  $_" }
            Fail "Test case '$name': line count differs. Actual=$($actual.Count) Expected=$($expected.Count)"
        }

        for ($i = 0; $i -lt $actual.Count; $i++) {
            if ($actual[$i] -ne $expected[$i]) {
                Write-Host "--- Expected output ---"
                $expected | ForEach-Object { Write-Host "  $_" }
                Fail "Test case '$name': mismatch at line $($i + 1). Actual='$($actual[$i])' Expected='$($expected[$i])'"
            }
        }

        Write-Host 'PASS'
    }

    Write-Host ''
    Write-Host 'All test cases passed.'
} finally {
    Remove-Item -LiteralPath $temp -Recurse -Force -ErrorAction SilentlyContinue
    $dataDir = Join-Path $projectRoot 'data'
    if (Test-Path -LiteralPath $dataDir) {
        Remove-Item -LiteralPath $dataDir -Recurse -Force -ErrorAction SilentlyContinue
    }
}
