package noms.command;

import noms.exception.NomsException;
import noms.parser.Parser;
import noms.storage.Storage;
import noms.task.Task;
import noms.task.TaskList;
import noms.ui.Ui;

/**
 * Marks a task as done. The task number is validated against the current
 * list size at execution time, since the list can change between commands.
 */
public class MarkCommand extends Command {
    private final String command;

    /**
     * Creates a command that marks the task named by the command line as done.
     *
     * @param command the full command line, e.g. {@code "mark 2"}
     */
    public MarkCommand(String command) {
        this.command = command;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NomsException {
        int taskNumber = Parser.parseTaskNumber(command, "mark", tasks.size());
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "Parser returned an invalid task number";
        Task task = tasks.get(taskNumber - 1);
        boolean changed = task.markAsDone();
        if (changed) {
            save(tasks, storage, task::markAsNotDone);
        }
        ui.showTaskMarked(task, changed);
    }
}
