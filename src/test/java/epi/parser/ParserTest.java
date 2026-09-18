package epi.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import epi.exception.EpiException;
import epi.task.Deadline;
import epi.task.Event;
import epi.task.Task;
import epi.task.TaskList;
import epi.task.Todo;

class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseInput_commandWithArgument_splitsOnlyAtFirstWhitespace() {
        String[] parts = parser.parseInput("todo read a book");

        assertEquals("todo", parts[0]);
        assertEquals("read a book", parts[1]);
    }

    @Test
    void parseTodo_validDescription_createsTodoTask() throws EpiException {
        Task task = parser.parseTodo(" borrow book ");

        assertEquals("[T][ ] borrow book", task.toString());
    }

    @Test
    void parseDeadline_validDateTime_createsFormattedDeadline() throws EpiException {
        Deadline deadline = parser.parseDeadline("return book /by 2019-12-02 1800");

        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00 PM)", deadline.toString());
    }

    @Test
    void parseEvent_validDateTimes_createsFormattedEvent() throws EpiException {
        Event event = parser.parseEvent("project meeting /from 2019-12-02 1400 /to 2019-12-02 1600");

        assertEquals("[E][ ] project meeting (from: Dec 02 2019, 2:00 PM to: Dec 02 2019, 4:00 PM)", event.toString());
    }

    @Test
    void parseDeadline_missingByMarker_throwsEpiException() {
        assertThrows(EpiException.class, () -> parser.parseDeadline("return book"));
    }

    @Test
    void parseEvent_missingToMarker_throwsEpiException() {
        assertThrows(EpiException.class, () -> parser.parseEvent(
                "project meeting /from 2019-12-02 1400"));
    }

    @Test
    void parseTaskIndex_validOneBasedIndex_returnsZeroBasedIndex() throws EpiException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(0, parser.parseTaskIndex("1", tasks));
    }

    @Test
    void parseInput_whitespaceAndCommandOnly_preservesExistingSplittingRules() {
        assertArrayEquals(new String[]{"list"}, parser.parseInput("  list  "));
        assertArrayEquals(new String[]{"todo", "Read Book"}, parser.parseInput("  todo\t Read Book  "));
    }

    @Test
    void parseTodo_emptyDescription_throwsEpiException() {
        assertThrows(EpiException.class, () -> parser.parseTodo(""));
    }

    @Test
    void parseDeadline_missingFields_throwsEpiException() {
        assertThrows(EpiException.class, () -> parser.parseDeadline(""));
        assertThrows(EpiException.class, () -> parser.parseDeadline(" /by 2019-12-02 1800"));
        assertThrows(EpiException.class, () -> parser.parseDeadline("return book /by "));
    }

    @Test
    void parseEvent_missingFields_throwsEpiException() {
        assertThrows(EpiException.class, () -> parser.parseEvent(""));
        assertThrows(EpiException.class, () -> parser.parseEvent("meeting"));
        assertThrows(EpiException.class, () -> parser.parseEvent(" /from 2019-12-02 1400 /to 2019-12-02 1600"));
        assertThrows(EpiException.class, () -> parser.parseEvent("meeting /from  /to 2019-12-02 1600"));
        assertThrows(EpiException.class, () -> parser.parseEvent("meeting /from 2019-12-02 1400 /to "));
    }

    @Test
    void parseTaskIndex_outsideList_throwsEpiException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(EpiException.class, () -> parser.parseTaskIndex("0", tasks));
        assertThrows(EpiException.class, () -> parser.parseTaskIndex("-1", tasks));
        assertThrows(EpiException.class, () -> parser.parseTaskIndex("2", tasks));
        assertThrows(EpiException.class, () -> parser.parseTaskIndex("1", new TaskList()));
    }

    @Test
    void parseTaskIndex_missingOrNonNumeric_reportsExistingErrors() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(EpiException.class, () -> parser.parseTaskIndex("", tasks));
        assertThrows(NumberFormatException.class, () -> parser.parseTaskIndex("abc", tasks));
        assertThrows(NumberFormatException.class, () -> parser.parseTaskIndex("2147483648", tasks));
    }

    @Test
    void parseDeadline_extraWhitespace_acceptsFieldsWithoutChangingDescription() throws EpiException {
        Deadline task = parser.parseDeadline("  Read  Book\t /BY\t2020-02-29   1800  ");
        assertEquals("D | 0 | Read  Book | 2020-02-29 1800", task.toFileFormat());
    }

    @Test
    void parseEvent_extraWhitespace_acceptsFieldsInTheRequiredOrder() throws EpiException {
        Event task = parser.parseEvent(" Meet  friends\t/FROM\t2020-02-29  2300\t/TO  2020-03-01\t0100 ");
        assertEquals("E | 0 | Meet  friends | 2020-02-29 2300 | 2020-03-01 0100", task.toFileFormat());
    }

    @Test
    void parseDeadline_repeatedOrIncorrectMarkers_throwsUsageError() {
        for (String argument : List.of("book /by 2020-01-01 1200 /by 2020-01-02 1200",
                "book /from 2020-01-01 1200", "book /by 2020-01-01 1200 /to 2020-01-02 1200",
                "book /by", "/by 2020-01-01 1200", "book /by2020-01-01 1200")) {
            assertEquals("Invalid format! Use: deadline <task> /by <time>",
                    assertThrows(EpiException.class, () -> parser.parseDeadline(argument)).getMessage(), argument);
        }
    }

    @Test
    void parseEvent_repeatedReorderedOrEmptyParameters_throwsUsageError() {
        for (String argument : List.of("meeting /to 2020-01-01 1200 /from 2020-01-01 1100",
                "meeting /from 2020-01-01 1100 /from 2020-01-01 1130 /to 2020-01-01 1200",
                "meeting /from 2020-01-01 1100 /to 2020-01-01 1200 /to 2020-01-01 1300",
                "meeting /from /to 2020-01-01 1200", "meeting /from 2020-01-01 1100 /to",
                "meeting /by 2020-01-01 1100 /to 2020-01-01 1200")) {
            assertEquals("Invalid format! Use: event <task> /from <start> /to <end>",
                    assertThrows(EpiException.class, () -> parser.parseEvent(argument)).getMessage(), argument);
        }
    }

    @Test
    void parseTasks_whitespaceOnlyDescriptions_throwsExistingEmptyErrors() {
        assertThrows(EpiException.class, () -> parser.parseTodo(" \t "));
        assertThrows(EpiException.class, () -> parser.parseDeadline(" \t "));
        assertThrows(EpiException.class, () -> parser.parseEvent(" \t "));
    }

    @Test
    void parseTaskIndex_limitsAndExtraValues_doNotOverflowOrIgnoreArguments() throws EpiException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("book"));
        assertEquals(0, parser.parseTaskIndex(" 1 ", tasks));
        for (String number : List.of("-2147483648", "2147483647", "0", "-1")) {
            assertThrows(EpiException.class, () -> parser.parseTaskIndex(number, tasks));
        }
        for (String number : List.of("99999999999999999999", "1 2", "1.0", "one")) {
            assertThrows(NumberFormatException.class, () -> parser.parseTaskIndex(number, tasks));
        }
    }
}
