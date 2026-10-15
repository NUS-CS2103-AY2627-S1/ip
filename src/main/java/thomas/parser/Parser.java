package thomas.parser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import thomas.ThomasException;
import thomas.command.AddCommand;
import thomas.command.Command;
import thomas.command.DeleteCommand;
import thomas.command.ExitCommand;
import thomas.command.FindCommand;
import thomas.command.Keyword;
import thomas.command.ListCommand;
import thomas.command.MarkCommand;
import thomas.command.OnCommand;
import thomas.command.UndoCommand;
import thomas.command.UnmarkCommand;
import thomas.task.DeadlineTask;
import thomas.task.EventTask;
import thomas.task.Task;
import thomas.task.TodoTask;

/**
 * Makes sense of one line the user typed.
 * <p>
 * {@link #parse} is the whole of this class from the outside: hand it a line and
 * it hands back a {@link Command} ready to be carried out. Everything that knows
 * the shape of the input -- where the keyword ends, that a deadline is written
 * {@code /by} and an event {@code /from ... /to ...}, which mistake each
 * complaint names -- is settled in here, so no other class ever sees the raw
 * text.
 * <p>
 * A {@code Parser} object is the working state of one such call: the line split
 * once, so the methods reading the arguments do not each split it again. That is
 * why the constructor is private, and why nothing outside holds one.
 * <p>
 * Every failure leaves as a {@link ThomasException} carrying a message meant for
 * the user, so the caller has one kind of error to report and no Java class name
 * ever reaches the screen.
 */
public class Parser {
    /** Told to a {@code todo} given no description. */
    private static final String MESSAGE_EMPTY_TODO =
            "Bust my buffers! A todo needs a description before I can couple it up.";

    /** Told to a {@code deadline} given no description. */
    private static final String MESSAGE_EMPTY_DEADLINE =
            "Bust my buffers! A deadline needs a description before I can couple it up.";

    /** Told to an {@code event} given no description. */
    private static final String MESSAGE_EMPTY_EVENT =
            "Bust my buffers! An event needs a description before I can couple it up.";

    /** Told to a {@code deadline} whose {@code /by} is missing or has no date. */
    private static final String MESSAGE_MISSING_BY =
            "When is it due? A deadline needs a /by before I can pull it.";

    /** Told to an {@code event} whose {@code /from} is missing or has no date. */
    private static final String MESSAGE_MISSING_FROM =
            "When does it set off? An event needs a /from.";

    /** Told to an {@code event} whose {@code /to} is missing or has no date. */
    private static final String MESSAGE_MISSING_TO =
            "When does it arrive? An event needs a /to after its /from.";

    /** Told to a line that is empty or only spaces. */
    private static final String MESSAGE_BLANK_LINE =
            "Peep? I didn't catch a signal. Give me a command, such as list or todo.";

    /** Told to a {@code deadline} given an event's marker. */
    private static final String MESSAGE_DEADLINE_WRONG_MARKER =
            "Bust my buffers! A deadline takes just a /by -- there's no /from or /to on it.";

    /** Told to an {@code event} given a deadline's marker. */
    private static final String MESSAGE_EVENT_WRONG_MARKER =
            "Bust my buffers! An event takes a /from and a /to -- there's no /by on it.";

    /** Told to an {@code event} whose {@code /to} comes before its {@code /from}. */
    private static final String MESSAGE_MARKERS_SWAPPED =
            "Bust my buffers! Your event's /to came before its /from. Set off first, then arrive.";

    /** The marker a deadline's date follows. */
    private static final String MARKER_BY = "/by";

    /** The marker an event's start follows. */
    private static final String MARKER_FROM = "/from";

    /** The marker an event's end follows. */
    private static final String MARKER_TO = "/to";

    /** The kind of command the line names. */
    private final Keyword keyword;

