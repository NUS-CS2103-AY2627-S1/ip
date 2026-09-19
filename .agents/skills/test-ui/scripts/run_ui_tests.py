import argparse
import json
import re
import subprocess
import sys
import tempfile
from pathlib import Path


DIVIDER = "____________________________________________________________"


def normalize_response(value):
    if isinstance(value, list):
        value = "\n".join(value)
    value = value.replace("\r\n", "\n").replace("\r", "\n")
    lines = value.split("\n")
    while lines and lines[0] == "":
        lines.pop(0)
    while lines and lines[-1] == "":
        lines.pop()
    return "\n".join(lines)


def load_cases(plan_path):
    plan_text = plan_path.read_text(encoding="utf-8")
    sections = re.findall(r"^##\s+(.+?)\s*$([\s\S]*?)(?=^##\s+|\Z)", plan_text, re.MULTILINE)
    cases = []
    for name, body in sections:
        aim_match = re.search(r"^\*\*Aim:\*\*\s*(.+?)\s*$", body, re.MULTILINE)
        storage_match = re.search(r"^\*\*Storage group:\*\*\s*(.+?)\s*$", body, re.MULTILINE)
        inputs_match = re.search(
            r"^###\s+Inputs\s*$\s*```json\s*\n([\s\S]*?)\n```",
            body,
            re.MULTILINE,
        )
        outputs_match = re.search(
            r"^###\s+Expected outputs\s*$\s*```json\s*\n([\s\S]*?)\n```",
            body,
            re.MULTILINE,
        )
        if not aim_match or not inputs_match or not outputs_match:
            raise ValueError(f"Test case '{name}' must contain Aim, Inputs, and Expected outputs")
        commands = json.loads(inputs_match.group(1))
        expected_outputs = json.loads(outputs_match.group(1))
        if not isinstance(commands, list) or not all(isinstance(command, str) for command in commands):
            raise ValueError(f"Test case '{name}' Inputs must be a JSON list of strings")
        if not isinstance(expected_outputs, list):
            raise ValueError(f"Test case '{name}' Expected outputs must be a JSON list")
        if len(commands) != len(expected_outputs):
            raise ValueError(f"Test case '{name}' must have one expected output per input")
        for output in expected_outputs:
            if not isinstance(output, str) and not (
                isinstance(output, list) and all(isinstance(line, str) for line in output)
            ):
                raise ValueError(f"Test case '{name}' expected outputs must contain strings or lists of lines")
        cases.append(
            {
                "name": name,
                "aim": aim_match.group(1),
                "storage_group": storage_match.group(1).strip() if storage_match else name,
                "commands": commands,
                "expected_outputs": [normalize_response(output) for output in expected_outputs],
            }
        )
    if not cases:
        raise ValueError("The UI test plan contains no test cases")
    return cases


def read_startup(process):
    output = []
    saw_prompt = False
    while True:
        line = process.stdout.readline()
        if line == "":
            raise RuntimeError("The program exited before displaying its input prompt")
        output.append(line)
        if "What can I do for you?" in line:
            saw_prompt = True
        if saw_prompt and line.rstrip("\r\n") == DIVIDER:
            return output


def read_command_response(process):
    output = []
    response = []
    divider_count = 0
    while divider_count < 2:
        line = process.stdout.readline()
        if line == "":
            raise RuntimeError("The program exited before completing its response")
        output.append(line)
        if line.rstrip("\r\n") == DIVIDER:
            divider_count += 1
        elif divider_count == 1:
            response.append(line)
    return output, normalize_response("".join(response))


def stop_process(process):
    if process.poll() is None:
        process.terminate()
        try:
            process.wait(timeout=5)
        except subprocess.TimeoutExpired:
            process.kill()
            process.wait(timeout=5)


def print_session(case_name, transcript):
    print(f"\n=== Console session: {case_name} ===")
    print("".join(transcript), end="" if transcript and transcript[-1].endswith("\n") else "\n")


