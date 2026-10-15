package grug.command.parser;

import grug.command.GrugCommand;

/**
 * The base class for the (checked) exceptions thrown when there was an error
 * parsing the user input as a {@link GrugCommand}.
 *
 * Note that this is a checked exception, instead of a runtime exception, since
 * the caller trying to parse a user inputted command *should* expect that an
 * exception is very likely to occur.
 */
public class GrugCommandParserException extends Exception {
    /**
     * Creates a new GrugCommandParserException with the supplied {@code msg}.
     *
     * @param msg The parser error message.
     */
    public GrugCommandParserException(String msg) {
        super(msg);
    }

    /**
     * Represents the error state when an invalid argument is supplied to a
     * valid command.
     */
    public static final class InvalidArgument extends GrugCommandParserException {
        public InvalidArgument(String command, String reason) {
            super("Invalid argument(s) for `%s`: %s".formatted(command, reason));
        }
    }

    /**
     * Represents the error state when a command is used without providing required
     * arguments.
     */
    public static final class InvalidUsage extends GrugCommandParserException {
        public InvalidUsage(String usage) {
            super("Invalid usage. Proper usage: " + usage);
        }
    }

    /**
     * Represents the error state when an unknown command is supplied.
     */
    public static final class UnknownCommand extends GrugCommandParserException {
        public UnknownCommand(String command) {
            super("Unknown command `%s`".formatted(command));
        }
    }
}
