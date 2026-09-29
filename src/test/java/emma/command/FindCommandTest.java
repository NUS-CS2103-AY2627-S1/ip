package emma.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import emma.Deadline;
import emma.EmmaException;
import emma.Event;
import emma.Parser;
import emma.Storage;
import emma.Task;
import emma.TaskList;
import emma.Todo;

/**
 * Checks that a find shows every task whose description holds the text, and nothing else.
 */
public class FindCommandTest {

    @TempDir
    Path tempDir;

    /**
     * Builds a list where "book" appears in three descriptions of different types, and
     * one task that does not mention it at all.
     */
    private TaskList sampleTasks() {
        List<Task> tasks = List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2019, 10, 15)),
                new Event("book club", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16)),
                new Todo("buy milk"));
        return new TaskList(tasks);
    }

    /** Runs a find; the storage is never written to. */
    private String find(String input, TaskList tasks) throws EmmaException {
        return Parser.parse(input).execute(tasks, new Storage(tempDir.resolve("emma.json").toString()));
    }

    @Test
    public void execute_textInSeveralDescriptions_findsEveryTypeOfTask() throws EmmaException {
        assertEquals("Here's what I found:\n"
                        + "1. [T][ ] read book\n"
                        + "2. [D][ ] return book (by: Oct 15 2019)\n"
                        + "3. [E][ ] book club (from: Oct 15 2019 to: Oct 16 2019)",
                find("find book", sampleTasks()));
    }

    @Test
    public void execute_match_keepsTheNumberFromTheFullList() throws EmmaException {
        assertEquals("Here's what I found:\n4. [T][ ] buy milk", find("find milk", sampleTasks()));
    }

    @Test
    public void execute_textInTheMiddleOfAWord_stillMatches() throws EmmaException {
        assertEquals("Here's what I found:\n4. [T][ ] buy milk", find("find uy mil", sampleTasks()));
    }

    @Test
    public void execute_textNowhereInAnyDescription_findsNothing() throws EmmaException {
        assertEquals("Nothing has \"homework\" in its description.",
                find("find homework", sampleTasks()));
    }

    @Test
    public void execute_textFromTheDateOrIcon_isNotMatched() throws EmmaException {
        assertEquals("Nothing has \"Oct\" in its description.", find("find Oct", sampleTasks()));
        assertEquals("Nothing has \"by:\" in its description.", find("find by:", sampleTasks()));
    }

    @Test
    public void execute_textInADifferentCase_doesNotMatch() throws EmmaException {
        assertEquals("Nothing has \"Book\" in its description.", find("find Book", sampleTasks()));
    }

    @Test
    public void execute_findOnAnEmptyList_findsNothing() throws EmmaException {
        assertEquals("Nothing has \"book\" in its description.",
                find("find book", new TaskList(List.of())));
    }

    @Test
    public void execute_find_leavesTheListUnchanged() throws EmmaException {
        TaskList tasks = sampleTasks();
        String before = tasks.format();
        find("find book", tasks);
        assertEquals(before, tasks.format());
        assertEquals(4, tasks.size());
    }
}
