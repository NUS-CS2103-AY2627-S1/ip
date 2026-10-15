"""Regression tests for failures that must not be reported as passing UI checks."""

from contextlib import redirect_stdout
import importlib.util
import io
from pathlib import Path
import subprocess
import sys
from tempfile import TemporaryDirectory
import unittest
from unittest.mock import patch


RUNNER_PATH = Path(__file__).resolve().parents[1] / ".codex/skills/test-ui/scripts/run-ui-tests.py"
SPEC = importlib.util.spec_from_file_location("ui_runner", RUNNER_PATH)
RUNNER = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(RUNNER)


class UiRunnerTest(unittest.TestCase):
    def run_session(self, command, cases):
        transcript = io.StringIO()
        with TemporaryDirectory() as temporary, redirect_stdout(transcript):
            status = RUNNER.run_session(command, cases, Path(temporary))
        return status, transcript.getvalue()

    def test_nonzero_exit_fails_even_when_stdout_matches(self):
        status, transcript = self.run_session(
            [sys.executable, "-c", "print('expected'); raise SystemExit(7)"],
            [{"input": "", "expected": "expected\n"}],
        )
        self.assertEqual(1, status)
        self.assertIn("exit code 7", transcript)

    def test_output_mismatch_stops_before_next_case(self):
        status, transcript = self.run_session(
            [sys.executable, "-c", "print('actual')"],
            [
                {"id": "first", "input": "", "expected": "expected\n"},
                {"id": "must-not-run", "input": "", "expected": "actual\n"},
            ],
        )
        self.assertEqual(1, status)
        self.assertIn("Expected:\nexpected", transcript)
        self.assertIn("Actual:\nactual", transcript)
        self.assertNotIn("Case: must-not-run", transcript)

    def test_each_case_has_an_independent_working_directory(self):
        program = "from pathlib import Path; p = Path('data.txt'); print(p.exists()); p.touch()"
        status, transcript = self.run_session(
            [sys.executable, "-c", program],
            [{"input": "", "expected": "False\n"}, {"input": "", "expected": "False\n"}],
        )
        self.assertEqual(0, status)
        self.assertEqual(2, transcript.count("Result: PASS"))

    def test_timeout_reports_partial_output_and_expected_output(self):
        with patch.object(RUNNER, "run_case", side_effect=subprocess.TimeoutExpired("java", 30, b"partial")):
            status, transcript = self.run_session(
                ["java"], [{"input": "bye\n", "expected": "complete\n"}],
            )
        self.assertEqual(1, status)
        self.assertIn("Expected:\ncomplete", transcript)
        self.assertIn("Actual:\npartial", transcript)

    def test_java_25_check_rejects_a_different_major_with_patch_25(self):
        result = subprocess.CompletedProcess(["java", "-version"], 0, "", 'openjdk version "21.0.25"\n')
        with patch.object(RUNNER.subprocess, "run", return_value=result), self.assertRaises(ValueError):
            RUNNER.require_java_25("java")

    def test_java_25_check_accepts_java_and_javac(self):
        for output in ('openjdk version "25.0.3"\n', "javac 25.0.3\n"):
            with self.subTest(output=output):
                result = subprocess.CompletedProcess(["java", "-version"], 0, "", output)
                with patch.object(RUNNER.subprocess, "run", return_value=result):
                    RUNNER.require_java_25("java")


if __name__ == "__main__":
    unittest.main()
