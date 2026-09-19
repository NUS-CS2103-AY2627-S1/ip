package bro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Integration and error-handling unit tests for {@link Bro}.
 */
public class BroTest {

    @TempDir
    Path tempDir;

    private Bro bro;
    private Path saveFilePath;

    @BeforeEach
    public void setUp() {
        saveFilePath = tempDir.resolve("test_tasks.txt");
        bro = new Bro(saveFilePath);
    }

    @Test
    public void getResponse_listWithExtraArguments_returnsErrorMessage() {
        String response = bro.getResponse("list extra arguments");
        assertEquals("Bro, the 'list' command doesn't take any extra arguments.", response);
    }

    @Test
    public void getResponse_byeWithExtraArguments_returnsErrorMessage() {
        String response = bro.getResponse("bye please");
        assertEquals("Bro, the 'bye' command doesn't take any extra arguments.", response);
    }

    @Test
    public void getResponse_unknownCommand_returnsHelpfulMessage() {
        String response = bro.getResponse("dance");
        assertTrue(response.contains("I don't get what you're trying to say bro"));
        assertTrue(response.contains("todo <desc>"));
        assertFalse(response.contains("!"));
    }

    @Test
    public void getResponse_duplicateTodo_returnsDuplicateWarning() {
        bro.getResponse("todo read book");
        String duplicateResponse = bro.getResponse("todo read book");
        assertTrue(duplicateResponse.contains("Bro, you already have that exact task in your list:"));
        assertTrue(duplicateResponse.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_duplicateDeadline_returnsDuplicateWarning() {
        bro.getResponse("deadline submit report /by 2026-10-15 1800");
        String duplicateResponse = bro.getResponse("deadline submit report /by 2026-10-15 1800");
        assertTrue(duplicateResponse.contains("Bro, you already have that exact task in your list:"));
    }

    @Test
    public void getResponse_duplicateEvent_returnsDuplicateWarning() {
        bro.getResponse("event party /from 2026-10-15 1800 /to 2026-10-15 2200");
        String duplicateResponse = bro.getResponse("event party /from 2026-10-15 1800 /to 2026-10-15 2200");
        assertTrue(duplicateResponse.contains("Bro, you already have that exact task in your list:"));
    }

    @Test
    public void getResponse_alreadyMarkedOrUnmarked_returnsFriendlyNotice() {
        bro.getResponse("todo read book");

        // Mark uncompleted task
        String markResponse = bro.getResponse("mark 1");
        assertTrue(markResponse.contains("Nice bro, I've marked this task as done"));

        // Mark already completed task
        String alreadyMarkedResponse = bro.getResponse("mark 1");
        assertEquals("Bro, that task is already marked as done:\n  [T][X] read book",
                alreadyMarkedResponse);

        // Unmark completed task
        String unmarkResponse = bro.getResponse("unmark 1");
        assertTrue(unmarkResponse.contains("That's tough bro, I've marked this task as not done yet"));

        // Unmark already uncompleted task
        String alreadyUnmarkedResponse = bro.getResponse("unmark 1");
        assertEquals("Bro, that task isn't marked as done yet anyway:\n  [T][ ] read book",
                alreadyUnmarkedResponse);
    }

    @Test
    public void getResponse_outOfBoundsTaskIndex_returnsContextualMessage() {
        // List is currently empty
        String emptyResponse = bro.getResponse("mark 1");
        assertEquals("Bro, your list is completely empty right now, so there's no task 1.",
                emptyResponse);

        bro.getResponse("todo read book");
        bro.getResponse("todo buy milk");

        // List has 2 items
        String outOfBoundsResponse = bro.getResponse("delete 5");
        assertEquals("Bro, task 5 doesn't exist. You have 2 tasks, so pick a number between 1 and 2.",
                outOfBoundsResponse);

        String negativeResponse = bro.getResponse("unmark 0");
        assertEquals("Bro, task 0 doesn't exist. You have 2 tasks, so pick a number between 1 and 2.",
                negativeResponse);
    }

    @Test
    public void getResponse_editEventEndBeforeStart_returnsChronologicalError() {
        bro.getResponse("event camp /from 2026-10-15 1000 /to 2026-10-15 1200");
        String response = bro.getResponse("edit 1 /to 2026-10-15 0900");
        assertTrue(response.contains("can't end before it even starts"));
    }

    @Test
    public void getResponse_editDuplicateTask_returnsDuplicateWarning() {
        bro.getResponse("todo read book");
        bro.getResponse("todo write essay");
        String editDuplicateResponse = bro.getResponse("edit 2 /desc read book");
        assertTrue(editDuplicateResponse.contains("already has those exact details"));
    }

    @Test
    public void getWelcome_corruptedLinesInSaveFile_appendsCorruptedLineCount() throws IOException {
        Files.writeString(saveFilePath, "T | 0 | clean room\nCORRUPTED LINE HERE\nANOTHER BAD LINE\n");
        Bro broWithCorruptedFile = new Bro(saveFilePath);
        String welcome = broWithCorruptedFile.getWelcome();
        assertTrue(welcome.contains("Heads up bro, skipped 2 corrupted line(s) in your save file."));
    }
}
