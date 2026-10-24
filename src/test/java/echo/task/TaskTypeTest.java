package echo.task; // same package as the class being tested

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskTypeTest {
    @Test
    public void getTag_todo_returnsT() {
        assertEquals("T", TaskType.TODO.getTag());
    }

    @Test
    public void getTag_deadline_returnsD() {
        assertEquals("D", TaskType.DEADLINE.getTag());
    }

    @Test
    public void getTag_event_returnsE() {
        assertEquals("E", TaskType.EVENT.getTag());
    }
}
