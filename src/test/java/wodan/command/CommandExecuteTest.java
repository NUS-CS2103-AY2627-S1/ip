package wodan.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import wodan.WodanException;
import wodan.parser.Parser;
import wodan.storage.Storage;
import wodan.task.TaskList;
import wodan.task.Todo;
import wodan.ui.Ui;

/**
 * Unit tests for command {@code execute} replies and list changes.
 */
public class CommandExecuteTest {
    @TempDir
    Path tempDir;

    @Test
    public void execute_listEmpty_headerOnly() throws WodanException {
        String output = execute("list", new TaskList());
        assertTrue(output.contains("The ravens have given these quests."));
        assertFalse(output.contains("1."));
    }

    @Test
    public void execute_listGroupedByCategory_keepsOriginalNumbers() throws WodanException {
        TaskList tasks = new TaskList();
        execute("todo borrow book", tasks);
        execute("todo cs2100", tasks);
        execute("tag 1 school", tasks);

        String output = execute("list", tasks);
        assertTrue(output.contains("general"));
        assertTrue(output.contains("school"));
        assertTrue(output.contains("2. [T][ ] cs2100"));
        assertTrue(output.contains("1. [T][ ] borrow book"));
    }

    @Test
    public void execute_markThenUnmark_togglesDoneAndSaves() throws WodanException {
        TaskList tasks = new TaskList();
        execute("todo borrow book", tasks);

        String marked = execute("mark 1", tasks);
        assertTrue(marked.contains("One less burden to carry."));
        assertTrue(marked.contains("[T][X] borrow book"));
        assertTrue(tasks.get(0).isDone());

        String unmarked = execute("unmark 1", tasks);
        assertTrue(unmarked.contains("The ravens retract their approval."));
        assertTrue(unmarked.contains("[T][ ] borrow book"));
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void execute_delete_removesTaskAndReportsCount() throws WodanException {
        TaskList tasks = new TaskList();
        execute("todo first", tasks);
        execute("todo second", tasks);

        String output = execute("delete 1", tasks);
        assertTrue(output.contains("Noted. I've removed this task:"));
        assertTrue(output.contains("[T][ ] first"));
        assertTrue(output.contains("Now you have 1 task in the list."));
        assertEquals(1, tasks.size());
        assertEquals("second", tasks.get(0).getDescription());
    }

    @Test
    public void execute_deleteLastRemaining_reportsZeroTasks() throws WodanException {
        TaskList tasks = new TaskList();
        execute("todo only", tasks);
        String output = execute("delete 1", tasks);
        assertTrue(output.contains("Now you have 0 tasks in the list."));
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void execute_find_matchesAndMisses() throws WodanException {
        TaskList tasks = new TaskList();
        execute("todo read book", tasks);
        execute("todo read song", tasks);

        String hits = execute("find BOOK", tasks);
        assertTrue(hits.contains("Here are the matching tasks in your list:"));
        assertTrue(hits.contains("1. [T][ ] read book"));
        assertFalse(hits.contains("read song"));

        String misses = execute("find missing", tasks);
        assertTrue(misses.contains("The ravens found no matching quests."));
    }

    @Test
    public void execute_on_matchesDeadlineAndReportsNone() throws WodanException {
        TaskList tasks = new TaskList();
        execute("todo ignore", tasks);
        execute("deadline return book /by 2019-12-02", tasks);

        String hits = execute("on 2019-12-02", tasks);
        assertTrue(hits.contains("The ravens found these quests on Dec 02 2019."));
        assertTrue(hits.contains("2. [D][ ] return book (by: Dec 02 2019)"));

        String misses = execute("on 2019-01-01", tasks);
        assertTrue(misses.contains("The ravens found no quests on Jan 01 2019."));
    }

    @Test
    public void execute_tag_savesLowercaseCategory() throws WodanException {
        TaskList tasks = new TaskList();
        execute("todo borrow book", tasks);
        String output = execute("tag 1 School", tasks);
        assertTrue(output.contains("The ravens branded this quest school."));
        assertEquals("school", tasks.get(0).getCategory());
    }

    @Test
    public void execute_bye_showsFarewellAndIsExit() throws WodanException {
        Command command = Parser.parse("bye");
        assertTrue(command.isExit());
        String output = capture(command, new TaskList());
        assertTrue(output.contains("So it is written. Farewell, wanderer."));
    }

    @Test
    public void execute_addDeadlineAndEvent_appendsToList() throws WodanException {
        TaskList tasks = new TaskList();
        String deadline = execute("deadline return book /by 2019-12-02 1800", tasks);
        assertTrue(deadline.contains("[D][ ] return book (by: Dec 02 2019, 6:00pm)"));
        String event = execute(
                "event meeting /from 2019-12-02 1400 /to 2019-12-02 1600", tasks);
        assertTrue(event.contains("[E][ ] meeting (from: Dec 02 2019, 2:00pm to: Dec 02 2019, 4:00pm)"));
        assertEquals(2, tasks.size());
    }

    @Test
    public void isExit_nonExitCommands_false() {
        assertFalse(new ListCommand().isExit());
        assertFalse(new AddCommand(new Todo("x")).isExit());
        assertFalse(new MarkCommand("1").isExit());
        assertFalse(new UnmarkCommand("1").isExit());
        assertFalse(new DeleteCommand("1").isExit());
        assertFalse(new FindCommand("book").isExit());
        assertFalse(new OnCommand("2019-12-02").isExit());
        assertFalse(new TagCommand("1 school").isExit());
    }

    @Test
    public void execute_findMissingKeyword_exceptionThrown() {
        assertThrows(WodanException.class, () -> execute("find", new TaskList()));
    }

    @Test
    public void execute_markExtraToken_exceptionThrown() throws WodanException {
        TaskList tasks = new TaskList();
        execute("todo borrow book", tasks);
        WodanException ex = assertThrows(WodanException.class, () -> execute(
                "mark 1 extra", tasks));
        assertEquals("mark takes only a quest number. Try: mark 1", ex.getMessage());
        assertFalse(tasks.get(0).isDone());
    }

    @Test
    public void execute_tagMissingCategory_exceptionThrown() throws WodanException {
        TaskList tasks = new TaskList();
        execute("todo borrow book", tasks);
        WodanException ex = assertThrows(WodanException.class, () -> execute("tag 1", tasks));
        assertEquals("Name the brand. Try: tag 1 school", ex.getMessage());
        assertEquals("general", tasks.get(0).getCategory());
    }

    @Test
    public void execute_onInvalidDate_exceptionThrown() {
        WodanException ex = assertThrows(WodanException.class, () -> execute(
                "on not-a-date", new TaskList()));
        assertEquals(
                "The ravens cannot read 'not-a-date'. Try: 2026-08-26 or 08/26/2019 1800",
                ex.getMessage());
    }

    @Test
    public void execute_addWhenSavePathIsDirectory_exceptionThrown() throws Exception {
        Path dir = tempDir.resolve("folder");
        Files.createDirectory(dir);
        TaskList tasks = new TaskList();
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        WodanException ex = assertThrows(WodanException.class, () -> Parser.parse(
                "todo borrow book").execute(tasks, new Ui(stream), new Storage(dir.toString())));
        assertEquals("The save path is not a file. The ravens could not record the quests.",
                ex.getMessage());
        assertEquals(1, tasks.size());
    }

    private String execute(String command, TaskList tasks) throws WodanException {
        return capture(Parser.parse(command), tasks);
    }

    private String capture(Command command, TaskList tasks) throws WodanException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        command.execute(tasks, new Ui(stream), new Storage(tempDir.resolve("wodan.txt").toString()));
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
