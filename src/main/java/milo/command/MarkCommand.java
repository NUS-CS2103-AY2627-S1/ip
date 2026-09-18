package milo.command;

import milo.exception.MiloException;
import milo.storage.Storage;
import milo.task.Task;
import milo.task.TaskList;
import milo.ui.Ui;

/**
 * Represents the "mark" and "unmark" commands, which set whether a task is
 * done. Both share this one class rather than getting one each, since they
 * differ only in which way the flag is flipped and which sentence is shown.
 */
public class MarkCommand extends Command {
    /** The task number the user typed, not yet validated against the list. */
    private final String rawArguments;

    /** True for "mark", false for "unmark". */
    private final boolean shouldMarkDone;

    /**
     * Creates a command that will mark or unmark a task.
     *
     * @param rawArguments   the task number the user typed, as text
     * @param shouldMarkDone true to mark the task done, false to undo that
     */
    public MarkCommand(String rawArguments, boolean shouldMarkDone) {
        this.rawArguments = rawArguments;
        this.shouldMarkDone = shouldMarkDone;
    }

    @Override
    public boolean isUndoable() {
        return true;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MiloException {
        String verb = shouldMarkDone ? "mark" : "unmark";
        Task task = tasks.get(resolveTaskIndex(tasks, verb, rawArguments));

        String[] confirmation;
        if (shouldMarkDone) {
            task.markAsDone();
            confirmation = new String[] {"Nice one. Crossing this off:", "  " + task};
        } else {
            task.markAsNotDone();
            confirmation = new String[] {"Sure — back on the pile:", "  " + task};
        }

        storage.save(tasks.asArrayList());
        ui.showResponse(confirmation);
    }
}
