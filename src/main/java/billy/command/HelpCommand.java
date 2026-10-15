package billy.command;

import billy.parser.CommandWord;
import billy.storage.Storage;
import billy.task.TaskList;
import billy.ui.Ui;

/**
 * Lists every command Billy accepts, with the shorter words that also invoke it.
 *
 * <p>The list Billy names when it fails to recognize a command is deliberately
 * short: it is read by someone who has just mistyped, and naming two ways of
 * saying the same thing would only be more to read. That leaves the
 * abbreviations undiscoverable from inside Billy, which is what this command is
 * for.
 *
 * <p>The only command that neither reads nor changes the task list. It takes the
 * list and the storage like every other command and ignores both, which costs
 * nothing and keeps one shape for carrying a command out.
 */
public class HelpCommand extends Command {

    /**
     * Shows every command, grouped by the kind of work it does.
     *
     * <p>The listing itself comes from {@link CommandWord}, so it cannot fall
     * out of step with what Billy accepts. Only the sentence introducing it is
     * written here, alongside the wording of every other command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.show("Everything I know how to do. Short forms in brackets, for the impatient.",
                CommandWord.describeCommands());
    }
}
