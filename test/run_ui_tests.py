#!/usr/bin/env python3
"""Run fail-fast command-by-command console UI tests from a Markdown plan."""

from __future__ import annotations

import argparse
import difflib
import os
from pathlib import Path
import re
import selectors
import subprocess
import sys
import time
from dataclasses import dataclass
from typing import IO, NoReturn, Optional, Sequence


CASE_HEADING = re.compile(
    r"^###\s+(?P<case_id>[A-Za-z0-9][A-Za-z0-9._-]*)"
    r"(?:\s*:\s*(?P<title>.+?))?\s*$"
)
COMMAND_HEADING = re.compile(r"^####\s+Command\s+(?P<number>[1-9][0-9]*)\s*$", re.I)
HEADING = re.compile(r"^(?P<marks>#{1,6})\s+(?P<title>.+?)\s*$")


class PlanError(ValueError):
    """Indicate that a test plan does not follow the required schema."""


class ResponseTimeout(TimeoutError):
    """Preserve partial process output when an interaction times out."""

    def __init__(self, message: str, stdout: str = "", stderr: str = "") -> None:
        """Create a timeout carrying stdout and stderr captured so far."""

        super().__init__(message)
        self.stdout = stdout
        self.stderr = stderr


@dataclass(frozen=True)
class CommandStep:
    """Represent one input command and its exact expected stdout response."""

    number: int
    input_text: str
    expected_output: str


@dataclass(frozen=True)
class TestCase:
    """Represent one isolated console process and its ordered commands."""

    case_id: str
    title: str
    aim: str
    commands: tuple[CommandStep, ...]


@dataclass(frozen=True)
class TestPlan:
    """Represent shared startup output and all console test cases."""

    expected_startup: str
    cases: tuple[TestCase, ...]


@dataclass(frozen=True)
class ConsoleEvent:
    """Represent one labelled item in an auditable console session."""

    stream: str
    text: str


@dataclass
class ProcessCapture:
    """Hold a process and its reusable nonblocking output selector."""

    process: subprocess.Popen[bytes]
    selector: selectors.BaseSelector
    stdout: IO[bytes]
    stderr: IO[bytes]


def positive_float(value: str) -> float:
    """Return a positive float suitable for timeout arguments."""

    number = float(value)
    if number <= 0:
        raise argparse.ArgumentTypeError("must be greater than zero")
    return number


def parse_args(argv: Sequence[str]) -> argparse.Namespace:
    """Parse runner arguments and the application command after ``--``."""

    parser = argparse.ArgumentParser(
        description="Run exact, fail-fast console UI tests from a Markdown plan."
    )
    parser.add_argument("--plan", type=Path, required=True, help="Markdown test plan")
    parser.add_argument(
        "--cwd",
        type=Path,
        default=Path.cwd(),
        help="working directory for the tested program (default: current directory)",
    )
    parser.add_argument(
        "--idle-timeout",
        type=positive_float,
        default=0.25,
        help="quiet period that ends one response (default: 0.25 seconds)",
    )
    parser.add_argument(
        "--command-timeout",
        type=positive_float,
        default=10.0,
        help="maximum wait for each startup/command response (default: 10 seconds)",
    )
    parser.add_argument(
        "program",
        nargs=argparse.REMAINDER,
        help="application command and arguments, preceded by --",
    )
    args = parser.parse_args(argv)
    if args.program and args.program[0] == "--":
        args.program = args.program[1:]
    if not args.program:
        parser.error("provide the application command after --")
    return args


