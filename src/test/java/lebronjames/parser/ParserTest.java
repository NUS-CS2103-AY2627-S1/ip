package lebronjames.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import lebronjames.LebronJamesException;
import lebronjames.command.AddCommand;
import lebronjames.command.Command;
import lebronjames.command.DeleteCommand;
import lebronjames.command.ExitCommand;
import lebronjames.command.FindCommand;
import lebronjames.command.ListCommand;
import lebronjames.command.MarkCommand;
import lebronjames.command.OnCommand;
import lebronjames.storage.Storage;
import lebronjames.task.Task;
import lebronjames.task.TaskList;
import lebronjames.ui.Ui;

/**
 * Tests {@link Parser}, which turns a typed line into a {@link Command}.
 *
 * <p>This is the class that decides what the user meant, so it is where a
 * mistake is most visible: the wrong command, a description with {@code /by}
 * left in it, or a helpful error message replaced by a confusing one.
 *
 * <p>A parsed AddCommand keeps its task privately, so these tests execute it
 * against a real TaskList to see what was actually built. Ui and Storage are
 * needed only to satisfy the signature; {@link #silentStorage()} writes into a
 * throwaway folder JUnit deletes afterwards, and the Ui's printing is ignored.
 */
public class ParserTest {
    /** Throwaway folder created and deleted by JUnit around this test class. */
    @TempDir
    private static Path tempFolder;

    /**
     * Returns storage that saves somewhere harmless, so executing a command in a
     * test does not touch the real save file.
     *
     * @return Storage pointing into a temporary folder.
     */
    private static Storage silentStorage() {
        return new Storage(tempFolder.toString(), "tasks.txt");
    }

    /**
     * Parses a line that adds a task, and returns the task it built.
     *
     * @param input Line to parse.
     * @return The task the resulting AddCommand adds.
     * @throws LebronJamesException If the line does not parse.
     */
    private static Task addedTask(String input) throws LebronJamesException {
        Command command = Parser.parse(input);
        assertInstanceOf(AddCommand.class, command);

        TaskList tasks = new TaskList();
        command.execute(tasks, new Ui(), silentStorage());

        assertEquals(1, tasks.size());
        return tasks.asList().get(0);
    }

    @Test
    public void parse_bye_exitCommandThatEndsTheProgram() throws LebronJamesException {
        Command command = Parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_list_listCommandThatDoesNotEndTheProgram() throws LebronJamesException {
        Command command = Parser.parse("list");

        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    public void parse_commandWordWithTrailingText_notTreatedAsThatCommand() {
        // "list" and "bye" take no arguments, so anything after them is not a
        // sloppy version of the command but a line we do not understand.
        assertThrows(LebronJamesException.class, () -> Parser.parse("list all"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("bye now"));
    }

    @Test
    public void parse_commandWordAsPrefixOfAnotherWord_notTreatedAsThatCommand() {
        // "todolist" starts with "todo" but is not a todo command.
        assertThrows(LebronJamesException.class, () -> Parser.parse("todolist"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("listing"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("byebye"));
    }

    @Test
    public void parse_todoWithDescription_addCommandCarryingTodo() throws LebronJamesException {
        assertEquals("[T][ ] read book", addedTask("todo read book").toString());
    }

    @Test
    public void parse_todoWithExtraSpaces_descriptionTrimmed() throws LebronJamesException {
        assertEquals("read book", addedTask("todo    read book   ").getDescription());
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("todo"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("todo   "));
    }

    @Test
    public void parse_deadlineWithDescriptionAndDate_addCommandCarryingDeadline()
            throws LebronJamesException {
        assertEquals("[D][ ] return book (by: Jun 06 2019)",
                addedTask("deadline return book /by 2019-06-06").toString());
    }

    @Test
    public void parse_deadlineWithTime_timeKept() throws LebronJamesException {
        assertEquals("[D][ ] submit report (by: Dec 02 2019, 6:00pm)",
                addedTask("deadline submit report /by 2019-12-02 1800").toString());
    }

    @Test
    public void parse_deadlineMissingBy_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("deadline return book"));
        // "/by" must be a separate word, not glued to the description.
        assertThrows(LebronJamesException.class, () -> Parser.parse("deadline return book/by 2019-06-06"));
    }

    @Test
    public void parse_deadlineWithEmptyPart_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("deadline /by 2019-06-06"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("deadline return book /by "));
        assertThrows(LebronJamesException.class, () -> Parser.parse("deadline"));
    }

    @Test
    public void parse_deadlineWithUnparseableDate_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("deadline return book /by someday"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("deadline pay rent /by 2019-02-30"));
    }

    @Test
    public void parse_eventWithBothDates_addCommandCarryingEvent() throws LebronJamesException {
        assertEquals("[E][ ] project meeting (from: Aug 06 2019, 2:00pm to: Aug 06 2019, 4:00pm)",
                addedTask("event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600").toString());
    }

    @Test
    public void parse_eventMissingFromOrTo_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("event meeting"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("event meeting /from 2019-08-06"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("event meeting /to 2019-08-06"));
    }

    @Test
    public void parse_eventWithToBeforeFrom_exceptionThrown() {
        // /to is looked for only after /from, so this order is not a valid event.
        assertThrows(LebronJamesException.class, () ->
                Parser.parse("event meeting /to 2019-08-06 /from 2019-08-06"));
    }

    @Test
    public void parse_eventWithEmptyPart_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("event /from 2019-08-06 /to 2019-08-07"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("event meeting /from  /to 2019-08-07"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("event meeting /from 2019-08-06 /to "));
    }

    @Test
    public void parse_markAndUnmark_markCommandEitherWay() throws LebronJamesException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
    }

