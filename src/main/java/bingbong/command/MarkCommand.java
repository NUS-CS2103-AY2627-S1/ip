package bingbong.command;

import bingbong.exception.BingBongException;
import bingbong.parser.Parser;
import bingbong.storage.Storage;
import bingbong.task.TaskList;
import bingbong.ui.Ui;

/**
 * bingbong.command.Command to mark a specific task as completed.
 */
public class MarkCommand extends Command {

    private final String in;

    /**
     * Initialises mark command object targeting completed state item.
     *
     * @param in The command line user input.
     */
    public MarkCommand(String in) {
        this.in = in;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BingBongException {
        if (in.trim().length() <= 4) {
            throw new BingBongException("Please specify a valid task number to mark! :(");
        }

        int taskIndex = Parser.parseIndex(in, 5);
        if (taskIndex < 0 || taskIndex >= tasks.size()) {
            throw new BingBongException("That task slot does not exist in your list! :(");
        }
        tasks.get(taskIndex).mark();
        ui.print("BingBong marks this task as done:\n" + tasks.get(taskIndex).toString());
        storage.save(tasks);

    }
}
