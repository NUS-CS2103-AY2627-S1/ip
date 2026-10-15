package michael;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import task.Task;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskListTest {

    private TaskList taskList;
    private DummyTask task1;
    private DummyTask task2;

    /**
     * Concrete stub extending Task for testing TaskList in isolation.
     */
    private static class DummyTask extends Task {
        public DummyTask(String description) throws MichaelException {
            super(description, "todo");
        }
    }

    @BeforeEach
    void setUp() throws MichaelException {
        taskList = new TaskList();
        task1 = new DummyTask("task 1");
        task2 = new DummyTask("task 2");
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Default constructor initializes an empty list")
        void defaultConstructor_createsEmptyList() {
            assertEquals(0, taskList.size());
            assertTrue(taskList.getTasks().isEmpty());
        }

        @Test
        @DisplayName("List constructor creates a defensive copy of provided tasks")
        void listConstructor_createsDefensiveCopy() {
            List<Task> initialTasks = new ArrayList<>();
            initialTasks.add(task1);

            TaskList populatedList = new TaskList(initialTasks);
            initialTasks.clear(); // Mutate original list

            assertEquals(1, populatedList.size()); // TaskList should remain unaffected
        }
    }

    @Nested
    @DisplayName("Task Management Operations")
    class OperationTests {

        @Test
        @DisplayName("add() increases size and appends task")
        void add_validTask_increasesSize() throws MichaelException {
            taskList.add(task1);
            assertEquals(1, taskList.size());
            assertEquals(task1, taskList.get(0));
        }

        @Test
        @DisplayName("delete() removes and returns task at target index")
        void delete_validIndex_removesTask() throws MichaelException {
            taskList.add(task1);
            taskList.add(task2);

            Task removedTask = taskList.delete(0);

            assertEquals(task1, removedTask);
            assertEquals(1, taskList.size());
            assertEquals(task2, taskList.get(0));
        }

        @Test
        @DisplayName("mark() marks task as done and returns it")
        void mark_validIndex_marksTaskDone() throws MichaelException {
            taskList.add(task1);

            Task markedTask = taskList.mark(0);

            assertTrue(markedTask.getStatusIcon().equals("X"));
            assertEquals(task1, markedTask);
        }

        @Test
        @DisplayName("unmark() marks task as not done and returns it")
        void unmark_validIndex_marksTaskNotDone() throws MichaelException {
            task1.markAsDone();
            taskList.add(task1);

            Task unmarkedTask = taskList.unmark(0);

            assertFalse(unmarkedTask.getStatusIcon().equals("X"));
            assertEquals(task1, unmarkedTask);
        }

        @Test
        @DisplayName("find() returns tasks containing the keyword")
        void find_matchingKeyword_returnsMatchingTask() throws MichaelException {
            taskList.add(task1);
            taskList.add(task2);

            assertEquals(List.of(task1), taskList.find("task 1"));
        }
    }

    @Nested
    @DisplayName("Index Validation & Boundary Edge Cases")
    class ExceptionTests {

        @Test
        @DisplayName("Operations throw MichaelException on negative index")
        void negativeIndex_throwsException() {
            taskList.add(task1);

            assertThrows(MichaelException.class, () -> taskList.get(-1));
            assertThrows(MichaelException.class, () -> taskList.delete(-1));
            assertThrows(MichaelException.class, () -> taskList.mark(-1));
            assertThrows(MichaelException.class, () -> taskList.unmark(-1));
        }

        @Test
        @DisplayName("Operations throw MichaelException when index equals size")
        void indexEqualToSize_throwsException() {
            taskList.add(task1); // size is 1, index 1 is out of bounds

            assertThrows(MichaelException.class, () -> taskList.get(1));
            assertThrows(MichaelException.class, () -> taskList.delete(1));
            assertThrows(MichaelException.class, () -> taskList.mark(1));
            assertThrows(MichaelException.class, () -> taskList.unmark(1));
        }

        @Test
        @DisplayName("Operations throw MichaelException on empty list")
        void emptyList_throwsException() {
            assertThrows(MichaelException.class, () -> taskList.get(0));
            assertThrows(MichaelException.class, () -> taskList.delete(0));
        }
    }
}
