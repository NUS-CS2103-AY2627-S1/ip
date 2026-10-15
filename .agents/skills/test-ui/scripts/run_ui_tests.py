#!/usr/bin/env python3
"""Compile the iP and run ordered command-line interaction checks."""

from __future__ import annotations

import argparse
import json
import os
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


JAVA_MAJOR_VERSION = "25"
DEFAULT_TIMEOUT_SECONDS = 30


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("project_root", type=Path)
    parser.add_argument("test_plan", type=Path)
    return parser.parse_args()


def load_plan(plan_path: Path) -> dict:
    """Load and validate the JSON block embedded in the Markdown test plan."""
    text = plan_path.read_text(encoding="utf-8")
    match = re.search(r"```json\s*(\{.*?\})\s*```", text, re.DOTALL)
    if match is None:
        raise ValueError(f"No fenced JSON test plan found in {plan_path}")

    plan = json.loads(match.group(1))
    if not isinstance(plan.get("main_class"), str) or not plan["main_class"]:
        raise ValueError("Test plan requires a non-empty main_class")
    cases = plan.get("cases")
    if not isinstance(cases, list) or not cases:
        raise ValueError("Test plan requires at least one case")

    required_fields = ("name", "aim", "inputs", "expected")
    for index, case in enumerate(cases, start=1):
        if not isinstance(case, dict):
            raise ValueError(f"Case {index} must be an object")
        missing = [field for field in required_fields if field not in case]
        if missing:
            raise ValueError(f"Case {index} is missing: {', '.join(missing)}")
        if not all(isinstance(case[field], str) and case[field] for field in ("name", "aim")):
            raise ValueError(f"Case {index} requires non-empty name and aim")
        for field in ("inputs", "expected"):
            values = case[field]
            if not isinstance(values, list) or not values:
                raise ValueError(f"Case {index} requires a non-empty {field} list")
            if not all(isinstance(value, str) for value in values):
                raise ValueError(f"Case {index} {field} values must be strings")
        if not all(case["expected"]):
            raise ValueError(f"Case {index} expected values must be non-empty strings")
    return plan


def find_java_tool(tool_name: str) -> str:
    """Find a Java tool on PATH or in the current SDKMAN candidate."""
    sdkman_tool = Path.home() / ".sdkman" / "candidates" / "java" / "current" / "bin" / tool_name
    if sdkman_tool.is_file():
        return str(sdkman_tool)

    path_tool = shutil.which(tool_name)
    if path_tool is not None:
        return path_tool
    raise FileNotFoundError(f"Cannot find {tool_name} on PATH or in the current SDKMAN Java candidate")


def check_java_version(javac: str) -> None:
    """Ensure tests use the repository's required Java major version."""
    result = subprocess.run([javac, "-version"], capture_output=True, text=True, check=False)
    version_output = (result.stdout + result.stderr).strip()
    if result.returncode != 0 or not re.search(rf"\b{JAVA_MAJOR_VERSION}(?:\.|\b)", version_output):
        raise RuntimeError(f"Expected Java {JAVA_MAJOR_VERSION}, but found: {version_output}")
    print(f"Java compiler: {version_output}")


def get_runtime_classpath(project_root: Path) -> str:
    """Resolve the production dependency classpath through the Gradle wrapper."""
    wrapper_name = "gradlew.bat" if os.name == "nt" else "gradlew"
    wrapper = project_root / wrapper_name
    command = [str(wrapper), "-q", "printRuntimeClasspath"]
    result = subprocess.run(command, cwd=project_root, capture_output=True, text=True, check=False)
    if result.returncode != 0:
        print(result.stdout, file=sys.stderr)
        print(result.stderr, file=sys.stderr)
        raise RuntimeError("Could not resolve the application runtime classpath")
    return result.stdout.strip()


