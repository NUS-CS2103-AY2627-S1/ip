package blud.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import blud.exception.CommandFormatException;
import blud.model.Task;
import blud.model.TaskList;
import blud.model.ToDo;
import blud.storage.Storage;

/** Tests extracted command operations and their persistence behavior. */
public class CommandOperationsTest {
    @TempDir
    private Path temporaryDirectory;

    /** Verifies that adding a task updates storage and formats its response. */
    @Test
    public void addTask_newTask_persistsAndReturnsResponse() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        TaskList taskList = new TaskList();

        String response = new AddTask(taskList, storage).execute(new ToDo(new String[] {"todo read"}));

        assertEquals("added: [T][ ] read\nNow you have 1 tasks in the list", response);
        assertEquals(1, storage.loadTasks().size());
    }

    /** Verifies that an empty list has the expected list response. */
    @Test
    public void listTasks_emptyList_returnsPreface() {
        assertEquals("Here are the tasks in your list:", new ListTasks(new TaskList()).execute());
    }

    /** Verifies that task mutations return responses and persist state. */
    @Test
    public void taskOperations_markUnmarkDelete_updatesTaskList() {
        Storage storage = new Storage(temporaryDirectory.resolve("tasks.txt").toString());
        TaskList taskList = new TaskList();
        Task task = new ToDo(new String[] {"todo read"});
        taskList.addTask(task);
        TaskOperations operations = new TaskOperations(taskList, storage);

        assertEquals("Nice! I've marked this task as done:\n[T][X] read", operations.mark(new String[] {"mark", "1"}));
        assertEquals("OK, I've marked this task as not done yet:\n[T][ ] read",
                operations.unmark(new String[] {"unmark", "1"}));
        assertEquals("Task removed successfully:\n[T][ ] read", operations.delete(new String[] {"delete", "1"}));
        assertEquals(0, taskList.getSize());
    }

    /** Verifies that sort operation accepts descending status order. */
    @Test
    public void sortTasks_statusDescending_returnsResponse() {
        TaskList taskList = new TaskList();
        taskList.addTask(new ToDo(new String[] {"todo read"}));

        assertEquals("Tasks sorted by completion status in descending order.",
                new SortTasks(taskList).execute(new String[] {"sort", "status", "desc"}));
    }

    /** Verifies that unsupported sort criteria are rejected. */
    @Test
    public void sortTasks_unknownCriterion_throwsCommandFormatException() {
        assertThrows(CommandFormatException.class,
                () -> new SortTasks(new TaskList()).execute(new String[] {"sort", "priority"}));
    }
}
