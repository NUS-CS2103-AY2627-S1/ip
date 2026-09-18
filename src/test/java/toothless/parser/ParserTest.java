package toothless.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import toothless.command.Command;
import toothless.command.DeleteCommand;
import toothless.command.EditCommand;
import toothless.command.EventCommand;
import toothless.command.ExitCommand;
import toothless.command.FindCommand;
import toothless.command.ListCommand;
import toothless.command.MarkCommand;
import toothless.command.UnmarkCommand;
import toothless.exception.ToothlessException;
import toothless.storage.Storage;
import toothless.task.Deadline;
import toothless.task.Event;
import toothless.task.TaskList;
import toothless.task.Todo;
import toothless.ui.Ui;

/**
 * Tests command parsing, parsed values, and malformed-input validation.
 */
public class ParserTest {
    private static final String UNKNOWN_COMMAND_MESSAGE =
            "Toothless tilted his head—he doesn’t recognise that command.\n"
                    + "Try todo, deadline, event, list, find, mark, unmark, delete, edit, or bye.";

    @TempDir
    private Path temporaryDirectory;

    /**
     * Verifies commands without arguments map to their corresponding command classes.
     */
    @Test
    public void parse_commandsWithoutArguments_returnsMatchingCommandTypes()
            throws ToothlessException {
        Parser parser = new Parser();

        Command listCommand = parser.parse("  list  ", 2);
        Command exitCommand = parser.parse("bye", 2);

        assertInstanceOf(ListCommand.class, listCommand);
        assertInstanceOf(ExitCommand.class, exitCommand);
        assertFalse(listCommand.isExit());
        assertTrue(exitCommand.isExit());
    }

    @Test
    public void parse_byeWithMixedCaseAndWhitespace_returnsExitCommand()
            throws ToothlessException {
        Parser parser = new Parser();

        assertInstanceOf(ExitCommand.class, parser.parse(" Bye ", 0));
        assertInstanceOf(ExitCommand.class, parser.parse("BYE", 0));
        assertInstanceOf(ExitCommand.class, parser.parse("  bYe  ", 0));
    }

