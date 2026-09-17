package nubish.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Tests parser validation for malformed command input.
 */
public class ParserTest {
    private TaskList taskList = new TaskList();
    private UI ui = new UI();
    private Parser parser = new Parser(null, ui, taskList);

    /**
     * Verifies that the bye command stops parsing.
     */
    @Test
    public void parse_byeCommand_returnsFalse() {
        assertFalse(parser.parse("bye"));
        assertTrue(ui.getLastResponse().contains("Sayonara"));
    }

    /**
     * Verifies that blank input shows the command list.
     */
    @Test
    public void parse_blankInput_showsCommandList() {
        parser.parse(" ");

        assertTrue(ui.getLastResponse().contains("Here is a list of the current commands"));
        assertTrue(ui.isLastResponseError());
    }

    /**
     * Verifies that unknown commands show the command list.
     */
    @Test
    public void parse_unknownCommand_showsCommandList() {
        parser.parse("dance");

        assertTrue(ui.getLastResponse().contains("Here is a list of the current commands"));
        assertTrue(ui.isLastResponseError());
    }

    /**
     * Verifies that list displays the current tasks.
     */
    @Test
    public void parse_listCommand_showsTaskList() {
        parser.parse("todo read book");

        parser.parse("list");

        assertTrue(ui.getLastResponse().contains("1. [T][ ] read book"));
    }

    /**
     * Verifies that a valid todo command adds a todo task.
     */
    @Test
    public void parse_validTodo_addsTodo() {
        parser.parse("todo read book");

        assertEquals(1, taskList.size());
        assertEquals("read book", taskList.get(0).getDescription());
        assertTrue(ui.getLastResponse().contains("todo task added"));
    }

    /**
     * Verifies that a todo command rejects unexpected argument tokens.
     */
    @Test
    public void parse_todoWithArgumentToken_showsTodoDescriptionMessage() {
        parser.parse("todo read book /by today");

        assertTrue(ui.getLastResponse().contains("description of a todo cannot be empty"));
        assertTrue(ui.isLastResponseError());
    }

    /**
     * Verifies that task details cannot contain the storage delimiter.
     */
    @Test
    public void parse_todoWithStorageDelimiter_showsDelimiterMessage() {
        parser.parse("todo read | book");

        assertTrue(ui.getLastResponse().contains("Please do not use | in task details"));
        assertTrue(ui.isLastResponseError());
    }

    /**
     * Verifies that leading spaces are rejected with the command's format guidance.
     */
    @Test
    public void parse_leadingSpaceInDeadline_showsDeadlineFormat() {
        parser.parse(" deadline read book /by 12/10/2020 1800");

        assertTrue(ui.getLastResponse().contains("deadline {taskname} /by {deadline}"));
    }

    /**
     * Verifies that duplicated deadline parameters are rejected.
     */
    @Test
    public void parse_duplicateDeadlineParameter_showsDeadlineFormat() {
        parser.parse("deadline read book /by 12/10/2020 1800 /by 13/10/2020 1800");

        assertTrue(ui.getLastResponse().contains("deadline {taskname} /by {deadline}"));
    }

    /**
     * Verifies that a valid deadline command adds a deadline task.
     */
    @Test
    public void parse_validDeadline_addsDeadline() {
        parser.parse("deadline read book /by 12/10/2020 1800");

        assertEquals(1, taskList.size());
        assertEquals("read book", taskList.get(0).getDescription());
        assertTrue(ui.getLastResponse().contains("Added task: read book"));
    }

    /**
     * Verifies that a deadline command with a wrong date format shows deadline format guidance.
     */
    @Test
    public void parse_deadlineWrongDateTimeFormat_showsDeadlineFormat() {
        parser.parse("deadline read book /by 12/10/20 1800");

        assertTrue(ui.getLastResponse().contains("deadline {taskname} /by {deadline}"));
    }

