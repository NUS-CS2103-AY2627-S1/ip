package harvey.command;

import harvey.HarveyException;
import harvey.storage.Storage;
import harvey.task.Task;
import harvey.task.TaskList;
import harvey.ui.Ui;
/** Marks one task as not completed after all. */
public class UnmarkCommand extends Command {
    /** The task number the user typed, still unchecked. */
    private final String argument;

    /**
     * Creates a command that will mark one task as not done.
     *
     * @param argument the task number, as typed.
     */
    public UnmarkCommand(String argument) {
        this.argument = argument;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws HarveyException {
        Task unmarked = tasks.get(parseTaskNumber(argument, tasks, CommandType.UNMARK));
        // Reported for the same reason as in MarkCommand: the user probably meant another task.
        if (!unmarked.isDone()) {
            throw new HarveyException(ui.formatLines("That one was never closed:", "  " + unmarked,
                    "Check the number with list."));
        }
        unmarked.markAsNotDone();
        storage.save(tasks.asList());
        return ui.formatLines("Reopened. Don't make a habit of it:", "  " + unmarked);
    }
}