    /**
     * Verifies descriptions, dates, and event times survive parsing into created tasks.
     */
    @Test
    public void parse_addCommands_createsTasksWithParsedValues() throws ToothlessException {
        Parser parser = new Parser();
        TaskList taskList = new TaskList();

        execute(parser.parse("todo read book", 0), taskList);
        execute(parser.parse("deadline return book /by 2019-12-02", 1), taskList);
        execute(parser.parse("event project meeting /from Monday 2pm /to Monday 3pm", 2),
                taskList);

        Todo todo = assertInstanceOf(Todo.class, taskList.getTask(0));
        Deadline deadline = assertInstanceOf(Deadline.class, taskList.getTask(1));
        Event event = assertInstanceOf(Event.class, taskList.getTask(2));
        assertEquals("read book", todo.getDescription());
        assertEquals("return book", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
        assertEquals("project meeting", event.getDescription());
        assertEquals("Monday 2pm", event.getFrom());
        assertEquals("Monday 3pm", event.getTo());
    }

    /**
     * Verifies flexible surrounding whitespace is removed from parsed task values.
     */
    @Test
    public void parse_addCommandsWithExtraWhitespace_trimsParsedValues()
            throws ToothlessException {
        Parser parser = new Parser();
        TaskList taskList = new TaskList();

        execute(parser.parse("  todo    read book   ", 0), taskList);
        execute(parser.parse(" deadline   spaced date   /by   2020-02-29 ", 1), taskList);
        execute(parser.parse(" event   meeting   /from   2pm   /to   3pm  ", 2), taskList);

        assertEquals("read book", taskList.getTask(0).getDescription());
        Deadline deadline = assertInstanceOf(Deadline.class, taskList.getTask(1));
        Event event = assertInstanceOf(Event.class, taskList.getTask(2));
        assertEquals("spaced date", deadline.getDescription());
        assertEquals(LocalDate.of(2020, 2, 29), deadline.getBy());
        assertEquals("meeting", event.getDescription());
        assertEquals("2pm", event.getFrom());
        assertEquals("3pm", event.getTo());
    }

    @Test
    public void parse_eventInvalidTimesAndOrder_rejectsWithoutChangingTasks() throws ToothlessException {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        for (String input : java.util.List.of(
                "event meeting /from 2pm /to 2pm",
                "event meeting /from 3pm /to 2pm",
                "event meeting /from 25:00 /to 3pm",
                "event meeting /from 2:60pm /to 3pm",
                "event meeting /from 30 February 2026 2pm /to 3pm",
                "event meeting /from 2026-02-30 14:00 /to 16:00",
                "event meeting /from Monday 2pm /to Tuesday 3pm")) {
            assertThrows(ToothlessException.class, () -> parser.parse(input, tasks.size()), input);
        }
        execute(parser.parse("event meeting /from 21 September 2026 2pm"
                + " /to 21 September 2026 3pm", tasks.size()), tasks);
        assertEquals(1, tasks.size());
    }

    @Test
    public void parse_longInputAndDescriptions_rejectsBeforeMutation() {
        Parser parser = new Parser();
        assertThrows(ToothlessException.class, () -> parser.parse("todo " + "a".repeat(501), 0));
        assertThrows(ToothlessException.class, () -> parser.parse("todo " + "a".repeat(4097), 0));
        assertThrows(ToothlessException.class, () -> parser.parse(null, 0));
    }

    @Test
    public void parse_eventWithFullDates_checksChronologyRatherThanOnlyClock() throws ToothlessException {
        Parser parser = new Parser();
        assertThrows(ToothlessException.class, () -> parser.parse(
                "event trip /from 22 September 2026 2pm /to 21 September 2026 3pm", 0));
        assertInstanceOf(EventCommand.class, parser.parse(
                "event trip /from 21 September 2026 11pm /to 22 September 2026 1am", 0));
    }

    /**
     * Verifies parsed one-based task numbers are carried into mutating commands.
     */
    @Test
    public void parse_taskNumberCommands_executeAgainstSelectedTask() throws ToothlessException {
        Parser parser = new Parser();
        TaskList taskList = createThreeTaskList();

        Command markCommand = parser.parse("mark 2", taskList.size());
        execute(markCommand, taskList);
        Command unmarkCommand = parser.parse("unmark +2", taskList.size());
        execute(unmarkCommand, taskList);
        Command deleteCommand = parser.parse("delete 2", taskList.size());
        execute(deleteCommand, taskList);

        assertInstanceOf(MarkCommand.class, markCommand);
        assertInstanceOf(UnmarkCommand.class, unmarkCommand);
        assertInstanceOf(DeleteCommand.class, deleteCommand);
        assertFalse(taskList.getTask(0).isDone());
        assertEquals(2, taskList.size());
        assertEquals("first", taskList.getTask(0).getDescription());
        assertEquals("third", taskList.getTask(1).getDescription());
    }

    /**
     * Verifies edit fields can appear in any order and preserve omitted task values.
     */
    @Test
    public void parse_editCommands_updatesSupportedFieldsAndShowsConfirmation()
            throws ToothlessException {
        Parser parser = new Parser();
        TaskList taskList = createEditableTaskList();

        String todoOutput = parseAndExecuteEdit(parser, taskList,
                "edit 1 /description read the new textbook");
        parseAndExecuteEdit(parser, taskList,
                "edit 2 /by 2026-09-21 /description submit project");
        parseAndExecuteEdit(parser, taskList,
                "edit 3 /to 6pm /description project consultation /from 4pm");

        assertEditedTodo(taskList, todoOutput);
        assertEditedDeadline(taskList);
        assertEditedEvent(taskList);
    }

    /**
     * Creates one task of each type, including a completed deadline.
     */
    private TaskList createEditableTaskList() throws ToothlessException {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("old todo"));
        taskList.addTask(new Deadline("old deadline", LocalDate.of(2026, 9, 20)));
        taskList.addTask(new Event("old event", "2pm", "3pm"));
        taskList.markTask(2);
        return taskList;
    }

