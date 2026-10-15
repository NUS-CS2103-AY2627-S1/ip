package zaru.command;

import zaru.exception.ZaruException;
import zaru.task.TaskList;

/** Executes the {@code unmark} command. */
public class UnmarkCommand extends Command {
    private final String taskNumber;

    /**
     * Creates an unmark command.
     *
     * @param taskNumber Task number entered by the user.
     */
    public UnmarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Returns the command word represented by this command.
     *
     * @return The {@code unmark} command word.
     */
    @Override
    public String getCommand() {
        return "unmark";
    }

    /**
     * Marks the selected task as incomplete and returns the response message.
     *
     * @param tasks Current task list.
     * @return Unmarked-task response message.
     * @throws ZaruException If the task number is invalid or unmarking fails.
     */
    @Override
    public String execute(TaskList tasks) throws ZaruException {
        int taskIndex = parseTaskNumber(tasks, taskNumber);
        tasks.markAsIncomplete(taskIndex);
        return "I've unmarked that task!\n%s".formatted(tasks.getTaskString(taskIndex));
    }
}
