package surveyprogram.processor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import surveyprogram.object.AfterTask;
import surveyprogram.object.Deadline;
import surveyprogram.object.Event;
import surveyprogram.object.TaskList;
import surveyprogram.object.TaskListResult;
import surveyprogram.object.Todo;

/**
 * Interprets supported task commands and returns the dialogue for each command.
 */
public class TaskCommandProcessor {
    private static final List<DateTimeFormatter> DATETIME_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-M-d HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-M-d HHmm"),
            DateTimeFormatter.ofPattern("yyyy/M/d HH:mm"),
            DateTimeFormatter.ofPattern("yyyy/M/d HHmm"),
            DateTimeFormatter.ofPattern("d-M-yyyy HH:mm"),
            DateTimeFormatter.ofPattern("d-M-yyyy HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm")
    );

    private static final List<DateTimeFormatter> DATE_FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-M-d"),
            DateTimeFormatter.ofPattern("yyyy/M/d"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy")
    );

    private final TaskList taskList;
    /**
     * Creates a processor that applies commands to a task list.
     *
     * @param taskList task list to query and modify
     */
    public TaskCommandProcessor(TaskList taskList) {
        this.taskList = taskList;
    }

    /**
     * Processes one command.
     *
     * @param input the exact line entered by the user
     * @return dialogue and whether the application should accept another command
     */
    public CommandResult process(String input) {
        try {
            return processCommand(input);
        } catch (InvalidCalendarDateException exception) {
            return new CommandResult("OH, HOW VERY SMART OF YOU TO PROPOSE THAT DATE. DENIED.",
                    true, ReplyType.ERROR);
        }
    }

    /** Routes a command after establishing the shared calendar-error handler. */
    private CommandResult processCommand(String input) {
        ParsedCommand command = ParsedCommand.from(input);
        return switch (command.name()) {
            case "bye" -> command.hasNonBlankArguments() ? unknownCommand() : new CommandResult("", false);
            case "list" -> command.hasNonBlankArguments() ? unknownCommand() : listTasks();
            case "find" -> findTasks(command.arguments());
            case "date" -> command.hasSeparator() ? handleDate(command.arguments()) : unknownCommand();
            case "mark" -> command.hasSeparator() ? handleMark(command.arguments(), false) : unknownCommand();
            case "unmark" -> command.hasSeparator() ? handleMark(command.arguments(), true) : unknownCommand();
            case "todo" -> command.hasSeparator() ? addTodo(command.arguments()) : unknownCommand();
            case "deadline" -> command.hasSeparator() ? addDeadline(command.arguments()) : unknownCommand();
            case "event" -> command.hasSeparator() ? addEvent(command.arguments()) : unknownCommand();
            case "pending" -> command.hasSeparator() ? addPending(command.arguments()) : unknownCommand();
            case "delete" -> command.hasSeparator() ? handleDelete(command.arguments()) : unknownCommand();
            default -> unknownCommand();
        };
    }

    private CommandResult listTasks() {
        TaskListResult result = taskList.read();
        return toCommandResult(new TaskListResult(
                "VERY WELL. HERE IS YOUR LIST:\n" + result.response(), result.status()), ReplyType.LIST);
    }

    private CommandResult findTasks(String keyword) {
        return toCommandResult(taskList.find(keyword), ReplyType.FIND);
    }

    private CommandResult addTodo(String description) {
        return toCommandResult(taskList.add(new Todo(description)), ReplyType.TASK_CREATED);
    }

    private CommandResult unknownCommand() {
        return new CommandResult("WELL, THAT IS NO LONGER A COMMAND.", true, ReplyType.ERROR);
    }

    /** Returns a command result whose reply type reflects the task-list operation status. */
    private CommandResult toCommandResult(TaskListResult result, ReplyType successfulType) {
        ReplyType replyType = switch (result.status()) {
            case SUCCESS -> successfulType;
            case EMPTY -> ReplyType.EMPTY;
            case ERROR -> ReplyType.ERROR;
        };
        return new CommandResult(result.response(), true, replyType);
    }

    /**
     * Parses a date command and prints tasks occurring on that date.
     *
     * @param dateText date argument entered by the user
     */
    private CommandResult handleDate(String dateText) {
        if (dateText.isBlank()) {
            return new CommandResult("BUT, THERE WAS NOT A DATE TO CHECK.", true, ReplyType.ERROR);
        }
        try {
            LocalDateTime dateTime = parseDateTime(dateText);
            LocalDate date = dateTime.toLocalDate();
            return toCommandResult(taskList.listByDate(date), ReplyType.DATE_QUERY);
        } catch (DateTimeParseException exception) {
            return new CommandResult("BUT, THE DATE IS INVALID.", true, ReplyType.ERROR);
        }
    }

    /**
     * Parses a one-based task index and marks or unmarks the selected task.
     *
     * @param indexText one-based task index entered by the user
     * @param isReverse {@code true} to unmark the task, or {@code false} to mark it
     */
    private CommandResult handleMark(String indexText, boolean isReverse) {
        if (indexText.isBlank()) {
            return new CommandResult("BUT, THE OBJECT IS NOT SPECIFIED.", true, ReplyType.ERROR);
        }
        try {
            int itemIndex = Integer.parseInt(indexText) - 1;
            ReplyType successfulType = isReverse ? ReplyType.UNMARKED : ReplyType.MARK_COMPLETED;
            return toCommandResult(taskList.mark(itemIndex, isReverse), successfulType);
        } catch (NumberFormatException exception) {
            return new CommandResult("BUT, IT IS INVALID.", true, ReplyType.ERROR);
        }
    }

    /**
     * Parses a one-based task index and deletes the selected task.
     *
     * @param indexText one-based task index entered by the user
     */
    private CommandResult handleDelete(String indexText) {
        if (indexText.isBlank()) {
            return new CommandResult("BUT, THE OBJECT IS NOT SPECIFIED.", true, ReplyType.ERROR);
        }
        try {
            int itemIndex = Integer.parseInt(indexText) - 1;
            return toCommandResult(taskList.delete(itemIndex), ReplyType.DELETED);
        } catch (NumberFormatException exception) {
            return new CommandResult("BUT, IT IS INVALID.", true, ReplyType.ERROR);
        }
    }

    /**
     * Parses and adds a deadline, falling back to a todo when its date is invalid.
     *
     * @param arguments deadline description and date arguments entered by the user
     */
    private CommandResult addDeadline(String arguments) {
        String[] parts = arguments.split("(?i) /by ", 2);
        if (parts.length == 2) {
            try {
                LocalDateTime by = parseDateTime(parts[1].trim());
                return toCommandResult(taskList.add(new Deadline(parts[0].trim(), by)),
                        ReplyType.TASK_CREATED);
            } catch (DateTimeParseException exception) {
                // fall through to error
            }
        }
        return mistakeWith(taskList.add(new Todo(arguments)));
    }

    /**
     * Parses named event endpoints and rejects missing, repeated, or non-increasing endpoints.
     *
     * @param arguments event description and date-range arguments entered by the user
     */
    private CommandResult addEvent(String arguments) {
        List<MatchResult> keywords = Pattern.compile("(?i)(?<!\\S)/(from|to)(?=\\s|$)")
                .matcher(arguments).results().toList();
        if (keywords.size() != 2 || keywords.get(0).group(1).equalsIgnoreCase(keywords.get(1).group(1))) {
            return new CommandResult("BUT, AN EVENT NEEDS EXACTLY ONE /FROM AND ONE /TO.",
                    true, ReplyType.ERROR);
        }
        MatchResult first = keywords.get(0);
        MatchResult second = keywords.get(1);
        String firstValue = arguments.substring(first.end(), second.start()).trim();
        String secondValue = arguments.substring(second.end()).trim();
        boolean isFromFirst = first.group(1).equalsIgnoreCase("from");
        if (firstValue.isEmpty() || secondValue.isEmpty()) {
            return new CommandResult("BUT, BOTH EVENT DATES MUST BE SPECIFIED.", true, ReplyType.ERROR);
        }
        try {
            LocalDateTime from = parseDateTime(isFromFirst ? firstValue : secondValue);
            LocalDateTime to = parseDateTime(isFromFirst ? secondValue : firstValue);
            if (!from.isBefore(to)) {
                return new CommandResult("BUT, THE EVENT MUST START BEFORE IT ENDS.", true, ReplyType.ERROR);
            }
            String description = arguments.substring(0, first.start()).trim();
            return toCommandResult(taskList.add(new Event(description, from, to)), ReplyType.TASK_CREATED);
        } catch (DateTimeParseException exception) {
            // Preserve the todo fallback for unparseable date values.
        }
        return mistakeWith(taskList.add(new Todo(arguments)));
    }

    /** Parses and adds a task whose availability depends on a time or another task. */
    private CommandResult addPending(String arguments) {
        long conditionCount = Pattern.compile("(?i)(?<!\\S)/(aftertask|after)(?=\\s|$)")
                .matcher(arguments).results().count();
        if (conditionCount > 1) {
            return new CommandResult("BUT, A PENDING TASK CAN HAVE ONLY ONE CONDITION.", true, ReplyType.ERROR);
        }
        String[] taskParts = arguments.split("(?i) /aftertask ", 2);
        if (taskParts.length == 2) {
            try {
                int parentIndex = Integer.parseInt(taskParts[1].trim()) - 1;
                return toCommandResult(taskList.addAfterTask(taskParts[0].trim(), parentIndex),
                        ReplyType.TASK_CREATED);
            } catch (NumberFormatException exception) {
                return new CommandResult("BUT, IT IS INVALID.", true, ReplyType.ERROR);
            }
        }

        String[] timeParts = arguments.split("(?i) /after ", 2);
        if (timeParts.length == 2) {
            try {
                LocalDateTime releaseAt = parseDateTime(timeParts[1].trim());
                return toCommandResult(taskList.add(new AfterTask(timeParts[0].trim(), releaseAt)),
                        ReplyType.TASK_CREATED);
            } catch (DateTimeParseException exception) {
                // fall through to the existing dated-task fallback
            }
        }
        return mistakeWith(taskList.add(new Todo(arguments)));
    }

    /** Returns the dated-task error followed by its fallback result. */
    private CommandResult mistakeWith(TaskListResult fallbackResult) {
        String response = "YOU MUST BE\nMISTAKEN.\n\nHERE.\n" + fallbackResult.response();
        return new CommandResult(response, true, ReplyType.ERROR);
    }

    /**
     * Tries all supported date/time patterns in order and returns the first successful parse.
     *
     * @param dateTimeString date or date-time text to parse
     * @return parsed date-time, with midnight used when no time is supplied
     * @throws DateTimeParseException if none of the patterns match
     */
    private LocalDateTime parseDateTime(String dateTimeString) throws DateTimeParseException {
        validateCalendarDate(dateTimeString);
        Stream<LocalDateTime> parsedDateTimes = DATETIME_FORMATTERS.stream()
                .map(formatter -> tryParseDateTime(dateTimeString, formatter))
                .flatMap(Optional::stream);
        Stream<LocalDateTime> parsedDates = DATE_FORMATTERS.stream()
                .map(formatter -> tryParseDate(dateTimeString, formatter))
                .flatMap(Optional::stream);

        return Stream.concat(parsedDateTimes, parsedDates)
                .findFirst()
                .orElseThrow(() -> new DateTimeParseException(
                        "Unable to parse: " + dateTimeString, dateTimeString, 0));
    }

    /** Rejects impossible numeric calendar dates before SMART parsing can normalize them. */
    private void validateCalendarDate(String dateTimeString) {
        String date = dateTimeString.split("\\s+", 2)[0];
        if (!date.matches("\\d+[-/]\\d+[-/]\\d+")) {
            return;
        }
        for (String pattern : List.of("uuuu-M-d", "uuuu/M/d", "d-M-uuuu", "d/M/uuuu")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
                    .withResolverStyle(ResolverStyle.STRICT);
            if (tryParseDate(date, formatter).isPresent()) {
                return;
            }
        }
        throw new InvalidCalendarDateException();
    }

    /** Distinguishes impossible calendar dates from malformed input eligible for the todo fallback. */
    private static class InvalidCalendarDateException extends RuntimeException {
    }

    /** Returns the parsed date-time, or an empty result when the format does not match. */
    private Optional<LocalDateTime> tryParseDateTime(String dateTimeString, DateTimeFormatter formatter) {
        try {
            return Optional.of(LocalDateTime.parse(dateTimeString, formatter));
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    /** Returns the parsed date at midnight, or an empty result when the format does not match. */
    private Optional<LocalDateTime> tryParseDate(String dateString, DateTimeFormatter formatter) {
        try {
            return Optional.of(LocalDate.parse(dateString, formatter).atStartOfDay());
        } catch (DateTimeParseException exception) {
            return Optional.empty();
        }
    }

    /** Stores a normalized command name separately from its trimmed arguments. */
    private record ParsedCommand(String name, String arguments, boolean hasSeparator) {
        private static ParsedCommand from(String input) {
            String inputWithoutLeadingSpace = input.stripLeading();
            int separatorIndex = inputWithoutLeadingSpace.indexOf(' ');
            if (separatorIndex < 0) {
                return new ParsedCommand(inputWithoutLeadingSpace.toLowerCase(Locale.ROOT), "", false);
            }

            String name = inputWithoutLeadingSpace.substring(0, separatorIndex).toLowerCase(Locale.ROOT);
            String arguments = inputWithoutLeadingSpace.substring(separatorIndex + 1).trim();
            return new ParsedCommand(name, arguments, true);
        }

        private boolean hasNonBlankArguments() {
            return !arguments.isEmpty();
        }
    }
}
