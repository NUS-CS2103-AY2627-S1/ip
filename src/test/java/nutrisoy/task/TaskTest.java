package nutrisoy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void tags_areUniqueAndPreserveInsertionOrder() {
        Task task = new Todo("read book");
        task.addTag("fun");
        task.addTag("school");
        task.addTag("fun");

        assertEquals("[T][ ] read book #fun #school", task.toString());
        assertEquals("T | 0 | read book | fun,school", task.toFileFormat());

        task.removeTag("fun");
        assertEquals("[T][ ] read book #school", task.toString());
    }
}