def parse_plan(path: Path) -> TestPlan:
    """Parse the documented Markdown schema into immutable test objects."""

    try:
        lines = path.read_text(encoding="utf-8").splitlines()
    except OSError as error:
        raise PlanError(f"cannot read {path}: {error}") from error

    find_heading_body(lines, 1, "UI Test Plan")
    find_heading_body(lines, 2, "Test configuration")
    startup_range = find_heading_body(lines, 2, "Expected startup output")
    expected_startup = parse_fenced_block(
        lines[slice(*startup_range)], "Expected startup output", allow_multiline=True
    )

    test_cases_range = find_heading_body(lines, 2, "Test cases")
    structural_lines = markdown_structural_lines(lines)
    case_starts = [
        index
        for index in range(test_cases_range[0], test_cases_range[1])
        if structural_lines[index]
        and lines[index].startswith("### ")
        and CASE_HEADING.fullmatch(lines[index])
    ]
    if not case_starts:
        raise PlanError(
            "no test cases found; expected headings such as '### TC-001: Title'"
        )

    cases: list[TestCase] = []
    for position, start in enumerate(case_starts):
        end = (
            case_starts[position + 1]
            if position + 1 < len(case_starts)
            else test_cases_range[1]
        )
        cases.append(parse_case(lines[start:end]))

    duplicate_ids = sorted(
        case_id
        for case_id in {case.case_id for case in cases}
        if sum(case.case_id == case_id for case in cases) > 1
    )
    if duplicate_ids:
        raise PlanError(f"duplicate test case IDs: {', '.join(duplicate_ids)}")
    return TestPlan(expected_startup=expected_startup, cases=tuple(cases))


def parse_case(lines: list[str]) -> TestCase:
    """Parse one level-three test-case section."""

    match = CASE_HEADING.fullmatch(lines[0])
    if match is None:
        raise PlanError(f"invalid test case heading: {lines[0]}")
    case_id = match.group("case_id")
    title = (match.group("title") or "Untitled test").strip()

    aim_range = find_heading_body(lines, 4, "Aim")
    aim = "\n".join(line.rstrip() for line in lines[slice(*aim_range)]).strip()
    if not aim:
        raise PlanError(f"{case_id}: Aim must not be empty")

    command_starts: list[tuple[int, int]] = []
    structural_lines = markdown_structural_lines(lines)
    for index, line in enumerate(lines):
        command_match = (
            COMMAND_HEADING.fullmatch(line) if structural_lines[index] else None
        )
        if command_match:
            command_starts.append((index, int(command_match.group("number"))))
    if not command_starts:
        raise PlanError(f"{case_id}: no '#### Command N' sections found")

    commands: list[CommandStep] = []
    for position, (start, number) in enumerate(command_starts):
        end = (
            command_starts[position + 1][0]
            if position + 1 < len(command_starts)
            else len(lines)
        )
        section = lines[start:end]
        input_range = find_heading_body(section, 5, "Input")
        output_range = find_heading_body(section, 5, "Expected output")
        input_text = parse_fenced_block(
            section[slice(*input_range)],
            f"{case_id} command {number} Input",
            allow_multiline=False,
        )
        expected_output = parse_fenced_block(
            section[slice(*output_range)],
            f"{case_id} command {number} Expected output",
            allow_multiline=True,
        )
        commands.append(CommandStep(number, input_text, expected_output))

    actual_numbers = [command.number for command in commands]
    expected_numbers = list(range(1, len(commands) + 1))
    if actual_numbers != expected_numbers:
        raise PlanError(
            f"{case_id}: command headings must be sequential from 1; "
            f"found {actual_numbers}"
        )
    return TestCase(case_id, title, aim, tuple(commands))


def find_heading_body(lines: list[str], level: int, title: str) -> tuple[int, int]:
    """Return the body range of one uniquely named Markdown heading."""

    matches: list[int] = []
    structural_lines = markdown_structural_lines(lines)
    for index, line in enumerate(lines):
        heading = HEADING.fullmatch(line) if structural_lines[index] else None
        if (
            heading
            and len(heading.group("marks")) == level
            and heading.group("title").casefold() == title.casefold()
        ):
            matches.append(index)
    if not matches:
        raise PlanError(f"missing {'#' * level} {title} heading")
    if len(matches) > 1:
        raise PlanError(f"duplicate {'#' * level} {title} heading")

    start = matches[0] + 1
    end = len(lines)
    for index in range(start, len(lines)):
        heading = HEADING.fullmatch(lines[index]) if structural_lines[index] else None
        if heading and len(heading.group("marks")) <= level:
            end = index
            break
    return start, end


