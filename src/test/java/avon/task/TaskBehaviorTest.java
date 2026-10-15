package avon.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class TaskBehaviorTest {
    private static final LocalDateTime START = LocalDateTime.of(2026, 9, 20, 14, 0);
    private static final LocalDateTime END = LocalDateTime.of(2026, 9, 20, 16, 0);

    @Test
    void todo_completionChanges_updatesStatusAndDisplay() {
        Todo todo = new Todo("read Hamlet");

        assertFalse(todo.isDone());
        assertEquals(" ", todo.getStatusIcon());
        assertEquals("[T][ ] read Hamlet", todo.toString());

        todo.markAsDone();
        assertTrue(todo.isDone());
        assertEquals("X", todo.getStatusIcon());
        assertEquals("[T][X] read Hamlet", todo.toString());

        todo.markAsNotDone();
        assertFalse(todo.isDone());
    }

    @Test
    void deadline_gettersAndDisplay_returnSuppliedDetails() {
        Deadline deadline = new Deadline("submit iP", END);

        assertEquals("submit iP", deadline.getDescription());
        assertEquals(END, deadline.getBy());
        assertEquals("[D][ ] submit iP (by: Sep 20 2026, 4:00PM)", deadline.toString());
    }

    @Test
    void event_gettersAndDisplay_returnSuppliedDetails() {
        Event event = new Event("rehearsal", START, END);

        assertEquals(START, event.getFrom());
        assertEquals(END, event.getTo());
        assertEquals("[E][ ] rehearsal (from: Sep 20 2026, 2:00PM to: Sep 20 2026, 4:00PM)",
                event.toString());
    }

    @Test
    void hasSameDetailsAs_nullDifferentTypeOrDifferentDetails_returnsFalse() {
        Deadline deadline = new Deadline("submit iP", END);

        assertFalse(deadline.hasSameDetailsAs(null));
        assertFalse(deadline.hasSameDetailsAs(new Todo("submit iP")));
        assertFalse(deadline.hasSameDetailsAs(new Deadline("submit report", END)));
        assertFalse(deadline.hasSameDetailsAs(new Deadline("submit iP", START)));
    }

    @Test
    void hasSameDetailsAs_matchingTaskIgnoringCompletion_returnsTrue() {
        Event completedEvent = new Event("rehearsal", START, END);
        completedEvent.markAsDone();

        assertTrue(completedEvent.hasSameDetailsAs(new Event("rehearsal", START, END)));
    }
}
