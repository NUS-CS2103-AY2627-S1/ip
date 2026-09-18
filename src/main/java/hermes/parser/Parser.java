package hermes.parser;

import java.time.LocalDateTime;
import java.util.Arrays;

import hermes.HermesException;
import hermes.command.AddCommand;
import hermes.command.ByeCommand;
import hermes.command.Command;
import hermes.command.DeleteCommand;
import hermes.command.DueCommand;
import hermes.command.FindCommand;
import hermes.command.ListCommand;
import hermes.command.MarkCommand;
import hermes.command.SortCommand;
import hermes.command.UndoCommand;
import hermes.command.UnknownCommand;
import hermes.command.UnmarkCommand;
import hermes.task.Deadline;
import hermes.task.Event;
import hermes.task.Storage;
import hermes.task.Task;
import hermes.task.ToDo;

/**
 * Makes sense of what the user typed.
 *
 * <p>This class turns text into the values the rest of Hermes works with: a
 * {@link Keyword}, a task number, a {@link Task}, a cutoff date. It decides
 * whether input is usable and says so with a {@link HermesException}, but it
 * never acts on the result — the caller does that.
 */
public class Parser {

    /**
     * Turns one line of input into the command it asks for.
     *
     * <p>Everything the command needs is read and checked here, so a command
     * that comes back is ready to run. Input Hermes cannot use is reported as a
     * {@link HermesException} instead, and nothing is carried out.
     *
     * @param input one full line of input, already trimmed.
     * @return the command the user asked for.
     * @throws HermesException if the input names a command but cannot supply it.
     */
    public Command parse(String input) throws HermesException {
        String commandWord = parseCommandWord(input);
        Keyword keyword = Keyword.of(commandWord);
        String arguments = parseArguments(input);

        return switch (keyword) {
            case BYE -> new ByeCommand();
            case LIST -> new ListCommand();
            case MARK -> new MarkCommand(parseTaskNumbers(arguments, keyword));
            case UNMARK -> new UnmarkCommand(parseTaskNumbers(arguments, keyword));
            case DELETE -> new DeleteCommand(parseTaskNumbers(arguments, keyword));
            case TODO -> new AddCommand(parseToDo(arguments));
            case DEADLINE -> new AddCommand(parseDeadline(arguments));
            case EVENT -> new AddCommand(parseEvent(arguments));
            case DUE -> new DueCommand(parseDueCutoff(arguments));
            case SORT -> new SortCommand();
            case FIND -> new FindCommand(parseFindOperation(arguments));
            case UNDO -> new UndoCommand();
            case UNKNOWN -> new UnknownCommand(parseCommandWord(input));
            // No default statement because UNKNOWN is logically equivalent to default
        };
    }

    /**
     * Returns the first word of the input, so an unrecognized command can be
     * quoted back to the user.
     *
     * @param input one full line of input, already trimmed.
     * @return the word the user typed as a command.
     */
    private String parseCommandWord(String input) {
        return input.split("\\s+", 2)[0];
    }

    /**
     * Returns everything the user typed after the command word.
     *
     * @param input one full line of input, already trimmed.
     * @return the arguments, or an empty string if the command stood alone.
     */
    private String parseArguments(String input) {
        String[] argumentParts = input.split("\\s+", 2);
        return argumentParts.length < 2 ? "" : argumentParts[1];
    }

    /**
     * Returns the single word the user asked Hermes to search for.
     *
     * @param arguments what the user typed after the command word.
     * @return the keyword.
     * @throws HermesException too many keywords were provided by the user.
     */
    private String parseFindOperation(String arguments) throws HermesException {
        String[] argumentParts = arguments.split("\\s+");

        if (argumentParts.length > 1) {
            throw new HermesException("Pardon me, but I seek only one word at a time, for instance: "
                    + Keyword.FIND.getExample());
        }

        if (argumentParts[0].isEmpty()) {
            throw new HermesException("Pardon me, but I cannot seek what thou hast not named, for instance: "
                    + Keyword.FIND.getExample());
        }

        return arguments.toLowerCase();
    }

    /**
     * Reads the task numbers given to a command such as {@code delete 1 3}.
     *
     * <p>Repeats are dropped only after each number has been converted, so two
     * spellings of the same number, such as 1 and 01, are recognized as one
     * task. Dropping them while they are still text would let both through, and
     * the second removal would then act on a position that no longer exists.
     *
     * @param arguments what the user typed after the command word.
     * @param keyword the command being run, used to quote a correct example.
     * @return each named task's index in the list, counting from zero.
     * @throws HermesException if no number is given or one is not a number.
     */
    private int[] parseTaskNumbers(String arguments, Keyword keyword) throws HermesException {
        if (arguments.isBlank()) {
            throw new HermesException(craftMissingTaskNumberMessage(keyword));
        }

        try {
            return Arrays.stream(arguments.split("\\s+"))
                    .mapToInt(Integer::parseInt)
                    .map(i -> i - 1)
                    .distinct()
                    .toArray();
        } catch (NumberFormatException e) {
            throw new HermesException(String.format(
                    "'%s' is not a task number. I carry messages, not riddles.", arguments));
        }
    }

    /**
     * Builds the complaint shown when a command that needs a task number was
     * given none. Deleting is worded differently from marking, so the wording
     * follows the command.
     */
    private String craftMissingTaskNumberMessage(Keyword keyword) {
        String request = keyword == Keyword.DELETE
                ? "Please name which task thou wouldst have removed, "
                : "Please name which task, ";
        return request + "for instance: " + keyword.getExample();
    }

