package penny;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests task creation, display, completion, and date matching.
 */
public class TaskTest {
    @Test
    void todo_completionLifecycle_preservesDescription() throws PennyException {
        Task task = ToDoTask.create("  read book  ");
        assertEquals("read book", task.getDescription());
        assertEquals("[T][ ] read book", task.toString());
        assertFalse(task.isDone());
        assertThrows(PennyException.class, task::unmarkAsDone);
        task.markAsDone();
        assertTrue(task.isDone());
        assertEquals("[T][X] read book", task.toString());
        assertThrows(PennyException.class, task::markAsDone);
        task.unmarkAsDone();
        assertFalse(task.isDone());
        assertFalse(task.isDueOn(LocalDateTime.of(2026, 9, 15, 0, 0)));
        assertTrue(task.hasKeyword("book"));
        assertFalse(task.hasKeyword("BOOK"));
    }

    @Test
    void todo_blankDescription_rejected() {
        for (String input : new String[]{"", " ", "\t\n"}) {
            assertThrows(PennyException.class, () -> ToDoTask.create(input), input);
        }
    }

    @Test
    void deadline_validInput_formatsAndMatchesCalendarDate() throws PennyException {
        Task task = DeadlineTask.create("  submit  /by 15092026 1800 ");
        assertEquals("submit", task.getDescription());
        assertEquals("[D][ ] submit (by: 15 09 2026 1800)", task.toString());
        assertTrue(task.isDueOn(LocalDateTime.of(2026, 9, 15, 1, 0)));
        assertFalse(task.isDueOn(LocalDateTime.of(2026, 9, 16, 18, 0)));
        task.markAsDone();
        assertEquals("[D][X] submit (by: 15 09 2026 1800)", task.toString());
    }

    @Test
    void deadline_invalidArguments_rejected() {
        for (String input : new String[]{"submit", "/by 15092026", "submit /by", "submit /by bad"}) {
            assertThrows(PennyException.class, () -> DeadlineTask.create(input), input);
        }
    }

    @Test
    void event_validInput_matchesStartDateOnly() throws PennyException {
        Task task = EventTask.create("  trip /from 15092026 /to 17092026 1800 ");
        assertEquals("trip", task.getDescription());
        assertEquals("[E][ ] trip (from: 15 09 2026 | to: 17 09 2026 1800)", task.toString());
        assertTrue(task.isDueOn(LocalDateTime.of(2026, 9, 15, 23, 59)));
        assertFalse(task.isDueOn(LocalDateTime.of(2026, 9, 16, 0, 0)));
        assertFalse(task.isDueOn(LocalDateTime.of(2026, 9, 17, 0, 0)));
        assertTrue(task.hasKeyword("17 09 2026"));
    }

    @Test
    void event_invalidArguments_rejected() {
        String[] inputs = {"trip", "trip /from 15092026", "trip /to 15092026",
            "trip /to 17092026 /from 15092026", "/from 15092026 /to 17092026",
            "trip /from /to 17092026", "trip /from 15092026 /to",
            "trip /from bad /to 17092026", "trip /from 15092026 /to bad"};
        for (String input : inputs) {
            assertThrows(PennyException.class, () -> EventTask.create(input), input);
        }
    }
}
