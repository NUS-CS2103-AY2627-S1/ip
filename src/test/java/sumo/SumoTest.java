package sumo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests responses, persistence, and recovery through Sumo's shared command entry point. */
class SumoTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    void getResponse_addAndListCommands_returnsExistingResponses() {
        Sumo sumo = createSumo();

        String addResponse = sumo.getResponse("todo read book");
        String listResponse = sumo.getResponse("list");

        assertEquals("Understood. I've added this task:" + System.lineSeparator()
                + "   [T][ ] read book" + System.lineSeparator()
                + " Now you have 1 tasks in the list.", addResponse);
        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                + " 1.[T][ ] read book", listResponse);
    }

    @Test
    void getResponse_invalidCommand_returnsExistingErrorResponse() {
        Sumo sumo = createSumo();

        String response = sumo.getResponse("blah");

        assertEquals("I could not complete that command: I do not recognise that command. "
                + "Try todo, deadline, event, list, sort, find, on, mark, unmark, or delete.", response);
        assertEquals("I could not complete that command: Use: sort. This command takes no arguments.",
                sumo.getResponse("sort descending"));
    }

    @Test
    void getResponse_sortCommand_displaysTemporarySortedView() throws Exception {
        Sumo sumo = createSumo();
        Path dataFile = temporaryDirectory.resolve("sumo.txt");

        sumo.getResponse("todo buy groceries");
        sumo.getResponse("deadline submit report /by 2026-02-09");
        sumo.getResponse("event meeting /from 2026-02-03 /to 2026-02-10");
        sumo.getResponse("mark 2");
        String storedTasksBeforeSort = Files.readString(dataFile);

        assertEquals("Here are your tasks sorted chronologically (ascending):" + System.lineSeparator()
                + " Incomplete tasks:" + System.lineSeparator()
                + " 1.[E][ ] meeting (from: Feb 03 2026 to: Feb 10 2026)" + System.lineSeparator()
                + " 2.[T][ ] buy groceries" + System.lineSeparator()
                + " Completed tasks:" + System.lineSeparator()
                + " 3.[D][X] submit report (by: Feb 09 2026)" + System.lineSeparator()
                + " Sorted view only; the normal task order is unchanged.", sumo.getResponse("sort"));
        assertEquals(storedTasksBeforeSort, Files.readString(dataFile));
        assertEquals("Here are the tasks in your list:" + System.lineSeparator()
                + " 1.[T][ ] buy groceries" + System.lineSeparator()
                + " 2.[D][X] submit report (by: Feb 09 2026)" + System.lineSeparator()
                + " 3.[E][ ] meeting (from: Feb 03 2026 to: Feb 10 2026)", sumo.getResponse("list"));
    }

    @Test
    void getResponse_sort_emptyState() {
        Sumo sumo = createSumo();

        assertEquals("No tasks to be sorted.", sumo.getResponse("sort"));
    }

    @Test
    void getResponse_exitCommand_returnsGoodbyeAndEndsSession() {
        Sumo sumo = createSumo();

        assertFalse(sumo.isExit());
        assertEquals("Until next time. I'll be here when you need me.", sumo.getResponse("bye"));
        assertTrue(sumo.isExit());
    }

    @Test
    void getResponse_duplicateRejectedWithoutChangingSavedTasks() throws Exception {
        Sumo sumo = createSumo();
        sumo.getResponse("todo read book");
        sumo.getResponse("mark 1");
        Path dataFile = temporaryDirectory.resolve("sumo.txt");
        String original = Files.readString(dataFile);

        assertEquals("I could not complete that command: That task is already in your list.",
                sumo.getResponse("todo  READ  book"));
        assertEquals(original, Files.readString(dataFile));
        assertTrue(sumo.getResponse("list").contains("1.[T][X] read book"));
    }

    @Test
    void getResponse_saveFailure_allMutationsRolledBackAndReadsStillWork() throws Exception {
        Sumo sumo = createSumo();
        sumo.getResponse("todo first");
        sumo.getResponse("todo second");
        sumo.getResponse("mark 2");
        String originalList = sumo.getResponse("list");
        Path dataFile = temporaryDirectory.resolve("sumo.txt");
        Files.writeString(dataFile, "T | 0 | external edit");

        for (String command : List.of("todo third", "mark 1", "unmark 2", "delete 1")) {
            assertEquals("I could not save your tasks: The data file changed outside this session. "
                    + "Restart Sumo before making changes.", sumo.getResponse(command));
            assertEquals(originalList, sumo.getResponse("list"));
            assertEquals("T | 0 | external edit", Files.readString(dataFile));
        }
        assertEquals("Until next time. I'll be here when you need me.", sumo.getResponse("bye"));
    }

    @Test
    void getResponse_corruptFile_warningVisibleAndOriginalPreserved() throws Exception {
        Path dataFile = temporaryDirectory.resolve("sumo.txt");
        String original = "T | 0 | first\ninvalid record\nT | 0 | last";
        Files.writeString(dataFile, original);
        Sumo sumo = createSumo();

        assertTrue(sumo.getStartupMessage().contains("line 2"));
        assertTrue(sumo.getStartupMessage().contains("Saving is disabled"));
        assertTrue(sumo.getResponse("list").contains("2.[T][ ] last"));
        assertTrue(sumo.getResponse("delete 1").startsWith("I could not save your tasks:"));
        assertTrue(sumo.getResponse("list").contains("1.[T][ ] first"));
        assertEquals(original, Files.readString(dataFile));
    }

    @Test
    void getResponse_unreadableFile_warningVisibleAndSavingBlocked() throws Exception {
        Path dataFile = temporaryDirectory.resolve("sumo.txt");
        Files.write(dataFile, new byte[] {(byte) 0xc3, 0x28});
        Sumo sumo = createSumo();

        assertTrue(sumo.getStartupMessage().contains("I could not load your saved tasks:"));
        assertTrue(sumo.getStartupMessage().contains("Saving is disabled"));
        assertTrue(sumo.getResponse("todo new").startsWith("I could not save your tasks:"));
        assertEquals("No tasks in your list yet.", sumo.getResponse("list"));
        assertEquals(2, Files.size(dataFile));
    }

    @Test
    void getResponse_invalidInput_sessionCanContinue() {
        Sumo sumo = createSumo();

        assertEquals("I could not complete that command: Please enter a command.", sumo.getResponse(null));
        assertEquals("I could not complete that command: Please enter a command.", sumo.getResponse("   "));
        assertTrue(sumo.getResponse("todo first\nsecond").startsWith("I could not complete that command:"));
        assertTrue(sumo.getResponse("todo valid").startsWith("Understood."));
    }

    private Sumo createSumo() {
        return new Sumo(temporaryDirectory.resolve("sumo.txt").toString());
    }
}
