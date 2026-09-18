package milo.parser;

import milo.command.AddCommand;
import milo.command.Command;
import milo.command.DeleteCommand;
import milo.command.ExitCommand;
import milo.command.FindCommand;
import milo.command.ListCommand;
import milo.command.MarkCommand;
import milo.command.UndoCommand;
import milo.exception.MiloException;
import milo.task.Deadline;
import milo.task.Event;
import milo.task.Task;
import milo.task.TaskDate;
import milo.task.Todo;

/**
 * Deals with making sense of the user's raw command text: splitting it into
 * a command word and its arguments, and turning that into the {@link Command}
 * that carries out the corresponding action.
 */
public class Parser {
    /** The command that makes Milo exit. */
    private static final String EXIT_COMMAND = "bye";

    /** The command that makes Milo display the stored tasks. */
    private static final String LIST_COMMAND = "list";

    /** The command that marks a task as done. */
    private static final String MARK_COMMAND = "mark";

    /** The command that marks a task as not done yet. */
    private static final String UNMARK_COMMAND = "unmark";

    /** The command that removes a task from the list. */
    private static final String DELETE_COMMAND = "delete";

    /** The command that searches for tasks by a keyword in their description. */
    private static final String FIND_COMMAND = "find";

    /** The command that reverses the most recent change to the task list. */
    private static final String UNDO_COMMAND = "undo";

    /** The command that adds a task with no date attached. */
    private static final String TODO_COMMAND = "todo";

    /** The command that adds a task due by a given date. */
    private static final String DEADLINE_COMMAND = "deadline";

    /** The command that adds a task spanning a start and an end date. */
    private static final String EVENT_COMMAND = "event";

    /**
     * The character the save file uses between fields. User text containing
     * it cannot be stored, so it is rejected on the way in.
     */
    private static final String FIELD_SEPARATOR = "|";

    /** Separates a deadline's description from its due date. */
    private static final String BY_MARKER = "/by";

    /** Separates an event's description from its start date. */
    private static final String FROM_MARKER = "/from";

    /** Separates an event's start date from its end date. */
    private static final String TO_MARKER = "/to";

    /**
     * Turns one line of user input into the {@link Command} it describes.
     *
     * @param fullCommand the line the user typed, already known not to be blank
     * @return the command to run
     * @throws MiloException if the command word is unknown, or its arguments
     *                       are missing or incomplete
     */
    public static Command parse(String fullCommand) throws MiloException {
        assert fullCommand != null : "parse() should never be handed null";

        // Split once, so the first word is the command and the remainder
        // (which may itself contain spaces) is that command's arguments.
        String[] parts = fullCommand.split("\\s+", 2);
        // split() always returns at least one element, which is what makes
        // reading parts[0] below safe without a length check of its own.
        assert parts.length >= 1 : "split() should always produce at least one element";

        String keyword = parts[0];
        String arguments = (parts.length > 1) ? parts[1].trim() : "";

        switch (keyword) {
            case EXIT_COMMAND:
                return new ExitCommand();

            case LIST_COMMAND:
                return new ListCommand();

            case MARK_COMMAND:
                return new MarkCommand(arguments, true);

            case UNMARK_COMMAND:
                return new MarkCommand(arguments, false);

            case DELETE_COMMAND:
                return new DeleteCommand(arguments);

            case UNDO_COMMAND:
                return new UndoCommand();

            case FIND_COMMAND:
                if (arguments.isEmpty()) {
                    throw new MiloException("What should I sniff out? Try: find book");
                }
                return new FindCommand(arguments);

            case TODO_COMMAND:
            case DEADLINE_COMMAND:
            case EVENT_COMMAND:
                return new AddCommand(buildTask(keyword, arguments));

            default:
                throw new MiloException("\"" + keyword + "\" isn't a trick I know. "
                        + "I can do: todo, deadline, event, list, find, mark, unmark, "
                        + "delete, undo and bye.");
        }
    }

