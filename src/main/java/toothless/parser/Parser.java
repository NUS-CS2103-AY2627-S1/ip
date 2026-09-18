package toothless.parser;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import toothless.command.Command;
import toothless.command.CommandType;
import toothless.command.DeadlineCommand;
import toothless.command.DeleteCommand;
import toothless.command.EditCommand;
import toothless.command.EventCommand;
import toothless.command.ExitCommand;
import toothless.command.FindCommand;
import toothless.command.ListCommand;
import toothless.command.MarkCommand;
import toothless.command.TodoCommand;
import toothless.command.UnmarkCommand;
import toothless.exception.ToothlessException;
import toothless.task.DeadlineDate;
import toothless.task.EventTime;
import toothless.task.TaskUpdate;

/**
 * Interprets user input and validates the details supplied to Toothless commands.
 */
public class Parser {
    private static final String COMMANDS =
            "todo, deadline, event, list, find, mark, unmark, delete, edit, or bye";
    private static final String DESCRIPTION_SEPARATOR = "/description";
    private static final String DEADLINE_SEPARATOR = "/by";
    private static final String EVENT_START_SEPARATOR = "/from";
    private static final String EVENT_END_SEPARATOR = "/to";
    private static final Set<String> EDIT_SEPARATORS = Set.of(
            DESCRIPTION_SEPARATOR, DEADLINE_SEPARATOR,
            EVENT_START_SEPARATOR, EVENT_END_SEPARATOR);
    private static final Pattern EDIT_FIELD_PATTERN = Pattern.compile("(?<!\\S)/\\S+");
    private static final int MAX_INPUT_LENGTH = 4096;
    private static final int MAX_DESCRIPTION_LENGTH = 500;

    /**
     * Creates a stateless parser for commands entered during a Toothless session.
     */
    public Parser() {
    }

    /**
     * Interprets one line of input and constructs its executable command.
     *
     * @param input complete line entered by the user.
     * @param taskCount current number of tasks.
     * @return executable command represented by the input
     * @throws ToothlessException if the input is empty, unknown, or malformed
     */
    public Command parse(String input, int taskCount) throws ToothlessException {
        String[] commandParts = splitInput(input);
        CommandType commandType = CommandType.fromKeyword(commandParts[0]);
        String details = commandParts.length == 2 ? commandParts[1].trim() : "";

        validateCommand(commandType, details);
        return createCommand(commandType, details, taskCount);
    }

    /**
     * Splits a nonempty input line into its command word and optional details.
     */
    private String[] splitInput(String input) throws ToothlessException {
        if (input == null || input.length() > MAX_INPUT_LENGTH) {
            throw new ToothlessException("That command is too long or unavailable for Toothless.\n"
                    + "Please enter a shorter command (at most 4096 characters).");
        }
        String trimmedInput = input.trim();
        if (trimmedInput.isEmpty()) {
            throw new ToothlessException("Toothless heard a tiny silence. What should he do?\n"
                    + "Try " + COMMANDS + ".");
        }
        return trimmedInput.split("\\s+", 2);
    }

    /**
     * Rejects unknown commands and extra details on commands that take none.
     */
    private void validateCommand(CommandType commandType, String details) throws ToothlessException {
        if (commandType == CommandType.UNKNOWN
                || commandType == CommandType.BYE && !details.isEmpty()) {
            throw new ToothlessException(
                    "Toothless tilted his head—he doesn’t recognise that command.\n"
                            + "Try " + COMMANDS + ".");
        }
        if (commandType == CommandType.LIST && !details.isEmpty()) {
            throw new ToothlessException("The list command doesn't need extra words.\n"
                    + "Try: list");
        }
    }

