$repositoryDirectory = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$temporaryDirectory = Join-Path ([System.IO.Path]::GetTempPath()) (
    'herta-ui-test-' + [System.Guid]::NewGuid().ToString('N'))
$temporaryDataDirectory = Join-Path $temporaryDirectory 'data'
$outputDirectory = Join-Path $repositoryDirectory 'out'
$commands = @($input)
$utf8Encoding = [System.Text.UTF8Encoding]::new($false)
$OutputEncoding = $utf8Encoding
$InputEncoding = $utf8Encoding
[Console]::OutputEncoding = $utf8Encoding
[Console]::InputEncoding = $utf8Encoding
$javaArguments = @('-Dstdout.encoding=UTF-8', '-Dstderr.encoding=UTF-8', '-cp', $outputDirectory,
    'herta.Herta')

New-Item -ItemType Directory -Path $temporaryDataDirectory -Force | Out-Null

if ($commands -contains 'restore 1') {
    $archiveFile = Join-Path $temporaryDataDirectory 'archive.txt'
    $savedArchiveTasks = @(
        'T | 1 | completed archive task',
        'T | 0 | incomplete archive task'
    )
    $utf8WithoutBom = [System.Text.UTF8Encoding]::new($false)
    [System.IO.File]::WriteAllLines($archiveFile, $savedArchiveTasks, $utf8WithoutBom)
}

$exitCode = 1
Push-Location -LiteralPath $temporaryDirectory
try {
    $commands | & java @javaArguments
    $exitCode = $LASTEXITCODE
} finally {
    Pop-Location
    Remove-Item -LiteralPath $temporaryDirectory -Recurse -Force -ErrorAction SilentlyContinue
}

exit $exitCode
