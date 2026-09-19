package mrchatbot.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import mrchatbot.exception.MrChatbotException;

public class TaskListTest {
    @Test
    public void isFull_emptyList_false() {
        TaskList tasks = new TaskList();

        assertFalse(tasks.isFull(1));
    }

    @Test
    public void isFull_taskCountBelowLimit_false() {
        TaskList tasks = new TaskList(new Todo("read book"));

        assertFalse(tasks.isFull(2));
    }

    @Test
    public void isFull_taskCountAtLimit_true() {
        TaskList tasks = new TaskList(new Todo("read book"));

        assertTrue(tasks.isFull(1));
    }

    @Test
    public void isFull_taskCountAboveLimit_true() {
        TaskList tasks = new TaskList(new Todo("read book"), new Todo("return book"));

        assertTrue(tasks.isFull(1));
    }

    @Test
    public void mark_existingTask_taskMarkedAndReturned() throws MrChatbotException {
        Todo task = new Todo("read book");
        TaskList tasks = new TaskList(task);

        Task markedTask = tasks.mark(1);

        assertSame(task, markedTask);
        assertEquals("[T][X] read book", task.toString());
    }

    @Test
    public void unmark_existingDoneTask_taskUnmarkedAndReturned() throws MrChatbotException {
        Todo task = new Todo("read book");
        task.markAsDone();
        TaskList tasks = new TaskList(task);

        Task unmarkedTask = tasks.unmark(1);

        assertSame(task, unmarkedTask);
        assertEquals("[T][ ] read book", task.toString());
    }

    @Test
    public void delete_existingTask_taskRemovedAndReturned() throws MrChatbotException {
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("return book");
        TaskList tasks = new TaskList(firstTask, secondTask);

        Task deletedTask = tasks.delete(1);

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(0));
    }

    @Test
    public void mark_zeroTaskNumber_exceptionThrown() {
        TaskList tasks = new TaskList(new Todo("read book"));

        MrChatbotException exception = assertThrows(MrChatbotException.class, () -> tasks.mark(0));
        assertEquals("This task doesn't exist...", exception.getMessage());
    }

    @Test
    public void delete_taskNumberAboveSize_exceptionThrown() {
        TaskList tasks = new TaskList(new Todo("read book"));

        MrChatbotException exception = assertThrows(MrChatbotException.class, () -> tasks.delete(2));
        assertEquals("This task doesn't exist...", exception.getMessage());
    }

    @Test
    public void find_matchingKeyword_matchingTasksReturned() {
        Todo todo = new Todo("read book");
        Deadline deadline = new Deadline("return book", LocalDate.parse("2019-12-01"));
        Event event = new Event("project meeting", LocalDate.parse("2019-12-01"), LocalDate.parse("2019-12-02"));
        TaskList tasks = new TaskList(todo, deadline, event);

        ArrayList<Task> matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertSame(todo, matchingTasks.get(0));
        assertSame(deadline, matchingTasks.get(1));
    }

    @Test
    public void find_keywordWithDifferentCase_matchingTasksReturned() {
        Todo task = new Todo("Read Book");
        TaskList tasks = new TaskList(task);

        ArrayList<Task> matchingTasks = tasks.find("book");

        assertEquals(1, matchingTasks.size());
        assertSame(task, matchingTasks.get(0));
    }

    @Test
    public void find_noMatchingKeyword_emptyListReturned() {
        TaskList tasks = new TaskList(new Todo("read book"));

        ArrayList<Task> matchingTasks = tasks.find("meeting");

        assertTrue(matchingTasks.isEmpty());
    }
}
