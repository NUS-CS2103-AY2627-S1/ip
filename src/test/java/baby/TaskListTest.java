package baby;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void  add_taskAdded_sizeIncreases() {
        TaskList tasks = new TaskList(new ArrayList<>());
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void remove_existingTask_taskRemoved() {
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(new Todo("read book"));
        tasks.add(new Todo("do homework"));

        tasks.remove(0);

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] do homework", tasks.get(0).toString());
    }
}
