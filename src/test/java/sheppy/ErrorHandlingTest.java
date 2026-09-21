package sheppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Checks that invalid input and storage failures preserve user data. */
class ErrorHandlingTest {
    @TempDir
    private Path directory;

    /** Exercises malformed commands between valid additions. */
    @Test
    void invalidCommands_leaveTasksUnchanged() {
        Sheppy app = new Sheppy(directory.resolve("tasks.txt").toString());
        app.getResponse("  todo\t read   book  ");
        for (String command : new String[]{"deadline", "event", "todo x | y", "  ",
                "event meeting /from Mon /to Tue /to Wed", "deadline x /by 2026-01-01 /by 2026-02-01"}) {
            assertTrue(app.getResponse(command).startsWith("Baa-error:"), command);
        }
        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                + "1.[T][ ] read book", app.getResponse(" list "));
        assertTrue(app.isExitCommand(" bye "));
    }

    /** Prevents a corrupt file being overwritten after a loading error. */
    @Test
    void corruptStorage_blocksChanges() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "broken data");
        Sheppy app = new Sheppy(file.toString());
        assertTrue(app.getStartupMessage().startsWith("Baa-error:"));
        assertTrue(app.getResponse("todo replacement").contains("repair the data file"));
        assertEquals("broken data", Files.readString(file));
    }

    /** Simulates a failed save without relying on operating-system permissions. */
    @Test
    void failedSave_restoresOrderStatusAndMembership() throws IOException {
        Path parent = directory.resolve("data");
        Path file = parent.resolve("tasks.txt");
        Sheppy app = new Sheppy(file.toString());
        app.getResponse("todo zebra");
        app.getResponse("todo apple");
        String original = app.getResponse("list");
        Files.delete(file);
        Files.delete(parent);
        Files.writeString(parent, "blocks directory creation");
        for (String command : new String[]{"mark 1", "delete 1", "sort", "todo extra"}) {
            assertTrue(app.getResponse(command).contains("couldn't save"));
            assertEquals(original, app.getResponse("list"), command);
        }
    }
}
