package lebron.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import lebron.command.Command;
import lebron.exception.LeBronException;
import lebron.task.TaskList;
import lebron.task.Todo;
import lebron.ui.Ui;

public class ParserTest {
    /**
     * Parses the input and executes the resulting Command against the given TaskList,
     * so we can verify what Parser produced by checking its observable effect
     * (Command's fields are private with no getters) and its reply message.
     */
    private String parseAndExecute(String input, TaskList taskList) throws LeBronException {
        Command command = Parser.parse(input);
        return command.execute(taskList, new Ui());
    }

    @Test
    public void parse_bye_returnsCommandThatExits() throws LeBronException {
        Command command = Parser.parse("bye");
        assertTrue(command.isExit());
    }

    @Test
    public void parse_list_returnsCommandThatShowsTaskList() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());

        String response = parseAndExecute("list", taskList);

        assertEquals(taskList.toString(), response);
    }

    @Test
    public void parse_todoValid_addsTodoWithCorrectDescription() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());

        String response = parseAndExecute("todo read book", taskList);

        assertEquals(1, taskList.size());
        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void parse_todoMissingDescription_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("todo"));
    }

    @Test
    public void parse_todoBlankDescription_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("todo    "));
    }

    @Test
    public void parse_inputWithLeadingAndTrailingWhitespace_stillParsedCorrectly() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());

        String response = parseAndExecute("   todo read book   ", taskList);

        assertEquals(1, taskList.size());
        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void parse_duplicateTodo_exceptionThrownAndListUnchanged() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        parseAndExecute("todo read book", taskList);

        assertThrows(LeBronException.class, () -> parseAndExecute("todo read book", taskList));
        assertEquals(1, taskList.size());
    }

    @Test
    public void parse_deadlineValid_addsDeadlineWithCorrectDescriptionAndDate() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());

        String response = parseAndExecute("deadline return book by 2019-10-15 1800", taskList);

        assertEquals(1, taskList.size());
        assertTrue(response.contains("[D][ ] return book (by: Oct 15 2019, 6:00 PM)"));
    }

    @Test
    public void parse_deadlineMissingDescription_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("deadline"));
    }

    @Test
    public void parse_deadlineMissingByKeyword_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("deadline return book 2019-10-15 1800"));
    }

    @Test
    public void parse_deadlineMalformedDate_dateTimeParseExceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> Parser.parse("deadline return book by not-a-date"));
    }

    @Test
    public void parse_deadlineNonExistentDate_dateTimeParseExceptionThrown() {
        assertThrows(DateTimeParseException.class, () -> Parser.parse("deadline return book by 2019-02-30 1800"));
    }

    @Test
    public void parse_deadlineDuplicateByKeyword_exceptionThrown() {
        assertThrows(LeBronException.class, () ->
                Parser.parse("deadline return book by 2019-10-15 1800 by 2019-10-20 1800"));
    }

    @Test
    public void parse_deadlineDescriptionWithSurroundingWhitespace_trimmedCorrectly() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());

        String response = parseAndExecute("deadline  return book   by 2019-10-15 1800", taskList);

        assertTrue(response.contains("[D][ ] return book (by: Oct 15 2019, 6:00 PM)"));
    }

    @Test
    public void parse_eventValid_addsEventWithCorrectDescriptionAndDates() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());

        String response = parseAndExecute(
                "event project meeting from 2019-10-16 0900 to 2019-10-16 1100", taskList);

        assertEquals(1, taskList.size());
        assertTrue(response.contains("[E][ ] project meeting (from Oct 16 2019, 9:00 AM til Oct 16 2019, 11:00 AM)"));
    }

    @Test
    public void parse_eventMissingDescription_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("event"));
    }

    @Test
    public void parse_eventMissingFromToKeywords_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("event project meeting 2019-10-16 0900"));
    }

    @Test
    public void parse_eventEndBeforeStart_exceptionThrown() {
        assertThrows(LeBronException.class, () ->
                Parser.parse("event project meeting from 2019-10-16 1100 to 2019-10-16 0900"));
    }

    @Test
    public void parse_eventEndEqualsStart_exceptionThrown() {
        assertThrows(LeBronException.class, () ->
                Parser.parse("event project meeting from 2019-10-16 0900 to 2019-10-16 0900"));
    }

    @Test
    public void parse_eventDuplicateFromKeyword_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse(
                "event project meeting from 2019-10-16 0900 from 2019-10-16 1000 to 2019-10-16 1100"));
    }

    @Test
    public void parse_eventDescriptionWithSurroundingWhitespace_trimmedCorrectly() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());

        String response = parseAndExecute(
                "event  project meeting   from 2019-10-16 0900 to 2019-10-16 1100", taskList);

        assertTrue(response.contains("[E][ ] project meeting (from Oct 16 2019, 9:00 AM til Oct 16 2019, 11:00 AM)"));
    }

    @Test
    public void parse_markValid_marksCorrectTask() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));

        String response = parseAndExecute("mark 1", taskList);

        assertTrue(response.contains("[T][X] read book"));
    }

    @Test
    public void parse_markWithExtraSpacesBetweenWordAndNumber_stillParsedCorrectly() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));

        String response = parseAndExecute("mark   1", taskList);

        assertTrue(response.contains("[T][X] read book"));
    }

    @Test
    public void parse_markMissingTaskNumber_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("mark"));
    }

    @Test
    public void parse_markTooManyTaskNumbers_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("mark 1 2"));
    }

    @Test
    public void parse_markNonNumericTaskNumber_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("mark abc"));
    }

    @Test
    public void parse_unmarkValid_unmarksCorrectTask() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));
        taskList.getTask(1).setStatus(true);

        String response = parseAndExecute("unmark 1", taskList);

        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void parse_deleteValid_deletesCorrectTask() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));

        String response = parseAndExecute("delete 1", taskList);

        assertEquals(0, taskList.size());
        assertTrue(response.contains("[T][ ] read book"));
    }

    @Test
    public void parse_findValid_reportsOnlyMatchingTasksToUi() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());
        taskList.addTask(new Todo("read book"));
        taskList.addTask(new Todo("write essay"));

        String response = parseAndExecute("find book", taskList);

        assertTrue(response.contains("[T][ ] read book"));
        assertEquals(2, response.split("\n").length);
    }

    @Test
    public void parse_findMissingKeyword_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("find"));
    }

    @Test
    public void parse_findBlankKeyword_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("find    "));
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("frobnicate"));
    }

    @Test
    public void parse_recurValid_addsRecurringTaskWithCorrectDescriptionIntervalAndDate() throws LeBronException {
        TaskList taskList = new TaskList(new ArrayList<>());

        String response = parseAndExecute("recur project meeting every week from 2019-10-15 1800", taskList);

        assertEquals(1, taskList.size());
        assertTrue(response.contains("[R][ ] project meeting (every week, next due: Oct 15 2019, 6:00 PM)"));
    }

    @Test
    public void parse_recurMissingDescription_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("recur"));
    }

    @Test
    public void parse_recurMissingEveryFromKeywords_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse("recur project meeting week 2019-10-15 1800"));
    }

    @Test
    public void parse_recurUnknownInterval_exceptionThrown() {
        assertThrows(LeBronException.class, () ->
                Parser.parse("recur project meeting every fortnight from 2019-10-15 1800"));
    }

    @Test
    public void parse_recurMalformedDate_dateTimeParseExceptionThrown() {
        assertThrows(DateTimeParseException.class, () ->
                Parser.parse("recur project meeting every week from not-a-date"));
    }

    @Test
    public void parse_recurDuplicateFromKeyword_exceptionThrown() {
        assertThrows(LeBronException.class, () -> Parser.parse(
                "recur project meeting every week from 2019-10-15 1800 from 2019-10-16 1800"));
    }
}
