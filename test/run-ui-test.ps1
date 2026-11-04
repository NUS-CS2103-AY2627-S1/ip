# Give each UI case an isolated data directory so its persistent files cannot
# conflict with files or host activity left over from another test process.
$classesPath = (Resolve-Path "_temp/ui-test-classes").Path
$testDirectory = Join-Path ([System.IO.Path]::GetTempPath()) `
        ("soar-ui-" + [System.Guid]::NewGuid().ToString("N"))
$dataFile = Join-Path $testDirectory "tasks.txt"
$exitCode = 1

New-Item -ItemType Directory -Path $testDirectory | Out-Null
Push-Location $testDirectory
try {
    & java -ea -cp $classesPath soar.Soar "tasks.txt"
    $exitCode = $LASTEXITCODE
} finally {
    Pop-Location
    Remove-Item -LiteralPath $dataFile -Force -ErrorAction SilentlyContinue
    Remove-Item -LiteralPath $testDirectory -Force -ErrorAction SilentlyContinue
}

exit $exitCode
