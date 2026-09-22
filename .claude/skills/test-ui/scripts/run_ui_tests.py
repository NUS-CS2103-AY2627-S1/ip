#!/usr/bin/env python3
"""
Runs the UI test cases recorded in test/ui-test-plan.md against the compiled
Steph program.

For each test case, the listed commands are fed into one running instance of
Steph (so state like previously-added tasks carries across commands within a
test case, the same way it would in a real session). Steph wraps every
command's response between a delimiter line (the "____...." banner line), so
splitting the captured stdout on that delimiter recovers one output block per
command, in order. Each block is compared against the expected output
recorded for that command.

Test cases run in the order they appear in the file. The first mismatch
stops the whole run immediately (per the project's test-ui skill) rather than
continuing on to later commands or test cases, since a wrong output usually
invalidates whatever state later commands were relying on.
"""
import argparse
import difflib
import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path

MAIN_CLASS = "steph.Steph"


def find_repo_root(start: Path) -> Path:
    for candidate in [start, *start.parents]:
        if (candidate / ".git").exists():
            return candidate
    raise SystemExit(f"error: could not find a .git directory above {start}")


def compile_sources(repo_root: Path, build_dir: Path) -> None:
    java_files = sorted((repo_root / "src" / "main" / "java").rglob("*.java"))
    if not java_files:
        raise SystemExit(f"error: no .java files found under {repo_root / 'src' / 'main' / 'java'}")
    result = subprocess.run(
        ["javac", "-d", str(build_dir), *[str(f) for f in java_files]],
        capture_output=True, text=True,
    )
    if result.returncode != 0:
        print("Compilation failed:\n", file=sys.stderr)
        print(result.stderr, file=sys.stderr)
        raise SystemExit(1)


def run_session(build_dir: Path, commands: list[str], cwd: Path) -> subprocess.CompletedProcess:
    """Runs one Steph process, feeding `commands` on stdin, with `cwd` as its
    working directory. Steph persists tasks to a hard-coded relative path
    ("./data/steph.txt"), so the caller controls `cwd` to keep that file
    isolated: a fresh directory per test case (so cases don't leak into each
    other or clobber the repo's real save file), reused across the segments of
    one case so a "### Restart" can load what an earlier segment saved."""
    stdin = "\n".join(commands) + "\n"
    try:
        return subprocess.run(
            ["java", "-cp", str(build_dir), MAIN_CLASS],
            input=stdin, capture_output=True, text=True, timeout=15,
            cwd=str(cwd),
        )
    except subprocess.TimeoutExpired:
        raise SystemExit(
            f"error: program did not exit within 15s for commands {commands}. "
            "Does one of them read more input than the test case supplies?"
        )


def split_into_blocks(stdout: str) -> list[str]:
    """Splits stdout on its own delimiter line into non-empty chunks.

    Steph prints the delimiter as the very first line, so it's read from the
    output itself instead of being hardcoded here -- if the banner format
    changes, this still finds the right line.
    """
    lines = stdout.split("\n")
    if not lines or lines[0] == "":
        return []
    delimiter = lines[0]
    blocks, current = [], []
    for line in lines:
        if line == delimiter:
            blocks.append("\n".join(current))
            current = []
        else:
            current.append(line)
    return [b for b in blocks if b.strip() != ""]


def normalize(text: str) -> str:
    """Strips trailing whitespace per line and leading/trailing blank lines,
    so incidental formatting (a stray trailing space, an extra blank line at
    the end of a fenced block) doesn't cause a false failure. Leading
    whitespace on a line is kept, since indentation is meaningful here (e.g.
    the two-space indent before a task line)."""
    lines = [line.rstrip() for line in text.split("\n")]
    while lines and lines[0] == "":
        lines.pop(0)
    while lines and lines[-1] == "":
        lines.pop()
    return "\n".join(lines)


TEST_CASE_RE = re.compile(r"^##\s*Test case:\s*(.+?)\s*$", re.MULTILINE)
AIM_RE = re.compile(r"\*\*Aim:\*\*\s*(.+)")
SECTION_RE = re.compile(r"^###[ \t]*(.+?)[ \t]*$", re.MULTILINE)
FENCE_RE = re.compile(r"```\n(.*?)\n```", re.DOTALL)


def iter_sections(body: str):
    """Yields (heading, fenced_block_or_None) for each `### ...` section in the
    order they appear. `fenced_block_or_None` is the first ```-fenced block
    between this heading and the next `###` (or the end), or None when the
    section has no fenced block -- e.g. a bare `### Restart` marker."""
    marks = list(SECTION_RE.finditer(body))
    for i, m in enumerate(marks):
        start = m.end()
        end = marks[i + 1].start() if i + 1 < len(marks) else len(body)
        fence = FENCE_RE.search(body, start, end)
        yield m.group(1).strip(), (fence.group(1) if fence else None)


