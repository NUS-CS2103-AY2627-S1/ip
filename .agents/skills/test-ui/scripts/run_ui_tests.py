#!/usr/bin/env python3
"""Compile Lumi and run the console sessions in test/ui-test-plan.md."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
import uuid
from dataclasses import dataclass
from pathlib import Path


DIVIDER = "____________________________________________________________"


@dataclass(frozen=True)
class UiTestCase:
    """A named console session and its expected command responses."""

    name: str
    aim: str
    initial_data: str | None
    commands: list[str]
    expected_responses: list[str]
    expected_data: str | None


def normalize_output(text: str) -> str:
    """Normalize line endings and trailing whitespace without changing indentation."""
    lines = text.replace("\r\n", "\n").replace("\r", "\n").split("\n")
    while lines and lines[0] == "":
        lines.pop(0)
    while lines and lines[-1] == "":
        lines.pop()
    return "\n".join(line.rstrip() for line in lines)


def fenced_block(section: str, heading: str, case_name: str) -> str:
    """Return a text-fenced block beneath a level-three heading."""
    pattern = rf"^### {re.escape(heading)}\s*\n+```text\s*\n(.*?)\n```"
    match = re.search(pattern, section, flags=re.MULTILINE | re.DOTALL)
    if not match:
        raise ValueError(f"{case_name}: missing '{heading}' text block")
    return match.group(1)


def optional_fenced_block(section: str, heading: str) -> str | None:
    """Return an optional text-fenced block beneath a level-three heading."""
    pattern = rf"^### {re.escape(heading)}\s*\n+```text\s*\n(.*?)\n```"
    match = re.search(pattern, section, flags=re.MULTILINE | re.DOTALL)
    return match.group(1) if match else None


def load_test_cases(plan_path: Path) -> list[UiTestCase]:
    """Parse UI test cases from the project Markdown plan."""
    plan_text = plan_path.read_text(encoding="utf-8").replace("\r\n", "\n")
    headings = list(re.finditer(r"^##\s+(.+?)\s*$", plan_text, flags=re.MULTILINE))
    if not headings:
        raise ValueError("The UI test plan contains no test cases")

    cases: list[UiTestCase] = []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(plan_text)
        section = plan_text[heading.end():end]
        name = heading.group(1)

        aim_match = re.search(r"^Aim:\s*(.+?)\s*$", section, flags=re.MULTILINE)
        if not aim_match:
            raise ValueError(f"{name}: missing Aim")

        input_lines = fenced_block(section, "Inputs", name).splitlines()
        commands = ["" if line == "<EMPTY>" else line for line in input_lines]
        expected_text = fenced_block(section, "Expected outputs", name)
        expected_responses = [normalize_output(block) for block in expected_text.split("\n---\n")]

        if not commands or any(line == "" for line in input_lines):
            raise ValueError(
                f"{name}: use <EMPTY> to represent an empty input command"
            )
        if len(commands) != len(expected_responses):
            raise ValueError(
                f"{name}: found {len(commands)} commands but "
                f"{len(expected_responses)} expected response blocks"
            )

        cases.append(UiTestCase(
            name,
            aim_match.group(1),
            optional_fenced_block(section, "Initial data"),
            commands,
            expected_responses,
            optional_fenced_block(section, "Expected data"),
        ))

    return cases


def find_project_root() -> Path:
    """Locate the repository root from this script's project-local location."""
    script_path = Path(__file__).resolve()
    for candidate in script_path.parents:
        if (candidate / "src" / "main" / "java").is_dir() and (candidate / "AGENTS.md").is_file():
            return candidate
    raise RuntimeError("Could not locate the project root")


def check_java_25() -> None:
    """Fail unless both the Java compiler and runtime are version 25."""
    checks = (("javac", ["javac", "-version"], r"\bjavac 25(?:\.|\s|$)"),
              ("java", ["java", "-version"], r'\bversion "25(?:\.|\")'))
    for name, command, version_pattern in checks:
        try:
            result = subprocess.run(command, capture_output=True, text=True, check=False)
        except FileNotFoundError as error:
            raise RuntimeError(f"{name} was not found on PATH") from error
        version_text = normalize_output(result.stdout + "\n" + result.stderr)
        if result.returncode != 0 or not re.search(version_pattern, version_text):
            raise RuntimeError(f"Java 25 is required, but {name} reported:\n{version_text}")


