package chattingheads.exception;

/**
 * Represents an error caused by one or more missing or invalid inputs.
 */
public class InvalidInputException extends ChattingHeadsException {

    private InvalidInputException(String message) {
        super(message);
    }

    /**
     * Creates an InvalidInputException for one or more invalid or missing inputs.
     * Date-time inputs are shown with the expected date-time format.
     *
     * @param inputs Names of the invalid or missing inputs.
     * @return InvalidInputException with a message describing the invalid inputs.
     */
    public static InvalidInputException invalidInput(String... inputs) {
        StringBuilder message = new StringBuilder("And you may ask yourself");
        for (String input : inputs) {
            if (input.equals("deadline") || input.equals("start") || input.equals("end")) {
                message.append(String.format("\n\"Where is that %s? (dd/MM/yyyy HH:mm)\"", input));
            } else {
                message.append(String.format("\n\"Where is that %s?\"", input));
            }
        }
        return new InvalidInputException(message.toString());
    }

    /**
     * Creates an InvalidInputException for an invalid event time range.
     *
     * @return InvalidInputException indicating that the end time is not after the start time.
     */
    public static InvalidInputException invalidTimeRange() {
        return new InvalidInputException(
                "End time not after start time\nTime isn't holding up\nTime isn't after us"
        );
    }

    /**
     * Creates an InvalidInputException for a duplicated command prefix.
     *
     * @param prefix The duplicated prefix.
     * @return InvalidInputException identifying the duplicated prefix.
     */
    public static InvalidInputException duplicatePrefix(String prefix) {
        return new InvalidInputException(
                String.format("Duplicate prefix: %s\nSay something once, why say it again?", prefix)
        );
    }

    /**
     * Creates an InvalidInputException for command prefixes that cannot be used together.
     *
     * @param prefixes The incompatible prefixes.
     * @return InvalidInputException identifying the incompatible prefixes.
     */
    public static InvalidInputException incompatiblePrefixes(String... prefixes) {
        return new InvalidInputException(
                "These prefixes cannot be used together: "
                        + String.join(", ", prefixes)
                        + "\nRun-a-run-a-run it all together\nCheck it out, still don't make no sense"
        );
    }

    /**
     * Creates an InvalidInputException for incorrect prefix orders.
     *
     * @param first  Prefix that should come first.
     * @param second Prefix that should come second.
     * @return InvalidInputException identifying the correct prefix order.
     */
    public static InvalidInputException invalidPrefixOrder(String first, String second) {
        return new InvalidInputException(
                String.format("%s must come before %s\nEveryone, get in line", first, second)
        );
    }

    /**
     * Creates an InvalidInputException for invalid syntax.
     *
     * @return InvalidInputException when there is an unexpected input.
     */
    public static InvalidInputException unexpectedInput() {
        return new InvalidInputException(
                """
                        Unexpected input in command
                        And you may ask yourself
                        "Well, how did that get there?"
                        """
        );
    }
}
