$ErrorActionPreference = "Stop"
$repositoryRoot = (Resolve-Path (Join-Path $PSScriptRoot "../../../../")).Path
$testDirectory = Join-Path $repositoryRoot "text-ui-test"
$compileDirectory = Join-Path $env:TEMP "slotbot-ui-test-$([Guid]::NewGuid())"
$runtimeDirectory = Join-Path $env:TEMP "slotbot-ui-runtime-$([Guid]::NewGuid())"
$actualPath = Join-Path $testDirectory "ACTUAL.TXT"
$inputPath = Join-Path $testDirectory "input.txt"
$expectedPath = Join-Path $testDirectory "EXPECTED.TXT"

try {
    New-Item -ItemType Directory -Path $compileDirectory | Out-Null
    New-Item -ItemType Directory -Path $runtimeDirectory | Out-Null

    $strictErrorPreference = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    $javaVersion = (java -version 2>&1 | Out-String)
    $ErrorActionPreference = $strictErrorPreference
    if ($javaVersion -notmatch 'version "25') {
        throw "Java 25 is required. Detected: $javaVersion"
    }

    # Compile the retained console and shared core without the JavaFX-only package.
    $sourceFiles = Get-ChildItem -Path (Join-Path $repositoryRoot "src/main/java") -Filter "*.java" -Recurse |
        Where-Object { $_.FullName -notmatch '[\\/]slotbot[\\/]gui[\\/]' }
    javac -d $compileDirectory $sourceFiles.FullName

    $processInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $processInfo.FileName = "java"
    $processInfo.Arguments = "-cp `"$compileDirectory`" slotbot.SlotBot"
    $processInfo.WorkingDirectory = $runtimeDirectory
    $processInfo.RedirectStandardInput = $true
    $processInfo.RedirectStandardOutput = $true
    $processInfo.RedirectStandardError = $true
    $processInfo.UseShellExecute = $false

    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $processInfo
    [void] $process.Start()
    $inputText = Get-Content -Raw -LiteralPath $inputPath
    $process.StandardInput.Write($inputText)
    $process.StandardInput.Close()
    $actualOutput = $process.StandardOutput.ReadToEnd()
    $errorOutput = $process.StandardError.ReadToEnd()
    $process.WaitForExit()

    [System.IO.File]::WriteAllText($actualPath, $actualOutput)

    if ($process.ExitCode -ne 0) {
        throw "Program exited with code $($process.ExitCode): $errorOutput"
    }

    $savePath = Join-Path $runtimeDirectory "data\slotbot.txt"
    if (-not (Test-Path -LiteralPath $savePath)) {
        throw "UI regression test did not create the relative data/slotbot.txt save file."
    }

    $normalizedActual = $actualOutput -replace "`r", ""
    $expectedOutput = Get-Content -Raw -LiteralPath $expectedPath
    $normalizedExpected = $expectedOutput -replace "`r", ""

    Write-Output "--- INPUT ---"
    Write-Output $inputText
    Write-Output "--- OUTPUT ---"
    Write-Output $actualOutput

    if ($normalizedActual -cne $normalizedExpected) {
        Write-Output "--- EXPECTED OUTPUT ---"
        Write-Output $expectedOutput
        throw "UI regression test failed: ACTUAL.TXT differs from EXPECTED.TXT."
    }

    Write-Output "PASS"
} finally {
    if (Test-Path $compileDirectory) {
        Remove-Item -LiteralPath $compileDirectory -Recurse -Force
    }
    if (Test-Path $runtimeDirectory) {
        Remove-Item -LiteralPath $runtimeDirectory -Recurse -Force
    }
}
