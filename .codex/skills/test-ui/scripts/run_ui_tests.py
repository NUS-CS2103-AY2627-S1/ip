"""Run console UI cases recorded in the Clover UI test plan."""

from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
import re
import shutil
import subprocess
import tempfile


CASE_HEADING = re.compile(r"^## Test case: (?P<name>.+)$", re.MULTILINE)


@dataclass
class TestCase:
    """A console test case parsed from the UI test plan."""

    name: str
    input_text: str
    expected_output: str
    saved_task_data: str | None
    expected_task_data: str | None
    expected_tutoree_data: str | None


def get_fenced_section(case_text: str, heading: str) -> str | None:
    """Return a named text block from one Markdown test case."""
    pattern = re.compile(
        rf"\*\*{re.escape(heading)}:\*\*\s*\n```text\n(?P<content>.*?)\n```",
        re.DOTALL,
    )
    match = pattern.search(case_text)
    return match.group("content") if match else None


def parse_test_cases(plan_text: str) -> tuple[list[TestCase], list[str]]:
    """Parse automated and manual test cases from the UI test plan."""
    matches = list(CASE_HEADING.finditer(plan_text))
    test_cases: list[TestCase] = []
    manual_cases: list[str] = []

    for index, match in enumerate(matches):
        case_text = plan_text[match.end():matches[index + 1].start() if index + 1 < len(matches) else None]
        name = match.group("name")
        if "**Type:** Manual" in case_text:
            manual_cases.append(name)
            continue

        input_text = get_fenced_section(case_text, "Input")
        expected_output = get_fenced_section(case_text, "Expected output")
        if input_text is None or expected_output is None:
            raise ValueError(
                f"Test case '{name}' must contain **Input:** and **Expected output:** text blocks."
            )

        test_cases.append(
            TestCase(
                name=name,
                input_text=input_text,
                expected_output=expected_output,
                saved_task_data=get_fenced_section(case_text, "Saved data"),
                expected_task_data=get_fenced_section(case_text, "Expected saved data"),
                expected_tutoree_data=get_fenced_section(case_text, "Expected saved tutoree data"),
            )
        )
    return test_cases, manual_cases


def write_optional_file(directory: Path, filename: str, content: str | None) -> None:
    """Write optional initial persisted data for a test case."""
    if content is None:
        return
    data_directory = directory / "data"
    data_directory.mkdir()
    (data_directory / filename).write_text(f"{content}\n", encoding="utf-8")


def read_saved_file(directory: Path, filename: str) -> str:
    """Return saved data, using an empty string when the file was not created."""
    file_path = directory / "data" / filename
    return file_path.read_text(encoding="utf-8").rstrip("\n") if file_path.exists() else ""


def print_transcript(case: TestCase, actual_output: str) -> None:
    """Show the command input and program output for one completed case."""
    print(f"\n=== {case.name} ===")
    print("Console input:")
    print(case.input_text)
    print("Console output:")
    print(actual_output)


def run_case(project_root: Path, case: TestCase) -> tuple[bool, str]:
    """Run one isolated Clover session and compare its observable results."""
    with tempfile.TemporaryDirectory(prefix="clover-ui-") as temporary_directory:
        working_directory = Path(temporary_directory)
        write_optional_file(working_directory, "clover.txt", case.saved_task_data)
        command = ["java", "-ea", "-cp", str(project_root / "build/classes/java/main"), "clover.Clover"]
        completed = subprocess.run(
            command,
            cwd=working_directory,
            input=f"{case.input_text}\n",
            capture_output=True,
            text=True,
            check=False,
        )
        actual_output = completed.stdout.rstrip("\n")
        print_transcript(case, actual_output)

        if completed.returncode != 0:
            return False, f"Program exited with status {completed.returncode}:\n{completed.stderr.rstrip()}"
        if actual_output != case.expected_output:
            return (
                False,
                "Expected output:\n"
                f"{case.expected_output}\n\nActual output:\n{actual_output}",
            )

        expected_files = (
            ("clover.txt", case.expected_task_data),
            ("tutorees.txt", case.expected_tutoree_data),
        )
        for filename, expected_data in expected_files:
            if expected_data is not None:
                actual_data = read_saved_file(working_directory, filename)
                if actual_data != expected_data:
                    return (
                        False,
                        f"Expected saved data in data/{filename}:\n{expected_data}"
                        f"\n\nActual saved data:\n{actual_data}",
                    )
    return True, ""


def check_java_version() -> bool:
    """Return whether the active Java runtime is version 25."""
    if shutil.which("java") is None:
        print("FAIL: Java is not available on PATH.")
        return False
    completed = subprocess.run(["java", "-version"], capture_output=True, text=True, check=False)
    version_output = completed.stdout + completed.stderr
    if completed.returncode != 0 or 'version "25.' not in version_output:
        print("FAIL: Java 25 is required to run Clover.")
        print(version_output.rstrip())
        return False
    return True


def main() -> int:
    """Build Clover and run every automated case in the UI test plan."""
    project_root = Path(__file__).resolve().parents[4]
    plan_path = project_root / "test/ui-test-plan.md"
    if not check_java_version():
        return 1

    build = subprocess.run(["./gradlew", "classes"], cwd=project_root, check=False)
    if build.returncode != 0:
        print("FAIL: Clover did not compile; UI tests were not run.")
        return build.returncode

    try:
        test_cases, manual_cases = parse_test_cases(plan_path.read_text(encoding="utf-8"))
    except ValueError as error:
        print(f"FAIL: {error}")
        return 1

    for case in test_cases:
        passed, failure_message = run_case(project_root, case)
        if not passed:
            print(f"\nFAIL: {case.name}")
            print(failure_message)
            return 1
        print(f"PASS: {case.name}")

    if manual_cases:
        print(f"\nManual cases not run: {', '.join(manual_cases)}")
    print(f"PASS: {len(test_cases)} automated UI test case(s) completed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
