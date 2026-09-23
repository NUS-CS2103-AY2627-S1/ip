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
import java.util.Scanner;

import bob.storage.Storage;
import bob.ui.Ui;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests complete command workflows, persistence across sessions, and error recovery.
 */
public class BobWorkflowTest {
    @TempDir
    private Path temporaryDirectory;

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    @Test
    public void respond_restart_restoresAllTaskTypesAndCompletionStatus() {
        Bob bob = createBob();
        bob.respond("todo read book");
        bob.respond("deadline return book /by 2/12/2019 1800");
        bob.respond("event meeting /from 3/12/2019 1400 /to 3/12/2019 1600");
        bob.respond("mark 2");
        assertTrue(bob.respond("bye"));

        Bob restartedBob = createBob();

        assertEquals("Here are the tasks in your list:\n"
                + "1.[T][ ] read book\n"
                + "2.[D][X] return book (by: Dec 02 2019 1800hrs)\n"
                + "3.[E][ ] meeting (from: Dec 03 2019 1400hrs to: Dec 03 2019 1600hrs)\n",
                respond(restartedBob, "list"));
    }

    @Test
    public void respond_deleteThenMark_persistsDeletionAndUsesNewNumber() {
        Bob bob = createBob();
        bob.respond("todo first");
        bob.respond("todo second");
        bob.respond("todo third");

        assertEquals("Noted. I've removed this task:\n[T][ ] second\n"
                + "Now you have 2 tasks in the list.\n", respond(bob, "delete 2"));
        assertEquals("I marked this task as done:\n[T][X] third\n", respond(bob, "mark 2"));

        assertEquals("Here are the tasks in your list:\n1.[T][ ] first\n2.[T][X] third\n",
                respond(createBob(), "list"));
    }

    @Test
    public void respond_emptyList_handlesQueriesAndRejectsMutations() {
        Bob bob = createBob();

        assertEquals("No tasks yet!\n", respond(bob, "list"));
        assertEquals("Here are the matching tasks in your list:\n", respond(bob, "find book"));
        for (String command : new String[] {"mark 1", "unmark 1", "delete 1"}) {
            assertEquals("Invalid task number.\n", respond(bob, command));
        }
        assertEquals("No tasks yet!\n", respond(bob, "list"));
    }

    @Test
    public void respond_overflowingTaskNumbers_preservesSavedTasks() throws IOException {
        Bob bob = createBob();
        bob.respond("todo keep me");
        String originalData = Files.readString(temporaryDirectory.resolve("bob.txt"));

        for (String command : new String[] {"mark", "unmark", "delete"}) {
            assertEquals("Invalid task number format.\n",
                    respond(bob, command + " 999999999999999999"));
            assertEquals(originalData, Files.readString(temporaryDirectory.resolve("bob.txt")));
        }
        assertEquals("Here are the tasks in your list:\n1.[T][ ] keep me\n", respond(bob, "list"));
    }

    @Test
    public void respond_invalidInputThenValidTask_recoversWithoutChangingExistingTasks() throws IOException {
        Bob bob = createBob();
        bob.respond("todo existing");
        String originalData = Files.readString(temporaryDirectory.resolve("bob.txt"));
        String dateError = "Invalid date or time. Use dd/MM/yyyy or "
                + "dd/MM/yyyy HHmm with a 24-hour time.\n";

        for (String command : new String[] {"", "   ", "nonsense"}) {
            assertEquals("sorry bro, that aint a command\n", respond(bob, command));
            assertEquals(originalData, Files.readString(temporaryDirectory.resolve("bob.txt")));
        }
        for (String command : new String[] {"deadline invalid /by 31/02/2019",
            "event invalid /from 3/12/2019 1400 /to 3/12/2019 2500"}) {
            assertEquals(dateError, respond(bob, command));
            assertEquals(originalData, Files.readString(temporaryDirectory.resolve("bob.txt")));
        }
        respond(bob, "todo recovered");

        assertEquals("Here are the tasks in your list:\n1.[T][ ] existing\n2.[T][ ] recovered\n",
                respond(createBob(), "list"));
    }

    @Test
    public void constructor_loadFailure_reportsWarningAndAllowsRecovery() throws IOException {
        Path file = temporaryDirectory.resolve("bob.txt");
        // A directory at the file path causes a read failure without relying on OS permissions.
        Files.createDirectory(file);
        output.reset();

        Bob bob = createBob();

        assertEquals("Warning: could not load tasks from the data file.\n", plainOutput());
        assertEquals("No tasks yet!\n", respond(bob, "list"));
        Files.delete(file);
        respond(bob, "todo recovered");
        assertEquals("Here are the tasks in your list:\n1.[T][ ] recovered\n",
                respond(createBob(), "list"));
    }

    @Test
    public void run_bye_stopsBeforeLaterCommandsAndPreservesSavedTasks() {
        Ui ui = new Ui(new Scanner("todo saved\nbye\ntodo ignored\n"),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        Bob bob = new Bob(ui, createStorage());

        bob.run();

        assertTrue(plainOutput().startsWith("hi im bob\n"
                + "____________________________________________________________\n"));
        assertTrue(plainOutput().contains("Bye. Hope to see you again soon!"));
        assertFalse(plainOutput().contains("ignored"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] saved\n", respond(createBob(), "list"));
    }

    /**
     * Creates a fresh chatbot session using the test's shared persistence file.
     */
    private Bob createBob() {
        Ui ui = new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
        return new Bob(ui, createStorage());
    }

    /**
     * Creates storage isolated from real application data.
     */
    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("bob.txt").toString());
    }

    /**
     * Executes a non-exit command and returns only its response.
     */
    private String respond(Bob bob, String command) {
        output.reset();
        assertFalse(bob.respond(command));
        return plainOutput();
    }

    /**
     * Returns captured output without ANSI colors and with normalized line endings.
     */
    private String plainOutput() {
        return output.toString(StandardCharsets.UTF_8).replaceAll("\u001B\\[[;\\d]*m", "")
                .replace("\r\n", "\n");
    }
}
