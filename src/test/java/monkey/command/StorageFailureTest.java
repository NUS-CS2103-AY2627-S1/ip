package monkey.command;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import monkey.model.Deadline;
import monkey.model.TaskList;
import monkey.model.ToDos;
import monkey.storage.Storage;
import monkey.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StorageFailureTest {
    private static final String SAVE_ERROR = "OOPS! Monkey says: Could not save tasks to disk.";

    @TempDir
    Path temporaryDirectory;

    @Test
    void execute_todoWhenSaveFails_doesNotAddTask() throws Exception {
        TaskList tasks = new TaskList();
        StringBuilder output = new StringBuilder();

        new TodoCommand("buy milk").execute(tasks, createUi(output), createFailingStorage());

        assertTrue(tasks.isEmpty());
        assertEquals(SAVE_ERROR, output.toString());
    }

    @Test
    void execute_deadlineWhenSaveFails_doesNotAddTask() throws Exception {
        TaskList tasks = new TaskList();
        StringBuilder output = new StringBuilder();

        new AddCommand(Command.DEADLINE, "submit /by 2099-01-01")
                .execute(tasks, createUi(output), createFailingStorage());

        assertTrue(tasks.isEmpty());
        assertEquals(SAVE_ERROR, output.toString());
    }

    @Test
    void execute_deleteWhenSaveFails_preservesTask() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("buy milk"));
        StringBuilder output = new StringBuilder();

        new DeleteCommand("1").execute(tasks, createUi(output), createFailingStorage());

        assertEquals(1, tasks.size());
        assertEquals("buy milk", tasks.get(0).getDescription());
        assertEquals(SAVE_ERROR, output.toString());
    }

    @Test
    void execute_markWhenSaveFails_restoresCompletionState() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("buy milk"));
        StringBuilder output = new StringBuilder();

        new MarkCommand("1", true).execute(tasks, createUi(output), createFailingStorage());

        assertFalse(tasks.get(0).isDone());
        assertEquals(SAVE_ERROR, output.toString());
    }

    @Test
    void execute_unmarkAlreadyOpenTaskWhenSaveFails_preservesCompletionState() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new ToDos("buy milk"));
        StringBuilder output = new StringBuilder();

        new MarkCommand("1", false).execute(tasks, createUi(output), createFailingStorage());

        assertFalse(tasks.get(0).isDone());
        assertEquals(SAVE_ERROR, output.toString());
    }

    @Test
    void execute_unmarkCompletedTaskWhenSaveFails_restoresCompletionState() throws Exception {
        TaskList tasks = new TaskList();
        ToDos todo = new ToDos("buy milk");
        todo.markAsDone();
        tasks.add(todo);
        StringBuilder output = new StringBuilder();

        new MarkCommand("1", false).execute(tasks, createUi(output), createFailingStorage());

        assertTrue(tasks.get(0).isDone());
        assertEquals(SAVE_ERROR, output.toString());
    }

    @Test
    void execute_snoozeWhenSaveFails_restoresDeadline() throws Exception {
        TaskList tasks = new TaskList();
        Deadline deadline = new Deadline("submit", "2099-01-01");
        tasks.add(deadline);
        StringBuilder output = new StringBuilder();

        new SnoozeCommand("1 2099-02-01").execute(tasks, createUi(output), createFailingStorage());

        assertEquals("2099-01-01", deadline.getStorageValue());
        assertEquals(SAVE_ERROR, output.toString());
    }

    private Storage createFailingStorage() throws Exception {
        Path blockingFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockingFile, "block child creation");
        return new Storage(blockingFile.resolve("monkey.txt").toString());
    }

    private static Ui createUi(StringBuilder output) {
        return new Ui(message -> {
            if (!output.isEmpty()) {
                output.append('\n');
            }
            output.append(message);
        });
    }
}
