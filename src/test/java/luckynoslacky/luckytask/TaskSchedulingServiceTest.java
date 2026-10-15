package luckynoslacky.luckytask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

import org.junit.jupiter.api.Test;

/** Tests scheduling validation provided by {@link TaskSchedulingService}. */
class TaskSchedulingServiceTest {
    private static final LocalDateTime DEADLINE =
            LocalDateTime.of(2026, 12, 6, 23, 59);
    private static final LocalDateTime EVENT_START =
            LocalDateTime.of(2026, 8, 6, 14, 0);
    private static final LocalDateTime EVENT_END =
            LocalDateTime.of(2026, 8, 6, 16, 0);

    /** Verifies that a task list is required to create a scheduling service. */
    @Test
    void construct_nullTaskList_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new TaskSchedulingService(null));
    }

    /** Verifies that scheduling state mirrors the selected task. */
    @Test
    void getTaskSchedule_deadlineTask_returnsTypeAndTimes() {
        TaskList taskList = new TaskList();
        taskList.addTask(new DeadlineTask("return book", DEADLINE));
        TaskSchedulingService schedulingService = new TaskSchedulingService(taskList);

        TaskSchedule schedule = schedulingService.getTaskSchedule(1);

        assertEquals(Task.TaskType.DEADLINE, schedule.taskType());
        assertEquals(TaskTimes.makeDeadlineTimes(DEADLINE), schedule.taskTimes());
    }

    /** Verifies that a ToDo cannot be handled by scheduling commands. */
    @Test
    void validateTimedTask_todoTask_throwsTodoTaskReason() {
        TaskList taskList = new TaskList();
        taskList.addTask(new TodoTask("read book"));
        TaskSchedulingService schedulingService = new TaskSchedulingService(taskList);

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> schedulingService.validateTimedTask(1));

        assertEquals(TaskSchedulingException.Reason.TODO_TASK, exception.getReason());
    }

    /** Verifies that a deadline cannot be changed to before the current time. */
    @Test
    void validateSnoozeTo_pastDeadline_throwsPastDeadlineReason() {
        TaskList taskList = new TaskList();
        taskList.addTask(new DeadlineTask("return book", DEADLINE));
        TaskSchedulingService schedulingService = new TaskSchedulingService(taskList);

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> schedulingService.validateSnoozeTo(
                        1, DEADLINE.minusHours(1), DEADLINE));

        assertEquals(TaskSchedulingException.Reason.PAST_DEADLINE, exception.getReason());
    }

    /** Verifies that an event cannot be rescheduled to end before it starts. */
    @Test
    void createRescheduledTimes_endBeforeStart_throwsEndBeforeStartReason() {
        TaskList taskList = new TaskList();
        taskList.addTask(new EventTask("project meeting", EVENT_START, EVENT_END));
        TaskSchedulingService schedulingService = new TaskSchedulingService(taskList);

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> schedulingService.createRescheduledTimes(
                        1, EVENT_END, EVENT_START, EVENT_START));

        assertEquals(TaskSchedulingException.Reason.END_BEFORE_START, exception.getReason());
    }

    /** Verifies that unrepresentable duration additions report time overflow. */
    @Test
    void validateSnoozeBy_unrepresentableDuration_throwsTimeOverflowReason() {
        TaskList taskList = new TaskList();
        taskList.addTask(new DeadlineTask("return book", DEADLINE));
        TaskSchedulingService schedulingService = new TaskSchedulingService(taskList);
        DurationPeriod unrepresentableAmount = new DurationPeriod(
                Period.ofYears(Integer.MAX_VALUE), Duration.ZERO);

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> schedulingService.validateSnoozeBy(
                        1, unrepresentableAmount, DEADLINE));

        assertEquals(TaskSchedulingException.Reason.TIME_OVERFLOW, exception.getReason());
    }
}
