#!/usr/bin/env python3
import argparse
import json
import os
import re
import shutil
import subprocess
import sys
from pathlib import Path
from tempfile import TemporaryDirectory


REPOSITORY_ROOT = Path(__file__).resolve().parents[4]


def extract_json_block(text: str):
    match = re.search(r"```json\s*(\{.*?\})\s*```", text, flags=re.DOTALL)
    if not match:
        raise ValueError("No JSON block found in test/ui-test-plan.md")
    return json.loads(match.group(1))


def java_tool(name: str) -> str:
    """Locates Java tools without storing a developer's machine-specific paths."""
    java_home = os.environ.get("JAVA_HOME")
    executable = name + (".exe" if os.name == "nt" else "")
    candidate = Path(java_home, "bin", executable) if java_home else None
    if candidate and candidate.is_file():
        return str(candidate)
    found = shutil.which(name)
    if not found:
        raise ValueError(f"{name} was not found. Install Java 25 and configure JAVA_HOME or PATH.")
    return found


def require_java_25(executable: str):
    result = subprocess.run(
        [executable, "-version"], capture_output=True, text=True, encoding="utf-8", timeout=15
    )
    if result.returncode != 0 or not re.search(
        r'(?:version\s+"|^javac\s+)25(?:[.\s"+-]|$)', result.stdout + result.stderr, re.MULTILINE
    ):
        raise ValueError("The UI tests require Java 25. Check JAVA_HOME and java -version.")


def compile_cli(classes: Path):
    """Compiles the CLI once, without requiring JavaFX or altering repository files."""
    compiler = java_tool("javac")
    require_java_25(compiler)
    sources = sorted(
        path for path in (REPOSITORY_ROOT / "src/main/java").rglob("*.java")
        if "gui" not in path.relative_to(REPOSITORY_ROOT / "src/main/java").parts
        and path.name != "Launcher.java"
    )
    classes.mkdir()
    result = subprocess.run(
        [compiler, "-encoding", "UTF-8", "--release", "25", "-d", str(classes), *map(str, sources)],
        capture_output=True, text=True, encoding="utf-8", timeout=60,
    )
    if result.returncode != 0:
        raise ValueError(f"CLI compilation failed:\n{result.stdout}{result.stderr}")


def run_case(app_command: list[str], case: dict, working_directory: Path):
    proc = subprocess.run(
        app_command,
        input=case.get("input", ""),
        text=True,
        encoding="utf-8",
        capture_output=True,
        cwd=working_directory,
        timeout=30,
    )
    actual = proc.stdout
    expected = case.get("expected", "")
    return proc, actual, expected


def run_session(app_command: list[str], cases: list[dict], work_root: Path):
    print("=== UI Test Session ===")
    print("Command:")
    print(json.dumps(app_command))
    for index, case in enumerate(cases):
        case_id = case.get("id", "unnamed-case")
        aim = case.get("aim", "No description provided.")
        input_text = case.get("input", "")
        expected = case.get("expected", "")

        print(f"\n--- Case: {case_id} ---")
        print(f"Aim: {aim}")
        print("Input:")
        print(input_text)

        working_directory = work_root / f"case-{index}"
        working_directory.mkdir()
        try:
            proc, actual, expected = run_case(app_command, case, working_directory)
        except subprocess.TimeoutExpired as exc:
            print("FAIL: application did not finish within 30 seconds")
            print("Expected:\n" + expected)
            output = exc.stdout or b""
            print("Actual:\n" + (output.decode("utf-8", errors="replace") if isinstance(output, bytes) else output))
            return 1
        print("Output:")
        print(actual)
        if proc.stderr:
            print("Standard error:")
            print(proc.stderr)

        if proc.returncode != 0 or actual != expected:
            print(f"\nFAIL: exit code {proc.returncode} or output mismatch")
            print("Expected:")
            print(expected)
            print("Actual:")
            print(actual)
            print("\nSession terminated immediately after failing case.")
            return 1

        print("Result: PASS")

    print("\n=== End of UI Test Session ===")
    return 0


def main():
    parser = argparse.ArgumentParser(description="Run UI acceptance tests from a markdown test plan.")
    parser.add_argument("--plan", default="test/ui-test-plan.md", help="Path to the UI test plan markdown file.")
    parser.add_argument("--jar", type=Path, help="Use an existing packaged JAR instead of compiling sources.")
    args = parser.parse_args()

    try:
        payload = extract_json_block(Path(args.plan).read_text(encoding="utf-8"))
        app_command = payload.get("app_command")
        cases = payload.get("cases", [])
        if not isinstance(app_command, list) or not app_command or app_command[0] != "java":
            raise ValueError("The test plan must include an 'app_command' argument array beginning with java.")
        if not all(isinstance(argument, str) for argument in app_command):
            raise ValueError("Each app_command argument must be a string.")
        if not isinstance(cases, list) or not cases:
            raise ValueError("The test plan must include at least one case.")
        for case in cases:
            if not isinstance(case, dict) or not all(isinstance(case.get(key), str) for key in ("input", "expected")):
                raise ValueError("Each test case needs string input and expected fields.")
        java = java_tool("java")
        require_java_25(java)
        with TemporaryDirectory(prefix="sage-ui-") as temporary:
            work_root = Path(temporary)
            if args.jar:
                classpath = args.jar.resolve(strict=True)
            else:
                classpath = work_root / "classes"
                compile_cli(classpath)
            command = [
                java, "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
                *[argument.replace("{classes}", str(classpath)) for argument in app_command[1:]],
            ]
            return run_session(command, cases, work_root)
    except (OSError, ValueError, subprocess.TimeoutExpired) as exc:
        print(str(exc), file=sys.stderr)
        return 2


if __name__ == "__main__":
    raise SystemExit(main())
