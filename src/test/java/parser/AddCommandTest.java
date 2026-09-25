package parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import datahandler.DataHandler;
import exception.FriedbergInternalException;

/**
 * Verifies that sharing response formatting does not change add-command persistence.
 */
class AddCommandTest {
    private static final String[] INPUTS = {
        "todo read book",
        "deadline work /by 2026-09-09",
        "event meeting /from 2026-09-09 /to 2026-09-10"
    };

    @TempDir
    Path tempDir;

    @Test
    void execute_validAddCommands_addAndSaveOncePerInput() throws Exception {
        CountingDataHandler dataHandler = new CountingDataHandler(tempDir, tempDir.resolve("tasks.txt"));
        CommandContext context = new CommandContext(dataHandler);

        for (int i = 0; i < INPUTS.length; i++) {
            String input = INPUTS[i];
            String response = CommandParser.parse(input).execute(input, context);
            int expectedCount = i + 1;

            assertEquals(expectedCount, context.getTasksSize());
            assertEquals(expectedCount, dataHandler.writeCount);
            assertTrue(response.endsWith("Now you have " + expectedCount + " tasks in the list."));
            assertEquals(context.renderTasks(), new CommandContext(dataHandler).renderTasks());
        }
    }

    @Test
    void execute_saveFailure_throwsInsteadOfReturningConfirmation() throws Exception {
        for (int i = 0; i < INPUTS.length; i++) {
            String input = INPUTS[i];
            Path dataFile = tempDir.resolve("blocked-" + i);
            CountingDataHandler dataHandler = new CountingDataHandler(tempDir, dataFile);
            CommandContext context = new CommandContext(dataHandler);
            // Replace the loaded file with a directory to cause a real write failure.
            Files.delete(dataFile);
            Files.createDirectory(dataFile);

            assertThrows(FriedbergInternalException.class, () -> CommandParser.parse(input).execute(input, context));
            assertEquals(1, dataHandler.writeCount);
            // Existing behavior mutates the list before saving; this refactoring must not change it.
            assertEquals(1, context.getTasksSize());
        }
    }

    /**
     * Counts save attempts while retaining real writes to temporary storage.
     */
    private static class CountingDataHandler extends DataHandler {
        private int writeCount;

        CountingDataHandler(Path folder, Path file) throws Exception {
            super(folder, file);
        }

        @Override
        public void write(String data) throws Exception {
            writeCount++;
            super.write(data);
        }
    }
}
