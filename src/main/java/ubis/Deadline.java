package ubis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a task that has a deadline date.
 */
public class Deadline extends Task {
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("MMM d yyyy");
    private static final Pattern BY_PARAMETER_PATTERN = Pattern.compile("(?<!\\S)/by(?!\\S)");

    private LocalDate deadline;

    /**
     * Initializes the deadline task from user input containing description and date.
     *
     * @param input Input string containing description and "/by &lt;date&gt;".
     * @return Initialized Deadline task, or null if arguments/date are invalid.
     */
    @Override
    public Task initialise(String input) {
        if (input == null || input.isBlank()) {
            setInitialisationError("Please provide a task name and deadline.\n"
                    + "Example: deadline submit report /by 2026-09-30");
            return null;
        }

        String trimmedInput = input.trim();
        Matcher byParameter = findByParameter(trimmedInput);
        if (byParameter == null) {
            return null;
        }

        String taskName = trimmedInput.substring(0, byParameter.start()).trim();
        String dateText = trimmedInput.substring(byParameter.end()).trim();
        if (!hasRequiredFields(taskName, dateText)) {
            return null;
        }

        this.name = taskName;
        if (!parseDate(dateText)) {
            return null;
        }

        this.type = TaskType.DEADLINE;
        return this;
    }

    /**
     * Finds the sole deadline parameter, or records an error and returns null.
     */
    private Matcher findByParameter(String trimmedInput) {
        Matcher byParameter = BY_PARAMETER_PATTERN.matcher(trimmedInput);
        if (!byParameter.find()) {
            setInitialisationError("A deadline needs a \"/by\" parameter followed by its date.\n"
                    + "Example: deadline submit report /by 2026-09-30");
            return null;
        }

        Matcher remainingParameters = BY_PARAMETER_PATTERN.matcher(trimmedInput);
        if (remainingParameters.find(byParameter.end())) {
            setInitialisationError("A deadline must contain exactly one \"/by\" parameter.");
            return null;
        }

        return byParameter;
    }

    /**
     * Checks that the task name and deadline date are present and records any error.
     */
    private boolean hasRequiredFields(String taskName, String dateText) {
        if (taskName.isBlank()) {
            setInitialisationError("Please provide a task name before \"/by\".");
            return false;
        }
        if (dateText.isBlank()) {
            setInitialisationError("Please provide a deadline date after \"/by\" in YYYY-MM-DD format.");
            return false;
        }

        return true;
    }

    /**
     * Parses the deadline date, recording an error if it is not a valid calendar date.
     */
    private boolean parseDate(String dateText) {
        try {
            this.deadline = LocalDate.parse(dateText);
        } catch (DateTimeParseException e) {
            setInitialisationError("The deadline date is invalid. "
                    + "Use YYYY-MM-DD and enter a real calendar date.");
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " (by: " + deadline.format(OUTPUT_FORMATTER) + ")";
    }

    @Override
    public String stringify() {
        return super.stringify() + "{" + deadline + "}";
    }
}
