$ErrorActionPreference = "Stop"

$repositoryRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot ".."))
$testDirectory = Join-Path ([IO.Path]::GetTempPath()) ("plana-storage-test-" + [guid]::NewGuid().ToString("N"))
$classesDirectory = Join-Path $testDirectory "classes"

New-Item -ItemType Directory -Path $classesDirectory -Force | Out-Null
Push-Location $testDirectory

try {
    $sourceFiles = @(Get-ChildItem -LiteralPath (Join-Path $repositoryRoot "src/main/java") -Filter *.java -File -Recurse |
        Where-Object {
            $_.Name -ne "Launcher.java" -and
            $_.FullName -notmatch "[\\/]plana[\\/]gui[\\/]"
        } |
        ForEach-Object { $_.FullName })
    & javac -d $classesDirectory @sourceFiles
    if ($LASTEXITCODE -ne 0) {
        throw "Compilation failed."
    }

    $input = @"
todo buy milk
deadline submit report /by 2019-10-15
event team meeting /from 2019-10-15 /to 2019-10-16
mark 1
delete 2
client add Alice /email alice@example.com /phone 91234567 /preferences no nuts
bye
"@
    $output = $input | & java -cp $classesDirectory plana.Plana
    if ($LASTEXITCODE -ne 0) {
        throw "Plana exited with code $LASTEXITCODE."
    }

    $dataFile = Join-Path $testDirectory "data/plana.txt"
    if (-not (Test-Path -LiteralPath $dataFile -PathType Leaf)) {
        throw "Expected save file was not created: $dataFile"
    }

    $actual = [IO.File]::ReadAllText($dataFile)
    $expected = "T | 1 | buy milk`nE | 0 | team meeting | 2019-10-15 | 2019-10-16`n"
    $actual = $actual -replace "`r`n|`r", "`n"
    if ($actual -cne $expected) {
        throw "Saved file contents were incorrect.`nExpected:`n$expected`nActual:`n$actual"
    }

    $clientDataFile = Join-Path $testDirectory "data/clients.txt"
    if (-not (Test-Path -LiteralPath $clientDataFile -PathType Leaf)) {
        throw "Expected client save file was not created: $clientDataFile"
    }
    $clientActual = [IO.File]::ReadAllText($clientDataFile)
    $clientExpected = "C | alice@example.com | Alice | 91234567 |  | no nuts | `n"
    $clientActual = $clientActual -replace "`r`n|`r", "`n"
    if ($clientActual -cne $clientExpected) {
        throw "Saved client file contents were incorrect.`nExpected:`n$clientExpected`nActual:`n$clientActual"
    }

    $corruptedRecords = @(
        "corrupted record",
        "T | 2 | invalid status",
        "D | 0 | missing date |",
        "E | 0 | missing end | 2pm |",
        "T | 0 | escaped \| pipe and \\ slash"
    )
    $corruptedText = [string]::Join([Environment]::NewLine, $corruptedRecords) + [Environment]::NewLine
    [IO.File]::AppendAllText($dataFile, $corruptedText, [Text.UTF8Encoding]::new($false))
    $corruptedClientRecords = @(
        "not a client record",
        "C | invalid | Missing valid domain |  |  |  | ",
        "C | alice@example.com | Duplicate |  |  |  | "
    )
    $corruptedClientText = [string]::Join([Environment]::NewLine, $corruptedClientRecords) + [Environment]::NewLine
    [IO.File]::AppendAllText($clientDataFile, $corruptedClientText, [Text.UTF8Encoding]::new($false))
    $secondInput = @"
list
client list
client view C1
bye
"@
    $secondOutput = (($secondInput | & java -cp $classesDirectory plana.Plana) -join "`n")
    if ($LASTEXITCODE -ne 0) {
        throw "Plana exited with code $LASTEXITCODE while loading."
    }
    if (-not $secondOutput.Contains("1.[T][X] buy milk") -or
        -not $secondOutput.Contains("2.[E][ ] team meeting (from: Oct 15 2019 to: Oct 16 2019)") -or
        -not $secondOutput.Contains("3.[T][ ] escaped | pipe and \ slash") -or
        -not $secondOutput.Contains("C1. Alice <alice@example.com>") -or
        -not $secondOutput.Contains("Preferences: no nuts")) {
        throw "Loaded task contents were incorrect."
    }

    Write-Output "Storage save/load test passed."
}
finally {
    Pop-Location
    if (Test-Path -LiteralPath $testDirectory) {
        Remove-Item -LiteralPath $testDirectory -Recurse -Force -ErrorAction SilentlyContinue
    }
}
