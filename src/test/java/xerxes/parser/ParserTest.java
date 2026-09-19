package xerxes.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import xerxes.storage.TaskStorage;
import xerxes.task.TaskList;

/**
 * Tests date parsing performed by {@link Parser}.
 */

class ParserTest {
    /** Verifies that the short to-do alias preserves the complete task description. */
    @Test
    void handleCommand_todoAlias_addsCompleteDescription() {
        Parser parser = new Parser(new TaskStorage("unused-test-path"));
        TaskList tasks = new TaskList();

        CommandResult result = parser.handleCommand("t hehee", tasks);

        assertFalse(result.isError());
        assertEquals("1: [T][ ] hehee", tasks.toString());
    }

    /** Verifies that a correctly formatted date is converted to the expected value. */
    @Test
    void formatDate_validDate_returnsParsedDate() {
        assertEquals(
                LocalDate.of(2026, 8, 24),
                Parser.formatDate("24/8/2026")
        );
    }

    /** Verifies that single-digit days and months are accepted. */
    @Test
    void formatDate_singleDigitDayAndMonth_returnsParsedDate() {
        assertEquals(
                LocalDate.of(2026, 3, 2),
                Parser.formatDate("2/3/2026")
        );
    }

    /** Verifies that dates outside the accepted input format are rejected. */
    @Test
    void formatDate_wrongFormat_throwsException() {
        assertThrows(
                IllegalArgumentException.class, () -> Parser.formatDate("2026-03-02")
        );
    }

    /** Verifies that impossible calendar dates are rejected. */
    @Test
    void formatDate_invalidCalendarDate_throwsException() {
        assertThrows(
                IllegalArgumentException.class, () -> Parser.formatDate("31/2/2026")
        );
    }

    /** Verifies that an empty date input is rejected. */
    @Test
    void formatDate_emptyInput_throwsException() {
        assertThrows(
                IllegalArgumentException.class, () -> Parser.formatDate("")
        );
    }

    /** Verifies that an event can start and end on the same date. */
    @Test
    void handleCommand_eventWithSameStartAndEndDate_addsEvent() {
        Parser parser = new Parser(new TaskStorage("unused-test-path"));
        TaskList tasks = new TaskList();

        CommandResult result = parser.handleCommand(
                "event meeting /from 2/3/2026 /to 2/3/2026", tasks);

        assertFalse(result.isError());
        assertEquals("1: [E][ ] meeting (from: Mar 02 2026 to: Mar 02 2026 )", tasks.toString());
    }

    /** Verifies that an event cannot end before its start date. */
    @Test
    void handleCommand_eventEndingBeforeStart_returnsError() {
        Parser parser = new Parser(new TaskStorage("unused-test-path"));
        TaskList tasks = new TaskList();

        CommandResult result = parser.handleCommand(
                "event meeting /from 3/3/2026 /to 2/3/2026", tasks);

        assertTrue(result.isError());
        assertEquals("Eh, an event cannot end before it starts ah.", result.getMessage());
        assertEquals("", tasks.toString());
    }
}
