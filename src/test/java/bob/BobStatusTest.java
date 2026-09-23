package bob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

import bob.storage.Storage;
import bob.task.Task;
import bob.task.Todo;
import bob.ui.Ui;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies mark and unmark feedback, validation, and persistence through command dispatch.
 */
public class BobStatusTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void respond_markAndUnmark_updatesOnlySelectedTaskAndPersists() throws IOException {
        Storage storage = new Storage(temporaryDirectory.resolve("bob.txt").toString());
        storage.save(List.of(new Todo("first"), new Todo("second")));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Bob bob = createBob(storage, output);

        for (String command : new String[] {"mark", "mark", "unmark", "unmark"}) {
            output.reset();
            assertFalse(bob.respond(command + " 2"));
            boolean isDone = command.equals("mark");
            List<Task> savedTasks = storage.load();
            assertEquals(2, savedTasks.size());
            assertFalse(savedTasks.get(0).isDone());
            assertEquals(isDone, savedTasks.get(1).isDone());
            String status = isDone ? "done" : "not done";
            String taskText = isDone ? "[T][X] second" : "[T][ ] second";
            assertEquals("I marked this task as " + status + ":\n" + taskText + "\n", plainOutput(output));
        }
    }

    @Test
    public void respond_invalidStatusArguments_reportsErrorsWithoutSaving() throws IOException {
        Path file = temporaryDirectory.resolve("bob.txt");
        Storage storage = new Storage(file.toString());
        storage.save(List.of(new Todo("first")));
        String originalData = Files.readString(file);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Bob bob = createBob(storage, output);

        for (String command : new String[] {"mark", "unmark"}) {
            String[] arguments = {"", " 1 2", " abc", " 0", " -1", " 2"};
            String usage = "Invalid command format. Use: " + command + " <task_number>\n";
            String[] expected = {usage, usage, "Invalid task number format.\n",
                "Invalid task number.\n", "Invalid task number.\n", "Invalid task number.\n"};
            for (int i = 0; i < arguments.length; i++) {
                output.reset();
                assertFalse(bob.respond(command + arguments[i]));
                assertEquals(expected[i], plainOutput(output));
                assertEquals(originalData, Files.readString(file));
            }
        }
    }

    @Test
    public void respond_saveFailure_reportsFailureAndRetainsStatusInMemory() throws IOException {
        Path directory = Files.createDirectory(temporaryDirectory.resolve("data"));
        Path file = directory.resolve("bob.txt");
        Storage storage = new Storage(file.toString());
        storage.save(List.of(new Todo("first")));
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Bob bob = createBob(storage, output);
        // Replace the storage directory with a file to force a portable save failure.
        Files.delete(file);
        Files.delete(directory);
        Files.writeString(directory, "not a directory");

        for (String command : new String[] {"mark", "unmark"}) {
            output.reset();
            assertFalse(bob.respond(command + " 1"));
            String status = command.equals("mark") ? "done" : "not done";
            assertTrue(plainOutput(output).startsWith("Warning: could not save tasks to the data file.\n"));
            assertTrue(plainOutput(output).contains("I marked this task as " + status + ":\n"));
            output.reset();
            bob.respond("list");
            String taskText = command.equals("mark") ? "[T][X] first" : "[T][ ] first";
            assertTrue(plainOutput(output).contains(taskText));
        }
    }

    /**
     * Creates a chatbot with isolated storage and captured output.
     */
    private Bob createBob(Storage storage, ByteArrayOutputStream output) {
        return new Bob(new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8)), storage);
    }

    /**
     * Returns output without terminal colors and with normalized line endings.
     */
    private String plainOutput(ByteArrayOutputStream output) {
        return output.toString(StandardCharsets.UTF_8).replaceAll("\u001B\\[[;\\d]*m", "")
                .replace("\r\n", "\n");
    }
}
