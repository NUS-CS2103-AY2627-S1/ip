param(
    [Parameter(Mandatory = $true)] [string[]]$Commands,
    [Parameter(Mandatory = $true)] [string[]]$ExpectedOutputs
)

$ErrorActionPreference = 'Stop'
if ($Commands.Count -ne $ExpectedOutputs.Count) {
    throw 'Commands and ExpectedOutputs must contain the same number of items.'
}

$javacCommand = Get-Command javac -ErrorAction SilentlyContinue
$javaCommand = Get-Command java -ErrorAction SilentlyContinue
if ($null -eq $javacCommand -or $null -eq $javaCommand) {
    throw 'Java 25 must be available on PATH as both javac and java.'
}
$outputDirectory = Join-Path $env:TEMP ('test-ui-' + [guid]::NewGuid())
New-Item -ItemType Directory -Path $outputDirectory | Out-Null

try {
    $javac = $javacCommand.Source
    $java = $javaCommand.Source
    $sourceFiles = Get-ChildItem src/main/java -Recurse -Filter *.java | ForEach-Object FullName
    $compilerOutput = & $javac -d $outputDirectory $sourceFiles 2>&1
    if ($LASTEXITCODE -ne 0) {
        Write-Host 'UI test failed: compilation failed.'
        $compilerOutput | ForEach-Object { Write-Host $_ }
        exit 1
    }
    $processInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $processInfo.FileName = $java
    $processInfo.Arguments = "-cp `"$outputDirectory`" arthur.ArthurDent"
    $processInfo.UseShellExecute = $false
    $processInfo.RedirectStandardInput = $true
    $processInfo.RedirectStandardOutput = $true
    $processInfo.RedirectStandardError = $true
    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $processInfo
    $process.Start() | Out-Null
    $Commands | ForEach-Object { $process.StandardInput.WriteLine($_) }
    $process.StandardInput.Close()
    $transcriptText = $process.StandardOutput.ReadToEnd().TrimEnd()
    $standardError = $process.StandardError.ReadToEnd().TrimEnd()
    $process.WaitForExit()
    if ($standardError) { $transcriptText += [Environment]::NewLine + $standardError }
    Write-Host '--- Console session ---'
    $Commands | ForEach-Object { Write-Host "> $_" }
    Write-Host '--- Console output ---'
    Write-Host $transcriptText

    $line = [regex]::Escape('_' * 60)
    $responses = @([regex]::Matches($transcriptText, "(?s)$line\r?\n(.*?)\r?\n$line") | ForEach-Object { $_.Groups[1].Value } | Select-Object -Skip 1)
    for ($index = 0; $index -lt $ExpectedOutputs.Count; $index++) {
        $actual = if ($index -lt $responses.Count) { $responses[$index] } else { '<no output>' }
        if ($actual -notmatch $ExpectedOutputs[$index]) {
            Write-Host "UI test failed at command $($index + 1): $($Commands[$index])"
            Write-Host "Expected pattern: $($ExpectedOutputs[$index])"
            Write-Host "Actual: $actual"
            exit 1
        }
    }
    Write-Host "UI test passed: $($Commands.Count) command(s) matched."
} finally {
    Remove-Item -Recurse -Force -LiteralPath $outputDirectory
}
