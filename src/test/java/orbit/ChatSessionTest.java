package orbit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Verifies that GUI chat sessions retain Orbit's commands, persistence, and failure recovery.
 */
class ChatSessionTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getResponse_consecutiveCommands_returnsOnlyCurrentReply() {
        ChatSession session = new ChatSession(getDataFile());
        String greeting = session.getGreeting();

        assertTrue(greeting.contains("Hello! I'm Orbit."));
        assertTrue(greeting.contains("What can I do for you?"));
        assertFalse(session.isExitRequested());

        String addedResponse = session.getResponse("todo read book");
        assertTrue(addedResponse.contains("Got it. I've added this task:"));
        assertFalse(addedResponse.contains("Hello!"));

        String listResponse = session.getResponse("list");
        assertTrue(listResponse.contains("1.[T][ ] read book"));
        assertFalse(listResponse.contains("Got it."));
        assertFalse(listResponse.contains("Hello!"));
        assertEquals(greeting, session.getGreeting());
    }

    @Test
    void getResponse_addTaskTypes_preservesMarkersDatesAndOrder() {
        ChatSession session = new ChatSession(getDataFile());

        assertTrue(session.getResponse("todo read notes").contains("[T][ ] read notes"));
        assertTrue(session.getResponse("deadline submit report /by 2028-02-29")
                .contains("[D][ ] submit report (by: Feb 29 2028)"));
        assertTrue(session.getResponse("event study group /from 2026-09-04 /to 2026-09-05")
                .contains("[E][ ] study group (from: Sep 4 2026 to: Sep 5 2026)"));

        String response = session.getResponse("list");
        int todoPosition = response.indexOf("1.[T][ ] read notes");
        int deadlinePosition = response.indexOf("2.[D][ ] submit report (by: Feb 29 2028)");
        int eventPosition = response.indexOf("3.[E][ ] study group (from: Sep 4 2026 to: Sep 5 2026)");
        assertTrue(todoPosition >= 0);
        assertTrue(deadlinePosition > todoPosition);
        assertTrue(eventPosition > deadlinePosition);
    }

    @Test
    void getResponse_markUnmarkDelete_preservesStateAndRenumbersTasks() {
        ChatSession session = new ChatSession(getDataFile());
        session.getResponse("todo first");
        session.getResponse("deadline second /by 2026-09-04");
        session.getResponse("todo third");

        assertTrue(session.getResponse("mark 2").contains("[D][X] second (by: Sep 4 2026)"));
        assertTrue(session.getResponse("unmark 2").contains("[D][ ] second (by: Sep 4 2026)"));
        String deleteResponse = session.getResponse("delete 2");
        assertTrue(deleteResponse.contains("Noted. I've removed this task:"));
        assertTrue(deleteResponse.contains("[D][ ] second (by: Sep 4 2026)"));
        assertTrue(deleteResponse.contains("Now you have 2 tasks in the list."));

        String response = session.getResponse("list");
        assertTrue(response.contains("1.[T][ ] first"));
        assertTrue(response.contains("2.[T][ ] third"));
        assertFalse(response.contains("second"));
        assertFalse(session.isExitRequested());
    }

    @Test
    void getResponse_findPhrase_returnsMatchesWithoutChangingList() {
        ChatSession session = new ChatSession(getDataFile());
        session.getResponse("todo read book");
        session.getResponse("todo read notes");
        session.getResponse("deadline return book /by 2026-09-04");
        String originalList = session.getResponse("list");

        String matches = session.getResponse("find book");
        assertTrue(matches.contains("Here are the matching tasks in your list:"));
        assertTrue(matches.contains("1.[T][ ] read book"));
        assertTrue(matches.contains("2.[D][ ] return book (by: Sep 4 2026)"));
        assertFalse(matches.contains("read notes"));

        String phraseMatches = session.getResponse("find read book");
        assertTrue(phraseMatches.contains("1.[T][ ] read book"));
        assertFalse(phraseMatches.contains("return book"));
        assertFalse(session.getResponse("find missing").contains("[T]"));
        assertEquals(originalList, session.getResponse("list"));
    }

    @Test
    void getResponse_invalidCommands_reportsErrorsAndKeepsSessionActive() {
        ChatSession session = new ChatSession(getDataFile());
        String emptyList = session.getResponse("list");
        List<String> invalidCommands = List.of("", "   ", "unknown", "finder book", "todo", "find",
                "mark 0", "delete abc", "deadline impossible /by 2026-02-29",
                "event bad date /from Monday /to 2026-09-05");

        for (String command : invalidCommands) {
            String response = session.getResponse(command);
            assertTrue(response.contains("OOPS!"), "Expected an error for: " + command);
            assertFalse(session.isExitRequested(), "Invalid command ended session: " + command);
        }

        assertEquals(emptyList, session.getResponse("list"));
        assertTrue(session.getResponse("todo recovered").contains("[T][ ] recovered"));
    }

    @Test
    void getResponse_byeWithArguments_exitsOnlyForValidBye() {
        ChatSession session = new ChatSession(getDataFile());

        assertTrue(session.getResponse("bye now").contains("OOPS! I don't know that command."));
        assertFalse(session.isExitRequested());
        assertTrue(session.getResponse("todo still active").contains("[T][ ] still active"));

        assertTrue(session.getResponse("bye").contains("Bye. Hope to see you again soon!"));
        assertTrue(session.isExitRequested());
    }

    @Test
    void reopen_savedTasks_preservesUnicodeDatesAndCompletion() {
        Path dataFile = getDataFile();
        ChatSession firstSession = new ChatSession(dataFile);
        assertTrue(firstSession.getResponse("todo read café 笔记").contains("[T][ ] read café 笔记"));
        firstSession.getResponse("deadline submit report /by 2026-09-04");
        firstSession.getResponse("event study group /from 2026-09-05 /to 2026-09-06");
        firstSession.getResponse("mark 2");
        String savedList = firstSession.getResponse("list");
        firstSession.getResponse("bye");

        ChatSession reopenedSession = new ChatSession(dataFile);
        assertFalse(reopenedSession.isExitRequested());
        assertEquals(savedList, reopenedSession.getResponse("list"));
        assertTrue(reopenedSession.getResponse("find 笔记").contains("1.[T][ ] read café 笔记"));

        reopenedSession.getResponse("unmark 2");
        reopenedSession.getResponse("delete 1");
        String updatedList = reopenedSession.getResponse("list");
        reopenedSession.getResponse("bye");

        ChatSession finalSession = new ChatSession(dataFile);
        assertEquals(updatedList, finalSession.getResponse("list"));
    }

    @Test
    void getResponse_addSaveFailure_restoresOriginalList() throws IOException {
        Path dataFile = getDataFile();
        ChatSession session = new ChatSession(dataFile);
        session.getResponse("todo existing");
        String originalList = session.getResponse("list");
        replaceDataFileWithDirectory(dataFile);

        String response = session.getResponse("todo unsaved");

        assertTrue(response.contains("OOPS! Could not save tasks to"));
        assertFalse(response.contains("Got it."));
        assertEquals(originalList, session.getResponse("list"));
        assertFalse(session.isExitRequested());
    }

    @Test
    void getResponse_markSaveFailure_restoresIncompleteState() throws IOException {
        Path dataFile = getDataFile();
        ChatSession session = new ChatSession(dataFile);
        session.getResponse("todo unfinished");
        String originalList = session.getResponse("list");
        replaceDataFileWithDirectory(dataFile);

        String response = session.getResponse("mark 1");

        assertTrue(response.contains("OOPS! Could not save tasks to"));
        assertFalse(response.contains("Nice!"));
        assertEquals(originalList, session.getResponse("list"));
        assertFalse(session.isExitRequested());
    }

    @Test
    void getResponse_unmarkSaveFailure_restoresCompletedState() throws IOException {
        Path dataFile = getDataFile();
        ChatSession session = new ChatSession(dataFile);
        session.getResponse("todo finished");
        session.getResponse("mark 1");
        String originalList = session.getResponse("list");
        replaceDataFileWithDirectory(dataFile);

        String response = session.getResponse("unmark 1");

        assertTrue(response.contains("OOPS! Could not save tasks to"));
        assertFalse(response.contains("OK,"));
        assertEquals(originalList, session.getResponse("list"));
        assertFalse(session.isExitRequested());
    }

    @Test
    void getResponse_deleteSaveFailure_restoresTaskAtOriginalPosition() throws IOException {
        Path dataFile = getDataFile();
        ChatSession session = new ChatSession(dataFile);
        session.getResponse("todo first");
        session.getResponse("todo middle");
        session.getResponse("todo last");
        session.getResponse("mark 2");
        String originalList = session.getResponse("list");
        replaceDataFileWithDirectory(dataFile);

        String response = session.getResponse("delete 2");

        assertTrue(response.contains("OOPS! Could not save tasks to"));
        assertFalse(response.contains("Noted."));
        assertEquals(originalList, session.getResponse("list"));
        assertFalse(session.isExitRequested());
    }

    @Test
    void getResponse_sortChronologically_persistsOrderAndCompletionState() {
        Path dataFile = getDataFile();
        ChatSession session = new ChatSession(dataFile);
        session.getResponse("todo undated first");
        session.getResponse("deadline later /by 2026-09-09");
        session.getResponse("event earliest /from 2026-09-02 /to 2026-09-03");
        session.getResponse("deadline middle /by 2026-09-04");
        session.getResponse("todo undated last");
        session.getResponse("mark 2");

        String sortResponse = session.getResponse("sort");
        String sortedList = session.getResponse("list");

        assertTrue(sortResponse.contains("Here are your tasks sorted chronologically:"));
        assertTasksAreInChronologicalOrder(sortResponse);
        assertTasksAreInChronologicalOrder(sortedList);
        session.getResponse("bye");
        ChatSession reopenedSession = new ChatSession(dataFile);
        assertEquals(sortedList, reopenedSession.getResponse("list"));
    }

    @Test
    void getResponse_invalidSortCommands_reportErrorsWithoutMutation() {
        ChatSession session = new ChatSession(getDataFile());
        session.getResponse("todo first");
        session.getResponse("deadline second /by 2026-09-04");
        String originalList = session.getResponse("list");

        assertTrue(session.getResponse("sort date").contains("OOPS! I don't know that command."));
        assertTrue(session.getResponse("sorter").contains("OOPS! I don't know that command."));
        assertEquals(originalList, session.getResponse("list"));
    }

    @Test
    void getResponse_sortSaveFailure_restoresOriginalOrder() throws IOException {
        Path dataFile = getDataFile();
        ChatSession session = new ChatSession(dataFile);
        session.getResponse("todo first");
        session.getResponse("deadline second /by 2026-09-04");
        String originalList = session.getResponse("list");
        replaceDataFileWithDirectory(dataFile);

        String response = session.getResponse("sort");

        assertTrue(response.contains("OOPS! Could not save tasks to"));
        assertFalse(response.contains("sorted chronologically"));
        assertEquals(originalList, session.getResponse("list"));
        assertFalse(session.isExitRequested());
    }

    private void assertTasksAreInChronologicalOrder(String response) {
        int eventPosition = response.indexOf("1.[E][ ] earliest");
        int middlePosition = response.indexOf("2.[D][ ] middle");
        int laterPosition = response.indexOf("3.[D][X] later");
        int firstTodoPosition = response.indexOf("4.[T][ ] undated first");
        int lastTodoPosition = response.indexOf("5.[T][ ] undated last");
        assertTrue(eventPosition >= 0);
        assertTrue(middlePosition > eventPosition);
        assertTrue(laterPosition > middlePosition);
        assertTrue(firstTodoPosition > laterPosition);
        assertTrue(lastTodoPosition > firstTodoPosition);
    }

    private Path getDataFile() {
        return temporaryDirectory.resolve("data").resolve("orbit.txt");
    }

    /**
     * Makes only this test's temporary storage unwritable as a file without relying on OS permissions.
     */
    private void replaceDataFileWithDirectory(Path dataFile) throws IOException {
        Files.delete(dataFile);
        Files.createDirectory(dataFile);
    }
}
