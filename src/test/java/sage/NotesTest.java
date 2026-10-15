package sage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NotesTest {
    @TempDir
    Path tempDir;

    @Test
    void note_addListFindDelete_persistsEachChange() {
        Path file = tempDir.resolve("notes.txt");
        Sage sage = new Sage(file.toString());
        sage.getResponse("todo buy tickets");
        assertEquals("Saved for later:" + System.lineSeparator() + "  [N] Waist: 32 inches",
                sage.getResponse("note Waist: 32 inches"));
        sage.getResponse("note Movie: Spirited Away");

        Sage reloaded = new Sage(file.toString());
        assertEquals(String.join(System.lineSeparator(), "Here's your list:",
                "1. [T][ ] buy tickets", "2. [N] Waist: 32 inches", "3. [N] Movie: Spirited Away"),
                reloaded.getResponse("list"));
        assertEquals(String.join(System.lineSeparator(), "Here's what I found:",
                "2. [N] Waist: 32 inches"), reloaded.getResponse("find WAIST"));
        assertEquals(String.join(System.lineSeparator(), "Note cleared:",
                "  [N] Waist: 32 inches"), reloaded.getResponse("delete 2"));

        String finalList = new Sage(file.toString()).getResponse("list");
        assertFalse(finalList.contains("Waist"));
        assertTrue(finalList.contains("1. [T][ ] buy tickets"));
        assertTrue(finalList.contains("2. [N] Movie: Spirited Away"));
    }

    @Test
    void note_specialCharacters_survivesReloadWithoutTruncation() {
        Path file = tempDir.resolve("special.txt");
        Sage sage = new Sage(file.toString());
        String text = "电影: 千と千尋 | waist: 32 | /by Sunday | path: C:\\notes";
        sage.getResponse("note " + text);

        assertEquals(String.join(System.lineSeparator(), "Here's your list:", "1. [N] " + text),
                new Sage(file.toString()).getResponse("list"));
    }

    @Test
    void note_invalidInput_doesNotCreateEntries() {
        Sage sage = new Sage(tempDir.resolve("invalid.txt").toString());
        for (String input : List.of("note", "note   ", "note \t", "notebook", "note first\nsecond")) {
            assertTrue(sage.getResponse(input).startsWith("Let's try that again. "), input);
        }
        assertEquals("A clear page. Add a task with todo <task> or a note with note <text>.", sage.getResponse("list"));
    }

    @Test
    void note_markAndUnmark_areRejectedWithoutAffectingOtherTasks() {
        Path file = tempDir.resolve("mark.txt");
        Sage sage = new Sage(file.toString());
        sage.getResponse("note Waist: 32 inches");
        sage.getResponse("todo buy jeans");
        for (String input : List.of("mark 1", "unmark 1")) {
            assertEquals("Let's try that again. Notes cannot be marked or unmarked. Use delete to remove a note.",
                    sage.getResponse(input));
        }
        sage.getResponse("mark 2");
        assertEquals(String.join(System.lineSeparator(), "Here's your list:",
                "1. [N] Waist: 32 inches", "2. [T][X] buy jeans"), new Sage(file.toString()).getResponse("list"));
    }

    @Test
    void note_duplicateText_returnsErrorWithoutAddingOrLosingData() {
        Path file = tempDir.resolve("duplicates.txt");
        Sage sage = new Sage(file.toString());
        sage.getResponse("note Waist: 32 inches");
        sage.getResponse("note waist:   32 INCHES");
        assertTrue(sage.hasError());
        assertEquals(String.join(System.lineSeparator(), "Here's your list:",
                "1. [N] Waist: 32 inches"), new Sage(file.toString()).getResponse("list"));
    }
}
