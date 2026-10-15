package zeus.parser;

import java.time.LocalDate;

import zeus.command.AddCommand;
import zeus.command.Command;
import zeus.command.DeleteCommand;
import zeus.command.ExitCommand;
import zeus.command.FindCommand;
import zeus.command.HelpCommand;
import zeus.command.ListCommand;
import zeus.command.MarkCommand;
import zeus.command.UnmarkCommand;
import zeus.exception.ZeusException;
import zeus.task.Deadline;
import zeus.task.Event;
import zeus.task.Task;
import zeus.task.Todo;

/** Converts user input into executable commands. */
public final class Parser {
    /** Command word used to search task descriptions. */
    private static final String FIND_COMMAND = "find";

    /** Command word used to display guidance. */
    private static final String HELP_COMMAND = "help";

    /** Command word used to create a task without a date. */
    private static final String TODO_COMMAND = "todo";

    /** Command word used to create a task with a due date. */
    private static final String DEADLINE_COMMAND = "deadline";

    /** Command word used to create a task with start and end dates. */
    private static final String EVENT_COMMAND = "event";

    /** Separator preceding a deadline's due date. */
    private static final String BY_SEPARATOR = "/by";

    /** Separator preceding an event's start date. */
    private static final String FROM_SEPARATOR = "/from";

    /** Separator preceding an event's end date. */
    private static final String TO_SEPARATOR = "/to";

    /** Prevents creation of a stateless parser object. */
    private Parser() {
    }

    /**
     * Parses a full input line into the corresponding command object.
     * @param fullCommand command entered by the user
     * @return command ready to execute
     * @throws ZeusException if the command or its arguments are invalid
     */
    public static Command parse(String fullCommand) throws ZeusException {
        if (fullCommand.equals("bye")) {
            return new ExitCommand();
        } else if (fullCommand.equals("list")) {
            return new ListCommand();
        } else if (fullCommand.equals(HELP_COMMAND)
                || fullCommand.startsWith(HELP_COMMAND + " ")) {
            return parseHelpCommand(fullCommand);
        } else if (fullCommand.equals(FIND_COMMAND)
                || fullCommand.startsWith(FIND_COMMAND + " ")) {
            return parseFindCommand(fullCommand);
        } else if (isNumberedCommand(fullCommand, "mark")) {
            return new MarkCommand(parseTaskNumber(fullCommand, "mark"));
        } else if (isNumberedCommand(fullCommand, "unmark")) {
            return new UnmarkCommand(parseTaskNumber(fullCommand, "unmark"));
        } else if (isNumberedCommand(fullCommand, "delete")) {
            return new DeleteCommand(parseTaskNumber(fullCommand, "delete"));
        } else if (isTaskCommand(fullCommand)) {
            return new AddCommand(parseTask(fullCommand));
        }

        throw new ZeusException(
                "Even Zeus cannot understand that command. Type 'help' to see available commands."
        );
    }

    /**
     * Creates a help command when no arguments were supplied.
     *
     * @param fullCommand Full help command entered by the user.
     * @return Command that displays user guidance.
     * @throws ZeusException If the command contains arguments.
     */
    private static HelpCommand parseHelpCommand(String fullCommand) throws ZeusException {
        String arguments = fullCommand.substring(HELP_COMMAND.length()).trim();
        if (!arguments.isEmpty()) {
            throw new ZeusException(
                    "The help command does not take any arguments. Type 'help' on its own.");
        }
        return new HelpCommand();
    }

    /**
     * Creates a find command from a validated keyword.
     *
     * @param fullCommand Full find command entered by the user.
     * @return Command that searches task descriptions.
     * @throws ZeusException If the keyword is empty.
     */
    private static FindCommand parseFindCommand(String fullCommand) throws ZeusException {
        String keyword = fullCommand.substring(FIND_COMMAND.length()).trim();
        if (keyword.isEmpty()) {
            throw new ZeusException("Tell me what to find, for example 'find book'.");
        }
        return new FindCommand(keyword);
    }

    /*
     * Reports whether input begins with the specified numbered command word.
     * @param fullCommand full user input
     * @param commandWord command word to recognize
     * @return true when the word is the complete command name
     */
    private static boolean isNumberedCommand(String fullCommand, String commandWord) {
        return fullCommand.equals(commandWord) || fullCommand.startsWith(commandWord + " ");
    }

    /**
     * Reports whether input requests creation of a supported task type.
     * @param fullCommand full user input
     * @return true for todo, deadline, or event input
     */
    private static boolean isTaskCommand(String fullCommand) {
        return fullCommand.equals(TODO_COMMAND) || fullCommand.startsWith(TODO_COMMAND + " ")
                || fullCommand.equals(DEADLINE_COMMAND)
                || fullCommand.startsWith(DEADLINE_COMMAND + " ")
                || fullCommand.equals(EVENT_COMMAND)
                || fullCommand.startsWith(EVENT_COMMAND + " ");
    }

