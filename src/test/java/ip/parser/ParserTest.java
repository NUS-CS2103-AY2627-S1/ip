package ip.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import ip.exception.TabbyException;
import ip.model.Deadline;
import ip.model.Event;

class ParserTest {

    @Test
    void parseTaskIndex_validCommand_returnsZeroBasedIndex() throws TabbyException {
        assertEquals(1, Parser.parseTaskIndex("delete 2", 3));
    }

    @Test
    void parseTaskIndex_invalidIndex_throwsException() {
        assertThrows(TabbyException.class, () -> Parser.parseTaskIndex("delete", 3));
        assertThrows(TabbyException.class, () -> Parser.parseTaskIndex("delete abc", 3));
        assertThrows(TabbyException.class, () -> Parser.parseTaskIndex("delete 4", 3));
        assertThrows(TabbyException.class, () -> Parser.parseTaskIndex("delete 0", 3));
    }

    @Test
    void parseTodo_validCommand_createsTodo() throws TabbyException {
        assertEquals("[T][ ] buy milk", Parser.parseTodo("todo buy milk").toString());
    }

    @Test
    void parseTodo_emptyDescription_throwsException() {
        assertThrows(TabbyException.class, () -> Parser.parseTodo("todo"));
        assertThrows(TabbyException.class, () -> Parser.parseTodo("todo   "));
    }

    @Test
    void parseDeadline_validDate_createsDeadline() throws TabbyException {
        Deadline deadline = Parser.parseDeadline("deadline submit report /by 2026-09-01");
        assertEquals("[D][ ] submit report (by: Sep 01 2026)", deadline.toString());
    }

    @Test
    void parseDeadline_missingByClause_throwsException() {
        assertThrows(TabbyException.class, () -> Parser.parseDeadline("deadline submit report"));
        assertThrows(TabbyException.class, () -> Parser.parseDeadline("deadline submit report /by"));
    }

    @Test
    void parseEvent_validDateTimes_createsEvent() throws TabbyException {
        Event event = Parser.parseEvent("event meeting /from 2026-09-01 0900 /to 2026-09-01 1000");
        assertEquals("[E][ ] meeting (from: Sep 01 2026, 9:00AM to: Sep 01 2026, 10:00AM)",
                event.toString());
    }

    @Test
    void parseEvent_missingTimeClause_throwsException() {
        assertThrows(TabbyException.class, () -> Parser.parseEvent("event meeting /from 2026-09-01 0900"));
        assertThrows(TabbyException.class, () -> Parser.parseEvent("event meeting /to 2026-09-01 1000"));
    }

    @Test
    void parseDateTime_supportedFormats_returnExpectedValues() throws TabbyException {
        assertEquals("Sep 01 2026", Parser.parseDateTime("2026-09-01").toString());
        assertEquals("Sep 01 2026, 9:00AM", Parser.parseDateTime("1/9/2026 0900").toString());
    }

    @Test
    void parseDateTime_invalidFormat_throwsException() {
        assertThrows(TabbyException.class, () -> Parser.parseDateTime("tomorrow"));
    }
}
