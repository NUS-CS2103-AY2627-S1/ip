package nyonbot.command;

import java.util.HashMap;

import nyonbot.Logic.Result;

/**
 * Creates a command to exit the program.
 */
public class ExitCommand extends Command {

    /**
     * Creates an exit command.
     *
     * @param arguments parsed command arguments
     */
    public ExitCommand(HashMap<String, String> arguments) {
        super(arguments);
    }

    /** {@inheritDoc} */
    @Override
    public Result execute() {
        return new Result("" , true);
    }
}
