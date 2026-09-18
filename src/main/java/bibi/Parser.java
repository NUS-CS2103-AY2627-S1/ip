package bibi;

import java.time.LocalDate;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bibi.command.AddCommand;
import bibi.command.Command;
import bibi.command.DeleteCommand;
import bibi.command.ExitCommand;
import bibi.command.FindCommand;
import bibi.command.HelpCommand;
import bibi.command.ListCommand;
import bibi.command.MarkCommand;
import bibi.command.OnCommand;
import bibi.command.SocialCommand;
import bibi.command.SortCommand;
import bibi.command.UndoCommand;
import bibi.command.UnmarkCommand;
import bibi.task.Deadline;
import bibi.task.Event;
import bibi.task.TaskDateTime;
import bibi.task.Todo;

/**
 * Turns a line of typed text into the {@link Command} it asks for.
 *
 * <p>All knowledge of command words and their syntax lives here, so the rest of
 * the program never inspects raw input. Anything the command needs is worked out
 * while parsing: a {@code deadline} line becomes a finished {@link Deadline}, and
 * {@code mark 2} becomes a number, so a command can assume its input is sound.
 *
 * <p>Input is treated generously where the user's meaning is obvious and strictly
 * where it is not. Any run of whitespace separates the command word from its
 * argument, so a stray tab or a double space is accepted; but a command given an
 * argument it cannot use, or a parameter supplied twice, is rejected rather than
 * silently ignored, because quietly dropping part of what someone typed is how a
 * user ends up with a task that is not what they asked for.
 */
public final class Parser {
    /** The commands that take no argument at all. */
    private static final String COMMANDS_WITHOUT_ARGUMENTS = "list, sort, undo and bye";

    /** The only argument {@code help} accepts, which asks for the worked examples. */
    private static final String EXAMPLES_FLAG = "--examples";

    private static final String USAGE_TODO = "Use todo <description>, for example: todo read book.";
    private static final String USAGE_DEADLINE = "Use deadline <description> /by <time>, "
            + "for example: deadline return book /by 21/12/2026.";
    private static final String USAGE_EVENT = "Use event <description> /from <start> /to <end>, "
            + "for example: event project meeting /from 21/12/2026 1400 /to 21/12/2026 1600.";
    private static final String USAGE_ON = "Use on <date>, for example: on 21/12/2026.";

    /** Matches only complete social inputs, so task descriptions never become conversation. */
    private static final Pattern SOCIAL_INPUT = Pattern.compile("(?i)(hi|hello|hey|thanks)\\s*[.!?,]*");

    /**
     * Finds {@code /by} after whitespace or at the start, in any case. A date
     * beginning with a numeric date or a day and month name may follow
     * immediately, but words such as {@code /bypass} stay in the description.
     *
     * <p>The markers are found in the text as typed rather than in a lowercased
     * copy. Lowercasing can change the length of a string, as a dotted capital
     * I does by becoming two characters, and a position found in the copy would
     * then cut the original in the wrong place.
     */
    private static final Pattern BY_MARKER = Pattern.compile("(?<!\\S)/by(?=\\s|[0-9]+(?:[./-]|\\s+[A-Za-z])|$)",
            Pattern.CASE_INSENSITIVE);

    /** Finds {@code /from} in the same way as {@code BY_MARKER}. */
    private static final Pattern FROM_MARKER = Pattern.compile("(?<!\\S)/from(?=\\s|[0-9]+(?:[./-]|\\s+[A-Za-z])|$)",
            Pattern.CASE_INSENSITIVE);

    /** Finds {@code /to} in the same way as {@code BY_MARKER}. */
    private static final Pattern TO_MARKER = Pattern.compile("(?<!\\S)/to(?=\\s|[0-9]+(?:[./-]|\\s+[A-Za-z])|$)",
            Pattern.CASE_INSENSITIVE);

    /** Matches digits with no sign or a leading plus, whatever their length. */
    private static final Pattern UNSIGNED_DIGITS = Pattern.compile("\\+?\\d+");

    /** Matches digits with a leading minus, whatever their length. */
    private static final Pattern NEGATIVE_DIGITS = Pattern.compile("-\\d+");

