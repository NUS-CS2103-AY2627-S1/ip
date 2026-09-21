package ted;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ted.command.AddCommand;
import ted.command.Command;
import ted.command.CommandType;
import ted.command.DeleteCommand;
import ted.command.ExitCommand;
import ted.command.FindByTagCommand;
import ted.command.FindCommand;
import ted.command.ListCommand;
import ted.command.MarkCommand;
import ted.command.TagCommand;
import ted.task.Deadline;
import ted.task.Event;
import ted.task.Tag;
import ted.task.Todo;

/**
 * Turns a line typed by the user into the {@link Command} it stands for.
 * <p>
 * All of the fiddly work of reading user input lives here: splitting off the
 * command word, finding {@code /by}, {@code /from} and {@code /to}, and
 * reading dates and tags. Because a command is only built once its details
 * make sense, the command classes themselves are free of input checking, and
 * this class can be tested without a keyboard or a save file.
 */
public class Parser {
    /**
     * Format expected for date-times entered in deadline and event commands.
     * The strict resolver is chosen because the default one quietly rounds an
     * impossible date down: 31/2/2019 would otherwise become 28 Feb, giving
     * the user a task on a day they never asked for.
     */
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);

    /** Separator introducing a deadline's due date and time. */
    private static final String OPTION_BY = "/by";

    /** Separator introducing an event's start time. */
    private static final String OPTION_FROM = "/from";

    /** Separator introducing an event's end time. */
    private static final String OPTION_TO = "/to";

    /** Parser is used through its static methods only, so it is never created. */
    private Parser() {
    }

    /**
     * Reads one line of user input as a command.
     *
     * @param input full line of text entered by the user, not blank.
     * @return the command the user asked for.
     * @throws TedException if the command is unknown or its details are unusable.
     */
    public static Command parse(String input) throws TedException {
        assert !input.isBlank() : "Ted skips blank input, so it never reaches the parser";

        // Any run of spaces or tabs is read as a single space, so that a stray
        // double space, e.g. between a date and its time, cannot make an
        // otherwise valid command unreadable.
        String normalizedInput = input.strip().replaceAll("\\s+", " ");

        // Splitting into at most two parts keeps the command word exact, so that
        // "todos" is not mistaken for "todo", while leaving the rest untouched.
        String[] parts = normalizedInput.split(" ", 2);
        String argument = parts.length > 1 ? parts[1] : "";
        CommandType commandType = CommandType.fromKeyword(parts[0]);

        return switch (commandType) {
            case LIST -> {
                requireNoArgument(argument, commandType);
                yield new ListCommand();
            }
            case MARK -> new MarkCommand(parseTaskIndex(argument, CommandType.MARK), true);
            case UNMARK -> new MarkCommand(parseTaskIndex(argument, CommandType.UNMARK), false);
            case DELETE -> new DeleteCommand(parseTaskIndex(argument, CommandType.DELETE));
            case FIND -> parseFind(argument);
            case TAG -> parseTagCommand(argument, CommandType.TAG, true);
            case UNTAG -> parseTagCommand(argument, CommandType.UNTAG, false);
            case TODO -> new AddCommand(parseTodo(argument));
            case DEADLINE -> new AddCommand(parseDeadline(argument));
            case EVENT -> new AddCommand(parseEvent(argument));
            case BYE -> {
                requireNoArgument(argument, commandType);
                yield new ExitCommand();
            }
        };
    }

    /**
     * Reads a search. A keyword starting with {@code #} is a tag, which matches
     * only the tasks that have exactly that tag; any other keyword is text to
     * find inside descriptions.
     *
     * @param argument everything the user typed after the command word.
     * @return the command that shows the matching tasks.
     * @throws TedException if no keyword was given, or it starts with {@code #}
     *                      but is not a valid tag.
     */
    private static Command parseFind(String argument) throws TedException {
        String keyword = parseKeyword(argument);
        if (keyword.startsWith(Tag.PREFIX)) {
            return new FindByTagCommand(parseTag(keyword, "Try: find #fun"));
        }
        return new FindCommand(keyword);
    }

    /**
     * Reads the keyword to search for.
     *
     * @param argument everything the user typed after the command word.
     * @return the keyword, with surrounding spaces removed.
     * @throws TedException if no keyword was given.
     */
    private static String parseKeyword(String argument) throws TedException {
        requireNotBlank(argument, "Find what, exactly? Give me a word to look for. Try: find book");
        return argument;
    }

    /**
     * Reads a task with no date attached.
     *
     * @param description what the user wants to get done.
     * @return the new todo.
     * @throws TedException if the description is missing.
     */
    private static Todo parseTodo(String description) throws TedException {
        requireNotBlank(description, "A todo about nothing? Tell me what it is. Try: todo borrow book");
        return new Todo(description);
    }

    /**
     * Reads a task due by a given date and time, from an argument of the form
     * {@code <description> /by <d/M/uuuu HHmm>}.
     *
     * @param argument everything the user typed after the command word.
     * @return the new deadline.
     * @throws TedException if the description or due date-time is missing,
     *                      repeated or unreadable.
     */
    private static Deadline parseDeadline(String argument) throws TedException {
        String example = "Try: deadline return book /by 25/9/2026 1800";
        requireNotBlank(argument, "A deadline needs a task and a due time. " + example);

        int separator = findOption(argument, OPTION_BY, example);
        if (separator == -1) {
            throw new TedException("When is it due? Add /by and a date and time. " + example);
        }

        String description = argument.substring(0, separator).trim();
        String by = argument.substring(separator + OPTION_BY.length()).trim();
        requireNotBlank(description, "Due by then, sure, but what is? Put the task before /by. " + example);
        requireNotBlank(by, "Due when? Put a date and time after /by. " + example);
        return new Deadline(description, parseDateTime(by, example));
    }

    /**
     * Reads a task spanning two date-times, from an argument of the form
     * {@code <description> /from <d/M/uuuu HHmm> /to <d/M/uuuu HHmm>}.
     *
     * @param argument everything the user typed after the command word.
     * @return the new event.
     * @throws TedException if the description, the start or the end is missing,
     *                      repeated or unreadable, or the event does not end
     *                      after it starts.
     */
    private static Event parseEvent(String argument) throws TedException {
        String example = "Try: event project meeting /from 22/9/2026 1400 /to 22/9/2026 1600";
        requireNotBlank(argument, "An event needs a name, a start and an end. " + example);

        int fromSeparator = findOption(argument, OPTION_FROM, example);
        int toSeparator = findOption(argument, OPTION_TO, example);
        if (fromSeparator == -1 || toSeparator == -1) {
            throw new TedException("An event needs a start and an end, so use both /from and /to. " + example);
        }
        if (toSeparator < fromSeparator) {
            throw new TedException("Other way round, buddy: /from goes before /to. " + example);
        }

        String description = argument.substring(0, fromSeparator).trim();
        String from = argument.substring(fromSeparator + OPTION_FROM.length(), toSeparator).trim();
        String to = argument.substring(toSeparator + OPTION_TO.length()).trim();
        requireNotBlank(description, "What is the event called? Put its name before /from. " + example);
        requireNotBlank(from, "Starts when? Put a date and time after /from. " + example);
        requireNotBlank(to, "Ends when? Put a date and time after /to. " + example);

        LocalDateTime start = parseDateTime(from, example);
        LocalDateTime end = parseDateTime(to, example);
        if (!end.isAfter(start)) {
            // Now that the times are real date-times rather than free text, Ted can
            // spot an impossible event before it is stored. An event that ends the
            // moment it starts is almost always a typo in one of the two times.
            throw new TedException("Even a bear knows the end comes after the start. Make /to later than /from. "
                    + example);
        }
        return new Event(description, start, end);
    }

    /**
     * Reads a tag or untag command, from an argument of the form
     * {@code <task number> #<tag> [#<tag>...]}.
     *
     * @param argument    everything the user typed after the command word.
     * @param commandType {@link CommandType#TAG} or {@link CommandType#UNTAG}, used in error messages.
     * @param isAdding    {@code true} to attach the tags, {@code false} to detach them.
     * @return the command that changes the task's tags.
     * @throws TedException if the task number or the tags are missing or unreadable.
     */
    private static TagCommand parseTagCommand(String argument, CommandType commandType, boolean isAdding)
            throws TedException {
        String example = "Try: " + commandType.getKeyword() + " 2 #fun";
        requireNotBlank(argument, "Which task, and which tags? " + example);

        // parse() has already collapsed runs of whitespace, so single spaces
        // separate the task number and each tag.
        String[] words = argument.split(" ");
        int index = parseTaskIndex(words[0], commandType);
        if (words.length == 1) {
            throw new TedException("Which tags? Start each one with #. " + example);
        }

        // A loop rather than a stream, because parseTag throws a checked
        // exception, which a lambda cannot pass on.
        List<Tag> tags = new ArrayList<>();
        for (int i = 1; i < words.length; i++) {
            tags.add(parseTag(words[i], example));
        }
        return new TagCommand(index, tags, isAdding);
    }

    /**
     * Converts a task number typed by the user into an index into the task list.
     * Whether the number points at a real task is checked by {@link ted.task.TaskList}
     * when the command runs, since only then is the list at hand.
     *
     * @param argument    task number as typed by the user, counting from 1.
     * @param commandType command the number was given to, used in the error message.
     * @return zero-based index of the task.
     * @throws TedException if the number is missing, is not a number, or is
     *                      followed by more numbers.
     */
    private static int parseTaskIndex(String argument, CommandType commandType) throws TedException {
        String example = "Try: " + commandType.getKeyword() + " 2";
        requireNotBlank(argument, "Which task? Give me its number. " + example);
        if (argument.contains(" ")) {
            throw new TedException("One task at a time, buddy. Give me a single number. " + example);
        }

        try {
            // The user counts from 1, the list counts from 0.
            return Integer.parseInt(argument) - 1;
        } catch (NumberFormatException e) {
            // Rethrown as a TedException so the main loop handles every failure the same way.
            throw new TedException("\"" + argument + "\" is not a task number. "
                    + "Use the number shown by list. " + example);
        }
    }

    /**
     * Turns a date and time typed by the user into a {@link LocalDateTime}.
     *
     * @param text    date and time as typed, e.g. {@code 2/12/2019 1800}.
     * @param example wording showing the expected format, used in the error message.
     * @return the date and time the text stands for.
     * @throws TedException if the text is not in the expected format, or names
     *                      a day or time that does not exist.
     */
    private static LocalDateTime parseDateTime(String text, String example) throws TedException {
        try {
            return LocalDateTime.parse(text, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            // Rethrown as a TedException so the main loop reports it like any other
            // problem with the user's input, instead of crashing.
            if (e.getCause() != null) {
                // The formatter attaches a cause only when the text has the right
                // shape but names a date or time that does not exist, e.g. 30/2/2019.
                throw new TedException("\"" + text + "\" is not a real date and time. "
                        + "Check the day, month and time. " + example);
            }
            throw new TedException("I can't read \"" + text + "\" as a date and time. "
                    + "Write it as d/M/yyyy HHmm. " + example);
        }
    }

    /**
     * Turns one tag typed by the user, e.g. {@code #Fun}, into a {@link Tag}.
     *
     * @param text    one word as typed.
     * @param example wording showing how the command is used, used in the error message.
     * @return the tag the word stands for.
     * @throws TedException if the word is not {@code #} followed by letters and digits.
     */
    private static Tag parseTag(String text, String example) throws TedException {
        if (!Tag.isValidText(text)) {
            throw new TedException("\"" + text + "\" is not a tag. "
                    + "A tag is # followed by letters and digits. " + example);
        }
        return Tag.fromText(text);
    }

    /**
     * Returns where an option such as {@code /to} starts in the argument.
     * Only a standalone word counts, so that the {@code /to} inside a
     * description like "lunch w/tom" is not mistaken for the separator.
     *
     * @param argument everything the user typed after the command word.
     * @param option   the option to look for, e.g. {@code /by}.
     * @param example  wording showing how the command is used, used in the error message.
     * @return index of the option's standalone use, or -1 if there is none.
     * @throws TedException if the option is used more than once.
     */
    private static int findOption(String argument, String option, String example) throws TedException {
        // The lookarounds require whitespace or the edge of the text on both
        // sides, without making that whitespace part of the match.
        Matcher matcher = Pattern.compile("(?<!\\S)" + Pattern.quote(option) + "(?!\\S)").matcher(argument);
        if (!matcher.find()) {
            return -1;
        }

        int start = matcher.start();
        if (matcher.find()) {
            // With two of them there is no telling which one the user meant.
            throw new TedException("You gave me " + option + " twice, so I can't tell which one you mean. " + example);
        }
        return start;
    }

    /**
     * Rejects anything typed after a command that works on its own, such as
     * {@code list}, so that a command the user got wrong is not quietly half
     * carried out.
     *
     * @param argument    everything the user typed after the command word.
     * @param commandType the command that takes no argument, used in the error message.
     * @throws TedException if anything was typed after the command word.
     */
    private static void requireNoArgument(String argument, CommandType commandType) throws TedException {
        if (!argument.isEmpty()) {
            String keyword = commandType.getKeyword();
            throw new TedException("\"" + keyword + "\" works on its own, so I don't know what to do with \""
                    + argument + "\". Try: " + keyword);
        }
    }

    /**
     * Rejects input the user left out.
     *
     * @param value   text entered by the user.
     * @param message explanation to show if the text is missing.
     * @throws TedException if {@code value} is empty or only whitespace.
     */
    private static void requireNotBlank(String value, String message) throws TedException {
        if (value.isBlank()) {
            throw new TedException(message);
        }
    }
}
