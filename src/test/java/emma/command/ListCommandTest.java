package emma.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import emma.Deadline;
import emma.Event;
import emma.Storage;
import emma.Task;
import emma.TaskList;
import emma.Todo;

/**
 * Checks that listing numbers the tasks from one, shows each type's own rendering, and
 * changes nothing.
 */
public class ListCommandTest {

    @TempDir
    Path tempDir;

    private Storage storage() {
        return new Storage(tempDir.resolve("emma.json").toString());
    }

    @Test
    public void execute_emptyList_saysNothingIsTracked() {
        String response = new ListCommand().execute(new TaskList(List.of()), storage());
        assertEquals("You haven't given me anything to track yet!", response);
    }

    @Test
    public void execute_oneOfEachType_numbersThemFromOne() {
        List<Task> initial = List.of(
                new Todo("read book"),
                new Deadline("return book", LocalDate.of(2019, 10, 15)),
                new Event("meeting", LocalDate.of(2019, 10, 15), LocalDate.of(2019, 10, 16)));
        String response = new ListCommand().execute(new TaskList(initial), storage());
        assertEquals("Here's your tasks:\n"
                + "1. [T][ ] read book\n"
                + "2. [D][ ] return book (by: Oct 15 2019)\n"
                + "3. [E][ ] meeting (from: Oct 15 2019 to: Oct 16 2019)", response);
    }

    @Test
    public void execute_taskThatIsDone_showsItsTick() {
        Task done = new Todo("read book");
        done.setDone(true);
        String response = new ListCommand().execute(new TaskList(List.of(done)), storage());
        assertEquals("Here's your tasks:\n1. [T][x] read book", response);
    }

    @Test
    public void execute_listing_leavesTheListUnchanged() {
        TaskList tasks = new TaskList(List.of(new Todo("read book"), new Todo("return book")));
        String before = tasks.format();
        new ListCommand().execute(tasks, storage());
        assertEquals(before, tasks.format());
        assertEquals(2, tasks.size());
    }

    @Test
    public void execute_listing_isNotAnExitCommand() {
        assertEquals(false, new ListCommand().isExit());
    }
}
