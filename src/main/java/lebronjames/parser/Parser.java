package lebronjames.parser;

import lebronjames.LebronJamesException;
import lebronjames.command.AddCommand;
import lebronjames.command.Command;
import lebronjames.command.DeleteCommand;
import lebronjames.command.ExitCommand;
import lebronjames.command.FindCommand;
import lebronjames.command.ListCommand;
import lebronjames.command.MarkCommand;
import lebronjames.command.OnCommand;
import lebronjames.task.Deadline;
import lebronjames.task.Event;
import lebronjames.task.TaskDateTime;
import lebronjames.task.Todo;

/**
 * Makes sense of the text the user types, turning it into a {@link Command}.
 *
 * <p>All the string handling lives here: finding the command word, splitting a
 * deadline at {@code /by}, checking that a task number is a whole number, and
 * complaining when any of that fails. The command classes therefore receive
 * ready-made values such as a {@link lebronjames.task.Task Task} or an {@code int}, and never have to
 * look at raw input.
 *
 * <p>The methods are static because parsing depends only on its input; a Parser
 * object would have nothing to remember between calls.
 */
public class Parser {
    /** Character reserved as the field separator inside the save file. */
    private static final String RESERVED_CHARACTER = "|";

    /**
     * Command words the chatbot understands.
     *
     * <p>Each is a constant rather than a literal because it is needed twice:
     * once to recognise the command, and again to measure how much of the line
     * to skip before the arguments begin.
     */
    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_ON = "on";
    private static final String COMMAND_FIND = "find";

    /** Keywords separating the parts of a deadline or an event. */
    private static final String BY_SEPARATOR = " /by ";
    private static final String FROM_SEPARATOR = " /from ";
    private static final String TO_SEPARATOR = " /to ";

    /**
     * Understands one line of user input.
     *
     * @param fullCommand Line typed by the user.
     * @return Command described by that line.
     * @throws LebronJamesException If the line is blank, unrecognised, or uses a
     *     recognised command word in the wrong format.
     */
    public static Command parse(String fullCommand) throws LebronJamesException {
        // A blank line is ordinary user input and is reported below, but null is
        // not something the user can type: Ui.readCommand() and the GUI text
        // field both always hand over a string.
        assert fullCommand != null : "A command line must not be null";

        if (fullCommand.equals(COMMAND_BYE)) {
            return new ExitCommand();
        }

        // The save file uses '|' to separate fields, so a task containing one
        // could not be read back correctly. Rejecting it early keeps the file valid.
        if (fullCommand.contains(RESERVED_CHARACTER)) {
            throw new LebronJamesException(
                    "AIRBALL. '|' is reserved for saving the playbook, so it cannot be used in a command.");
        }

        if (fullCommand.equals(COMMAND_LIST)) {
            return new ListCommand();
        }
        if (isCommand(fullCommand, COMMAND_MARK)) {
            return new MarkCommand(parseTaskNumber(fullCommand, COMMAND_MARK), true);
        }
        if (isCommand(fullCommand, COMMAND_UNMARK)) {
            return new MarkCommand(parseTaskNumber(fullCommand, COMMAND_UNMARK), false);
        }
        if (isCommand(fullCommand, COMMAND_DELETE)) {
            return new DeleteCommand(parseTaskNumber(fullCommand, COMMAND_DELETE));
        }
        if (isCommand(fullCommand, COMMAND_TODO)) {
            return new AddCommand(parseTodo(fullCommand));
        }
        if (isCommand(fullCommand, COMMAND_DEADLINE)) {
            return new AddCommand(parseDeadline(fullCommand));
        }
        if (isCommand(fullCommand, COMMAND_EVENT)) {
            return new AddCommand(parseEvent(fullCommand));
        }
        if (isCommand(fullCommand, COMMAND_ON)) {
            return parseOn(fullCommand);
        }
        if (isCommand(fullCommand, COMMAND_FIND)) {
            return parseFind(fullCommand);
        }
        if (fullCommand.isBlank()) {
            throw new LebronJamesException("AIRBALL. Call a play first.");
        }
        throw new LebronJamesException("AIRBALL. That play is not in my book.\n"
                + "Call your plays as todo, deadline, or event using the formats above.");
    }

    /**
     * Returns whether the input starts with the given command word.
     *
     * <p>The word must stand alone or be followed by a space, so that
     * {@code "todolist"} is not mistaken for a {@code todo} command.
     *
     * @param fullCommand Line typed by the user.
     * @param commandName Command word to look for.
     * @return Whether the line uses that command.
     */
    private static boolean isCommand(String fullCommand, String commandName) {
        return fullCommand.equals(commandName) || fullCommand.startsWith(commandName + " ");
    }

    /**
     * Returns the text following the command word, with spaces trimmed off.
     *
     * <p>Every caller has already established that the line uses this command
     * word, so the length of the word itself is exactly how much to skip.
     *
     * @param fullCommand Line typed by the user.
     * @param commandName Command word the line begins with.
     * @return Arguments given to the command, which may be empty.
     */
    private static String argumentsAfter(String fullCommand, String commandName) {
        return fullCommand.substring(commandName.length()).trim();
    }

    /**
     * Builds the to-do described by a {@code todo} command.
     *
     * @param fullCommand Line typed by the user.
     * @return To-do to add.
     * @throws LebronJamesException If no description was given.
     */
    private static Todo parseTodo(String fullCommand) throws LebronJamesException {
        // argumentsAfter skips exactly the command word, which is the right
        // amount only because parse() has already matched it.
        assert isCommand(fullCommand, COMMAND_TODO) : "parseTodo handles only todo commands";

        String description = argumentsAfter(fullCommand, COMMAND_TODO);
        if (description.isEmpty()) {
            throw new LebronJamesException("AIRBALL. A todo needs a description. Try: todo <task>");
        }
        return new Todo(description);
    }

