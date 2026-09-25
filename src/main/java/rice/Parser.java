package rice;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles command parsing, date parsing, and saved task reconstruction.
 */
public class Parser {
    //Expected input for date-time format
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd");

    /**
     * Reconstructs task objects from saved delimiter-separated records.
     *
     * @param lines saved task records
     * @return parsed tasks
     */
    public List<Task> parseTasks(List<String> lines) {
        List<Task> result = new ArrayList<>();
        for (String line : lines) {
            if (line == null || line.isBlank()) {
                continue;
            }

            Task task = parseStoredTask(line);
            if (task != null) {
                result.add(task);
            }
        }
        return result;
    }

    /**
     * Parses one saved task record and skips malformed entries.
     *
     * @param line saved task record
     * @return parsed task, or null if the record is malformed
     */
    private Task parseStoredTask(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length < 3) {
            return null;
        }

        try {
            Task task = switch (fields[0]) {
                case "T" -> fields.length == 3 ? new Todo(fields[2]) : null;
                case "D" -> fields.length == 4 ? new Deadline(fields[2], fields[3]) : null;
                case "E" -> fields.length == 5 ? new Event(fields[2], fields[3], fields[4]) : null;
                default -> null;
            };

            if (task != null && "1".equals(fields[1])) {
                task.mark();
            }
            return task;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Parses a user-provided date in yyyy-MM-dd format.
     *
     * @param date user input date
     * @return parsed date
     */
    public static LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date. Use yyyy-MM-dd, for example 2026-12-12.");
        }
    }

    /**
     * Formats a date for displaying to the user.
     *
     * @param date date to format
     * @return formatted date
     */
    public static String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("MMM dd yyyy"));
    }

    /**
     * Creates a task from a complete user command.
     *
     * @param input full command entered by the user
     * @return created task
     * @throws RiceException if the command is missing required details
     */
    public Task createTask(String input) throws RiceException {
        String[] commandParts = splitCommand(input);
        validateDescription(commandParts[1]);

        String command = commandParts[0];
        String body = commandParts[1];

        return switch (command) {
            case "todo" -> createTodo(body);
            case "deadline" -> createDeadline(body);
            case "event" -> createEvent(body);
            default -> throw new RiceException("Unknown task command, I want more rice");
        };
    }

    private String[] splitCommand(String input) throws RiceException {
        String[] parts = input.split(" ", 2);
        if (parts.length < 2) {
            throw new RiceException("Task description cannot be empty");
        }
        return parts;
    }

    private void validateDescription(String description) throws RiceException {
        if (description == null || description.isBlank()) {
            throw new RiceException("Task description cannot be empty");
        }
    }

    private Task createTodo(String body) {
        return new Todo(body.trim());
    }

    /**
     * Parses the description and date in a deadline command.
     */
    private Task createDeadline(String body) throws RiceException {
        String[] parts = body.split("/", 2);
        if (parts.length != 2 || !parts[1].trim().startsWith("by ")) {
            throw new RiceException("Use: deadline description /by yyyy-MM-dd");
        }
        String description = parts[0].trim();
        String dueDate = parts[1].trim().substring(3).trim();
        return new Deadline(description, dueDate);
    }

    /**
     * Parses the description and dates in an event command.
     */
    private Task createEvent(String body) throws RiceException {
        String[] parts = body.split("/", 3);
        if (parts.length != 3 || !parts[1].trim().startsWith("from ") || !parts[2].trim().startsWith("to ")) {
            throw new RiceException("Use: event description /from date /to date");
        }
        String description = parts[0].trim();
        String startDate = parts[1].trim().substring(5).trim();
        String endDate = parts[2].trim().substring(3).trim();
        return new Event(description, startDate, endDate);
    }
}
