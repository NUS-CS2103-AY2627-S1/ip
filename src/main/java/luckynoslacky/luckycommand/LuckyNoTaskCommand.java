package luckynoslacky.luckycommand;

import luckynoslacky.ResponseContent;
import luckynoslacky.ResponseTone;
import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckytask.Task;
import luckynoslacky.luckytask.TaskMaster;

/**
 * Represents a command that creates a task.
 */
public class LuckyNoTaskCommand extends LuckyNoCommand {
    private final Task task;
    private final TaskMaster taskMaster;

    /**
     * Creates a task-creation command bound to a task master.
     *
     * @param task task to add
     * @param taskMaster task master that should receive the task
     * @throws IllegalArgumentException if either argument is null
     */
    public LuckyNoTaskCommand(Task task, TaskMaster taskMaster) {
        this.taskMaster = requireTaskMaster(taskMaster);
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        this.task = task;
    }

    /**
     * Adds the task and returns the resulting user-facing reply.
     *
     * @return task-added response
     */
    @Override
    protected ResponseContent executeContent() {
        this.taskMaster.addTask(task);
        return LuckyNoTaskResponses.added(
                task, this.taskMaster.getTaskCount());
    }

    /**
     * Returns the success tone for the task-creation response.
     *
     * @return success response tone
     */
    @Override
    protected ResponseTone responseTone() {
        return ResponseTone.SUCCESS;
    }

}
