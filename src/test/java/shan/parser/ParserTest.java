package shan.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import shan.command.AddCommand;
import shan.command.DeleteCommand;
import shan.command.ExitCommand;
import shan.command.FindCommand;
import shan.command.ListCommand;
import shan.command.MarkCommand;
import shan.command.OnCommand;
import shan.command.UndoCommand;
import shan.command.UnmarkCommand;
import shan.exception.InvalidArgumentException;
import shan.exception.InvalidCommandException;
import shan.exception.MissingArgumentException;
import shan.exception.ShanException;
import shan.task.Deadline;
import shan.task.Event;
import shan.task.ToDo;

class ParserTest {
    @Test
    void parse_supportedCommands_returnsMatchingCommandTypes() throws ShanException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(UndoCommand.class, Parser.parse("undo"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(
                AddCommand.class,
                Parser.parse("deadline return book /by 2019-12-02 18:00"));
        assertInstanceOf(
                AddCommand.class,
                Parser.parse(
                        "event meeting /from 2019-12-02 14:00 /to 2019-12-02 16:00"));
    }

    @Test
    void parse_mixedCaseCommand_returnsMatchingCommandType() throws ShanException {
        assertInstanceOf(ListCommand.class, Parser.parse("LiSt"));
    }

    @Test
    void parse_blankCommand_exceptionThrown() {
        assertThrows(InvalidCommandException.class, () -> Parser.parse("   "));
    }

    @Test
    void parse_unknownCommand_exceptionThrown() {
        assertThrows(InvalidCommandException.class, () -> Parser.parse("search book"));
    }

    @Test
    void parse_findWithoutKeyword_exceptionThrown() {
        assertThrows(MissingArgumentException.class, () -> Parser.parse("find"));
    }

    @Test
    void parseTaskNumber_integer_returnsInteger() throws ShanException {
        assertEquals(12, Parser.parseTaskNumber("12"));
    }

    @Test
    void parseTaskNumber_blankArgument_exceptionThrown() {
        assertThrows(MissingArgumentException.class, () -> Parser.parseTaskNumber(" "));
    }

    @Test
    void parseTaskNumber_nonInteger_exceptionThrown() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parseTaskNumber("one"));
    }

    @Test
    void parseToDo_validDescription_returnsToDo() throws ShanException {
        ToDo task = Parser.parseToDo("read book");

        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    void parseToDo_blankDescription_exceptionThrown() {
        assertThrows(MissingArgumentException.class, () -> Parser.parseToDo(" "));
    }

    @Test
    void parseToDo_descriptionContainsFileDelimiter_exceptionThrown() {
        assertThrows(InvalidArgumentException.class, () -> Parser.parseToDo("read | book"));
    }

    @Test
    void parseDeadline_validArguments_returnsDeadline() throws ShanException {
        Deadline task = Parser.parseDeadline("return book /by 2019-12-02 18:00");

        assertEquals("D | 0 | return book | 2019-12-02 18:00", task.toFileString());
    }

    @Test
    void parseDeadline_missingByDelimiter_exceptionThrown() {
        assertThrows(
                MissingArgumentException.class, () -> Parser.parseDeadline("return book 2019-12-02 18:00"));
    }

    @Test
    void parseDeadline_missingRequiredField_exceptionThrown() {
        assertThrows(
                MissingArgumentException.class, () -> Parser.parseDeadline("return book /by "));
        assertThrows(
                MissingArgumentException.class, () -> Parser.parseDeadline(" /by 2019-12-02 18:00"));
    }

    @Test
    void parseDeadline_invalidDateTime_exceptionThrown() {
        assertThrows(
                InvalidArgumentException.class, () -> Parser.parseDeadline("return book /by 2019-02-29 18:00"));
    }

    @Test
    void parseDeadline_fieldContainsFileDelimiter_exceptionThrown() {
        assertThrows(
                InvalidArgumentException.class, () -> Parser.parseDeadline("return | book /by 2019-12-02 18:00"));
    }

    @Test
    void parseEvent_validArguments_returnsEvent() throws ShanException {
        Event task = Parser.parseEvent(
                "project meeting /from 2019-12-02 14:00 /to 2019-12-02 16:00");

        assertEquals(
                "E | 0 | project meeting | 2019-12-02 14:00 | 2019-12-02 16:00",
                task.toFileString());
    }

    @Test
    void parseEvent_missingFromDelimiter_exceptionThrown() {
        assertThrows(
                MissingArgumentException.class, () -> Parser.parseEvent(
                        "project meeting 2019-12-02 14:00 /to 2019-12-02 16:00"));
    }

    @Test
    void parseEvent_missingToDelimiter_exceptionThrown() {
        assertThrows(
                MissingArgumentException.class, () -> Parser.parseEvent(
                        "project meeting /from 2019-12-02 14:00 2019-12-02 16:00"));
    }

    @Test
    void parseEvent_missingRequiredField_exceptionThrown() {
        assertThrows(
                MissingArgumentException.class, () -> Parser.parseEvent(
                        "project meeting /from /to 2019-12-02 16:00"));
        assertThrows(
                MissingArgumentException.class, () -> Parser.parseEvent(
                        "project meeting /from 2019-12-02 14:00 /to "));
    }

    @Test
    void parseEvent_endNotAfterStart_exceptionThrown() {
        assertThrows(
                InvalidArgumentException.class, () -> Parser.parseEvent(
                        "project meeting /from 2019-12-02 16:00 /to 2019-12-02 16:00"));
        assertThrows(
                InvalidArgumentException.class, () -> Parser.parseEvent(
                        "project meeting /from 2019-12-02 16:00 /to 2019-12-02 14:00"));
    }

    @Test
    void parseEvent_fieldContainsFileDelimiter_exceptionThrown() {
        assertThrows(
                InvalidArgumentException.class, () -> Parser.parseEvent(
                        "project | meeting /from 2019-12-02 14:00 /to 2019-12-02 16:00"));
    }

    @Test
    void parseDateRange_singleDate_returnsSameStartAndEnd() throws ShanException {
        Parser.DateRange result = Parser.parseDateRange("2019-12-02");

        assertEquals(LocalDate.of(2019, 12, 2), result.startDate());
        assertEquals(LocalDate.of(2019, 12, 2), result.endDate());
        assertFalse(result.isRange());
    }

    @Test
    void parseDateRange_validRange_returnsInclusiveDates() throws ShanException {
        Parser.DateRange result = Parser.parseDateRange("2019-12-02 /to 2019-12-05");

        assertEquals(LocalDate.of(2019, 12, 2), result.startDate());
        assertEquals(LocalDate.of(2019, 12, 5), result.endDate());
        assertTrue(result.isRange());
    }

    @Test
    void parseDateRange_blankArgument_exceptionThrown() {
        assertThrows(MissingArgumentException.class, () -> Parser.parseDateRange(" "));
    }

    @Test
    void parseDateRange_missingRangeDate_exceptionThrown() {
        assertThrows(
                MissingArgumentException.class, () -> Parser.parseDateRange("2019-12-02 /to "));
        assertThrows(
                MissingArgumentException.class, () -> Parser.parseDateRange(" /to 2019-12-05"));
    }

    @Test
    void parseDateRange_endBeforeStart_exceptionThrown() {
        assertThrows(
                InvalidArgumentException.class, () -> Parser.parseDateRange("2019-12-05 /to 2019-12-02"));
    }

    @Test
    void parseDateRange_invalidDate_exceptionThrown() {
        assertThrows(
                InvalidArgumentException.class, () -> Parser.parseDateRange("2019-02-29"));
    }
}
