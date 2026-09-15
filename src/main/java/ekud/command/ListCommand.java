package ekud.command;

import ekud.EkudException;
import ekud.parser.Parser;
import ekud.storage.Storage;
import ekud.task.TaskList;
import ekud.ui.Ui;

/**
 * Shows every task currently in the list.
 * This command does not take arguments.
 */
public class ListCommand implements Command {
    private ListCommand() {
    }

    /**
     * Parses {@code list} with no extra text.
     *
     * @param arguments text after the command word
     * @return a list command
     * @throws EkudException if extra arguments are present
     */
    public static ListCommand parse(String arguments) throws EkudException {
        Parser.requireNoArguments(CommandType.LIST.getKeyword(), arguments);
        return new ListCommand();
    }

    /**
     * Displays the tasks through the UI. The save file is not written.
     *
     * @param tasks   the list to display
     * @param ui      used to show the list
     * @param storage unused; listing does not change the list
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
