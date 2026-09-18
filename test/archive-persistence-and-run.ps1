$repositoryDirectory = (Resolve-Path (Join-Path $PSScriptRoot '..')).Path
$temporaryDirectory = Join-Path ([System.IO.Path]::GetTempPath()) (
    'herta-archive-ui-test-' + [System.Guid]::NewGuid().ToString('N'))
$temporaryDataDirectory = Join-Path $temporaryDirectory 'data'
$outputDirectory = Join-Path $repositoryDirectory 'out'
$secondSessionCommands = @($input)
$firstSessionCommands = @('todo persisted task', 'mark 1', 'archive 1', 'bye')
$utf8Encoding = [System.Text.UTF8Encoding]::new($false)
$OutputEncoding = $utf8Encoding
$InputEncoding = $utf8Encoding
[Console]::OutputEncoding = $utf8Encoding
[Console]::InputEncoding = $utf8Encoding
$javaArguments = @('-Dstdout.encoding=UTF-8', '-Dstderr.encoding=UTF-8', '-cp', $outputDirectory,
    'herta.Herta')

New-Item -ItemType Directory -Path $temporaryDataDirectory -Force | Out-Null

$exitCode = 1
Push-Location -LiteralPath $temporaryDirectory
try {
    $firstSessionCommands | & java @javaArguments
    $firstExitCode = $LASTEXITCODE
    $secondSessionCommands | & java @javaArguments
    $exitCode = if ($firstExitCode -eq 0) { $LASTEXITCODE } else { $firstExitCode }
} finally {
    Pop-Location
    Remove-Item -LiteralPath $temporaryDirectory -Recurse -Force -ErrorAction SilentlyContinue
}

exit $exitCode
