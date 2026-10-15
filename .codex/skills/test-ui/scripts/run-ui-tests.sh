#!/usr/bin/env bash
# Runs the console UI test cases recorded in test/ui-test-plan.md.
set -u

plan_file="test/ui-test-plan.md"
session_file="test/ui-test-session.log"

if [[ ! -f "$plan_file" ]]; then
    echo "Test plan not found: $plan_file" >&2
    exit 2
fi

mkdir -p "$(dirname "$session_file")"
: > "$session_file"
log() { printf '%s\n' "$1" | tee -a "$session_file"; }
log_block() { printf '%s\n' "$1" | tee -a "$session_file"; }

trim_trailing_newlines() {
    local value="$1"
    while [[ "$value" == *$'\n' ]]; do value="${value%$'\n'}"; done
    printf '%s' "$value"
}
normalise() { printf '%s' "$1" | sed $'s/\r$//'; }
fail() {
    log "RESULT: FAILED — $case_name"
    log "Expected output:"; log_block "$expected_output"
    log "Actual output:"; log_block "$actual_output"
    log "Test session terminated after the first failure."
    exit 1
}
run_case() {
    case_name="$1"; command="$2"; input="$3"; expected_output="$4"
    log "=== $case_name ==="
    log "Command:"; log_block "$command"
    log "Console input:"; log_block "$input"
    log "Console output:"
    actual_output="$(printf '%s' "$input" | bash -lc "$command" 2>&1)"; status=$?
    actual_output="$(trim_trailing_newlines "$actual_output")"
    expected_output="$(trim_trailing_newlines "$expected_output")"
    log_block "$actual_output"
    [[ $status -eq 0 ]] || { log "Program exited with status $status."; fail; }
    [[ "$(normalise "$actual_output")" == "$(normalise "$expected_output")" ]] || fail
    log "RESULT: PASSED"
}

case_name=""; command=""; input=""; expected_output=""; field=""; in_fence=0; case_count=0
while IFS= read -r line || [[ -n "$line" ]]; do
    if [[ "$line" =~ ^##\ Test\ Case:\ (.+)$ ]]; then
        if [[ -n "$case_name" ]]; then run_case "$case_name" "$command" "$input" "$expected_output"; case_count=$((case_count + 1)); fi
        case_name="${BASH_REMATCH[1]}"; command=""; input=""; expected_output=""; field=""; in_fence=0
    elif [[ "$line" =~ ^-\ \*\*(Command|Input|Expected\ output)\*\* ]]; then
        field="${BASH_REMATCH[1]}"
    elif [[ "$line" == '```'* ]]; then
        in_fence=$((1 - in_fence))
    elif [[ $in_fence -eq 1 ]]; then
        case "$field" in
            Command) command+="$line"$'\n' ;;
            Input) input+="$line"$'\n' ;;
            "Expected output") expected_output+="$line"$'\n' ;;
        esac
    fi
done < "$plan_file"
if [[ -n "$case_name" ]]; then run_case "$case_name" "$command" "$input" "$expected_output"; case_count=$((case_count + 1)); fi
[[ $case_count -gt 0 ]] || { log "No test cases found. Use headings like: ## Test Case: Name"; exit 2; }
log "All $case_count UI test case(s) passed."