    /**
     * Builds the deadline described by a {@code deadline} command.
     *
     * @param fullCommand Line typed by the user.
     * @return Deadline to add.
     * @throws LebronJamesException If {@code /by} is missing, or either part is empty.
     */
    private static Deadline parseDeadline(String fullCommand) throws LebronJamesException {
        // The description is read from the end of the command word onwards, and
        // no lower bound on byIndex is needed, both of which hold only because
        // parse() has already matched that word.
        assert isCommand(fullCommand, COMMAND_DEADLINE) : "parseDeadline handles only deadline commands";

        int byIndex = fullCommand.indexOf(BY_SEPARATOR);
        if (byIndex == -1) {
            throw new LebronJamesException("AIRBALL. Use this deadline format: deadline <task> /by <deadline>");
        }

        // The line always begins with the command word, so /by can never appear
        // before the description starts. No lower bound on byIndex is needed.
        String description = fullCommand.substring(COMMAND_DEADLINE.length(), byIndex).trim();
        String byText = fullCommand.substring(byIndex + BY_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new LebronJamesException("AIRBALL. A deadline needs a task description.");
        }
        if (byText.isEmpty()) {
            throw new LebronJamesException("AIRBALL. A deadline needs a date or time after /by.");
        }
        return new Deadline(description, TaskDateTime.parse(byText));
    }

    /**
     * Builds the event described by an {@code event} command.
     *
     * @param fullCommand Line typed by the user.
     * @return Event to add.
     * @throws LebronJamesException If {@code /from} or {@code /to} is missing, or any part is empty.
     */
    private static Event parseEvent(String fullCommand) throws LebronJamesException {
        // As in parseDeadline, reading the description from the end of the
        // command word is safe only because parse() matched that word first.
        assert isCommand(fullCommand, COMMAND_EVENT) : "parseEvent handles only event commands";

        int fromIndex = fullCommand.indexOf(FROM_SEPARATOR);
        int toIndex = fromIndex == -1
                ? -1
                : fullCommand.indexOf(TO_SEPARATOR, fromIndex + FROM_SEPARATOR.length());
        if (fromIndex == -1 || toIndex == -1) {
            throw new LebronJamesException("AIRBALL. Use this event format: event <task> /from <start> /to <end>");
        }

        String description = fullCommand.substring(COMMAND_EVENT.length(), fromIndex).trim();
        String fromText = fullCommand.substring(fromIndex + FROM_SEPARATOR.length(), toIndex).trim();
        String toText = fullCommand.substring(toIndex + TO_SEPARATOR.length()).trim();
        if (description.isEmpty()) {
            throw new LebronJamesException("AIRBALL. An event needs a description.");
        }
        if (fromText.isEmpty()) {
            throw new LebronJamesException("AIRBALL. An event needs a start date or time after /from.");
        }
        if (toText.isEmpty()) {
            throw new LebronJamesException("AIRBALL. An event needs an end date or time after /to.");
        }
        return new Event(description, TaskDateTime.parse(fromText), TaskDateTime.parse(toText));
    }

    /**
     * Builds the command described by an {@code on} command.
     *
     * @param fullCommand Line typed by the user.
     * @return Command that reports what is scheduled on the given date.
     * @throws LebronJamesException If the date is missing or not in an accepted format.
     */
    private static OnCommand parseOn(String fullCommand) throws LebronJamesException {
        assert isCommand(fullCommand, COMMAND_ON) : "parseOn handles only on commands";

        String dateText = argumentsAfter(fullCommand, COMMAND_ON);
        if (dateText.isEmpty()) {
            throw new LebronJamesException("AIRBALL. The on command needs a date. Try: on 2019-12-02");
        }
        return new OnCommand(TaskDateTime.parse(dateText).getDate());
    }

    /**
     * Builds the command described by a {@code find} command.
     *
     * @param fullCommand Line typed by the user.
     * @return Command that shows the tasks matching the keyword.
     * @throws LebronJamesException If no keyword was given.
     */
    private static FindCommand parseFind(String fullCommand) throws LebronJamesException {
        assert isCommand(fullCommand, COMMAND_FIND) : "parseFind handles only find commands";

        String keyword = argumentsAfter(fullCommand, COMMAND_FIND);
        if (keyword.isEmpty()) {
            throw new LebronJamesException("AIRBALL. The find command needs a keyword. Try: find book");
        }
        return new FindCommand(keyword);
    }

    /**
     * Extracts the one-based task number supplied with a command.
     *
     * <p>Only the text itself is checked here. Whether a task with that number
     * actually exists is decided by {@link lebronjames.task.TaskList TaskList}, which is the object that
     * knows how long the list is.
     *
     * @param fullCommand Line typed by the user.
     * @param commandName Command word preceding the task number.
     * @return Task number as typed by the user, counting from one.
     * @throws LebronJamesException If the task number is missing or not a whole number.
     */
    private static int parseTaskNumber(String fullCommand, String commandName) throws LebronJamesException {
        // This helper is told which command word to skip rather than knowing it,
        // so the caller must pass the one the line actually begins with.
        assert isCommand(fullCommand, commandName) : "The command word must match the line being parsed";

        String numberText = argumentsAfter(fullCommand, commandName);
        if (numberText.isEmpty()) {
            throw new LebronJamesException("AIRBALL. The " + commandName + " command needs a play number.");
        }

        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException exception) {
            throw new LebronJamesException(
                    "AIRBALL. The " + commandName + " command needs a whole-number play number.");
        }
    }
}
