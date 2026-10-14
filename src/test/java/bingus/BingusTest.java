package bingus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bingus.command.CommandType;

/**
 * Tests the GUI-facing command-response API of {@link Bingus}.
 */
class BingusTest {
    /** Directory created by JUnit for isolated task save files. */
    @TempDir
    Path temporaryDirectory;

    /**
     * Creates a Bingus instance with a test-specific save file.
     *
     * @return Bingus instance backed by a temporary save file.
     */
    private Bingus createBingus() {
        return new Bingus(temporaryDirectory.resolve("data/bingus.txt").toString());
    }

    /**
     * Verifies that a new GUI session can display Bingus's introduction.
     */
    @Test
    void getWelcomeMessage_newSession_returnsIntroductionWithoutChangingCommandType() {
        Bingus bingus = createBingus();

        assertEquals("Hi! I'm Bingus, your task-watching cat.\n"
                + "Tell me what you need to remember, and I'll keep an eye on it.",
                bingus.getWelcomeMessage());
        assertEquals(CommandType.NONE, bingus.getCommandType());
    }

    /**
     * Verifies that updating a description changes and persists the selected task.
     */
    @Test
    void getResponse_updateDescription_changesAndPersistsTask() {
        Bingus bingus = createBingus();
        bingus.getResponse("todo read book");
        bingus.getResponse("mark 1");

        String updateResponse = bingus.getResponse("update 1 /desc read two chapters");

        assertEquals(CommandType.UPDATE, bingus.getCommandType());
        assertTrue(updateResponse.contains("read two chapters"));
        assertTrue(updateResponse.contains("✓"));

        Bingus reloadedBingus = createBingus();
        String listResponse = reloadedBingus.getResponse("list");
        assertTrue(listResponse.contains("read two chapters"));
        assertTrue(listResponse.contains("✓"));
        assertFalse(listResponse.contains("read book"));
    }

    /**
     * Verifies that updating a deadline changes and persists only its due date.
     */
    @Test
    void getResponse_updateDeadline_changesAndPersistsDueDate() {
        Bingus bingus = createBingus();
        bingus.getResponse("deadline submit report /by 2026-09-15 2359");
        bingus.getResponse("mark 1");

        String updateResponse = bingus.getResponse("update 1 /by 2026-09-20 1800");

        assertEquals(CommandType.UPDATE, bingus.getCommandType());
        assertTrue(updateResponse.contains("submit report"));
        assertTrue(updateResponse.contains("Sep 20 2026, 6:00 PM"));
        assertTrue(updateResponse.contains("✓"));

        Bingus reloadedBingus = createBingus();
        String listResponse = reloadedBingus.getResponse("list");
        assertTrue(listResponse.contains("submit report"));
        assertTrue(listResponse.contains("Sep 20 2026, 6:00 PM"));
        assertTrue(listResponse.contains("✓"));
        assertFalse(listResponse.contains("Sep 15 2026, 11:59 PM"));
    }

    /**
     * Verifies that updating both event endpoints changes and persists only its times.
     */
    @Test
    void getResponse_updateEventTimes_changesAndPersistsStartAndEndTimes() {
        Bingus bingus = createBingus();
        bingus.getResponse("event project meeting /from 2026-09-20 1400 /to 2026-09-20 1600");
        bingus.getResponse("mark 1");

        bingus.getResponse("update 1 /from 2026-09-20 1500");
        String updateResponse = bingus.getResponse("update 1 /to 2026-09-20 1730");

        assertEquals(CommandType.UPDATE, bingus.getCommandType());
        assertTrue(updateResponse.contains("project meeting"));
        assertTrue(updateResponse.contains("Sep 20 2026, 3:00 PM"));
        assertTrue(updateResponse.contains("Sep 20 2026, 5:30 PM"));
        assertTrue(updateResponse.contains("✓"));

        Bingus reloadedBingus = createBingus();
        String listResponse = reloadedBingus.getResponse("list");
        assertTrue(listResponse.contains("project meeting"));
        assertTrue(listResponse.contains("Sep 20 2026, 3:00 PM"));
        assertTrue(listResponse.contains("Sep 20 2026, 5:30 PM"));
        assertTrue(listResponse.contains("✓"));
        assertFalse(listResponse.contains("Sep 20 2026, 2:00 PM"));
        assertFalse(listResponse.contains("Sep 20 2026, 4:00 PM"));
    }

