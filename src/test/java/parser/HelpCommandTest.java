package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import datahandler.DataHandler;
import exception.FriedbergUserInputException;

/**
 * Tests for the {@link HelpCommand} command summary.
 */
class HelpCommandTest {
    @TempDir
    Path tempDir;

    @Test
    void execute_validHelp_returnsAllCommands() throws Exception {
        HelpCommand command = new HelpCommand();
        CommandContext context = new CommandContext(
                new DataHandler(tempDir, tempDir.resolve("tasks.txt")));

        String response = command.execute("help", context);

        String[] commandWords = {
            "todo", "deadline", "event", "list", "find", "mark", "unmark",
            "delete", "archive", "alist", "unarchive", "help", "bye"
        };
        for (String commandWord : commandWords) {
            assertTrue(response.contains(commandWord), commandWord);
        }
    }

    @Test
    void execute_helpWithParameter_throwsUserInputException() throws Exception {
        HelpCommand command = new HelpCommand();
        CommandContext context = new CommandContext(
                new DataHandler(tempDir, tempDir.resolve("tasks.txt")));

        FriedbergUserInputException error = assertThrows(
                FriedbergUserInputException.class, () -> command.execute("help extra", context));

        assertEquals(new FriedbergUserInputException(
                "The help command does not take parameters. Expected: help").getMessage(), error.getMessage());
    }
}
