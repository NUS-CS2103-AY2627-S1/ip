package larper.command;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import larper.exception.InvalidDateException;
import larper.exception.InvalidDescriptionException;
import larper.exception.InvalidTagException;
import larper.exception.InvalidTimeException;
import larper.exception.LarperException;
import larper.exception.MissingTagException;
import larper.exception.NoDescriptionException;
import larper.exception.NoTaskTypeException;
import larper.exception.NonNumberDeleteException;
import larper.task.Deadline;
import larper.task.Event;
import larper.task.Task;
import larper.task.TaskDateTime;
import larper.task.TaskDateTimeParser;
import larper.task.Todo;

/**
 * Interprets raw user input as Larper commands or task objects.
 */
public class Parser {
    private static final String EXIT_COMMAND = "exit";
    private static final String LIST_COMMAND = "list";
    private static final String FIND_COMMAND = "find";
    private static final String FIND_TAG_COMMAND = "find tag";
    private static final String MARK_COMMAND_PREFIX = "mark ";
    private static final String UNMARK_COMMAND_PREFIX = "unmark ";
    private static final String TAG_COMMAND_PREFIX = "tag ";
    private static final String UNTAG_COMMAND_PREFIX = "untag ";
    private static final String FIND_TAG_COMMAND_PREFIX = FIND_TAG_COMMAND + " ";
    private static final String TODO_COMMAND = "todo";
    private static final String DEADLINE_COMMAND = "deadline";
    private static final String EVENT_COMMAND = "event";
    private static final String TODO_COMMAND_PREFIX = TODO_COMMAND + " ";
    private static final String DEADLINE_COMMAND_PREFIX = DEADLINE_COMMAND + " ";
    private static final String EVENT_COMMAND_PREFIX = EVENT_COMMAND + " ";
    private static final String BY_MARKER = "/by";
    private static final String FROM_MARKER = "/from";
    private static final String TO_MARKER = "/to";
    private static final int EXPECTED_EVENT_SLASH_COUNT = 2;
    private static final Pattern DELETE_PATTERN = Pattern.compile("\\bdelete\\b\\s+(\\S+)");
    private static final Pattern DELETE_WORD_PATTERN = Pattern.compile("\\bdelete\\b");

    private PendingTask pendingTask;

    /**
     * Returns whether the input is the command that exits Larper.
     */
    public boolean isExitCommand(String input) {
        return input.equals(EXIT_COMMAND);
    }

    /**
     * Returns whether the input is the command that lists all tasks.
     */
    public boolean isListCommand(String input) {
        return input.equals(LIST_COMMAND);
    }

    /**
     * Returns whether the input starts with the command for marking a task as done.
     */
    public boolean isMarkCommand(String input) {
        return input.startsWith(MARK_COMMAND_PREFIX);
    }

    /**
     * Returns whether the input starts with the command for marking a task as not done.
     */
    public boolean isUnmarkCommand(String input) {
        return input.startsWith(UNMARK_COMMAND_PREFIX);
    }

    /**
     * Returns whether the input contains a delete command word.
     */
    public boolean isDeleteCommand(String input) {
        return DELETE_WORD_PATTERN.matcher(input).find();
    }

    /**
     * Returns whether the input is the command that starts a find search.
     */
    public boolean isFindCommand(String input) {
        return input.equals(FIND_COMMAND);
    }

    /**
     * Returns whether the input starts a direct search for an exact tag.
     */
    public boolean isFindTagCommand(String input) {
        return input.equals(FIND_TAG_COMMAND) || input.startsWith(FIND_TAG_COMMAND_PREFIX);
    }

    /**
     * Returns whether the input starts a command that adds tags to a task.
     */
    public boolean isTagCommand(String input) {
        return input.startsWith(TAG_COMMAND_PREFIX);
    }

    /**
     * Returns whether the input starts a command that removes tags from a task.
     */
    public boolean isUntagCommand(String input) {
        return input.startsWith(UNTAG_COMMAND_PREFIX);
    }

