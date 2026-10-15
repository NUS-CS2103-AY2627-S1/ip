package bubu.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListReminderTest {
    private static final LocalDateTime CURRENT_TIME = LocalDateTime.of(2026, 9, 11, 12, 0);

    @Test
    void findIncompleteTasksDueBetween_returnsOnlyUpcomingIncompleteScheduledTasks() {
        Deadline upcomingDeadline = new Deadline("submit assignment", CURRENT_TIME.plusDays(1));
        Event upcomingEvent = new Event("team meeting", CURRENT_TIME.plusDays(2),
                CURRENT_TIME.plusDays(2).plusHours(1));
        Deadline completedDeadline = new Deadline("finished task", CURRENT_TIME.plusDays(2));
        completedDeadline.markAsDone();
        Deadline overdueDeadline = new Deadline("missed task", CURRENT_TIME.minusHours(1));
        Deadline laterDeadline = new Deadline("next week task", CURRENT_TIME.plusDays(4));
        ToDo todo = new ToDo("read notes");
        TaskList taskList = new TaskList(List.of(upcomingDeadline, upcomingEvent, completedDeadline,
                overdueDeadline, laterDeadline, todo));

        List<Task> reminders = taskList.findIncompleteTasksDueBetween(CURRENT_TIME, CURRENT_TIME.plusDays(3));

        assertEquals(List.of(upcomingDeadline, upcomingEvent), reminders);
    }
}
