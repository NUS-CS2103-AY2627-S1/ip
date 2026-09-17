"""Run console UI tests and stop at the first output mismatch."""
import argparse
import json
import subprocess
import sys
import tempfile
import time
from pathlib import Path

TEST_DATA_FILE = Path("temp.csv")
MAIN_CLASS = "megatron.Megatron"


def run_command(command, input_text, input_delay_ms=0):
    """Run a command and optionally pause between lines of input."""
    if input_delay_ms <= 0:
        return subprocess.run(command, input=input_text, text=True,
                              capture_output=True, shell=False)

    with tempfile.TemporaryFile(mode="w+", encoding="utf-8") as stdout_file, \
            tempfile.TemporaryFile(mode="w+", encoding="utf-8") as stderr_file:
        process = subprocess.Popen(command, stdin=subprocess.PIPE,
                                   stdout=stdout_file, stderr=stderr_file,
                                   text=True, shell=False)
        try:
            for line_number, line in enumerate(input_text.splitlines(keepends=True)):
                if line_number > 0:
                    time.sleep(input_delay_ms / 1000)
                process.stdin.write(line)
                process.stdin.flush()
        except BrokenPipeError:
            pass
        finally:
            try:
                process.stdin.close()
            except BrokenPipeError:
                pass

        return_code = process.wait()
        stdout_file.seek(0)
        stderr_file.seek(0)
        return subprocess.CompletedProcess(command, return_code,
                                           stdout_file.read(), stderr_file.read())

def load_cases(path):
    text = path.read_text(encoding="utf-8")
    if path.suffix.lower() == ".json": return json.loads(text)
    start = text.index("<!-- TEST_CASES_JSON")
    start = text.index("\n", start) + 1
    return json.loads(text[start:text.index("-->", start)].strip())

def main():
    parser = argparse.ArgumentParser()
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument("--plan", type=Path); group.add_argument("--cases", type=Path)
    args = parser.parse_args(); passed = 0
    for number, case in enumerate(load_cases(args.plan or args.cases), 1):
        if case.get("persistence"):
            TEST_DATA_FILE.unlink(missing_ok=True)
            command = ["java", "-cp", "out/production/ip_project", MAIN_CLASS, str(TEST_DATA_FILE)]
            input_delay_ms = case.get("input_delay_ms", 0)
            first = run_command(command, case["save_input"], input_delay_ms)
            second = run_command(command, case["load_input"], input_delay_ms)
            TEST_DATA_FILE.unlink(missing_ok=True)
            actual = second.stdout
            print(f"\n=== Test {number}: {case['name']} ===\nAim: {case['aim']}\n")
            print(f"Save input:\n{case['save_input']}\nLoad input:\n{case['load_input']}\nOutput:\n{actual}")
            expected_matches = (case.get("expected_contains") in actual
                                if case.get("expected_contains") else actual == case["expected_output"])
            if first.returncode or second.returncode or not expected_matches:
                print("RESULT: FAIL")
                print(f"Expected output:\n{case['expected_output']}")
                print(f"Stopped after {passed} passed test(s).")
                return 1
            print("RESULT: PASS")
            passed += 1
            continue
        command = case["command"] if isinstance(case["command"], list) else case["command"].split()
        if command[-1:] == [MAIN_CLASS]:
            command.append(str(TEST_DATA_FILE))
        TEST_DATA_FILE.unlink(missing_ok=True)
        if case.get("initial_data") is not None:
            TEST_DATA_FILE.write_text(case["initial_data"], encoding="utf-8")
        result = run_command(command, case.get("input", ""), case.get("input_delay_ms", 0))
        TEST_DATA_FILE.unlink(missing_ok=True)
        actual, expected = result.stdout, case["expected_output"]
        print(f"\n=== Test {number}: {case['name']} ===\nAim: {case['aim']}\n$ {' '.join(command)}")
        print(f"Input:\n{case.get('input', '')}\nOutput:\n{actual}")
        expected_matches = (case.get("expected_contains") in actual
                            if case.get("expected_contains") else actual == expected)
        if result.returncode or not expected_matches:
            print(f"RESULT: FAIL\nExpected output:\n{expected}")
            if result.stderr: print(f"stderr:\n{result.stderr}")
            print(f"Stopped after {passed} passed test(s)."); return 1
        print("RESULT: PASS"); passed += 1
    print(f"\nAll {passed} test(s) passed."); return 0

if __name__ == "__main__": sys.exit(main())
