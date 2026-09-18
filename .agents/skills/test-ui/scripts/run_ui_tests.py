"""Compile Furina and run the console cases in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import tempfile


ROOT = Path(__file__).resolve().parents[4]


def find_java25() -> tuple[Path, Path]:
    """Find matching Java 25 runtime and compiler executables."""
    candidates: list[Path] = []
    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        candidates.append(Path(java_home))

    for executable in ("java", "java.exe"):
        found = shutil.which(executable)
        if found:
            candidates.append(Path(found).parent.parent)

    for parent in (Path.home() / ".jdks", Path("C:/Program Files/Java")):
        if parent.is_dir():
            candidates.extend(sorted(parent.iterdir(), reverse=True))

    seen: set[Path] = set()
    for home in candidates:
        home = home.resolve()
        if home in seen:
            continue
        seen.add(home)
        java = home / "bin" / ("java.exe" if os.name == "nt" else "java")
        javac = home / "bin" / ("javac.exe" if os.name == "nt" else "javac")
        if not java.is_file() or not javac.is_file():
            continue
        version = subprocess.run(
            [javac, "-version"], capture_output=True, text=True, check=False
        )
        version_text = (version.stdout + version.stderr).strip()
        if re.search(r"javac 25(?:[.\s]|$)", version_text):
            return java, javac

    raise RuntimeError("Java 25 was not found. Set JAVA_HOME to a JDK 25 installation.")


def read_cases(plan_path: Path) -> list[dict[str, str]]:
    """Read test cases with Input and Expected output fenced blocks."""
    text = plan_path.read_text(encoding="utf-8")
    sections = re.split(r"^## Test case[^\n]*\n", text, flags=re.MULTILINE)
    headings = re.findall(r"^## (Test case[^\n]*)$", text, flags=re.MULTILINE)
    cases: list[dict[str, str]] = []
    for heading, section in zip(headings, sections[1:]):
        input_match = re.search(r"Input:\s*```[^\n]*\n(.*?)```", section, flags=re.DOTALL)
        expected_match = re.search(
            r"Expected output:\s*```[^\n]*\n(.*?)```", section, flags=re.DOTALL
        )
        if not input_match or not expected_match:
            raise ValueError(f"{heading} must contain Input and Expected output blocks")
        cases.append(
            {
                "name": heading,
                "input": input_match.group(1),
                "expected": expected_match.group(1),
            }
        )
    if not cases:
        raise ValueError(f"No test cases found in {plan_path}")
    return cases


def display_block(label: str, value: str) -> None:
    print(f"{label}:")
    print(value, end="" if value.endswith("\n") else "\n")


def find_gradle_wrapper() -> Path:
    """Return the platform-specific Gradle wrapper script."""
    wrapper_name = "gradlew.bat" if os.name == "nt" else "./gradlew"
    wrapper = ROOT / wrapper_name
    if not wrapper.is_file():
        raise RuntimeError(f"Gradle wrapper was not found at {wrapper}")
    return wrapper


def get_compile_classpath() -> str:
    """Ask Gradle for the classpath needed to compile the application."""
    result = subprocess.run(
        [find_gradle_wrapper(), "-q", "printUiTestClasspath"],
        cwd=ROOT,
        capture_output=True,
        text=True,
        check=False,
    )
    if result.returncode != 0:
        raise RuntimeError(result.stdout + result.stderr)
    return result.stdout.strip()


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan", type=Path, default=ROOT / "test" / "ui-test-plan.md"
    )
    args = parser.parse_args()

    try:
        java, javac = find_java25()
        cases = read_cases(args.plan)
        compile_classpath = get_compile_classpath()
    except (OSError, RuntimeError, ValueError) as error:
        print(f"SETUP FAILURE: {error}", file=sys.stderr)
        return 1

    source_files = sorted((ROOT / "src" / "main" / "java").rglob("*.java"))
    if not source_files:
        print("SETUP FAILURE: no Java source files found", file=sys.stderr)
        return 1

    with tempfile.TemporaryDirectory(prefix="furina-ui-") as temporary:
        classes = Path(temporary) / "classes"
        classes.mkdir()
        compile_result = subprocess.run(
            [javac, "-cp", compile_classpath, "-d", classes, *source_files],
            cwd=ROOT,
            capture_output=True,
            text=True,
            check=False,
        )
        if compile_result.returncode != 0:
            print("COMPILATION FAILURE")
            display_block("Compiler output", compile_result.stdout + compile_result.stderr)
            return 1

        for case_number, case in enumerate(cases, start=1):
            case_directory = Path(temporary) / f"case-{case_number}"
            case_directory.mkdir()
            runtime_classpath = os.pathsep.join((str(classes), compile_classpath))
            result = subprocess.run(
                [java, "-cp", runtime_classpath, "duke.Furina"],
                cwd=case_directory,
                input=case["input"],
                capture_output=True,
                text=True,
                check=False,
            )
            actual = result.stdout.replace("\r\n", "\n")
            expected = case["expected"].replace("\r\n", "\n")
            actual_compare = actual.rstrip("\n")
            expected_compare = expected.rstrip("\n")

            print(f"=== {case_number}. {case['name']} ===")
            display_block("Console input", case["input"])
            display_block("Console output", actual)
            if result.returncode != 0 or actual_compare != expected_compare:
                print("RESULT: FAIL")
                display_block("Expected output", expected)
                if result.stderr:
                    display_block("Error output", result.stderr)
                return 1
            print("RESULT: PASS")
            print()

    print(f"All {len(cases)} UI test cases passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