    /**
     * Verifies that a valid event command adds an event task.
     */
    @Test
    public void parse_validEvent_addsEvent() {
        parser.parse("event meeting /from 12/10/2020 1800 /to 12/10/2020 1900");

        assertEquals(1, taskList.size());
        assertEquals("meeting", taskList.get(0).getDescription());
        assertTrue(ui.getLastResponse().contains("Added event: meeting"));
    }

    /**
     * Verifies that duplicated event parameters are rejected.
     */
    @Test
    public void parse_duplicateEventParameter_showsEventFormat() {
        parser.parse("event meeting /from 12/10/2020 1800 /from 12/10/2020 1830 /to 12/10/2020 1900");

        assertTrue(ui.getLastResponse().contains("event {eventName} /from {startDate} /to {enddate}"));
    }

    /**
     * Verifies that malformed event date-times show event syntax and date-order guidance.
     */
    @Test
    public void parse_invalidEventDateTime_showsEventDateTimeFormat() {
        parser.parse("event meeting /from 30/02/2020 1800 /to 31/02/2020 1800");

        assertTrue(ui.getLastResponse().contains("event {eventName} /from {startDate} /to {enddate}"));
        assertTrue(ui.getLastResponse().contains("Start times must be earlier than end times."));
    }

    /**
     * Verifies that invalid task indexes are reported as user-facing errors.
     */
    @Test
    public void parse_invalidMarkIndex_showsTaskNumberMessage() {
        parser.parse("mark abc");

        assertTrue(ui.getLastResponse().contains("Please put a valid task number"));
    }

    /**
     * Verifies that mark updates an existing task.
     */
    @Test
    public void parse_validMark_marksTask() {
        parser.parse("todo read book");

        parser.parse("mark 1");

        assertEquals("X", taskList.get(0).getStatusIcon());
        assertTrue(ui.getLastResponse().contains("marked this task as done"));
    }

    /**
     * Verifies that unmark updates an existing task.
     */
    @Test
    public void parse_validUnmark_unmarksTask() {
        parser.parse("todo read book");
        parser.parse("mark 1");

        parser.parse("unmark 1");

        assertEquals(" ", taskList.get(0).getStatusIcon());
        assertTrue(ui.getLastResponse().contains("I've unmarked this task"));
    }

    /**
     * Verifies that out-of-range task indexes are reported as user-facing errors.
     */
    @Test
    public void parse_markOutOfRange_showsTaskNumberMessage() {
        parser.parse("mark 1");

        assertTrue(ui.getLastResponse().contains("Please put a valid task number"));
    }

    /**
     * Verifies that delete removes an existing task.
     */
    @Test
    public void parse_validDelete_removesTask() {
        parser.parse("todo read book");

        parser.parse("delete 1");

        assertEquals(0, taskList.size());
        assertTrue(ui.getLastResponse().contains("I have removed this task"));
    }

    /**
     * Verifies that invalid delete indexes are reported as user-facing errors.
     */
    @Test
    public void parse_invalidDeleteIndex_showsTaskNumberMessage() {
        parser.parse("delete abc");

        assertTrue(ui.getLastResponse().contains("Please put a valid task number"));
    }

    /**
     * Verifies that find rejects empty keywords.
     */
    @Test
    public void parse_emptyFindKeyword_showsKeywordMessage() {
        parser.parse("find");

        assertTrue(ui.getLastResponse().contains("Please enter keywords to search for"));
    }

    /**
     * Verifies that find displays matching tasks.
     */
    @Test
    public void parse_validFind_showsMatchingTasks() {
        parser.parse("todo read book");
        parser.parse("todo write code");

        parser.parse("find read");

        assertTrue(ui.getLastResponse().contains("1. [T][ ] read book"));
    }

    /**
     * Verifies that duplicate task descriptions are shown as parser errors.
     */
    @Test
    public void parse_duplicateTaskDescription_showsDuplicateMessage() {
        parser.parse("todo read book");

        parser.parse("todo read book");

        assertTrue(ui.getLastResponse().contains("same details already exists"));
        assertTrue(ui.isLastResponseError());
    }
}
