package bo.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import bo.Tracker;
import bo.command.CommandException;
import bo.task.Deadline;
import bo.task.Event;
import bo.task.Todo;

/**
 * Interprets user commands and performs the requested task operation.
 */
public class Parser {
    private static final DateTimeFormatter EVENT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");

    /**
     * Validates and performs one user command.
     *
     * @param tracker The tracker used to manage tasks.
     * @param command The command entered by the user.
     * @throws CommandException If the command is incomplete or unknown.
     */
    public void executeCommand(Tracker tracker, String command) throws CommandException {
        if (command.isEmpty()) {
            throw new CommandException("Please enter a command. Type an action such as todo or list.");
        }

        String commandName = command.split("\\s+", 2)[0];
        switch (commandName) {
        case "list":
            if (command.equals("list")) {
                tracker.printTasks();
                return;
            }
            break;
        case "upcoming":
            if (command.equals("upcoming")) {
                tracker.printUpcomingDeadlines(LocalDate.now());
                return;
            }
            throw new CommandException("Use upcoming without extra text to see future deadlines.");
        case "due":
            showDeadlinesDueOn(tracker, getArguments(command, "due"));
            return;
        case "todo":
            addTodo(tracker, command);
            return;
        case "deadline":
            addDeadline(tracker, command);
            return;
        case "event":
            addEvent(tracker, command);
            return;
        case "mark":
            changeTaskStatus(tracker, getArguments(command, "mark"), true);
            return;
        case "unmark":
            changeTaskStatus(tracker, getArguments(command, "unmark"), false);
            return;
        case "delete":
            deleteTask(tracker, getArguments(command, "delete"));
            return;
        case "find":
            findTasks(tracker, getArguments(command, "find"));
            return;
        case "snooze":
            snoozeTask(tracker, getArguments(command, "snooze"));
            return;
        case "reschedule":
            rescheduleTask(tracker, getArguments(command, "reschedule"));
            return;
        default:
            break;
        }
        throw new CommandException(getCommandInstructions());
    }

    /**
     * Adds a to-do task after validating its description.
     *
     * @param tracker The tracker used to manage tasks.
     * @param command The to-do command entered by the user.
     * @throws CommandException If the description is missing or saving fails.
     */
    private void addTodo(Tracker tracker, String command) throws CommandException {
        String description = getArguments(command, "todo");
        if (description.isEmpty()) {
            throw new CommandException("A todo needs a description. Use: todo <description>");
        }
        tracker.addTask(new Todo(description));
    }

    /**
     * Postpones a deadline after validating the task number and duration.
     *
     * @param tracker The tracker used to update the deadline.
     * @param arguments The task number and postponement duration.
     * @throws CommandException If the arguments are invalid.
     */
    private void snoozeTask(Tracker tracker, String arguments) throws CommandException {
        String[] parts = arguments.split("\\s+");
        if (parts.length != 2) {
            throw new CommandException("Use snooze <task number> <number of days>d.");
        }
        if (!parts[1].matches("[1-9][0-9]*d")) {
            throw new CommandException("Snooze duration must be a positive number of days. "
                    + "Use: snooze <task number> <number of days>d");
        }
        int taskNumber = parseTaskNumber(tracker, parts[0],
                "Use snooze <task number> <number of days>d.");
        int days;
        try {
            days = Integer.parseInt(parts[1].substring(0, parts[1].length() - 1));
        } catch (NumberFormatException exception) {
            throw new CommandException("Snooze duration is too large. Use: snooze <task number> <number of days>d");
        }
        tracker.snoozeTask(taskNumber, days);
    }

    /**
     * Reschedules a deadline after validating its task number and new date.
     *
     * @param tracker The tracker used to update the deadline.
     * @param arguments The task number and replacement date.
     * @throws CommandException If the arguments are invalid.
     */
    private void rescheduleTask(Tracker tracker, String arguments) throws CommandException {
        String[] parts = arguments.split("\\s+");
        if (parts.length != 2) {
            throw new CommandException("A reschedule needs a task number and date. "
                    + "Use: reschedule <task number> <yyyy-MM-dd>");
        }
        int taskNumber = parseTaskNumber(tracker, parts[0],
                "Use reschedule <task number> <yyyy-MM-dd>.");
        LocalDate newDate = parseDate(parts[1], "reschedule <task number> <yyyy-MM-dd>");
        if (newDate.isBefore(LocalDate.now())) {
            throw new CommandException("A deadline cannot be rescheduled to a past date.");
        }
        tracker.rescheduleTask(taskNumber, newDate);
    }

