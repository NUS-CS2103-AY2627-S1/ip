package bot67;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bot67.storage.Storage;
import bot67.task.Task;
import bot67.ui.Ui;

/** Checks command recovery and consistency between in-memory tasks and saved data. */
class Bot67ErrorHandlingTest {
    @TempDir
    private Path directory;

    @Test
    void failedChanges_restoreListStatusAndOrderAndAllowRetry() throws IOException {
        Path file = directory.resolve("tasks.txt");
        FailingStorage storage = new FailingStorage(file);
        Bot67 bot = new Bot67(storage);
        bot.getResponse("todo zebra");
        bot.getResponse("todo apple");
        bot.getResponse("mark 2");
        String original = Files.readString(file);
        String originalList = bot.getResponse("list");
        storage.isFailing = true;
        for (String command : List.of("todo new", "deadline new /by Sunday",
                "event new /from Mon /to Tue", "mark 1", "mark 2", "unmark 1", "unmark 2", "delete 1", "sort")) {
            String response = bot.getResponse(command);
            assertTrue(response.contains("Could not save tasks. Your change was not applied."), command);
            assertTrue(bot.isLastResponseError());
            assertEquals(originalList, bot.getResponse("list"), command);
            assertFalse(bot.isLastResponseError());
            assertEquals(original, Files.readString(file), command);
        }
        storage.isFailing = false;
        assertTrue(bot.getResponse("todo retry").contains("I've added this task:"));
        assertEquals(3, new Storage(file).load().size());
    }

    @Test
    void damagedLoad_warnsAndBlocksChangesWithoutOverwritingFile() throws IOException {
        Path file = directory.resolve("tasks.txt");
        String original = "T | 0 | good\nD | 0 | bad | 2026-02-30\n";
        Files.writeString(file, original);
        Bot67 bot = new Bot67(new Storage(file));
        assertTrue(bot.getStartupWarning().contains("Invalid saved task at line 2."));
        for (String command : List.of("todo replacement", "sort")) {
            assertTrue(bot.getResponse(command).contains("Changes are disabled"));
            assertTrue(bot.isLastResponseError());
        }
        assertEquals(original, Files.readString(file));
        assertEquals(Ui.GOODBYE, bot.getResponse("  bye  "));
        assertTrue(bot.isExitRequested());
    }

    @Test
    void invalidCommands_recoverAndAllowNumbersAboveOneHundred() {
        Bot67 bot = new Bot67(new Storage(directory.resolve("tasks.txt")));
        assertTrue(bot.getResponse(null).contains("Enter a command."));
        assertTrue(bot.getResponse("\nbye").contains("control characters"));
        assertFalse(bot.isExitRequested());
        assertTrue(bot.getResponse("mark 1").contains("Task number is out of range."));
        assertTrue(bot.getResponse("unmark 1").contains("Task number is out of range."));
        for (int i = 1; i <= 101; i++) {
            bot.getResponse("todo task " + i);
            assertFalse(bot.isLastResponseError());
        }
        assertTrue(bot.getResponse("mark 101").contains("[X] task 101"));
        assertFalse(bot.isLastResponseError());
    }

    /** Simulates a permission or disk failure after the initial successful saves. */
    private static class FailingStorage extends Storage {
        private boolean isFailing;

        FailingStorage(Path file) {
            super(file);
        }

        @Override
        public void save(List<Task> tasks) throws IOException {
            if (isFailing) {
                throw new IOException("Simulated write failure");
            }
            super.save(tasks);
        }
    }
}
