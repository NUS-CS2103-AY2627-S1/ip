package nyonbot.command;

import java.util.HashMap;

import nyonbot.Logic.Result;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

/**
 * Deletes a task from the provided task list.
 */
public class DeleteCommand extends Command {
    private TaskList list;

    /**
     * Creates a delete command with parsed arguments and a TaskList.
     *
     * @param arguments parsed command arguments.
     * @param list the TaskList to delete from.
     */
    public DeleteCommand(HashMap<String, String> arguments, TaskList list) {
        super(arguments);
        this.list = list;
    }

    /** {@inheritDoc} */
    @Override
    public Result execute() throws NyonException {
        assert arguments != null;
        String taskName = arguments.getOrDefault(DESCRIPTION_KEY, "").strip();
        try {
            Integer.parseInt(taskName);
        } catch (NumberFormatException e) {
            return deleteTaskByName(taskName);
        }
        return deleteTaskByNumber(taskName);
    }

    private Result deleteTaskByName(String taskName) throws NyonException {
        Task toRemove = null;
        for (Task task : list) {
            if (task.isSameTask(taskName)) {
                toRemove = task;
                break;
            }
        }
        if (toRemove == null) {
            throw new NyonException("couldn't find the task. did you spell it right?");
        }
        list.remove(toRemove);
        return new Result(String.format("I've removed %s from your list", toRemove), false, true);
    }

    private Result deleteTaskByNumber(String taskName) throws NyonException {
        int taskNumber = Integer.parseInt(taskName) - 1;
        if (taskNumber < 0 || taskNumber >= list.size()) {
            throw new NyonException("index out of bounds");
        }
        Task removed = list.remove(taskNumber);
        return new Result(String.format("I've removed %s from your list", removed), false, true);
    }
}
