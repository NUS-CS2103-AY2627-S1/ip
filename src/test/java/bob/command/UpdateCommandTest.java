package bob.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import bob.exception.BobException;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.TaskList;
import bob.task.ToDo;

/**
 * Tests the behavior of {@link UpdateCommand}.
 */
public class UpdateCommandTest extends CommandTestBase {

    @Test
    public void execute_updateTodoDescription_updatesAndPreservesDone() throws BobException {
        ToDo todo = new ToDo("initial description");
        todo.mark();
        tasks.add(todo);

        UpdateCommand cmd = new UpdateCommand(1, "updated description", null, null, null);
        cmd.execute(tasks, ui, storage);

        assertEquals("[T][X] updated description", tasks.get(0).toString());

        TaskList loaded = storage.load();
        assertEquals("[T][X] updated description", loaded.get(0).toString());
    }

    @Test
    public void execute_updateDeadlineDescriptionAndBy_updatesBothFieldsAndPersists() throws BobException {
        LocalDateTime initialBy = LocalDateTime.of(2026, 11, 11, 18, 0);
        Deadline deadline = new Deadline("initial deadline", initialBy);
        tasks.add(deadline);

        LocalDateTime newBy = LocalDateTime.of(2026, 12, 12, 23, 59);
        UpdateCommand cmd = new UpdateCommand(1, "new deadline desc", newBy, null, null);
        cmd.execute(tasks, ui, storage);

        assertEquals("[D][ ] new deadline desc (by: 12 December 2026, 2359 hrs)", tasks.get(0).toString());

        TaskList loaded = storage.load();
        assertEquals("[D][ ] new deadline desc (by: 12 December 2026, 2359 hrs)", loaded.get(0).toString());
    }

    @Test
    public void execute_updateEventStartAndEnd_updatesBothTimesAndPersists() throws BobException {
        LocalDateTime initialFrom = LocalDateTime.of(2026, 11, 11, 14, 0);
        LocalDateTime initialTo = LocalDateTime.of(2026, 11, 11, 16, 0);
        Event event = new Event("team sync", initialFrom, initialTo);
        tasks.add(event);

        LocalDateTime newFrom = LocalDateTime.of(2026, 12, 15, 10, 0);
        LocalDateTime newTo = LocalDateTime.of(2026, 12, 15, 12, 0);
        UpdateCommand cmd = new UpdateCommand(1, null, null, newFrom, newTo);
        cmd.execute(tasks, ui, storage);

        assertEquals("[E][ ] team sync (from: 15 December 2026, 1000 hrs to: 15 December 2026, 1200 hrs)",
                tasks.get(0).toString());

        TaskList loaded = storage.load();
        assertEquals("[E][ ] team sync (from: 15 December 2026, 1000 hrs to: 15 December 2026, 1200 hrs)",
                loaded.get(0).toString());
    }

    @Test
    public void execute_updateEventEndTimeOnly_updatesEndPreservesStart() throws BobException {
        LocalDateTime from = LocalDateTime.of(2026, 11, 11, 14, 0);
        LocalDateTime initialTo = LocalDateTime.of(2026, 11, 11, 16, 0);
        Event event = new Event("team sync", from, initialTo);
        tasks.add(event);

        LocalDateTime newTo = LocalDateTime.of(2026, 11, 11, 18, 0);
        UpdateCommand cmd = new UpdateCommand(1, null, null, null, newTo);
        cmd.execute(tasks, ui, storage);

        assertEquals("[E][ ] team sync (from: 11 November 2026, 1400 hrs to: 11 November 2026, 1800 hrs)",
                tasks.get(0).toString());
    }

    @Test
    public void execute_updateEventStartTimeOnly_updatesStartPreservesEnd() throws BobException {
        LocalDateTime initialFrom = LocalDateTime.of(2026, 11, 11, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 11, 16, 0);
        Event event = new Event("team sync", initialFrom, to);
        tasks.add(event);

        LocalDateTime newFrom = LocalDateTime.of(2026, 11, 11, 15, 0);
        UpdateCommand cmd = new UpdateCommand(1, null, null, newFrom, null);
        cmd.execute(tasks, ui, storage);

        assertEquals("[E][ ] team sync (from: 11 November 2026, 1500 hrs to: 11 November 2026, 1600 hrs)",
                tasks.get(0).toString());
    }

