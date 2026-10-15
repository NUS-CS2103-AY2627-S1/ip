$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $PSScriptRoot
$classesDirectory = Join-Path $projectRoot "target/classes"
$mavenRepository = Join-Path $projectRoot "_temp/.m2"
$dataFile = Join-Path $projectRoot "data/gongrilla.txt"
$actualFile = Join-Path $PSScriptRoot "ACTUAL.TXT"
$expectedDataFile = Join-Path $PSScriptRoot "EXPECTED-DATA.TXT"
$expectedOutputFragmentsFile = Join-Path $PSScriptRoot "EXPECTED-OUTPUT-FRAGMENTS.TXT"

Remove-Item $dataFile -ErrorAction SilentlyContinue

mvn "-Dmaven.repo.local=$mavenRepository" -q -DskipTests compile
if ($LASTEXITCODE -ne 0) {
    throw "Maven compilation failed."
}
Get-Content (Join-Path $PSScriptRoot "INPUT.TXT") |
        java -cp $classesDirectory gongrilla.Gongrilla |
        Set-Content $actualFile
if ($LASTEXITCODE -ne 0) {
    throw "gongrilla.Gongrilla exited with an error."
}

$expectedData = Get-Content $expectedDataFile
$actualData = Get-Content $dataFile
if (Compare-Object $actualData $expectedData) {
    throw "Saved task data did not match EXPECTED-DATA.TXT"
}

$actualOutput = Get-Content -Raw $actualFile
foreach ($expectedFragment in Get-Content $expectedOutputFragmentsFile) {
    if (!$actualOutput.Contains($expectedFragment)) {
        throw "Output did not contain: $expectedFragment"
    }
}

Write-Output "UI persistence test passed."
