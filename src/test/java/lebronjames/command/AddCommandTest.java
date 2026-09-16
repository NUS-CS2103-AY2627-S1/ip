package lebronjames.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lebronjames.LebronJamesException;
import lebronjames.storage.Storage;
import lebronjames.task.Deadline;
import lebronjames.task.Task;
import lebronjames.task.TaskDateTime;
import lebronjames.task.TaskList;
import lebronjames.task.Todo;
import lebronjames.ui.Ui;

/**
 * Tests that {@link AddCommand} refuses a task the user already has.
 *
 * <p>{@link TaskList} only answers whether a duplicate exists; turning that
 * answer into a refusal is this command's job, so it is tested here rather than
 * alongside the task list.
 */
public class AddCommandTest {
    /** Throwaway folder created and deleted by JUnit around this test class. */
    @TempDir
    private static Path tempFolder;

    /**
     * Returns storage that saves somewhere harmless, so a test does not touch
     * the real save file.
     *
     * @return Storage pointing into a temporary folder.
     */
    private static Storage silentStorage() {
        return new Storage(tempFolder.toString(), "tasks.txt");
    }

    /**
     * Adds a task to the given list through an AddCommand.
     *
     * @param tasks List to add to.
     * @param task Task to add.
     * @throws LebronJamesException If the command refuses the task.
     */
    private static void add(TaskList tasks, Task task) throws LebronJamesException {
        new AddCommand(task).execute(tasks, new Ui(), silentStorage());
    }

    @Test
    public void execute_taskNotAlreadyPresent_added() throws LebronJamesException {
        TaskList tasks = new TaskList();

        add(tasks, new Todo("read book"));

        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_duplicateTask_refusedAndListUnchanged() throws LebronJamesException {
        TaskList tasks = new TaskList();
        add(tasks, new Todo("read book"));

        assertThrows(LebronJamesException.class, () -> add(tasks, new Todo("read book")));
        // The point of refusing is that the list does not grow.
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_duplicateDifferingInCase_refused() throws LebronJamesException {
        TaskList tasks = new TaskList();
        add(tasks, new Todo("read book"));

        assertThrows(LebronJamesException.class, () -> add(tasks, new Todo("Read Book")));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_duplicateOfCompletedTask_refused() throws LebronJamesException {
        TaskList tasks = new TaskList();
        add(tasks, new Todo("read book"));
        tasks.get(1).markAsDone();

        // Ticking a task off must not make room for a second copy of it.
        assertThrows(LebronJamesException.class, () -> add(tasks, new Todo("read book")));
        assertEquals(1, tasks.size());
    }

    @Test
    public void execute_refusal_messageNamesTheExistingTaskNumber() throws LebronJamesException {
        TaskList tasks = new TaskList();
        add(tasks, new Todo("write essay"));
        add(tasks, new Todo("read book"));

        LebronJamesException exception = assertThrows(LebronJamesException.class, () ->
                add(tasks, new Todo("read book")));

        // The user has to be able to find the task that is already there.
        assertTrue(exception.getMessage().contains("2"),
                "message should name the number of the existing task");
    }

    @Test
    public void execute_sameDescriptionDifferentDueDate_added() throws LebronJamesException {
        TaskList tasks = new TaskList();
        add(tasks, new Deadline("submit report", TaskDateTime.parse("2019-12-02")));

        // A weekly chore is not a duplicate; only the date tells them apart.
        add(tasks, new Deadline("submit report", TaskDateTime.parse("2019-12-09")));

        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_sameDescriptionDifferentTaskType_added() throws LebronJamesException {
        TaskList tasks = new TaskList();
        add(tasks, new Todo("read book"));

        add(tasks, new Deadline("read book", TaskDateTime.parse("2019-12-02")));

        assertEquals(2, tasks.size());
    }
}
