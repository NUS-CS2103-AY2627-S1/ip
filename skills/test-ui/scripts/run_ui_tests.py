#!/usr/bin/env python3
"""Run the command-line UI cases declared in test/ui-test-plan.md."""

from pathlib import Path
import re
import shlex
import subprocess
import sys


ROOT = Path(__file__).resolve().parents[3]
PLAN = ROOT / "test/ui-test-plan.md"
DATA_FILE = ROOT / "data/dude.jsonl"
LEGACY_DATA_FILE = ROOT / "data/dude.txt"


def read_block(text: str, heading: str, start: int, end: int) -> str:
    pattern = rf"### {re.escape(heading)}\s*\n```(?:text)?\n(.*?)```"
    match = re.search(pattern, text[start:end], re.DOTALL)
    if match is None:
        raise ValueError(f"Missing {heading!r} block in test case")
    return match.group(1)


def parse_plan(text: str) -> tuple[str, str, list[dict[str, str]]]:
    build = re.search(r"^Build command:\s*`([^`]+)`$", text, re.MULTILINE)
    program = re.search(r"^Program command:\s*`([^`]+)`$", text, re.MULTILINE)
    if build is None or program is None:
        raise ValueError("The plan must define Build command and Program command")

    matches = list(re.finditer(r"^## Test case: (.+)$", text, re.MULTILINE))
    if not matches:
        raise ValueError("The plan must contain at least one test case")

    cases = []
    for index, match in enumerate(matches):
        end = matches[index + 1].start() if index + 1 < len(matches) else len(text)
        section = text[match.start():end]
        aim = re.search(r"^Aim:\s*(.+)$", section, re.MULTILINE)
        if aim is None:
            raise ValueError(f"Test case {match.group(1)!r} is missing an Aim")
        cases.append({
            "name": match.group(1),
            "aim": aim.group(1),
            "input": read_block(text, "Inputs", match.start(), end),
            "expected": read_block(text, "Expected output", match.start(), end),
            "data": read_optional_block(text, "Data file before startup", match.start(), end),
        })
    return build.group(1), program.group(1), cases


def read_optional_block(text: str, heading: str, start: int, end: int) -> str | None:
    pattern = rf"### {re.escape(heading)}\s*\n```(?:text)?\n(.*?)```"
    match = re.search(pattern, text[start:end], re.DOTALL)
    return None if match is None else match.group(1)


def prepare_data_file(contents: str | None) -> None:
    DATA_FILE.unlink(missing_ok=True)
    LEGACY_DATA_FILE.unlink(missing_ok=True)
    if contents is not None:
        DATA_FILE.parent.mkdir(parents=True, exist_ok=True)
        DATA_FILE.write_text(contents)


def run(command: str, stdin: str) -> str:
    result = subprocess.run(
        shlex.split(command),
        input=stdin,
        text=True,
        capture_output=True,
        cwd=ROOT,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError(
            f"Command failed with exit code {result.returncode}: {command}\n"
            f"stdout:\n{result.stdout}\nstderr:\n{result.stderr}"
        )
    return result.stdout


def append_session(text: str, records: list[dict[str, str]]) -> None:
    marker = "\n## Latest test session\n"
    text = text.split(marker, 1)[0].rstrip() + marker
    for record in records:
        recorded_output = "\n".join(
            line.rstrip() for line in record["actual"].splitlines()
        )
        if record["actual"].endswith("\n"):
            recorded_output += "\n"
        text += (
            f"\n### {record['name']}\n"
            "Console input:\n```text\n"
            f"{record['input']}"
            "```\nConsole output:\n```text\n"
            f"{recorded_output}"
            "```\n"
        )
    PLAN.write_text(text)


def comparable_output(output: str) -> str:
    """Ignore ANSI styling and box-padding spaces while preserving output lines."""
    without_ansi = re.sub(r"\x1b\[[0-9;]*m", "", output)
    return "\n".join(line.rstrip() for line in without_ansi.splitlines())


def main() -> int:
    try:
        plan = PLAN.read_text()
        build, program, cases = parse_plan(plan)
        run(build, "")
    except (OSError, ValueError, RuntimeError) as error:
        print(f"UI test setup failed: {error}", file=sys.stderr)
        return 2

    records = []
    for case in cases:
        try:
            prepare_data_file(case["data"])
            actual = run(program, case["input"])
        except OSError as error:
            print(f"UI test data setup failed: {error}", file=sys.stderr)
            return 2
        record = {**case, "actual": actual}
        records.append(record)
        if comparable_output(actual) != comparable_output(case["expected"]):
            append_session(plan, records)
            print(f"FAILED: {case['name']}")
            print("Actual output:")
            print(actual, end="")
            print("Expected output:")
            print(case["expected"], end="")
            return 1

    append_session(plan, records)
    print(f"Passed {len(cases)} UI test case(s).")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
