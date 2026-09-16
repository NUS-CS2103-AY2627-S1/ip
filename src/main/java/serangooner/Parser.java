package serangooner;

import java.time.LocalDate;
import java.util.Arrays;

import serangooner.command.AddCommand;
import serangooner.command.Command;
import serangooner.command.CommandType;
import serangooner.command.DeleteCommand;
import serangooner.command.ExitCommand;
import serangooner.command.FindCommand;
import serangooner.command.HelpCommand;
import serangooner.command.ListCommand;
import serangooner.command.MarkCommand;
import serangooner.command.OnCommand;
import serangooner.command.UndoCommand;
import serangooner.command.UnmarkCommand;
import serangooner.task.DateRange;
import serangooner.task.Deadline;
import serangooner.task.Event;
import serangooner.task.TaskDateTime;
import serangooner.task.Todo;

/**
 * Makes sense of the lines that the user types.
 * This class is the only one that knows how a command is written, so the
 * wording of a command can change here without any task or list having to
 * change with it. One line of input becomes one {@link Command}, already
 * holding the values the line carried: a task, a task number or a range of
 * dates, with nothing left for the command to work out later.
 * Surrounding space is the user's to leave in, so every method here trims
 * the line before making sense of it.
 * Reading the save file is a separate concern and is left to
 * {@link serangooner.storage.Storage Storage}.
 */
public class Parser {
    private static final String SEPARATOR_BY = " by ";
    private static final String SEPARATOR_FROM = " from ";
    private static final String SEPARATOR_TO = " to ";

    private Parser() {
        // A parser holds nothing of its own, so there is never a reason to build one.
    }

