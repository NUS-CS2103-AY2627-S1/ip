package sozius.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import sozius.exception.SoziusException;

public class EventTaskTest {

    @Test
    public void toUserString_datesOnly_formatsCorrectly() throws SoziusException {
        EventTask task = new EventTask("project meeting",
                DueDate.parse("2024-01-01"), DueDate.parse("2024-01-02"));
        assertEquals("[E][ ] project meeting (from: Jan 1 2024 to: Jan 2 2024)",
                task.toUserString());
    }

    @Test
    public void toUserString_withTimes_formatsCorrectly() throws SoziusException {
        EventTask task = new EventTask("project meeting",
                DueDate.parse("2024-01-01 1000"), DueDate.parse("2024-01-01 1200"));
        assertEquals("[E][ ] project meeting (from: Jan 1 2024 1000 to: Jan 1 2024 1200)",
                task.toUserString());
    }

    @Test
    public void toUserString_done_showsX() throws SoziusException {
        EventTask task = new EventTask("project meeting",
                DueDate.parse("2024-01-01"), DueDate.parse("2024-01-02"));
        task.setDone(true);
        assertEquals("[E][X] project meeting (from: Jan 1 2024 to: Jan 2 2024)",
                task.toUserString());
    }

    @Test
    public void toFileString_datesOnly_formatsCorrectly() throws SoziusException {
        EventTask task = new EventTask("project meeting",
                DueDate.parse("2024-01-01"), DueDate.parse("2024-01-02"));
        assertEquals("E | 0 | project meeting | 2024-01-01/2024-01-02", task.toFileString());
    }

    @Test
    public void toFileString_withTimesAndDone_formatsCorrectly() throws SoziusException {
        EventTask task = new EventTask("project meeting",
                DueDate.parse("2024-01-01 1000"), DueDate.parse("2024-01-01 1200"));
        task.setDone(true);
        assertEquals("E | 1 | project meeting | 2024-01-01 1000/2024-01-01 1200",
                task.toFileString());
    }
}
