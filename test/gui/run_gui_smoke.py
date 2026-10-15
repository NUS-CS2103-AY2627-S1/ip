"""Build and exercise the packaged JavaFX application using disposable task data."""
import os
from pathlib import Path
import subprocess
import tempfile

root = Path(__file__).resolve().parents[2]
jar = root / 'build/libs/tungtung.jar'
classes = root / 'build/gui-smoke'
classes.mkdir(parents=True, exist_ok=True)
for executable in ('java', 'javac'):
    result = subprocess.run([executable, '-version'], capture_output=True, text=True, check=True)
    if '25.' not in result.stdout + result.stderr:
        raise RuntimeError('GUI smoke checks require Java 25.')
subprocess.run(['javac', '-cp', str(jar), '-d', str(classes), str(root / 'test/gui/GuiSmoke.java')], check=True)
for mode in ('normal', 'corrupt', 'save-failure'):
    with tempfile.TemporaryDirectory(prefix='tungtung-gui-') as session:
        if mode == 'corrupt':
            data = Path(session) / 'data/tungtung.txt'
            data.parent.mkdir()
            data.write_text('T | 0 | precious task\nbroken line\n', encoding='utf-8')
        result = subprocess.run(['java', '-cp', os.pathsep.join((str(jar), str(classes))), 'GuiSmoke', mode],
                                cwd=session, capture_output=True, text=True, timeout=45)
        print(result.stdout, end='')
        if result.returncode != 0 or f'GUI_SMOKE_PASS {mode}' not in result.stdout:
            print(result.stderr)
            raise SystemExit(result.returncode or 1)
print('All packaged GUI smoke checks passed.')