    @Test
    public void execute_updateIncompatibleFlagOnTodo_throwsBobException() {
        tasks.add(new ToDo("simple todo"));

        LocalDateTime date = LocalDateTime.of(2026, 12, 12, 12, 0);
        UpdateCommand cmdBy = new UpdateCommand(1, null, date, null, null);
        assertThrows(BobException.class, () -> cmdBy.execute(tasks, ui, storage));

        UpdateCommand cmdFrom = new UpdateCommand(1, null, null, date, null);
        assertThrows(BobException.class, () -> cmdFrom.execute(tasks, ui, storage));

        UpdateCommand cmdTo = new UpdateCommand(1, null, null, null, date);
        assertThrows(BobException.class, () -> cmdTo.execute(tasks, ui, storage));
    }

    @Test
    public void execute_updateIncompatibleFlagOnDeadline_throwsBobException() {
        tasks.add(new Deadline("deadline task", LocalDateTime.of(2026, 12, 12, 12, 0)));

        LocalDateTime date = LocalDateTime.of(2026, 12, 12, 14, 0);
        UpdateCommand cmdFrom = new UpdateCommand(1, null, null, date, null);
        assertThrows(BobException.class, () -> cmdFrom.execute(tasks, ui, storage));

        UpdateCommand cmdTo = new UpdateCommand(1, null, null, null, date);
        assertThrows(BobException.class, () -> cmdTo.execute(tasks, ui, storage));
    }

    @Test
    public void execute_updateIncompatibleFlagOnEvent_throwsBobException() throws BobException {
        LocalDateTime from = LocalDateTime.of(2026, 11, 11, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 11, 16, 0);
        tasks.add(new Event("event task", from, to));

        LocalDateTime by = LocalDateTime.of(2026, 12, 12, 12, 0);
        UpdateCommand cmdBy = new UpdateCommand(1, null, by, null, null);
        assertThrows(BobException.class, () -> cmdBy.execute(tasks, ui, storage));
    }

    @Test
    public void execute_updateEventChronologyViolation_throwsBobException() throws BobException {
        LocalDateTime from = LocalDateTime.of(2026, 11, 11, 14, 0);
        LocalDateTime to = LocalDateTime.of(2026, 11, 11, 16, 0);
        tasks.add(new Event("event task", from, to));

        // new from > existing to
        LocalDateTime invalidFrom = LocalDateTime.of(2026, 11, 11, 17, 0);
        UpdateCommand cmd1 = new UpdateCommand(1, null, null, invalidFrom, null);
        assertThrows(BobException.class, () -> cmd1.execute(tasks, ui, storage));

        // new to < existing from
        LocalDateTime invalidTo = LocalDateTime.of(2026, 11, 11, 13, 0);
        UpdateCommand cmd2 = new UpdateCommand(1, null, null, null, invalidTo);
        assertThrows(BobException.class, () -> cmd2.execute(tasks, ui, storage));

        // new from > new to
        UpdateCommand cmd3 = new UpdateCommand(1, null, null, invalidFrom, invalidTo);
        assertThrows(BobException.class, () -> cmd3.execute(tasks, ui, storage));
    }

    @Test
    public void execute_invalidTaskId_throwsBobException() {
        UpdateCommand cmd = new UpdateCommand(5, "desc", null, null, null);
        assertThrows(BobException.class, () -> cmd.execute(tasks, ui, storage));
    }

    @Test
    public void execute_validUpdate_setsFormattedUiDiff() throws BobException {
        tasks.add(new ToDo("original task"));

        UpdateCommand cmd = new UpdateCommand(1, "changed task", null, null, null);
        cmd.execute(tasks, ui, storage);

        String expected = "Plan revised from:\n  [T][ ] original task\nto:\n  [T][ ] changed task";
        assertEquals(expected, ui.getLastResponse());
    }

    @Test
    public void isExit_returnsFalse() {
        UpdateCommand cmd = new UpdateCommand(1, "desc", null, null, null);
        assertFalse(cmd.isExit());
    }
}
