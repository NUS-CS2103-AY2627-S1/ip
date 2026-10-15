package jason;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jason.command.AddCommand;
import jason.command.Command;
import jason.command.CommandWords;
import jason.command.DeleteCommand;
import jason.command.ExitCommand;
import jason.command.FindCommand;
import jason.command.ListCommand;
import jason.command.MarkCommand;
import jason.command.UnmarkCommand;
import jason.exception.InvalidCommandException;
import jason.exception.InvalidDeadlineException;
import jason.exception.InvalidEventException;
import jason.exception.InvalidToDoException;
import jason.task.Deadline;
import jason.task.Event;
import jason.task.Task;
import jason.task.ToDo;

/**
 * Interprets user commands and creates tasks from add-task commands.
 */
public class Parser {
    private static final Pattern HOURS_PATTERN = Pattern.compile("(\\d+)h(?:\\s+(\\d+)m)?");
    private static final Pattern MINUTES_PATTERN = Pattern.compile("(\\d+)m");
    private static final int MAX_DURATION_MINUTES = 24 * 60;

    /**
     * Converts a complete user command into an executable command object.
     *
     * @param description complete command entered by the user.
     * @return executable command represented by the input.
     * @throws InvalidCommandException if the command is not recognized or is incomplete.
     * @throws InvalidToDoException if a todo command is malformed.
     * @throws InvalidDeadlineException if a deadline command is malformed.
     * @throws InvalidEventException if an event command is malformed.
     */
    public Command parse(String description)
            throws InvalidCommandException, InvalidToDoException, InvalidDeadlineException,
            InvalidEventException {
        if (description == null) {
            throw new InvalidCommandException();
        }
        String[] words = description.trim().split("\\s+");
        if (words.length == 0 || words[0].isEmpty()) {
            throw new InvalidCommandException();
        }
        return switch (words[0]) {
            case CommandWords.TODO, CommandWords.DEADLINE, CommandWords.EVENT
                    -> new AddCommand(parseTask(description));
            case CommandWords.LIST -> new ListCommand();
            case CommandWords.FIND -> new FindCommand(parseKeyword(description));
            case CommandWords.EXIT -> new ExitCommand();
            case CommandWords.MARK -> new MarkCommand(parseIndex(description));
            case CommandWords.UNMARK -> new UnmarkCommand(parseIndex(description));
            case CommandWords.DELETE -> new DeleteCommand(parseIndex(description));
            default -> throw new InvalidCommandException();
        };
    }

    /**
     * Returns the search keyword from a find command.
     */
    private String parseKeyword(String description) throws InvalidCommandException {
        String[] parts = description.trim().split("\\s+", 2);
        if (parts.length < 2 || parts[1].isBlank()) {
            throw new InvalidCommandException();
        }
        return parts[1].trim();
    }

    /**
     * Returns the task index from a mark, unmark, or delete command.
     *
     * @param description mark, unmark, or delete command.
     * @return one-based task index.
     * @throws IllegalArgumentException if the command does not contain a numeric index.
     */
    public int parseIndex(String description) {
        String[] words = description.trim().split("\\s+");
        if (words.length < 2) {
            throw new IllegalArgumentException("A task index is required.");
        }
        try {
            return Integer.parseInt(words[1]);
        } catch (NumberFormatException ignored) {
            throw new IllegalArgumentException("The task index must be a number.");
        }
    }

    /**
     * Creates the task represented by an add-task command.
     *
     * @param description complete add-task command.
     * @return task represented by the command.
     * @throws InvalidToDoException if the todo command is malformed.
     * @throws InvalidDeadlineException if the deadline command is malformed.
     * @throws InvalidEventException if the event command is malformed.
     */
    public Task parseTask(String description)
            throws InvalidToDoException, InvalidDeadlineException, InvalidEventException {
        String normalizedDescription = description.trim();
        String[] parsedInput = normalizedDescription.split("\\s+");
        if (parsedInput.length == 0) {
            throw new InvalidToDoException();
        }

        return switch (parsedInput[0]) {
            case CommandWords.TODO -> parseTodo(normalizedDescription, parsedInput);
            case CommandWords.DEADLINE -> parseDeadline(normalizedDescription, parsedInput);
            case CommandWords.EVENT -> parseEvent(normalizedDescription, parsedInput);
            default -> throw new InvalidToDoException();
        };
    }

