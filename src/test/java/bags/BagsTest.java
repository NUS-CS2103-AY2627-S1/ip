package bags;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bags.exception.BagsException;

/**
 * Tests command workflows in the {@link Bags} application.
 */
class BagsTest {

    @TempDir
    Path tempDir;

    private Bags createBags() {
        Path storagePath = tempDir.resolve("Bags.txt");
        return new Bags(storagePath.toString());
    }

    @Test
    void processCommand_invalidInput_throwsException() {
        Bags bags = createBags();

        assertThrows(BagsException.class, () -> bags.processCommand("   "));
        assertThrows(BagsException.class, () -> bags.processCommand("remind me"));
    }

    @Test
    void processCommand_taskTypeOutsideAddMode_promptsForTaskMode() {
        Bags bags = createBags();

        String[] taskCommands = {
            "todo Read book",
            "deadline Submit report /by 2026-09-12 23:59",
            "event Project meeting /from 2026-09-13 10:00 /to 2026-09-13 11:00"
        };

        for (String taskCommand : taskCommands) {
            BagsException exception = assertThrows(
                    BagsException.class, () -> bags.processCommand(taskCommand)
            );

            assertTrue(exception.getMessage().contains("enter task-entry mode first"));
        }
    }

    @Test
    void constructor_missingSaveFile_returnsStartupAlert() {
        Bags bags = createBags();

        assertTrue(bags.getStartupMessage().contains("No saved task file"));
    }

    @Test
    void processCommand_saveFailure_throwsException() throws Exception {
        Path storagePath = tempDir.resolve("Bags");
        Files.createDirectory(storagePath);
        Bags bags = new Bags(storagePath.toString());
        bags.processCommand("add task");

        BagsException exception = assertThrows(
                BagsException.class, () -> bags.processCommand("todo Read book")
        );

        assertTrue(exception.getMessage().contains("Unable to save tasks"));
    }

    @Test
    void processCommand_addMode_addsSupportedTaskTypesAndExits() throws BagsException {
        Bags bags = createBags();

        assertTrue(bags.processCommand("add task").contains("What would you like me to pack"));
        assertTrue(bags.processCommand("todo Read book").contains("Safely packed"));
        assertTrue(bags.processCommand("deadline Submit report /by 2026-09-12 23:59")
                .contains("Safely packed"));
        assertTrue(bags.processCommand("event Project meeting /from 2026-09-13 10:00"
                + " /to 2026-09-13 11:00").contains("Safely packed"));
        assertEquals("Okay, I've closed the bag for now.", bags.processCommand("exit"));

    }

    @Test
    void processCommand_invalidAddTypeKeepsModeActive()
            throws BagsException {
        Bags bags = createBags();
        bags.processCommand("add task");

        BagsException exception = assertThrows(BagsException.class, () ->
                bags.processCommand("reminder Buy milk")
        );

        assertTrue(exception.getMessage().contains("can't pack that task type"));
        assertTrue(bags.processCommand("todo Buy milk").contains("Safely packed"));
    }

    @Test
    void processCommand_echoMode_echosInputAndRejectsSilence() throws BagsException {
        Bags bags = createBags();

        assertTrue(bags.processCommand("echo").contains("echo what you put"));

        BagsException exception = assertThrows(
                BagsException.class, () -> bags.processCommand("   ")
        );

        assertTrue(exception.getMessage().contains("can't echo silence"));
        assertEquals("Okay, I've closed the bag for now.", bags.processCommand("exit"));
    }

    @Test
    void processCommand_editMode_rejectsDifferentTypeAndUpdatesTask() throws BagsException {
        Bags bags = createBags();
        bags.processCommand("add task");
        bags.processCommand("todo Read book");
        bags.processCommand("exit");

        assertTrue(bags.processCommand("edit 1").contains("Let's repack this task"));
        BagsException exception = assertThrows(BagsException.class, () ->
                bags.processCommand("deadline Submit report /by 2026-09-12 23:59")
        );
        assertTrue(exception.getMessage().contains("same type"));
        assertTrue(bags.processCommand("todo Read chapter").contains("updated"));
        assertTrue(bags.processCommand("list").contains("Read chapter"));
    }

    @Test
    void processCommand_taskOperationsPersistUpdatedRecords() throws Exception {
        Path storagePath = tempDir.resolve("Bags.txt");
        Bags bags = new Bags(storagePath.toString());
        bags.processCommand("add task");
        bags.processCommand("todo Read book");
        bags.processCommand("exit");

        assertTrue(bags.processCommand("mark 1").contains("marked this task as done"));
        assertTrue(bags.processCommand("search book").contains("Read book"));
        assertTrue(bags.processCommand("unmark 1").contains("marked this task as undone"));
        assertTrue(bags.processCommand("delete 1").contains("removed"));
        assertEquals("See you soon! :)", bags.processCommand("bye"));

        assertEquals(List.of(), Files.readAllLines(storagePath));
        assertThrows(BagsException.class, () -> bags.processCommand("list"));
    }
}
