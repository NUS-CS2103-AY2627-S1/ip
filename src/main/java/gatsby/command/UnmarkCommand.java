package gatsby.command;

import gatsby.exception.GatsbyException;
import gatsby.model.TaskList;
import gatsby.ui.Ui;

/**
 * Marks one selected task as not done.
 */
public class UnmarkCommand extends Command {
    /** The task number entered after the {@code unmark} command. */
    private final String payload;

    /**
     * Creates a command for the supplied task number.
     *
     * @param payload the text entered after {@code unmark}
     */
    public UnmarkCommand(String payload) {
        this.payload = payload == null ? "" : payload.strip();
    }

    /**
     * Unmarks the selected task and displays the result.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     * @throws GatsbyException when the task number is missing or invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws GatsbyException {
        updateTaskStatus(tasks, ui, payload, false);
    }
}
