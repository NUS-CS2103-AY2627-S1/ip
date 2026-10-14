package baby;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Parses user commands into task details and task numbers.
 */
public class Parser {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HHmm").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Removes leading and trailing whitespace and collapses repeated whitespace.
     *
     * @param input The raw user input.
     * @return A normalized command, or an empty string for null input.
     */
    public static String normalizeInput(String input) {
        return input == null ? "" : input.trim().replaceAll("\\s+", " ");
    }

    /**
     * Gets the description part of a todo command.
     *
     * @param input The full user command.
     * @return The trimmed todo description.
     */
    public static String getTodoDescription(String input) {
        return input.substring(Command.TODO.getCommandWord().length()).trim();
    }

    /**
     * Creates a deadline from the user's deadline command.
     *
     * @param input The full user command.
     * @param ui The UI used to show parsing errors.
     * @return The parsed deadline, or null if the input is invalid.
     */
    public static Deadline createDeadline(String input, Ui ui) {
        String details = input.substring(Command.DEADLINE.getCommandWord().length()).trim();
        int byCount = countToken(details, "/by");
        if (byCount == 0) {
            ui.printError("Oh snow! A deadline needs a description and /by. Try: deadline return book /by Sunday");
            return null;
        } else if (byCount > 1) {
            ui.printError("Oh snow! A deadline can only have one /by parameter.");
            return null;
        }

        int byIndex = details.indexOf("/by");
        String description = details.substring(0, byIndex).trim();
        String byText = details.substring(byIndex + "/by".length()).trim();
        if (description.isEmpty()) {
            ui.printError("Oh snow! A deadline needs a description before /by.");
            return null;
        } else if (byText.isEmpty()) {
            ui.printError("Oh snow! A deadline needs a date or time after /by.");
            return null;
        }

        try {
            return new Deadline(description, parseDateTime(byText));
        } catch (DateTimeParseException e) {
            ui.printError("Oh snow! Please use the format yyyy-MM-dd HHmm.");
            return null;
        }
    }

    /**
     * Creates an event from the user's event command.
     *
     * @param input The full user command.
     * @param ui The UI used to show parsing errors.
     * @return The parsed event, or null if the input is invalid.
     */
    public static Event createEvent(String input, Ui ui) {
        String details = input.substring(Command.EVENT.getCommandWord().length()).trim();
        int fromCount = countToken(details, "/from");
        int toCount = countToken(details, "/to");
        if (fromCount == 0) {
            ui.printError("Oh snow! An event needs a description, /from, and /to. Try: "
                    + "event meeting /from Mon 2pm /to 4pm");
            return null;
        } else if (fromCount > 1) {
            ui.printError("Oh snow! An event can only have one /from parameter.");
            return null;
        } else if (toCount == 0) {
            ui.printError("Oh snow! An event needs an end time after /to.");
            return null;
        } else if (toCount > 1) {
            ui.printError("Oh snow! An event can only have one /to parameter.");
            return null;
        }

        int fromIndex = details.indexOf("/from");
        int toIndex = details.indexOf("/to");
        if (toIndex < fromIndex) {
            ui.printError("Oh snow! Please place /from before /to.");
            return null;
        }

        String description = details.substring(0, fromIndex).trim();
        String fromText = details.substring(fromIndex + "/from".length(), toIndex).trim();
        String toText = details.substring(toIndex + "/to".length()).trim();
        if (description.isEmpty()) {
            ui.printError("Oh snow! An event needs a description before /from.");
            return null;
        } else if (fromText.isEmpty()) {
            ui.printError("Oh snow! An event needs a start time after /from.");
            return null;
        } else if (toText.isEmpty()) {
            ui.printError("Oh snow! An event needs an end time after /to.");
            return null;
        }

        return createEventWithParsedTimes(description, fromText, toText, ui);
    }

    private static Event createEventWithParsedTimes(String description, String fromText, String toText, Ui ui) {
        try {
            LocalDateTime from = parseDateTime(fromText);
            LocalDateTime to = parseDateTime(toText);
            if (!to.isAfter(from)) {
                ui.printError("Oh snow! An event's end time must be after its start time.");
                return null;
            }

            return new Event(description, from, to);
        } catch (DateTimeParseException e) {
            ui.printError("Oh snow! Please use the format yyyy-MM-dd HHmm.");
            return null;
        }
    }

    private static int countToken(String text, String token) {
        int count = 0;
        for (String part : text.split(" ")) {
            if (part.equals(token)) {
                count++;
            }
        }
        return count;
    }

    static LocalDateTime parseDateTime(String text) {
        return LocalDateTime.parse(text, DATE_TIME_FORMATTER);
    }

    /**
     * Converts a 1-based task number from user input into a 0-based list index.
     *
     * @param input The full user command.
     * @param command The command word at the start of the input.
     * @param taskCount The number of tasks currently stored.
     * @param ui The UI used to show parsing errors.
     * @return The valid 0-based task index, or -1 if the input is invalid.
     */
    public static int getTaskIndex(String input, String command, int taskCount, Ui ui) {
        String taskNumberText = input.substring(command.length()).trim();

        if (taskNumberText.isEmpty()) {
            ui.printError("Oh snow! Please give me a task number to " + command + ". Try: " + command + " 1");
            return -1;
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(taskNumberText);
        } catch (NumberFormatException e) {
            ui.printError("Oh snow! Task numbers must be whole numbers. Try: " + command + " 1");
            return -1;
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            ui.printError("Oh snow! Task number " + taskNumber + " is not in your list.");
            return -1;
        }

        return taskNumber - 1;
    }
}
