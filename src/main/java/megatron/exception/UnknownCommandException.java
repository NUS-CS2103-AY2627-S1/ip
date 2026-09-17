package megatron.exception;

import java.util.List;

/**
 * Reports that the user entered a command that Megatron does not support.
 */
public final class UnknownCommandException extends MegatronException {
    /**
     * Creates an error that lists the commands that Megatron supports.
     *
     * @param availableCommands The supported command names.
     */
    public UnknownCommandException(List<String> availableCommands) {
        super("I do not recognise that command. Try " + formatCommands(availableCommands) + ".");
    }

    /**
     * Formats command names as a readable list with "or" before the final command.
     *
     * @param availableCommands The command names to format.
     * @return The readable command list.
     */
    private static String formatCommands(List<String> availableCommands) {
        if (availableCommands.size() == 1) {
            return availableCommands.get(0);
        }
        String firstCommands = String.join(", ", availableCommands.subList(0, availableCommands.size() - 1));
        return firstCommands + ", or " + availableCommands.get(availableCommands.size() - 1);
    }
}
