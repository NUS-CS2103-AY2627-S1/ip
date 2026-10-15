package sherlock.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task list storage and retrieval behavior. */
class TaskListTest {
    @Test
    void addThenGet_preservesTaskAndIncreasesSize() {
        TaskList tasks = new TaskList(1);
        Task task = new Todo("review clues");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    void delete_removesTaskAndReturnsIt() {
        TaskList tasks = new TaskList(2);
        Task firstTask = new Todo("first clue");
        Task secondTask = new Todo("second clue");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task deletedTask = tasks.delete(0);

        assertEquals(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertEquals(secondTask, tasks.get(0));
    }

    @Test
    void find_returnsMatchingTasksInListOrderIgnoringCase() {
        TaskList tasks = new TaskList(3);
        Task firstMatch = new Todo("read book");
        Task secondMatch = new Deadline("return BOOK", "2019-12-02");
        tasks.add(firstMatch);
        tasks.add(new Todo("plan meeting"));
        tasks.add(secondMatch);

        assertEquals(List.of(firstMatch, secondMatch), tasks.find("book"));
    }
}
