#!/usr/bin/env python3
"""Verify that Harold persists tasks across application runs."""

from __future__ import annotations

import subprocess
import tempfile
from pathlib import Path


PROJECT_ROOT = Path(__file__).resolve().parents[1]
SOURCE_ROOT = PROJECT_ROOT / "src" / "main" / "java"
SOURCES = sorted(
    source
    for source in SOURCE_ROOT.rglob("*.java")
    if "gui" not in source.relative_to(SOURCE_ROOT).parts
)


def run_harold(build_directory: Path, working_directory: Path, commands: list[str]) -> str:
    """Run Harold with the supplied commands and return its console output."""
    result = subprocess.run(
        ["java", "-cp", str(build_directory), "harold.Harold"],
        cwd=working_directory,
        input="\n".join(commands) + "\n",
        capture_output=True,
        text=True,
        timeout=10,
        check=False,
    )
    if result.returncode != 0:
        raise AssertionError(
            f"Harold exited with status {result.returncode}:\n{result.stderr}"
        )
    return result.stdout


def require_text(output: str, expected: str) -> None:
    """Fail the test when expected text is absent from the output."""
    if expected not in output:
        raise AssertionError(f"Expected output was not found:\n{expected}\n\nActual:\n{output}")


def main() -> None:
    """Compile Harold and test saving, loading, deletion, and corrupt data handling."""
    with tempfile.TemporaryDirectory(prefix="harold-storage-test-") as temporary_directory:
        temporary_path = Path(temporary_directory)
        build_directory = temporary_path / "build"
        working_directory = temporary_path / "workspace"
        build_directory.mkdir()
        working_directory.mkdir()

        subprocess.run(
            ["javac", "-d", str(build_directory), *map(str, SOURCES)],
            cwd=PROJECT_ROOT,
            check=True,
        )

        run_harold(
            build_directory,
            working_directory,
            [
                "todo read | save notes",
                "deadline submit work /by 2026-08-28",
                "event meeting /from 2026-08-31 /to 2026-09-01",
                "mark 1",
                "bye",
            ],
        )

        data_file = working_directory / "data" / "harold.txt"
        if not data_file.is_file():
            raise AssertionError("Harold did not create data/harold.txt")

        loaded_output = run_harold(
            build_directory,
            working_directory,
            ["list", "delete 2", "bye"],
        )
        require_text(loaded_output, "1.[T][X] read | save notes")
        require_text(loaded_output, "2.[D][ ] submit work (by: Aug 28 2026)")
        require_text(
            loaded_output,
            "3.[E][ ] meeting (from: Aug 31 2026 to: Sep 1 2026)",
        )

        reloaded_output = run_harold(
            build_directory,
            working_directory,
            ["list", "bye"],
        )
        require_text(reloaded_output, "1.[T][X] read | save notes")
        require_text(
            reloaded_output,
            "2.[E][ ] meeting (from: Aug 31 2026 to: Sep 1 2026)",
        )
        if "submit work" in reloaded_output:
            raise AssertionError("Deleted deadline reappeared after restarting Harold")

        with data_file.open("a", encoding="utf-8") as data_stream:
            data_stream.write("corrupted task record\n")
        corrupted_output = run_harold(
            build_directory,
            working_directory,
            ["list", "bye"],
        )
        require_text(
            corrupted_output,
            "OOPS!!! I skipped 1 invalid task record(s) while loading your data.",
        )
        require_text(corrupted_output, "1.[T][X] read | save notes")

    print("Storage persistence tests passed.")


if __name__ == "__main__":
    main()
