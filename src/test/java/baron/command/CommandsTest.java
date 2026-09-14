package baron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import baron.data.BaronState;
import baron.exception.BaronException;
import baron.exception.FormatException;
import baron.exception.IndexException;
import baron.task.Deadline;
import baron.task.Event;
import baron.task.Task;
import baron.task.Todo;

class CommandsTest {
    private static final Path SAVE_FILE = Path.of("./data/tasks.json");

    private boolean hasSaveFile;
    private byte[] originalSaveFile;

    @BeforeEach
    void setUp() throws IOException {
        hasSaveFile = Files.exists(SAVE_FILE);
        if (hasSaveFile) {
            originalSaveFile = Files.readAllBytes(SAVE_FILE);
        }
        Files.deleteIfExists(SAVE_FILE);
        BaronState.init();
    }

    @AfterEach
    void restoreSaveFile() throws IOException {
        if (hasSaveFile) {
            Files.write(SAVE_FILE, originalSaveFile);
        } else {
            Files.deleteIfExists(SAVE_FILE);
        }
    }

    @Test
    void todo_validDescription_addsTodoTask() throws BaronException {
        Commands.TODO.execute("read book");

        assertEquals(1, BaronState.getTasks().size());
        Task task = BaronState.getTasks().get(0);
        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getName());
    }

    @Test
    void todo_descriptionContainingPipe_addsTodoTask() throws BaronException {
        Commands.TODO.execute("Plan | review");

        assertEquals("Plan | review", BaronState.getTasks().get(0).getName());
    }

    @Test
    void todo_emptyDescription_throwsFormatException() {
        assertThrows(FormatException.class, () -> Commands.TODO.execute(""));
        assertTrue(BaronState.getTasks().isEmpty());
    }

    @Test
    void deadline_validDescriptionAndDate_addsDeadlineTask() throws BaronException {
        Commands.DEADLINE.execute("submit report /by 2025-08-20 14:30");

        assertEquals(1, BaronState.getTasks().size());
        Task task = BaronState.getTasks().get(0);
        assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", task.getName());
    }

    //CHECKSTYLE.OFF: SeparatorWrap
    @Test
    void deadline_missingByFormat_throwsFormatException() {
        assertThrows(
                FormatException.class,
                () -> Commands.DEADLINE.execute("submit report 2025-08-20"));
        assertTrue(BaronState.getTasks().isEmpty());
    }

    @Test
    void deadline_invalidDate_throwsBaronException() {
        assertThrows(
                BaronException.class,
                () -> Commands.DEADLINE.execute("submit report /by not a date"));
        assertTrue(BaronState.getTasks().isEmpty());
    }

    @Test
    void event_validDescriptionAndTimes_addsEventTask() throws BaronException {
        Commands.EVENT.execute("team meeting /from 2025-08-21 10:00 /to 2025-08-21 11:00");

        assertEquals(1, BaronState.getTasks().size());
        Task task = BaronState.getTasks().get(0);
        assertInstanceOf(Event.class, task);
        assertEquals("team meeting", task.getName());
    }

    @Test
    void event_missingTimeFormat_throwsFormatException() {
        assertThrows(
                FormatException.class,
                () -> Commands.EVENT.execute("team meeting /from 2025-08-21 10:00"));
        assertTrue(BaronState.getTasks().isEmpty());
    }
    //CHECKSTYLE.ON: SeparatorWrap

    @Test
    void markThenUnmark_validTask_updatesCompletionStatus() throws BaronException {
        BaronState.addTask(new Todo("read book"));

        Commands.MARK.execute("1");
        assertTrue(BaronState.getTasks().get(0).isDone());

        Commands.UNMARK.execute("1");
        assertFalse(BaronState.getTasks().get(0).isDone());
    }

    @Test
    void delete_validTaskNumber_deletesSpecifiedTask() throws BaronException {
        BaronState.addTask(new Todo("keep this task"));
        BaronState.addTask(new Todo("delete this task"));

        Commands.DELETE.execute("2");

        ArrayList<Task> remainingTasks = BaronState.getTasks();
        assertEquals(1, remainingTasks.size());
        assertEquals("keep this task", remainingTasks.get(0).getName());
    }

    @Test
    void delete_nonNumericTaskNumber_throwsFormatException() throws BaronException {
        BaronState.addTask(new Todo("keep this task"));

        assertThrows(FormatException.class, () -> Commands.DELETE.execute("one"));
        assertEquals(1, BaronState.getTasks().size());
    }

    @Test
    void delete_outOfRangeTaskNumber_throwsIndexException() throws BaronException {
        BaronState.addTask(new Todo("keep this task"));

        assertThrows(IndexException.class, () -> Commands.DELETE.execute("2"));
        assertThrows(IndexException.class, () -> Commands.DELETE.execute("0"));
        assertEquals(List.of("keep this task"),
                BaronState.getTasks().stream().map(Task::getName).toList());
    }

    @Test
    void find_matchingKeyword_printsMatchingTasks() throws BaronException {
        BaronState.addTask(new Todo("read book"));
        BaronState.addTask(new Todo("write report"));
        BaronState.addTask(new Todo("read notes"));

        String output = Commands.FIND.execute("read");

        assertEquals("The ledger reveals these matching tasks:\n"
            + "1.[T][ ] read book\n"
            + "2.[T][ ] read notes", output);
    }

    @Test
    void find_nonMatchingKeyword_printsNoTasks() throws BaronException {
        BaronState.addTask(new Todo("read book"));

        String output = Commands.FIND.execute("exercise");
        assertEquals("No matching task answers the call. Try another name or keyword.", output);
    }

    @Test
    void find_emptyKeyword_throwsFormatException() {
        assertThrows(FormatException.class, () -> Commands.FIND.execute(""));
    }

    @Test
    void reminder_tasksWithDeadlines_returnsTasksFromEarliestToLatest() throws BaronException {
        Commands.DEADLINE.execute("later task /by 2025-08-22 14:00");
        Commands.EVENT.execute("earlier event /from 2025-08-20 09:00 /to 2025-08-20 10:00");
        Commands.DEADLINE.execute("earliest task /by 2025-08-19 18:30");

        String output = Commands.REMINDER.execute("");

        assertEquals("These tasks demand your attention soon:\n"
                + "1.[D][ ] earliest task (by: Aug 19 2025 18:30)\n"
                + "2.[E][ ] earlier event (from: Aug 20 2025 09:00 to: Aug 20 2025 10:00)\n"
                + "3.[D][ ] later task (by: Aug 22 2025 14:00)\n", output);
    }

    @Test
    void reminder_includesOnlyTasksWithDeadlines() throws BaronException {
        Commands.TODO.execute("task without a deadline");
        Commands.DEADLINE.execute("task with a deadline /by 2025-08-20");

        String output = Commands.REMINDER.execute("");

        assertEquals("These tasks demand your attention soon:\n"
                + "1.[D][ ] task with a deadline (by: Aug 20 2025 00:00)\n", output);
    }

    @Test
    void reminder_noTasksWithDeadlines_returnsEmptyMessage() throws BaronException {
        Commands.TODO.execute("task without a deadline");

        String output = Commands.REMINDER.execute("");

        assertEquals("The horizon is clear for now. No tasks are due soon.", output);
    }

    @Test
    void reminder_excludesCompletedTasks() throws BaronException {
        Commands.DEADLINE.execute("completed task /by 2025-08-19");
        Commands.DEADLINE.execute("pending task /by 2025-08-20");
        Commands.MARK.execute("1");

        String output = Commands.REMINDER.execute("");

        assertEquals("These tasks demand your attention soon:\n"
                + "1.[D][ ] pending task (by: Aug 20 2025 00:00)\n", output);
    }
}
