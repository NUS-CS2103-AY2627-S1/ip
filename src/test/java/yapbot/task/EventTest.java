package yapbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;

public class EventTest {

    @Test
    public void toFileFormat_returnsFromAndToAppended() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("E | 0 | project meeting | Mon 2pm | 4pm", event.toFileFormat());
    }

    @Test
    public void toFileFormat_doneTask_statusFieldReflectsDone() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        event.markAsDone();

        assertEquals("E | 1 | project meeting | Mon 2pm | 4pm", event.toFileFormat());
    }

    @Test
    public void toString_notDone_showsFromAndTo() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        assertEquals("[E][ ]project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void toString_done_showsXMarkFromAndTo() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        event.markAsDone();

        assertEquals("[E][X]project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void toFileFormat_prioritySet_appendsPriorityAfterFromAndTo() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        event.setPriority(Priority.MEDIUM);

        assertEquals("E | 0 | project meeting | Mon 2pm | 4pm | MEDIUM", event.toFileFormat());
    }

    @Test
    public void toString_prioritySet_showsPriorityTagBeforeDescription() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        event.setPriority(Priority.MEDIUM);

        assertEquals("[E][ ][MEDIUM]project meeting (from: Mon 2pm to: 4pm)", event.toString());
    }

    @Test
    public void isDuplicateOf_sameDetailsDifferentCase_returnsTrue() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        Event other = new Event("Project Meeting", "MON 2PM", "4PM");

        assertTrue(event.isDuplicateOf(other));
    }

    @Test
    public void isDuplicateOf_differentToTime_returnsFalse() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        Event other = new Event("project meeting", "Mon 2pm", "5pm");

        assertFalse(event.isDuplicateOf(other));
    }

    @Test
    public void isDuplicateOf_sameDetailsButTodo_returnsFalse() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");
        Todo other = new Todo("project meeting");

        assertFalse(event.isDuplicateOf(other));
    }

    @Test
    public void isOverdue_toIsFreeText_returnsFalse() {
        Event event = new Event("project meeting", "Mon 2pm", "4pm");

        assertFalse(event.isOverdue());
    }

    @Test
    public void isOverdue_toIsDateInPast_returnsTrue() {
        String pastDate = LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        Event event = new Event("project meeting", "2019-10-15", pastDate);

        assertTrue(event.isOverdue());
    }

    @Test
    public void isOverdue_toIsDateInFuture_returnsFalse() {
        String futureDate = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        Event event = new Event("project meeting", "2019-10-15", futureDate);

        assertFalse(event.isOverdue());
    }
}
