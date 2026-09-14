package chione;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import chione.command.AddCommand;
import chione.command.Command;
import chione.command.CommandType;
import chione.command.DeleteCommand;
import chione.command.ExitCommand;
import chione.command.FindCommand;
import chione.command.ListCommand;
import chione.command.MarkCommand;
import chione.command.OnCommand;
import chione.command.UnmarkCommand;
import chione.task.Deadline;
import chione.task.Event;
import chione.task.Task;
import chione.task.Todo;

/**
 * Turns what the user typed into something Chione can act on.
 *
 * <p>Every piece of knowledge about the shape of a command lives here: which
 * keyword starts it, where {@code /by} splits a deadline, and what counts as a
 * usable task number. What comes back is a {@link Command} that already knows how
 * to carry itself out, so {@link Chione} never has to ask what it was handed.
 *
 * <p>Gathering the checks here also gathers the error messages here, so the
 * advice offered when a command is mistyped stays consistent across commands.
 * Each message ends with an example that would actually work, because "invalid
 * input" tells the user nothing about what to type instead.
 *
 * <p>This class only holds static helpers, so it is {@code final} and its
 * constructor is private: there is never any reason to create one.
 */
public final class Parser {
    /** A working deadline, quoted in every message about a mistyped one. */
    private static final String DEADLINE_EXAMPLE = "Try: deadline return book /by 2019-10-15";

    /** A working event, quoted in every message about a mistyped one. */
    private static final String EVENT_EXAMPLE =
            "Try: event project meeting /from 2019-10-15 1400 /to 2019-10-15 1600";

    /**
     * The one character a description may not contain.
     *
     * <p>The save file keeps its columns apart with {@link Task#SEPARATOR}, so a
     * description holding that character would read back as too many columns
     * and the whole file would be refused on the next start. Taken from the
     * separator itself rather than spelled out, so the two cannot disagree.
     */
    private static final String SEPARATOR_CHARACTER = Task.SEPARATOR.trim();

    /** Prevents this utility class from being instantiated. */
    private Parser() {
    }

