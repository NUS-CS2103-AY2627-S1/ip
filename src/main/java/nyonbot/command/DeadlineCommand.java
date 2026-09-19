package nyonbot.command;

import java.time.LocalDateTime;
import java.util.HashMap;

import nyonbot.Logic.Result;
import nyonbot.Parser;
import nyonbot.model.Deadline;
import nyonbot.model.NyonException;
import nyonbot.model.Task;
import nyonbot.model.TaskList;

/**
 * Adds a deadline task to the task list.
 */
public class DeadlineCommand extends Command {
    private TaskList list;

    /**
     * Creates a deadline command with parsed arguments and a task list.
     *
     * @param arguments parsed command arguments.
     * @param list task list to add the task to.
     */
    public DeadlineCommand(HashMap<String, String> arguments, TaskList list) {
        super(arguments);
        this.list = list;
    }

    /** {@inheritDoc} */
    @Override
    public Result execute() throws NyonException {
        String description = arguments.get(DESCRIPTION_KEY);
        String deadlineValue = arguments.get("--by");
        if (description == null || description.isBlank()) {
            throw new NyonException("cannot omit the description");
        }
        if (deadlineValue == null) {
            throw new NyonException("use --by to specify the deadline");
        }
        if (deadlineValue.isBlank()) {
            throw new NyonException("cannot omit the deadline date");
        }

        LocalDateTime deadlineTime = Parser.parseDate(deadlineValue);
        if (deadlineTime == null) {
            throw new NyonException(
                    "couldn't read your date\nenter the time in the following format: dd/MM/yyyy HHmm");
        }

        Task deadline = new Deadline(description, deadlineTime);
        list.add(deadline);
        return new Result(String.format(
                "I've added this task: %n%s%nThere are %s tasks in your list",
                deadline, list.size()), false, true);
    }
}