    /**
     * Returns the task number from a mark command.
     * If the command does not contain a number, -1 is returned.
     */
    public int parseMarkNumber(String input) {
        assert isMarkCommand(input) : "Mark number parser should only receive mark commands.";
        return parseTaskNumber(input.substring(MARK_COMMAND_PREFIX.length()).trim());
    }

    /**
     * Returns the task number from an unmark command.
     * If the command does not contain a number, -1 is returned.
     */
    public int parseUnmarkNumber(String input) {
        assert isUnmarkCommand(input) : "Unmark number parser should only receive unmark commands.";
        return parseTaskNumber(input.substring(UNMARK_COMMAND_PREFIX.length()).trim());
    }

    /**
     * Returns the task number from a tag or untag command.
     *
     * @param input Tag command to parse.
     * @return Parsed one-based task number, or -1 when it is not numeric.
     */
    public int parseTagTaskNumber(String input) {
        assert isTagCommand(input) || isUntagCommand(input)
                : "Tag task number parser should only receive tag commands.";
        String arguments = getTagCommandArguments(input);
        int separatorIndex = arguments.indexOf(' ');
        String taskNumber = separatorIndex == -1 ? arguments : arguments.substring(0, separatorIndex);
        return parseTaskNumber(taskNumber);
    }

    /**
     * Returns normalized tags from a tag or untag command.
     *
     * @param input Tag command to parse.
     * @return Normalized tags in the order entered.
     * @throws LarperException If no tag was supplied or a tag is invalid.
     */
    public ArrayList<String> parseTagNames(String input) throws LarperException {
        assert isTagCommand(input) || isUntagCommand(input)
                : "Tag name parser should only receive tag commands.";
        String arguments = getTagCommandArguments(input);
        int separatorIndex = arguments.indexOf(' ');
        if (separatorIndex == -1 || arguments.substring(separatorIndex).trim().isEmpty()) {
            throw new MissingTagException();
        }

        ArrayList<String> tags = new ArrayList<>();
        String tagText = arguments.substring(separatorIndex).trim();
        for (String tag : tagText.split("\\s+")) {
            String normalizedTag = Task.normalizeTag(tag);
            if (!Task.isValidTag(normalizedTag)) {
                throw new InvalidTagException();
            }
            tags.add(normalizedTag);
        }
        return tags;
    }

    /**
     * Returns the normalized tag from a direct tag search command.
     *
     * @param input Find-tag command to parse.
     * @return Normalized tag text.
     * @throws LarperException If no tag was supplied or the tag is invalid.
     */
    public String parseFindTag(String input) throws LarperException {
        assert isFindTagCommand(input) : "Find tag parser should only receive find tag commands.";
        String tag = input.substring(FIND_TAG_COMMAND.length()).trim();
        if (tag.isEmpty()) {
            throw new MissingTagException();
        }
        String normalizedTag = Task.normalizeTag(tag);
        if (!Task.isValidTag(normalizedTag)) {
            throw new InvalidTagException();
        }
        return normalizedTag;
    }

    /**
     * Returns the task number from a delete command embedded in the input.
     *
     * @throws NonNumberDeleteException If the delete command is missing a number or uses a non-numeric value.
     */
    public int parseDeleteNumber(String input) throws NonNumberDeleteException {
        Matcher matcher = DELETE_PATTERN.matcher(input);
        if (!matcher.find()) {
            throw new NonNumberDeleteException();
        }

        try {
            return Integer.parseInt(matcher.group(1));
        } catch (NumberFormatException e) {
            throw new NonNumberDeleteException();
        }
    }

    /**
     * Returns a task parsed from the user's input.
     * If a previous command is waiting for a time confirmation, the input is interpreted as that time answer.
     *
     * @throws LarperException If the input does not match a valid task command or pending time answer.
     */
    public Task parseTask(String input) throws LarperException {
        if (pendingTask != null && !startsWithTaskCommand(input)) {
            return completePendingTask(input);
        }

        pendingTask = null;
        return createTask(input);
    }

