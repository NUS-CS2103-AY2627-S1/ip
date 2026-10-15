param(
    [Parameter(Mandatory = $true)] [string] $JavaExecutable,
    [Parameter(Mandatory = $true)] [string] $ClassPath,
    [Parameter(Mandatory = $true)] [string] $MainClass,
    [Parameter(Mandatory = $true)] [string] $CasesFile
)

$ErrorActionPreference = 'Stop'
$cases = Get-Content -Raw -LiteralPath $CasesFile | ConvertFrom-Json
$projectRoot = Get-Location
$absoluteClassPath = (Resolve-Path -LiteralPath $ClassPath).Path
$passed = 0

foreach ($case in $cases) {
    $caseDirectory = Join-Path $projectRoot "out\ui-test-work\$($case.id)"
    $dataFile = Join-Path $caseDirectory 'data\stephen.txt'
    [void] (New-Item -ItemType Directory -Force -Path $caseDirectory)
    if (Test-Path -LiteralPath $dataFile) {
        Remove-Item -LiteralPath $dataFile
    }
    if ($null -ne $case.initialDataLines) {
        $dataDirectory = Split-Path -Parent $dataFile
        [void] (New-Item -ItemType Directory -Force -Path $dataDirectory)
        [System.IO.File]::WriteAllLines($dataFile, [string[]] $case.initialDataLines)
    }

    Write-Output "=== $($case.id): $($case.aim) ==="
    $startInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $startInfo.FileName = $JavaExecutable
    $startInfo.ArgumentList.Add('-cp')
    $startInfo.ArgumentList.Add($absoluteClassPath)
    $startInfo.ArgumentList.Add($MainClass)
    $startInfo.WorkingDirectory = $caseDirectory
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
    $actualLines = if ($actualText.TrimEnd("`n").Length -eq 0) {
        @()
    } else {
        @($actualText.TrimEnd("`n") -split "`n")
    }
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
