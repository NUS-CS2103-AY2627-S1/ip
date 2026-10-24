package nabmak.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link TaskList}.
 */
public class TaskListTest {
    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        taskList = new TaskList(new ArrayList<>());
        taskList.add(new ToDo("read book"));
        taskList.add(new ToDo("return book"));
        taskList.add(new ToDo("buy mcdonalds"));
    }

    @Test
    public void add_newTask_sizeIncreases() {
        taskList.add(new ToDo("sleep"));
        assertEquals(4, taskList.size());
        assertEquals("sleep", taskList.get(3).getDesc());
    }

    @Test
    public void delete_validIndex_returnsDeletedTaskAndShrinksList() {
        Task second = taskList.get(1);
        Task deleted = taskList.delete(1);

        assertSame(second, deleted);
        assertEquals(2, taskList.size());
        assertEquals("buy mcdonalds", taskList.get(1).getDesc());
    }

    @Test
    public void find_keyword_returnsMatches() {
        TaskList result = taskList.find("book");

        assertEquals(2, result.size());
        assertEquals("read book", result.get(0).getDesc());
        assertEquals("return book", result.get(1).getDesc());
    }

    @Test
    public void find_noMatch_returnsEmptyList() {
        assertTrue(taskList.find("tough").size() == 0);
    }

}
