package avon.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void find_keywordInDescriptions_returnsMatchingTasksInOrder() {
        TaskList taskList = new TaskList(List.of(
                new Todo("read book"),
                new Todo("write essay"),
                new Todo("return book")));

        List<Task> matchingTasks = taskList.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals("read book", matchingTasks.get(0).getDescription());
        assertEquals("return book", matchingTasks.get(1).getDescription());
    }

    @Test
    void find_keywordIsCaseSensitive_returnsNoMatch() {
        TaskList taskList = new TaskList(List.of(new Todo("read book")));

        assertEquals(0, taskList.find("Book").size());
    }

    @Test
    void getTaskNumber_storedAndUnknownTasks_returnsOriginalNumberOrThrows() {
        Todo firstTask = new Todo("first");
        Todo thirdTask = new Todo("third");
        TaskList taskList = new TaskList(List.of(firstTask, new Todo("second"), thirdTask));

        assertEquals(1, taskList.getTaskNumber(firstTask));
        assertEquals(3, taskList.getTaskNumber(thirdTask));
        assertThrows(IllegalArgumentException.class, () ->
                taskList.getTaskNumber(new Todo("unknown")));
    }

    @Test
    void containsDuplicateOf_sameDetailsIgnoringCompletion_returnsTrue() {
        Todo storedTodo = new Todo("read book");
        storedTodo.markAsDone();
        TaskList taskList = new TaskList(List.of(storedTodo));

        assertTrue(taskList.containsDuplicateOf(new Todo("read book")));
    }

    @Test
    void containsDuplicateOf_differentTypeOrTime_returnsFalse() {
        LocalDateTime firstDeadline = LocalDateTime.of(2026, 9, 10, 18, 0);
        LocalDateTime secondDeadline = LocalDateTime.of(2026, 9, 11, 18, 0);
        LocalDateTime differentEventStart = LocalDateTime.of(2026, 9, 10, 19, 0);
        LocalDateTime eventEnd = LocalDateTime.of(2026, 9, 10, 20, 0);
        TaskList taskList = new TaskList(List.of(
                new Deadline("return book", firstDeadline),
                new Event("attend lecture", firstDeadline, eventEnd)));

        assertFalse(taskList.containsDuplicateOf(new Todo("return book")));
        assertFalse(taskList.containsDuplicateOf(new Deadline("return book", secondDeadline)));
        assertFalse(taskList.containsDuplicateOf(new Event("attend lecture", differentEventStart, eventEnd)));
        assertTrue(taskList.containsDuplicateOf(new Event("attend lecture", firstDeadline, eventEnd)));
    }

    @Test
    void constructor_nullList_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TaskList(null));
    }

    @Test
    void constructor_listWithNullTask_throwsIllegalArgumentException() {
        List<Task> tasks = Arrays.asList(new Todo("read book"), null);

        assertThrows(IllegalArgumentException.class, () -> new TaskList(tasks));
    }

    @Test
    void addAtIndexAndRemoveTask_validIndex_preservesExpectedOrder() {
        TaskList taskList = new TaskList(List.of(new Todo("first"), new Todo("third")));

        taskList.add(1, new Todo("second"));
        Task removedTask = taskList.removeTask(0);

        assertEquals("first", removedTask.getDescription());
        assertEquals(2, taskList.size());
        assertEquals("second", taskList.getTask(0).getDescription());
        assertEquals("third", taskList.getTask(1).getDescription());
    }

    @Test
    void add_nullTask_throwsIllegalArgumentException() {
        TaskList taskList = new TaskList();

        assertThrows(IllegalArgumentException.class, () -> taskList.add(null));
        assertThrows(IllegalArgumentException.class, () -> taskList.add(0, null));
    }

    @Test
    void getOrRemoveTask_outsideList_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList(List.of(new Todo("only")));

        assertThrows(IndexOutOfBoundsException.class, () -> taskList.getTask(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.getTask(1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.removeTask(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.removeTask(1));
    }
}
