package zsiggy;

/**
 * Parses user input into values used by the application.
 */
public class Parser {

    /**
     * Extracts a task number from a user command and converts it
     * into a zero-based task index.
     *
     * @param input the full user input command
     * @param commandLength the length of the command prefix
     * @return the zero-based task index
     * @throws ZsiggyException if the task number is not a valid integer
     */
    public static int parseTaskNumber(
            String input,
            int commandLength
    ) throws ZsiggyException {

        try {
            int taskNumber =
                    Integer.parseInt(
                            input.substring(commandLength)
                    );

            return taskNumber - 1;

        } catch (NumberFormatException e) {
            throw new ZsiggyException(
                    "Give me a proper task number."
            );
        }
    }
}
