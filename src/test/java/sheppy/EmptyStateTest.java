package sheppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Checks empty-state guidance without masking storage failures or changing data. */
class EmptyStateTest {
    @TempDir
    private Path directory;

    @Test
    void emptyListAndSort_giveGuidanceWithoutCreatingFile() {
        Path file = directory.resolve("tasks.txt");
        Sheppy app = new Sheppy(file.toString());
        assertTrue(app.getResponse("list").contains("Your meadow is empty!"));
        assertTrue(app.getResponse("list").contains("todo, deadline, or event"));
        assertTrue(app.getResponse("sort").startsWith("Nothing to sort yet."));
        assertFalse(Files.exists(file));
        assertEquals("No matching tasks found. Try another keyword!", app.getResponse("find book"));
    }

    @Test
    void deleteLastTaskAndRestart_preserveEmptyState() {
        Path file = directory.resolve("tasks.txt");
        Sheppy app = new Sheppy(file.toString());
        app.getResponse("todo book");
        assertFalse(app.getResponse("list").contains("Your meadow is empty!"));
        assertTrue(app.getResponse("delete 1").contains("0 tasks"));
        String emptyList = app.getResponse("list");
        assertTrue(emptyList.contains("Your meadow is empty!"));
        Sheppy restarted = new Sheppy(file.toString());
        assertEquals(emptyList, restarted.getResponse("list"));
        assertTrue(restarted.getResponse("sort").startsWith("Nothing to sort yet."));
        assertTrue(restarted.getResponse("mark 1").startsWith("Baa-error:"));
        assertTrue(restarted.getResponse("unmark 1").startsWith("Baa-error:"));
        assertTrue(restarted.getResponse("delete 1").startsWith("Baa-error:"));
        restarted.getResponse("todo new task");
        assertTrue(restarted.getResponse("list").contains("1.[T][ ] new task"));
    }

    @Test
    void corruptStorage_reportsUnavailableRatherThanEmpty() throws IOException {
        Path file = directory.resolve("tasks.txt");
        Files.writeString(file, "broken record\n");
        Sheppy app = new Sheppy(file.toString());
        for (String command : new String[] {"list", "find book"}) {
            String response = app.getResponse(command);
            assertTrue(response.startsWith("Baa-error: your tasks are unavailable"));
            assertFalse(response.contains("Your meadow is empty!"));
            assertFalse(response.contains("No matching tasks"));
        }
        assertTrue(app.getResponse("sort").startsWith("Baa-error:"));
        assertEquals("broken record\n", Files.readString(file));
    }
}
