package kiwi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for {@link Kiwi#getResponse(String)}.
 */
class KiwiTest {

    @TempDir
    Path tempDir;

    private Kiwi kiwi;

    @BeforeEach
    void setUp() {
        kiwi = new Kiwi(tempDir.resolve("kiwi.txt").toString());
    }

    @Test
    void getResponse_todo_returnsAddedConfirmation() {
        String response = kiwi.getResponse("todo read book");
        assertEquals(
                "Got it. I've added this task:\n"
                        + "  [T][ ] read book\n"
                        + "Now you have 1 task in the list.",
                response);
    }

    @Test
    void getResponse_unknownCommand_returnsErrorMessage() {
        String response = kiwi.getResponse("jump");
        assertEquals(
                "Hmm, Kiwi doesn't recognize that. Try todo, deadline, event, on, find, free, list, "
                        + "mark, unmark, delete, or bye.",
                response);
        assertTrue(kiwi.isError());
        assertFalse(kiwi.isExit());
    }

    @Test
    void getResponse_todo_doesNotSetIsError() {
        kiwi.getResponse("todo read book");
        assertFalse(kiwi.isError());
    }

    @Test
    void getResponse_listAfterAdd_includesTheTask() {
        kiwi.getResponse("todo read book");
        String response = kiwi.getResponse("list");
        assertTrue(response.contains("1.[T][ ] read book"));
        assertFalse(kiwi.isExit());
    }

    @Test
    void getResponse_bye_setsIsExitAndReturnsGoodbye() {
        String response = kiwi.getResponse("bye");
        assertEquals("Bye. Hope to see you again soon!", response);
        assertTrue(kiwi.isExit());
        assertFalse(kiwi.isError());
    }

    @Test
    void getResponse_freeSlot_returnsNearestEventFreeWorkDay() {
        kiwi.getResponse("event conference /from 2019-12-01 /to 2019-12-03");
        String response = kiwi.getResponse("free 4 /from 2019-12-01");
        assertEquals("The nearest 4-hour free slot is on Dec 04 2019, 08:00-12:00.", response);
    }

    @Test
    void getResponse_freeSlotWhenFullyBooked_returnsNotFound() {
        kiwi.getResponse("event long trip /from 2019-01-01 /to 2021-12-31");
        String response = kiwi.getResponse("free 4 /from 2019-12-01");
        assertEquals("Couldn't find a 4-hour free slot in the next 365 days.", response);
    }

    @Test
    void getResponse_blankInput_returnsPromptToTypeACommand() {
        String response = kiwi.getResponse("   ");
        assertEquals("Please type a command, e.g. todo borrow book", response);
        assertTrue(kiwi.isError());
    }

    @Test
    void getResponse_duplicateTodo_returnsAlreadyInList() {
        kiwi.getResponse("todo read book");
        String response = kiwi.getResponse("todo read book");
        assertEquals("That task is already in your list.", response);
        assertTrue(kiwi.isError());
        assertEquals("Here are the tasks in your list:\n1.[T][ ] read book", kiwi.getResponse("list"));
    }

    @Test
    void getResponse_impossibleDate_returnsCalendarDateError() {
        String response = kiwi.getResponse("deadline return book /by 2019-02-30");
        assertEquals(
                "2019-02-30 is not a real calendar date. Please use a date as yyyy-MM-dd, e.g. 2019-12-02",
                response);
        assertTrue(kiwi.isError());
    }

    @Test
    void getResponse_doesNotPrintToStandardOutput() {
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(captured));
        try {
            kiwi.getResponse("todo read book");
            kiwi.getResponse("list");
        } finally {
            System.setOut(originalOut);
        }
        assertEquals("", captured.toString());
    }
}
