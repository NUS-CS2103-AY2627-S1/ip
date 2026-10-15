"""Test the same release JAR on each OS using an ordinary Java 25 installation."""

import argparse
import hashlib
import shutil
import subprocess
import tempfile
import zipfile
from pathlib import Path


def inspect_jar(jar_path):
    """Catch missing platforms, entry points, and accidental test-tool packaging."""
    required = {
        "anders/Launcher.class",
        "view/MainWindow.fxml",
        "view/DialogBox.fxml",
        "view/main.css",
        "natives/win/glass.dll",
        "natives/linux/libglass.so",
        "natives/mac/libglass.dylib",
        "natives/mac-aarch64/libglass.dylib",
    }
    with zipfile.ZipFile(jar_path) as archive:
        names = archive.namelist()
        missing = required - set(names)
        if missing:
            raise AssertionError(f"Missing JAR entries: {sorted(missing)}")
        if len(names) != len(set(names)):
            raise AssertionError("The release JAR contains duplicate entries.")
        if any(name.startswith("anders/smoke/") for name in names):
            raise AssertionError("The test agent must not be inside the release JAR.")
        manifest = archive.read("META-INF/MANIFEST.MF").decode("utf-8")
        if "Main-Class: anders.Launcher" not in manifest:
            raise AssertionError("The release JAR does not launch anders.Launcher.")


def run_session(jar_path, agent_path, directory, mode, report_directory):
    """Launch the actual executable JAR; an external test agent operates its GUI."""
    command = [
        "java",
        f"-Duser.home={directory / 'home'}",
        f"-javaagent:{agent_path}={mode}",
        "-jar",
        str(jar_path),
    ]
    try:
        result = subprocess.run(command, cwd=directory, capture_output=True, text=True, timeout=45)
        output = result.stdout + result.stderr
    except subprocess.TimeoutExpired as exception:
        output = (exception.stdout or b"").decode("utf-8", errors="replace")
        output += (exception.stderr or b"").decode("utf-8", errors="replace")
        (report_directory / f"{mode}.txt").write_text(output, encoding="utf-8")
        raise AssertionError(f"The packaged application timed out during {mode}.") from exception
    print(output)
    (report_directory / f"{mode}.txt").write_text(output, encoding="utf-8")
    if result.returncode != 0 or f"SMOKE PASSED: {mode}" not in result.stdout:
        raise AssertionError(f"Packaged GUI smoke test failed during {mode}: exit {result.returncode}")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--jar", type=Path, default=Path("build/libs/anders.jar"))
    parser.add_argument("--agent", type=Path, default=Path("build/libs/anders-smoke-agent.jar"))
    parser.add_argument("--report-dir", type=Path, default=Path("build/reports/jar-smoke"))
    arguments = parser.parse_args()
    jar_path = arguments.jar.resolve(strict=True)
    agent_path = arguments.agent.resolve(strict=True)
    report_directory = arguments.report_dir.resolve()
    report_directory.mkdir(parents=True, exist_ok=True)

    version = subprocess.run(["java", "-version"], capture_output=True, text=True, check=True)
    if 'version "25' not in version.stdout + version.stderr:
        raise AssertionError("Run this smoke test with Java 25.")
    modules = subprocess.run(["java", "--list-modules"], capture_output=True, text=True, check=True)
    if any(line.startswith("javafx.") for line in modules.stdout.splitlines()):
        raise AssertionError("Use a plain Java 25 JDK without JavaFX to test the bundled libraries.")

    inspect_jar(jar_path)
    digest = hashlib.sha256(jar_path.read_bytes()).hexdigest()
    print(f"Release JAR SHA-256: {digest}")
    (report_directory / "sha256.txt").write_text(digest + "\n", encoding="utf-8")
    with tempfile.TemporaryDirectory(prefix="anders-jar-smoke-") as temporary:
        directory = Path(temporary).resolve()
        assert directory.parent == Path(tempfile.gettempdir()).resolve()
        copied_jar = directory / "anders.jar"
        shutil.copy2(jar_path, copied_jar)
        run_session(copied_jar, agent_path, directory, "create", report_directory)
        if not (directory / "data/anders.txt").is_file():
            raise AssertionError("The packaged app did not create its data file.")
        run_session(copied_jar, agent_path, directory, "reload", report_directory)
    print("PASSED: packaged GUI launches, accepts commands, saves, and reloads.")


if __name__ == "__main__":
    main()
