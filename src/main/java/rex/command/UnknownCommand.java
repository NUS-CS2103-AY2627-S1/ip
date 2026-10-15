package rex.command;

import rex.RexException;
import rex.TaskList;
import rex.Ui;

/**
 * A command word the program does not recognize.
 *
 * Refusing the input is treated as a command of its own, rather than as a
 * special case in the parser, so that the main loop has a command to run
 * whatever the user typed and never has to handle "no command at all".
 */
public class UnknownCommand extends Command {
    /**
     * Always refuses.
     *
     * @throws RexException every time, carrying the message shown to the user.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws RexException {
        throw new RexException("Woof? I don't know what that means :-(");
    }
}
