package ff15;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ff15.command.Command;
import ff15.command.DeleteCommand;
import ff15.command.MarkCommand;
import ff15.task.TaskList;
import ff15.task.Todo;

/**
 * Tests the lines {@link Parser#parse(String)} must refuse: a missing or
 * unreadable argument, an impossible event, a duplicate task, and the save
 * file's reserved character. Where the message matters, it is checked too.
 */
public class ParserRejectionTest extends ParserTestBase {

    // --- lines that should be rejected ---------------------------------------

    @Test
    public void parse_unknownCommand_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("blah"));
        assertThrows(FF15Exception.class, () -> Parser.parse(""));
        assertThrows(FF15Exception.class, () -> Parser.parse("Todo read book"));
        assertThrows(FF15Exception.class, () -> Parser.parse("mark2"));
    }

    @Test
    public void parse_todoWithoutDescription_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("todo"));
        assertThrows(FF15Exception.class, () -> Parser.parse("todo   "));
    }

    @Test
    public void parse_taskNumberMissing_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("mark"));
        assertThrows(FF15Exception.class, () -> Parser.parse("unmark   "));
        assertThrows(FF15Exception.class, () -> Parser.parse("delete"));
    }

    @Test
    public void parse_taskNumberNotANumber_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("mark two"));
        assertThrows(FF15Exception.class, () -> Parser.parse("delete 1.5"));
        assertThrows(FF15Exception.class, () -> Parser.parse("unmark 1 2"));
    }

    @Test
    public void parse_taskNumberOutOfRange_isLeftForTheTaskListToReject() throws FF15Exception {
        // Parsing only checks that a number was typed; whether it names a task is
        // something only the list knows, so the complaint comes at execute time.
        Command command = Parser.parse("mark 99");
        TaskList empty = new TaskList();
        assertThrows(FF15Exception.class, () -> run(empty, "mark 99"));
        assertInstanceOf(MarkCommand.class, command);
    }

    @Test
    public void parse_negativeTaskNumber_parsesButIsRejectedOnExecute() throws FF15Exception {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete -1"));
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        assertThrows(FF15Exception.class, () -> run(tasks, "delete -1"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void parse_deadlineWithoutBy_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("deadline return book"));
        assertThrows(FF15Exception.class, () -> Parser.parse("deadline"));
    }

    @Test
    public void parse_deadlineWithEmptyDate_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("deadline return book /by"));
        assertThrows(FF15Exception.class, () -> Parser.parse("deadline return book /by   "));
    }

    @Test
    public void parse_deadlineWithUnreadableDate_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("deadline return book /by sunday"));
        assertThrows(FF15Exception.class, () -> Parser.parse("deadline return book /by 2019-13-02"));
        assertThrows(FF15Exception.class, () -> Parser.parse("deadline return book /by 2019-12-02 2500"));
    }

    @Test
    public void parse_eventMissingFromOrTo_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("event meeting"));
        assertThrows(FF15Exception.class, () -> Parser.parse("event meeting /from 2019-12-05"));
        assertThrows(FF15Exception.class, () -> Parser.parse("event meeting /to 2019-12-05"));
        assertThrows(FF15Exception.class, () -> Parser.parse("event"));
    }

    @Test
    public void parse_eventWithToBeforeFrom_throwsException() {
        assertThrows(FF15Exception.class, () ->
                Parser.parse("event meeting /to 2019-12-06 /from 2019-12-05"));
    }

    @Test
    public void parse_eventWithEmptyEnds_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("event meeting /from /to 2019-12-06"));
        assertThrows(FF15Exception.class, () -> Parser.parse("event meeting /from 2019-12-05 /to"));
    }

    @Test
    public void parse_eventEndingBeforeItStarts_throwsException() {
        assertThrows(FF15Exception.class, () ->
                Parser.parse("event meeting /from 2019-12-06 /to 2019-12-05"));
        assertThrows(FF15Exception.class, () ->
                Parser.parse("event meeting /from 2019-12-05 1600 /to 2019-12-05 1400"));
    }

    @Test
    public void parse_eventStartingAndEndingAtTheSameMoment_throwsException() {
        FF15Exception thrown = assertThrows(FF15Exception.class, () ->
                Parser.parse("event meeting /from 2019-12-05 1400 /to 2019-12-05 1400"));
        assertTrue(thrown.getMessage().contains("moment"), thrown.getMessage());
        // A date-only end is the start of that day, so it is the same moment as 0000.
        assertThrows(FF15Exception.class, () ->
                Parser.parse("event meeting /from 2019-12-05 0000 /to 2019-12-05"));
    }

    @Test
    public void parse_taskAlreadyInTheList_isRefusedAndNamesIt() throws Exception {
        TaskList tasks = new TaskList();
        run(tasks, "todo read book");
        run(tasks, "todo buy milk");

        FF15Exception thrown = assertThrows(FF15Exception.class, () -> run(tasks, "todo read book"));

        assertTrue(thrown.getMessage().contains("task 1"), thrown.getMessage());
        assertEquals(2, tasks.size());
    }

    @Test
    public void parse_taskDifferingOnlyInDate_isNotADuplicate() throws Exception {
        TaskList tasks = new TaskList();
        run(tasks, "deadline return book /by 2019-12-02");
        run(tasks, "deadline return book /by 2019-12-03");
        assertEquals(2, tasks.size());
    }

    @Test
    public void parse_findWithoutAKeyword_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("find"));
        assertThrows(FF15Exception.class, () -> Parser.parse("find   "));
    }

    @Test
    public void parse_onWithoutADate_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("on"));
        assertThrows(FF15Exception.class, () -> Parser.parse("on   "));
    }

    @Test
    public void parse_onWithUnreadableDate_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("on someday"));
        assertThrows(FF15Exception.class, () -> Parser.parse("on 2019-12-02-03"));
    }

    @Test
    public void parse_unknownCommand_messageExplainsItWasNotUnderstood() {
        FF15Exception thrown = assertThrows(FF15Exception.class, () -> Parser.parse("blah"));
        assertTrue(thrown.getMessage().contains("don't know what that means"), thrown.getMessage());
    }

    @Test
    public void parse_deadlineWithMarkerButNoDescription_saysTheDescriptionIsMissing() {
        FF15Exception thrown = assertThrows(FF15Exception.class, () ->
                Parser.parse("deadline /by 2019-12-02"));
        assertTrue(thrown.getMessage().contains("description"), thrown.getMessage());
    }

    @Test
    public void parse_eventWithMarkersButNoDescription_saysTheDescriptionIsMissing() {
        FF15Exception thrown = assertThrows(FF15Exception.class, () ->
                Parser.parse("event /from 2019-12-05 /to 2019-12-06"));
        assertTrue(thrown.getMessage().contains("description"), thrown.getMessage());
    }

    @Test
    public void parse_taskNumberMissing_exampleNamesTheCommandTyped() {
        FF15Exception fromDelete = assertThrows(FF15Exception.class, () -> Parser.parse("delete"));
        assertTrue(fromDelete.getMessage().contains("e.g. delete 2"), fromDelete.getMessage());
        FF15Exception fromUnmark = assertThrows(FF15Exception.class, () -> Parser.parse("unmark"));
        assertTrue(fromUnmark.getMessage().contains("e.g. unmark 2"), fromUnmark.getMessage());
    }

    // --- the save file's reserved character ----------------------------------

    @Test
    public void parse_descriptionContainingTheSeparator_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("todo read | book"));
        assertThrows(FF15Exception.class, () -> Parser.parse("deadline a|b /by 2019-12-02"));
        assertThrows(FF15Exception.class, () -> Parser.parse("event x | y /from 2019-12-05 /to 2019-12-06"));
    }

    @Test
    public void parse_contactFieldContainingTheSeparator_throwsException() {
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add John | Smith"));
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add John /email a|b@example.com"));
    }

    @Test
    public void parse_separatorRejection_explainsWhy() {
        FF15Exception thrown = assertThrows(FF15Exception.class, () -> Parser.parse("todo read | book"));
        assertTrue(thrown.getMessage().contains("save"), thrown.getMessage());
    }
}
