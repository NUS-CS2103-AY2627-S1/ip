package bibi;

import bibi.command.AddCommand;
import bibi.command.Command;
import bibi.command.DeleteCommand;
import bibi.command.ExitCommand;
import bibi.command.HelpCommand;
import bibi.command.ListCommand;
import bibi.command.MarkCommand;
import bibi.command.OnCommand;
import bibi.command.UnmarkCommand;
import bibi.task.Deadline;
import bibi.task.Event;
import bibi.task.TaskDateTime;
import bibi.task.Todo;
import java.util.Locale;

/**
 * Turns a line of typed text into the {@link Command} it asks for.
 *
 * <p>All knowledge of command words and their syntax lives here, so the rest of
 * the program never inspects raw input. Anything the command needs is worked out
 * while parsing: a {@code deadline} line becomes a finished {@link Deadline}, and
 * {@code mark 2} becomes a number, so a command can assume its input is sound.
 */
public class Parser {
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
        String input = fullCommand.trim();
        if (input.isEmpty()) {
            throw new BibiException("Please enter a command.");
        }

        // Compared in lower case so command words are recognised whatever case
        // the user typed, while arguments are taken from the original text.
        String command = input.toLowerCase(Locale.ROOT);

        if (isCommand(command, "bye")) {
            return new ExitCommand();
        } else if (isCommand(command, "list")) {
            return new ListCommand();
        } else if (isCommand(command, "help")) {
            return new HelpCommand();
        } else if (isCommand(command, "todo")) {
            return new AddCommand(new Todo(argumentOf(input, "todo")));
        } else if (isCommand(command, "deadline")) {
            return new AddCommand(parseDeadline(argumentOf(input, "deadline")));
        } else if (isCommand(command, "event")) {
            return new AddCommand(parseEvent(argumentOf(input, "event")));
        } else if (isCommand(command, "mark")) {
            return new MarkCommand(parseTaskNumber(argumentOf(input, "mark"), "mark"));
        } else if (isCommand(command, "unmark")) {
            return new UnmarkCommand(parseTaskNumber(argumentOf(input, "unmark"), "unmark"));
        } else if (isCommand(command, "remove")) {
            return new DeleteCommand(parseTaskNumber(argumentOf(input, "remove"), "remove"));
        } else if (isCommand(command, "on")) {
            // Any time of day in the query is ignored, since the question is
            // which tasks belong to the day as a whole.
            return new OnCommand(TaskDateTime.parse(argumentOf(input, "on")).getDate());
        } else {
            throw new BibiException("I don't understand that command. "
                    + "Try todo, deadline, event, list, on, mark, unmark, or bye.");
        }
    }

    /**
     * Reports whether the input uses the given command word.
     *
     * @param command the input, already in lower case
     * @param commandWord the word being looked for
     * @return {@code true} when the input is that word, alone or with arguments
     */
    private static boolean isCommand(String command, String commandWord) {
        return command.equals(commandWord) || command.startsWith(commandWord + " ");
    }

    /**
     * Returns whatever follows the command word.
     *
     * @param input the input in its original case
     * @param commandWord the word that starts the input
     * @return the remaining text, which may be empty
     */
    private static String argumentOf(String input, String commandWord) {
        return input.substring(commandWord.length()).trim();
    }

    /**
     * Builds a deadline from text in the form {@code description /by date}.
     *
     * @param deadlineText the text after the {@code deadline} command word
     * @return the finished deadline
     * @throws BibiException if the text is missing required information
     */
    private static Deadline parseDeadline(String deadlineText) throws BibiException {
        int byIndex = deadlineText.toLowerCase(Locale.ROOT).indexOf(" /by ");
        if (byIndex < 0) {
            throw new BibiException("Use deadline <description> /by <time>.");
        }

        String description = deadlineText.substring(0, byIndex).trim();
        String by = deadlineText.substring(byIndex + 5).trim();
        return new Deadline(description, by);
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
        String normalizedText = eventText.toLowerCase(Locale.ROOT);
        int fromIndex = normalizedText.indexOf(" /from ");
        int toIndex = normalizedText.indexOf(" /to ");

        if (fromIndex < 0 || toIndex < fromIndex + 7) {
            throw new BibiException("Use event <description> /from <start> /to <end>.");
        }

        String description = eventText.substring(0, fromIndex).trim();
        String from = eventText.substring(fromIndex + 7, toIndex).trim();
        String to = eventText.substring(toIndex + 5).trim();
        return new Event(description, from, to);
    }

    /**
     * Reads the task number that a command such as {@code mark 2} refers to.
     *
     * @param numberText the text supplied after the command word
     * @param commandWord the command word, used in the error message
     * @return the one-based task number
     * @throws BibiException if the text is not a number
     */
    private static int parseTaskNumber(String numberText, String commandWord)
            throws BibiException {
        try {
            return Integer.parseInt(numberText);
        } catch (NumberFormatException notANumber) {
            throw new BibiException("Use " + commandWord + " followed by a task number, "
                    + "for example: " + commandWord + " 2");
        }
    }
}
