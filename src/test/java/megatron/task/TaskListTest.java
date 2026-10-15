package megatron.task;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests task collection validation. */
class TaskListTest {
    @Test
    void add_duplicateTask_throwsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("conquer the Autobots"));

        assertThrows(IllegalArgumentException.class,
                () -> tasks.add(new Todo("conquer the Autobots")));
    }
}
