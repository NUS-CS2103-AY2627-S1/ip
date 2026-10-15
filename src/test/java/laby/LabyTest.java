package laby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests command execution through the application facade. */
class LabyTest {

    @Test
    void executeCommand_addAndListTask_returnsResponsesAndPersistsTask(@TempDir Path tempDir) {
        Laby laby = new Laby(tempDir.resolve("laby.txt").toString());

        String addResponse = laby.executeCommand("todo read book");
        String listResponse = laby.executeCommand("list");

        assertTrue(addResponse.contains("[T][ ] read book"));
        assertTrue(addResponse.contains("There is a total of 1 task in your list."));
        assertTrue(listResponse.contains("1.[T][ ] read book"));
    }

    @Test
    void executeCommand_modifyTask_updatesTaskAndPersistsChange(@TempDir Path tempDir) {
        Laby laby = new Laby(tempDir.resolve("laby.txt").toString());
        laby.executeCommand("todo read book");

        String markResponse = laby.executeCommand("mark 1");
        String unmarkResponse = laby.executeCommand("unmark 1");
        String deleteResponse = laby.executeCommand("deletetask 1");

        assertTrue(markResponse.contains("[T][X] read book"));
        assertTrue(markResponse.contains("Understood. Laby has marked the task as done."));
        assertTrue(unmarkResponse.contains("[T][ ] read book"));
        assertTrue(unmarkResponse.contains("Understood. Laby has marked the task as not done."));
        assertTrue(deleteResponse.contains("Laby has deleted the task"));
        assertTrue(laby.executeCommand("list").contains("Here are the tasks and contacts in your list."));
        assertFalse(laby.executeCommand("list").contains("read book"));
    }

    @Test
    void executeCommand_addListAndDeleteContact_persistsContact(@TempDir Path tempDir) {
        Path file = tempDir.resolve("laby.txt");
        Laby laby = new Laby(file.toString());

        String addResponse = laby.executeCommand(
                "contact John Doe /p 91234567 /e john@example.com");
        String listResponse = laby.executeCommand("list");

        assertTrue(addResponse.contains("Laby has added the contact."));
        assertTrue(addResponse.contains("There is a total of 1 contact in your list."));
        assertTrue(listResponse.contains("1.John Doe | Phone: 91234567 | Email: john@example.com"));

        Laby reloadedLaby = new Laby(file.toString());
        assertTrue(reloadedLaby.executeCommand("list")
                .contains("John Doe | Phone: 91234567 | Email: john@example.com"));

        String deleteResponse = reloadedLaby.executeCommand("deletecontact 1");
        assertTrue(deleteResponse.contains("Laby has deleted the contact."));
        assertTrue(reloadedLaby.executeCommand("list").contains("Here are the tasks and contacts"));
        assertFalse(reloadedLaby.executeCommand("list")
                .contains("John Doe | Phone: 91234567 | Email: john@example.com"));
    }

    @Test
    void executeCommand_invalidCommand_returnsFormattedError(@TempDir Path tempDir) {
        Laby laby = new Laby(tempDir.resolve("laby.txt").toString());

        String response = laby.executeCommand("archive");

        assertEquals("Error detected... please input the correct commands.\n", response);
    }

    @Test
    void executeCommand_bye_returnsExitMessageWithoutTerminatingProcess(@TempDir Path tempDir) {
        Laby laby = new Laby(tempDir.resolve("laby.txt").toString());

        String response = laby.executeCommand("bye");

        assertTrue(response.contains("Goodbye. Switching to rest mode."));
    }

    @Test
    void executeCommand_addsDeadlineEventAndFindsMatchingTasks(@TempDir Path tempDir) {
        Laby laby = new Laby(tempDir.resolve("laby.txt").toString());

        assertTrue(laby.executeCommand("deadline return book /by 2026-08-23 10:00")
                .contains("[D][ ] return book (by: Aug 23 2026 10:00)"));
        assertTrue(laby.executeCommand("event project meeting /from 2026-08-23 11:00 /to 2026-08-23 12:00")
                .contains("[E][ ] project meeting"));
        assertTrue(laby.executeCommand("find BOOK").contains("1.[D][ ] return book"));
        assertFalse(laby.executeCommand("find holiday").contains("return book"));
    }

    @Test
    void executeCommand_invalidIndex_returnsTaskOrContactError(@TempDir Path tempDir) {
        Laby laby = new Laby(tempDir.resolve("laby.txt").toString());

        assertEquals("Error detected... please enter a valid task index.\n",
                laby.executeCommand("deletetask 1"));
        assertEquals("Error detected... please enter a valid contact index.\n",
                laby.executeCommand("deletecontact 1"));
    }
}