def markdown_structural_lines(lines: list[str]) -> list[bool]:
    """Mark lines outside fenced blocks so console text is never parsed as headings."""

    structural: list[bool] = []
    fence_character: Optional[str] = None
    fence_length = 0
    for line in lines:
        stripped = line.lstrip()
        if fence_character is not None:
            structural.append(False)
            closing = re.fullmatch(
                rf"{re.escape(fence_character)}{{{fence_length},}}\s*", stripped
            )
            if closing:
                fence_character = None
                fence_length = 0
            continue

        opening = re.match(r"(?P<fence>`{3,}|~{3,})", stripped)
        if opening:
            fence = opening.group("fence")
            fence_character = fence[0]
            fence_length = len(fence)
            structural.append(False)
        else:
            structural.append(True)
    return structural


def parse_fenced_block(lines: list[str], label: str, allow_multiline: bool) -> str:
    """Extract one exact text block and restore its final newline."""

    nonblank = [index for index, line in enumerate(lines) if line.strip()]
    if not nonblank:
        raise PlanError(f"{label}: missing fenced code block")
    opening_index = nonblank[0]
    opening = lines[opening_index].strip()
    fence_match = re.fullmatch(r"(?P<fence>`{3,}|~{3,})[^`~]*", opening)
    if fence_match is None:
        raise PlanError(f"{label}: content must start with a fenced code block")
    fence = fence_match.group("fence")

    closing_index: Optional[int] = None
    for index in range(opening_index + 1, len(lines)):
        if lines[index].strip() == fence:
            closing_index = index
            break
    if closing_index is None:
        raise PlanError(f"{label}: fenced code block is not closed")
    if any(line.strip() for line in lines[closing_index + 1 :]):
        raise PlanError(f"{label}: unexpected content after fenced code block")

    content_lines = lines[opening_index + 1 : closing_index]
    if not allow_multiline and len(content_lines) > 1:
        raise PlanError(f"{label}: input must contain exactly one command line")
    if not content_lines:
        return ""
    return "\n".join(content_lines) + "\n"


def start_process(command: Sequence[str], cwd: Path) -> ProcessCapture:
    """Start a console process with nonblocking stdout and stderr pipes."""

    process = subprocess.Popen(
        command,
        cwd=cwd,
        stdin=subprocess.PIPE,
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
    )
    if process.stdout is None or process.stderr is None:
        process.kill()
        raise RuntimeError("failed to create application output pipes")

    os.set_blocking(process.stdout.fileno(), False)
    os.set_blocking(process.stderr.fileno(), False)
    selector = selectors.DefaultSelector()
    selector.register(process.stdout, selectors.EVENT_READ, "stdout")
    selector.register(process.stderr, selectors.EVENT_READ, "stderr")
    return ProcessCapture(process, selector, process.stdout, process.stderr)


def read_response(
    capture: ProcessCapture,
    idle_timeout: float,
    command_timeout: float,
    expect_output: bool,
) -> tuple[str, str]:
    """Read stdout/stderr until both streams are quiet for the idle period."""

    stdout_chunks: list[bytes] = []
    stderr_chunks: list[bytes] = []
    started = time.monotonic()
    last_activity: Optional[float] = None

    while True:
        now = time.monotonic()
        elapsed = now - started
        if elapsed >= command_timeout:
            raise ResponseTimeout(
                f"response exceeded {command_timeout:g} seconds",
                decode_output(stdout_chunks),
                decode_output(stderr_chunks),
            )
        if not capture.selector.get_map():
            break

        if last_activity is None:
            if not expect_output and elapsed >= idle_timeout:
                break
            response_wait = (
                command_timeout - elapsed if expect_output else idle_timeout - elapsed
            )
        else:
            quiet = now - last_activity
            if quiet >= idle_timeout:
                break
            response_wait = idle_timeout - quiet

        wait = min(response_wait, command_timeout - elapsed)
        stdout, stderr = read_ready_streams(capture, wait)
        if stdout or stderr:
            last_activity = time.monotonic()
            stdout_chunks.extend(stdout)
            stderr_chunks.extend(stderr)

        if capture.process.poll() is not None and not capture.selector.get_map():
            break

    return decode_output(stdout_chunks), decode_output(stderr_chunks)


