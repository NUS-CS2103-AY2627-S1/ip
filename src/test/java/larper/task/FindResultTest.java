package larper.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

public class FindResultTest {
    @Test
    public void toString_taskAndNumber_expectedFormat() {
        Task task = new Todo("read book");
        FindResult result = new FindResult(4, task);

        assertEquals(4, result.getTaskNumber());
        assertSame(task, result.getTask());
        assertEquals("[T][ ] read book (task no: 4)", result.toString());
    }
}
