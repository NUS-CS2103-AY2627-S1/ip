package peter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import peter.command.AddCommand;
import peter.command.Command;
import peter.command.DeleteCommand;
import peter.command.ExitCommand;
import peter.command.FindCommand;
import peter.command.FindOnDateCommand;
import peter.command.ListCommand;
import peter.command.MarkCommand;
import peter.storage.Storage;
import peter.task.Task;
import peter.task.TaskList;
import peter.ui.Ui;

/**
 * Unit tests for {@link Parser}, which holds the most branching logic in the
 * application: it routes command words and validates every argument.
 *
 * <p>Most tests assert on the exact {@link PeterException} message, because
 * those messages are the app's entire error-handling contract with the user.
 * The private helper methods of {@code Parser} are reached through the public
 * {@link Parser#parse(String)} entry point rather than being tested directly.
 */
public class ParserTest {

    /** Temporary data directory, so executing a command never touches real data. */
    @TempDir
    private Path tempDir;

    // =====================================================================
    // parse(String): command word routing
    // =====================================================================

    @Test
    public void parse_byeCommand_exitCommandReturned() throws PeterException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        // isExit() drives the main loop's termination, so a regression here
        // would leave the app unable to quit.
        assertTrue(command.isExit());
    }

    @Test
    public void parse_listCommand_listCommandReturned() throws PeterException {
        Command command = Parser.parse("list");
        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    public void parse_onCommand_findOnDateCommandReturned() throws PeterException {
        assertInstanceOf(FindOnDateCommand.class, Parser.parse("on 2019-12-02"));
    }

    @Test
    public void parse_markCommand_markCommandReturned() throws PeterException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
    }

    @Test
    public void parse_unmarkCommand_markCommandReturned() throws PeterException {
        // Both directions share one class, differing only in the flag passed.
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
    }

    @Test
    public void parse_deleteCommand_deleteCommandReturned() throws PeterException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("blah"));
        assertEquals("My spider-sense doesn't recognize that command. Please try again.",
                exception.getMessage());
    }

    @Test
    public void parse_emptyCommand_exceptionThrown() {
        assertThrows(PeterException.class, () -> Parser.parse(""));
    }

    @Test
    public void parse_wrongCaseCommand_exceptionThrown() {
        // Command matching is case-sensitive.
        assertThrows(PeterException.class, () -> Parser.parse("Bye"));
    }

    @Test
    public void parse_commandWordAsPrefixOfAnotherWord_exceptionThrown() {
        // "byebye" must not be accepted as "bye": a keyword only matches when
        // it is the whole command or is followed by a space.
        assertThrows(PeterException.class, () -> Parser.parse("byebye"));
        assertThrows(PeterException.class, () -> Parser.parse("listing"));
    }

    @Test
    public void parse_argumentlessCommandWithArgument_exceptionThrown() {
        // "bye" and "list" take no arguments, so trailing text is rejected
        // rather than silently ignored.
        assertThrows(PeterException.class, () -> Parser.parse("list all"));
        assertThrows(PeterException.class, () -> Parser.parse("bye now"));
    }

    @Test
    public void parse_argumentlessCommandWithTrailingSpace_exceptionThrown() {
        // Documents current behavior: input is not trimmed before matching,
        // so "list " does not equal "list" and is not a keyword with arguments.
        assertThrows(PeterException.class, () -> Parser.parse("list "));
    }

    @Test
    public void parse_taskNumberCommandWithoutNumber_commandReturnedNotThrown() throws PeterException {
        // Deliberate design: mark/unmark/delete defer validating the task
        // number until execution, because the check needs the task count.
        // Parsing must therefore succeed even for an obviously bad number.
        assertInstanceOf(MarkCommand.class, Parser.parse("mark"));
        assertInstanceOf(MarkCommand.class, Parser.parse("mark abc"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete"));
    }

    // =====================================================================
    // parse(String): "on" query date
    // =====================================================================

    @Test
    public void parse_onCommandWithoutDate_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("on"));
        assertEquals("Use 'on <date>' (e.g., on 2019-12-02).", exception.getMessage());
    }

    @Test
    public void parse_onCommandWithBlankDate_exceptionThrown() {
        // The date text is trimmed, so whitespace counts as missing.
        assertThrows(PeterException.class, () -> Parser.parse("on    "));
    }

    @Test
    public void parse_onCommandWithSurroundingSpaces_dateAccepted() throws PeterException {
        assertInstanceOf(FindOnDateCommand.class, Parser.parse("on   2019-12-02  "));
    }

    @Test
    public void parse_onCommandWithNonIsoDate_exceptionThrown() {
        // "on" accepts only ISO dates, unlike deadline/event which also accept
        // d/M/yyyy HHmm, so the error names the one supported format.
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("on 2/12/2019"));
        assertEquals("Please enter the date in yyyy-MM-dd format (e.g., 2019-12-02).",
                exception.getMessage());
    }

    @Test
    public void parse_onCommandWithImpossibleDate_exceptionThrown() {
        assertThrows(PeterException.class, () -> Parser.parse("on 2019-02-30"));
    }

    // =====================================================================
    // parse(String): "find" keywords
    // =====================================================================

    @Test
    public void parse_findCommand_findCommandReturned() throws PeterException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_findCommandWithoutKeyword_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("find"));
        assertEquals("Use 'find <keywords>' (e.g., find read book).", exception.getMessage());
    }

    @Test
    public void parse_findCommandWithBlankKeyword_exceptionThrown() {
        // The arguments are trimmed, so whitespace counts as missing.
        assertThrows(PeterException.class, () -> Parser.parse("find    "));
    }

    @Test
    public void parse_findCommandWithSurroundingSpaces_keywordAccepted() throws PeterException {
        assertEquals(List.of("book"), parseToKeywords("find   book  "));
    }

    @Test
    public void parse_findCommandWithOneKeyword_singleKeywordReturned() throws PeterException {
        assertEquals(List.of("book"), parseToKeywords("find book"));
    }

    @Test
    public void parse_findCommandWithSeveralKeywords_keywordsSplit() throws PeterException {
        // Each word is its own keyword, rather than the whole remainder being
        // one search term, so a task matches only if it contains both.
        assertEquals(List.of("read", "book"), parseToKeywords("find read book"));
    }

    @Test
    public void parse_findCommandWithRepeatedInnerSpaces_keywordsSplit() throws PeterException {
        // Splitting on runs of whitespace means extra spacing is insignificant.
        assertEquals(List.of("read", "book"), parseToKeywords("find   read    book"));
    }

    @Test
    public void parse_findCommandWithKeywordsInAnyOrder_orderPreserved() throws PeterException {
        // The keywords are kept in the order typed; the search itself is what
        // ignores their order.
        assertEquals(List.of("book", "read"), parseToKeywords("find book read"));
    }

    @Test
    public void parse_findAsPrefixOfAnotherWord_exceptionThrown() {
        // "finder" must not be accepted as "find".
        assertThrows(PeterException.class, () -> Parser.parse("finder"));
    }

    // =====================================================================
    // parse(String): todo
    // =====================================================================

    @Test
    public void parse_todoWithDescription_taskCreated() throws PeterException {
        assertEquals("T | 0 | read book", parseToTask("todo read book").toDataString());
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("todo"));
        assertEquals("Please include a description after 'todo'.", exception.getMessage());
    }

    @Test
    public void parse_todoWithBlankDescription_exceptionThrown() {
        // Both "todo " and "todo    " must be rejected, not stored as blank.
        assertThrows(PeterException.class, () -> Parser.parse("todo "));
        assertThrows(PeterException.class, () -> Parser.parse("todo    "));
    }

    @Test
    public void parse_todoDescriptionContainingStorageDelimiter_exceptionThrown() {
        // " | " separates fields in the save file, so allowing it in a
        // description would corrupt the file and break the next load.
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("todo read | book"));
        assertEquals("Oh dear! Task details cannot contain ' | '.", exception.getMessage());
    }

    @Test
    public void parse_todoDescriptionContainingBarWithoutSpaces_taskCreated() throws PeterException {
        // Only the exact " | " delimiter is dangerous; a bare "|" is fine.
        assertEquals("T | 0 | read|book", parseToTask("todo read|book").toDataString());
    }

    // =====================================================================
    // parse(String): deadline
    // =====================================================================

    @Test
    public void parse_deadlineWithIsoDate_taskCreated() throws PeterException {
        // A date without a time is stored at midnight.
        assertEquals("D | 0 | return book | 2019-12-02T00:00",
                parseToTask("deadline return book /by 2019-12-02").toDataString());
    }

    @Test
    public void parse_deadlineWithDateAndTime_taskCreated() throws PeterException {
        assertEquals("D | 0 | return book | 2019-12-02T18:00",
                parseToTask("deadline return book /by 2/12/2019 1800").toDataString());
    }

    @Test
    public void parse_deadlineWithoutByMarker_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("deadline return book"));
        assertEquals("Use 'deadline <description> /by <date>'.", exception.getMessage());
    }

    @Test
    public void parse_deadlineWithTrailingByMarker_exceptionThrown() {
        // "/by" present but nothing after it gets a more specific message than
        // the generic usage hint.
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("deadline return book /by"));
        assertEquals("Please include a due date after '/by'.", exception.getMessage());
    }

    @Test
    public void parse_deadlineWithBlankDueDate_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("deadline return book /by    "));
        assertEquals("Please include a due date after '/by'.", exception.getMessage());
    }

    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("deadline /by 2019-12-02"));
        assertEquals("Please include a description before '/by'.", exception.getMessage());
    }

    @Test
    public void parse_deadlineWithBlankDescription_exceptionThrown() {
        assertThrows(PeterException.class, () -> Parser.parse("deadline    /by 2019-12-02"));
    }

    @Test
    public void parse_deadlineWithInvalidDate_exceptionThrown() {
        // The message names which date failed, since events have three.
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("deadline return book /by tomorrow"));
        assertEquals("Please enter the due date as yyyy-MM-dd or d/M/yyyy HHmm"
                + " (e.g., 2019-10-15 or 2/12/2019 1800).", exception.getMessage());
    }

    @Test
    public void parse_deadlineDueDateContainingStorageDelimiter_exceptionThrown() {
        assertThrows(PeterException.class, () ->
                Parser.parse("deadline return book /by a | b"));
    }

    // =====================================================================
    // parse(String): event
    // =====================================================================

    @Test
    public void parse_eventWithIsoDates_taskCreated() throws PeterException {
        assertEquals("E | 0 | camp | 2019-12-02T00:00 | 2019-12-05T00:00",
                parseToTask("event camp /from 2019-12-02 /to 2019-12-05").toDataString());
    }

    @Test
    public void parse_eventWithDatesAndTimes_taskCreated() throws PeterException {
        assertEquals("E | 0 | camp | 2019-12-02T18:00 | 2019-12-05T09:30",
                parseToTask("event camp /from 2/12/2019 1800 /to 5/12/2019 0930")
                        .toDataString());
    }

    @Test
    public void parse_eventWithoutMarkers_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("event camp"));
        assertEquals("Use 'event <description> /from <start-date> /to <end-date>'.",
                exception.getMessage());
    }

    @Test
    public void parse_eventWithTrailingFromMarker_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("event camp /from"));
        assertEquals("Please include a start date after '/from'.", exception.getMessage());
    }

    @Test
    public void parse_eventWithTrailingToMarker_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("event camp /from 2019-12-02 /to"));
        assertEquals("Please include an end date after '/to'.", exception.getMessage());
    }

    @Test
    public void parse_eventWithMarkersReversed_exceptionThrown() {
        // "/to" before "/from" is rejected rather than parsed backwards.
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("event camp /to 2019-12-05 /from 2019-12-02"));
        assertEquals("Use 'event <description> /from <start-date> /to <end-date>'.",
                exception.getMessage());
    }

    @Test
    public void parse_eventWithoutDescription_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("event /from 2019-12-02 /to 2019-12-05"));
        assertEquals("Please include a description before '/from'.", exception.getMessage());
    }

    @Test
    public void parse_eventWithEmptyStartDate_exceptionThrown() {
        // "/from" and "/to" adjacent leaves no room for a start date.
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("event camp /from /to 2019-12-05"));
        assertEquals("Please include a start date after '/from'.", exception.getMessage());
    }

    @Test
    public void parse_eventWithBlankEndDate_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("event camp /from 2019-12-02 /to    "));
        assertEquals("Please include an end date after '/to'.", exception.getMessage());
    }

    @Test
    public void parse_eventWithInvalidStartDate_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("event camp /from tomorrow /to 2019-12-05"));
        assertEquals("Please enter the start date as yyyy-MM-dd or d/M/yyyy HHmm"
                + " (e.g., 2019-10-15 or 2/12/2019 1800).", exception.getMessage());
    }

    @Test
    public void parse_eventWithInvalidEndDate_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("event camp /from 2019-12-02 /to tomorrow"));
        assertEquals("Please enter the end date as yyyy-MM-dd or d/M/yyyy HHmm"
                + " (e.g., 2019-10-15 or 2/12/2019 1800).", exception.getMessage());
    }

    @Test
    public void parse_eventEndingBeforeStart_exceptionThrown() {
        // Such an event would cover no dates and so could never be found
        // again under 'on', which is why it is rejected at parse time.
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parse("event camp /from 2019-12-05 /to 2019-12-02"));
        assertEquals("Please make sure the end date is not before the start date.",
                exception.getMessage());
    }

    @Test
    public void parse_eventEndingBeforeStartOnSameDay_exceptionThrown() {
        // The check compares times, not just dates, so a backwards event
        // within a single day is caught too.
        assertThrows(PeterException.class, () ->
                Parser.parse("event meeting /from 2/12/2019 1700 /to 2/12/2019 0900"));
    }

    @Test
    public void parse_eventStartingAndEndingAtSameInstant_taskCreated() throws PeterException {
        // The boundary of the new check: equal start and end is a zero-length
        // but still findable event, so it must remain allowed.
        assertEquals("E | 0 | meeting | 2019-12-02T09:00 | 2019-12-02T09:00",
                parseToTask("event meeting /from 2/12/2019 0900 /to 2/12/2019 0900")
                        .toDataString());
    }

    @Test
    public void parse_eventEndingOneMinuteAfterStart_taskCreated() throws PeterException {
        // Just inside the allowed side of the boundary.
        assertEquals("E | 0 | meeting | 2019-12-02T09:00 | 2019-12-02T09:01",
                parseToTask("event meeting /from 2/12/2019 0900 /to 2/12/2019 0901")
                        .toDataString());
    }

    // =====================================================================
    // parseTaskIndex(String, int)
    // =====================================================================

    @Test
    public void parseTaskIndex_firstTaskNumber_zeroReturned() throws PeterException {
        // One-based input becomes a zero-based index.
        assertEquals(0, Parser.parseTaskIndex("mark 1", 5));
    }

    @Test
    public void parseTaskIndex_lastTaskNumber_lastIndexReturned() throws PeterException {
        // The upper boundary must be inclusive.
        assertEquals(4, Parser.parseTaskIndex("mark 5", 5));
    }

    @Test
    public void parseTaskIndex_onlyTaskInList_zeroReturned() throws PeterException {
        assertEquals(0, Parser.parseTaskIndex("delete 1", 1));
    }

    @Test
    public void parseTaskIndex_numberWithExtraSpaces_indexReturned() throws PeterException {
        assertEquals(2, Parser.parseTaskIndex("mark   3  ", 5));
    }

    @Test
    public void parseTaskIndex_missingNumber_exceptionThrown() {
        // The message names the action, so it must adapt to each command word.
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parseTaskIndex("mark", 5));
        assertEquals("Oh dear! Please provide a task number to mark.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_missingNumberForDelete_actionNamedInMessage() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parseTaskIndex("delete", 5));
        assertEquals("Oh dear! Please provide a task number to delete.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_missingNumberForUnmark_actionNamedInMessage() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parseTaskIndex("unmark", 5));
        assertEquals("Oh dear! Please provide a task number to unmark.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_emptyList_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parseTaskIndex("mark 1", 0));
        assertEquals("Oh dear! There are no tasks to mark.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_emptyListAndMissingNumber_missingNumberReported() {
        // Checks the order of the two guards: the missing number is reported
        // first, even though the list is also empty.
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parseTaskIndex("mark", 0));
        assertEquals("Oh dear! Please provide a task number to mark.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_numberBelowRange_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parseTaskIndex("mark 0", 5));
        assertEquals("Oh dear! Task number must be between 1 and 5.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_negativeNumber_exceptionThrown() {
        assertThrows(PeterException.class, () -> Parser.parseTaskIndex("mark -1", 5));
    }

    @Test
    public void parseTaskIndex_numberAboveRange_exceptionThrown() {
        // Just past the end: the classic off-by-one boundary.
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parseTaskIndex("mark 6", 5));
        assertEquals("Oh dear! Task number must be between 1 and 5.", exception.getMessage());
    }

    @Test
    public void parseTaskIndex_nonNumericNumber_exceptionThrown() {
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parseTaskIndex("mark abc", 5));
        assertEquals("Oh dear! Please enter an integer task number to mark.",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_decimalNumber_exceptionThrown() {
        assertThrows(PeterException.class, () -> Parser.parseTaskIndex("mark 1.5", 5));
    }

    @Test
    public void parseTaskIndex_numberTooLargeForInt_exceptionThrown() {
        // An overflowing value must be reported as a bad integer rather than
        // wrapping around into a valid-looking index.
        PeterException exception = assertThrows(PeterException.class, () ->
                Parser.parseTaskIndex("mark 99999999999999", 5));
        assertEquals("Oh dear! Please enter an integer task number to mark.",
                exception.getMessage());
    }

    @Test
    public void parseTaskIndex_unknownCommandWord_exceptionThrown() {
        // The index parser re-derives the command word, so an unrecognized one
        // fails here too.
        assertThrows(PeterException.class, () -> Parser.parseTaskIndex("blah 1", 5));
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    /**
     * Parses a task-creation command and returns the resulting {@link Task}.
     *
     * <p>{@link AddCommand} keeps its task private, so the only way to inspect
     * what the parser built is to execute the command against a real task list.
     * Storage is pointed at a temporary directory and console output is
     * discarded, so this stays a check on parsing rather than on the UI.
     *
     * @param command task-creation command to parse.
     * @return the task the parser produced.
     * @throws PeterException if the command is invalid.
     */
    private Task parseToTask(String command) throws PeterException {
        Command parsedCommand = Parser.parse(command);
        assertInstanceOf(AddCommand.class, parsedCommand);

        TaskList tasks = new TaskList();
        Storage storage = new Storage(tempDir.resolve("tasks.txt").toString());
        PrintStream originalOut = System.out;
        try {
            System.setOut(new PrintStream(new ByteArrayOutputStream()));
            parsedCommand.execute(tasks, new Ui(), storage);
        } finally {
            System.setOut(originalOut);
        }
        return tasks.get(0);
    }

    /**
     * Parses a find command and returns the keywords it produced.
     *
     * <p>Unlike {@link #parseToTask(String)}, this needs no task list or
     * storage, because {@link FindCommand} exposes its keywords directly. The
     * splitting rule is therefore pinned here rather than inferred from
     * rendered console output.
     *
     * @param command find command to parse.
     * @return the keywords the parser produced.
     * @throws PeterException if the command is invalid.
     */
    private List<String> parseToKeywords(String command) throws PeterException {
        Command parsedCommand = Parser.parse(command);
        assertInstanceOf(FindCommand.class, parsedCommand);

        return ((FindCommand) parsedCommand).getKeywords();
    }
}
