package shannon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests commands through the chatbot API, including persisted state across sessions. */
class ShannonTest {
    @TempDir
    Path directory;

    @Test
    void getResponse_taskLifecycle_persistsEveryChange() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Shannon bot = new Shannon(file.toString());
        assertTrue(bot.getStartupMessage().contains("Hello!"));
        assertEquals("Your list is empty!", bot.getResponse("list").text());
        assertFalse(bot.getResponse("todo Read book").isError());
        assertFalse(bot.getResponse("deadline Report /by 2026-08-09").isError());
        assertFalse(bot.getResponse("event Camp /from 2026-08-09 /to 2026-08-10").isError());
        assertFalse(bot.getResponse("mark 1 2").isError());
        assertTrue(Files.readString(file).contains("T | 1 | Read book"));
        assertFalse(bot.getResponse("unmark 1").isError());
        assertTrue(Files.readString(file).contains("T | 0 | Read book"));
        assertTrue(bot.getResponse("find BOOK").text().contains("Read book"));
        assertEquals("No matching tasks found!", bot.getResponse("find missing").text());

        Shannon restored = new Shannon(file.toString());
        assertTrue(restored.getStartupMessage().contains("3 tasks"));
        assertEquals(bot.getResponse("list"), restored.getResponse("list"));
        assertFalse(restored.getResponse("delete 3 1").isError());
        assertEquals("1. [D][X] Report (by: Aug 09 2026)", restored.getResponse("list").text());
        assertFalse(restored.getResponse("delete 1").isError());
        assertEquals("", Files.readString(file));
    }

    @Test
    void getResponse_invalidCommands_reportErrorsWithoutChangingSavedTasks() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Shannon bot = new Shannon(file.toString());
        assertFalse(bot.getResponse("todo Read book").isError());
        String original = Files.readString(file);
        for (String command : new String[] {
            "", "unknown", "list done", "bye now", "todo read BOOK", "mark 1 99",
            "unmark 0", "delete 1 99", "delete two", "find", "todo"
        }) {
            Response response = bot.getResponse(command);
            assertTrue(response.isError(), command);
            assertFalse(response.text().isBlank(), command);
            assertEquals(original, Files.readString(file), command);
        }
        assertTrue(bot.isExitCommand(" BYE "));
        assertFalse(bot.isExitCommand("bye now"));
        assertFalse(bot.isExitCommand("list"));
        assertFalse(bot.getResponse("bye").isError());
    }

    @Test
    void getStartupMessage_corruptFile_reportsRecoveryAndLoadsValidTask() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "T | 0 | Keep\nbroken\n");
        Shannon bot = new Shannon(file.toString());
        String message = bot.getStartupMessage();
        assertTrue(message.contains("1 task"));
        assertTrue(message.contains("1 line"));
        assertTrue(message.contains("tasks.txt.bak"));
        assertEquals("1. [T][ ] Keep", bot.getResponse("list").text());
    }

    @Test
    void getStartupMessage_backupFailure_warnsAndRefusesSave() throws Exception {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "broken");
        Path backup = Files.createDirectory(directory.resolve("tasks.txt.bak"));
        Files.writeString(backup.resolve("keep"), "keep");
        Shannon bot = new Shannon(file.toString());
        assertTrue(bot.getStartupMessage().contains("won't save"));
        assertTrue(bot.getResponse("todo Read book").isError());
        assertEquals("broken", Files.readString(file));
    }

    @Test
    void getStartupMessage_directoryPath_reportsLoadFailureWithoutCrashing() {
        Shannon bot = new Shannon(directory.toString());
        assertTrue(bot.getStartupMessage().contains("folder, not a file"));
        assertEquals("Your list is empty!", bot.getResponse("list").text());
    }
}