    /**
     * Turns one line of input into the command it asks for.
     *
     * <p>Spacing is tidied first: spaces around the line and runs of spaces
     * inside it are not something the user meant, so they are dropped rather
     * than reported. A line with nothing else on it is refused before any
     * command is looked for, so it is answered with what to type rather than
     * with a complaint about an unknown command called "".
     *
     * <p>The switch covers every {@link CommandType}, and covers it by name rather
     * than with a fallback branch. A command word added to the enum therefore
     * stops this file compiling until it is given a command to produce here,
     * which is a far better reminder than an error at run time would be.
     *
     * @param input one line of user input, as typed
     * @return the command the user asked for
     * @throws ChioneException if the line is empty, does not name a command, or
     *                         its arguments cannot be used
     */
    public static Command parse(String input) throws ChioneException {
        String line = normalizeSpacing(input);
        if (line.isEmpty()) {
            throw new ChioneException("You haven't typed anything yet. Try: list");
        }

        CommandType type = parseType(line);

        // Stripping the keyword once here means the branches below deal only with
        // what the user typed after it.
        String arguments = type.argumentsOf(line);

        return switch (type) {
            case TODO -> new AddCommand(parseTodo(arguments));
            case DEADLINE -> new AddCommand(parseDeadline(arguments));
            case EVENT -> new AddCommand(parseEvent(arguments));
            case LIST -> new ListCommand();
            case ON -> new OnCommand(parseDate(arguments));
            case FIND -> new FindCommand(parseKeyword(arguments));
            case MARK -> new MarkCommand(parseTaskNumber(arguments, type));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(arguments, type));
            case DELETE -> new DeleteCommand(parseTaskNumber(arguments, type));
            case BYE -> new ExitCommand();
        };
    }

    /**
     * Removes the spacing the user did not mean: spaces at either end of the
     * line, and any run of spaces or tabs inside it, which becomes one space.
     *
     * <p>Done once, before anything else looks at the line, so that
     * {@code "todo   read  book"} is the same command as {@code "todo read book"}
     * everywhere: in the keyword match, in the {@code /by} split, and in the
     * description that is finally stored. Package-private so a test can check
     * the rule directly.
     *
     * @param input one line of user input, as typed
     * @return the line with its spacing tidied
     */
    static String normalizeSpacing(String input) {
        // \s matches any whitespace, and + means "one or more", so every run of
        // whitespace, whatever it is made of, becomes a single space.
        return input.trim().replaceAll("\\s+", " ");
    }

    /**
     * Works out which command word a line of input starts with.
     *
     * <p>A keyword on its own counts as a match, as well as a keyword followed by
     * arguments. That is what lets a bare {@code "todo"} be reported as a missing
     * description rather than as an unknown command.
     *
     * @param input one line of user input, with its spacing already tidied
     * @return the matching command word
     * @throws ChioneException if no command matches
     */
    private static CommandType parseType(String input) throws ChioneException {
        // values() returns every constant of the enum, in the order declared.
        // Written as a stream so that the answer and the refusal sit side by
        // side: with a loop, falling out of the bottom is what means "no match",
        // and the reader has to notice that for themselves.
        return Arrays.stream(CommandType.values())
                .filter(command -> input.equals(command.getKeyword())
                        || input.startsWith(command.getKeyword() + " "))
                .findFirst()
                .orElseThrow(() -> new ChioneException("I don't know what \"" + input + "\" means. "
                        + "I understand: " + listKeywords() + "."));
    }

    /**
     * Builds a todo from the arguments of a {@code todo} command,
     * e.g. {@code "borrow book"}.
     *
     * @param description everything typed after the keyword
     * @return the new todo
     * @throws ChioneException if no description was given, or it cannot be saved
     */
    public static Todo parseTodo(String description) throws ChioneException {
        if (description.isEmpty()) {
            throw new ChioneException("A todo needs a description. Try: todo borrow book");
        }
        requireSavable(description);
        return new Todo(description);
    }

    /**
     * Builds a deadline from the arguments of a {@code deadline} command,
     * e.g. {@code "return book /by 2019-10-15"}.
     *
     * @param arguments everything typed after the keyword
     * @return the new deadline
     * @throws ChioneException if the description or the due date is missing,
     *                         given twice, or unreadable
     */
    public static Deadline parseDeadline(String arguments) throws ChioneException {
        // The limit of 2 stops the split at the first " /by ", so everything after
        // that -- further separators included -- is taken as the date, where
        // hasWord below catches a second /by. The space glued on the front lets
        // "deadline /by Sunday" split as well, so its empty description is
        // reported as such rather than as a missing due date.
        String[] parts = (" " + arguments).split(" /by ", 2);
        if (parts.length < 2) {
            throw new ChioneException("A deadline needs a due date after /by. " + DEADLINE_EXAMPLE);
        }

        String description = parts[0].trim();
        String by = parts[1].trim();
        if (description.isEmpty()) {
            throw new ChioneException("A deadline needs a description before /by. " + DEADLINE_EXAMPLE);
        }
        if (by.isEmpty()) {
            throw new ChioneException("Tell me when it is due after /by. " + DEADLINE_EXAMPLE);
        }
        if (hasWord(by, "/by")) {
            throw new ChioneException("Give /by only once. " + DEADLINE_EXAMPLE);
        }
        requireSavable(description);

        // DateTimes.parse rejects anything that is not a date it can read, so a
        // Deadline can never be built around text that only looks like one.
        return new Deadline(description, DateTimes.parse(by));
    }

    /**
     * Builds an event from the arguments of an {@code event} command,
     * e.g. {@code "meeting /from 2019-10-15 1400 /to 2019-10-15 1600"}.
     *
     * <p>Besides the shape of the line, the two moments themselves are checked
     * against each other: an event that ends before it starts, or at the very
     * moment it starts, is refused, because nothing sensible can be shown for
     * it and the {@code on} command could never find it.
     *
     * @param arguments everything typed after the keyword
     * @return the new event
     * @throws ChioneException if the description, the start or the end is
     *                         missing, out of order, given twice or unreadable,
     *                         or if the event does not end after it starts
     */
    public static Event parseEvent(String arguments) throws ChioneException {
        // Peel off the description first, then the start time, leaving the end time.
        // The glued-on spaces serve the same purpose as in parseDeadline.
        String[] descriptionAndRest = (" " + arguments).split(" /from ", 2);
        if (descriptionAndRest.length < 2) {
            throw new ChioneException("An event needs a start time after /from. " + EVENT_EXAMPLE);
        }
        if (hasWord(descriptionAndRest[0], "/to")) {
            // A /to in front of the /from would otherwise be swallowed into the
            // description, and the user told that the end time is missing when
            // it is merely in the wrong place.
            throw new ChioneException("Put /from before /to. " + EVENT_EXAMPLE);
        }

        String[] fromAndTo = (" " + descriptionAndRest[1]).split(" /to ", 2);
        if (fromAndTo.length < 2) {
            throw new ChioneException("An event needs an end time after /to. " + EVENT_EXAMPLE);
        }

        String description = descriptionAndRest[0].trim();
        String from = fromAndTo[0].trim();
        String to = fromAndTo[1].trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new ChioneException("An event needs a description, a start and an end. " + EVENT_EXAMPLE);
        }
        if (hasWord(from, "/from") || hasWord(to, "/from") || hasWord(to, "/to")) {
            throw new ChioneException("Give /from and /to once each. " + EVENT_EXAMPLE);
        }
        requireSavable(description);

        LocalDateTime start = DateTimes.parse(from);
        LocalDateTime end = DateTimes.parse(to);
        if (!end.isAfter(start)) {
            throw new ChioneException("An event has to end after it starts. This one starts at "
                    + DateTimes.format(start) + " and ends at " + DateTimes.format(end) + ".");
        }
        return new Event(description, start, end);
    }

    /**
     * Extracts the list index of the task referred to by a command such as
     * {@code mark}, {@code unmark} or {@code delete}.
     *
     * <p>The user counts tasks from 1 but the list is indexed from 0, so the
     * number typed is decremented by one.
     *
     * <p>Whether the number actually points at a task is mostly not decided
     * here: {@link chione.task.TaskList TaskList} knows how long it is and
     * refuses a position it does not hold. What this method insists on is that a
     * number was given at all, and that it is at least 1, since no list of any
     * length has a task 0 or a task -3.
     *
     * @param arguments everything typed after the keyword, e.g. {@code "2"}
     * @param command   the command being carried out, quoted back in error messages
     * @return the index into the task list
     * @throws ChioneException if the number is missing, is not a number, or is below 1
     */
    public static int parseTaskNumber(String arguments, CommandType command)
            throws ChioneException {
        String keyword = command.getKeyword();
        if (arguments.isEmpty()) {
            throw new ChioneException("Tell me which task to " + keyword + ". "
                    + "Try: " + keyword + " 2");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(arguments);
        } catch (NumberFormatException e) {
            // Integer.parseInt reports a parsing failure; the user needs to be told
            // what to type instead, so the low-level exception is translated here.
            throw new ChioneException("\"" + arguments + "\" is not a task number. "
                    + "Try: " + keyword + " 2");
        }

        if (taskNumber < 1) {
            throw new ChioneException("Task numbers start at 1. Try: " + keyword + " 2");
        }
        return taskNumber - 1;
    }

    /**
     * Extracts the day referred to by an {@code on} command.
     *
     * <p>{@link DateTimes} knows what a date looks like; what this adds is that an
     * {@code on} with nothing after it is a missing argument rather than an
     * unreadable date, and deserves to be told so.
     *
     * @param arguments everything typed after {@code on}, e.g. {@code "2019-10-15"}
     * @return the day being asked about
     * @throws ChioneException if no day was given, or it is not a readable date
     */
    public static LocalDate parseDate(String arguments) throws ChioneException {
        if (arguments.isEmpty()) {
            throw new ChioneException("Tell me which day to look at. Try: on 2019-10-15");
        }
        return DateTimes.parseDate(arguments);
    }

    /**
     * Extracts the word to search for from a {@code find} command.
     *
     * <p>An empty search would match every task, which is what {@code list}
     * already does, so it is refused rather than answered.
     *
     * @param arguments everything typed after {@code find}, e.g. {@code "book"}
     * @return the word to search for
     * @throws ChioneException if nothing was given to search for
     */
    public static String parseKeyword(String arguments) throws ChioneException {
        if (arguments.isEmpty()) {
            throw new ChioneException("Tell me what to look for. Try: find book");
        }
        return arguments;
    }

    /**
     * Refuses a description that could not be read back from the save file.
     *
     * <p>Checked when the task is typed rather than when it is saved, because
     * by then it is already in the list and the user has been told it was
     * added. Refusing it here costs one retype; refusing it later would cost
     * the whole file on the next start.
     *
     * @param description what the user wants to do
     * @throws ChioneException if the description contains the save file's separator
     */
    private static void requireSavable(String description) throws ChioneException {
        if (description.contains(SEPARATOR_CHARACTER)) {
            throw new ChioneException("A description can't contain \"" + SEPARATOR_CHARACTER
                    + "\": that is what keeps my save file in columns. Leave it out and try again.");
        }
    }

    /**
     * Reports whether a separator word such as {@code /by} appears on its own
     * somewhere in a piece of text.
     *
     * <p>Spaces are glued on both ends so that a word at the very start or end
     * is found, while {@code "/bye"} or {@code "a/to"} is not: only the whole
     * word, set off by spaces, counts. That is the same rule the splits above
     * use, so what they treat as a separator and what this finds agree.
     *
     * @param text the text to look in, with its spacing already tidied
     * @param word the separator word, e.g. {@code "/by"}
     * @return {@code true} if the word appears on its own in the text
     */
    private static boolean hasWord(String text, String word) {
        return (" " + text + " ").contains(" " + word + " ");
    }

    /**
     * Returns every keyword as a comma-separated list, for use in error messages.
     *
     * <p>Building this from {@code values()} keeps the message honest: a command
     * added later shows up here without anyone having to remember to update it.
     *
     * @return e.g. {@code "todo, deadline, event, list, on, ..."}
     */
    private static String listKeywords() {
        return Arrays.stream(CommandType.values())
                .map(CommandType::getKeyword)
                .collect(Collectors.joining(", "));
    }
}
