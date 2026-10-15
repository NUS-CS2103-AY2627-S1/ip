package milo.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.ToDo;

public class StorageTest {

    @Test
    public void restoreTask_validTodoRecord_expectedToDoTask() {
        Task task = Storage.restoreTask("T | 0 | read book");

        ToDo todo = assertInstanceOf(ToDo.class, task);
        assertEquals("[T] [ ] read book", todo.toString());
    }

    @Test
    public void restoreTask_markedDeadlineRecord_expectedDoneDeadlineTask() {
        Task task = Storage.restoreTask("D | 1 | return book | 2019-12-02 1800");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("D | 1 | return book | 2019-12-02 1800", deadline.storageString());
    }

    @Test
    public void restoreTask_validEventRecord_expectedEventTask() {
        Task task = Storage.restoreTask("E | 0 | exam | 2019-12-02 0900 | 2019-12-03 1100");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("E | 0 | exam | 2019-12-02 0900 | 2019-12-03 1100",
                event.storageString());
    }

    @Test
    public void restoreTask_corruptedRecords_expectedNull() {
        assertNull(Storage.restoreTask(null));
        assertNull(Storage.restoreTask(""));
        assertNull(Storage.restoreTask("D | 0 | missing date | not-a-date"));
        assertNull(Storage.restoreTask("X | 0 | unknown task"));
    }
}
