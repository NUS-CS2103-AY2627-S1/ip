#!/usr/bin/env python3
"""Run console UI tests described in a small Markdown test plan."""

from __future__ import annotations

import argparse
import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One executable UI test case from the Markdown plan."""

    title: str
    aim: str
    command: str
    inputs: str
    expected: str
    saved_data: str = ""
    restart_inputs: str = ""
    restart_expected: str = ""


FIELD_PATTERN = re.compile(
    r"^\*\*(Aim|Command|Inputs|Expected output|Saved data|Restart inputs|Expected restart output):\*\*\s*(.*)$",
    re.IGNORECASE,
)
HEADING_PATTERN = re.compile(r"^##\s+Test case(?:\s+\d+)?\s*:\s*(.+)$", re.IGNORECASE)


def _strip_code_ticks(value: str) -> str:
    """Remove optional inline Markdown code ticks from a field value."""
    value = value.strip()
    if len(value) >= 2 and value.startswith("`") and value.endswith("`"):
        return value[1:-1]
    return value


def _read_code_block(lines: list[str], start: int) -> tuple[str, int]:
    """Read a fenced code block after a field and return its content and next index."""
    index = start
    while index < len(lines) and not lines[index].strip():
        index += 1
    if index >= len(lines) or not lines[index].strip().startswith("```"):
        raise ValueError("expected a fenced code block")

    index += 1
    content: list[str] = []
    while index < len(lines) and not lines[index].strip().startswith("```"):
        content.append(lines[index])
        index += 1
    if index == len(lines):
        raise ValueError("unterminated fenced code block")
    return "\n".join(content) + ("\n" if content else ""), index + 1


def parse_plan(path: Path) -> list[TestCase]:
    """Parse the supported Markdown test-plan format."""
    lines = path.read_text(encoding="utf-8").splitlines()
    cases: list[TestCase] = []
    current: dict[str, str] | None = None
    index = 0

    def finish_case() -> None:
        if current is None:
            return
        missing = [key for key in ("aim", "command", "inputs", "expected") if key not in current]
        if missing:
            raise ValueError(
                f"test case '{current.get('title', '<unnamed>')}' is missing: {', '.join(missing)}"
            )
        has_restart_inputs = "restart_inputs" in current
        has_restart_expected = "restart_expected" in current
        if has_restart_inputs != has_restart_expected:
            raise ValueError(
                f"test case '{current.get('title', '<unnamed>')}' must provide both restart inputs and restart expected output"
            )
        cases.append(TestCase(**current))

    while index < len(lines):
        heading = HEADING_PATTERN.match(lines[index])
        if heading:
            finish_case()
            current = {"title": heading.group(1).strip()}
            index += 1
            continue

        field = FIELD_PATTERN.match(lines[index])
        if field:
            if current is None:
                raise ValueError(f"field appears before a test-case heading on line {index + 1}")
            key = field.group(1).lower().replace(" ", "_")
            value = field.group(2).strip()
            if key in ("inputs", "expected_output", "saved_data", "restart_inputs", "expected_restart_output"):
                if value:
                    raise ValueError(f"{field.group(1)} must be followed by a fenced code block")
                value, index = _read_code_block(lines, index + 1)
                if key == "expected_output":
                    current["expected"] = value
                elif key == "inputs":
                    current["inputs"] = value
                elif key == "saved_data":
                    current["saved_data"] = value
                elif key == "restart_inputs":
                    current["restart_inputs"] = value
                else:
                    current["restart_expected"] = value
                continue
            if not value:
                index += 1
                while index < len(lines) and not lines[index].strip():
                    index += 1
                if index >= len(lines) or FIELD_PATTERN.match(lines[index]) or HEADING_PATTERN.match(lines[index]):
                    raise ValueError(f"{field.group(1)} is empty on line {index + 1}")
                value = lines[index].strip()
            current["aim" if key == "aim" else "command"] = _strip_code_ticks(value)
        index += 1

    finish_case()
    if not cases:
        raise ValueError("the test plan contains no test cases")
    return cases


def _normalize(value: str) -> str:
    """Normalize only platform line endings; all other output remains significant."""
    return value.replace("\r\n", "\n").replace("\r", "\n")


def _display(value: str) -> str:
    """Make an empty transcript visible while preserving all other characters."""
    return value if value else "<empty>\n"


def run_process(command: str, inputs: str, timeout: float, working_dir: Path) -> tuple[str, int | None, bool]:
    """Run one process and return captured output, exit code, and whether it timed out."""
    try:
        process = subprocess.run(
            command,
            input=inputs,
            text=True,
            encoding="utf-8",
            capture_output=True,
            cwd=working_dir,
            shell=True,
            timeout=timeout,
            check=False,
        )
        return process.stdout + process.stderr, process.returncode, False
    except subprocess.TimeoutExpired as error:
        output = error.stdout or ""
        if isinstance(output, bytes):
            output = output.decode(errors="replace")
        return output + f"\n<timed out after {timeout:g} seconds>\n", None, True


def run_case(
    case: TestCase, timeout: float, working_dir: Path
) -> list[tuple[str, int | None, bool]]:
    """Run one case, optionally restarting Cookie in the same working directory."""
    results = [run_process(case.command, case.inputs, timeout, working_dir)]
    if case.restart_inputs:
        results.append(run_process(case.command, case.restart_inputs, timeout, working_dir))
    return results


def main() -> int:
    """Run the plan, stop at the first failure, and print the session transcript."""
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("plan", type=Path, help="Markdown UI test plan")
    parser.add_argument("--timeout", type=float, default=30.0, help="per-case timeout in seconds")
    parser.add_argument("--transcript", type=Path, help="optional file for the printed session transcript")
    args = parser.parse_args()

    try:
        cases = parse_plan(args.plan)
    except (OSError, ValueError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 2

    transcript: list[str] = []

    def record(line: str = "") -> None:
        transcript.append(line)

    record(f"UI test session: {args.plan}")
    for number, case in enumerate(cases, start=1):
        with tempfile.TemporaryDirectory(prefix="cookie-ui-test-") as temporary_directory:
            working_dir = Path(temporary_directory)
            shutil.copytree(Path("out"), working_dir / "out")
            if case.saved_data:
                data_file = working_dir / "data" / "cookie.txt"
                data_file.parent.mkdir(parents=True, exist_ok=True)
                data_file.write_text(case.saved_data, encoding="utf-8")
            results = run_case(case, args.timeout, working_dir)
        expected_outputs = [case.expected]
        if case.restart_inputs:
            expected_outputs.append(case.restart_expected)
        passed = all(
            not timed_out and exit_code == 0 and _normalize(actual) == _normalize(expected)
            for (actual, exit_code, timed_out), expected in zip(results, expected_outputs)
        )

        record()
        record(f"=== Test case {number}: {case.title} ===")
        record(f"Aim: {case.aim}")
        record(f"Command: {case.command}")
        for phase, ((actual, exit_code, timed_out), inputs) in enumerate(
            zip(results, [case.inputs, case.restart_inputs]), start=1
        ):
            if len(results) > 1:
                record(f"Process {phase}:")
            record("Console input:")
            record(_display(inputs).rstrip("\n"))
            record("Console output:")
            record(_display(actual).rstrip("\n"))
            record(f"Exit code: {exit_code if exit_code is not None else '<timeout>'}")

        if passed:
            record("Result: PASS")
            continue

        record("Result: FAIL")
        record("Expected output:")
        record(_display(case.expected).rstrip("\n"))
        failed_result = next(
            (
                (actual, exit_code, timed_out),
                expected,
            )
            for (actual, exit_code, timed_out), expected in zip(results, expected_outputs)
            if timed_out or exit_code != 0 or _normalize(actual) != _normalize(expected)
        )
        actual, exit_code, timed_out = failed_result[0]
        if timed_out:
            record(f"Failure: process exceeded the {args.timeout:g}-second timeout.")
        elif exit_code != 0:
            record(f"Failure: process exited with code {exit_code}.")
        else:
            record("Expected output:")
            record(_display(failed_result[1]).rstrip("\n"))
        break
    else:
        record()
        record(f"Passed {len(cases)} test case(s).")

    output = "\n".join(transcript) + "\n"
    print(output, end="")
    if args.transcript:
        try:
            args.transcript.parent.mkdir(parents=True, exist_ok=True)
            args.transcript.write_text(output, encoding="utf-8")
        except OSError as error:
            print(f"ERROR: could not write transcript: {error}", file=sys.stderr)
            return 2
    return 0 if transcript[-1].startswith("Passed ") else 1


if __name__ == "__main__":
    raise SystemExit(main())
