package augustus.parser;

import augustus.exception.AugustusException;

/**
 * Parses user input and extracts commands and command parameters
 */
public class Parser {

    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String FIND_COMMAND = "find";

    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_FROM_SEPARATOR = " /from ";
    private static final String EVENT_TO_SEPARATOR = " /to ";
    private static final String TAG_SEPARATOR = " /t ";

    /**
     * Prevents instantiation of the Parser class.
     */
    private Parser() {
    }

    /**
     * Returns the command word from the user's input
     *
     * @param input User input
     * @return the command word
     */
    public static String getCommand(String input) {
        return input.trim().split("\\s+")[0];
    }

    /**
     * Extracts the task number from a command
     *
     * @param input User input
     * @return The task number from the commands mark or unmark or delete
     * @throws AugustusException if the task number is missing or invalid
     */
    public static int parseTaskNumber(String input) throws AugustusException {
        String[] inputParts = input.trim().split("\\s+");
        if (inputParts.length < 2) {
            throw new AugustusException("A task number is required");
        }
        try {
            return Integer.parseInt(inputParts[1]);
        } catch (NumberFormatException e) {
            throw new AugustusException("Task number must be a number");
        }
    }

    /**
     * Extracts the description of todo task
     *
     * @param input User input containing todo command
     * @return the todo description
     * @throws AugustusException If the description is empty
     */
    public static String parseTodo(String input) throws AugustusException {
        String description = input.substring(TODO_COMMAND.length()).trim();
        if (description.isEmpty()) {
            throw new AugustusException("You cannot enter the empire without a description");
        }
        return description;
    }

    /**
     * Parses a deadline command into its description and due date
     *
     * @param input User input containing the deadline command
     * @return an array containing description and the due date
     * @throws AugustusException if the deadline format is invalid
     */
    public static String[] parseDeadline(String input) throws AugustusException {
        int index = input.indexOf(DEADLINE_SEPARATOR);
        if (index == -1) {
            throw new AugustusException("A deadline must contain /by followed by the date");
        }

        String description =
                input.substring(DEADLINE_COMMAND.length(), index).trim();
        String date =
                input.substring(index + DEADLINE_SEPARATOR.length()).trim();

        if (description.isEmpty()) {
            throw new AugustusException("The deadline must have a description");
        }
        if (date.isEmpty()) {
            throw new AugustusException("Specify when the deadline is due");
        }
        return new String[]{description, date};
    }

    /**
     * Parses an event command into its description, start time and end time.
     *
     * @param input User input containing event command
     * @return An array containing the description, start time and end time.
     * @throws AugustusException if the event format is invalid
     */
    public static String[] parseEvent(String input) throws AugustusException {
        int fromIndex = input.indexOf(EVENT_FROM_SEPARATOR);
        int toIndex = input.indexOf(EVENT_TO_SEPARATOR);

        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new AugustusException("The message should include /from and /to");
        }
        int fromStart = fromIndex + EVENT_FROM_SEPARATOR.length();
        if (fromStart > toIndex) {
            throw new AugustusException("Write when this event starts and ends");
        }

        String description =
                input.substring(EVENT_COMMAND.length(), fromIndex).trim();
        String from =
                input.substring(fromStart, toIndex).trim();
        String to =
                input.substring(toIndex + EVENT_TO_SEPARATOR.length()).trim();

        if (description.isEmpty()) {
            throw new AugustusException("The event must have a description");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new AugustusException("Write when this event starts and ends");
        }

        return new String[]{description, from, to};
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param input User input containing the find command.
     * @return Keyword used to search for tasks.
     * @throws AugustusException If the keyword is empty.
     */
    public static String parseFind(String input) throws AugustusException {
        String keyword = input.substring(FIND_COMMAND.length()).trim();

        if (keyword.isEmpty()) {
            throw new AugustusException("A keyword is required");
        }

        return keyword;
    }

    /**
     * Extracts the tag from a tag command.
     *
     * @param input User input containing the tag command.
     * @return Tag to assign to the task.
     * @throws AugustusException If the tag command format is invalid.
     */
    public static String parseTag(String input) throws AugustusException {
        int index = input.indexOf(TAG_SEPARATOR);

        if (index == -1) {
            throw new AugustusException("Tag must have following format: tag TASK_NUMBER /t TAG");
        }

        String tag =
                input.substring(index + TAG_SEPARATOR.length()).trim();

        if (tag.isEmpty()) {
            throw new AugustusException("A tag description is required");
        }

        return tag;
    }


}
