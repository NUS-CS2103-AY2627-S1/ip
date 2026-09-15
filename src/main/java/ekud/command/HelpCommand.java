package ekud.command;

import ekud.EkudException;
import ekud.parser.Parser;
import ekud.storage.Storage;
import ekud.task.TaskList;
import ekud.ui.Ui;

/**
 * Shows a short guide to the commands the chatbot understands.
 * This command does not take arguments.
 */
public class HelpCommand implements Command {
    private HelpCommand() {
    }

    /**
     * Parses {@code help} with no extra text.
     *
     * @param arguments text after the command word
     * @return a help command
     * @throws EkudException if extra arguments are present
     */
    public static HelpCommand parse(String arguments) throws EkudException {
        Parser.requireNoArguments(CommandType.HELP.getKeyword(), arguments);
        return new HelpCommand();
    }

    /**
     * Displays the command guide. The save file is not written.
     *
     * @param tasks   unused
     * @param ui      used to show the guide
     * @param storage unused; help does not change the list
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showHelp();
    }
}
