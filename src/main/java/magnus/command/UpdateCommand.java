package magnus.command;

import magnus.exception.CommandSyntaxException;
import magnus.exception.DuplicateTaskException;
import magnus.exception.MagnusException;
import magnus.exception.TaskNotFoundException;
import magnus.task.DeadlineTask;
import magnus.task.EventTask;
import magnus.task.Task;
import magnus.task.TaskList;
import magnus.task.ToDoTask;

/**
 * Updates an existing task through a two-step interaction.
 * The first input selects a task, and the next input supplies replacement information
 * in the format required by that task's type.
 */
public class UpdateCommand implements Command {
    private static final int NO_PENDING_TASK = -1;
    private static final String UPDATE_FAILED_MESSAGE =
            "\tThat move does not match the required format - the task was not updated.";
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HHmm";

    private final TaskList tasks;
    private int pendingTaskIndex = NO_PENDING_TASK;

    /**
     * Creates a command that updates tasks in the specified task list.
     *
     * @param tasks The task list containing tasks that can be updated.
     */
    public UpdateCommand(TaskList tasks) {
        this.tasks = tasks;
    }

    /**
     * Selects the task identified by a one-based task number and asks for its replacement data.
     *
     * @param args The command arguments, with the one-based task number at index 0.
     * @return Type-specific guidance for entering the replacement task information.
     * @throws CommandSyntaxException If the task number has invalid syntax.
     * @throws TaskNotFoundException If the task number does not identify an existing task.
     */
    @Override
    public String execute(String... args) throws MagnusException {
        int taskIndex = TaskIndexParser.parseTaskIndex(args, "update", this.tasks.getTaskCount());
        Task selectedTask = this.tasks.getTask(taskIndex);
        this.pendingTaskIndex = taskIndex;

        if (selectedTask instanceof DeadlineTask) {
            return "\tYour move - enter the updated Deadline task in this format:\n"
                    + "\t<new task name> /by <deadline time>\n"
                    + "\tDeadline time format: " + DATE_TIME_FORMAT;
        }
        if (selectedTask instanceof EventTask) {
            return "\tYour move - enter the updated Event task in this format:\n"
                    + "\t<new task name> /from <start time> /to <end time>\n"
                    + "\tStart and end time format: " + DATE_TIME_FORMAT;
        }
        return "\tYour move - enter the updated To-Do task in this format:\n"
                + "\t<new task name>";
    }

    /**
     * Returns whether the command is waiting for replacement task information.
     *
     * @return {@code true} after a task has been selected and before update input is processed.
     */
    boolean isAwaitingUpdatedTask() {
        return this.pendingTaskIndex != NO_PENDING_TASK;
    }

    /**
     * Validates replacement information for the selected task and applies the update.
     * The pending update is always cleared, including when validation fails.
     *
     * @param updateInput Raw replacement task information entered by the user.
     * @return A message displaying the updated task.
     * @throws CommandSyntaxException If the replacement information does not match the required format.
     */
    String completeUpdate(String updateInput) throws MagnusException {
        assert isAwaitingUpdatedTask() : "An update must select a task before receiving replacement data";
        int taskIndex = this.pendingTaskIndex;
        this.pendingTaskIndex = NO_PENDING_TASK;

        Task originalTask = this.tasks.getTask(taskIndex);
        Task updatedTask;
        try {
            updatedTask = createUpdatedTask(originalTask, updateInput);
        } catch (IllegalArgumentException exception) {
            throw new CommandSyntaxException(UPDATE_FAILED_MESSAGE);
        }

        if (originalTask.isDone()) {
            updatedTask.markAsDone();
        }
        if (this.tasks.containsTaskWithSameDetailsExcept(updatedTask, taskIndex)) {
            throw new DuplicateTaskException(
                    "\tThat piece is already on the board - an identical task already exists.");
        }
        this.tasks.replaceTask(taskIndex, updatedTask);
        return "\tPosition updated - I've updated this task:\n\n\t" + updatedTask;
    }

    /**
     * Creates a replacement task of the same type as the selected task.
     *
     * @param originalTask The task whose type determines the required input format.
     * @param updateInput Raw replacement information.
     * @return A validated replacement task.
     * @throws IllegalArgumentException If the replacement information is malformed.
     */
    private Task createUpdatedTask(Task originalTask, String updateInput) {
        if (updateInput == null || updateInput.isBlank()
                || updateInput.contains("\n") || updateInput.contains("\r")) {
            throw new IllegalArgumentException("Updated task information must not be blank or multiline");
        }

        String strippedInput = updateInput.strip();
        if (originalTask instanceof DeadlineTask) {
            return createDeadlineTask(strippedInput);
        }
        if (originalTask instanceof EventTask) {
            return createEventTask(strippedInput);
        }
        if (TaskArgumentsParser.containsFieldDelimiter(strippedInput)) {
            throw new IllegalArgumentException("A To-Do update accepts only a task name");
        }
        return new ToDoTask(strippedInput);
    }

    /**
     * Creates a deadline task from an update input containing exactly one {@code /by} field.
     *
     * @param updateInput The stripped update input.
     * @return A validated deadline task.
     * @throws IllegalArgumentException If the syntax or deadline time is invalid.
     */
    private Task createDeadlineTask(String updateInput) {
        TaskArgumentsParser.DeadlineArguments taskArguments =
                TaskArgumentsParser.parseDeadline(updateInput);
        return new DeadlineTask(taskArguments.description(), taskArguments.deadline());
    }

    /**
     * Creates an event task from update input containing one {@code /from} and one {@code /to} field.
     *
     * @param updateInput The stripped update input.
     * @return A validated event task.
     * @throws IllegalArgumentException If the syntax or either event time is invalid.
     */
    private Task createEventTask(String updateInput) {
        TaskArgumentsParser.EventArguments taskArguments =
                TaskArgumentsParser.parseEvent(updateInput);
        return new EventTask(
                taskArguments.description(), taskArguments.start(), taskArguments.end());
    }
}
