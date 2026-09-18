package plana.parser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import plana.exception.PlanaException;

/**
 * Parses and validates the arguments of task-creation commands.
 */
class TaskCommandParser {
    private static final String TODO_DESCRIPTION_ERROR = "Oops, a ToDo description can't be empty."
            + " Try: todo <description>.";
    private static final int MAXIMUM_TASK_DESCRIPTION_LENGTH = 200;

    private final Parser dateParser;

    /**
     * Creates a task command parser that delegates date parsing to the main parser.
     *
     * @param dateParser the parser that validates task dates.
     */
    TaskCommandParser(Parser dateParser) {
        this.dateParser = dateParser;
    }

    /**
     * Parses arguments for a recognized task-creation command.
     *
     * @param command the parsed task-creation command.
     * @return the validated values used to construct the task.
     * @throws PlanaException if the task syntax or values are invalid.
     */
    Parser.TaskArguments parseTaskArguments(Parser.ParsedCommand command) throws PlanaException {
        return switch (command.type()) {
            case TODO -> parseTodo(command.arguments());
            case DEADLINE -> parseDeadline(command.arguments());
            case EVENT -> parseEvent(command.arguments());
            default -> throw new IllegalArgumentException("Task arguments requested for a non-task command");
        };
    }

    private Parser.TaskArguments parseTodo(String arguments) throws PlanaException {
        if (arguments.isBlank()) {
            throw new PlanaException(TODO_DESCRIPTION_ERROR);
        }
        validateTaskDescription(arguments, "ToDo");
        return new Parser.TaskArguments(arguments, null, null);
    }

    private Parser.TaskArguments parseDeadline(String arguments) throws PlanaException {
        if (arguments.isBlank()) {
            throw Parser.deadlineError("a deadline needs a description and a due date.");
        }
        List<Integer> byMarkers = findMarkers(arguments, "by");
        if (byMarkers.isEmpty()) {
            throw Parser.deadlineError("I couldn't find /by and a due date is missing.");
        }
        if (byMarkers.size() > 1) {
            throw Parser.deadlineError("the /by marker was provided more than once.");
        }
        int bySeparatorIndex = byMarkers.get(0);
        String description = arguments.substring(0, bySeparatorIndex).trim();
        String dueDate = arguments.substring(bySeparatorIndex + "/by".length()).trim();
        if (description.isBlank()) {
            throw Parser.deadlineError("that deadline is missing its description.");
        }
        if (dueDate.isBlank()) {
            throw Parser.deadlineError("that deadline is missing its due date.");
        }
        validateTaskDescription(description, "deadline");
        return new Parser.TaskArguments(description, dateParser.parseDate(dueDate, "deadline"), null);
    }

    private Parser.TaskArguments parseEvent(String arguments) throws PlanaException {
        if (arguments.isBlank()) {
            throw Parser.eventError("an event needs a description, a start, and an end.");
        }
        List<Integer> fromMarkers = findMarkers(arguments, "from");
        List<Integer> toMarkers = findMarkers(arguments, "to");
        if (fromMarkers.isEmpty()) {
            throw Parser.eventError("that event is missing its start marker /from.");
        }
        if (toMarkers.isEmpty()) {
            throw Parser.eventError("that event is missing its end marker /to.");
        }
        if (fromMarkers.size() > 1) {
            throw Parser.eventError("the /from marker was provided more than once.");
        }
        if (toMarkers.size() > 1) {
            throw Parser.eventError("the /to marker was provided more than once.");
        }
        int fromSeparatorIndex = fromMarkers.get(0);
        int toSeparatorIndex = toMarkers.get(0);
        if (toSeparatorIndex < fromSeparatorIndex) {
            throw Parser.eventError("use /from before /to in an event.");
        }
        String description = arguments.substring(0, fromSeparatorIndex).trim();
        String from = arguments.substring(fromSeparatorIndex + "/from".length(), toSeparatorIndex).trim();
        String to = arguments.substring(toSeparatorIndex + "/to".length()).trim();
        if (description.isBlank()) {
            throw Parser.eventError("that event is missing its description.");
        }
        if (from.isBlank()) {
            throw Parser.eventError("that event is missing its start time.");
        }
        if (to.isBlank()) {
            throw Parser.eventError("that event is missing its end time.");
        }
        validateTaskDescription(description, "event");
        LocalDate startDate = dateParser.parseDate(from, "event");
        LocalDate endDate = dateParser.parseDate(to, "event");
        if (!startDate.isBefore(endDate)) {
            throw Parser.eventError("an event's start date must be before its end date.");
        }
        return new Parser.TaskArguments(description, startDate, endDate);
    }

    private List<Integer> findMarkers(String arguments, String markerName) {
        Pattern markerPattern = Pattern.compile("(?:^|\\s)/" + Pattern.quote(markerName) + "(?=\\s|$)");
        Matcher matcher = markerPattern.matcher(arguments);
        List<Integer> markerPositions = new ArrayList<>();
        while (matcher.find()) {
            markerPositions.add(matcher.start() + (matcher.group().startsWith("/") ? 0 : 1));
        }
        return markerPositions;
    }

    private void validateTaskDescription(String description, String taskType) throws PlanaException {
        if (description.length() > MAXIMUM_TASK_DESCRIPTION_LENGTH || containsControlCharacter(description)) {
            throw new PlanaException("Oops, that " + taskType
                    + " description is too long or contains invalid characters. Use 200 characters or fewer.");
        }
    }

    private boolean containsControlCharacter(String value) {
        return value.chars().anyMatch(character -> Character.isISOControl((char) character));
    }
}
