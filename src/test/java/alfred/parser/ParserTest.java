package alfred.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import alfred.command.AddCommand;
import alfred.command.Command;
import alfred.command.DeleteCommand;
import alfred.command.ExitCommand;
import alfred.command.FindCommand;
import alfred.command.ListCommand;
import alfred.command.MarkCommand;
import alfred.command.SortCommand;
import alfred.command.UnmarkCommand;
import alfred.exception.AlfredException;
import alfred.storage.Storage;
import alfred.task.Deadline;
import alfred.task.Event;
import alfred.task.Task;
import alfred.task.TaskList;
import alfred.task.TaskType;
import alfred.task.Todo;
import alfred.ui.Ui;

class ParserTest {
    @TempDir
    Path temporaryDirectory;

    private final Parser parser = new Parser();

    @Test
    void parseCommand_nonTaskCommands_returnsMatchingCommandTypes() throws AlfredException {
        assertInstanceOf(ExitCommand.class, parser.parseCommand("bye", 1));
        assertInstanceOf(ExitCommand.class, parser.parseCommand("  bye  ", 1));
        assertInstanceOf(ListCommand.class, parser.parseCommand("list", 1));
        assertInstanceOf(ListCommand.class, parser.parseCommand(" list ", 1));
        assertInstanceOf(FindCommand.class, parser.parseCommand("find book", 1));
        assertInstanceOf(SortCommand.class, parser.parseCommand("sort", 1));
        assertInstanceOf(SortCommand.class, parser.parseCommand("sort asc", 1));
        assertInstanceOf(SortCommand.class, parser.parseCommand("sort desc", 1));
        assertInstanceOf(MarkCommand.class, parser.parseCommand("mark 1", 1));
        assertInstanceOf(UnmarkCommand.class, parser.parseCommand("unmark 1", 1));
        assertInstanceOf(DeleteCommand.class, parser.parseCommand("delete 1", 1));
    }

    @Test
    void parseCommand_todoCommand_addsTodoWithTrimmedDescription() throws AlfredException {
        Task task = getAddedTask("todo   read book  ");

        assertInstanceOf(Todo.class, task);
        assertEquals(TaskType.TODO, task.getType());
        assertEquals("read book", task.getDescription());
    }

