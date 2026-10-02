package floppy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParserTest {
    private Parser parser;

    @BeforeEach
    void setUp() {
        parser = new Parser();
    }

    @Test
    void parse_todoCommand_returnsAddCommand() throws FloppyException {
        Command command = parser.parse("todo read book");

        assertEquals(CommandType.ADD, command.getCommandType());
        assertInstanceOf(Todo.class, command.getTask());
        assertEquals("read book", command.getTask().getDescription());
    }

    @Test
    void parse_deadlineCommand_parsesIsoDate() throws FloppyException {
        Command command = parser.parse("deadline return book /by 2026-09-01");

        Deadline deadline = assertInstanceOf(Deadline.class, command.getTask());
        assertEquals(LocalDate.of(2026, 9, 1), deadline.getDueDate());
    }

    @Test
    void parse_eventWithReversedDates_throwsException() {
        assertThrows(FloppyException.class, () -> parser.parse(
                "event camp /from 2026-09-03 /to 2026-09-01"));
    }

    @Test
    void parse_periodCommand_returnsPeriodTask() throws FloppyException {
        Command command = parser.parse(
                "period collect certificate /from 2026-09-15 /to 2026-09-25");

        PeriodTask periodTask = assertInstanceOf(PeriodTask.class, command.getTask());
        assertEquals(LocalDate.of(2026, 9, 15), periodTask.getStartDate());
        assertEquals(LocalDate.of(2026, 9, 25), periodTask.getEndDate());
    }

    @Test
    void parse_periodWithReversedDates_throwsException() {
        assertThrows(FloppyException.class, () -> parser.parse(
                "period collect certificate /from 2026-09-25 /to 2026-09-15"));
    }

    @Test
    void parse_invalidDate_throwsException() {
        assertThrows(FloppyException.class, () -> parser.parse(
                "deadline return book /by 2026-02-30"));
    }

    @Test
    void parse_indexedCommand_convertsToZeroBasedIndex() throws FloppyException {
        Command command = parser.parse("mark 2");

        assertEquals(CommandType.MARK, command.getCommandType());
        assertEquals(1, command.getTaskIndex());
    }

    @Test
    void parse_unknownCommand_throwsException() {
        assertThrows(FloppyException.class, () -> parser.parse("dance"));
    }

    @Test
    void parse_findCommand_returnsKeyword() throws FloppyException {
        Command command = parser.parse("find project book");

        assertEquals(CommandType.FIND, command.getCommandType());
        assertEquals("project book", command.getKeyword());
    }

    @Test
    void parse_findWithoutKeyword_throwsException() {
        assertThrows(FloppyException.class, () -> parser.parse("find"));
    }

    @Test
    void parse_commandsWithoutTaskData_returnsExpectedTypes() throws FloppyException {
        assertEquals(CommandType.BYE, parser.parse("  bye  ").getCommandType());
        assertEquals(CommandType.LIST, parser.parse("list").getCommandType());
        assertEquals(CommandType.UNMARK, parser.parse("unmark 1").getCommandType());
        assertEquals(CommandType.DELETE, parser.parse("delete 1").getCommandType());
    }

    @Test
    void parse_incompleteDatedCommands_throwException() {
        assertThrows(FloppyException.class, () -> parser.parse("deadline submit report"));
        assertThrows(FloppyException.class, () -> parser.parse("event camp /from 2026-09-20"));
        assertThrows(FloppyException.class, () -> parser.parse("period collect /to 2026-09-21"));
    }

    @Test
    void parse_commandWordWithoutSeparator_throwsException() {
        assertThrows(FloppyException.class, () -> parser.parse("mark1"));
        assertThrows(FloppyException.class, () -> parser.parse("unmark2"));
        assertThrows(FloppyException.class, () -> parser.parse("delete3"));
    }

    @Test
    void parse_descriptionWithStorageSeparator_throwsException() {
        assertThrows(FloppyException.class, () -> parser.parse("todo read | write"));
        assertThrows(FloppyException.class, () -> parser.parse(
                "deadline read | write /by 2026-09-20"));
    }

    @Test
    void parse_duplicateDateMarkers_throwsException() {
        assertThrows(FloppyException.class, () -> parser.parse(
                "deadline submit /by 2026-09-20 /by 2026-09-21"));
        assertThrows(FloppyException.class, () -> parser.parse(
                "period collect /from 2026-09-20 /from 2026-09-21 /to 2026-09-22"));
    }
}
