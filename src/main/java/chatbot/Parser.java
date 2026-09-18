package chatbot;

/**
 * Parses user input strings to extract commands and command arguments.
 */
public class Parser {
    private static final String WHITESPACE_REGEX = "\\s+";
    private static final String DEADLINE_MARKER = "/by";
    private static final String EVENT_START_MARKER = "/from";
    private static final String EVENT_END_MARKER = "/to";

    /**
     * Extracts the command from the beginning of the input string.
     *
     * @param input Input string from user.
     * @return Command by user.
     */
    public static String getCommand(String input) {
        return splitCommandAndArgument(input)[0];
    }

    /**
     * Retrieves task number to be marked/unmarked from input string (1-based).
     *
     * @param input Input string from user.
     * @return Task number of task to be marked/unmarked.
     */
    public static int getMarkOrUnmarkTaskNumber(String input) {
        String[] parsedInput = splitCommandAndArgument(input);
        try {
            if (parsedInput.length < 2) {
                throw new NumberFormatException();
            }
            return Integer.parseInt(parsedInput[1].trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Bro you gotta key in a number after the mark/unmark command.");
        }
    }

    /**
     * Retrieves task number to be deleted from user's input string (1-based).
     *
     * @param input Input string from user.
     * @return Task number of task to be deleted.
     */
    public static int getDeleteTaskNumber(String input) {
        String[] parsedInput = splitCommandAndArgument(input);
        if (parsedInput.length < 2 || parsedInput[1].trim().isEmpty()) {
            throw new InvalidInputException("Heyo! The description of a delete cannot be empty, and/or the task number "
                + "must be more than 0. Please try again, including the task number of the task you want to delete "
                + "after the delete command");
        }
        try {
            return Integer.parseInt(parsedInput[1].trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("HEY! Please key in a number after the delete command.");
        }
    }

    /**
     * Retrieves the description from a todo command.
     *
     * @param input User input.
     * @return Description of todo.
     */
    public static String getTodoDescription(String input) {
        String[] parsedInput = splitCommandAndArgument(input);
        if (parsedInput.length < 2 || parsedInput[1].trim().isEmpty()) {
            throw new InvalidInputException("Heyo! The description of a todo cannot be empty. Please try again.");
        }
        return validateDescription(parsedInput[1]);
    }

    private static String[] getDeadlineParts(String input) {
        String[] commandParts = splitCommandAndArgument(input);
        if (commandParts.length < 2
                || !commandParts[1].contains(DEADLINE_MARKER)) {
            throw new InvalidInputException(
                "Eh bro you gotta give the deadline command in this format:\ndeadline <task desc> /by <due date>"
                    + " without angular brackets."
            );
        }

        String[] parts = commandParts[1].split(DEADLINE_MARKER, -1);
        if (parts.length != 2
                || parts[0].trim().isEmpty()
                || parts[1].trim().isEmpty()) {
            throw new InvalidInputException(
                "Eh bro you gotta give the deadline command in this format:\ndeadline <task desc> /by <due date>"
                    + " without angular brackets."
            );
        }
        return parts;
    }

    /**
     * Retrieves the description from a deadline command.
     *
     * @param input User input.
     * @return Description of deadline.
     */
    public static String getDeadlineDescription(String input) {
        return validateDescription(getDeadlineParts(input)[0]);
    }

    /**
     * Retrieves the deadline of a deadline instruction from user input.
     *
     * @param input User input.
     * @return The deadline of a deadline instruction as a string.
     */
    public static String getDeadline(String input) {
        return getDeadlineParts(input)[1].trim();
    }

    private static String[] getEventParts(String input) {
        String[] commandParts = splitCommandAndArgument(input);
        if (commandParts.length < 2
                || !commandParts[1].contains(EVENT_START_MARKER)
                || !commandParts[1].contains(EVENT_END_MARKER)) {
            throw new InvalidInputException(
                "Eh bro, you gotta provide a description, start and end date/time for the event command in this"
                    + "format:\nevent <task desc> /from <start> /to <end> without angular brackets."
            );
        }

        int fromIndex = commandParts[1].indexOf(EVENT_START_MARKER);
        int toIndex = commandParts[1].indexOf(EVENT_END_MARKER);

        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            throw new InvalidInputException(
                "Eh bro your /from must be before /to i.e.:\nevent <task desc> /from <start> /to <end>"
            );
        }

        String[] parts = commandParts[1]
            .split(EVENT_START_MARKER + "|" + EVENT_END_MARKER, -1);

        if (parts.length != 3
                || parts[0].trim().isEmpty()
                || parts[1].trim().isEmpty()
                || parts[2].trim().isEmpty()) {
            throw new InvalidInputException(
                "Eh bro, you gotta provide a description, start and end date/time for the event command in this"
                    + "format:\nevent <task desc> /from <start> /to <end> without angular brackets."
            );
        }

        return parts;
    }

    /**
     * Retrieves the description from an event command.
     *
     * @param input Input string of user.
     * @return Description of event.
     */
    public static String getEventDescription(String input) {
        return validateDescription(getEventParts(input)[0]);
    }

    /**
     * Retrieves the start time from an event command.
     *
     * @param input Input string of user.
     * @return Event start time as a String.
     */
    public static String getEventStart(String input) {
        return getEventParts(input)[1].trim();
    }

    /**
     * Retrieves the end time from an event command.
     *
     * @param input Input string of user.
     * @return Event end time as a String.
     */
    public static String getEventEnd(String input) {
        return getEventParts(input)[2].trim();
    }

    /**
     * Retrieves the date field from an {@code on} command.
     *
     * @param input Input string of user.
     * @return Date field of an "on" command instruction.
     */
    public static String getOnCommandDateText(String input) {
        String[] parsedInput = splitCommandAndArgument(input);
        if (parsedInput.length < 2) {
            throw new InvalidInputException("Heyo! You gotta provide a date in the format YYYY-MM-DD");
        }
        return parsedInput[1].trim();
    }

    /**
     * Retrieves the keyword from a find command.
     *
     * @param input Input string from user.
     * @return Keyword to search for.
     */
    public static String getFindKeyword(String input) {
        String[] parsedInput = splitCommandAndArgument(input);
        if (parsedInput.length < 2
                || parsedInput[1].trim().isEmpty()) {
            throw new InvalidInputException("Heyo! You gotta provide a keyword after the find command.");
        }
        return parsedInput[1].trim();
    }

    /**
     * Splits user input into a command and its optional argument.
     * Leading, trailing, and repeated whitespace is ignored.
     *
     * @param input Input string from the user.
     * @return An array containing the command and, when present, its argument.
     */
    private static String[] splitCommandAndArgument(String input) {
        if (input == null || input.isBlank()) {
            throw new InvalidInputException("Yo your input cannot be empty!");
        }

        return input.trim().split(WHITESPACE_REGEX, 2);
    }

    /**
     * Checks whether task description is empty or contains invalid characters.
     *
     * @param description Task description to check.
     * @return Trimmed description if description is valid, else throws an InvalidInputException.
     */
    private static String validateDescription(String description) {
        String trimmedDescription = description.trim();

        if (trimmedDescription.isEmpty()) {
            throw new InvalidInputException("Task descriptions cannot be empty.");
        }

        if (trimmedDescription.contains("|")) {
            throw new InvalidInputException(
                "Task descriptions cannot contain the | character."
            );
        }

        return trimmedDescription;
    }
}