    /**
     * The line split into keyword and argument.
     * <p>
     * Split at the first run of spaces only, so {@code parts[0]} is the command
     * keyword and {@code parts[1]}, when present, is everything after it --
     * descriptions contain spaces, so the rest of the line must stay in one
     * piece. The argument has been tidied: no spaces at either end, and every
     * run of spaces or tabs inside it squeezed to one space.
     */
    private final String[] parts;

    /**
     * Reads a line far enough to know which command it is.
     * <p>
     * Spacing is tidied before anything is read from the line, so that a
     * stray space at the front, a double space after the keyword or a tab
     * where a space was meant are none of them mistakes: the user typed the
     * command they meant, and the tidying finds it. It is done once here, so
     * every method below can assume single spaces and a clean argument, which
     * is what lets the markers be matched with a space on either side.
     * <p>
     * An unrecognized keyword is rejected here, so the arguments are only ever
     * read for a command that really exists. A line with no words on it at all
     * is told so, rather than being reported as an unknown command.
     *
     * @param line The line exactly as the user typed it.
     * @throws ThomasException If the line is blank, or its first word is not a
     *                         command.
     */
    private Parser(String line) throws ThomasException {
        // Whitespace, not just the space character: a tab pasted in from
        // elsewhere would otherwise ride along as part of the keyword.
        String tidied = line.trim().replaceAll("\\s+", " ");
        if (tidied.isEmpty()) {
            throw new ThomasException(MESSAGE_BLANK_LINE);
        }
        this.parts = tidied.split(" ", 2);
        // split never returns an empty array, so parts[0] is always there to
        // be read as the keyword. Stated because every method below indexes
        // into parts on that basis rather than checking it again.
        assert parts.length >= 1 : "Splitting a line produced no parts: " + line;
        this.keyword = Keyword.of(parts[0]);
    }

    /**
     * Turns a typed line into the command it asks for, arguments and all.
     * <p>
     * Each command is built with what it needs already read and checked, so
     * carrying it out afterwards does no parsing. A line that cannot be
     * understood throws here rather than producing a command that would fail
     * halfway through doing something.
     * <p>
     * The {@code switch} is an expression over an enum, so the compiler checks
     * that every {@link Keyword} is covered: adding a keyword without giving
     * it a command stops the build rather than silently doing nothing at run
     * time. That is what makes a {@code default} branch unnecessary here, where
     * the read loop this replaced needed one.
     *
     * @param fullCommand The line exactly as the user typed it.
     * @return The command that line asks for.
     * @throws ThomasException If the line is blank or not a command Thomas
     *                         understands, or its arguments are missing,
     *                         unreadable, or given to a command that takes none.
     */
    public static Command parse(String fullCommand) throws ThomasException {
        Parser parser = new Parser(fullCommand);
        Command command = switch (parser.keyword) {
            case BYE -> parser.requireNoArgument(new ExitCommand());
            case LIST -> parser.requireNoArgument(new ListCommand());
            case ON -> new OnCommand(parser.parseDay());
            case FIND -> new FindCommand(parser.parseKeyword());
            case MARK -> new MarkCommand(parser.parseTaskNumber("mark"));
            case UNMARK -> new UnmarkCommand(parser.parseTaskNumber("unmark"));
            case DELETE -> new DeleteCommand(parser.parseTaskNumber("delete"));
            case UNDO -> parser.requireNoArgument(new UndoCommand());
            // The three add commands differ only in the task they build, which
            // parseNewTask settles, so one AddCommand serves all three.
            case TODO, DEADLINE, EVENT -> new AddCommand(parser.parseNewTask());
        };
        // This method either returns a command or throws, never both and
        // never neither. Both callers run what comes back without testing it,
        // so a null would reach them as a NullPointerException from inside
        // the run loop rather than from the parsing that produced it.
        assert command != null : "Parsing produced no command for: " + fullCommand;
        return command;
    }