    private Task createTask(String input) throws LarperException {
        if (input.isEmpty()) {
            throw new NoTaskTypeException();
        }

        if (isTaskTypeWithoutDescription(input)) {
            throw new NoDescriptionException();
        }

        if (input.startsWith(TODO_COMMAND_PREFIX)) {
            return parseTodo(input);
        }
        if (input.startsWith(DEADLINE_COMMAND_PREFIX)) {
            return parseDeadline(input);
        }
        if (input.startsWith(EVENT_COMMAND_PREFIX)) {
            return parseEvent(input);
        }

        throw new NoTaskTypeException();
    }

    private Task parseTodo(String input) throws LarperException {
        String taskInfo = input.substring(TODO_COMMAND_PREFIX.length()).trim();
        if (taskInfo.isEmpty() || countSlashes(taskInfo) != 0) {
            throw new NoDescriptionException();
        }
        ParsedDescription parsedDescription = parseDescriptionAndTags(taskInfo);
        ensureDescriptionCanBeSaved(parsedDescription.description);
        Todo task = new Todo(parsedDescription.description);
        task.addTags(parsedDescription.tags);
        return task;
    }

    private Task parseDeadline(String input) throws LarperException {
        String taskInfo = input.substring(DEADLINE_COMMAND_PREFIX.length()).trim();
        int byIndex = taskInfo.indexOf(BY_MARKER);
        if (byIndex == -1) {
            throw new InvalidDateException("deadline");
        }

        ParsedDescription parsedDescription = parseDescriptionAndTags(taskInfo.substring(0, byIndex));
        String description = parsedDescription.description;
        String by = taskInfo.substring(byIndex + BY_MARKER.length()).trim();
        ensureDescriptionCanBeSaved(description);

        TaskDateTime byDateTime = parseTaskDateTime(by, "deadline", PendingStage.DEADLINE,
                description, null, null, parsedDescription.tags);
        Deadline task = new Deadline(description, byDateTime.getDate(), byDateTime.getTime());
        task.addTags(parsedDescription.tags);
        return task;
    }

    private Task parseEvent(String input) throws LarperException {
        String taskInfo = input.substring(EVENT_COMMAND_PREFIX.length()).trim();
        int fromIndex = taskInfo.indexOf(FROM_MARKER);
        int toIndex = taskInfo.indexOf(TO_MARKER);
        if (fromIndex == -1 || toIndex == -1 || fromIndex > toIndex) {
            throw new InvalidDateException("event start or end");
        }

        ParsedDescription parsedDescription = parseDescriptionAndTags(taskInfo.substring(0, fromIndex));
        String description = parsedDescription.description;
        String from = taskInfo.substring(fromIndex + FROM_MARKER.length(), toIndex).trim();
        String to = taskInfo.substring(toIndex + TO_MARKER.length()).trim();
        ensureDescriptionCanBeSaved(description);
        ensureEventHasExpectedMarkers(taskInfo);

        TaskDateTime startDateTime = parseTaskDateTime(from, "event start", PendingStage.EVENT_START,
                description, to, null, parsedDescription.tags);
        TaskDateTime endDateTime = parseTaskDateTime(to, "event end", PendingStage.EVENT_END,
                description, startDateTime.getDate().toString(), startDateTime.getTime(), parsedDescription.tags);
        Event task = new Event(description, startDateTime, endDateTime);
        task.addTags(parsedDescription.tags);
        return task;
    }

    private Task completePendingTask(String input) throws LarperException {
        assert pendingTask != null : "Pending task completion should only run when a task is waiting for time.";
        String time = input.trim();
        ensurePendingTimeIsValid(time);

        PendingTask taskToComplete = removePendingTask();

        if (taskToComplete.pendingStage == PendingStage.DEADLINE) {
            return completePendingDeadline(taskToComplete, time);
        }
        if (taskToComplete.pendingStage == PendingStage.EVENT_START) {
            return completePendingEventStart(taskToComplete, time);
        }
        if (taskToComplete.pendingStage == PendingStage.EVENT_END) {
            return completePendingEventEnd(taskToComplete, time);
        }

        throw new NoTaskTypeException();
    }

