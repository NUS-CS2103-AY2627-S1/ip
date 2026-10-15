package zen.command;

import zen.ZenException;
import zen.storage.Storage;
import zen.task.TaskList;
import zen.ui.Ui;

/** Adds a deadline task. */
public class DeadlineCommand extends AddCommand {
    private final String arguments;

    /**
     * Creates a command using the supplied deadline details.
     *
     * @param arguments description and due by details to parse
     */
    public DeadlineCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Adds the parsed deadline task, displays a confirmation, and saves the updated task list.
     *
     * @param tasks task list to which the deadline is added
     * @param ui user interface used to display the confirmation
     * @param storage storage service used to save the updated task list
     * @throws ZenException if the deadline details are invalid or the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZenException {
        addTask(Parser.parseDeadline(arguments), tasks, ui, storage);
    }
}
