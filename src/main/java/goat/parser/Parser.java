package goat.parser;

import java.time.LocalDateTime;

import goat.GoatException;
import goat.command.AddCommand;
import goat.command.Command;
import goat.command.DeleteCommand;
import goat.command.ExitCommand;
import goat.command.FindCommand;
import goat.command.ListCommand;
import goat.command.MarkCommand;
import goat.command.SortCommand;
import goat.task.Deadline;
import goat.task.Event;
import goat.task.SortOrder;
import goat.task.Task;
import goat.task.Todo;

/**
 * Turns the text the user typed into the values the rest of Goat works with.
 *
 * Everything here answers the same question -- "what does this line mean?" --
 * and nothing here does anything about the answer. That split is the point:
 * {@link goat.Goat} was previously both reading the user's text and acting on it,
 * so a change to the wording of a command (say, allowing "by:" as well as
 * "/by") meant editing the same method that also adds tasks and saves files.
 *
 * Because this class only reads text and never touches the list, the screen or
 * the disk, its methods can be tested by calling them with a string and
 * checking what comes back -- which is what the A-JUnit increment will want.
 *
 * The whole translation now ends in a {@link Command}: {@link #parse} takes a
 * line and hands back an object that knows how to carry that line out, so
 * {@link goat.Goat} never has to look at the user's text at all.
 *
 * The methods are static because there is nothing for an instance to remember:
 * each call is a self-contained translation from one string to one value.
 */
public class Parser {
    /** How a deadline's due date is introduced. */
    private static final String BY_MARKER = "/by";

    /** How an event's start time is introduced. */
    private static final String FROM_MARKER = "/from";

    /** How an event's end time is introduced. */
    private static final String TO_MARKER = "/to";

    /** Not instantiable: this class is a collection of translations, not a thing. */
    private Parser() {
    }

    /**
     * Turns one line of input into the command it asks for.
     *
     * This is the only method {@link goat.Goat} calls. Everything below it is a
     * step in getting here, and the switch that used to sit in Goat now lives
     * here instead -- which is the right place for it, because choosing a
     * class based on a keyword is a parsing decision.
     *
     * @param fullCommand one whole line as the user typed it
     * @return a command ready to be executed
     * @throws GoatException if the line is not a command Goat knows, or its
     *                       argument is missing or malformed
     */
    public static Command parse(String fullCommand) throws GoatException {
        assert fullCommand != null : "Ui.readCommand never returns null";
        // Trimmed here as well as by Ui. Splitting a line that starts with a
        // space yields an empty first word, which would be reported as an
        // unknown command rather than as the stray space it is -- and Parser
        // should not depend on every caller having tidied up first.
        String command = fullCommand.trim();
        CommandType commandType = parseCommandType(command);
        String argument = parseArgument(command);
        // parseCommandType throws rather than returning nothing, so by here a
        // command word has definitely been recognized.
        assert commandType != null : "parseCommandType returned no command";
        assert argument != null : "parseArgument returned null";
        // Arrow labels cannot fall through, so no break is needed.
        return switch (commandType) {
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case MARK -> new MarkCommand(parseTaskNumberFor(commandType, argument), true);
            case UNMARK -> new MarkCommand(parseTaskNumberFor(commandType, argument), false);
            case DELETE -> new DeleteCommand(parseTaskNumberFor(commandType, argument));
            case FIND -> new FindCommand(parseKeyword(argument));
            case SORT -> new SortCommand(SortOrder.fromKeyword(argument));
            case TODO, DEADLINE, EVENT -> new AddCommand(parseNewTask(commandType, argument));
        };
    }

    /**
     * Works out which command a line is asking for.
     *
     * @param fullCommand one whole line as the user typed it
     * @return the command named by the line's first word
     * @throws GoatException if the first word is not a command Goat knows
     */
    private static CommandType parseCommandType(String fullCommand) throws GoatException {
        return CommandType.fromKeyword(commandWord(fullCommand));
    }

