package billy.parser;

import java.time.LocalDate;

import billy.BillyException;
import billy.command.AddCommand;
import billy.command.Command;
import billy.command.DeleteCommand;
import billy.command.ExitCommand;
import billy.command.FindCommand;
import billy.command.HelpCommand;
import billy.command.ListCommand;
import billy.command.MarkCommand;
import billy.command.OnCommand;
import billy.task.Deadline;
import billy.task.Event;
import billy.task.Task;
import billy.task.TaskDate;
import billy.task.TaskList;
import billy.task.Todo;

/**
 * Turns what the user typed into something Billy can act on.
 *
 * <p>This is the only place that knows the shape of a command: that the first
 * word names it, that a deadline puts {@value #BY_SEPARATOR} before its date,
 * that a task is named by a number. Everywhere else deals in tasks and dates
 * that have already been made sense of, so a change to how commands are typed
 * stops here.
 *
 * <p>Parsing is deliberately unforgiving. Reading a command produces either
 * something valid or a {@link BillyException} explaining what to type instead,
 * and never a half-built task. That is what lets the rest of Billy assume that
 * anything reaching it is sound.
 *
 * <p>The methods are static because parsing depends only on the text handed in;
 * there is nothing to remember between commands, so there is nothing to be an
 * instance of.
 */
public class Parser {

    /**
     * The one character a description may not contain.
     *
     * <p>Taken from {@link billy.task.Task#FIELD_SEPARATOR}, which is what a
     * saved line is split on, so the two cannot drift apart.
     */
    private static final String SEPARATOR_CHARACTER = Task.FIELD_SEPARATOR.trim();

    /** Separates a deadline's description from its due date. */
    private static final String BY_SEPARATOR = "/by";

    /** Separates an event's description from its start time. */
    private static final String FROM_SEPARATOR = "/from";

    /** Separates an event's start time from its end time. */
    private static final String TO_SEPARATOR = "/to";

    /** Shown alongside an error to remind the user how a todo is typed. */
    private static final String TODO_USAGE = "Try: todo borrow book";

    /** Shown alongside an error to remind the user how a deadline is typed. */
    private static final String DEADLINE_USAGE =
            "Try: deadline return book /by 2019-12-02 1800";

    /** Shown alongside an error to remind the user how an event is typed. */
    private static final String EVENT_USAGE =
            "Try: event project meeting /from 2019-12-02 1400 /to 2019-12-02 1600";

    /** Shown alongside an error to remind the user how a day is asked about. */
    private static final String ON_USAGE = "Try: on 2019-12-02";

    /** Shown alongside an error to remind the user how a search is typed. */
    private static final String FIND_USAGE = "Try: find book";

    /**
     * Represents the two halves a command is split into, such as a description
     * and the due date after {@value #BY_SEPARATOR}.
     *
     * @param before what came before the separator, trimmed
     * @param after what came after the separator, trimmed
     */
    private record Halves(String before, String after) {
    }

    /** Prevents an instance being made, since parsing needs nothing remembered. */
    private Parser() {
    }

    /**
     * Turns one typed line into the command it asks for.
     *
     * <p>Everything a command needs is worked out here, so what comes back is
     * ready to be carried out and known to be sound. A line that cannot be made
     * sense of produces an exception instead, never a command that will fail
     * halfway through.
     *
     * @param line one whole line as the user typed it, already trimmed
     * @return the command it asks for
     * @throws BillyException if the line is empty, names no known command, or is
     *                        malformed for the command it does name
     */
    public static Command parse(String line) throws BillyException {
        // Trimmed here as well as by the callers. Both of them do trim today,
        // so this changes nothing a user can see; it means the method cannot be
        // made to answer "I don't know what '' means" by a caller that forgets.
        line = line.trim();
        if (line.isEmpty()) {
            throw new BillyException("You'll have to give me something to work with!");
        }
        // Split into the keyword and everything after it, e.g. "mark 2" -> "mark", "2".
        String[] parts = line.split("\\s+", 2);
        CommandWord word = CommandWord.fromKeyword(parts[0]);
        String argument = parts.length > 1 ? parts[1] : "";

        return switch (word) {
            case TODO -> new AddCommand(parseTodo(argument));
            case DEADLINE -> new AddCommand(parseDeadline(argument));
            case EVENT -> new AddCommand(parseEvent(argument));
            case LIST -> new ListCommand();
            case ON -> new OnCommand(parseDay(argument));
            case FIND -> new FindCommand(parseKeyword(argument));
            case MARK -> new MarkCommand(parseTaskNumber(argument, word), true);
            case UNMARK -> new MarkCommand(parseTaskNumber(argument, word), false);
            case DELETE -> new DeleteCommand(parseTaskNumber(argument, word));
            case HELP -> new HelpCommand();
            case BYE -> new ExitCommand();
        };
    }

    /**
     * Builds a todo from what the user typed after the keyword.
     *
     * @param argument the description of the task
     * @return the todo it describes
     * @throws BillyException if the description is missing
     */
    private static Todo parseTodo(String argument) throws BillyException {
        if (argument.isBlank()) {
            throw new BillyException("The description of a todo can't be empty. " + TODO_USAGE);
        }
        return new Todo(checkDescription(argument.trim()));
    }