    /**
     * Checks the parsed command type and returns its confirmation output.
     */
    private String parseAndExecuteEdit(Parser parser, TaskList taskList, String input)
            throws ToothlessException {
        Command edit = parser.parse(input, taskList.size());
        assertInstanceOf(EditCommand.class, edit);
        return execute(edit, taskList);
    }

    /**
     * Checks the edited todo description and its confirmation message.
     */
    private void assertEditedTodo(TaskList taskList, String todoOutput) {
        assertEquals("A clever little roar! Toothless has updated this task:\n"
                + "  [T][ ] read the new textbook\n", todoOutput);
        assertEquals("read the new textbook", taskList.getTask(0).getDescription());
    }

    /**
     * Checks the edited deadline fields and retained completion status.
     */
    private void assertEditedDeadline(TaskList taskList) {
        Deadline deadline = assertInstanceOf(Deadline.class, taskList.getTask(1));
        assertEquals("submit project", deadline.getDescription());
        assertEquals(LocalDate.of(2026, 9, 21), deadline.getBy());
        assertTrue(deadline.isDone());
    }

    /**
     * Checks the edited event description and times.
     */
    private void assertEditedEvent(TaskList taskList) {
        Event event = assertInstanceOf(Event.class, taskList.getTask(2));
        assertEquals("project consultation", event.getDescription());
        assertEquals("4pm", event.getFrom());
        assertEquals("6pm", event.getTo());
    }

    /**
     * Verifies missing and non-numeric edit task numbers produce focused guidance.
     */
    @Test
    public void parse_editWithoutValidTaskNumber_throwsTaskNumberErrors() {
        Parser parser = new Parser();

        ToothlessException missingException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit", 3));
        ToothlessException fieldFirstException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit /description changed", 3));
        ToothlessException nonNumericException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit first /description changed", 3));

        assertEquals("Toothless needs a task number to edit.\nTry: edit 1",
                missingException.getMessage());
        assertEquals(missingException.getMessage(), fieldFirstException.getMessage());
        assertEquals("That task number looks a little unusual.\n"
                + "Please use a whole number, like: edit 1", nonNumericException.getMessage());
    }

    /**
     * Verifies an edit must contain at least one complete replacement field.
     */
    @Test
    public void parse_editWithoutFieldsOrWithEmptyValues_throwsFocusedErrors() {
        Parser parser = new Parser();
        String noFieldMessage = "Toothless needs at least one field to edit.\n"
                + "Try: edit 1 /description read the new textbook";

        ToothlessException noFieldException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit 1", 3));
        ToothlessException emptyDescriptionException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit 1 /description", 3));
        ToothlessException emptyByException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit 1 /by", 3));
        ToothlessException emptyFromException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit 1 /from", 3));
        ToothlessException emptyToException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit 1 /to", 3));

        assertEquals(noFieldMessage, noFieldException.getMessage());
        assertEquals("This edit is missing a value after '/description'.\n"
                + "Please add the new value and try again.",
                emptyDescriptionException.getMessage());
        assertEquals("This edit is missing a value after '/by'.\n"
                + "Please add the new value and try again.", emptyByException.getMessage());
        assertEquals("This edit is missing a value after '/from'.\n"
                + "Please add the new value and try again.", emptyFromException.getMessage());
        assertEquals("This edit is missing a value after '/to'.\n"
                + "Please add the new value and try again.", emptyToException.getMessage());
    }

    /**
     * Verifies malformed, unknown, and duplicate edit fields are rejected.
     */
    @Test
    public void parse_editWithMalformedFields_throwsStructureErrors() {
        Parser parser = new Parser();
        String malformedMessage = "This edit's format has Toothless puzzled.\n"
                + "Use: edit TASK_NUMBER /FIELD NEW_VALUE";

        ToothlessException strayTextException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit 1 change /description new task", 3));
        ToothlessException unknownFieldException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit 1 /when tomorrow", 3));
        ToothlessException duplicateFieldException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit 1 /description first /description second", 3));

        assertEquals(malformedMessage, strayTextException.getMessage());
        assertEquals(malformedMessage, unknownFieldException.getMessage());
        assertEquals("Each edit field can appear only once.\n"
                + "Try: edit 1 /description read the new textbook",
                duplicateFieldException.getMessage());
    }

