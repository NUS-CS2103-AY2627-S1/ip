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
        assertEquals("Here are your tasks:\n1. [T][ ] read book",
                walter.getResponse("list"));
        assertTrue(walter.getResponse("mark 1").contains("[T][X] read book"));
    }

    @Test
    public void getResponse_invalidCommand_errorReturned() {
        Walter walter = createWalter();

        String response = walter.getResponse("archive book");
        assertTrue(response.startsWith("Unknown command. Try:"));
        assertTrue(response.contains("todo DESCRIPTION"));
        assertTrue(response.contains("deadline DESCRIPTION /by yyyy-MM-dd"));
        assertTrue(response.endsWith("Stay sharp, Jesse."));
    }

    @Test
    public void getResponse_knownErrorTypes_personalityPreservesSpecificDiagnostics() {
        Walter walter = createWalter();

        assertEquals("Todo description cannot be empty. Stay sharp, Jesse.",
                walter.getResponse("todo"));
        assertEquals("Task number is out of range. Stay sharp, Jesse.",
                walter.getResponse("mark 1"));
        assertEquals("Deadline date must be in yyyy-MM-dd format. Stay sharp, Jesse.",
                walter.getResponse("deadline report /by tomorrow"));
        assertEquals("Keyword is required for the find command. Stay sharp, Jesse.",
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

        assertTrue(walter.getResponse("archive book").startsWith("Unknown command. Try:"));
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
    public void getResponse_normalizedDuplicatesAfterRestart_rejectedAndStorageUnchanged()
            throws IOException {
        Path saveFile = temporaryDirectory.resolve("walter.txt");
        Storage storage = new Storage(saveFile);
        Walter firstWalter = new Walter(storage);
        assertTrue(firstWalter.getResponse("todo Read   book").contains("[T][ ] Read   book"));
        firstWalter.getResponse("mark 1");
        assertTrue(firstWalter.getResponse("deadline Submit report /by 2026-12-10")
                .contains("[D][ ] Submit report"));
        assertTrue(firstWalter.getResponse(
                "event Workshop /from 2026-12-01 /to 2026-12-10")
                .contains("[E][ ] Workshop"));
        String storedBeforeRejections = Files.readString(saveFile, StandardCharsets.UTF_8);

        Walter restartedWalter = new Walter(storage);
        assertEquals("That task already exists. Stay sharp, Jesse.",
                restartedWalter.getResponse("todo read book"));
        assertEquals("That task already exists. Stay sharp, Jesse.",
                restartedWalter.getResponse("deadline submit   REPORT /by 2026-12-10"));
        assertEquals("That task already exists. Stay sharp, Jesse.", restartedWalter.getResponse(
                "event workshop /from 2026-12-01 /to 2026-12-10"));
        assertEquals(storedBeforeRejections, Files.readString(saveFile, StandardCharsets.UTF_8));
        assertEquals("Here are your tasks:\n"
                + "1. [T][X] Read   book\n"
                + "2. [D][ ] Submit report (by: Dec 10 2026)\n"
                + "3. [E][ ] Workshop (from: 2026-12-01 to: 2026-12-10)",
                restartedWalter.getResponse("list"));
    }

    @Test
    public void getResponse_reversedIsoEventRange_notAddedOrPersisted() {
        Storage storage = new Storage(temporaryDirectory.resolve("walter.txt"));
        Walter walter = new Walter(storage);

        assertEquals("Event start must not be after its end. Stay sharp, Jesse.",
                walter.getResponse("event Trip /from 2026-12-20 /to 2026-12-10"));
        assertEquals("The board is clean. Nothing needs doing.", walter.getResponse("list"));

        Walter restartedWalter = new Walter(storage);
        assertEquals("The board is clean. Nothing needs doing.",
                restartedWalter.getResponse("list"));
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

        assertEquals("Place name cannot be empty. Stay sharp, Jesse.",
                walter.getResponse("place"));
        assertEquals("Place requires exactly one /at. Stay sharp, Jesse.",
                walter.getResponse("place Alex's home"));
        assertEquals("Place name cannot be empty. Stay sharp, Jesse.",
                walter.getResponse("place /at 123 Clementi Ave 3"));
        assertEquals("Place address cannot be empty. Stay sharp, Jesse.",
                walter.getResponse("place Alex's home /at"));
        assertEquals("Place number must be an integer. Stay sharp, Jesse.",
                walter.getResponse("deleteplace abc"));
        assertEquals("Place number is out of range. Stay sharp, Jesse.",
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
            assertTrue(walter.getResponse(command).endsWith("Stay sharp, Jesse."));
        }

        assertEquals("Here are your tasks:\n"
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

        assertEquals("Malformed saved place text. Stay sharp, Jesse.",
                walter.getResponse("places"));
        assertEquals("The board is clean. Nothing needs doing.", walter.getResponse("list"));
    }

    @Test
    public void getResponse_repeatedStateTransitions_errorsAndPersistenceRemainCorrect() {
        Storage storage = new Storage(temporaryDirectory.resolve("walter.txt"));
        Walter walter = new Walter(storage);
        walter.getResponse("todo test");

        assertEquals("Task is already not done. Stay sharp, Jesse.",
                walter.getResponse("unmark 1"));
        assertTrue(walter.getResponse("mark 1").contains("[T][X] test"));
        assertEquals("Task is already marked as done. Stay sharp, Jesse.",
                walter.getResponse("mark 1"));
        assertEquals("Task is already marked as done. Stay sharp, Jesse.",
                walter.getResponse("done 1"));

        Walter restartedWalter = new Walter(storage);
        assertEquals("Here are your tasks:\n1. [T][X] test",
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

        assertEquals("Task list is full. Stay sharp, Jesse.",
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
        assertEquals("Here are your tasks:\n"
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
