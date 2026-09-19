#!/usr/bin/env python3
"""Verify that a built Nori.jar is a complete, runnable release artifact.

The release JAR has to carry JavaFX inside it, because a user who downloads it
will not have JavaFX installed separately. A JAR that is missing those classes
still builds, still starts from the console, and only fails when the graphical
interface is opened -- which is too late to find out. These checks run against
the built artifact rather than the source tree, so they catch a packaging fault
that the JUnit and UI suites cannot see.

    python test/jar_release_test.py [--jar build/libs/Nori.jar]

Every case prints PASS or FAIL. The script stops at the first failure and exits
non-zero, so it can gate a release.
"""

from __future__ import annotations

import argparse
import os
import shutil
import subprocess
import sys
import tempfile
import zipfile
from pathlib import Path

# A JAR without JavaFX bundled comes to roughly 40 KB. The published guidance is
# that anything under 5 MB is missing its libraries.
MIN_RELEASE_BYTES = 5 * 1024 * 1024

EXPECTED_MAIN_CLASS = "nori.Launcher"

# One entry per thing the GUI needs at runtime. Each is a class the application
# loads on the path from launch to a drawn window.
REQUIRED_ENTRIES = [
    ("javafx/application/Application.class", "JavaFX application class"),
    ("javafx/fxml/FXMLLoader.class", "JavaFX FXML loader"),
    ("javafx/scene/control/Button.class", "JavaFX controls"),
    ("javafx/scene/layout/VBox.class", "JavaFX layout"),
    ("nori/Launcher.class", "Nori launcher"),
    ("nori/Main.class", "Nori JavaFX entry point"),
    ("nori/Nori.class", "Nori command loop"),
    ("view/MainWindow.fxml", "Main window layout resource"),
]


class CaseFailed(Exception):
    """Raised when a verification case does not hold."""


def report(name: str, detail: str) -> None:
    """Prints a passing case and what it observed."""
    print("PASS: %s" % name)
    if detail:
        print("      %s" % detail)


def check_size(jar: Path) -> None:
    """Checks the JAR is large enough to contain its bundled libraries."""
    size = jar.stat().st_size
    readable = "%d bytes (%.2f MB)" % (size, size / 1024 / 1024)
    if size < MIN_RELEASE_BYTES:
        raise CaseFailed(
            "JAR is %s, under the %d MB release floor. JavaFX is almost"
            " certainly not bundled; check the shadowJar configuration."
            % (readable, MIN_RELEASE_BYTES // 1024 // 1024))
    report("JAR is large enough to hold JavaFX", readable)


def check_manifest(jar: Path) -> None:
    """Checks the manifest names the launcher that starts the GUI."""
    with zipfile.ZipFile(jar) as archive:
        manifest = archive.read("META-INF/MANIFEST.MF").decode("utf-8")
    for line in manifest.splitlines():
        if line.startswith("Main-Class:"):
            actual = line.split(":", 1)[1].strip()
            if actual != EXPECTED_MAIN_CLASS:
                raise CaseFailed("Main-Class is %r, expected %r"
                                 % (actual, EXPECTED_MAIN_CLASS))
            report("Manifest names the launcher", "Main-Class: %s" % actual)
            return
    raise CaseFailed("manifest has no Main-Class entry")


def check_contents(jar: Path) -> None:
    """Checks every class and resource the GUI needs is inside the JAR."""
    with zipfile.ZipFile(jar) as archive:
        names = set(archive.namelist())
        total = len(names)
        natives = [n for n in names if n.endswith((".dll", ".so", ".dylib"))]

    missing = [label for entry, label in REQUIRED_ENTRIES if entry not in names]
    if missing:
        raise CaseFailed("JAR is missing: " + ", ".join(missing))
    report("JAR carries every class the GUI needs",
           "%d entries, all %d required entries present"
           % (total, len(REQUIRED_ENTRIES)))

    if not natives:
        raise CaseFailed("JAR carries no native libraries, so JavaFX cannot"
                         " render on any platform")
    report("JAR carries JavaFX native libraries",
           "%d native libraries bundled" % len(natives))


def run_session(work_dir: Path, jar_name: str, commands: str) -> str:
    """Runs one console session against the JAR and returns its output."""
    result = subprocess.run(
        ["java", "-cp", jar_name, "nori.Nori"],
        input=commands,
        text=True,
        cwd=str(work_dir),
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
    )
    if result.returncode != 0:
        raise CaseFailed("Nori exited with status %d. Output:\n%s"
                         % (result.returncode, result.stdout))
    return result.stdout.replace("\r\n", "\n")


def check_runs_from_empty_folder(jar: Path) -> None:
    """Checks the JAR runs, saves, and reloads from a folder holding only it."""
    work_dir = Path(tempfile.mkdtemp(prefix="nori-release-"))
    try:
        shutil.copy2(jar, work_dir / jar.name)

        first = run_session(work_dir, jar.name,
                            "todo pack bag\ndeadline submit report /by 2019-12-02\nbye\n")
        for expected in ["Task tucked safely under my wing:",
                         "[T][ ] pack bag",
                         "[D][ ] submit report (by: Dec 02 2019)",
                         "The iceberg now holds 2 task(s)."]:
            if expected not in first:
                raise CaseFailed("first session did not print %r. Output:\n%s"
                                 % (expected, first))
        report("JAR runs from a folder containing only itself",
               "added two tasks and exited cleanly")

        save_file = work_dir / "data" / "nori.txt"
        if not save_file.is_file():
            raise CaseFailed("no save file was created at data/nori.txt")
        report("JAR creates its data folder beside itself",
               "wrote %s" % save_file.relative_to(work_dir).as_posix())

        second = run_session(work_dir, jar.name, "list\nbye\n")
        for expected in ["1.[T][ ] pack bag",
                         "2.[D][ ] submit report (by: Dec 02 2019)"]:
            if expected not in second:
                raise CaseFailed("relaunch did not list %r. Output:\n%s"
                                 % (expected, second))
        report("JAR reloads tasks saved by an earlier run",
               "both tasks listed after relaunch")
    finally:
        shutil.rmtree(work_dir, ignore_errors=True)


def main() -> int:
    """Runs every release check against the built JAR."""
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jar", type=Path, default=Path("build/libs/Nori.jar"),
                        help="the release artifact to verify")
    args = parser.parse_args()

    jar = args.jar
    if not jar.is_file():
        print("FAIL: no JAR at %s. Run './gradlew clean check shadowJar' first."
              % jar, file=sys.stderr)
        return 1

    print("Verifying release artifact: %s\n" % jar)
    checks = [check_size, check_manifest, check_contents,
              check_runs_from_empty_folder]
    for check in checks:
        try:
            check(jar)
        except CaseFailed as failure:
            print("FAIL: %s" % check.__doc__.splitlines()[0], file=sys.stderr)
            print("      %s" % failure, file=sys.stderr)
            return 1

    print("\nAll release checks passed. The JAR is ready to attach to a"
          " GitHub release.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
