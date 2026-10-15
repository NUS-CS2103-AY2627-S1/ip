package esther.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void countNotDone_mixedCompletionStatuses_returnsIncompleteCount() {
        Task completedTask = new Todo("Read a book");
        completedTask.markAsDone();
        TaskList taskList = new TaskList(List.of(
                completedTask,
                new Todo("Return a book"),
                new Todo("Buy milk")));

        assertEquals(2, taskList.countNotDone());
    }

    @Test
    public void add_nullTask_throwsAssertionError() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.add(null));
    }

    @Test
    public void get_outOfBoundsIndex_throwsAssertionError() {
        TaskList taskList = new TaskList(List.of(new Todo("Read a book")));

        assertThrows(AssertionError.class, () -> taskList.get(1));
    }

    @Test
    public void delete_negativeIndex_throwsAssertionError() {
        TaskList taskList = new TaskList(List.of(new Todo("Read a book")));

        assertThrows(AssertionError.class, () -> taskList.delete(-1));
    }

    @Test
    public void find_matchingKeyword_returnsMatchingTasksInOrder() {
        Task firstTask = new Todo("Read a book");
        Task secondTask = new Todo("Return a book");
        Task thirdTask = new Todo("Buy milk");

        TaskList taskList = new TaskList(
                List.of(firstTask, secondTask, thirdTask));

        List<Task> matchingTasks = taskList.find("book");

        assertEquals(2, matchingTasks.size());
        assertEquals(firstTask, matchingTasks.get(0));
        assertEquals(secondTask, matchingTasks.get(1));
    }

    @Test
    public void find_missingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList(
                List.of(new Todo("Read a book")));

        assertTrue(taskList.find("movie").isEmpty());
    }
}