def parse_plan(plan_text: str) -> list[dict]:
    pieces = TEST_CASE_RE.split(plan_text)
    # pieces[0] is the preamble before the first "## Test case:"; ignore it.
    test_cases = []
    for i in range(1, len(pieces), 2):
        title, body = pieces[i].strip(), pieces[i + 1]
        aim_match = AIM_RE.search(body)
        aim = aim_match.group(1).strip() if aim_match else "(no **Aim:** line found)"

        # Walk the `###` sections in order. A "### Restart" between an Expected
        # output and the next Command records the index of that next command in
        # `restart_before`; run_test_case starts a fresh Steph process there.
        commands, expected, restart_before = [], [], set()
        pending_command = None
        restart_pending = False

        for heading, block in iter_sections(body):
            key = heading.lower()
            if key == "restart":
                restart_pending = True
            elif key == "command":
                if block is None:
                    continue
                cmd_lines = [l for l in block.split("\n") if l.strip() != ""]
                if not cmd_lines:
                    continue
                if len(cmd_lines) > 1:
                    print(f"warning: test case {title!r} has a Command block with "
                          f"multiple lines; using only the first: {cmd_lines[0]!r}",
                          file=sys.stderr)
                pending_command = cmd_lines[0]
            elif key == "expected output":
                if pending_command is None or block is None:
                    continue
                if restart_pending:
                    restart_before.add(len(commands))
                    restart_pending = False
                commands.append(pending_command)
                expected.append(normalize(block))
                pending_command = None

        if not commands:
            print(f"warning: test case {title!r} has no Command/Expected output "
                  "pairs; skipping it", file=sys.stderr)
            continue
        test_cases.append({
            "title": title, "aim": aim,
            "commands": commands, "expected": expected,
            "restart_before": restart_before,
        })
    return test_cases


def format_wrapped(delimiter: str, body: str) -> str:
    return f"{delimiter}\n{body}\n{delimiter}"


def run_test_case(build_dir: Path, case: dict) -> tuple[bool, list[str]]:
    """Runs one test case. Returns (passed, transcript_lines) -- the
    transcript covers only as much of the session as actually ran, so a
    failure's transcript stops at the command that failed.

    Commands are split into consecutive segments at every `restart_before`
    index; each segment runs as its own Steph process, and all segments of the
    case share one working directory. That's what a `### Restart` in the plan
    exercises: the second process starts with an empty task list in memory but
    loads whatever the first process saved to ./data/steph.txt."""
    commands = case["commands"]
    expected = case["expected"]
    restart_before = case.get("restart_before", set())

    boundaries = sorted(b for b in restart_before if 0 < b < len(commands))
    segment_starts = set(boundaries)  # command indices where a fresh process begins
    segments, prev = [], 0
    for b in boundaries + [len(commands)]:
        segments.append(commands[prev:b])
        prev = b

    case_cwd = Path(tempfile.mkdtemp(prefix="test-ui-case-"))
    transcript: list[str] = []
    try:
        inner: list[str] = []
        delimiter = "____"
        last_stderr = ""
        for seg_index, seg_commands in enumerate(segments):
            result = run_session(build_dir, seg_commands, case_cwd)
            last_stderr = result.stderr
            if result.stdout:
                delimiter = result.stdout.split("\n", 1)[0]
            blocks = split_into_blocks(result.stdout)
            if len(blocks) < 2:
                print(f"\nFAILED: {case['title']}")
                print(f"Aim: {case['aim']}")
                where = "" if len(segments) == 1 else (
                    f" (segment {seg_index + 1} of {len(segments)}, "
                    "the run started by a \"### Restart\")" if seg_index > 0
                    else f" (segment 1 of {len(segments)})")
                print("The program did not produce the expected startup banner "
                      f"and goodbye message{where}. Raw output follows.\n")
                print("--- stdout ---")
                print(result.stdout)
                if result.stderr:
                    print("--- stderr ---")
                    print(result.stderr)
                return False, transcript
            inner.extend(blocks[1:-1])

        for i, command in enumerate(commands):
            if i in segment_starts:
                transcript.append(
                    "--- restart: fresh Steph process, same ./data/steph.txt ---")
            transcript.append(f"> {command}")
            if i >= len(inner):
                print(f"\nFAILED: {case['title']}")
                print(f"Aim: {case['aim']}")
                print(f"Command {i + 1} (\"{command}\") produced no output block -- "
                      "the program likely exited early (e.g. a \"bye\" command in "
                      "a Command block, or a crash).")
                if last_stderr:
                    print("--- stderr ---")
                    print(last_stderr)
                print("\nTranscript up to the failure:")
                print("\n".join(transcript))
                return False, transcript

            actual = normalize(inner[i])
            transcript.append(format_wrapped(delimiter, actual))

            if actual != expected[i]:
                print(f"\nFAILED: {case['title']}")
                print(f"Aim: {case['aim']}")
                print(f"Command: {command}\n")
                print("--- expected ---")
                print(expected[i])
                print("\n--- actual ---")
                print(actual)
                diff = difflib.unified_diff(
                    expected[i].splitlines(), actual.splitlines(),
                    fromfile="expected", tofile="actual", lineterm="",
                )
                diff_text = "\n".join(diff)
                if diff_text:
                    print("\n--- diff (expected -> actual) ---")
                    print(diff_text)
                print("\nTranscript up to the failure:")
                print("\n".join(transcript))
                return False, transcript

        return True, transcript
    finally:
        shutil.rmtree(case_cwd, ignore_errors=True)


