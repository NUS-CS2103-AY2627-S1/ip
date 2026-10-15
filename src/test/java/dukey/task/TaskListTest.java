package dukey.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class TaskListTest {

    @Test
    public void size_emptyTaskList_returnsZero() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
    }

    @Test
    public void constructor_existingTaskList_keepsGivenTasks() {
        ArrayList<Task> existingTasks = new ArrayList<>();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        existingTasks.add(firstTask);
        existingTasks.add(secondTask);

        TaskList tasks = new TaskList(existingTasks);

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(1));
        assertSame(secondTask, tasks.get(2));
    }

    @Test
    public void add_oneTask_increasesSizeAndStoresTask() {
        TaskList tasks = new TaskList();
        Task task = new Todo("read book");

        tasks.add(task);

        assertEquals(1, tasks.size());
        assertSame(task, tasks.get(1));
    }

    @Test
    public void add_twoTasks_preservesInsertionOrder() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");

        tasks.add(firstTask);
        tasks.add(secondTask);

        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(1));
        assertSame(secondTask, tasks.get(2));
    }

    @Test
    public void get_validTaskNumber_returnsOneBasedTask() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        tasks.add(firstTask);
        tasks.add(secondTask);

        assertSame(firstTask, tasks.get(1));
        assertSame(secondTask, tasks.get(2));
    }

    @Test
    public void isValidTaskNumber_emptyTaskList_returnsFalse() {
        TaskList tasks = new TaskList();

        assertFalse(tasks.isValidTaskNumber(-1));
        assertFalse(tasks.isValidTaskNumber(0));
        assertFalse(tasks.isValidTaskNumber(1));
    }

    @Test
    public void isValidTaskNumber_taskListWithTwoTasks_checksBoundariesCorrectly() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        assertFalse(tasks.isValidTaskNumber(0));
        assertTrue(tasks.isValidTaskNumber(1));
        assertTrue(tasks.isValidTaskNumber(2));
        assertFalse(tasks.isValidTaskNumber(3));
    }

    @Test
    public void delete_firstTask_removesAndReturnsTask() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task deletedTask = tasks.delete(1);

        assertSame(firstTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(secondTask, tasks.get(1));
    }

    @Test
    public void delete_lastTask_removesAndReturnsTask() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        tasks.add(firstTask);
        tasks.add(secondTask);

        Task deletedTask = tasks.delete(2);

        assertSame(secondTask, deletedTask);
        assertEquals(1, tasks.size());
        assertSame(firstTask, tasks.get(1));
    }

    @Test
    public void delete_middleTask_preservesOrderOfRemainingTasks() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        Task thirdTask = new Todo("buy bread");
        tasks.add(firstTask);
        tasks.add(secondTask);
        tasks.add(thirdTask);

        Task deletedTask = tasks.delete(2);

        assertSame(secondTask, deletedTask);
        assertEquals(2, tasks.size());
        assertSame(firstTask, tasks.get(1));
        assertSame(thirdTask, tasks.get(2));
    }

    @Test
    public void find_keywordWithMatches_returnsMatchingTasksInOriginalOrder() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        Task thirdTask = new Todo("buy bread");
        tasks.add(firstTask);
        tasks.add(secondTask);
        tasks.add(thirdTask);

        TaskList matchingTasks = tasks.find("book");

        assertEquals(2, matchingTasks.size());
        assertSame(firstTask, matchingTasks.get(1));
        assertSame(secondTask, matchingTasks.get(2));
    }

    @Test
    public void find_keywordWithoutMatches_returnsEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("return book"));

        TaskList matchingTasks = tasks.find("bread");

        assertEquals(0, matchingTasks.size());
    }

    @Test
    public void countCompletedTasks_mixedTaskList_returnsDoneTaskCount() {
        TaskList tasks = new TaskList();
        Task firstTask = new Todo("read book");
        Task secondTask = new Todo("return book");
        firstTask.markAsDone(LocalDateTime.of(2026, 9, 16, 14, 30));
        tasks.add(firstTask);
        tasks.add(secondTask);

        assertEquals(1, tasks.countCompletedTasks());
        assertEquals(1, tasks.countPendingTasks());
    }

    @Test
    public void countCompletedInCalendarWeek_mixedCompletionDates_returnsCurrentWeekCount() {
        TaskList tasks = new TaskList();
        Task thisWeekTask = new Todo("read book");
        Task previousWeekTask = new Todo("return book");
        Task unknownCompletionTask = new Todo("buy bread");
        thisWeekTask.markAsDone(LocalDateTime.of(2026, 9, 16, 14, 30));
        previousWeekTask.markAsDone(LocalDateTime.of(2026, 9, 13, 23, 59));
        unknownCompletionTask.markAsDone();
        tasks.add(thisWeekTask);
        tasks.add(previousWeekTask);
        tasks.add(unknownCompletionTask);

        assertEquals(1, tasks.countCompletedInCalendarWeek(LocalDate.of(2026, 9, 14)));
    }

    @Test
    public void countCompletedInCalendarMonth_mixedCompletionDates_returnsCurrentMonthCount() {
        TaskList tasks = new TaskList();
        Task thisMonthTask = new Todo("read book");
        Task previousMonthTask = new Todo("return book");
        Task pendingTask = new Todo("buy bread");
        thisMonthTask.markAsDone(LocalDateTime.of(2026, 9, 1, 0, 0));
        previousMonthTask.markAsDone(LocalDateTime.of(2026, 8, 31, 23, 59));
        tasks.add(thisMonthTask);
        tasks.add(previousMonthTask);
        tasks.add(pendingTask);

        assertEquals(1, tasks.countCompletedInCalendarMonth(LocalDate.of(2026, 9, 30)));
    }
}
