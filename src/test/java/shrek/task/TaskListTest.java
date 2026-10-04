package shrek.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void add_singleTask_increasesSizeByOne() {
        TaskList tasks = new TaskList();
        assertEquals(0, tasks.size());
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.size());
    }

    @Test
    public void add_multipleTasks_preservesOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        assertEquals("first", tasks.get(0).getDescription());
        assertEquals("second", tasks.get(1).getDescription());
    }

    @Test
    public void remove_existingIndex_removesAndReturnsTask() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        Task removed = tasks.remove(0);

        assertEquals("read book", removed.getDescription());
        assertEquals(1, tasks.size());
        assertEquals("return book", tasks.get(0).getDescription());
    }

    @Test
    public void getAll_returnsUnderlyingList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        assertEquals(1, tasks.getAll().size());
    }

    @Test
    public void find_keywordMatchesCaseInsensitively() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write notes"));

        assertEquals(1, tasks.find("BOOK").size());
        assertEquals("read book", tasks.find("BOOK").get(0).getDescription());
    }

    @Test
    public void find_keywordMatchesTagSubstring() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("play game", java.util.List.of("#weekend")));
        tasks.add(new Todo("read book"));

        assertEquals(1, tasks.find("WEEK").size());
        assertEquals("play game", tasks.find("WEEK").get(0).getDescription());
    }
}