    @Test
    public void parse_mark_marksTheTaskTheUserNumbered() throws LebronJamesException {
        TaskList tasks = new TaskList();
        Parser.parse("todo first").execute(tasks, new Ui(), silentStorage());
        Parser.parse("todo second").execute(tasks, new Ui(), silentStorage());

        Parser.parse("mark 2").execute(tasks, new Ui(), silentStorage());

        // Only the second task should be affected: a one-off here would tick
        // the wrong box.
        assertEquals("[T][ ] first", tasks.get(1).toString());
        assertEquals("[T][X] second", tasks.get(2).toString());
    }

    @Test
    public void parse_unmark_clearsTheTask() throws LebronJamesException {
        TaskList tasks = new TaskList();
        Parser.parse("todo first").execute(tasks, new Ui(), silentStorage());
        Parser.parse("mark 1").execute(tasks, new Ui(), silentStorage());

        Parser.parse("unmark 1").execute(tasks, new Ui(), silentStorage());

        assertEquals("[T][ ] first", tasks.get(1).toString());
    }

    @Test
    public void parse_deleteWithNumber_deleteCommand() throws LebronJamesException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_taskNumberMissing_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("mark"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("unmark  "));
        assertThrows(LebronJamesException.class, () -> Parser.parse("delete"));
    }

    @Test
    public void parse_taskNumberNotAWholeNumber_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("mark abc"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("delete 1.5"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("delete 1 2"));
    }

    @Test
    public void parse_taskNumberOutOfRange_parsesButFailsOnExecute() throws LebronJamesException {
        // Parser only checks that a number was typed; whether the task exists is
        // the task list's business, and is reported when the command runs.
        Command command = Parser.parse("delete 99");

        assertThrows(LebronJamesException.class, () ->
                command.execute(new TaskList(), new Ui(), silentStorage()));
    }

    @Test
    public void parse_onWithDate_onCommand() throws LebronJamesException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
    }

    @Test
    public void parse_onWithoutOrWithBadDate_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("on"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("on   "));
        assertThrows(LebronJamesException.class, () -> Parser.parse("on someday"));
    }

    @Test
    public void parse_findWithKeyword_findCommand() throws LebronJamesException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_findWithMultiWordKeyword_wholePhraseKept() throws LebronJamesException {
        TaskList tasks = new TaskList();
        Parser.parse("todo read book").execute(tasks, new Ui(), silentStorage());
        Parser.parse("todo read newspaper").execute(tasks, new Ui(), silentStorage());

        assertEquals(1, tasks.findTasksWithKeyword("read book").size());
        assertInstanceOf(FindCommand.class, Parser.parse("find read book"));
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("find"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("find   "));
    }

    @Test
    public void parse_findAsPrefixOfAnotherWord_notTreatedAsFind() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("finder"));
    }

    @Test
    public void parse_reservedPipeCharacter_exceptionThrown() {
        // '|' separates the fields of the save file, so a task containing one
        // could not be read back; it has to be refused at the door.
        assertThrows(LebronJamesException.class, () -> Parser.parse("todo read |book"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("list |"));
    }

    @Test
    public void parse_reservedPipeCharacter_messageExplainsWhy() {
        LebronJamesException exception =
                assertThrows(LebronJamesException.class, () -> Parser.parse("todo read |book"));

        assertTrue(exception.getMessage().contains("'|'"), "message should name the offending character");
    }

    @Test
    public void parse_blankInput_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse(""));
        assertThrows(LebronJamesException.class, () -> Parser.parse("     "));
    }

    @Test
    public void parse_unknownCommand_exceptionThrown() {
        assertThrows(LebronJamesException.class, () -> Parser.parse("blah"));
        assertThrows(LebronJamesException.class, () -> Parser.parse("LIST"));
    }
}
