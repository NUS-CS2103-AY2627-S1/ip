package eve.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests the equals()/hashCode() overrides that {@link eve.command.AddCommand}
 * relies on to reject an exact duplicate task.
 */
public class TaskEqualityTest {
    private static final LocalDate DATE = LocalDate.of(2020, 1, 1);
    private static final LocalDate OTHER_DATE = LocalDate.of(2020, 2, 1);

    @Test
    public void equals_sameTypeAndDescription_true() {
        assertEquals(new ToDo("read book"), new ToDo("read book"));
    }

    @Test
    public void equals_differentDescription_false() {
        assertNotEquals(new ToDo("read book"), new ToDo("return book"));
    }

    @Test
    public void equals_sameDescriptionDifferentType_false() {
        assertNotEquals(new ToDo("read book"), new Deadline("read book", DATE));
    }

    @Test
    public void equals_doneStatusIgnored_stillEqual() {
        ToDo done = new ToDo("read book");
        done.markAsDone();

        assertEquals(done, new ToDo("read book"));
    }

    @Test
    public void equals_deadlineSameDate_true() {
        assertEquals(new Deadline("pay rent", DATE), new Deadline("pay rent", DATE));
    }

    @Test
    public void equals_deadlineDifferentDate_false() {
        assertNotEquals(new Deadline("pay rent", DATE), new Deadline("pay rent", OTHER_DATE));
    }

    @Test
    public void equals_eventSameDates_true() {
        assertEquals(new Event("trip", DATE, OTHER_DATE), new Event("trip", DATE, OTHER_DATE));
    }

    @Test
    public void equals_eventDifferentEndDate_false() {
        assertNotEquals(new Event("trip", DATE, OTHER_DATE), new Event("trip", DATE, DATE));
    }

    @Test
    public void hashCode_equalTasks_sameHashCode() {
        assertEquals(new Deadline("pay rent", DATE).hashCode(), new Deadline("pay rent", DATE).hashCode());
    }
}
