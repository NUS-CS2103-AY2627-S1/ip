#!/usr/bin/env python3
"""Run console UI tests listed in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import difflib
import re
import shutil
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    name: str
    aim: str
    initial_data_file: str | None
    inputs: str
    expected: str
    expected_data_file: str | None


def normalize_newlines(text: str) -> str:
    return text.replace("\r\n", "\n").replace("\r", "\n")


def read_labeled_block(section: str, label: str) -> str:
    pattern = rf"{label}:\s*```(?:text)?\s*\n(.*?)\n```"
    match = re.search(pattern, section, flags=re.IGNORECASE | re.DOTALL)
    if not match:
        raise ValueError(f"Missing fenced text block after '{label}:'")
    return normalize_newlines(match.group(1))


def read_optional_labeled_block(section: str, label: str) -> str | None:
    try:
        return read_labeled_block(section, label)
    except ValueError:
        return None


def parse_plan(plan_path: Path) -> list[TestCase]:
    text = normalize_newlines(plan_path.read_text(encoding="utf-8"))
    heading_pattern = re.compile(r"^##+\s+(.+?)\s*$", flags=re.MULTILINE)
    matches = list(heading_pattern.finditer(text))
    cases: list[TestCase] = []

    for index, match in enumerate(matches):
        name = match.group(1)
        start = match.end()
        end = matches[index + 1].start() if index + 1 < len(matches) else len(text)
        section = text[start:end]

        aim_match = re.search(r"^Aim:\s*(.+?)\s*$", section, flags=re.MULTILINE)
        if not aim_match:
            raise ValueError(f"Missing Aim for test case '{name}'")

        cases.append(
            TestCase(
                name=name,
                aim=aim_match.group(1),
                initial_data_file=read_optional_labeled_block(section, "Initial data file"),
                inputs=read_labeled_block(section, "Inputs"),
                expected=read_labeled_block(section, "Expected output"),
                expected_data_file=read_optional_labeled_block(section, "Expected data file"),
            )
        )

    if not cases:
        raise ValueError(f"No test cases found in {plan_path}")
    return cases


def find_java_files(source_root: Path) -> list[str]:
    return [str(path) for path in source_root.rglob("*.java")]


def java_tool(tool_name: str, java_home: str | None) -> str:
    if java_home:
        suffix = ".exe" if sys.platform.startswith("win") else ""
        tool_path = Path(java_home) / "bin" / f"{tool_name}{suffix}"
        if tool_path.exists():
            return str(tool_path)
        raise RuntimeError(f"{tool_name} was not found under {tool_path.parent}")

    tool = shutil.which(tool_name)
    if tool is None:
        raise RuntimeError(
            f"{tool_name} was not found on PATH. Install/configure JDK 25 first, "
            f"or pass --java-home <path-to-jdk>."
        )
    return tool


def compile_sources(repo_root: Path, build_dir: Path, java_home: str | None) -> None:
    javac = java_tool("javac", java_home)

    source_root = repo_root / "src" / "main" / "java"
    java_files = find_java_files(source_root)
    if not java_files:
        raise RuntimeError(f"No Java source files found under {source_root}")

    build_dir.mkdir(parents=True, exist_ok=True)
    result = subprocess.run(
        [javac, "-d", str(build_dir), *java_files],
        cwd=repo_root,
        text=True,
        capture_output=True,
    )
    if result.returncode != 0:
        raise RuntimeError(
            "Compilation failed.\n\nSTDOUT:\n"
            + result.stdout
            + "\nSTDERR:\n"
            + result.stderr
        )


def run_case(
    repo_root: Path,
    build_dir: Path,
    main_class: str,
    case: TestCase,
    java_home: str | None,
) -> tuple[int, str, str]:
    java = java_tool("java", java_home)

    stdin = case.inputs
    if stdin and not stdin.endswith("\n"):
        stdin += "\n"

    result = subprocess.run(
        [java, "-cp", str(build_dir), main_class],
        cwd=repo_root,
        input=stdin,
        text=True,
        capture_output=True,
    )
    return result.returncode, normalize_newlines(result.stdout), normalize_newlines(result.stderr)


def clear_data_file(repo_root: Path) -> None:
    data_file = repo_root / "data" / "fifi.txt"
    if data_file.exists():
        data_file.unlink()


def write_data_file(repo_root: Path, content: str) -> None:
    data_file = repo_root / "data" / "fifi.txt"
    data_file.parent.mkdir(parents=True, exist_ok=True)
    data_file.write_text(content, encoding="utf-8")


def read_data_file(repo_root: Path) -> str:
    data_file = repo_root / "data" / "fifi.txt"
    if not data_file.exists():
        return ""
    return normalize_newlines(data_file.read_text(encoding="utf-8"))


def print_transcript(case: TestCase, actual: str) -> None:
    print(f"## {case.name}")
    print()
    print(f"Aim: {case.aim}")
    print()
    print("Console input:")
    print("```text")
    print(case.inputs)
    print("```")
    print()
    print("Console output:")
    print("```text")
    print(actual)
    print("```")
    print()


def print_failure(case: TestCase, expected: str, actual: str, stderr: str, returncode: int) -> None:
    print(f"FAILED: {case.name}")
    print(f"Aim: {case.aim}")
    print(f"Exit code: {returncode}")
    print()
    print("Commands entered:")
    print("```text")
    print(case.inputs)
    print("```")
    print()
    print("Expected output:")
    print("```text")
    print(expected)
    print("```")
    print()
    print("Actual output:")
    print("```text")
    print(actual)
    print("```")

    if stderr:
        print()
        print("Error output:")
        print("```text")
        print(stderr)
        print("```")

    print()
    print("Diff:")
    print("```diff")
    for line in difflib.unified_diff(
        expected.splitlines(),
        actual.splitlines(),
        fromfile="expected",
        tofile="actual",
        lineterm="",
    ):
        print(line)
    print("```")


def print_data_file_failure(case: TestCase, expected: str, actual: str) -> None:
    print(f"FAILED: {case.name}")
    print(f"Aim: {case.aim}")
    print()
    print("Expected data file:")
    print("```text")
    print(expected)
    print("```")
    print()
    print("Actual data file:")
    print("```text")
    print(actual)
    print("```")
    print()
    print("Diff:")
    print("```diff")
    for line in difflib.unified_diff(
        expected.splitlines(),
        actual.splitlines(),
        fromfile="expected-data-file",
        tofile="actual-data-file",
        lineterm="",
    ):
        print(line)
    print("```")


def main() -> int:
    parser = argparse.ArgumentParser(description="Run console UI tests from a Markdown test plan.")
    parser.add_argument("--repo-root", default=".", help="Repository root directory.")
    parser.add_argument("--plan", default="test/ui-test-plan.md", help="Markdown UI test plan path.")
    parser.add_argument("--main-class", default="Fifi", help="Main Java class to run.")
    parser.add_argument("--build-dir", default="out/test-ui", help="Temporary build output directory.")
    parser.add_argument("--java-home", help="JDK home directory to use when java/javac are not on PATH.")
    parser.add_argument("--keep-build", action="store_true", help="Keep compiled class files after the run.")
    args = parser.parse_args()

    repo_root = Path(args.repo_root).resolve()
    plan_path = (repo_root / args.plan).resolve()
    build_dir = (repo_root / args.build_dir).resolve()

    try:
        cases = parse_plan(plan_path)
        if build_dir.exists() and not args.keep_build:
            shutil.rmtree(build_dir)
        compile_sources(repo_root, build_dir, args.java_home)

        for case in cases:
            clear_data_file(repo_root)
            if case.initial_data_file is not None:
                write_data_file(repo_root, case.initial_data_file)
            returncode, actual, stderr = run_case(repo_root, build_dir, args.main_class, case, args.java_home)
            expected = normalize_newlines(case.expected)
            if returncode != 0 or actual != expected:
                print_failure(case, expected, actual, stderr, returncode)
                return 1
            if case.expected_data_file is not None:
                actual_data_file = read_data_file(repo_root)
                expected_data_file = normalize_newlines(case.expected_data_file)
                if actual_data_file != expected_data_file:
                    print_data_file_failure(case, expected_data_file, actual_data_file)
                    return 1
            print_transcript(case, actual)

        print(f"PASS: {len(cases)} UI test case(s) passed.")
        return 0
    except Exception as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 2
    finally:
        if build_dir.exists() and not args.keep_build:
            shutil.rmtree(build_dir)


if __name__ == "__main__":
    raise SystemExit(main())
