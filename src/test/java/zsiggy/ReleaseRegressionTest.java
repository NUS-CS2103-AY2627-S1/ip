package zsiggy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Regression checks for the failures reported in forum issue 443.
 */
public class ReleaseRegressionTest {
    @TempDir
    private Path directory;

    private Duke newSession() {
        Duke duke = new Duke(directory.resolve("data/tasks.txt").toString());
        duke.loadTasks();
        return duke;
    }

    @Test
    public void getResponse_badEventArguments_rejectsWithoutMutation() {
        Duke duke = newSession();
        String[] invalid = {
            "event sleep /to 2004-01-01 /from 2004-01-01",
            "event sleep /from 2026-09-20 /to",
            "event sleep /from 2026-09-20 /to 2026-09-19",
            "event sleep /from 2026-02-30 /to 2026-03-01",
            "event sleep /from 2026-09-20 /from 2026-09-21 /to 2026-09-22"
        };
        for (String command : invalid) {
            assertTrue(duke.getResponse(command).startsWith("Oi."), command);
        }
        assertFalse(duke.getResponse("list").contains("[E]"));
    }

    @Test
    public void getResponse_pipesAndLineBreaks_rejectsBeforeSaving() {
        Duke duke = newSession();
        for (String command : new String[]{"todo read | book", "deadline read | book /by 2026-09-20",
            "event trailing | /from 2026-09-20 /to 2026-09-21", "todo one\nT | 0 | injected"}) {
            assertTrue(duke.getResponse(command).startsWith("Oi."));
        }
        assertEquals(duke.getResponse("list"), newSession().getResponse("list"));
    }

    @Test
    public void getResponse_whitespaceAndDuplicates_normalizesAndRejectsDuplicates() {
        Duke duke = newSession();
        assertTrue(duke.getResponse("  t\t read   book  ").contains("[T][ ] read book"));
        assertTrue(duke.getResponse("mark  1").contains("[X] read book"));
        assertTrue(duke.getResponse("todo read book").contains("already on your list"));
        String event = "event tutorial /from 2026-09-20 /to 2026-09-20";
        assertFalse(duke.getResponse(event).startsWith("Oi."));
        assertTrue(duke.getResponse(event).contains("already on your list"));
        assertEquals(duke.getResponse("list"), newSession().getResponse("list"));
    }

    @Test
    public void find_matchingTask_keepsFullListIndex() {
        Duke duke = newSession();
        duke.getResponse("todo first");
        duke.getResponse("todo book");
        assertTrue(duke.getResponse("find book").contains("2. [T][ ] book"));
        assertTrue(duke.getResponse("mark 2").contains("[X] book"));
        assertTrue(duke.getResponse("list").contains("1. [T][ ] first"));
    }

    @Test
    public void getResponse_moreThanOneHundredTasks_survivesReload() {
        Duke duke = newSession();
        for (int i = 1; i <= 105; i++) {
            assertFalse(duke.getResponse("todo task " + i).startsWith("Oi."));
        }
        assertTrue(newSession().getResponse("list").contains("105. [T][ ] task 105"));
    }

    @Test
    public void load_invalidRecords_preservesFileAndWarns() throws IOException {
        Path file = directory.resolve("data/tasks.txt");
        Files.createDirectories(file.getParent());
        for (String record : new String[]{"T", "D | 0 | bad | 2026-02-30", "T | 5 | bad",
            "T | 0 | read | book", "E | 0 | trailing |  | 2026-09-20 | 2026-09-21"}) {
            Files.writeString(file, record);
            Duke duke = newSession();
            assertTrue(duke.getStartupWarning().contains("line 1"));
            assertTrue(duke.getResponse("todo new task").contains("Couldn't save"));
            assertEquals(record, Files.readString(file));
            assertFalse(duke.getResponse("list").contains("new task"));
        }
    }

    @Test
    public void save_failedWrite_rollsBackAllMutations() throws IOException {
        Duke duke = newSession();
        duke.getResponse("todo original");
        Path file = directory.resolve("data/tasks.txt");
        Files.delete(file);
        Files.createDirectory(file);
        Files.writeString(file.resolve("blocker"), "keep");
        String original = duke.getResponse("list");
        for (String command : new String[]{"mark 1", "delete 1", "todo added"}) {
            assertTrue(duke.getResponse(command).contains("Couldn't save"));
            assertEquals(original, duke.getResponse("list"));
        }
    }

    @Test
    public void load_missingFileAndUnicode_roundTrips() throws IOException {
        Duke duke = newSession();
        assertEquals("", duke.getStartupWarning());
        duke.getResponse("todo 中文 café ☕");
        duke.getResponse("deadline assignment /by 2028-02-29");
        duke.getResponse("event holiday /from 2026-09-20 /to 2026-09-22");
        duke.getResponse("mark 2");
        assertEquals(duke.getResponse("list"), newSession().getResponse("list"));
        assertThrows(IOException.class, () -> new Storage(directory.toString()).load());
    }
}
