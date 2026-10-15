#!/usr/bin/env python3
"""Run the console UI test plan in test/ui-test-plan.md against the compiled
program, one command at a time, and stop at the first mismatch.

Usage (from the repository root):
    py .claude/skills/test-ui/scripts/run-ui-tests.py [path/to/ui-test-plan.md]

To test a built jar instead of compiling the sources (e.g. a release), add
--jar path/to/ff15.jar. The program then runs from that jar, in the current
directory, so run it from an empty folder to see what a fresh user sees.
"""
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

DIVIDER_RE = re.compile(r"^ {4}_+\s*$")
CASE_RE = re.compile(r"^## Test Case: (.+)$", re.MULTILINE)
FIELD_RE = re.compile(
    r"\*\*Aim:\*\*\s*(?P<aim>.*?)\s*\n"
    r"\*\*Input:\*\*\s*```\n(?P<input>.*?)```\s*\n"
    r"\*\*Expected Output:\*\*\s*```\n(?P<expected>.*?)```",
    re.DOTALL,
)


class TestCase:
    def __init__(self, name, aim, input_lines, expected_lines):
        self.name = name
        self.aim = aim
        self.input_lines = input_lines
        self.expected_lines = expected_lines


def parse_plan(plan_path):
    text = plan_path.read_text(encoding="utf-8")
    headers = list(CASE_RE.finditer(text))
    cases = []
    for i, header in enumerate(headers):
        start = header.end()
        end = headers[i + 1].start() if i + 1 < len(headers) else len(text)
        body = text[start:end]
        match = FIELD_RE.search(body)
        if not match:
            raise ValueError(
                f"Test case '{header.group(1)}' is missing an Aim, Input, "
                "or Expected Output block."
            )
        input_lines = [line for line in match.group("input").split("\n") if line != ""]
        if len(input_lines) > 1:
            raise ValueError(
                f"Test case '{header.group(1)}' has {len(input_lines)} input "
                "lines. Each test case may send at most one command; split "
                "extra commands into their own test cases."
            )
        expected_lines = match.group("expected").rstrip("\n").split("\n")
        cases.append(TestCase(header.group(1), match.group("aim").strip(), input_lines, expected_lines))
    if not cases:
        raise ValueError(f"No '## Test Case: ...' sections found in {plan_path}")
    if cases[0].input_lines:
        raise ValueError("The first test case must be the startup case, with an empty Input block.")
    return cases


PACKAGE_RE = re.compile(r"^\s*package\s+([\w.]+)\s*;", re.MULTILINE)
# Libraries the console program runs without: JavaFX (the GUI) and LangChain4j
# (the AI, which only the GUI wires in). Plain javac has neither on its classpath.
EXTERNAL_IMPORT_RE = re.compile(r"^\s*import\s+(javafx|dev\.langchain4j)\.", re.MULTILINE)


def console_sources(src_dir):
    """
    Return the sources that make up the console program: everything under src_dir
    except the classes that import JavaFX or LangChain4j. A console test plan
    cannot drive a window or reach an AI, and compiling either would need jars
    this script deliberately does without -- plain javac, no build tool.
    """
    return [
        java_file
        for java_file in sorted(src_dir.rglob("*.java"))
        if not EXTERNAL_IMPORT_RE.search(java_file.read_text(encoding="utf-8"))
    ]


def find_main_class(src_dir):
    for java_file in console_sources(src_dir):
        text = java_file.read_text(encoding="utf-8")
        if re.search(r"public\s+static\s+void\s+main\s*\(", text):
            package_match = PACKAGE_RE.search(text)
            if package_match:
                return f"{package_match.group(1)}.{java_file.stem}"
            return java_file.stem
    raise ValueError(f"No file with a public static void main(...) found under {src_dir}")


def find_main_class_in_jar(jar_path):
    """
    Returns the console entry point inside jar_path: the one class outside the
    GUI package whose constant pool declares a main(String[]) method. Only the
    class files' bytes are read; nothing in the jar is run.
    """
    import zipfile
    with zipfile.ZipFile(jar_path) as jar:
        for name in jar.namelist():
            if not name.endswith(".class") or "$" in name or "/gui/" in name:
                continue
            if name.startswith(("javafx/", "com/sun/", "dev/", "META-INF/", "org/", "module-info")):
                continue
            data = jar.read(name)
            if b"main" in data and b"([Ljava/lang/String;)V" in data:
                return name[:-len(".class")].replace("/", ".")
    raise ValueError(f"No console main class found in {jar_path}")


def compile_program(src_dir, build_dir):
    java_files = [str(p) for p in console_sources(src_dir)]
    result = subprocess.run(
        ["javac", "-d", str(build_dir), *java_files],
        capture_output=True, text=True,
    )
    if result.returncode != 0:
        print("********** BUILD FAILURE **********")
        print(result.stderr)
        sys.exit(1)


