param(
    [string]$PlanPath = "test\ui-test-plan.md"
)

# This script checks whether the test plan needs updating (simple heuristics) and then runs the test-ui runner.
# It exits with non-zero if the plan appears outdated so CI can fail and require a human commit.

Write-Host "Checking for built artifacts (jar files)..."
$jar = Get-ChildItem -Path . -Recurse -Filter *.jar -ErrorAction SilentlyContinue | Sort-Object LastWriteTime -Descending | Select-Object -First 1

$planNeedsUpdate = $false
$reason = ""
if ($null -ne $jar) {
    $jarRel = $jar.FullName.Substring((Get-Location).Path.Length+1) -replace '/','\\'
    Write-Host "Found jar: $jarRel"

    $planText = Get-Content $PlanPath -Raw -ErrorAction SilentlyContinue
    if ($null -eq $planText) { $planText = "" }

    if ($planText -notmatch [regex]::Escape("java -jar $jarRel")) {
        # If the plan contains any java -jar command that doesn't match the found jar, flag update.
        if ($planText -match "java -jar") {
            $planNeedsUpdate = $true
            $reason = "test plan contains a java -jar command but it doesn't reference the built jar: $jarRel"
        } else {
            $planNeedsUpdate = $true
            $reason = "built jar exists ($jarRel) but no java -jar command found in plan"
        }
    }
} else {
    Write-Host "No jar found; skipping jar-based checks." 
}

if ($planNeedsUpdate) {
    Write-Host "TEST PLAN OUT OF DATE: $reason" -ForegroundColor Yellow
    Write-Host "Please update $PlanPath to include commands for the built artifact (or run the updater locally)." -ForegroundColor Yellow
    exit 4
}

# If checks passed, run the test-ui runner
Write-Host "Running test-ui runner..."
& powershell -ExecutionPolicy Bypass -File .copilot\skills\test-ui\scripts\test-ui.ps1 $PlanPath
exit $LASTEXITCODE
