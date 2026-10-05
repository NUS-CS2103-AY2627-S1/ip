package gatsby.command;

import gatsby.exception.EmptyPayloadException;
import gatsby.exception.GatsbyException;
import gatsby.model.Task;
import gatsby.model.TaskList;
import gatsby.storage.Storage;
import gatsby.ui.Ui;

/**
 * Removes one selected task from the list.
 */
public class DeleteCommand extends Command {
    /** The task number entered after the {@code delete} command. */
    private final String payload;

    /**
     * Creates a command for the supplied task number.
     *
     * @param payload the text entered after {@code delete}
     */
    public DeleteCommand(String payload) {
        this.payload = payload == null ? "" : payload.strip();
    }

    /**
     * Removes, saves, and reports the selected task.
     *
     * @param tasks the current task list
     * @param ui the console interaction handler
     * @throws GatsbyException when the task number is missing or invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws GatsbyException {
        if (payload.isEmpty()) {
            throw new EmptyPayloadException(" OOPS! How do I even delete nothing??");
        }
        Task removed = tasks.remove(parseTaskIndex(tasks, payload));
        Storage.save(tasks.asList());
        ui.printLine(" Noted. I've removed this task:");
        ui.printLine("  " + removed);
        printTaskCount(tasks, ui);
    }
}
