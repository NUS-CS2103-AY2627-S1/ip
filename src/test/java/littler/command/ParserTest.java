package littler.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
//
//
//

import org.junit.jupiter.api.Test;

import littler.datetime.StringDateTimeConverter.ParsedDateTime;
import littler.exception.LittleRException;
import littler.task.Task;

public class ParserTest {

    // ---- parseTask: TODO ----

    @Test
    public void parseTask_validTodo_createsTodoWithCorrectName() throws LittleRException {
        Task task = Parser.parseTask("todo read book", Command.TODO);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void parseTask_emptyTodoDescription_throwsException() {
        assertThrows(LittleRException.class, () -> Parser.parseTask("todo", Command.TODO));
    }

    // ---- parseTask: DEADLINE ----

    @Test
    public void parseTask_validDeadline_createsDeadlineWithParsedDate() throws LittleRException {
        Task task = Parser.parseTask("deadline return book /by 2-12-2019 1800", Command.DEADLINE);
        assertEquals("[D][ ] return book (due: 2019-12-02 18:00)", task.toString());
    }

    @Test
    public void parseTask_deadlineMissingByKeyword_throwsException() {
        assertThrows(LittleRException.class, () ->
            Parser.parseTask("deadline return book", Command.DEADLINE));
    }

    @Test
    public void parseTask_deadlineWithInvalidDate_throwsException() {
        assertThrows(LittleRException.class, () ->
            Parser.parseTask("deadline return book /by notadate", Command.DEADLINE));
    }

    // ---- parseTask: EVENT ----

    @Test
    public void parseTask_validEvent_createsEventWithParsedDates() throws LittleRException {
        Task task = Parser.parseTask(
            "event project meeting /from 6-8-2026 1400 /to 6-8-2026 1600", Command.EVENT);
        assertEquals("[E][ ] project meeting (2026-08-06 14:00 to 2026-08-06 16:00)", task.toString());
    }

    @Test
    public void parseTask_eventMissingToClause_throwsException() {
        assertThrows(LittleRException.class, () ->
            Parser.parseTask("event meeting /from 6-8-2026 1400", Command.EVENT));
    }

    // ---- parseDate ----

    @Test
    public void parseDate_dateOnly_hasNoTimeComponent() throws LittleRException {
        ParsedDateTime result = Parser.parseDate("on 2-12-2019", Command.ON);
        assertFalse(result.hasTime());
        assertEquals("2019-12-02", result.toString());
    }

    @Test
    public void parseDate_dateWithTime_hasCorrectTimeComponent() throws LittleRException {
        ParsedDateTime result = Parser.parseDate("on 2-12-2019 1800", Command.ON);
        assertTrue(result.hasTime());
        assertEquals("2019-12-02 18:00", result.toString());
    }

    @Test
    public void parseDate_invalidFormat_throwsException() {
        assertThrows(LittleRException.class, () -> Parser.parseDate("on notadate", Command.ON));
    }
}
