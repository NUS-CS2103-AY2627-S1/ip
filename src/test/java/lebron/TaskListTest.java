package lebron;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void delete_existingTask_removesCorrectTask() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));
        tasks.add(new Todo("play basketball"));

        Task deletedTask = tasks.delete(1);

        assertEquals(
                "[T][ ] read book",
                deletedTask.toString()
        );

        assertEquals(
                1,
                tasks.size()
        );

        assertEquals(
                "[T][ ] play basketball",
                tasks.get(0).toString()
        );
    }

    @Test
    public void mark_existingTask_marksCorrectTask() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));

        Task markedTask = tasks.mark(1);

        assertEquals(
                "[T][X] read book",
                markedTask.toString()
        );
    }

    @Test
    public void find_multipleKeywords_returnsMatchingTasks() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));
        tasks.add(new Todo("play basketball"));
        tasks.add(new Todo("buy groceries"));

        TaskList results = tasks.find("book", "basketball");

        assertEquals(2, results.size());
        assertEquals("[T][ ] read book", results.get(0).toString());
        assertEquals("[T][ ] play basketball", results.get(1).toString());
    }

    @Test
    public void sort_unsortedTasks_returnsAlphabeticalOrder() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("wash clothes"));
        tasks.add(new Todo("buy groceries"));
        tasks.add(new Todo("read book"));

        tasks.sort();

        assertEquals("[T][ ] buy groceries", tasks.get(0).toString());
        assertEquals("[T][ ] read book", tasks.get(1).toString());
        assertEquals("[T][ ] wash clothes", tasks.get(2).toString());
    }
}