    /**
     * Refuses arguments that name the same parameter more than once.
     *
     * <p>Only the first occurrence is treated as a separator, so a second one
     * would become part of the value beside it and be reported as an unreadable
     * date. Naming the repeated parameter points the user at what they typed.
     *
     * @param arguments what the user typed after the command word.
     * @param parameter the parameter to count, such as /by.
     * @param example how the whole command should be written.
     * @throws HermesException if the parameter appears more than once.
     */
    private void rejectRepeatedParameter(String arguments, String parameter, String example)
            throws HermesException {
        int occurrences = arguments.split("\\s*" + parameter + "\\s*", -1).length - 1;

        if (occurrences > 1) {
            throw new HermesException("I see " + parameter + " more than once. Name it but once, "
                    + example);
        }
    }

    /**
     * Builds a {@link ToDo} from a command such as {@code todo borrow book}.
     *
     * @param arguments what the user typed after the command word.
     * @return the task the user described.
     * @throws HermesException if the description is missing or cannot be stored.
     */
    private Task parseToDo(String arguments) throws HermesException {
        if (arguments.isBlank()) {
            throw new HermesException("A todo must have a description, for instance: "
                    + Keyword.TODO.getExample());
        }

        String description = arguments.trim();
        Storage.rejectSeparator(description);

        return new ToDo(description);
    }

    /**
     * Builds a {@link Deadline} from a command such as
     * {@code deadline return book /by 17/08/2026 1500}.
     *
     * @param arguments what the user typed after the command word.
     * @return the task the user described.
     * @throws HermesException if the description or date is missing, cannot be
     *     stored, or is not a date Hermes recognises.
     */
    private Task parseDeadline(String arguments) throws HermesException {
        String example = "for instance: " + Keyword.DEADLINE.getExample();

        if (arguments.isBlank()) {
            throw new HermesException("A deadline must have a description, " + example);
        }

        rejectRepeatedParameter(arguments, "/by", example);

        String[] taskDescriptionAndDueDate = arguments.split("\\s*/by\\s*", 2);

        if (taskDescriptionAndDueDate.length < 2) {
            throw new HermesException("Every deadline needs its /by date, lest it be forgotten, " + example);
        }

        String taskDescription = taskDescriptionAndDueDate[0].trim();
        String dueDate = taskDescriptionAndDueDate[1].trim();

        if (taskDescription.isEmpty() || dueDate.isEmpty()) {
            throw new HermesException("A deadline must have both a description and a /by date, " + example);
        }

        Storage.rejectSeparator(taskDescription, dueDate);

        return new Deadline(taskDescription, DateTimeFormat.parseDateTime(dueDate));
    }

    /**
     * Builds an {@link Event} from a command such as
     * {@code event project meeting /from 17/08/2026 1500 /to 17/08/2026 1630}.
     *
     * @param arguments what the user typed after the command word.
     * @return the task the user described.
     * @throws HermesException if any part is missing, cannot be stored, or is
     *     not a date Hermes recognises.
     */
    private Task parseEvent(String arguments) throws HermesException {
        String example = "for instance: " + Keyword.EVENT.getExample();

        if (arguments.isBlank()) {
            throw new HermesException("An event must have a description, " + example);
        }

        rejectRepeatedParameter(arguments, "/from", example);
        rejectRepeatedParameter(arguments, "/to", example);

        String[] taskDescriptionAndEventStart = arguments.split("\\s*/from\\s*", 2);

        if (taskDescriptionAndEventStart.length < 2) {
            throw new HermesException("An event must have a /from time, " + example);
        }

        String[] eventStartAndEventEnd = taskDescriptionAndEventStart[1].split("\\s*/to\\s*", 2);

        if (eventStartAndEventEnd.length < 2) {
            throw new HermesException("Every road must end somewhere; give thy event a /to time, " + example);
        }

        String taskDescription = taskDescriptionAndEventStart[0].trim();
        String eventStart = eventStartAndEventEnd[0].trim();
        String eventEnd = eventStartAndEventEnd[1].trim();

        if (taskDescription.isEmpty() || eventStart.isEmpty() || eventEnd.isEmpty()) {
            throw new HermesException("An event must have a description, a /from time and a /to time, "
                    + example);
        }

        LocalDateTime startDateTime = DateTimeFormat.parseDateTime(eventStart);
        LocalDateTime endDateTime = DateTimeFormat.parseDateTime(eventEnd);

        // An end at the same moment as the start is refused too: an event of no
        // length is far more likely a mistyped time than something the user means.
        if (!endDateTime.isAfter(startDateTime)) {
            throw new HermesException("An event must end after it begins, yet thou hast set its end "
                    + "to '" + eventEnd + "' and its start to '" + eventStart + "'.");
        }

        Storage.rejectSeparator(taskDescription, eventStart, eventEnd);

        return new Event(taskDescription, startDateTime, endDateTime);
    }

    /**
     * Reads the cutoff given to a command such as
     * {@code due /by 18/08/2026 1600}.
     *
     * @param arguments what the user typed after the command word.
     * @return the moment tasks are being measured against.
     * @throws HermesException if the date is missing or is not one Hermes recognizes.
     */
    private LocalDateTime parseDueCutoff(String arguments) throws HermesException {
        rejectRepeatedParameter(arguments, "/by", "for instance: " + Keyword.DUE.getExample());

        String[] fields = arguments.split("\\s*/by\\s*", 2);

        if (fields.length < 2 || fields[1].isBlank()) {
            throw new HermesException("Name the hour to reckon by with /by, for instance: "
                    + Keyword.DUE.getExample());
        }

        return DateTimeFormat.parseDateTime(fields[1].trim());
    }
}
