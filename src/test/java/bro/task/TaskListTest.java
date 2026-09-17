package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/** Verifies that TaskList owns task collection operations without exposing mutable state. */
class TaskListTest {
    /** Adding a task makes it available at the next list position. */
    @Test
    void addTask_storesTaskInInsertionOrder() {
        TaskList tasks = new TaskList();
        Task task = new ToDos("read book");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.getTask(0));
    }

    /** Removing a task returns it and leaves the remaining tasks in order. */
    @Test
    void removeTask_returnsRemovedTaskAndPreservesRemainingOrder() {
        Task firstTask = new ToDos("read book");
        Task secondTask = new ToDos("write notes");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask));

        assertSame(firstTask, tasks.removeTask(0));
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.getTask(0));
    }

    /** The task list rejects an out-of-range access instead of returning invalid data. */
    @Test
    void getTask_rejectsOutOfRangeIndex() {
        TaskList tasks = new TaskList();

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.getTask(0));
    }

    /** An empty list reports its state and becomes non-empty after a task is added. */
    @Test
    void isEmpty_newListThenAdd_reportsCurrentState() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
        tasks.add(new ToDos("read book"));
        assertFalse(tasks.isEmpty());
    }

    /** The task list rejects null tasks rather than storing invalid entries. */
    @Test
    void add_nullTask_throwsNullPointerException() {
        TaskList tasks = new TaskList();

        assertThrows(NullPointerException.class, () -> tasks.add(null));
    }

    /** The exposed task collection cannot be mutated outside TaskList. */
    @Test
    void getTasks_returnedList_isUnmodifiable() {
        TaskList tasks = new TaskList(List.of(new ToDos("read book")));

        assertThrows(UnsupportedOperationException.class,
                () -> tasks.getTasks().add(new ToDos("write notes")));
        assertEquals(1, tasks.size());
    }

    /** Constructing a task list copies its source collection instead of retaining external ownership. */
    @Test
    void constructor_sourceList_isCopied() {
        List<Task> sourceTasks = new ArrayList<>();
        sourceTasks.add(new ToDos("read book"));

        TaskList tasks = new TaskList(sourceTasks);
        sourceTasks.add(new ToDos("write notes"));

        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.getTask(0).getDescription());
    }

    /** A find keyword matches descriptions without changing task order or requiring letter case. */
    @Test
    void findTasks_keyword_matchesDescriptionsCaseInsensitivelyInOrder() {
        Task firstTask = new ToDos("read book");
        Task secondTask = new ToDos("write notes");
        Task thirdTask = new ToDos("return BOOK");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask, thirdTask));

        assertEquals(List.of(firstTask, thirdTask), tasks.findTasks("book"));
    }

    /** Multiple partial terms match descriptions in any order while excluding incomplete matches. */
    @Test
    void findTasks_multiplePartialTerms_matchesAllTermsInAnyOrder() {
        Task firstTask = new ToDos("Meeting about Java projects");
        Task secondTask = new ToDos("Read a book about Java");
        Task thirdTask = new ToDos("Plan the project meeting");
        TaskList tasks = new TaskList(List.of(firstTask, secondTask, thirdTask));

        assertEquals(List.of(firstTask, thirdTask), tasks.findTasks("pro meet"));
    }

    /** A keyword with no matching description returns an empty result without changing the list. */
    @Test
    void findTasks_noMatch_returnsEmptyList() {
        Task task = new ToDos("read book");
        TaskList tasks = new TaskList(List.of(task));

        assertTrue(tasks.findTasks("meeting").isEmpty());
        assertEquals(1, tasks.size());
    }

    /** Searching with a null keyword is rejected rather than treated as an accidental wildcard. */
    @Test
    void findTasks_nullKeyword_throwsNullPointerException() {
        TaskList tasks = new TaskList(List.of(new ToDos("read book")));

        assertThrows(NullPointerException.class, () -> tasks.findTasks(null));
    }
}
