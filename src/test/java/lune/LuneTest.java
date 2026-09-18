package lune;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import lune.Lune.CommandType;
import lune.exception.LuneException;
import lune.task.Deadline;
import lune.task.Event;
import lune.task.Task;
import lune.task.TaskList;
import lune.task.Todo;

/**
 * Tests the highest-value logic in Lune: command dispatch (CommandType),
 * task-index validation, date/time parsing, save-file line parsing, and
 * processCommand() itself (every case except ARCHIVE, which performs
 * real file I/O and is left to the console-level test-ui suite). main()
 * and getResponse() are themselves left untested here — they're thin
 * orchestration around processCommand() plus real file I/O (saveTasks),
 * exercised end-to-end by the console suite instead.
 */
public class LuneTest {

    // --- CommandType.fromInput ---

    @Test
    public void fromInput_bareCommandWord_matchingTypeReturned() {
        assertEquals(CommandType.LIST, CommandType.fromInput("list"));
        assertEquals(CommandType.TODO, CommandType.fromInput("todo"));
    }

    @Test
    public void fromInput_commandWordWithArguments_matchingTypeReturned() {
        assertEquals(CommandType.MARK, CommandType.fromInput("mark 2"));
        assertEquals(CommandType.DEADLINE, CommandType.fromInput("deadline return book /by 2019-10-15"));
    }