    /**
     * Verifies that adding a task produces a response and lists an incomplete task icon.
     */
    @Test
    void getResponse_todoThenList_returnsAddedIncompleteTask() {
        Bingus bingus = createBingus();

        String addResponse = bingus.getResponse("todo read book");
        assertTrue(addResponse.contains("read book"));
        assertEquals(CommandType.ADD, bingus.getCommandType());

        String listResponse = bingus.getResponse("list");
        assertEquals(CommandType.LIST, bingus.getCommandType());
        assertTrue(listResponse.contains("○"));
        assertTrue(listResponse.contains("read book"));
    }

    /**
     * Verifies that marking a task changes its displayed status icon.
     */
    @Test
    void getResponse_markThenList_returnsCompletedTask() {
        Bingus bingus = createBingus();
        bingus.getResponse("todo read book");

        String markResponse = bingus.getResponse("mark 1");
        assertTrue(markResponse.contains("✓"));
        assertEquals(CommandType.MARK, bingus.getCommandType());

        String listResponse = bingus.getResponse("list");
        assertTrue(listResponse.contains("✓"));
        assertTrue(listResponse.contains("read book"));
    }

    /**
     * Verifies that an invalid command returns an error and records an invalid command type.
     */
    @Test
    void getResponse_invalidCommand_returnsHelpfulMessageAndInvalidCommandType() {
        Bingus bingus = createBingus();

        String response = bingus.getResponse("nonsense");

        assertEquals("Hmm, I do not recognise that command.\n"
                + "Try `todo`, `deadline`, `event`, `list`, `mark`, `unmark`, `delete`, "
                + "`find`, `update`, or `bye`.", response);
        assertEquals(CommandType.INVALID, bingus.getCommandType());
    }

    /**
     * Verifies that an invalid command does not remove tasks that were already added.
     */
    @Test
    void getResponse_invalidCommand_preservesExistingTasks() {
        Bingus bingus = createBingus();
        bingus.getResponse("todo read book");

        String errorResponse = bingus.getResponse("delete 2");

        assertEquals("I could not find that task number in your list.", errorResponse);
        assertEquals(CommandType.INVALID, bingus.getCommandType());
        assertTrue(bingus.getResponse("list").contains("read book"));
    }

    /**
     * Verifies that Bingus starts with an empty list and exposes an error after a corrupted save file.
     *
     * @throws IOException if the test save file cannot be written.
     */
    @Test
    void constructor_corruptedSaveFile_startsWithEmptyListAndLoadError() throws IOException {
        Path saveFile = temporaryDirectory.resolve("data/bingus.txt");
        Files.createDirectories(saveFile.getParent());
        Files.writeString(saveFile, "T|0|not-valid-base64!");

        Bingus bingus = new Bingus(saveFile.toString());

        assertEquals("I could not load your saved tasks. I have started with an empty list.",
                bingus.getLoadErrorMessage());
        assertEquals("Your task list is empty. Time for a cat nap!", bingus.getResponse("list"));
        assertEquals(CommandType.LIST, bingus.getCommandType());
    }

    /**
     * Verifies that the bye command produces a farewell response for the GUI to display.
     */
    @Test
    void getResponse_bye_returnsFarewellAndExitCommandType() {
        Bingus bingus = createBingus();

        String response = bingus.getResponse("bye");

        assertEquals("Bingus is curling up for a nap. See you soon!", response);
        assertEquals(CommandType.EXIT, bingus.getCommandType());
    }

    /**
     * Verifies that tasks added through the response API are loaded by a later Bingus instance.
     */
    @Test
    void getResponse_addedTask_isLoadedByNewInstance() {
        Bingus firstBingus = createBingus();
        firstBingus.getResponse("todo persist task");

        Bingus reloadedBingus = createBingus();
        String listResponse = reloadedBingus.getResponse("list");

        assertTrue(listResponse.contains("persist task"));
    }
}
