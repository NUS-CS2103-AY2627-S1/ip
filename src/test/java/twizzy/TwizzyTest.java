package twizzy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests Twizzy's GUI-facing behavior. */
class TwizzyTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_unreadableDataBlocksChangesAndPreservesFile() throws IOException {
        Path dataFile = temporaryDirectory.resolve("twizzy.txt");
        String invalidData = "T | invalid-status | task";
        Files.writeString(dataFile, invalidData);
        Twizzy twizzy = new Twizzy(new Storage(dataFile));

        twizzy.initializeForGui();
        String response = twizzy.getResponse("todo protect saved data");

        assertNotNull(twizzy.getGuiStartupError());
        assertTrue(response.contains("changes are locked to protect it"));
        assertEquals(invalidData, Files.readString(dataFile));
    }

    @Test
    void getResponse_taskSummaryReflectsCompletedAndSnoozedTasks() {
        Twizzy twizzy = new Twizzy(new Storage(temporaryDirectory.resolve("twizzy.txt")));
        twizzy.initializeForGui();

        assertEquals("Tasks: 0 total · 0 done · 0 snoozed", twizzy.getTaskSummary());
        twizzy.getResponse("todo revise notes");
        twizzy.getResponse("mark 1");
        twizzy.getResponse("snooze 1 /until 2099-12-31");

        assertEquals("Tasks: 1 total · 1 done · 1 snoozed", twizzy.getTaskSummary());
    }

    @Test
    void getResponse_emptyFindAndRepeatedSnoozeExplainWhatWentWrong() {
        Twizzy twizzy = new Twizzy(new Storage(temporaryDirectory.resolve("twizzy.txt")));
        twizzy.initializeForGui();

        assertTrue(twizzy.getResponse("find missing").contains("No tasks matched that"));
        twizzy.getResponse("todo revise notes");
        twizzy.getResponse("snooze 1 /until 2099-12-31");

        assertTrue(twizzy.getResponse("snooze 1 /until 2099-12-31")
                .contains("There are no tasks to snooze"));
    }

    @Test
    void getResponse_unsnoozeReturnsTaskToActiveListAndSavesIt() {
        Path dataFile = temporaryDirectory.resolve("twizzy.txt");
        Twizzy twizzy = new Twizzy(new Storage(dataFile));
        twizzy.initializeForGui();
        twizzy.getResponse("todo revise notes");
        twizzy.getResponse("snooze 1 /until 2099-12-31");

        String response = twizzy.getResponse("unsnooze 1");
        Twizzy reloadedTwizzy = new Twizzy(new Storage(dataFile));
        reloadedTwizzy.initializeForGui();

        assertTrue(response.startsWith("Unsnoozed, twin:"));
        assertTrue(reloadedTwizzy.getResponse("list").contains("revise notes"));
        assertTrue(reloadedTwizzy.getResponse("list snoozed").contains("No snoozed tasks"));
    }
}
