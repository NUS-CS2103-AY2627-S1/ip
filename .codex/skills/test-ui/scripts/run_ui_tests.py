#!/usr/bin/env python3
"""Compile Orbit and run fail-fast console tests defined in Markdown."""

from __future__ import annotations

import argparse
import os
import re
import shutil
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class TestCase:
    """One console session and the output fragments it must produce in order."""

    name: str
    aim: str
    sessions: tuple[str, ...]
    expected_fragments: tuple[str, ...]


def parse_test_plan(plan_path: Path) -> list[TestCase]:
    """Parse test cases from the plan's documented Markdown structure."""
    plan_text = plan_path.read_text(encoding="utf-8")
    section_pattern = re.compile(r"^## (?P<name>.+?)\n(?P<body>.*?)(?=^## |\Z)", re.MULTILINE | re.DOTALL)
    cases: list[TestCase] = []

    for section in section_pattern.finditer(plan_text):
        name = section.group("name").strip()
        body = section.group("body")
        aim_match = re.search(r"^Aim:\s*(.+)$", body, re.MULTILINE)
        input_matches = re.findall(
            r"^### (?:Input|Restart input(?: \d+)?)\s*\n```(?:text)?\s*\n(.*?)```",
            body,
            re.MULTILINE | re.DOTALL,
        )
        expected_match = re.search(
            r"### Expected output \(ordered fragments\)\s*```(?:text)?\s*\n(.*?)```",
            body,
            re.DOTALL,
        )
        if not (aim_match and input_matches and expected_match):
            raise ValueError(f"{name}: expected Aim, Input, and Expected output sections")

        sessions = tuple(commands.rstrip() + "\n" for commands in input_matches)
        expected_fragments = tuple(
            line.strip() for line in expected_match.group(1).splitlines() if line.strip()
        )
        if not expected_fragments:
            raise ValueError(f"{name}: expected at least one output fragment")
        cases.append(TestCase(name, aim_match.group(1).strip(), sessions, expected_fragments))

    if not cases:
        raise ValueError("No test cases found in the UI test plan")
    return cases


def resolve_java_tools(java_home_argument: str | None) -> tuple[Path, Path]:
    """Locate Java and javac, preferring the explicit argument and JAVA_HOME."""
    java_home = java_home_argument or os.environ.get("JAVA_HOME")
    executable_suffix = ".exe" if os.name == "nt" else ""
    if java_home:
        java = Path(java_home) / "bin" / f"java{executable_suffix}"
        javac = Path(java_home) / "bin" / f"javac{executable_suffix}"
    else:
        java_location = shutil.which("java")
        javac_location = shutil.which("javac")
        if not java_location or not javac_location:
            raise RuntimeError("Java and javac were not found; install JDK 25 or pass --java-home")
        java = Path(java_location)
        javac = Path(javac_location)

    if not java.is_file() or not javac.is_file():
        raise RuntimeError(f"Java tools were not found below {java_home}")

    version_result = subprocess.run([str(java), "--version"], capture_output=True, text=True, check=True)
    version_text = version_result.stdout + version_result.stderr
    major_match = re.search(r"(?:openjdk|java)\s+(?:version\s+)?\"?(\d+)", version_text)
    if not major_match or major_match.group(1) != "25":
        first_line = version_text.splitlines()[0] if version_text.splitlines() else "unknown version"
        raise RuntimeError(f"UI tests require Java 25, but found: {first_line}")
    return java, javac


def compile_sources(repo_root: Path, javac: Path, main_class: str) -> Path:
    """Compile the console entry point and its dependencies, without requiring JavaFX."""
    source_root = repo_root / "src" / "main" / "java"
    main_source = source_root.joinpath(*main_class.split(".")).with_suffix(".java")
    if not main_source.is_file():
        raise RuntimeError(f"Console entry point not found: {main_source}")

    build_dir = (repo_root / "_temp" / "ui-test-classes").resolve()
    expected_parent = (repo_root / "_temp").resolve()
    if build_dir.parent != expected_parent:
        raise RuntimeError("Refusing to clean a build directory outside the repository's _temp folder")
    if build_dir.exists():
        shutil.rmtree(build_dir)
    build_dir.mkdir(parents=True)

    compile_result = subprocess.run(
        [str(javac), "-d", str(build_dir), "-sourcepath", str(source_root), str(main_source)],
        capture_output=True,
        text=True,
    )
    if compile_result.returncode != 0:
        sys.stdout.write(compile_result.stdout)
        sys.stderr.write(compile_result.stderr)
        raise RuntimeError("Java compilation failed")
    return build_dir


def assert_fragments_in_order(output: str, fragments: tuple[str, ...]) -> str | None:
    """Return the first missing fragment, or None when all appear in order."""
    normalized_output = output.replace("\r\n", "\n")
    cursor = 0
    for fragment in fragments:
        fragment_index = normalized_output.find(fragment, cursor)
        if fragment_index < 0:
            return fragment
        cursor = fragment_index + len(fragment)
    return None


def run_tests(java: Path, build_dir: Path, main_class: str, cases: list[TestCase]) -> None:
    """Run each independent console session and stop immediately on failure."""
    for case_number, case in enumerate(cases, start=1):
        print(f"\n=== {case_number}. {case.name} ===")
        print(f"Aim: {case.aim}")
        combined_output = ""
        return_code = 0
        with tempfile.TemporaryDirectory(prefix="orbit-ui-") as working_directory:
            for session_number, commands in enumerate(case.sessions, start=1):
                print(f"--- input session {session_number} ---")
                print(commands, end="")
                result = subprocess.run(
                    [str(java), "-cp", str(build_dir), main_class],
                    input=commands,
                    capture_output=True,
                    text=True,
                    timeout=10,
                    cwd=working_directory,
                )
                session_output = result.stdout + result.stderr
                combined_output += session_output
                print(f"--- output session {session_number} ---")
                print(session_output, end="" if session_output.endswith("\n") else "\n")
                if result.returncode != 0:
                    return_code = result.returncode
                    break

        missing_fragment = assert_fragments_in_order(combined_output, case.expected_fragments)
        if return_code != 0 or missing_fragment is not None:
            print("--- expected ordered fragments ---")
            print("\n".join(case.expected_fragments))
            if return_code != 0:
                print(f"FAIL: process exited with code {return_code}")
            else:
                print(f"FAIL: missing or out-of-order fragment: {missing_fragment}")
            raise RuntimeError(f"{case.name} failed")
        print("PASS")

    print(f"\nAll {len(cases)} UI test cases passed.")


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--plan", default="test/ui-test-plan.md", help="Markdown test plan path")
    parser.add_argument("--main-class", default="orbit.Orbit", help="Java class containing main")
    parser.add_argument("--java-home", help="JDK 25 directory; overrides JAVA_HOME")
    arguments = parser.parse_args()

    repo_root = Path(__file__).resolve().parents[4]
    plan_path = (repo_root / arguments.plan).resolve()
    try:
        cases = parse_test_plan(plan_path)
        java, javac = resolve_java_tools(arguments.java_home)
        build_dir = compile_sources(repo_root, javac, arguments.main_class)
        run_tests(java, build_dir, arguments.main_class, cases)
    except (OSError, RuntimeError, ValueError, subprocess.SubprocessError) as error:
        sys.stdout.flush()
        print(f"ERROR: {error}", file=sys.stderr)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
