#!/usr/bin/env python3
"""Run Nudge's data-driven command-line UI tests in batches."""

import argparse
import difflib
import itertools
import json
from pathlib import Path
import subprocess
import sys
import tempfile


SEPARATOR = "_" * 60
STARTUP_LINES = [
    " _   _           _            ",
    "| \\ | |_   _  __| | __ _  ___ ",
    "|  \\| | | | |/ _` |/ _` |/ _ \\",
    "| |\\  | |_| | (_| | (_| |  __/",
    "|_| \\_|\\__,_|\\__,_|\\__, |\\___|",
    "                   |___/",
    "    > Hi, I'm Nudge. Ready when you are—what should we keep on your radar?",
]
FAILURE_ARTIFACT = Path("_temp/ui-test-failure.txt")


class UiTestConfigurationError(Exception):
    """Indicates that the UI test configuration cannot be executed safely."""


def parse_arguments():
    """Parse command-line options for selecting fixtures and cases."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--fixtures",
        default="test/ui-test-cases.json",
        help="path to the JSON fixture file",
    )
    parser.add_argument(
        "--case",
        action="append",
        dest="case_ids",
        help="case ID to run; repeat to select multiple cases",
    )
    return parser.parse_args()


def format_fixture_value(value, index):
    """Substitute a repeat index throughout one fixture value."""
    if isinstance(value, str):
        if value == "{i}":
            return index
        return value.format(i=index)
    if isinstance(value, list):
        return [format_fixture_value(item, index) for item in value]
    if isinstance(value, dict):
        return {
            key: format_fixture_value(item, index)
            for key, item in value.items()
            if key != "repeat"
        }
    return value


def expand_steps(case):
    """Expand compact repeat specifications into executable command steps."""
    for step in case["steps"]:
        repeat = step.get("repeat")
        if repeat is None:
            yield step
            continue

        for index in range(repeat["from"], repeat["to"] + 1):
            yield format_fixture_value(step, index)


def response_lines(expectation):
    """Render an expected response into its exact lines."""
    kind = expectation["kind"]
    if kind == "added":
        count = int(expectation["count"])
        task_label = "task" if count == 1 else "tasks"
        return [
            "    > On your radar—I've added:",
            f"      {expectation['task']}",
            f"    > You now have {count} {task_label} on your radar.",
        ]
    if kind == "list":
        return ["    > Here's what's on your radar:"] + [
            f"      {index}.{task}"
            for index, task in enumerate(expectation["tasks"], start=1)
        ]
    if kind == "matches":
        return ["    > Here are the matches I found:"] + [
            f"      {index}.{task}"
            for index, task in enumerate(expectation["tasks"], start=1)
        ]
    if kind == "sorted":
        return ["    > All lined up—your deadlines are sorted by date:"] + [
            f"      {index}.{task}"
            for index, task in enumerate(expectation["tasks"], start=1)
        ]
    if kind == "marked":
        return [
            "    > Nice work—that one's complete:",
            f"      {expectation['task']}",
        ]
    if kind == "unmarked":
        return [
            "    > Back on your radar—this task is not done yet:",
            f"      {expectation['task']}",
        ]
    if kind == "removed":
        count = int(expectation["count"])
        task_label = "task" if count == 1 else "tasks"
        return [
            "    > Cleared away—I've removed:",
            f"      {expectation['task']}",
            f"    > You now have {count} {task_label} on your radar.",
        ]
    if kind == "message":
        return [f"    > {expectation['text']}"]
    if kind == "lines":
        return expectation["lines"]
    raise UiTestConfigurationError(f"Unknown expectation kind: {kind}")


def framed(lines):
    """Wrap response lines in the application's separator lines."""
    return [SEPARATOR, *lines, SEPARATOR]


def build_expected_output(case):
    """Build exact output and line ranges for each expanded command."""
    output_lines = framed(STARTUP_LINES)
    ranges = [(1, len(output_lines), "startup")]
    startup_message = case.get("startup_message")
    if startup_message is not None:
        start_line = len(output_lines) + 1
        output_lines.extend(framed([f"    > {startup_message}"]))
        ranges.append((start_line, len(output_lines), "startup storage"))
    expanded_steps = list(expand_steps(case))

    for step in expanded_steps:
        start_line = len(output_lines) + 1
        output_lines.extend(framed(response_lines(step["expect"])))
        ranges.append((start_line, len(output_lines), step["input"]))

    return "\n".join(output_lines) + "\n", ranges, expanded_steps


