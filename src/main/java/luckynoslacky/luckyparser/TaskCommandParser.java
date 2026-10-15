package luckynoslacky.luckyparser;

import java.time.LocalDateTime;

import luckynoslacky.luckycommand.LuckyNoFindCommand;
import luckynoslacky.luckyexception.LuckyNoInputException;
import luckynoslacky.luckyresponse.LuckyNoFormatMessages;
import luckynoslacky.luckyresponse.LuckyNoQuips;
import luckynoslacky.luckytask.DeadlineTask;
import luckynoslacky.luckytask.EventTask;
import luckynoslacky.luckytask.TaskMaster;
import luckynoslacky.luckytask.TodoTask;

/**
 * Parses commands that create tasks or search existing tasks.
 */
final class TaskCommandParser {
    private final DateTimeParser dateTimeParser;
    private final DateTimePrefixParser dateTimePrefixParser;

    /**
     * Creates a task-command parser with shared date/time dependencies.
     *
     * @param dateTimeParser parser used to resolve the current time
     * @param dateTimePrefixParser parser used to read date/time prefixes
     */
    TaskCommandParser(
            DateTimeParser dateTimeParser,
            DateTimePrefixParser dateTimePrefixParser) {
        if (dateTimeParser == null || dateTimePrefixParser == null) {
            throw new IllegalArgumentException("Date/time parsers cannot be null.");
        }
        this.dateTimeParser = dateTimeParser;
        this.dateTimePrefixParser = dateTimePrefixParser;
    }

    /**
     * Parses the description of a ToDo command.
     *
     * @param commandArguments command arguments
     * @return constructed ToDo task
     * @throws LuckyNoInputException if the description is missing
     */
    TodoTask parseTodo(String commandArguments) throws LuckyNoInputException {
        if (commandArguments.isEmpty()) {
            throw new LuckyNoInputException(
                    LuckyNoQuips.missingTaskDescriptionMessage());
        }
        CommandArgumentParser.rejectMarkerLikeSlash(
                commandArguments,
                LuckyNoParser.CommandName.TODO,
                LuckyNoFormatMessages.todoFormat());
        return new TodoTask(commandArguments);
    }

    /**
     * Parses a deadline description and its {@code /by} date/time.
     *
     * @param commandArguments command arguments
     * @return constructed deadline task
     * @throws LuckyNoInputException if the format or date/time is invalid
     */
    DeadlineTask parseDeadline(String commandArguments)
            throws LuckyNoInputException {
        int byIndex = CommandArgumentParser.markerIndex(commandArguments, "/by", 0);
        if (byIndex <= 0) {
            throw CommandArgumentParser.invalidFormat(
                    LuckyNoParser.CommandName.DEADLINE);
        }

        String description = commandArguments.substring(0, byIndex).trim();
        String byTimeText = commandArguments.substring(byIndex + 3).trim();
        if (description.isEmpty() || byTimeText.isEmpty()) {
            throw CommandArgumentParser.invalidFormat(
                    LuckyNoParser.CommandName.DEADLINE);
        }
        CommandArgumentParser.rejectMarkerLikeSlash(
                description,
                LuckyNoParser.CommandName.DEADLINE);
        CommandArgumentParser.rejectMarkerLikeSlash(
                byTimeText,
                LuckyNoParser.CommandName.DEADLINE);
        LocalDateTime byTime = dateTimePrefixParser
                .parseEndDateTimeIgnoringTrailingText(byTimeText);
        if (byTime.isBefore(dateTimeParser.now())) {
            throw new LuckyNoInputException(LuckyNoQuips.timeTravelMessage());
        }
        return new DeadlineTask(description, byTime);
    }

    /**
     * Parses a find command with an optional description and date filter.
     * Text before the {@code /on} tag is treated as the description query.
     *
     * @param commandArguments command arguments
     * @param taskMaster task master attached to the parsed command
     * @return parsed find command
     * @throws LuckyNoInputException if the search format or date is invalid
     */
    LuckyNoFindCommand parseFind(
            String commandArguments, TaskMaster taskMaster)
            throws LuckyNoInputException {
        if (commandArguments.isBlank()) {
            throw CommandArgumentParser.invalidFormat(
                    LuckyNoParser.CommandName.FIND);
        }

        int onIndex = CommandArgumentParser.markerIndex(commandArguments, "/on", 0);
        if (onIndex < 0) {
            CommandArgumentParser.rejectMarkerLikeSlash(
                    commandArguments, LuckyNoParser.CommandName.FIND);
            return new LuckyNoFindCommand(
                    commandArguments.trim(), null, taskMaster);
        }

        if (CommandArgumentParser.markerIndex(commandArguments, "/on", onIndex + 3) >= 0) {
            throw CommandArgumentParser.invalidFormat(
                    LuckyNoParser.CommandName.FIND);
        }

        String descriptionQuery = commandArguments.substring(0, onIndex).trim();
        String dateText = commandArguments.substring(onIndex + 3).trim();
        if (dateText.isEmpty()) {
            throw CommandArgumentParser.invalidFormat(
                    LuckyNoParser.CommandName.FIND);
        }
        CommandArgumentParser.rejectMarkerLikeSlash(
                descriptionQuery, LuckyNoParser.CommandName.FIND);
        CommandArgumentParser.rejectMarkerLikeSlash(
                dateText, LuckyNoParser.CommandName.FIND);

        LocalDateTime searchDateTime = dateTimePrefixParser
                .parseStartDateTimeIgnoringTrailingText(dateText);
        if (descriptionQuery.isEmpty()) {
            descriptionQuery = null;
        }
        return new LuckyNoFindCommand(
                descriptionQuery,
                searchDateTime,
                taskMaster);
    }

    /**
     * Parses an event description and its {@code /from} and {@code /to} times.
     *
     * @param commandArguments command arguments
     * @return constructed event task
     * @throws LuckyNoInputException if the format or date/time is invalid
     */
    EventTask parseEvent(String commandArguments)
            throws LuckyNoInputException {
        int fromIndex = CommandArgumentParser.markerIndex(commandArguments, "/from", 0);
        int toIndex = CommandArgumentParser.markerIndex(commandArguments, "/to", 0);
        if (fromIndex <= 0 || toIndex <= fromIndex) {
            throw CommandArgumentParser.invalidFormat(
                    LuckyNoParser.CommandName.EVENT);
        }

        String description = commandArguments.substring(0, fromIndex).trim();
        String startTimeText = commandArguments
                .substring(fromIndex + 5, toIndex).trim();
        String endTimeText = commandArguments.substring(toIndex + 3).trim();
        if (description.isEmpty() || startTimeText.isEmpty() || endTimeText.isEmpty()) {
            throw CommandArgumentParser.invalidFormat(
                    LuckyNoParser.CommandName.EVENT);
        }
        CommandArgumentParser.rejectMarkerLikeSlash(
                description, LuckyNoParser.CommandName.EVENT);
        CommandArgumentParser.rejectMarkerLikeSlash(
                startTimeText, LuckyNoParser.CommandName.EVENT);
        CommandArgumentParser.rejectMarkerLikeSlash(
                endTimeText, LuckyNoParser.CommandName.EVENT);
        LocalDateTime startTime = dateTimePrefixParser
                .parseStartDateTimeIgnoringTrailingText(startTimeText);
        LocalDateTime endTime = dateTimePrefixParser
                .parseEndDateTimeIgnoringTrailingText(endTimeText, startTime);
        if (endTime.isBefore(startTime)) {
            throw new LuckyNoInputException(LuckyNoQuips.timeTravelMessage());
        }
        return new EventTask(description, startTime, endTime);
    }
}
