package luckynoslacky.luckycommand;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;

import luckynoslacky.ResponseContent;
import luckynoslacky.ResponseTone;
import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckytask.DurationPeriod;
import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskMaster;

/**
 * Represents a request to extend or replace the ending time of a timed task.
 */
public class LuckyNoSnoozeCommand extends LuckyNoCommand {
    private final int taskNumber;
    private final DurationPeriod delayAmount;
    private final LocalDateTime targetEndTime;
    private final TaskMaster taskMaster;

    /**
     * Creates a one-hour snooze command.
     *
     * @param taskNumber one-based task number
     * @param taskMaster task master containing the task
     */
    public LuckyNoSnoozeCommand(int taskNumber, TaskMaster taskMaster) {
        this(taskNumber,
                new DurationPeriod(Period.ZERO, Duration.ofHours(1)),
                taskMaster);
    }

    /**
     * Creates a duration-based snooze command.
     *
     * @param taskNumber one-based task number
     * @param delayAmount duration by which to extend the ending time
     * @param taskMaster task master containing the task
     * @throws IllegalArgumentException if an argument is null
     */
    public LuckyNoSnoozeCommand(
            int taskNumber, DurationPeriod delayAmount, TaskMaster taskMaster) {
        if (delayAmount == null) {
            throw new IllegalArgumentException("Snooze amount cannot be null.");
        }
        this.taskNumber = taskNumber;
        this.delayAmount = delayAmount;
        this.targetEndTime = null;
        this.taskMaster = requireTaskMaster(taskMaster);
    }

    /**
     * Creates a snooze command with an explicit ending time.
     *
     * @param taskNumber one-based task number
     * @param targetEndTime replacement ending time
     * @param taskMaster task master containing the task
     * @throws IllegalArgumentException if an argument is null
     */
    public LuckyNoSnoozeCommand(
            int taskNumber, LocalDateTime targetEndTime, TaskMaster taskMaster) {
        if (targetEndTime == null) {
            throw new IllegalArgumentException("Snooze end time cannot be null.");
        }
        this.taskNumber = taskNumber;
        this.delayAmount = null;
        this.targetEndTime = targetEndTime;
        this.taskMaster = requireTaskMaster(taskMaster);
    }

    /**
     * Applies the snooze and returns the updated task.
     *
     * @return snooze response
     */
    @Override
    protected ResponseContent executeContent() {
        Task updatedTask = targetEndTime == null
                ? taskMaster.snoozeTaskBy(taskNumber, delayAmount)
                : taskMaster.snoozeTaskTo(taskNumber, targetEndTime);
        return LuckyNoTaskResponses.snoozed(updatedTask);
    }

    /**
     * Returns the success tone for the snooze response.
     *
     * @return success response tone
     */
    @Override
    protected ResponseTone responseTone() {
        return ResponseTone.SUCCESS;
    }

}
