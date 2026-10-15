package noms.command;

import noms.exception.NomsException;
import noms.parser.Parser;
import noms.storage.Storage;
import noms.task.Task;
import noms.task.TaskList;
import noms.ui.Ui;

/**
 * Marks a task as not done. The task number is validated against the
 * current list size at execution time.
 */
public class UnmarkCommand extends Command {
    private final String command;

    /**
     * Creates a command that marks the task named by the command line as not done.
     *
     * @param command the full command line, e.g. {@code "unmark 2"}
     */
    public UnmarkCommand(String command) {
        this.command = command;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NomsException {
        int taskNumber = Parser.parseTaskNumber(command, "unmark", tasks.size());
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "Parser returned an invalid task number";
        Task task = tasks.get(taskNumber - 1);
        boolean changed = task.markAsNotDone();
        if (changed) {
            save(tasks, storage, task::markAsDone);
        }
        ui.showTaskUnmarked(task, changed);
    }
}
