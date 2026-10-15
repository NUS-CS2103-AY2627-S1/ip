package bern.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bern.logic.Response;
import bern.logic.ScheduleResponse;

/**
 * Verifies task completion messages, token-based searches, and defensive list copies.
 */
public class TaskManagerTest {
    private TaskManager taskManager;

    /**
     * Resets the singleton manager and adds three incomplete tasks before each test.
     */
    @BeforeEach
    public void init() {
        taskManager = TaskManager.getInstance();
        taskManager.clear();
        taskManager.addTask(new Todo("sample task 1"));
        taskManager.addTask(new Todo("sample task 2"));
        taskManager.addTask(new Todo("sample task 3"));
    }

    @Test
    public void markTask_alreadyMarked_showsDifferentMessage() {
        Response markTaskOnce = taskManager.markTask(1);
        Response markTaskTwice = taskManager.markTask(1);
        assertNotEquals(markTaskOnce.getResponseText(), markTaskTwice.getResponseText());
    }

    @Test
    public void unmarkTask_alreadyUnmarked_showsDifferentMessage() {
        taskManager.markTask(1);
        Response unmarkTaskOnce = taskManager.unmarkTask(1);
        Response unmarkTaskTwice = taskManager.unmarkTask(1);
        assertNotEquals(unmarkTaskOnce.getResponseText(), unmarkTaskTwice.getResponseText());
    }

    @Test
    public void findTask_onlySearchesByTokens() {
        Response findTasksWithSample = taskManager.findTasks("sample");
        Response findTasksWithAmple = taskManager.findTasks("ample");

        assertInstanceOf(ScheduleResponse.class, findTasksWithSample);
        assertFalse(findTasksWithAmple instanceof ScheduleResponse);
    }

    @Test
    public void getTaskList_returnsNewCopy() {
        int previousSize = taskManager.size();
        List<Task> taskList = taskManager.getTaskList();
        taskList.clear(); // Modify the output of getTaskList
        assertEquals(taskManager.size(), previousSize); // taskManager is unmodified
    }
}
