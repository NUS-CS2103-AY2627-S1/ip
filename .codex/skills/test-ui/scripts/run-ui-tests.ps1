param(
    [string]$TestPlan = "test/ui-test-plan.md",
    [string]$JarPath = ""
)

$ErrorActionPreference = "Stop"

$javaVersion = & javac -version
if ($javaVersion -notmatch '^javac 25\.') {
    throw "Java 25 is required, but the active compiler is: $javaVersion"
}

if (-not (Test-Path -LiteralPath $TestPlan)) {
    throw "UI test plan not found: $TestPlan"
}

if ($JarPath) {
    $JarPath = (Resolve-Path -LiteralPath $JarPath -ErrorAction Stop).Path
}

$plan = Get-Content -Raw -LiteralPath $TestPlan
$casePattern = '(?ms)^## Test case: (?<name>.+?)\r?\n\r?\nAim: (?<aim>.+?)\r?\n\r?\n(?:```saved\r?\n(?<saved>.*?)\r?\n```\r?\n\r?\n)?### Input\r?\n\r?\n```input\r?\n(?<input>.*?)\r?\n```\r?\n\r?\n### Expected output\r?\n\r?\n```expected\r?\n(?<expected>.*?)\r?\n```'
$cases = [regex]::Matches($plan, $casePattern)
if ($cases.Count -eq 0) {
    throw "No test cases found in $TestPlan"
}

$buildDirectory = Join-Path ([System.IO.Path]::GetTempPath()) ("phin-ui-tests-" + [guid]::NewGuid())
New-Item -ItemType Directory -Path $buildDirectory | Out-Null

try {
    if (-not $JarPath) {
        $sourceFiles = Get-ChildItem -LiteralPath "src/main/java" -Recurse -Filter "*.java" | ForEach-Object FullName
        & javac -d $buildDirectory $sourceFiles
        if ($LASTEXITCODE -ne 0) {
            throw "Compilation failed."
        }
    }

    foreach ($testCase in $cases) {
        $name = $testCase.Groups['name'].Value.Trim()
        $aim = $testCase.Groups['aim'].Value.Trim()
        $inputText = $testCase.Groups['input'].Value -replace "`r`n", "`n"
        $expected = $testCase.Groups['expected'].Value -replace "`r`n", "`n"

        $caseDirectory = Join-Path $buildDirectory ([guid]::NewGuid().ToString())
        New-Item -ItemType Directory -Path $caseDirectory | Out-Null
        if ($testCase.Groups['saved'].Success) {
            $dataDirectory = Join-Path $caseDirectory 'data'
            New-Item -ItemType Directory -Path $dataDirectory | Out-Null
            [System.IO.File]::WriteAllText((Join-Path $dataDirectory 'phin.txt'), $testCase.Groups['saved'].Value)
        }
        if ($JarPath) {
            Copy-Item -LiteralPath $JarPath -Destination (Join-Path $caseDirectory 'phin.jar')
        }
        Push-Location $caseDirectory
        try {
            $actualLines = foreach ($session in ($inputText -split '(?m)^# restart\r?\n')) {
                if ($JarPath) {
                    $session | & java -jar 'phin.jar'
                } else {
                    $session | & java -cp $buildDirectory phin.Phin
                }
                if ($LASTEXITCODE -ne 0) { throw 'Phin exited unsuccessfully.' }
            }
        } finally {
            Pop-Location
        }
        $actual = ($actualLines -join "`n")

        Write-Output "=== $name ==="
        Write-Output "Aim: $aim"
        Write-Output "Console input:"
        Write-Output $inputText
        Write-Output "Console output:"
        Write-Output $actual

        if ($actual -cne $expected) {
            Write-Output "Expected output:"
            Write-Output $expected
            throw "UI test failed: $name"
        }

        Write-Output "Result: PASS"
    }

    Write-Output "All $($cases.Count) UI test case(s) passed."
} finally {
    if (Test-Path -LiteralPath $buildDirectory) {
        $resolvedBuild = [System.IO.Path]::GetFullPath($buildDirectory)
        $tempRoot = [System.IO.Path]::GetFullPath([System.IO.Path]::GetTempPath())
        if (-not $resolvedBuild.StartsWith($tempRoot) -or (Split-Path $resolvedBuild -Leaf) -notlike 'phin-ui-tests-*') {
            throw 'Refusing to delete a directory outside the test workspace.'
        }
        Remove-Item -LiteralPath $resolvedBuild -Recurse -Force
    }
}
