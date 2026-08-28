#!/usr/bin/env python3
"""Compile and run the dependency-free Java test classes using the active JDK.

Select Java 25.0.3.fx-zulu before invoking this runner. All compiled files go
into an automatically cleaned temporary directory chosen by the operating system.
"""

from pathlib import Path
import subprocess
import tempfile


def main():
    """Run each *Test.java main method, stopping if compilation or any test fails."""
    repo = Path(__file__).resolve().parents[1]
    sources = sorted((repo / "src/main/java").glob("*.java"))
    tests = sorted((repo / "test").glob("*Test.java"))
    if not tests:
        raise SystemExit("No Java test classes found.")
    with tempfile.TemporaryDirectory(prefix="friday-unit-") as temporary:
        build = Path(temporary) / "classes"
        subprocess.run(
            ["javac", "-d", str(build)] + [str(path) for path in sources + tests],
            check=True,
            cwd=repo,
        )
        for test in tests:
            subprocess.run(["java", "-cp", str(build), test.stem], check=True, cwd=repo)


if __name__ == "__main__":
    main()
