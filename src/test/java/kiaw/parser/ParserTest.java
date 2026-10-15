package kiaw.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import kiaw.exception.KiawException;

public class ParserTest {

    @Test
    public void parse_todoCommand_parsesDescription() throws KiawException {
        ParsedCommand command = Parser.parse("todo read book");

        assertEquals("todo", command.getCommandType());
        assertEquals("read book", command.getDescription());
    }

    @Test
    public void parse_deadlineCommand_parsesDescriptionAndDate() throws KiawException {
        ParsedCommand command = Parser.parse("deadline submit report /by 2026-09-15");

        assertEquals("deadline", command.getCommandType());
        assertEquals("submit report", command.getDescription());
        assertEquals(LocalDate.of(2026, 9, 15), command.getDate());
    }

    @Test
    public void parse_eventCommand_parsesDescriptionAndDates() throws KiawException {
        ParsedCommand command = Parser.parse(
                "event conference /from 2026-09-20 /to 2026-09-22");

        assertEquals("event", command.getCommandType());
        assertEquals("conference", command.getDescription());
        assertEquals(LocalDate.of(2026, 9, 20), command.getFrom());
        assertEquals(LocalDate.of(2026, 9, 22), command.getTo());
    }

    @Test
    public void parse_markCommand_parsesTaskNumber() throws KiawException {
        ParsedCommand command = Parser.parse("mark 3");

        assertEquals("mark", command.getCommandType());
        assertEquals(3, command.getTaskNumber());
    }

    @Test
    public void parse_deleteCommand_parsesTaskNumber() throws KiawException {
        ParsedCommand command = Parser.parse("delete 2");

        assertEquals("delete", command.getCommandType());
        assertEquals(2, command.getTaskNumber());
    }

    @Test
    public void parse_invalidDeadlineDate_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse(
                "deadline homework /by Friday"));
    }

    @Test
    public void parse_invalidTaskNumber_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse("delete abc"));
    }

    @Test
    public void parse_eventEndBeforeStart_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse(
                "event holiday /from 2026-11-10 /to 2026-11-01"));
    }

    @Test
    public void parse_emptyTodo_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse("todo"));
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse("blah"));
    }

    @Test
    public void parse_findCommand_parsesKeyword() throws KiawException {
        ParsedCommand command = Parser.parse("find book");

        assertEquals("find", command.getCommandType());
        assertEquals("book", command.getDescription());
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse("find"));
    }

    @Test
    public void parse_sortCommand_parsesCommand() throws KiawException {
        ParsedCommand command = Parser.parse("sort");

        assertEquals("sort", command.getCommandType());
    }

    @Test
    public void parse_sortWithArgument_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse("sort date"));
    }

    @Test
    public void parse_commandWithSurroundingSpaces_commandParsed()
            throws KiawException {

        ParsedCommand command = Parser.parse("   list   ");

        assertEquals("list", command.getCommandType());
    }

    @Test
    public void parse_markWithMultipleSpaces_taskNumberParsed()
            throws KiawException {

        ParsedCommand command = Parser.parse("mark      2");

        assertEquals("mark", command.getCommandType());
        assertEquals(2, command.getTaskNumber());
    }

    @Test
    public void parse_emptyInput_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse("   "));
    }

    @Test
    public void parse_nullInput_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse(null));
    }

    @Test
    public void parse_deadlineWithDuplicateBy_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse(
                "deadline homework /by 2026-10-10 /by 2026-11-10"));
    }

    @Test
    public void parse_eventWithDuplicateFrom_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse(
                "event meeting /from 2026-10-01 "
                        + "/from 2026-10-02 /to 2026-10-03"));
    }

    @Test
    public void parse_eventWithDuplicateTo_exceptionThrown() {
        assertThrows(KiawException.class, () -> Parser.parse(
                "event meeting /from 2026-10-01 "
                        + "/to 2026-10-02 /to 2026-10-03"));
    }
}
