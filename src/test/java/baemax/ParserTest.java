package baemax;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests for {@link Parser}, which turns command text into tasks and task numbers. */
public class ParserTest {

    // ---- parseTask: todo ----

    @Test
    public void parseTask_todo_buildsPendingTodo() throws BaemaxException {
        assertEquals("[T][ ] read book", Parser.parseTask("todo read book").toString());
    }

    @Test
    public void parseTask_todoWithoutDescription_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTask("todo"));
        assertThrows(BaemaxException.class, () -> Parser.parseTask("todo    "));
    }

    // ---- parseTask: deadline ----

    @Test
    public void parseTask_deadline_buildsDeadlineWithParsedDate() throws BaemaxException {
        assertEquals("[D][ ] return book (by: Oct 15 2019)",
                Parser.parseTask("deadline return book /by 2019-10-15").toString());
    }

    @Test
    public void parseTask_deadlineWithoutBy_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTask("deadline return book"));
    }

    @Test
    public void parseTask_deadlineWithoutDescription_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTask("deadline  /by 2019-10-15"));
    }

    @Test
    public void parseTask_deadlineWithUnparseableDate_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTask("deadline return book /by someday"));
    }

    // ---- parseTask: event ----

    @Test
    public void parseTask_event_buildsEventWithParsedDates() throws BaemaxException {
        assertEquals("[E][ ] camp (from: Aug 05 2019 to: Aug 07 2019)",
                Parser.parseTask("event camp /from 2019-08-05 /to 2019-08-07").toString());
    }

    @Test
    public void parseTask_eventWithoutTo_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTask("event camp /from 2019-08-05"));
    }

    @Test
    public void parseTask_eventWithoutFrom_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTask("event camp /to 2019-08-07"));
    }

    @Test
    public void parseTask_eventEndsBeforeItStarts_throwsBaemaxException() {
        String command = "event camp /from 2019-08-07 /to 2019-08-05";
        assertThrows(BaemaxException.class, () -> Parser.parseTask(command));
    }

    @Test
    public void parseTask_eventEndsAtSameDayEarlierTime_throwsBaemaxException() {
        String command = "event camp /from 2019-08-05 1800 /to 2019-08-05 0900";
        assertThrows(BaemaxException.class, () -> Parser.parseTask(command));
    }

    // ---- parseTask: unknown ----

    @Test
    public void parseTask_unknownCommandWord_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTask("sleep 8 hours"));
    }

    // ---- parseTaskNumber ----

    @Test
    public void parseTaskNumber_validNumberInRange_returnsNumber() throws BaemaxException {
        assertEquals(2, Parser.parseTaskNumber("mark 2", "mark", 3));
        assertEquals(1, Parser.parseTaskNumber("delete 1", "delete", 1));
    }

    @Test
    public void parseTaskNumber_missingNumber_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTaskNumber("mark", "mark", 3));
    }

    @Test
    public void parseTaskNumber_nonNumeric_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTaskNumber("mark two", "mark", 3));
    }

    @Test
    public void parseTaskNumber_extraTokens_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTaskNumber("mark 1 2", "mark", 3));
    }

    @Test
    public void parseTaskNumber_belowRange_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTaskNumber("mark 0", "mark", 3));
    }

    @Test
    public void parseTaskNumber_aboveRange_throwsBaemaxException() {
        assertThrows(BaemaxException.class, () -> Parser.parseTaskNumber("mark 4", "mark", 3));
    }

    @Test
    public void parseTaskNumber_emptyList_anyNumberOutOfRange() {
        assertThrows(BaemaxException.class, () -> Parser.parseTaskNumber("mark 1", "mark", 0));
    }

    @Test
    public void parseTaskNumber_emptyList_explainsTheListIsEmpty() {
        BaemaxException exception =
                assertThrows(BaemaxException.class, () -> Parser.parseTaskNumber("mark 1", "mark", 0));
        assertEquals("Your task list is empty, so there's nothing to mark.", exception.getMessage());
    }
}
