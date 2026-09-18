package sozius.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import sozius.exception.SoziusException;

public class DeadlineTaskTest {

    @Test
    public void toUserString_dateOnly_formatsCorrectly() throws SoziusException {
        DeadlineTask task = new DeadlineTask("return book", DueDate.parse("2024-12-31"));
        assertEquals("[D][ ] return book (by: Dec 31 2024)", task.toUserString());
    }

    @Test
    public void toUserString_withTime_formatsCorrectly() throws SoziusException {
        DeadlineTask task = new DeadlineTask("return book", DueDate.parse("2024-12-31 1800"));
        assertEquals("[D][ ] return book (by: Dec 31 2024 1800)", task.toUserString());
    }

    @Test
    public void toUserString_done_showsX() throws SoziusException {
        DeadlineTask task = new DeadlineTask("return book", DueDate.parse("2024-12-31"));
        task.setDone(true);
        assertEquals("[D][X] return book (by: Dec 31 2024)", task.toUserString());
    }

    @Test
    public void toFileString_dateOnly_formatsCorrectly() throws SoziusException {
        DeadlineTask task = new DeadlineTask("return book", DueDate.parse("2024-12-31"));
        assertEquals("D | 0 | return book | 2024-12-31", task.toFileString());
    }

    @Test
    public void toFileString_withTimeAndDone_formatsCorrectly() throws SoziusException {
        DeadlineTask task = new DeadlineTask("return book", DueDate.parse("2024-12-31 1800"));
        task.setDone(true);
        assertEquals("D | 1 | return book | 2024-12-31 1800", task.toFileString());
    }
}
