#!/usr/bin/env python3
"""Integration test for Goat's write-on-change task persistence."""

from pathlib import Path
import subprocess
import tempfile
import unittest


REPOSITORY = Path(__file__).parent.parent.resolve()
SOURCES = sorted((REPOSITORY / "src/main/java").rglob("*.java"))


class StorageIntegrationTest(unittest.TestCase):
    """Checks saving, loading, and storage error recovery through the console app."""

    @classmethod
    def setUpClass(cls) -> None:
        cls.temporary_classes = tempfile.TemporaryDirectory()
        cls.classes_directory = Path(cls.temporary_classes.name)
        subprocess.run(
            ["javac", "-d", str(cls.classes_directory), *map(str, SOURCES)],
            check=True,
        )

    @classmethod
    def tearDownClass(cls) -> None:
        cls.temporary_classes.cleanup()

    def run_goat(self, working_directory: Path, commands: str) -> subprocess.CompletedProcess[str]:
        """Runs one Goat session in the supplied isolated working directory."""
        return subprocess.run(
            ["java", "-cp", str(self.classes_directory), "goat.Goat"],
            cwd=working_directory,
            input=commands,
            text=True,
            capture_output=True,
            check=True,
        )

    def test_latest_task_state_is_saved_after_every_kind_of_change(self) -> None:
        with tempfile.TemporaryDirectory() as temporary_directory:
            working_directory = Path(temporary_directory)
            self.run_goat(
                working_directory,
                (
                    "todo read book\n"
                    "deadline return book /by 2026-06-06\n"
                    "event project meeting /from 2026-08-06 /to 2026-08-07\n"
                    "mark 1\n"
                    "delete 2\n"
                    "bye\n"
                ),
            )

            saved_data = (working_directory / "data/goat.txt").read_text(encoding="utf-8")
            self.assertEqual(
                saved_data,
                "T | 1 | read book\n"
                "E | 0 | project meeting | 2026-08-06 | 2026-08-07\n",
            )

            loaded_session = self.run_goat(working_directory, "list\nbye\n")
            self.assertIn("1.[T][X] read book", loaded_session.stdout)
            self.assertIn(
                "2.[E][ ] project meeting (from: Aug 06 2026 to: Aug 07 2026)",
                loaded_session.stdout,
            )

    def test_pipe_and_backslash_characters_round_trip(self) -> None:
        with tempfile.TemporaryDirectory() as temporary_directory:
            working_directory = Path(temporary_directory)
            self.run_goat(working_directory, "todo read | C:\\notes\nbye\n")

            saved_data = (working_directory / "data/goat.txt").read_text(encoding="utf-8")
            self.assertEqual(saved_data, "T | 0 | read \\| C:\\\\notes\n")
            loaded_session = self.run_goat(working_directory, "list\nbye\n")
            self.assertIn("1.[T][ ] read | C:\\notes", loaded_session.stdout)

    def test_malformed_data_shows_warning_and_starts_empty(self) -> None:
        malformed_records = (
            "D | maybe | broken",
            "X | 0 | unknown type",
            "D | 0 | missing date",
            "D | 0 | invalid date | Friday",
            "T | 0 |",
            "T",
        )
        for malformed_record in malformed_records:
            with self.subTest(record=malformed_record):
                with tempfile.TemporaryDirectory() as temporary_directory:
                    working_directory = Path(temporary_directory)
                    data_directory = working_directory / "data"
                    data_directory.mkdir()
                    (data_directory / "goat.txt").write_text(
                        malformed_record + "\n", encoding="utf-8"
                    )

                    result = self.run_goat(working_directory, "list\nbye\n")

                    self.assertIn(
                        "I couldn't load saved tasks: Invalid data on line 1", result.stdout
                    )
                    self.assertIn("Starting with an empty task list.", result.stdout)
                    self.assertIn("Here are the tasks in your list:\n_", result.stdout)

    def test_failed_save_rolls_back_task_change(self) -> None:
        with tempfile.TemporaryDirectory() as temporary_directory:
            working_directory = Path(temporary_directory)
            (working_directory / "data").write_text("not a directory", encoding="utf-8")

            result = self.run_goat(working_directory, "todo read book\nlist\nbye\n")

            self.assertIn("I couldn't save your tasks:", result.stdout)
            self.assertIn("Your task list was not changed.", result.stdout)
            self.assertIn("Here are the tasks in your list:\n_", result.stdout)


if __name__ == "__main__":
    unittest.main()
