package panda.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import panda.exception.ErrorType;
import panda.exception.PandaException;
import panda.model.CommandType;
import panda.model.Deadline;
import panda.model.Event;

class ParserTest {

    @Test
    void getCommandType_knownCommands_returnsMatchingType() {
        assertEquals(CommandType.TODO, Parser.getCommandType("todo read"));
        assertEquals(CommandType.DEADLINE, Parser.getCommandType("deadline report /by 2026-10-20"));
        assertEquals(CommandType.EVENT, Parser.getCommandType("event meeting /from 2026-10-20 /to 2026-10-21"));
        assertEquals(CommandType.LIST, Parser.getCommandType("list"));
        assertEquals(CommandType.MARK, Parser.getCommandType("mark 1"));
        assertEquals(CommandType.UNMARK, Parser.getCommandType("unmark 1"));
        assertEquals(CommandType.DELETE, Parser.getCommandType("delete 1"));
        assertEquals(CommandType.FIND, Parser.getCommandType("find book"));
        assertEquals(CommandType.BYE, Parser.getCommandType("bye"));
    }

    @Test
    void getCommandType_nullAndNearMissCommands_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, Parser.getCommandType(null));
        assertEquals(CommandType.UNKNOWN, Parser.getCommandType("todoish"));
        assertEquals(CommandType.UNKNOWN, Parser.getCommandType("list now"));
    }

    @Test
    void createTask_validCommands_returnsCorrectTaskSubtypes() throws PandaException {
        assertEquals("[T][ ] read book", Parser.createTask("todo read book").toString());
        assertEquals("[D][ ] report (by: Oct 20 2026)",
                ((Deadline) Parser.createTask("deadline report /by 2026-10-20")).toString());
        assertEquals("[E][ ] meeting (from: Oct 20 2026 9:00 AM to: Oct 20 2026 10:00 AM)",
                ((Event) Parser.createTask("event meeting /from 2026-10-20 0900 /to 2026-10-20 1000")).toString());
    }

    @Test
    void createTask_malformedParameters_throwsSpecificErrors() {
        assertError(ErrorType.UNEXPECTED_PARAMETER, "todo read /by tomorrow");
        assertError(ErrorType.UNEXPECTED_PARAMETER, "deadline report /from tomorrow /by 2026-10-20");
        assertError(ErrorType.DUPLICATE_PARAMETER,
                "event meeting /from 2026-10-20 0900 /to 2026-10-20 1000 /to 2026-10-20 1100");
        assertError(ErrorType.MISSING_EVENT_TIMES, "event meeting /to 2026-10-20 1000 /from 2026-10-20 0900");
        assertError(ErrorType.INVALID_DESCRIPTION, "deadline a | b /by 2026-10-20");
    }

    @Test
    void createTask_duplicateDeadlineParameter_throwsTypedException() {
        PandaException exception = assertThrows(PandaException.class,
                () -> Parser.createTask("deadline report /by 2026-10-20 /by 2026-10-21"));

        assertEquals(ErrorType.DUPLICATE_PARAMETER, exception.getErrorType());
    }

    @Test
    void getTaskIndex_multipleNumbers_throwsTypedException() {
        PandaException exception = assertThrows(PandaException.class,
                () -> Parser.getTaskIndex("mark 1 2", "mark", 2));

        assertEquals(ErrorType.NON_NUMERIC_TASK_NUMBER, exception.getErrorType());
    }

    @Test
    void validateCommandFormat_tabSeparatedCommand_throwsTypedException() {
        PandaException exception = assertThrows(PandaException.class,
                () -> Parser.validateCommandFormat("todo\tread book"));

        assertEquals(ErrorType.INVALID_COMMAND_FORMAT, exception.getErrorType());
    }

    @Test
    void getTaskIndex_validAndInvalidValues_returnsIndexOrTypedError() throws PandaException {
        assertEquals(1, Parser.getTaskIndex("delete 2", "delete", 3));
        assertError(ErrorType.MISSING_TASK_NUMBER, () -> Parser.getTaskIndex("delete", "delete", 3));
        assertError(ErrorType.NON_NUMERIC_TASK_NUMBER, () -> Parser.getTaskIndex("delete 1.5", "delete", 3));
        assertError(ErrorType.TASK_NOT_FOUND, () -> Parser.getTaskIndex("delete -1", "delete", 3));
        assertError(ErrorType.NON_NUMERIC_TASK_NUMBER,
                () -> Parser.getTaskIndex("delete 999999999999999999", "delete", 3));
    }

    @Test
    void findMarker_matchesOnlyWhitespaceBoundedMarkers() {
        assertEquals(5, Parser.findMarker("task /by tomorrow", "/by"));
        assertEquals(-1, Parser.findMarker("task/by tomorrow", "/by"));
        assertEquals(-1, Parser.findMarker("task /bypass tomorrow", "/by"));
    }

    private void assertError(ErrorType expectedError, String command) {
        assertError(expectedError, () -> Parser.createTask(command));
    }

    private void assertError(ErrorType expectedError, ThrowingAction action) {
        PandaException exception = assertThrows(PandaException.class, action::run);

        assertEquals(expectedError, exception.getErrorType());
    }

    @FunctionalInterface
    private interface ThrowingAction {
        void run() throws PandaException;
    }
}