def compile_application(project_root: Path, output_dir: Path) -> None:
    """Compile the console application's Java sources into a temporary directory."""
    source_dir = project_root / "src" / "main" / "java"
    javafx_sources = {
        source_dir / "lumi" / "Launcher.java",
        source_dir / "lumi" / "Main.java",
        source_dir / "lumi" / "ui" / "DialogBox.java",
        source_dir / "lumi" / "ui" / "MainWindow.java",
    }
    sources = sorted(source for source in source_dir.rglob("*.java")
                     if source not in javafx_sources)
    if not sources:
        raise RuntimeError(f"No Java sources found in {source_dir}")

    result = subprocess.run(
        ["javac", "-d", str(output_dir), *(str(source) for source in sources)],
        cwd=project_root,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        compiler_output = normalize_output(result.stdout + "\n" + result.stderr)
        raise RuntimeError(f"Compilation failed:\n{compiler_output}")


def extract_console_sections(output: str, command_count: int) -> tuple[str, list[str]]:
    """Extract startup text and per-command responses from divider-delimited output."""
    normalized = output.replace("\r\n", "\n").replace("\r", "\n")
    lines = normalized.split("\n")
    divider_count = sum(line.rstrip() == DIVIDER for line in lines)
    expected_divider_count = 2 + 2 * command_count
    if divider_count != expected_divider_count:
        raise ValueError(
            f"expected {expected_divider_count} divider lines but found {divider_count}"
        )

    sections: list[str] = []
    current_lines: list[str] = []
    for line in lines:
        if line.rstrip() == DIVIDER:
            section = normalize_output("\n".join(current_lines))
            if section:
                sections.append(section)
            current_lines = []
        else:
            current_lines.append(line)

    final_section = normalize_output("\n".join(current_lines))
    if final_section:
        sections.append(final_section)

    if len(sections) != command_count + 1:
        raise ValueError(
            f"expected startup plus {command_count} response blocks but found {len(sections)} sections"
        )
    return sections[0], sections[1:]


def print_transcript(
        case: UiTestCase,
        startup: str,
        responses: list[str],
        response_limit: int | None = None,
) -> None:
    """Print a readable record of console input and actual output."""
    limit = len(case.commands) if response_limit is None else response_limit
    print(f"\n=== {case.name} ===")
    print(f"Aim: {case.aim}")
    print("Console session:")
    print(DIVIDER)
    print(startup)
    print(DIVIDER)
    for command, response in zip(case.commands[:limit], responses[:limit]):
        displayed_command = command if command else "<EMPTY>"
        print(f"> {displayed_command}")
        print(DIVIDER)
        print(response)
        print(DIVIDER)


def run_case(case: UiTestCase, working_dir: Path, class_dir: Path) -> bool:
    """Run one fresh Lumi process and stop at its first output mismatch."""
    data_file = working_dir / "data" / "lumi.txt"
    if case.initial_data is not None:
        data_file.parent.mkdir(parents=True)
        data_file.write_text(case.initial_data + "\n", encoding="utf-8")

    result = subprocess.run(
        ["java", "-ea", "-cp", str(class_dir), "lumi.Lumi"],
        cwd=working_dir,
        input="\n".join(case.commands) + "\n",
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        print(f"\nFAIL: {case.name} exited with status {result.returncode}")
        print("Actual standard output:")
        print(normalize_output(result.stdout) or "<empty>")
        print("Actual standard error:")
        print(normalize_output(result.stderr) or "<empty>")
        return False

    try:
        startup, actual_responses = extract_console_sections(result.stdout, len(case.commands))
    except ValueError as error:
        print(f"\nFAIL: {case.name}: {error}")
        print("Actual console output:")
        print(normalize_output(result.stdout) or "<empty>")
        return False

    for index, (actual, expected) in enumerate(
            zip(actual_responses, case.expected_responses), start=1):
        if normalize_output(actual) != normalize_output(expected):
            print_transcript(case, startup, actual_responses, response_limit=index)
            failed_command = case.commands[index - 1] or "<EMPTY>"
            print(f"FAIL: {case.name}, command {index}: {failed_command}")
            print("Expected output:")
            print(expected or "<empty>")
            print("Actual output:")
            print(actual or "<empty>")
            return False

    if case.expected_data is not None:
        if not data_file.is_file():
            print_transcript(case, startup, actual_responses)
            print(f"FAIL: {case.name}: expected {data_file} to be created")
            return False

        actual_data = normalize_output(data_file.read_text(encoding="utf-8"))
        expected_data = normalize_output(case.expected_data)
        if actual_data != expected_data:
            print_transcript(case, startup, actual_responses)
            print(f"FAIL: {case.name}: saved data did not match")
            print("Expected data:")
            print(expected_data or "<empty>")
            print("Actual data:")
            print(actual_data or "<empty>")
            return False

    print_transcript(case, startup, actual_responses)
    if case.expected_data is not None:
        print("Saved data:")
        print(normalize_output(data_file.read_text(encoding="utf-8")) or "<empty>")
    print(f"PASS: {case.name}")
    return True


def main() -> int:
    """Run all planned UI test cases, terminating at the first failure."""
    try:
        project_root = find_project_root()
        plan_path = project_root / "test" / "ui-test-plan.md"
        test_cases = load_test_cases(plan_path)
        check_java_25()

        class_dir = project_root / "build" / f"lumi-ui-tests-{uuid.uuid4().hex}"
        test_root = project_root / "build" / f"lumi-ui-data-{uuid.uuid4().hex}"
        class_dir.mkdir(parents=True)
        test_root.mkdir(parents=True)
        try:
            compile_application(project_root, class_dir)
            for index, test_case in enumerate(test_cases, start=1):
                case_directory = test_root / f"case-{index}"
                case_directory.mkdir()
                if not run_case(test_case, case_directory, class_dir):
                    return 1
        finally:
            shutil.rmtree(class_dir)
            shutil.rmtree(test_root)
    except (OSError, RuntimeError, ValueError) as error:
        print(f"UI test setup failed: {error}", file=sys.stderr)
        return 1

    print(f"\nAll {len(test_cases)} UI test cases passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
