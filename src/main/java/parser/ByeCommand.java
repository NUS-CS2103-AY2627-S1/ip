package parser;

import exception.FriedbergException;

/**
 * Handles the 'bye' Command for Friedberg.
 */
public class ByeCommand implements Command {
    @Override
    public boolean isCommand(String userInput) {
        return ParserUtil.hasCommandWord(userInput, "bye");
    }

    @Override
    public boolean isBye() {
        return true;
    }

    @Override
    public String execute(String userInput, CommandContext commandContext) throws FriedbergException {
        ParserUtil.parseNoArgumentCommand(userInput, "bye");
        return "Bye bye, see you again next time.";
    }
}
