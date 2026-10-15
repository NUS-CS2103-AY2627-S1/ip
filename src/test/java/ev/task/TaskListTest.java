package ev.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import ev.EvException;

public class TaskListTest {

    private TaskList threeTasks() throws EvException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0)));
        tasks.add(new Todo("water plants"));
        return tasks;
    }

    @Test
    public void newTaskList_noArguments_empty() throws EvException {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void add_task_appendedAtEnd() throws EvException {
        TaskList tasks = threeTasks();
        tasks.add(new Todo("last"));
        assertEquals(4, tasks.size());
        assertEquals("[T][ ] last", tasks.get(3).toString());
        assertFalse(tasks.isEmpty());
    }

    @Test
    public void getByNumber_firstAndLast_returnsMatchingTask() throws EvException {
        TaskList tasks = threeTasks();
        assertEquals("[T][ ] read book", tasks.getByNumber(1).toString());
        assertEquals("[T][ ] water plants", tasks.getByNumber(3).toString());
    }

    @Test
    public void getByNumber_returnsLiveTask_markingIsVisibleInList() throws EvException {
        TaskList tasks = threeTasks();
        tasks.getByNumber(2).markAsDone();
        assertEquals("[D][X] return book (by: Dec 2 2019, 6:00 PM)", tasks.get(1).toString());
    }

    @Test
    public void getByNumber_zero_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> threeTasks().getByNumber(0));
    }

    @Test
    public void getByNumber_negative_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> threeTasks().getByNumber(-1));
    }

    @Test
    public void getByNumber_justPastEnd_exceptionThrown() throws EvException {
        assertThrows(EvException.class, () -> threeTasks().getByNumber(4));
    }

    @Test
    public void getByNumber_emptyList_saysListIsEmpty() throws EvException {
        EvException thrown = assertThrows(EvException.class, () -> new TaskList().getByNumber(1));
        assertTrue(thrown.getMessage().contains("empty"));
    }

    @Test
    public void removeByNumber_middleTask_remainingTasksCloseTheGap() throws EvException {
        TaskList tasks = threeTasks();
        assertEquals("[D][ ] return book (by: Dec 2 2019, 6:00 PM)", tasks.removeByNumber(2).toString());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
        assertEquals("[T][ ] water plants", tasks.get(1).toString());
    }

    @Test
    public void removeByNumber_lastTask_listBecomesEmpty() throws EvException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only"));
        tasks.removeByNumber(1);
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void removeByNumber_outOfRange_nothingRemoved() throws EvException {
        TaskList tasks = threeTasks();
        assertThrows(EvException.class, () -> tasks.removeByNumber(4));
        assertEquals(3, tasks.size());
    }

    @Test
    public void describeSize_oneTask_singular() throws EvException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertEquals("1 task", tasks.describeSize());
    }

    @Test
    public void describeSize_zeroOrManyTasks_plural() throws EvException {
        assertEquals("0 tasks", new TaskList().describeSize());
        assertEquals("3 tasks", threeTasks().describeSize());
    }

    @Test
    public void getTasks_reflectsLaterChanges() throws EvException {
        TaskList tasks = threeTasks();
        assertEquals(3, tasks.getTasks().size());
        tasks.add(new Todo("later"));
        assertEquals(4, tasks.getTasks().size());
    }

    @Test
    public void add_taskAlreadyOnTheList_rejectedAndListUnchanged() throws EvException {
        TaskList tasks = threeTasks();
        EvException thrown = assertThrows(EvException.class, () -> tasks.add(new Todo("read book")));
        assertTrue(thrown.getMessage().contains("task 1"));
        assertEquals(3, tasks.size());
    }

    @Test
    public void add_sameDetailsButAlreadyDone_stillRejected() throws EvException {
        TaskList tasks = threeTasks();
        tasks.get(0).markAsDone();
        assertThrows(EvException.class, () -> tasks.add(new Todo("read book")));
    }

    @Test
    public void add_sameDescriptionButDifferentType_accepted() throws EvException {
        TaskList tasks = threeTasks();
        tasks.add(new Deadline("read book", LocalDateTime.of(2019, 12, 5, 18, 0)));
        assertEquals(4, tasks.size());
    }

    @Test
    public void add_sameDescriptionButDifferentDate_accepted() throws EvException {
        TaskList tasks = threeTasks();
        tasks.add(new Deadline("return book", LocalDateTime.of(2020, 1, 1, 0, 0)));
        assertEquals(4, tasks.size());
    }
}
