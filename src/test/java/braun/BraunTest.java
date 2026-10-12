package braun;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for {@link Braun} core dispatch and GUI bridge methods.
 */
public class BraunTest {

    @TempDir
    Path tempDir;

    private Braun createTestBraun() {
        return new Braun(tempDir.resolve("test_braun.txt").toString());
    }

    @Test
    public void getWelcomeMessage_default_returnsPersonaGreeting() {
        Braun braun = createTestBraun();
        String welcome = braun.getWelcomeMessage();
        assertTrue(welcome.contains("*kzzzt... bzzzt!*"));
        assertTrue(welcome.contains("Good evening, dear guest! I'm Braun, host of the Late-Night Show."));
    }

    @Test
    public void getResponse_emptyInput_returnsEmptyString() {
        Braun braun = createTestBraun();
        String response = braun.getResponse("   ");
        assertEquals("", response);
    }

    @Test
    public void getResponse_listEmptyTasks_returnsHeaderOnly() {
        Braun braun = createTestBraun();
        String response = braun.getResponse("list");
        assertEquals("Here are the tasks in your list:", response);
    }

    @Test
    public void getResponse_addTodo_returnsSuccessAndRemark() {
        Braun braun = createTestBraun();
        String response = braun.getResponse("todo ghost exploration");
        assertTrue(response.contains("Got it. I've added this task:"));
        assertTrue(response.contains("[T][ ] ghost exploration"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
        assertTrue(response.contains("A new anomaly! Daydream Inc. will want this documented."));
    }

    @Test
    public void getResponse_unknownCommand_returnsStaticErrorMessage() {
        Braun braun = createTestBraun();
        String response = braun.getResponse("invalidcommand");
        assertTrue(response.contains("*static* Unknown broadcast command!"));
    }

    @Test
    public void getResponse_bye_returnsFarewellMessage() {
        Braun braun = createTestBraun();
        String response = braun.getResponse("bye");
        assertTrue(response.contains("*bzzzt* That's a wrap for today's broadcast!"));
        assertTrue(response.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    public void getResponse_markAndUnmarkTask_statusUpdatedCorrectly() {
        Braun braun = createTestBraun();
        braun.getResponse("todo explore abandoned hallway");
        String markResponse = braun.getResponse("mark 1");
        assertTrue(markResponse.contains("Nice! I've marked this task as done:"));
        assertTrue(markResponse.contains("[T][X] explore abandoned hallway"));

        String unmarkResponse = braun.getResponse("unmark 1");
        assertTrue(unmarkResponse.contains("OK, I've marked this task as not done yet:"));
        assertTrue(unmarkResponse.contains("[T][ ] explore abandoned hallway"));
    }

    @Test
    public void getResponse_findExistingTask_returnsMatchingTasks() {
        Braun braun = createTestBraun();
        braun.getResponse("todo pink rabbit doll");
        braun.getResponse("todo investigate anomaly");
        String findResponse = braun.getResponse("find rabbit");
        assertTrue(findResponse.contains("Here are the matching tasks in your list:"));
        assertTrue(findResponse.contains("1.[T][ ] pink rabbit doll"));
    }

    @Test
    public void getResponse_findMultipleKeywords_returnsAllMatchingTasks() {
        Braun braun = createTestBraun();
        braun.getResponse("todo pink rabbit doll");
        braun.getResponse("todo investigate ghost anomaly");
        braun.getResponse("todo staff coffee break");

        String findResponse = braun.getResponse("find rabbit ghost");
        assertTrue(findResponse.contains("Here are the matching tasks in your list:"));
        assertTrue(findResponse.contains("1.[T][ ] pink rabbit doll"));
        assertTrue(findResponse.contains("2.[T][ ] investigate ghost anomaly"));
        assertFalse(findResponse.contains("coffee break"));
    }

    @Test
    public void getResponse_findQuotedPhrase_matchesExactPhrase() {
        Braun braun = createTestBraun();
        braun.getResponse("todo pink rabbit doll");
        braun.getResponse("todo rabbit in pink box");

        String findResponse = braun.getResponse("find \"pink rabbit\"");
        assertTrue(findResponse.contains("Here are the matching tasks in your list:"));
        assertTrue(findResponse.contains("1.[T][ ] pink rabbit doll"));
        assertFalse(findResponse.contains("rabbit in pink box"));
    }

    @Test
    public void getResponse_findDateOrTime_matchesScheduledTasks() {
        Braun braun = createTestBraun();
        braun.getResponse("deadline submit monthly report /by 2026-08-30 1700");

        String findByFormattedDate = braun.getResponse("find Aug 30");
        assertTrue(findByFormattedDate.contains("submit monthly report"));

        String findByIsoDate = braun.getResponse("find 2026-08-30");
        assertTrue(findByIsoDate.contains("submit monthly report"));
    }

    @Test
    public void getResponse_findEmptyQuotes_returnsErrorMessage() {
        Braun braun = createTestBraun();
        String response = braun.getResponse("find \"\"   ");
        assertTrue(response.contains("*static* Please specify a keyword to search for (e.g. find book)."));
    }

    @Test
    public void getResponse_duplicateTodo_returnsErrorMessage() {
        Braun braun = createTestBraun();
        braun.getResponse("todo examine pink rabbit doll");
        String duplicateResponse = braun.getResponse("todo examine pink rabbit doll");
        assertTrue(duplicateResponse.contains("*static* Duplicate broadcast task detected!"));
    }

    @Test
    public void getResponse_duplicateDeadline_returnsErrorMessage() {
        Braun braun = createTestBraun();
        braun.getResponse("deadline submit monthly report /by 2026-08-30 1700");
        String duplicateResponse = braun.getResponse("deadline submit monthly report /by 2026-08-30 1700");
        assertTrue(duplicateResponse.contains("*static* Duplicate broadcast task detected!"));
    }

    @Test
    public void getResponse_multipleByDelimiters_returnsErrorMessage() {
        Braun braun = createTestBraun();
        String response = braun.getResponse("deadline submit report /by 2026-08-30 1700 /by 2026-09-01 1800");
        assertTrue(response.contains("*static* Multiple /by delimiters detected!"));
    }

    @Test
    public void getResponse_multipleFromOrToDelimiters_returnsErrorMessage() {
        Braun braun = createTestBraun();
        String response = braun.getResponse("event meeting /from 2pm /to 3pm /to 4pm");
        assertTrue(response.contains("*static* Multiple /from or /to delimiters detected!"));
    }
}
