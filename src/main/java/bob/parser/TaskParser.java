package bob.parser;

import static bob.parser.MarkedArguments.BY_KEYWORD;
import static bob.parser.MarkedArguments.DESC_KEYWORD;
import static bob.parser.MarkedArguments.FROM_KEYWORD;
import static bob.parser.MarkedArguments.TO_KEYWORD;

import java.util.List;
import java.util.Optional;

import bob.BobException;
import bob.command.CommandWord;
import bob.task.Deadline;
import bob.task.Event;
import bob.task.TaskDateTime;
import bob.task.Todo;

/**
 * Parses the text following {@link CommandWord#TODO}, {@link CommandWord#DEADLINE}
 * or {@link CommandWord#EVENT} into the task that command adds.
 *
 * <p>These are the commands with the most rules about how they are written: which
 * markers each takes and in what order, which parts may be missing, and whether the
 * dates make sense together. Keeping those rules here leaves {@link Parser} to
 * decide only which command a line is.
 *
 * <p>Finding the markers in the text is left to {@link MarkedArguments}, which
 * {@link EditParser} uses too, so a marker is recognized by the same rules whether a
 * task is being added or changed. What this class adds is which markers each kind of
 * task takes.
 */
class TaskParser {

    /** Example of a well-formed {@link CommandWord#TODO} command, shown when one is malformed. */
    private static final String TODO_EXAMPLE = CommandWord.TODO.getKeyword() + " borrow book";

    /**
     * Example of a well-formed {@link CommandWord#DEADLINE} command, shown when one is malformed.
     *
     * <p>The date in it is taken from {@link TaskDateTime}, which is the class that
     * decides how a date may be written, so this example cannot drift out of step
     * with the dates the chatbot actually accepts.
     */
    private static final String DEADLINE_EXAMPLE =
            CommandWord.DEADLINE.getKeyword() + " return book " + BY_KEYWORD + " "
                    + TaskDateTime.EXAMPLE_DATE;

    /**
     * The end time shown in {@link #EVENT_EXAMPLE}, two hours after the start.
     *
     * <p>Written out here rather than taken from {@link TaskDateTime}, which offers a
     * single example date and not a pair of them. It is the one example date in
     * this package that is spelled out, so if the accepted form of a date ever
     * changes, this is the one line to change with it.
     */
    private static final String EVENT_EXAMPLE_END = "2026-12-02 2000";

    /** Example of a well-formed {@link CommandWord#EVENT} command, shown when one is malformed. */
    private static final String EVENT_EXAMPLE =
            CommandWord.EVENT.getKeyword() + " project meeting " + FROM_KEYWORD + " "
                    + TaskDateTime.EXAMPLE_DATE_TIME + " " + TO_KEYWORD + " " + EVENT_EXAMPLE_END;

    /**
     * Returns the {@link Todo} described by the text following {@link CommandWord#TODO}.
     * Everything the user typed is the description.
     *
     * <p>A todo takes no markers, so text holding one is refused rather than kept as
     * part of the description. A user who writes {@value MarkedArguments#BY_KEYWORD}
     * after a todo most likely meant a deadline, and one who writes
     * {@value MarkedArguments#FROM_KEYWORD} or {@value MarkedArguments#TO_KEYWORD} an
     * event, so each is pointed to that command rather than left with a todo whose
     * description ends in a date.
     *
     * @param arguments the text following the command word.
     * @throws BobException if no description was given, or the text holds a marker.
     */
    static Todo parseTodo(String arguments) throws BobException {
        if (arguments.isEmpty()) {
            throw BobException.withExample("A todo needs a description — tell me what to do.", TODO_EXAMPLE);
        }
        MarkedArguments marked = MarkedArguments.parse(arguments);
        requireNoDescKeyword(marked, CommandWord.TODO, TODO_EXAMPLE);
        if (marked.hasKeyword(BY_KEYWORD)) {
            throw createTodoWithDatesError(CommandWord.DEADLINE, DEADLINE_EXAMPLE);
        }
        if (marked.hasKeyword(FROM_KEYWORD) || marked.hasKeyword(TO_KEYWORD)) {
            throw createTodoWithDatesError(CommandWord.EVENT, EVENT_EXAMPLE);
        }
        return new Todo(arguments);
    }

    /**
     * Returns the {@link Deadline} described by the text following
     * {@link CommandWord#DEADLINE}, which is the description and the due date
     * separated by {@value MarkedArguments#BY_KEYWORD}.
     *
     * <p>A marker a deadline does not take is refused before anything else is looked
     * at. Left in, it would be read as part of the due date, and the user would be
     * told the date could not be read rather than what is actually wrong.
     *
     * <p>The three things that can be missing — the marker, the description before
     * it, the date after it — are reported separately, so the user is told which one
     * to add rather than just that the command is wrong.
     *
     * <p>The due date is handed to {@link TaskDateTime#parse} rather than stored as
     * typed, so a deadline is only built once its date has been understood. Text
     * that is not a date is refused there, with its own explanation.
     *
     * @param arguments the text following the command word.
     * @throws BobException if a marker is written twice, in the wrong case or is not
     *                      one a deadline takes, if the marker, the description or
     *                      the due date is missing, or if the due date is not a date.
     */
    static Deadline parseDeadline(String arguments) throws BobException {
        MarkedArguments marked = MarkedArguments.parse(arguments);
        requireOnlyKeywords(marked, CommandWord.DEADLINE, "A deadline", List.of(BY_KEYWORD),
                DEADLINE_EXAMPLE);
        Optional<String> by = marked.getValue(BY_KEYWORD);
        if (by.isEmpty()) {
            throw BobException.withExample("A deadline needs a due date, written after "
                    + BY_KEYWORD + ".", DEADLINE_EXAMPLE);
        }
        String description = marked.getPreamble();
        if (description.isEmpty()) {
            throw BobException.withExample("A deadline needs a description, written before "
                    + BY_KEYWORD + ".", DEADLINE_EXAMPLE);
        }
        if (by.get().isEmpty()) {
            throw BobException.withExample("You wrote " + BY_KEYWORD + " but not when it is due.",
                    DEADLINE_EXAMPLE);
        }
        return new Deadline(description, TaskDateTime.parse(by.get()));
    }

