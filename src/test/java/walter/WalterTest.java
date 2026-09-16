package walter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import walter.command.CommandCategory;
import walter.storage.Storage;

/**
 * Tests the presentation-neutral command API used by Walter's GUI.
 */
public class WalterTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getWelcomeMessage_defaultMessage_breakingBadThemeGreetingReturned() {
        Walter walter = createWalter();

        assertEquals("Jesse, focus. We’ve got things to cook.\n"
                + "Tell me what needs to be done, and I’ll handle the list.\n"
                + "Start with `list`, `todo <task>`, or "
                + "`event <task> /from <start> /to <end>`.", walter.getWelcomeMessage());
    }

    @Test
    public void getResponse_statefulCommands_responsesAndStatePreserved() {
        Walter walter = createWalter();

        assertTrue(walter.getResponse("todo read book").contains("[T][ ] read book"));
        assertEquals("Here is the current operation:\n1. [T][ ] read book",
                walter.getResponse("list"));
        assertTrue(walter.getResponse("mark 1").contains("[T][X] read book"));
    }

    @Test
    public void getResponse_invalidCommand_errorReturned() {
        Walter walter = createWalter();

        assertEquals("Precision matters, Jesse. Unknown command.",
                walter.getResponse("archive book"));
    }

    @Test
    public void getResponse_knownErrorTypes_personalityPreservesSpecificDiagnostics() {
        Walter walter = createWalter();

        assertEquals("Precision matters, Jesse. Todo description cannot be empty.",
                walter.getResponse("todo"));
        assertEquals("Precision matters, Jesse. Task number is out of range.",
                walter.getResponse("mark 1"));
        assertEquals("Precision matters, Jesse. Deadline date must be in yyyy-MM-dd format.",
                walter.getResponse("deadline report /by tomorrow"));
        assertEquals("Precision matters, Jesse. Keyword is required for the find command.",
                walter.getResponse("find"));
    }

    @Test
    public void getResponse_commandCategory_matchesCommandType() {
        Walter walter = createWalter();

        walter.getResponse("todo read book");
        assertEquals(CommandCategory.ADD, walter.getLastCommandCategory());

        walter.getResponse("deadline submit report /by 2026-09-04");
        assertEquals(CommandCategory.ADD, walter.getLastCommandCategory());

        walter.getResponse("event project meeting /at 3pm");
        assertEquals(CommandCategory.ADD, walter.getLastCommandCategory());

        walter.getResponse("mark 1");
        assertEquals(CommandCategory.STATE_CHANGE, walter.getLastCommandCategory());

        walter.getResponse("unmark 1");
        assertEquals(CommandCategory.STATE_CHANGE, walter.getLastCommandCategory());

        walter.getResponse("delete 1");
        assertEquals(CommandCategory.DELETE, walter.getLastCommandCategory());

        walter.getResponse("list");
        assertEquals(CommandCategory.NORMAL, walter.getLastCommandCategory());
    }

    @Test
    public void getResponse_invalidCommand_setsErrorCategory() {
        Walter walter = createWalter();
        walter.getResponse("todo read book");

        assertEquals("Precision matters, Jesse. Unknown command.",
                walter.getResponse("archive book"));
        assertEquals(CommandCategory.ERROR, walter.getLastCommandCategory());
    }

    @Test
    public void getResponse_byeCommand_farewellReturnedWithoutCliSeparator() {
        Walter walter = createWalter();

        assertEquals("All right. The operation is closed. Stay focused, Jesse.",
                walter.getResponse("bye"));
        assertTrue(walter.wasLastCommandExit());
    }

    @Test
    public void wasLastCommandExit_commandAfterBye_exitStateReset() {
        Walter walter = createWalter();
        walter.getResponse("bye");

        walter.getResponse("list");

        assertFalse(walter.wasLastCommandExit());
    }

    @Test
    public void getResponse_persistedTask_loadedByNewInstance() {
        Storage storage = new Storage(temporaryDirectory.resolve("walter.txt"));
        Walter firstWalter = new Walter(storage);
        firstWalter.getResponse("deadline submit report /by 2026-09-04");

        Walter secondWalter = new Walter(storage);

        assertTrue(secondWalter.getResponse("list").contains("submit report (by: Sep 4 2026)"));
    }

    @Test
    public void constructor_corruptedTaskFile_warningShownAndEmptyListUsed() throws IOException {
        Path saveFile = temporaryDirectory.resolve("walter.txt");
        Files.writeString(saveFile, "corrupted record", StandardCharsets.UTF_8);

        Walter walter = new Walter(new Storage(saveFile));

        assertTrue(walter.getWelcomeMessage().contains(
                "Malformed saved task record. Starting with an empty list."));
        assertEquals("The board is clean. Nothing needs doing.", walter.getResponse("list"));
    }

    @Test
    public void getResponse_placeLifecycle_addListDeleteAndPersist() {
        Storage storage = new Storage(temporaryDirectory.resolve("walter.txt"));
        Walter firstWalter = new Walter(storage);

        assertTrue(firstWalter.getResponse("place Alex's home /at 123 Clementi Ave 3")
                .contains("Alex's home — 123 Clementi Ave 3"));
        assertTrue(firstWalter.getResponse("place NUS Library /at 12 Computing Drive")
                .contains("The location list now contains 2 places."));
        assertEquals("Here are the recorded locations:\n"
                + "1. Alex's home — 123 Clementi Ave 3\n"
                + "2. NUS Library — 12 Computing Drive", firstWalter.getResponse("places"));

        assertTrue(firstWalter.getResponse("deleteplace 1")
                .contains("Alex's home — 123 Clementi Ave 3"));
        assertEquals("Here are the recorded locations:\n"
                + "1. NUS Library — 12 Computing Drive", firstWalter.getResponse("places"));

        Walter restartedWalter = new Walter(storage);
        assertEquals("Here are the recorded locations:\n"
                + "1. NUS Library — 12 Computing Drive", restartedWalter.getResponse("places"));
    }

    @Test
    public void getResponse_invalidPlaceCommands_errorsReturnedWithoutChangingPlaces() {
        Walter walter = createWalter();

        assertEquals("Precision matters, Jesse. Place name cannot be empty.",
                walter.getResponse("place"));
        assertEquals("Precision matters, Jesse. Place requires exactly one /at.",
                walter.getResponse("place Alex's home"));
        assertEquals("Precision matters, Jesse. Place name cannot be empty.",
                walter.getResponse("place /at 123 Clementi Ave 3"));
        assertEquals("Precision matters, Jesse. Place address cannot be empty.",
                walter.getResponse("place Alex's home /at"));
        assertEquals("Precision matters, Jesse. Place number must be an integer.",
                walter.getResponse("deleteplace abc"));
        assertEquals("Precision matters, Jesse. Place number is out of range.",
                walter.getResponse("deleteplace 999"));
        assertEquals("The location list is empty.",
                walter.getResponse("places"));
    }

    @Test
    public void getResponse_boundaryTaskIndexes_errorsReturnedWithoutChangingTasks() {
        Walter walter = createWalter();
        walter.getResponse("todo first");
        walter.getResponse("todo second");

        String[] boundaryCommands = {
            "mark 0", "done -1", "unmark -999", "delete 999999",
            "mark 2147483647", "delete -2147483648",
            "mark 2147483648", "delete -2147483649",
            "mark 1.0", "done 1e3", "unmark NaN", "delete abc"
        };
        for (String command : boundaryCommands) {
            assertTrue(walter.getResponse(command).startsWith("Precision matters, Jesse."));
        }

        assertEquals("Here is the current operation:\n"
                + "1. [T][ ] first\n"
                + "2. [T][ ] second", walter.getResponse("list"));
    }

    @Test
    public void getResponse_taskAndPlaceState_coexistsAcrossRestart() {
        Storage storage = new Storage(temporaryDirectory.resolve("data").resolve("walter.txt"));
        Walter firstWalter = new Walter(storage);
        firstWalter.getResponse("todo read book");
        firstWalter.getResponse("deadline submit report /by 2026-09-04");
        firstWalter.getResponse("event consultation /at Monday 2pm");
        firstWalter.getResponse("event lab /from Tuesday 10am /to 12pm");
        firstWalter.getResponse("mark 2");
        firstWalter.getResponse("place library /at 12 Computing Drive");

        Walter restartedWalter = new Walter(storage);

        assertEquals(firstWalter.getResponse("list"), restartedWalter.getResponse("list"));
        assertEquals(firstWalter.getResponse("places"), restartedWalter.getResponse("places"));
    }

    @Test
    public void getResponse_corruptedPlaceFile_errorReturnedWithoutCrashingSession()
            throws IOException {
        Path saveFile = temporaryDirectory.resolve("data").resolve("walter.txt");
        Path placeFile = saveFile.resolveSibling("places.txt");
        Files.createDirectories(placeFile.getParent());
        Files.writeString(placeFile, "P\tname\tdangling\\", StandardCharsets.UTF_8);
        Walter walter = new Walter(new Storage(saveFile));

        assertEquals("Precision matters, Jesse. Malformed saved place text.",
                walter.getResponse("places"));
        assertEquals("The board is clean. Nothing needs doing.", walter.getResponse("list"));
    }

    @Test
    public void getResponse_repeatedStateTransitions_errorsAndPersistenceRemainCorrect() {
        Storage storage = new Storage(temporaryDirectory.resolve("walter.txt"));
        Walter walter = new Walter(storage);
        walter.getResponse("todo test");

        assertEquals("Precision matters, Jesse. Task is already not done.",
                walter.getResponse("unmark 1"));
        assertTrue(walter.getResponse("mark 1").contains("[T][X] test"));
        assertEquals("Precision matters, Jesse. Task is already marked as done.",
                walter.getResponse("mark 1"));
        assertEquals("Precision matters, Jesse. Task is already marked as done.",
                walter.getResponse("done 1"));

        Walter restartedWalter = new Walter(storage);
        assertEquals("Here is the current operation:\n1. [T][X] test",
                restartedWalter.getResponse("list"));
    }

    @Test
    public void getResponse_capacityBoundary_persistsAndRecoversAfterDeletion()
            throws DukeException {
        Storage storage = new Storage(temporaryDirectory.resolve("capacity").resolve("walter.txt"));
        Walter walter = new Walter(storage);
        for (int i = 1; i <= 100; i++) {
            assertTrue(walter.getResponse("todo capacity-" + i)
                    .contains("The list now contains " + i + " task"));
        }

        assertEquals("Precision matters, Jesse. Task list is full.",
                walter.getResponse("todo rejected-overflow"));
        assertEquals(100, storage.load().size());

        Walter restartedWalter = new Walter(storage);
        assertTrue(restartedWalter.getResponse("delete 50").contains("capacity-50"));
        assertTrue(restartedWalter.getResponse("todo replacement").contains("100 tasks"));
        assertEquals(100, storage.load().size());
        assertEquals("replacement", storage.load().get(99).getDescription());
    }

    @Test
    public void getResponse_multipleMutationRestartCycles_exactStateRestored() {
        Storage storage = new Storage(temporaryDirectory.resolve("cycles").resolve("walter.txt"));
        Walter first = new Walter(storage);
        first.getResponse("todo A");
        first.getResponse("deadline B /by 2026-12-01");
        first.getResponse("event C /at noon");
        first.getResponse("event D /from 1pm /to 2pm");
        first.getResponse("mark 2");
        first.getResponse("place Home /at Somewhere");

        Walter second = new Walter(storage);
        assertEquals(first.getResponse("list"), second.getResponse("list"));
        assertEquals(first.getResponse("places"), second.getResponse("places"));
        second.getResponse("unmark 2");
        second.getResponse("delete 1");
        second.getResponse("deleteplace 1");

        Walter third = new Walter(storage);
        assertEquals("Here is the current operation:\n"
                + "1. [D][ ] B (by: Dec 1 2026)\n"
                + "2. [E][ ] C (at: noon)\n"
                + "3. [E][ ] D (from: 1pm to: 2pm)", third.getResponse("list"));
        assertEquals("The location list is empty.", third.getResponse("places"));
    }

    @Test
    public void getResponse_largeUnicodeFields_surviveCommandsAndRestart() {
        Storage storage = new Storage(temporaryDirectory.resolve("large").resolve("walter.txt"));
        Walter walter = new Walter(storage);
        String longText = "中文🚀! long-field ".repeat(400) + "end";

        assertTrue(walter.getResponse("todo " + longText).contains(longText));
        assertTrue(walter.getResponse("event long-event /at " + longText).contains(longText));
        assertTrue(walter.getResponse("place long-place /at " + longText).contains(longText));
        assertTrue(walter.getResponse("find 中文🚀").contains(longText));

        Walter restarted = new Walter(storage);
        assertTrue(restarted.getResponse("list").contains(longText));
        assertTrue(restarted.getResponse("places").contains(longText));
    }

    /**
     * Creates Walter with an isolated save file for one test.
     */
    private Walter createWalter() {
        return new Walter(new Storage(temporaryDirectory.resolve("walter.txt")));
    }
}
