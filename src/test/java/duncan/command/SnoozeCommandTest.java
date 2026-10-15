package duncan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.Deadline;
import duncan.task.Event;
import duncan.task.Task;
import duncan.task.TaskList;
import duncan.task.Todo;

public class SnoozeCommandTest {
    private static final String NEWLINE = System.lineSeparator();

    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("tasks.txt").toString());
    }

    private static TaskList listOf(Task task) {
        TaskList tasks = new TaskList();
        tasks.add(task);
        return tasks;
    }

    @Test
    public void execute_deadline_dateMovedAndOldAndNewShown() throws DuncanException {
        TaskList tasks = listOf(new Deadline("return book", LocalDate.of(2019, 12, 2)));
        Ui ui = new Ui();

        new SnoozeCommand(0, 7).execute(tasks, ui, newStorage());

        assertEquals("OK, I've snoozed this task by 7 days:" + NEWLINE
                + "  from: [D][ ] return book (by: Dec 2 2019)" + NEWLINE
                + "  to:   [D][ ] return book (by: Dec 9 2019)" + NEWLINE,
                ui.flushOutput());
    }

    @Test
    public void execute_event_bothDatesMoved() throws DuncanException {
        TaskList tasks = listOf(new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2)));

        new SnoozeCommand(0, 7).execute(tasks, new Ui(), newStorage());

        assertEquals("[E][ ] project fair (from: Dec 8 2019 to: Dec 9 2019)", tasks.get(0).toString());
    }

    @Test
    public void execute_doneEvent_stillDone() throws DuncanException {
        Event event = new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2));
        event.markAsDone();
        TaskList tasks = listOf(event);

        new SnoozeCommand(0, 1).execute(tasks, new Ui(), newStorage());

        assertEquals("[E][X] project fair (from: Dec 2 2019 to: Dec 3 2019)", tasks.get(0).toString());
    }

    @Test
    public void execute_validSnooze_newDateSaved() throws DuncanException {
        TaskList tasks = listOf(new Deadline("return book", LocalDate.of(2019, 12, 2)));
        Storage storage = newStorage();

        new SnoozeCommand(0, 7).execute(tasks, new Ui(), storage);

        assertEquals("[D][ ] return book (by: Dec 9 2019)", storage.load().get(0).toString());
    }

    @Test
    public void execute_todo_exceptionThrown() {
        TaskList tasks = listOf(new Todo("read book"));
        SnoozeCommand command = new SnoozeCommand(0, 7);
        Ui ui = new Ui();
        Storage storage = newStorage();

        DuncanException e = assertThrows(DuncanException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("HEY! only deadlines and events can be rescheduled", e.getMessage());
    }

    @Test
    public void execute_indexOutOfRange_exceptionThrown() {
        TaskList tasks = listOf(new Deadline("return book", LocalDate.of(2019, 12, 2)));
        SnoozeCommand command = new SnoozeCommand(5, 7);
        Ui ui = new Ui();
        Storage storage = newStorage();

        DuncanException e = assertThrows(DuncanException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("HEY! this task number is bad", e.getMessage());
    }

    @Test
    public void execute_deadlinePastLatestSupportedDate_exceptionThrownAndTaskUnchangedAndNotSaved() {
        TaskList tasks = listOf(new Deadline("return book", LocalDate.MAX));
        SnoozeCommand command = new SnoozeCommand(0, 1);
        Ui ui = new Ui();
        Storage storage = newStorage();

        DuncanException e = assertThrows(DuncanException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("HEY! the number of days is bad", e.getMessage());
        assertEquals("D\t0\treturn book\t" + LocalDate.MAX, tasks.get(0).toFileFormat());
        assertTrue(storage.load().isEmpty());
        assertEquals("", ui.flushOutput());
    }

    @Test
    public void execute_eventEndPastLatestSupportedDate_exceptionThrownAndBothDatesUnchanged() {
        LocalDate from = LocalDate.MAX.minusDays(10);
        TaskList tasks = listOf(new Event("project fair", from, LocalDate.MAX));
        SnoozeCommand command = new SnoozeCommand(0, 5);
        Ui ui = new Ui();
        Storage storage = newStorage();

        DuncanException e = assertThrows(DuncanException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("HEY! the number of days is bad", e.getMessage());
        assertEquals("E\t0\tproject fair\t" + from + "\t" + LocalDate.MAX, tasks.get(0).toFileFormat());
    }
}