    /**
     * Hides the constructor, because this class holds only static helpers and
     * is never meant to be instantiated.
     */
    private Parser() {
    }

    /**
     * Reads one line of user input and returns the command it describes.
     *
     * @param fullCommand the line the user typed
     * @return the command to execute
     * @throws BibiException if the line is empty, unknown, or malformed
     */
    public static Command parse(String fullCommand) throws BibiException {
        String input = fullCommand.strip();
        if (input.isEmpty()) {
            throw new BibiException("Please enter a command.");
        }

        Matcher socialMatch = SOCIAL_INPUT.matcher(input);
        if (socialMatch.matches()) {
            return new SocialCommand(socialMatch.group(1).equalsIgnoreCase("thanks"));
        }

        // Splitting on any run of whitespace means a tab or a double space
        // between the command word and its argument is read as the user meant
        // it, rather than as an unknown command.
        String[] parts = input.split("\\s+", 2);
        String argument = parts.length > 1 ? parts[1].strip() : "";
        return toCommand(parts[0], argument);
    }

    /**
     * Builds the command named by one command word.
     *
     * <p>Kept apart from {@link #parse} so that reading a line and choosing a
     * command stay at their own levels of detail. Everything here is one entry
     * per command word; the work of splitting the line is above.
     *
     * @param typedWord the command word as the user typed it, in their own case
     * @param argument whatever followed the command word, already stripped
     * @return the command that word asks for
     * @throws BibiException if the word is unknown, or its argument malformed
     */
    private static Command toCommand(String typedWord, String argument) throws BibiException {
        String commandWord = typedWord.toLowerCase(Locale.ROOT);
        return switch (commandWord) {
            case "bye" -> {
                requireNoArgument(argument, "bye");
                yield new ExitCommand();
            }
            case "list" -> {
                requireNoArgument(argument, "list");
                yield new ListCommand();
            }
            case "sort" -> {
                requireNoArgument(argument, "sort");
                yield new SortCommand();
            }
            case "help" -> new HelpCommand(parseExamplesFlag(argument));
            case "undo" -> {
                requireNoArgument(argument, "undo");
                yield new UndoCommand();
            }
            case "todo" -> new AddCommand(new Todo(requireDescription(collapseSpaces(argument), USAGE_TODO)));
            case "deadline" -> new AddCommand(parseDeadline(argument));
            case "event" -> new AddCommand(parseEvent(argument));
            case "mark" -> new MarkCommand(parseTaskNumber(argument, "mark"));
            case "unmark" -> new UnmarkCommand(parseTaskNumber(argument, "unmark"));
            case "remove", "delete" -> new DeleteCommand(parseTaskNumber(argument, commandWord));
            case "find" -> new FindCommand(requireKeyword(collapseSpaces(argument)));
            case "on" -> new OnCommand(parseQueryDate(argument));
            // Pointing at help, rather than listing the commands here as well,
            // keeps the full list in one place.
            default -> throw new BibiException("I don't recognize '" + typedWord + "'. "
                    + "Check the command word, or type help for examples.");
        };
    }

    /**
     * Rejects an argument given to a command that does not take one.
     *
     * <p>Ignoring the extra text would be friendlier in the moment and worse
     * overall: someone typing {@code list done} means to filter the list, and
     * showing them everything without a word looks like the filter worked.
     *
     * @param argument whatever followed the command word
     * @param commandWord the command being used, for the message
     * @throws BibiException if anything followed the command word
     */
    private static void requireNoArgument(String argument, String commandWord)
            throws BibiException {
        if (!argument.isEmpty()) {
            throw new BibiException(commandWord + " does not take anything after it, but I found '"
                    + argument + "'. " + COMMANDS_WITHOUT_ARGUMENTS + " are used on their own. "
                    + "Use " + commandWord + ", for example: " + commandWord + ".");
        }
    }