    private void ensurePendingTimeIsValid(String time) throws InvalidTimeException {
        if (time.isEmpty() || !isValidTimeAnswer(time)) {
            throw new InvalidTimeException(pendingTask.waitingFor);
        }
    }

    private PendingTask removePendingTask() {
        PendingTask taskToComplete = pendingTask;
        pendingTask = null;
        assert taskToComplete.description != null && !taskToComplete.description.isBlank()
                : "Pending task should preserve its original description.";
        assert taskToComplete.firstDate != null && !taskToComplete.firstDate.isBlank()
                : "Pending task should preserve its parsed date.";
        return taskToComplete;
    }

    private Task completePendingDeadline(PendingTask taskToComplete, String time) {
        String normalizedTime = TaskDateTimeParser.normalizeTime(time);
        Deadline task = new Deadline(taskToComplete.description, LocalDate.parse(taskToComplete.firstDate),
                normalizedTime);
        task.addTags(taskToComplete.tags);
        return task;
    }

    private Task completePendingEventStart(PendingTask taskToComplete, String time) throws LarperException {
        String normalizedTime = TaskDateTimeParser.normalizeTime(time);
        TaskDateTime endDateTime = parseTaskDateTime(taskToComplete.secondDate, "event end", PendingStage.EVENT_END,
                taskToComplete.description, taskToComplete.firstDate, normalizedTime, taskToComplete.tags);
        Event task = new Event(taskToComplete.description, LocalDate.parse(taskToComplete.firstDate),
                normalizedTime, endDateTime.getDate(), endDateTime.getTime());
        task.addTags(taskToComplete.tags);
        return task;
    }

    private Task completePendingEventEnd(PendingTask taskToComplete, String time) {
        String normalizedTime = TaskDateTimeParser.normalizeTime(time);
        Event task = new Event(taskToComplete.description, taskToComplete.firstDate, taskToComplete.firstTime,
                taskToComplete.secondDate, normalizedTime);
        task.addTags(taskToComplete.tags);
        return task;
    }

    private TaskDateTime parseTaskDateTime(String text, String label, PendingStage pendingStage, String description,
            String extraDate, String extraTime, ArrayList<String> tags)
            throws InvalidDateException, InvalidTimeException {
        TaskDateTime taskDateTime = parseDateTimeOrThrow(text, label);

        if (taskDateTime.getTime().isEmpty()) {
            handleMissingTime(taskDateTime, label, pendingStage, description, extraDate, extraTime, tags);
        }

        assert taskDateTime.getDate() != null : "Date-time parser should return a parsed date on success.";
        assert !taskDateTime.getTime().isEmpty() : "Date-time parser should return a time on success.";
        return taskDateTime;
    }

    private TaskDateTime parseDateTimeOrThrow(String text, String label) throws InvalidDateException {
        try {
            return TaskDateTimeParser.parse(text, "");
        } catch (RuntimeException e) {
            throw new InvalidDateException(label);
        }
    }

    private void handleMissingTime(TaskDateTime taskDateTime, String label, PendingStage pendingStage,
            String description, String extraDate, String extraTime, ArrayList<String> tags)
            throws InvalidTimeException {
        rememberPendingTask(taskDateTime, label, pendingStage, description, extraDate, extraTime, tags);
        throw new InvalidTimeException(label);
    }

    private void rememberPendingTask(TaskDateTime taskDateTime, String label, PendingStage pendingStage,
            String description,
            String extraDate, String extraTime, ArrayList<String> tags) {
        if (pendingStage == PendingStage.EVENT_END) {
            pendingTask = new PendingTask(pendingStage, description, extraDate, extraTime,
                    taskDateTime.getDate().toString(), label, tags);
        } else {
            pendingTask = new PendingTask(pendingStage, description, taskDateTime.getDate().toString(),
                    extraTime, extraDate, label, tags);
        }
        assert pendingTask != null : "Missing optional time should create a pending task.";
        assert pendingTask.pendingStage == pendingStage
                : "Pending task should remember the command type being completed.";
    }

