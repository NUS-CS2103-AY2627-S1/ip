package vector.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void findTasks_matchingKeyword_returnsMatchingTasks() {
        TaskList taskList = new TaskList();
        try {
            taskList.add(new Todo("read book"));
            taskList.add(new Todo("return book"));
            taskList.add(new Todo("buy groceries"));
        } catch (vector.VectorException e) {
            org.junit.jupiter.api.Assertions.fail("Should not throw exception.");
        }

        ArrayList<Task> result = taskList.findTasks("book");
        assertEquals(2, result.size());
        assertEquals("read book", result.get(0).getDescription());
        assertEquals("return book", result.get(1).getDescription());
    }

    @Test
    public void findTasks_noMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList();
        try {
            taskList.add(new Todo("read book"));
            taskList.add(new Todo("return book"));
        } catch (vector.VectorException e) {
            org.junit.jupiter.api.Assertions.fail("Should not throw exception.");
        }

        ArrayList<Task> result = taskList.findTasks("grocery");
        assertEquals(0, result.size());
    }

    @Test
    public void add_duplicateTask_throwsException() {
        TaskList taskList = new TaskList();
        try {
            taskList.add(new Todo("read book"));
        } catch (vector.VectorException e) {
            org.junit.jupiter.api.Assertions.fail("Should not throw exception.");
        }

        try {
            taskList.add(new Todo("read book"));
            org.junit.jupiter.api.Assertions.fail("Should throw VectorException for duplicate task.");
        } catch (vector.VectorException e) {
            assertEquals("An identical task already exists in the matrix.", e.getMessage());
        }
    }

    @Test
    public void get_validIndex_returnsTask() {
        TaskList taskList = new TaskList();
        try {
            taskList.add(new Todo("read book"));
            Task t = taskList.get(0);
            assertEquals("read book", t.getDescription());
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.fail("Should not throw exception.");
        }
    }

    @Test
    public void get_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();
        try {
            taskList.get(0);
            org.junit.jupiter.api.Assertions.fail("Should throw IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException e) {
            // Success
        }
    }

    @Test
    public void remove_validIndex_removesTask() {
        TaskList taskList = new TaskList();
        try {
            taskList.add(new Todo("read book"));
            Task removed = taskList.remove(0);
            assertEquals("read book", removed.getDescription());
            assertEquals(0, taskList.size());
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.fail("Should not throw exception.");
        }
    }

    @Test
    public void remove_invalidIndex_throwsIndexOutOfBoundsException() {
        TaskList taskList = new TaskList();
        try {
            taskList.remove(0);
            org.junit.jupiter.api.Assertions.fail("Should throw IndexOutOfBoundsException.");
        } catch (IndexOutOfBoundsException e) {
            // Success
        }
    }

    @Test
    public void size_returnsCorrectSize() {
        TaskList taskList = new TaskList();
        assertEquals(0, taskList.size());
        try {
            taskList.add(new Todo("read book"));
            assertEquals(1, taskList.size());
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.fail("Should not throw exception.");
        }
    }
}
