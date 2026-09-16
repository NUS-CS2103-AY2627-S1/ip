package eve.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the behavior {@link Task} defines for every task type, exercised
 * through {@link ToDo} since {@link Task} has no public constructor of its
 * own and ToDo adds no behavior beyond it.
 */
public class TaskTest {
    @Test
    public void getStatusIcon_notDone_isSpace() {
        assertEquals(" ", new ToDo("read book").getStatusIcon());
    }

    @Test
    public void getStatusIcon_done_isX() {
        Task task = new ToDo("read book");
        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsDone_thenMarkAsNotDone_reversesStatus() {
        Task task = new ToDo("read book");

        task.markAsDone();
        task.markAsNotDone();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void matches_keywordPresentDifferentCase_true() {
        assertTrue(new ToDo("Read Book").matches("book"));
    }

    @Test
    public void matches_keywordAbsent_false() {
        assertFalse(new ToDo("read book").matches("sports"));
    }

    @Test
    public void occursOn_plainTask_alwaysFalse() {
        assertFalse(new ToDo("read book").occursOn(LocalDate.of(2019, 1, 1)));
    }

    @Test
    public void getScheduleDate_plainTask_isEmpty() {
        assertTrue(new ToDo("read book").getScheduleDate().isEmpty());
    }
}
