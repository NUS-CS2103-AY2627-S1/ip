package larp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LarpTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_addAndListTask_returnsUpdatedTaskList() {
        Larp larp = new Larp(temporaryDirectory.resolve("tasks.txt").toString());

        String addResponse = larp.getResponse("todo read book");
        String listResponse = larp.getResponse("list");

        assertTrue(addResponse.contains("read book"));
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", listResponse);
    }

    @Test
    void getResponse_emptyCommand_returnsErrorMessage() {
        Larp larp = new Larp(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Let's fix that: Please enter a command.", larp.getResponse("   "));
    }

    @Test
    void getResponse_nullCommand_returnsErrorMessage() {
        Larp larp = new Larp(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Let's fix that: Please enter a command.", larp.getResponse(null));
    }

    @Test
    void getResponse_corruptDataFile_doesNotOverwriteOriginalData() throws Exception {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        String corruptData = "D | 0 | impossible date | 2026-02-30";
        java.nio.file.Files.writeString(dataFile, corruptData);
        Larp larp = new Larp(dataFile.toString());

        String response = larp.getResponse("todo read book");

        assertTrue(response.contains("can't change tasks"));
        assertEquals(corruptData, java.nio.file.Files.readString(dataFile));
    }

    @Test
    void getResponse_findWithDifferentCase_returnsMatchingTask() {
        Larp larp = new Larp(temporaryDirectory.resolve("tasks.txt").toString());
        larp.getResponse("todo Read Book");

        String findResponse = larp.getResponse("find BOOK");

        assertEquals("Here are the matching tasks in your list:\n1.[T][ ] Read Book",
                findResponse);
    }

    @Test
    void getResponse_taskLifecycle_updatesAndDeletesTask() {
        Larp larp = new Larp(temporaryDirectory.resolve("tasks.txt").toString());
        larp.getResponse("todo read book");

        assertTrue(larp.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(larp.getResponse("unmark 1").contains("[T][ ] read book"));
        assertTrue(larp.getResponse("delete 1").contains("Now you have 0 tasks"));
        assertEquals("Your task list is empty.", larp.getResponse("list"));
    }

    @Test
    void getResponse_deadlineAndEvent_addsBothTaskTypes() {
        Larp larp = new Larp(temporaryDirectory.resolve("tasks.txt").toString());

        larp.getResponse("deadline submit report /by 2026-09-18");
        larp.getResponse("event demo /from 2pm /to 3pm");

        String response = larp.getResponse("list");
        assertTrue(response.contains("[D][ ] submit report (by: Sep 18 2026)"));
        assertTrue(response.contains("[E][ ] demo (from: 2pm to: 3pm)"));
    }

    @Test
    void getResponse_unknownCommand_returnsHelpfulError() {
        Larp larp = new Larp(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("Let's fix that: I don't recognize that command.",
                larp.getResponse("remember read book"));
    }

    @Test
    void isExitCommand_onlyExactByeCommand_returnsTrue() {
        Larp larp = new Larp(temporaryDirectory.resolve("tasks.txt").toString());

        assertTrue(larp.isExitCommand(" bye "));
        assertFalse(larp.isExitCommand("bye now"));
    }

    @Test
    void getResponse_byeCommand_returnsPersonalizedFarewell() {
        Larp larp = new Larp(temporaryDirectory.resolve("tasks.txt").toString());

        assertEquals("All set. Your tasks are saved. See you next time!",
                larp.getResponse("bye"));
    }
}
