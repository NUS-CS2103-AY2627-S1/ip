package nicola;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    void add_taskIncreaseListSize() {
        TaskList tasks = new TaskList();

        tasks.add(new TodoTask("buy milk"));

        assertEquals(1, tasks.size());
    }

    @Test
    void remove_taskDecreaseListSize() {
        TaskList tasks = new TaskList();
        tasks.add(new TodoTask("buy milk"));

        tasks.remove(0);

        assertEquals(0, tasks.size());
    }
}
