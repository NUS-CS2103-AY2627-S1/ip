package larper.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import larper.exception.EmptyDeletionException;
import larper.exception.InvalidNumberDeleteException;
import larper.exception.NoFindException;
import larper.exception.TaskAlreadyMarkedException;
import larper.exception.TaskAlreadyUnmarkedException;

public class TaskListTest {
    @Test
    public void addTask_emptyList_taskStoredAndCountUpdated() {
        TaskList taskList = new TaskList();
        Task task = new Todo("read book");

        taskList.addTask(task);

        assertEquals(1, taskList.size());
        assertFalse(taskList.isEmpty());
        assertTrue(taskList.hasTaskNumber(1));
        assertSame(task, taskList.getTask(1));
        assertFalse(taskList.hasTaskNumber(0));
        assertFalse(taskList.hasTaskNumber(2));
    }

    @Test
    public void constructor_existingTasks_memoryRetainedAndCountSet() {
        ArrayList<Task> tasks = new ArrayList<>();
        Todo firstTask = new Todo("first");
        Deadline secondTask = new Deadline("second", "2026-08-23", "no time");
        tasks.add(firstTask);
        tasks.add(secondTask);

        TaskList taskList = new TaskList(tasks);

        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.getTask(1));
        assertSame(secondTask, taskList.getTask(2));
        assertEquals(tasks, taskList.getTasks());
        assertNotSame(tasks, taskList.getTasks());

        tasks.clear();
        assertEquals(2, taskList.size());
    }

    @Test
    public void deleteTask_middleTask_taskRemovedAndRemainingOrderRetained() throws Exception {
        TaskList taskList = new TaskList();
        Task firstTask = new Todo("alpha");
        Task secondTask = new Todo("beta");
        Task thirdTask = new Todo("gamma");
        taskList.addTask(firstTask);
        taskList.addTask(secondTask);
        taskList.addTask(thirdTask);

        Task removedTask = taskList.deleteTask(2);

        assertSame(secondTask, removedTask);
        assertEquals(2, taskList.size());
        assertSame(firstTask, taskList.getTask(1));
        assertSame(thirdTask, taskList.getTask(2));
        assertTrue(taskList.hasTaskNumber(2));
        assertFalse(taskList.hasTaskNumber(3));
    }

    @Test
    public void deleteTask_lastRemainingTask_listBecomesEmpty() throws Exception {
        TaskList taskList = new TaskList();
        Task task = new Todo("solo");
        taskList.addTask(task);

        Task removedTask = taskList.deleteTask(1);

        assertSame(task, removedTask);
        assertEquals(0, taskList.size());
        assertTrue(taskList.isEmpty());
        assertFalse(taskList.hasTaskNumber(1));
    }

    @Test
    public void deleteTask_emptyOrInvalidNumber_expectedExceptions() throws Exception {
        TaskList taskList = new TaskList();
        assertThrows(EmptyDeletionException.class, () -> taskList.deleteTask(1));

        taskList.addTask(new Todo("alpha"));
        assertThrows(InvalidNumberDeleteException.class, () -> taskList.deleteTask(0));
        assertThrows(InvalidNumberDeleteException.class, () -> taskList.deleteTask(2));
    }

    @Test
    public void findTasks_fullPhraseCaseInsensitive_matchingTasksReturned() throws Exception {
        TaskList taskList = new TaskList();
        Task firstTask = new Todo("Read Book");
        Task secondTask = new Deadline("return library book", "2026-08-23", "no time");
        Task thirdTask = new Event("project meeting", "2026-08-24", "1400", "2026-08-25", "1600");
        taskList.addTask(firstTask);
        taskList.addTask(secondTask);
        taskList.addTask(thirdTask);

        ArrayList<FindResult> bookResults = taskList.findTasks("BOOK");
        ArrayList<FindResult> phraseResults = taskList.findTasks("return library");

        assertEquals(2, bookResults.size());
        assertEquals(1, bookResults.get(0).getTaskNumber());
        assertSame(firstTask, bookResults.get(0).getTask());
        assertEquals(2, bookResults.get(1).getTaskNumber());
        assertSame(secondTask, bookResults.get(1).getTask());
        assertEquals(1, phraseResults.size());
        assertEquals(2, phraseResults.get(0).getTaskNumber());
        assertSame(secondTask, phraseResults.get(0).getTask());
    }

    @Test
    public void findTasks_dateOnlyOrNoMatch_exceptionThrown() {
        TaskList taskList = new TaskList();
        taskList.addTask(new Deadline("return book", "2026-08-23", "no time"));

        assertThrows(NoFindException.class, () -> taskList.findTasks("Aug"));
        assertThrows(NoFindException.class, () -> taskList.findTasks("movie"));
        assertThrows(NoFindException.class, () -> taskList.findTasks(" "));
    }

    @Test
    public void findTasks_exactTagMatch_caseInsensitiveTagReturned() throws Exception {
        TaskList taskList = new TaskList();
        Task taggedTask = new Todo("prepare slides");
        taggedTask.addTag("#School");
        Task similarTagTask = new Todo("buy groceries");
        similarTagTask.addTag("#schoolhouse");
        taskList.addTask(taggedTask);
        taskList.addTask(similarTagTask);

        ArrayList<FindResult> results = taskList.findTasks("SCHOOL");

        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getTaskNumber());
        assertSame(taggedTask, results.get(0).getTask());
    }

    @Test
    public void tagAndUntagTask_completedTask_tagsUpdated() throws Exception {
        TaskList taskList = new TaskList();
        Task task = new Todo("prepare slides");
        taskList.addTask(task);
        taskList.markTask(1);

        taskList.tagTask(1, java.util.List.of("school", "#urgent"));
        taskList.untagTask(1, java.util.List.of("#school"));

        assertTrue(task.isDone());
        assertEquals("[urgent]", task.getTags().toString());
        assertEquals("[T][X] prepare slides [#urgent]", task.toString());
    }

    @Test
    public void findTasksByTag_exactCaseInsensitiveMatch_returnsOriginalNumbers() throws Exception {
        TaskList taskList = new TaskList();
        Task taggedTask = new Todo("prepare slides");
        taggedTask.addTag("#School");
        Task similarTagTask = new Todo("buy groceries");
        similarTagTask.addTag("#schoolhouse");
        taskList.addTask(taggedTask);
        taskList.addTask(similarTagTask);

        ArrayList<FindResult> results = taskList.findTasksByTag("school");

        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getTaskNumber());
        assertSame(taggedTask, results.get(0).getTask());
    }

    @Test
    public void markAndUnmarkTask_validTask_statusUpdated() throws Exception {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("read book"));

        Task markedTask = taskList.markTask(1);
        assertTrue(markedTask.isDone());
        assertEquals("[T][X] read book", markedTask.toString());

        Task unmarkedTask = taskList.unmarkTask(1);
        assertFalse(unmarkedTask.isDone());
        assertEquals("[T][ ] read book", unmarkedTask.toString());
    }

    @Test
    public void markAndUnmarkTask_repeatedStatusChange_expectedExceptions() throws Exception {
        TaskList taskList = new TaskList();
        taskList.addTask(new Todo("read book"));

        assertThrows(TaskAlreadyUnmarkedException.class, () -> taskList.unmarkTask(1));

        taskList.markTask(1);
        assertThrows(TaskAlreadyMarkedException.class, () -> taskList.markTask(1));
    }
}
