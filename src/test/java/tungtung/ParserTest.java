package tungtung;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/** Tests task creation and validation performed by {@link Parser}. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void identify_listCommand_returnsList() {
        assertEquals(Parser.CommandType.LIST, parser.identify("list"));
    }

    @Test
    void identify_findCommand_returnsFind() {
        assertEquals(Parser.CommandType.FIND, parser.identify("find book"));
    }

    @Test
    void identify_sortCommand_returnsSort() {
        assertEquals(Parser.CommandType.SORT, parser.identify("sort by deadline"));
    }

    @Test
    void identify_markCommand_returnsMark() {
        assertEquals(Parser.CommandType.MARK, parser.identify("mark"));
        assertEquals(Parser.CommandType.MARK, parser.identify("mark 1"));
    }

    @Test
    void identify_unmarkCommand_returnsUnmark() {
        assertEquals(Parser.CommandType.UNMARK, parser.identify("unmark"));
        assertEquals(Parser.CommandType.UNMARK, parser.identify("unmark 1"));
    }

    @Test
    void identify_deleteCommand_returnsDelete() {
        assertEquals(Parser.CommandType.DELETE, parser.identify("delete"));
        assertEquals(Parser.CommandType.DELETE, parser.identify("delete 1"));
    }

    @Test
    void identifyTaskCommand_returnsAdd() {
        assertEquals(Parser.CommandType.ADD, parser.identify("todo read book"));
        assertEquals(Parser.CommandType.ADD, parser.identify("unknown command"));
    }

    @Test
    void parseTask_todoCommand_createsTodo() throws TungTungException {
        Task task = parser.parseTask("todo read book");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void parseTask_deadlineCommand_createsDeadline() throws TungTungException {
        Task task = parser.parseTask("deadline return book /by 2019-10-15");

        assertEquals("[D][ ] return book (by: Oct 15 2019)", task.toString());
    }

    @Test
    void parseTask_eventCommand_createsEvent() throws TungTungException {
        Task task = parser.parseTask("event project meeting /from 2019-10-15 /to 2019-10-16");

        assertEquals("[E][ ] project meeting (from: Oct 15 2019 - to: Oct 16 2019)", task.toString());
    }

    @Test
    void parseTask_eventCommand_preservesDates() throws TungTungException {
        Event event = (Event) parser.parseTask("event meeting /from 2019-10-15 /to 2019-10-16");

        assertEquals(LocalDate.of(2019, 10, 15), event.from);
        assertEquals(LocalDate.of(2019, 10, 16), event.to);
    }

    @Test
    void parseTask_missingTodoDescription_exceptionThrown() {
        assertThrows(TungTungException.class, () -> parser.parseTask("todo"));
    }

    @Test
    void parseTask_missingDeadlineDetails_exceptionThrown() {
        assertThrows(TungTungException.class, () -> parser.parseTask("deadline"));
        assertThrows(TungTungException.class, () -> parser.parseTask("deadline return book"));
        assertThrows(TungTungException.class, () -> parser.parseTask("deadline return book /by "));
    }

    @Test
    void parseTask_invalidDate_exceptionThrown() {
        assertThrows(TungTungException.class,
                () -> parser.parseTask("deadline return book /by tomorrow"));
    }

    @Test
    void parseTask_reversedEventDates_exceptionThrown() {
        assertThrows(TungTungException.class,
                () -> parser.parseTask("event backwards /from 2019-10-16 /to 2019-10-15"));
    }

    @Test
    void parseTask_missingEventDetails_exceptionThrown() {
        assertThrows(TungTungException.class, () -> parser.parseTask("event project /from Monday"));
        assertThrows(TungTungException.class,
                () -> parser.parseTask("event project /from 2019-10-15 /to "));
    }

    @Test
    void parseTask_separatorInDescription_exceptionThrown() {
        assertThrows(TungTungException.class, () -> parser.parseTask("todo buy | sell"));
    }

    @Test
    void parseTask_unknownCommand_exceptionThrown() {
        assertThrows(TungTungException.class, () -> parser.parseTask("blah"));
    }
    @Test
    void parseTask_wrongEventLabels_rejectsCommand() {
        for (String labels : new String[] {" /to 2026-09-17 /from 2026-09-18",
                " /from 2026-09-17 /from 2026-09-18", " /to 2026-09-17 /to 2026-09-18",
                " /from 2026-09-17 /to 2026-09-18 /to 2026-09-19"}) {
            assertThrows(TungTungException.class, () -> parser.parseTask("event meeting" + labels));
        }
    }

    @Test
    void parseTask_trailingSeparator_rejectsUnsafeDescription() {
        assertThrows(TungTungException.class,
                () -> parser.parseTask("deadline report | /by 2026-09-18"));
        assertThrows(TungTungException.class,
                () -> parser.parseTask("event meeting | /from 2026-09-18 /to 2026-09-19"));
    }

    @Test
    void parseTask_impossibleDates_rejectsCommand() {
        for (String date : new String[] {"2025-02-29", "2026-04-31", "2026-00-01", "2026-13-01"}) {
            assertThrows(TungTungException.class, () -> parser.parseTask("deadline task /by " + date));
        }
    }

    @Test
    void parseTask_missingStartDate_rejectsWithoutCrashing() {
        assertThrows(TungTungException.class,
                () -> parser.parseTask("event meeting /from /to 2026-09-18"));
    }

    @Test
    void parseTask_generatedEventFragments_neverThrowsUncheckedException() {
        String[] fragments = {"", " ", "meeting", " /from", " /from ", " /to", " /to ", "2026-09-18"};
        for (String first : fragments) {
            for (String second : fragments) {
                for (String third : fragments) {
                    try {
                        parser.parseTask("event " + first + second + third);
                    } catch (TungTungException exception) {
                        // Invalid syntax must produce a user-facing error, never an unchecked exception.
                    }
                }
            }
        }
    }

}
