#!/usr/bin/env python3
"""Run the command-line UI test cases recorded in a Markdown test plan."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    """One independent UI test session."""

    name: str
    aim: str
    inputs: str
    expected: str
    setup: str = ""


def _fenced_block(section: str, section_name: str) -> str:
    """Extract a fenced text block from a test-case section."""
    heading = re.search(
        rf"^###\s+{re.escape(section_name)}\s*$([\s\S]*?)(?=^###\s+|\Z)",
        section,
        re.MULTILINE,
    )
    if not heading:
        raise ValueError(f"missing '{section_name}' section")
    block = re.search(r"```(?:text)?\s*\n([\s\S]*?)\n```", heading.group(1))
    if not block:
        raise ValueError(f"'{section_name}' must contain a fenced text block")
    return block.group(1)


def parse_plan(plan_path: Path) -> tuple[dict[str, str], list[TestCase]]:
    """Parse runner settings and test cases from the Markdown plan."""
    content = plan_path.read_text(encoding="utf-8")
    metadata: dict[str, str] = {}
    metadata_pattern = (
        r"^-\s*(Program command|Build command|Working directory|Timeout seconds|Reset files):\s*(.+)$"
    )
    for key, value in re.findall(metadata_pattern, content, re.MULTILINE):
        metadata[key] = value.strip().strip("`")

    matches = list(re.finditer(r"^##\s+Test case:\s*(.+?)\s*$", content, re.MULTILINE))
    if not matches:
        raise ValueError("the plan must contain at least one '## Test case:' section")

    cases: list[TestCase] = []
    for index, match in enumerate(matches):
        section_end = matches[index + 1].start() if index + 1 < len(matches) else len(content)
        section = content[match.end():section_end]
        aim_match = re.search(
            r"^###\s+Aim\s*$([\s\S]*?)(?=^###\s+|\Z)", section, re.MULTILINE
        )
        if not aim_match:
            raise ValueError(f"test case '{match.group(1)}' is missing an Aim section")
        setup_match = re.search(
            r"^###\s+Setup\s*$([\s\S]*?)(?=^###\s+|\Z)", section, re.MULTILINE
        )
        setup = ""
        if setup_match:
            setup_block = re.search(r"```[^\n]*\s*\n([\s\S]*?)\n```", setup_match.group(1))
            if not setup_block:
                raise ValueError(f"test case '{match.group(1)}' has an invalid Setup block")
            setup = setup_block.group(1).strip()

        cases.append(TestCase(
            name=match.group(1).strip(),
            aim=aim_match.group(1).strip(),
            inputs=_fenced_block(section, "Inputs"),
            expected=_fenced_block(section, "Expected output"),
            setup=setup,
        ))

    if "Program command" not in metadata:
        raise ValueError("the plan must specify '- Program command:'")
    return metadata, cases


def normalize_output(value: str) -> str:
    """Normalize platform line endings without hiding substantive differences."""
    return value.replace("\r\n", "\n").replace("\r", "\n").rstrip("\n")


def print_transcript(case: TestCase, output: str) -> None:
    """Print the input and output captured for one test session."""
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("--- Console input ---")
    print(case.inputs if case.inputs else "(no input)")
    print("--- Console output ---")
    print(output if output else "(no output)")


def run(plan_path: Path) -> int:
    """Run all plan cases, stopping immediately at the first failure."""
    try:
        metadata, cases = parse_plan(plan_path)
    except (OSError, ValueError) as error:
        print(f"Test plan error: {error}", file=sys.stderr)
        return 2

    repo_root = plan_path.resolve().parent.parent
    working_directory = Path(metadata.get("Working directory", "."))
    if not working_directory.is_absolute():
        working_directory = (repo_root / working_directory).resolve()
    timeout = float(metadata.get("Timeout seconds", "10"))
    reset_files = [
        Path(item.strip())
        for item in metadata.get("Reset files", "").split(",")
        if item.strip()
    ]

    build_command = metadata.get("Build command", "").strip()
    if build_command and build_command.lower() != "none":
        print(f"Running build command: {build_command}")
        build = subprocess.run(
            build_command,
            cwd=working_directory,
            shell=True,
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            timeout=timeout,
        )
        if build.returncode != 0:
            print("Build failed; no UI test cases were run.", file=sys.stderr)
            print(build.stdout, end="")
            print(build.stderr, end="", file=sys.stderr)
            return build.returncode or 1

    program_command = metadata["Program command"]
    for case in cases:
        for reset_file in reset_files:
            reset_path = reset_file if reset_file.is_absolute() else repo_root / reset_file
            if reset_path.is_file():
                reset_path.unlink()

        if case.setup:
            setup = subprocess.run(
                case.setup,
                cwd=working_directory,
                shell=True,
                capture_output=True,
                text=True,
                encoding="utf-8",
                errors="replace",
                timeout=timeout,
            )
            if setup.returncode != 0:
                print(f"Setup failed for '{case.name}'; no UI test was run.", file=sys.stderr)
                print(setup.stdout, end="")
                print(setup.stderr, end="", file=sys.stderr)
                return setup.returncode or 1

        try:
            process = subprocess.run(
                program_command,
                cwd=working_directory,
                shell=True,
                input=case.inputs,
                capture_output=True,
                text=True,
                encoding="utf-8",
                errors="replace",
                timeout=timeout,
            )
            actual = process.stdout
            if process.stderr:
                actual += f"\n[stderr]\n{process.stderr}"
        except subprocess.TimeoutExpired as error:
            actual = (error.stdout or "") + "\n[process timed out]"
            print_transcript(case, actual)
            print("Result: FAIL")
            print("--- Expected output ---")
            print(case.expected)
            print("The test session timed out; stopping immediately.", file=sys.stderr)
            return 1

        print_transcript(case, actual)
        if process.returncode != 0 or normalize_output(actual) != normalize_output(case.expected):
            print("Result: FAIL")
            print("--- Expected output ---")
            print(case.expected)
            print("--- Actual output ---")
            print(actual)
            print("Stopping after the first failed test case.", file=sys.stderr)
            return 1
        print("Result: PASS")

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


def main() -> int:
    """Parse command-line options and run the plan."""
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
    if hasattr(sys.stderr, "reconfigure"):
        sys.stderr.reconfigure(encoding="utf-8", errors="replace")
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"),
                        help="Markdown test plan (default: test/ui-test-plan.md)")
    return run(parser.parse_args().plan)


if __name__ == "__main__":
    raise SystemExit(main())
