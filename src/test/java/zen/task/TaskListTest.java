package zen.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import zen.ZenException;

/**
 * Tests task-list filtering behaviour.
 */
public class TaskListTest {

    @Test
    public void findTasksByDescription_matchingKeyword_returnsMatchingTasksInPriorityOrder() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));
        tasks.addTask(new Deadline("return book", LocalDateTime.of(2026, 6, 6, 18, 0), Priority.HIGH));
        tasks.addTask(new Event("book club", LocalDateTime.of(2026, 6, 7, 18, 0),
                LocalDateTime.of(2026, 6, 7, 19, 0), Priority.MEDIUM));
        tasks.addTask(new Todo("write essay"));

        TaskList matchingTasks = tasks.findTasksByDescription("book");

        assertEquals(3, matchingTasks.size());
        assertEquals("1.[D][ ] return book (by: Jun 06 2026 18:00:00) (priority: high)\n"
                        + "2.[E][ ] book club (from: Jun 07 2026 18:00:00 to: Jun 07 2026 19:00:00) "
                        + "(priority: medium)\n3.[T][ ] read book (priority: none)",
                matchingTasks.toString());
    }

    @Test
    public void getAllTasksBasedOnDescription_noMatchOrDifferentCase_returnsEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read Book"));

        TaskList noKeywordMatch = tasks.findTasksByDescription("notes");
        TaskList differentCaseMatch = tasks.findTasksByDescription("book");

        assertTrue(noKeywordMatch.isEmpty());
        assertTrue(differentCaseMatch.isEmpty());
    }

    @Test
    public void addTask_mixedPriorities_keepsPriorityOrderAndStableTies() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first low", Priority.LOW));
        tasks.addTask(new Todo("first high", Priority.HIGH));
        tasks.addTask(new Todo("second high", Priority.HIGH));
        tasks.addTask(new Todo("no priority"));

        assertEquals("1.[T][ ] first high (priority: high)\n2.[T][ ] second high (priority: high)\n"
                + "3.[T][ ] first low (priority: low)\n4.[T][ ] no priority (priority: none)", tasks.toString());
    }

    @Test
    public void markTask_outOfRange_throwsHelpfulException() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("read book"));

        ZenException exception = assertThrows(ZenException.class, () -> tasks.markTask(2));

        assertEquals("Choose a task number from 1 to 1.", exception.getMessage());
    }

    // AI-assisted
    @Test
    public void deleteTask_validTaskNumber_removesAndReturnsTask() throws ZenException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first task"));
        tasks.addTask(new Todo("second task"));

        Task deletedTask = tasks.deleteTask(1);

        assertEquals("[T][ ] first task (priority: none)", deletedTask.toString());
        assertEquals(1, tasks.size());
        assertEquals("1.[T][ ] second task (priority: none)", tasks.toString());
    }

    // AI-assisted
    @Test
    public void deleteTask_emptyList_throwsHelpfulException() {
        TaskList tasks = new TaskList();

        ZenException exception = assertThrows(ZenException.class, () -> tasks.deleteTask(1));

        assertEquals("The task list is empty. Add a task first.", exception.getMessage());
    }

    // AI-assisted
    @Test
    public void deleteTask_zeroOrOutOfRange_throwsHelpfulException() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("only task"));

        ZenException zeroException = assertThrows(ZenException.class, () -> tasks.deleteTask(0));
        ZenException outOfRangeException = assertThrows(ZenException.class, () -> tasks.deleteTask(2));

        assertEquals("Choose a task number from 1 to 1.", zeroException.getMessage());
        assertEquals("Choose a task number from 1 to 1.", outOfRangeException.getMessage());
    }

    // AI-assisted
    @Test
    public void markTaskAndUnmarkTask_validTaskNumber_updatesCompletionStatus() throws ZenException {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("submit form"));

        Task completedTask = tasks.markTask(1);
        assertEquals("[T][X] submit form (priority: none)", completedTask.toString());

        Task incompleteTask = tasks.unmarkTask(1);

        assertEquals("[T][ ] submit form (priority: none)", incompleteTask.toString());
    }

    // AI-assisted
    @Test
    public void markTaskAndUnmarkTask_emptyList_throwHelpfulException() {
        TaskList tasks = new TaskList();

        ZenException markException = assertThrows(ZenException.class, () -> tasks.markTask(1));
        ZenException unmarkException = assertThrows(ZenException.class, () -> tasks.unmarkTask(1));

        assertEquals("The task list is empty. Add a task first.", markException.getMessage());
        assertEquals("The task list is empty. Add a task first.", unmarkException.getMessage());
    }

    // AI-assisted
    @Test
    public void getAllTasksOnDate_matchingDeadlineAndEvent_returnsOnlyDatedTasks() {
        TaskList tasks = new TaskList();
        LocalDate date = LocalDate.of(2026, 10, 10);
        tasks.addTask(new Todo("undated task", Priority.HIGH));
        tasks.addTask(new Deadline("due today", date.atTime(18, 0), Priority.HIGH));
        tasks.addTask(new Event("multi-day event", date.minusDays(1).atTime(9, 0),
                date.plusDays(1).atTime(17, 0), Priority.MEDIUM));
        tasks.addTask(new Deadline("due tomorrow", date.plusDays(1).atTime(9, 0), Priority.LOW));

        TaskList tasksOnDate = tasks.getAllTasksOnDate(date);

        assertEquals(2, tasksOnDate.size());
        assertEquals("1.[D][ ] due today (by: Oct 10 2026 18:00:00) (priority: high)\n"
                        + "2.[E][ ] multi-day event (from: Oct 09 2026 09:00:00 to: Oct 11 2026 17:00:00) "
                        + "(priority: medium)",
                tasksOnDate.toString());
        assertFalse(tasksOnDate.toString().contains("undated task"));
    }

    // AI-assisted
    @Test
    public void toStorageString_emptyList_returnsEmptyString() {
        TaskList tasks = new TaskList();

        assertEquals("", tasks.toStorageString());
    }

    // AI-assisted
    @Test
    public void toStorageString_multipleTasks_returnsNewlineSeparatedRecords() {
        TaskList tasks = new TaskList();
        tasks.addTask(new Todo("first task", Priority.HIGH));
        tasks.addTask(new Todo("second task", Priority.LOW));

        assertEquals("T | 0 | first task | high\nT | 0 | second task | low", tasks.toStorageString());
    }
}
