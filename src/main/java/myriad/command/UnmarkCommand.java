package myriad.command;

import myriad.MyriadException;
import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.ui.Ui;

/**
 * Marks the task with the given number as not done yet.
 */
public class UnmarkCommand extends TaskNumberCommand {

    /**
     * Creates a command that marks the task the user numbered as not done.
     *
     * @param taskNumber the task number as typed, 1-based and not yet
     *                   checked against the list (see
     *                   {@link TaskNumberCommand#resolveIndex}).
     */
    public UnmarkCommand(int taskNumber) {
        super(taskNumber);
    }

    /**
     * Marks the numbered task not done, shows it in its new state, then saves.
     *
     * @throws MyriadException if no task has that number, or if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MyriadException {
        int index = resolveIndex(tasks);
        tasks.markNotDone(index);
        ui.showUnmarked(tasks.get(index));
        save(tasks, storage);
    }
}
