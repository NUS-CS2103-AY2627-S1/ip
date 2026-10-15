#!/usr/bin/env python3
"""Run text-UI test cases defined in test/ui-test-plan.md (fail-fast)."""

from __future__ import annotations

import argparse
import re
import subprocess
import sys
import tempfile
from dataclasses import dataclass
from pathlib import Path


@dataclass
class TestCase:
    case_id: str
    title: str
    aim: str
    inputs: str
    expected: str
    seed_file: str | None = None


CASE_HEADING = re.compile(r"^###\s+(?P<id>[^\s:]+)\s*:\s*(?P<title>.+?)\s*$", re.M)
FIELD = re.compile(
    r"\*\*(?P<name>Aim|Inputs|Expected output|Seed file):\*\*\s*\n```(?:\w+)?\n(?P<body>.*?)```",
    re.S,
)
# Aim may be a single line without a fence.
AIM_LINE = re.compile(r"\*\*Aim:\*\*\s*(?P<aim>.+?)\s*(?=\n\*\*|\n###|\Z)", re.S)


def repo_root() -> Path:
    return Path(__file__).resolve().parents[4]


def parse_plan(plan_text: str) -> list[TestCase]:
    matches = list(CASE_HEADING.finditer(plan_text))
    cases: list[TestCase] = []
    for index, match in enumerate(matches):
        start = match.end()
        end = matches[index + 1].start() if index + 1 < len(matches) else len(plan_text)
        body = plan_text[start:end]

        fields = {m.group("name"): m.group("body") for m in FIELD.finditer(body)}
        aim = fields.get("Aim")
        if aim is None:
            aim_match = AIM_LINE.search(body)
            aim = aim_match.group("aim").strip() if aim_match else ""
        else:
            aim = aim.strip()

        if "Inputs" not in fields or "Expected output" not in fields:
            raise ValueError(
                f"Test case {match.group('id')} must include Inputs and Expected output fenced blocks"
            )

        seed = fields.get("Seed file")
        cases.append(
            TestCase(
                case_id=match.group("id"),
                title=match.group("title").strip(),
                aim=aim,
                inputs=fields["Inputs"],
                expected=fields["Expected output"],
                seed_file=seed if seed is not None else None,
            )
        )
    return cases


def prepare_storage(root: Path, seed_file: str | None) -> None:
    """Reset ./data/kiwi.txt so each case starts from a known disk state."""
    data_dir = root / "data"
    save_path = data_dir / "kiwi.txt"
    if seed_file is None:
        if save_path.exists():
            save_path.unlink()
        return
    data_dir.mkdir(parents=True, exist_ok=True)
    text = seed_file.replace("\r\n", "\n").replace("\r", "\n")
    if text and not text.endswith("\n"):
        text += "\n"
    save_path.write_text(text, encoding="utf-8")


def normalize(text: str) -> str:
    text = text.replace("\r\n", "\n").replace("\r", "\n")
    if text and not text.endswith("\n"):
        text += "\n"
    return text


def read_plan_meta(plan_text: str) -> dict[str, str]:
    meta: dict[str, str] = {}
    for key in ("Main class", "Source directory", "Classpath output"):
        match = re.search(
            rf"^(?:- )?\*\*{re.escape(key)}:\*\*\s*`([^`]+)`",
            plan_text,
            re.M,
        )
        if match:
            meta[key] = match.group(1)
    return meta


def compile_program(root: Path, source_dir: str, out_dir: Path, main_class: str) -> None:
    sources = sorted((root / source_dir).rglob("*.java"))
    if not sources:
        raise RuntimeError(f"No Java sources found under {root / source_dir}")
    cmd = ["javac", "-d", str(out_dir), *[str(path) for path in sources]]
    result = subprocess.run(cmd, cwd=root, capture_output=True, text=True)
    if result.returncode != 0:
        raise RuntimeError(
            "Compilation failed:\n"
            + (result.stderr or result.stdout or "(no compiler output)")
        )
    # Touch main class existence lightly.
    class_file = out_dir / f"{main_class.replace('.', '/')}.class"
    if not class_file.exists():
        raise RuntimeError(f"Expected compiled class missing: {class_file}")


def run_program(root: Path, classpath: Path, main_class: str, stdin_text: str) -> str:
    result = subprocess.run(
        ["java", "-ea", "-cp", str(classpath), main_class],
        cwd=root,
        input=normalize(stdin_text),
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        err = result.stderr.strip() or f"exit code {result.returncode}"
        raise RuntimeError(f"Program exited with an error:\n{err}\n\nstdout:\n{result.stdout}")
    return result.stdout


def format_session(inputs: str, output: str) -> str:
    lines = ["=== Console session ===", "--- input ---"]
    for line in normalize(inputs).splitlines():
        lines.append(f"> {line}")
    lines.append("--- output ---")
    lines.append(normalize(output).rstrip("\n"))
    lines.append("=== end session ===")
    return "\n".join(lines)


def main(argv: list[str]) -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--plan",
        default="test/ui-test-plan.md",
        help="path to the UI test plan (relative to repo root)",
    )
    args = parser.parse_args(argv)

    root = repo_root()
    plan_path = root / args.plan
    if not plan_path.is_file():
        print(f"error: plan not found: {plan_path}", file=sys.stderr)
        return 2

    plan_text = plan_path.read_text(encoding="utf-8")
    meta = read_plan_meta(plan_text)
    main_class = meta.get("Main class", "Kiwi")
    source_dir = meta.get("Source directory", "src/main/java")

    try:
        cases = parse_plan(plan_text)
    except ValueError as exc:
        print(f"error: {exc}", file=sys.stderr)
        return 2

    if not cases:
        print("error: no test cases found in plan", file=sys.stderr)
        return 2

    with tempfile.TemporaryDirectory(prefix="ui-test-") as tmp:
        out_dir = Path(tmp)
        try:
            compile_program(root, source_dir, out_dir, main_class)
        except RuntimeError as exc:
            print(f"error: {exc}", file=sys.stderr)
            return 2

        passed = 0
        for case in cases:
            print(f"RUN {case.case_id}: {case.title}")
            print(f"Aim: {case.aim}")
            prepare_storage(root, case.seed_file)
            try:
                actual = run_program(root, out_dir, main_class, case.inputs)
            except RuntimeError as exc:
                print(format_session(case.inputs, str(exc)))
                print(f"FAIL {case.case_id}: program error")
                print(exc)
                return 1

            print(format_session(case.inputs, actual))

            if normalize(actual) != normalize(case.expected):
                print(f"FAIL {case.case_id}: output mismatch")
                print("--- expected ---")
                print(normalize(case.expected), end="")
                print("--- actual ---")
                print(normalize(actual), end="")
                print("Test session terminated on first failure.")
                return 1

            print(f"PASS {case.case_id}")
            print()
            passed += 1

    print(f"All {passed} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main(sys.argv[1:]))
