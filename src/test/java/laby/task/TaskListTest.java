package laby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import laby.LabyException;

/** Tests the public behavior of {@link TaskList}. */
class TaskListTest {

    private static final LocalDateTime DEADLINE = LocalDateTime.of(2026, 8, 23, 10, 0);
    private static final LocalDateTime EVENT_START = LocalDateTime.of(2026, 8, 23, 11, 0);
    private static final LocalDateTime EVENT_END = LocalDateTime.of(2026, 8, 23, 12, 0);

    @Test
    void size_emptyList_returnsZero() {
        TaskList taskList = new TaskList(new ArrayList<>());

        assertEquals(0, taskList.size());
    }

    @Test
    void size_tasksAdded_returnsNumberOfTasks() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTodo("read book");
        taskList.addDeadline("return book", DEADLINE);

        assertEquals(2, taskList.size());
    }

    @Test
    void addTodo_validDescription_addsAndReturnsTodo() {
        TaskList taskList = new TaskList(new ArrayList<>());

        String addedTask = taskList.addTodo("read book");

        assertEquals("[T][ ] read book", addedTask);
        assertEquals(1, taskList.size());
    }

    @Test
    void addDeadline_validDetails_addsAndReturnsDeadline() {
        TaskList taskList = new TaskList(new ArrayList<>());

        String addedTask = taskList.addDeadline("return book", DEADLINE);

        assertEquals("[D][ ] return book (by: Aug 23 2026 10:00)", addedTask);
        assertEquals(1, taskList.size());
    }

    @Test
    void addEvent_validDetails_addsAndReturnsEvent() {
        TaskList taskList = new TaskList(new ArrayList<>());

        String addedTask = taskList.addEvent("project meeting", EVENT_START, EVENT_END);

        assertEquals("[E][ ] project meeting (from: Aug 23 2026 11:00 to: Aug 23 2026 12:00)",
                addedTask);
        assertEquals(1, taskList.size());
    }

    @Test
    void markTask_validIndex_taskIsMarkedDone() throws LabyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTodo("read book");

        String markedTask = taskList.modifyTaskStatus(0, true);

        assertEquals("[T][X] read book", markedTask);
    }

    @Test
    void markTask_alreadyMarkedTask_remainsMarkedDone() throws LabyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTodo("read book");
        taskList.modifyTaskStatus(0, true);

        String markedTask = taskList.modifyTaskStatus(0, true);

        assertEquals("[T][X] read book", markedTask);
    }

    @Test
    void markTask_negativeIndex_exceptionThrown() {
        TaskList taskList = new TaskList(new ArrayList<>());

        LabyException exception = assertThrows(LabyException.class,
                () -> taskList.modifyTaskStatus(-1, true));

        assertEquals("please enter a valid task index.", exception.getMessage());
    }

    @Test
    void markTask_indexBeyondList_exceptionThrown() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTodo("read book");

        LabyException exception = assertThrows(LabyException.class,
                () -> taskList.modifyTaskStatus(1, true));

        assertEquals("please enter a valid task index.", exception.getMessage());
    }

    @Test
    void unmarkTask_markedTask_taskIsMarkedUndone() throws LabyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTodo("read book");
        taskList.modifyTaskStatus(0, true);

        String unmarkedTask = taskList.modifyTaskStatus(0, false);

        assertEquals("[T][ ] read book", unmarkedTask);
    }

    @Test
    void unmarkTask_invalidIndex_exceptionThrown() {
        TaskList taskList = new TaskList(new ArrayList<>());

        LabyException exception = assertThrows(LabyException.class,
                () -> taskList.modifyTaskStatus(0, false));

        assertEquals("please enter a valid task index.", exception.getMessage());
    }

    @Test
    void deleteTask_validIndex_removesAndReturnsTask() throws LabyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTodo("read book");
        taskList.addTodo("write notes");

        String deletedTask = taskList.deleteTask(0);

        assertEquals("[T][ ] read book", deletedTask);
        assertEquals(1, taskList.size());
        assertEquals("1.[T][ ] write notes\n", taskList.toString());
    }

    @Test
    void deleteTask_invalidIndex_exceptionThrown() {
        TaskList taskList = new TaskList(new ArrayList<>());

        LabyException exception = assertThrows(LabyException.class,
                () -> taskList.deleteTask(-1));

        assertEquals("please enter a valid task index.", exception.getMessage());
    }

    @Test
    void toString_emptyList_returnsEmptyString() {
        TaskList taskList = new TaskList(new ArrayList<>());

        assertEquals("", taskList.toString());
    }

    @Test
    void toString_multipleTasks_returnsNumberedTasks() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTodo("read book");
        taskList.addDeadline("return book", DEADLINE);

        assertEquals("""
                1.[T][ ] read book
                2.[D][ ] return book (by: Aug 23 2026 10:00)
                """, taskList.toString());
    }

    @Test
    void filterTasks_matchingKeyword_returnsMatchingTasksWithOriginalNumbers() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTodo("read book");
        taskList.addTodo("write notes");
        taskList.addDeadline("return book", DEADLINE);

        String matchingTasks = taskList.filterTasks(" BOOK ");

        assertEquals("""
                1.[T][ ] read book
                3.[D][ ] return book (by: Aug 23 2026 10:00)
                """, matchingTasks);
    }

    @Test
    void filterTasks_noMatchingKeyword_returnsEmptyString() {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTodo("read book");

        assertEquals("", taskList.filterTasks("calendar"));
    }

    @Test
    void toFileString_multipleTaskTypes_returnsStorageFormat() throws LabyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTodo("read book");
        taskList.addDeadline("return book", DEADLINE);
        taskList.addEvent("project meeting", EVENT_START, EVENT_END);
        taskList.modifyTaskStatus(0, true);

        assertEquals("""
                        T|1|read book
                        D|0|return book|2026-08-23 10:00
                        E|0|project meeting|2026-08-23 11:00|2026-08-23 12:00
                        """,
                taskList.toFileString());
    }
}