    /**
     * Returns the command that the given line of input asks for, ready to run.
     * Everything the command needs is worked out here, so that carrying it
     * out later cannot fail for want of understanding what was typed.
     *
     * @param fullCommand Line of input entered by the user.
     * @return Command the input asks for.
     * @throws SerangoonerException If the input names no command, or does not
     *         give that command what it needs.
     */
    public static Command parse(String fullCommand) {
        CommandType commandType = parseCommandType(fullCommand);
        Command command = switch (commandType) {
            case TODO -> new AddCommand(parseTodo(fullCommand));
            case DEADLINE -> new AddCommand(parseDeadline(fullCommand));
            case EVENT -> new AddCommand(parseEvent(fullCommand));
            case MARK -> new MarkCommand(parseTaskNumber(fullCommand, commandType));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(fullCommand, commandType));
            case DELETE -> new DeleteCommand(parseTaskNumber(fullCommand, commandType));
            case ON -> new OnCommand(parseDateRange(fullCommand));
            case FIND -> new FindCommand(parseKeyword(fullCommand));
            case LIST -> requireNoArgument(fullCommand, commandType, new ListCommand());
            case UNDO -> requireNoArgument(fullCommand, commandType, new UndoCommand());
            case HELP -> requireNoArgument(fullCommand, commandType, new HelpCommand());
            case BYE -> requireNoArgument(fullCommand, commandType, new ExitCommand());
        };
        assert command != null : "every command type must map to a command";
        return command;
    }

    /**
     * Returns the command whose keyword matches the first word of the given input.
     * Case is ignored, so a keyword typed with a capital or in full caps is
     * still recognized.
     *
     * @param input Line of input entered by the user.
     * @return Command matching the first word of the input.
     * @throws SerangoonerException If the input is blank or matches no command.
     */
    public static CommandType parseCommandType(String input) {
        // Lookup authored with Codex.
        if (input.isBlank()) {
            throw createInvalidCommandException();
        }
        String firstWord = input.trim().split("\\s+", 2)[0];
        return Arrays.stream(CommandType.values())
                .filter(command -> command.getKeyword().equalsIgnoreCase(firstWord))
                .findFirst()
                .orElseThrow(Parser::createInvalidCommandException);
    }

    /**
     * Returns the todo described by the given command.
     *
     * @param command Command in the form "todo &lt;description&gt;".
     * @return Todo the command describes.
     * @throws SerangoonerException If the description is missing.
     */
    public static Todo parseTodo(String command) {
        String description = getArgument(command, CommandType.TODO);
        if (description.isEmpty()) {
            throw new SerangoonerException("pls name ur task: " + CommandType.TODO.getSyntax());
        }
        return new Todo(description);
    }

    /**
     * Returns the deadline described by the given command.
     * The command is split at the last " by ", since a date never carries
     * that word but a description such as "stand by me" may.
     *
     * @param rawCommand Command in the form "deadline &lt;description&gt; by &lt;date&gt;".
     * @return Deadline the command describes, its description trimmed.
     * @throws SerangoonerException If the command does not follow that form, its
     *         description is blank or carries the save delimiter, or its date is
     *         not in an accepted format.
     */
    public static Deadline parseDeadline(String rawCommand) {
        // Parsing logic authored with Codex.
        String command = rawCommand.trim();
        int descriptionStart = getArgumentStart(CommandType.DEADLINE);
        int byIndex = command.lastIndexOf(SEPARATOR_BY);
        if (byIndex < descriptionStart) {
            throw createInvalidFormatException(CommandType.DEADLINE);
        }

        String description = command.substring(descriptionStart, byIndex);
        String deadline = command.substring(byIndex + SEPARATOR_BY.length());
        return new Deadline(requireText(description, CommandType.DEADLINE),
                requireText(deadline, CommandType.DEADLINE));
    }

    /**
     * Returns the event described by the given command.
     * The command is split at the last " to ", and at the last " from " before
     * it, since dates never carry those words but a description may.
     *
     * @param rawCommand Command in the form "event &lt;description&gt; from &lt;date&gt; to &lt;date&gt;".
     * @return Event the command describes, its description trimmed.
     * @throws SerangoonerException If the command does not follow that form, its
     *         description is blank or carries the save delimiter, either of its
     *         dates is not in an accepted format, or the event does not end after
     *         it starts.
     */
    public static Event parseEvent(String rawCommand) {
        // Parsing logic authored with Codex.
        String command = rawCommand.trim();
        int descriptionStart = getArgumentStart(CommandType.EVENT);
        int toIndex = command.lastIndexOf(SEPARATOR_TO);
        int fromIndex = command.lastIndexOf(SEPARATOR_FROM, toIndex);
        int fromStart = fromIndex + SEPARATOR_FROM.length();

        boolean isFromAfterKeyword = fromIndex >= descriptionStart;
        // The two separators may share a space, as in "from to", leaving no room for a start.
        boolean isToAfterFrom = toIndex >= fromStart;
        if (!isFromAfterKeyword || !isToAfterFrom) {
            throw createInvalidFormatException(CommandType.EVENT);
        }

        String description = command.substring(descriptionStart, fromIndex);
        String start = command.substring(fromStart, toIndex);
        String end = command.substring(toIndex + SEPARATOR_TO.length());
        return new Event(requireText(description, CommandType.EVENT),
                requireText(start, CommandType.EVENT), requireText(end, CommandType.EVENT));
    }

    /**
     * Returns the task number given as the sole argument of the command.
     * Whether that number names a task is for the task list to say.
     *
     * @param command Command in the form "&lt;keyword&gt; &lt;number&gt;".
     * @param commandType Command that the input was recognized as.
     * @return Number the user typed.
     * @throws SerangoonerException If the number is missing or is not a number.
     */
    public static int parseTaskNumber(String command, CommandType commandType) {
        String argument = getArgument(command, commandType);
        String commandName = commandType.name();
        if (argument.isEmpty()) {
            throw new SerangoonerException(commandName + " FAILED. pls use format: "
                    + commandType.getSyntax());
        }

        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw new SerangoonerException(commandName + " FAILED. pls give a valid task number", exception);
        }
    }

    /**
     * Returns the span of dates named by the given command.
     * A command naming a single date gives a range covering that day alone.
     *
     * @param command Command in the form "on &lt;date&gt;" or "on &lt;date&gt; to &lt;date&gt;".
     * @return Range of dates the command names.
     * @throws SerangoonerException If a date is missing, is not in an accepted
     *         format, or the range ends before it starts.
     */
    public static DateRange parseDateRange(String command) {
        String argument = getArgument(command, CommandType.ON);
        if (argument.isEmpty()) {
            throw new SerangoonerException("pls give a date: " + CommandType.ON.getSyntax() + ". "
                    + TaskDateTime.FORMAT_HINT);
        }

        int toIndex = argument.indexOf(SEPARATOR_TO);
        if (toIndex < 0) {
            LocalDate date = TaskDateTime.parseDate(argument);
            return new DateRange(date, date);
        }
        return new DateRange(TaskDateTime.parseDate(argument.substring(0, toIndex)),
                TaskDateTime.parseDate(argument.substring(toIndex + SEPARATOR_TO.length())));
    }

    /**
     * Returns the text that the given command searches task descriptions for.
     * Everything after the keyword is searched for as it stands, so a keyword
     * of several words looks for that whole phrase.
     *
     * @param command Command in the form "find &lt;keyword&gt;".
     * @return Text to look for in each task description.
     * @throws SerangoonerException If no keyword is given.
     */
    public static String parseKeyword(String command) {
        String keyword = getArgument(command, CommandType.FIND);
        if (keyword.isEmpty()) {
            throw new SerangoonerException("pls give me something to look for: "
                    + CommandType.FIND.getSyntax());
        }
        return keyword;
    }

    /**
     * Returns everything the user typed after the command keyword, trimmed.
     *
     * @param command Line of input entered by the user.
     * @param commandType Command that the input was recognized as.
     * @return Argument of the command, or an empty string if none was given.
     */
    private static String getArgument(String command, CommandType commandType) {
        String trimmed = command.trim();
        int keywordLength = commandType.getKeyword().length();
        assert trimmed.regionMatches(true, 0, commandType.getKeyword(), 0, keywordLength)
                : "the argument is cut off past a keyword that was already matched";
        return trimmed.length() > keywordLength ? trimmed.substring(keywordLength).trim() : "";
    }

    /**
     * Returns the given command, once the input is known to carry nothing
     * after the keyword.
     * Extra words are refused rather than ignored, since "undo 3" or "bye now"
     * suggests the user expects something the command does not do.
     *
     * @param input Line of input entered by the user.
     * @param commandType Command that the input was recognized as.
     * @param command Command to return, which takes no argument.
     * @return The given command.
     * @throws SerangoonerException If anything follows the keyword.
     */
    private static Command requireNoArgument(String input, CommandType commandType, Command command) {
        if (!getArgument(input, commandType).isEmpty()) {
            throw new SerangoonerException(commandType.name() + " FAILED. '" + commandType.getKeyword()
                    + "' doesn't take anything after it");
        }
        return command;
    }

    /**
     * Returns one part of a command with the space around it removed.
     *
     * @param part Text found between separators of the command.
     * @param commandType Command that the input was recognized as.
     * @return Part without surrounding space.
     * @throws SerangoonerException If the part is blank.
     */
    private static String requireText(String part, CommandType commandType) {
        if (part.isBlank()) {
            throw createInvalidFormatException(commandType);
        }
        return part.trim();
    }

    /**
     * Returns the position at which the argument of the given command starts,
     * which is just past its keyword and the space that follows it.
     *
     * @param commandType Command that the input was recognized as.
     * @return Index of the first character after the keyword and its space.
     */
    private static int getArgumentStart(CommandType commandType) {
        return commandType.getKeyword().length() + 1;
    }

    private static SerangoonerException createInvalidCommandException() {
        return new SerangoonerException(
                "invalid command :/ if you don't know what you're doing, "
                        + "pls type 'help' for the command library .-.");
    }

    private static SerangoonerException createInvalidFormatException(CommandType commandType) {
        return new SerangoonerException("INVALID. pls use format: " + commandType.getSyntax()
                + ". " + TaskDateTime.FORMAT_HINT);
    }
}
