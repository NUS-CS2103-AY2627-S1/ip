package noms.command;

import noms.exception.NomsException;
import noms.parser.Parser;
import noms.storage.Storage;
import noms.task.Task;
import noms.task.TaskList;
import noms.ui.Ui;

/**
 * Removes a task from the list. The task number is validated against the
 * current list size at execution time.
 */
public class DeleteCommand extends Command {
    private final String command;

    /**
     * Creates a command that deletes the task named by the command line.
     *
     * @param command the full command line, e.g. {@code "delete 2"}
     */
    public DeleteCommand(String command) {
        this.command = command;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NomsException {
        int taskNumber = Parser.parseTaskNumber(command, "delete", tasks.size());
        assert taskNumber >= 1 && taskNumber <= tasks.size()
                : "Parser returned an invalid task number";
        Task deletedTask = tasks.delete(taskNumber - 1);
        save(tasks, storage, () -> tasks.add(taskNumber - 1, deletedTask));
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
