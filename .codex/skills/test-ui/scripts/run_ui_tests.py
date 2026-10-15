"""Run the console UI test cases declared in test/ui-test-plan.md."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


REPOSITORY_ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = REPOSITORY_ROOT / "test" / "ui-test-plan.md"
SOURCE_ROOT = REPOSITORY_ROOT / "src" / "main" / "java"
RESOURCE_ROOT = REPOSITORY_ROOT / "src" / "main" / "resources"
MAIN_CLASS = "chudgpt.ChudGpt"


@dataclass(frozen=True)
class TestCase:
    name: str
    aim: str
    inputs: str
    expected: str
    initial_save: str | None = None


def normalize(value: str) -> str:
    """Normalize platform line endings without changing any other output."""
    return value.replace("\r\n", "\n").replace("\r", "\n")


def read_block(text: str, label: str, start: int) -> tuple[str, int]:
    """Read a labeled Markdown text block and return its content and end offset."""
    match = re.search(
        rf"^{re.escape(label)}:\s*\n\s*```(?:text)?\s*\n(.*?)^```\s*$",
        text[start:],
        re.MULTILINE | re.DOTALL,
    )
    if not match:
        raise ValueError(f"Missing {label} fenced block")
    return normalize(match.group(1)), start + match.end()


def parse_plan(text: str) -> list[TestCase]:
    """Parse the deliberately small, human-editable test-plan format."""
    headings = list(re.finditer(r"^## Test Case \d+: (.+)$", text, re.MULTILINE))
    if not headings:
        raise ValueError("No test cases found in test/ui-test-plan.md")

    cases: list[TestCase] = []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(text)
        section = text[heading.end() : end]
        aim_match = re.search(r"^Aim:\s*(.+)$", section, re.MULTILINE)
        if not aim_match:
            raise ValueError(f"Test case '{heading.group(1)}' is missing Aim")
        inputs, inputs_end = read_block(section, "Inputs", 0)
        expected, _ = read_block(section, "Expected output", inputs_end)
        initial_save_match = re.search(
            r"^Initial save file:\s*\n\s*```(?:text)?\s*\n(.*?)^```\s*$",
            section,
            re.MULTILINE | re.DOTALL,
        )
        initial_save = normalize(initial_save_match.group(1)) if initial_save_match else None
        cases.append(TestCase(heading.group(1), aim_match.group(1), inputs, expected, initial_save))
    return cases


def check_java_25() -> None:
    """Require both Java tools to report major version 25."""
    java = shutil.which("java")
    javac = shutil.which("javac")
    if not java or not javac:
        raise RuntimeError("Both java and javac must be available on PATH")
    versions = []
    for executable in (java, javac):
        result = subprocess.run([executable, "-version"], capture_output=True, text=True, check=False)
        version_text = result.stderr + result.stdout
        version_match = re.search(r'(?:version )?"?(\d+)', version_text)
        if not version_match or version_match.group(1) != "25":
            raise RuntimeError(f"Java 25 is required; detected {executable}: {version_text.strip()}")
        versions.append(version_text.strip())


def compile_application(classes: Path) -> None:
    """Compile the application and copy classpath resources into a temporary tree."""
    # The root package also contains JavaFX controllers; compile only the console app and its packages.
    sources = [SOURCE_ROOT / "chudgpt" / "ChudGpt.java"]
    for package in ("command", "exception", "parser", "storage", "task", "ui"):
        sources.extend(sorted((SOURCE_ROOT / "chudgpt" / package).rglob("*.java")))
    subprocess.run(
        ["javac", "--release", "25", "-encoding", "UTF-8", "-d", str(classes),
         *(str(source) for source in sources)],
        cwd=REPOSITORY_ROOT,
        check=True,
    )
    if RESOURCE_ROOT.exists():
        for resource in RESOURCE_ROOT.rglob("*"):
            if resource.is_file():
                destination = classes / resource.relative_to(RESOURCE_ROOT)
                destination.parent.mkdir(parents=True, exist_ok=True)
                shutil.copy2(resource, destination)


def run_case(case: TestCase, classes: Path, working_directory: Path) -> tuple[str, str, int]:
    """Run one isolated test case and return normalized actual stdout and input."""
    input_text = case.inputs
    result = subprocess.run(
        ["java", "-ea", "-cp", str(classes), MAIN_CLASS],
        cwd=working_directory,
        input=input_text,
        capture_output=True,
        text=True,
        encoding="utf-8",
        timeout=30,
        check=False,
    )
    if result.stderr:
        print(f"[stderr from {case.name}]\n{result.stderr}", file=sys.stderr)
    return input_text, normalize(result.stdout), result.returncode


def print_transcript(case: TestCase, input_text: str, actual: str) -> None:
    """Print the captured console input and output for user-visible evidence."""
    print(f"INPUT ({case.name}):")
    print(input_text, end="" if input_text.endswith("\n") else "\n")
    print(f"OUTPUT ({case.name}):")
    print(actual, end="" if actual.endswith("\n") else "\n")


def main() -> int:
    try:
        check_java_25()
        cases = parse_plan(PLAN_PATH.read_text(encoding="utf-8"))
        with tempfile.TemporaryDirectory(prefix="ui-test-") as directory:
            classes = Path(directory) / "classes"
            classes.mkdir()
            compile_application(classes)
            for number, case in enumerate(cases, start=1):
                working_directory = Path(directory) / f"case-{number}"
                working_directory.mkdir()
                if case.initial_save is not None:
                    data_directory = working_directory / "data"
                    data_directory.mkdir()
                    (data_directory / "tasks.txt").write_text(
                        case.initial_save,
                        encoding="utf-8",
                    )
                input_text, actual, exit_code = run_case(case, classes, working_directory)
                print(f"\n=== Test Case {number}: {case.name} ===")
                print(f"Aim: {case.aim}")
                print_transcript(case, input_text, actual)
                if exit_code != 0 or actual != case.expected:
                    print("RESULT: FAILED")
                    print("EXPECTED OUTPUT:")
                    print(case.expected, end="" if case.expected.endswith("\n") else "\n")
                    print("ACTUAL OUTPUT:")
                    print(actual, end="" if actual.endswith("\n") else "\n")
                    print("Stopping immediately; later test cases were not run.")
                    return 1
                print("RESULT: PASSED")
        print(f"\nAll {len(cases)} UI test cases passed.")
        return 0
    except (OSError, ValueError, RuntimeError, subprocess.SubprocessError) as error:
        print(f"UI test runner error: {error}", file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
