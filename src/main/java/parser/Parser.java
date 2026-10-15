package parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.Locale;

import command.Command;
import command.CommandWord;
import command.DeadlineCommand;
import command.DeleteCommand;
import command.EventCommand;
import command.ExitCommand;
import command.FindCommand;
import command.ListCommand;
import command.ListDateCommand;
import command.MarkCommand;
import command.SortCommand;
import command.TodoCommand;
import command.UnmarkCommand;
import exception.CbtException;
import task.Deadline;
import task.Event;
import task.Task;
import task.Todo;

/** Converts a raw user command into the command object that performs it. */
public class Parser {
    /** Supported formats for a date with a time. */
    private static final List<DateTimeFormatter> DATE_TIME_FORMATTERS = List.of(
            strictFormatter("d/M/uuuu HHmm"), // 2/12/2019 1800
            strictFormatter("d-M-uuuu HHmm"), // 2-12-2019 1800
            strictFormatter("uuuu-MM-dd HHmm"), // 2019-12-02 1800
            strictFormatter("d/M/uuuu HH:mm"), // 2/12/2019 18:00
            strictFormatter("d-M-uuuu HH:mm"), // 2-12-2019 18:00
            strictFormatter("uuuu-MM-dd HH:mm") // 2019-12-02 18:00
    );

    /** Supported formats for a date without a time. */
    private static final List<DateTimeFormatter> DATE_ONLY_FORMATTERS = List.of(
            strictFormatter("d/M/uuuu"), // 2/12/2019
            strictFormatter("d-M-uuuu"), // 2-12-2019
            strictFormatter("uuuu-MM-dd") // 2019-12-02
    );

    /** Format used to display dates stored in tasks. */
    private static final DateTimeFormatter DATE_PRINT_FORMATTER =
            DateTimeFormatter.ofPattern("MMM dd uuuu");

    /**
     * Parses one input line without changing the task list.
     *
     * @param fullCommand complete command entered by the user.
     * @return command object corresponding to the entered command word.
     * @throws CbtException if the command word is not recognized.
     */
    public static Command parseCommand(String fullCommand) throws CbtException {
        if (fullCommand == null || fullCommand.isBlank()) {
            throw new CbtException("Please enter a command.");
        }
        String normalizedCommand = fullCommand.strip().replaceAll("\\s+", " ");
        String[] parts = normalizedCommand.split(" ", 2);
        String keyword = parts[0].toUpperCase(Locale.ROOT);
        String arguments = parts.length == 2 ? parts[1].trim() : "";

        CommandWord commandWord;
        try {
            commandWord = CommandWord.valueOf(keyword);
        } catch (IllegalArgumentException exception) {
            commandWord = CommandWord.UNKNOWN;
        }

        return switch (commandWord) {
            case CommandWord.TODO -> new TodoCommand(arguments);
            case CommandWord.DEADLINE -> new DeadlineCommand(arguments);
            case CommandWord.EVENT -> new EventCommand(arguments);
            case CommandWord.DATE -> new ListDateCommand(arguments);
            case CommandWord.LISTDATE -> new ListDateCommand(arguments);
            case CommandWord.LIST -> {
                ensureNoArguments("list", arguments);
                yield new ListCommand();
            }
            case CommandWord.MARK -> new MarkCommand(arguments);
            case CommandWord.UNMARK -> new UnmarkCommand(arguments);
            case CommandWord.DELETE -> new DeleteCommand(arguments);
            case CommandWord.SORT -> new SortCommand(arguments);
            case CommandWord.BYE -> {
                ensureNoArguments("bye", arguments);
                yield new ExitCommand();
            }
            case CommandWord.FIND -> new FindCommand(arguments);
            default -> throw new CbtException("That command is off course. "
                    + "Try todo, deadline, event, date, list, listdate, find, mark, unmark, delete, sort, or bye.");
        };
    }

    /** Rejects unexpected arguments supplied to a command that accepts none. */
    private static void ensureNoArguments(String commandWord, String arguments) throws CbtException {
        if (!arguments.isEmpty()) {
            throw new CbtException("Use: " + commandWord);
        }
    }

    /**
     * Converts one stored task line to a task.
     *
     * @param line task serialized in the application's file format.
     * @return reconstructed task, or {@code null} if the stored data is malformed.
     */
    public static Task parseLineToTask(String line) {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0].toUpperCase(Locale.ROOT);
        if (!parts[1].equals("0") && !parts[1].equals("1")) {
            return null;
        }
        boolean isDone = parts[1].equals("1");
        String description = parts[2].trim();
        if (description.isBlank()) {
            return null;
        }

        CommandWord commandWord;
        try {
            commandWord = CommandWord.valueOf(type);
        } catch (IllegalArgumentException exception) {
            return null;
        }

        Task task = null;
        switch (commandWord) {
            case CommandWord.TODO:
                for (int i = 3; i < parts.length; i++) {
                    description += " | " + parts[i];
                }
                task = new Todo(description);
                break;
            case CommandWord.DEADLINE:
                for (int i = 3; i < parts.length - 1; i++) {
                    description += " | " + parts[i];
                }
                try {
                    task = new Deadline(description, parseLineToDate(parts[parts.length - 1]));
                } catch (CbtException exception) {
                    System.out.println("Invalid deadline date format stored in the task data file");
                }
                break;
            case CommandWord.EVENT:
                for (int i = 3; i < parts.length - 2; i++) {
                    description += " | " + parts[i];
                }
                try {
                    LocalDateTime startDate = parseLineToDate(parts[parts.length - 2]);
                    LocalDateTime endDate = parseLineToDate(parts[parts.length - 1]);
                    task = new Event(description, startDate, endDate);
                } catch (CbtException exception) {
                    System.out.println("Invalid event date format stored in the task data file");
                }
                break;
            default:
                break;
        }

        if (task != null) {
            task.restoreCompletionStatus(isDone);
        }
        return task;
    }

    /**
     * Parses a supported date or date-time input, using midnight for a date-only input.
     *
     * @param line date or date-time text to parse.
     * @return parsed date and time.
     * @throws CbtException if the text does not match a supported format.
     */
    public static LocalDateTime parseLineToDate(String line) throws CbtException {
        line = line.trim();
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return LocalDateTime.parse(line, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        for (DateTimeFormatter formatter : DATE_ONLY_FORMATTERS) {
            try {
                LocalDate date = LocalDate.parse(line, formatter);
                return date.atTime(0, 0);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new CbtException("Cannot recognize date/time! Example valid formats:\n"
                + "  - 2/12/2019 1800\n"
                + "  - 2-12-2019 1800\n"
                + "  - 2019-12-02 1800\n"
                + "  - 2/12/2019");
    }

    /**
     * Parses a date without accepting a time component.
     *
     * @param line date text to parse.
     * @return parsed date.
     * @throws CbtException if the text does not match a supported date format.
     */
    public static LocalDate parseDate(String line) throws CbtException {
        for (DateTimeFormatter formatter : DATE_ONLY_FORMATTERS) {
            try {
                return LocalDate.parse(line.trim(), formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported format.
            }
        }
        throw new CbtException("Cannot recognize date! Use a date such as 2019-12-02.");
    }

    /**
     * Formats a date for display.
     *
     * @param date date to format.
     * @return date formatted for user-facing output.
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_PRINT_FORMATTER);
    }

    /**
     * Creates a formatter that rejects invalid calendar dates instead of adjusting them.
     *
     * @param pattern date-time pattern accepted by the formatter.
     * @return strict date-time formatter.
     */
    private static DateTimeFormatter strictFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern).withResolverStyle(ResolverStyle.STRICT);
    }
}
