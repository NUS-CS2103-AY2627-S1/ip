package luckynoslacky.luckycommand;

import luckynoslacky.ResponseContent;
import luckynoslacky.ResponseTone;
import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskMaster;
import luckynoslacky.luckytask.TaskTimes;

/**
 * Represents a request to replace the time or times of a timed task.
 */
public class LuckyNoReschedCommand extends LuckyNoCommand {
    private final int taskNumber;
    private final TaskTimes newTimes;
    private final TaskMaster taskMaster;

    /**
     * Creates a task rescheduling command.
     *
     * @param taskNumber one-based task number
     * @param newTimes replacement task timing information
     * @param taskMaster task master containing the task
     * @throws IllegalArgumentException if an argument is null
     */
    public LuckyNoReschedCommand(
            int taskNumber, TaskTimes newTimes, TaskMaster taskMaster) {
        if (newTimes == null) {
            throw new IllegalArgumentException(
                    "Rescheduled task times cannot be null.");
        }
        this.taskNumber = taskNumber;
        this.newTimes = newTimes;
        this.taskMaster = requireTaskMaster(taskMaster);
    }

    /**
     * Applies the rescheduling and returns the updated task.
     *
     * @return rescheduling response
     */
    @Override
    protected ResponseContent executeContent() {
        Task updatedTask = taskMaster.rescheduleTask(
                taskNumber, newTimes);
        return LuckyNoTaskResponses.rescheduled(updatedTask);
    }

    /**
     * Returns the success tone for the rescheduling response.
     *
     * @return success response tone
     */
    @Override
    protected ResponseTone responseTone() {
        return ResponseTone.SUCCESS;
    }

}