def read_block(proc):
    """Read lines until a divider line, or EOF. Returns (content_lines, divider_line_or_None)."""
    content = []
    while True:
        line = proc.stdout.readline()
        if line == "":
            return content, None
        line = line.rstrip("\r\n")
        if DIVIDER_RE.match(line):
            return content, line
        content.append(line)


def strip_blank_edges(lines):
    start, end = 0, len(lines)
    while start < end and lines[start] == "":
        start += 1
    while end > start and lines[end - 1] == "":
        end -= 1
    return lines[start:end]


def describe_crash(proc):
    remaining_err = proc.stderr.read() if proc.stderr else ""
    proc.wait(timeout=5)
    detail = f"Program exited (code {proc.returncode}) before printing the expected output."
    if remaining_err.strip():
        detail += f"\n--- stderr ---\n{remaining_err}"
    return detail


def parse_args(argv):
    """Returns (plan_path or None, jar_path or None) from the command line."""
    plan_path = None
    jar_path = None
    args = list(argv)
    while args:
        arg = args.pop(0)
        if arg == "--jar":
            if not args:
                sys.exit("--jar needs a path")
            jar_path = Path(args.pop(0))
        else:
            plan_path = Path(arg)
    return plan_path, jar_path


def main():
    repo_root = Path.cwd()
    plan_path, jar_path = parse_args(sys.argv[1:])
    if plan_path is None:
        plan_path = repo_root / "test" / "ui-test-plan.md"
    src_dir = repo_root / "src" / "main" / "java"

    cases = parse_plan(plan_path)

    build_dir = Path(tempfile.mkdtemp(prefix="ui-test-build-"))
    try:
        if jar_path is None:
            main_class = find_main_class(src_dir)
            compile_program(src_dir, build_dir)
            classpath = str(build_dir)
        else:
            # The jar's manifest names the GUI launcher; the console main class is
            # the one the jar's own sources declare, read from the jar itself.
            main_class = find_main_class_in_jar(jar_path)
            classpath = str(jar_path)

        proc = subprocess.Popen(
            ["java", "-cp", classpath, main_class],
            stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=subprocess.PIPE,
            text=True, bufsize=1,
        )

        transcript = []
        reference_divider = None
        failure = None

        # Content printed before the very first divider (should be nothing).
        leading, reference_divider = read_block(proc)
        if reference_divider is None:
            failure = ("(startup)", "the program's first divider line", describe_crash(proc))
        else:
            transcript.append(reference_divider)

        for case in cases:
            if failure:
                break
            if case.input_lines:
                proc.stdin.write(case.input_lines[0] + "\n")
                proc.stdin.flush()
                transcript.append("")
                transcript.append(case.input_lines[0])

                # Each response is bounded by an opening divider (printed as
                # soon as the input is read) and a closing divider. Consume
                # the opening one here; only blank lines (the separator
                # after the previous case's closing divider) may precede it.
                opening_content, opening_divider = read_block(proc)
                if opening_divider is None:
                    failure = (case.name, "an opening divider line", describe_crash(proc))
                    break
                if strip_blank_edges(opening_content):
                    failure = (
                        case.name,
                        "(only blank lines before the opening divider)",
                        "\n".join(opening_content),
                    )
                    break
                if opening_divider != reference_divider:
                    failure = (
                        case.name,
                        f"divider line: {reference_divider!r}",
                        f"divider line: {opening_divider!r}",
                    )
                    break
                transcript.append(opening_divider)

            content, divider = read_block(proc)
            if divider is None:
                failure = (case.name, "\n".join(case.expected_lines), describe_crash(proc))
                break
            if divider != reference_divider:
                failure = (
                    case.name,
                    f"divider line: {reference_divider!r}",
                    f"divider line: {divider!r}",
                )
                break

            actual_lines = strip_blank_edges(content)
            if actual_lines != case.expected_lines:
                failure = (case.name, "\n".join(case.expected_lines), "\n".join(actual_lines))
                break

            transcript.extend(actual_lines)
            transcript.append(reference_divider)

        if proc.poll() is None:
            try:
                proc.wait(timeout=3)
            except subprocess.TimeoutExpired:
                proc.terminate()

        print("=== Console session ===")
        print("\n".join(transcript))
        print()

        if failure:
            name, expected, actual = failure
            print(f"Test result: FAILED at test case '{name}'")
            print("--- Expected ---")
            print(expected)
            print("--- Actual ---")
            print(actual)
            sys.exit(1)
        else:
            print(f"Test result: PASSED ({len(cases)} test cases)")
    finally:
        shutil.rmtree(build_dir, ignore_errors=True)


if __name__ == "__main__":
    main()
