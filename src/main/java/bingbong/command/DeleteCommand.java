package bingbong.command;

import bingbong.exception.BingBongException;
import bingbong.parser.Parser;
import bingbong.storage.Storage;
import bingbong.task.Task;
import bingbong.task.TaskList;
import bingbong.ui.Ui;

/**
 * bingbong.command.Command to remove a task from the list.
 */
public class DeleteCommand extends Command {

    private final String in;

    /**
     * Initialises a delete command object.
     *
     * @param in The command line user input.
     */
    public DeleteCommand(String in) {
        this.in = in;
    }

    /**
     * extracts targeted indexing pointers, clears entries out, updates users, and writes to files.
     *
     * @param tasks   The active TaskList tracking current items.
     * @param ui      The user interface formatting output blocks.
     * @param storage The storage file handler managing disk reads and writes.
     * @throws BingBongException If user bounds evaluation checks fail or numeric arguments are missing.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingBongException {
        if (in.trim().length() <= 6) {
            throw new BingBongException("Please specify a valid task number to delete! :(");
        }

        int taskIndex = Parser.parseIndex(in, 7);
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new BingBongException("That task slot does not exist in your list! :(");
        }
        Task removedTask = tasks.remove(taskIndex);
        ui.print(
                "BingBong removed this task from the list(" + tasks.size() + " task(s) remaining):\n"
                        + removedTask.toString());
        storage.save(tasks);

    }
}
