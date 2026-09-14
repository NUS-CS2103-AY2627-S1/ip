package penguin.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import penguin.command.AddCommand;
import penguin.command.Command;
import penguin.command.FindCommand;
import penguin.command.OnCommand;
import penguin.command.UndoCommand;
import penguin.exception.PenguinException;

/**
 * Tests command and saved-task parsing behavior.
 */
class ParserTest {
    @Test
    void parseUndo_validCommand_returnsUndoCommand() throws PenguinException {
        assertInstanceOf(UndoCommand.class, Parser.parseCommand("undo"));
    }

    @Test
    void parseUndo_extraArgument_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("undo now"));
    }

    @Test
    void parseUndo_caseInsensitive_returnsUndoCommand() throws PenguinException {
        assertInstanceOf(UndoCommand.class, Parser.parseCommand("UNDO"));
    }

    @Test
    void parseTodo_validCommand_returnsAddCommand() throws PenguinException {
        Command command = Parser.parseCommand("todo read book");

        assertInstanceOf(AddCommand.class, command);
    }

    @Test
    void parseTodo_reservedDelimiter_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("todo read | book"));
    }

    @Test
    void parseUnknownCommand_invalidInput_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("unknown command"));
    }

    @Test
    void parseCommand_nullInput_throwsPenguinException() {
        assertThrows(PenguinException.class, () -> Parser.parseCommand(null));
    }

    @Test
    void parseTaskCommand_nullInput_throwsPenguinException() {
        PenguinException exception = assertThrows(PenguinException.class,
                () -> TaskCommandParser.parse(null));

        assertEquals("Please input a task.", exception.getMessage());
    }

    @Test
    void parseDeadline_validCommand_returnsAddCommand() throws PenguinException {
        assertInstanceOf(AddCommand.class,
                Parser.parseCommand("deadline submit report /by 2099-12-31 1800"));
    }

    @Test
    void parseDeadline_reservedDelimiter_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand(
                        "deadline submit | report /by 2099-12-31 1800"));
    }

    @Test
    void parseEvent_validCommand_returnsAddCommand() throws PenguinException {
        assertInstanceOf(AddCommand.class,
                Parser.parseCommand("event meeting /from 2099-12-31 1400"
                        + " /to 2099-12-31 1600"));
    }

    @Test
    void parseEvent_reservedDelimiter_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("event project | meeting"
                        + " /from 2099-12-31 1400 /to 2099-12-31 1600"));
    }

    @Test
    void parseOn_validCommand_returnsOnCommand() throws PenguinException {
        assertInstanceOf(OnCommand.class,
                Parser.parseCommand("on 2099-12-31"));
    }

    @Test
    void parseFind_validKeyword_returnsFindCommand() throws PenguinException {
        assertInstanceOf(FindCommand.class,
                Parser.parseCommand("find read book"));
    }

    @Test
    void parseFind_repeatedWhitespace_returnsFindCommand()
            throws PenguinException {
        assertInstanceOf(FindCommand.class,
                Parser.parseCommand("find   read   book"));
    }

    @Test
    void parseFind_missingKeyword_throwsException() {
        PenguinException exception = assertThrows(PenguinException.class,
                () -> Parser.parseCommand("find"));

        assertEquals("Please enter a keyword.", exception.getMessage());
    }

    @Test
    void parseDeadline_pastDate_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("deadline report /by 2000-01-01 1800"));
    }

    @Test
    void parseTodo_surroundingWhitespace_returnsAddCommand()
            throws PenguinException {
        assertInstanceOf(AddCommand.class,
                Parser.parseCommand("  todo   read book  "));
    }

    @Test
    void parseTodo_repeatedDescriptionWhitespace_returnsNormalizedTask()
            throws PenguinException {
        assertEquals("[T][ ] read book",
                TaskCommandParser.parse("todo read    book").toString());
    }

    @Test
    void parseDeadline_duplicateBySeparators_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("deadline report /by 2099-12-31 1800"
                        + " /by 2099-12-31 1900"));
    }

    @Test
    void parseEvent_duplicateFromSeparator_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("event meeting /from 2099-12-31 1400"
                        + " /from 2099-12-31 1500 /to 2099-12-31 1600"));
    }

    @Test
    void parseEvent_duplicateToSeparator_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("event meeting /from 2099-12-31 1400"
                        + " /to 2099-12-31 1600 /to 2099-12-31 1700"));
    }

    @Test
    void parseSavedTask_malformedData_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseSavedTask("D | 0 | report"));
    }

    @Test
    void parseMark_nonNumericIndex_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("mark abc"));
    }

    @Test
    void parseCommand_missingArgument_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("delete"));
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("on"));
    }

    @Test
    void parseCommand_unexpectedArgument_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("list now"));
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("bye now"));
    }

    @Test
    void parseEvent_endBeforeStart_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("event meeting /from 2099-12-31 1800"
                        + " /to 2099-12-31 1400"));
    }

    @Test
    void parseEvent_equalStartAndEnd_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseCommand("event meeting /from 2099-12-31 1800"
                        + " /to 2099-12-31 1800"));
    }

    @Test
    void parseSavedTask_completedStatus_restoresCompletedState() throws PenguinException {
        assertEquals("[T][X] read book",
                Parser.parseSavedTask("T | 1 | read book").toString());
    }

    @Test
    void parseSavedTask_invalidStatus_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseSavedTask("T | 2 | read book"));
    }

    @Test
    void parseSavedTask_unknownType_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseSavedTask("X | 0 | read book"));
    }

    @Test
    void parseSavedTask_blankDescription_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseSavedTask("T | 0 | "));
    }

    @Test
    void parseSavedTask_extraFields_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseSavedTask("T | 0 | read book | unexpected"));
    }

    @Test
    void parseSavedTask_flexibleDelimiterSpacing_returnsTask() throws PenguinException {
        assertEquals("[T][ ] read book",
                Parser.parseSavedTask("T|0|read book").toString());
    }

    @Test
    void parseSavedTask_invalidDateTime_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseSavedTask("D | 0 | submit report | 2099-13-31 1800"));
    }

    @Test
    void parseSavedTask_eventEndBeforeStart_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseSavedTask("E | 0 | meeting | 2099-12-31 1800"
                        + " | 2099-12-31 1400"));
    }

    @Test
    void parseSavedTask_eventEqualStartAndEnd_throwsException() {
        assertThrows(PenguinException.class,
                () -> Parser.parseSavedTask("E | 0 | meeting | 2099-12-31 1800"
                        + " | 2099-12-31 1800"));
    }

    @Test
    void parseSavedTask_pastEvent_returnsEvent() throws PenguinException {
        assertEquals("[E][ ] meeting (from: 1 Jan 2000, 2:00PM to: 1 Jan 2000, 4:00PM)",
                Parser.parseSavedTask("E | 0 | meeting | 2000-01-01 1400"
                        + " | 2000-01-01 1600").toString());
    }
}
