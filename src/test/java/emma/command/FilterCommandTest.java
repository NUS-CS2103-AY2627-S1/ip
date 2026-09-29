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
 * Checks that a filter shows exactly the tasks it should, keeping their numbers from
 * the full list and changing nothing.
 */
public class FilterCommandTest {

    @TempDir
    Path tempDir;

    private static LocalDate october(int day) {
        return LocalDate.of(2019, 10, day);
    }

    /**
     * Builds a list holding one todo, three deadlines and two events, so that a filter
     * has something of every type to leave out.
     */
    private TaskList sampleTasks() {
        List<Task> tasks = List.of(
                new Todo("read book"),
                new Deadline("early", october(10)),
                new Deadline("exact", october(15)),
                new Deadline("late", october(20)),
                new Event("conference", october(15), october(18)),
                new Event("standup", october(20), october(20)));
        return new TaskList(tasks);
    }

    /** Runs a filter against the sample list; the storage is never written to. */
    private String filter(String input, TaskList tasks) throws EmmaException {
        return Parser.parse(input).execute(tasks, new Storage(tempDir.resolve("emma.json").toString()));
    }

    @Test
    public void execute_filterByType_showsOnlyTasksOfThatType() throws EmmaException {
        TaskList tasks = sampleTasks();
        assertEquals("Here's what matches:\n1. [T][ ] read book",
                filter("filter /type todo", tasks));
        assertEquals("Here's what matches:\n"
                        + "2. [D][ ] early (by: Oct 10 2019)\n"
                        + "3. [D][ ] exact (by: Oct 15 2019)\n"
                        + "4. [D][ ] late (by: Oct 20 2019)",
                filter("filter /type deadline", tasks));
        assertEquals("Here's what matches:\n"
                        + "5. [E][ ] conference (from: Oct 15 2019 to: Oct 18 2019)\n"
                        + "6. [E][ ] standup (from: Oct 20 2019 to: Oct 20 2019)",
                filter("filter /type event", tasks));
    }

    @Test
    public void execute_dueByCutoff_keepsDeadlinesOnOrBeforeIt() throws EmmaException {
        TaskList tasks = sampleTasks();
        assertEquals("Here's what matches:\n"
                        + "2. [D][ ] early (by: Oct 10 2019)\n"
                        + "3. [D][ ] exact (by: Oct 15 2019)",
                filter("filter /type deadline /due-by 2019-10-15", tasks));
    }

    @Test
    public void execute_dueByCutoffBeforeEveryDeadline_matchesNothing() throws EmmaException {
        assertEquals("Nothing matches that filter.",
                filter("filter /type deadline /due-by 2019-01-01", sampleTasks()));
    }

    @Test
    public void execute_eventAtDate_matchesTheFirstMiddleAndLastDay() throws EmmaException {
        TaskList tasks = sampleTasks();
        String conference = "Here's what matches:\n"
                + "5. [E][ ] conference (from: Oct 15 2019 to: Oct 18 2019)";
        assertEquals(conference, filter("filter /type event /at 2019-10-15", tasks));
        assertEquals(conference, filter("filter /type event /at 2019-10-17", tasks));
        assertEquals(conference, filter("filter /type event /at 2019-10-18", tasks));
    }

    @Test
    public void execute_eventAtDateOutsideEveryEvent_matchesNothing() throws EmmaException {
        assertEquals("Nothing matches that filter.",
                filter("filter /type event /at 2019-10-19", sampleTasks()));
    }

    @Test
    public void execute_eventAtDate_matchesAOneDayEvent() throws EmmaException {
        assertEquals("Here's what matches:\n"
                        + "6. [E][ ] standup (from: Oct 20 2019 to: Oct 20 2019)",
                filter("filter /type event /at 2019-10-20", sampleTasks()));
    }

    @Test
    public void execute_filterOnEmptyList_matchesNothing() throws EmmaException {
        assertEquals("Nothing matches that filter.",
                filter("filter /type todo", new TaskList(List.of())));
    }

    @Test
    public void execute_filter_leavesTheListUnchanged() throws EmmaException {
        TaskList tasks = sampleTasks();
        String before = tasks.format();
        filter("filter /type deadline /due-by 2019-10-15", tasks);
        filter("filter /type event /at 2019-10-17", tasks);
        assertEquals(before, tasks.format());
        assertEquals(6, tasks.size());
    }
}
