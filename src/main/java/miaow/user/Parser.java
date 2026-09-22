package miaow.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import miaow.task.Deadline;
import miaow.task.Event;
import miaow.task.Task;

/**
 * parser class
 */
public class Parser {
    /**
     * command types
     */
    public enum CommandType {
        BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, UNKNOWN,
                SORT, MIAOW, INVALID_TODO, INVALID_DEADLINE, INVALID_EVENT
    }

    /**
     * Sorts input into the enum Commandtype
     * @param input String input.
     * @return CommandType.
     */
    public CommandType getCommandType(String input) {
        if (input == null) {
            return CommandType.UNKNOWN;
        }

        input = input.trim();

        if (input.equals("bye")) {
            return CommandType.BYE;
        } else if (input.equals("list")) {
            return CommandType.LIST;
        } else if (input.startsWith("mark ")) {
            return CommandType.MARK;
        } else if (input.startsWith("unmark ")) {
            return CommandType.UNMARK;
        } else if (input.startsWith("delete ")) {
            return CommandType.DELETE;
        } else if (input.equals("todo") || input.startsWith("todo ")) {
            return CommandType.TODO;
        } else if (input.equals("deadline") || input.startsWith("deadline ")) {
            return CommandType.DEADLINE;
        } else if (input.equals("event") || input.startsWith("event ")) {
            return CommandType.EVENT;
        } else if (input.equals("find") || input.startsWith("find ")) {
            return CommandType.FIND;
        } else if (input.equals("sort")) {
            return CommandType.SORT;
        } else if (input.equals("miaow")) {
            return CommandType.MIAOW;
        } else {
            return CommandType.UNKNOWN;
        }
    }

    /**
     * Creates task with name input
     * @param input Name of task
     * @return a new task
     */
    public Task parseTodo(String input) {
        if (input == null) {
            return null;
        }

        String cleanedInput = input.trim();

        if (!cleanedInput.matches("todo\\s+.+")) {
            return null;
        }

        String description = cleanedInput.substring(4).trim();

        if (description.isEmpty()) {
            return null;
        }

        return new Task(description);
    }

    /**
     * Creates deadline
     * @param input Name of deadline task + deadline date
     * @return a new deadline
     */
    public Task parseDeadline(String input) {
        if (input == null) {
            return null;
        }

        String cleanedInput = input.trim();

        if (!cleanedInput.matches("deadline\\s+.+")) {
            return null;
        }

        String content = cleanedInput.substring(8).trim();
        String[] parts = content.split("\\s+/by\\s+", -1);

        if (parts.length != 2
                || parts[0].trim().isEmpty()
                || parts[1].trim().isEmpty()
                || parts[1].contains("/by")) {
            return null;
        }

        try {
            Deadline deadline = new Deadline(parts[0].trim());
            deadline.by(parts[1].trim());
            return deadline;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * create a event
     * @param input name of event, start date, end date
     * @return a new event
     */
    public Task parseEvent(String input) {
        if (input == null || !input.startsWith("event ")) {
            return null;
        }
        String content = input.substring(6).trim();
        String[] parts = content.split(" /from | /to ", 3);
        if (parts.length < 3 || parts[0].trim().isEmpty()
                || parts[1].trim().isEmpty() || parts[2].trim().isEmpty()) {
            return null;
        }
        Event event = new Event(parts[0].trim());
        try {
            event.from(parts[1].trim());
            event.to(parts[2].trim());
            return event;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        dateStr = dateStr.trim();

        try {
            DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("uuuu-MM-dd")
                    .withResolverStyle(ResolverStyle.STRICT);

            return LocalDate.parse(dateStr, formatter);
        } catch (DateTimeParseException e) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter
                        .ofPattern("d/M/uuuu")
                        .withResolverStyle(ResolverStyle.STRICT);

                return LocalDate.parse(dateStr, formatter);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }

    /**
     * gives number to task
     * @param input name of task?
     * @param commandPrefix type of task
     * @return number of task
     */
    public int parseTaskNumber(String input, String commandPrefix) {
        try {
            String numberStr = input.substring(commandPrefix.length()).trim();
            return Integer.parseInt(numberStr) - 1; // Convert to 0-based index
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            return -1;
        }
    }

    private static boolean isValidTaskNumber(int taskNumber, int taskCount) {
        return taskNumber >= 0 && taskNumber < taskCount;
    }

    /**
     * turns string into LocalDateTime
     * @param dateTimeStr input date
     * @return a LocalDateTime
     */
    public LocalDateTime parseDateTime(String dateTimeStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (DateTimeParseException e1) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy HHmm");
                return LocalDateTime.parse(dateTimeStr, formatter);
            } catch (DateTimeParseException e2) {
                try {
                    LocalDate date = LocalDate.parse(dateTimeStr);
                    return date.atStartOfDay();
                } catch (DateTimeParseException e3) {
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
                        LocalDate date = LocalDate.parse(dateTimeStr, formatter);
                        return date.atStartOfDay();
                    } catch (DateTimeParseException e4) {
                        return null;
                    }
                }
            }
        }
    }

    /**
     * Extracts the date from "deadlines on" or "events on" commands
     * @param input The full command string
     * @param prefix The command prefix (e.g., "deadlines on ")
     * @return The date string or null if empty
     */
    public String extractDateString(String input, String prefix) {
        if (!input.startsWith(prefix)) {
            return null;
        }
        String dateStr = input.substring(prefix.length()).trim();
        return dateStr.isEmpty() ? null : dateStr;
    }

    /**
     * Turns input into a String to search
     * @param input Input string
     * @return String used for searching
     */
    public String parseFindKeyword(String input) {
        if (!input.startsWith("find")) {
            return null;
        }

        String keyword = input.substring(4).trim();
        return keyword.isEmpty() ? null : keyword;
    }
}
