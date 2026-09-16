package aider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command orchestration across parsing, task management, and storage. */
class AiderTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void processCommand_normalizesWhitespaceAndPersistsTask() throws Exception {
        Aider aider = newAider();

        String response = aider.processCommand("  todo   read   book  ");

        assertTrue(response.contains("read book"));
        Aider reloaded = newAider();
        assertTrue(reloaded.processCommand("list").contains("read book"));
    }

    @Test
    void processCommand_rejectsNullAndBlankInput() {
        Aider aider = newAider();

        assertThrows(AiderException.class, () -> aider.processCommand(null));
        assertThrows(AiderException.class, () -> aider.processCommand("   "));
    }

    @Test
    void processCommand_rejectsDuplicateWithoutAddingAnotherTask() throws Exception {
        Aider aider = newAider();
        aider.processCommand("todo read book");

        assertThrows(AiderException.class, () -> aider.processCommand("todo read book"));
        assertEquals(1, countOccurrences(aider.processCommand("list"), "[T][ ] read book"));
    }

    @Test
    void processCommand_handlesSearchScheduleAndDeletion() throws Exception {
        Aider aider = newAider();
        aider.processCommand("todo read book");
        aider.processCommand("deadline submit report /by 2026-09-01");

        assertTrue(aider.processCommand("find REPORT").contains("submit report"));
        assertTrue(aider.processCommand("schedule 2026-09-01").contains("submit report"));
        assertTrue(aider.processCommand("delete 1").contains("read book"));
    }

    private Aider newAider() {
        return new Aider(temporaryDirectory.resolve("tasks.txt").toString());
    }

    private static int countOccurrences(String text, String search) {
        return text.split(java.util.regex.Pattern.quote(search), -1).length - 1;
    }
}
