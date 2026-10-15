package luckynoslacky.luckycommand;

import luckynoslacky.ResponseContent;
import luckynoslacky.ResponseTone;
import luckynoslacky.luckyresponse.LuckyNoTaskResponses;
import luckynoslacky.luckytask.TaskMaster;

/**
 * Represents a request to list all stored tasks.
 */
public class LuckyNoListCommand extends LuckyNoCommand {

    private final TaskMaster taskMaster;

    /**
     * Creates a list command bound to a task master.
     *
     * @param taskMaster task master whose tasks should be listed
     * @throws IllegalArgumentException if {@code taskMaster} is null
     */
    public LuckyNoListCommand(TaskMaster taskMaster) {
        this.taskMaster = requireTaskMaster(taskMaster);
    }

    /**
     * Returns the formatted list of tasks.
     *
     * @return task-list response
     */
    @Override
    protected ResponseContent executeContent() {
        return LuckyNoTaskResponses.listed(taskMaster.listTasks());
    }

    /**
     * Returns the information tone for the task-list response.
     *
     * @return information response tone
     */
    @Override
    protected ResponseTone responseTone() {
        return ResponseTone.INFO;
    }

}