def read_ready_streams(
    capture: ProcessCapture, timeout: float
) -> tuple[list[bytes], list[bytes]]:
    """Read all process-pipe chunks that become ready within one wait."""

    stdout_chunks: list[bytes] = []
    stderr_chunks: list[bytes] = []
    for key, _ in capture.selector.select(max(0.0, timeout)):
        try:
            data = os.read(key.fd, 65_536)
        except BlockingIOError:
            continue
        if not data:
            try:
                capture.selector.unregister(key.fileobj)
            except KeyError:
                pass
            continue
        if key.data == "stdout":
            stdout_chunks.append(data)
        else:
            stderr_chunks.append(data)
    return stdout_chunks, stderr_chunks


def decode_output(chunks: list[bytes]) -> str:
    """Decode subprocess bytes and normalize platform line endings."""

    return normalize_newlines(b"".join(chunks).decode("utf-8", errors="replace"))


def normalize_newlines(text: str) -> str:
    """Normalize Windows and classic Mac line endings to LF."""

    return text.replace("\r\n", "\n").replace("\r", "\n")


def stop_process(capture: ProcessCapture) -> None:
    """Terminate only the test process and release its local resources."""

    process = capture.process
    if process.poll() is None:
        process.terminate()
        try:
            process.wait(timeout=1)
        except subprocess.TimeoutExpired:
            process.kill()
            process.wait(timeout=1)
    capture.selector.close()
    if process.stdin is not None and not process.stdin.closed:
        process.stdin.close()
    capture.stdout.close()
    capture.stderr.close()


def finish_process(
    capture: ProcessCapture, command_timeout: float
) -> tuple[int, str, str]:
    """Wait for natural exit while draining and preserving trailing output."""

    process = capture.process
    stdout_chunks: list[bytes] = []
    stderr_chunks: list[bytes] = []
    started = time.monotonic()

    while process.poll() is None:
        elapsed = time.monotonic() - started
        if elapsed >= command_timeout:
            raise ResponseTimeout(
                f"process did not exit within {command_timeout:g} seconds "
                "after the final command",
                decode_output(stdout_chunks),
                decode_output(stderr_chunks),
            )
        stdout, stderr = read_ready_streams(
            capture, min(0.05, command_timeout - elapsed)
        )
        stdout_chunks.extend(stdout)
        stderr_chunks.extend(stderr)

    while capture.selector.get_map():
        stdout, stderr = read_ready_streams(capture, 0.01)
        stdout_chunks.extend(stdout)
        stderr_chunks.extend(stderr)
        if not stdout and not stderr:
            break

    return (
        process.returncode,
        decode_output(stdout_chunks),
        decode_output(stderr_chunks),
    )


def print_output(text: str) -> None:
    """Print captured output without adding a second trailing newline."""

    if text:
        print(text, end="" if text.endswith("\n") else "\n")
    else:
        print("<empty>")


def print_session(
    test_case: TestCase,
    events: list[ConsoleEvent],
    status: str,
    include_startup: bool,
) -> None:
    """Print one compact but complete command/response session."""

    print(f"\n[{status}] {test_case.case_id} - {test_case.title}")
    print(f"Aim: {test_case.aim}")
    for event in events:
        if event.stream == "stdout:startup" and not include_startup:
            continue
        if event.stream.startswith("stdin:command"):
            command = event.text.removesuffix("\n")
            print(f"$ {command}" if command else "$ <empty>")
        elif event.stream == "stdout:startup":
            print("[startup stdout]")
            print_output(event.text)
        elif event.stream.startswith("stdout:command"):
            print_output(event.text)
        else:
            print(f"[{event.stream}]")
            print_output(event.text)


