package swell.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import swell.exception.SwellException;

public class TaskListTest {
    @Test
    public void add_validTask_increasesTaskCount() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("read book"));

        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    @Test
    public void add_sameTodoTwice_increasesTaskCount() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        tasks.add(new Todo("read book"));

        assertEquals(2, tasks.size());
    }

    @Test
    public void markTask_validTaskNumber_marksTaskAsDone() throws SwellException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        Task markedTask = tasks.markTask(1);

        assertEquals("[T][X] read book", markedTask.toString());
        assertEquals("[T][X] read book", tasks.get(0).toString());
    }

    @Test
    public void markTask_alreadyDoneTask_throwsSwellException() throws SwellException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.markTask(1);

        assertThrows(SwellException.class, () -> tasks.markTask(1));
    }

    @Test
    public void unmarkTask_doneTask_marksTaskAsNotDone() throws SwellException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.markTask(1);

        Task unmarkedTask = tasks.unmarkTask(1);

        assertEquals("[T][ ] read book", unmarkedTask.toString());
    }

    @Test
    public void unmarkTask_notDoneTask_throwsSwellException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(SwellException.class, () -> tasks.unmarkTask(1));
    }

    @Test
    public void deleteTask_validTaskNumber_removesAndReturnsTask() throws SwellException {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("write notes");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task deletedTask = tasks.deleteTask(1);

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(0));
    }

    @Test
    public void markTask_taskNumberTooLarge_throwsSwellException() throws SwellException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertThrows(SwellException.class, () -> tasks.markTask(2));
    }

    @Test
    public void findTasksByTag_matchingTag_returnsTaggedTasks() {
        TaskList tasks = new TaskList();
        Task taggedTask = new Todo("email recruiter", new ArrayList<>(List.of("Acme", "followup")));
        Task untaggedTask = new Todo("read book");
        tasks.add(taggedTask);
        tasks.add(untaggedTask);

        TaskList matchingTasks = tasks.findTasksByTag("followup");

        assertEquals(1, matchingTasks.size());
        assertSame(taggedTask, matchingTasks.get(0));
    }
}
