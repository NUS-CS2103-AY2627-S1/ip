#!/usr/bin/env python3
"""Run fail-fast console UI tests documented in a Markdown test plan."""

from __future__ import annotations

import argparse
import json
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


CASE_HEADING = "## Test Case:"
REQUIRED_SECTIONS = ("Aim", "Command", "Inputs", "Expected output")


@dataclass(frozen=True)
class TestCase:
    """A console UI test case parsed from the Markdown test plan."""

    name: str
    aim: str
    command: list[str]
    inputs: str
    expected_output: str


class PlanError(ValueError):
    """Indicate that a UI test plan does not follow the required format."""


class Transcript:
    """Write identical test-session records to the console and a transcript file."""

    def __init__(self) -> None:
        self._parts: list[str] = []

    def write(self, text: str = "") -> None:
        """Append one line to the session record and display it immediately."""
        line = text + "\n"
        self._parts.append(line)
        print(text, flush=True)

    def write_block(self, label: str, content: str) -> None:
        """Append a labelled verbatim block to the session record."""
        self.write(f"--- {label} ---")
        if content:
            for line in content.splitlines():
                self.write(line)
            if content.endswith("\n") and not content.splitlines():
                self.write()
        self.write(f"--- end {label} ---")

    def save(self, path: Path) -> None:
        """Persist the complete session record."""
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text("".join(self._parts), encoding="utf-8")


def _extract_fenced_block(section: str, case_name: str, field_name: str) -> str:
    """Return the exact content inside a section's first fenced code block."""
    lines = section.splitlines(keepends=True)
    opening = next((index for index, line in enumerate(lines) if line.lstrip().startswith("```")), None)
    if opening is None:
        raise PlanError(f"{case_name}: {field_name} must contain a fenced code block")

    closing = next(
        (index for index in range(opening + 1, len(lines)) if lines[index].lstrip().startswith("```")),
        None,
    )
    if closing is None:
        raise PlanError(f"{case_name}: {field_name} has an unclosed code block")
    return "".join(lines[opening + 1 : closing])


def _parse_sections(case_text: str, case_name: str) -> dict[str, str]:
    """Split one test case into its required level-three Markdown sections."""
    sections: dict[str, list[str]] = {}
    current: str | None = None
    in_fence = False
    for line in case_text.splitlines(keepends=True):
        if line.lstrip().startswith("```"):
            in_fence = not in_fence
        if not in_fence and line.startswith("### "):
            current = line[4:].strip()
            sections[current] = []
        elif current is not None:
            sections[current].append(line)

    missing = [name for name in REQUIRED_SECTIONS if name not in sections]
    if missing:
        raise PlanError(f"{case_name}: missing section(s): {', '.join(missing)}")
    return {name: "".join(lines) for name, lines in sections.items()}


def _split_case_chunks(text: str) -> list[tuple[str, str]]:
    """Find case headings while ignoring heading-like text inside code fences."""
    cases: list[tuple[str, list[str]]] = []
    in_fence = False
    for line in text.splitlines(keepends=True):
        if line.lstrip().startswith("```"):
            in_fence = not in_fence
        if not in_fence and line.startswith(CASE_HEADING):
            name = line[len(CASE_HEADING) :].strip()
            cases.append((name, []))
        elif cases:
            cases[-1][1].append(line)
    return [(name, "".join(lines)) for name, lines in cases]


