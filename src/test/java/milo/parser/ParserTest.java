package milo.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import milo.command.AddCommand;
import milo.command.Command;
import milo.command.DeleteCommand;
import milo.command.ExitCommand;
import milo.command.FindCommand;
import milo.command.ListCommand;
import milo.command.MarkCommand;
import milo.command.UndoCommand;
import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.TaskList;
import milo.task.Todo;
import milo.ui.Ui;

/**
 * Parser.parse() is the busiest method in the codebase: every user command
 * passes through it, and it is responsible both for picking the right
 * Command subtype and for correctly wiring that command's data (e.g. that
 * "unmark" really does produce a mark-as-not-done command, not just "some
 * MarkCommand"). Where the built command's data is not directly inspectable,
 * these tests execute it against a small TaskList to verify the wiring.
 */
class ParserTest {
    @TempDir
    Path tempDir;

    private Storage newStorage() {
        return new Storage(tempDir.resolve("milo.txt").toString());
    }

    @Test
    void parse_bye_returnsExitCommand() throws MiloException {
        assertInstanceOf(ExitCommand.class, Parser.parse("bye"));
    }

    @Test
    void parse_list_returnsListCommand() throws MiloException {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    void parse_delete_returnsDeleteCommand() throws MiloException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    void parse_descriptionContainingFieldSeparator_isRejected() {
        // The save file separates fields with "|", so a description holding
        // one is written back as extra fields and dropped on the next load.
        assertThrows(MiloException.class, () -> Parser.parse("todo read | book"));
        assertThrows(MiloException.class, () -> Parser.parse("deadline a | b /by 2019-10-15"));
        assertThrows(MiloException.class, () -> Parser.parse("event a | b /from x /to y"));
    }

    @Test
    void parse_eventDatesContainingFieldSeparator_areRejected() {
        assertThrows(MiloException.class, () -> Parser.parse("event trip /from a | b /to y"));
        assertThrows(MiloException.class, () -> Parser.parse("event trip /from x /to a | b"));
    }

    @Test
    void parse_repeatedByMarker_isRejected() {
        assertThrows(MiloException.class, () -> Parser.parse("deadline x /by 2019-10-15 /by 2019-11-01"));
    }

    @Test
    void parse_repeatedEventMarkers_areRejected() {
        assertThrows(MiloException.class, () -> Parser.parse("event x /from a /from b /to c"));
        assertThrows(MiloException.class, () -> Parser.parse("event x /from a /to b /to c"));
    }

    @Test
    void parse_eventEndingBeforeItStarts_isRejected() {
        assertThrows(MiloException.class, () -> Parser.parse("event trip /from 2019-10-15 /to 2019-10-01"));
    }

    @Test
    void parse_eventEndingWhenItStarts_isRejected() {
        assertThrows(MiloException.class, () -> Parser.parse("event trip /from 2019-10-15 /to 2019-10-15"));
    }

    @Test
    void parse_eventWithFreeTextTimes_isStillAccepted() throws MiloException {
        // There is no way to order "Mon 2pm" against "4pm", so the ordering
        // check has to stay out of the way of text like this.
        assertInstanceOf(AddCommand.class, Parser.parse("event meeting /from Mon 2pm /to 4pm"));
    }

    @Test
    void parse_eventInOrder_isAccepted() throws MiloException {
        assertInstanceOf(AddCommand.class, Parser.parse("event trip /from 2019-10-01 /to 2019-10-15"));
    }

    @Test
    void parse_undo_returnsUndoCommand() throws MiloException {
        assertInstanceOf(UndoCommand.class, Parser.parse("undo"));
    }

    @Test
    void parse_findWithKeyword_returnsFindCommandCarryingKeyword() throws MiloException {
        Command command = Parser.parse("find book");
        assertInstanceOf(FindCommand.class, command);
        assertEquals("book", ((FindCommand) command).getKeyword());
    }

    @Test
    void parse_findWithoutKeyword_throwsMiloException() {
        assertThrows(MiloException.class, () -> Parser.parse("find"));
    }

    @Test
    void parse_mark_actuallyMarksTaskDoneWhenExecuted() throws MiloException {
        Command command = Parser.parse("mark 1");
        assertInstanceOf(MarkCommand.class, command);

        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        command.execute(tasks, new Ui(), newStorage());
        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    void parse_unmark_actuallyMarksTaskNotDoneWhenExecuted() throws MiloException {
        Command command = Parser.parse("unmark 1");
        assertInstanceOf(MarkCommand.class, command);

        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        todo.markAsDone();
        tasks.add(todo);
        command.execute(tasks, new Ui(), newStorage());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    void parse_todoWithDescription_returnsAddCommandCarryingTodo() throws MiloException {
        Command command = Parser.parse("todo borrow book");
        assertInstanceOf(AddCommand.class, command);
        Task task = ((AddCommand) command).getTask();
        assertInstanceOf(Todo.class, task);
        assertEquals("[T][ ] borrow book", task.toString());
    }

    @Test
    void parse_todoWithoutDescription_throwsMiloException() {
        assertThrows(MiloException.class, () -> Parser.parse("todo"));
    }

    @Test
    void parse_deadlineWithByMarker_returnsAddCommandCarryingDeadline() throws MiloException {
        Command command = Parser.parse("deadline return book /by 2019-10-15");
        Task task = ((AddCommand) command).getTask();
        assertInstanceOf(Deadline.class, task);
        assertEquals("[D][ ] return book (by: Oct 15 2019)", task.toString());
    }

    @Test
    void parse_deadlineWithoutByMarker_throwsMiloException() {
        assertThrows(MiloException.class, () -> Parser.parse("deadline return book"));
    }

    @Test
    void parse_deadlineMarkerBuriedInsideAWord_isNotTreatedAsAMarker() throws MiloException {
        // Regression test for the word-boundary fix: "/by" inside
        // "essay/byline" must not be mistaken for the real marker later in
        // the string.
        Command command = Parser.parse("deadline finish essay/byline /by 2019-10-15");
        Task task = ((AddCommand) command).getTask();
        assertEquals("[D][ ] finish essay/byline (by: Oct 15 2019)", task.toString());
    }

    @Test
    void parse_eventWithFromAndTo_returnsAddCommandCarryingEvent() throws MiloException {
        Command command = Parser.parse("event project meeting /from Mon 2pm /to 4pm");
        Task task = ((AddCommand) command).getTask();
        assertInstanceOf(Event.class, task);
        assertEquals("[E][ ] project meeting (from: Mon 2pm to: 4pm)", task.toString());
    }

    @Test
    void parse_eventMarkersOutOfOrder_throwsMiloException() {
        assertThrows(MiloException.class, () -> Parser.parse("event m /to 4pm /from Mon"));
    }

    @Test
    void parse_eventMissingToMarker_throwsMiloException() {
        assertThrows(MiloException.class, () -> Parser.parse("event m /from Mon"));
    }

    @Test
    void parse_unknownKeyword_throwsMiloException() {
        assertThrows(MiloException.class, () -> Parser.parse("blah"));
    }

    @Test
    void parse_extraWhitespaceBetweenKeywordAndArguments_isHandled() throws MiloException {
        Command command = Parser.parse("todo   borrow book");
        Task task = ((AddCommand) command).getTask();
        assertEquals("[T][ ] borrow book", task.toString());
    }

    @Test
    void parse_trailingWhitespace_isTrimmedFromDescription() throws MiloException {
        Command command = Parser.parse("todo borrow book   ");
        Task task = ((AddCommand) command).getTask();
        assertEquals("[T][ ] borrow book", task.toString());
    }
}
