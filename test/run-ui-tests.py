"""Runs every console test in ui-test-plan.md and records exact output."""

import os
from pathlib import Path
import re
import subprocess
import tempfile

ROOT = Path(__file__).resolve().parents[1]
PLAN = ROOT / "test/ui-test-plan.md"
REPORT = ROOT / "test/ui-test-session.md"


def block(case, label):
    """Returns the literal fenced block following a case label."""
    return re.search(r"\*\*" + re.escape(label) + r":\*\*\n\n```[^\n]*\n(.*?)```", case, re.S)[1]


def main():
    """Compiles with Java 25, runs every case, and writes the session record."""
    version = subprocess.run(["java", "-version"], capture_output=True, text=True, check=True)
    if not re.search(r'version "25[.\"]', version.stderr):
        raise SystemExit("Java 25 is required.")
    cases = PLAN.read_text().split("## Test case: ")[1:]
    failures = []
    records = ["# Complete console UI test session\n\n```text\n" + version.stderr + "```\n"]
    with tempfile.TemporaryDirectory(prefix="minerva-ui-") as temporary:
        directory = Path(temporary)
        classes = directory / "classes"
        classes.mkdir()
        sources = [str(path) for path in (ROOT / "src/main/java").rglob("*.java")
                   if "gui" not in path.parts]
        subprocess.run(["javac", "-d", str(classes), *sources], check=True)
        for number, case in enumerate(cases, 1):
            name = case.splitlines()[0]
            working = directory / str(number)
            saved = working / "data/minerva.txt"
            saved.parent.mkdir(parents=True)
            if "**Storage mode:** directory" in case:
                saved.mkdir()
            else:
                saved.write_text(block(case, "Storage fixture"))
            command = block(case, "Command")
            inputs = block(case, "Console input")
            expected = block(case, "Expected console output")
            environment = dict(os.environ, MINERVA_CLASSES=str(classes))
            result = subprocess.run(command, shell=True, cwd=working, env=environment,
                                    input=inputs.encode(), capture_output=True)
            actual = result.stdout.decode()
            errors = result.stderr.decode()
            passed = result.returncode == 0 and result.stdout == expected.encode() and not result.stderr
            if not passed:
                failures.append(name)
            aim = re.search(r"\*\*Aim:\*\* (.*)", case)[1]
            records.append(f"## {number}. {name}: {'PASS' if passed else 'FAIL'}\n\n"
                           f"Aim: {aim}\n\nCommand:\n```bash\n{command}```\n\n"
                           f"Console input:\n```text\n{inputs}```\n\n"
                           f"Captured stdout:\n```text\n{actual}```\n\n"
                           f"Captured stderr:\n```text\n{errors}```\n\n"
                           f"Exit status: {result.returncode}\n")
            if not passed:
                records.append(f"Expected stdout:\n```text\n{expected}```\n")
            print(f"{'PASS' if passed else 'FAIL'}: {name}")
    records.append(f"## Failures\n\n" + ("\n".join(f"- {name}" for name in failures)
                   if failures else "None.") + f"\n\n{len(cases) - len(failures)}/{len(cases)} cases passed.\n")
    REPORT.write_text("\n".join(records))
    raise SystemExit(bool(failures))


if __name__ == "__main__":
    main()
