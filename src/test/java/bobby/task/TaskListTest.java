package bobby.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import bobby.BobbyException;

/**
 * Tests task-list operations that mutate Bobby's in-memory task collection.
 */
class TaskListTest {
    @Test
    void add_appendsTaskAndIncreasesSize() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        Todo task = new Todo("borrow book");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertSame(task, taskList.asList().get(0));
    }

    @Test
    void add_duplicateTask_throwsBobbyException() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());

        taskList.add(new Todo("borrow book"));

        assertThrows(BobbyException.class, () -> taskList.add(new Todo("borrow book")));
    }

    @Test
    void add_sameDescriptionButDifferentTaskDetails_appendsBothTasks() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());

        taskList.add(new Todo("read book"));
        taskList.add(new Deadline("read book", LocalDateTime.of(2019, 12, 6, 0, 0)));
        taskList.add(new Deadline("read book", LocalDateTime.of(2019, 12, 7, 0, 0)));

        assertEquals(3, taskList.size());
    }

    @Test
    void find_keywordMatchesDescriptions_returnsMatchingTasksInOrder() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("buy milk");
        Todo thirdTask = new Todo("return Book");
        taskList.add(firstTask);
        taskList.add(secondTask);
        taskList.add(thirdTask);

        List<Task> matchingTasks = taskList.find("book");

        assertEquals(2, matchingTasks.size());
        assertSame(firstTask, matchingTasks.get(0));
        assertSame(thirdTask, matchingTasks.get(1));
    }

    @Test
    void find_keywordMatchesTags_returnsMatchingTasksInOrder() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("buy milk");
        taskList.add(firstTask);
        taskList.add(secondTask);
        taskList.addTag(0, "#fun");
        taskList.addTag(1, "#errand");

        List<Task> matchingTasks = taskList.find("#fun");

        assertEquals(1, matchingTasks.size());
        assertSame(firstTask, matchingTasks.get(0));
    }

    @Test
    void find_keywordDoesNotMatchAnyDescription_returnsEmptyList() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new Todo("read book"));

        assertTrue(taskList.find("milk").isEmpty());
    }

    @Test
    void markAndUnmark_updatesSelectedTaskOnly() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        taskList.add(firstTask);
        taskList.add(secondTask);

        Task markedTask = taskList.mark(1);

        assertSame(secondTask, markedTask);
        assertEquals("[T][ ] first", firstTask.toString());
        assertEquals("[T][X] second", secondTask.toString());

        Task unmarkedTask = taskList.unmark(1);

        assertSame(secondTask, unmarkedTask);
        assertEquals("[T][ ] second", secondTask.toString());
    }

    @Test
    void delete_removesSelectedTaskAndKeepsRemainingOrder() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        Todo thirdTask = new Todo("third");
        taskList.add(firstTask);
        taskList.add(secondTask);
        taskList.add(thirdTask);

        Task deletedTask = taskList.delete(1);

        assertSame(secondTask, deletedTask);
        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.asList().get(0));
        assertSame(thirdTask, taskList.asList().get(1));
    }

    @Test
    void addTag_addsTagToSelectedTaskOnly() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        Todo firstTask = new Todo("first");
        Todo secondTask = new Todo("second");
        taskList.add(firstTask);
        taskList.add(secondTask);

        Task taggedTask = taskList.addTag(1, "#fun");

        assertSame(secondTask, taggedTask);
        assertEquals("[T][ ] first", firstTask.toString());
        assertEquals("[T][ ] second #fun", secondTask.toString());
        assertEquals("T | 0 | second | #fun", secondTask.toStorageString());
    }

    @Test
    void addTag_duplicateTag_throwsBobbyException() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new Todo("first"));
        taskList.addTag(0, "#fun");

        assertThrows(BobbyException.class, () -> taskList.addTag(0, "#fun"));
    }

    @Test
    void isValidIndex_checksLowerAndUpperBounds() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new Todo("only task"));

        assertFalse(taskList.isValidIndex(-1));
        assertTrue(taskList.isValidIndex(0));
        assertFalse(taskList.isValidIndex(1));
    }

    @Test
    void constructor_mutatingOriginalList_doesNotChangeTaskList() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("only task"));
        TaskList taskList = new TaskList(tasks);

        tasks.clear();

        assertEquals(1, taskList.size());
        assertEquals("[T][ ] only task", taskList.asList().get(0).toString());
    }

    @Test
    void asList_mutatingReturnedList_doesNotChangeTaskList() throws BobbyException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.add(new Todo("only task"));

        List<Task> tasks = taskList.asList();
        tasks.clear();

        assertEquals(1, taskList.size());
        assertEquals("[T][ ] only task", taskList.asList().get(0).toString());
    }
}
