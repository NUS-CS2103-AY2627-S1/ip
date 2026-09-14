#!/usr/bin/env python3
"""Run both recorded console plans without reading or changing the user's data."""

from pathlib import Path
import os
import shutil
import subprocess
import sys
import tempfile


def main() -> int:
    """Stage each plan with its own fixture and preserve the complete session."""
    repo = Path(__file__).resolve().parents[1]
    runner = repo / ".agents/skills/test-ui/scripts/run_ui_tests.py"
    if not runner.is_file():
        print(f"Required test-ui skill runner is missing: {runner}", file=sys.stderr)
        return 1
    environment = os.environ.copy()
    environment["JDK_JAVA_OPTIONS"] = environment.get("JDK_JAVA_OPTIONS", "") + " -ea"
    output_directory = repo / "_temp"
    output_directory.mkdir(exist_ok=True)
    sessions = []
    plans = [
        ("core", "ui-test-plan.md", None),
        ("storage", "storage-ui-test-plan.md", "D | 0 | broken | invalid\n"),
    ]
    for name, plan, fixture in plans:
        with tempfile.TemporaryDirectory(prefix="groot-ui-tests-") as root:
            staged = Path(root)
            shutil.copytree(repo / "src", staged / "src")
            shutil.copytree(repo / "test", staged / "test")
            if fixture is not None:
                (staged / "data").mkdir()
                (staged / "data/groot.txt").write_text(fixture, encoding="utf-8")
                # Storage messages use the operating system's native path separator.
                plan_path = staged / "test" / plan
                content = plan_path.read_text(encoding="utf-8")
                content = content.replace("in data/groot.txt is invalid", f"in {Path('data', 'groot.txt')} is invalid")
                plan_path.write_text(content, encoding="utf-8")
            output = output_directory / f"ui-test-session-{name}.txt"
            result = subprocess.run(
                [sys.executable, str(runner), "--repo", root, "--plan", f"test/{plan}",
                 "--session-output", str(output)],
                env=environment, capture_output=True, text=True, check=False,
            )
            print(result.stdout, end="")
            if result.stderr:
                print(result.stderr, file=sys.stderr, end="")
            sessions.append(output.read_text(encoding="utf-8") if output.exists() else result.stdout)
            (output_directory / "ui-test-session.txt").write_text("\n".join(sessions), encoding="utf-8")
            if result.returncode:
                return result.returncode
            if fixture is not None and (staged / "data/groot.txt").read_text(encoding="utf-8") != fixture:
                failure = "FAILED: startup changed the malformed saved file.\n"
                print(failure, file=sys.stderr, end="")
                with (output_directory / "ui-test-session.txt").open("a", encoding="utf-8") as record:
                    record.write(failure)
                return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
