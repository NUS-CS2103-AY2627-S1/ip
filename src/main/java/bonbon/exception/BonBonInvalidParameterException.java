package bonbon.exception;

import bonbon.command.Command;

public class BonBonInvalidParameterException extends BonBonException {
    /**
     * Creates a BonBonInvalidParameterException
     *
     * @param c The command.
     */
    public BonBonInvalidParameterException(String c) {
        super(createErrorMessage(c));
    }

    /**
     * Creates an error message which includes the correct format
     *
     * @param c The command.
     * @return The formatted error string.
     */
    private static String createErrorMessage(String c) {
        String format = Command.valueOf(c.toUpperCase()).getSyntax();
        return String.format("%s in the wrong format!\nThe correct format is:\n%s", c, format);
    }
}