    /**
     * Converts a task-creation command into the corresponding task subtype.
     * @param fullCommand command entered by the user
     * @return task represented by the command
     * @throws ZeusException if required task details are missing or invalid
     */
    private static Task parseTask(String fullCommand) throws ZeusException {
        assert isTaskCommand(fullCommand)
        : "Only recognized task commands should reach task parsing.";

        if (fullCommand.equals(TODO_COMMAND)) {
            throw new ZeusException("A todo needs a description after 'todo'.");
        } else if (fullCommand.startsWith(TODO_COMMAND + " ")) {
            String description = fullCommand.substring(TODO_COMMAND.length()).trim();
            if (description.isEmpty()) {
                throw new ZeusException("A todo needs a description after 'todo'.");
            }
            return new Todo(description);
        } else if (fullCommand.equals(DEADLINE_COMMAND)) {
            throw new ZeusException("A deadline needs a description and '/by' date.");
        } else if (fullCommand.startsWith(DEADLINE_COMMAND + " ")) {
            return parseDeadline(fullCommand);
        } else if (fullCommand.equals(EVENT_COMMAND)) {
            throw new ZeusException(
                    "An event needs a description, '/from' start date, and '/to' end date.");
        } else if (fullCommand.startsWith(EVENT_COMMAND + " ")) {
            return parseEvent(fullCommand);
        }

        throw new IllegalStateException("Unsupported task command: " + fullCommand);
    }

    /**
     * Parses a deadline command after its command word has been recognized.
     * @param fullCommand full deadline command
     * @return parsed deadline
     * @throws ZeusException if its description or date is invalid
     */
    private static Deadline parseDeadline(String fullCommand) throws ZeusException {
        String taskDetails = fullCommand.substring(DEADLINE_COMMAND.length()).trim();
        int byIndex = taskDetails.indexOf(BY_SEPARATOR);
        if (byIndex < 0) {
            throw new ZeusException("A deadline needs a '/by' date.");
        }

        String description = taskDetails.substring(0, byIndex).trim();
        String by = taskDetails.substring(byIndex + BY_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new ZeusException("A deadline needs a description before '/by'.");
        } else if (by.isEmpty()) {
            throw new ZeusException("A deadline needs a date after '/by'.");
        }
        return new Deadline(description, TaskDateParser.parse(by, "deadline"));
    }

    /**
     * Parses an event command after its command word has been recognized.
     * @param fullCommand full event command
     * @return parsed event
     * @throws ZeusException if its description or dates are invalid
     */
    private static Event parseEvent(String fullCommand) throws ZeusException {
        String taskDetails = fullCommand.substring(EVENT_COMMAND.length()).trim();
        int fromIndex = taskDetails.indexOf(FROM_SEPARATOR);
        if (fromIndex < 0) {
            throw new ZeusException("An event needs a start date after '/from'.");
        }

        int toIndex = taskDetails.indexOf(
                TO_SEPARATOR, fromIndex + FROM_SEPARATOR.length());
        if (toIndex < 0) {
            throw new ZeusException("An event needs an end date after '/to'.");
        }

        String description = taskDetails.substring(0, fromIndex).trim();
        String from = taskDetails.substring(
                fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String to = taskDetails.substring(toIndex + TO_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new ZeusException("An event needs a description before '/from'.");
        } else if (from.isEmpty()) {
            throw new ZeusException("An event needs a start date after '/from'.");
        } else if (to.isEmpty()) {
            throw new ZeusException("An event needs an end date after '/to'.");
        }

        LocalDate fromDate = TaskDateParser.parse(from, "event start");
        LocalDate toDate = TaskDateParser.parse(to, "event end");
        TaskDateParser.validateEventDates(fromDate, toDate);
        return new Event(description, fromDate, toDate);
    }

    /**
     * Extracts the one-based task number from a numbered command.
     * @param fullCommand command entered by the user
     * @param commandWord command word being parsed
     * @return one-based task number
     * @throws ZeusException if the number is missing or malformed
     */
    private static int parseTaskNumber(String fullCommand, String commandWord)
            throws ZeusException {
        String numberText = fullCommand.substring(commandWord.length()).trim();
        if (numberText.isEmpty()) {
            throw new ZeusException("Tell me which task to " + commandWord + ", for example '"
                    + commandWord + " 1'.");
        }

        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new ZeusException("The task number must be a whole number.");
        }
    }
}
