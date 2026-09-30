package marcus.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests task-list integrity checks. */
class TaskListTest {
    /** Verifies that identical task details are recognised as duplicates. */
    @Test
    void containsEquivalentTask_duplicateDetails_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.containsEquivalentTask(new Todo("read book")));
    }

    /** Verifies that different task details are not rejected as duplicates. */
    @Test
    void containsEquivalentTask_differentDetails_returnsFalse() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertFalse(tasks.containsEquivalentTask(new Todo("write notes")));
    }

    /** Verifies that removing a task shifts later tasks and preserves their order. */
    @Test
    void remove_middleTask_shiftsRemainingTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));

        assertEquals("[T][ ] second", tasks.remove(2).toString());
        assertEquals("[T][ ] third", tasks.get(2).toString());
        assertEquals(2, tasks.size());
    }

    /** Verifies that task searches return only matching tasks and preserve list numbers. */
    @Test
    void taskSearches_matchingTasks_preserveTaskNumbers() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit report", LocalDate.of(2026, 9, 15)));

        assertTrue(tasks.tasksMatchingKeywordToString("BOOK").contains("1.[T][ ] read book"));
        assertTrue(tasks.tasksOnDateToString(LocalDate.of(2026, 9, 15)).contains("2.[D][ ]"));
    }

    /** Verifies that tag searches show tags while ordinary searches hide them. */
    @Test
    void taskSearches_tagSearchShowsTags_keywordSearchHidesTags() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.addTag("#school");
        tasks.add(todo);

        assertTrue(tasks.tasksMatchingTagToString("#school").contains("#school"));
        assertFalse(tasks.tasksMatchingKeywordToString("book").contains("#school"));
    }

    /** Verifies that a full list reports its capacity state. */
    @Test
    void isFull_atCapacity_returnsTrue() {
        Task[] storedTasks = new Task[TaskList.CAPACITY];
        for (int index = 0; index < storedTasks.length; index++) {
            storedTasks[index] = new Todo("task " + index);
        }

        assertTrue(new TaskList(storedTasks).isFull());
    }
}
