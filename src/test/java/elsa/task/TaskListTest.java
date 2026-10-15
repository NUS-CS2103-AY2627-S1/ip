package elsa.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskList}, the collection the chatbot keeps its tasks in.
 *
 * <p>Several of its methods pass straight through to the ArrayList inside and are
 * not worth a test of their own. What is tested here is the behaviour that is the
 * list's rather than the ArrayList's: that deleting returns the task removed and
 * closes the gap it left, that marking changes the task in the list rather than a
 * copy of it, and that a list built from tasks read off the disk starts with them
 * already in it.
 */
public class TaskListTest {

    /**
     * The date the reminder tests count from. Fixed rather than read from the
     * clock, which getReminders allows by taking the date as a parameter, so every
     * boundary gives the same answer whatever day the tests are run.
     */
    private static final LocalDate TODAY = LocalDate.of(2026, 9, 13);

    /** How many days ahead the chatbot looks when reminding. */
    private static final int WEEK = 7;

    /** A list holding three todos, named so that positions can be told apart. */
    private static TaskList threeTasks() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(new Todo("third"));
        return tasks;
    }

    @Test
    public void newTaskList_noTasks_isEmpty() {
        TaskList tasks = new TaskList();
        assertTrue(tasks.isEmpty());
        assertEquals(0, tasks.size());
    }

    @Test
    public void add_oneTask_growsTheListAndKeepsTheTask() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("read book");
        tasks.add(todo);
        assertEquals(1, tasks.size());
        assertFalse(tasks.isEmpty());
        assertSame(todo, tasks.get(0));
    }

    @Test
    public void hasTask_listHoldingTheSameTask_returnsTrue() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertTrue(tasks.hasTask(new Todo("read book")));
    }

    @Test
    public void hasTask_listWithoutIt_returnsFalse() {
        assertFalse(threeTasks().hasTask(new Todo("fourth")));
    }

    @Test
    public void hasTask_emptyList_returnsFalse() {
        assertFalse(new TaskList().hasTask(new Todo("read book")));
    }

    /** Tasks are added to the end, so the list stays in the order they were typed. */
    @Test
    public void add_severalTasks_keepsThemInTheOrderAdded() {
        TaskList tasks = threeTasks();
        assertEquals("first", tasks.get(0).description);
        assertEquals("second", tasks.get(1).description);
        assertEquals("third", tasks.get(2).description);
    }

    /**
     * A list built from tasks read off the disk starts with them already in it,
     * which is how a saved list survives a restart.
     */
    @Test
    public void newTaskList_builtFromExistingTasks_holdsThem() {
        ArrayList<Task> loaded = new ArrayList<>();
        loaded.add(new Todo("read book"));
        TaskList tasks = new TaskList(loaded);
        assertEquals(1, tasks.size());
        assertEquals("[T][ ] read book", tasks.get(0).toString());
    }

    // ------------------------------------------------------------------
    // Deleting
    // ------------------------------------------------------------------

    /** The task removed is handed back, so the caller can show which one it was. */
    @Test
    public void get_positionPastTheEnd_assertionFails() {
        TaskList tasks = threeTasks();
        // Gradle runs the tests with -ea, so the assertion inside get() is live.
        // A position no task sits at is a mistake in the calling code rather than
        // something a user could type, which is why it is an assertion and not an
        // ElsaException: the parser and TaskNumberCommand have already refused
        // every number a user could get wrong before a position reaches here.
        assertThrows(AssertionError.class, () -> tasks.get(3));
    }

    @Test
    public void delete_negativePosition_assertionFails() {
        TaskList tasks = threeTasks();
        assertThrows(AssertionError.class, () -> tasks.delete(-1));
    }

    @Test
    public void delete_middleTask_returnsThatTask() {
        TaskList tasks = threeTasks();
        Task removed = tasks.delete(1);
        assertEquals("second", removed.description);
    }

    @Test
    public void delete_middleTask_closesTheGapItLeft() {
        TaskList tasks = threeTasks();
        tasks.delete(1);
        assertEquals(2, tasks.size());
        assertEquals("first", tasks.get(0).description);
        assertEquals("third", tasks.get(1).description);
    }

    @Test
    public void delete_theOnlyTask_leavesTheListEmpty() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.delete(0);
        assertTrue(tasks.isEmpty());
    }

    // ------------------------------------------------------------------
    // Marking
    // ------------------------------------------------------------------

    /**
     * Marking has to change the task that is in the list, not a copy of it, or the
     * change would be shown to the user and then not saved.
     */
    @Test
    public void mark_task_marksTheTaskInTheListAndReturnsIt() {
        TaskList tasks = threeTasks();
        Task marked = tasks.mark(1);
        assertEquals("X", marked.getStatusIcon());
        assertEquals("X", tasks.get(1).getStatusIcon());
        assertSame(marked, tasks.get(1));
    }

    @Test
    public void mark_oneTask_leavesTheOthersAlone() {
        TaskList tasks = threeTasks();
        tasks.mark(1);
        assertEquals(" ", tasks.get(0).getStatusIcon());
        assertEquals(" ", tasks.get(2).getStatusIcon());
    }

    @Test
    public void unmark_taskThatWasDone_marksItNotDoneInTheList() {
        TaskList tasks = threeTasks();
        tasks.mark(0);
        Task unmarked = tasks.unmark(0);
        assertEquals(" ", unmarked.getStatusIcon());
        assertEquals(" ", tasks.get(0).getStatusIcon());
    }

    // ------------------------------------------------------------------
    // Reminders
    // ------------------------------------------------------------------

    /** Returns an unfinished deadline due a number of days after TODAY; negative for a past one. */
    private static Deadline dueIn(String description, int days) {
        return new Deadline(description, TODAY.plusDays(days));
    }

    /** Returns the deadlines the reminders are about, in the order given. */
    private static List<Deadline> deadlinesOf(List<Reminder> reminders) {
        return reminders.stream().map(Reminder::deadline).toList();
    }

    @Test
    public void getReminders_deadlinesAroundTheWindow_keepsOverdueTodayAndLastDayOnly() {
        Deadline overdue = dueIn("overdue", -3);
        Deadline today = dueIn("today", 0);
        Deadline lastDay = dueIn("last day", WEEK);
        Deadline dayAfter = dueIn("the day after", WEEK + 1);
        TaskList tasks = new TaskList();
        tasks.add(overdue);
        tasks.add(today);
        tasks.add(lastDay);
        tasks.add(dayAfter);

        List<Reminder> reminders = tasks.getReminders(TODAY, WEEK);

        assertEquals(List.of(overdue, today, lastDay), deadlinesOf(reminders));
        assertEquals(List.of(-3L, 0L, 7L), reminders.stream().map(Reminder::daysUntilDue).toList());
    }

    @Test
    public void getReminders_doneDeadlines_areLeftOut() {
        Deadline donePast = dueIn("done and past", -2);
        donePast.markAsDone();
        Deadline doneToday = dueIn("done and due today", 0);
        doneToday.markAsDone();
        Deadline owed = dueIn("still owed", 1);
        TaskList tasks = new TaskList();
        tasks.add(donePast);
        tasks.add(doneToday);
        tasks.add(owed);

        assertEquals(List.of(owed), deadlinesOf(tasks.getReminders(TODAY, WEEK)));
    }

    @Test
    public void getReminders_todosAndEvents_areLeftOut() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Event("running now", TODAY.minusDays(1), TODAY.plusDays(1)));
        tasks.add(new Event("starting soon", TODAY.plusDays(2), TODAY.plusDays(3)));

        assertTrue(tasks.getReminders(TODAY, WEEK).isEmpty());
    }

    @Test
    public void getReminders_outOfOrder_sortsSoonestFirstAndKeepsListOrderForTies() {
        Deadline later = dueIn("later", 5);
        Deadline overdue = dueIn("overdue", -1);
        Deadline sameDayAsLater = dueIn("same day as later", 5);
        TaskList tasks = new TaskList();
        tasks.add(later);
        tasks.add(overdue);
        tasks.add(sameDayAsLater);

        assertEquals(List.of(overdue, later, sameDayAsLater),
                deadlinesOf(tasks.getReminders(TODAY, WEEK)));
    }

    /**
     * A reminder is shown with the task's number in the full list, which is what
     * "mark" and "delete" count. The deadline sits behind two todos here, so a
     * position counted among the reminders alone would wrongly be 0.
     */
    @Test
    public void getReminders_tasksBeforeTheDeadline_indexIsItsPositionInTheFullList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));
        tasks.add(dueIn("third", 1));

        assertEquals(2, tasks.getReminders(TODAY, WEEK).get(0).index());
    }

    @Test
    public void getReminders_emptyList_returnsNoReminders() {
        assertTrue(new TaskList().getReminders(TODAY, WEEK).isEmpty());
    }
}
