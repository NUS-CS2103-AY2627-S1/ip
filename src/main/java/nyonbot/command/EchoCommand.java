package nyonbot.command;

import java.util.HashMap;

import nyonbot.Logic.Result;

/**
 * Echos the user input.
 */
public class EchoCommand extends Command {
    public EchoCommand(HashMap<String, String> arguments) {
        super(arguments);
    }

    /** {@inheritDoc} */
    @Override
    public Result execute() {
        assert arguments != null;
        String description = arguments.getOrDefault(DESCRIPTION_KEY, null);
        return new Result(description == null
                ? arguments.getOrDefault(COMMAND_KEY, "")
                : description);
    }
}
