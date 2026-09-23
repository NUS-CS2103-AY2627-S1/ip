package bob;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

import bob.storage.Storage;
import bob.ui.Ui;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command dispatch and validation with internal assertions enabled.
 */
public class BobTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void respond_unknownAndBlankCommands_reportsUnknownCommand() {
        assertTrue(Bob.class.desiredAssertionStatus());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Bob bob = createBob(output);

        for (String command : new String[] {"nonsense", "", "   "}) {
            output.reset();
            assertFalse(bob.respond(command));
            assertTrue(output.toString(StandardCharsets.UTF_8).contains("sorry bro, that aint a command"));
        }
    }

    @Test
    public void respond_invalidTaskNumber_reportsValidationError() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Bob bob = createBob(output);

        assertFalse(bob.respond("mark 1"));
        assertTrue(output.toString(StandardCharsets.UTF_8).contains("Invalid task number."));
        assertTrue(bob.respond("bye"));
    }

    @Test
    public void respond_exitCommands_requestsExitOnlyWithoutArguments() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Bob bob = createBob(output);

        for (String command : new String[] {"bye", "b"}) {
            output.reset();
            assertFalse(bob.respond(command + " now"));
            assertTrue(output.toString(StandardCharsets.UTF_8).contains("sorry bro, that aint a command"));
            output.reset();
            assertTrue(bob.respond(command));
            assertTrue(output.toString(StandardCharsets.UTF_8).contains("Bye. Hope to see you again soon!"));
        }
    }

    /**
     * Creates an isolated chatbot with captured output and temporary storage.
     */
    private Bob createBob(ByteArrayOutputStream output) {
        Ui ui = new Ui(new Scanner(""), new PrintStream(output, true, StandardCharsets.UTF_8));
        return new Bob(ui, new Storage(temporaryDirectory.resolve("bob.txt").toString()));
    }
}
