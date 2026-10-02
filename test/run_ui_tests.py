"""Run the console UI cases recorded in test/ui-test-plan.md.

The runner uses only Python's standard library. It compiles the Java sources with
Gradle and JDK 25, executes every test case in a fresh process, compares complete
console output, and replaces test/ui-test-results.md with the latest results.
"""

from __future__ import annotations

import argparse
from dataclasses import dataclass
from datetime import datetime
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile


if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(encoding="utf-8")


REPOSITORY = Path(__file__).resolve().parent.parent
PLAN_PATH = REPOSITORY / "test" / "ui-test-plan.md"
RESULTS_PATH = REPOSITORY / "test" / "ui-test-results.md"
OUTPUT_DIRECTORY = REPOSITORY / "build" / "classes" / "java" / "main"


@dataclass(frozen=True)
class TestCase:
    """A console UI test parsed from the Markdown plan."""

    name: str
    aim: str
    input_text: str
    expected_output: str
    initial_data: str | None
    expected_data: str | None


@dataclass(frozen=True)
class TestResult:
    """The captured outcome of one test case."""

    case: TestCase
    actual_output: str
    standard_error: str
    exit_code: int
    actual_data: str | None
    passed: bool


def normalize_newlines(text: str) -> str:
    """Normalize platform line endings without changing other whitespace."""

    return text.replace("\r\n", "\n").replace("\r", "\n")


def parse_cases(plan_text: str) -> list[TestCase]:
    """Extract ordered test cases from the documented Markdown structure."""

    normalized_plan = normalize_newlines(plan_text)
    pattern = re.compile(
        r"^## (?P<name>TC-[^\n]+)\n(?P<body>.*?)(?=^## TC-|\Z)",
        re.MULTILINE | re.DOTALL,
    )
    cases = []
    for match in pattern.finditer(normalized_plan):
        body = match.group("body")
        aim = extract_prose_section(body, "Aim")
        input_text = extract_text_section(body, "Input")
        expected_output = extract_text_section(body, "Expected output")
        initial_data = extract_optional_text_section(body, "Initial data file")
        expected_data = extract_optional_text_section(body, "Expected data file")
        cases.append(
            TestCase(
                name=match.group("name"),
                aim=aim,
                input_text=input_text,
                expected_output=expected_output + "\n",
                initial_data=initial_data,
                expected_data=expected_data,
            )
        )
    return cases


def extract_prose_section(body: str, heading: str) -> str:
    """Extract a required prose section from a test case body."""

    match = re.search(
        rf"^### {re.escape(heading)}\n\n(?P<content>.*?)(?=\n\n### )",
        body,
        re.MULTILINE | re.DOTALL,
    )
    if not match:
        raise ValueError(f"Missing {heading} section in test plan")
    return match.group("content")


def extract_text_section(body: str, heading: str) -> str:
    """Extract a required fenced-text section from a test case body."""

    content = extract_optional_text_section(body, heading)
    if content is None:
        raise ValueError(f"Missing {heading} section in test plan")
    return content


def extract_optional_text_section(body: str, heading: str) -> str | None:
    """Extract an optional fenced-text section from a test case body."""

    match = re.search(
        rf"^### {re.escape(heading)}\n\n```text\n(?P<content>.*?)\n```",
        body,
        re.MULTILINE | re.DOTALL,
    )
    return match.group("content") if match else None


def executable_name(name: str) -> str:
    """Return the platform-specific Java executable name."""

    return f"{name}.exe" if os.name == "nt" else name


def candidate_jdk_homes(explicit_home: str | None) -> list[Path]:
    """Return likely JDK homes in priority order without duplicates."""

    candidates: list[Path] = []
    if explicit_home:
        candidates.append(Path(explicit_home).expanduser())
    if os.environ.get("JAVA_HOME"):
        candidates.append(Path(os.environ["JAVA_HOME"]))

    path_java = shutil.which("java")
    if path_java:
        candidates.append(Path(path_java).resolve().parent.parent)

    candidates.extend(sorted((Path.home() / ".jdks").glob("*25*"), reverse=True))
    candidates.extend(sorted(Path("/usr/lib/jvm").glob("*25*"), reverse=True))
    candidates.extend(
        sorted(
            Path("/Library/Java/JavaVirtualMachines").glob("*25*.jdk/Contents/Home"),
            reverse=True,
        )
    )

    unique: list[Path] = []
    seen: set[str] = set()
    for candidate in candidates:
        key = str(candidate.resolve()) if candidate.exists() else str(candidate)
        if key not in seen:
            seen.add(key)
            unique.append(candidate)
    return unique


