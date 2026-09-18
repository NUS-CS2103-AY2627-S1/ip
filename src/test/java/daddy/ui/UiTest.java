package daddy.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import daddy.task.Deadline;
import daddy.task.Task;
import daddy.task.TaskList;
import daddy.task.Todo;

/**
 * Tests console input handling and the text produced by user-interface methods.
 */
class UiTest {
    /** Verifies that the graphical-output UI cannot be used to read console commands. */
    @Test
    void readCommand_outputOnlyUi_throwsIllegalStateException() {
        Ui ui = new Ui(ignored -> { });

        assertFalse(ui.hasNextCommand());
        assertThrows(IllegalStateException.class, ui::readCommand);
        ui.close();
    }

    /** Verifies console input, banner formatting, and exhaustion detection using isolated streams. */
    @Test
    void consoleUi_commandAndGreeting_readsAndFormatsConsoleSession() {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        ByteArrayInputStream testInput = new ByteArrayInputStream("list\n".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream testOutput = new ByteArrayOutputStream();
        try {
            System.setIn(testInput);
            System.setOut(new PrintStream(testOutput, true, StandardCharsets.UTF_8));
            Ui ui = new Ui();

            assertTrue(ui.hasNextCommand());
            assertEquals("list", ui.readCommand());
            assertFalse(ui.hasNextCommand());
            ui.showGreeting();
            ui.close();
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String output = testOutput.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("____________________________________________________________"));
        assertTrue(output.contains(" ____                      _       _"));
        assertTrue(output.contains("Hello, my friend."));
    }

    /** Verifies greeting, error, and exit messages without console-only decoration. */
    @Test
    void simpleMessages_outputOnlyUi_emitExpectedLines() {
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showGreeting();
        ui.showError("Something went wrong.");
        ui.showExit();

        assertEquals(List.of(
                "Hello, my friend.",
                "Big dreams require big actions. What are we getting done today?",
                " Something went wrong.",
                "Go make Daddy proud, my friend. See you soon."), output);
    }

    /** Verifies full-list, date-filtered, and keyword-filtered task output. */
    @Test
    void taskCollections_populatedAndEmpty_emitNumberedOrEmptyMessages() {
        LocalDate date = LocalDate.of(2026, 12, 2);
        Task todo = new Todo("read book");
        Task deadline = new Deadline("return book", date.atTime(18, 0));
        deadline.markAsDone();
        TaskList taskList = new TaskList();
        taskList.add(todo, deadline);
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showTaskList(taskList);
        assertEquals(List.of(
                " Here's the game plan, my friend:",
                " 1.[T][ ] read book",
                " 2.[D][X] return book (by: Dec 2 2026 18:00)"), output);

        output.clear();
        ui.showTasksOnDate(date, List.of(deadline));
        assertEquals(List.of(
                " Here's the programme for 2026-12-02:",
                " 1.[D][X] return book (by: Dec 2 2026 18:00)"), output);

        output.clear();
        ui.showTasksOnDate(date, List.of());
        assertEquals(List.of(
                " Here's the programme for 2026-12-02:",
                " That day is clear. Use it wisely."), output);

        output.clear();
        ui.showMatchingTasks(List.of(todo));
        assertEquals(List.of(
                " Here are your matching tasks, my friend:",
                " 1.[T][ ] read book"), output);

        output.clear();
        ui.showMatchingTasks(List.of());
        assertEquals(List.of(
                " Here are your matching tasks, my friend:",
                " No match, my friend. Try another keyword."), output);
    }

    /** Verifies hour and minute free-slot durations are formatted appropriately. */
    @Test
    void showFreeTime_hourAndMinuteDurations_emitExpectedLabels() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 14, 10, 0);
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showFreeTime(start, Duration.ofHours(4));
        assertTrue(output.contains(" That's a 4-hour slot. Protect it, then get back to the mission."));

        output.clear();
        ui.showFreeTime(start, Duration.ofMinutes(90));
        assertTrue(output.contains(" That's a 90-minute slot. Protect it, then get back to the mission."));
    }

    /** Verifies add, mark, unmark, and delete confirmations including singular and plural grammar. */
    @Test
    void taskChangeMessages_variedCounts_emitExpectedDescriptionsAndGrammar() {
        Task task = new Todo("read book");
        List<String> output = new ArrayList<>();
        Ui ui = new Ui(output::add);

        ui.showTaskAdded(task, 1);
        assertTrue(output.contains(" That's 1 task in the list. Small steps, real progress."));

        output.clear();
        ui.showTaskAdded(task, 2);
        assertTrue(output.contains(" That's 2 tasks in the list. Small steps, real progress."));

        output.clear();
        task.markAsDone();
        ui.showTaskMarked(task);
        assertTrue(output.contains("   [X] read book"));

        output.clear();
        task.markAsNotDone();
        ui.showTaskUnmarked(task);
        assertTrue(output.contains("   [ ] read book"));

        output.clear();
        ui.showTaskDeleted(task, 1);
        assertTrue(output.contains(" 1 task remains. Focus on what matters."));

        output.clear();
        ui.showTaskDeleted(task, 0);
        assertTrue(output.contains(" 0 tasks remain. Focus on what matters."));
    }
}