def normalize_output(output):
    """Normalize only CRLF line endings, as required by the test plan."""
    if isinstance(output, bytes):
        output = output.decode("utf-8", errors="replace")
    return output.replace("\r\n", "\n")


def first_mismatch_line(expected, actual):
    """Return the one-based line number of the first output mismatch."""
    for line_number, (expected_line, actual_line) in enumerate(
        itertools.zip_longest(expected.splitlines(), actual.splitlines()), start=1
    ):
        if expected_line != actual_line:
            return line_number
    return None


def command_for_line(ranges, line_number):
    """Map a mismatching output line to its originating command."""
    for start_line, end_line, command in ranges:
        if start_line <= line_number <= end_line:
            return command
    return "process completion"


def validate_cases(cases):
    """Reject ambiguous fixture data before compiling the application."""
    seen_ids = set()
    for case in cases:
        case_id = case["id"]
        if case_id in seen_ids:
            raise UiTestConfigurationError(f"Duplicate case ID: {case_id}")
        seen_ids.add(case_id)

        steps = list(expand_steps(case))
        if not steps:
            raise UiTestConfigurationError(f"{case_id} has no commands")
        if steps[-1]["input"] != "bye":
            raise UiTestConfigurationError(
                f"{case_id} must record 'bye' explicitly as its final command"
            )
        for step in steps:
            response_lines(step["expect"])


def require_java_25(repo_root):
    """Ensure the active compiler is Java 25 before building."""
    result = subprocess.run(
        ["javac", "-version"],
        cwd=repo_root,
        text=True,
        capture_output=True,
        check=False,
    )
    version_output = (result.stdout + result.stderr).strip()
    if result.returncode != 0 or not version_output.startswith("javac 25"):
        raise UiTestConfigurationError(
            "Java 25 is required; active compiler reported: "
            + (version_output or "unavailable")
        )


def compile_application(repo_root, classes_directory):
    """Compile the non-JavaFX Nudge sources once for the selected test cases."""
    source_root = repo_root / "src/main/java/nudge"
    source_files = sorted(
        source_file
        for source_file in source_root.rglob("*.java")
        if "gui" not in source_file.relative_to(source_root).parts
        and source_file.name != "Launcher.java"
    )
    if not source_files:
        raise UiTestConfigurationError("No Java source files found")

    result = subprocess.run(
        ["javac", "-d", classes_directory, *source_files],
        cwd=repo_root,
        text=True,
        capture_output=True,
        check=False,
    )
    if result.returncode != 0:
        details = (result.stdout + result.stderr).strip()
        raise UiTestConfigurationError("Compilation failed:\n" + details)


def write_failure_artifact(repo_root, case, inputs, expected, actual, stderr):
    """Save complete failure evidence without flooding successful output."""
    artifact = repo_root / FAILURE_ARTIFACT
    artifact.parent.mkdir(parents=True, exist_ok=True)
    artifact.write_text(
        f"Case: {case['id']} — {case['title']}\n\n"
        "Console input:\n"
        f"{inputs}\n"
        "Expected output:\n"
        f"{expected}\n"
        "Actual output:\n"
        f"{actual}\n"
        "Standard error:\n"
        f"{stderr}",
        encoding="utf-8",
    )
    return artifact


def focused_diff(expected, actual):
    """Return a bounded unified diff suitable for terminal reporting."""
    diff_lines = list(
        difflib.unified_diff(
            expected.splitlines(),
            actual.splitlines(),
            fromfile="expected",
            tofile="actual",
            n=3,
            lineterm="",
        )
    )
    maximum_lines = 80
    if len(diff_lines) > maximum_lines:
        return diff_lines[:maximum_lines] + ["... diff truncated; see failure artifact"]
    return diff_lines