    /**
     * Returns a command that takes no argument, rejecting it if one was given.
     * <p>
     * {@code list all} and {@code bye now} used to be carried out with the
     * extra text ignored. Ignoring it is the wrong side to err on: the user
     * who typed {@code list 2019-12-02} meant something by the date, and
     * silently listing everything answers a question they did not ask. Saying
     * what was not understood lets them find the command they wanted.
     * <p>
     * Takes and returns the command rather than being a bare check, so that
     * the {@code switch} in {@link #parse} reads the same way for every
     * keyword: each arm builds its command in one expression.
     *
     * @param command The command the keyword names.
     * @return That same command.
     * @throws ThomasException If anything follows the keyword.
     */
    private Command requireNoArgument(Command command) throws ThomasException {
        if (parts.length > 1) {
            throw new ThomasException("Bust my buffers! '" + parts[0] + "' is a signal on its own -- "
                    + "I don't know what to do with '" + parts[1] + "'.");
        }
        return command;
    }

    /**
     * Returns this command's argument, rejecting a command given without one.
     * <p>
     * {@code parts} holds a single element when the user typed a bare keyword
     * such as {@code todo}. The constructor has already trimmed the line, so
     * {@code "todo    "} is the same case: it splits into the one part too.
     * <p>
     * Returning the argument rather than only checking it keeps the indexing
     * into {@code parts} in one place, instead of every caller reaching back for
     * {@code parts[1]} after asking whether it exists.
     *
     * @param message What to tell the user when the argument is missing.
     * @return The argument, already tidied of stray spaces.
     * @throws ThomasException If there is no argument.
     */
    private String requireArgument(String message) throws ThomasException {
        if (parts.length < 2) {
            throw new ThomasException(message);
        }
        String argument = parts[1];
        // Every caller treats what comes back as real content -- a task
        // description, a date, a keyword -- and none tests it for emptiness
        // again. That holds only because the constructor trimmed the line
        // before splitting it, so a second part is never empty or all spaces.
        assert !argument.isBlank() : "A split line produced a blank argument";
        return argument;
    }

    /**
     * Returns a description, refusing one that holds the save file's field
     * separator.
     * <p>
     * A description containing {@code " | "} cannot survive being saved: it
     * splits into an extra field, which {@link thomas.storage.Storage} refuses on the way back
     * in rather than loading the description back truncated. Without this check
     * the task is accepted, listed, and then gone on the next run, with a
     * complaint about a save file line the user never knew existed -- a loss
     * noticed long after the moment anything could be done about it. Refusing it
     * as it is typed turns that into an answer the user can act on.
     * <p>
     * Refused here rather than in {@link Task}'s constructor, which is where the
     * event ordering rule lives, because the two rules can be broken by
     * different routes. A save file can hold an event running backwards, so
     * loading has to be held to that rule as well as typing; it cannot hold a
     * description containing the separator, since the field count catches that
     * first. Typing is the only way one can be made, so typing is where it is
     * caught.
     *
     * @param description The description, already trimmed.
     * @return That same description.
     * @throws ThomasException If it contains the field separator.
     */
    private static String requireSeparatorFree(String description) throws ThomasException {
        if (description.contains(Task.FIELD_SEPARATOR)) {
            throw new ThomasException("Bust my buffers! A description can't contain '"
                    + Task.FIELD_SEPARATOR + "' -- that's how I keep my wagons apart in the save file.");
        }
        return description;
    }

