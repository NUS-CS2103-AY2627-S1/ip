package ff15;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ff15.command.AddCommand;
import ff15.command.Command;
import ff15.command.DeleteCommand;
import ff15.command.ExitCommand;
import ff15.command.FindCommand;
import ff15.command.ListCommand;
import ff15.command.MarkCommand;
import ff15.command.OnCommand;
import ff15.command.UnmarkCommand;
import ff15.task.TaskList;
import ff15.task.Todo;

/**
 * Tests {@link Parser#parse(String)} on lines it should accept: which command
 * each turns into, what that command was given, and how much leeway the shape
 * of the line gets. The lines it should refuse are in {@link ParserRejectionTest},
 * and the contact commands in {@link ContactParserTest}.
 */
public class ParserTest extends ParserTestBase {

    // --- which command each line becomes -------------------------------------

    @Test
    public void parse_list_returnsListCommand() throws FF15Exception {
        assertInstanceOf(ListCommand.class, Parser.parse("list"));
    }

    @Test
    public void parse_bye_returnsExitCommandThatEndsTheSession() throws FF15Exception {
        Command command = Parser.parse("bye");
        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_anythingButBye_returnsCommandThatDoesNotEndTheSession() throws FF15Exception {
        assertFalse(Parser.parse("list").isExit());
        assertFalse(Parser.parse("todo read book").isExit());
        assertFalse(Parser.parse("mark 1").isExit());
        assertFalse(Parser.parse("on 2019-12-02").isExit());
    }

    @Test
    public void parse_addingCommands_returnAddCommand() throws FF15Exception {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-12-02"));
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2019-12-05 1400 /to 2019-12-05 1600"));
    }

    @Test
    public void parse_taskNumberCommands_returnTheMatchingCommand() throws FF15Exception {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_find_returnsFindCommand() throws FF15Exception {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_on_returnsOnCommand() throws FF15Exception {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12"));
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019"));
    }

    // --- what the parsed command was given -----------------------------------

    @Test
    public void parse_todo_buildsTodoWithTheDescription() throws Exception {
        assertEquals("[T][ ] read book", addedBy("todo read book").toString());
    }

    @Test
    public void parse_todoWithSurroundingSpaces_trimsTheDescription() throws Exception {
        assertEquals("[T][ ] read book", addedBy("todo    read book   ").toString());
    }

    @Test
    public void parse_deadlineWithDateOnly_buildsDeadlineWithThatDate() throws Exception {
        assertEquals("[D][ ] return book (by: Dec 02 2019)",
                addedBy("deadline return book /by 2019-12-02").toString());
    }

    @Test
    public void parse_deadlineWithTime_keepsTheTime() throws Exception {
        assertEquals("[D][ ] return book (by: Dec 02 2019, 6:00pm)",
                addedBy("deadline return book /by 2019-12-02 1800").toString());
    }

    @Test
    public void parse_deadlineWithSpacesAroundTheBy_trimsBothSides() throws Exception {
        assertEquals("[D][ ] return book (by: Dec 02 2019)",
                addedBy("deadline return book   /by    2019-12-02  ").toString());
    }

    @Test
    public void parse_event_buildsEventWithBothEnds() throws Exception {
        assertEquals("[E][ ] project meeting (from: Dec 05 2019, 2:00pm to: Dec 05 2019, 4:00pm)",
                addedBy("event project meeting /from 2019-12-05 1400 /to 2019-12-05 1600").toString());
    }

    @Test
    public void parse_multiWordDescriptions_keepInnerSpacing() throws Exception {
        assertEquals("[T][ ] borrow a book from the library",
                addedBy("todo borrow a book from the library").toString());
    }

    @Test
    public void parse_mark_marksTheTaskTheUserNumbered() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"), new Todo("b"));
        run(tasks, "mark 2");
        assertEquals("[T][ ] a", tasks.get(1).toString());
        assertEquals("[T][X] b", tasks.get(2).toString());
    }

    @Test
    public void parse_unmark_clearsTheTaskTheUserNumbered() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"));
        run(tasks, "mark 1");
        run(tasks, "unmark 1");
        assertEquals("[T][ ] a", tasks.get(1).toString());
    }

    @Test
    public void parse_delete_removesTheTaskTheUserNumbered() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("a"), new Todo("b"));
        run(tasks, "delete 1");
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] b", tasks.get(1).toString());
    }

    @Test
    public void parse_find_reportsTheMatchingTasks() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"), new Todo("buy milk"));

        String printed = runCapturing(tasks, "find book");

        assertTrue(printed.contains("Michael Scarn:"), printed);
        assertTrue(printed.contains("1.[T][ ] read book"), printed);
        assertFalse(printed.contains("buy milk"), printed);
    }

    @Test
    public void parse_findWithNothingMatching_saysSo() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        String printed = runCapturing(tasks, "find homework");

        assertTrue(printed.contains("Nothing matching 'homework'. I looked."), printed);
    }

    @Test
    public void parse_findWithSurroundingSpaces_trimsTheKeyword() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        String printed = runCapturing(tasks, "find    book   ");

        assertTrue(printed.contains("1.[T][ ] read book"), printed);
    }

    @Test
    public void parse_findLeavesTheListAlone_doesNotChangeAnyTask() throws Exception {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        runCapturing(tasks, "find book");
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(1).toString());
    }

    // --- forgiving the shape of the line, rejecting the substance ------------

    @Test
    public void parse_spaceAtEitherEnd_stillMatchesTheCommand() throws FF15Exception {
        assertInstanceOf(ListCommand.class, Parser.parse(" list"));
        assertInstanceOf(ListCommand.class, Parser.parse("list "));
        assertInstanceOf(ListCommand.class, Parser.parse("   list   "));
        assertInstanceOf(MarkCommand.class, Parser.parse("  mark   2  "));
    }

    @Test
    public void parse_runsOfSpacesInsideADescription_collapseToOne() throws Exception {
        assertEquals("[T][ ] read the book", addedBy("todo read   the    book").toString());
        assertEquals("[D][ ] return book (by: Dec 02 2019)",
                addedBy("deadline  return   book  /by  2019-12-02").toString());
    }

    @Test
    public void parse_blankLine_throwsAndAsksForSomething() {
        FF15Exception thrown = assertThrows(FF15Exception.class, () -> Parser.parse(""));
        assertTrue(thrown.getMessage().contains("Say something"), thrown.getMessage());
        assertThrows(FF15Exception.class, () -> Parser.parse("   "));
    }

    @Test
    public void parse_markerGivenTwice_throwsAndNamesIt() {
        FF15Exception thrown = assertThrows(FF15Exception.class, () ->
                Parser.parse("deadline x /by 2019-12-02 /by 2019-12-03"));
        assertTrue(thrown.getMessage().contains("/by twice"), thrown.getMessage());
        assertThrows(FF15Exception.class, () ->
                Parser.parse("event x /from 2019-12-05 /from 2019-12-06 /to 2019-12-07"));
        assertThrows(FF15Exception.class, () ->
                Parser.parse("event x /from 2019-12-05 /to 2019-12-06 /to 2019-12-07"));
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add John /phone 1 /phone 2"));
        assertThrows(FF15Exception.class, () -> Parser.parse("contact add John /email a@b.c /email d@e.f"));
    }
}
