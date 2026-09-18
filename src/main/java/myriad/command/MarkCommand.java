package myriad.command;

import myriad.MyriadException;
import myriad.storage.Storage;
import myriad.task.TaskList;
import myriad.ui.Ui;

/**
 * Marks the task with the given number as done.
 */
public class MarkCommand extends TaskNumberCommand {

    /**
     * Creates a command that marks the task the user numbered as done.
     *
     * @param taskNumber the task number as typed, 1-based and not yet
     *                   checked against the list (see
     *                   {@link TaskNumberCommand#resolveIndex}).
     */
    public MarkCommand(int taskNumber) {
        super(taskNumber);
    }

    /**
     * Marks the numbered task done, shows it in its new state, then saves.
     *
     * @throws MyriadException if no task has that number, or if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MyriadException {
        int index = resolveIndex(tasks);
        tasks.markDone(index);
        ui.showMarked(tasks.get(index));
        save(tasks, storage);
    }
}