    /**
     * Returns everything the user typed after the command word.
     *
     * @param fullCommand one whole line as the user typed it
     * @return the rest of the line, with surrounding spaces removed;
     *         an empty string if the line was only a command word
     */
    private static String parseArgument(String fullCommand) {
        String commandWord = commandWord(fullCommand);
        return fullCommand.substring(commandWord.length()).trim();
    }

    /**
     * Reads the number the user typed after "mark", "unmark" or "delete".
     *
     * This method only reads the number. Whether it refers to a task that
     * exists is {@link goat.task.TaskList}'s business, checked when the command runs.
     *
     * @param argument the text typed after the command word
     * @return the number as the user wrote it, counting from 1
     * @throws GoatException if the text is not a whole number
     */
    private static int parseTaskNumber(String argument) throws GoatException {
        try {
            return Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            throw new GoatException("There is no task '" + argument
                    + "'. Use list to see what there is.");
        }
    }

    /**
     * Reads the keyword a "find" command should search for.
     *
     * @param argument the text typed after the command word
     * @return the keyword, with surrounding spaces already removed
     * @throws GoatException if no keyword was given, since a search for
     *                       nothing would match every task
     */
    private static String parseKeyword(String argument) throws GoatException {
        if (argument.isEmpty()) {
            throw new GoatException("Find what? Try: find book");
        }
        return argument;
    }

    /**
     * Reads the task number for a command that needs one, complaining in that
     * command's own words if the user left it out.
     *
     * The wording differs between "mark"/"unmark" and "delete", and only this
     * method knows which command is being read, so the check lives here rather
     * than in {@link #parseTaskNumber}.
     *
     * @param command  the command the number belongs to
     * @param argument the text typed after the command word
     * @return the number as the user wrote it, counting from 1
     * @throws GoatException if the number is missing or is not a whole number
     */
    private static int parseTaskNumberFor(CommandType command, String argument)
            throws GoatException {
        if (argument.isEmpty()) {
            throw new GoatException(command == CommandType.DELETE
                    ? "Which one? Try: delete 1"
                    : "Which one? Try: mark 1");
        }
        return parseTaskNumber(argument);
    }

    /**
     * Builds the right kind of task for what the user typed.
     * The command decides the subclass, and the text after it supplies the
     * description and any dates.
     *
     * The dates are turned into values here, at the edge where the user's text
     * comes in, so that a {@link Deadline} or {@link Event} can never end up
     * holding a date that was never understood.
     *
     * @param command  which of the task-adding commands was used
     * @param argument everything after the command word, already trimmed
     * @return the new task
     * @throws GoatException if the description or dates are missing, or a
     *                       date is not written in a format Goat understands
     */
    private static Task parseNewTask(CommandType command, String argument) throws GoatException {
        switch (command) {
            case TODO:
                return parseTodo(argument);
            case DEADLINE:
                return parseDeadline(argument);
            case EVENT:
                return parseEvent(argument);
            default:
                // Unreachable: parse() sends only TODO, DEADLINE and EVENT here.
                // The assert says so for a reader; the throw is what actually
                // happens if a new task type is added and this is not updated,
                // since assertions are off unless -ea is passed.
                assert false : "not a task-adding command: " + command;
                throw new IllegalStateException("not a task-adding command: " + command);
        }
    }

    /**
     * Reads a "todo" argument, which is the description and nothing else.
     *
     * @param argument everything after the command word
     * @return the new todo
     * @throws GoatException if no description was given
     */
    private static Todo parseTodo(String argument) throws GoatException {
        if (argument.isEmpty()) {
            throw new GoatException("A todo needs something to do. "
                    + "Try: todo read book");
        }
        requireStorable(argument);
        return new Todo(argument);
    }

    /**
     * Rejects a description the save file could not carry.
     *
     * The save file separates a task's fields with a bar, so a description
     * containing one cannot be read back: the extra bar looks like another
     * field, and everything after it is silently dropped the next time Goat
     * starts. Refusing it at the point of entry is better than accepting it,
     * confirming it, and losing half of it overnight.
     *
     * @param description the text the user gave
     * @throws GoatException if the description contains the separator
     */
    private static void requireStorable(String description) throws GoatException {
        if (description.contains("|")) {
            throw new GoatException("A description cannot contain '|'. "
                    + "Goat uses it to separate fields in the save file, "
                    + "so the task would come back with everything after it missing.");
        }
    }

