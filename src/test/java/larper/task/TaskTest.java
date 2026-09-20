package larper.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void toStringAndFileString_unknownTypeFormat() {
        Task task = new Task("plain task");

        assertEquals("plain task", task.getDescription());
        assertEquals("?", task.getTypeIcon());
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[?][ ] plain task", task.toString());
        assertEquals("? | 0 | plain task", task.toFileString());
    }

    @Test
    public void markAndUnmark_statusIconAndFileStringUpdated() {
        Task task = new Task("plain task");

        task.markAsDone();
        assertEquals("X", task.getStatusIcon());
        assertEquals("[?][X] plain task", task.toString());
        assertEquals("? | 1 | plain task", task.toFileString());

        task.unmarkAsDone();
        assertEquals(" ", task.getStatusIcon());
        assertEquals("[?][ ] plain task", task.toString());
        assertEquals("? | 0 | plain task", task.toFileString());
    }

    @Test
    public void setDone_trueAndFalse_statusUpdated() {
        Task task = new Task("plain task");

        task.setDone(true);
        assertEquals("X", task.getStatusIcon());

        task.setDone(false);
        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void addTag_mixedCaseAndNumberSign_normalizedAndShownAtEnd() {
        Task task = new Task("plain task");

        task.addTag("#Work");
        task.addTag("urgent");

        assertEquals("[urgent, work]", task.getTags().toString());
        assertEquals("[?][ ] plain task [#urgent] [#work]", task.toString());
        assertEquals("? | 0 | plain task | #urgent #work", task.toFileString());
    }
}
