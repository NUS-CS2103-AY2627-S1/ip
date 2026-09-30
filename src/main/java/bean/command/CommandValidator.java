package bean.command;

import bean.exception.InvalidSyntaxException;

/** Provides validation helpers shared by command implementations. */
final class CommandValidator {

    private CommandValidator() {
    }

    /** Throws an exception when a command component is empty. */
    static void checkEmpty(StringBuilder string, String input, String commandName,
                           String message, String usage) {
        if (string.isEmpty()) {
            throw new InvalidSyntaxException("Uh Oh! Invalid Syntax for " + commandName + "!\n"
                    + message
                    + "Usage: " + usage, input);
        }
    }

}
