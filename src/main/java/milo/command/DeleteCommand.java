package milo.command;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.Task;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Represents the "delete" command, which removes a task from the list.
 * Tasks after it shift up, so the numbers shown by "list" stay contiguous.
 */
public class DeleteCommand extends Command {
    /** The task number the user typed, not yet validated against the list. */
    private final String rawArguments;

    /**
     * Creates a command that will delete a task.
     *
     * @param rawArguments the task number the user typed, as text
     */
    public DeleteCommand(String rawArguments) {
        this.rawArguments = rawArguments;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MiloException {
        // delete() hands back the task it took out, so it can be shown to the
        // user without having to fetch it separately beforehand.
        Task removed = tasks.delete(resolveTaskIndex(tasks, "delete", rawArguments));
        storage.save(tasks.asArrayList());
        ui.showResponse(
                "Buried it. This one's gone:",
                "  " + removed,
                Ui.describeCount(tasks.size()) + " left.");
    }
}
