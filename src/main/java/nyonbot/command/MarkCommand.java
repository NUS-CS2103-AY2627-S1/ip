package nyonbot.command;

import java.util.HashMap;

import nyonbot.Logic.Result;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

/**
 * Marks a task as completed.
 */
public class MarkCommand extends Command {
    private TaskList list;

    /**
     * Creates a MarkCommand with parsed arguments and a TaskList to check.
     *
     * @param arguments parsed command arguments.
     * @param list TaskList to be checked.
     */
    public MarkCommand(HashMap<String, String> arguments, TaskList list) {
        super(arguments);
        this.list = list;
    }

    /** {@inheritDoc} */
    @Override
    public Result execute() throws NyonException {
        assert arguments != null;
        String taskName = arguments.getOrDefault(DESCRIPTION_KEY, "").strip();
        if (taskName.isBlank()) {
            throw new NyonException("cannot mark without a description");
        }
        try {
            int idx = Integer.parseInt(taskName) - 1;
            if (idx < 0 || idx >= list.size()) {
                throw new NyonException("index out of bounds...");
            }
            Task task = list.get(idx);
            task.completeTask();
            return new Result(String.format("Marked %s as completed", task), false, true);
        } catch (NumberFormatException e) {
            for (Task task : list) {
                if (task.isSameTask(taskName)) {
                    task.completeTask();
                    return new Result(String.format("Marked %s as completed", task), false, true);
                }
            }
            return new Result("couldn't find the task... did you spell it right?");
        }

    }
}