    /**
     * Reads the optional flag that may follow {@code help}.
     *
     * <p>{@code help} is the one command with an optional argument, so it is
     * checked here rather than through {@link #requireNoArgument}. Anything else
     * is still rejected, for the same reason: a user who typed a flag Bibi does
     * not have should be told so, not shown the short help as though they had
     * typed nothing.
     *
     * @param argument whatever followed the command word
     * @return {@code true} when the user asked for the worked examples
     * @throws BibiException if the argument is anything but the examples flag
     */
    private static boolean parseExamplesFlag(String argument) throws BibiException {
        if (argument.isEmpty()) {
            return false;
        }
        if (argument.equalsIgnoreCase(EXAMPLES_FLAG)) {
            return true;
        }
        throw new BibiException("help takes nothing or " + EXAMPLES_FLAG + " after it, but I found '"
                + argument + "'. Use help for the command list, or help " + EXAMPLES_FLAG
                + " for examples and date formats.");
    }

    /**
     * Replaces runs of whitespace inside text with single spaces.
     *
     * <p>Applied to text the user typed freely, so that "read    book" and
     * "read book" are stored as the same description and are recognized as
     * duplicates of each other.
     *
     * @param text the text to tidy
     * @return the text with its inner spacing normalized
     */
    private static String collapseSpaces(String text) {
        return text.replaceAll("\\s+", " ").strip();
    }

    /**
     * Checks that a parameter such as {@code /by} was supplied exactly once.
     *
     * @param text the text being parsed
     * @param markerPattern the pattern used to find the parameter
     * @param marker the parameter to name, for example {@code /by}
     * @param usage the valid command form and example
     * @throws BibiException if the parameter appears more than once
     */
    private static void requireSingleUse(String text, Pattern markerPattern, String marker, String usage)
            throws BibiException {
        long useCount = markerPattern.matcher(text).results().count();
        if (useCount > 1) {
            throw new BibiException("You used " + marker + " " + useCount + " times, but it belongs "
                    + "exactly once. I can't tell which one you meant. " + usage);
        }
    }

    /**
     * Rejects a missing description with the syntax for the task being added.
     *
     * @param description the normalized task description
     * @param usage the valid command form and example
     * @return the description when it is present
     * @throws BibiException if the description is empty
     */
    private static String requireDescription(String description, String usage) throws BibiException {
        if (description.isEmpty()) {
            throw new BibiException("A task needs a description. " + usage);
        }
        return description;
    }

    /**
     * Builds a deadline from text in the form {@code description /by date}.
     *
     * @param deadlineText the text after the {@code deadline} command word
     * @return the finished deadline
     * @throws BibiException if the text is missing required information
     */
    private static Deadline parseDeadline(String deadlineText) throws BibiException {
        requireSingleUse(deadlineText, BY_MARKER, "/by", USAGE_DEADLINE);

        Matcher byMatch = BY_MARKER.matcher(deadlineText);
        if (!byMatch.find()) {
            throw new BibiException("I need a due date marked with /by. " + USAGE_DEADLINE);
        }

        String description = requireDescription(collapseSpaces(deadlineText.substring(0, byMatch.start())),
                USAGE_DEADLINE);
        String dueTimeText = collapseSpaces(deadlineText.substring(byMatch.end()));
        if (dueTimeText.isEmpty()) {
            throw new BibiException("I need a due date after /by. " + USAGE_DEADLINE);
        }
        return new Deadline(description, dueTimeText);
    }

    /**
     * Builds an event from text in the form
     * {@code description /from start /to end}.
     *
     * @param eventText the text after the {@code event} command word
     * @return the finished event
     * @throws BibiException if the text is missing required information
     */
    private static Event parseEvent(String eventText) throws BibiException {
        requireSingleUse(eventText, FROM_MARKER, "/from", USAGE_EVENT);
        requireSingleUse(eventText, TO_MARKER, "/to", USAGE_EVENT);

        Matcher fromMatch = FROM_MARKER.matcher(eventText);
        Matcher toMatch = TO_MARKER.matcher(eventText);
        boolean hasFrom = fromMatch.find();
        boolean hasTo = toMatch.find();

        if (hasFrom && hasTo && toMatch.start() < fromMatch.start()) {
            throw new BibiException("The /to came before the /from. "
                    + USAGE_EVENT);
        }
        if (!hasFrom || !hasTo) {
            throw new BibiException("I need both /from and /to dates for an event. " + USAGE_EVENT);
        }

        String description = requireDescription(collapseSpaces(eventText.substring(0, fromMatch.start())),
                USAGE_EVENT);
        String startTimeText = collapseSpaces(eventText.substring(fromMatch.end(), toMatch.start()));
        String endTimeText = collapseSpaces(eventText.substring(toMatch.end()));
        if (startTimeText.isEmpty() || endTimeText.isEmpty()) {
            String missingMarker = startTimeText.isEmpty() ? "/from" : "/to";
            throw new BibiException("I need a date after " + missingMarker + ". " + USAGE_EVENT);
        }
        return new Event(description, startTimeText, endTimeText);
    }

