#!/usr/bin/env python3
"""Run the Markdown-defined command-line UI tests in order."""

from __future__ import annotations

import re
import os
import shutil
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    name: str
    aim: str
    command: str
    inputs: str
    expected: str


def _fenced_value(text: str, label: str, start: int, end: int) -> str:
    match = re.search(
        rf"(?ims)^\s*{re.escape(label)}\s*:\s*\n```[^\n]*\n(.*?)\n```",
        text[start:end],
    )
    if not match:
        raise ValueError(f"missing fenced {label} field")
    return match.group(1)


def parse_plan(plan_path: Path) -> list[TestCase]:
    text = plan_path.read_text(encoding="utf-8")
    sections = list(re.finditer(r"(?m)^###\s+(.+?)\s*$", text))
    if not sections:
        raise ValueError("the plan contains no ### test-case sections")

    cases = []
    for index, section in enumerate(sections):
        start = section.end()
        end = sections[index + 1].start() if index + 1 < len(sections) else len(text)
        body = text[start:end]
        aim_match = re.search(r"(?im)^\s*Aim:\s*(.+?)\s*$", body)
        command_match = re.search(r"(?im)^\s*Command:\s*`([^`]+)`\s*$", body)
        if not aim_match or not command_match:
            raise ValueError(f"test case '{section.group(1)}' needs Aim and Command")
        cases.append(
            TestCase(
                section.group(1).strip(),
                aim_match.group(1).strip(),
                command_match.group(1),
                _fenced_value(body, "Inputs", 0, len(body)),
                _fenced_value(body, "Expected output", 0, len(body)),
            )
        )
    return cases


def comparable(value: str) -> str:
    return value.replace("\r\n", "\n").replace("\r", "\n").removesuffix("\n")


def resolve_main_classpath(root: Path, transcript: list[str]) -> tuple[bool, str]:
    gradle_wrapper = root / ("gradlew.bat" if sys.platform == "win32" else "gradlew")
    environment = os.environ.copy()
    environment["GRADLE_USER_HOME"] = str(root / ".gradle-user")
    transcript.append(f"$ {gradle_wrapper.name} -q printMainClasspath")
    try:
        completed = subprocess.run(
            [str(gradle_wrapper), "-q", "printMainClasspath"],
            cwd=root,
            text=True,
            capture_output=True,
            timeout=60,
            env=environment,
        )
    except subprocess.TimeoutExpired as error:
        output = (error.stdout or "") + (error.stderr or "")
        transcript += ["[classpath output]", output, "[FAIL: classpath resolution timed out]"]
        return False, "<classpath resolution timed out>"

    output = completed.stdout + completed.stderr
    transcript += ["[classpath output]", output]
    if completed.returncode != 0:
        transcript.append(f"[FAIL: classpath resolution exit code {completed.returncode}]")
        return False, output
    classpath = next((line.strip() for line in reversed(completed.stdout.splitlines()) if line.strip()), "")
    if not classpath:
        transcript.append("[FAIL: Gradle returned an empty classpath]")
        return False, "<empty classpath>"
    transcript.append("[PASS]")
    return True, classpath


def compile_application(root: Path, transcript: list[str], classpath: str) -> tuple[bool, str]:
    transcript.append("=== Compilation ===")
    transcript.append("$ javac -cp <Gradle main classpath> -d out src/main/java/**/*.java")
    output_directory = root / "out"
    if output_directory.exists():
        shutil.rmtree(output_directory)
    source_root = root / "src" / "main" / "java"
    try:
        completed = subprocess.run(
            ["javac", "-cp", classpath, "-d", "out",
             *sorted(str(path) for path in source_root.rglob("*.java"))],
            cwd=root,
            text=True,
            capture_output=True,
            timeout=30,
        )
    except subprocess.TimeoutExpired as error:
        output = (error.stdout or "") + (error.stderr or "")
        transcript += ["[output]", output, "[FAIL: compilation timed out]"]
        return False, "<compilation timed out>"

    output = completed.stdout + completed.stderr
    transcript += ["[output]", output]
    if completed.returncode != 0:
        transcript.append(f"[FAIL: compilation exit code {completed.returncode}]")
        return False, output
    transcript.append("[PASS]")
    return True, output


def run(plan_path: Path) -> int:
    cases = parse_plan(plan_path)
    root = plan_path.resolve().parent.parent
    transcript: list[str] = []
    classpath_resolved, classpath = resolve_main_classpath(root, transcript)
    if not classpath_resolved:
        return finish(
            plan_path,
            transcript,
            None,
            None,
            None,
            failure_title="CLASSPATH RESOLUTION FAILED",
            failure_actual=classpath,
        )
    compiled, compilation_output = compile_application(root, transcript, classpath)
    if not compiled:
        return finish(
            plan_path,
            transcript,
            None,
            None,
            None,
            failure_title="COMPILATION FAILED",
            failure_actual=compilation_output,
        )
    for number, case in enumerate(cases, 1):
        command = case.command.replace(
            "java -cp out",
            f"java -cp out{os.pathsep}{classpath}",
        )
        transcript += [
            f"=== Test {number}: {case.name} ===",
            f"Aim: {case.aim}",
            f"$ {command}",
            f"[input]",
            case.inputs,
        ]
        try:
            completed = subprocess.run(
                command,
                cwd=root,
                input=case.inputs,
                text=True,
                capture_output=True,
                shell=True,
                timeout=30,
            )
        except subprocess.TimeoutExpired as error:
            actual = (error.stdout or "") + (error.stderr or "")
            transcript += ["[output]", actual, "[FAIL: timeout]"]
            return finish(plan_path, transcript, case, actual, "<process timed out>")

        actual = completed.stdout + completed.stderr
        transcript += ["[output]", actual]
        if completed.returncode != 0 or comparable(actual) != comparable(case.expected):
            transcript.append(f"[FAIL: exit code {completed.returncode}]")
            return finish(plan_path, transcript, case, actual, case.expected)
        transcript.append("[PASS]")

    return finish(plan_path, transcript, None, None, None)


def finish(
    plan_path: Path,
    transcript: list[str],
    failed: TestCase | None,
    actual: str | None,
    expected: str | None,
    failure_title: str | None = None,
    failure_actual: str | None = None,
) -> int:
    session_path = plan_path.parent / "ui-test-session.log"
    session_path.write_text("\n".join(transcript) + "\n", encoding="utf-8")
    print("\n".join(transcript))
    if failure_title:
        print(f"\n{failure_title}")
        print("--- Compiler output ---")
        print(failure_actual or "<no output>")
        print(f"Session record: {session_path}")
        return 1
    if failed:
        print("\nFAILED TEST:", failed.name)
        print("--- Actual output ---")
        print(actual or "<no output>")
        print("--- Expected output ---")
        print(expected or "<no output>")
        return 1
    test_count = sum(line.startswith("=== Test ") for line in transcript)
    print(f"\nAll {test_count} test case(s) passed.")
    print(f"Session record: {session_path}")
    return 0


if __name__ == "__main__":
    if len(sys.argv) != 2:
        raise SystemExit("usage: run_ui_tests.py test/ui-test-plan.md")
    try:
        raise SystemExit(run(Path(sys.argv[1])))
    except (OSError, ValueError) as error:
        raise SystemExit(f"test-ui: {error}")
