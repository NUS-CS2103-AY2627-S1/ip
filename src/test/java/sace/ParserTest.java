package sace;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests command recognition and extraction of command arguments.
 */
class ParserTest {
    @Test
    void parseCommandType_helpCommand_returnsHelpCommandType() throws SaceException {
        assertEquals(Parser.CommandType.HELP, Parser.parseCommandType("help"));
    }

    @Test
    void parseCommandType_findCommand_returnsFindCommandType() throws SaceException {
        assertEquals(Parser.CommandType.FIND, Parser.parseCommandType("find book"));
    }

    @Test
    void parseCommandType_mixedCaseCommandWithTab_returnsMatchingCommandType()
            throws SaceException {
        assertEquals(
                Parser.CommandType.DEADLINE,
                Parser.parseCommandType("DeAdLiNe\tsubmit report"));
    }

    @Test
    void parseCommandType_allSupportedCommands_returnsMatchingTypes()
            throws SaceException {
        assertEquals(Parser.CommandType.BYE, Parser.parseCommandType("bye"));
        assertEquals(Parser.CommandType.HELP, Parser.parseCommandType("help"));
        assertEquals(Parser.CommandType.LIST, Parser.parseCommandType("list"));
        assertEquals(Parser.CommandType.MARK, Parser.parseCommandType("mark 1"));
        assertEquals(Parser.CommandType.UNMARK, Parser.parseCommandType("unmark 1"));
        assertEquals(Parser.CommandType.DELETE, Parser.parseCommandType("delete 1"));
        assertEquals(Parser.CommandType.FIND, Parser.parseCommandType("find report"));
        assertEquals(Parser.CommandType.TODO, Parser.parseCommandType("todo read"));
        assertEquals(
                Parser.CommandType.DEADLINE,
                Parser.parseCommandType("deadline report /by 2026-09-30"));
        assertEquals(
                Parser.CommandType.EVENT,
                Parser.parseCommandType("event class /from 2pm /to 4pm"));
    }