    /**
     * Builds the task described by an add-type command, choosing the
     * subclass that matches the command word.
     *
     * @param keyword   {@value #TODO_COMMAND}, {@value #DEADLINE_COMMAND} or
     *                  {@value #EVENT_COMMAND}
     * @param arguments everything after that first word
     * @return the new task
     * @throws MiloException if the arguments are missing or incomplete
     */
    private static Task buildTask(String keyword, String arguments) throws MiloException {
        // parse() routes only the three add-type command words here, so the
        // default branch below is unreachable in normal use and exists only
        // because the compiler cannot see that.
        assert keyword.equals(TODO_COMMAND) || keyword.equals(DEADLINE_COMMAND)
                || keyword.equals(EVENT_COMMAND)
                : "buildTask() was given a command word it cannot build: " + keyword;

        switch (keyword) {
            case TODO_COMMAND:
                return buildTodo(arguments);

            case DEADLINE_COMMAND:
                return buildDeadline(arguments);

            case EVENT_COMMAND:
                return buildEvent(arguments);

            default:
                // Unreachable: parse() only calls buildTask for the three cases above.
                throw new MiloException("I don't know what \"" + keyword + "\" means.");
        }
    }

    /**
     * Builds the task described by a "todo" command.
     *
     * @param arguments everything the user typed after the command word
     * @return the new todo
     * @throws MiloException if no description was given
     */
    private static Task buildTodo(String arguments) throws MiloException {
        if (arguments.isEmpty()) {
            throw new MiloException("Tell me what the todo is. "
                    + "Try: todo borrow book");
        }
        requireStorable(arguments, "a description");
        return new Todo(arguments);
    }

    /**
     * Builds the task described by a "deadline" command, splitting the
     * arguments at {@value #BY_MARKER}.
     *
     * @param arguments everything the user typed after the command word
     * @return the new deadline
     * @throws MiloException if the marker, the description or the date is missing
     */
    private static Task buildDeadline(String arguments) throws MiloException {
        requireAtMostOneMarker(arguments, BY_MARKER);

        int byIndex = indexOfMarker(arguments, BY_MARKER, 0);
        if (byIndex < 0) {
            throw new MiloException("When's that due? "
                    + "Add " + BY_MARKER + ", like: deadline return book "
                    + BY_MARKER + " 2026-10-15");
        }

        String description = arguments.substring(0, byIndex).trim();
        String by = arguments.substring(byIndex + BY_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new MiloException("A deadline needs a description before "
                    + BY_MARKER + ". Try: deadline return book "
                    + BY_MARKER + " 2026-10-15");
        }
        if (by.isEmpty()) {
            throw new MiloException("Tell me what comes after " + BY_MARKER
                    + ", like: deadline return book " + BY_MARKER + " 2026-10-15");
        }
        requireStorable(description, "a description");
        return new Deadline(description, by);
    }

    /**
     * Builds the task described by an "event" command, splitting the
     * arguments at {@value #FROM_MARKER} and {@value #TO_MARKER}.
     *
     * @param arguments everything the user typed after the command word
     * @return the new event
     * @throws MiloException if a marker, the description or either date is missing
     */
    private static Task buildEvent(String arguments) throws MiloException {
        requireAtMostOneMarker(arguments, FROM_MARKER);
        requireAtMostOneMarker(arguments, TO_MARKER);

        int fromIndex = indexOfMarker(arguments, FROM_MARKER, 0);
        if (fromIndex < 0) {
            throw new MiloException("When does that event start? "
                    + "Add " + FROM_MARKER + ", like: event project meeting "
                    + FROM_MARKER + " Mon 2pm " + TO_MARKER + " 4pm");
        }

        // Look for /to only after /from, so the two markers cannot be
        // picked up out of order.
        int toIndex = indexOfMarker(arguments, TO_MARKER, fromIndex + FROM_MARKER.length());
        if (toIndex < 0) {
            throw new MiloException("And when does it end? "
                    + "Add " + TO_MARKER + " after " + FROM_MARKER
                    + ", like: event project meeting " + FROM_MARKER
                    + " Mon 2pm " + TO_MARKER + " 4pm");
        }

        String description = arguments.substring(0, fromIndex).trim();
        String from = arguments.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String to = arguments.substring(toIndex + TO_MARKER.length()).trim();
        if (description.isEmpty()) {
            throw new MiloException("An event needs a description before "
                    + FROM_MARKER + ". Try: event project meeting "
                    + FROM_MARKER + " Mon 2pm " + TO_MARKER + " 4pm");
        }
        if (from.isEmpty() || to.isEmpty()) {
            throw new MiloException("An event needs both a start and an end. "
                    + "Try: event project meeting " + FROM_MARKER + " Mon 2pm "
                    + TO_MARKER + " 4pm");
        }
        requireStorable(description, "a description");
        requireStorable(from, "a start");
        requireStorable(to, "an end");
        requireEndsAfterStart(from, to);
        return new Event(description, from, to);
    }

