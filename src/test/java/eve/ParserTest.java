package eve;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import eve.command.Command;
import eve.command.DeleteCommand;
import eve.command.ExitCommand;
import eve.command.FindCommand;
import eve.command.HelpCommand;
import eve.command.ListCommand;
import eve.command.MarkCommand;
import eve.command.OnCommand;
import eve.command.ScheduleCommand;
import eve.command.UnmarkCommand;
import eve.task.TaskList;

/**
 * Tests {@link Parser#parse}: which {@link Command} each command word
 * dispatches to, and every validation error it can raise along the way.
 * A successfully-parsed add command is verified by executing it against a
 * real {@link TaskList} and checking the resulting task's {@code toString()},
 * since the parsed {@code Task} itself isn't otherwise exposed.
 */
public class ParserTest {
    @TempDir
    private Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("eve.txt").toString());
    }

    // ----- command word dispatch -----

    @Test
    public void parse_help_returnsHelpCommand() throws EveException {
        assertInstanceOf(HelpCommand.class, Parser.parse("help"));
    }

    @Test
    public void parse_bye_returnsCommandThatIsExit() throws EveException {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_list_returnsListCommand() throws EveException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_mark_returnsMarkCommand() throws EveException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
    }

    @Test
    public void parse_unmark_returnsUnmarkCommand() throws EveException {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
    }

    @Test
    public void parse_delete_returnsDeleteCommand() throws EveException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_on_returnsOnCommand() throws EveException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
    }

    @Test
    public void parse_find_returnsFindCommand() throws EveException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_schedule_returnsScheduleCommand() throws EveException {
        assertInstanceOf(ScheduleCommand.class, Parser.parse("schedule"));
    }

    @Test
    public void parse_caseInsensitiveCommandWord_stillDispatches() throws EveException {
        assertInstanceOf(ListCommand.class, Parser.parse("LIST"));
    }

    @Test
    public void parse_unknownWord_throws() {
        EveException exception = assertThrows(EveException.class, () -> Parser.parse("blah"));
        assertTrue(exception.getMessage().contains("don't recognize"));
    }

    @Test
    public void parse_leadingWhitespaceBeforeCommandWord_stillDispatches() throws EveException {
        assertInstanceOf(ListCommand.class, Parser.parse("   list"));
    }

    @Test
    public void parse_trailingWhitespaceAfterCommand_stillDispatches() throws EveException {
        assertInstanceOf(ListCommand.class, Parser.parse("list   "));
    }

    // ----- todo -----

    @Test
    public void parse_todoWithDescription_addsToDoWithThatDescription() throws EveException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, new Ui(), newStorage());

        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void parse_todoEmptyDescription_throws() {
        EveException exception = assertThrows(EveException.class, () -> Parser.parse("todo"));
        assertTrue(exception.getMessage().contains("needs a description"));
    }

    @Test
    public void parse_todoDescriptionWithPipe_throws() {
        assertThrows(EveException.class, () -> Parser.parse("todo buy milk | eggs"));
    }

    // ----- deadline -----

    @Test
    public void parse_deadlineWellFormed_addsDeadlineWithDescriptionAndDate() throws EveException {
        TaskList tasks = new TaskList();
        Parser.parse("deadline return book /by 2019-12-02").execute(tasks, new Ui(), newStorage());

        assertEquals("[D][ ] return book (by: Dec 2 2019)", tasks.get(0).toString());
    }

    @Test
    public void parse_deadlineMissingByMarker_throws() {
        EveException exception = assertThrows(EveException.class, () -> Parser.parse("deadline return book"));
        assertTrue(exception.getMessage().contains("/by"));
    }

    @Test
    public void parse_deadlineEmptyDescription_throws() {
        assertThrows(EveException.class, () -> Parser.parse("deadline /by 2019-12-02"));
    }

    @Test
    public void parse_deadlineRepeatedByMarker_throws() {
        EveException exception = assertThrows(EveException.class, () ->
                Parser.parse("deadline task /by 2020-01-01 /by 2020-02-02"));
        assertTrue(exception.getMessage().contains("more than once") || exception.getMessage().contains("twice")
                || exception.getMessage().contains("two"));
    }

    @Test
    public void parse_deadlineDescriptionWithPipe_throws() {
        assertThrows(EveException.class, () -> Parser.parse("deadline pay | rent /by 2020-01-01"));
    }

    @Test
    public void parse_deadlineInvalidDate_throws() {
        EveException exception = assertThrows(EveException.class, () ->
                Parser.parse("deadline return book /by Sunday"));
        assertTrue(exception.getMessage().contains("yyyy-mm-dd"));
    }

    @Test
    public void parse_deadlineNonExistentDate_throws() {
        // February the 30th doesn't exist in any year.
        assertThrows(EveException.class, () -> Parser.parse("deadline return book /by 2019-02-30"));
    }

    // ----- event -----

    @Test
    public void parse_eventWellFormed_addsEventWithDescriptionAndDates() throws EveException {
        TaskList tasks = new TaskList();
        Parser.parse("event trip /from 2019-10-04 /to 2019-10-11").execute(tasks, new Ui(), newStorage());

        assertEquals("[E][ ] trip (from: Oct 4 2019 to: Oct 11 2019)", tasks.get(0).toString());
    }

    @Test
    public void parse_eventMissingFromMarker_throws() {
        assertThrows(EveException.class, () -> Parser.parse("event trip /to 2019-10-11"));
    }

    @Test
    public void parse_eventMissingToMarker_throws() {
        assertThrows(EveException.class, () -> Parser.parse("event trip /from 2019-10-04"));
    }

    @Test
    public void parse_eventToBeforeFromMarkerInText_throws() {
        // "/to" appearing (in the text) before "/from" is treated the same as a missing marker.
        assertThrows(EveException.class, () -> Parser.parse("event trip /to 2019-10-11 /from 2019-10-04"));
    }

    @Test
    public void parse_eventEmptyFromDate_throws() {
        assertThrows(EveException.class, () -> Parser.parse("event trip /from /to 2019-10-11"));
    }

    @Test
    public void parse_eventRepeatedFromMarker_throws() {
        EveException exception = assertThrows(EveException.class, () ->
                Parser.parse("event trip /from 2020-01-01 /from 2020-01-02 /to 2020-01-03"));
        assertTrue(exception.getMessage().contains("/from"));
    }

    @Test
    public void parse_eventRepeatedToMarker_throws() {
        EveException exception = assertThrows(EveException.class, () ->
                Parser.parse("event trip /from 2020-01-01 /to 2020-01-02 /to 2020-01-03"));
        assertTrue(exception.getMessage().contains("/to"));
    }

    @Test
    public void parse_eventDescriptionWithPipe_throws() {
        assertThrows(EveException.class, () -> Parser.parse("event trip | home /from 2020-01-01 /to 2020-01-02"));
    }

    @Test
    public void parse_eventEndsBeforeItStarts_throws() {
        EveException exception = assertThrows(EveException.class, () ->
                Parser.parse("event trip /from 2020-05-10 /to 2020-05-01"));
        assertTrue(exception.getMessage().contains("ends before it starts"));
    }

    @Test
    public void parse_eventSameStartAndEndDate_addsSingleDayEvent() throws EveException {
        TaskList tasks = new TaskList();
        Parser.parse("event day trip /from 2020-05-10 /to 2020-05-10").execute(tasks, new Ui(), newStorage());

        assertEquals("[E][ ] day trip (from: May 10 2020 to: May 10 2020)", tasks.get(0).toString());
    }

    // ----- on -----

    @Test
    public void parse_onWithDate_returnsOnCommandThatFindsMatches() throws Exception {
        TaskList tasks = new TaskList();
        Parser.parse("deadline return book /by 2019-12-02").execute(tasks, new Ui(), newStorage());

        String output = TestUtil.captureStdOut(() ->
                Parser.parse("on 2019-12-02").execute(tasks, new Ui(), newStorage()));
        assertTrue(output.contains("return book"));
    }

    @Test
    public void parse_onMissingDate_throws() {
        assertThrows(EveException.class, () -> Parser.parse("on"));
    }

    @Test
    public void parse_onInvalidDate_throws() {
        assertThrows(EveException.class, () -> Parser.parse("on notadate"));
    }

    // ----- find -----

    @Test
    public void parse_findMissingKeyword_throws() {
        assertThrows(EveException.class, () -> Parser.parse("find"));
    }

    // ----- schedule -----

    @Test
    public void parse_scheduleNoDate_returnsScheduleCommand() throws EveException {
        assertInstanceOf(ScheduleCommand.class, Parser.parse("schedule"));
    }

    @Test
    public void parse_scheduleWithDate_returnsScheduleCommand() throws EveException {
        assertInstanceOf(ScheduleCommand.class, Parser.parse("schedule 2019-12-02"));
    }

    @Test
    public void parse_scheduleInvalidDate_throws() {
        assertThrows(EveException.class, () -> Parser.parse("schedule notadate"));
    }

    // ----- mark/unmark/delete argument parsing -----

    @Test
    public void parse_markMissingNumber_throws() {
        assertThrows(EveException.class, () -> Parser.parse("mark"));
    }

    @Test
    public void parse_markNonNumericArgument_throws() {
        EveException exception = assertThrows(EveException.class, () -> Parser.parse("mark abc"));
        assertTrue(exception.getMessage().contains("abc"));
    }
}
