"""Run Baymax's console interface from the Shadow JAR in isolation."""

from __future__ import annotations

import argparse
import os
from pathlib import Path
import subprocess
import sys
import tempfile


def main() -> int:
    """Build the Shadow JAR, then run the console interface with clean storage."""

    repo_root = Path(__file__).resolve().parents[1]
    parser = argparse.ArgumentParser(description=__doc__)
    storage_options = parser.add_mutually_exclusive_group()
    storage_options.add_argument("--blank-storage", action="store_true")
    storage_options.add_argument("--corrupt-storage", action="store_true")
    storage_options.add_argument("--bom-storage", action="store_true")
    arguments = parser.parse_args()
    wrapper_name = "gradlew.bat" if os.name == "nt" else "gradlew"

    gradle_wrapper = repo_root / wrapper_name
    build_result = subprocess.run(
        [str(gradle_wrapper), "--quiet", "shadowJar"],
        cwd=repo_root,
        stdin=subprocess.DEVNULL,
        check=False,
    )
    if build_result.returncode != 0:
        return build_result.returncode

    shadow_jar = repo_root / "build" / "libs" / "baymax.jar"
    sys.stdin.reconfigure(encoding="utf-8")
    console_input = sys.stdin.read()
    with tempfile.TemporaryDirectory(prefix="baymax-ui-test-") as work_dir:
        if arguments.blank_storage:
            data_file = Path(work_dir) / "data" / "Baymax.txt"
            data_file.parent.mkdir()
            data_file.write_bytes(b"\r\n \t\r\n")
        if arguments.corrupt_storage:
            data_file = Path(work_dir) / "data" / "Baymax.txt"
            data_file.parent.mkdir()
            data_file.write_bytes(b"T | 0 | existing\ninvalid record\n")
        if arguments.bom_storage:
            data_file = Path(work_dir) / "data" / "Baymax.txt"
            data_file.parent.mkdir()
            data_file.write_bytes(b"\xef\xbb\xbfT | 0 | existing\n")
        app_result = subprocess.run(
            ["java", "-ea", "-cp", str(shadow_jar), "baymax.Baymax"],
            cwd=work_dir,
            input=console_input,
            text=True,
            encoding="utf-8",
            check=False,
        )
        return app_result.returncode


if __name__ == "__main__":
    raise SystemExit(main())
