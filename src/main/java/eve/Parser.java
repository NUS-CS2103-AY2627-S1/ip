package eve;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import eve.command.AddCommand;
import eve.command.Command;
import eve.command.CommandWord;
import eve.command.DeleteCommand;
import eve.command.ExitCommand;
import eve.command.FindCommand;
import eve.command.HelpCommand;
import eve.command.ListCommand;
import eve.command.MarkCommand;
import eve.command.OnCommand;
import eve.command.ScheduleCommand;
import eve.command.UnmarkCommand;
import eve.task.Deadline;
import eve.task.Event;
import eve.task.ToDo;

/**
 * Deals with making sense of a user command: splits a line of input into a
 * command word and its arguments, and turns them into the matching
 * {@link Command} to execute -- or an {@link EveException} explaining what
 * was wrong with them.
 */
public class Parser {
    private Parser() {
        // Not meant to be instantiated: every method is static.
    }

    /**
     * Parses one line of user input into the {@link Command} it requests.
     *
     * @param fullCommand the full line of input, e.g. "todo read book".
     * @throws EveException if the command word is unknown or its arguments are invalid.
     */
    public static Command parse(String fullCommand) throws EveException {
        // Without this, a leading space before the command word (e.g. " todo read book")
        // makes getCommandWord see an empty word instead of "todo", since it splits on the
        // very first space -- misreporting a perfectly valid command as unrecognized.
        String trimmedCommand = fullCommand.trim();
        String word = getCommandWord(trimmedCommand);
        String arguments = getArguments(trimmedCommand);
        CommandWord commandWord = CommandWord.fromWord(word);
        switch (commandWord) {
            case HELP:
                return new HelpCommand();
            case BYE:
                return new ExitCommand();
            case LIST:
                return new ListCommand();
            case MARK:
                return new MarkCommand(parseTaskNumber(arguments));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(arguments));
            case DELETE:
                return new DeleteCommand(parseTaskNumber(arguments));
            case TODO:
                return new AddCommand(parseTodo(arguments));
            case DEADLINE:
                return new AddCommand(parseDeadline(arguments));
            case EVENT:
                return new AddCommand(parseEvent(arguments));
            case ON:
                return new OnCommand(parseOnDate(arguments));
            case FIND:
                return new FindCommand(parseFindKeyword(arguments));
            case SCHEDULE:
                return new ScheduleCommand(parseOptionalScheduleDate(arguments));
            default:
                // Unreachable: CommandWord.fromWord only ever returns one of the cases above.
                // If a new CommandWord constant is ever added without a case here, this
                // assertion should fail loudly during development instead of silently
                // falling through to a misleading "unknown command" error at runtime.
                assert false : "Unhandled CommandWord: " + commandWord;
                throw new EveException("Oops, I don't recognize that command! No worries -- give it another shot?");
        }
    }

    private static String getCommandWord(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? input : input.substring(0, spaceIndex);
    }

    private static String getArguments(String input) {
        int spaceIndex = input.indexOf(' ');
        return spaceIndex == -1 ? "" : input.substring(spaceIndex + 1).trim();
    }

    /**
     * Parses the format of a task number typed by the user (missing or
     * non-numeric). Whether the number is in range depends on the current
     * list size, so that's checked later by {@link TaskList#toIndex}.
     */
    private static int parseTaskNumber(String text) throws EveException {
        if (text.isEmpty()) {
            throw new EveException("Oops, I need a task number for that! Try something like mark 2.");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            throw new EveException("Oops, '" + text + "' isn't a valid task number! Numbers only, please.");
        }
    }

    /**
     * Parses the arguments of a {@code todo} command.
     *
     * @param arguments the text after "todo".
     * @throws EveException if the description is empty or contains a reserved character.
     */
    private static ToDo parseTodo(String arguments) throws EveException {
        if (arguments.isEmpty()) {
            throw new EveException("Oops, your to-do needs a description! What are we adding?");
        }
        checkNoReservedCharacters(arguments);
        return new ToDo(arguments);
    }

    /**
     * Parses the arguments of a {@code deadline} command, e.g.
     * {@code "return book /by 2019-12-02"}.
     *
     * @param arguments the text after "deadline".
     * @throws EveException if the description or '/by' date is missing, empty, duplicated,
     *      malformed, or the description contains a reserved character.
     */
    private static Deadline parseDeadline(String arguments) throws EveException {
        int byIndex = arguments.indexOf(" /by ");
        if (byIndex == -1) {
            throw new EveException("Oops, a deadline needs a description and a '/by' date! "
                    + "Try: deadline return book /by 2019-12-02.");
        }
        if (arguments.indexOf(" /by ", byIndex + 1) != -1) {
            throw new EveException("Oops, I only need one '/by' date -- you've given me two!");
        }
        String description = arguments.substring(0, byIndex).trim();
        String byText = arguments.substring(byIndex + " /by ".length()).trim();
        if (description.isEmpty()) {
            throw new EveException("Oops, your deadline needs a description too!");
        }
        if (byText.isEmpty()) {
            throw new EveException("Oops, don't forget the '/by' date!");
        }
        checkNoReservedCharacters(description);
        LocalDate by = parseDate("'/by' date", byText, "2019-12-02");
        return new Deadline(description, by);
    }

    /**
     * Parses the arguments of an {@code event} command, e.g.
     * {@code "project meeting /from 2019-10-04 /to 2019-10-11"}.
     *
     * @param arguments the text after "event".
     * @throws EveException if the description or either date is missing, empty, duplicated,
     *      malformed, out of order (the event ends before it starts), or the description
     *      contains a reserved character.
     */
    private static Event parseEvent(String arguments) throws EveException {
        int fromIndex = arguments.indexOf(" /from ");
        int toIndex = arguments.indexOf(" /to ");
        if (fromIndex == -1 || toIndex == -1 || toIndex < fromIndex) {
            throw new EveException("Oops, an event needs a description, a '/from' date, and a '/to' date! "
                    + "Try: event project meeting /from 2019-10-04 /to 2019-10-11.");
        }
        if (arguments.indexOf(" /from ", fromIndex + 1) != -1) {
            throw new EveException("Oops, I only need one '/from' date -- you've given me two!");
        }
        if (arguments.indexOf(" /to ", toIndex + 1) != -1) {
            throw new EveException("Oops, I only need one '/to' date -- you've given me two!");
        }
        int fromTextStart = fromIndex + " /from ".length();
        String description = arguments.substring(0, fromIndex).trim();
        // "/from" and "/to" can end up adjacent (e.g. "trip /from /to 2019-10-11") with no
        // room for a from-date between them -- fromTextStart would then land past toIndex,
        // which substring(start, end) rejects. Treat that the same as an empty from-date
        // rather than letting it crash with a raw StringIndexOutOfBoundsException.
        String fromText = fromTextStart < toIndex ? arguments.substring(fromTextStart, toIndex).trim() : "";
        String toText = arguments.substring(toIndex + " /to ".length()).trim();
        if (description.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
            throw new EveException("Oops, fill in the event's description, '/from' date, "
                    + "and '/to' date -- all of them!");
        }
        checkNoReservedCharacters(description);
        LocalDate from = parseDate("'/from' date", fromText, "2019-10-04");
        LocalDate to = parseDate("'/to' date", toText, "2019-10-11");
        if (from.isAfter(to)) {
            throw new EveException("Oops, that event ends before it starts! Double check your '/from' and '/to'.");
        }
        return new Event(description, from, to);
    }

    /**
     * Rejects a description containing "|" or a line break -- {@link Storage} uses " | " to
     * separate a saved task's fields and one line per task, so either would silently corrupt
     * that task (and any task saved after it) the next time it's written to disk.
     *
     * @param description the description to check.
     * @throws EveException if it contains a reserved character.
     */
    private static void checkNoReservedCharacters(String description) throws EveException {
        if (description.contains("|") || description.contains("\n") || description.contains("\r")) {
            throw new EveException("Oops, descriptions can't contain '|' or line breaks -- "
                    + "I use those behind the scenes to save your tasks!");
        }
    }

    /**
     * Parses the arguments of an {@code on} command, e.g. {@code "2019-12-02"}.
     *
     * @param arguments the text after "on".
     * @throws EveException if the date is missing or malformed.
     */
    private static LocalDate parseOnDate(String arguments) throws EveException {
        if (arguments.isEmpty()) {
            throw new EveException("Oops, which date? Try: on 2019-12-02.");
        }
        return parseDate("date", arguments, "on 2019-12-02");
    }

    /**
     * Parses text as an ISO date, or throws a field-specific error naming
     * what was expected. Shared by every command that takes a date, so the
     * "please use yyyy-mm-dd" message stays worded consistently.
     *
     * @param fieldDescription names the field in the error message, e.g. {@code "'/by' date"}.
     * @param dateText the text to parse.
     * @param example a valid example shown in the error message, e.g. {@code "2019-12-02"}.
     * @throws EveException if the text isn't a valid yyyy-mm-dd date.
     */
    private static LocalDate parseDate(String fieldDescription, String dateText, String example)
            throws EveException {
        try {
            return LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            throw new EveException("Oops, give the " + fieldDescription
                    + " as yyyy-mm-dd! Like " + example + ".");
        }
    }

    /**
     * Parses the arguments of a {@code schedule} command: an optional date
     * to filter to, e.g. {@code "2019-12-02"}, or empty for the whole
     * schedule.
     *
     * @param arguments the text after "schedule".
     * @throws EveException if a date was given but is malformed.
     */
    private static Optional<LocalDate> parseOptionalScheduleDate(String arguments) throws EveException {
        if (arguments.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(parseDate("date", arguments, "schedule 2019-12-02"));
    }

    /**
     * Parses the arguments of a {@code find} command.
     *
     * @param arguments the text after "find".
     * @throws EveException if the keyword is empty.
     */
    private static String parseFindKeyword(String arguments) throws EveException {
        if (arguments.isEmpty()) {
            throw new EveException("Oops, what should I search for? Try: find book.");
        }
        return arguments;
    }
}
