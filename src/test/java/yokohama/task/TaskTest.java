package yokohama.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void hasKeyword_keywordMatchesIgnoringCase_true() {
        Task task = new Task("Buy Groceries", false);

        assertTrue(task.hasKeyword("groceries"));
    }

    @Test
    public void hasKeyword_keywordDoesNotMatch_false() {
        Task task = new Task("Buy Groceries", false);

        assertFalse(task.hasKeyword("book"));
    }

    @Test
    public void toDbString_taskNotDone_correctFormat() {
        Task task = new Task("buy groceries", false);
        assertEquals("T | 0 | buy groceries\n", task.toDbString());
    }

    @Test
    public void toDbString_taskDone_correctFormat() {
        Task task = new Task("read book", true);
        assertEquals("T | 1 | read book\n", task.toDbString());
    }

    @Test
    public void toString_validTask_correctFormat() {
        Task task = new Task("fix bug", false);

        assertEquals("[T][  ] fix bug", task.toString());
    }

    @Test
    public void occursOn_unscheduledTask_false() {
        Task task = new Task("buy groceries", false);

        assertFalse(task.occursOn(LocalDate.of(2026, 9, 10)));
    }

    @Test
    public void markComplete_incompleteTask_returnsTrueAndMarksTask() {
        Task task = new Task("buy groceries", false);

        assertTrue(task.markComplete());
        assertEquals("[T][ X ] buy groceries", task.toString());
    }

    @Test
    public void markComplete_completedTask_returnsFalse() {
        Task task = new Task("buy groceries", true);

        assertFalse(task.markComplete());
    }

    @Test
    public void unmarkComplete_completedTask_returnsTrueAndUnmarksTask() {
        Task task = new Task("buy groceries", true);

        assertTrue(task.unmarkComplete());
        assertEquals("[T][  ] buy groceries", task.toString());
    }

    @Test
    public void unmarkComplete_incompleteTask_returnsFalse() {
        Task task = new Task("buy groceries", false);

        assertFalse(task.unmarkComplete());
    }

    @Test
    public void constructor_blankDescription_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Task("  ", false));
    }

    @Test
    public void constructor_nullDescription_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> new Task(null, false));
    }
}
