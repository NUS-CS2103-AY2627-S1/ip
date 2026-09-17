$ErrorActionPreference = "Stop"
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$JarPath = Join-Path $ProjectRoot "build/libs/orbit.jar"
$OutputDirectory = Join-Path $ProjectRoot ("build/cli-test-" + [guid]::NewGuid().ToString("N"))
$ActualOutput = Join-Path $OutputDirectory "ACTUAL.TXT"
$ExpectedOutput = Join-Path $PSScriptRoot "EXPECTED.TXT"

if (-not (Test-Path -LiteralPath $JarPath)) {
    throw "Build the packaged app first: gradlew check shadowJar"
}

# Isolate persisted tasks so every run starts empty and preserves the user's data.
New-Item -ItemType Directory -Path $OutputDirectory | Out-Null
Push-Location $OutputDirectory
try {
    Get-Content (Join-Path $PSScriptRoot "input.txt") |
        java -ea '-Duser.language=en' '-Duser.country=US' -cp $JarPath orbit.Orbit |
        Set-Content $ActualOutput
    if ($LASTEXITCODE -ne 0) {
        throw "Packaged CLI exited with code $LASTEXITCODE."
    }
} finally {
    Pop-Location
}

$ExpectedLines = (Get-Content $ExpectedOutput) -join "`n"
$ActualLines = (Get-Content $ActualOutput) -join "`n"
if ($ExpectedLines -cne $ActualLines) {
    Write-Host "Actual output: $ActualOutput"
    throw "Text UI test failed: ACTUAL.TXT differs from EXPECTED.TXT."
}

Write-Host "Text UI test passed."
