"""Compile Koji's Pawn and run exact-output console UI test cases."""

from __future__ import annotations

import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile


REPOSITORY_ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = REPOSITORY_ROOT / "test" / "ui-test-plan.md"
SOURCE_DIRECTORY = REPOSITORY_ROOT / "src" / "main" / "java"
BUILD_DIRECTORY = REPOSITORY_ROOT / "out" / "test-ui"
MAIN_CLASS = "kojispawn.KojisPawn"


def find_java_tools() -> tuple[Path, Path]:
    """Return Java 25's java and javac executables."""
    executable_suffix = ".exe" if os.name == "nt" else ""
    candidate_homes: list[Path] = []

    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        candidate_homes.append(Path(java_home))

    if os.name == "nt":
        user_profile = Path(os.environ.get("USERPROFILE", Path.home()))
        candidate_homes.extend(sorted(
            (user_profile / ".jdks").glob("openjdk-25*"), reverse=True
        ))

    for home in candidate_homes:
        java = home / "bin" / f"java{executable_suffix}"
        javac = home / "bin" / f"javac{executable_suffix}"
        if java.is_file() and javac.is_file() and is_java_25(java):
            return java, javac

    java_on_path = shutil.which("java")
    javac_on_path = shutil.which("javac")
    if java_on_path and javac_on_path:
        java = Path(java_on_path)
        javac = Path(javac_on_path)
        if is_java_25(java):
            return java, javac

    raise RuntimeError(
        "Java 25 was not found. Configure JAVA_HOME or install it under ~/.jdks."
    )


def is_java_25(java: Path) -> bool:
    """Return whether the supplied executable reports Java 25."""
    result = subprocess.run(
        [str(java), "--version"], capture_output=True, text=True, check=False
    )
    version_output = result.stdout + result.stderr
    return result.returncode == 0 and re.search(r"\b25(?:\.|\s)", version_output) is not None


def parse_test_plan() -> list[dict[str, str]]:
    """Parse test cases from the Markdown test plan."""
    text = PLAN_PATH.read_text(encoding="utf-8")
    cases: list[dict[str, str]] = []

    for block in re.split(r"(?m)^## ", text)[1:]:
        name, _, body = block.partition("\n")
        aim_match = re.search(r"(?m)^Aim:\s*(.+)$", body)
        commands_match = re.search(
            r"### Commands\s*\n```text\n(.*?)\n```", body, re.DOTALL
        )
        expected_match = re.search(
            r"### Expected output\s*\n```text\n(.*?)\n```", body, re.DOTALL
        )
        initial_data_match = re.search(
            r"### Initial data\s*\n```text\n(.*?)\n```", body, re.DOTALL
        )

        if not aim_match or not commands_match or not expected_match:
            raise ValueError(f"Incomplete test case definition: {name}")

        cases.append({
            "name": name.strip(),
            "aim": aim_match.group(1).strip(),
            "commands": commands_match.group(1),
            "expected": expected_match.group(1),
            "initial_data": initial_data_match.group(1) if initial_data_match else None,
        })

    if not cases:
        raise ValueError("No test cases were found in the UI test plan.")

    return cases


def compile_program(javac: Path) -> None:
    """Compile all application sources into the ignored test build directory."""
    BUILD_DIRECTORY.mkdir(parents=True, exist_ok=True)
    sources = sorted(SOURCE_DIRECTORY.rglob("*.java"))
    if not sources:
        raise RuntimeError(f"No Java sources found in {SOURCE_DIRECTORY}")

    result = subprocess.run(
        [str(javac), "-d", str(BUILD_DIRECTORY), *map(str, sources)],
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        print("Compilation failed.")
        print(result.stdout, end="")
        print(result.stderr, end="", file=sys.stderr)
        raise SystemExit(result.returncode)


def normalise_output(text: str) -> str:
    """Normalise platform line endings while retaining meaningful whitespace."""
    return text.replace("\r\n", "\n").rstrip("\n")


def run_case(java: Path, case: dict[str, str]) -> bool:
    """Run one stateful command sequence and compare its exact output."""
    commands = case["commands"] + "\n"
    with tempfile.TemporaryDirectory(prefix="kojispawn-ui-test-") as working_directory:
        if case["initial_data"] is not None:
            data_file = Path(working_directory) / "data" / "kojispawn.txt"
            data_file.parent.mkdir(parents=True)
            data_file.write_text(case["initial_data"] + "\n", encoding="utf-8")

        result = subprocess.run(
            [str(java), "-cp", str(BUILD_DIRECTORY), MAIN_CLASS],
            input=commands,
            capture_output=True,
            text=True,
            check=False,
            cwd=working_directory,
        )
    actual = normalise_output(result.stdout)
    expected = normalise_output(case["expected"])

    print(f"=== {case['name']} ===")
    print(f"Aim: {case['aim']}")
    print("Commands:")
    print(case["commands"])
    print("Actual output:")
    print(actual)

    if result.returncode != 0 or actual != expected:
        print("RESULT: FAIL")
        print(f"Process exit code: {result.returncode}")
        if result.stderr:
            print("Standard error:")
            print(result.stderr.rstrip("\n"))
        print("Expected output:")
        print(expected)
        return False

    print("RESULT: PASS")
    return True


def main() -> int:
    """Compile the program and run each test case until one fails."""
    try:
        java, javac = find_java_tools()
        cases = parse_test_plan()
        compile_program(javac)
    except (OSError, RuntimeError, ValueError) as error:
        print(f"Test setup failed: {error}", file=sys.stderr)
        return 1

    for case in cases:
        if not run_case(java, case):
            return 1

    print(f"All {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