def run_case(case, java_command, working_directory):
    process = subprocess.Popen(
        java_command,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True,
        encoding="utf-8",
        errors="replace",
        bufsize=1,
        cwd=working_directory,
    )
    transcript = []
    try:
        transcript.extend(read_startup(process))
        for command, expected in zip(case["commands"], case["expected_outputs"]):
            transcript.append(f"> {command}\n")
            process.stdin.write(command + "\n")
            process.stdin.flush()
            raw_output, actual = read_command_response(process)
            transcript.extend(raw_output)
            if actual != expected:
                print(f"FAIL: {case['name']}")
                print(f"Aim: {case['aim']}")
                print(f"Command: {command}")
                print("Expected output:")
                print(expected)
                print("Actual output:")
                print(actual)
                print_session(case["name"], transcript)
                return False
        if process.poll() is None:
            process.stdin.close()
        process.wait(timeout=5)
        if process.returncode != 0:
            error_output = process.stderr.read()
            print(f"FAIL: {case['name']} exited with code {process.returncode}")
            print(error_output)
            print_session(case["name"], transcript)
            return False
        print(f"PASS: {case['name']} - {case['aim']}")
        print_session(case["name"], transcript)
        return True
    except (BrokenPipeError, RuntimeError, subprocess.TimeoutExpired) as error:
        print(f"FAIL: {case['name']}")
        print(str(error))
        print_session(case["name"], transcript)
        return False
    finally:
        stop_process(process)


def main():
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8", errors="replace")
        sys.stderr.reconfigure(encoding="utf-8", errors="replace")

    parser = argparse.ArgumentParser(description="Run chatbot UI tests from a Markdown plan")
    parser.add_argument("--project-root", type=Path, default=Path.cwd())
    parser.add_argument("--plan", type=Path, default=Path("test/ui-test-plan.md"))
    parser.add_argument("--main-class", default="gary.Gary")
    arguments = parser.parse_args()

    project_root = arguments.project_root.resolve()
    plan_path = arguments.plan if arguments.plan.is_absolute() else project_root / arguments.plan
    source_directory = project_root / "src" / "main" / "java"
    source_files = sorted(
        source_file
        for source_file in source_directory.rglob("*.java")
        if "gui" not in source_file.relative_to(source_directory).parts
    )
    if not source_files:
        print(f"No Java source files found in {source_directory}", file=sys.stderr)
        return 2

    try:
        cases = load_cases(plan_path)
    except (OSError, ValueError, json.JSONDecodeError) as error:
        print(f"Invalid UI test plan: {error}", file=sys.stderr)
        return 2

    with tempfile.TemporaryDirectory(prefix="test-ui-") as temporary_directory:
        temporary_root = Path(temporary_directory)
        build_directory = temporary_root / "classes"
        build_directory.mkdir()
        compile_result = subprocess.run(
            ["javac", "-encoding", "UTF-8", "-d", str(build_directory), *map(str, source_files)],
            cwd=project_root,
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
        )
        if compile_result.returncode != 0:
            print("Compilation failed:")
            print(compile_result.stdout)
            print(compile_result.stderr)
            return 1

        java_command = [
            "java",
            "-Dfile.encoding=UTF-8",
            "-Dsun.stdout.encoding=UTF-8",
            "-Dsun.stderr.encoding=UTF-8",
            "-cp",
            str(build_directory),
            arguments.main_class,
        ]
        storage_directories = {}
        for case in cases:
            storage_group = case["storage_group"]
            if storage_group not in storage_directories:
                storage_directory = temporary_root / "sessions" / f"group-{len(storage_directories) + 1}"
                storage_directory.mkdir(parents=True)
                storage_directories[storage_group] = storage_directory
            if not run_case(case, java_command, storage_directories[storage_group]):
                return 1

    print(f"\nAll {len(cases)} UI test cases passed.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
