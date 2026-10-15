package bot67.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void taskLifecycleAndFileFormat_areUpdatedTogether() {
        Task task = new Task("read book", TaskType.TODO);
        assertEquals("[T][ ] read book", task.getDescription());
        assertEquals("T | 0 | read book", task.toFileFormat());

        task.mark();
        assertEquals("[T][X] read book", task.getDescription());
        assertEquals("T | 1 | read book", task.toFileFormat());

        task.unmark();
        assertEquals(" ", task.getStatusIcon());
    }
}
