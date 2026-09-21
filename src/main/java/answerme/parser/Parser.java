package answerme.parser;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;

import answerme.command.AddDeadlineCommand;
import answerme.command.AddEventCommand;
import answerme.command.AddToDoCommand;
import answerme.command.Command;
import answerme.command.DedupCommand;
import answerme.command.DeleteCommand;
import answerme.command.ExitCommand;
import answerme.command.FindCommand;
import answerme.command.ListCommand;
import answerme.command.MarkCommand;
import answerme.command.UnmarkCommand;
import answerme.exception.AnswerMeException;

/**
 * Parses user input into the corresponding command objects.
 */
public class Parser {
    private static final String BY_FLAG = "/by";
    private static final String FROM_FLAG = "/from";
    private static final String TO_FLAG = "/to";

    private static final String WHITESPACE_REGEX = "\\s+";
    private static final int COMMAND_ARGUMENT_SPLIT_LIMIT = 2;
    private static final int ARGUMENTS_INDEX = 1;

    /**
     * Constructs a new Parser object.
     */
    private Parser() {
    }

    /**
     * Parses a user input string into the corresponding command.
     *
     * @param userResponse The raw command string entered by the user.
     * @return The command object to be executed.
     * @throws AnswerMeException If the raw command is empty, unknown or
     *                           malformed.
     */
    public static Command parse(String userResponse)
            throws AnswerMeException {
        if (userResponse == null || userResponse.isBlank()) {
            throw new AnswerMeException("Please enter a command.");
        }

        String[] responseParts = userResponse.trim().split(WHITESPACE_REGEX, COMMAND_ARGUMENT_SPLIT_LIMIT);
        String commandName = responseParts[0].toLowerCase(Locale.ROOT);
        String args = extractArgs(responseParts);

        switch (commandName) {
            case "bye":
                return new ExitCommand();

            case "list":
                return new ListCommand();

            case "mark":
                return new MarkCommand(extractTaskIndex(args));

            case "unmark":
                return new UnmarkCommand(extractTaskIndex(args));

            case "delete":
                return new DeleteCommand(extractTaskIndex(args));

            case "todo":
                return parseToDo(args);

            case "deadline":
                return parseDeadline(args);

            case "event":
                return parseEvent(args);

            case "find":
                return parseFind(args);

            case "dedup":
                return new DedupCommand();

            default:
                throw new AnswerMeException("I'm not sure what you mean :(");
        }
    }

    /**
     * Extracts a task index from the command arguments.
     *
     * @param args The arguments containing the task index.
     * @return The extracted task index.
     * @throws AnswerMeException If the index is missing, invalid or
     *                           less than one.
     */
    private static int extractTaskIndex(String args) throws AnswerMeException {
        if (args.isBlank()) {
            throw new AnswerMeException("An index must be supplied for this command.");
        }

        try {
            int index = Integer.parseInt(args);
            if (index < 1) {
                throw new AnswerMeException("The list index must be at least 1.");
            }
            return index - 1;
        } catch (NumberFormatException e) {
            throw new AnswerMeException("The list index must be a valid integer.");
        }
    }

    /**
     * Extracts and joins the argument components of a command.
     *
     * @param responseParts The components of the user command.
     * @return The arguments joined into a single string.
     */
    private static String extractArgs(String[] responseParts) {
        return responseParts.length > ARGUMENTS_INDEX
                ? responseParts[ARGUMENTS_INDEX]
                : "";
    }

    /**
     * Parses the arguments supplied to the Find command
     * and creates a corresponding FindCommand object.
     *
     * @param args The search string entered by the user.
     * @return A command that searches for the supplied string in current tasks contained
     *         within the task list.
     * @throws AnswerMeException If no search string is provided.
     */
    private static Command parseFind(String args)
            throws AnswerMeException {
        if (args.isBlank()) {
            throw new AnswerMeException("Format: find <string>");
        }
        return new FindCommand(args);
    }

    /**
     * Parses arguments for a command that adds a todo task.
     *
     * @param args The arguments containing the task description.
     * @return A command that adds the specified todo task.
     * @throws AnswerMeException If the task description is missing.
     */
    private static Command parseToDo(String args)
            throws AnswerMeException {
        if (args.isBlank()) {
            throw new AnswerMeException("Format: todo <description>");
        }
        validateDescription(args);
        return new AddToDoCommand(args);
    }

    /**
     * Parses arguments for a command that adds a deadline task.
     *
     * @param args The arguments containing the task description and deadline.
     * @return A command that adds the specified deadline task.
     * @throws AnswerMeException If the description, deadline flag, or deadline
     *                           value is invalid.
     */
    private static Command parseDeadline(String args)
            throws AnswerMeException {
        String[] segments = args.split("\\s+(?=(?i:/by\\b))");
        String description = segments[0].trim();
        HashMap<String, String> flags = extractFlags(segments);

        if (description.isBlank() || !flags.containsKey(BY_FLAG)) {
            throw new AnswerMeException("Format: deadline <description> /by <when>");
        }

        validateDescription(description);
        LocalDateTime by = DateTimeParser.parseDateTime(flags.get(BY_FLAG));
        return new AddDeadlineCommand(description, by);
    }

    /**
     * Parses arguments for a command that adds an event task.
     *
     * @param args The arguments containing the task description, start time
     *             and end time.
     * @return A command that adds the specified event task.
     * @throws AnswerMeException If the event details are missing, invalid or
     *                           in the wrong order.
     */
    private static Command parseEvent(String args)
            throws AnswerMeException {
        String[] segments = args.split("\\s+(?=(?i:/(?:from|to)\\b))");
        String description = segments[0].trim();
        HashMap<String, String> flags = extractFlags(segments);

        if (description.isBlank() || !flags.containsKey(FROM_FLAG)
                || !flags.containsKey(TO_FLAG)) {
            throw new AnswerMeException("Format: event <description> /from <when> /to <when>");
        }
        LocalDateTime from = DateTimeParser.parseDateTime(flags.get(FROM_FLAG));
        LocalDateTime to = DateTimeParser.parseDateTime(flags.get(TO_FLAG));

        if (from.isAfter(to)) {
            throw new AnswerMeException("'From' datetime cannot occur after 'To'.");
        }
        validateDescription(description);
        return new AddEventCommand(description, from, to);
    }

    /**
     * Extracts command flags and their values from supplied argument segments.
     *
     * @param segments The argument segments from which to extract flags.
     * @return A mapping from each flag to its value.
     * @throws AnswerMeException If a flag does not have a specified value or
     *                           when there are duplicate flags.
     */
    private static HashMap<String, String> extractFlags(String[] segments)
            throws AnswerMeException {
        HashMap<String, String> flags = new HashMap<>();

        for (int i = 1; i < segments.length; i++) {
            String segment = segments[i].trim();
            String[] flagArguments = segment.split(WHITESPACE_REGEX, COMMAND_ARGUMENT_SPLIT_LIMIT);
            if (flagArguments.length < 2 || flagArguments[1].isBlank()) {
                throw new AnswerMeException("Every flag must be followed by an argument.");
            }
            String flag = flagArguments[0].toLowerCase(Locale.ROOT);
            String value = flagArguments[1];

            if (flags.containsKey(flag)) {
                throw new AnswerMeException("Flag " + flag + " cannot be repeated!");
            }
            flags.put(flag, value);
        }
        return flags;
    }

    private static void validateDescription(String description)
            throws AnswerMeException {
        if (description.contains("|")) {
            throw new AnswerMeException("Task description cannot contain pipe characters!");
        }
    }
}
