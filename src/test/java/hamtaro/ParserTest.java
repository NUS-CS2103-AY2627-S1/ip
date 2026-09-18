package hamtaro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import hamtaro.command.Command;
import hamtaro.exception.HamtaroException;

/** Tests command parsing for valid commands and invalid user input. */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parseCommand_validTodo_returnsAddTaskCommand() throws HamtaroException {
        Command command = parser.parseCommand("todo read book");

        assertEquals("AddTaskCommand", command.getClass().getSimpleName());
        assertEquals(true, command.changesTasks());
    }

    @Test
    void parseCommand_validDeadline_returnsAddTaskCommand() throws HamtaroException {
        Command command = parser.parseCommand("deadline submit assignment /by 2026-09-10");

        assertEquals("AddTaskCommand", command.getClass().getSimpleName());
        assertEquals(true, command.changesTasks());
    }

    @Test
    void parseCommand_validEvent_returnsAddTaskCommand() throws HamtaroException {
        Command command = parser.parseCommand("event project meeting /from 01/09/2026 1400 /to 01/09/2026 1500");

        assertEquals("AddTaskCommand", command.getClass().getSimpleName());
        assertEquals(true, command.changesTasks());
    }

    @Test
    void parseCommand_validFind_returnsFindCommand() throws HamtaroException {
        Command command = parser.parseCommand("find project");

        assertEquals("FindCommand", command.getClass().getSimpleName());
        assertEquals(false, command.changesTasks());
    }

    @Test
    void parseCommand_validTag_returnsTagCommand() throws HamtaroException {
        Command command = parser.parseCommand("tag 1 #Fun");

        assertEquals("TagCommand", command.getClass().getSimpleName());
        assertEquals(true, command.changesTasks());
    }

    @Test
    void parseCommand_validTagFilter_returnsFindTagCommand() throws HamtaroException {
        Command command = parser.parseCommand("list #fun");

        assertEquals("FindTagCommand", command.getClass().getSimpleName());
        assertEquals(false, command.changesTasks());
    }

    @Test
    void parseCommand_blankInput_throwsException() {
        HamtaroException exception = assertThrows(HamtaroException.class, () -> parser.parseCommand("   "));

        assertEquals("Command doesn't exist!", exception.getMessage());
    }

    @Test
    void parseCommand_eventWithoutEndTime_throwsException() {
        HamtaroException exception = assertThrows(HamtaroException.class,
                () -> parser.parseCommand("event project meeting /from 01/09/2026 1400"));

        assertEquals("Invalid Argument Format! Usage: event [task description] /from [start] /to [end]",
                exception.getMessage());
    }

    @Test
    void parseCommand_findWithoutKeyword_throwsException() {
        HamtaroException exception = assertThrows(HamtaroException.class, () -> parser.parseCommand("find"));

        assertEquals("Invalid Argument Format! Usage: find [keyword]", exception.getMessage());
    }
}
