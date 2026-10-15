#!/usr/bin/env python3
"""Checks a release archive and its CLI persistence away from the source checkout."""

import argparse
import os
from pathlib import Path
import re
import shutil
import struct
import subprocess
import sys
from tempfile import TemporaryDirectory
from zipfile import BadZipFile, ZipFile


def verify_archive(jar: Path, mac_architecture: str):
    with ZipFile(jar) as archive:
        names = archive.namelist()
        required = {
            "sage/Launcher.class", "sage/Sage.class", "sage/gui/Main.class",
            "view/MainWindow.fxml", "view/DialogBox.fxml", "css/main.css", "css/dialog-box.css",
            "javafx/application/Application.class", "javafx/fxml/FXMLLoader.class",
            "glass.dll", "libglass.so", "libglass.dylib",
        }
        missing = required.difference(names)
        if missing:
            raise ValueError(f"JAR is missing required application resources: {sorted(missing)}")
        if len(names) != len(set(names)):
            raise ValueError("JAR contains duplicate entries, which can hide incompatible native libraries.")
        if any(name.startswith(("data/", ".git/", ".idea/", ".codex/")) for name in names):
            raise ValueError("JAR unexpectedly includes personal data or development-only files.")
        manifest = archive.read("META-INF/MANIFEST.MF").decode("utf-8")
        if "Main-Class: sage.Launcher" not in manifest.splitlines() or "Class-Path:" in manifest:
            raise ValueError("JAR must start sage.Launcher without depending on external classpath files.")
        launcher = archive.read("sage/Launcher.class")
        if struct.unpack(">H", launcher[6:8])[0] != 69:
            raise ValueError("Release classes must target Java 25.")
        native = archive.read("libglass.dylib")
        expected_cpu = {"x64": 0x01000007, "arm64": 0x0100000C}[mac_architecture]
        if native[:4] != b"\xcf\xfa\xed\xfe" or struct.unpack("<I", native[4:8])[0] != expected_cpu:
            raise ValueError(f"macOS native library does not match the intended {mac_architecture} release.")


def run_cli(java: str, work: Path, commands: str) -> str:
    process = subprocess.run(
        [java, "-ea", "-Dfile.encoding=UTF-8", "-Dstdout.encoding=UTF-8", "-Dstderr.encoding=UTF-8",
         "-cp", "sage.jar", "sage.Sage"],
        cwd=work, input=commands, text=True, encoding="utf-8", capture_output=True, timeout=30,
    )
    if process.returncode != 0:
        raise ValueError(f"Packaged CLI exited with {process.returncode}:\n{process.stdout}\n{process.stderr}")
    return process.stdout


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jar", type=Path, required=True)
    parser.add_argument("--mac-architecture", choices=("x64", "arm64"), default="x64")
    args = parser.parse_args()
    try:
        jar = args.jar.resolve(strict=True)
        verify_archive(jar, args.mac_architecture)
        java_home = os.environ.get("JAVA_HOME")
        executable = "java.exe" if os.name == "nt" else "java"
        java = str(Path(java_home, "bin", executable)) if java_home else shutil.which("java")
        if not java:
            raise ValueError("Java 25 must be installed and available through JAVA_HOME or PATH.")
        version = subprocess.run(
            [java, "-version"], capture_output=True, text=True, encoding="utf-8", timeout=15,
        )
        if version.returncode != 0 or not re.search(r'version\s+"25(?:[."+-]|$)', version.stdout + version.stderr):
            raise ValueError("Release checks require Java 25. Check JAVA_HOME and java -version.")
        with TemporaryDirectory(prefix="sage-release-") as temporary:
            work = Path(temporary) / "Sage release with spaces"
            work.mkdir()
            shutil.copy2(jar, work / "sage.jar")
            note = "Remember café and 电影"
            first = run_cli(java, work, f"note {note}\ntodo release check\nbye\n")
            second = run_cli(java, work, "list\nbye\n")
            for output in (first, second):
                if f"[N] {note}" not in output or "[T][ ] release check" not in output:
                    raise ValueError(f"The standalone JAR did not preserve task and Unicode note data:\n{output}")
            third = run_cli(java, work, "delete 1\nbye\n")
            fourth = run_cli(java, work, "list\nbye\n")
            if note in fourth or "[T][ ] release check" not in fourth:
                raise ValueError(f"Deletion was not persisted correctly:\n{third}\n{fourth}")
        print("PASS: launcher, resources, native architecture, relocated JAR, Unicode, and persistence.")
        print("GUI rendering still requires a manual check on each supported operating system.")
        return 0
    except (OSError, ValueError, KeyError, BadZipFile, struct.error, subprocess.TimeoutExpired) as exc:
        print(f"FAIL: {exc}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
