package linus.validator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import linus.invalidtaskexception.InvalidTaskException;
import linus.task.Task;

/**
 * Represents a validator that ensures input is valid for acting on a specific tasklist.
 */
public class Validator {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final int TASK_DESCRIPTION_INDEX = 1;
    public static final int TASK_START_DATE_INDEX = 2;

    private static final String EMPTY_DESCRIPTION_ERROR = "Here's a tech tip! Enter a valid task description :-(";
    private static final String INVALID_TASK_INDEX_ERROR = "Here's a tech tip! Enter a valid task ID :-(";
    private static final String UNKNOWN_COMMAND_ERROR = "Here's a tech tip! Enter the command with a valid format! :-(";
    private static final String INVALID_START_END_ERROR =
            "Here's a tech tip! The start date cannot be after end date! :-(";
    private static final String TASK_INDEX_REGEX = "[1-9][0-9]*";

    /** The tasklist to validate the input against. */
    private final List<Task> taskList;

    /**
     * Creates a new Validator to validate input.
     *
     * @param taskList List of tasks to validate input against.
     */
    public Validator(List<Task> taskList) {
        this.taskList = taskList;
    }

    /**
     * Validates that the parsed input is valid for the specific tasklist.
     *
     * @param parsedInput Parsed input from the parser.
     * @throws InvalidTaskException If description is blank or task number is negative, 0 or larger than tasklist size.
     * @throws NumberFormatException If the task ID is not a valid integer.
     * @throws DateTimeParseException If date does not follow yyyy-MM-dd format.
     */
    public void validate(List<String> parsedInput) throws InvalidTaskException, NumberFormatException,
            DateTimeParseException {
        assert parsedInput != null : "Parsed input provided for validation is null";
        assert !parsedInput.isEmpty() : "No parsed input provided for validation";
        String command = parsedInput.getFirst();

        switch (command) {
            case "list":
                break;
            case "find":
                // Fallthrough
            case "todo":
                assert parsedInput.size() == 2 : "Invalid number of details provided for a find/todo command";
                this.validateDescription(parsedInput.get(TASK_DESCRIPTION_INDEX));
                break;
            case "mark":
                // Fallthrough
            case "unmark":
                // Fallthrough
            case "delete":
                assert parsedInput.size() == 2 : "Invalid number of details provided for a mark/unmark/delete command";
                // For mark/unmark/delete command, the description represents the task index.
                this.validateTaskIndex(parsedInput.get(TASK_DESCRIPTION_INDEX));
                break;
            case "deadline":
                this.validateDeadlineCommand(parsedInput);
                break;
            case "event":
                this.validateEventCommand(parsedInput);
                break;
            default:
                // Should not reach here.
                throw new InvalidTaskException(UNKNOWN_COMMAND_ERROR);
        }
    }

    private void validateDescription(String description) throws InvalidTaskException {
        if (description.isBlank()) {
            throw new InvalidTaskException(EMPTY_DESCRIPTION_ERROR);
        }
    }

    private void validateTaskIndex(String index) throws NumberFormatException, InvalidTaskException {
        if (!index.matches(TASK_INDEX_REGEX)) {
            throw new NumberFormatException();
        }
        int taskIndex = Integer.parseInt(index);
        if (taskIndex < 1 || taskIndex > this.taskList.size()) {
            throw new InvalidTaskException(INVALID_TASK_INDEX_ERROR);
        }
    }

    private LocalDate validateDateFormat(String date) throws DateTimeParseException {
        return LocalDate.parse(date, Validator.DATE_FORMAT);
    }

    private void validateDeadlineCommand(List<String> parsedInput) throws InvalidTaskException {
        assert parsedInput.size() == 3 : "Invalid number of details provided for a deadline command";
        String description = parsedInput.get(TASK_DESCRIPTION_INDEX);
        this.validateDescription(description);
        String deadline = parsedInput.getLast();
        this.validateDateFormat(deadline);
    }

    private void validateEventCommand(List<String> parsedInput) throws InvalidTaskException {
        assert parsedInput.size() == 4 : "Invalid number of details provided for an event command";
        String description = parsedInput.get(TASK_DESCRIPTION_INDEX);
        this.validateDescription(description);
        String startDate = parsedInput.get(TASK_START_DATE_INDEX);
        String endDate = parsedInput.getLast();
        LocalDate start = this.validateDateFormat(startDate);
        LocalDate end = this.validateDateFormat(endDate);
        if (start.isAfter(end)) {
            throw new InvalidTaskException(INVALID_START_END_ERROR);
        }
    }
}
