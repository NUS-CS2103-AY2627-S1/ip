package monkey.command;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import monkey.model.TaskList;
import monkey.storage.Storage;
import monkey.ui.Ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimpleCommandTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void exitCommand_executeAndIsExit_reportsFarewellAndExitState() {
        StringBuilder output = new StringBuilder();
        ExitCommand command = new ExitCommand();

        command.execute(new TaskList(), new Ui(output::append), createStorage());

        assertEquals("Bye! Keep swinging, and I hope to see you again soon!", output.toString());
        assertTrue(command.isExit());
    }

    @Test
    void unknownCommand_defaultAndCustomMessages_reportsConfiguredMessage() {
        StringBuilder output = new StringBuilder();

        new UnknownCommand().execute(new TaskList(), createUi(output), createStorage());
        new UnknownCommand("custom error").execute(new TaskList(), createUi(output), createStorage());

        assertEquals("OOPS! Monkey says: This monkey does not recognize that command. "
                + "Try todo, deadline, event, list, mark, unmark, delete, or bye.\ncustom error", output.toString());
    }

    @Test
    void nonExitCommand_isExit_returnsFalse() {
        assertFalse(new ListCommand().isExit());
    }

    private Storage createStorage() {
        return new Storage(temporaryDirectory.resolve("monkey.txt").toString());
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
