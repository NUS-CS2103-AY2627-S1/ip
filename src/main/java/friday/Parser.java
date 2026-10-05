package friday;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import friday.task.Deadline;
import friday.task.Event;
import friday.task.RepeatFrequency;
import friday.task.Task;
import friday.task.Todo;

/**
 * Parses user input and saved task lines.
 */
public class Parser {
    private static final String DEADLINE_USAGE = "deadline <description> /by <date and time>";
    private static final String EVENT_USAGE = "event <description> /from <date and time> /to <date and time>";

    /**
     * Returns the first word of the user input as the command.
     *
     * @param input full user input
     * @return command word from the input
     */
    public static String getCommand(String input) {
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            return "";
        }
        return trimmedInput.split("\\s+", 2)[0];
    }

    /**
     * Returns everything after the command word.
     *
     * @param input full user input
     * @return details after the command word, or an empty string if there are none
     */
    public static String getDetails(String input) {
        String[] parts = input.trim().split("\\s+", 2);
        return parts.length > 1 ? parts[1] : "";
    }

    /**
     * Converts a one-based task number from user input to a zero-based index.
     *
     * @param details user input containing the task number
     * @return zero-based task index
     * @throws FridayException if the task number is not a valid integer
     */
    public static int parseTaskNumber(String details) throws FridayException {
        try {
            return Integer.parseInt(details.trim()) - 1;
        } catch (NumberFormatException e) {
            throw new FridayException("Apologies, please give me a valid task number sir");
        }
    }

    /**
     * Returns the keyword to search for.
     *
     * @param details user input containing the keyword
     * @return keyword to search for
     * @throws FridayException if the keyword is blank
     */
    public static String parseKeyword(String details) throws FridayException {
        String keyword = details.trim();
        if (keyword.isBlank()) {
            throw new FridayException("Apologies, please give me a keyword to find sir");
        }
        return keyword;
    }

    /**
     * Converts the task number in a repeat command to a zero-based index.
     *
     * @param details repeat command details
     * @return zero-based task index
     * @throws FridayException if the task number is not a valid integer
     */
    public static int parseRepeatTaskNumber(String details) throws FridayException {
        return parseTaskNumber(splitRepeatDetails(details)[0]);
    }

    /**
     * Returns the repeat frequency from a repeat command.
     *
     * @param details repeat command details
     * @return repeat frequency
     * @throws FridayException if the frequency is missing or unsupported
     */
    public static RepeatFrequency parseRepeatFrequency(String details) throws FridayException {
        return RepeatFrequency.parse(splitRepeatDetails(details)[1]);
    }

    /**
     * Creates a task from the user's command and command details.
     *
     * @param command command word from the user input
     * @param details details after the command word
     * @return task created from the command
     * @throws FridayException if the command or task details are invalid
     */
    public static Task parseTask(String command, String details) throws FridayException {
        validateDescription(details);
        if (command.equals("todo")) {
            return parseTodo(details);
        }

        if (command.equals("deadline")) {
            return parseDeadline(details);
        }

        if (command.equals("event")) {
            return parseEvent(details);
        }

        throw new FridayException("Apologies i have no clue what that means");
    }

    /**
     * Creates a task from one line in the save file.
     *
     * @param line line from the save file
     * @return task created from the saved line
     * @throws FridayException if the saved line cannot be parsed
     */
    public static Task parseSavedTask(String line) throws FridayException {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            throw new FridayException("Invalid saved task format");
        }

        validateSavedStatus(parts[1]);
        if (parts[2].isBlank()) {
            throw new FridayException("Saved task description cannot be blank");
        }

        Task task;
        String taskType = parts[0];
        String description = parts[2];
        if (taskType.equals("T") && hasExpectedSavedFields(parts, 3)) {
            task = new Todo(description);
        } else if (taskType.equals("D") && hasExpectedSavedFields(parts, 4)) {
            task = new Deadline(description, parts[3]);
        } else if (taskType.equals("E") && hasExpectedSavedFields(parts, 5)) {
            task = new Event(description, parts[3], parts[4]);
        } else {
            throw new FridayException("Invalid saved task type or field count");
        }

        if (parts[1].equals("1")) {
            task.markAsDone();
        }
        applySavedRepeatFrequency(task, parts);
        return task;
    }

    private static Task parseTodo(String details) throws FridayException {
        String description = details.trim();
        if (description.isBlank()) {
            throw new FridayException("Apologies, todo cannot have an empty description sir");
        }
        return new Todo(description);
    }

    private static Task parseDeadline(String details) throws FridayException {
        String[] deadlineParts = splitDetails(details, "/by", DEADLINE_USAGE);
        if (deadlineParts[0].isBlank()) {
            throw new FridayException("Apologies, deadline cannot have an empty description sir");
        }
        return new Deadline(deadlineParts[0], deadlineParts[1]);
    }

    private static Task parseEvent(String details) throws FridayException {
        String[] eventParts = splitDetails(details, "/from", EVENT_USAGE);
        if (eventParts[0].isBlank()) {
            throw new FridayException("Apologies, event cannot have an empty description sir");
        }

        String[] timeParts = splitDetails(eventParts[1], "/to", EVENT_USAGE);
        return new Event(eventParts[0], timeParts[0], timeParts[1]);
    }

    private static String[] splitDetails(String details, String marker, String usage) throws FridayException {
        Pattern markerPattern = Pattern.compile("(?<!\\S)" + Pattern.quote(marker) + "(?!\\S)");
        Matcher matcher = markerPattern.matcher(details);
        if (!matcher.find()) {
            throw new FridayException("Apologies, please use: " + usage);
        }

        int markerStart = matcher.start();
        int markerEnd = matcher.end();
        if (matcher.find()) {
            throw new FridayException("Apologies, " + marker + " can only be specified once sir");
        }

        String description = details.substring(0, markerStart).trim();
        String dateOrTime = details.substring(markerEnd).trim();
        if (dateOrTime.isBlank()) {
            throw new FridayException("Apologies, please provide a value after " + marker + " sir");
        }
        return new String[] { description, dateOrTime };
    }

    private static String[] splitRepeatDetails(String details) throws FridayException {
        String[] parts = details.trim().split("\\s+", 2);
        if (parts.length < 2 || parts[0].isBlank() || parts[1].isBlank()) {
            throw new FridayException("Apologies, please use repeat <task number> <frequency> sir");
        }
        return parts;
    }

    private static void applySavedRepeatFrequency(Task task, String[] parts) throws FridayException {
        int baseFields = task instanceof Event ? 5 : task instanceof Deadline ? 4 : 3;
        if (parts.length == baseFields) {
            return;
        }
        String repeatPrefix = "repeat:";
        String possibleRepeatFrequency = parts[parts.length - 1];
        if (possibleRepeatFrequency.startsWith(repeatPrefix)) {
            task.setRepeatFrequency(RepeatFrequency.parse(possibleRepeatFrequency.substring(repeatPrefix.length())));
        }
    }

    private static boolean hasExpectedSavedFields(String[] parts, int requiredFieldCount) {
        return parts.length == requiredFieldCount
                || parts.length == requiredFieldCount + 1 && parts[parts.length - 1].startsWith("repeat:");
    }

    private static void validateSavedStatus(String status) throws FridayException {
        if (!status.equals("0") && !status.equals("1")) {
            throw new FridayException("Saved task status must be 0 or 1");
        }
    }

    /**
     * Rejects characters reserved by the line-based save format.
     *
     * @param description task text to validate
     * @throws FridayException if reserved characters are present
     */
    public static void validateDescription(String description) throws FridayException {
        if (description.contains("|") || description.contains("\n") || description.contains("\r")) {
            throw new FridayException("Please do not use pipe (|) characters or line breaks in task descriptions.");
        }
    }
}