def fail_case(
    test_case: TestCase,
    events: list[ConsoleEvent],
    point: str,
    actual: str,
    expected: str,
    reason: str,
) -> bool:
    """Print a complete failure record and return ``False`` for fail-fast flow."""

    print_session(test_case, events, "FAIL", include_startup=True)
    print(f"Failure point: {point}")
    print(f"Reason: {reason}")
    print("--- Actual output ---")
    if actual:
        print(actual, end="" if actual.endswith("\n") else "\n")
    else:
        print("<empty>")
    print("--- Expected output ---")
    if expected:
        print(expected, end="" if expected.endswith("\n") else "\n")
    else:
        print("<empty>")
    print("--- Unified diff (expected -> actual) ---")
    diff = difflib.unified_diff(
        expected.splitlines(keepends=True),
        actual.splitlines(keepends=True),
        fromfile="expected",
        tofile="actual",
    )
    rendered_diff = "".join(diff)
    if rendered_diff:
        print(rendered_diff, end="" if rendered_diff.endswith("\n") else "\n")
    else:
        print("<no stdout diff; see failure reason>")
    print(f"STOPPED: {test_case.case_id} failed; later test cases were not run.")
    return False


def timeout_reason(error: ResponseTimeout) -> str:
    """Describe a timeout and include any partial stderr in the reason."""

    if error.stderr:
        return f"{error}; partial stderr: {error.stderr!r}"
    return str(error)


def run_case(
    test_case: TestCase,
    expected_startup: str,
    command: Sequence[str],
    cwd: Path,
    idle_timeout: float,
    command_timeout: float,
) -> bool:
    """Run one isolated case and stop at its first failing interaction."""

    events: list[ConsoleEvent] = []
    capture = start_process(command, cwd)
    try:
        try:
            startup, startup_stderr = read_response(
                capture,
                idle_timeout,
                command_timeout,
                expect_output=bool(expected_startup),
            )
        except ResponseTimeout as error:
            events.append(ConsoleEvent("stdout:startup", error.stdout))
            if error.stderr:
                events.append(ConsoleEvent("stderr:startup", error.stderr))
            return fail_case(
                test_case,
                events,
                "startup",
                error.stdout,
                expected_startup,
                timeout_reason(error),
            )
        events.append(ConsoleEvent("stdout:startup", startup))
        if startup_stderr:
            events.append(ConsoleEvent("stderr:startup", startup_stderr))
            return fail_case(
                test_case,
                events,
                "startup",
                startup,
                expected_startup,
                f"unexpected stderr: {startup_stderr!r}",
            )
        if startup != expected_startup:
            return fail_case(
                test_case,
                events,
                "startup",
                startup,
                expected_startup,
                "stdout did not match exactly",
            )

        for step in test_case.commands:
            if capture.process.poll() is not None:
                return fail_case(
                    test_case,
                    events,
                    f"command {step.number}",
                    "",
                    step.expected_output,
                    "application exited before the command was sent",
                )
            input_line = step.input_text.removesuffix("\n")
            events.append(
                ConsoleEvent(f"stdin:command {step.number}", input_line + "\n")
            )
            if capture.process.stdin is None:
                return fail_case(
                    test_case,
                    events,
                    f"command {step.number}",
                    "",
                    step.expected_output,
                    "application stdin is unavailable",
                )
            try:
                capture.process.stdin.write((input_line + "\n").encode("utf-8"))
                capture.process.stdin.flush()
            except BrokenPipeError:
                return fail_case(
                    test_case,
                    events,
                    f"command {step.number}",
                    "",
                    step.expected_output,
                    "application closed stdin before the command was sent",
                )

            try:
                actual, stderr = read_response(
                    capture,
                    idle_timeout,
                    command_timeout,
                    expect_output=bool(step.expected_output),
                )
            except ResponseTimeout as error:
                events.append(
                    ConsoleEvent(f"stdout:command {step.number}", error.stdout)
                )
                if error.stderr:
                    events.append(
                        ConsoleEvent(f"stderr:command {step.number}", error.stderr)
                    )
                return fail_case(
                    test_case,
                    events,
                    f"command {step.number}",
                    error.stdout,
                    step.expected_output,
                    timeout_reason(error),
                )
            events.append(ConsoleEvent(f"stdout:command {step.number}", actual))
            if stderr:
                events.append(ConsoleEvent(f"stderr:command {step.number}", stderr))
                return fail_case(
                    test_case,
                    events,
                    f"command {step.number}",
                    actual,
                    step.expected_output,
                    f"unexpected stderr: {stderr!r}",
                )
            if actual != step.expected_output:
                return fail_case(
                    test_case,
                    events,
                    f"command {step.number}",
                    actual,
                    step.expected_output,
                    "stdout did not match exactly",
                )

        try:
            return_code, trailing_stdout, trailing_stderr = finish_process(
                capture, command_timeout
            )
        except ResponseTimeout as error:
            if error.stdout:
                events.append(ConsoleEvent("stdout:after final command", error.stdout))
            if error.stderr:
                events.append(ConsoleEvent("stderr:after final command", error.stderr))
            return fail_case(
                test_case,
                events,
                "shutdown",
                error.stdout,
                "",
                timeout_reason(error),
            )
        if trailing_stdout:
            events.append(ConsoleEvent("stdout:after final command", trailing_stdout))
        if trailing_stderr:
            events.append(ConsoleEvent("stderr:after final command", trailing_stderr))
        if trailing_stdout or trailing_stderr or return_code != 0:
            reason_parts = []
            if trailing_stdout:
                reason_parts.append("unexpected trailing stdout")
            if trailing_stderr:
                reason_parts.append(f"unexpected trailing stderr: {trailing_stderr!r}")
            if return_code != 0:
                reason_parts.append(f"application exited with status {return_code}")
            return fail_case(
                test_case,
                events,
                "shutdown",
                trailing_stdout,
                "",
                "; ".join(reason_parts),
            )

        print_session(test_case, events, "PASS", include_startup=False)
        return True
    finally:
        stop_process(capture)


