package chim.ui;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import chim.task.Task;
import chim.task.Todo;

public class UiTest {

    private final Ui ui = new Ui();

    @Test
    public void getGoodbyeMessage_returnsNonEmptyMessage() {
        assertTrue(!ui.getGoodbyeMessage().isEmpty());
    }

    @Test
    public void getTaskListMessage_emptyList_showsEmptyMessage() {
        List<Task> tasks = new ArrayList<>();
        String message = ui.getTaskListMessage(tasks);
        assertTrue(message.toLowerCase().contains("empty"));
    }

    @Test
    public void getTaskListMessage_withTasks_listsEachTask() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("write essay"));

        String message = ui.getTaskListMessage(tasks);

        assertTrue(message.contains("read book"));
        assertTrue(message.contains("write essay"));
        assertTrue(message.contains("1."));
        assertTrue(message.contains("2."));
    }

    @Test
    public void getMatchingTasksMessage_emptyList_showsNoMatchMessage() {
        List<Task> tasks = new ArrayList<>();
        String message = ui.getMatchingTasksMessage(tasks);
        assertTrue(message.toLowerCase().contains("couldn't find") || message.toLowerCase().contains("find"));
    }

    @Test
    public void getMatchingTasksMessage_withMatches_listsThem() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));

        String message = ui.getMatchingTasksMessage(tasks);

        assertTrue(message.contains("read book"));
    }

    @Test
    public void getTaskAddedMessage_singularCount_usesSingularWord() {
        Task task = new Todo("read book");
        String message = ui.getTaskAddedMessage(task, 1);
        assertTrue(message.contains("1 task "));
    }

    @Test
    public void getTaskAddedMessage_pluralCount_usesPluralWord() {
        Task task = new Todo("read book");
        String message = ui.getTaskAddedMessage(task, 2);
        assertTrue(message.contains("2 tasks"));
    }

    @Test
    public void getTaskDeletedMessage_includesTaskAndCount() {
        Task task = new Todo("read book");
        String message = ui.getTaskDeletedMessage(task, 0);
        assertTrue(message.contains("read book"));
        assertTrue(message.contains("0"));
    }

    @Test
    public void getTaskMarkedMessage_includesTask() {
        Task task = new Todo("read book");
        String message = ui.getTaskMarkedMessage(task);
        assertTrue(message.contains("read book"));
    }

    @Test
    public void getTaskUnmarkedMessage_includesTask() {
        Task task = new Todo("read book");
        String message = ui.getTaskUnmarkedMessage(task);
        assertTrue(message.contains("read book"));
    }

    @Test
    public void getTaskPriorityMessage_includesTask() {
        Task task = new Todo("read book");
        task.setPriority("high");
        String message = ui.getTaskPriorityMessage(task);
        assertTrue(message.contains("read book"));
    }
}
