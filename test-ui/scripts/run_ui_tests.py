"""Run markdown-defined console UI tests and stop at the first failure."""

from __future__ import annotations

import re
import subprocess
import sys
from pathlib import Path


def extract(text: str, label: str) -> str:
    match = re.search(rf"^### {re.escape(label)}\s*\n(.*?)(?=^### |^## |\Z)", text, re.M | re.S)
    if not match:
        raise ValueError(f"missing ### {label}")
    body = match.group(1).strip("\n")
    fenced = re.search(r"```(?:\w+)?\s*\n(.*?)```", body, re.S)
    return (fenced.group(1) if fenced else body).rstrip("\n")


def parse(plan: str) -> list[tuple[str, str, str, str, str]]:
    sections = list(re.finditer(r"^## (.+?)\s*$", plan, re.M))
    cases = []
    for index, section in enumerate(sections):
        end = sections[index + 1].start() if index + 1 < len(sections) else len(plan)
        block = plan[section.end():end]
        cases.append((section.group(1), extract(block, "Aim"), extract(block, "Inputs"),
                      extract(block, "Command"), extract(block, "Expected output")))
    return cases


def normalize(value: str) -> str:
    return value.replace("\r\n", "\n").rstrip("\n")


def main() -> int:
    plan_path = Path(sys.argv[1]) if len(sys.argv) > 1 else Path("test/ui-test-plan.md")
    cases = parse(plan_path.read_text(encoding="utf-8"))
    if not cases:
        print(f"No test cases found in {plan_path}.")
        return 1
    for number, (name, aim, inputs, command, expected) in enumerate(cases, 1):
        # Each case is isolated; persistence across two launches is tested separately.
        saved_tasks = Path("data/victoria.txt")
        if saved_tasks.exists():
            saved_tasks.unlink()
        if name == "Load saved tasks":
            saved_tasks.parent.mkdir(parents=True, exist_ok=True)
            saved_tasks.write_text("T|1|cGVyc2lzdGVudCB0YXNr\n", encoding="utf-8")
        if name == "Ignore corrupted records":
            saved_tasks.parent.mkdir(parents=True, exist_ok=True)
            saved_tasks.write_text("not a task\nD|0|bad-base64|!!!\nT|0|dmFsaWQgdGFza3M=\n",
                                   encoding="utf-8")
        print(f"\n=== Test {number}: {name} ===\nAim: {aim}\n$ {command}")
        print(f"--- console input ---\n{inputs if inputs else '(none)'}\n--- console output ---")
        result = subprocess.run(command, input=inputs + ("\n" if inputs else ""), text=True,
                                capture_output=True, shell=True)
        actual = result.stdout + result.stderr
        print(actual, end="" if actual.endswith("\n") else "\n")
        if normalize(actual) != normalize(expected):
            print("--- FAILED ---")
            print(f"Expected:\n{expected}\nActual:\n{actual}")
            return 1
        if name == "ToDo and list":
            saved_tasks = Path("data/victoria.txt")
            if not saved_tasks.exists() or not saved_tasks.read_text(encoding="utf-8").strip():
                print("--- FAILED ---")
                print("Expected a non-empty data/victoria.txt after adding a task.")
                return 1
        print("--- PASSED ---")
    print(f"\nAll {len(cases)} UI test(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
