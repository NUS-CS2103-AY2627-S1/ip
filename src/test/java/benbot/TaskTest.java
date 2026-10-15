package benbot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests basic task state and the task categories shown to users. */
class TaskTest {
    @Test
    void task_newTask_isNotDoneAndUsesTodoFormat() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
        assertEquals("[T][ ] read book", task.toString());
        assertEquals("todo read book", task.toStorageString());
    }

    @Test
    void markDone_thenMarkUndone_updatesCompletionState() {
        Task task = new Task("read book");

        task.markDone();
        assertTrue(task.isDone());
        assertEquals("[T][X] read book", task.toString());

        task.markUndone();
        assertFalse(task.isDone());
    }

    @Test
    void markDoneAndUndone_repeatedCalls_areIdempotent() {
        Task task = new Task("read book");

        task.markDone();
        task.markDone();
        assertTrue(task.isDone());
        assertEquals("[T][X] read book", task.toString());

        task.markUndone();
        task.markUndone();
        assertFalse(task.isDone());
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void taskTypes_returnExpectedListSymbols() {
        assertEquals("[T]", TaskType.TODO.getSymbol());
        assertEquals("[D]", TaskType.DEADLINE.getSymbol());
        assertEquals("[E]", TaskType.EVENT.getSymbol());
    }

    @Test
    void taskStatuses_returnExpectedCompletionIcons() {
        assertEquals("[ ]", TaskStatus.NOT_DONE.getIcon());
        assertEquals("[X]", TaskStatus.DONE.getIcon());
    }

    @Test
    void toDo_constructor_joinsDescriptionWords() {
        ToDo task = new ToDo("todo", "read", "book");

        assertEquals("[T][ ] read book", task.toString());
        assertEquals("todo read book", task.toStorageString());
    }

    @Test
    void task_blankDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Task(" "));
    }

    @Test
    void task_nullDescription_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Task(null));
    }

    @Test
    void task_nullType_throwsAssertionError() {
        assertThrows(AssertionError.class, () -> new Task("read book", null));
    }
}