    /**
     * Returns the {@link Event} described by the text following
     * {@link CommandWord#EVENT}, which is the description, then
     * {@value MarkedArguments#FROM_KEYWORD} and the start, then
     * {@value MarkedArguments#TO_KEYWORD} and the end.
     *
     * <p>The two markers must come in that order. Each date is labeled by its marker,
     * so the other order could be read, but a user who writes the end first has most
     * likely mixed the two up, and is better asked to write them the usual way than
     * have them silently taken as typed.
     *
     * <p>As with a deadline, both times are handed to {@link TaskDateTime#parse}, so
     * an event is only built once the chatbot has understood when it runs. Having
     * understood both, it can also check that they make sense together, which is
     * what {@link Event#requireValidPeriod} does.
     *
     * @param arguments the text following the command word.
     * @throws BobException if a marker is written twice, in the wrong case, is not one
     *                      an event takes or is out of order, if a marker, the
     *                      description, the start or the end is missing, if the
     *                      start or the end is not a date, or if the end comes
     *                      before the start or, with both times given, at the same
     *                      moment.
     */
    static Event parseEvent(String arguments) throws BobException {
        MarkedArguments marked = MarkedArguments.parse(arguments);
        requireOnlyKeywords(marked, CommandWord.EVENT, "An event", List.of(FROM_KEYWORD, TO_KEYWORD),
                EVENT_EXAMPLE);
        Optional<String> fromText = marked.getValue(FROM_KEYWORD);
        if (fromText.isEmpty()) {
            throw BobException.withExample("An event needs a start time, written after "
                    + FROM_KEYWORD + ".", EVENT_EXAMPLE);
        }
        Optional<String> toText = marked.getValue(TO_KEYWORD);
        if (toText.isEmpty()) {
            throw BobException.withExample("An event needs an end time, written after " + TO_KEYWORD
                    + " at the end.", EVENT_EXAMPLE);
        }
        List<String> keywords = marked.getKeywords();
        if (keywords.indexOf(TO_KEYWORD) < keywords.indexOf(FROM_KEYWORD)) {
            throw BobException.withExample("Put " + FROM_KEYWORD + " before " + TO_KEYWORD + ".",
                    EVENT_EXAMPLE);
        }

        String description = marked.getPreamble();
        if (description.isEmpty()) {
            throw BobException.withExample("An event needs a description, written before "
                    + FROM_KEYWORD + ".", EVENT_EXAMPLE);
        }
        if (fromText.get().isEmpty() || toText.get().isEmpty()) {
            throw BobException.withExample("An event needs a time on both sides: one after "
                    + FROM_KEYWORD + " and one after " + TO_KEYWORD + ".", EVENT_EXAMPLE);
        }
        TaskDateTime from = TaskDateTime.parse(fromText.get());
        TaskDateTime to = TaskDateTime.parse(toText.get());
        Event.requireValidPeriod(from, to);
        return new Event(description, from, to);
    }

    /**
     * Returns the error for a todo written with a date marker, pointing the user to
     * the command that takes that marker.
     *
     * @param suggestedCommand the command the user most likely meant.
     * @param example          a well-formed use of that command.
     */
    private static BobException createTodoWithDatesError(CommandWord suggestedCommand, String example) {
        return BobException.withExample("A todo has no dates. Did yu mean "
                + suggestedCommand.getKeyword() + "?", example);
    }

    /**
     * Checks that every marker in {@code marked} is one the command takes.
     *
     * <p>The markers are checked in the order they were typed, so the first stray one
     * is the one named, and the message says which markers belong instead.
     *
     * @param marked        the command's text, split at its markers.
     * @param command       the command being read.
     * @param taskName      the kind of task it adds, with its article, such as {@code A deadline}.
     * @param takenKeywords the markers that command takes.
     * @param example       a well-formed use of that command.
     * @throws BobException if a marker is not one the command takes.
     */
    private static void requireOnlyKeywords(MarkedArguments marked, CommandWord command, String taskName,
            List<String> takenKeywords, String example) throws BobException {
        requireNoDescKeyword(marked, command, example);
        for (String keyword : marked.getKeywords()) {
            if (!takenKeywords.contains(keyword)) {
                throw BobException.withExample(taskName + " only takes " + String.join(" and ", takenKeywords)
                        + ", not " + keyword + ".", example);
            }
        }
    }

    /**
     * Checks that {@value MarkedArguments#DESC_KEYWORD}, which only
     * {@link CommandWord#EDIT} takes, was not used in a command adding a task.
     *
     * <p>A new task's description is simply the text after the command word, so the
     * user is told to write it there, and shown how.
     *
     * @param marked  the command's text, split at its markers.
     * @param command the command being read.
     * @param example a well-formed use of that command.
     * @throws BobException if {@value MarkedArguments#DESC_KEYWORD} was used.
     */
    private static void requireNoDescKeyword(MarkedArguments marked, CommandWord command, String example)
            throws BobException {
        if (marked.hasKeyword(DESC_KEYWORD)) {
            throw BobException.withExample(DESC_KEYWORD + " is only used with "
                    + CommandWord.EDIT.getKeyword() + ". Write the description straight after "
                    + command.getKeyword() + ".", example);
        }
    }
}
