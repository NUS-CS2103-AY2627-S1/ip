package shan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListStateTest {
    @Test
    void createState_tasksMutated_stateStillContainsOriginalCopies() {
        TaskList tasks = new TaskList();
        ToDo original = new ToDo("original");
        tasks.add(original);

        TaskList.State state = tasks.createState();
        original.markDone();
        tasks.delete(1);
        tasks.add(new ToDo("replacement"));

        tasks.restoreState(state);
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] original", tasks.get(1).toString());
        assertFalse(tasks.get(1).isDone());
    }

    @Test
    void state_sourceListMutated_stateRemainsUnchanged() {
        List<Task> source = new ArrayList<>();
        source.add(new ToDo("original"));

        TaskList.State state = new TaskList.State(source, List.of(false));
        source.clear();

        assertEquals(1, state.tasks().size());
        assertThrows(UnsupportedOperationException.class, () -> state.tasks().add(new ToDo("another")));
    }

    @Test
    void recordUndoState_secondState_replacesPreviousHistory() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("first"));
        TaskList.State firstState = tasks.createState();
        tasks.add(new ToDo("second"));
        TaskList.State secondState = tasks.createState();
        tasks.add(new ToDo("third"));

        tasks.recordUndoState(firstState);
        tasks.recordUndoState(secondState);
        tasks.restoreUndoState();

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] second", tasks.get(2).toString());
        assertTrue(tasks.canUndo());
    }

    @Test
    void clearUndoState_recordedState_removesHistory() {
        TaskList tasks = new TaskList();
        tasks.recordUndoState(tasks.createState());
        assertTrue(tasks.canUndo());

        tasks.clearUndoState();

        assertFalse(tasks.canUndo());
    }

    @Test
    void invalidStateOperations_assertionErrorThrown() {
        TaskList tasks = new TaskList();

        assertThrows(AssertionError.class, () -> tasks.add(null));
        assertThrows(AssertionError.class, () -> tasks.delete(0));
        assertThrows(AssertionError.class, () -> tasks.recordUndoState(null));
        assertThrows(AssertionError.class, () -> tasks.restoreState(null));
        assertThrows(AssertionError.class, tasks::restoreUndoState);
        List<Task> tasksWithNull = new ArrayList<>();
        tasksWithNull.add(new ToDo("valid"));
        tasksWithNull.add(null);
        assertThrows(AssertionError.class, () -> tasks.replaceAll(tasksWithNull));
        TaskList.State mismatchedState = new TaskList.State(
                List.of(new ToDo("task")), List.of());
        assertThrows(AssertionError.class, () -> tasks.restoreState(mismatchedState));
    }
}
