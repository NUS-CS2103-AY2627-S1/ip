package clarry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import clarry.storage.Storage;
import clarry.task.Todo;

/** Checks invalid input and persistence failures through the shared console/GUI backend. */
class MoreErrorHandlingTest {
    @TempDir
    private Path directory;

    @Test
    void getCommandResponse_extraWhitespace_acceptsCommandsAndExit() {
        Clarry bot = new Clarry(directory.resolve("tasks.txt"));
        assertFalse(bot.getCommandResponse(" \ttodo\t read   book  ").isError());
        assertTrue(bot.getResponse(" list ").contains("read book"));
        assertFalse(bot.getCommandResponse(" bye\t ").isError());
        assertTrue(bot.isExitRequested());
    }

    @Test
    void getCommandResponse_duplicateTask_rejectsRegardlessOfCompletion() {
        Clarry bot = new Clarry(directory.resolve("tasks.txt"));
        assertFalse(bot.getCommandResponse("todo read book").isError());
        assertFalse(bot.getCommandResponse("mark 1").isError());
        assertTrue(bot.getCommandResponse("todo read book").isError());
        assertFalse(bot.getCommandResponse("deadline read book /by 2026-09-15").isError());
        assertTrue(bot.getCommandResponse("deadline read book /by 2026-09-15").isError());
        assertFalse(bot.getCommandResponse("deadline read book /by 2026-09-16").isError());
    }

    @Test
    void getCommandResponse_repeatedOrMissingMarkers_returnsErrors() {
        Clarry bot = new Clarry(directory.resolve("tasks.txt"));
        for (String input : List.of("deadline book /by", "deadline /by 2026-09-15",
                "deadline book /by 2026-09-15 /by 2026-09-16",
                "event swim /from 2026-09-15 10:00 /from 2026-09-15 11:00 /to 2026-09-15 12:00",
                "event swim /from 2026-09-15 10:00 /to 2026-09-15 11:00 /to 2026-09-15 12:00",
                "event swim /to 2026-09-15 11:00 /from 2026-09-15 10:00",
                "event swim /from 2026-09-15 10:00 /to")) {
            assertTrue(bot.getCommandResponse(input).isError(), input);
        }
    }

    @Test
    void getCommandResponse_invalidDatesAndEqualTimes_returnsErrors() {
        Clarry bot = new Clarry(directory.resolve("tasks.txt"));
        for (String input : List.of("deadline book /by 2026-02-30", "on 2026-02-29",
                "event swim /from 2026-09-15 10:00 /to 2026-09-15 10:00",
                "event swim /from 2026-09-15 10:00 /to 2026-09-15 09:00",
                "event swim /from 2026-09-15 24:00 /to 2026-09-16 10:00")) {
            assertTrue(bot.getCommandResponse(input).isError(), input);
        }
    }

    @Test
    void getCommandResponse_invalidIndices_returnsErrorsWithoutChangingTasks() {
        Clarry bot = new Clarry(directory.resolve("tasks.txt"));
        bot.getResponse("todo book");
        for (String value : List.of("0", "-1", "+1", "1.0", "1 2", "2147483648", "-2147483648", "2")) {
            assertTrue(bot.getCommandResponse("delete " + value).isError(), value);
        }
        assertTrue(bot.getResponse("list").contains("1.[T][ ] book"));
    }

    @Test
    void getCommandResponse_unsafeDescriptionsAndBlankInput_returnsErrors() {
        Clarry bot = new Clarry(directory.resolve("tasks.txt"));
        for (String input : List.of("", "   ", "todo a\nb", "todo a\u0000b", "todo a|b",
                "deadline a|b /by 2026-09-15", "event a|b /from 2026-09-15 10:00 /to 2026-09-15 11:00")) {
            assertTrue(bot.getCommandResponse(input).isError(), input);
        }
        assertTrue(bot.getCommandResponse(null).isError());
    }

    @Test
    void getCommandResponse_missingDirectory_createsUtf8SaveAndReloads() {
        Path file = directory.resolve("nested/tasks.txt");
        Clarry bot = new Clarry(file);
        assertFalse(bot.getCommandResponse("todo café 鱼").isError());
        assertTrue(new Clarry(file).getResponse("list").contains("café 鱼"));
    }

    @Test
    void getCommandResponse_corruptedSave_preservesOriginalAndBlocksWrites() throws IOException {
        Path file = directory.resolve("tasks.txt");
        String original = "T | 0 | book\nbroken\nT | 1 | book\nE | 0 | swim | bad | bad\n";
        Files.writeString(file, original);
        Clarry bot = new Clarry(file);
        Response response = bot.getCommandResponse("list");
        assertTrue(response.isError());
        assertTrue(response.text().contains("Showing readable tasks only"));
        assertTrue(response.text().contains("1.[T][ ] book"));
        assertTrue(bot.getCommandResponse("delete 1").isError());
        assertEquals(original, Files.readString(file));
        assertTrue(bot.getResponse("list").contains("1.[T][ ] book"));
    }

    @Test
    void getCommandResponse_unreadablePath_reportsLoadFailureInResponse() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.createDirectory(file);
        Clarry bot = new Clarry(file);
        Response response = bot.getCommandResponse("list");
        assertTrue(response.isError());
        assertTrue(response.text().contains("Could not load saved tasks"));
        assertTrue(bot.getCommandResponse("todo book").isError());
        assertTrue(Files.isDirectory(file));
    }

    @Test
    void getCommandResponse_saveFailure_rollsBackAddDeleteAndCompletion() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Clarry bot = new Clarry(file);
        bot.getResponse("todo book");
        bot.getResponse("todo completed task");
        bot.getResponse("mark 2");
        String original = bot.getResponse("list");
        Files.delete(file);
        Files.createDirectory(file);
        Files.writeString(file.resolve("blocker"), "Keep this directory");
        for (String input : List.of("todo swim", "delete 1", "mark 1", "unmark 2")) {
            Response response = bot.getCommandResponse(input);
            assertTrue(response.isError(), input);
            assertTrue(response.text().contains("No changes were applied"));
            assertEquals(original, bot.getResponse("list"));
        }
        Files.delete(file.resolve("blocker"));
        Files.delete(file);
        assertFalse(bot.getCommandResponse("mark 1").isError());
        assertTrue(new Clarry(file).getResponse("list").contains("[X] book"));
    }

    @Test
    void save_serializationFailure_keepsPreviousFileAndCleansTemporaryFile() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | original\n");
        Todo broken = new Todo("broken") {
            @Override
            public String toFileFormat() {
                throw new IllegalStateException("Simulated serialization failure");
            }
        };
        assertThrows(IllegalStateException.class, () -> new Storage(file).save(List.of(new Todo("new"), broken)));
        assertEquals("T | 0 | original\n", Files.readString(file));
        try (var files = Files.list(directory)) {
            assertEquals(1, files.count());
        }
    }
}
