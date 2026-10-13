"""Verify one copied release JAR without using the source tree or the user's saved tasks."""

import os
from pathlib import Path
import shutil
import subprocess
import sys
import tempfile
from zipfile import ZipFile


def run(command, directory, environment, input_text=None):
    try:
        result = subprocess.run(command, cwd=directory, env=environment, input=input_text,
                                text=True, capture_output=True, timeout=60)
    except subprocess.TimeoutExpired as error:
        print(error.stdout or "", file=sys.stderr)
        print(error.stderr or "", file=sys.stderr)
        raise RuntimeError("Release check timed out; ensure a graphical desktop is available") from error
    print(result.stdout, end="")
    if result.returncode:
        print(result.stderr, file=sys.stderr)
        raise RuntimeError(f"Release check failed: {command[0]}")
    return result.stdout


def verify_contents(jar):
    with ZipFile(jar) as archive:
        names = set(archive.namelist())
        for platform in ("win", "linux", "mac", "mac-aarch64"):
            folder = f"javafx-natives/{platform}/"
            libraries = archive.read(folder + "libraries.list").decode().splitlines()
            assert libraries, f"Missing libraries for {platform}"
            assert all(folder + library in names for library in libraries)
        assert "duke/gui/main.css" in names
        assert "duke/gui/QuestIcon.class" in names
        assert "duke/gui/MessageIcon.class" in names
        media_extensions = (".png", ".jpg", ".jpeg", ".gif", ".svg", ".webp", ".ico",
                            ".mp3", ".wav", ".ogg", ".m4a", ".aac", ".flac", ".mp4")
        assert not any(name.startswith("duke/") and name.lower().endswith(media_extensions) for name in names)
        assert "META-INF/wangsa/CREDITS.md" in names
        notice_root = "META-INF/wangsa/dependency-notices/"
        for library in ("jackson-core", "jackson-databind"):
            prefix = notice_root + f"com.fasterxml.jackson.core/{library}-"
            notices = [name for name in names if name.startswith(prefix) and name.endswith("/META-INF/NOTICE")]
            assert len(notices) == 1, f"Missing separate notice for {library}"
            assert archive.read(notices[0]).strip(), f"Empty notice for {library}"
        assert "duke/Storage.class" in names
        assert not any(name.startswith("org/sqlite/") for name in names)
        assert "duke/SqliteTaskRepository.class" not in names
        assert "duke/gui/CharacterAvatar.class" not in names
        assert not any("/" not in name and name.endswith((".dll", ".so", ".dylib")) for name in names)
    print("PASS: four JavaFX platforms and code-drawn symbols; no app image/audio assets or SQLite dependency")
    print("PASS: dependency notice files with identical names are preserved separately")


def main():
    repository = Path(__file__).resolve().parents[1]
    source_jar = Path(sys.argv[1]).resolve() if len(sys.argv) > 1 else repository / "build/libs/Wangsa.jar"
    verify_contents(source_jar)
    source_zip = Path(sys.argv[2]).resolve() if len(sys.argv) > 2 else repository / "build/distributions/Wangsa.zip"
    if source_zip.exists():
        with ZipFile(source_zip) as archive:
            assert archive.read("Wangsa/Wangsa.jar") == source_jar.read_bytes()
            assert "Java 25" in archive.read("Wangsa/QUICK-START.txt").decode()
            assert b"%~dp0Wangsa.jar" in archive.read("Wangsa/Start-Wangsa.bat")
            assert (archive.getinfo("Wangsa/Start-Wangsa.command").external_attr >> 16) & 0o111
            assert not any("/data/" in name for name in archive.namelist())
        print("PASS: easy-start ZIP matches the JAR and contains no personal data")
    environment = dict(os.environ)
    environment.pop("LLM_API_KEY", None)
    environment.pop("LLM_MODEL", None)
    with tempfile.TemporaryDirectory(prefix="wangsa-release-") as temporary:
        folder = Path(temporary)
        app_folder = folder / "My Wangsa app"
        app_folder.mkdir()
        jar = app_folder / "Wangsa.jar"
        shutil.copy2(source_jar, jar)
        old_database = app_folder / "data/wangsa.db"
        old_database.parent.mkdir()
        old_database.write_bytes(b"old database must not block the text-file app")
        classes = folder / "smoke-classes"
        classes.mkdir()
        run(["javac", "-cp", str(jar), "-d", str(classes),
             str(repository / "src/test/java/duke/gui/ReleaseSmoke.java")], folder, environment)
        command = ["java", "--enable-native-access=ALL-UNNAMED", "-ea", "-cp",
                   os.pathsep.join((str(classes), str(jar))), "duke.gui.ReleaseSmoke"]
        run(command + ["normal"], folder, environment)
        assert (folder / "data/wangsa.txt").is_file()
        assert not (app_folder / "data/wangsa.txt").exists()
        native_cache = folder / ".wangsa/javafx-natives"
        assert native_cache.is_dir()
        cached_libraries = list(native_cache.glob("*/*"))
        assert cached_libraries, "The JAR must extract natives inside its launch folder"
        cache_times = {path: path.stat().st_mtime_ns for path in cached_libraries}
        assert old_database.read_bytes() == b"old database must not block the text-file app"
        run(command + ["reload"], folder, environment)
        assert cache_times == {path: path.stat().st_mtime_ns for path in cached_libraries}
        output = run(["java", "--enable-native-access=ALL-UNNAMED", "-cp", str(jar), "duke.Wangsa"],
                     folder, environment, "list\n@ai How do I add a task?\nbye\n")
        assert "[D][X] submit report" in output and "[E][ ] team meeting" in output
        assert "Offline help:" in output and "todo DESCRIPTION" in output
        assert "•" not in output and "—" not in output
        print("PASS: GUI/CLI share text saves inside the launch folder without writing beside a remote JAR")
        print("PASS: native libraries stay inside the launch folder and are reused without rewriting")
        print("PASS: old databases remain untouched and optional AI falls back with console-safe text")
        blocked = folder / "blocked"
        (blocked / "data").mkdir(parents=True)
        save_file = blocked / "data/wangsa.txt"
        invalid_data = b"T | 2 | deliberately invalid task for release verification\n"
        save_file.write_bytes(invalid_data)
        blocked_jar = blocked / "Wangsa.jar"
        shutil.copy2(source_jar, blocked_jar)
        blocked_command = ["java", "--enable-native-access=ALL-UNNAMED", "-ea", "-cp",
                           os.pathsep.join((str(classes), str(blocked_jar))), "duke.gui.ReleaseSmoke"]
        run(blocked_command + ["blocked"], blocked, environment)
        assert save_file.read_bytes() == invalid_data
        print("PASS: startup errors preserve the original data")


if __name__ == "__main__":
    main()