    /**
     * Checks that a search was given something to look for.
     *
     * @param keyword the text supplied after {@code find}
     * @return the keyword when it is usable
     * @throws BibiException if no keyword was supplied
     */
    private static String requireKeyword(String keyword) throws BibiException {
        if (keyword.isEmpty()) {
            throw new BibiException("Use find followed by a keyword, for example: find book");
        }
        return keyword;
    }

    /**
     * Reads the date that a query such as {@code on 21/12/2026} asks about.
     *
     * <p>A missing date is answered here rather than left to the date parser,
     * which knows what a date should look like but not which command wanted
     * one. Naming the command and an example keeps this rejection as useful as
     * every other missing-argument rejection.
     *
     * <p>Any time of day is dropped, since the question is which tasks belong
     * to the day as a whole.
     *
     * @param argument whatever followed the command word
     * @return the calendar date being asked about
     * @throws BibiException if no date was given, or it cannot be read
     */
    private static LocalDate parseQueryDate(String argument) throws BibiException {
        if (argument.isEmpty()) {
            throw new BibiException("I need a date to look up. " + USAGE_ON);
        }
        return TaskDateTime.parse(argument).getDate();
    }

    /**
     * Reads the task number that a command such as {@code mark 2} refers to.
     *
     * @param numberText the text supplied after the command word
     * @param commandWord the command word, used in the error message
     * @return the one-based task number
     * @throws BibiException if the text is not a usable task number
     */
    private static int parseTaskNumber(String numberText, String commandWord)
            throws BibiException {
        if (numberText.isEmpty()) {
            throw new BibiException("Use " + commandWord + " followed by a task number, "
                    + "for example: " + commandWord + " 2");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(numberText);
        } catch (NumberFormatException notANumber) {
            // A run of digits too long to fit an int is still a number, so
            // calling it "not a task number" would tell the user something
            // untrue. Which end it overflows decides which explanation fits.
            if (NEGATIVE_DIGITS.matcher(numberText).matches()) {
                throw belowOneException(numberText, commandWord);
            }
            if (UNSIGNED_DIGITS.matcher(numberText).matches()) {
                throw new BibiException("Task numbers do not go as high as " + numberText
                        + ". Use list to see the numbers. Use " + commandWord
                        + " <number>, for example: " + commandWord + " 1.");
            }
            throw new BibiException("'" + numberText + "' is not a task number. Use "
                    + commandWord + " followed by one number, for example: " + commandWord + " 2");
        }

        // Caught here rather than in TaskList so the message can say what is
        // wrong with the number itself, not merely that no such task exists.
        if (taskNumber < 1) {
            throw belowOneException(String.valueOf(taskNumber), commandWord);
        }
        return taskNumber;
    }

    /**
     * Builds the complaint about a task number that is below the first one.
     *
     * <p>Takes the number as text because it is raised both for a number that
     * parsed and for one too large and negative to parse at all.
     *
     * @param numberText the number as the user wrote it
     * @param commandWord the command word, used in the message
     * @return the exception to throw
     */
    private static BibiException belowOneException(String numberText, String commandWord) {
        return new BibiException("Task numbers start at 1, so " + numberText
                + " cannot refer to a task. Use list to see the numbers. Use " + commandWord
                + " <number>, for example: " + commandWord + " 1.");
    }
}