    /**
     * Returns the description unchanged, having checked it can be written to the
     * save file and read back.
     *
     * <p>Billy separates the fields of a saved task with
     * {@value billy.task.Task#FIELD_SEPARATOR}, so a description containing that
     * character splits into the wrong fields when the file is read again. A todo
     * happens to survive it, since its description is the last field on the
     * line, but a deadline or an event loses its date and the whole task is
     * dropped as damaged. Refusing the character here costs the user one
     * character; letting it through costs them the task, silently, on the next
     * run.
     *
     * @param description the description as the user typed it, trimmed
     * @return the same description
     * @throws BillyException if it contains the field separator
     */
    private static String checkDescription(String description) throws BillyException {
        if (description.contains(SEPARATOR_CHARACTER)) {
            throw new BillyException("A description can't contain '" + SEPARATOR_CHARACTER
                    + "', since that's what I use to separate the parts of a saved task."
                    + " Try wording it without one.");
        }
        return description;
    }

    /**
     * Builds a deadline from what the user typed after the keyword.
     *
     * @param argument expected as {@code <description> /by <date>}
     * @return the deadline it describes
     * @throws BillyException if the description or the due date is missing or unreadable
     */
    private static Deadline parseDeadline(String argument) throws BillyException {
        Halves parts = splitOn(argument, BY_SEPARATOR, "description", "due date", DEADLINE_USAGE);
        return new Deadline(checkDescription(parts.before()), TaskDate.parse(parts.after()));
    }

    /**
     * Builds an event from what the user typed after the keyword.
     *
     * @param argument expected as {@code <description> /from <start> /to <end>}
     * @return the event it describes
     * @throws BillyException if any part is missing or unreadable, or if the
     *                        event would end before it starts
     */
    private static Event parseEvent(String argument) throws BillyException {
        Halves descriptionAndRest =
                splitOn(argument, FROM_SEPARATOR, "description", "start time", EVENT_USAGE);
        // The start and end times are still joined together, so split them apart too.
        Halves startAndEnd = splitOn(descriptionAndRest.after(), TO_SEPARATOR,
                "start time", "end time", EVENT_USAGE);
        return new Event(checkDescription(descriptionAndRest.before()), TaskDate.parse(startAndEnd.before()),
                TaskDate.parse(startAndEnd.after()));
    }

    /**
     * Reads what the user typed as a task number.
     *
     * <p>Only the reading happens here. Whether the number names a task that
     * exists is for {@link TaskList} to say, since it is the one that knows.
     *
     * @param argument the text the user typed after the keyword
     * @param command the command the number belongs to, used to give a fitting example
     * @return the number as the user wrote it, counting from 1
     * @throws BillyException if the text is not a number at all
     */
    private static int parseTaskNumber(String argument, CommandWord command) throws BillyException {
        try {
            return Integer.parseInt(argument.trim());
        } catch (NumberFormatException e) {
            // Covers both a missing number ("mark") and a non-number ("mark two").
            throw new BillyException(
                    "I need a task number, like '" + command.getKeyword() + " 2'.");
        }
    }

    /**
     * Reads what the user typed as the word to search for.
     *
     * <p>Whatever was typed is taken as it stands, spaces and all, so
     * {@code find read book} looks for that whole phrase rather than for either
     * word. A search for two words at once would need a way to say whether both
     * or either must match, and nothing yet asks for one.
     *
     * @param argument the text the user typed after the keyword
     * @return the word to look for, trimmed
     * @throws BillyException if nothing was given to look for
     */
    private static String parseKeyword(String argument) throws BillyException {
        if (argument.isBlank()) {
            throw new BillyException("What should I look for? " + FIND_USAGE);
        }
        return argument.trim();
    }

    /**
     * Reads what the user typed as the day to look at.
     *
     * <p>A time of day may be given but is ignored: a whole day is being asked
     * about, so {@code 2019-12-02 1800} means the same as {@code 2019-12-02}.
     *
     * @param argument the text the user typed after the keyword
     * @return the day it names
     * @throws BillyException if no day is given, or it cannot be read as one
     */
    private static LocalDate parseDay(String argument) throws BillyException {
        if (argument.isBlank()) {
            throw new BillyException("Which day should I look at? " + ON_USAGE);
        }
        return TaskDate.parse(argument).getDate();
    }

    /**
     * Splits input around a separator such as {@value #BY_SEPARATOR}.
     *
     * <p>The two halves are named by the caller so that a failure can say exactly
     * which part of the command is missing.
     *
     * @param input the text to split
     * @param separator the marker to split around
     * @param beforeName what the text before the separator means, e.g. "description"
     * @param afterName what the text after the separator means, e.g. "due date"
     * @param usage an example of the command, shown to help the user correct it
     * @return the two trimmed halves
     * @throws BillyException if the separator is missing or either half is empty
     */
    private static Halves splitOn(String input, String separator, String beforeName,
            String afterName, String usage) throws BillyException {
        int separatorPosition = input.indexOf(separator);
        if (separatorPosition == -1) {
            throw new BillyException("I need '" + separator + "' in that command. " + usage);
        }

        // Splitting at the first one and leaving the rest in place would push a
        // second copy into the date, where it surfaces as an unreadable date
        // rather than as the duplicated marker it is.
        if (input.indexOf(separator, separatorPosition + separator.length()) != -1) {
            throw new BillyException("I found more than one '" + separator + "' in that"
                    + " command, so I don't know which one you meant. " + usage);
        }

        String before = input.substring(0, separatorPosition).trim();
        String after = input.substring(separatorPosition + separator.length()).trim();
        if (before.isEmpty()) {
            throw new BillyException("The " + beforeName + " can't be empty. " + usage);
        }
        if (after.isEmpty()) {
            throw new BillyException("The " + afterName + " can't be empty. " + usage);
        }
        return new Halves(before, after);
    }
}
