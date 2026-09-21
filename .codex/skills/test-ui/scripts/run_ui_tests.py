#!/usr/bin/env python3
"""Run Sheppy's scripted UI cases from a small JSON file."""

import json
import pathlib
import subprocess
import sys
import tempfile


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: run_ui_tests.py path/to/cases.json", file=sys.stderr)
        return 2

    root = pathlib.Path(__file__).resolve().parents[4]
    cases = json.loads(pathlib.Path(sys.argv[1]).read_text())
    subprocess.run([str(root / "gradlew"), "classes"], cwd=root, check=True)
    classes = root / "build/classes/java/main"

    for case in cases:
        with tempfile.TemporaryDirectory(prefix="sheppy-ui-") as case_directory:
            data_file = pathlib.Path(case_directory) / "data/tasks.txt"
            if "storage" in case:
                data_file.parent.mkdir(parents=True, exist_ok=True)
                data_file.write_text("\n".join(case["storage"]) + "\n", encoding="utf-8")
            input_text = "\n".join(case["input"]) + "\n"
            result = subprocess.run(
                ["java", "-cp", str(classes), "sheppy.Sheppy"],
                cwd=case_directory,
                input=input_text,
                text=True,
                capture_output=True,
                check=True,
                timeout=15,
            )
            print(f"--- {case['name']} ---")
            print(f"input: {input_text.rstrip()}")
            print(result.stdout, end="")
            missing = [line for line in case["expected"] if line not in result.stdout]
            if missing:
                print(f"FAIL: missing expected output: {missing}", file=sys.stderr)
                return 1
            print("PASS")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