def cmd_run(args: argparse.Namespace) -> None:
    repo_root = find_repo_root(Path(__file__).resolve())
    plan_path = Path(args.plan) if args.plan else repo_root / "test" / "ui-test-plan.md"
    if not plan_path.exists():
        raise SystemExit(
            f"error: no test plan at {plan_path}. Add test cases there first "
            "(see the format documented at the top of that file, or use "
            "--record to generate a block from a real run)."
        )

    test_cases = parse_plan(plan_path.read_text())
    if not test_cases:
        raise SystemExit(f"error: {plan_path} has no test cases to run.")

    build_dir = Path(tempfile.mkdtemp(prefix="test-ui-build-"))
    try:
        compile_sources(repo_root, build_dir)

        full_transcript = []
        for case in test_cases:
            passed, transcript = run_test_case(build_dir, case)
            full_transcript.append(f"\n=== Test case: {case['title']} ===")
            full_transcript.append(f"Aim: {case['aim']}")
            full_transcript.extend(transcript)
            if not passed:
                sys.exit(1)

        print(f"All {len(test_cases)} test case(s) passed.\n")
        print("Console session record:")
        print("\n".join(full_transcript))
    finally:
        shutil.rmtree(build_dir, ignore_errors=True)


def cmd_record(args: argparse.Namespace) -> None:
    repo_root = find_repo_root(Path(__file__).resolve())
    build_dir = Path(tempfile.mkdtemp(prefix="test-ui-build-"))
    record_cwd = Path(tempfile.mkdtemp(prefix="test-ui-record-"))
    try:
        compile_sources(repo_root, build_dir)

        # A bare "RESTART" argument (case-insensitive) between commands means:
        # stop Steph and start a fresh process in the same directory. It prints
        # a "### Restart" line between the surrounding blocks, so the recorded
        # output can be pasted straight into a restart test case.
        segments: list[list[str]] = [[]]
        for command in args.commands:
            if command.strip().upper() == "RESTART":
                segments.append([])
            else:
                segments[-1].append(command)
        segments = [s for s in segments if s]

        recorded: list[list[tuple[str, str]]] = []
        for seg_commands in segments:
            result = run_session(build_dir, seg_commands, record_cwd)
            blocks = split_into_blocks(result.stdout)
            inner = blocks[1:-1] if len(blocks) >= 2 else []
            if len(inner) != len(seg_commands):
                print(f"warning: expected {len(seg_commands)} output blocks, got "
                      f"{len(inner)}. Raw stdout follows instead of per-command "
                      "blocks -- check for a crash or an early \"bye\".\n",
                      file=sys.stderr)
                print(result.stdout, file=sys.stderr)
                if result.stderr:
                    print(result.stderr, file=sys.stderr)
                raise SystemExit(1)
            recorded.append(list(zip(seg_commands, inner)))

        for seg_index, pairs in enumerate(recorded):
            if seg_index > 0:
                print("### Restart")
                print()
            for command, block in pairs:
                print("### Command")
                print("```")
                print(command)
                print("```")
                print()
                print("### Expected output")
                print("```")
                print(normalize(block))
                print("```")
                print()
    finally:
        shutil.rmtree(build_dir, ignore_errors=True)
        shutil.rmtree(record_cwd, ignore_errors=True)


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    subparsers = parser.add_subparsers(dest="mode")

    run_parser = subparsers.add_parser("run", help="run the test plan (default)")
    run_parser.add_argument("--plan", help="path to the test plan markdown file")
    run_parser.set_defaults(func=cmd_run)

    record_parser = subparsers.add_parser(
        "record",
        help="run a sequence of commands and print ready-to-paste Command/Expected output blocks",
    )
    record_parser.add_argument("commands", nargs="+", help="commands to send, in order")
    record_parser.set_defaults(func=cmd_record)

    # Default to "run" with no subcommand so `run_ui_tests.py` and
    # `run_ui_tests.py --plan foo.md` both work without typing "run" first.
    if len(sys.argv) > 1 and sys.argv[1] not in ("run", "record", "-h", "--help"):
        sys.argv.insert(1, "run")
    elif len(sys.argv) == 1:
        sys.argv.insert(1, "run")

    args = parser.parse_args()
    args.func(args)


if __name__ == "__main__":
    main()