    @Test
    void parseCommand_deadlineCommand_addsDeadlineWithDateAndTime() throws AlfredException {
        Task task = getAddedTask("deadline submit report /by 2/12/2019 1800");

        Deadline deadline = assertInstanceOf(Deadline.class, task);
        assertEquals("submit report", deadline.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), deadline.getBy());
    }

    @Test
    void parseCommand_eventCommand_addsEventWithDateRange() throws AlfredException {
        Task task = getAddedTask("event project meeting /from 2019-12-02 /to 2/12/2019 1800");

        Event event = assertInstanceOf(Event.class, task);
        assertEquals("project meeting", event.getDescription());
        assertEquals(LocalDateTime.of(2019, 12, 2, 0, 0), event.getFrom());
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), event.getTo());
    }

    @Test
    void parseTaskIndex_validOneBasedNumber_returnsZeroBasedIndex() throws AlfredException {
        assertEquals(2, parser.parseTaskIndex("mark 3", "mark", 3));
    }

    @Test
    void parseTaskIndex_missingNumber_exceptionWithGuidanceThrown() {
        assertError("Alfred needs a task number after `mark`.", () ->
                parser.parseTaskIndex("mark", "mark", 3));
    }

    @Test
    void parseTaskIndex_emptyTaskList_exceptionWithGuidanceThrown() {
        assertError("Alfred's task list is empty, so there is nothing to delete.", () ->
                parser.parseTaskIndex("delete 1", "delete", 0));
    }

    @Test
    void parseTaskIndex_nonNumericNumber_exceptionWithGuidanceThrown() {
        assertError("Alfred needs a whole-number task number after `unmark`.", () ->
                parser.parseTaskIndex("unmark two", "unmark", 3));
    }

    @Test
    void parseTaskIndex_outOfRangeNumber_exceptionWithGuidanceThrown() {
        assertError("Alfred cannot find task 0. Choose a number from 1 to 3.", () ->
                parser.parseTaskIndex("mark 0", "mark", 3));
        assertError("Alfred cannot find task 4. Choose a number from 1 to 3.", () ->
                parser.parseTaskIndex("mark 4", "mark", 3));
    }

    @Test
    void parseCommand_invalidTaskDetails_exceptionWithSpecificGuidanceThrown() {
        assertError("Alfred needs a keyword after `find`.", () ->
                parser.parseCommand("find", 0));
        assertError("Alfred cannot add a to-do without a mission description.", () ->
                parser.parseCommand("todo", 0));
        assertError("Alfred needs `/by` followed by a due time for a deadline.", () ->
                parser.parseCommand("deadline submit report", 0));
        assertError("Alfred needs a deadline description before `/by`.", () ->
                parser.parseCommand("deadline /by 2019-12-02", 0));
        assertError("Alfred needs a due time after `/by`.", () ->
                parser.parseCommand("deadline submit report /by", 0));
        assertError("Alfred needs `/from` followed by a start time for an event.", () ->
                parser.parseCommand("event project meeting", 0));
        assertError("Alfred needs `/to` followed by an end time for an event.", () ->
                parser.parseCommand("event project meeting /from 2019-12-02", 0));
        assertError("Alfred needs an event description before `/from`.", () ->
                parser.parseCommand("event /from 2019-12-02 /to 2019-12-03", 0));
        assertError("Alfred needs a start time after `/from`.", () ->
                parser.parseCommand("event project /from  /to 2019-12-03", 0));
        assertError("Alfred needs an end time after `/to`.", () ->
                parser.parseCommand("event project /from 2019-12-02 /to", 0));
    }

    @Test
    void parseCommand_repeatedMarkersAndUnexpectedArguments_exceptionWithGuidanceThrown() {
        assertError("Alfred's `bye` command does not take additional arguments.", () ->
                parser.parseCommand("bye now", 0));
        assertError("Alfred's `list` command does not take additional arguments.", () ->
                parser.parseCommand("list all", 0));
        assertError("Alfred accepts only one `/by` marker for a deadline.", () ->
                parser.parseCommand("deadline submit report /by 2019-12-02 /by 2019-12-03", 0));
        assertError("Alfred accepts only one `/from` marker for an event.", () ->
                parser.parseCommand("event meeting /from 2019-12-02 /from 2019-12-03 /to 2019-12-04", 0));
        assertError("Alfred accepts only one `/to` marker for an event.", () ->
                parser.parseCommand("event meeting /from 2019-12-02 /to 2019-12-03 /to 2019-12-04", 0));
    }

    @Test
    void parseCommand_eventEndNotAfterStart_exceptionWithGuidanceThrown() {
        assertError("Alfred needs an event end time later than its start time.", () ->
                parser.parseCommand("event meeting /from 2019-12-03 /to 2019-12-03", 0));
        assertError("Alfred needs an event end time later than its start time.", () ->
                parser.parseCommand("event meeting /from 2019-12-04 /to 2019-12-03", 0));
    }

    @Test
    void parseCommand_invalidSortDirection_exceptionWithUsageGuidanceThrown() {
        String expectedMessage = "Alfred only understands `sort`, `sort asc`, or `sort desc`.";

        assertError(expectedMessage, () -> parser.parseCommand("sort ascending", 0));
        assertError(expectedMessage, () -> parser.parseCommand("sort asc now", 0));
        assertError(expectedMessage, () -> parser.parseCommand("SORT asc", 0));
    }

    @Test
    void parseCommand_unknownCommand_exceptionWithGuidanceThrown() {
        assertError("I do not recognise that command. Come again.", () ->
                parser.parseCommand("remind me", 0));
    }

    @Test
    void fromInput_unknownCommand_returnsUnknownCommandType() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("remind me"));
    }

    private Task getAddedTask(String input) throws AlfredException {
        TaskList tasks = new TaskList();
        Command command = parser.parseCommand(input, tasks.size());
        Storage storage = new Storage(temporaryDirectory.resolve("alfred.txt"));

        assertInstanceOf(AddCommand.class, command);
        command.execute(tasks, new Ui(), storage);
        return tasks.get(0);
    }

    private void assertError(String expectedMessage, ThrowingOperation operation) {
        AlfredException exception = assertThrows(AlfredException.class, operation::run);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @FunctionalInterface
    private interface ThrowingOperation {
        void run() throws AlfredException;
    }
}
