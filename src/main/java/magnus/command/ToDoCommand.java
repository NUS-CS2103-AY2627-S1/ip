package magnus.command;

import magnus.exception.CommandSyntaxException;
import magnus.exception.DuplicateTaskException;
import magnus.exception.MagnusException;
import magnus.task.TaskList;
import magnus.task.ToDoTask;

/**
 * Adds to-do tasks to a task list.
 */
public class ToDoCommand implements Command {
    private static final String USAGE_MESSAGE = "\tUsage: todo <task description>";

    private final TaskList tasks;

    /**
     * Creates a command that adds to-do tasks to the specified task list.
     *
     * @param tasks The task list to which to-do tasks are added.
     */
    public ToDoCommand(TaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a to-do task whose description is the first command argument.
     *
     * @param args The command arguments, with the task description at index 0.
     * @return A message describing the task that was added and the current task total.
     * @throws CommandSyntaxException If the task description is missing or blank.
     */
    @Override
    public String execute(String... args) throws MagnusException {
        if (args.length == 0 || args[0].isBlank()) {
            throw new CommandSyntaxException("\tThat move is incomplete - please give me the task description.\n"
                    + USAGE_MESSAGE);
        }

        if (args.length > 1) {
            throw new CommandSyntaxException("\tToo many pieces in that move - the todo command accepts one "
                    + "description.\n"
                    + USAGE_MESSAGE);
        }
        if (TaskArgumentsParser.containsFieldDelimiter(args[0])) {
            throw new CommandSyntaxException("\tThat move uses a reserved clock field - the todo command "
                    + "accepts only a description.\n" + USAGE_MESSAGE);
        }

        String taskDescription = args[0];
        ToDoTask newTask = new ToDoTask(taskDescription);
        if (tasks.containsTaskWithSameDetails(newTask)) {
            throw new DuplicateTaskException(
                    "\tThat piece is already on the board - an identical task already exists.");
        }
        tasks.addTask(newTask);

        return "\tOpening move complete - I've added this To-Do task:\n\n\t" + newTask
                + "\n\n" + TaskCountMessage.formatTotal(this.tasks.getTaskCount());
    }
}
