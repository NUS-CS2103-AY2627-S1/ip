package am;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import am.storage.Storage;
import am.task.TaskList;

/** Exercises edits through the same response entry point used by the GUI. */
public class AmEditTest {
    private static final String ORIGINAL = "T | 0 | buy bread\n"
            + "D | 0 | submit report | 2026-09-11T23:59\n"
            + "E | 1 | project meeting | 2026-09-10T14:00 | 2026-09-10T16:00\n";
    @TempDir
    Path directory;
    private Path file;
    private Am am;
    private Locale previousLocale;

    @BeforeEach
    void setUp() throws Exception {
        previousLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
        file = directory.resolve("AM.txt");
        Files.writeString(file, ORIGINAL);
        am = new Am(new Storage(file.toString()));
    }

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(previousLocale);
    }

    @Test
    void editsOnlyEndTimeAndPreservesStatusAndOrderAfterRestart() {
        assertEquals("Edited:\n3. [E][X] project meeting (from: Sep 10 2026 2:00 PM"
                + " to: Sep 10 2026 6:00 PM)", am.getResponse("edit 3 /to 1800"));
        Am restarted = new Am(new Storage(file.toString()));
        assertEquals(am.getResponse("list"), restarted.getResponse("list"));
        assertTrue(restarted.getResponse("list").startsWith("1. [T][ ] buy bread\n2. [D][ ] submit report"));
    }

    @Test
    void editsNamesAndUsesDateOnlyDefaults() {
        assertEquals("Edited:\n1. [T][ ] buy  milk", am.getResponse("edit 1 /name buy  milk"));
        assertEquals("Edited:\n2. [D][ ] submit report (by: Sep 12 2026 11:59 PM)",
                am.getResponse("edit 2 /by 2026-09-12"));
        assertEquals("Edited:\n3. [E][X] project meeting (from: Sep 10 2026 12:00 AM"
                + " to: Sep 10 2026 11:59 PM)",
                am.getResponse("edit 3 /from 2026-09-10 /to 2026-09-10"));
    }

    @Test
    void validatesFinalIntervalAndAllowsEqualEndpointsAndPastDates() {
        assertTrue(am.getResponse("edit 3 /to 2000-01-01 1500 /name old /from 2000-01-01 1500")
                .startsWith("Edited:\n3. [E][X] old"));
        assertTrue(am.getResponse("past").contains("3. [E][X] old"));
        assertTrue(am.getResponse("find old").startsWith("3."));
        assertTrue(am.getResponse("edit 3 /name older").contains("3. [E][X] older"));
    }

    @Test
    void rejectsInvalidEditsWithoutChangingMemoryOrDisk() throws Exception {
        String before = am.getResponse("list");
        String syntaxMessage = "You messed up the command.\nExample: edit 1 /name buy milk";
        String[] malformed = {"edit", "edit 3", "edit abc /name x", "edit 3 /name",
            "edit 3 /to 1700 /to 1800", "edit 3 /until 1800", "edit 1 /by 2026-09-12",
            "edit 3 /by 2026-09-12", "edit 3 /type todo", "edit 3 /name a|b",
            "edit 3 /name /tmp", "edit 3 stray /name x", "edit 2147483648 /name x"};
        for (String input : malformed) {
            assertEquals(syntaxMessage, am.getResponse(input), input);
            assertTrue(am.isResponseError());
        }
        for (String value : new String[]{"2400", "2026-02-30", "1300", "900"}) {
            assertEquals("You messed up the command.\n"
                    + "Example: edit 1 /from 2026-09-12 1400 /to 2026-09-12 1600",
                    am.getResponse("edit 3 /to " + value), value);
        }
        for (int number : new int[]{0, -1, Integer.MIN_VALUE}) {
            assertEquals(syntaxMessage,
                    am.getResponse("edit " + number + " /name x"));
        }
        for (int number : new int[]{Integer.MAX_VALUE, 4}) {
            assertEquals("Task " + number + " does not exist. Choose a task number from 1 to 3."
                    + " Use list to see your tasks.", am.getResponse("edit " + number + " /name x"));
        }
        assertEquals(before, am.getResponse("list"));
        assertEquals(ORIGINAL, Files.readString(file));
    }

    @Test
    void failureBeforeWritingPreservesStateAndNoOpSkipsSaving() throws Exception {
        Storage failing = new Storage(file.toString()) {
            @Override
            public void save(TaskList tasks) throws IOException {
                throw new IOException("Simulated write failure");
            }
        };
        Am chatbot = new Am(failing);
        String before = chatbot.getResponse("list");
        assertEquals("No changes.", chatbot.getResponse("edit 3 /to 1600"));
        assertEquals("I couldn't access my memory.", chatbot.getResponse("edit 3 /to 1800"));
        assertEquals(before, chatbot.getResponse("list"));
        assertEquals(ORIGINAL, Files.readString(file));
    }

    @Test
    void editsDeadlineTimeAndAllowsDuplicateDescriptions() {
        assertTrue(am.getResponse("edit 2 /name buy bread /by 0900").contains("Sep 11 2026 9:00 AM"));
        assertTrue(am.getResponse("edit 3 /from 1300").contains("1:00 PM"));
        assertTrue(am.getResponse("edit 2 /by 2026-09-12 1030").contains("Sep 12 2026 10:30 AM"));
    }

    @Test
    void replacesTheWholeSavedListWithoutAppendingOldContents() throws Exception {
        assertEquals("Edited:\n1. [T][ ] x", am.getResponse("edit 1 /name x"));
        String expected = "T | 0 | x\n"
                + "D | 0 | submit report | 2026-09-11T23:59\n"
                + "E | 1 | project meeting | 2026-09-10T14:00 | 2026-09-10T16:00\n";
        assertEquals(expected.replace("\n", System.lineSeparator()), Files.readString(file));
        assertEquals(am.getResponse("list"), new Am(new Storage(file.toString())).getResponse("list"));
    }

    @Test
    void writeFailureAfterTruncationStillPreservesLiveTasks() throws Exception {
        Storage failing = new Storage(file.toString()) {
            @Override
            public void save(TaskList tasks) throws IOException {
                Files.writeString(file, "");
                throw new IOException("Simulated failure after truncation");
            }
        };
        Am chatbot = new Am(failing);
        String before = chatbot.getResponse("list");
        assertEquals("I couldn't access my memory.", chatbot.getResponse("edit 1 /name changed"));
        assertEquals(before, chatbot.getResponse("list"));
        // The existing save operation does not promise to restore a partially written file.
        assertEquals("", Files.readString(file));
    }

    @Test
    void retainsEndpointDateAndUntouchedSeconds() throws Exception {
        Files.writeString(file, "E | 0 | overnight | 2026-09-10T23:00:30 | 2026-09-11T02:00:45\n");
        assertTrue(am.getResponse("edit 1 /to 0100").startsWith("Edited:"));
        assertTrue(Files.readString(file).contains("2026-09-10T23:00:30 | 2026-09-11T01:00"));
    }
}
