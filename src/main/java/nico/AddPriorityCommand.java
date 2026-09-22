package nico;

import java.util.List;

/**
 * Assigns or replaces a task's priority and saves the change.
 */
public class AddPriorityCommand extends TaskNumberCommand {
    private final Priority priority;

    /**
     * Creates a priority command from a task number and priority value.
     */
    public AddPriorityCommand(String argument) throws NicoException {
        this(splitArgument(argument));
    }

    private AddPriorityCommand(String[] arguments) throws NicoException {
        super("addpriority", arguments[0]);
        priority = Priority.parse(arguments[1]);
    }

    private static String[] splitArgument(String argument) throws NicoException {
        String[] arguments = argument == null ? new String[0] : argument.trim().split("\\s+");
        if (arguments.length != 2) {
            throw NicoException.invalidInput("Choose a task number and priority.",
                    "addpriority TASK_NUMBER PRIORITY");
        }
        return arguments;
    }

    @Override
    public void execute(List<Task> tasks, Ui ui) throws NicoException {
        Task task = getTask(tasks);
        Priority previousPriority = task.getPriority();
        task.setPriority(priority);
        try {
            TaskStorage.writeAllTasks(Nico.FILE_PATH, tasks);
        } catch (NicoException exception) {
            task.setPriority(previousPriority);
            throw exception;
        }
        ui.showMessage("Can lah, I've set this task's priority to " + priority + ":");
        ui.showMessage(task.toString());
    }
}