def find_jdk_25(explicit_home: str | None) -> tuple[Path, Path, str]:
    """Locate Java and javac executables belonging to a JDK 25 installation."""

    for home in candidate_jdk_homes(explicit_home):
        bin_directory = home if home.name.lower() == "bin" else home / "bin"
        java = bin_directory / executable_name("java")
        javac = bin_directory / executable_name("javac")
        if not java.is_file() or not javac.is_file():
            continue

        version_result = subprocess.run(
            [str(java), "-version"],
            text=True,
            capture_output=True,
            encoding="utf-8",
            errors="replace",
        )
        version_output = normalize_newlines(
            version_result.stdout + version_result.stderr
        )
        if version_result.returncode == 0 and re.search(
            r'(?:java|openjdk) version "25(?:\.|\")', version_output
        ):
            return java, javac, version_output

    raise RuntimeError(
        "JDK 25 was not found. Set JAVA_HOME or pass --jdk with the JDK home."
    )


def markdown_block(text: str) -> list[str]:
    """Format captured text as a Markdown text block."""

    content = normalize_newlines(text)
    return ["```text", content.rstrip("\n"), "```"]


def first_difference(expected: str, actual: str) -> str:
    """Describe the first differing output line."""

    expected_lines = expected.splitlines()
    actual_lines = actual.splitlines()
    for number, (expected_line, actual_line) in enumerate(
        zip(expected_lines, actual_lines), start=1
    ):
        if expected_line != actual_line:
            return (
                f"First mismatch at line {number}: expected `{expected_line}`, "
                f"actual `{actual_line}`."
            )
    if len(expected_lines) != len(actual_lines):
        return (
            "Output line count differs: "
            f"expected {len(expected_lines)}, actual {len(actual_lines)}."
        )
    return "Output text differs."


def write_results(
    java_version: str,
    compile_status: str,
    results: list[TestResult],
    compile_output: str = "",
) -> None:
    """Replace the durable latest-run Markdown report."""

    failures = [result for result in results if not result.passed]
    timestamp = datetime.now().astimezone().isoformat(timespec="seconds")
    java_summary = java_version.splitlines()[0] if java_version else "Unavailable"
    lines = [
        "# Latest Console UI Test Results",
        "",
        f"- Run at: {timestamp}",
        f"- Java: {java_summary}",
        f"- Compilation: {compile_status}",
        f"- Test cases run: {len(results)}",
        f"- Passed: {len(results) - len(failures)}",
        f"- Failed: {len(failures)}",
        "",
    ]

    if compile_output:
        lines.extend(["## Compiler output", "", *markdown_block(compile_output), ""])

    lines.extend(
        [
            "## Results in execution order",
            "",
            "| Test case | Result |",
            "| --- | --- |",
        ]
    )
    for result in results:
        status = "PASS" if result.passed else "FAIL"
        lines.append(f"| {result.case.name} | {status} |")

    for result in failures:
        lines.extend(
            [
                "",
                f"## Failure: {result.case.name}",
                "",
                "### Aim",
                "",
                result.case.aim,
                "",
                "### Input",
                "",
                "```text",
                *[f"> {line}" for line in result.case.input_text.split("\n")],
                "```",
                "",
                "### Difference",
                "",
                result_difference(result),
                "",
                "### Actual output",
                "",
                *markdown_block(result.actual_output),
                "",
                "### Standard error",
                "",
                *markdown_block(result.standard_error or "None"),
                "",
                f"- Exit code: `{result.exit_code}`",
            ]
        )
        if result.case.expected_data is not None:
            lines.extend(
                [
                    "",
                    "### Actual data file",
                    "",
                    *markdown_block(result.actual_data or "Missing"),
                    "",
                    "### Expected data file",
                    "",
                    *markdown_block(result.case.expected_data),
                ]
            )
        lines.extend(
            [
                "",
                "### Expected output",
                "",
                *markdown_block(result.case.expected_output),
            ]
        )

    with RESULTS_PATH.open("w", encoding="utf-8", newline="\n") as results_file:
        results_file.write("\n".join(lines) + "\n")


