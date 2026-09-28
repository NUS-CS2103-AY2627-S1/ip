package kotha.tasks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import kotha.KothaException;

class ConstraintTest {
    @Test
    void createConstraint_createsTaskWithTriggerAndExpectedText() throws KothaException {
        Constraint task = (Constraint) Task.createConstraint("constraint return book /after exam");

        assertEquals("return book", task.getDescription());
        assertEquals("exam", task.getAfter());
        assertEquals("[C][ ] return book (after: exam)", task.toString());
    }

    @Test
    void createConstraint_afterDateAcceptsDateTime() throws KothaException {
        Constraint task = (Constraint) Task.createConstraint(
                "constraint submit report /afterdate 16/05/2027 1159");

        assertEquals("submit report", task.getDescription());
        assertEquals(LocalDateTime.of(2027, 5, 16, 11, 59), task.getAfterDate());
        assertEquals("[C][ ] submit report (after: May 16 2027 11:59am)", task.toString());
    }

    @Test
    void createConstraint_afterDateRejectsInvalidDateTime() {
        assertThrows(KothaException.class, () -> Task.createConstraint(
                "constraint submit report /afterdate not-a-date"));
    }
}
