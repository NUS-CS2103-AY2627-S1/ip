package monkey.command;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import monkey.model.Deadline;
import monkey.model.Event;
import monkey.model.TaskList;
import monkey.model.ToDos;
import monkey.storage.Storage;
import monkey.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class SnoozeCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_futureDeadline_updatesDeadlineAndStorage() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("keep position"));
        tasks.add(new Deadline("submit report", "2099-01-01"));
        Path saveFile = temporaryDirectory.resolve("monkey.txt");
        StringBuilder output = new StringBuilder();

        new SnoozeCommand("2 2099-02-01").execute(
                tasks, createUi(output), new Storage(saveFile.toString()));

        assertEquals("Snoozed this task to Feb 01 2099:\n"
                + "  [D][ ] submit report (by: Feb 01 2099)", output.toString());
        assertEquals("2099-02-01", ((Deadline) tasks.get(1)).getStorageValue());
        assertEquals("T | 0 | keep position\nD | 0 | submit report | 2099-02-01",
                Files.readString(saveFile).trim());
        assertEquals("keep position", tasks.get(0).getDescription());
    }

    @Test
    void execute_futureDateTime_displaysDateTime() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", "2099-01-01"));
        StringBuilder output = new StringBuilder();

        new SnoozeCommand("1 2099-02-01 1730").execute(
                tasks, createUi(output), new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("Snoozed this task to Feb 01 2099, 5:30 PM:\n"
                + "  [D][ ] submit report (by: Feb 01 2099, 5:30 PM)", output.toString());
    }

    @Test
    void execute_todoTask_rejectsSnooze() {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("buy milk"));
        StringBuilder output = new StringBuilder();

        new SnoozeCommand("1 2099-02-01").execute(
                tasks, createUi(output), new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("Only deadline tasks can be snoozed.", output.toString());
        assertFalse(Files.exists(temporaryDirectory.resolve("monkey.txt")));
    }

    @Test
    void execute_eventTask_rejectsSnooze() {
        TaskList tasks = new TaskList();
        tasks.add(new Event("meeting", "10am", "11am"));
        StringBuilder output = new StringBuilder();

        new SnoozeCommand("1 2099-02-01").execute(
                tasks, createUi(output), new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("Only deadline tasks can be snoozed.", output.toString());
    }

    @Test
    void execute_completedDeadline_rejectsSnooze() {
        TaskList tasks = new TaskList();
        Deadline deadline = new Deadline("submitted report", "2099-01-01");
        deadline.markAsDone();
        tasks.add(deadline);
        StringBuilder output = new StringBuilder();

        new SnoozeCommand("1 2099-02-01").execute(
                tasks, createUi(output), new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("Completed tasks cannot be snoozed.", output.toString());
        assertEquals("2099-01-01", deadline.getStorageValue());
    }

    @Test
    void execute_invalidTaskNumber_rejectsInput() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", "2099-01-01"));
        StringBuilder output = new StringBuilder();

        new SnoozeCommand("abc 2099-02-01").execute(
                tasks, createUi(output), new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("OOPS! Monkey says: Please provide a valid task number after 'snooze'.",
                output.toString());
    }

    @Test
    void execute_outOfRangeTaskNumber_rejectsInput() {
        StringBuilder output = new StringBuilder();

        new SnoozeCommand("1 2099-02-01").execute(
                new TaskList(), createUi(output), new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("That task number does not exist.", output.toString());
    }

    @Test
    void constructor_nullArguments_reportsUsage() {
        StringBuilder output = new StringBuilder();

        new SnoozeCommand(null).execute(
                new TaskList(), createUi(output), new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("Use: snooze <task number> <date or date-time>.", output.toString());
    }

    @Test
    void execute_pastDeadline_rejectsInputAndPreservesOriginal() {
        TaskList tasks = new TaskList();
        Deadline deadline = new Deadline("submit report", "2099-01-01");
        tasks.add(deadline);
        StringBuilder output = new StringBuilder();

        new SnoozeCommand("1 2000-01-01").execute(
                tasks, createUi(output), new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("A snoozed deadline must be in the future.", output.toString());
        assertEquals("2099-01-01", deadline.getStorageValue());
    }

    @Test
    void execute_invalidDate_rejectsInputAndPreservesOriginal() {
        TaskList tasks = new TaskList();
        Deadline deadline = new Deadline("submit report", "2099-01-01");
        tasks.add(deadline);
        StringBuilder output = new StringBuilder();

        new SnoozeCommand("1 next-week").execute(
                tasks, createUi(output), new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("Use a date like yyyy-mm-dd or d/M/yyyy, or a date/time like d/M/yyyy HHmm.",
                output.toString());
        assertEquals("2099-01-01", deadline.getStorageValue());
    }

    @Test
    void execute_missingArguments_reportsUsage() {
        TaskList tasks = new TaskList();
        StringBuilder output = new StringBuilder();

        new SnoozeCommand("").execute(
                tasks, createUi(output), new Storage(temporaryDirectory.resolve("monkey.txt").toString()));

        assertEquals("Use: snooze <task number> <date or date-time>.", output.toString());
    }

    private static Ui createUi(StringBuilder output) {
        return new Ui(message -> {
            if (!output.isEmpty()) {
                output.append('\n');
            }
            output.append(message);
        });
    }
}
