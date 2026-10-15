package tuesday.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import tuesday.task.Deadline;
import tuesday.task.Event;
import tuesday.task.Task;
import tuesday.task.Todo;

/**
 * Tests saved-row interpretation independently of file access and user-input syntax.
 */
public class SavedTaskParserTest {
    @Test
    public void parseTask_todo_preservesDescriptionAndCompletion() {
        Task task = SavedTaskParser.parseTask("  [T][X] Read   Book  ");
        assertInstanceOf(Todo.class, task);
        assertTrue(task.isDone());
        assertEquals("[T][X] Read   Book", task.toString());
    }

    @Test
    public void parseTask_scheduledTasks_preservesAllDateTimeFormsAndStatuses() {
        String[] dateTimes = {"Sep 20 2026", "10:30", "Sep 20 2026 10:30"};
        for (String status : new String[] {" ", "X"}) {
            for (String from : dateTimes) {
                String deadlineRow = "[D][" + status + "] work (by: " + from + ")";
                assertSavedTask(deadlineRow, Deadline.class, status.equals("X"));
                for (String to : dateTimes) {
                    String eventRow = "[E][" + status + "] meeting (from: " + from + " to: " + to + ")";
                    assertSavedTask(eventRow, Event.class, status.equals("X"));
                }
            }
        }
    }

    @Test
    public void parseTask_malformedRows_returnsNull() {
        String[] rows = {null, "", "  ", "todo read", "[T][ ] ", "[T][?] read", "[Z][ ] read",
            "[T][ ]read", "[T][ ]", "[T][ ]\tread", "[D][ ] work", "[D][ ]  (by: Sep 20 2026)",
            "[D][ ] work (by: )", "[D][ ] work (by: invalid)", "[D][ ] work (by: 2026-09-20)",
            "[D][ ] work (by: Sep 20 2026", "[E][ ] meeting (from: to: 10:00)",
            "[E][ ] meeting (from: 10:00 to: )", "[E][ ] meeting (from: bad to: 11:00)",
            "[E][ ] meeting (from: 10:00 to: 11:00", "[E][ ]  (from: 10:00 to: 11:00)"};
        for (String row : rows) {
            assertNull(SavedTaskParser.parseTask(row), row);
        }
    }

    @Test
    public void parseTask_savedDateNormalization_preservesCompatibility() {
        String[][] cases = {
            {"[D][ ] date (by: Feb 30 2025)", "[D][ ] date (by: Feb 28 2025)"},
            {"[D][ ] time (by: 24:00)", "[D][ ] time (by: 00:00)"},
            {"[D][ ] both (by: Sep 20 2026 24:00)", "[D][ ] both (by: Sep 20 2026 00:00)"}
        };
        for (String[] example : cases) {
            Task task = SavedTaskParser.parseTask(example[0]);
            assertNotNull(task);
            assertEquals(example[1], task.toString());
        }
    }

    /**
     * Checks a saved row against its expected task type, completion status, and display text.
     */
    private static void assertSavedTask(String row, Class<? extends Task> type, boolean isDone) {
        Task task = SavedTaskParser.parseTask(row);
        assertInstanceOf(type, task);
        assertEquals(isDone, task.isDone());
        assertEquals(row, task.toString());
    }
}
