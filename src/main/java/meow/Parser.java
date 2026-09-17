package meow;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses user commands and converts them into tasks.
 */
public class Parser {

    /**
     * Parses a Todo command into a Todo task.
     *
     * @param input the full user command
     * @return the Todo task represented by the command
     * @throws MeowException if the command is invalid
     */
    private Task parseTodo(String input) throws MeowException {
        assert input.startsWith("todo ") : "parseTodo expects a todo command";
        String content = input.substring(5).trim();

        if (content.isBlank()) {
            throw new MeowException("Meow! A todo needs a description.");
        }
        validateDescription(content);

        return new Todo(content);
    }

    /**
     * Parses a Deadline command into a Deadline task.
     *
     * @param input the full user command
     * @return the Deadline task represented by the command
     * @throws MeowException if the command is invalid
     */
    private Task parseDeadline(String input) throws MeowException {
        assert input.startsWith("deadline ") : "parseDeadline expects a deadline command";
        String content = input.substring(9);
        if (content.isBlank()) {
            throw new MeowException("Meow! A deadline needs a description and a /by date.");
        }

        int byIndex = getUniqueParameterIndex(content, "/by");
        if (byIndex == -1) {
            throw new MeowException("Meow! A deadline needs a /by date.");
        }

        String description = content.substring(0, byIndex).trim();
        String by = content.substring(byIndex + "/by".length()).trim();

        if (description.isBlank()) {
            throw new MeowException("Meow! A deadline needs a description.");
        }
        validateDescription(description);
        if (by.isBlank()) {
            throw new MeowException("Meow! A deadline needs a /by date.");
        }

        LocalDate dueDate = parseDate(
                by,
                "Meow! Please enter a valid date in yyyy-MM-dd format.");

        return new Deadline(description, dueDate);
    }

    /**
     * Parses an Event command into an Event task.
     *
     * @param input the full user command
     * @return the Event task represented by the command
     * @throws MeowException if the command is invalid
     */
    private Task parseEvent(String input) throws MeowException {
        assert input.startsWith("event ")
                : "parseEvent expects an event command";

        String content = input.substring(6).trim();
        if (content.isBlank()) {
            throw new MeowException(
                    "Meow! An event needs a description, a /from date and a /to date.");
        }

        int fromIndex = getUniqueParameterIndex(content, "/from");
        int toIndex = getUniqueParameterIndex(content, "/to");
        if (fromIndex == -1) {
            throw new MeowException("Meow! An event needs a /from date.");
        }
        if (toIndex == -1) {
            throw new MeowException("Meow! An event needs a /to date.");
        }
        if (toIndex < fromIndex) {
            throw new MeowException(
                    "Meow! Please place /from before /to.");
        }

        String description = content.substring(0, fromIndex).trim();
        String from = content.substring(
                fromIndex + "/from".length(), toIndex).trim();
        String to = content.substring(
                toIndex + "/to".length()).trim();

        if (description.isBlank()) {
            throw new MeowException("Meow! An event needs a description.");
        }
        validateDescription(description);
        if (from.isBlank()) {
            throw new MeowException("Meow! An event needs a /from date.");
        }
        if (to.isBlank()) {
            throw new MeowException("Meow! An event needs a /to date.");
        }

        String dateErrorMessage =
                "Meow! Please enter valid event dates in yyyy-MM-dd format.";

        LocalDate fromDate = parseDate(from, dateErrorMessage);
        LocalDate toDate = parseDate(to, dateErrorMessage);

        validateEventDates(fromDate, toDate);

        return new Event(description, fromDate, toDate);
    }

    /**
     * Parses a user command into the corresponding task.
     *
     * @param input the full user command
     * @return the task represented by the command
     * @throws MeowException if the command is invalid
     */
    public Task parseTask(String input) throws MeowException {
        if (input.startsWith("todo ")) {
            return parseTodo(input);
        }
        if (input.startsWith("deadline ")) {
            return parseDeadline(input);
        }
        if (input.startsWith("event ")) {
            return parseEvent(input);
        }
        throw new MeowException("Meow! I'm sorry, but I don't know what that means.");
    }

    /**
     * Extracts the keyword from a find command.
     *
     * @param input the full user command.
     * @return the keyword to search for.
     * @throws MeowException if no keyword is provided.
     */
    public String parseFindKeyword(String input) throws MeowException {
        String[] parts = input.split("\\s+", 2);

        if (parts.length < 2 || parts[1].isBlank()) {
            throw new MeowException(
                    "Meow! I need a keyword to sniff out those tasks.");
        }

        return parts[1].trim();
    }

    /**
     * Finds the index of a parameter that appears exactly once as a separate token.
     *
     * @param content the command content to search
     * @param parameter the parameter to find
     * @return the index of the parameter, or -1 if it is not present
     * @throws MeowException if the parameter appears more than once
     */
    private int getUniqueParameterIndex(String content, String parameter)
            throws MeowException {
        Pattern pattern = Pattern.compile(
                "(?<!\\S)" + Pattern.quote(parameter) + "(?!\\S)");
        Matcher matcher = pattern.matcher(content);

        if (!matcher.find()) {
            return -1;
        }

        int parameterIndex = matcher.start();

        if (matcher.find()) {
            throw new MeowException(
                    "Meow! Please specify " + parameter + " only once.");
        }

        return parameterIndex;
    }

    /**
     * Checks that a task description does not contain reserved characters.
     *
     * @param description the task description to validate
     * @throws MeowException if the description contains a reserved character
     */
    private void validateDescription(String description) throws MeowException {
        if (description.contains("|")) {
            throw new MeowException(
                    "Meow! Task descriptions cannot contain the '|' character.");
        }
    }

    /**
     * Parses a date from the given text.
     *
     * @param date the date text to parse
     * @param errorMessage the message to show if parsing fails
     * @return the parsed date
     * @throws MeowException if the date is invalid
     */
    private LocalDate parseDate(String date, String errorMessage)
            throws MeowException {
        try {
            return LocalDate.parse(date);
        } catch (DateTimeParseException e) {
            throw new MeowException(errorMessage);
        }
    }

    /**
     * Checks that an event does not end before it starts.
     *
     * @param fromDate the event start date
     * @param toDate the event end date
     * @throws MeowException if the event ends before it starts
     */
    private void validateEventDates(LocalDate fromDate, LocalDate toDate)
            throws MeowException {
        if (toDate.isBefore(fromDate)) {
            throw new MeowException(
                    "Meow! An event cannot end before it starts.");
        }
    }
}