def print_case(result: TestResult) -> None:
    """Print an inspectable transcript for one executed case."""

    print(f"CASE: {result.case.name}")
    print("INPUT")
    for line in result.case.input_text.split("\n"):
        print(f"> {line}")
    print("ACTUAL OUTPUT")
    print(result.actual_output, end="")
    if result.standard_error:
        print("STANDARD ERROR")
        print(result.standard_error, end="")
    if result.case.expected_data is not None:
        print("ACTUAL DATA FILE")
        print(result.actual_data or "Missing")
    print(f"EXIT CODE: {result.exit_code}")
    print(f"RESULT: {'PASS' if result.passed else 'FAIL'}")
    if not result.passed:
        print("EXPECTED OUTPUT")
        print(result.case.expected_output, end="")
        if result.case.expected_data is not None:
            print("EXPECTED DATA FILE")
            print(result.case.expected_data)


def result_difference(result: TestResult) -> str:
    """Describe whether output, saved data, or process status caused failure."""

    if result.actual_output != result.case.expected_output:
        return first_difference(result.case.expected_output, result.actual_output)
    if result.actual_data != result.case.expected_data:
        return "Saved data file differs from the expected contents."
    if result.standard_error:
        return "The program wrote unexpected text to standard error."
    return f"The program exited with code {result.exit_code}."


def main() -> int:
    """Compile the application, run every planned case, and save the report."""

    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--jdk",
        help="JDK 25 home or bin directory; normally discovered automatically",
    )
    args = parser.parse_args()

    cases = parse_cases(PLAN_PATH.read_text(encoding="utf-8"))
    if not cases:
        print("No test cases were found in test/ui-test-plan.md.", file=sys.stderr)
        return 2

    try:
        java, _, java_version = find_jdk_25(args.jdk)
    except RuntimeError as error:
        print(error, file=sys.stderr)
        write_results("", "BLOCKED", [])
        return 2

    print("JDK VERSION")
    print(java_version, end="")

    gradle_wrapper = REPOSITORY / (
        "gradlew.bat" if os.name == "nt" else "gradlew"
    )
    compile_environment = os.environ.copy()
    compile_environment["JAVA_HOME"] = str(java.parent.parent)
    compile_result = subprocess.run(
        [str(gradle_wrapper), "classes"],
        cwd=REPOSITORY,
        env=compile_environment,
        text=True,
        capture_output=True,
        encoding="utf-8",
        errors="replace",
    )
    compile_output = normalize_newlines(
        compile_result.stdout + compile_result.stderr
    )
    print("COMPILE")
    print("$ gradlew classes")
    if compile_output:
        print(compile_output, end="")
    if compile_result.returncode != 0:
        print(f"COMPILE FAIL (exit {compile_result.returncode})")
        write_results(java_version, "FAIL", [], compile_output)
        return 2
    print("COMPILE PASS")

    results: list[TestResult] = []
    for case in cases:
        with tempfile.TemporaryDirectory(prefix="ari-ui-test-") as test_directory:
            test_path = Path(test_directory)
            data_path = test_path / "data" / "ari.txt"
            if case.initial_data is not None:
                data_path.parent.mkdir(parents=True)
                data_path.write_text(
                    case.initial_data + "\n", encoding="utf-8", newline="\n"
                )

            process = subprocess.run(
                [str(java), "-ea", "-cp", str(OUTPUT_DIRECTORY), "ari.Ari"],
                cwd=test_path,
                input=case.input_text + "\n",
                text=True,
                capture_output=True,
                encoding="utf-8",
                errors="replace",
            )
            actual_data = (
                normalize_newlines(data_path.read_text(encoding="utf-8")).rstrip("\n")
                if data_path.is_file()
                else None
            )
        actual_output = normalize_newlines(process.stdout)
        standard_error = normalize_newlines(process.stderr)
        data_matches = (
            case.expected_data is None or actual_data == case.expected_data
        )
        passed = (
            process.returncode == 0
            and not standard_error
            and actual_output == case.expected_output
            and data_matches
        )
        result = TestResult(
            case=case,
            actual_output=actual_output,
            standard_error=standard_error,
            exit_code=process.returncode,
            actual_data=actual_data,
            passed=passed,
        )
        results.append(result)
        print_case(result)

    write_results(java_version, "PASS", results)
    failure_count = sum(not result.passed for result in results)
    print(
        f"SUMMARY: {len(results) - failure_count} passed, "
        f"{failure_count} failed"
    )
    return 1 if failure_count else 0


if __name__ == "__main__":
    raise SystemExit(main())
