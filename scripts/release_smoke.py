"""Check the public release's commands, storage, and GUI process startup.

Uses only Python's standard library and Java 25. Run Linux GUI checks under
xvfb-run. This is a packaging smoke test, not a visual or interactive GUI test.
All task data is confined to an automatically cleaned temporary directory.
"""

import hashlib
import json
import os
from pathlib import Path
import platform
import subprocess
import tempfile
import urllib.request


def fetch(url):
    request = urllib.request.Request(url, headers={"User-Agent": "GOAT-release-smoke"})
    with urllib.request.urlopen(request, timeout=60) as response:
        return response.read()


def run_cli(jar, folder, commands):
    result = subprocess.run(
        ["java", "-cp", str(jar), "seedu.goat.Goat"],
        input="\n".join(commands) + "\n",
        cwd=folder,
        text=True,
        capture_output=True,
        timeout=30,
        check=True,
    )
    if result.stderr:
        raise AssertionError(result.stderr)
    return result.stdout


def require(output, *snippets):
    for snippet in snippets:
        if snippet not in output:
            raise AssertionError(f"Missing {snippet!r} in:\n{output}")


def smoke(jar, folder):
    first = run_cli(jar, folder, [
        "todo read notes", "deadline submit iP /by 2026-09-18",
        "event retreat /from 2026-09-19 /to 2026-09-20", "mark 1", "list",
        "find retreat", "on 2026-09-18", "todo READ notes", "mark 99",
        "deadline impossible /by 2026-02-30", "bye",
    ])
    require(first, "Now you have 3 tasks", "[T][X] read notes", "[D][ ] submit iP",
            "[E][ ] retreat", "already have", "There is no task 99", "could not read")
    saved = folder / "data" / "goat.txt"
    assert len(saved.read_text(encoding="utf-8").splitlines()) == 3
    second = run_cli(jar, folder, ["list", "unmark 1", "delete 3", "bye"])
    require(second, "restored 3 tasks", "[T][X] read notes", "[T][ ] read notes", "removed")
    third = run_cli(jar, folder, ["list", "bye"])
    require(third, "restored 2 tasks", "[T][ ] read notes")
    assert "retreat" not in third
    saved.write_text(saved.read_text(encoding="utf-8") + "broken record\n", encoding="utf-8")
    original = saved.read_bytes()
    damaged = run_cli(jar, folder, ["list", "todo must not overwrite", "bye"])
    require(damaged, "Changes are disabled", "read notes")
    assert saved.read_bytes() == original


def gui_startup(jar, folder):
    # A fresh folder exercises the packaged Launcher without any existing data.
    # The test owns and terminates only this child process after the observation.
    with subprocess.Popen(
        ["java", "-jar", str(jar)], cwd=folder,
        stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True,
    ) as process:
        try:
            output, errors = process.communicate(timeout=10)
            raise AssertionError(f"GUI exited before observation: {process.returncode}\n{output}\n{errors}")
        except subprocess.TimeoutExpired:
            process.terminate()
            output, errors = process.communicate(timeout=10)
        print("GUI output:", output, errors)
        for failure in ("Exception", "Error initializing", "no suitable pipeline", "Could not find"):
            if failure in output + errors:
                raise AssertionError(f"GUI startup reported {failure}")


def main():
    version = subprocess.run(["java", "-version"], capture_output=True, text=True, check=True)
    print(platform.platform(), platform.machine(), version.stderr, flush=True)
    assert 'version "25' in version.stderr
    release = json.loads(fetch("https://api.github.com/repos/iians0n/ip/releases/latest"))
    assert not release["draft"] and not release["prerelease"]
    assets = release["assets"]
    assert len(assets) == 1 and assets[0]["name"] == "goat.jar"
    with tempfile.TemporaryDirectory(prefix="goat-release-") as temporary:
        root = Path(temporary)
        jar = root / "goat.jar"
        jar.write_bytes(fetch(assets[0]["browser_download_url"]))
        digest = hashlib.sha256(jar.read_bytes()).hexdigest()
        assert assets[0]["digest"] == "sha256:" + digest
        commands = root / "commands"
        commands.mkdir()
        smoke(jar, commands)
        gui = root / "gui"
        gui.mkdir()
        gui_startup(jar, gui)
        report = (f"Release {release['tag_name']} on {platform.platform()}: commands, restart, "
                  f"damaged-file preservation, and 10-second GUI startup passed. SHA-256: {digest}. "
                  "This does not verify visual layout or interactive GUI controls.")
        print(report, flush=True)
        if os.environ.get("GITHUB_STEP_SUMMARY"):
            with open(os.environ["GITHUB_STEP_SUMMARY"], "a", encoding="utf-8") as summary:
                summary.write(report + "\n")


if __name__ == "__main__":
    main()