def fatal(message: str, exit_code: int = 2) -> NoReturn:
    """Print a runner-level error and exit without a traceback."""

    print(f"ERROR: {message}", file=sys.stderr)
    raise SystemExit(exit_code)


def main(argv: Optional[Sequence[str]] = None) -> int:
    """Run all test cases until completion or the first failure."""

    args = parse_args(sys.argv[1:] if argv is None else argv)
    try:
        plan = parse_plan(args.plan)
    except PlanError as error:
        fatal(f"invalid test plan: {error}")
    if not args.cwd.is_dir():
        fatal(f"working directory does not exist: {args.cwd}")

    print(f"Test plan: {args.plan}")
    print(f"Program: {' '.join(args.program)}")
    command_count = sum(len(test_case.commands) for test_case in plan.cases)
    print(f"Coverage: {len(plan.cases)} cases, {command_count} commands")
    print("Shared startup output (checked in every fresh process):")
    print_output(plan.expected_startup)
    passed = 0
    for test_case in plan.cases:
        try:
            succeeded = run_case(
                test_case,
                plan.expected_startup,
                args.program,
                args.cwd,
                args.idle_timeout,
                args.command_timeout,
            )
        except FileNotFoundError as error:
            fatal(f"cannot start application: {error}")
        except OSError as error:
            fatal(f"application process error: {error}")
        if not succeeded:
            remaining = len(plan.cases) - passed - 1
            print(
                f"\nResult: FAIL - {passed} passed, 1 failed, "
                f"{remaining} skipped"
            )
            return 1
        passed += 1

    print(
        f"\nResult: PASS - {passed}/{len(plan.cases)} cases, "
        f"{command_count}/{command_count} commands"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
