package axiom.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import axiom.AxiomException;
import axiom.command.Command;
import axiom.task.Deadline;
import axiom.task.Event;
import axiom.task.Todo;

class ParserTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Test
    void getCommand_validInput_returnsCommand() {
        assertEquals(Command.LIST, parser.getCommand("list"));
        assertEquals(Command.SORT, parser.getCommand("sort"));
        assertEquals(Command.TODO, parser.getCommand("todo read book"));
    }

    @Test
    void getCommand_unknownInput_returnsUnknown() {
        assertEquals(Command.UNKNOWN, parser.getCommand("blah"));
    }

    @Test
    void parseTaskNumber_validNumber_returnsOneBasedIndex() throws AxiomException {
        assertEquals(2, parser.parseTaskNumber(Command.MARK, "mark 2", 3));
    }

    @Test
    void parseTaskNumber_missingArgument_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTaskNumber(Command.MARK, "mark", 3));
        assertEquals("Please specify which task to mark. Usage: mark <task number>",
                exception.getMessage());
    }

    @Test
    void parseTaskNumber_nonNumericArgument_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTaskNumber(Command.DELETE, "delete abc", 3));
        assertEquals("'abc' is not a valid task number. Usage: delete <task number>",
                exception.getMessage());
    }

    @Test
    void parseTaskNumber_outOfRangeArgument_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTaskNumber(Command.UNMARK, "unmark 99", 2));
        assertEquals("That task number isn't in your list. You currently have 2 task(s).",
                exception.getMessage());
    }

    @Test
    void parseTaskNumber_zeroTaskCount_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTaskNumber(Command.MARK, "mark 1", 0));
        assertEquals("That task number isn't in your list. You currently have 0 task(s).",
                exception.getMessage());
    }

    @Test
    void parseTodo_validInput_returnsTodo() throws AxiomException {
        Todo todo = parser.parseTodo("todo read book");
        assertEquals("read book", todo.getDescription());
    }

    @Test
    void parseTodo_emptyDescription_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTodo("todo"));
        assertEquals("A todo needs a description. Usage: todo <description>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_validInput_returnsDeadline() throws AxiomException {
        Deadline deadline = parser.parseDeadline("deadline return book /by 2019-06-06");
        assertEquals("return book", deadline.getDescription());
    }

    @Test
    void parseDeadline_missingBy_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline homework"));
        assertEquals("A deadline must include /by. Usage: deadline <description> /by <time>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_emptyInput_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline"));
        assertEquals("A deadline needs a description and a /by time. "
                        + "Usage: deadline <description> /by <time>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_missingDescription_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline /by 2019-06-06"));
        assertEquals("A deadline needs a description. Usage: deadline <description> /by <time>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_missingByTime_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline homework /by"));
        assertEquals("A deadline needs a /by time. Usage: deadline <description> /by <time>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_invalidDate_exceptionThrown() {
        assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline homework /by no idea"));
    }

    @Test
    void parseEvent_validInput_returnsEvent() throws AxiomException {
        Event event = parser.parseEvent(
                "event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600");
        assertEquals("project meeting", event.getDescription());
    }

    @Test
    void parseEvent_emptyInput_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent("event"));
        assertEquals("An event needs a description, /from, and /to times. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingFromAndTo_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent("event meeting"));
        assertEquals("An event must include /from and /to. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingDescription_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent("event /from 2019-08-06 1400 /to 2019-08-06 1600"));
        assertEquals("An event needs a description. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingFromTime_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent("event meeting /from  /to 2019-08-06 1600"));
        assertEquals("An event needs a /from time. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }

    @Test
    void parseEvent_missingToTime_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent("event meeting /from 2019-08-06 1400 /to"));
        assertEquals("An event needs a /to time. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }

    @Test
    void parseFindKeyword_validInput_returnsKeyword() throws AxiomException {
        assertEquals("book", parser.parseFindKeyword("find book"));
    }

    @Test
    void parseFindKeyword_emptyKeyword_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseFindKeyword("find"));
        assertEquals("A find needs a keyword. Usage: find <keyword>",
                exception.getMessage());
    }

    @Test
    void getCommand_leadingAndTrailingSpaces_returnsCommand() {
        assertEquals(Command.LIST, parser.getCommand("  list  "));
        assertEquals(Command.TODO, parser.getCommand("   todo   read book"));
    }

    @Test
    void parseTodo_extraInternalSpaces_collapsesWhitespace() throws AxiomException {
        Todo todo = parser.parseTodo("todo   read   book  ");
        assertEquals("read book", todo.getDescription());
    }

    @Test
    void parseTodo_pipeInDescription_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTodo("todo read | book"));
        assertEquals("Task descriptions cannot contain '|'.", exception.getMessage());
    }

    @Test
    void parseDeadline_repeatedByFlag_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline homework /by 2019-06-06 /by 2019-07-01"));
        assertEquals("The /by time is specified more than once. Usage: deadline <description> /by <time>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_unexpectedFromFlag_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseDeadline("deadline homework /from 2019-06-06"));
        assertEquals("Unexpected flag '/from'. Usage: deadline <description> /by <time>",
                exception.getMessage());
    }

    @Test
    void parseDeadline_extraSpacesAroundFlag_parsesSuccessfully() throws AxiomException {
        Deadline deadline = parser.parseDeadline("deadline   return book   /by   2019-06-06");
        assertEquals("return book", deadline.getDescription());
    }

    @Test
    void parseEvent_repeatedToFlag_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent(
                        "event meeting /from 2019-08-06 1400 /to 2019-08-06 1600 /to 2019-08-06 1700"));
        assertEquals("The /to time is specified more than once. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }

    @Test
    void parseEvent_fromAfterToFlag_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent(
                        "event meeting /to 2019-08-06 1600 /from 2019-08-06 1400"));
        assertEquals("Please specify /from before /to. "
                        + "Usage: event <description> /from <start> /to <end>",
                exception.getMessage());
    }

    @Test
    void parseEvent_startNotBeforeEnd_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent(
                        "event meeting /from 2019-08-06 1600 /to 2019-08-06 1400"));
        assertEquals("The event /from time must be earlier than the /to time.",
                exception.getMessage());
    }

    @Test
    void parseEvent_sameStartAndEnd_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseEvent(
                        "event meeting /from 2019-08-06 /to 2019-08-06"));
        assertEquals("The event /from time must be earlier than the /to time.",
                exception.getMessage());
    }

    @Test
    void parseTaskNumber_extraArgument_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTaskNumber(Command.MARK, "mark 1 extra", 3));
        assertEquals("'1 extra' is not a valid task number. Usage: mark <task number>",
                exception.getMessage());
    }

    @Test
    void parseTaskNumber_negativeNumber_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.parseTaskNumber(Command.DELETE, "delete -1", 3));
        assertEquals("'-1' is not a valid task number. Usage: delete <task number>",
                exception.getMessage());
    }

    @Test
    void requireNoArguments_withExtraText_exceptionThrown() {
        AxiomException exception = assertThrows(AxiomException.class,
                () -> parser.requireNoArguments(Command.LIST, "list extra"));
        assertEquals("list does not take any arguments. Usage: list", exception.getMessage());
    }

    @Test
    void requireNoArguments_exactKeyword_succeeds() throws AxiomException {
        parser.requireNoArguments(Command.SORT, "  sort  ");
    }
}
