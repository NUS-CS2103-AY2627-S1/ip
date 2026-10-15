package luckynoslacky.luckycommand;

import luckynoslacky.ResponseContent;
import luckynoslacky.ResponseTone;
import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskMaster;

/**
 * Represents a request to delete a task.
 */
public class LuckyNoDeleteCommand extends LuckyNoCommand {
    private final int taskNumber;
    private final TaskMaster taskMaster;

    /**
     * Creates a task-deletion command bound to a task master.
     *
     * @param taskNumber one-based task number
     * @param taskMaster task master whose task should be deleted
     * @throws IllegalArgumentException if {@code taskMaster} is null
     */
    public LuckyNoDeleteCommand(int taskNumber, TaskMaster taskMaster) {
        this.taskMaster = requireTaskMaster(taskMaster);
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the selected task and returns the resulting user-facing reply.
     *
     * @return deletion response
     */
    @Override
    protected ResponseContent executeContent() {
        Task deletedTask = this.taskMaster.deleteTask(taskNumber);
        return LuckyNoTaskResponses.deleted(
                deletedTask, this.taskMaster.getTaskCount());
    }

    /**
     * Returns the success tone for the task-deletion response.
     *
     * @return success response tone
     */
    @Override
    protected ResponseTone responseTone() {
        return ResponseTone.SUCCESS;
    }

}
