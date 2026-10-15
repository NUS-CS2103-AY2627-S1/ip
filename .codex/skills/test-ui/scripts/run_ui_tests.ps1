param(
    [Parameter(Mandatory = $true)]
    [string] $JavaExecutable,

    [Parameter(Mandatory = $true)]
    [string] $ClassPath,

    [Parameter(Mandatory = $true)]
    [string] $MainClass,

    [Parameter(Mandatory = $true)]
    [string] $CasesFile
)

$ErrorActionPreference = 'Stop'
$cases = Get-Content -Raw -LiteralPath $CasesFile | ConvertFrom-Json
$passed = 0

foreach ($case in $cases) {
    Write-Output "=== $($case.id): $($case.aim) ==="

    $startInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $startInfo.FileName = $JavaExecutable
    $startInfo.ArgumentList.Add('-cp')
    $startInfo.ArgumentList.Add($ClassPath)
    $startInfo.ArgumentList.Add($MainClass)
    $startInfo.UseShellExecute = $false
    $startInfo.RedirectStandardInput = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true
    $startInfo.CreateNoWindow = $true

    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $startInfo
    [void] $process.Start()

    foreach ($command in $case.commands) {
        Write-Output "> $command"
        $process.StandardInput.WriteLine($command)
    }
    $process.StandardInput.Close()

    $actualText = $process.StandardOutput.ReadToEnd()
    $errorText = $process.StandardError.ReadToEnd()
    $process.WaitForExit()

    $actualText = $actualText -replace "`r`n", "`n" -replace "`r", "`n"
    $actualText = $actualText.TrimEnd("`n")
    $actualLines = if ($actualText.Length -eq 0) { @() } else { @($actualText -split "`n") }
    $expectedLines = @($case.expectedOutput | ForEach-Object { [string] $_ })

    Write-Output '--- Console output ---'
    $actualLines | ForEach-Object { Write-Output $_ }

    $matches = $process.ExitCode -eq 0 -and $errorText.Length -eq 0
    if ($matches) {
        $matches = $actualLines.Count -eq $expectedLines.Count
    }
    if ($matches) {
        for ($index = 0; $index -lt $expectedLines.Count; $index++) {
            if ($actualLines[$index] -cne $expectedLines[$index]) {
                $matches = $false
                break
            }
        }
    }

    if (-not $matches) {
        Write-Output '--- RESULT: FAILED ---'
        Write-Output '--- Expected output ---'
        $expectedLines | ForEach-Object { Write-Output $_ }
        if ($errorText.Length -gt 0) {
            Write-Output '--- Standard error ---'
            Write-Output $errorText.TrimEnd()
        }
        Write-Output "Stopped after $passed passed test case(s); later cases were not run."
        exit 1
    }

    $passed++
    Write-Output '--- RESULT: PASSED ---'
}

Write-Output "All $passed test case(s) passed."
