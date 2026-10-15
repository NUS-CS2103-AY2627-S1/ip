package nova.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import nova.command.AddCommand;
import nova.command.Command;
import nova.command.DeleteCommand;
import nova.command.ExitCommand;
import nova.command.FindCommand;
import nova.command.HelpCommand;
import nova.command.ListCommand;
import nova.command.MarkCommand;
import nova.command.MarkCommand.CompletionAction;
import nova.command.ShowOnDateCommand;
import nova.exception.NovaException;
import nova.task.Deadline;
import nova.task.Event;
import nova.task.Todo;

/**
 * Interprets user input as commands that Nova can execute.
 */
public class Parser {
    /**
     * Creates a parser for Nova commands.
     */
    public Parser() {
    }

    /**
     * Parses user input into a command and validates its arguments.
     *
     * @param input raw user input.
     * @return parsed command.
     * @throws NovaException if the command or its arguments are invalid.
     */
    public Command parse(String input) throws NovaException {
        if (input.equals("help")) {
            return new HelpCommand();
        }

        String commandWord = getCommandWord(input);
        switch (commandWord) {
            case "bye":
                if (input.equals(commandWord)) {
                    return new ExitCommand();
                }
                break;
            case "list":
                if (input.equals(commandWord)) {
                    return new ListCommand();
                }
                break;
            case "mark":
                return new MarkCommand(parseTaskNumber(input, commandWord), CompletionAction.MARK);
            case "unmark":
                return new MarkCommand(parseTaskNumber(input, commandWord), CompletionAction.UNMARK);
            case "delete":
                return new DeleteCommand(parseTaskNumber(input, commandWord));
            case "find":
                return parseFind(input);
            case "todo":
                return parseTodo(input);
            case "deadline":
                return parseDeadline(input);
            case "event":
                return parseEvent(input);
            case "on":
                return parseDateSearch(input);
            default:
                break;
        }
        throw new NovaException("I'm sorry, but I don't know what that means :-(");
    }

    /**
     * Returns the first space-delimited word in the input.
     *
     * @param input raw user input
     * @return first word, or the entire input when it contains no spaces
     */
    private String getCommandWord(String input) {
        int firstSpace = input.indexOf(' ');
        return firstSpace < 0 ? input : input.substring(0, firstSpace);
    }

    /**
     * Returns the task-list number in a numbered command.
     *
     * @param input raw user input
     * @param commandWord command word preceding the task number
     * @return parsed task number
     * @throws NovaException if the command does not contain an integer task number
     */
    private int parseTaskNumber(String input, String commandWord) throws NovaException {
        assert getCommandWord(input).equals(commandWord)
                : "Numbered-command parser must receive the matching command";

        String taskNumberText = input.substring(commandWord.length()).trim();
        try {
            return Integer.parseInt(taskNumberText);
        } catch (NumberFormatException exception) {
            throw new NovaException("Please enter a task number, for example: " + commandWord + " 1");
        }
    }

    /**
     * Returns an add command containing a todo.
     *
     * @param input raw todo command
     * @return command that adds the parsed todo
     * @throws NovaException if the todo description is empty
     */
    private Command parseTodo(String input) throws NovaException {
        String description = input.substring("todo".length()).trim();
        if (description.isEmpty()) {
            throw new NovaException("The description of a todo cannot be empty.");
        }
        return new AddCommand(new Todo(description));
    }

    /**
     * Returns a find command containing a non-empty keyword.
     *
     * @param input raw find command.
     * @return command that searches for the parsed keyword.
     * @throws NovaException if the keyword is empty.
     */
    private Command parseFind(String input) throws NovaException {
        String keyword = input.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new NovaException("The keyword for a find command cannot be empty.");
        }
        return new FindCommand(keyword);
    }

    /**
     * Returns an add command containing a deadline.
     *
     * @param input raw deadline command
     * @return command that adds the parsed deadline
     * @throws NovaException if the command format or deadline date is invalid
     */
    private Command parseDeadline(String input) throws NovaException {
        String details = input.substring("deadline".length()).trim();
        int byMarker = details.indexOf(" /by ");
        if (byMarker < 1 || byMarker + " /by ".length() >= details.length()) {
            throw new NovaException("A deadline must follow: deadline DESCRIPTION /by yyyy-MM-dd");
        }

        String description = details.substring(0, byMarker).trim();
        String byText = details.substring(byMarker + " /by ".length()).trim();
        try {
            return new AddCommand(new Deadline(description, LocalDate.parse(byText)));
        } catch (DateTimeParseException exception) {
            throw new NovaException("The deadline date must be a valid date in yyyy-MM-dd format.");
        }
    }

    /**
     * Returns an add command containing an event.
     *
     * @param input raw event command
     * @return command that adds the parsed event
     * @throws NovaException if the command format or event times are invalid
     */
    private Command parseEvent(String input) throws NovaException {
        String details = input.substring("event".length()).trim();
        int fromMarker = details.indexOf(" /from ");
        int toMarker = details.indexOf(" /to ", fromMarker + 1);
        boolean isInvalid = fromMarker < 1
                || toMarker < fromMarker + " /from ".length()
                || toMarker + " /to ".length() >= details.length();
        if (isInvalid) {
            throw new NovaException("An event must follow: event DESCRIPTION /from START /to END");
        }

        String description = details.substring(0, fromMarker).trim();
        String from = details.substring(fromMarker + " /from ".length(), toMarker).trim();
        String to = details.substring(toMarker + " /to ".length()).trim();
        if (from.isEmpty()) {
            throw new NovaException("An event needs a start date or time after /from.");
        }
        return new AddCommand(new Event(description, from, to));
    }

    /**
     * Returns a command that searches for deadlines on a date.
     *
     * @param input raw date-search command
     * @return command that searches the parsed date
     * @throws NovaException if the date is invalid
     */
    private Command parseDateSearch(String input) throws NovaException {
        String dateText = input.substring("on".length()).trim();
        try {
            LocalDate date = LocalDate.parse(dateText);
            return new ShowOnDateCommand(date);
        } catch (DateTimeParseException exception) {
            throw new NovaException("The date must be a valid date in yyyy-MM-dd format.");
        }
    }

}
