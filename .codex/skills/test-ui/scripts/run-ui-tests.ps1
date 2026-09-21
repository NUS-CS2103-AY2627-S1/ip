param(
    [string]$PlanPath = "test/ui-test-plan.md",
    [string]$RunCommand = "java -cp out keef.Keef"
)

$ErrorActionPreference = "Stop"
$divider = "____________________________________________________________"

if (-not (Test-Path -LiteralPath $PlanPath)) {
    throw "UI test plan not found: $PlanPath"
}

function Read-UntilDivider {
    param([System.IO.StreamReader]$Reader)

    $lines = [System.Collections.Generic.List[string]]::new()
    while ($true) {
        $line = $Reader.ReadLine()
        if ($null -eq $line) {
            throw "The program ended before printing its divider."
        }
        if ($line -eq $divider) {
            return ($lines -join [Environment]::NewLine)
        }
        $lines.Add($line)
    }
}

function Write-TranscriptResponse {
    param([string]$Command, [string]$Response)

    Write-Output "> $Command"
    Write-Output $divider
    if ($Response.Length -gt 0) {
        Write-Output $Response
    }
    Write-Output $divider
}

function Normalize-Output {
    param([string]$Output)

    return ([regex]::Replace($Output.Trim(), "`r`n?", "`n"))
}

$plan = Get-Content -LiteralPath $PlanPath -Raw
$caseMatches = [regex]::Matches($plan, '(?ms)^## Test case: (?<name>.+?)\r?\n(?<body>.*?)(?=^## Test case:|\z)')
if ($caseMatches.Count -eq 0) {
    throw "The plan must contain at least one '## Test case:' heading."
}

foreach ($caseMatch in $caseMatches) {
    $body = $caseMatch.Groups['body'].Value
    $aimMatch = [regex]::Match($body, '(?ms)^### Aim\r?\n(?<aim>.*?)(?=^### |^#### |\z)')
    if (-not $aimMatch.Success) {
        throw "Test case '$($caseMatch.Groups['name'].Value)' has no Aim section."
    }

    $stepMatches = [regex]::Matches(
        $body,
        '(?ms)^#### Command\s*```text\r?\n(?<command>.*?)\r?\n```\s*#### Expected output\s*```text\r?\n(?<expected>.*?)\r?\n```'
    )
    if ($stepMatches.Count -eq 0) {
        throw "Test case '$($caseMatch.Groups['name'].Value)' has no command steps."
    }

    Write-Output "=== Test case: $($caseMatch.Groups['name'].Value) ==="
    Write-Output "Aim: $($aimMatch.Groups['aim'].Value.Trim())"
    Write-Output "Console session:"

    $startInfo = [System.Diagnostics.ProcessStartInfo]::new()
    $startInfo.FileName = "cmd.exe"
    $startInfo.Arguments = "/c $RunCommand"
    $startInfo.UseShellExecute = $false
    $startInfo.RedirectStandardInput = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true
    $startInfo.CreateNoWindow = $true

    if (Test-Path -LiteralPath 'data') {
        Remove-Item -LiteralPath 'data' -Recurse -Force
    }
    $process = [System.Diagnostics.Process]::new()
    $process.StartInfo = $startInfo
    [void]$process.Start()

    try {
        [void](Read-UntilDivider $process.StandardOutput)
        $startup = Read-UntilDivider $process.StandardOutput
        Write-Output $divider
        Write-Output $startup
        Write-Output $divider

        foreach ($step in $stepMatches) {
            $command = $step.Groups['command'].Value.TrimEnd("`r", "`n")
            $expected = $step.Groups['expected'].Value.Trim()
            $process.StandardInput.WriteLine($command)
            $process.StandardInput.Flush()

            [void](Read-UntilDivider $process.StandardOutput)
            $actual = Read-UntilDivider $process.StandardOutput
            Write-TranscriptResponse $command $actual

            if ((Normalize-Output $actual) -ne (Normalize-Output $expected)) {
                Write-Output "FAILED: command '$command'"
                Write-Output "Expected output:"
                Write-Output $expected
                Write-Output "Actual output:"
                Write-Output $actual.Trim()
                if (-not $process.HasExited) {
                    $process.Kill()
                }
                exit 1
            }
        }
    } finally {
        if (-not $process.HasExited) {
            $process.StandardInput.Close()
            $process.WaitForExit()
        }
        $standardError = $process.StandardError.ReadToEnd().Trim()
        if ($standardError.Length -gt 0) {
            Write-Error $standardError
        }
        $process.Dispose()
    }

    Write-Output "PASSED: $($caseMatch.Groups['name'].Value)"
}
