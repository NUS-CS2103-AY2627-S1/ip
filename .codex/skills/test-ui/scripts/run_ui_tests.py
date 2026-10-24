#!/usr/bin/env python3
import difflib
import os
import re
import subprocess
import sys
from dataclasses import dataclass
from pathlib import Path


ROOT = Path(__file__).resolve().parents[4]
PLAN_PATH = ROOT / "test" / "ui-test-plan.md"
SOURCE_DIR = ROOT / "src" / "main" / "java"
DATA_PATH = ROOT / "data" / "kiki.txt"


@dataclass
class TestCase:
    name: str
    aim: str
    initial_saved_data: str | None
    inputs: str
    expected_output: str
    actual_output: str = ""


def normalize(text: str) -> str:
    return text.replace("\r\n", "\n").rstrip()


def run_command(command: list[str], input_text: str | None = None) -> subprocess.CompletedProcess:
    return subprocess.run(
        command,
        cwd=ROOT,
        input=input_text,
        text=True,
        capture_output=True,
        check=False,
    )


def parse_test_cases() -> list[TestCase]:
    if not PLAN_PATH.exists():
        raise FileNotFoundError(f"Missing test plan: {PLAN_PATH}")

    plan = PLAN_PATH.read_text(encoding="utf-8").replace("\r\n", "\n")
    chunks = re.split(r"(?m)^### Test Case:\s*", plan)
    cases = []

    for chunk in chunks[1:]:
        lines = chunk.splitlines()
        name = lines[0].strip()
        body = "\n".join(lines[1:]).strip("\n")

        match = re.search(
            r"(?s)^Aim:\n(?P<aim>.*?)(?:\n\nInitial Saved Data:\n(?P<initial>.*?))?"
            r"\n\nInputs:\n(?P<inputs>.*?)\n\nExpected Output:\n(?P<expected>.*)$",
            body,
        )
        if not match:
            raise ValueError(
                f"Test case '{name}' must contain Aim, Inputs, and Expected Output sections."
            )

        cases.append(
            TestCase(
                name=name,
                aim=match.group("aim").strip(),
                initial_saved_data=(
                    match.group("initial").strip("\n") if match.group("initial") is not None else None
                ),
                inputs=match.group("inputs").strip("\n"),
                expected_output=match.group("expected").strip("\n"),
            )
        )

    if not cases:
        raise ValueError(f"No test cases found in {PLAN_PATH}")

    return cases


def java_sources() -> list[str]:
    sources = sorted(str(path.relative_to(ROOT)) for path in SOURCE_DIR.glob("*.java"))
    if not sources:
        raise FileNotFoundError(f"No Java source files found in {SOURCE_DIR}")
    return sources


def find_main_class() -> str:
    pattern = re.compile(r"public\s+class\s+(\w+).*?public\s+static\s+void\s+main\s*\(", re.DOTALL)
    main_classes = []

    for source_file in SOURCE_DIR.glob("*.java"):
        content = source_file.read_text(encoding="utf-8")
        if pattern.search(content):
            main_classes.append(source_file.stem)

    if not main_classes:
        raise ValueError("Could not find a Java class with public static void main.")
    if len(main_classes) > 1:
        raise ValueError(f"Found multiple main classes: {', '.join(sorted(main_classes))}")

    return main_classes[0]


def compile_program() -> None:
    result = run_command(["javac", "-d", "out", *java_sources()])
    if result.returncode != 0:
        print("Compilation failed.", file=sys.stderr)
        print(result.stdout, file=sys.stderr)
        print(result.stderr, file=sys.stderr)
        raise SystemExit(result.returncode)


def snapshot_saved_data() -> bytes | None:
    if DATA_PATH.exists():
        return DATA_PATH.read_bytes()
    return None


def restore_saved_data(saved_data: bytes | None) -> None:
    if saved_data is None:
        if DATA_PATH.exists():
            DATA_PATH.unlink()
        return

    DATA_PATH.parent.mkdir(exist_ok=True)
    DATA_PATH.write_bytes(saved_data)


def prepare_saved_data(initial_saved_data: str | None) -> None:
    if initial_saved_data is None:
        if DATA_PATH.exists():
            DATA_PATH.unlink()
        return

    DATA_PATH.parent.mkdir(exist_ok=True)
    DATA_PATH.write_text(initial_saved_data + "\n", encoding="utf-8")


def run_test_case(test_case: TestCase, main_class: str) -> None:
    input_text = test_case.inputs
    if not input_text.endswith("\n"):
        input_text += "\n"

    prepare_saved_data(test_case.initial_saved_data)
    result = run_command(["java", "-cp", "out", main_class], input_text)
    test_case.actual_output = result.stdout

    actual = normalize(result.stdout)
    expected = normalize(test_case.expected_output)

    if result.returncode != 0 or actual != expected:
        report_failure(test_case, expected, actual, result.stderr)
        raise SystemExit(1)


