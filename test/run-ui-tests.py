"""Compile with Java 25 and check Markdown console cases in order, stopping on failure."""
import difflib
import os
from pathlib import Path
import re
import subprocess
import sys
import tempfile

ROOT = Path(__file__).resolve().parents[1]
JDK = Path.home() / '.sdkman/candidates/java/25.0.3.fx-zulu'
ENV = dict(os.environ, JAVA_HOME=str(JDK), PATH=f'{JDK}/bin:' + os.environ['PATH'])
plan = (ROOT / 'test/ui-test-plan.md').read_text()
record = Path(sys.argv[1]) if len(sys.argv) > 1 else ROOT / 'out/ui-test-session.txt'
record.parent.mkdir(parents=True, exist_ok=True)
version = subprocess.run(['java', '-version'], env=ENV, capture_output=True, text=True, check=True)
if not re.search(r'version "25[.\"]', version.stderr):
    raise SystemExit('UI tests require Java 25.')
sources = sorted(str(p) for p in (ROOT / 'src/main/java').rglob('*.java') if '/ui/gui/' not in str(p))
classes = ROOT / 'out/ui-test'
classes.mkdir(parents=True, exist_ok=True)
subprocess.run(['javac', '-d', str(classes), *sources], env=ENV, check=True)

def block(case, heading, language='text'):
    match = re.search(r'### ' + heading + r'\n\n```' + language + r'\n(.*?)\n```', case, re.S)
    return match.group(1) if match else None

def normalize(value):
    value = value.replace('\r\n', '\n')
    return value[:-1] if value.endswith('\n') else value

with record.open('w') as log:
    log.write(version.stderr + '\n')
    for case in re.split(r'^## Test Case ', plan, flags=re.M)[1:]:
        name = case.splitlines()[0]
        inputs, expected = block(case, 'Inputs'), block(case, 'Expected output')
        if inputs is None or expected is None:
            raise SystemExit(f'Missing input/output in {name}')
        with tempfile.TemporaryDirectory(prefix='stewie-ui-') as directory:
            setup = block(case, 'Setup', 'sh')
            if setup:
                subprocess.run(['sh', '-eu', '-c', setup], cwd=directory, env=ENV, check=True)
            result = subprocess.run(['java', '-ea', '-cp', str(classes), 'stewie.Stewie'],
                                    input=inputs + '\n', cwd=directory, env=ENV,
                                    capture_output=True, text=True, timeout=30)
            passed = result.returncode == 0 and not result.stderr and normalize(result.stdout) == normalize(expected)
            log.write(f'=== Test Case: {name} ===\nINPUT:\n{inputs}\nOUTPUT:\n{result.stdout}')
            log.write(f'RESULT: {"PASS" if passed else "FAIL"}\nSTDERR: {result.stderr or "(empty)"}\n')
            log.write(f'EXIT STATUS: {result.returncode}\n\n')
            print(f'{name}: {"PASS" if passed else "FAIL"}')
            if not passed:
                log.write(f'EXPECTED OUTPUT:\n{expected}\nACTUAL OUTPUT:\n{result.stdout}\n')
                diff = ''.join(difflib.unified_diff(expected.splitlines(True), result.stdout.splitlines(True),
                                                    fromfile='expected', tofile='actual'))
                log.write(diff + '\nExecution stopped immediately; later cases were not run.\n')
                print(diff)
                raise SystemExit(1)
print(f'Console sessions: {record.resolve()}')