    /**
     * Verifies invalid edited deadline dates are rejected before task mutation.
     */
    @Test
    public void parse_editWithInvalidDeadlineDate_throwsDateError() {
        Parser parser = new Parser();
        String expectedMessage = "That edited deadline date made Toothless tilt his head.\n"
                + "Please use a real date in yyyy-MM-dd format.";

        ToothlessException impossibleDateException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit 1 /by 2026-02-30", 3));
        ToothlessException wrongFormatException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit 1 /by 20 September 2026 6pm", 3));

        assertEquals(expectedMessage, impossibleDateException.getMessage());
        assertEquals(expectedMessage, wrongFormatException.getMessage());
    }

    /**
     * Verifies task-type and range validation occur before any edit is applied.
     */
    @Test
    public void parse_editWithUnsupportedFieldOrTaskNumber_preservesTasks()
            throws ToothlessException {
        Parser parser = new Parser();
        TaskList taskList = new TaskList();
        Todo todo = new Todo("todo");
        Deadline deadline = new Deadline("deadline", LocalDate.of(2026, 9, 20));
        Event event = new Event("event", "2pm", "3pm");
        taskList.addTask(todo);
        taskList.addTask(deadline);
        taskList.addTask(event);

        Command byOnTodo = parser.parse("edit 1 /description changed /by 2026-09-21", 3);
        Command fromOnDeadline = parser.parse("edit 2 /from 4pm", 3);
        Command byOnEvent = parser.parse("edit 3 /by 2026-09-21", 3);
        Command outsideRange = parser.parse("edit 4 /description changed", 3);

        assertThrows(ToothlessException.class, () -> execute(byOnTodo, taskList));
        assertThrows(ToothlessException.class, () -> execute(fromOnDeadline, taskList));
        assertThrows(ToothlessException.class, () -> execute(byOnEvent, taskList));
        assertThrows(ToothlessException.class, () -> execute(outsideRange, taskList));
        assertEquals("todo", todo.getDescription());
        assertEquals("deadline", deadline.getDescription());
        assertEquals("event", event.getDescription());
        assertEquals(3, taskList.size());
    }

    /**
     * Verifies a find keyword is trimmed and carried into a read-only find command.
     */
    @Test
    public void parse_findWithKeyword_returnsCommandUsingTrimmedKeyword()
            throws ToothlessException {
        Parser parser = new Parser();
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("read book"));

        Command command = parser.parse("   find    READ book   ", taskList.size());
        String output = execute(command, taskList);