def run_case(repo_root, classes_directory, case, timeout_seconds):
    """Run one complete case in a fresh process and compare exact output."""
    expected, ranges, steps = build_expected_output(case)
    inputs = "\n".join(step["input"] for step in steps) + "\n"

    with tempfile.TemporaryDirectory(prefix="nudge-ui-case-") as working_directory:
        storage_path = Path(working_directory) / "data/nudge.txt"
        storage_before = case.get("storage_before")
        if storage_before is not None:
            storage_path.parent.mkdir(parents=True, exist_ok=True)
            storage_path.write_text(storage_before, encoding="utf-8")
        elif case.get("storage_is_directory"):
            storage_path.parent.mkdir(parents=True, exist_ok=True)
            storage_path.mkdir()
        try:
            result = subprocess.run(
                ["java", "-cp", classes_directory, "nudge.Nudge"],
                cwd=working_directory,
                input=inputs,
                text=True,
                capture_output=True,
                timeout=timeout_seconds,
                check=False,
            )
        except subprocess.TimeoutExpired as exception:
            actual = normalize_output(exception.stdout or "")
            stderr = normalize_output(exception.stderr or "")
            artifact = write_failure_artifact(
                repo_root, case, inputs, expected, actual, stderr
            )
            return False, "process timeout", [], artifact, "timeout"

        actual = normalize_output(result.stdout)
        stderr = normalize_output(result.stderr)
        expected_storage = case.get("storage")
        actual_storage = None
        if expected_storage is not None:
            actual_storage = storage_path.read_text(encoding="utf-8") if storage_path.exists() else None
        if expected_storage is not None and actual_storage != expected_storage:
            artifact = write_failure_artifact(
                repo_root, case, inputs, expected_storage, actual_storage or "", stderr
            )
            return False, "task storage", [], artifact, "storage file mismatch"

    if result.returncode == 0 and not stderr and actual == expected:
        return True, None, [], None, None

    mismatch_line = first_mismatch_line(expected, actual)
    command = command_for_line(ranges, mismatch_line) if mismatch_line else "process"
    artifact = write_failure_artifact(repo_root, case, inputs, expected, actual, stderr)
    if result.returncode != 0:
        reason = f"exit status {result.returncode}"
    elif stderr:
        reason = "unexpected standard error"
    else:
        reason = f"output mismatch at line {mismatch_line}"
    return False, command, focused_diff(expected, actual), artifact, reason


def select_cases(cases, requested_ids):
    """Select requested cases while retaining fixture order."""
    if not requested_ids:
        return cases
    unknown_ids = sorted(set(requested_ids) - {case["id"] for case in cases})
    if unknown_ids:
        raise UiTestConfigurationError(
            "Unknown case ID(s): " + ", ".join(unknown_ids)
        )
    requested_id_set = set(requested_ids)
    return [case for case in cases if case["id"] in requested_id_set]


def main():
    """Compile once, run selected cases, and report only useful evidence."""
    arguments = parse_arguments()
    repo_root = Path(__file__).resolve().parent.parent
    fixture_path = repo_root / arguments.fixtures

    try:
        fixture_data = json.loads(fixture_path.read_text(encoding="utf-8"))
        all_cases = fixture_data["cases"]
        validate_cases(all_cases)
        selected_cases = select_cases(all_cases, arguments.case_ids)
        require_java_25(repo_root)
    except (OSError, json.JSONDecodeError, KeyError, UiTestConfigurationError) as error:
        print(f"UI test configuration failed: {error}", file=sys.stderr)
        return 2

    failure_artifact = repo_root / FAILURE_ARTIFACT
    failure_artifact.unlink(missing_ok=True)
    timeout_seconds = fixture_data.get("timeout_seconds", 5)

    with tempfile.TemporaryDirectory(prefix="nudge-ui-test-") as classes_directory:
        try:
            compile_application(repo_root, classes_directory)
        except UiTestConfigurationError as error:
            print(f"UI test preflight failed: {error}", file=sys.stderr)
            return 2

        passed = 0
        for index, case in enumerate(selected_cases):
            success, command, diff_lines, artifact, reason = run_case(
                repo_root, classes_directory, case, timeout_seconds
            )
            if success:
                passed += 1
                continue

            not_run = len(selected_cases) - index - 1
            print(f"FAILED {case['id']} — {case['title']}")
            print(f"Command: {command!r}")
            print(f"Reason: {reason}")
            if diff_lines:
                print("Focused diff:")
                print("\n".join(diff_lines))
            print(f"Full failure evidence: {artifact}")
            print(f"UI tests: {passed} passed, 1 failed, {not_run} not run.")
            return 1

    print(f"UI tests: {passed} passed, 0 failed, 0 not run.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
