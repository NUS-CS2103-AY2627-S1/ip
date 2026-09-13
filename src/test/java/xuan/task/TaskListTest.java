package xuan.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void findDeadlinesOnDate_matchingDeadline_returnsDeadline() {
        ArrayList<Task> tasks = new ArrayList<>();
        Deadline deadline = new Deadline(
                "submit assignment",
                LocalDate.parse("2026-08-30")
        );
        tasks.add(deadline);

        TaskList taskList = new TaskList(tasks);

        ArrayList<Deadline> result =
                taskList.findDeadlinesOnDate(LocalDate.parse("2026-08-30"));

        assertEquals(1, result.size());
        assertEquals(deadline, result.get(0));
    }

    @Test
    public void findDeadlinesOnDate_noMatchingDeadline_returnsEmptyList() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Deadline(
                "submit assignment",
                LocalDate.parse("2026-08-30")
        ));

        TaskList taskList = new TaskList(tasks);

        ArrayList<Deadline> result =
                taskList.findDeadlinesOnDate(LocalDate.parse("2026-09-01"));

        assertTrue(result.isEmpty());
    }

    @Test
    public void findDeadlinesOnDate_multipleTasks_returnsOnlyMatchingDeadlines() {
        ArrayList<Task> tasks = new ArrayList<>();

        Deadline firstDeadline = new Deadline(
                "submit assignment",
                LocalDate.parse("2026-08-30")
        );

        Deadline secondDeadline = new Deadline(
                "return book",
                LocalDate.parse("2026-08-30")
        );

        Deadline otherDeadline = new Deadline(
                "project meeting",
                LocalDate.parse("2026-09-01")
        );

        tasks.add(firstDeadline);
        tasks.add(new Todo("read book"));
        tasks.add(secondDeadline);
        tasks.add(otherDeadline);
        tasks.add(new Event("meeting", "2pm", "4pm"));

        TaskList taskList = new TaskList(tasks);

        ArrayList<Deadline> result =
                taskList.findDeadlinesOnDate(LocalDate.parse("2026-08-30"));

        assertEquals(2, result.size());
        assertEquals(firstDeadline, result.get(0));
        assertEquals(secondDeadline, result.get(1));
    }

    @Test
    public void findTasks_matchingKeyword_returnsMatchingTask() {
        ArrayList<Task> tasks = new ArrayList<>();
        Todo matchingTask = new Todo("read book");
        tasks.add(matchingTask);
        tasks.add(new Todo("finish homework"));

        TaskList taskList = new TaskList(tasks);

        ArrayList<Task> result = taskList.findTasks("book");

        assertEquals(1, result.size());
        assertEquals(matchingTask, result.get(0));
    }

    @Test
    public void findTasks_noMatchingKeyword_returnsEmptyList() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("finish homework"));

        TaskList taskList = new TaskList(tasks);

        ArrayList<Task> result = taskList.findTasks("meeting");

        assertTrue(result.isEmpty());
    }

    @Test
    public void findTasks_multipleMatchingTasks_returnsAllMatchingTasks() {
        ArrayList<Task> tasks = new ArrayList<>();

        Todo firstTask = new Todo("read book");
        Deadline secondTask = new Deadline(
                "return book",
                LocalDate.parse("2026-09-20"));

        tasks.add(firstTask);
        tasks.add(new Todo("finish homework"));
        tasks.add(secondTask);

        TaskList taskList = new TaskList(tasks);

        ArrayList<Task> result = taskList.findTasks("book");

        assertEquals(2, result.size());
        assertEquals(firstTask, result.get(0));
        assertEquals(secondTask, result.get(1));
    }


    @Test
    public void size_multipleTasks_returnsCorrectSize() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("finish homework"));

        TaskList taskList = new TaskList(tasks);

        assertEquals(2, taskList.size());
    }

    @Test
    public void get_validIndex_returnsCorrectTask() {
        ArrayList<Task> tasks = new ArrayList<>();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("finish homework");

        tasks.add(firstTask);
        tasks.add(secondTask);

        TaskList taskList = new TaskList(tasks);

        assertEquals(secondTask, taskList.get(1));
    }

    @Test
    public void add_task_increasesSizeAndStoresTask() {
        TaskList taskList = new TaskList(new ArrayList<>());
        Todo task = new Todo("read book");

        taskList.add(task);

        assertEquals(1, taskList.size());
        assertEquals(task, taskList.get(0));
    }

    @Test
    public void delete_validIndex_removesAndReturnsTask() {
        ArrayList<Task> tasks = new ArrayList<>();
        Todo firstTask = new Todo("read book");
        Todo secondTask = new Todo("finish homework");

        tasks.add(firstTask);
        tasks.add(secondTask);

        TaskList taskList = new TaskList(tasks);

        Task deletedTask = taskList.delete(0);

        assertEquals(firstTask, deletedTask);
        assertEquals(1, taskList.size());
        assertEquals(secondTask, taskList.get(0));
    }
}
