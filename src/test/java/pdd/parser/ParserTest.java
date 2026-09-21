package pdd.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pdd.PDDException;
import pdd.command.Command;
import pdd.storage.Storage;
import pdd.task.TaskList;
import pdd.ui.Ui;

/**
 * Tests {@link Parser#parse(String)} by executing the {@link Command} it
 * returns against a fresh {@link TaskList} and checking the resulting
 * state, since Command's parsed fields are intentionally private (no
 * getters) and behavior, not internal state, is what parse() promises.
 */
public class ParserTest {
    @TempDir
    Path tempDir;

    private TaskList newTaskList() {
        return new TaskList(new ArrayList<>());
    }

    private Storage newStorage() {
        return new Storage(tempDir.resolve("pdd.txt").toString());
    }

    @Test
    public void parse_todoCommand_addsTodoWithGivenDescription() throws PDDException {
        Command c = Parser.parse("todo read book");
        TaskList tasks = newTaskList();
        c.execute(tasks, new Ui(), newStorage());
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void parse_deadlineCommand_addsDeadlineWithParsedDate() throws PDDException {
        Command c = Parser.parse("deadline return book /by 2019-12-01");
        TaskList tasks = newTaskList();
        c.execute(tasks, new Ui(), newStorage());
        assertEquals(1, tasks.size());
        assertEquals("[D][ ] return book (by: Dec 01 2019)", tasks.get(0).toString());
    }

    @Test
    public void parse_eventCommand_addsEventWithParsedFromDateAndToText() throws PDDException {
        Command c = Parser.parse("event project meeting /from 2019-12-02 /to 4pm");
        TaskList tasks = newTaskList();
        c.execute(tasks, new Ui(), newStorage());
        assertEquals(1, tasks.size());
        assertEquals("[E][ ] project meeting (from: Dec 02 2019 to: 4pm)", tasks.get(0).toString());
    }

    @Test
    public void parse_markCommand_marksExistingTaskDone() throws PDDException {
        TaskList tasks = newTaskList();
        tasks.add(new pdd.task.Todo("read book"));
        Command c = Parser.parse("mark 1");
        c.execute(tasks, new Ui(), newStorage());
        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    public void parse_deleteCommand_removesTaskFromList() throws PDDException {
        TaskList tasks = newTaskList();
        tasks.add(new pdd.task.Todo("read book"));
        Command c = Parser.parse("delete 1");
        c.execute(tasks, new Ui(), newStorage());
        assertEquals(0, tasks.size());
    }

    @Test
    public void parse_findCommand_returnsFindCommandAndDoesNotMutateList() throws PDDException {
        TaskList tasks = newTaskList();
        tasks.add(new pdd.task.Todo("read book"));
        Command c = Parser.parse("find book");
        assertTrue(c instanceof pdd.command.FindCommand);
        c.execute(tasks, new Ui(), newStorage());
        assertEquals(1, tasks.size());
    }

    @Test
    public void parse_emptyFindKeyword_throwsException() {
        PDDException e = assertThrows(PDDException.class, () -> Parser.parse("find"));
        assertEquals("OOPS!!! Please provide a keyword to search for, e.g. find book", e.getMessage());
    }

    @Test
    public void parse_byeCommand_returnsCommandThatSignalsExit() throws PDDException {
        Command c = Parser.parse("bye");
        assertTrue(c.isExit());
    }

    @Test
    public void parse_nonByeCommand_doesNotSignalExit() throws PDDException {
        Command c = Parser.parse("list");
        assertTrue(!c.isExit());
    }

    @Test
    public void parse_emptyTodoDescription_throwsException() {
        PDDException e = assertThrows(PDDException.class, () -> Parser.parse("todo"));
        assertEquals("OOPS!!! The description of a todo cannot be empty.", e.getMessage());
    }

    @Test
    public void parse_deadlineMissingByMarker_throwsException() {
        PDDException e = assertThrows(PDDException.class, () -> Parser.parse("deadline return book"));
        assertEquals("OOPS!!! A deadline needs a description and a '/by' "
                + "date, e.g. deadline return book /by 2019-10-15", e.getMessage());
    }

    @Test
    public void parse_deadlineWithInvalidDate_throwsException() {
        PDDException e = assertThrows(PDDException.class, () -> Parser.parse("deadline return book /by not-a-date"));
        assertEquals("OOPS!!! Please enter the date in yyyy-MM-dd format, e.g. 2019-10-15.", e.getMessage());
    }

    @Test
    public void parse_unknownCommandWord_throwsException() {
        PDDException e = assertThrows(PDDException.class, () -> Parser.parse("blah"));
        assertEquals("OOPS!!! I'm sorry, but I don't know what that means :-(", e.getMessage());
    }

    @Test
    public void parse_sortCommand_sortsTaskListInPlace() throws PDDException {
        TaskList tasks = newTaskList();
        tasks.add(new pdd.task.Todo("z todo"));
        tasks.add(new pdd.task.Todo("a todo"));
        Command c = Parser.parse("sort");
        assertTrue(c instanceof pdd.command.SortCommand);
        c.execute(tasks, new Ui(), newStorage());
        assertEquals("a todo", tasks.get(0).getDescription());
        assertEquals("z todo", tasks.get(1).getDescription());
    }
}
