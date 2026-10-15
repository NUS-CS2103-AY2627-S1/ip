package duncan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

public class RescheduleCommandTest {
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
    public void execute_deadlineWithBy_dateChangedAndOldAndNewShown() throws DuncanException {
        TaskList tasks = listOf(new Deadline("return book", LocalDate.of(2019, 12, 2)));
        Ui ui = new Ui();

        new RescheduleCommand(0, LocalDate.of(2019, 12, 9), null, null).execute(tasks, ui, newStorage());

        assertEquals("OK, I've rescheduled this task:" + NEWLINE
                + "  from: [D][ ] return book (by: Dec 2 2019)" + NEWLINE
                + "  to:   [D][ ] return book (by: Dec 9 2019)" + NEWLINE,
                ui.flushOutput());
    }

    @Test
    public void execute_eventWithFromAndTo_datesChanged() throws DuncanException {
        TaskList tasks = listOf(new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2)));

        new RescheduleCommand(0, null, LocalDate.of(2019, 12, 5), LocalDate.of(2019, 12, 6))
                .execute(tasks, new Ui(), newStorage());

        assertEquals("[E][ ] project fair (from: Dec 5 2019 to: Dec 6 2019)", tasks.get(0).toString());
    }

    @Test
    public void execute_doneDeadline_stillDone() throws DuncanException {
        Deadline deadline = new Deadline("return book", LocalDate.of(2019, 12, 2));
        deadline.markAsDone();
        TaskList tasks = listOf(deadline);

        new RescheduleCommand(0, LocalDate.of(2019, 12, 9), null, null).execute(tasks, new Ui(), newStorage());

        assertEquals("[D][X] return book (by: Dec 9 2019)", tasks.get(0).toString());
    }

    @Test
    public void execute_validReschedule_newDateSaved() throws DuncanException {
        TaskList tasks = listOf(new Deadline("return book", LocalDate.of(2019, 12, 2)));
        Storage storage = newStorage();

        new RescheduleCommand(0, LocalDate.of(2019, 12, 9), null, null).execute(tasks, new Ui(), storage);

        assertEquals("[D][ ] return book (by: Dec 9 2019)", storage.load().get(0).toString());
    }

    @Test
    public void execute_todo_exceptionThrown() {
        TaskList tasks = listOf(new Todo("read book"));
        RescheduleCommand command = new RescheduleCommand(0, LocalDate.of(2019, 12, 9), null, null);
        Ui ui = new Ui();
        Storage storage = newStorage();

        DuncanException e = assertThrows(DuncanException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("HEY! only deadlines and events can be rescheduled", e.getMessage());
    }

    @Test
    public void execute_deadlineWithFromAndToOnly_exceptionThrownAndTaskUnchanged() {
        TaskList tasks = listOf(new Deadline("return book", LocalDate.of(2019, 12, 2)));
        RescheduleCommand command =
                new RescheduleCommand(0, null, LocalDate.of(2019, 12, 5), LocalDate.of(2019, 12, 6));
        Ui ui = new Ui();
        Storage storage = newStorage();

        DuncanException e = assertThrows(DuncanException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("HEY! deadlines must have /by <date/time>", e.getMessage());
        assertEquals("[D][ ] return book (by: Dec 2 2019)", tasks.get(0).toString());
    }

    @Test
    public void execute_eventWithByOnly_exceptionThrownAndTaskUnchanged() {
        TaskList tasks = listOf(new Event("project fair", LocalDate.of(2019, 12, 1), LocalDate.of(2019, 12, 2)));
        RescheduleCommand command = new RescheduleCommand(0, LocalDate.of(2019, 12, 9), null, null);
        Ui ui = new Ui();
        Storage storage = newStorage();

        DuncanException e = assertThrows(DuncanException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("HEY! events must use /from and /to <date/time>", e.getMessage());
        assertEquals("[E][ ] project fair (from: Dec 1 2019 to: Dec 2 2019)", tasks.get(0).toString());
    }

    @Test
    public void execute_indexOutOfRange_exceptionThrown() {
        TaskList tasks = listOf(new Deadline("return book", LocalDate.of(2019, 12, 2)));
        RescheduleCommand command = new RescheduleCommand(5, LocalDate.of(2019, 12, 9), null, null);
        Ui ui = new Ui();
        Storage storage = newStorage();

        DuncanException e = assertThrows(DuncanException.class, () -> command.execute(tasks, ui, storage));

        assertEquals("HEY! this task number is bad", e.getMessage());
    }
}