    /**
     * Checks whether an input starts with a complete command word.
     *
     * @param input The trimmed user input.
     * @param commandName The command word to check.
     * @return True if the input is the command or has arguments after it.
     */
    private boolean isCommand(String input, String commandName) {
        return input.equals(commandName)
                || (input.startsWith(commandName)
                        && input.length() > commandName.length()
                        && Character.isWhitespace(input.charAt(commandName.length())));
    }

    /**
     * Returns the trimmed text following a command word.
     *
     * @param input The complete user input.
     * @param commandName The command word at the start of the input.
     * @return The command arguments, or an empty string when none were supplied.
     */
    private String getArguments(String input, String commandName) {
        assert isCommand(input, commandName)
                : "Arguments can only be extracted from a matching command";
        return input.substring(commandName.length()).trim();
    }

    /**
     * Adds a validated deadline task.
     *
     * @param tracker The tracker used to manage tasks.
     * @param command The deadline command entered by the user.
     * @throws CommandException If the command is incomplete.
     */
    private void addDeadline(Tracker tracker, String command) throws CommandException {
        String details = getArguments(command, "deadline");
        int byIndex = details.indexOf("/by");
        if (byIndex < 0) {
            throw new CommandException("A deadline needs /by information. "
                    + "Use: deadline <description> /by <yyyy-MM-dd>");
        }

        String description = details.substring(0, byIndex).trim();
        String by = details.substring(byIndex + 3).trim();
        if (description.isEmpty() || by.isEmpty()) {
            throw new CommandException("A deadline needs both a description and a time. "
                    + "Use: deadline <description> /by <yyyy-MM-dd>");
        }
        LocalDate dueDate = parseDate(by, "deadline <description> /by <yyyy-MM-dd>");
        tracker.addTask(new Deadline(description, dueDate));
    }

    /**
     * Displays deadlines that fall on a date supplied by the user.
     *
     * @param tracker The tracker used to manage tasks.
     * @param dateText The date entered after the due command.
     * @throws CommandException If no valid date is supplied.
     */
    private void showDeadlinesDueOn(Tracker tracker, String dateText) throws CommandException {
        LocalDate date = parseDate(dateText, "due <yyyy-MM-dd>");
        tracker.printDeadlinesDueOn(date);
    }

