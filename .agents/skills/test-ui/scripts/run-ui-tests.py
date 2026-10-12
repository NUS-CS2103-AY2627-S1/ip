#!/usr/bin/env python3
"""
UI Test Runner for Braun (CS2103T iP)

Executes test cases specified in a test plan markdown file (default: test/ui-test-plan.md),
validates the output against expected results with fail-fast behavior on discrepancies,
and prints a complete record of the interactive console session upon completion.
"""

import argparse
import difflib
import glob
import os
import re
import shutil
import subprocess
import sys
from pathlib import Path

# Ensure UTF-8 output and automatic line flushing
if hasattr(sys.stdout, "reconfigure"):
    sys.stdout.reconfigure(line_buffering=True, encoding="utf-8")
if hasattr(sys.stderr, "reconfigure"):
    sys.stderr.reconfigure(line_buffering=True, encoding="utf-8")

# ANSI Color codes for formatted terminal output
GREEN = "\033[92m"
RED = "\033[91m"
YELLOW = "\033[93m"
CYAN = "\033[96m"
MAGENTA = "\033[95m"
BOLD = "\033[1m"
RESET = "\033[0m"

DIVIDER = "    ____________________________________________________________"


class TestCase:
    def __init__(self, name: str, aim: str, input_text: str, expected_output: str, case_num: int):
        self.name = name.strip()
        self.aim = aim.strip()
        self.input_text = input_text.strip()
        self.expected_output = expected_output.strip()
        self.case_num = case_num


def find_repo_root() -> Path:
    """Finds the root directory of the repository."""
    current = Path.cwd()
    while current != current.parent:
        if (current / ".git").exists() or (current / "src" / "main" / "java" / "Braun.java").exists():
            return current
        current = current.parent
    return Path.cwd()


def find_java_binaries() -> tuple[str, str]:
    """
    Locates working javac and java executables.
    Checks JAVA_HOME, PATH, SDKMAN candidates, and system JVM directories.
    """
    # 1. Check JAVA_HOME
    java_home = os.environ.get("JAVA_HOME")
    if java_home:
        javac_path = Path(java_home) / "bin" / "javac"
        java_path = Path(java_home) / "bin" / "java"
        if javac_path.is_file() and java_path.is_file():
            try:
                res1 = subprocess.run([str(java_path), "-version"], capture_output=True, timeout=2)
                res2 = subprocess.run([str(javac_path), "-version"], capture_output=True, timeout=2)
                if res1.returncode == 0 and res2.returncode == 0:
                    return str(javac_path), str(java_path)
            except Exception:
                pass

    # 2. Check candidate directories on macOS/Linux
    search_patterns = [
        str(Path.home() / ".sdkman" / "candidates" / "java" / "current" / "bin"),
        str(Path.home() / ".sdkman" / "candidates" / "java" / "*" / "bin"),
        str(Path.home() / ".jdks" / "*" / "bin"),
        "/Library/Java/JavaVirtualMachines/*/Contents/Home/bin",
        "/opt/homebrew/opt/openjdk*/bin",
        "/usr/local/opt/openjdk*/bin",
    ]

    for pattern in search_patterns:
        for match in sorted(glob.glob(pattern), reverse=True):
            candidate_dir = Path(match)
            javac_candidate = candidate_dir / "javac"
            java_candidate = candidate_dir / "java"
            if javac_candidate.is_file() and java_candidate.is_file():
                # Verify that this candidate can actually execute
                try:
                    res1 = subprocess.run([str(java_candidate), "-version"], capture_output=True, timeout=2)
                    res2 = subprocess.run([str(javac_candidate), "-version"], capture_output=True, timeout=2)
                    if res1.returncode == 0 and res2.returncode == 0:
                        return str(javac_candidate), str(java_candidate)
                except Exception:
                    continue

    # 3. Fallback to system PATH binaries
    javac_sys = shutil.which("javac") or "javac"
    java_sys = shutil.which("java") or "java"
    return javac_sys, java_sys


def parse_test_plan(plan_path: Path) -> list[TestCase]:
    """Parses test cases from markdown test plan file."""
    if not plan_path.is_file():
        raise FileNotFoundError(f"Test plan file not found: {plan_path}")

    content = plan_path.read_text(encoding="utf-8")

    # Regular expression to match each test case section
    case_pattern = re.compile(
        r"###\s+Test Case\s*(\d*):\s*(.*?)\n"
        r"(?:[\s\S]*?)\*\*Aim\*\*:\s*(.*?)\n"
        r"(?:[\s\S]*?)\*\*Input\*\*:\s*```(?:\w+)?\n([\s\S]*?)```"
        r"(?:[\s\S]*?)\*\*Expected Output\*\*:\s*```(?:\w+)?\n([\s\S]*?)```",
        re.MULTILINE,
    )

    test_cases = []
    for idx, match in enumerate(case_pattern.finditer(content), 1):
        num_str, title, aim, inp, out = match.groups()
        case_num = int(num_str) if num_str.isdigit() else idx
        test_cases.append(TestCase(name=title, aim=aim, input_text=inp, expected_output=out, case_num=case_num))

    if not test_cases:
        raise ValueError(f"No valid test cases found in {plan_path}. Please check format.")

    return test_cases


def compile_java(repo_root: Path, javac_cmd: str) -> None:
    """Compiles all Java source files in src/main/java."""
    src_dir = repo_root / "src" / "main" / "java"
    java_files = list(src_dir.rglob("*.java"))
    if not java_files:
        raise FileNotFoundError(f"No Java files found in {src_dir}")

    cmd = [javac_cmd] + [str(f) for f in java_files]
    result = subprocess.run(cmd, cwd=str(repo_root), capture_output=True, text=True)
    if result.returncode != 0:
        print(f"{RED}{BOLD}Compilation failed:{RESET}\n{result.stderr}", file=sys.stderr)
        sys.exit(1)


