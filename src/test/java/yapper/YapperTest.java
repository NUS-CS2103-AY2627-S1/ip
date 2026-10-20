package yapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Exercises the shared command API, persistence boundaries, and GUI lifecycle.
 */
public class YapperTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    public void getResponse_commandsAndReload_preservesSavedState() {
        Path save = temporaryDirectory.resolve("tasks.txt");
        Yapper yapper = new Yapper(save);
        assertTrue(yapper.getWelcome().contains("HELLO THERE"));
        assertTrue(yapper.getResponse("  todo read book  ").contains("[T][ ] read book"));
        assertTrue(yapper.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(yapper.getResponse("find book").contains("1.[T][X] read book"));
        Yapper reloaded = new Yapper(save);
        assertTrue(reloaded.getResponse("list").contains("1.[T][X] read book"));
        assertTrue(reloaded.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(reloaded.getResponse("delete 1").contains("0 task(s)"));
        assertFalse(new Yapper(save).getResponse("list").contains("read book"));
    }

    @Test
    public void getResponse_invalidInputAndBye_keepsStateAndStopsChanges() {
        Yapper yapper = new Yapper(temporaryDirectory.resolve("tasks.txt"));
        assertTrue(yapper.getResponse("todo").contains("cannot be empty"));
        assertFalse(yapper.isExitRequested());
        assertTrue(yapper.getResponse("bye").contains("FAREWELL"));
        assertTrue(yapper.isExitRequested());
        assertTrue(yapper.getResponse("todo ignored").contains("FAREWELL"));
        assertFalse(Files.exists(temporaryDirectory.resolve("tasks.txt")));
    }

    @Test
    public void getResponse_failedLoad_blocksChangesEvenAfterFileRepair() throws IOException {
        Path save = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(save, "broken");
        Yapper yapper = new Yapper(save);
        assertTrue(yapper.getWelcome().contains("could not load"));
        assertTrue(yapper.getResponse("todo ignored").contains("could not load"));
        assertEquals("broken", Files.readString(save));
        Files.writeString(save, "T | 0 | repaired");
        assertTrue(yapper.getResponse("delete 1").contains("could not load"));
        assertEquals("T | 0 | repaired", Files.readString(save));
        assertTrue(yapper.getResponse("bye").contains("FAREWELL"));
    }

    @Test
    public void getResponse_failedSave_restoresAddDeleteAndCompletionChanges() throws IOException {
        Path save = temporaryDirectory.resolve("tasks.txt");
        Yapper yapper = new Yapper(save);
        yapper.getResponse("todo first");
        yapper.getResponse("todo second");
        yapper.getResponse("mark 2");
        String before = yapper.getResponse("list");
        Files.delete(save);
        Files.createDirectory(save);
        Files.writeString(save.resolve("keep.txt"), "prevent replacement");
        for (String command : new String[] {"todo third", "delete 1", "mark 1", "unmark 2"}) {
            assertTrue(yapper.getResponse(command).contains("could not save"), command);
            assertEquals(before, yapper.getResponse("list"), command);
        }
        assertEquals("prevent replacement", Files.readString(save.resolve("keep.txt")));
    }

    @Test
    public void getWelcome_repeatedCalls_doesNotReloadOrMixInstanceResponses() throws IOException {
        Path firstSave = temporaryDirectory.resolve("first.txt");
        Yapper first = new Yapper(firstSave);
        Yapper second = new Yapper(temporaryDirectory.resolve("second.txt"));
        first.getResponse("todo first");
        second.getResponse("todo second");
        Files.writeString(firstSave, "T | 0 | external");
        assertTrue(first.getWelcome().contains("HELLO THERE"));
        assertTrue(first.getResponse("list").contains("first"));
        assertFalse(first.getResponse("list").contains("second"));
        assertFalse(second.getResponse("list").contains("first"));
    }

    @Test
    public void getResponse_statistics_tracksChangesWithoutSaving() {
        Yapper yapper = new Yapper(temporaryDirectory.resolve("tasks.txt"));
        assertTrue(yapper.getResponse("stats").contains(" Total: 0"));
        assertFalse(Files.exists(temporaryDirectory.resolve("tasks.txt")));
        yapper.getResponse("todo first");
        yapper.getResponse("deadline second /by 2026-10-20");
        yapper.getResponse("mark 2");
        String before = yapper.getResponse("list");
        String statistics = yapper.getResponse("stats");
        assertTrue(statistics.contains(" Total: 2"));
        assertTrue(statistics.contains(" Completed: 1"));
        assertTrue(statistics.contains(" Pending: 1"));
        assertEquals(before, yapper.getResponse("list"));
        yapper.getResponse("unmark 2");
        assertTrue(yapper.getResponse("stats").contains(" Completed: 0"));
        yapper.getResponse("delete 1");
        assertTrue(yapper.getResponse("stats").contains(" Pending: 1"));
        assertTrue(yapper.getResponse("help").contains("  stats"));
    }
    @Test
    public void getResponse_multilineDescription_rejectsWithoutCorruptingSave() throws IOException {
        Path save = temporaryDirectory.resolve("tasks.txt");
        Yapper yapper = new Yapper(save);
        yapper.getResponse("todo original");
        String before = Files.readString(save);
        for (String command : new String[] {"todo first\nsecond", "todo first\rsecond",
            "deadline first\nsecond /by 2026-10-20",
            "event first\rsecond /from 2026-10-20 /to 2026-10-21"}) {
            assertTrue(yapper.getResponse(command).contains("must stay on one line"));
            assertEquals(before, Files.readString(save));
        }
        assertTrue(new Yapper(save).getResponse("list").contains("[T][ ] original"));
    }

}
