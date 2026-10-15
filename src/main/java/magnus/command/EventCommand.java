package magnus.command;

import magnus.exception.CommandSyntaxException;
import magnus.exception.DuplicateTaskException;
import magnus.exception.MagnusException;
import magnus.task.EventTask;
import magnus.task.TaskList;

/**
 * Adds event tasks to a task list.
 */
public class EventCommand implements Command {
    private static final String USAGE_MESSAGE =
            "\tUsage: event <task description> /from <dd/MM/yyyy HHmm> /to <dd/MM/yyyy HHmm>";

    private final TaskList tasks;

    /**
     * Creates a command that adds event tasks to the specified task list.
     *
     * @param tasks The task list to which event tasks are added.
     */
    public EventCommand(TaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds an event task using its description, start time, and end time from the command arguments.
     *
     * @param args The command arguments, containing the description, {@code /from} field,
     *             and {@code /to} field together at index 0.
     * @return A message describing the event task that was added and the current task total.
     * @throws CommandSyntaxException If the description, start time, or end time is missing.
     */
    @Override
    public String execute(String... args) throws MagnusException {
        if (args.length == 0 || args[0].isBlank()) {
            throw new CommandSyntaxException("\tThat move is incomplete - please give me the task description.\n"
                    + USAGE_MESSAGE);
        }

        if (args.length > 1) {
            throw new CommandSyntaxException("\tToo many clocks in that move - the event command requires "
                    + "exactly one /from field and one /to field.\n"
                    + USAGE_MESSAGE);
        }

        EventTask newTask;
        try {
            TaskArgumentsParser.EventArguments taskArguments =
                    TaskArgumentsParser.parseEvent(args[0]);
            newTask = new EventTask(
                    taskArguments.description(), taskArguments.start(), taskArguments.end());
        } catch (IllegalArgumentException exception) {
            throw new CommandSyntaxException("\tThe clock rejects that event - use valid start and end times "
                    + "in dd/MM/yyyy HHmm format, with exactly one /from followed by one /to and the start "
                    + "strictly before the end.\n" + USAGE_MESSAGE);
        }
        if (tasks.containsTaskWithSameDetails(newTask)) {
            throw new DuplicateTaskException(
                    "\tThat piece is already on the board - an identical task already exists.");
        }
        tasks.addTask(newTask);

        return "\tThe position is prepared - I've added this Event task:\n\n\t" + newTask
                + "\n\n" + TaskCountMessage.formatTotal(this.tasks.getTaskCount());
    }
}