    /**
     * Reads the task number given to {@code mark}, {@code unmark} or
     * {@code delete}.
     * <p>
     * Only that the argument is a whole number is settled here. Whether a task
     * actually carries that number is {@link thomas.task.TaskList}'s to answer, since only
     * the list knows how many tasks there are; a parser never sees the list.
     *
     * @param action The command being run, used to word the missing-argument
     *               message, for example {@code "mark"}.
     * @return The number the user typed, counting from 1 and not yet checked
     *         against the list.
     * @throws ThomasException If the number is missing or is not a whole number.
     */
    private int parseTaskNumber(String action) throws ThomasException {
        String argument = requireArgument("Which wagon do you want me to " + action + "? Give me its number.");

        // "mark 1 2" is a real request, for two tasks at once, and the answer
        // is that there is no such thing -- not that "1 2" is not a number.
        if (argument.contains(" ")) {
            throw new ThomasException("One wagon at a time! Give me a single number to " + action + ".");
        }
        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            // parseInt fails the same way for "abc" and for a number too big
            // for an int. The second is all digits and is a number, just one
            // no list will ever reach, so it gets the range answer rather
            // than being told it is not a number.
            if (argument.matches("\\d+")) {
                throw new ThomasException("There's no wagon " + argument + " on my train! "
                        + "No train is that long.");
            }
            throw new ThomasException("Bust my buffers! That's not a number. My wagons are numbered 1, 2, 3...");
        }
    }

    /**
     * Reads the whole day given to {@code on}.
     * <p>
     * Separate from {@link Task#parseDate} because the two read different
     * things. A task happens at a moment, so it needs a date and a time;
     * {@code on} asks about a whole day, so a time would be meaningless there
     * and is refused rather than ignored. Returning a {@link LocalDate} rather
     * than a {@link LocalDateTime} is what says so in the type.
     *
     * @return The day the argument names.
     * @throws ThomasException If the day is missing or is not written as
     *                         {@code yyyy-mm-dd}.
     */
    private LocalDate parseDay() throws ThomasException {
        String text = requireArgument("Which day's timetable do you want to see?");
        try {
            return LocalDate.parse(text);
        } catch (DateTimeParseException e) {
            throw new ThomasException("I can't find '" + text + "' on my timetable! "
                    + "Write the day as 2019-12-02.");
        }
    }

    /**
     * Reads the keyword given to {@code find}.
     * <p>
     * The whole argument is the keyword, spaces and all, so
     * {@code find read book} searches for the phrase rather than for either
     * word: splitting it would make a search of several words mean something
     * the user did not ask for.
     * <p>
     * Only that a keyword was given is settled here. Whether any task contains
     * it is {@link thomas.task.TaskList}'s to answer, since a parser never sees the list.
     *
     * @return the text to search for, with surrounding spaces removed
     * @throws ThomasException if no keyword was given
     */
    private String parseKeyword() throws ThomasException {
        return requireArgument("What am I looking for? Give me a word to search the yard for.");
    }

    /**
     * Builds the task that {@code todo}, {@code deadline} or {@code event}
     * describes.
     * <p>
     * The three add commands are read through one method because the caller
     * treats them alike: whichever was typed, it ends with a task to append and
     * announce. Which subclass comes back is this method's business, and the
     * caller only handles a {@link Task}.
     * <p>
     * A task is returned only if every part of it parsed, so a half-built task
     * never escapes to be added to the list.
     *
     * @return The task the line describes.
     * @throws ThomasException If a description, marker or date is missing or unreadable.
     */
    private Task parseNewTask() throws ThomasException {
        return switch (keyword) {
            case TODO -> parseTodo();
            case DEADLINE -> parseDeadline();
            case EVENT -> parseEvent();
            // Reached only by calling this for a command that adds no task, which
            // is a mistake in the caller rather than anything the user did.
            default -> throw new AssertionError("Not an add command: " + keyword);
        };
    }

    /**
     * Builds the task {@code todo <description>} describes.
     *
     * @return The new todo.
     * @throws ThomasException If the description is missing, or contains the
     *                         save file's field separator.
     */
    private Task parseTodo() throws ThomasException {
        return new TodoTask(requireSeparatorFree(requireArgument(MESSAGE_EMPTY_TODO)));
    }

    /**
     * Splits an argument at a marker, naming whichever part is missing.
     * <p>
     * Two different mistakes arrive here, because the marker is matched with a
     * leading space so that a word such as {@code standby} is not taken for
     * one. {@link #requireArgument} has already trimmed the argument, so a
     * line that is nothing but {@code /by ...} has no space in front of its
     * marker for the split to match: the marker is there, and it is the
     * description in front of it that is missing.
     *
     * @param arguments The argument to split, already trimmed.
     * @param marker The marker to split at, for example {@code "/by"}.
     * @param emptyDescriptionMessage What to say when the line opens with the marker.
     * @param missingMarkerMessage What to say when the marker is absent.
     * @return The text before the marker, then the text after it.
     * @throws ThomasException If the marker is not there to split at.
     */
    private static String[] splitAtMarker(String arguments, String marker,
            String emptyDescriptionMessage, String missingMarkerMessage) throws ThomasException {
        String[] details = arguments.split(" " + marker + " ", 2);
        if (details.length < 2) {
            if (arguments.startsWith(marker)) {
                throw new ThomasException(emptyDescriptionMessage);
            }
            throw new ThomasException(missingMarkerMessage);
        }
        return details;
    }

    /**
     * Returns whether a marker appears in some text as a word of its own.
     * <p>
     * Padding both with spaces is what makes the match whole-word: {@code /by}
     * is found in {@code "x /by y"} and at either end, but not inside
     * {@code "standby"} or {@code "/byte"}. The constructor has squeezed every
     * run of spaces to one, so a marker cannot hide behind a double space.
     *
     * @param text The text to look in, already tidied.
     * @param marker The marker to look for, for example {@code "/by"}.
     * @return True if the marker is there as a separate word.
     */
    private static boolean containsMarker(String text, String marker) {
        return (" " + text + " ").contains(" " + marker + " ");
    }

    /**
     * Rejects text that holds a marker it should not.
     * <p>
     * Three mistakes are caught this way, each with its own message from the
     * caller: a marker belonging to the other kind of task, a marker given
     * twice, and an event's markers in the wrong order. All three would
     * otherwise reach {@link Task#parseDate} as a "date" with a marker in
     * it, and be reported as an unreadable date -- true, but pointing the
     * user at the wrong part of the line.
     *
     * @param text The text to check, already tidied.
     * @param marker The marker that must not be in it.
     * @param message What to tell the user if it is.
     * @throws ThomasException If the marker is there.
     */
    private static void requireMarkerAbsent(String text, String marker, String message)
            throws ThomasException {
        if (containsMarker(text, marker)) {
            throw new ThomasException(message);
        }
    }

    /**
     * Rejects text in which a marker appears a second time.
     *
     * @param text The text after the marker's first appearance, already tidied.
     * @param marker The marker that has already been read once.
     * @throws ThomasException If the marker is there again.
     */
    private static void requireNotRepeated(String text, String marker) throws ThomasException {
        requireMarkerAbsent(text, marker, "Bust my buffers! You've given " + marker
                + " more than once. Once is all I need.");
    }

    /**
     * Returns a field of a command, rejecting one the user left blank.
     * <p>
     * A marker can be there with nothing after it, as in {@code "... /by  "},
     * and the text in front of one can be missing just as easily. Splitting on
     * the marker finds neither, so each piece is checked once it has been
     * trimmed, and returned so the check sits on the line that reads it.
     *
     * @param field The piece of the line, already trimmed.
     * @param message What to tell the user when it is blank.
     * @return That same field.
     * @throws ThomasException If it is empty.
     */
    private static String requireNonEmpty(String field, String message) throws ThomasException {
        if (field.isEmpty()) {
            throw new ThomasException(message);
        }
        return field;
    }

    /**
     * Builds the task {@code deadline <description> /by <date>} describes.
     *
     * @return The new deadline.
     * @throws ThomasException If the description, the marker or the date is missing,
     *                         the marker is given twice or is an event's, the
     *                         date cannot be read, or the description contains
     *                         the save file's field separator.
     */
    private Task parseDeadline() throws ThomasException {
        String arguments = requireArgument(MESSAGE_EMPTY_DEADLINE);

        // Checked before splitting, so "deadline x /from ..." is told it has
        // the wrong marker rather than that its /by is missing: the user has
        // mixed up the two commands, and that is the mistake worth naming.
        requireMarkerAbsent(arguments, MARKER_FROM, MESSAGE_DEADLINE_WRONG_MARKER);
        requireMarkerAbsent(arguments, MARKER_TO, MESSAGE_DEADLINE_WRONG_MARKER);

        // "return book /by 2019-12-02 1800"
        //     -> ["return book", "2019-12-02 1800"]
        String[] details = splitAtMarker(arguments, MARKER_BY,
                MESSAGE_EMPTY_DEADLINE, MESSAGE_MISSING_BY);

        String description = requireNonEmpty(details[0].trim(), MESSAGE_EMPTY_DEADLINE);
        // The marker can be present with nothing after it: "... /by".
        String by = requireNonEmpty(details[1].trim(), MESSAGE_MISSING_BY);
        // The split stopped at the first /by, so a second one is still in
        // the text after it.
        requireNotRepeated(by, MARKER_BY);

        LocalDateTime byDate = Task.parseDate(by, "a deadline date");
        return new DeadlineTask(requireSeparatorFree(description), byDate);
    }

    /**
     * Builds the task {@code event <description> /from <date> /to <date>}
     * describes.
     *
     * @return The new event.
     * @throws ThomasException If the description, either marker or either date is
     *                         missing, a marker is given twice, out of order or
     *                         is a deadline's, a date cannot be read, the
     *                         description contains the save file's field
     *                         separator, or the event ends before it starts.
     */
    private Task parseEvent() throws ThomasException {
        String arguments = requireArgument(MESSAGE_EMPTY_EVENT);

        // As for a deadline: a /by means the two commands have been mixed up,
        // and that is the mistake to name.
        requireMarkerAbsent(arguments, MARKER_BY, MESSAGE_EVENT_WRONG_MARKER);

        // Split the markers off one at a time rather than together. Splitting on
        // " /from | /to " at once matches them in any order, so
        // "/to 4pm /from 2pm" would silently swap the two.
        // "meeting /from Mon 2pm /to 4pm" -> ["meeting", "Mon 2pm /to 4pm"]
        String[] afterFrom = splitAtMarker(arguments, MARKER_FROM,
                MESSAGE_EMPTY_EVENT, MESSAGE_MISSING_FROM);

        // A /to in front of the /from is the two markers the wrong way round.
        // Caught here, before the /to is looked for after the /from, where
        // its absence would be reported as a missing /to -- which the user,
        // looking at the /to they typed, could make nothing of.
        requireMarkerAbsent(afterFrom[0], MARKER_TO, MESSAGE_MARKERS_SWAPPED);

        // "Mon 2pm /to 4pm" -> ["Mon 2pm", "4pm"]
        // Not splitAtMarker: what sits in front of a missing /to is the start
        // date rather than the description, so the description complaint that
        // helper can raise would name the wrong part of the line.
        // A space is put in front first so that "/to 4pm", where the start
        // was left out, still splits: the marker is matched with a space on
        // each side, and the tidying in the constructor left none in front of
        // one that directly follows the /from.
        String[] afterTo = (" " + afterFrom[1]).split(" " + MARKER_TO + " ", 2);
        if (afterTo.length < 2) {
            throw new ThomasException(MESSAGE_MISSING_TO);
        }

        String description = requireNonEmpty(afterFrom[0].trim(), MESSAGE_EMPTY_EVENT);
        String from = requireNonEmpty(afterTo[0].trim(), MESSAGE_MISSING_FROM);
        String to = requireNonEmpty(afterTo[1].trim(), MESSAGE_MISSING_TO);

        // Each split stopped at the first of its marker, so a repeat is still
        // in whichever piece came after it: a second /from lands in the start
        // or the end, a second /to only in the end.
        requireNotRepeated(from, MARKER_FROM);
        requireNotRepeated(to, MARKER_FROM);
        requireNotRepeated(to, MARKER_TO);

        LocalDateTime fromDate = Task.parseDate(from, "a start date");
        LocalDateTime toDate = Task.parseDate(to, "an end date");
        return new EventTask(requireSeparatorFree(description), fromDate, toDate);
    }
}