    /**
     * Rejects text that could not be written to the save file and read back.
     * Fields there are separated by {@value #FIELD_SEPARATOR}, so text
     * carrying that character splits into the wrong number of fields on the
     * way back in, and the task is dropped as unreadable. Catching it here
     * means the user is told at once, rather than losing the task silently
     * the next time Milo starts.
     *
     * @param text what the user typed
     * @param what how to describe the field in the error, e.g. "a description"
     * @throws MiloException if the text cannot be stored
     */
    private static void requireStorable(String text, String what) throws MiloException {
        if (text.contains(FIELD_SEPARATOR)) {
            throw new MiloException("I keep your tasks in a file that uses "
                    + FIELD_SEPARATOR + " between fields, so I can't put one in "
                    + what + ". Could you write it without the "
                    + FIELD_SEPARATOR + "?");
        }
    }

    /**
     * Rejects a command that uses the same marker twice. Only the first is
     * ever acted on, so a second one would otherwise be swallowed into the
     * text around it and the command would quietly do something other than
     * what was asked.
     *
     * @param arguments everything the user typed after the command word
     * @param marker    the marker that may appear at most once
     * @throws MiloException if the marker appears more than once
     */
    private static void requireAtMostOneMarker(String arguments, String marker)
            throws MiloException {
        int first = indexOfMarker(arguments, marker, 0);
        if (first < 0) {
            return;
        }

        if (indexOfMarker(arguments, marker, first + marker.length()) >= 0) {
            throw new MiloException("You've given me " + marker + " more than once, "
                    + "and I only know what to do with one of them.");
        }
    }

    /**
     * Rejects an event that ends before, or exactly when, it starts.
     * Only checked when both ends are dates Milo can read: an event is free
     * to say "Mon 2pm", and there is no way to order text like that.
     *
     * @param from when the event starts, as typed
     * @param to   when the event ends, as typed
     * @throws MiloException if both are dates and the end does not follow the start
     */
    private static void requireEndsAfterStart(String from, String to) throws MiloException {
        var start = TaskDate.parse(from.trim());
        var end = TaskDate.parse(to.trim());
        if (start.isEmpty() || end.isEmpty()) {
            return;
        }

        if (!end.get().value().isAfter(start.get().value())) {
            throw new MiloException("That event ends before it starts. "
                    + "Check the dates: " + FROM_MARKER + " " + from
                    + " " + TO_MARKER + " " + to);
        }
    }

    /**
     * Finds a marker such as {@value #BY_MARKER}, but only where it stands as
     * a word of its own rather than as part of a longer one.
     * A plain {@code indexOf} would split "finish essay/byline /by Sunday" at
     * the "/by" buried inside "essay/byline"; requiring whitespace (or the
     * end of the text) on both sides makes only the real marker count.
     *
     * @param arguments the text to search
     * @param marker    the marker to look for
     * @param fromIndex where in the text to start searching
     * @return the position of the marker, or -1 if it does not appear as a
     *         word of its own at or after fromIndex
     */
    private static int indexOfMarker(String arguments, String marker, int fromIndex) {
        // Callers pass either 0 or a position just past a marker they already
        // found, so the search never starts outside the text.
        assert fromIndex >= 0 && fromIndex <= arguments.length()
                : "fromIndex should be a position within the arguments";

        int index = arguments.indexOf(marker, fromIndex);
        while (index >= 0) {
            int afterMarker = index + marker.length();
            boolean startsWord = (index == 0)
                    || Character.isWhitespace(arguments.charAt(index - 1));
            boolean endsWord = (afterMarker == arguments.length())
                    || Character.isWhitespace(arguments.charAt(afterMarker));
            if (startsWord && endsWord) {
                return index;
            }
            // This occurrence was part of a longer word; keep looking.
            index = arguments.indexOf(marker, index + 1);
        }
        return -1;
    }
}
