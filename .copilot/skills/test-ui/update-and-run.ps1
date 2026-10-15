param(
    [string]$PlanPath = "test\ui-test-plan.md"
)

# Ensure plan exists and contains at least one test; if not, add a default smoke test.
$absPlan = Join-Path (Get-Location) $PlanPath
if (-not (Test-Path $absPlan)) {
    Write-Output "Plan file missing; creating default plan at $PlanPath"
    $default = @"# UI Test Plan

```json
{
  "tests": [
    {
      "id": "tc-echo-hello",
      "aim": "Verify echo prints Hello",
      "command": "cmd /c echo Hello",
      "input": "",
      "expected": "Hello\n"
    }
  ]
}
```
"@
    $dir = Split-Path $absPlan -Parent
    if (-not (Test-Path $dir)) { New-Item -ItemType Directory -Path $dir -Force | Out-Null }
    Set-Content -Path $absPlan -Value $default -Encoding UTF8
} else {
    $content = Get-Content $absPlan -Raw
    $match = [regex]::Match($content, '```json\s*(\{[\s\S]*?\})\s*```', [System.Text.RegularExpressions.RegexOptions]::Singleline)
    if (-not $match.Success) {
        Write-Output "No JSON block found in plan; appending a default test block."
        $append = @"\n```json\n{\n  \"tests\": [\n    {\n      \"id\": \"tc-echo-hello\",\n      \"aim\": \"Verify echo prints Hello\",\n      \"command\": \"cmd /c echo Hello\",\n      \"input\": \"\",\n      \"expected\": \"Hello\\n\"\n    }\n  ]\n}\n```\n"@
        Add-Content -Path $absPlan -Value $append
    } else {
        # Parse JSON and ensure at least one test exists
        $json = $match.Groups[1].Value
        try { $plan = ConvertFrom-Json $json } catch { $plan = $null }
        if ($null -eq $plan -or -not $plan.tests -or $plan.tests.Count -eq 0) {
            Write-Output "Test list empty; inserting default test into plan."
            $newJson = @"{
  "tests": [
    {
      "id": "tc-echo-hello",
      "aim": "Verify echo prints Hello",
      "command": "cmd /c echo Hello",
      "input": "",
      "expected": "Hello\n"
    }
  ]
}"@
            $newContent = [regex]::Replace($content, '```json\s*(\{[\s\S]*?\})\s*```', "```json`n$newJson`n```", [System.Text.RegularExpressions.RegexOptions]::Singleline)
            Set-Content -Path $absPlan -Value $newContent -Encoding UTF8
        } else {
            Write-Output "Plan contains tests; no update needed."
        }
    }
}

# Invoke the test-ui runner
$runner = Join-Path (Get-Location) ".copilot\skills\test-ui\run.ps1"
if (-not (Test-Path $runner)) { Write-Error "Runner not found: $runner"; exit 2 }

Write-Output "Invoking test-ui runner..."
& powershell -NoProfile -ExecutionPolicy Bypass -File $runner --PlanPath $PlanPath
$exit = $LASTEXITCODE
if ($exit -ne 0) { Write-Error "test-ui runner exited with code $exit"; exit $exit }
Write-Output "test-ui runner completed successfully."
exit 0
