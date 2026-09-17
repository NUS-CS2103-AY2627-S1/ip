package harvey.command;

import harvey.HarveyException;
import harvey.storage.Storage;
import harvey.task.Task;
import harvey.task.TaskList;
import harvey.ui.Ui;
/** Marks one task as completed. */
public class MarkCommand extends Command {
    /** The task number the user typed, still unchecked. */
    private final String argument;

    /**
     * Creates a command that will mark one task as done.
     *
     * @param argument the task number, as typed.
     */
    public MarkCommand(String argument) {
        this.argument = argument;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws HarveyException {
        Task marked = tasks.get(parseTaskNumber(argument, tasks, CommandType.MARK));
        // Reported rather than silently repeated, because marking a finished task usually
        // means the user picked the wrong number and the task they meant is still open.
        if (marked.isDone()) {
            throw new HarveyException(ui.formatLines("That one's already closed:", "  " + marked,
                    "Check the number with list."));
        }
        marked.markAsDone();
        storage.save(tasks.asList());
        return ui.formatLines("Closed. Another win:", "  " + marked);
    }
}
