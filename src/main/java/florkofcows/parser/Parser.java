package florkofcows.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import florkofcows.command.Command;
import florkofcows.command.CommandType;
import florkofcows.exception.FlorkingExceptions;

/**
 * Parser for incoming command lines.
 *
 * Converts raw input strings into Command instances. This class performs
 * validation and throws FlorkingExceptions for invalid user input.
 */
public class Parser {
    /**
     * Parses a full command line into a Command object.
     *
     * @param fullCommand the raw input string from the user.
     * @return a Command instance representing the parsed command.
     * @throws FlorkingExceptions if the input is invalid or unrecognized.
     */
    public static Command parse(String fullCommand) throws FlorkingExceptions {
        String trimmed = requireNonBlank(fullCommand, "What are you trying to say?");
        String[] words = trimmed.split("\\s+", 2);
        String commandWord = words[0].toUpperCase();
        CommandType commandType = parseCommandType(commandWord);

        switch (commandType) {
            case BYE:
                if (hasExtraArguments(words)) {
                    throw new FlorkingExceptions("Bye doesn't take extra arguments.");
                }
                return new Command.ExitCommand();
            case LIST:
                if (hasExtraArguments(words)) {
                    throw new FlorkingExceptions("List doesn't take extra arguments.");
                }
                return new Command.ListCommand();
            case ON:
                return new Command.OnDateCommand(parseDateArg(extractRemainingText(trimmed, commandWord)));
            case TODO:
                return new Command.AddTodoCommand(parseTodoDescription(trimmed));
            case DEADLINE:
                String[] deadlineParts = parseDeadlineParts(trimmed);
                return new Command.AddDeadlineCommand(deadlineParts[0], deadlineParts[1]);
            case EVENT:
                String[] eventParts = parseEventParts(trimmed);
                return new Command.AddEventCommand(eventParts[0], eventParts[1], eventParts[2]);
            case MARK:
                return new Command.MarkCommand(parseIndexArg(words, "mark"), true);
            case UNMARK:
                return new Command.MarkCommand(parseIndexArg(words, "unmark"), false);
            case DELETE:
                return new Command.DeleteCommand(parseIndexArg(words, "delete"));
            case FIND:
                return new Command.FindCommand(parseFindKeyword(trimmed));
            case TAG:
                return new Command.TagCommand(parseTagIndex(trimmed), parseTagValues(trimmed));
            default:
                throw new FlorkingExceptions("What are you trying to say?");
        }
    }

    private static boolean hasExtraArguments(String[] words) {
        return words.length > 1 && !words[1].trim().isEmpty();
    }

    private static String requireNonBlank(String value, String message) throws FlorkingExceptions {
        if (value == null || value.trim().isEmpty()) {
            throw new FlorkingExceptions(message);
        }
        return value.trim();
    }

    private static CommandType parseCommandType(String commandWord) throws FlorkingExceptions {
        try {
            return CommandType.valueOf(commandWord);
        } catch (IllegalArgumentException e) {
            throw new FlorkingExceptions("What are you trying to say?");
        }
    }

    private static String extractRemainingText(String line, String commandWord) {
        String remainder = line.substring(commandWord.length()).trim();
        return remainder;
    }

    private static int parseIndexArg(String[] words, String actionName) throws FlorkingExceptions {
        String argument = words.length > 1 ? words[1].trim() : "";
        if (argument.isEmpty()) {
            throw new FlorkingExceptions("Say properly which task you want " + actionName + ".");
        }
        if (argument.contains(" ")) {
            throw new FlorkingExceptions("Task number should be a single integer, like 'mark 2'.");
        }
        try {
            int index = Integer.parseInt(argument);
            if (index <= 0) {
                throw new FlorkingExceptions("Task number must be positive.");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new FlorkingExceptions("Eh, '" + argument + "' isn't a valid task number eh.");
        }
    }

    /**
     * Parses a date argument in the format yyyy-MM-dd.
     *
     * @param dateArg the raw date string from the user.
     * @return a LocalDate instance representing the parsed date.
     * @throws FlorkingExceptions if the input is missing or not a valid date.
     */
    public static LocalDate parseDateArg(String dateArg) throws FlorkingExceptions {
        if (dateArg == null || dateArg.trim().isEmpty()) {
            throw new FlorkingExceptions("Which date? Try: on 2019-12-02");
        }
        String normalizedDate = dateArg.trim();
        // Parsing should only proceed for a non-blank date string; otherwise the method would reject invalid input.
        assert !normalizedDate.isEmpty() : "Date string must not be blank after trimming";
        try {
            return LocalDate.parse(normalizedDate);
        } catch (DateTimeParseException e) {
            throw new FlorkingExceptions(
                    "'" + normalizedDate + "' isn't a valid date. Use yyyy-MM-dd, e.g. 2019-12-02.");
        }
    }

    /**
     * Parses the description for a todo command.
     *
     * @param line the full command line starting with "todo".
     * @return the trimmed description of the todo task.
     * @throws FlorkingExceptions if the description is missing or empty.
     */
    public static String parseTodoDescription(String line) throws FlorkingExceptions {
        String description = extractCommandArgument(line, "todo");
        if (description.isEmpty()) {
            throw new FlorkingExceptions("No todo description sia.");
        }
        return description;
    }

    /**
     * Parses the description and due date for a deadline command.
     *
     * @param line the full command line starting with "deadline".
     * @return an array containing the trimmed description and due date.
     * @throws FlorkingExceptions if the description or due date is missing or empty.
     */
    public static String[] parseDeadlineParts(String line) throws FlorkingExceptions {
        String remainder = extractCommandArgument(line, "deadline");
        if (remainder.isEmpty()) {
            throw new FlorkingExceptions("No deadline description sia.");
        }
        if (remainder.contains("/by") && !remainder.contains(" /by ")) {
            throw new FlorkingExceptions("Use ' /by ' with spaces, like: deadline return book /by Sunday.");
        }
        String[] parts = remainder.split(" /by ", 2);
        String deadlineDescription = parts[0].trim();
        if (deadlineDescription.isEmpty()) {
            throw new FlorkingExceptions("No deadline description sia.");
        }
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new FlorkingExceptions(
                    "Deadline needs a '/by' date/time one eh, like deadline return book /by Sunday.");
        }
        if (parts[1].trim().contains(" /by ")) {
            throw new FlorkingExceptions("Deadline can only have one '/by' value.");
        }
        return new String[] {deadlineDescription, parts[1].trim()};
    }

