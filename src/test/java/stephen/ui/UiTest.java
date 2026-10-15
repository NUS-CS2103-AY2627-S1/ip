package stephen.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import stephen.task.Deadline;
import stephen.task.Event;
import stephen.task.Task;
import stephen.task.TaskList;
import stephen.task.Todo;

/** Tests console input normalization and every user-facing output branch. */
class UiTest {
    private InputStream originalInput;
    private PrintStream originalOutput;
    private ByteArrayOutputStream output;

    /** Redirects standard streams so each test can inspect console behavior. */
    @BeforeEach
    void setUpStreams() {
        originalInput = System.in;
        originalOutput = System.out;
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    /** Restores global streams after each test. */
    @AfterEach
    void restoreStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    /** Verifies that input availability and whitespace trimming are predictable. */
    @Test
    void readCommandInputWithWhitespaceReturnsTrimmedCommand() {
        System.setIn(new ByteArrayInputStream("  list  \n".getBytes(StandardCharsets.UTF_8)));
        Ui ui = new Ui();

        assertTrue(ui.hasNextInput());
        assertEquals("list", ui.readCommand());
        assertFalse(ui.hasNextInput());
    }

    /** Verifies Orbit's greeting, error prefix, dividers, and farewell. */
    @Test
    void showSessionMessagesWritesExactOutput() {
        Ui ui = new Ui();

        ui.showWelcome();
        ui.showError("Test alert");
        ui.showGoodbye();

        assertEquals("Orbit online.\n"
                + "Ready to plan your next move?\n\n"
                + "________________________________________________\n"
                + "Navigation alert: Test alert\n\n"
                + "________________________________________________\n"
                + "Orbit signing off. Keep moving forward!\n\n"
                + "________________________________________________\n", normalizedOutput());
    }

    /** Verifies list and task-change messages, including both mark branches. */
    @Test
    void showTaskOperationsWritesExpectedMessages() {
        Ui ui = new Ui();
        Todo task = new Todo("read book");
        TaskList tasks = new TaskList(List.of(task));

        ui.showTaskList(tasks);
        ui.showTasksSorted(tasks);
        ui.showTaskAdded(task, 1);
        ui.showTaskDeleted(task, 0);
        ui.showTaskMarked(task, true);
        ui.showTaskMarked(task, false);

        assertEquals(List.of(
                "Current mission plan:",
                "1.[T][ ] read book",
                "Mission plan sorted alphabetically:",
                "1.[T][ ] read book",
                "Mission logged. I've added this task:",
                "  [T][ ] read book",
                "You now have 1 task on the radar.",
                "Course adjusted. I've removed this task:",
                "  [T][ ] read book",
                "You now have 0 tasks on the radar.",
                "Milestone reached! This task is complete:",
                "[T][ ] read book",
                "Task reopened. It's back on the radar:",
                "[T][ ] read book"), outputLines());
    }

    /** Verifies matching and empty schedule output branches. */
    @Test
    void showScheduleMatchingAndEmptyDatesWritesExpectedMessages() {
        Ui ui = new Ui();
        TaskList tasks = new TaskList(List.of(
                new Todo("undated"),
                new Deadline("submit", LocalDate.of(2024, 6, 15)),
                new Event("conference", LocalDate.of(2024, 6, 14),
                        LocalDate.of(2024, 6, 16))));

        ui.showSchedule(tasks, LocalDate.of(2024, 6, 15));
        ui.showSchedule(tasks, LocalDate.of(2024, 6, 20));

        assertEquals(List.of(
                "On the radar for Jun 15 2024:",
                "2.[D][ ] submit (by: Jun 15 2024)",
                "3.[E][ ] conference (from: Jun 14 2024 to: Jun 16 2024)",
                "Clear skies - no deadlines or events on Jun 20 2024."), outputLines());
    }

    /** Verifies singular, plural, and empty search-result output branches. */
    @Test
    void showFindResultsAllResultCountsWritesExpectedMessages() {
        Ui ui = new Ui();
        List<Task> matches = List.of(new Todo("read book"), new Todo("return book"));

        ui.showFindResults(matches.subList(0, 1), "read");
        ui.showFindResults(matches, "book");
        ui.showFindResults(List.of(), "missing");

        assertEquals(List.of(
                "Scan found 1 matching task:",
                "1.[T][ ] read book",
                "Scan found 2 matching tasks:",
                "1.[T][ ] read book",
                "2.[T][ ] return book",
                "Scan clear - no tasks match \"missing\"."), outputLines());
    }

    /** Returns captured console output with platform line endings normalized. */
    private String normalizedOutput() {
        return output.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    /** Returns captured non-empty output lines. */
    private List<String> outputLines() {
        return normalizedOutput().lines().toList();
    }
}