def compile_application(project_root: Path, javac: str, build_dir: Path,
                        runtime_classpath: str) -> None:
    """Compile every production Java source into the temporary build directory."""
    source_root = project_root / "src" / "main" / "java"
    sources = sorted(source_root.rglob("*.java"))
    if not sources:
        raise FileNotFoundError(f"No Java sources found under {source_root}")

    command = [
        javac,
        "-Xlint:all",
        "-cp",
        runtime_classpath,
        "-d",
        str(build_dir),
        *(str(source) for source in sources),
    ]
    result = subprocess.run(command, cwd=project_root, capture_output=True, text=True, check=False)
    if result.returncode != 0:
        print("Compilation failed", file=sys.stderr)
        print(result.stdout, file=sys.stderr)
        print(result.stderr, file=sys.stderr)
        raise RuntimeError("Java compilation failed")
    print(f"Compilation passed: {len(sources)} source file(s)")


def find_ordered_fragment(output: str, fragment: str, start: int) -> int:
    """Return the position after an expected fragment, or -1 when absent."""
    position = output.find(fragment, start)
    return -1 if position < 0 else position + len(fragment)


def run_case(java: str, build_dir: Path, runtime_classpath: str,
             working_dir: Path, main_class: str, case: dict) -> bool:
    """Run one interaction case, print its transcript, and check expectations."""
    user_input = "\n".join(case["inputs"]) + "\n"
    try:
        result = subprocess.run(
            [java, "-cp", os.pathsep.join((str(build_dir), runtime_classpath)), main_class],
            cwd=working_dir,
            input=user_input,
            capture_output=True,
            text=True,
            timeout=DEFAULT_TIMEOUT_SECONDS,
            check=False,
        )
    except subprocess.TimeoutExpired as error:
        print(f"FAIL {case['name']}: timed out after {DEFAULT_TIMEOUT_SECONDS} seconds")
        print(f"Aim: {case['aim']}")
        print("--- INPUT ---")
        print(user_input, end="")
        print("--- OUTPUT BEFORE TIMEOUT ---")
        print((error.stdout or "") + (error.stderr or ""))
        return False

    output = result.stdout.replace("\r", "\n")
    if result.stderr:
        output += "\n--- STDERR ---\n" + result.stderr

    print(f"\n=== {case['name']} ===")
    print(f"Aim: {case['aim']}")
    print("--- INPUT ---")
    print(user_input, end="")
    print("--- OUTPUT ---")
    print(output, end="" if output.endswith("\n") else "\n")

    if result.returncode != 0:
        print(f"FAIL: process exited with status {result.returncode}")
        return False

    search_position = 0
    for fragment in case["expected"]:
        next_position = find_ordered_fragment(output, fragment, search_position)
        if next_position < 0:
            print("FAIL: expected output fragment not found in order")
            print("--- EXPECTED FRAGMENT ---")
            print(fragment)
            print("--- ACTUAL OUTPUT ---")
            print(output)
            return False
        search_position = next_position

    print("PASS")
    return True


def main() -> int:
    args = parse_args()
    project_root = args.project_root.resolve()
    plan_path = args.test_plan if args.test_plan.is_absolute() else project_root / args.test_plan

    try:
        plan = load_plan(plan_path)
        javac = find_java_tool("javac")
        java = find_java_tool("java")
        check_java_version(javac)
        runtime_classpath = get_runtime_classpath(project_root)
        with tempfile.TemporaryDirectory(prefix="green-chonk-ui-tests-") as directory:
            build_dir = Path(directory)
            compile_application(project_root, javac, build_dir, runtime_classpath)
            for index, case in enumerate(plan["cases"], start=1):
                working_dir = build_dir / "cases" / str(index)
                working_dir.mkdir(parents=True)
                if not run_case(java, build_dir, runtime_classpath,
                                working_dir, plan["main_class"], case):
                    return 1
    except (FileNotFoundError, ValueError, RuntimeError, json.JSONDecodeError) as error:
        print(f"ERROR: {error}", file=sys.stderr)
        return 1

    print(f"\nAll {len(plan['cases'])} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
