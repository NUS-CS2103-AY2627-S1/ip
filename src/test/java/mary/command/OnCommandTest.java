package mary.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import mary.task.Deadline;
import mary.task.Event;
import mary.task.Todo;

/**
 * Tests inclusive event boundaries and date filtering without persistence writes.
 */
class OnCommandTest extends CommandTestSupport {
    /**
     * Tests execute: matching date; includes deadline and event but not todo.
     */
    @Test
    void execute_matchingDate_includesDeadlineAndEventButNotTodo() {
        tasks.add(new Todo("hidden"));
        tasks.add(new Deadline("return", LocalDateTime.of(2019, 12, 2, 18, 0)));
        tasks.add(new Event("camp", LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 4, 16, 0)));
        assertEquals(" Tasks occurring on 2019-12-02:\n"
                        + " [D][ ] return (by: 2 Dec 2019 18:00)\n"
                        + " [E][ ] camp (from: 2 Dec 2019 14:00 to: 4 Dec 2019 16:00)\n",
                execute(new OnCommand("on 2/12/2019")));
        assertFalse(Files.exists(directory.resolve("tasks.txt")));
    }

    /**
     * Tests execute: multi day event; includes start interior and end dates.
     */
    @Test
    void execute_multiDayEvent_includesStartInteriorAndEndDates() {
        tasks.add(new Event("camp", LocalDateTime.of(2019, 12, 2, 14, 0),
                LocalDateTime.of(2019, 12, 4, 16, 0)));
        for (int day : new int[] {2, 3, 4}) {
            assertTrue(execute(new OnCommand("on " + day + "/12/2019")).contains("[E][ ] camp"));
        }
        for (int day : new int[] {1, 5}) {
            assertTrue(execute(new OnCommand("on " + day + "/12/2019")).contains("No deadlines or events"));
        }
    }

    /**
     * Tests execute: empty list; reports no matches.
     */
    @Test
    void execute_emptyList_reportsNoMatches() {
        assertEquals(" No deadlines or events occur on 2019-12-02.\n",
                execute(new OnCommand("on 2/12/2019")));
    }

    /**
     * Tests execute: missing and malformed date; reports usage.
     */
    @Test
    void execute_missingAndMalformedDate_reportsUsage() {
        for (String input : new String[] {"on", "on ", "on tomorrow", "on 1/13/2019"}) {
            assertTrue(execute(new OnCommand(input)).startsWith(" Error:"), input);
        }
    }
}
