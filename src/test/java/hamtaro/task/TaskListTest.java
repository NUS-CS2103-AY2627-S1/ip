package hamtaro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

/** Tests task-list operations that involve more than one task. */
class TaskListTest {

    @Test
    void findTasks_keywordMatchesDescriptionsIgnoringCase() {
        TaskList tasks = new TaskList();
        Task firstMatch = new Todo("Read project proposal");
        Task nonMatch = new Todo("Buy groceries");
        Task secondMatch = new Todo("Submit PROJECT report");
        tasks.addTask(firstMatch);
        tasks.addTask(nonMatch);
        tasks.addTask(secondMatch);

        List<Task> matchingTasks = tasks.findTasks("project");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    void findTasks_keywordWithNoMatches_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("Read book"));

        assertEquals(List.of(), tasks.findTasks("exercise"));
    }

    @Test
    void findTasksByTag_matchesTagsIgnoringCase() {
        Task matchingTask = new Todo("Go out");
        matchingTask.addTag("#Fun");
        Task nonMatchingTask = new Todo("Stay home");
        TaskList tasks = new TaskList();
        tasks.addTask(matchingTask);
        tasks.addTask(nonMatchingTask);

        assertEquals(List.of(matchingTask), tasks.findTasksByTag("FUN"));
    }
}