def report_failure(test_case: TestCase, expected: str, actual: str, stderr: str) -> None:
    print(f"FAILED TEST CASE: {test_case.name}")
    print()
    print("Aim:")
    print(test_case.aim)
    print()
    print("Inputs:")
    print(test_case.inputs)
    print()
    print("Expected Output:")
    print(expected)
    print()
    print("Actual Output:")
    print(actual)
    print()
    print("Diff:")
    diff = difflib.unified_diff(
        expected.splitlines(),
        actual.splitlines(),
        fromfile="expected",
        tofile="actual",
        lineterm="",
    )
    print("\n".join(diff))
    if stderr:
        print()
        print("Process stderr:")
        print(stderr)


def print_session(test_case: TestCase) -> None:
    print(f"### Test Case: {test_case.name}")
    print(f"Aim: {test_case.aim}")
    print()
    print("Console Session:")
    print("```text")
    for command in test_case.inputs.splitlines():
        print(f"> {command}")
    print(normalize(test_case.actual_output))
    print("```")
    print()


def git_output(command: list[str]) -> str:
    result = run_command(command)
    if result.returncode != 0:
        raise RuntimeError(result.stderr.strip() or result.stdout.strip())
    return result.stdout.strip()


def should_finish_level() -> bool:
    if "--finish" in sys.argv[1:]:
        return True
    return bool(os.environ.get("LEVEL") or os.environ.get("COMMIT_MESSAGE"))


def ask_if_missing(value: str | None, prompt: str, env_name: str) -> str:
    if value:
        return value

    if not sys.stdin.isatty():
        raise SystemExit(f"Missing required value. Re-run with --finish in a terminal, or set {env_name}.")

    answer = input(f"{prompt}: ").strip()
    if not answer:
        raise SystemExit(f"{prompt} cannot be empty.")
    return answer


def normalize_level(raw_level: str) -> str:
    level = raw_level.strip()
    if re.fullmatch(r"\d+", level):
        return f"Level-{level}"
    if re.search(r"\s", level):
        raise SystemExit("Level/tag name cannot contain whitespace.")
    return level


def finish_level() -> None:
    if not should_finish_level():
        return

    level = os.environ.get("LEVEL")
    commit_message = os.environ.get("COMMIT_MESSAGE")

    level = normalize_level(ask_if_missing(level, "Level number or tag name (e.g. 4 or A-MoreOOP)", "LEVEL"))
    commit_message = ask_if_missing(commit_message, "COMMIT_MESSAGE", "COMMIT_MESSAGE")
    print(f"Using tag: {level}")
    print()

    branch = git_output(["git", "branch", "--show-current"])
    if branch != "master":
        raise SystemExit(f"Refusing to finish level: current branch is '{branch}', not 'master'.")

    existing_tag = run_command(["git", "rev-parse", "-q", "--verify", f"refs/tags/{level}"])
    if existing_tag.returncode == 0:
        raise SystemExit(f"Refusing to finish level: tag '{level}' already exists.")

    status = git_output(["git", "status", "--short"])
    print("Git status before commit:")
    print(status or "(working tree clean)")
    print()

    if not status:
        raise SystemExit("Refusing to commit: there are no changes to commit.")

    print("Files about to be committed:")
    for line in status.splitlines():
        print(line)
    print()

    commands = [
        ["git", "add", "."],
        ["git", "commit", "-m", commit_message],
        ["git", "tag", level],
        ["git", "push", "origin", "master"],
        ["git", "push", "origin", level],
    ]

    for command in commands:
        print("$ " + " ".join(command))
        result = run_command(command)
        if result.stdout:
            print(result.stdout, end="")
        if result.stderr:
            print(result.stderr, end="", file=sys.stderr)
        if result.returncode != 0:
            raise SystemExit(result.returncode)


def main() -> int:
    saved_data = snapshot_saved_data()
    try:
        cases = parse_test_cases()
        main_class = find_main_class()
        compile_program()

        for test_case in cases:
            run_test_case(test_case, main_class)

        for test_case in cases:
            print_session(test_case)

        print("ALL UI TESTS PASSED")
        print()

        is_finishing = should_finish_level()
        finish_level()
        if is_finishing:
            print()
            print("ALL UI TESTS PASSED")
        return 0
    except Exception as error:
        print(f"UI test runner error: {error}", file=sys.stderr)
        return 1
    finally:
        restore_saved_data(saved_data)


if __name__ == "__main__":
    raise SystemExit(main())
