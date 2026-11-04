import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import soar.command.AddCommand;
import soar.command.Command;
import soar.command.DeleteCommand;
import soar.command.EditCommand;
import soar.command.EditField;
import soar.command.MarkCommand;
import soar.command.UnmarkCommand;
import soar.exception.InvalidTaskNumberException;
import soar.exception.StorageException;
import soar.storage.Storage;
import soar.task.Task;
import soar.task.TaskList;
import soar.task.ToDo;
import soar.ui.Ui;

/**
 * Checks command behavior that is not practical to trigger in normal UI cases.
 */
public class CommandTest {
    /**
     * Verifies that task changes are rolled back when their saves fail.
     *
     * @param args command-line arguments; they are not used
     * @throws Exception if the test fixture cannot be prepared or cleaned up
     */
    public static void main(String[] args) throws Exception {
        Path storageTarget = Path.of("_temp", "command-save-target");
        Files.deleteIfExists(storageTarget);
        Files.createDirectories(storageTarget);
        Storage storage = new Storage(storageTarget);

        try {
            verifyAddRollback(storage);
            verifyMarkRollback(storage);
            verifyUnmarkRollback(storage);
            verifyDeleteRollback(storage);
            verifyEditRollback(storage);
            verifyTaskNumberValidation(storage);
        } finally {
            Files.deleteIfExists(storageTarget);
        }

        System.out.println("[PASS] Commands validated task numbers and rolled back additions, "
                + "completion changes, and deletions after save failures");
    }

    /** Verifies that a failed save removes the task that was just added. */
    private static void verifyAddRollback(Storage storage) throws Exception {
        TaskList tasks = new TaskList();
        expectSaveFailure(new AddCommand(new ToDo("must be rolled back")), tasks, storage);
        require(tasks.size() == 0, "A failed addition remained in the task list");
    }

    /** Verifies that a failed mark restores an incomplete task. */
    private static void verifyMarkRollback(Storage storage) throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("remain incomplete"));
        expectSaveFailure(new MarkCommand(1), tasks, storage);
        require(!tasks.get(0).isDone(), "A failed mark left the task completed");
    }

    /** Verifies that a failed unmark restores a completed task. */
    private static void verifyUnmarkRollback(Storage storage) throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDo("remain complete"));
        tasks.mark(0);
        expectSaveFailure(new UnmarkCommand(1), tasks, storage);
        require(tasks.get(0).isDone(), "A failed unmark left the task incomplete");
    }

    /** Verifies that a failed deletion restores the task at its original index. */
    private static void verifyDeleteRollback(Storage storage) throws Exception {
        TaskList tasks = new TaskList();
        Task firstTask = new ToDo("stay first");
        Task secondTask = new ToDo("stay second");
        tasks.add(firstTask);
        tasks.add(secondTask);
        expectSaveFailure(new DeleteCommand(1), tasks, storage);
        require(tasks.size() == 2, "A failed deletion changed the task count");
        require(tasks.get(0) == firstTask && tasks.get(1) == secondTask,
                "A failed deletion did not restore the original order");
    }

    /** Verifies that a failed edit restores the original task object. */
    private static void verifyEditRollback(Storage storage) throws Exception {
        Task original = new ToDo("before edit");
        TaskList tasks = new TaskList();
        tasks.add(original);

        expectSaveFailure(new EditCommand(1,
                Map.of(EditField.DESCRIPTION, "after edit")), tasks, storage);
        require(tasks.get(0) == original, "A failed edit did not restore the original task");
    }

    /** Verifies that numbered commands reject tasks that do not currently exist. */
    private static void verifyTaskNumberValidation(Storage storage) throws Exception {
        TaskList tasks = new TaskList();
        expectInvalidTaskNumber(new MarkCommand(1), tasks, storage, "open sky");

        Task task = new ToDo("remain unchanged");
        tasks.add(task);
        expectInvalidTaskNumber(new UnmarkCommand(0), tasks, storage, "Task 0");
        expectInvalidTaskNumber(new DeleteCommand(2), tasks, storage, "Task 2");
        require(tasks.size() == 1 && tasks.get(0) == task && !task.isDone(),
                "An invalid task number changed the task list");
    }

    /** Executes a numbered command and requires it to explain an invalid selection. */
    private static void expectInvalidTaskNumber(Command command, TaskList tasks,
            Storage storage, String expectedMessagePart) throws Exception {
        try {
            command.execute(tasks, new Ui(), storage);
            throw new AssertionError("The command should reject a task that does not exist");
        } catch (InvalidTaskNumberException expected) {
            require(expected.getMessage().contains(expectedMessagePart),
                    "The invalid task number did not produce the expected guidance");
        }
    }

    /** Executes a command and requires persistence to fail with the shared message. */
    private static void expectSaveFailure(Command command, TaskList tasks, Storage storage)
            throws Exception {
        try {
            command.execute(tasks, new Ui(), storage);
            throw new AssertionError("The command should fail when the data-file path is a directory");
        } catch (StorageException expected) {
            require(expected.getMessage().contains("change was not kept"),
                    "The save failure did not explain that the change was rolled back");
        }
    }

    /** Fails with a useful message when a condition is false. */
    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
