package luckynoslacky.luckytask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import luckynoslacky.luckyexception.LuckyNoStorageException;
import luckynoslacky.luckystorage.CsvSaver;

/** Tests snoozing and rescheduling behavior provided by {@link TaskMaster}. */
class TaskMasterSchedulingTest {
    private static final LocalDateTime DEADLINE =
            LocalDateTime.of(2026, 12, 6, 23, 59);
    private static final LocalDateTime EVENT_START =
            LocalDateTime.of(2026, 8, 6, 14, 0);
    private static final LocalDateTime EVENT_END =
            LocalDateTime.of(2026, 8, 6, 16, 0);

    @TempDir
    Path temporaryDirectory;

    /** Verifies that a deadline snooze extends its due time. */
    @Test
    void snoozeTaskBy_deadlineDuration_updatesDeadline() {
        TaskMaster taskMaster = createTaskMaster();
        DeadlineTask task = new DeadlineTask("return book", DEADLINE);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        taskMaster.snoozeTaskBy(1, new DurationPeriod(Period.ZERO, Duration.ofHours(2)));

        assertEquals(DEADLINE.plusHours(2), task.getByTime());
    }

    /** Verifies that an event snooze extends only its end time. */
    @Test
    void snoozeTaskBy_eventDuration_updatesOnlyEventEnd() {
        TaskMaster taskMaster = createTaskMaster();
        EventTask task = new EventTask("project meeting", EVENT_START, EVENT_END);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        taskMaster.snoozeTaskBy(1, new DurationPeriod(Period.ZERO, Duration.ofHours(2)));

        assertEquals(EVENT_START, task.getStartTime());
        assertEquals(EVENT_END.plusHours(2), task.getEndTime());
    }

