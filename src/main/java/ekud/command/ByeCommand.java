package ekud.command;

import ekud.EkudException;
import ekud.parser.Parser;
import ekud.storage.Storage;
import ekud.task.TaskList;
import ekud.ui.Ui;

/**
 * Ends the chatbot session after printing a goodbye message.
 * This command does not take arguments.
 */
public class ByeCommand implements Command {
    private ByeCommand() {
    }

    /**
     * Parses {@code bye} with no extra text.
     *
     * @param arguments text after the command word
     * @return a bye command
     * @throws EkudException if extra arguments are present
     */
    public static ByeCommand parse(String arguments) throws EkudException {
        Parser.requireNoArguments(CommandType.BYE.getKeyword(), arguments);
        return new ByeCommand();
    }

    /**
     * Shows the goodbye message. The chatbot then exits because {@link #isExit()}
     * is {@code true}. The save file is not written; earlier commands already saved.
     *
     * @param tasks   unused
     * @param ui      used to show the goodbye message
     * @param storage unused; exiting does not change the list
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Signals that the command loop should stop.
     *
     * @return {@code true}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