    /**
     * Creates the command after its word and allowed details have been checked.
     */
    private Command createCommand(CommandType commandType, String details, int taskCount)
            throws ToothlessException {
        assert commandType != CommandType.UNKNOWN
                : "Validated command type should be supported";
        switch (commandType) {
            case BYE:
                return new ExitCommand();
            case LIST:
                return new ListCommand();
            case FIND:
                return new FindCommand(parseFindKeyword(details));
            case MARK:
                return new MarkCommand(parseTaskNumber(commandType, details, taskCount));
            case UNMARK:
                return new UnmarkCommand(parseTaskNumber(commandType, details, taskCount));
            case DELETE:
                return new DeleteCommand(parseTaskNumber(commandType, details, taskCount));
            case EDIT:
                ParsedEdit edit = parseEditDetails(details, taskCount);
                return new EditCommand(edit.getTaskNumber(), edit.getUpdate());
            case TODO:
                return new TodoCommand(parseTodoDescription(details));
            case DEADLINE:
                ParsedDeadline deadline = parseDeadlineDetails(details);
                return new DeadlineCommand(deadline.getDescription(), deadline.getBy());
            case EVENT:
                ParsedEvent event = parseEventDetails(details);
                return new EventCommand(event.getDescription(), event.getFrom(), event.getTo());
            default:
                throw new IllegalStateException("Unsupported command type: " + commandType);
        }
    }

