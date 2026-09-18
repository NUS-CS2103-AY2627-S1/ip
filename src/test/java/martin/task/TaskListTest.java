package martin.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for task-list operations.
 */
public class TaskListTest {

    @Test
    public void constructor_varargs_initializesTasksCorrectly() {
        Todo firstTask = new Todo("Read a book");
        Todo secondTask = new Todo("Book a flight");
        TaskList taskList = new TaskList(firstTask, secondTask);

        assertEquals(2, taskList.size());
        assertEquals(firstTask, taskList.get(0));
        assertEquals(secondTask, taskList.get(1));
    }

    @Test
    public void find_keywordMatchesIgnoringCase_returnsTasksInOriginalOrder() {
        Todo firstTask = new Todo("Read a book");
        Todo secondTask = new Todo("Book a flight");
        TaskList taskList = new TaskList(List.of(firstTask, new Todo("Buy groceries"), secondTask));

        assertEquals(List.of(firstTask, secondTask), taskList.find(" BOOK "));
    }

    @Test
    public void find_noMatchingDescription_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("Buy groceries")));

        assertEquals(List.of(), taskList.find("book"));
    }

    @Test
    public void find_nullKeyword_throwsIllegalArgumentException() {
        TaskList taskList = new TaskList();

        assertThrows(IllegalArgumentException.class, () -> taskList.find(null));
    }
}