    /** Verifies that an explicit event snooze changes only its end time. */
    @Test
    void snoozeTaskTo_eventEndTime_preservesEventStart() {
        TaskMaster taskMaster = createTaskMaster();
        EventTask task = new EventTask("project meeting", EVENT_START, EVENT_END);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));
        LocalDateTime newEnd = LocalDateTime.of(2026, 8, 6, 18, 0);

        taskMaster.snoozeTaskTo(1, newEnd);

        assertEquals(EVENT_START, task.getStartTime());
        assertEquals(newEnd, task.getEndTime());
    }

    /** Verifies that an explicit deadline snooze replaces its deadline. */
    @Test
    void snoozeTaskTo_deadlineEndTime_updatesDeadline() {
        TaskMaster taskMaster = createTaskMaster();
        DeadlineTask task = new DeadlineTask("return book", DEADLINE);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));
        LocalDateTime newDeadline = LocalDateTime.of(2026, 12, 7, 10, 0);

        taskMaster.snoozeTaskTo(1, newDeadline);

        assertEquals(newDeadline, task.getEndTime());
    }

    /** Verifies that a combined snooze is persisted with task status. */
    @Test
    void snoozeTaskBy_combinedDuration_persistsUpdatedTimeAndStatus() {
        Path dataFile = temporaryDirectory.resolve("combined-snooze.csv");
        CsvSaver saver = new CsvSaver(dataFile);
        TaskMaster taskMaster = new TaskMaster(100, saver);
        EventTask task = new EventTask(
                "project meeting",
                LocalDateTime.of(2028, 1, 31, 14, 0),
                LocalDateTime.of(2028, 1, 31, 16, 0));
        task.markAsDone();
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        taskMaster.snoozeTaskBy(1,
                new DurationPeriod(Period.ofMonths(1), Duration.ofHours(12)));

        EventTask reloadedTask = (EventTask) new CsvSaver(dataFile).load().get(0);
        assertEquals(LocalDateTime.of(2028, 1, 31, 14, 0), reloadedTask.getStartTime());
        assertEquals(LocalDateTime.of(2028, 3, 1, 4, 0), reloadedTask.getEndTime());
        assertTrue(reloadedTask.isDone());
    }

    /** Verifies that explicit rescheduling replaces a deadline. */
    @Test
    void rescheduleTask_newDeadlineTimes_replacesDeadline() {
        TaskMaster taskMaster = createTaskMaster();
        DeadlineTask task = new DeadlineTask("return book", DEADLINE);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));
        LocalDateTime newDeadline = LocalDateTime.of(2026, 12, 7, 10, 0);

        taskMaster.rescheduleTask(
                1, TaskTimes.makeDeadlineTimes(newDeadline));

        assertEquals(newDeadline, task.getByTime());
    }

    /** Verifies that explicit event rescheduling replaces both event times. */
    @Test
    void rescheduleTask_validEventTimes_replacesBothEventTimes() {
        TaskMaster taskMaster = createTaskMaster();
        EventTask task = new EventTask("project meeting", EVENT_START, EVENT_END);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));
        LocalDateTime newStart = LocalDateTime.of(2026, 8, 7, 10, 0);
        LocalDateTime newEnd = LocalDateTime.of(2026, 8, 7, 11, 0);

        taskMaster.rescheduleTask(
                1, TaskTimes.makeEventTimes(newStart, newEnd));

        assertEquals(newStart, task.getStartTime());
        assertEquals(newEnd, task.getEndTime());
    }

    /** Verifies that a ToDo cannot be snoozed or rescheduled. */
    @Test
    void snoozeOrReschedule_todoTask_throwsIllegalArgumentException() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.loadTasksFromCsvStorageRecord(List.of(new TodoTask("read book")));

        assertThrows(IllegalArgumentException.class, () ->
                taskMaster.snoozeTaskBy(1, new DurationPeriod(Period.ZERO, Duration.ofHours(1))));
        assertThrows(IllegalArgumentException.class, () -> taskMaster.rescheduleTask(
                1, TaskTimes.makeDeadlineTimes(DEADLINE)));
    }

    /** Verifies scheduling validation identifies a ToDo as an unsupported timed task. */
    @Test
    void validateTimedTask_todoTask_throwsTodoTaskReason() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.loadTasksFromCsvStorageRecord(List.of(new TodoTask("read book")));

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> taskMaster.validateTimedTask(1));

        assertEquals(TaskSchedulingException.Reason.TODO_TASK, exception.getReason());
    }

    /** Verifies scheduling validation rejects a deadline set before the current time. */
    @Test
    void validateSnoozeTo_pastDeadline_throwsPastDeadlineReason() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.loadTasksFromCsvStorageRecord(
                List.of(new DeadlineTask("return book", DEADLINE)));

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> taskMaster.validateSnoozeTo(
                        1, DEADLINE.minusHours(1), DEADLINE));

        assertEquals(TaskSchedulingException.Reason.PAST_DEADLINE, exception.getReason());
    }

    /** Verifies scheduling validation rejects an event ending before its start. */
    @Test
    void createRescheduledTimes_endBeforeStart_throwsEndBeforeStartReason() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.loadTasksFromCsvStorageRecord(
                List.of(new EventTask("project meeting", EVENT_START, EVENT_END)));

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> taskMaster.createRescheduledTimes(
                        1, EVENT_END, EVENT_START, EVENT_START));

        assertEquals(TaskSchedulingException.Reason.END_BEFORE_START, exception.getReason());
    }

    /** Verifies scheduling validation reports durations outside the date-time range. */
    @Test
    void validateSnoozeBy_unrepresentableDuration_throwsTimeOverflowReason() {
        TaskMaster taskMaster = createTaskMaster();
        taskMaster.loadTasksFromCsvStorageRecord(
                List.of(new DeadlineTask("return book", DEADLINE)));
        DurationPeriod unrepresentableAmount = new DurationPeriod(
                Period.ofYears(Integer.MAX_VALUE), Duration.ZERO);

        TaskSchedulingException exception = assertThrows(
                TaskSchedulingException.class, () -> taskMaster.validateSnoozeBy(
                        1, unrepresentableAmount, DEADLINE));

        assertEquals(TaskSchedulingException.Reason.TIME_OVERFLOW, exception.getReason());
    }

    /** Verifies that a failed snooze save restores the original event times. */
    @Test
    void snoozeTaskBy_saveFailure_restoresOriginalEventTimes() {
        TaskMaster taskMaster = new TaskMaster(100, new FailingSaver());
        EventTask task = new EventTask("project meeting", EVENT_START, EVENT_END);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () ->
                taskMaster.snoozeTaskBy(1, new DurationPeriod(Period.ZERO, Duration.ofHours(2))));

        assertEquals("simulated save failure", exception.getMessage());
        assertEquals(EVENT_START, task.getStartTime());
        assertEquals(EVENT_END, task.getEndTime());
    }

    /** Verifies that a failed explicit snooze restores the original end time. */
    @Test
    void snoozeTaskTo_saveFailure_restoresOriginalEventEnd() {
        TaskMaster taskMaster = new TaskMaster(100, new FailingSaver());
        EventTask task = new EventTask("project meeting", EVENT_START, EVENT_END);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));
        LocalDateTime newEnd = LocalDateTime.of(2026, 8, 6, 18, 0);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () ->
                taskMaster.snoozeTaskTo(1, newEnd));

        assertEquals("simulated save failure", exception.getMessage());
        assertEquals(EVENT_START, task.getStartTime());
        assertEquals(EVENT_END, task.getEndTime());
    }

    /** Verifies that a failed event rescheduling save restores both times. */
    @Test
    void rescheduleTask_saveFailure_restoresOriginalEventTimes() {
        TaskMaster taskMaster = new TaskMaster(100, new FailingSaver());
        EventTask task = new EventTask("project meeting", EVENT_START, EVENT_END);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));
        LocalDateTime newStart = LocalDateTime.of(2026, 8, 7, 10, 0);
        LocalDateTime newEnd = LocalDateTime.of(2026, 8, 7, 11, 0);

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () ->
                taskMaster.rescheduleTask(
                        1, TaskTimes.makeEventTimes(newStart, newEnd)));

        assertEquals("simulated save failure", exception.getMessage());
        assertEquals(EVENT_START, task.getStartTime());
        assertEquals(EVENT_END, task.getEndTime());
    }

    /** Verifies that failed schedule mutations preserve persisted task data. */
    @Test
    void scheduleMutation_saveFailure_preservesExistingCsvRecords() {
        Path dataFile = temporaryDirectory.resolve("schedule-rollback.csv");
        CsvSaver workingSaver = new CsvSaver(dataFile);
        TaskMaster initialTaskMaster = new TaskMaster(100, workingSaver);
        initialTaskMaster.addTask(new EventTask(
                "project meeting", EVENT_START, EVENT_END));
        List<TaskView> recordsBefore = readTaskRecords(dataFile);

        TaskMaster taskMaster = new TaskMaster(
                100, new FailingSaver(dataFile));
        EventTask task = new EventTask("project meeting", EVENT_START, EVENT_END);
        taskMaster.loadTasksFromCsvStorageRecord(List.of(task));

        LuckyNoStorageException exception = assertThrows(
                LuckyNoStorageException.class, () -> taskMaster.snoozeTaskBy(
                        1, new DurationPeriod(Period.ZERO, Duration.ofHours(2))));
        assertEquals("simulated save failure", exception.getMessage());
        exception = assertThrows(
                LuckyNoStorageException.class, () -> taskMaster.snoozeTaskTo(
                        1, LocalDateTime.of(2026, 8, 6, 18, 0)));
        assertEquals("simulated save failure", exception.getMessage());
        exception = assertThrows(
                LuckyNoStorageException.class, () -> taskMaster.rescheduleTask(
                        1, TaskTimes.makeEventTimes(
                                LocalDateTime.of(2026, 8, 7, 10, 0),
                                LocalDateTime.of(2026, 8, 7, 11, 0))));
        assertEquals("simulated save failure", exception.getMessage());

        assertEquals(recordsBefore, readTaskRecords(dataFile));
        assertEquals(EVENT_START, task.getStartTime());
        assertEquals(EVENT_END, task.getEndTime());
    }

    /** Creates a task master with a temporary backing file. */
    private TaskMaster createTaskMaster() {
        return new TaskMaster(new CsvSaver(temporaryDirectory.resolve("tasks.csv")));
    }

    /** Reads task state from a persistence file without using display text. */
    private List<TaskView> readTaskRecords(Path dataFile) {
        return new CsvSaver(dataFile).load().stream()
                .map(TaskView::fromTask)
                .toList();
    }

    /** A saver that fails every save attempt for rollback tests. */
    private static class FailingSaver extends CsvSaver {
        FailingSaver() {
            this(Path.of("unused.csv"));
        }

        FailingSaver(Path dataFile) {
            super(dataFile);
        }

        /** Always throws to simulate a persistence failure. */
        @Override
        public void save(TaskList taskList) {
            throw new LuckyNoStorageException("simulated save failure");
        }
    }
}