    /**
     * Parses the one-based task number supplied to a task command.
     *
     * @param commandType command type whose argument is being checked.
     * @param argument text following the command name.
     * @param taskCount current number of tasks.
     * @return parsed one-based task number
     * @throws ToothlessException if the task number is absent or not numeric
     */
    private int parseTaskNumber(CommandType commandType, String argument, int taskCount)
            throws ToothlessException {
        String commandName = commandType.toString();
        if (taskCount == 0) {
            throw new ToothlessException("Toothless's cave is empty, so there is no task to "
                    + commandName + ".\nAdd a task first, then try again.");
        }
        if (argument.isBlank()) {
            throw new ToothlessException("Toothless needs a task number to " + commandName + ".\n"
                    + "Try: " + commandName + " 1");
        }
        if (!argument.matches("[+-]?\\d+")) {
            throw new ToothlessException("That task number looks a little unusual.\n"
                    + "Please use a whole number, like: " + commandName + " 1");
        }

        final int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw new ToothlessException("That task number is too large for Toothless to count.\n"
                    + "Please choose a number from 1 to " + taskCount + ".");
        }
        return taskNumber;
    }

    /**
     * Parses the one-based task number and named replacement fields of an edit command.
     *
     * @param details text following the edit command.
     * @param taskCount current number of tasks.
     * @return validated task number and replacement fields
     * @throws ToothlessException if the task number or field structure is invalid
     */
    private ParsedEdit parseEditDetails(String details, int taskCount)
            throws ToothlessException {
        if (details.isBlank()) {
            parseTaskNumber(CommandType.EDIT, "", taskCount);
        }

        String[] editParts = details.split("\\s+", 2);
        if (editParts[0].startsWith("/")) {
            parseTaskNumber(CommandType.EDIT, "", taskCount);
        }
        int taskNumber = parseTaskNumber(CommandType.EDIT, editParts[0], taskCount);
        if (editParts.length == 1 || editParts[1].isBlank()) {
            throw new ToothlessException("Toothless needs at least one field to edit.\n"
                    + "Try: edit 1 /description read the new textbook");
        }

        String fieldText = editParts[1].trim();
        List<ParsedEditField> fields = findEditFields(fieldText);
        if (fields.isEmpty() || fields.get(0).getStartIndex() != 0) {
            throw malformedEditException();
        }
        return createParsedEdit(taskNumber, fieldText, fields);
    }

    /**
     * Finds every slash-prefixed field token in edit details.
     *
     * @param fieldText text following the edit task number.
     * @return field tokens in their input order
     */
    private List<ParsedEditField> findEditFields(String fieldText) {
        List<ParsedEditField> fields = new ArrayList<>();
        Matcher matcher = EDIT_FIELD_PATTERN.matcher(fieldText);
        while (matcher.find()) {
            fields.add(new ParsedEditField(matcher.group(), matcher.start(), matcher.end()));
        }
        return fields;
    }

    /**
     * Converts ordered edit fields and their values into one task update.
     *
     * @param taskNumber parsed one-based task number.
     * @param fieldText complete named-field text.
     * @param fields field tokens in their input order.
     * @return validated edit details
     * @throws ToothlessException if a field is unknown, duplicated, empty, or invalid
     */
    private ParsedEdit createParsedEdit(int taskNumber, String fieldText,
            List<ParsedEditField> fields) throws ToothlessException {
        Set<String> seenFields = new HashSet<>();
        String description = null;
        LocalDate by = null;
        String from = null;
        String to = null;

        for (int i = 0; i < fields.size(); i++) {
            ParsedEditField field = fields.get(i);
            String separator = field.getSeparator();
            if (!EDIT_SEPARATORS.contains(separator)) {
                throw malformedEditException();
            }
            if (!seenFields.add(separator)) {
                throw new ToothlessException("Each edit field can appear only once.\n"
                        + "Try: edit 1 /description read the new textbook");
            }

            int valueEndIndex = i + 1 < fields.size()
                    ? fields.get(i + 1).getStartIndex() : fieldText.length();
            String value = fieldText.substring(field.getValueStartIndex(), valueEndIndex).trim();
            if (value.isEmpty()) {
                throw new ToothlessException("This edit is missing a value after '"
                        + separator + "'.\nPlease add the new value and try again.");
            }

            switch (separator) {
                case DESCRIPTION_SEPARATOR:
                    validateDescription(value);
                    description = value;
                    break;
                case DEADLINE_SEPARATOR:
                    by = parseEditedDeadlineDate(value);
                    break;
                case EVENT_START_SEPARATOR:
                    from = value;
                    break;
                case EVENT_END_SEPARATOR:
                    to = value;
                    break;
                default:
                    throw new IllegalStateException("Unsupported edit field: " + separator);
            }
        }
        return new ParsedEdit(taskNumber, new TaskUpdate(description, by, from, to));
    }

    /**
     * Parses an edited deadline date using the same format as deadline creation.
     *
     * @param value replacement deadline date text.
     * @return parsed deadline date
     * @throws ToothlessException if the date is not a real ISO date
     */
    private LocalDate parseEditedDeadlineDate(String value) throws ToothlessException {
        try {
            return DeadlineDate.parse(value);
        } catch (DateTimeParseException exception) {
            throw new ToothlessException("That edited deadline date made Toothless tilt his head.\n"
                    + "Please use a real date in yyyy-MM-dd format.");
        }
    }

    /**
     * Creates the shared guidance for malformed edit field structure.
     *
     * @return user-facing edit format exception
     */
    private ToothlessException malformedEditException() {
        return new ToothlessException("This edit's format has Toothless puzzled.\n"
                + "Use: edit TASK_NUMBER /FIELD NEW_VALUE");
    }

    /**
     * Parses a todo description after confirming that it is present.
     *
     * @param details text following the todo command.
     * @return validated todo description
     * @throws ToothlessException if the description is empty
     */
    private String parseTodoDescription(String details) throws ToothlessException {
        if (details.isBlank()) {
            throw new ToothlessException("Toothless couldn’t find a description for that todo.\n"
                    + "Try: todo borrow book");
        }
        validateDescription(details);
        return details.trim();
    }

    /**
     * Parses a search keyword after confirming that it is present.
     *
     * @param details text following the find command
     * @return validated search keyword
     * @throws ToothlessException if the keyword is empty
     */
    private String parseFindKeyword(String details) throws ToothlessException {
        if (details.isBlank()) {
            throw new ToothlessException("Toothless needs a keyword to sniff out matching tasks.\n"
                    + "Try: find book");
        }
        if (details.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ToothlessException("That search is too long for Toothless.\n"
                    + "Try a shorter keyword (at most 500 characters).");
        }
        return details.trim();
    }

    /**
     * Rejects descriptions too large to display and save comfortably.
     */
    private void validateDescription(String description) throws ToothlessException {
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new ToothlessException("That task description is too long for Toothless.\n"
                    + "Please use at most 500 characters.");
        }
    }

    /**
     * Parses and validates a deadline description and finishing date.
     *
     * @param details text following the deadline command.
     * @return validated deadline details
     * @throws ToothlessException if the command structure is incomplete or ambiguous
     */
    private ParsedDeadline parseDeadlineDetails(String details) throws ToothlessException {
        String trimmed = details.trim();
        int byIndex = findSeparator(trimmed, DEADLINE_SEPARATOR, 0);
        if (byIndex < 0) {
            throw new ToothlessException("This deadline is missing '/by' and its date.\n"
                    + "Try: deadline return book /by 2019-12-02");
        }
        int contentAfterByIndex = byIndex + DEADLINE_SEPARATOR.length();
        if (findSeparator(trimmed, DEADLINE_SEPARATOR, contentAfterByIndex) >= 0
                || containsAnySeparator(trimmed, EVENT_START_SEPARATOR, EVENT_END_SEPARATOR)) {
            throw new ToothlessException("This deadline's format has Toothless puzzled.\n"
                    + "Please use: deadline DESCRIPTION /by yyyy-MM-dd");
        }
        String description = trimmed.substring(0, byIndex).trim();
        String by = trimmed.substring(contentAfterByIndex).trim();
        if (description.isEmpty()) {
            throw new ToothlessException(
                    "Toothless couldn’t find a description for that deadline.\n"
                            + "Try: deadline return book /by 2019-12-02");
        }
        validateDescription(description);
        if (by.isEmpty()) {
            throw new ToothlessException("This deadline is missing its date.\n"
                    + "Try: deadline return book /by 2019-12-02");
        }
        try {
            LocalDate date = DeadlineDate.parse(by);
            return new ParsedDeadline(description, date);
        } catch (DateTimeParseException exception) {
            throw new ToothlessException("That deadline date made Toothless tilt his head.\n"
                    + "Please use a real date in yyyy-MM-dd format.");
        }
    }

    /**
     * Holds validated values parsed from a deadline command.
     */
    private static final class ParsedDeadline {
        private final String description;
        private final LocalDate by;

        /**
         * Groups a validated deadline description with its parsed due date.
         *
         * @param description validated text describing the deadline task
         * @param by parsed date by which the task should be completed
         */
        private ParsedDeadline(String description, LocalDate by) {
            this.description = description;
            this.by = by;
        }

        /**
         * Returns the parsed deadline description.
         *
         * @return deadline description
         */
        private String getDescription() {
            return description;
        }

        /**
         * Returns the parsed deadline date.
         *
         * @return deadline date
         */
        private LocalDate getBy() {
            return by;
        }
    }

    /**
     * Holds a parsed task number and its validated replacement fields.
     */
    private static final class ParsedEdit {
        private final int taskNumber;
        private final TaskUpdate update;

        /**
         * Groups a task number with the replacement fields to apply.
         *
         * @param taskNumber parsed one-based task number.
         * @param update validated replacement fields.
         */
        private ParsedEdit(int taskNumber, TaskUpdate update) {
            this.taskNumber = taskNumber;
            this.update = update;
        }

        /**
         * Returns the parsed one-based task number.
         *
         * @return task number
         */
        private int getTaskNumber() {
            return taskNumber;
        }

        /**
         * Returns the parsed replacement fields.
         *
         * @return task update
         */
        private TaskUpdate getUpdate() {
            return update;
        }
    }

    /**
     * Identifies one named edit field and the location of its value.
     */
    private static final class ParsedEditField {
        private final String separator;
        private final int startIndex;
        private final int valueStartIndex;

        /**
         * Records the separator and source indexes of one edit field.
         *
         * @param separator slash-prefixed field name.
         * @param startIndex index at which the field name begins.
         * @param valueStartIndex index immediately after the field name.
         */
        private ParsedEditField(String separator, int startIndex, int valueStartIndex) {
            this.separator = separator;
            this.startIndex = startIndex;
            this.valueStartIndex = valueStartIndex;
        }

        /**
         * Returns the slash-prefixed field name.
         *
         * @return field separator
         */
        private String getSeparator() {
            return separator;
        }

        /**
         * Returns where the field begins within the edit details.
         *
         * @return field start index
         */
        private int getStartIndex() {
            return startIndex;
        }

        /**
         * Returns where the field's value begins within the edit details.
         *
         * @return value start index
         */
        private int getValueStartIndex() {
            return valueStartIndex;
        }
    }

    /**
     * Parses and validates an event description, start, and end.
     *
     * @param details text following the event command.
     * @return validated event details
     * @throws ToothlessException if the command structure is incomplete or ambiguous
     */
    private ParsedEvent parseEventDetails(String details) throws ToothlessException {
        String trimmed = details.trim();
        int fromIndex = findSeparator(trimmed, EVENT_START_SEPARATOR, 0);
        int toIndex = findSeparator(trimmed, EVENT_END_SEPARATOR, 0);
        if (fromIndex < 0) {
            throw new ToothlessException("This event is missing its starting time after '/from'.\n"
                    + "Try: event DESCRIPTION /from START /to END");
        }
        if (toIndex < 0) {
            throw new ToothlessException("This event is missing its ending time after '/to'.\n"
                    + "Try: event DESCRIPTION /from START /to END");
        }
        if (toIndex < fromIndex) {
            throw new ToothlessException("The event's '/from' must come before '/to'.\n"
                    + "Try: event DESCRIPTION /from START /to END");
        }
        int contentAfterFromIndex = fromIndex + EVENT_START_SEPARATOR.length();
        int contentAfterToIndex = toIndex + EVENT_END_SEPARATOR.length();
        if (findSeparator(trimmed, EVENT_START_SEPARATOR, contentAfterFromIndex) >= 0
                || findSeparator(trimmed, EVENT_END_SEPARATOR, contentAfterToIndex) >= 0
                || containsAnySeparator(trimmed, DEADLINE_SEPARATOR)) {
            throw new ToothlessException("This event's format has Toothless puzzled.\n"
                    + "Try: event DESCRIPTION /from START /to END");
        }
        String description = trimmed.substring(0, fromIndex).trim();
        String from = trimmed.substring(contentAfterFromIndex, toIndex).trim();
        String to = trimmed.substring(contentAfterToIndex).trim();
        if (description.isEmpty()) {
            throw new ToothlessException("Toothless couldn’t find a description for that event.\n"
                    + "Try: event DESCRIPTION /from START /to END");
        }
        validateDescription(description);
        if (from.isEmpty()) {
            throw new ToothlessException("This event is missing its starting time.\n"
                    + "Try: event DESCRIPTION /from START /to END");
        }
        if (to.isEmpty()) {
            throw new ToothlessException("This event is missing its ending time.\n"
                    + "Try: event DESCRIPTION /from START /to END");
        }
        EventTime.validate(from, to);
        return new ParsedEvent(description, from, to);
    }

    /**
     * Holds validated values parsed from an event command.
     */
    private static final class ParsedEvent {
        private final String description;
        private final String from;
        private final String to;

        /**
         * Groups the validated text fields needed to create an event task.
         *
         * @param description validated text describing the event
         * @param from validated event starting time
         * @param to validated event ending time
         */
        private ParsedEvent(String description, String from, String to) {
            this.description = description;
            this.from = from;
            this.to = to;
        }

        /**
         * Returns the parsed event description.
         *
         * @return event description
         */
        private String getDescription() {
            return description;
        }

        /**
         * Returns the parsed event starting time.
         *
         * @return event starting time
         */
        private String getFrom() {
            return from;
        }

        /**
         * Returns the parsed event ending time.
         *
         * @return event ending time
         */
        private String getTo() {
            return to;
        }
    }

    /**
     * Returns whether a separator occurs as a separate input token.
     *
     * @param text complete command details being inspected
     * @param index starting index of the separator occurrence
     * @param separator separator token expected at the given index
     * @return true when whitespace or a text boundary surrounds the separator
     */
    private boolean isSeparatorAt(String text, int index, String separator) {
        boolean hasLeftBoundary = index == 0 || Character.isWhitespace(text.charAt(index - 1));
        int endIndex = index + separator.length();
        boolean hasRightBoundary = endIndex == text.length()
                || Character.isWhitespace(text.charAt(endIndex));
        return hasLeftBoundary && hasRightBoundary;
    }

    /**
     * Returns whether the text contains any of the given separator tokens.
     *
     * @param text complete command details being inspected
     * @param separators separator tokens to locate
     * @return true when any separator occurs as a complete token
     */
    private boolean containsAnySeparator(String text, String... separators) {
        return Arrays.stream(separators)
                .anyMatch(separator -> findSeparator(text, separator, 0) >= 0);
    }

    /**
     * Finds a separator that occurs as a complete token rather than inside a value.
     *
     * @param text complete command details being searched
     * @param separator separator token to locate
     * @param fromIndex index from which to begin searching
     * @return index of the next complete separator token, or {@code -1} if none exists
     */
    private int findSeparator(String text, String separator, int fromIndex) {
        int index = text.indexOf(separator, fromIndex);
        while (index >= 0 && !isSeparatorAt(text, index, separator)) {
            index = text.indexOf(separator, index + 1);
        }
        return index;
    }

}
