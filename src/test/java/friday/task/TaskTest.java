package friday.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Tests behavior shared by all task types. */
class TaskTest {

    @Test
    void markAsDone_undoneTask_updatesDisplayStatus() {
        Task task = new Task("read book", "[T]");

        task.markAsDone();

        assertEquals("[T][X] read book", task.toString());
    }
}