        assertInstanceOf(FindCommand.class, command);
        assertEquals("Here are the matching tasks in your list:\n"
                + "1.[T][ ] read book\n", output);
        assertEquals(1, taskList.size());
        assertFalse(taskList.getTask(0).isDone());
    }

    /**
     * Verifies blank input and unknown command words produce distinct guidance.
     */
    @Test
    public void parse_blankAndUnknownInput_throwFocusedErrors() {
        Parser parser = new Parser();

        ToothlessException blankException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("   ", 0));
        ToothlessException unknownException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("fly", 0));

        assertEquals("Toothless heard a tiny silence. What should he do?\n"
                + "Try todo, deadline, event, list, find, mark, unmark, delete, edit, or bye.",
                blankException.getMessage());
        assertEquals(UNKNOWN_COMMAND_MESSAGE, unknownException.getMessage());
    }

    /**
     * Verifies commands that forbid details reject trailing input.
     */
    @Test
    public void parse_byeAndListWithExtraDetails_throwFocusedErrors() {
        Parser parser = new Parser();

        ToothlessException byeException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("bye later", 1));
        ToothlessException listException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("list now", 1));

        assertEquals(UNKNOWN_COMMAND_MESSAGE, byeException.getMessage());
        assertEquals("The list command doesn't need extra words.\nTry: list",
                listException.getMessage());
    }

    /**
     * Verifies task-number commands prioritise the empty-list error for unusable input.
     */
    @Test
    public void parse_taskNumberCommandsForEmptyList_throwCommandSpecificErrors() {
        Parser parser = new Parser();

        ToothlessException markException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("mark", 0));
        ToothlessException unmarkException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("unmark anything", 0));
        ToothlessException deleteException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("delete 1", 0));
        ToothlessException editException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit 1 /description changed", 0));

        assertEquals("Toothless's cave is empty, so there is no task to mark.\n"
                + "Add a task first, then try again.", markException.getMessage());
        assertEquals("Toothless's cave is empty, so there is no task to unmark.\n"
                + "Add a task first, then try again.", unmarkException.getMessage());
        assertEquals("Toothless's cave is empty, so there is no task to delete.\n"
                + "Add a task first, then try again.", deleteException.getMessage());
        assertEquals("Toothless's cave is empty, so there is no task to edit.\n"
                + "Add a task first, then try again.", editException.getMessage());
    }

    /**
     * Verifies an omitted task number names the command whose input is incomplete.
     */
    @Test
    public void parse_taskNumberCommandsWithoutNumber_throwCommandSpecificErrors() {
        Parser parser = new Parser();

        ToothlessException markException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("mark", 3));
        ToothlessException unmarkException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("unmark", 3));
        ToothlessException deleteException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("delete", 3));
        ToothlessException editException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("edit", 3));

        assertEquals("Toothless needs a task number to mark.\nTry: mark 1",
                markException.getMessage());
        assertEquals("Toothless needs a task number to unmark.\nTry: unmark 1",
                unmarkException.getMessage());
        assertEquals("Toothless needs a task number to delete.\nTry: delete 1",
                deleteException.getMessage());
        assertEquals("Toothless needs a task number to edit.\nTry: edit 1",
                editException.getMessage());
    }

    /**
     * Verifies decimals, words, and multiple arguments cannot be task numbers.
     */
    @Test
    public void parse_nonIntegerTaskNumbers_throwWholeNumberError() {
        Parser parser = new Parser();
        String expectedMessage = "That task number looks a little unusual.\n"
                + "Please use a whole number, like: mark 1";

        ToothlessException decimalException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("mark 1.5", 3));
        ToothlessException wordException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("mark second", 3));
        ToothlessException extraArgumentException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("mark 1 extra", 3));

        assertEquals(expectedMessage, decimalException.getMessage());
        assertEquals(expectedMessage, wordException.getMessage());
        assertEquals(expectedMessage, extraArgumentException.getMessage());
    }

    /**
     * Verifies syntactically numeric input outside the integer range has specific guidance.
     */
    @Test
    public void parse_taskNumberLargerThanInteger_throwsTooLargeError() {
        Parser parser = new Parser();

        ToothlessException exception = assertThrows(
                ToothlessException.class, () ->
                parser.parse("delete 999999999999999999999", 3));

        assertEquals("That task number is too large for Toothless to count.\n"
                + "Please choose a number from 1 to 3.", exception.getMessage());
    }

    /**
     * Verifies a missing todo description is rejected without producing a command.
     */
    @Test
    public void parse_todoWithoutDescription_throwsDescriptionError() {
        Parser parser = new Parser();

        ToothlessException exception = assertThrows(
                ToothlessException.class, () ->
                parser.parse("todo", 0));

        assertEquals("Toothless couldn’t find a description for that todo.\n"
                + "Try: todo borrow book", exception.getMessage());
    }

    /**
     * Verifies a missing or blank find keyword produces focused guidance.
     */
    @Test
    public void parse_findWithoutKeyword_throwsKeywordError() {
        Parser parser = new Parser();
        String expectedMessage = "Toothless needs a keyword to sniff out matching tasks.\n"
                + "Try: find book";

        ToothlessException missingException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("find", 0));
        ToothlessException blankException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("   find     ", 2));

        assertEquals(expectedMessage, missingException.getMessage());
        assertEquals(expectedMessage, blankException.getMessage());
    }

    /**
     * Verifies missing and empty deadline parts produce focused errors.
     */
    @Test
    public void parse_deadlineWithMissingParts_throwsFocusedErrors() {
        Parser parser = new Parser();

        ToothlessException separatorException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("deadline return book", 0));
        ToothlessException descriptionException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("deadline /by 2019-12-02", 0));
        ToothlessException dateException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("deadline return book /by", 0));

        assertEquals("This deadline is missing '/by' and its date.\n"
                + "Try: deadline return book /by 2019-12-02", separatorException.getMessage());
        assertEquals("Toothless couldn’t find a description for that deadline.\n"
                + "Try: deadline return book /by 2019-12-02", descriptionException.getMessage());
        assertEquals("This deadline is missing its date.\n"
                + "Try: deadline return book /by 2019-12-02", dateException.getMessage());
    }

    /**
     * Verifies ambiguous deadline separators are rejected as malformed structure.
     */
    @Test
    public void parse_deadlineWithConflictingSeparators_throwsFormatError() {
        Parser parser = new Parser();
        String expectedMessage = "This deadline's format has Toothless puzzled.\n"
                + "Please use: deadline DESCRIPTION /by yyyy-MM-dd";

        ToothlessException duplicateByException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("deadline task /by 2019-12-02 /by 2020-01-01", 0));
        ToothlessException eventSeparatorException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("deadline task /by 2019-12-02 /to evening", 0));

        assertEquals(expectedMessage, duplicateByException.getMessage());
        assertEquals(expectedMessage, eventSeparatorException.getMessage());
    }

    /**
     * Verifies strict deadline calendar validation is surfaced as a friendly parse error.
     */
    @Test
    public void parse_deadlineWithInvalidDate_throwsDateError() {
        Parser parser = new Parser();
        String expectedMessage = "That deadline date made Toothless tilt his head.\n"
                + "Please use a real date in yyyy-MM-dd format.";

        ToothlessException impossibleDateException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("deadline task /by 2019-02-30", 0));
        ToothlessException wrongFormatException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("deadline task /by 02-12-2019", 0));

        assertEquals(expectedMessage, impossibleDateException.getMessage());
        assertEquals(expectedMessage, wrongFormatException.getMessage());
    }

    /**
     * Verifies separator-like description text is accepted unless it is a complete token.
     */
    @Test
    public void parse_deadlineWithSeparatorPrefixInDescription_preservesDescription()
            throws ToothlessException {
        Parser parser = new Parser();
        TaskList taskList = new TaskList();

        execute(parser.parse("deadline review /bypass rules /by 2019-12-02", 0), taskList);

        Deadline deadline = assertInstanceOf(Deadline.class, taskList.getTask(0));
        assertEquals("review /bypass rules", deadline.getDescription());
        assertEquals(LocalDate.of(2019, 12, 2), deadline.getBy());
    }

    /**
     * Verifies absent event separators identify which time is missing.
     */
    @Test
    public void parse_eventWithMissingSeparators_throwsFocusedErrors() {
        Parser parser = new Parser();

        ToothlessException fromException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("event meeting /to 3pm", 0));
        ToothlessException toException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("event meeting /from 2pm", 0));

        assertEquals("This event is missing its starting time after '/from'.\n"
                + "Try: event DESCRIPTION /from START /to END", fromException.getMessage());
        assertEquals("This event is missing its ending time after '/to'.\n"
                + "Try: event DESCRIPTION /from START /to END", toException.getMessage());
    }

    /**
     * Verifies empty event description, start, and end values are distinguished.
     */
    @Test
    public void parse_eventWithEmptyValues_throwsFocusedErrors() {
        Parser parser = new Parser();

        ToothlessException descriptionException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("event /from 2pm /to 3pm", 0));
        ToothlessException fromException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("event meeting /from /to 3pm", 0));
        ToothlessException toException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("event meeting /from 2pm /to", 0));

        assertEquals("Toothless couldn’t find a description for that event.\n"
                + "Try: event DESCRIPTION /from START /to END", descriptionException.getMessage());
        assertEquals("This event is missing its starting time.\n"
                + "Try: event DESCRIPTION /from START /to END", fromException.getMessage());
        assertEquals("This event is missing its ending time.\n"
                + "Try: event DESCRIPTION /from START /to END", toException.getMessage());
    }

    /**
     * Verifies out-of-order and duplicate event separators cannot be ambiguous.
     */
    @Test
    public void parse_eventWithConflictingSeparators_throwsStructureError() {
        Parser parser = new Parser();

        ToothlessException orderException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("event meeting /to 3pm /from 2pm", 0));
        ToothlessException duplicateException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("event meeting /from 1pm /from 2pm /to 3pm", 0));
        ToothlessException deadlineSeparatorException = assertThrows(
                ToothlessException.class, () ->
                parser.parse("event meeting /from 2pm /to 3pm /by Friday", 0));

        assertEquals("The event's '/from' must come before '/to'.\n"
                + "Try: event DESCRIPTION /from START /to END", orderException.getMessage());
        assertEquals("This event's format has Toothless puzzled.\n"
                + "Try: event DESCRIPTION /from START /to END", duplicateException.getMessage());
        assertEquals("This event's format has Toothless puzzled.\n"
                + "Try: event DESCRIPTION /from START /to END",
                deadlineSeparatorException.getMessage());
    }

    @Test
    public void parse_commandKeywords_onlyByeIgnoresCase() throws ToothlessException {
        Parser parser = new Parser();

        for (String keyword : java.util.List.of("LIST", "Todo book", "Mark 1", "HELP", "help")) {
            ToothlessException error = assertThrows(ToothlessException.class, () ->
                    parser.parse(keyword, 1), keyword);
            assertEquals(UNKNOWN_COMMAND_MESSAGE, error.getMessage());
        }
        assertInstanceOf(ExitCommand.class, parser.parse("  bYe  ", 1));
    }

    @Test
    public void parse_shortAndMaximumDescriptions_preservesUnicodeAndPunctuation()
            throws ToothlessException {
        Parser parser = new Parser();
        TaskList tasks = new TaskList();
        String longDescription = "书".repeat(500);

        execute(parser.parse("todo !", 0), tasks);
        execute(parser.parse("todo " + longDescription, 1), tasks);
        execute(parser.parse("deadline 读书! /by 2024-02-29", 2), tasks);
        execute(parser.parse("event 朋友聚会! /from 2pm /to 3pm", 3), tasks);

        assertEquals("!", tasks.getTask(0).getDescription());
        assertEquals(longDescription, tasks.getTask(1).getDescription());
        assertEquals("读书!", tasks.getTask(2).getDescription());
        assertEquals("朋友聚会!", tasks.getTask(3).getDescription());
        assertEquals(4, tasks.size());
    }

    @Test
    public void parse_findLengthBoundary_accepts500AndRejects501() throws ToothlessException {
        Parser parser = new Parser();

        assertInstanceOf(FindCommand.class, parser.parse("find " + "a".repeat(500), 0));
        ToothlessException error = assertThrows(ToothlessException.class, () ->
                parser.parse("find " + "a".repeat(501), 0));
        assertEquals("That search is too long for Toothless.\n"
                + "Try a shorter keyword (at most 500 characters).", error.getMessage());
    }

    /**
     * Executes a parsed command with isolated output and temporary storage.
     */
    private String execute(Command command, TaskList taskList) throws ToothlessException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Ui ui = new Ui(new ByteArrayInputStream(new byte[0]),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        Storage storage = new Storage(temporaryDirectory.resolve("parser-tasks.txt"));

        command.execute(taskList, ui, storage);
        return output.toString(StandardCharsets.UTF_8)
                .replace(System.lineSeparator(), "\n");
    }

    /**
     * Creates a representative ordered list for parsed task-number commands.
     */
    private TaskList createThreeTaskList() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("first"));
        taskList.addTask(new Todo("second"));
        taskList.addTask(new Todo("third"));
        return taskList;
    }
}
