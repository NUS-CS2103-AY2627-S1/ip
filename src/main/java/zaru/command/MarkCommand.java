package zaru.command;

import zaru.exception.ZaruException;
import zaru.task.TaskList;

/** Executes the {@code mark} command. */
public class MarkCommand extends Command {
    private final String taskNumber;

    /**
     * Creates a mark command.
     *
     * @param taskNumber Task number entered by the user.
     */
    public MarkCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Returns the command word represented by this command.
     *
     * @return The {@code mark} command word.
     */
    @Override
    public String getCommand() {
        return "mark";
    }

    /**
     * Marks the selected task as complete and returns the response message.
     *
     * @param tasks Current task list.
     * @return Marked-task response message.
     * @throws ZaruException If the task number is invalid or marking fails.
     */
    @Override
    public String execute(TaskList tasks) throws ZaruException {
        int taskIndex = parseTaskNumber(tasks, taskNumber);
        tasks.markAsComplete(taskIndex);
        return "Meow! I've marked that task as done!\n%s".formatted(tasks.getTaskString(taskIndex));
    }
}
