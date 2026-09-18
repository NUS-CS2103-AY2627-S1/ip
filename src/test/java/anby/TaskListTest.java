package anby;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import anby.task.Task;
import anby.task.Todo;

public class TaskListTest {
    @Test
    public void isEmpty_emptyList_returnsTrue() {
        TaskList tasks = new TaskList(new ArrayList<>());

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void isEmpty_nonEmptyList_returnsFalse() {
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(new Todo("eat burger"));

        assertFalse(tasks.isEmpty());
    }

    @Test
    public void getTask_validTaskNumber_returnsMatchingTask() throws AnbyException {
        TaskList tasks = new TaskList(new ArrayList<>());
        Task firstTask = new Todo("eat burger");
        Task secondTask = new Todo("drink tea");

        tasks.add(firstTask, secondTask);

        assertSame(secondTask, tasks.getTask("2"));
    }

    @Test
    public void getTask_zeroTaskNumber_throwsAnbyException() {
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(new Todo("eat burger"));

        assertThrows(AnbyException.class, () -> tasks.getTask("0"));
    }

    @Test
    public void getTask_taskNumberTooLarge_throwsAnbyException() {
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(new Todo("eat burger"));

        assertThrows(AnbyException.class, () -> tasks.getTask("2"));
    }

    @Test
    public void getTask_nonNumericTaskNumber_throwsAnbyException() {
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(new Todo("eat burger"));

        assertThrows(AnbyException.class, () -> tasks.getTask("one"));
    }

    @Test
    public void delete_validTaskNumber_removesAndReturnsTask() throws AnbyException {
        TaskList tasks = new TaskList(new ArrayList<>());
        Task firstTask = new Todo("eat burger");
        Task secondTask = new Todo("drink tea");

        tasks.add(firstTask, secondTask);
        Task deletedTask = tasks.delete("1");

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(0));
    }

    @Test
    public void mark_unmarkedTask_marksTaskAsDone() throws AnbyException {
        TaskList tasks = new TaskList(new ArrayList<>());
        Task task = new Todo("eat burger");

        tasks.add(task);
        tasks.mark("1");

        assertTrue(task.isDone());
    }

    @Test
    public void mark_alreadyMarkedTask_throwsAnbyException() throws AnbyException {
        TaskList tasks = new TaskList(new ArrayList<>());
        Task task = new Todo("eat burger");

        tasks.add(task);
        tasks.mark("1");

        assertThrows(AnbyException.class, () -> tasks.mark("1"));
    }

    @Test
    public void unmark_markedTask_marksTaskAsNotDone() throws AnbyException {
        TaskList tasks = new TaskList(new ArrayList<>());
        Task task = new Todo("eat burger");

        tasks.add(task);
        tasks.mark("1");
        tasks.unmark("1");

        assertFalse(task.isDone());
    }

    @Test
    public void unmark_unmarkedTask_throwsAnbyException() {
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(new Todo("eat burger"));

        assertThrows(AnbyException.class, () -> tasks.unmark("1"));
    }

    @Test
    public void find_matchingKeywordWithDifferentCase_returnsMatches() {
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(new Todo("Eat Burger"), new Todo("drink tea"), new Todo("buy burger sauce"));
        TaskList matchingTasks = tasks.find("burger");

        assertEquals(2, matchingTasks.size());
        assertEquals("Eat Burger", matchingTasks.get(0).getName());
        assertEquals("buy burger sauce", matchingTasks.get(1).getName());
    }

    @Test
    public void find_noMatchingKeyword_returnsEmptyList() {
        TaskList tasks = new TaskList(new ArrayList<>());

        tasks.add(new Todo("eat burger"));
        TaskList matchingTasks = tasks.find("pizza");

        assertTrue(matchingTasks.isEmpty());
    }
}
