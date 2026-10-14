package verity.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import verity.VerityException;
import verity.command.AddCommand;
import verity.command.Command;
import verity.command.DeleteCommand;
import verity.command.ExitCommand;
import verity.command.FindCommand;
import verity.command.ListCommand;
import verity.command.MarkCommand;
import verity.command.OnCommand;
import verity.command.UndoCommand;
import verity.command.Undoable;
import verity.command.UnmarkCommand;
import verity.storage.Storage;
import verity.task.TaskList;

class ParserTest {

    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("verity.txt").toString());
    }

    // ---- todo ----

    @Test
    void parse_validTodo_addsTodoToTaskList() throws VerityException, IOException {
        Command command = Parser.parse("todo read book");
        assertInstanceOf(AddCommand.class, command);

        TaskList tasks = new TaskList();
        command.execute(tasks, newStorage());

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void parse_todoEmptyDescription_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("todo"));
    }

    @Test
    void parse_whitespaceAroundCommand_ignoredSuccessfully() throws VerityException, IOException {
        Command command = Parser.parse("  todo read book  ");
        TaskList tasks = new TaskList();

        command.execute(tasks, newStorage());

        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    // ---- deadline ----

    @Test
    void parse_validDeadline_addsDeadlineWithParsedDate() throws VerityException, IOException {
        Command command = Parser.parse("deadline return book /by 2019-10-15");

        TaskList tasks = new TaskList();
        command.execute(tasks, newStorage());

        assertEquals("[D][ ] return book (by: Oct 15 2019)", tasks.get(0).toString());
    }

    @Test
    void parse_deadlineMissingByMarker_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("deadline return book"));
    }

    @Test
    void parse_deadlineEmptyDescription_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("deadline /by 2019-10-15"));
    }

    @Test
    void parse_deadlineEmptyDateText_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("deadline return book /by"));
    }

    @Test
    void parse_deadlineInvalidDateFormat_exceptionThrown() {
        String input = "deadline return book /by tomorrow";

        VerityException e = assertThrows(VerityException.class, () -> Parser.parse(input));

        assertTrue(e.getMessage().contains("yyyy-MM-dd"));
    }

    @Test
    void parse_deadlineByMarkerIsCaseInsensitive_parsedSuccessfully() throws VerityException, IOException {
        Command command = Parser.parse("deadline return book /BY 2019-10-15");

        TaskList tasks = new TaskList();
        command.execute(tasks, newStorage());

        assertEquals("[D][ ] return book (by: Oct 15 2019)", tasks.get(0).toString());
    }

    @Test
    void parse_deadlineDuplicateByMarker_exceptionExplainsDuplicateMarker() {
        String input = "deadline return book /by 2019-01-01 /by 2020-01-01";

        VerityException e = assertThrows(VerityException.class, () -> Parser.parse(input));

        assertTrue(e.getMessage().contains("more than once"));
    }

    @Test
    void parse_deadlineNonExistentCalendarDate_exceptionThrown() {
        // Well-formed yyyy-MM-dd syntax, but February never has a 30th day.
        String input = "deadline return book /by 2019-02-30";

        VerityException e = assertThrows(VerityException.class, () -> Parser.parse(input));

        assertTrue(e.getMessage().contains("yyyy-MM-dd"));
    }

    // ---- event ----

    @Test
    void parse_validEvent_addsEventWithParsedDates() throws VerityException, IOException {
        Command command = Parser.parse("event project meeting /from 2019-08-06 /to 2019-08-07");

        TaskList tasks = new TaskList();
        command.execute(tasks, newStorage());

        assertEquals("[E][ ] project meeting (from: Aug 06 2019 to: Aug 07 2019)", tasks.get(0).toString());
    }

    @Test
    void parse_eventMissingFromMarker_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("event project meeting"));
    }

    @Test
    void parse_eventMissingToMarker_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("event project meeting /from 2019-08-06"));
    }

    @Test
    void parse_eventEmptyDescription_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("event /from 2019-08-06 /to 2019-08-07"));
    }

    @Test
    void parse_eventInvalidToDate_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("event meeting /from 2019-08-06 /to whenever"));
    }

    @Test
    void parse_eventEndingBeforeItStarts_exceptionExplainsDateOrder() {
        VerityException exception = assertThrows(VerityException.class, () ->
                Parser.parse("event meeting /from 2019-08-07 /to 2019-08-06"));

        assertTrue(exception.getMessage().contains("end before it starts"));
    }

    @Test
    void parse_eventSameDayStartAndEnd_isValid() throws VerityException, IOException {
        // A single-day event is legitimate - only a start strictly after the end is rejected.
        Command command = Parser.parse("event conference /from 2019-08-06 /to 2019-08-06");

        TaskList tasks = new TaskList();
        command.execute(tasks, newStorage());

        assertEquals("[E][ ] conference (from: Aug 06 2019 to: Aug 06 2019)", tasks.get(0).toString());
    }

    @Test
    void parse_eventToMarkerBeforeFromMarker_exceptionExplainsMarkerOrder() {
        String input = "event meeting /to 2019-08-07 /from 2019-08-06";

        VerityException e = assertThrows(VerityException.class, () -> Parser.parse(input));

        assertTrue(e.getMessage().contains("must come before"));
    }

    @Test
    void parse_eventDuplicateFromMarker_exceptionExplainsDuplicateMarker() {
        VerityException e = assertThrows(VerityException.class, () -> Parser.parse(
                "event meeting /from 2019-08-06 /from 2019-08-07 /to 2019-08-08"));

        assertTrue(e.getMessage().contains("more than once"));
    }

    @Test
    void parse_eventDuplicateToMarker_exceptionExplainsDuplicateMarker() {
        VerityException e = assertThrows(VerityException.class, () -> Parser.parse(
                "event meeting /from 2019-08-06 /to 2019-08-07 /to 2019-08-08"));

        assertTrue(e.getMessage().contains("more than once"));
    }

    @Test
    void parse_eventFromMarkerRepeatedAfterToMarker_exceptionExplainsDuplicateMarker() {
        VerityException e = assertThrows(VerityException.class, () -> Parser.parse(
                "event meeting /from 2019-08-06 /to 2019-08-07 /from 2019-08-08"));

        assertTrue(e.getMessage().contains("more than once"));
    }

    @Test
    void parse_eventEmptyStartTime_exceptionThrown() {
        String input = "event meeting /from /to 2019-08-07";

        VerityException e = assertThrows(VerityException.class, () -> Parser.parse(input));

        assertTrue(e.getMessage().contains("start time"));
    }

    @Test
    void parse_eventEmptyEndTime_exceptionThrown() {
        String input = "event meeting /from 2019-08-06 /to";

        VerityException e = assertThrows(VerityException.class, () -> Parser.parse(input));

        assertTrue(e.getMessage().contains("end time"));
    }

    // ---- list ----

    @Test
    void parse_list_returnsListCommand() {
        assertInstanceOf(ListCommand.class, assertDoesNotThrowParse("list"));
    }

    // ---- on ----

    @Test
    void parse_validOnDate_returnsOnCommandThatFiltersMatchingTasks() throws VerityException, IOException {
        TaskList tasks = new TaskList();
        Parser.parse("deadline return book /by 2019-12-02").execute(tasks, newStorage());
        Parser.parse("deadline other /by 2019-12-25").execute(tasks, newStorage());

        Command onCommand = Parser.parse("on 2019-12-02");
        assertInstanceOf(OnCommand.class, onCommand);
        // Executing it must not throw, and must not mutate the task list.
        onCommand.execute(tasks, newStorage());

        assertEquals(2, tasks.size());
    }

    @Test
    void parse_onMissingDate_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("on"));
    }

    @Test
    void parse_onInvalidDate_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("on someday"));
    }

    // ---- find ----

    @Test
    void parse_validFind_returnsFindCommandThatFiltersMatchingTasks() throws VerityException, IOException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, newStorage());
        Parser.parse("todo write essay").execute(tasks, newStorage());

        Command findCommand = Parser.parse("find book");
        assertInstanceOf(FindCommand.class, findCommand);
        // Executing it must not throw, and must not mutate the task list.
        findCommand.execute(tasks, newStorage());

        assertEquals(2, tasks.size());
    }

    @Test
    void parse_findMissingKeyword_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("find"));
    }

    // ---- mark / unmark / delete ----

    @Test
    void parse_validMark_marksTaskAsDone() throws VerityException, IOException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, newStorage());

        Command markCommand = Parser.parse("mark 1");
        assertInstanceOf(MarkCommand.class, markCommand);
        markCommand.execute(tasks, newStorage());

        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    void parse_validUnmark_marksTaskAsNotDone() throws VerityException, IOException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, newStorage());
        Parser.parse("mark 1").execute(tasks, newStorage());

        Command unmarkCommand = Parser.parse("unmark 1");
        assertInstanceOf(UnmarkCommand.class, unmarkCommand);
        unmarkCommand.execute(tasks, newStorage());

        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void parse_validDelete_removesTask() throws VerityException, IOException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, newStorage());

        Command deleteCommand = Parser.parse("delete 1");
        assertInstanceOf(DeleteCommand.class, deleteCommand);
        deleteCommand.execute(tasks, newStorage());

        assertTrue(tasks.isEmpty());
    }

    @Test
    void parse_markMissingNumber_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("mark"));
    }

    @Test
    void parse_markNonNumericArgument_exceptionThrown() {
        assertThrows(VerityException.class, () -> Parser.parse("mark abc"));
    }

    @Test
    void parse_markZero_exceptionExplainsTaskNumbersStartAtOne() {
        VerityException exception = assertThrows(VerityException.class, () -> Parser.parse("mark 0"));

        assertTrue(exception.getMessage().contains("start at 1"));
    }

    @Test
    void parse_markIndexOutOfRange_exceptionThrownOnlyAtExecuteTime() throws VerityException {
        // Parsing succeeds - "5" is a well-formed number. Only execute(), which has access
        // to the actual task list, can know it doesn't refer to an existing task.
        Command markCommand = Parser.parse("mark 5");

        TaskList tasks = new TaskList();
        VerityException e = assertThrows(VerityException.class, () -> markCommand.execute(tasks, newStorage()));
        assertTrue(e.getMessage().contains("no task 5"));
    }

    // ---- undo ----

    @Test
    void parse_undoNoHistory_returnsUndoCommandReportingNothingToUndo() throws VerityException, IOException {
        Command undoCommand = Parser.parse("undo");
        assertInstanceOf(UndoCommand.class, undoCommand);

        String response = undoCommand.execute(new TaskList(), newStorage());

        assertEquals("There's nothing to undo, genius.", response);
    }

    @Test
    void parse_undoWithHistory_returnsUndoCommandThatReversesIt() throws VerityException, IOException {
        TaskList tasks = new TaskList();
        Command addCommand = Parser.parse("todo read book");
        addCommand.execute(tasks, newStorage());

        Command undoCommand = Parser.parse("undo", (Undoable) addCommand);
        String response = undoCommand.execute(tasks, newStorage());

        assertTrue(tasks.isEmpty());
        assertTrue(response.contains("Whatever. Un-did it"));
    }

    // ---- bye ----

    @Test
    void parse_bye_returnsExitCommandWhoseIsExitIsTrue() {
        Command command = assertDoesNotThrowParse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    // ---- unrecognized ----

    @Test
    void parse_unrecognizedCommand_exceptionThrown() {
        VerityException e = assertThrows(VerityException.class, () -> Parser.parse("gibberish"));

        assertTrue(e.getMessage().contains("invalid command"));
    }

    @Test
    void parse_blankOrNullInput_helpfulExceptionThrown() {
        VerityException blankException = assertThrows(VerityException.class, () -> Parser.parse("  \t  "));
        VerityException nullException = assertThrows(VerityException.class, () -> Parser.parse(null));

        assertTrue(blankException.getMessage().contains("didn't catch a command"));
        assertTrue(nullException.getMessage().contains("didn't catch a command"));
    }

    private static Command assertDoesNotThrowParse(String input) {
        try {
            return Parser.parse(input);
        } catch (VerityException e) {
            throw new AssertionError("Parser.parse(\"" + input + "\") should not have thrown", e);
        }
    }
}
