package verity.command;

import verity.storage.Storage;
import verity.task.TaskList;

/**
 * A command whose effect on the task list can be reversed.
 */
public interface Undoable {
    /**
     * Reverses this command's effect on the task list.
     *
     * @param tasks Task list to act on.
     * @param storage Storage to persist the reversal through.
     * @return Response to show the user.
     */
    String undo(TaskList tasks, Storage storage);
}
