package sallman.command;

import sallman.Parser;
import sallman.SallmanException;
import sallman.Storage;
import sallman.TaskList;
import sallman.Ui;
import sallman.task.Task;

/**
 * Removes one task from the list.
 */
public class DeleteCommand extends Command {

    /** The task number the user gave, checked when the command runs. */
    private final String arguments;

    /**
     * Creates a command that deletes the task the given argument names.
     *
     * @param arguments text the user typed after the command
     */
    public DeleteCommand(String arguments) {
        this.arguments = arguments;
    }

    /**
     * Removes the numbered task, reports what was removed, and saves the list.
     *
     * @param tasks   the list to remove from
     * @param ui      used to report the removal
     * @param storage used to save the shortened list
     * @throws SallmanException if the number is missing, unreadable or outside
     *                          the list, or if the list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SallmanException {
        // Checked here rather than while parsing, because whether a number is
        // in range depends on the list, which the parser does not see.
        int index = Parser.parseTaskNumber(CommandType.DELETE.getKeyword(), arguments,
                tasks.size());
        tasks.saveSnapshot();
        Task removed = tasks.remove(index);
        ui.showRemoved(removed, tasks.size());
        storage.save(tasks.asList());
    }
}
