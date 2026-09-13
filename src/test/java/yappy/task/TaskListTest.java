package yappy.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class TaskListTest {
    @Test
    public void find_keywordWithDifferentCase_returnsMatchingDescriptionsOnly() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));
        tasks.add(new Todo("book flights"));
        tasks.add(new Todo("buy groceries"));

        TaskList matchingTasks = tasks.find("BOOK");

        assertEquals(2, matchingTasks.size());
        assertEquals("[T][ ] Read Book", matchingTasks.get(0).toString());
        assertEquals("[T][ ] book flights", matchingTasks.get(1).toString());
    }

    @Test
    public void find_missingKeyword_returnsEmptyTaskList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(0, tasks.find("report").size());
    }

    @Test
    public void getUpcomingDeadlines_excludesDonePastAndNonDeadlineTasks() {
        TaskList tasks = new TaskList();
        Deadline dueToday = new Deadline("pay library fine", LocalDate.of(2026, 9, 6));
        Deadline dueSoon = new Deadline("submit report", LocalDate.of(2026, 9, 10));
        Deadline completed = new Deadline("finished task", LocalDate.of(2026, 9, 8));
        completed.markAsDone();
        tasks.add(new Deadline("overdue task", LocalDate.of(2026, 9, 5)));
        tasks.add(dueToday);
        tasks.add(dueSoon);
        tasks.add(completed);
        tasks.add(new Todo("date-free task"));

        TaskList reminders = tasks.getUpcomingDeadlines(LocalDate.of(2026, 9, 6), 7);

        assertEquals(2, reminders.size());
        assertEquals(dueToday, reminders.get(0));
        assertEquals(dueSoon, reminders.get(1));
    }
}
