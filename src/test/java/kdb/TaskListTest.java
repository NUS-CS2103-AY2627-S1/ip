package kdb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests adding, retrieving, counting, and removing tasks. */
class TaskListTest {
    @Test
    void addAndGet_preservesInsertionOrder() {
        TaskList tasks = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");

        tasks.add(first);
        tasks.add(second);

        assertEquals(2, tasks.size());
        assertEquals(first, tasks.get(0));
        assertEquals(second, tasks.get(1));
    }

    @Test
    void remove_returnsRemovedTaskAndReducesSize() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("keep"));
        Task removed = new Todo("remove");
        tasks.add(removed);

        assertEquals(removed, tasks.remove(1));
        assertEquals(1, tasks.size());
        assertEquals("keep", tasks.get(0).getDescription());
    }

    @Test
    void iterator_visitsEveryTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        StringBuilder descriptions = new StringBuilder();
        for (Task task : tasks) {
            descriptions.append(task.getDescription()).append(",");
        }

        assertEquals("first,second,", descriptions.toString());
    }

    @Test
    void find_keywordIsCaseInsensitive_returnsMatchingTasksInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read a book"));
        tasks.add(new Deadline("submit report", java.time.LocalDateTime.of(2025, 1, 1, 9, 0)));
        tasks.add(new Todo("Return book"));

        TaskList matchingTasks = tasks.find("BOOK");

        assertEquals(2, matchingTasks.size());
        assertEquals("Read a book", matchingTasks.get(0).getDescription());
        assertEquals("Return book", matchingTasks.get(1).getDescription());
    }

    @Test
    void findNoMatch_returnsEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read a book"));

        assertEquals(0, tasks.find("meeting").size());
    }

    @Test
    void getOutsideListBounds_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(0));
    }

    @Test
    void removeOutsideListBounds_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("only task"));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.remove(1));
    }
}