    private boolean isValidTimeAnswer(String text) {
        return TaskDateTimeParser.isValidTimeAnswer(text);
    }

    private boolean startsWithTaskCommand(String input) {
        return input.startsWith(TODO_COMMAND_PREFIX) || input.startsWith(DEADLINE_COMMAND_PREFIX)
                || input.startsWith(EVENT_COMMAND_PREFIX) || isTaskTypeWithoutDescription(input);
    }

    private boolean isTaskTypeWithoutDescription(String input) {
        return input.equals(TODO_COMMAND) || input.equals(DEADLINE_COMMAND) || input.equals(EVENT_COMMAND);
    }

    private void ensureDescriptionCanBeSaved(String description)
            throws NoDescriptionException, InvalidDescriptionException {
        if (description.isEmpty()) {
            throw new NoDescriptionException();
        }
        if (description.contains("|")) {
            throw new InvalidDescriptionException();
        }
    }

    private ParsedDescription parseDescriptionAndTags(String taskInfo) throws InvalidTagException {
        ArrayList<String> tags = new ArrayList<>();
        StringBuilder description = new StringBuilder();
        String[] words = taskInfo.trim().split("\\s+");
        for (String word : words) {
            if (word.startsWith("#")) {
                String tag = word.substring(1);
                if (!Task.isValidTag(tag)) {
                    throw new InvalidTagException();
                }
                tags.add(tag);
            } else {
                if (description.length() > 0) {
                    description.append(" ");
                }
                description.append(word);
            }
        }
        return new ParsedDescription(description.toString(), tags);
    }

    private void ensureEventHasExpectedMarkers(String taskInfo) throws InvalidDateException {
        if (countSlashes(taskInfo) != EXPECTED_EVENT_SLASH_COUNT) {
            throw new InvalidDateException("event start or end");
        }
    }

    private int countSlashes(String input) {
        int slashCount = 0;
        int index = 0;
        while (index < input.length()) {
            if (input.charAt(index) == '/') {
                slashCount++;
            }
            index++;
        }
        return slashCount;
    }

    private int parseTaskNumber(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String getTagCommandArguments(String input) {
        String commandPrefix = isUntagCommand(input) ? UNTAG_COMMAND_PREFIX : TAG_COMMAND_PREFIX;
        return input.substring(commandPrefix.length()).trim();
    }

    private enum PendingStage {
        DEADLINE,
        EVENT_START,
        EVENT_END
    }

    private static class PendingTask {
        private PendingStage pendingStage;
        private String description;
        private String firstDate;
        private String firstTime;
        private String secondDate;
        private String waitingFor;
        private ArrayList<String> tags;

        public PendingTask(PendingStage pendingStage, String description, String firstDate, String firstTime,
                String secondDate, String waitingFor, ArrayList<String> tags) {
            assert pendingStage != null : "Pending task stage should identify how to resume parsing.";
            assert waitingFor != null && !waitingFor.isBlank() : "Pending task should know which time is missing.";
            assert tags != null : "Pending task should preserve task tags while waiting for a time.";
            this.pendingStage = pendingStage;
            this.description = description;
            this.firstDate = firstDate;
            this.firstTime = firstTime;
            this.secondDate = secondDate;
            this.waitingFor = waitingFor;
            this.tags = tags;
        }
    }

    private static class ParsedDescription {
        private String description;
        private ArrayList<String> tags;

        ParsedDescription(String description, ArrayList<String> tags) {
            assert description != null : "Parsed task description should not be null.";
            assert tags != null : "Parsed task tags should not be null.";
            this.description = description;
            this.tags = tags;
        }
    }
}