    /**
     * Parses the description, start time, and end time for an event command.
     *
     * @param line the full command line starting with "event".
     * @return an array containing the trimmed description, start time, and end time.
     * @throws FlorkingExceptions if the description, start time, or end time is missing or empty.
     */
    public static String[] parseEventParts(String line) throws FlorkingExceptions {
        String eventRemainder = extractCommandArgument(line, "event");
        if (eventRemainder.isEmpty()) {
            throw new FlorkingExceptions("No event description sia.");
        }
        if (eventRemainder.contains("/from") && !eventRemainder.contains(" /from ")) {
            throw new FlorkingExceptions("Use ' /from ' with spaces, like: event meeting /from Mon 2pm /to 4pm.");
        }
        String[] fromParts = eventRemainder.split(" /from ", 2);
        String eventDescription = fromParts[0].trim();
        if (eventDescription.isEmpty()) {
            throw new FlorkingExceptions("No event description sia.");
        }
        if (fromParts.length < 2 || fromParts[1].trim().isEmpty()) {
            throw new FlorkingExceptions(
                    "Event needs a '/from' time one eh, like event meeting /from Mon 2pm /to 4pm.");
        }
        String timeframe = fromParts[1].trim();
        if (timeframe.contains("/to") && !timeframe.contains(" /to ")) {
            throw new FlorkingExceptions("Use ' /to ' with spaces, like: event meeting /from Mon 2pm /to 4pm.");
        }
        String[] toParts = timeframe.split(" /to ", 2);
        String from = toParts[0].trim();
        if (toParts.length < 2 || toParts[1].trim().isEmpty()) {
            throw new FlorkingExceptions(
                    "Event needs a '/to' time one eh, like event meeting /from Mon 2pm /to 4pm.");
        }
        if (from.isEmpty() || toParts[1].trim().isEmpty()) {
            throw new FlorkingExceptions(
                    "Event needs a '/from' and '/to' time one eh, like event meeting /from Mon 2pm /to 4pm.");
        }
        String to = toParts[1].trim();
        if (to.contains(" /to ")) {
            throw new FlorkingExceptions("Event can only have one '/to' value.");
        }
        return new String[] {eventDescription, from, to};
    }

    /**
     * Parses the keyword for a find command.
     *
     * @param line the full command line starting with "find".
     * @return the trimmed keyword to search for.
     * @throws FlorkingExceptions if the keyword is missing or empty.
     */
    public static String parseFindKeyword(String line) throws FlorkingExceptions {
        String keyword = extractCommandArgument(line, "find");
        if (keyword.isEmpty()) {
            throw new FlorkingExceptions("What you want me find? Give me a keyword sia.");
        }
        if (keyword.contains(" ")) {
            return keyword;
        }
        return keyword;
    }

    /**
     * Parses the task index and tag list for a tag command.
     *
     * @param line the full command line starting with "tag".
     * @return the parsed task index.
     * @throws FlorkingExceptions if the arguments are missing or invalid.
     */
    public static int parseTagIndex(String line) throws FlorkingExceptions {
        String remainder = extractCommandArgument(line, "tag").trim();
        if (remainder.isEmpty()) {
            throw new FlorkingExceptions("Say properly which task you want tag eh.");
        }
        String[] tokens = remainder.split("\\s+");
        try {
            return Integer.parseInt(tokens[0]);
        } catch (NumberFormatException e) {
            throw new FlorkingExceptions("Oi, '" + tokens[0] + "' isn't a valid task number eh.");
        }
    }

    /**
     * Parses the tag tokens for a tag command.
     *
     * @param line the full command line starting with "tag".
     * @return the tag values to attach.
     * @throws FlorkingExceptions if no valid tags are provided.
     */
    public static String[] parseTagValues(String line) throws FlorkingExceptions {
        String remainder = extractCommandArgument(line, "tag").trim();
        if (remainder.isEmpty()) {
            throw new FlorkingExceptions("Use something like 'tag 2 #fun'");
        }
        String[] tokens = remainder.split("\\s+");
        if (tokens.length < 2) {
            throw new FlorkingExceptions("Use something like 'tag 2 #fun'");
        }
        String[] tags = new String[tokens.length - 1];
        for (int i = 1; i < tokens.length; i++) {
            String tag = tokens[i].trim();
            if (tag.isEmpty() || (!tag.startsWith("#") && tag.contains("#"))) {
                throw new FlorkingExceptions("Tags should look like #fun. Try: tag 2 #fun");
            }
            tags[i - 1] = tag;
        }
        return tags;
    }

    private static String extractCommandArgument(String line, String commandWord) {
        String trimmed = line.trim();
        if (trimmed.length() <= commandWord.length()) {
            return "";
        }
        return trimmed.substring(commandWord.length()).trim();
    }
}
