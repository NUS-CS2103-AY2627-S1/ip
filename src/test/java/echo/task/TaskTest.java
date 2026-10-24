package echo.task; // same package as the class being tested

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class TaskTest {
    @Test
    public void matchesKeyword_descriptionContainsKeyword_returnsTrue() {
        Task task = new Task("read book", TaskType.TODO);
        assertTrue(task.matchesKeyword("book"));
    }

    @Test
    public void matchesKeyword_differentCase_returnsTrue() {
        Task task = new Task("read book", TaskType.TODO);
        assertTrue(task.matchesKeyword("BOOK"));
    }

    @Test
    public void matchesKeyword_descriptionMissingKeyword_returnsFalse() {
        Task task = new Task("read book", TaskType.TODO);
        assertFalse(task.matchesKeyword("magazine"));
    }
}