    @Test
    void parseCommandType_blankCommand_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseCommandType("  \t  "));

        assertEquals("Your command scroll is blank. Enter an order.", exception.getMessage());
    }

    @Test
    void parseCommandType_unknownCommandOrUnexpectedArguments_throwsException() {
        SaceException unknownException = assertThrows(SaceException.class, () ->
                Parser.parseCommandType("dance"));
        SaceException extraArgumentException = assertThrows(SaceException.class, () ->
                Parser.parseCommandType("list now"));

        assertEquals(
                "That order is not recorded in the battle manual.",
                unknownException.getMessage());
        assertEquals(
                "That order is not recorded in the battle manual.",
                extraArgumentException.getMessage());
    }

    @Test
    void parseTask_validTodo_returnsTodo() throws SaceException {
        Task task = Parser.parseTask("todo   revise notes  ", Parser.CommandType.TODO);

        Todo todo = assertInstanceOf(Todo.class, task);
        assertEquals("revise notes", todo.getDescription());
    }

    @Test
    void parseTask_todoWithoutDescription_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseTask("todo", Parser.CommandType.TODO));

        assertEquals("Every quest needs a description after todo.", exception.getMessage());
    }

    @Test
    void parseTask_deadlineWithFlexibleSpacing_returnsDeadline() throws SaceException {
        Task task = Parser.parseTask(
                "DEADLINE submit report   /BY   2026-09-30",
                Parser.CommandType.DEADLINE);

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 30), deadline.getBy());
    }

    @Test
    void parseTask_deadlineWithDuplicateBy_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseTask(
                        "deadline report /by 2026-09-30 /by 2026-10-01",
                        Parser.CommandType.DEADLINE));

        assertEquals("A deadline accepts only one /by parameter.", exception.getMessage());
    }

    @Test
    void parseTask_deadlineWithMissingFields_throwsSpecificExceptions() {
        SaceException missingMarkerException = assertThrows(SaceException.class, () ->
                Parser.parseTask("deadline report", Parser.CommandType.DEADLINE));
        SaceException missingDescriptionException = assertThrows(SaceException.class, () ->
                Parser.parseTask("deadline /by 2026-09-30", Parser.CommandType.DEADLINE));
        SaceException missingDateException = assertThrows(SaceException.class, () ->
                Parser.parseTask("deadline report /by", Parser.CommandType.DEADLINE));

        assertEquals(
                "Use this formation: deadline DESCRIPTION /by yyyy-MM-dd.",
                missingMarkerException.getMessage());
        assertEquals(
                "Every deadline quest needs a description.",
                missingDescriptionException.getMessage());
        assertEquals(
                "A deadline needs a battle date after /by.",
                missingDateException.getMessage());
    }

    @Test
    void parseTask_deadlineWithInvalidCalendarDate_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseTask(
                        "deadline report /by 2026-02-30",
                        Parser.CommandType.DEADLINE));

        assertEquals(
                "Use a valid battle date in yyyy-MM-dd format, for example 2026-08-31.",
                exception.getMessage());
    }

    @Test
    void parseTask_validEvent_returnsEvent() throws SaceException {
        Task task = Parser.parseTask(
                "EVENT project meeting  /FROM  Monday 2pm  /TO  Monday 4pm",
                Parser.CommandType.EVENT);

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals("Monday 2pm", event.getFrom());
        assertEquals("Monday 4pm", event.getTo());
    }

    @Test
    void parseTask_eventWithDuplicateTo_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseTask(
                        "event meeting /from Monday /to Tuesday /to Wednesday",
                        Parser.CommandType.EVENT));

        assertEquals("An event accepts only one /to parameter.", exception.getMessage());
    }

    @Test
    void parseTask_eventWithDuplicateFrom_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseTask(
                        "event meeting /from Monday /from Tuesday /to Wednesday",
                        Parser.CommandType.EVENT));

        assertEquals("An event accepts only one /from parameter.", exception.getMessage());
    }

    @Test
    void parseTask_eventWithSameStartAndEnd_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseTask(
                        "event meeting /from Monday 2pm /to monday 2PM",
                        Parser.CommandType.EVENT));

        assertEquals(
                "An event must have different start and end times.",
                exception.getMessage());
    }

    @Test
    void parseTask_eventWithMissingOrReorderedMarkers_throwsException() {
        SaceException missingMarkerException = assertThrows(SaceException.class, () ->
                Parser.parseTask("event meeting /from Monday", Parser.CommandType.EVENT));
        SaceException reorderedMarkerException = assertThrows(SaceException.class, () ->
                Parser.parseTask(
                        "event meeting /to Tuesday /from Monday",
                        Parser.CommandType.EVENT));

        assertEquals(
                "Use this formation: event DESCRIPTION /from START /to END.",
                missingMarkerException.getMessage());
        assertEquals(
                "Use this formation: event DESCRIPTION /from START /to END.",
                reorderedMarkerException.getMessage());
    }

    @Test
    void parseTask_eventWithMissingValues_throwsSpecificExceptions() {
        SaceException missingDescriptionException = assertThrows(SaceException.class, () ->
                Parser.parseTask("event /from Monday /to Tuesday", Parser.CommandType.EVENT));
        SaceException missingStartException = assertThrows(SaceException.class, () ->
                Parser.parseTask("event meeting /from /to Tuesday", Parser.CommandType.EVENT));
        SaceException missingEndException = assertThrows(SaceException.class, () ->
                Parser.parseTask("event meeting /from Monday /to", Parser.CommandType.EVENT));

        assertEquals(
                "Every event quest needs a description.",
                missingDescriptionException.getMessage());
        assertEquals(
                "An event needs a starting time after /from.",
                missingStartException.getMessage());
        assertEquals(
                "An event needs an ending time after /to.",
                missingEndException.getMessage());
    }

    @Test
    void parseTask_nonCreationCommand_violatesDocumentedAssumption() {
        assertThrows(AssertionError.class, () ->
                Parser.parseTask("help", Parser.CommandType.HELP));
    }

    @Test
    void parseFindKeyword_validCommand_returnsTrimmedKeyword() throws SaceException {
        assertEquals("project meeting", Parser.parseFindKeyword("find   project meeting  "));
    }

    @Test
    void parseFindKeyword_missingKeyword_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseFindKeyword("find"));

        assertEquals(
                "The scouts need a keyword to begin their search.",
                exception.getMessage());
    }

    @Test
    void parseTaskIndex_validTaskNumbers_returnsZeroBasedIndexes() throws SaceException {
        assertEquals(0, Parser.parseTaskIndex("mark 1", "mark", 3));
        assertEquals(1, Parser.parseTaskIndex("unmark   2", "unmark", 3));
        assertEquals(2, Parser.parseTaskIndex("delete 3", "delete", 3));
    }

    @Test
    void parseTaskIndex_missingTaskNumber_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseTaskIndex("mark", "mark", 3));

        assertEquals("Name a quest number after mark.", exception.getMessage());
    }

    @Test
    void parseTaskIndex_nonNumericTaskNumber_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseTaskIndex("delete two", "delete", 3));

        assertEquals(
                "The quest number for delete must be a whole number.",
                exception.getMessage());
    }

    @Test
    void parseTaskIndex_numberOutsideIntegerRange_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseTaskIndex("delete 999999999999999999999", "delete", 3));

        assertEquals(
                "The quest number for delete must be a whole number.",
                exception.getMessage());
    }

    @Test
    void parseTaskIndex_emptyTaskList_throwsException() {
        SaceException exception = assertThrows(SaceException.class, () ->
                Parser.parseTaskIndex("unmark 1", "unmark", 0));

        assertEquals(
                "Your quest log is empty; there is nothing to unmark.",
                exception.getMessage());
    }

    @Test
    void parseTaskIndex_outOfRangeTaskNumbers_throwsException() {
        SaceException belowRangeException = assertThrows(SaceException.class, () ->
                Parser.parseTaskIndex("mark 0", "mark", 3));
        SaceException aboveRangeException = assertThrows(SaceException.class, () ->
                Parser.parseTaskIndex("mark 4", "mark", 3));

        assertEquals(
                "Choose a quest number between 1 and 3.",
                belowRangeException.getMessage());
        assertEquals(
                "Choose a quest number between 1 and 3.",
                aboveRangeException.getMessage());
    }
}
