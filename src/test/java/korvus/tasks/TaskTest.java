package korvus.tasks;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class TaskTest {

    @Test
    public void taskCreation_noError() {
        assertDoesNotThrow(() -> new Task("Sample Task", null));
        assertDoesNotThrow(() -> new Task("Sample Task 2", null));
        assertDoesNotThrow(() -> new Task("", null));
    }

    @Test
    public void taskCreation_nullError() {
        assertThrows(NullPointerException.class, () -> new Task(null, null));
    }

    @Test
    public void taskCreation_invalidTaskError() {
        assertThrows(InvalidTaskException.class, () -> new Task("-invalid task name", null));
        assertThrows(InvalidTaskException.class, () -> new Task("-", null));
    }

    @Test
    public void taskGeneration_invalidTaskError() {
        assertThrows(InvalidTaskException.class, () -> Task.generateTask("-d -invalid name // some date", null));
        assertThrows(InvalidTaskException.class, () -> Task.generateTask("-t -still bad", null));
        assertThrows(InvalidTaskException.class, () -> Task.generateTask("-e -", null));
    }
}
