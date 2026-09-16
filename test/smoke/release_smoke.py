"""Smoke-test the exact published v0.2 JAR on isolated desktop CI runners.

Keyboard and screenshot APIs follow https://pyautogui.readthedocs.io/.
Never run this script on a personal desktop: it types into the active window.
"""
import base64
import hashlib
import os
from pathlib import Path
import subprocess
import tempfile
import time
import urllib.request

import pyautogui

URL = 'https://github.com/antaradaw/ip/releases/download/v0.2/bambolino.jar'
SHA256 = '3c24761144c5c72af0b21762f2cd9aaa533998d1f18a48a54589818154fc260f'
RESULTS = Path('smoke-results').resolve()


def wait_for(predicate, message, process):
    """Wait for observable state, failing early if Java exits."""
    deadline = time.monotonic() + 20
    while time.monotonic() < deadline:
        if process.poll() is not None:
            raise AssertionError(f'Java exited: {process.returncode}; {message}')
        if predicate():
            return
        time.sleep(0.2)
    raise AssertionError(message)


def command(text):
    """Replace any selected failed command and submit through the GUI."""
    pyautogui.hotkey('ctrl', 'a')
    pyautogui.write(text, interval=0.04)
    pyautogui.press('enter')
    time.sleep(0.5)


def main():
    """Check creation, validation, and reloading using only released code."""
    if os.environ.get('GITHUB_ACTIONS') != 'true':
        raise RuntimeError('Run only on an isolated GitHub Actions desktop')
    RESULTS.mkdir(exist_ok=True)
    subprocess.run(['java', '-version'], check=True)
    with tempfile.TemporaryDirectory(prefix='bambolino-smoke-') as folder:
        work = Path(folder)
        jar = work / 'bambolino.jar'
        urllib.request.urlretrieve(URL, jar)
        assert hashlib.sha256(jar.read_bytes()).hexdigest() == SHA256
        assert list(work.iterdir()) == [jar]
        data = work / 'data/bambolino.txt'
        encoded = base64.b64encode(b'smoke task').decode()
        for attempt in range(2):
            with (RESULTS / f'java-{attempt}.log').open('w') as log:
                process = subprocess.Popen(['java', '-jar', str(jar)], cwd=work,
                                           stdout=log, stderr=subprocess.STDOUT)
                try:
                    time.sleep(8)
                    assert process.poll() is None, 'GUI failed to start'
                    if os.name != 'nt':
                        window = subprocess.check_output(
                            ['xdotool', 'search', '--sync', '--onlyvisible', '--name', '^Bambolino$'],
                            timeout=20, text=True).splitlines()[-1]
                        subprocess.run(['xdotool', 'windowactivate', '--sync', window],
                                       check=True, timeout=20)
                        subprocess.run(['xdotool', 'windowfocus', '--sync', window],
                                       check=True, timeout=20)
                    if attempt == 0:
                        command('todo smoke task')
                        wait_for(lambda: data.exists() and f'T|0|{encoded}' in data.read_text(),
                                 'GUI did not save added task', process)
                        command('mark 1')
                        wait_for(lambda: f'T|1|{encoded}' in data.read_text(),
                                 'GUI did not mark task', process)
                        original = data.read_bytes()
                        command('deadline invalid /by 2026-02-30')
                        assert data.read_bytes() == original, 'Invalid date changed saved tasks'
                        pyautogui.screenshot().save(RESULTS / 'validation.png')
                    else:
                        # This only succeeds if the new process reloaded task 1.
                        command('unmark 1')
                        wait_for(lambda: f'T|0|{encoded}' in data.read_text(),
                                 'Task did not survive restart', process)
                        command('list')
                        pyautogui.screenshot().save(RESULTS / 'reloaded.png')
                        command('delete 1')
                        wait_for(lambda: data.read_text() == '', 'Delete failed', process)
                    print(f'PASS: GUI session {attempt + 1}', flush=True)
                except Exception:
                    pyautogui.screenshot().save(RESULTS / f'failure-{attempt}.png')
                    raise
                finally:
                    if process.poll() is None:
                        process.terminate()
                        try:
                            process.wait(timeout=5)
                        except subprocess.TimeoutExpired:
                            process.kill()
                            process.wait()
        (RESULTS / 'result.txt').write_text(
            f'PASS: released v0.2 SHA256 {SHA256}\n'
            'GUI startup, add, mark, invalid-date preservation, restart, unmark, list, delete.\n')


if __name__ == '__main__':
    main()
