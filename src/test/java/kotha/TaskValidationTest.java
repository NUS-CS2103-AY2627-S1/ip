package kotha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import kotha.tasks.Constraint;
import kotha.tasks.Deadline;
import kotha.tasks.Task;

class TaskValidationTest {
    @Test
    void createTodo_rejectsNullAndMissingDescription() {
        assertThrows(KothaException.class, () -> Task.createTodo(null));
        assertThrows(KothaException.class, () -> Task.createTodo("todo"));
        assertThrows(KothaException.class, () -> Task.createTodo("todo   "));
    }

    @Test
    void createDeadline_acceptsFullDateAndTime() throws KothaException {
        Deadline task = (Deadline) Task.createDeadline("deadline submit /by 16/05/2027 1159");

        assertEquals(LocalDateTime.of(2027, 5, 16, 11, 59), task.getDeadline());
        assertEquals("[D][ ] submit (by: May 16 2027 11:59am)", task.toString());
    }

    @Test
    void createDeadline_rejectsMissingDuplicateAndInvalidMarkers() {
        assertThrows(KothaException.class, () -> Task.createDeadline("deadline submit"));
        assertThrows(KothaException.class, () -> Task.createDeadline("deadline submit /by"));
        assertThrows(KothaException.class, () -> Task.createDeadline("deadline submit /by 1/1 /by 2/2"));
        assertThrows(KothaException.class, () -> Task.createDeadline("deadline submit /by 1/13/2027"));
    }

    @Test
    void createEvent_rejectsMalformedRangeAndEndBeforeStart() {
        assertThrows(KothaException.class, () -> Task.createEvent("event meeting /from 1/1"));
        assertThrows(KothaException.class, () -> Task.createEvent("event meeting /from 2/1 /to 1/1"));
        assertThrows(KothaException.class, () -> Task.createEvent(
                "event meeting /from 1/1 /to 2/1 /to 3/1"));
    }

    @Test
    void createConstraint_supportsTextAndDateTriggers() throws KothaException {
        Constraint text = (Constraint) Task.createConstraint("constraint pay bill /after salary");
        Constraint date = (Constraint) Task.createConstraint(
                "constraint pay bill /afterdate 16/05/2027 1159");

        assertEquals("salary", text.getAfter());
        assertEquals(LocalDateTime.of(2027, 5, 16, 11, 59), date.getAfterDate());
        assertThrows(KothaException.class, () -> Task.createConstraint("constraint pay bill"));
        assertThrows(KothaException.class, () -> Task.createConstraint("constraint pay bill /afterdate"));
    }
}