    /**
     * Rejects a command that repeats a marker Goat only expects once.
     *
     * Without this the second marker is swallowed into whatever the first one
     * introduced, so "deadline x /by monday /by tuesday" complains that
     * "monday /by tuesday" is not a date, which sends the user looking in the
     * wrong place.
     *
     * @param argument everything after the command word
     * @param marker   the marker to count, e.g. "/by"
     * @param usage    an example of the command used correctly
     * @throws GoatException if the marker appears more than once
     */
    private static void requireOneMarker(String argument, String marker, String usage)
            throws GoatException {
        if (argument.split(java.util.regex.Pattern.quote(marker), -1).length > 2) {
            throw new GoatException("Only one " + marker + ", please. Try: " + usage);
        }
    }

    /**
     * Reads a "deadline" argument, which is a description and a due date
     * separated by "/by".
     *
     * @param argument everything after the command word
     * @return the new deadline
     * @throws GoatException if either half is missing, or the date is unreadable
     */
    private static Deadline parseDeadline(String argument) throws GoatException {
        requireOneMarker(argument, BY_MARKER, "deadline return book /by 2019-12-02 1800");
        // Split once on "/by": everything before it is the description.
        String[] parts = argument.split(BY_MARKER, 2);
        if (parts.length < 2 || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            throw new GoatException("A deadline needs a description and a time. "
                    + "Try: deadline return book /by 2019-12-02 1800");
        }
        requireStorable(parts[0].trim());
        return new Deadline(parts[0].trim(), DateTimes.parse(parts[1]));
    }

    /**
     * Reads an "event" argument, which is a description, a start and an end,
     * separated by "/from" and "/to".
     *
     * @param argument everything after the command word
     * @return the new event
     * @throws GoatException if any of the three parts is missing, or a date
     *                       is unreadable
     */
    private static Event parseEvent(String argument) throws GoatException {
        String usage = "event project meeting /from 2019-08-06 1400 /to 2019-08-06 1600";
        requireOneMarker(argument, FROM_MARKER, usage);
        requireOneMarker(argument, TO_MARKER, usage);
        // Split on "/from" first, then split what follows on "/to".
        String[] fromParts = argument.split(FROM_MARKER, 2);
        String[] toParts = fromParts.length < 2
                ? new String[0] : fromParts[1].split(TO_MARKER, 2);
        if (toParts.length < 2 || fromParts[0].trim().isEmpty()
                || toParts[0].trim().isEmpty() || toParts[1].trim().isEmpty()) {
            throw new GoatException("An event needs a description, a start and an end. "
                    + "Try: event project meeting /from 2019-08-06 1400 "
                    + "/to 2019-08-06 1600");
        }
        requireStorable(fromParts[0].trim());
        LocalDateTime from = DateTimes.parse(toParts[0]);
        LocalDateTime to = DateTimes.parse(toParts[1]);
        // An event that ends before it starts, or at the same moment it starts,
        // is almost always the two times typed the wrong way round.
        if (!to.isAfter(from)) {
            throw new GoatException("An event has to end after it starts. "
                    + "You gave " + DateTimes.format(from) + " to " + DateTimes.format(to) + ".");
        }
        return new Event(fromParts[0].trim(), from, to);
    }

    /**
     * Returns the first word of a line.
     *
     * Splitting into the first word and the rest -- rather than into every
     * word -- means a bare "todo" is still recognized as the todo command with
     * a missing description, instead of as some unknown command.
     *
     * @param fullCommand one whole line as the user typed it
     * @return the line's first word, which may be empty if the line was blank
     */
    private static String commandWord(String fullCommand) {
        return fullCommand.split(" ", 2)[0];
    }
}
