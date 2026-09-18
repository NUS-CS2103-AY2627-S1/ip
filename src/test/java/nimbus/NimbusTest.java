package nimbus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class NimbusTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_updateCommand_updatesExistingTask() {
        Nimbus nimbus = new Nimbus(temporaryDirectory.resolve("tasks.txt"));
        nimbus.getResponse("todo old description");

        String response = nimbus.getResponse("update 1 new description");

        assertTrue(response.contains("[T][ ] new description"));
        assertTrue(nimbus.getResponse("list").contains("[T][ ] new description"));
    }

    @Test
    void getResponse_deadlineWithoutDescription_returnsUsageError() {
        Nimbus nimbus = new Nimbus(temporaryDirectory.resolve("tasks.txt"));

        String response = nimbus.getResponse("deadline /by 2026-01-01");

        assertEquals("Your decree is rejected: Use: deadline DESCRIPTION /by YYYY-MM-DD.", response);
    }

    @Test
    void getResponse_eventWithoutDescription_returnsUsageError() {
        Nimbus nimbus = new Nimbus(temporaryDirectory.resolve("tasks.txt"));

        String response = nimbus.getResponse("event /from Monday /to Tuesday");

        assertEquals("Your decree is rejected: Use: event DESCRIPTION /from START /to END.", response);
    }

    @Test
    void getResponseWithStatus_byeWithTrailingText_marksSessionForExit() {
        Nimbus nimbus = new Nimbus(temporaryDirectory.resolve("tasks.txt"));

        Nimbus.Response response = nimbus.getResponseWithStatus("bye now");

        assertTrue(response.isExit());
        assertEquals("You are dismissed, land dweller. Return when you require my greatness!", response.message());
    }

    @Test
    void getResponseWithStatus_regularCommand_keepsSessionOpen() {
        Nimbus nimbus = new Nimbus(temporaryDirectory.resolve("tasks.txt"));

        Nimbus.Response response = nimbus.getResponseWithStatus("todo read book");

        assertFalse(response.isExit());
    }

    @Test
    void getWelcomeMessage_storageLoadFails_includesWarning() throws IOException {
        Path blockingFile = temporaryDirectory.resolve("blocking-file");
        Files.writeString(blockingFile, "not a directory");

        Nimbus nimbus = new Nimbus(blockingFile.resolve("tasks.txt"));

        assertTrue(nimbus.getWelcomeMessage().contains("couldn't load your saved tasks"));
    }

    @Test
    void getWelcomeMessage_damagedRecord_skipsRecordAndIncludesWarning() throws IOException {
        Path storageFile = temporaryDirectory.resolve("tasks.txt");
        Nimbus firstSession = new Nimbus(storageFile);
        firstSession.getResponse("todo keep this task");
        Files.writeString(storageFile, "damaged record", StandardOpenOption.APPEND);

        Nimbus recoveredSession = new Nimbus(storageFile);

        assertTrue(recoveredSession.getWelcomeMessage().contains("skipped 1 damaged saved record"));
        assertTrue(recoveredSession.getResponse("list").contains("keep this task"));
    }

    @Test
    void getResponse_blankInput_returnsHelpfulError() {
        Nimbus nimbus = new Nimbus(temporaryDirectory.resolve("tasks.txt"));

        assertEquals("Speak, land dweller. Please type a command.", nimbus.getResponse("   "));
    }

    @Test
    void getResponse_taskLifecycle_updatesListAndPersistsChanges() {
        Path storageFile = temporaryDirectory.resolve("tasks.txt");
        Nimbus nimbus = new Nimbus(storageFile);

        nimbus.getResponse("todo read book");
        nimbus.getResponse("deadline submit report /by 2026-09-18");
        assertTrue(nimbus.getResponse("mark 2").contains("[D][X] submit report"));
        assertTrue(nimbus.getResponse("unmark 2").contains("[D][ ] submit report"));
        assertTrue(nimbus.getResponse("delete 1").contains("read book"));

        Nimbus nextSession = new Nimbus(storageFile);
        String list = nextSession.getResponse("list");
        assertTrue(list.contains("[D][ ] submit report"));
        assertFalse(list.contains("read book"));
    }

    @Test
    void getResponse_findCommand_returnsOnlyMatchingTasks() {
        Nimbus nimbus = new Nimbus(temporaryDirectory.resolve("tasks.txt"));
        nimbus.getResponse("todo Read book");
        nimbus.getResponse("todo buy milk");

        String response = nimbus.getResponse("find BOOK");

        assertTrue(response.contains("Read book"));
        assertFalse(response.contains("buy milk"));
    }

    @Test
    void getResponse_invalidDeadlineDate_returnsFormatError() {
        Nimbus nimbus = new Nimbus(temporaryDirectory.resolve("tasks.txt"));

        String response = nimbus.getResponse("deadline submit report /by next Friday");

        assertEquals("Your decree is rejected: Use a deadline date in YYYY-MM-DD format.", response);
    }

    @Test
    void getResponse_outOfRangeTaskNumber_returnsSelectionError() {
        Nimbus nimbus = new Nimbus(temporaryDirectory.resolve("tasks.txt"));
        nimbus.getResponse("todo only task");

        String response = nimbus.getResponse("mark 2");

        assertEquals("Your decree is rejected: Choose a task number from the list.", response);
    }

    @Test
    void getResponse_unknownCommand_returnsHintError() {
        Nimbus nimbus = new Nimbus(temporaryDirectory.resolve("tasks.txt"));

        String response = nimbus.getResponse("dance now");

        assertTrue(response.contains("Even I cannot command that"));
    }
}
