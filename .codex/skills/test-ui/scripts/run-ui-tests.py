"""Compile the chatbot and run the console UI test cases in test/ui-test-plan.md."""

from __future__ import annotations

import re
import subprocess
import sys
from pathlib import Path


CASE_PATTERN = re.compile(
    r"^## (?P<title>.+?)\n\n"
    r"Aim: (?P<aim>.+?)\n\n"
    r"(?:### Saved tasks\n```text\n(?P<saved_tasks>.*?)\n```\n\n)?"
    r"### Input\n```text\n(?P<input>.*?)\n```\n\n"
    r"### Expected output\n```text\n(?P<expected>.*?)\n```"
    r"(?:\n\n### Second session input\n```text\n(?P<second_input>.*?)\n```\n\n"
    r"### Second session expected output\n```text\n(?P<second_expected>.*?)\n```)?",
    re.MULTILINE | re.DOTALL,
)


def normalise(output: str) -> str:
    """Normalise line endings and insignificant trailing whitespace for comparison."""
    return "\n".join(line.rstrip() for line in output.replace("\r\n", "\n").split("\n")).strip()


def load_cases(plan_path: Path) -> list[dict[str, str]]:
    """Load structured UI test cases from the Markdown test plan."""
    plan_text = plan_path.read_text(encoding="utf-8")
    cases = [match.groupdict() for match in CASE_PATTERN.finditer(plan_text)]
    if not cases:
        raise ValueError("No test cases found. Follow the format in test/ui-test-plan.md.")
    return cases


def compile_program(repo_root: Path) -> Path:
    """Compile all production Java source files and return the classes directory."""
    source_files = sorted((repo_root / "src/main/java").rglob("*.java"))
    if not source_files:
        raise ValueError("No Java source files found in src/main/java.")

    classes_dir = repo_root / "build/classes"
    result = subprocess.run(
        ["javac", "-d", str(classes_dir), *(str(source) for source in source_files)],
        cwd=repo_root,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        print("Compilation failed:")
        print(result.stderr, end="")
        raise SystemExit(result.returncode)
    return classes_dir


def verify_java_version() -> None:
    """Ensure the UI tests run with the project's required Java 25 runtime."""
    result = subprocess.run(
        ["java", "-version"],
        capture_output=True,
        text=True,
        check=False,
    )
    version_output = result.stdout + result.stderr
    if result.returncode != 0 or not re.search(r'version "25(?:\.|\")', version_output):
        print("UI tests require Java 25. Detected:")
        print(version_output, end="")
        raise SystemExit(1)


def prepare_storage(repo_root: Path, saved_tasks: str | None) -> None:
    """Give each test a clean data file, optionally seeded with saved tasks."""
    data_dir = repo_root / "data"
    data_file = data_dir / "es.txt"
    if data_file.exists() or data_file.is_symlink():
        data_file.unlink()
    if saved_tasks is None:
        return
    data_dir.mkdir(parents=True, exist_ok=True)
    data_file.write_text(saved_tasks + "\n", encoding="utf-8")


def run_case(repo_root: Path, classes_dir: Path, commands: str) -> str:
    """Run one command sequence and return the chatbot output."""
    result = subprocess.run(
        ["java", "-cp", str(classes_dir), "es.Es"],
        cwd=repo_root,
        input=commands + "\n",
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        print("Program exited with an error:")
        print(result.stderr, end="")
        raise SystemExit(result.returncode)
    return result.stdout


def report_session(label: str, commands: str, actual: str, expected: str) -> None:
    """Print one session and stop if its output does not match."""
    print(f"Console input{label}:")
    print(commands)
    print(f"Console output{label}:")
    print(actual, end="" if actual.endswith("\n") else "\n")

    if normalise(actual) != normalise(expected):
        print("FAIL: output did not match the expected output.")
        print("Expected output:")
        print(expected)
        print("Actual output:")
        print(actual, end="" if actual.endswith("\n") else "\n")
        raise SystemExit(1)


def main() -> None:
    """Run each planned UI test and stop at the first mismatch."""
    repo_root = Path(__file__).resolve().parents[4]
    cases = load_cases(repo_root / "test/ui-test-plan.md")
    verify_java_version()
    classes_dir = compile_program(repo_root)

    for number, case in enumerate(cases, start=1):
        prepare_storage(repo_root, case.get("saved_tasks"))
        actual = run_case(repo_root, classes_dir, case["input"])
        print(f"=== Test {number}: {case['title']} ===")
        print(f"Aim: {case['aim']}")
        second_label = " (session 1)" if case.get("second_input") else ""
        report_session(second_label, case["input"], actual, case["expected"])

        if case.get("second_input"):
            second_actual = run_case(repo_root, classes_dir, case["second_input"])
            report_session(
                " (session 2)",
                case["second_input"],
                second_actual,
                case["second_expected"],
            )

        print("PASS\n")

    print(f"All {len(cases)} UI test case(s) passed.")


if __name__ == "__main__":
    main()
