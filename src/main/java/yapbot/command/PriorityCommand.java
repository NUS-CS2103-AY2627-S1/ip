package yapbot.command;

import yapbot.exception.YapBotException;
import yapbot.storage.Storage;
import yapbot.task.Priority;
import yapbot.task.TaskList;
import yapbot.ui.Ui;

/**
 * Represents the "priority" command, which changes an existing task's priority.
 */
public class PriorityCommand extends Command {

    private static final String COMMAND_WORD = "priority";

    private final String fullCommand;

    /**
     * Creates a priority command from the full command text.
     *
     * @param fullCommand the full command text as typed by the user, e.g. "priority 2 high".
     */
    public PriorityCommand(String fullCommand) {
        this.fullCommand = fullCommand;
    }

    /**
     * Changes the priority of the task named in the command, persists the
     * updated list to disk, and shows a confirmation to the user.
     *
     * @param taskList the task list containing the task to update.
     * @param ui the Ui to show the confirmation on.
     * @throws YapBotException if no valid task number and priority were given.
     */
    @Override
    public void execute(TaskList taskList, Ui ui) throws YapBotException {
        String argument = fullCommand.length() > COMMAND_WORD.length()
                ? fullCommand.substring(COMMAND_WORD.length()).trim()
                : "";
        String[] tokens = argument.split("\\s+", 2);
        if (tokens.length < 2 || tokens[0].isEmpty() || tokens[1].isEmpty()) {
            throw new YapBotException("Please specify a task number and a priority, "
                    + "e.g. 'priority 2 high'.");
        }

        int taskIndex = validateTaskIndex(taskList, tokens[0]);
        Priority priority = Priority.fromInput(tokens[1].trim());

        taskList.get(taskIndex).setPriority(priority);
        Storage.save(taskList.toArray(), taskList.size());
        ui.showPriorityChanged(taskList.get(taskIndex));
    }

    /**
     * Validates the task-number token against the current list, reusing
     * {@link TaskList#validateIndex}'s number/range checks, but with a
     * custom empty-list message: its generic "...nothing to priority."
     * wording doesn't read naturally, since "priority" isn't a verb the
     * way "mark"/"unmark"/"delete" are.
     *
     * @param taskList   the task list to validate against.
     * @param indexToken the task-number token, e.g. "2".
     * @return the validated, zero-based task index.
     * @throws YapBotException if the token isn't a valid, in-range task number.
     */
    private int validateTaskIndex(TaskList taskList, String indexToken) throws YapBotException {
        if (taskList.size() == 0) {
            throw new YapBotException(
                    "Your task list is empty, so there's nothing to change the priority of.");
        }
        return taskList.validateIndex(COMMAND_WORD + " " + indexToken, COMMAND_WORD);
    }
}
