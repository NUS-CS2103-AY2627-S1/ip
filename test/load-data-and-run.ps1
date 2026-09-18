$repositoryDirectory = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$temporaryDirectoryName = 'herta-load-ui-test-' + [System.Guid]::NewGuid().ToString('N')
$temporaryDirectory = Join-Path ([System.IO.Path]::GetTempPath()) $temporaryDirectoryName
$temporaryDataDirectory = Join-Path $temporaryDirectory 'data'
$outputDirectory = (Resolve-Path (Join-Path $repositoryDirectory 'out')).Path
$savedTasks = @(
    'T | 1 | loaded todo',
    'D | 0 | loaded deadline | 2019-10-15T00:00',
    'E | 0 | loaded event | 2019-10-15T00:00 | 2019-10-16T00:00'
)
$commands = @($input)
$utf8Encoding = [System.Text.UTF8Encoding]::new($false)
$OutputEncoding = $utf8Encoding
$InputEncoding = $utf8Encoding
[Console]::OutputEncoding = $utf8Encoding
[Console]::InputEncoding = $utf8Encoding
$javaArguments = @('-Dstdout.encoding=UTF-8', '-Dstderr.encoding=UTF-8', '-cp', $outputDirectory,
    'herta.Herta')

$exitCode = 1
$hasCreatedTemporaryDirectory = $false
$hasChangedLocation = $false
try {
    New-Item -ItemType Directory -Path $temporaryDataDirectory -Force | Out-Null
    $hasCreatedTemporaryDirectory = $true
    $dataFile = Join-Path $temporaryDataDirectory 'herta.txt'
    [System.IO.File]::WriteAllLines($dataFile, $savedTasks, $utf8Encoding)

    Push-Location -LiteralPath $temporaryDirectory
    $hasChangedLocation = $true
    $commands | & java @javaArguments
    $exitCode = $LASTEXITCODE
} finally {
    if ($hasChangedLocation) {
        Pop-Location
    }

    $temporaryDirectoryPath = (Resolve-Path -LiteralPath $temporaryDirectory -ErrorAction SilentlyContinue).Path
    $temporaryRootPath = (Resolve-Path -LiteralPath ([System.IO.Path]::GetTempPath())).Path
    $isExpectedTemporaryDirectory = $hasCreatedTemporaryDirectory `
        -and $null -ne $temporaryDirectoryPath `
        -and $temporaryDirectoryPath -eq $temporaryDirectory `
        -and (Split-Path -Parent $temporaryDirectoryPath) -eq $temporaryRootPath `
        -and $temporaryDirectoryName.StartsWith('herta-load-ui-test-', [System.StringComparison]::Ordinal)
    if ($isExpectedTemporaryDirectory) {
        Remove-Item -LiteralPath $temporaryDirectoryPath -Recurse -Force -ErrorAction SilentlyContinue
    }
}

exit $exitCode