def read_response_block(process: subprocess.Popen) -> str:
    """
    Reads lines from the Braun process stdout until a complete block
    enclosed by divider lines is captured.
    """
    lines = []
    divider_count = 0

    while True:
        line = process.stdout.readline()
        if not line:
            break
        lines.append(line)
        if DIVIDER in line:
            divider_count += 1
            if divider_count >= 2:
                break

    return "".join(lines)


def normalize_text(text: str) -> str:
    """Normalizes line endings and trailing whitespace per line."""
    lines = [line.rstrip() for line in text.strip().splitlines()]
    return "\n".join(lines)


def generate_diff(expected: str, actual: str) -> str:
    """Generates a colorized unified diff between expected and actual output."""
    exp_lines = expected.splitlines(keepends=True)
    act_lines = actual.splitlines(keepends=True)
    diff = difflib.unified_diff(
        exp_lines,
        act_lines,
        fromfile="Expected Output",
        tofile="Actual Output",
        lineterm="",
    )

    colored_diff = []
    for line in diff:
        if line.startswith("+"):
            colored_diff.append(f"{GREEN}{line}{RESET}")
        elif line.startswith("-"):
            colored_diff.append(f"{RED}{line}{RESET}")
        elif line.startswith("@"):
            colored_diff.append(f"{CYAN}{line}{RESET}")
        else:
            colored_diff.append(line)
    return "\n".join(colored_diff)


def run_tests(plan_path: Path, repo_root: Path, compile_first: bool = True) -> bool:
    """Runs the test cases sequentially and checks outputs."""
    javac_cmd, java_cmd = find_java_binaries()

    if compile_first:
        print(f"{CYAN}Compiling Java sources using {javac_cmd}...{RESET}", flush=True)
        compile_java(repo_root, javac_cmd)

    test_cases = parse_test_plan(plan_path)
    print(f"{BOLD}Loaded {len(test_cases)} test cases from {plan_path.name}{RESET}\n", flush=True)

    # Ensure fresh storage state for test session
    default_data_file = repo_root / "data" / "braun.txt"
    if default_data_file.exists():
        default_data_file.unlink()

    # Spawn Braun process
    main_class = "braun.Braun" if (repo_root / "src" / "main" / "java" / "braun" / "Braun.java").exists() else "Braun"
    proc = subprocess.Popen(
        [java_cmd, "-cp", "src/main/java", main_class],
        cwd=str(repo_root),
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        bufsize=1,
    )

    session_records = []
    all_passed = True

    try:
        for tc in test_cases:
            print(f"Running Test Case {tc.case_num:02d}: {BOLD}{tc.name}{RESET}...", end=" ", flush=True)

            if tc.input_text:
                # Send input command to Braun
                proc.stdin.write(tc.input_text + "\n")
                proc.stdin.flush()

            actual_raw = read_response_block(proc)
            actual_norm = normalize_text(actual_raw)
            expected_norm = normalize_text(tc.expected_output)

            # Record session log entry
            if tc.input_text:
                session_records.append((f"> {tc.input_text}", actual_raw.rstrip()))
            else:
                session_records.append(("[Startup]", actual_raw.rstrip()))

            if actual_norm == expected_norm:
                print(f"{GREEN}PASSED{RESET}", flush=True)
            else:
                print(f"{RED}FAILED{RESET}", flush=True)
                print("\n" + "=" * 80)
                print(f"{RED}{BOLD}TEST CASE {tc.case_num} FAILED: {tc.name}{RESET}")
                print(f"{BOLD}Aim:{RESET} {tc.aim}")
                if tc.input_text:
                    print(f"{BOLD}Input:{RESET} {tc.input_text}")
                print("-" * 80)
                print(f"{BOLD}Diff (Expected vs Actual):{RESET}")
                print(generate_diff(expected_norm, actual_norm))
                print("=" * 80 + "\n")

                all_passed = False
                break

    finally:
        try:
            if proc.stdin and not proc.stdin.closed:
                proc.stdin.close()
            proc.terminate()
            proc.wait(timeout=2)
        except Exception:
            pass

    if all_passed:
        print(f"\n{GREEN}{BOLD}{'=' * 80}")
        print(f" ✔ All {len(test_cases)} test cases passed successfully!")
        print(f"{'=' * 80}{RESET}\n", flush=True)

        print(f"{MAGENTA}{BOLD}==================== CONSOLE SESSION TRANSCRIPT ===================={RESET}")
        for prompt, response in session_records:
            print(f"{YELLOW}{BOLD}{prompt}{RESET}")
            print(response)
            print()
        print(f"{MAGENTA}{BOLD}===================================================================={RESET}")
        return True
    else:
        print(f"{RED}{BOLD}✖ Test session terminated immediately due to failure.{RESET}", flush=True)
        return False


def main():
    parser = argparse.ArgumentParser(description="Run UI/IO tests for the Braun chatbot application.")
    parser.add_argument(
        "--plan",
        type=str,
        default="test/ui-test-plan.md",
        help="Path to the test plan markdown file (default: test/ui-test-plan.md)",
    )
    parser.add_argument(
        "--no-compile",
        action="store_true",
        help="Skip recompiling Java sources before running tests",
    )

    args = parser.parse_args()
    repo_root = find_repo_root()
    plan_path = (repo_root / args.plan).resolve()

    success = run_tests(plan_path=plan_path, repo_root=repo_root, compile_first=not args.no_compile)
    sys.exit(0 if success else 1)


if __name__ == "__main__":
    main()
