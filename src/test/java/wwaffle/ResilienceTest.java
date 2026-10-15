package wwaffle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ResilienceTest {
    @TempDir
    private Path directory;

    @Test
    void malformedSave_blocksEditsAndPreservesOriginal() throws IOException {
        Path file = directory.resolve("tasks.txt");
        String original = "T | wrong | keep me\n";
        Files.writeString(file, original);
        WWaffle app = new WWaffle(file.toString());
        assertTrue(app.getWelcomeMessage().startsWith("⚠️"));
        for (String command : List.of("todo new", "deadline new /by 2026-09-18",
                "event new /from 2pm /to 3pm", "mark 1", "unmark 1", "delete 1", "sort name", "clear")) {
            assertTrue(app.getResponse(command).startsWith("⚠️"));
            assertEquals(original, Files.readString(file));
        }
    }

    @Test
    void failedSaves_restoreAddDeleteMarkUnmarkAndSort() throws IOException {
        Path file = directory.resolve("tasks.txt");
        WWaffle app = new WWaffle(file.toString());
        app.getResponse("todo zebra");
        app.getResponse("todo apple");
        app.getResponse("mark 1");
        String original = app.getResponse("list");
        Files.delete(file);
        Files.createDirectory(file);
        Files.writeString(file.resolve("blocker"), "keep");
        for (String command : List.of("todo new", "delete 1", "mark 2", "unmark 1",
                "sort name", "sort status", "clear")) {
            assertTrue(app.getResponse(command).startsWith("⚠️"), command);
            assertEquals(original, app.getResponse("list"), command);
            assertEquals("keep", Files.readString(file.resolve("blocker")));
        }
    }

    @Test
    void whitespaceAndUnicode_surviveSaveAndReload() {
        Path file = directory.resolve("tasks.txt");
        WWaffle app = new WWaffle(file.toString());
        assertTrue(app.getResponse("  todo\t 阅读   chapter 5  ").startsWith("Task added."));
        app.getResponse("deadline\t report   /by   2026-09-18");
        app.getResponse("event meeting   /from   2pm  /to   3pm");
        assertEquals(3, app.getTaskCount());
        assertEquals(app.getResponse("list"), new WWaffle(file.toString()).getResponse("list"));
    }

    @Test
    void invalidCommands_doNotChangeTasksOrFile() throws IOException {
        Path file = directory.resolve("tasks.txt");
        WWaffle app = new WWaffle(file.toString());
        app.getResponse("todo keep");
        String original = Files.readString(file);
        for (String command : List.of("todo", "todo a | b", "deadline x /by 2026-02-30",
                "deadline x /by 2026-09-18 /by 2026-09-19", "event x /from a /to b /to c",
                "event x /from a /from b /to c", "mark -1", "mark 2147483648", "delete 0",
                "unmark abc", "find", "sort wrong", "clear now")) {
            assertTrue(app.getResponse(command).startsWith("⚠️"), command);
            assertEquals(original, Files.readString(file), command);
            assertEquals(1, app.getTaskCount());
        }
    }
}
