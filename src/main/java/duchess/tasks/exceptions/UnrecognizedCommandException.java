package duchess.tasks.exceptions;

/** Represents commands that do not follow a recognised command format. */
public class UnrecognizedCommandException extends TaskException {
    private UnrecognizedCommandException(String message) {
        super(message);
    }

    /** Creates a generic unrecognised-command exception. */
    public static UnrecognizedCommandException declare(String command) {
        return new UnrecognizedCommandException(String.format(
                "I do not recognise that command:\n  %s\n"
                        + "You may consult the commands I have graciously made available.",
                command));
    }

    /** Creates an exception with the expected format and an example command. */
    public static UnrecognizedCommandException declareWithFormatAndExample(
            String command, String correctFormat, String exampleCommand) {
        return new UnrecognizedCommandException(String.format(
                "That command is improperly formed: %s\n"
                + "Use the prescribed format: %s\n"
                + "Example: %s",
                command, correctFormat, exampleCommand));
    }
}
