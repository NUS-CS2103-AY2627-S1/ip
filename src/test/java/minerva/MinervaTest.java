package minerva;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command processing in Minerva. */
public class MinervaTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void processCommand_emptyInput_returnsFriendlyMessage() {
        Minerva minerva = createMinerva();

        assertEquals("No worries! Please enter a command so I can help.",
                minerva.processCommand("   "));
    }

    @Test
    public void processCommand_addAndList_containsAddedTask() {
        Minerva minerva = createMinerva();

        minerva.processCommand("todo read book");

        assertTrue(minerva.processCommand("list").contains("[T][ ] read book"));
    }

    @Test
    public void processCommand_unknownCommand_returnsHelpfulMessage() {
        Minerva minerva = createMinerva();

        assertEquals(
                "I’m sorry, I didn’t recognize that command. Type help to see what I can do.",
                minerva.processCommand("unknown"));
    }

    @Test
    public void processCommand_undoAfterAdd_removesAddedTask() {
        Minerva minerva = createMinerva();

        minerva.processCommand("todo read book");
        minerva.processCommand("undo");

        assertEquals("Here are the tasks in your list:", minerva.processCommand("list"));
    }

    @Test
    public void processCommand_restart_preservesAllTaskTypesAndStatuses() {
        Minerva minerva = createMinerva();
        minerva.processCommand("todo read book");
        minerva.processCommand("deadline submit /by 2026-09-30");
        minerva.processCommand("event meeting /from 2026-09-20 /to 3pm");
        minerva.processCommand("task plan weekend");
        for (int number = 1; number <= 4; number++) {
            minerva.processCommand("mark " + number);
        }
        assertEquals(minerva.processCommand("list"), createMinerva().processCommand("list"));
        for (int number = 1; number <= 4; number++) {
            minerva.processCommand("unmark " + number);
        }
        assertEquals(minerva.processCommand("list"), createMinerva().processCommand("list"));
    }

    @Test
    public void processCommand_invalidIndex_preservesUndoHistory() {
        for (String command : new String[]{"mark", "unmark", "delete"}) {
            Minerva minerva = createMinerva();
            minerva.processCommand("todo one");
            for (String number : new String[]{"0", "-1", "99", "2147483648", "abc"}) {
                assertTrue(minerva.processCommand(command + " " + number).startsWith("I couldn’t find"));
            }
            minerva.processCommand("undo");
            assertEquals("Here are the tasks in your list:", minerva.processCommand("list"));
        }
    }

    @Test
    public void processCommand_storageSeparators_rejectsWithoutChangingHistory() {
        Minerva minerva = createMinerva();
        for (String command : new String[]{"todo read | revise", "task read|revise",
                "deadline read | revise /by 2026-09-30",
                "event read | revise /from 2026-09-20 /to 3pm",
                "event meeting /from 2026-09-20 /to 3pm | 4pm", "todo first\nsecond"}) {
            assertEquals("Please avoid pipes (|) and line breaks in task details.",
                    minerva.processCommand(command));
        }
        assertEquals("Here are the tasks in your list:", minerva.processCommand("list"));
        assertTrue(minerva.processCommand("undo").startsWith("There’s nothing"));
    }

    @Test
    public void processCommand_invalidEventSeparators_rejectsTask() {
        Minerva minerva = createMinerva();
        for (String command : new String[]{"event meeting /to 2026-09-20 /from 3pm",
                "event meeting /from 2026-09-20 /from 3pm",
                "event meeting /to 2026-09-20 /to 3pm"}) {
            assertTrue(minerva.processCommand(command).startsWith("An event needs"));
        }
        assertEquals("Here are the tasks in your list:", minerva.processCommand("list"));
    }

    @Test
    public void getResponse_saveFailure_warnsForEveryMutation() throws IOException {
        Path blockedFile = temporaryDirectory.resolve("blocked.txt");
        Files.createDirectory(blockedFile);
        Minerva minerva = new Minerva(blockedFile.toString());
        for (String command : new String[]{"todo one", "task two",
                "deadline three /by 2026-09-30", "event four /from 2026-09-20 /to 3pm",
                "mark 1", "unmark 1", "delete 1", "undo"}) {
            assertTrue(minerva.getResponse(command).endsWith(
                    "Changes could not be saved. Please check the storage location."));
        }
        assertFalse(minerva.getResponse("list").contains("Changes could not be saved"));
    }

    /**
     * Creates an application using the temporary saved-task file.
     *
     * @return application backed by the test file
     */
    private Minerva createMinerva() {
        return new Minerva(temporaryDirectory.resolve("minerva.txt").toString());
    }
}
