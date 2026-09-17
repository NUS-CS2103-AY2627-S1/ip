#!/usr/bin/env python3
"""Run the repository's exact-output plan in isolated directories with storage faults.

Uses the adjacent run_ui_tests module. Optional case metadata can specify
``- Block storage before command: N`` and ``- Restore storage before command: N``.
The fixture moves the data file aside and puts a directory at its old path,
then restores the untouched data file before a retry. Real application input
and output are checked by the shared runner without substitutions.
"""

from pathlib import Path
import re
import sys
import tempfile

import run_ui_tests as runner


def read_fixtures(plan_path, plan):
    """Read optional fault timing and reject incomplete fixture declarations."""
    fixtures = {}
    cases = {case.case_id: case for case in plan.cases}
    sections = re.split(r'^### ', plan_path.read_text(encoding='utf-8'), flags=re.MULTILINE)[1:]
    for section in sections:
        case_id = section.split(':', 1)[0].strip()
        timings = {}
        for action, number in re.findall(
                r'^- (Block|Restore) storage before command: ([1-9][0-9]*)$', section, re.MULTILINE):
            if action in timings:
                raise ValueError(f'{case_id}: duplicate storage fixture action {action}')
            timings[action] = int(number)
        if timings:
            if set(timings) != {'Block', 'Restore'}:
                raise ValueError(f'{case_id}: both block and restore timings are required')
            if not 1 < timings['Block'] < timings['Restore'] <= len(cases[case_id].commands):
                raise ValueError(f'{case_id}: invalid storage fixture timing')
            fixtures[case_id] = timings
    return fixtures


class FaultInput:
    """Apply a filesystem fault just before sending a specified command."""

    def __init__(self, stream, directory, timings):
        self.stream = stream
        self.directory = directory
        self.timings = timings
        self.command_number = 0
        self.original_data = None

    def write(self, data):
        self.command_number += 1
        target = self.directory / 'data/glennon.txt'
        backup = self.directory / 'saved-before-failure.txt'
        if self.command_number == self.timings['Block']:
            self.original_data = target.read_bytes()
            target.rename(backup)
            target.mkdir()
        elif self.command_number == self.timings['Restore']:
            if backup.read_bytes() != self.original_data:
                raise AssertionError('A failed command changed saved mission data')
            target.rmdir()
            backup.rename(target)
        return self.stream.write(data)

    def __getattr__(self, name):
        return getattr(self.stream, name)


def main():
    args = runner.parse_args(sys.argv[1:])
    plan = runner.parse_plan(args.plan)
    fixtures = read_fixtures(args.plan, plan)
    sequences = [tuple(step.input_text for step in case.commands) for case in plan.cases]
    if len(sequences) != len(set(sequences)):
        raise ValueError('Every complete command sequence must be unique')
    print(f'Test plan: {args.plan}', flush=True)
    print('Shared startup output (checked in every fresh process):', flush=True)
    runner.print_output(plan.expected_startup)
    original_start = runner.start_process
    command_count = sum(len(case.commands) for case in plan.cases)
    for count, case in enumerate(plan.cases):
        with tempfile.TemporaryDirectory(prefix='glennon-ui-') as temporary:
            directory = Path(temporary)

            def start_process(command, cwd):
                capture = original_start(command, cwd)
                if case.case_id in fixtures:
                    capture.process.stdin = FaultInput(capture.process.stdin, directory, fixtures[case.case_id])
                return capture

            runner.start_process = start_process
            if not runner.run_case(case, plan.expected_startup, args.program, directory,
                                   args.idle_timeout, args.command_timeout):
                print(f'Result: FAIL - {count} passed, 1 failed, {len(plan.cases) - count - 1} skipped')
                return 1
    print(f'Result: PASS - {len(plan.cases)}/{len(plan.cases)} cases, '
          f'{command_count}/{command_count} commands')
    return 0


if __name__ == '__main__':
    raise SystemExit(main())
