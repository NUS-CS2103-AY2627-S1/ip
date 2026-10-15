package luckynoslacky.luckycommand;

import luckynoslacky.ResponseContent;
import luckynoslacky.ResponseTone;
import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskMaster;

/**
 * Represents either a mark or an unmark request.
 */
public class LuckyNoMarkCommand extends LuckyNoCommand {
    private final int taskNumber;
    private final boolean shouldMarkDone;
    private final TaskMaster taskMaster;

    /**
     * Creates a task-status command bound to a task master.
     *
     * @param taskNumber one-based task number
     * @param shouldMarkDone whether the task should be marked done
     * @param taskMaster task master containing the task
     * @throws IllegalArgumentException if {@code taskMaster} is null
     */
    public LuckyNoMarkCommand(
            int taskNumber, boolean shouldMarkDone, TaskMaster taskMaster) {
        this.taskMaster = requireTaskMaster(taskMaster);
        this.taskNumber = taskNumber;
        this.shouldMarkDone = shouldMarkDone;
    }

    /**
     * Applies the requested status and returns the resulting reply.
     *
     * @return mark or unmark response
     */
    @Override
    protected ResponseContent executeContent() {
        Task updatedTask = shouldMarkDone
                ? this.taskMaster.markTaskDone(taskNumber)
                : this.taskMaster.unmarkTaskUndone(taskNumber);

        return shouldMarkDone
                ? LuckyNoTaskResponses.marked(updatedTask)
                : LuckyNoTaskResponses.unmarked(updatedTask);
    }

    /**
     * Returns the success tone for the task-status response.
     *
     * @return success response tone
     */
    @Override
    protected ResponseTone responseTone() {
        return ResponseTone.SUCCESS;
    }

}
