"""Compile Bobby and run the console UI cases defined in test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
import tempfile
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = ROOT / "test" / "ui-test-plan.md"
SOURCE_DIRECTORY = ROOT / "src" / "main" / "java"


def extract_section(case_text: str, title: str) -> str:
    """Return a fenced text section from one test case."""
    pattern = rf"^### {re.escape(title)}\n(?:\n)?```text\n(.*?)\n```"
    match = re.search(pattern, case_text, flags=re.MULTILINE | re.DOTALL)
    if match is None:
        raise ValueError(f"Missing or invalid '{title}' section")
    return match.group(1)


def extract_aim(case_text: str) -> str:
    """Return the aim text that follows the Aim heading."""
    match = re.search(r"^### Aim\n(.*?)(?=^### |\Z)", case_text, flags=re.MULTILINE | re.DOTALL)
    if match is None:
        raise ValueError("Missing 'Aim' section")
    return match.group(1).strip()


def extract_data_scope(case_text: str) -> str | None:
    """Return an optional shared data scope declared in a test-case comment."""
    match = re.search(r"<!--\s*data-scope:\s*([\w-]+)\s*-->", case_text)
    return match.group(1) if match else None


def read_test_cases() -> list[tuple[str, str, str, str, str | None]]:
    """Read test data and optional working-directory scopes from the plan."""
    plan = PLAN_PATH.read_text(encoding="utf-8").replace("\r\n", "\n")
    headings = list(re.finditer(r"^## Test Case \d+: (.+)$", plan, flags=re.MULTILINE))
    if not headings:
        raise ValueError("The test plan contains no test cases")

    cases = []
    for index, heading in enumerate(headings):
        end = headings[index + 1].start() if index + 1 < len(headings) else len(plan)
        case_text = plan[heading.end():end]
        cases.append((
            heading.group(1),
            extract_aim(case_text),
            extract_section(case_text, "Input"),
            extract_section(case_text, "Expected Output"),
            extract_data_scope(case_text),
        ))
    return cases


def normalise(text: str) -> str:
    """Normalise line endings and an optional final newline before comparison."""
    return text.replace("\r\n", "\n").rstrip("\n")


def is_console_source(source: Path) -> bool:
    """Return whether a Java source file can be compiled without JavaFX."""
    source_text = source.read_text(encoding="utf-8")
    return "import javafx." not in source_text


def compile_program(class_directory: Path) -> None:
    """Compile console Java sources into the supplied temporary directory."""
    sources = sorted(
        str(source) for source in SOURCE_DIRECTORY.rglob("*.java") if is_console_source(source)
    )
    if not sources:
        raise FileNotFoundError("No console Java files were found under src/main/java")
    result = subprocess.run(
        ["javac", "-encoding", "UTF-8", "-d", str(class_directory), *sources],
        capture_output=True,
        text=True,
        encoding="utf-8",
    )
    if result.returncode != 0:
        raise RuntimeError(f"Compilation failed:\n{result.stdout}{result.stderr}")


def print_transcript(title: str, aim: str, test_input: str, actual_output: str) -> None:
    """Print a readable record of one completed console test session."""
    print(f"\n=== {title} ===")
    print(f"Aim: {aim}")
    print("Console input:")
    print(test_input)
    print("Console output:")
    print(actual_output, end="" if actual_output.endswith("\n") else "\n")


def main() -> int:
    """Run every planned test, stopping after the first failure."""
    sys.stdout.reconfigure(encoding="utf-8")
    try:
        test_cases = read_test_cases()
        with tempfile.TemporaryDirectory(prefix="bobby-ui-tests-") as temporary_directory:
            class_directory = Path(temporary_directory)
            compile_program(class_directory)
            data_directories: dict[str, Path] = {}
            for case_number, (title, aim, test_input, expected_output, data_scope) in enumerate(
                    test_cases, start=1):
                if data_scope is None:
                    working_directory = Path(temporary_directory) / f"case-{case_number}"
                else:
                    working_directory = data_directories.setdefault(
                        data_scope, Path(temporary_directory) / f"scope-{data_scope}")
                working_directory.mkdir(parents=True, exist_ok=True)
                result = subprocess.run(
                    [
                        "java",
                        "-Dfile.encoding=UTF-8",
                        "-Dstdout.encoding=UTF-8",
                        "-Dstderr.encoding=UTF-8",
                        "-cp",
                        str(class_directory),
                        "bobby.Bobby",
                    ],
                    input=test_input + "\n",
                    capture_output=True,
                    text=True,
                    encoding="utf-8",
                    cwd=working_directory,
                )
                actual_output = result.stdout
                print_transcript(title, aim, test_input, actual_output)
                if result.returncode != 0 or normalise(actual_output) != normalise(expected_output):
                    print("FAILED: testing stopped at the first failing test case.")
                    if result.returncode != 0:
                        print(f"Program exit code: {result.returncode}")
                        print(f"Program error output:\n{result.stderr}")
                    print("Expected output:")
                    print(expected_output)
                    print("Actual output:")
                    print(actual_output, end="" if actual_output.endswith("\n") else "\n")
                    return 1
                print("PASSED")
    except (FileNotFoundError, RuntimeError, ValueError) as error:
        print(f"FAILED: {error}")
        return 1

    print("\nAll UI test cases passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
