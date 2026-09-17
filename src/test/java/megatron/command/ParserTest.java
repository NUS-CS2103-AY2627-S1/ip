package megatron.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import megatron.exception.EmptyCommandException;
import megatron.exception.EmptyDescriptionException;
import megatron.exception.InvalidTaskFormatException;
import megatron.exception.InvalidTaskNumberException;
import megatron.exception.MegatronException;
import megatron.exception.UnknownCommandException;
import megatron.task.Deadline;
import megatron.task.Event;
import megatron.task.Todo;

/**
 * Tests command parsing and task creation by {@link Parser}.
 */
class ParserTest {
    private final Parser parser = new Parser();

    @Test
    void parse_bye_returnsExitCommand() throws MegatronException {
        assertInstanceOf(ExitCommand.class, parser.parse("bye"));
    }

    @Test
    void parse_list_returnsListCommand() throws MegatronException {
        assertInstanceOf(ListCommand.class, parser.parse("list"));
    }

    @Test
    void parse_findWithKeyword_returnsFindCommand() throws MegatronException {
        assertInstanceOf(FindCommand.class, parser.parse("find book"));
    }

    @Test
    void parse_findWithoutKeyword_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.parse("find"));
    }

    @Test
    void parse_findWithBlankKeyword_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.parse("find   "));
    }

    @Test
    void parse_datetimeHelp_returnsDatetimeHelpCommand() throws MegatronException {
        assertInstanceOf(DatetimeHelpCommand.class, parser.parse("datetime-help"));
    }

    @Test
    void parse_mark_returnsMarkCommand() throws MegatronException {
        assertInstanceOf(MarkCommand.class, parser.parse("mark 2"));
    }

    @Test
    void parse_unmark_returnsUnmarkCommand() throws MegatronException {
        assertInstanceOf(UnmarkCommand.class, parser.parse("unmark   2"));
    }

    @Test
    void parse_delete_returnsDeleteCommand() throws MegatronException {
        assertInstanceOf(DeleteCommand.class, parser.parse("delete 2"));
    }

    @Test
    void parse_todoInput_returnsAddCommand() throws MegatronException {
        assertInstanceOf(AddCommand.class, parser.parse("todo buy milk"));
    }

    @Test
    void parse_deadlineInput_returnsAddCommand() throws MegatronException {
        assertInstanceOf(AddCommand.class, parser.parse("deadline submit report /by 2026-08-06"));
    }

    @Test
    void parse_eventInput_returnsAddCommand() throws MegatronException {
        assertInstanceOf(AddCommand.class, parser.parse("event meeting /from 1400 /to 1600"));
    }

    @Test
    void parse_emptyInput_throwsEmptyCommandException() {
        assertThrows(EmptyCommandException.class, () -> parser.parse(""));
    }

    @Test
    void parse_whitespaceInput_throwsEmptyCommandException() {
        assertThrows(EmptyCommandException.class, () -> parser.parse("   "));
    }

    @Test
    void parse_markWithTextNumber_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class, () -> parser.parse("mark abc"));
    }

    @Test
    void parse_markWithoutNumber_throwsInvalidTaskFormatException() {
        InvalidTaskFormatException exception = assertThrows(
                InvalidTaskFormatException.class, () -> parser.parse("mark "));
        assertEquals("Use: mark <task number>.", exception.getMessage());
    }

    @Test
    void parse_markWithoutArgument_throwsInvalidTaskFormatException() {
        InvalidTaskFormatException exception = assertThrows(
                InvalidTaskFormatException.class, () -> parser.parse("mark"));
        assertEquals("Use: mark <task number>.", exception.getMessage());
    }

    @Test
    void parse_unmarkWithoutArgument_throwsInvalidTaskFormatException() {
        InvalidTaskFormatException exception = assertThrows(
                InvalidTaskFormatException.class, () -> parser.parse("unmark"));
        assertEquals("Use: unmark <task number>.", exception.getMessage());
    }

    @Test
    void parse_unmarkWithoutNumber_throwsInvalidTaskFormatException() {
        InvalidTaskFormatException exception = assertThrows(
                InvalidTaskFormatException.class, () -> parser.parse("unmark "));
        assertEquals("Use: unmark <task number>.", exception.getMessage());
    }

    @Test
    void parse_unmarkWithDecimalNumber_throwsInvalidTaskNumberException() {
        assertThrows(InvalidTaskNumberException.class, () -> parser.parse("unmark 1.5"));
    }

    @Test
    void parse_deleteWithoutNumber_throwsInvalidTaskFormatException() {
        InvalidTaskFormatException exception = assertThrows(
                InvalidTaskFormatException.class, () -> parser.parse("delete"));
        assertEquals("Use: delete <task number>.", exception.getMessage());
    }

    @Test
    void parse_deleteWithBlankNumber_throwsInvalidTaskFormatException() {
        InvalidTaskFormatException exception = assertThrows(
                InvalidTaskFormatException.class, () -> parser.parse("delete "));
        assertEquals("Use: delete <task number>.", exception.getMessage());
    }

    @Test
    void parse_byeWithSuffix_returnsAddCommand() throws MegatronException {
        assertInstanceOf(AddCommand.class, parser.parse("bye now"));
    }

    @Test
    void parse_listWithSuffix_returnsAddCommand() throws MegatronException {
        assertInstanceOf(AddCommand.class, parser.parse("list now"));
    }

    @Test
    void parse_datetimeHelpWithSuffix_returnsAddCommand() throws MegatronException {
        assertInstanceOf(AddCommand.class, parser.parse("datetime-help now"));
    }

    @Test
    void parse_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    void createTask_validTodo_createsTodoWithTrimmedDescription() throws MegatronException {
        Todo todo = assertInstanceOf(Todo.class, parser.createTask("todo    buy milk   "));

        assertEquals("buy milk", todo.getDescription());
        assertEquals("T", todo.getTypeCode());
        assertEquals("", todo.getExtra());
    }

    @Test
    void createTask_validDeadline_createsDeadlineWithParsedDateTime() throws MegatronException {
        Deadline deadline = assertInstanceOf(Deadline.class,
                parser.createTask("deadline submit report /by 2026-08-06 1400"));

        assertEquals("submit report", deadline.getDescription());
        assertEquals("D", deadline.getTypeCode());
        assertEquals("2026-08-06T14:00", deadline.getExtra());
    }

    @Test
    void createTask_eventWithTimeOnlyEnd_usesStartDateForEnd() throws MegatronException {
        Event event = assertInstanceOf(Event.class,
                parser.createTask("event review /from 2026-08-06 1400 /to 4:30pm"));

        assertEquals("review", event.getDescription());
        assertEquals("E", event.getTypeCode());
        assertEquals("2026-08-06T14:00|2026-08-06T16:30", event.getExtra());
    }

    @Test
    void createTask_todoWithoutDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> parser.createTask("todo"));
    }

    @Test
    void createTask_whitespaceOnlyTodoDescription_throwsEmptyDescriptionException() {
        assertThrows(EmptyDescriptionException.class, () -> parser.createTask("todo   "));
    }

    @Test
    void createTask_deadlineWithoutDate_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.createTask("deadline report"));
    }

    @Test
    void createTask_deadlineWithoutArguments_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.createTask("deadline"));
    }

    @Test
    void createTask_deadlineWithoutDescription_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.createTask("deadline  /by 2026-08-06"));
    }

    @Test
    void createTask_deadlineWithoutDateAfterMarker_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.createTask("deadline report /by "));
    }

    @Test
    void createTask_deadlineWithInvalidDate_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.createTask("deadline report /by 31/2/2026"));
    }

    @Test
    void createTask_eventWithoutFromMarker_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.createTask("event review"));
    }

    @Test
    void createTask_eventWithoutArguments_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.createTask("event"));
    }

    @Test
    void createTask_eventWithoutToMarker_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.createTask("event review /from 2026-08-06 1400"));
    }

    @Test
    void createTask_eventWithoutDescription_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser
                .createTask("event  /from 2026-08-06 1400 /to 1600"));
    }

    @Test
    void createTask_eventWithoutStart_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.createTask("event review /from /to 1600"));
    }

    @Test
    void createTask_eventWithoutEnd_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser
                .createTask("event review /from 2026-08-06 1400 /to "));
    }

    @Test
    void createTask_eventWithInvalidStart_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, () -> parser.createTask("event review /from invalid /to 1600"));
    }

    @Test
    void createTask_eventWithInvalidEnd_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, (
        ) -> parser.createTask("event review /from 2026-08-06 1400 /to invalid"));
    }

    @Test
    void createTask_eventWithInvalidRange_throwsInvalidTaskFormatException() {
        assertThrows(InvalidTaskFormatException.class, (
        ) -> parser.createTask("event review /from 2026-08-06 1600 /to 2026-08-06 1400"));
    }

    @Test
    void createTask_unknownTaskType_throwsUnknownCommandException() {
        assertThrows(UnknownCommandException.class, () -> parser.createTask("reminder buy milk"));
    }

    @Test
    void createTask_nullInput_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.createTask(null));
    }
}
