package chim.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaskListTest {

    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList();
    }

    @Test
    public void add_singleTask_sizeIncreases() {
        taskList.add(new Todo("read book"));
        assertEquals(1, taskList.size());
    }

    @Test
    public void add_multipleTasks_sizeMatchesCount() {
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("write essay"));
        taskList.add(new Todo("clean room"));
        assertEquals(3, taskList.size());
    }

    @Test
    public void get_validIndex_returnsCorrectTask() {
        Todo todo = new Todo("read book");
        taskList.add(todo);
        assertEquals(todo, taskList.get(0));
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() {
        Todo todo1 = new Todo("read book");
        Todo todo2 = new Todo("write essay");
        taskList.add(todo1);
        taskList.add(todo2);

        Task removed = taskList.delete(0);

        assertEquals(todo1, removed);
        assertEquals(1, taskList.size());
        assertEquals(todo2, taskList.get(0));
    }

    @Test
    public void delete_invalidIndex_throwsException() {
        taskList.add(new Todo("read book"));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(5));
    }

    @Test
    public void constructor_withExistingList_containsGivenTasks() {
        ArrayList<Task> existing = new ArrayList<>();
        existing.add(new Todo("task 1"));
        existing.add(new Todo("task 2"));

        TaskList list = new TaskList(existing);

        assertEquals(2, list.size());
    }

    @Test
    public void getTasks_returnsUnderlyingList() {
        Todo todo = new Todo("read book");
        taskList.add(todo);
        assertEquals(1, taskList.getTasks().size());
        assertTrue(taskList.getTasks().contains(todo));
    }

    @Test
    public void find_matchingKeyword_returnsMatchingTasks() {
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("write essay"));

        ArrayList<Task> results = taskList.find("book");

        assertEquals(1, results.size());
        assertEquals("read book", results.get(0).getDescription());
    }

    @Test
    public void find_caseInsensitive_stillMatches() {
        taskList.add(new Todo("Read Book"));

        ArrayList<Task> results = taskList.find("book");

        assertEquals(1, results.size());
    }

    @Test
    public void find_noMatch_returnsEmptyList() {
        taskList.add(new Todo("read book"));

        ArrayList<Task> results = taskList.find("xyz");

        assertTrue(results.isEmpty());
    }

    @Test
    public void find_multipleMatches_returnsAllMatching() {
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("return book"));
        taskList.add(new Todo("write essay"));

        ArrayList<Task> results = taskList.find("book");

        assertEquals(2, results.size());
    }
}
