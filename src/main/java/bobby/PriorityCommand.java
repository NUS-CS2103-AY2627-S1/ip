package bobby;

/** Assigns a priority to a selected task and persists the change. */
public class PriorityCommand extends Command {
    /** Number of arguments expected after the command name. */
    private static final int EXPECTED_ARGUMENT_COUNT = 2;

    /** Original user input containing the task number and priority. */
    private final String commandInput;

    /**
     * Creates a priority command containing the user's original input.
     *
     * @param commandInput original user input
     */
    public PriorityCommand(String commandInput) {
        assert commandInput != null : "Priority command input must not be null";
        this.commandInput = commandInput;
    }

    /**
     * Assigns the requested priority, restoring the old priority if saving fails.
     *
     * @param tasks current task list to update
     * @param ui interface used to show the confirmation
     * @param storage persistence service used to save the updated list
     * @throws BobbyException if the command is invalid or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BobbyException {
        Parser parser = new Parser();
        String arguments = parser.getCommandArgument(commandInput, "priority");
        if (arguments.isEmpty()) {
            throw new BobbyException("Error! The task number cannot be empty!");
        }

        String[] argumentParts = arguments.split("\\s+");
        if (argumentParts.length == 1) {
            throw new BobbyException("Error! The priority level cannot be empty!");
        } else if (argumentParts.length != EXPECTED_ARGUMENT_COUNT) {
            throw new BobbyException("Error! The priority command must follow: "
                    + "priority TASK_NUMBER LEVEL.");
        }

        int taskNumber = parseTaskNumber(argumentParts[0]);
        if (tasks.size() == 0) {
            throw new BobbyException("No tasks available to prioritize.");
        } else if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new BobbyException("Error! The task number must be between 1 and "
                    + tasks.size() + ".");
        }

        Priority priority = Priority.fromUserInput(argumentParts[1]);
        Task task = tasks.get(taskNumber - 1);
        Priority previousPriority = task.getPriority();
        task.setPriority(priority);
        try {
            storage.save(tasks);
        } catch (BobbyException exception) {
            task.setPriority(previousPriority);
            throw exception;
        }
        ui.showTaskPriorityUpdated(task);
    }

    /**
     * Parses the one-based task number supplied to this command.
     *
     * @param input task-number input
     * @return parsed one-based task number
     * @throws BobbyException if the input is not an integer
     */
    private int parseTaskNumber(String input) throws BobbyException {
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            throw new BobbyException("Error! The task number must be a valid integer.");
        }
    }
}
