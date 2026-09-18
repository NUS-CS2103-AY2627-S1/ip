package student.project.bob.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import student.project.bob.exception.BobException;

/**
 * Tests the task-number validation and conversion performed by {@link TaskList#getIndex}.
 */
public class TaskListTest {
    private static final String COMMAND_NAME = "mark";

    /**
     * Verifies that constructing a task list copies the supplied list.
     */
    @Test
    public void constructor_withTasks_copiesInputList() {
        List<Task> source = new ArrayList<>(List.of(new Todo("first"), new Todo("second")));
        TaskList taskList = new TaskList(source);

        source.add(new Todo("third"));

        assertEquals(2, taskList.size());
        assertSame(source.get(0), taskList.get(0));
        assertSame(source.get(1), taskList.get(1));
    }

    /**
     * Verifies that adding and removing tasks updates the list and returns the removed task.
     */
    @Test
    public void addAndRemove_tasks_updatesSizeAndContents() {
        TaskList taskList = new TaskList();
        Task first = new Todo("first");
        Task second = new Todo("second");

        taskList.add(first);
        taskList.add(second);

        assertEquals(2, taskList.size());
        assertSame(first, taskList.get(0));
        assertSame(second, taskList.get(1));
        assertSame(first, taskList.remove(0));
        assertEquals(1, taskList.size());
        assertSame(second, taskList.get(0));
    }

    /**
     * Verifies that retrieving or removing an invalid index is rejected.
     */
    @Test
    public void getAndRemove_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));

        assertAll(
                () -> assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(-1)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(1)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(-1)),
                () -> assertThrows(IndexOutOfBoundsException.class, () -> taskList.remove(1)));
    }

    /**
     * Verifies that the exposed list is read-only while preserving task order.
     */
    @Test
    public void asList_tasks_returnsUnmodifiableOrderedView() {
        List<Task> tasks = List.of(new Todo("first"), new Todo("second"));
        TaskList taskList = new TaskList(tasks);

        assertIterableEquals(tasks, taskList.asList());
        assertThrows(
                UnsupportedOperationException.class, () -> taskList.asList().add(new Todo("third")));
    }

    /**
     * Verifies that streaming a task list returns all tasks in insertion order.
     */
    @Test
    public void stream_tasks_returnsTasksInInsertionOrder() {
        Task first = new Todo("first");
        Task second = new Todo("second");
        TaskList taskList = new TaskList(List.of(first, second));

        assertIterableEquals(List.of(first, second), taskList.stream().toList());
    }

    /**
     * Verifies that valid one-based task numbers are converted to zero-based indexes.
     */
    @Test
    public void getIndex_validTaskNumbers_returnsZeroBasedIndexes() throws BobException {
        TaskList taskList = new TaskList(List.of(new Todo("first"), new Todo("second"), new Todo("third")));

        assertAll(
                () -> assertEquals(0, taskList.getIndex(new String[] {COMMAND_NAME, "1"}, COMMAND_NAME)),
                () -> assertEquals(1, taskList.getIndex(new String[] {COMMAND_NAME, "2"}, COMMAND_NAME)),
                () -> assertEquals(2, taskList.getIndex(new String[] {COMMAND_NAME, "3"}, COMMAND_NAME)));
    }

    /**
     * Verifies that a missing or extra task number produces the command-format error.
     */
    @Test
    public void getIndex_missingOrExtraTaskNumber_throwsFormatException() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));
        String expectedMessage = "Use \"mark <task number>\". Example: \"mark 1\".";

        assertAll(
                () -> assertEquals(
                        expectedMessage,
                        assertThrows(
                                        BobException.class,
                                        () -> taskList.getIndex(new String[] {COMMAND_NAME}, COMMAND_NAME))
                                .getMessage()),
                () -> assertEquals(
                        expectedMessage,
                        assertThrows(
                                        BobException.class,
                                        () -> taskList.getIndex(
                                                new String[] {COMMAND_NAME, "1", "extra"}, COMMAND_NAME))
                                .getMessage()));
    }

    /**
     * Verifies that non-numeric task numbers produce the invalid-number error.
     */
    @Test
    public void getIndex_nonNumericTaskNumber_throwsInvalidNumberException() {
        TaskList taskList = new TaskList(List.of(new Todo("task")));
        String expectedMessage = "The task number must be a whole number. Example: \"mark 1\".";

        assertAll(
                () -> assertEquals(
                        expectedMessage,
                        assertThrows(
                                        BobException.class,
                                        () -> taskList.getIndex(new String[] {COMMAND_NAME, "abc"}, COMMAND_NAME))
                                .getMessage()),
                () -> assertEquals(
                        expectedMessage,
                        assertThrows(
                                        BobException.class,
                                        () -> taskList.getIndex(
                                                new String[] {COMMAND_NAME, "999999999999999999999"}, COMMAND_NAME))
                                .getMessage()));
    }

    /**
     * Verifies that task numbers outside the list range produce the task-not-found error.
     */
    @Test
    public void getIndex_outOfRangeTaskNumber_throwsTaskNotFoundException() {
        TaskList taskList = new TaskList(List.of(new Todo("first"), new Todo("second")));
        String expectedMessage = "That task number does not exist. Use \"list\" to view the available task numbers.";

        assertAll(
                () -> assertEquals(
                        expectedMessage,
                        assertThrows(
                                        BobException.class,
                                        () -> taskList.getIndex(new String[] {COMMAND_NAME, "0"}, COMMAND_NAME))
                                .getMessage()),
                () -> assertEquals(
                        expectedMessage,
                        assertThrows(
                                        BobException.class,
                                        () -> taskList.getIndex(new String[] {COMMAND_NAME, "-1"}, COMMAND_NAME))
                                .getMessage()),
                () -> assertEquals(
                        expectedMessage,
                        assertThrows(
                                        BobException.class,
                                        () -> taskList.getIndex(new String[] {COMMAND_NAME, "3"}, COMMAND_NAME))
                                .getMessage()));
    }

    /**
     * Verifies that no task number is valid when the task list is empty.
     */
    @Test
    public void getIndex_emptyTaskList_throwsTaskNotFoundException() {
        TaskList taskList = new TaskList();

        BobException exception = assertThrows(
                BobException.class, () -> taskList.getIndex(new String[] {COMMAND_NAME, "1"}, COMMAND_NAME));

        assertEquals(
                "That task number does not exist. Use \"list\" to view the available task numbers.",
                exception.getMessage());
    }
}
