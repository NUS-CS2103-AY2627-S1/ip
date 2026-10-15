package zaru.command;

import zaru.exception.ZaruException;
import zaru.task.TaskList;

/** Executes the {@code delete} command. */
public class DeleteCommand extends Command {
    private final String taskNumber;

    /**
     * Creates a delete command.
     *
     * @param taskNumber Task number entered by the user.
     */
    public DeleteCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Returns the command word represented by this command.
     *
     * @return The {@code delete} command word.
     */
    @Override
    public String getCommand() {
        return "delete";
    }

    /**
     * Validates the task number, removes the selected task, and returns the response message.
     *
     * @param tasks Current task list.
     * @return Deleted-task response message.
     * @throws ZaruException If the task number is invalid or deletion fails.
     */
    @Override
    public String execute(TaskList tasks) throws ZaruException {
        int taskIndex = parseTaskNumber(tasks, taskNumber);
        String deletedTask = tasks.getTaskString(taskIndex);
        tasks.delete(taskIndex);
        return "I've deleted that task!\n%s".formatted(deletedTask);
    }
}
