package bo.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests task-list storage, ordering, capacity, and index validation. */
class TaskListTest {
    /** Verifies that tasks are added in order and returned as an independent array. */
    @Test
    void add_tasksToArray_preservesOrderAndCopiesArray() {
        Task first = new Todo("first");
        Task second = new Deadline("second", LocalDate.of(2026, 8, 28));
        TaskList taskList = new TaskList();

        assertTrue(taskList.add(first));
        assertTrue(taskList.add(second));
        Task[] copiedTasks = taskList.toArray();
        copiedTasks[0] = new Todo("changed copy");

        assertAll(
                () -> assertEquals(2, taskList.size()),
                () -> assertFalse(taskList.isFull()),
                () -> assertSame(first, taskList.get(0)),
                () -> assertSame(second, taskList.get(1)),
                () -> assertArrayEquals(new Task[] {first, second}, taskList.toArray()));
    }

    /** Verifies that removing a task shifts later tasks and returns the removed task. */
    @Test
    void remove_taskInMiddle_shiftsRemainingTasks() {
        Task first = new Todo("first");
        Task removed = new Todo("removed");
        Task last = new Todo("last");
        TaskList taskList = new TaskList();
        taskList.add(first);
        taskList.add(removed);
        taskList.add(last);

        Task actualRemoved = taskList.remove(1);

        assertAll(
                () -> assertSame(removed, actualRemoved),
                () -> assertEquals(2, taskList.size()),
                () -> assertSame(first, taskList.get(0)),
                () -> assertSame(last, taskList.get(1)),
                () -> assertArrayEquals(new Task[] {first, last}, taskList.toArray()));
    }

    /** Verifies that find is case-insensitive and preserves the original matching order. */
    @Test
    void find_keyword_returnsMatchingTasksInOrder() {
        Task firstMatch = new Todo("read a book");
        Task nonMatch = new Todo("buy milk");
        Task secondMatch = new Deadline("return book", LocalDate.of(2026, 8, 28));
        TaskList taskList = new TaskList();
        taskList.add(firstMatch);
        taskList.add(nonMatch);
        taskList.add(secondMatch);

        Task[] matchingTasks = taskList.find("BOOK");

        assertArrayEquals(new Task[] {firstMatch, secondMatch}, matchingTasks);
    }

    /** Verifies chronological ordering across task types and stable undated ordering. */
    @Test
    void sortByDate_mixedTasks_placesUndatedTasksLast() {
        Task undated = new Todo("undated");
        Task laterDeadline = new Deadline("later deadline", LocalDate.of(2026, 8, 28));
        Task event = new Event("event", LocalDateTime.of(2026, 8, 20, 9, 0),
                LocalDateTime.of(2026, 8, 20, 10, 0));
        Task earlierDeadline = new Deadline("earlier deadline", LocalDate.of(2026, 8, 15));
        Task legacyUndated = new Deadline("legacy undated", "Friday");
        TaskList taskList = new TaskList();
        taskList.add(undated);
        taskList.add(laterDeadline);
        taskList.add(event);
        taskList.add(earlierDeadline);
        taskList.add(legacyUndated);

        taskList.sortByDate();

        assertArrayEquals(new Task[] {earlierDeadline, event, laterDeadline,
                undated, legacyUndated}, taskList.toArray());
    }

    /** Verifies that a full list rejects another task without changing its contents. */
    @Test
    void add_fullList_returnsFalseAndKeepsExistingTasks() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList taskList = new TaskList(new Task[2], 0);
        taskList.add(first);
        taskList.add(second);

        boolean added = taskList.add(new Todo("third"));

        assertAll(
                () -> assertFalse(added),
                () -> assertTrue(taskList.isFull()),
                () -> assertEquals(2, taskList.size()),
                () -> assertArrayEquals(new Task[] {first, second}, taskList.toArray()));
    }

    /** Verifies rejection of null tasks and invalid construction arguments. */
    @Test
    void taskList_invalidArguments_throwException() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new TaskList(null, 0)),
                () -> assertThrows(IllegalArgumentException.class, () -> new TaskList(new Task[1], -1)),
                () -> assertThrows(IllegalArgumentException.class, () -> new TaskList(new Task[1], 2)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new TaskList(new Task[] {null}, 1)),
                () -> assertThrows(IllegalArgumentException.class, () -> new TaskList().add(null)),
                () -> assertThrows(IllegalArgumentException.class, () -> new TaskList().find(" ")),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> new TaskList().get(0)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> new TaskList().remove(-1)));
    }
}
