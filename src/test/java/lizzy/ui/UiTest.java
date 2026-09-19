package lizzy.ui;

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
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import lizzy.task.Deadline;
import lizzy.task.Event;
import lizzy.task.Task;
import lizzy.task.TaskList;
import lizzy.task.Todo;
import lizzy.task.WithinPeriodTask;

/**
 * Tests console input normalization and every user-facing output branch in {@link Ui}.
 */
public class UiTest {
    @Test
    void readCommand_commandsAvailable_whitespaceRemovedAndEndDetected() {
        InputStream inputStream = new ByteArrayInputStream("  list  \nbye\n".getBytes(StandardCharsets.UTF_8));
        Ui ui = new Ui(inputStream, new PrintStream(new ByteArrayOutputStream()));

        assertTrue(ui.hasNextCommand());
        assertEquals("list", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    void showSessionMessages_expectedTextWritten() {
        String welcome = captureOutput(Ui::showWelcome);
        String goodbye = captureOutput(Ui::showGoodbye);
        String help = captureOutput(Ui::showHelp);
        String error = captureOutput(ui -> ui.showError("A recoverable error."));
        String divider = captureOutput(Ui::showDivider);

        assertTrue(welcome.contains("Hello! I'm Lizzy.\nWhat brings you here today?\n"
                + "Type help whenever you would like a quick command guide."));
        assertEquals("Goodbye! May your plans prosper—and leave you a little leisure.\n", goodbye);
        assertTrue(help.contains("A brief guide, should memory prove uncooperative:"));
        assertTrue(help.contains("Add tasks:\n  • todo <description> — undated task"));
        assertTrue(help.contains("View tasks:\n  • list — show every task"));
        assertTrue(help.contains("Manage tasks:\n  • mark <number> — complete a task"));
        assertTrue(help.contains("event <description> /on <yyyy-MM-dd> — one-day event"));
        assertTrue(help.contains("within <description> /from <yyyy-MM-dd> /to <yyyy-MM-dd>"
                + " — flexible-period task"));
        assertTrue(help.contains("Dates use yyyy-MM-dd (for example, 2026-09-10)"));
        assertTrue(help.contains("bye — close Lizzy"));
        assertEquals("A recoverable error.\n", error);
        assertEquals("____________________________________________________________\n", divider);
    }

    @Test
    void showTaskList_emptyAndPopulated_correctBranchesWritten() {
        TaskList emptyTasks = new TaskList();
        TaskList populatedTasks = new TaskList(new Todo("read chapter"),
                new Deadline("submit report", LocalDate.of(2026, 9, 20)));

        String emptyOutput = captureOutput(ui -> ui.showTaskList(emptyTasks));
        String populatedOutput = captureOutput(ui -> ui.showTaskList(populatedTasks));

        assertEquals("Your list is blissfully free of obligations.\n", emptyOutput);
        assertEquals("Let us see what has found its way onto your list:\n"
                + "1.[T][ ] read chapter\n"
                + "2.[D][ ] submit report (by: Sep 20 2026)\n", populatedOutput);
    }

    @Test
    void showMatchingTasks_emptyAndPopulated_correctBranchesWritten() {
        TaskList tasks = new TaskList(new Todo("read chapter"), new Todo("write tests"));

        String emptyOutput = captureOutput(ui -> ui.showMatchingTasks(tasks, List.of()));
        String populatedOutput = captureOutput(ui -> ui.showMatchingTasks(tasks, List.of(2)));

        assertEquals("Not a single task answers to that description.\n", emptyOutput);
        assertEquals("These tasks answer to your search:\n2.[T][ ] write tests\n", populatedOutput);
    }

    @Test
    void showAddedTasks_allTypesAndCounts_correctTextWritten() {
        Todo todo = new Todo("read chapter");
        Deadline deadline = new Deadline("submit report", LocalDate.of(2026, 9, 20));
        Event event = new Event("conference", LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 21));
        WithinPeriodTask periodTask = new WithinPeriodTask("collect certificate",
                LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 22));

        assertEquals("Here comes another matter to keep track of:\n"
                + "  [T][ ] read chapter\nThat makes 1 task awaiting your attention.\n",
                captureOutput(ui -> ui.showTodoAdded(todo, 1)));
        assertTrue(captureOutput(ui -> ui.showDeadlineAdded(deadline, 2))
                .contains("That makes 2 tasks awaiting your attention."));
        assertTrue(captureOutput(ui -> ui.showEventAdded(event, 3))
                .contains("[E][ ] conference (from: Sep 20 2026 to: Sep 21 2026)"));
        assertTrue(captureOutput(ui -> ui.showPeriodTaskAdded(periodTask, 4))
                .contains("[W][ ] collect certificate (within: Sep 20 2026 to: Sep 22 2026)"));
    }

    @Test
    void showTaskStateChanges_expectedTextWritten() {
        Task task = new Todo("read chapter");
        task.markAsDone();

        String markedOutput = captureOutput(ui -> ui.showTaskMarked(task));
        task.markAsNotDone();
        String unmarkedOutput = captureOutput(ui -> ui.showTaskUnmarked(task));
        String deletedOutput = captureOutput(ui -> ui.showTaskDeleted(task, 0));

        assertEquals("Very good! That is one matter settled:\n  [T][X] read chapter\n", markedOutput);
        assertEquals("Ah, it seems this matter is not quite settled:\n  [T][ ] read chapter\n", unmarkedOutput);
        assertEquals("And away it goes—one less matter on the list:\n  [T][ ] read chapter\n"
                + "That makes 0 tasks awaiting your attention.\n", deletedOutput);
    }

    @Test
    void showTasksOnDate_matchesAndNoMatches_correctBranchesWritten() {
        TaskList tasks = new TaskList(
                new Todo("undated"),
                new Deadline("submit report", LocalDate.of(2026, 9, 20)),
                new Event("conference", LocalDate.of(2026, 9, 19), LocalDate.of(2026, 9, 21)));

        String matchingOutput = captureOutput(ui -> ui.showTasksOnDate(tasks, LocalDate.of(2026, 9, 20)));
        String emptyOutput = captureOutput(ui -> ui.showTasksOnDate(tasks, LocalDate.of(2026, 9, 22)));

        assertEquals("On Sep 20 2026, your schedule has the following in store:\n"
                + "2.[D][ ] submit report (by: Sep 20 2026)\n"
                + "3.[E][ ] conference (from: Sep 19 2026 to: Sep 21 2026)\n", matchingOutput);
        assertEquals("Sep 22 2026 appears to make no demands upon you.\n", emptyOutput);
    }

    /**
     * Returns the text written by one operation on a UI with isolated streams.
     *
     * @param operation the UI output operation to run
     * @return the emitted UTF-8 text with LF line separators
     */
    private static String captureOutput(Consumer<Ui> operation) {
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        try (PrintStream outputStream = new PrintStream(outputBytes, true, StandardCharsets.UTF_8)) {
            Ui ui = new Ui(InputStream.nullInputStream(), outputStream);
            operation.accept(ui);
        }
        return outputBytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
