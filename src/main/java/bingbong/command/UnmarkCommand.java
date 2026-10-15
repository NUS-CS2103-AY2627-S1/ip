package bingbong.command;

import bingbong.exception.BingBongException;
import bingbong.parser.Parser;
import bingbong.storage.Storage;
import bingbong.task.TaskList;
import bingbong.ui.Ui;

/**
 * bingbong.command.Command to unmark a specific task as incomplete.
 */
public class UnmarkCommand extends Command {

    private final String in;

    /**
     * Initialises unmark command object targeting incomplete state item.
     *
     * @param in The command line user input.
     */
    public UnmarkCommand(String in) {
        this.in = in;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingBongException {
        if (in.trim().length() <= 6) {
            throw new BingBongException("Please specify a valid task number to mark! :(");
        }

        int taskIndex = Parser.parseIndex(in, 7);
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new BingBongException("That task slot does not exist in your list! :(");
        }
        tasks.get(taskIndex).unmark();
        ui.print("BingBong marks this task as not done yet:\n" + tasks.get(taskIndex).toString());
        storage.save(tasks);

    }
}