    /**
     * Parses a date entered in ISO date format.
     *
     * @param dateText The date text to parse.
     * @param usage The correct command usage to show if parsing fails.
     * @return The parsed date.
     * @throws CommandException If the date is blank or invalid.
     */
    private LocalDate parseDate(String dateText, String usage) throws CommandException {
        if (dateText.isEmpty()) {
            throw new CommandException("A date is required. Use: " + usage);
        }
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw new CommandException("Dates must use yyyy-MM-dd. Use: " + usage);
        }
    }

    /**
     * Adds a validated event task.
     *
     * @param tracker The tracker used to manage tasks.
     * @param command The event command entered by the user.
     * @throws CommandException If the command is incomplete.
     */
    private void addEvent(Tracker tracker, String command) throws CommandException {
        String details = getArguments(command, "event");
        int fromIndex = details.indexOf("/from");
        int toIndex = details.indexOf("/to");
        if (fromIndex < 0 || toIndex < 0 || fromIndex >= toIndex) {
            throw new CommandException("An event needs /from and /to information. "
                    + "Use: event <description> /from <yyyy-MM-dd HH:mm> "
                    + "/to <yyyy-MM-dd HH:mm>");
        }

        String description = details.substring(0, fromIndex).trim();
        String from = details.substring(fromIndex + 5, toIndex).trim();
        String to = details.substring(toIndex + 3).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new CommandException("An event needs a description, start, and end date and time. "
                    + "Use: event <description> /from <yyyy-MM-dd HH:mm> "
                    + "/to <yyyy-MM-dd HH:mm>");
        }
        LocalDateTime startDateTime = parseEventDateTime(from);
        LocalDateTime endDateTime = parseEventDateTime(to);
        if (!startDateTime.isBefore(endDateTime)) {
            throw new CommandException("An event's end must be after its start.");
        }
        tracker.addTask(new Event(description, startDateTime, endDateTime));
    }

    /**
     * Parses an event date and time in the format accepted by Bo.
     *
     * @param dateTimeText The event date and time text.
     * @return The parsed event date and time.
     * @throws CommandException If the date and time format is invalid.
     */
    private LocalDateTime parseEventDateTime(String dateTimeText) throws CommandException {
        try {
            return LocalDateTime.parse(dateTimeText, EVENT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException exception) {
            throw new CommandException("Event dates and times must use yyyy-MM-dd HH:mm.");
        }
    }

    /**
     * Changes a numbered task's completion status.
     *
     * @param tracker The tracker used to manage tasks.
     * @param taskNumberText The task number entered by the user.
     * @param shouldMarkDone Whether the task should be marked as complete.
     * @throws CommandException If the task number is invalid.
     */
    private void changeTaskStatus(Tracker tracker, String taskNumberText, boolean shouldMarkDone)
            throws CommandException {
        int taskNumber = parseTaskNumber(tracker, taskNumberText,
                "Use mark <task number> or unmark <task number>.");
        if (shouldMarkDone) {
            tracker.markTask(taskNumber);
        } else {
            tracker.unmarkTask(taskNumber);
        }
    }

    /**
     * Deletes a numbered task after checking that its number is valid.
     *
     * @param tracker The tracker used to manage tasks.
     * @param taskNumberText The task number entered by the user.
     * @throws CommandException If the task number is invalid.
     */
    private void deleteTask(Tracker tracker, String taskNumberText) throws CommandException {
        int taskNumber = parseTaskNumber(tracker, taskNumberText,
                "Use delete <task number> to remove a task.");
        tracker.deleteTask(taskNumber);
    }

    /**
     * Parses and validates a user-supplied one-based task number.
     *
     * @param tracker The tracker used to validate the task number.
     * @param taskNumberText The task number entered by the user.
     * @param formatMessage The error message for non-numeric input.
     * @return The validated task number.
     * @throws CommandException If the text is not numeric or refers to no task.
     */
    private int parseTaskNumber(Tracker tracker, String taskNumberText, String formatMessage)
            throws CommandException {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            if (!tracker.isValidTaskNumber(taskNumber)) {
                throw new CommandException("That task number does not exist. Use list to see your tasks.");
            }
            return taskNumber;
        } catch (NumberFormatException exception) {
            throw new CommandException(formatMessage);
        }
    }

    /**
     * Displays tasks matching a keyword.
     *
     * @param tracker the tracker used to manage tasks
     * @param keyword the keyword entered by the user
     * @throws CommandException if no keyword is supplied
     */
    private void findTasks(Tracker tracker, String keyword) throws CommandException {
        if (keyword.isEmpty()) {
            throw new CommandException("A find needs a keyword. Use: find <keyword>");
        }
        tracker.findTasks(keyword);
    }

    /**
     * Returns the commands that Bo understands.
     *
     * @return A multi-line command guide.
     */
    private String getCommandInstructions() {
        return "I don't recognize that command. Try one of these:\n"
                + "todo <description>\n"
                + "deadline <description> /by <yyyy-MM-dd>\n"
                + "event <description> /from <yyyy-MM-dd HH:mm> /to <yyyy-MM-dd HH:mm>\n"
                + "list\n"
                + "find <keyword>\n"
                + "upcoming\n"
                + "due <yyyy-MM-dd>\n"
                + "mark <task number>\n"
                + "unmark <task number>\n"
                + "delete <task number>\n"
                + "snooze <task number> <number of days>d\n"
                + "reschedule <task number> <yyyy-MM-dd>\n"
                + "bye";
    }
}
