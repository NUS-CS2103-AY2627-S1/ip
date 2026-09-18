param(
    [string]$TestPlanPath = "test/ui-test-plan.md",
    [string]$MainClass = "plana.Plana",
    [string]$SourcePath = "src/main/java",
    [int]$TimeoutSeconds = 30
)

$ErrorActionPreference = "Stop"

function Normalize-LineEndings {
    param([AllowNull()][string]$Text)

    if ($null -eq $Text) {
        return ""
    }

    return [regex]::Replace($Text, "`r`n|`r", "`n")
}

function Get-MarkdownBlock {
    param(
        [string]$Body,
        [string]$Heading,
        [string]$TestName
    )

    $headingPattern = [regex]::Escape($Heading)
    $pattern = '(?ms)^###\s+' + $headingPattern + '\s*\r?\n```(?:text)?\r?\n(?<value>.*?)\r?\n```'
    $match = [regex]::Match($Body, $pattern)
    if (-not $match.Success) {
        throw "Test case '$TestName' is missing a fenced '$Heading' block."
    }

    return $match.Groups["value"].Value
}

function Show-Transcript {
    param(
        [string]$Title,
        [string]$InputText,
        [string]$OutputText
    )

    Write-Host ""
    Write-Host "=== $Title ==="
    Write-Host "--- Console input ---"
    Write-Host $InputText
    Write-Host "--- Console output ---"
    Write-Host $OutputText
}

try {
    $planFile = [IO.Path]::GetFullPath($TestPlanPath)
    $sourceDirectory = [IO.Path]::GetFullPath($SourcePath)

    if (-not (Test-Path -LiteralPath $planFile -PathType Leaf)) {
        throw "Test plan not found: $planFile"
    }
    if (-not (Test-Path -LiteralPath $sourceDirectory -PathType Container)) {
        throw "Source directory not found: $sourceDirectory"
    }
    if ($TimeoutSeconds -le 0) {
        throw "TimeoutSeconds must be positive."
    }

    $javaVersion = (& java -version 2>&1 | Out-String)
    $javaMatch = [regex]::Match($javaVersion, '"(?<major>\d+)')
    if (-not $javaMatch.Success -or $javaMatch.Groups["major"].Value -ne "25") {
        throw "Java 25 is required. Detected version output:`n$javaVersion"
    }

    $plan = [IO.File]::ReadAllText($planFile)
    $casePattern = '(?ms)^##\s+Test Case:\s*(?<name>[^\r\n]+)\r?\n(?<body>.*?)(?=^##\s+Test Case:|\z)'
    $cases = [regex]::Matches($plan, $casePattern)
    if ($cases.Count -eq 0) {
        throw "No test cases found. Add '## Test Case: ...' sections to $planFile."
    }

    # JavaFX sources are compiled by Gradle; the console test runner excludes
    # them so it can continue using javac without a JavaFX classpath.
    $sourceFiles = @(Get-ChildItem -LiteralPath $sourceDirectory -Filter *.java -File -Recurse |
        Where-Object {
            $_.Name -ne "Launcher.java" -and
            $_.FullName -notmatch "[\\/]plana[\\/]gui[\\/]"
        } |
        ForEach-Object { $_.FullName })
    if ($sourceFiles.Count -eq 0) {
        throw "No Java source files found under $sourceDirectory."
    }

    $runDirectory = Join-Path ([IO.Path]::GetTempPath()) ("test-ui-" + [guid]::NewGuid().ToString("N"))
    $classesDirectory = Join-Path $runDirectory "classes"
    New-Item -ItemType Directory -Path $classesDirectory -Force | Out-Null

    try {
        Write-Host "Compiling $($sourceFiles.Count) Java source file(s) with Java 25..."
        $compileOutput = @(& javac -d $classesDirectory @sourceFiles 2>&1)
        if ($LASTEXITCODE -ne 0) {
            throw "Compilation failed:`n$($compileOutput -join [Environment]::NewLine)"
        }

        for ($caseIndex = 0; $caseIndex -lt $cases.Count; $caseIndex++) {
            $case = $cases[$caseIndex]
            $testName = $case.Groups["name"].Value.Trim()
            $body = $case.Groups["body"].Value
            $inputText = Get-MarkdownBlock -Body $body -Heading "Inputs" -TestName $testName
            $expectedText = Get-MarkdownBlock -Body $body -Heading "Expected Output" -TestName $testName
            $inputText = Normalize-LineEndings $inputText
            $expectedText = Normalize-LineEndings $expectedText

            $caseDirectory = Join-Path $runDirectory ("case-" + ($caseIndex + 1))
            New-Item -ItemType Directory -Path $caseDirectory -Force | Out-Null
            $inputFile = Join-Path $caseDirectory "input.txt"
            $outputFile = Join-Path $caseDirectory "stdout.txt"
            $errorFile = Join-Path $caseDirectory "stderr.txt"
            [IO.File]::WriteAllText($inputFile, $inputText + "`n", [Text.UTF8Encoding]::new($false))

            $process = Start-Process -FilePath "java" `
                -ArgumentList @("-Dstdout.encoding=UTF-8", "-cp", $classesDirectory, $MainClass) `
                -RedirectStandardInput $inputFile `
                -RedirectStandardOutput $outputFile `
                -RedirectStandardError $errorFile `
                -NoNewWindow `
                -WorkingDirectory $caseDirectory `
                -PassThru

            if (-not $process.WaitForExit($TimeoutSeconds * 1000)) {
                $process.Kill()
                throw "Test case '$testName' timed out after $TimeoutSeconds second(s)."
            }

            $actualText = Normalize-LineEndings ([IO.File]::ReadAllText($outputFile))
            $errorText = Normalize-LineEndings ([IO.File]::ReadAllText($errorFile))
            $expectedWithFinalNewline = $expectedText + "`n"
            Show-Transcript -Title "Test Case: $testName" -InputText $inputText -OutputText $actualText

            if ($process.ExitCode -ne 0 -or $errorText.Length -gt 0 -or $actualText -cne $expectedWithFinalNewline) {
                Write-Host "FAIL: $testName"
                Write-Host "--- Expected output ---"
                Write-Host $expectedWithFinalNewline
                Write-Host "--- Actual output ---"
                Write-Host $actualText
                if ($errorText.Length -gt 0) {
                    Write-Host "--- Standard error ---"
                    Write-Host $errorText
                }
                throw "Test session stopped after the first failure in '$testName'."
            }

            Write-Host "PASS: $testName"
        }

        Write-Host ""
        Write-Host "All $($cases.Count) UI test case(s) passed."
    }
    finally {
        if (Test-Path -LiteralPath $runDirectory) {
            Remove-Item -LiteralPath $runDirectory -Recurse -Force -ErrorAction SilentlyContinue
        }
    }
}
catch {
    Write-Error $_.Exception.Message
    exit 1
}