    /**
     * Creates a todo task after validating its description.
     *
     * @param description complete todo command.
     * @param parsedInput command tokens.
     * @return parsed todo task.
     * @throws InvalidToDoException if the description is missing.
     */
    private Task parseTodo(String description, String[] parsedInput) throws InvalidToDoException {
        if (parsedInput.length < 2) {
            throw new InvalidToDoException();
        }
        int durationMarkerIndex = Arrays.asList(parsedInput).indexOf("/for");
        if (durationMarkerIndex < 0) {
            String[] parts = description.split("\\s+", 2);
            if (parts.length != 2 || parts[1].isBlank()) {
                throw new InvalidToDoException();
            }
            return new ToDo(parts[1].trim());
        }
        if (durationMarkerIndex < 2 || durationMarkerIndex == parsedInput.length - 1) {
            throw new InvalidToDoException();
        }

        String taskDescription = String.join(" ", Arrays.copyOfRange(parsedInput, 1,
                durationMarkerIndex));
        String durationText = String.join(" ", Arrays.copyOfRange(parsedInput,
                durationMarkerIndex + 1, parsedInput.length));
        return new ToDo(taskDescription, parseDuration(durationText));
    }

    /**
     * Parses a duration written in hours and minutes.
     *
     * @param durationText duration text from a todo command.
     * @return parsed duration.
     * @throws InvalidToDoException if the duration is malformed or outside the allowed range.
     */
    private Duration parseDuration(String durationText) throws InvalidToDoException {
        try {
            Matcher hoursMatcher = HOURS_PATTERN.matcher(durationText);
            Matcher minutesMatcher = MINUTES_PATTERN.matcher(durationText);
            long totalMinutes;
            if (hoursMatcher.matches()) {
                long hours = Long.parseLong(hoursMatcher.group(1));
                String minutePart = hoursMatcher.group(2);
                long minutes = minutePart == null ? 0 : Long.parseLong(minutePart);
                if (minutes > 59) {
                    throw new InvalidToDoException();
                }
                totalMinutes = Math.addExact(Math.multiplyExact(hours, 60), minutes);
            } else if (minutesMatcher.matches()) {
                totalMinutes = Long.parseLong(minutesMatcher.group(1));
            } else {
                throw new InvalidToDoException();
            }
            if (totalMinutes < 1 || totalMinutes > MAX_DURATION_MINUTES) {
                throw new InvalidToDoException();
            }
            return Duration.ofMinutes(totalMinutes);
        } catch (ArithmeticException | NumberFormatException e) {
            throw new InvalidToDoException();
        }
    }

    /**
     * Creates a deadline task after validating and parsing its date.
     *
     * @param description complete deadline command.
     * @param parsedInput command tokens.
     * @return parsed deadline task.
     * @throws InvalidDeadlineException if the command or date is invalid.
     */
    private Task parseDeadline(String description, String[] parsedInput)
            throws InvalidDeadlineException {
        if (parsedInput.length < 4 || !Arrays.asList(parsedInput).contains("/by")) {
            throw new InvalidDeadlineException();
        }
        String[] parts = description.split("deadline\\s+|\\s+/by\\s+", 3);
        if (parts.length != 3 || parts[1].isBlank() || parts[2].isBlank()) {
            throw new InvalidDeadlineException();
        }
        try {
            return new Deadline(parts[1], parts[2]);
        } catch (DateTimeParseException e) {
            throw new InvalidDeadlineException();
        }
    }

    /**
     * Creates an event task after validating and parsing its dates.
     *
     * @param description complete event command.
     * @param parsedInput command tokens.
     * @return parsed event task.
     * @throws InvalidEventException if the command or dates are invalid.
     */
    private Task parseEvent(String description, String[] parsedInput) throws InvalidEventException {
        List<String> words = Arrays.asList(parsedInput);
        if (parsedInput.length < 6 || !words.contains("/from") || !words.contains("/to")) {
            throw new InvalidEventException();
        }
        String[] parts = description.split("event\\s+|\\s+/from\\s+|\\s+/to\\s+", 4);
        if (parts.length != 4 || parts[1].isBlank() || parts[2].isBlank() || parts[3].isBlank()) {
            throw new InvalidEventException();
        }
        try {
            return new Event(parts[1], parts[2], parts[3]);
        } catch (DateTimeParseException e) {
            throw new InvalidEventException();
        }
    }

}
