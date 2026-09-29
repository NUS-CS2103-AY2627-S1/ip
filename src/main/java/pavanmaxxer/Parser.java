package pavanmaxxer;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses supported Pavanmaxxer commands and their arguments.
 */
public final class Parser {
    private Parser() {
    }

    /**
     * Identifies the command represented by the input.
     *
     * @param input Raw user input.
     * @return Matching command, or {@link Command#UNKNOWN}.
     */
    public static Command parseCommand(String input) {
        return Command.from(input);
    }

    /**
     * Creates a task from a task-creation command.
     *
     * @param input Full command entered by the user.
     * @param command Classified task-creation command.
     * @return Parsed task.
     * @throws PavanmaxxerException If a required field or valid date is missing.
     */
    public static Task parseTask(String input, Command command)
            throws PavanmaxxerException {
        if (command == Command.TODO) {
            String description = input.substring("todo".length()).trim();
            if (description.isEmpty()) {
                throw new PavanmaxxerException(
                        "The description of a todo cannot be empty.");
            }
            validateStorageSafe(description);
            return new Todo(description);
        }

        if (command == Command.DEADLINE) {
            String arguments = input.substring("deadline".length()).trim();
            int byCount = countField(arguments, "/by");
            int byIndex = findField(arguments, "/by");
            if (arguments.isEmpty() || byIndex == 0) {
                throw new PavanmaxxerException(
                        "The description of a deadline cannot be empty.");
            }
            if (byCount == 0) {
                throw new PavanmaxxerException("A deadline needs a /by date.");
            }
            if (byCount > 1) {
                throw new PavanmaxxerException(
                        "A deadline must contain exactly one /by field.");
            }
            String description = arguments.substring(0, byIndex).trim();
            String byText = arguments.substring(byIndex + 3).trim();
            if (description.isEmpty()) {
                throw new PavanmaxxerException(
                        "The description of a deadline cannot be empty.");
            }
            if (byText.isEmpty()) {
                throw new PavanmaxxerException("A deadline needs a /by date.");
            }
            validateStorageSafe(description, byText);
            try {
                return new Deadline(description, LocalDate.parse(byText));
            } catch (DateTimeParseException exception) {
                throw new PavanmaxxerException(
                        "Use yyyy-MM-dd for deadline dates.");
            }
        }

        if (command == Command.EVENT) {
            String arguments = input.substring("event".length()).trim();
            int fromCount = countField(arguments, "/from");
            int toCount = countField(arguments, "/to");
            int fromIndex = findField(arguments, "/from");
            int firstToIndex = findField(arguments, "/to");
            if (arguments.isEmpty() || fromIndex == 0) {
                throw new PavanmaxxerException(
                        "The description of an event cannot be empty.");
            }
            if (fromCount == 0) {
                throw new PavanmaxxerException("An event needs a /from time.");
            }
            if (toCount == 0) {
                throw new PavanmaxxerException("An event needs a /to time.");
            }
            if (fromCount > 1 || toCount > 1) {
                throw new PavanmaxxerException(
                        "An event must contain exactly one /from and one /to field.");
            }
            if (firstToIndex < fromIndex) {
                throw new PavanmaxxerException(
                        "Use event DESCRIPTION /from START /to END.");
            }
            String description = arguments.substring(0, fromIndex).trim();
            String fromAndTo = arguments.substring(fromIndex + 5).trim();
            int toIndex = findField(fromAndTo, "/to");
            if (description.isEmpty()) {
                throw new PavanmaxxerException(
                        "The description of an event cannot be empty.");
            }
            if (toIndex < 0) {
                throw new PavanmaxxerException("An event needs a /to time.");
            }
            String from = fromAndTo.substring(0, toIndex).trim();
            String to = fromAndTo.substring(toIndex + 3).trim();
            if (from.isEmpty()) {
                throw new PavanmaxxerException("An event needs a /from time.");
            }
            if (to.isEmpty()) {
                throw new PavanmaxxerException("An event needs a /to time.");
            }
            validateStorageSafe(description, from, to);
            return new Event(description, from, to);
        }

        throw new PavanmaxxerException("That command does not create a task.");
    }

    private static void validateStorageSafe(String... values)
            throws PavanmaxxerException {
        for (String value : values) {
            if (value.contains("|")) {
                throw new PavanmaxxerException(
                        "Task details cannot contain the | character.");
            }
        }
    }

    private static int countField(String text, String field) {
        Matcher matcher = fieldPattern(field).matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private static int findField(String text, String field) {
        Matcher matcher = fieldPattern(field).matcher(text);
        if (!matcher.find()) {
            return -1;
        }
        return matcher.start() + matcher.group(1).length();
    }

    private static Pattern fieldPattern(String field) {
        return Pattern.compile("(^|\\s)" + Pattern.quote(field)
                + "(?=\\s|$)");
    }

    /**
     * Converts a one-based task number into a valid list index.
     *
     * @param input Full command entered by the user.
     * @param commandWord Command whose argument is being parsed.
     * @param taskListSize Current number of tasks.
     * @return Zero-based task index.
     * @throws PavanmaxxerException If the index is missing or invalid.
     */
    public static int parseTaskIndex(String input, String commandWord,
            int taskListSize) throws PavanmaxxerException {
        String taskNumberText = input.substring(commandWord.length()).trim();
        if (taskNumberText.isEmpty()) {
            throw new PavanmaxxerException(
                    "Please provide a task number to " + commandWord + ".");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new PavanmaxxerException("The task number must be an integer.");
        }

        if (taskNumber < 1 || taskNumber > taskListSize) {
            throw new PavanmaxxerException("That task number does not exist.");
        }
        return taskNumber - 1;
    }

    /**
     * Extracts the required keyword from a find command.
     *
     * @param input Full command entered by the user.
     * @return Non-empty search keyword.
     * @throws PavanmaxxerException If the keyword is missing.
     */
    public static String parseFindKeyword(String input)
            throws PavanmaxxerException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new PavanmaxxerException("Please provide a keyword to find.");
        }
        return keyword;
    }
}
