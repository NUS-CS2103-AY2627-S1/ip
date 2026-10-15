package sevensix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Verifies that the remembered task list is independent of later changes and is restored once.
 */
class UndoHistoryTest {
    /** Snapshots of dated tasks must preserve schedules and independently copy completion state. */
    @Test
    void saveCopiesDeadlineAndEventDetails() {
        LocalDate day = LocalDate.of(2024, 2, 29);
        Deadline deadline = new Deadline("report", day, LocalTime.NOON);
        Event event = new Event("call", day, LocalTime.NOON, day, LocalTime.of(13, 0));
        deadline.markAsDone();
        UndoHistory history = new UndoHistory();
        history.save(List.of(deadline, event), ChangeType.ADD, new Todo("change"));
        deadline.markAsNotDone();
        event.markAsDone();
        List<Task> restored = history.takeEntry().restoreTasks();

        assertNotSame(deadline, restored.get(0));
        assertNotSame(event, restored.get(1));
        assertEquals("[D][X] report (by: Feb 29 2024 12:00 PM)", restored.get(0).toString());
        assertEquals("[E][ ] call (from: Feb 29 2024 12:00 PM to: Feb 29 2024 1:00 PM)", restored.get(1).toString());
    }

    /** An empty saved list is still a valid undo snapshot. */
    @Test
    void saveEmptyListCreatesConsumableSnapshot() {
        UndoHistory history = new UndoHistory();
        history.save(List.of(), ChangeType.ADD, new Todo("change"));

        assertTrue(history.hasSnapshot());
        assertTrue(history.takeEntry().restoreTasks().isEmpty());
        assertFalse(history.hasSnapshot());
    }

    /** Taking and modifying one history must not consume or modify its copy. */
    @Test
    void copyCreatesIndependentHistoryAndTasks() {
        UndoHistory original = new UndoHistory();
        original.save(List.of(new Todo("first")), ChangeType.ADD, new Todo("change"));
        UndoHistory copy = original.copy();
        original.takeEntry().restoreTasks().get(0).markAsDone();

        assertFalse(original.hasSnapshot());
        assertTrue(copy.hasSnapshot());
        assertFalse(copy.takeEntry().restoreTasks().get(0).isDone());
        assertFalse(new UndoHistory().copy().hasSnapshot());
    }

    /** Undo descriptions and snapshots must remain stable even if callers mutate returned tasks. */
    @Test
    void entryKeepsActionAndTaskIndependentOfLaterChanges() {
        Todo task = new Todo("read book");
        UndoEntry entry = new UndoEntry(List.of(task), ChangeType.DELETE, task);
        task.markAsDone();
        entry.getTask().markAsDone();
        entry.restoreTasks().get(0).markAsDone();

        assertEquals(ChangeType.DELETE, entry.getType());
        assertEquals("Undid deletion: [T][ ] read book", Ui.formatUndoMessage(entry));
        assertFalse(entry.restoreTasks().get(0).isDone());
    }

    @Test
    void hasSnapshotBeforeAnySaveReturnsFalse() {
        assertFalse(new UndoHistory().hasSnapshot());
    }

    @Test
    void takeSnapshotAfterSaveReturnsRememberedTasksAndClearsHistory() {
        UndoHistory history = new UndoHistory();
        history.save(new TaskList(List.of(new Todo("read book"))), ChangeType.ADD, new Todo("change"));

        assertTrue(history.hasSnapshot());
        List<Task> restoredTasks = history.takeEntry().restoreTasks();

        assertEquals(1, restoredTasks.size());
        assertEquals("read book", restoredTasks.get(0).getDescription());
        assertFalse(history.hasSnapshot());
    }

    @Test
    void takeSnapshotAfterTaskIsMarkedReturnsStatusFromBeforeTheChange() {
        Todo task = new Todo("read book");
        UndoHistory history = new UndoHistory();
        history.save(new TaskList(List.of(task)), ChangeType.ADD, new Todo("change"));

        task.markAsDone();

        assertFalse(history.takeEntry().restoreTasks().get(0).isDone());
    }

    @Test
    void saveRetainsEarlierSnapshotsInReverseOrder() {
        UndoHistory history = new UndoHistory();
        history.save(new TaskList(List.of(new Todo("read book"))), ChangeType.ADD, new Todo("change"));
        history.save(new TaskList(List.of(new Todo("join sports club"), new Todo("borrow book"))),
                ChangeType.ADD, new Todo("change"));

        assertEquals(2, history.takeEntry().restoreTasks().size());
        assertEquals("read book", history.takeEntry().restoreTasks().get(0).getDescription());
        assertFalse(history.hasSnapshot());
    }
}
