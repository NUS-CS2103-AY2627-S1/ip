package mary.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import mary.command.AddCommand;
import mary.command.DeleteCommand;
import mary.command.ErrorCommand;
import mary.command.FindCommand;
import mary.command.ListCommand;
import mary.command.MarkCommand;
import mary.command.OnCommand;
import mary.command.SortCommand;
import mary.command.UnknownCommand;
import mary.exception.MaryException;
import mary.task.Deadline;
import mary.task.Event;
import mary.task.Task;
import mary.task.Todo;

/**
 * Verifies command dispatch and task syntax independently of console I/O.
 */
class ParserTest {
    /**
     * Checks the exact sort word, whitespace and invalid arguments without matching longer words.
     */
    @Test
    void parse_sortCommand_dispatchesOnlyExactWord() {
        for (String input : List.of("sort", "sort ", "sort\t", "sort descending")) {
            assertInstanceOf(SortCommand.class, Parser.parse(input));
        }
        for (String input : List.of("sorter", "sortbook", "SORT")) {
            assertInstanceOf(UnknownCommand.class, Parser.parse(input));
        }
    }

    /**
     * Checks that extracted parsers retain whitespace handling and Unicode descriptions.
     */
    @Test
    void parseTask_paddedDatedArguments_preservesDescriptionAndDates() throws MaryException {
        Task deadline = Parser.parseTask("deadline   return café book   /by   2/12/2019 1800  ");
        assertEquals("D | 0 | return café book | 2019-12-02T18:00", deadline.toStorageRecord());
        Task event = Parser.parseTask("event   book club   /from   2/12/2019 1400   /to   2/12/2019 1600  ");
        assertEquals("E | 0 | book club | 2019-12-02T14:00 | 2019-12-02T16:00", event.toStorageRecord());
    }

    /**
     * Checks command-word boundaries and supported whitespace around search text.
     */
    @Test
    void parse_findCommands_dispatchesOnlyExactCommandWord() {
        for (String input : new String[] {"find", "find ", "find book", "find\tbook"}) {
            assertInstanceOf(FindCommand.class, Parser.parse(input), input);
        }
        for (String input : new String[] {"finder", "findbook", "FIND book"}) {
            assertInstanceOf(UnknownCommand.class, Parser.parse(input), input);
        }
    }

    /**
     * Tests parse: supported commands: returns matching command.
     */
    @Test
    void parse_supportedCommands_returnsMatchingCommand() {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline read /by 2/12/2019 1800"));
        assertInstanceOf(AddCommand.class, Parser.parse("event read /from x /to y"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2/12/2019"));
        assertTrue(Parser.parse("bye").isExit());
        assertFalse(Parser.parse("list").isExit());
    }

    /**
     * Tests parse: unknown or blank: returns unknown command.
     */
    @Test
    void parse_unknownOrBlank_returnsUnknownCommand() {
        for (String input : new String[] {"", "   ", "blah", "LIST"}) {
            assertInstanceOf(UnknownCommand.class, Parser.parse(input), input);
        }
        assertInstanceOf(ErrorCommand.class, Parser.parse("list extra"));
    }

    /**
     * Tests parse task: todo: trims outer whitespace and preserves description.
     */
    @Test
    void parseTask_todo_trimsOuterWhitespaceAndPreservesDescription() throws MaryException {
        Task task = Parser.parseTask("todo   read  café book   ");
        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] read  café book", task.toString());
    }

    /**
     * Tests parse task: deadline: parses day before month.
     */
    @Test
    void parseTask_deadline_parsesDayBeforeMonth() throws MaryException {
        Deadline task = assertInstanceOf(Deadline.class,
                Parser.parseTask("deadline return book /by 2/12/2019 1800"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), task.getBy());
    }

    /**
     * Tests parse task: event: preserves both dates.
     */
    @Test
    void parseTask_event_preservesBothDates() throws MaryException {
        Event task = assertInstanceOf(Event.class,
                Parser.parseTask("event camp /from 2/12/2019 1400 /to 4/12/2019 1600"));
        assertEquals(LocalDateTime.of(2019, 12, 2, 14, 0), task.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 4, 16, 0), task.getTo());
    }

    /**
     * Tests parse task: missing todo description: explains correction.
     */
    @Test
    void parseTask_missingTodoDescription_explainsCorrection() {
        MaryException error = assertThrows(MaryException.class, () -> Parser.parseTask("todo   "));
        assertEquals("please add a task description after 'todo'.", error.getMessage());
    }

    /**
     * Tests parse task: malformed deadline: rejects missing fields.
     */
    @Test
    void parseTask_malformedDeadline_rejectsMissingFields() {
        String[] inputs = {"deadline read", "deadline /by 2/12/2019 1800",
            "deadline read /by "};
        for (String input : inputs) {
            MaryException error = assertThrows(MaryException.class, () -> Parser.parseTask(input), input);
            assertEquals("use 'deadline description /by date or time'.", error.getMessage());
        }
    }

    /**
     * Tests parse task: malformed event: rejects missing or reversed markers.
     */
    @Test
    void parseTask_malformedEvent_rejectsMissingOrReversedMarkers() {
        String[] inputs = {"event camp", "event camp /from 2/12/2019 1400",
            "event camp /to 2/12/2019 1600 /from 2/12/2019 1400",
            "event /from 2/12/2019 1400 /to 2/12/2019 1600"};
        for (String input : inputs) {
            assertThrows(MaryException.class, () -> Parser.parseTask(input), input);
        }
    }

    /**
     * Tests parse task: invalid date time: reports accepted format.
     */
    @Test
    void parseTask_invalidDateTime_reportsAcceptedFormat() {
        assertTrue(assertThrows(MaryException.class, () ->
                Parser.parseTask("deadline read /by tomorrow")).getMessage().contains("d/M/yyyy HHmm"));
        assertTrue(assertThrows(MaryException.class, () ->
                Parser.parseTask("event camp /from tomorrow /to later"))
                .getMessage().contains("d/M/yyyy HHmm"));
    }

    /**
     * Tests parse task: unknown task command: throws mary exception.
     */
    @Test
    void parseTask_unknownTaskCommand_throwsMaryException() {
        assertThrows(MaryException.class, () -> Parser.parseTask("read book"));
    }
}
