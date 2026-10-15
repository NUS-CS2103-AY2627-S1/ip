package koko;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests command processing in the Koko chatbot.
 */
public class KokoTest {

    /**
     * Tests that an event update changes only the requested field,
     * retains its completed status, and is saved for the next session.
     *
     * @param tempDir an isolated directory for the test save file
     */
    @Test
    void getResponse_updateEventEndTime_preservesOtherDetailsAndStatus(@TempDir Path tempDir) {
        Storage storage = new Storage(tempDir.resolve("koko.txt"));
        Koko koko = new Koko(storage);
        koko.getResponse("event project meeting /from 2026-09-20 1400 /to 2026-09-20 1600");
        koko.getResponse("mark 1");

        String response = koko.getResponse("update 1 /to 2026-09-20 1800");

        String expectedTask = "[E][X] project meeting (from: Sept 20 2026, 2:00 pm "
                + "to: Sept 20 2026, 6:00 pm)";
        assertEquals("Freshly fluffed! I've updated this task:\n  " + expectedTask, response);
        assertTrue(new Koko(storage).getResponse("list").contains(expectedTask));
    }

    /**
     * Tests that a task rejects fields that do not apply to its type.
     *
     * @param tempDir an isolated directory for the test save file
     */
    @Test
    void getResponse_updateTodoWithDateField_returnsError(@TempDir Path tempDir) {
        Koko koko = new Koko(new Storage(tempDir.resolve("koko.txt")));
        koko.getResponse("todo read book");

        assertEquals("That field cannot be updated for this task.",
                koko.getResponse("update 1 /by 2026-09-20 1800"));
    }
}
