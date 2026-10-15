package slotbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import slotbot.task.Deadline;
import slotbot.task.TaskList;
import slotbot.task.Todo;

public class UiTest {
    @Test
    public void showLines_noArguments_producesNoOutput() {
        StringBuilder output = new StringBuilder();
        new Ui(output::append).showLines();

        assertEquals("", output.toString());
    }

    @Test
    public void showLines_oneLine_appendsLineSeparator() {
        StringBuilder output = new StringBuilder();
        new Ui(output::append).showLines("100% complete");

        assertEquals("100% complete" + System.lineSeparator(), output.toString());
    }

    @Test
    public void showLines_multipleLines_preservesOrderAndBlankLine() {
        StringBuilder output = new StringBuilder();
        new Ui(output::append).showLines("first", "", "last");

        String newline = System.lineSeparator();
        assertEquals("first" + newline + newline + "last" + newline, output.toString());
    }

    @Test
    public void showLoadError_twoLines_preservesWarningText() {
        StringBuilder output = new StringBuilder();
        new Ui(output::append).showLoadError();

        String newline = System.lineSeparator();
        assertEquals("Warning: Unable to load saved tasks." + newline
                + "Starting with an empty list." + newline, output.toString());
    }

    @Test
    public void commandInput_availableLines_readsInOrder() {
        InputStream originalInput = System.in;
        try {
            System.setIn(new ByteArrayInputStream("list\nbye\n".getBytes(StandardCharsets.UTF_8)));
            Ui ui = new Ui();

            assertTrue(ui.hasNextCommand());
            assertEquals("list", ui.readCommand());
            assertTrue(ui.hasNextCommand());
            assertEquals("bye", ui.readCommand());
            assertFalse(ui.hasNextCommand());
        } finally {
            System.setIn(originalInput);
        }
    }

    @Test
    public void showWelcomeGoodbyeAndError_containsExpectedMessages() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);

        ui.showWelcome();
        ui.showGoodbye();
        ui.showError("Invalid command");

        assertTrue(output.toString().contains("Hello! I'm SlotBot."));
        assertTrue(output.toString().contains("All done. See you next time!"));
        assertTrue(output.toString().contains("Invalid command"));
    }

    @Test
    public void showTaskChanges_containsTasksCountsAndStatuses() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);
        Todo task = new Todo("read book");

        ui.showAddedTask(task, 1);
        task.markDone();
        ui.showTaskStatusChanged(task, true);
        task.markUndone();
        ui.showTaskStatusChanged(task, false);
        ui.showDeletedTask(task, 0);

        assertTrue(output.toString().contains("added this task"));
        assertTrue(output.toString().contains("Now you have 1 tasks"));
        assertTrue(output.toString().contains("Nice! We got one."));
        assertTrue(output.toString().contains("marked this task as not done"));
        assertTrue(output.toString().contains("removed this task"));
        assertTrue(output.toString().contains("Now you have 0 tasks"));
    }

    @Test
    public void showTaskCollections_containsNumberedTasksAndEmptyReminderMessage() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("submit", LocalDate.of(2026, 9, 18));

        TaskList tasks = new TaskList(List.of(todo, deadline));
        ui.showTaskList(tasks);
        ui.showMatchingTasks(tasks, List.of(1), "submit");
        ui.showReminders(List.of(deadline), 7);
        ui.showReminders(List.of(), 7);

        assertTrue(output.toString().contains("Here are the tasks in your list:"));
        assertTrue(output.toString().contains("1. [T][ ] read book"));
        assertTrue(output.toString().contains("2. [D][ ] submit"));
        assertTrue(output.toString().contains("Here are the matching tasks"));
        assertTrue(output.toString().contains("Upcoming reminders"));
        assertTrue(output.toString().contains("No upcoming deadlines."));
    }

    @Test
    public void showMatchingTasks_emptyList_showsSearchKeyword() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);

        ui.showMatchingTasks(new TaskList(List.of()), List.of(), "book");

        assertTrue(output.toString().contains("No tasks match \"book\"."));
        assertFalse(output.toString().contains("Here are the matching tasks"));
    }

    @Test
    public void showStorageWarnings_containsExpectedMessagesAndLineNumber() {
        StringBuilder output = new StringBuilder();
        Ui ui = new Ui(output::append);

        ui.showSaveError();
        ui.showInvalidTaskDataWarning(3);

        assertTrue(output.toString().contains("Unable to save tasks to disk"));
        assertTrue(output.toString().contains("invalid task data on line 3"));
    }
}
