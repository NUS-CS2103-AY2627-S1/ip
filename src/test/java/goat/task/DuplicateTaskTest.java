package goat.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/**
 * Tests what counts as the same task.
 *
 * The line has to fall in the right place: too loose and Goat refuses tasks a
 * user legitimately wants twice, too tight and the check never fires.
 */
public class DuplicateTaskTest {

    private static final LocalDateTime EARLY = LocalDateTime.of(2019, 8, 6, 14, 0);
    private static final LocalDateTime LATE = LocalDateTime.of(2019, 8, 6, 16, 0);

    @Test
    public void hasSameDetailsAs_identicalTodos_true() {
        assertTrue(new Todo("read book").hasSameDetailsAs(new Todo("read book")));
    }

    @Test
    public void hasSameDetailsAs_differentDescriptions_false() {
        assertFalse(new Todo("read book").hasSameDetailsAs(new Todo("read books")));
    }

    @Test
    public void hasSameDetailsAs_differentCase_false() {
        // Capitalization is part of what the user wrote, so it is a real
        // difference rather than a near-miss to be second-guessed.
        assertFalse(new Todo("read book").hasSameDetailsAs(new Todo("Read Book")));
    }

    @Test
    public void hasSameDetailsAs_oneIsDone_stillTrue() {
        Todo done = new Todo("read book");
        done.markAsDone();
        // A task already ticked off is still the same task; a second copy would
        // be no more useful than the first.
        assertTrue(done.hasSameDetailsAs(new Todo("read book")));
    }

    @Test
    public void hasSameDetailsAs_differentTypes_false() {
        assertFalse(new Todo("x").hasSameDetailsAs(new Deadline("x", EARLY)));
        assertFalse(new Deadline("x", EARLY).hasSameDetailsAs(new Todo("x")));
    }

    @Test
    public void hasSameDetailsAs_deadlinesDifferingOnlyInDate_false() {
        assertFalse(new Deadline("x", EARLY).hasSameDetailsAs(new Deadline("x", LATE)));
    }

    @Test
    public void hasSameDetailsAs_identicalDeadlines_true() {
        assertTrue(new Deadline("x", EARLY).hasSameDetailsAs(new Deadline("x", EARLY)));
    }

    @Test
    public void hasSameDetailsAs_eventsDifferingOnlyInEnd_false() {
        // getScheduledTime reports only the start, so without Event's override
        // these two would wrongly count as the same event.
        assertFalse(new Event("x", EARLY, LATE)
                .hasSameDetailsAs(new Event("x", EARLY, LATE.plusHours(1))));
    }

    @Test
    public void hasSameDetailsAs_identicalEvents_true() {
        assertTrue(new Event("x", EARLY, LATE).hasSameDetailsAs(new Event("x", EARLY, LATE)));
    }

    @Test
    public void containsSameTaskAs_listHoldingIt_true() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("pay fees", EARLY));

        assertTrue(tasks.containsSameTaskAs(new Todo("read book")));
        assertTrue(tasks.containsSameTaskAs(new Deadline("pay fees", EARLY)));
    }

    @Test
    public void containsSameTaskAs_listWithoutIt_false() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertFalse(tasks.containsSameTaskAs(new Todo("write book")));
        assertFalse(tasks.containsSameTaskAs(new Deadline("read book", EARLY)));
    }

    @Test
    public void containsSameTaskAs_emptyList_false() {
        assertFalse(new TaskList().containsSameTaskAs(new Todo("read book")));
    }
}