    @Test
    public void fromInput_unrecognizedWord_unknownReturned() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("blah"));
    }

    @Test
    public void fromInput_emptyInput_unknownReturned() {
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput(""));
    }

    @Test
    public void fromInput_wordThatIsCommandWordPlusExtraLetters_unknownReturned() {
        // "todolist" must not accidentally match TODO just because it starts
        // with "todo" — fromInput compares the whole leading word, not a prefix.
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("todolist"));
    }

    @Test
    public void fromInput_bye_unknownReturned() {
        // "bye" is deliberately not a CommandType constant - it's handled
        // separately in main()'s loop before processCommand() is ever called.
        assertEquals(CommandType.UNKNOWN, CommandType.fromInput("bye"));
    }

    // --- parseTaskIndex ---

    @Test
    public void parseTaskIndex_validNumberWithinRange_zeroBasedIndexReturned() throws LuneException {
        assertEquals(1, Lune.parseTaskIndex("mark 2", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_lowerBoundaryOne_zeroReturned() throws LuneException {
        assertEquals(0, Lune.parseTaskIndex("mark 1", CommandType.MARK, 1));
    }

    @Test
    public void parseTaskIndex_upperBoundaryEqualsTaskCount_lastIndexReturned() throws LuneException {
        assertEquals(2, Lune.parseTaskIndex("mark 3", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_noArgumentGiven_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_onlyWhitespaceArgument_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark   ", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_nonNumericArgument_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark abc", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_zero_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark 0", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_pastTaskCount_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark 4", CommandType.MARK, 3));
    }

    @Test
    public void parseTaskIndex_emptyTaskList_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseTaskIndex("mark 1", CommandType.MARK, 0));
    }

    // --- parseDateTime ---

    @Test
    public void parseDateTime_isoDate_parsedAsStartOfDay() throws LuneException {
        assertEquals(LocalDateTime.of(2019, 10, 15, 0, 0), Lune.parseDateTime("/by", "2019-10-15"));
    }

    @Test
    public void parseDateTime_slashDateWithTime_parsedWithTime() throws LuneException {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), Lune.parseDateTime("/by", "2/12/2019 1800"));
    }

    @Test
    public void parseDateTime_garbageText_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseDateTime("/by", "not-a-date"));
    }

    @Test
    public void parseDateTime_invalidMonthInIsoFormat_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseDateTime("/by", "2019-13-45"));
    }

    @Test
    public void parseDateTime_emptyText_exceptionThrown() {
        assertThrows(LuneException.class, () -> Lune.parseDateTime("/by", ""));
    }

    @Test
    public void parseDateTime_nonExistentSlashDate_exceptionThrown() {
        // Before the STRICT resolver fix, "30/2/2019" would silently parse
        // as Feb 28 2019 instead of being rejected.
        assertThrows(LuneException.class, () -> Lune.parseDateTime("/by", "30/2/2019 1800"));
    }

    // --- collapseWhitespace ---

    @Test
    public void collapseWhitespace_internalRunOfSpaces_collapsedToOne() {
        assertEquals("read book", Lune.collapseWhitespace("read   book"));
    }

    @Test
    public void collapseWhitespace_leadingAndTrailingSpaces_stripped() {
        assertEquals("read book", Lune.collapseWhitespace("  read book  "));
    }

    @Test
    public void collapseWhitespace_alreadySingleSpaced_unchanged() {
        assertEquals("read book", Lune.collapseWhitespace("read book"));
    }

    // --- isDuplicateDescription ---

    @Test
    public void isDuplicateDescription_sameTypeSameDescription_trueReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertTrue(Lune.isDuplicateDescription(tasks, Todo.class, "read book"));
    }

    @Test
    public void isDuplicateDescription_sameTypeDifferentCase_trueReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        assertTrue(Lune.isDuplicateDescription(tasks, Todo.class, "read book"));
    }

    @Test
    public void isDuplicateDescription_differentType_falseReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertFalse(Lune.isDuplicateDescription(tasks, Deadline.class, "read book"));
    }

    @Test
    public void isDuplicateDescription_sameTypeDifferentDescription_falseReturned() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertFalse(Lune.isDuplicateDescription(tasks, Todo.class, "buy milk"));
    }

    @Test
    public void isDuplicateDescription_emptyList_falseReturned() {
        assertFalse(Lune.isDuplicateDescription(new TaskList(), Todo.class, "read book"));
    }

    // --- parseSavedTask ---

    @Test
    public void parseSavedTask_validDoneTodo_todoReturnedWithDoneStatus() {
        Task task = Lune.parseSavedTask("T | 1 | read book");
        assertInstanceOf(Todo.class, task);
        assertEquals("read book", task.getDescription());
        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void parseSavedTask_validNotDoneTodo_todoReturnedWithNotDoneStatus() {
        Task task = Lune.parseSavedTask("T | 0 | read book");
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void parseSavedTask_validDeadline_deadlineReturnedWithCorrectDate() {
        Task task = Lune.parseSavedTask("D | 0 | return book | 2019-06-06T00:00");
        assertInstanceOf(Deadline.class, task);
        assertTrue(task.occursOn(LocalDate.of(2019, 6, 6)));
        assertFalse(task.occursOn(LocalDate.of(2019, 6, 7)));
    }

    @Test
    public void parseSavedTask_validEvent_eventReturnedWithCorrectRange() {
        Task task = Lune.parseSavedTask("E | 0 | project meeting | 2019-08-06T00:00 | 2019-08-08T00:00");
        assertInstanceOf(Event.class, task);
        assertTrue(task.occursOn(LocalDate.of(2019, 8, 7)));
        assertFalse(task.occursOn(LocalDate.of(2019, 8, 9)));
    }

    @Test
    public void parseSavedTask_tooFewFields_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("this is garbage"));
    }

    @Test
    public void parseSavedTask_invalidDoneFlag_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("T | 2 | read book"));
    }

    @Test
    public void parseSavedTask_emptyDescription_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("T | 1 |  "));
    }

    @Test
    public void parseSavedTask_unknownTypeLetter_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("X | 0 | read book"));
    }

    @Test
    public void parseSavedTask_todoWithExtraFields_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("T | 0 | read book | extra"));
    }

    @Test
    public void parseSavedTask_deadlineMissingByField_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("D | 0 | return book"));
    }

    @Test
    public void parseSavedTask_deadlineWithBlankBy_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("D | 0 | return book | "));
    }

    @Test
    public void parseSavedTask_deadlineWithInvalidDate_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedTask("D | 0 | return book | not-a-date"));
    }

    @Test
    public void parseSavedTask_eventMissingToField_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () ->
                Lune.parseSavedTask("E | 0 | project meeting | 2019-08-06T00:00"));
    }

    @Test
    public void parseSavedTask_eventWithBlankTo_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () ->
                Lune.parseSavedTask("E | 0 | project meeting | 2019-08-06T00:00 | "));
    }

    // --- parseSavedDateTime ---

    @Test
    public void parseSavedDateTime_validIsoDateTime_parsedCorrectly() {
        assertEquals(LocalDateTime.of(2019, 12, 2, 18, 0), Lune.parseSavedDateTime("2019-12-02T18:00"));
    }

    @Test
    public void parseSavedDateTime_garbageText_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedDateTime("not-a-date"));
    }

    @Test
    public void parseSavedDateTime_dateOnlyMissingTimePart_exceptionThrown() {
        // LocalDateTime.parse requires the "T<time>" part; a bare date isn't
        // enough, unlike parseDateTime()'s more lenient user-input parsing.
        assertThrows(IllegalArgumentException.class, () -> Lune.parseSavedDateTime("2019-12-02"));
    }

    // --- processCommand: list ---

    @Test
    public void processCommandList_emptyList_headerOnlyNoItems() throws LuneException {
        TaskList tasks = new TaskList();
        String result = Lune.processCommand("list", tasks);
        assertTrue(result.contains("Here's what you've got going on:"));
        assertFalse(result.contains("1."));
    }

    @Test
    public void processCommandList_withTasks_numberedListingShown() throws LuneException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("buy milk"));
        String result = Lune.processCommand("list", tasks);
        assertTrue(result.contains("1.[T][ ] read book"));
        assertTrue(result.contains("2.[T][ ] buy milk"));
    }

    // --- processCommand: mark/unmark ---

    @Test
    public void processCommandMark_validIndex_taskMarkedDone() throws LuneException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Lune.processCommand("mark 1", tasks);
        assertEquals("X", tasks.get(0).getStatusIcon());
    }

    @Test
    public void processCommandMark_invalidIndex_exceptionThrownTaskListUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(LuneException.class, () -> Lune.processCommand("mark 5", tasks));
        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    @Test
    public void processCommandUnmark_validIndex_taskMarkedNotDone() throws LuneException {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        Lune.processCommand("unmark 1", tasks);
        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    // --- processCommand: delete ---

    @Test
    public void processCommandDelete_validIndex_taskRemoved() throws LuneException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        Lune.processCommand("delete 1", tasks);
        assertEquals(0, tasks.size());
    }

    @Test
    public void processCommandDelete_invalidIndex_exceptionThrownListUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertThrows(LuneException.class, () -> Lune.processCommand("delete 5", tasks));
        assertEquals(1, tasks.size());
    }

    // --- processCommand: todo ---

    @Test
    public void processCommandTodo_validDescription_taskAdded() throws LuneException {
        TaskList tasks = new TaskList();
        Lune.processCommand("todo read book", tasks);
        assertEquals(1, tasks.size());
        assertEquals("read book", tasks.get(0).getDescription());
        assertInstanceOf(Todo.class, tasks.get(0));
    }

    @Test
    public void processCommandTodo_emptyDescription_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () -> Lune.processCommand("todo", tasks));
    }

    @Test
    public void processCommandTodo_whitespaceCollapsedInStoredDescription() throws LuneException {
        TaskList tasks = new TaskList();
        Lune.processCommand("todo read   book", tasks);
        assertEquals("read book", tasks.get(0).getDescription());
    }

    @Test
    public void processCommandTodo_descriptionContainsDelimiter_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () -> Lune.processCommand("todo read | book", tasks));
    }

    @Test
    public void processCommandTodo_duplicateDescriptionAnyCase_exceptionThrown() throws LuneException {
        TaskList tasks = new TaskList();
        Lune.processCommand("todo read book", tasks);
        assertThrows(LuneException.class, () -> Lune.processCommand("todo Read Book", tasks));
    }

    @Test
    public void processCommandTodo_sameDescriptionDifferentType_notADuplicate() throws LuneException {
        TaskList tasks = new TaskList();
        Lune.processCommand("todo read book", tasks);
        Lune.processCommand("deadline read book /by 2019-10-15", tasks);
        assertEquals(2, tasks.size());
    }

    // --- processCommand: deadline ---

    @Test
    public void processCommandDeadline_valid_taskAddedWithCorrectDate() throws LuneException {
        TaskList tasks = new TaskList();
        Lune.processCommand("deadline return book /by 2019-10-15", tasks);
        assertEquals(1, tasks.size());
        assertInstanceOf(Deadline.class, tasks.get(0));
        assertTrue(tasks.get(0).occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void processCommandDeadline_emptyDescription_exceptionThrown() {
        TaskList tasks = new TaskList();
        // Two spaces after "deadline" so the (empty) description before
        // " /by " is genuinely blank, exercising that branch specifically
        // rather than the missing-/by branch.
        assertThrows(LuneException.class, () -> Lune.processCommand("deadline  /by 2019-10-15", tasks));
    }

    @Test
    public void processCommandDeadline_missingBy_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () -> Lune.processCommand("deadline return book", tasks));
    }

    @Test
    public void processCommandDeadline_emptyByText_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () -> Lune.processCommand("deadline return book /by ", tasks));
    }

    @Test
    public void processCommandDeadline_repeatedBy_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () ->
                Lune.processCommand("deadline return book /by 2019-01-01 /by 2019-02-01", tasks));
    }

    @Test
    public void processCommandDeadline_nonExistentDate_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () -> Lune.processCommand("deadline return book /by 2019-02-30", tasks));
    }

    @Test
    public void processCommandDeadline_duplicateDescription_exceptionThrown() throws LuneException {
        TaskList tasks = new TaskList();
        Lune.processCommand("deadline return book /by 2019-10-15", tasks);
        assertThrows(LuneException.class, () ->
                Lune.processCommand("deadline return book /by 2019-11-20", tasks));
    }

    // --- processCommand: event ---

    @Test
    public void processCommandEvent_valid_taskAddedWithCorrectRange() throws LuneException {
        TaskList tasks = new TaskList();
        Lune.processCommand("event meeting /from 2019-10-15 /to 2019-10-17", tasks);
        assertEquals(1, tasks.size());
        assertInstanceOf(Event.class, tasks.get(0));
        assertTrue(tasks.get(0).occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void processCommandEvent_emptyDescription_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () ->
                Lune.processCommand("event  /from 2019-10-15 /to 2019-10-17", tasks));
    }

    @Test
    public void processCommandEvent_missingFrom_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () -> Lune.processCommand("event meeting /to 2019-10-17", tasks));
    }

    @Test
    public void processCommandEvent_missingTo_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () -> Lune.processCommand("event meeting /from 2019-10-15", tasks));
    }

    @Test
    public void processCommandEvent_repeatedFrom_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () ->
                Lune.processCommand("event meeting /from 2019-10-15 /from 2019-10-16 /to 2019-10-17", tasks));
    }

    @Test
    public void processCommandEvent_repeatedTo_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () ->
                Lune.processCommand("event meeting /from 2019-10-15 /to 2019-10-17 /to 2019-10-18", tasks));
    }

    @Test
    public void processCommandEvent_toBeforeFrom_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () ->
                Lune.processCommand("event meeting /from 2019-10-15 /to 2019-10-10", tasks));
    }

    @Test
    public void processCommandEvent_toEqualsFrom_stillAccepted() throws LuneException {
        // Regression guard: a single-day event (from == to) must remain
        // valid — only a to strictly before from should be rejected.
        TaskList tasks = new TaskList();
        Lune.processCommand("event standup /from 2019-10-15 /to 2019-10-15", tasks);
        assertEquals(1, tasks.size());
    }

    @Test
    public void processCommandEvent_duplicateDescription_exceptionThrown() throws LuneException {
        TaskList tasks = new TaskList();
        Lune.processCommand("event meeting /from 2019-10-15 /to 2019-10-17", tasks);
        assertThrows(LuneException.class, () ->
                Lune.processCommand("event meeting /from 2019-11-01 /to 2019-11-02", tasks));
    }

    @Test
    public void processCommandEvent_descriptionContainsDelimiter_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () ->
                Lune.processCommand("event a | b /from 2019-10-15 /to 2019-10-17", tasks));
    }

    // --- processCommand: on ---

    @Test
    public void processCommandOn_matchingDate_taskListed() throws LuneException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 10, 15, 0, 0)));
        String result = Lune.processCommand("on 2019-10-15", tasks);
        assertTrue(result.contains("return book"));
    }

    @Test
    public void processCommandOn_noMatchingDate_emptyListing() throws LuneException {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return book", LocalDateTime.of(2019, 10, 15, 0, 0)));
        String result = Lune.processCommand("on 2019-10-20", tasks);
        assertFalse(result.contains("return book"));
    }

    @Test
    public void processCommandOn_missingDate_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () -> Lune.processCommand("on", tasks));
    }

    @Test
    public void processCommandOn_invalidDate_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () -> Lune.processCommand("on not-a-date", tasks));
    }

    // --- processCommand: find ---

    @Test
    public void processCommandFind_matchingKeyword_caseInsensitive() throws LuneException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        String result = Lune.processCommand("find book", tasks);
        assertTrue(result.contains("Read Book"));
    }

    @Test
    public void processCommandFind_noMatch_emptyListing() throws LuneException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        String result = Lune.processCommand("find xyz", tasks);
        assertFalse(result.contains("read book"));
    }

    @Test
    public void processCommandFind_missingKeyword_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () -> Lune.processCommand("find", tasks));
    }

    // --- processCommand: unknown ---

    @Test
    public void processCommand_unrecognizedCommand_exceptionThrown() {
        TaskList tasks = new TaskList();
        assertThrows(LuneException.class, () -> Lune.processCommand("blah", tasks));
    }
}
