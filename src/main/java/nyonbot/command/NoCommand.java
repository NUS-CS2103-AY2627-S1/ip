package nyonbot.command;

import nyonbot.Logic.Result;

/**
 * Creates a NoCommand.
 */
public class NoCommand extends Command {

    /** {@inheritDoc} */
    @Override
    public Result execute() {
        return new Result("what...?");
    }
}
