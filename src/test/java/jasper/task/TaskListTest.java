package jasper.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.Test;

import jasper.JasperException;

public class TaskListTest {
    @Test
    public void delete_validIndex_success() {
        Task stub = new TaskStub();
        TaskList tasks = new TaskList(List.of(stub));
        try {
            tasks.delete(0, 0);
            assertEquals(0, tasks.getCount());
        } catch (JasperException e) {
            fail("Exception should not be thrown for valid index");
        }
    }

    @Test
    public void delete_invalidIndices_exceptionThrown() {
        TaskStub stub1 = new TaskStub();
        TaskStub stub2 = new TaskStub();
        TaskList tasks = new TaskList(List.of(stub1, stub2)); // ignore duplicate tasks

        try {
            tasks.delete(-1, -1);
            fail("Expected JasperException for negative index");
        } catch (JasperException e) {
            assertEquals("Task index out of range!", e.getMessage());
        }

        try {
            tasks.delete(1, 2);
            fail("Expected JasperException for out of bounds index");
        } catch (JasperException e) {
            assertEquals("Task index out of range!", e.getMessage());
        }

        try {
            tasks.delete(2, 1);
            fail("Expected JasperException for start index greater than stop index");
        } catch (JasperException e) {
            assertEquals("Start index must be at most stop index!", e.getMessage());
        }
    }

    @Test
    public void delete_validRange_success() {
        Task stub1 = new TaskStub();
        Task stub2 = new TaskStub();
        Task stub3 = new TaskStub();
        TaskList tasks = new TaskList(List.of(stub1, stub2, stub3)); // ignore duplicate tasks

        try {
            String result = tasks.delete(0, 1); // delete first two tasks
            assertNotNull(result);
            assertEquals(1, tasks.getCount());
            // remaining task should be stub3 now at index 0
            tasks.delete(0, 0);
            assertEquals(0, tasks.getCount());
        } catch (JasperException e) {
            fail("Exception should not be thrown for valid range");
        }
    }

    @Test
    public void mark_validIndex_success() {
        Task stub = new TaskStub();
        TaskList tasks = new TaskList(List.of(stub));
        try {
            tasks.mark(0, 0);
            assertTrue(stub.isDone());
        } catch (JasperException e) {
            fail("Exception should not be thrown for valid index");
        }
    }

    @Test
    void mark_invalidIndices_exceptionThrown() {
        TaskStub stub1 = new TaskStub();
        TaskStub stub2 = new TaskStub();
        TaskList tasks = new TaskList(List.of(stub1, stub2)); // ignore duplicate tasks

        try {
            tasks.mark(-1, -1);
            fail("Expected JasperException for negative index");
        } catch (JasperException e) {
            assertEquals("Task index out of range!", e.getMessage());
        }

        try {
            tasks.mark(1, 2);
            fail("Expected JasperException for out of bounds index");
        } catch (JasperException e) {
            assertEquals("Task index out of range!", e.getMessage());
        }

        try {
            tasks.mark(2, 1);
            fail("Expected JasperException for start index greater than stop index");
        } catch (JasperException e) {
            assertEquals("Start index must be at most stop index!", e.getMessage());
        }
    }

    @Test
    public void mark_validRange_success() {
        Task stub1 = new TaskStub();
        Task stub2 = new TaskStub();
        Task stub3 = new TaskStub();
        TaskList tasks = new TaskList(List.of(stub1, stub2, stub3)); // ignore duplicate tasks

        try {
            String result = tasks.mark(1, 2); // mark last two tasks
            assertNotNull(result);
            assertFalse(stub1.isDone()); // first task should remain unmarked
            assertTrue(stub2.isDone());
            assertTrue(stub3.isDone());
        } catch (JasperException e) {
            fail("Exception should not be thrown for valid range");
        }
    }

    @Test
    public void unmark_validIndex_success() {
        Task stub = new TaskStub();
        stub.markDone();
        TaskList tasks = new TaskList(List.of(stub));
        try {
            tasks.unmark(0, 0);
            assertFalse(stub.isDone());
        } catch (JasperException e) {
            fail("Exception should not be thrown for valid index");
        }
    }

    @Test
    public void unmark_invalidIndices_exceptionThrown() {
        TaskStub stub1 = new TaskStub();
        TaskStub stub2 = new TaskStub();
        stub1.markDone();
        stub2.markUndone();
        TaskList tasks = new TaskList(List.of(stub1, stub2)); // ignore duplicate tasks

        try {
            tasks.unmark(-1, -1);
            fail("Expected JasperException for negative index");
        } catch (JasperException e) {
            assertEquals("Task index out of range!", e.getMessage());
        }

        try {
            tasks.unmark(1, 2);
            fail("Expected JasperException for out of bounds index");
        } catch (JasperException e) {
            assertEquals("Task index out of range!", e.getMessage());
        }

        try {
            tasks.unmark(2, 1);
            fail("Expected JasperException for start index greater than stop index");
        } catch (JasperException e) {
            assertEquals("Start index must be at most stop index!", e.getMessage());
        }
    }

    @Test
    public void unmark_validRange_success() {
        Task stub1 = new TaskStub();
        Task stub2 = new TaskStub();
        Task stub3 = new TaskStub();
        stub1.markDone();
        stub2.markDone();
        stub3.markDone();
        TaskList tasks = new TaskList(List.of(stub1, stub2, stub3)); // ignore duplicate tasks

        try {
            String result = tasks.unmark(0, 1); // unmark first two tasks
            assertNotNull(result);
            assertFalse(stub1.isDone());
            assertFalse(stub2.isDone());
            assertTrue(stub3.isDone()); // third task should remain marked
        } catch (JasperException e) {
            fail("Exception should not be thrown for valid range");
        }
    }
}
