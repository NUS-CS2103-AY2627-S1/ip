package jasper.command;

import jasper.JasperException;
import jasper.task.Task;
import jasper.task.TaskList;
import jasper.task.Todo;

/**
 * Represents a command to add a todo task.
 */
public class TodoCommand implements Command {
    /** Valid command format message */
    private static final String USAGE_MSG = "Usage: todo <task>";
    /** Todo task to be added */
    private final Task task;

    /**
     * Constructs a TodoCommand by parsing the task description.
     *
     * @param arg The argument string containing the task description.
     * @throws JasperException If the argument string is empty.
     */
    public TodoCommand(String arg) throws JasperException {
        if (arg.isEmpty()) {
            throw new JasperException(USAGE_MSG);
        }
        task = new Todo(arg);
    }

    @Override
    public CommandResult execute(TaskList tasks) throws JasperException {
        tasks.add(task);
        return new CommandResult(CommandType.TODO, "Aye, aye. I've added this task:\n  " + task);
    }
}
