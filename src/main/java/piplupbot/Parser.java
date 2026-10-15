package piplupbot;

import java.util.Arrays;

import piplupbot.command.AddCommand;
import piplupbot.command.Command;
import piplupbot.command.CommandWord;
import piplupbot.command.DeleteCommand;
import piplupbot.command.ExitCommand;
import piplupbot.command.FindCommand;
import piplupbot.command.ListCommand;
import piplupbot.command.MarkCommand;
import piplupbot.command.SortCommand;
import piplupbot.task.DateTimes;
import piplupbot.task.Deadline;
import piplupbot.task.Event;
import piplupbot.task.SortDirection;
import piplupbot.task.SortKey;
import piplupbot.task.Task;
import piplupbot.task.TaskList;
import piplupbot.task.Todo;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Turns a line the user typed into the {@link Command} it asks for.
 *
 * <p>Every command that carries more than its own keyword needs its line pulled
 * apart before anything can be done with it: a deadline hides a description and
 * a date on either side of {@code /by}, an event hides three parts, and
 * {@code mark} hides a number. That work used to sit in {@link PiplupBot}
 * alongside the decisions about what to do with the result, which meant the
 * class that decides <em>what</em> a command means also had to know the exact
 * spelling of every separator.</p>
 *
 * <p>Splitting the two apart leaves this class with one job: read the line, or
 * explain why it cannot be read. Nothing here stores a task, prints anything, or
 * knows that a task list exists -- it hands back a command and lets the caller
 * decide when to run it. That is also what makes it easy to test:
 * {@link #parse} is a line of text in, a command or a {@link PiplupBotException}
 * out.</p>
 *
 * <p>Unlike {@link Ui} and {@link TaskList}, this is a class of static methods
 * rather than an object to create, because it has nothing to remember between
 * calls. Reading one line tells it nothing it would want to know while reading
 * the next, so there is no state for an object to hold and no reason to make one.</p>
 *
 * <p>Recognizing the word itself is still {@link CommandWord#fromInput}'s
 * question, not this class's: the enum holds the keywords, so matching them
 * belongs with them. This class asks that question and then does what the enum
 * cannot -- build the command object, filled in with whatever the rest of the
 * line said.</p>
 */
public class Parser {
    /**
     * Separates a deadline's description from its due date.
     * The spaces are part of the constant, so a description containing the
     * characters "/by" is not mistaken for the separator.
     */
    private static final String SEPARATOR_BY = " /by ";

    /**
     * Separates an event's description from its start time.
     * Surrounded by spaces for the same reason as {@link #SEPARATOR_BY}, which
     * is why a description mentioning "from" is safe.
     */
    private static final String SEPARATOR_FROM = " /from ";

    /**
     * Separates an event's start time from its end time.
     * Surrounded by spaces for the same reason as {@link #SEPARATOR_BY}.
     */
    private static final String SEPARATOR_TO = " /to ";

    /**
     * What a task number looks like: one or more of the digits 0 to 9, and
     * nothing else -- no sign, and no digits from other writing systems.
     */
    private static final String TASK_NUMBER_PATTERN = "[0-9]+";

    /**
     * Returns the command a whole line asks for, ready to run.
     *
     * <p>The command is built but not carried out, so a line that cannot be
     * understood is refused before anything has happened: no task is stored, no
     * confirmation is printed, and the list is exactly as it was. That is why
     * every method below throws rather than returning something half-filled.</p>
     *
     * <p>The {@code switch} names every constant, so adding a command to
     * {@link CommandWord} leaves a gap here that the compiler reports -- a
     * {@code switch} expression over an enum must cover all of them. This is the
     * one place left that lists the commands; {@link PiplupBot#run} no longer
     * does, because it asks whatever it is given to execute itself.</p>
     *
     * <p>All three task keywords produce an {@link AddCommand}, differing only in
     * which kind of {@link Task} was built for it -- the same polymorphism that
     * lets one list hold all three.</p>
     *
     * @param input The whole line the user typed, already trimmed and not empty.
     * @return The command the line asks for.
     * @throws PiplupBotException If the line holds a control character, names no
     *                            command, or names one but is missing or
     *                            mistaking what should follow it.
     */
    public static Command parse(String input) throws PiplupBotException {
        assert !input.isBlank() : "parse() was given a blank line: \"" + input + "\"";
        assert input.stripLeading().equals(input)
                : "parse() expects the leading spaces already removed: \"" + input + "\"";

        // Checked first, because the reply to an unknown command quotes the line
        // back, and a control character printed to the console can garble it.
        requirePlainText(input);

        CommandWord commandWord = CommandWord.fromInput(input);
        return switch (commandWord) {
            case TODO -> new AddCommand(parseTodo(input));
            case DEADLINE -> new AddCommand(parseDeadline(input));
            case EVENT -> new AddCommand(parseEvent(input));
            case LIST -> new ListCommand();
            case FIND -> new FindCommand(parseKeyword(input));
            case SORT -> parseSort(input);
            case MARK -> new MarkCommand(parseTaskNumber(input, commandWord), true);
            case UNMARK -> new MarkCommand(parseTaskNumber(input, commandWord), false);
            case DELETE -> new DeleteCommand(parseTaskNumber(input, commandWord));
            case BYE -> new ExitCommand();
        };
    }

    /**
     * Checks that a line holds no control characters, such as a tab, or the
     * invisible codes some consoles send when an arrow key is pressed.
     *
     * <p>Left in, such a character would be stored in a description, where it
     * prints as garbage -- or moves the cursor -- every time the task is listed.
     * A tab is also easy to mistake for a space: {@code todo<tab>read book}
     * would be refused as an unknown command, with no visible reason why.
     * Refusing the line up front, and saying what is wrong with it, is clearer
     * than either.</p>
     *
     * <p>Neither face can actually deliver a line break, so there is no need to
     * worry about one splitting a saved task in two: the console reads a line at
     * a time, and the window's text box strips control characters as they are
     * typed or pasted.</p>
     *
     * @param input The whole line the user typed.
     * @throws PiplupBotException If the line holds a control character.
     */
    private static void requirePlainText(String input) throws PiplupBotException {
        if (input.chars().anyMatch(Character::isISOControl)) {
            throw new PiplupBotException(
                    "Pip... That line has a tab or another control character in it.",
                    "Please type it again with plain spaces, and without the arrow keys.");
        }
    }

    /**
     * Returns the todo that a line of the form {@code todo <description>}
     * describes.
     *
     * @param input The whole line the user typed.
     * @return The task the line describes.
     * @throws PiplupBotException If no description follows the command word.
     */
    private static Todo parseTodo(String input) throws PiplupBotException {
        String description = CommandWord.TODO.extractArgument(input);
        if (description.isEmpty()) {
            throw new PiplupBotException("Pip... A todo needs a description, e.g. todo borrow book.");
        }
        return new Todo(description);
    }

    /**
     * Returns the deadline that a line of the form
     * {@code deadline <description> /by <when>} describes.
     *
     * <p>This method only splits the line apart; whether the {@code /by} part is
     * a date at all is {@link DateTimes}'s question, asked by the
     * {@link Deadline} constructor. Both kinds of mistake reach the user the
     * same way, as a {@link PiplupBotException} the main loop turns into a
     * reply.</p>
     *
     * @param input The whole line the user typed.
     * @return The task the line describes.
     * @throws PiplupBotException If the description or the {@code /by} part is
     *                            missing, the {@code /by} part is given twice, or
     *                            the date cannot be understood.
     */
    private static Deadline parseDeadline(String input) throws PiplupBotException {
        String[] parts = splitIntoParts(CommandWord.DEADLINE.extractArgument(input),
                "Pip... A deadline needs a /by part, "
                        + "e.g. deadline return book /by 2019-10-15 1800.",
                SEPARATOR_BY);
        return new Deadline(parts[0], parts[1]);
    }

    /**
     * Returns the event that a line of the form
     * {@code event <description> /from <start> /to <end>} describes.
     * The two times are read the same way a deadline's date is.
     *
     * @param input The whole line the user typed.
     * @return The task the line describes.
     * @throws PiplupBotException If the description, the {@code /from} part
     *                            or the {@code /to} part is missing, either part
     *                            is given twice, either time cannot be
     *                            understood, or the event ends before it starts.
     */
    private static Event parseEvent(String input) throws PiplupBotException {
        String[] parts = splitIntoParts(CommandWord.EVENT.extractArgument(input),
                "Pip... An event needs a /from and a /to part, "
                        + "e.g. event project meeting /from 2019-10-02 1400 /to 2019-10-02 1600.",
                SEPARATOR_FROM, SEPARATOR_TO);
        return new Event(parts[0], parts[1], parts[2]);
    }

    /**
     * Returns the trimmed parts of what the user typed after a command word,
     * split on the separators that command uses.
     *
     * <p>{@code deadline} and {@code event} differ only in how many separators
     * they use, so the rule for cutting a line apart is written once here rather
     * than once per command. Each of them used to carry its own copy, and the
     * copies had already begun to differ: only the event one searched for a
     * separator after the one before it. Sharing the code shares that decision
     * too, so a third command with a separator of its own gets it for free.</p>
     *
     * <p>Every part must have something in it, so a half-typed line reports the
     * hint instead of storing a task with no description or no date.</p>
     *
     * @param details    Everything the user typed after the command word.
     * @param hint       What to tell the user when the line cannot be read.
     * @param separators The separators this command uses, in the order they are
     *                   expected to appear.
     * @return One more part than there are separators, each trimmed and not empty.
     * @throws PiplupBotException If a separator is missing or repeated, or a part
     *                            is empty.
     */
    private static String[] splitIntoParts(String details, String hint, String... separators)
            throws PiplupBotException {
        assert separators.length > 0 : "splitIntoParts() was given nothing to split on";

        String[] parts = new String[separators.length + 1];
        int partStart = 0;
        for (int i = 0; i < separators.length; i++) {
            // Searching from partStart rather than from the start of the line is
            // what puts the separators in order: each is found only after the
            // part it ends, so "event lunch /to dinner /from ... /to ..." keeps
            // the first "/to" as ordinary text.
            int separatorIndex = details.indexOf(separators[i], partStart);
            if (separatorIndex < 0) {
                throw new PiplupBotException(hint);
            }
            requireNoRepeat(details, separators[i], separatorIndex);
            parts[i] = details.substring(partStart, separatorIndex).trim();
            partStart = separatorIndex + separators[i].length();
        }
        // Whatever follows the last separator is the last part.
        parts[parts.length - 1] = details.substring(partStart).trim();

        if (Arrays.stream(parts).anyMatch(String::isEmpty)) {
            throw new PiplupBotException(hint);
        }
        return parts;
    }

    /**
     * Checks that a separator does not appear again after the place it was
     * found, as the second {@code /by} does in
     * {@code deadline return book /by 2019-10-15 1800 /by 2019-10-16 1800}.
     *
     * <p>A second copy is most likely a part typed twice. Without this check it
     * would be swallowed into the part the first copy opens, and the user would
     * be told that {@code 2019-10-15 1800 /by 2019-10-16 1800} is not a date --
     * true, but little help in finding what to fix.</p>
     *
     * <p>The search starts one character after the first copy rather than at its
     * end, so two copies sharing the space between them, as in
     * {@code " /by /by "}, still count as two.</p>
     *
     * @param details   Everything the user typed after the command word.
     * @param separator The separator that was found, e.g. {@code " /by "}.
     * @param position  Where in {@code details} it was found.
     * @throws PiplupBotException If the separator appears again further on.
     */
    private static void requireNoRepeat(String details, String separator, int position)
            throws PiplupBotException {
        if (details.indexOf(separator, position + 1) >= 0) {
            throw new PiplupBotException(
                    "Pip... Please give the " + separator.trim() + " part only once.");
        }
    }

    /**
     * Returns the text to look for in a line of the form
     * {@code find <keyword>}.
     *
     * <p>What follows the command word is taken whole, spaces and all, rather
     * than being split into words: {@code find read book} looks for the phrase
     * "read book", not for either word on its own. That is the simpler rule and
     * the one a user is likely to expect from a single line of text; searching
     * for several words at once would need a way to say whether all of them or
     * any of them must match, which nothing in the requirements asks for.</p>
     *
     * @param input The whole line the user typed.
     * @return The text to look for.
     * @throws PiplupBotException If nothing follows the command word.
     */
    private static String parseKeyword(String input) throws PiplupBotException {
        String keyword = CommandWord.FIND.extractArgument(input);
        // A bare "find" would otherwise match every task, since every string
        // contains the empty string -- a confusing way to answer a line that
        // never said what to look for.
        if (keyword.isEmpty()) {
            throw new PiplupBotException("Pip... Please tell me what to look for, e.g. find book.");
        }
        return keyword;
    }

    /**
     * Returns the command that a line of the form {@code sort <key>} or
     * {@code sort <key> <direction>} asks for.
     *
     * <p>The key has to be given, for the reason a bare {@code find} is refused:
     * a line that never said what to sort by would otherwise be answered by
     * quietly picking a key on the user's behalf. The direction may be left out,
     * and then means {@link SortDirection#ASC} -- the ordinary case, so the
     * ordinary line stays short.</p>
     *
     * <p>Everything after the key is read as the direction, spaces and all,
     * rather than just the next word. That is what makes
     * {@code sort date desc now} report a direction it could not understand
     * instead of quietly ignoring the word it had no room for.</p>
     *
     * @param input The whole line the user typed.
     * @return The command the line asks for.
     * @throws PiplupBotException If no key is given, or the key or the direction
     *                            is not one this bot knows.
     */
    private static SortCommand parseSort(String input) throws PiplupBotException {
        String argument = CommandWord.SORT.extractArgument(input);
        if (argument.isEmpty()) {
            throw new PiplupBotException("Pip... Please tell me what to sort by, e.g. sort date.",
                    SortKey.getKeywordHint());
        }

        // Split once rather than on every space, so that the key is the first
        // word and whatever follows it stays in one piece.
        String[] parts = argument.split("\\s+", 2);
        SortKey key = SortKey.fromKeyword(parts[0]);
        SortDirection direction = parts.length < 2
                ? SortDirection.ASC
                : SortDirection.fromKeyword(parts[1]);
        return new SortCommand(key, direction);
    }

    /**
     * Returns the task number that follows a command such as {@code mark},
     * {@code unmark} or {@code delete}.
     * It only reads the number; whether any task has that number is
     * {@link TaskList}'s question, and what happens to the task is the caller's,
     * which is why all three commands can share this one method.
     *
     * @param input       The whole line the user typed.
     * @param commandWord The command the line names.
     * @return The number typed after the command word.
     * @throws PiplupBotException If what follows the command word is not a whole
     *                            number written with the digits 0 to 9 alone.
     */
    private static int parseTaskNumber(String input, CommandWord commandWord)
            throws PiplupBotException {
        // Everything after the command word should be the task number.
        // extractArgument() copes with the word on its own, e.g. a bare "mark", which
        // leaves an empty argument that the check below rejects like any other
        // non-number.
        String argument = commandWord.extractArgument(input);
        String hint = "Pip... Please give me a task number, e.g. " + commandWord.getKeyword() + " 2.";

        // parseInt on its own is too forgiving: it accepts "+1", "-1", and digits
        // from other writing systems, such as Arabic-Indic ones. None of those is
        // how list shows a task number, so only plain digits are let through.
        if (!argument.matches(TASK_NUMBER_PATTERN)) {
            throw new PiplupBotException(hint);
        }

        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            // Only digits get this far, so the number is too large for an int --
            // more tasks than any list could hold. Java's own exception is
            // translated into the bot's own kind, so that the main loop has just
            // one kind of error to report.
            throw new PiplupBotException(hint);
        }
    }
}