def parse_plan(path: Path) -> list[TestCase]:
    """Parse all test cases from a Markdown UI test plan."""
    try:
        text = path.read_text(encoding="utf-8")
    except OSError as error:
        raise PlanError(f"cannot read plan {path}: {error}") from error

    cases: list[TestCase] = []
    for name, remainder in _split_case_chunks(text):
        if not name:
            raise PlanError("every test case needs a title after '## Test Case:'")
        sections = _parse_sections(remainder, name)

        aim = sections["Aim"].strip()
        if not aim:
            raise PlanError(f"{name}: Aim must not be empty")

        command_text = _extract_fenced_block(sections["Command"], name, "Command").strip()
        try:
            command = json.loads(command_text)
        except json.JSONDecodeError as error:
            raise PlanError(f"{name}: Command is not valid JSON: {error}") from error
        if not isinstance(command, list) or not command or not all(isinstance(item, str) for item in command):
            raise PlanError(f"{name}: Command must be a non-empty JSON array of strings")

        inputs = _extract_fenced_block(sections["Inputs"], name, "Inputs")
        expected = _extract_fenced_block(sections["Expected output"], name, "Expected output")
        cases.append(TestCase(name, aim, command, inputs, expected))

    if not cases:
        raise PlanError(f"no test cases found in {path}")
    return cases


def normalize_newlines(text: str) -> str:
    """Normalize platform line endings without changing other whitespace."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def run_case(test_case: TestCase, timeout: float) -> tuple[str, int, bool]:
    """Run one case and return output, exit code, and timeout status."""
    try:
        completed = subprocess.run(
            test_case.command,
            input=test_case.inputs,
            stdout=subprocess.PIPE,
            stderr=subprocess.STDOUT,
            text=True,
            encoding="utf-8",
            errors="replace",
            timeout=timeout,
            check=False,
        )
        return normalize_newlines(completed.stdout), completed.returncode, False
    except subprocess.TimeoutExpired as error:
        partial_output = error.stdout or ""
        if isinstance(partial_output, bytes):
            partial_output = partial_output.decode("utf-8", errors="replace")
        return normalize_newlines(partial_output), -1, True
    except OSError as error:
        return f"Unable to start command: {error}\n", -1, False


def run_tests(cases: list[TestCase], timeout: float, transcript_path: Path) -> int:
    """Run cases in order, save the transcript, and stop at the first failure."""
    transcript = Transcript()
    transcript.write("=== UI TEST SESSION ===")
    transcript.write(f"Cases scheduled: {len(cases)}")

    for index, test_case in enumerate(cases, start=1):
        transcript.write()
        transcript.write(f"=== CASE {index}: {test_case.name} ===")
        transcript.write(f"Aim: {test_case.aim}")
        transcript.write(f"Command: {json.dumps(test_case.command, ensure_ascii=False)}")
        transcript.write_block("console input", test_case.inputs)

        actual, exit_code, timed_out = run_case(test_case, timeout)
        transcript.write_block("actual console output", actual)

        expected = normalize_newlines(test_case.expected_output)
        failure_reason: str | None = None
        if timed_out:
            failure_reason = f"timed out after {timeout:g} seconds"
        elif exit_code != 0:
            failure_reason = f"process exited with status {exit_code}"
        elif actual != expected:
            failure_reason = "actual output did not match expected output"

        if failure_reason is not None:
            transcript.write(f"RESULT: FAIL — {failure_reason}")
            transcript.write_block("expected console output", expected)
            remaining = len(cases) - index
            transcript.write(f"SESSION TERMINATED: {remaining} later case(s) not run")
            transcript.save(transcript_path)
            return 1

        transcript.write("RESULT: PASS")

    transcript.write()
    transcript.write(f"SESSION PASSED: {len(cases)} case(s)")
    transcript.save(transcript_path)
    return 0


def main() -> int:
    """Parse command-line arguments and execute the documented UI tests."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"))
    parser.add_argument("--transcript", type=Path, default=Path("_temp/ui-test-session.txt"))
    parser.add_argument("--timeout", type=float, default=15.0, help="seconds allowed per test case")
    args = parser.parse_args()

    if args.timeout <= 0:
        parser.error("--timeout must be greater than zero")

    try:
        cases = parse_plan(args.plan)
    except PlanError as error:
        print(f"PLAN ERROR: {error}", file=sys.stderr)
        return 2
    return run_tests(cases, args.timeout, args.transcript)


if __name__ == "__main__":
    raise SystemExit(main())
