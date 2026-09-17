package dog.model;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dog.exceptions.DogException;
import dog.storage.DateUtils;

/**
 * Represents a deadline task that must be completed by a specific date.
 */
public class Deadline extends Task {
    static final String TASK_ICON = "D";

    protected LocalDate dueDate;

    /**
     * Creates a deadline task with the specified description and deadline date.
     *
     * @param description the task description.
     * @param dueDate     the deadline date.
     */
    public Deadline(String description, LocalDate dueDate) {
        super(description);
        this.dueDate = dueDate;
    }

    /**
     * Creates a deadline task with the specified description, deadline date, and completion status.
     *
     * @param description the task description.
     * @param dueDate     the deadline date.
     * @param isDone      true if the task is completed, false otherwise.
     */
    public Deadline(String description, LocalDate dueDate, boolean isDone) {
        super(description, isDone);
        this.dueDate = dueDate;
    }

    /**
     * Parses a deadline task from its saved string format.
     *
     * @param saveString the saved string in format "D | [X or space] | date | description".
     * @return the parsed Deadline, or null if the format is invalid.
     */
    public static Deadline fromSaveFormat(String saveString) {
        if (saveString == null) {
            return null;
        }

        String[] parts = saveString.split(" \\| ", 4);
        if (parts.length < 4) {
            return null;
        }

        String taskIcon = parts[0];
        String statusIcon = parts[1];
        String byStr = parts[2];
        String description = parts[3];

        if (!taskIcon.equals(TASK_ICON)) {
            return null;
        }
        if (isInvalidStatusIcon(statusIcon)) {
            return null;
        }

        boolean isDone = isCompletedStatusIcon(statusIcon);

        try {
            LocalDate by = DateUtils.parse(byStr);
            return new Deadline(description, by, isDone);
        } catch (DogException e) {
            System.out.println("Error loading deadline: " + e.getMessage());
            return null;
        }
    }

    /**
     * Parses a deadline task from user input.
     *
     * @param input the user input string.
     * @return the parsed Deadline.
     * @throws DogException if the input format is invalid.
     */
    public static Deadline parse(String input) throws DogException {
        final String badInputMessage = "ARF ARF Deadline tasks must have a description and due date!\n"
                + "Expected: deadline <description> /by <due date>";

        // Pattern: "<description> /by <by>" where <by> is in yyyy-MM-dd format
        Pattern pattern = Pattern.compile("^\\s*(.+?)\\s+/by\\s+(.+?)\\s*$");
        Matcher matcher = pattern.matcher(input);

        if (!matcher.matches()) {
            throw new DogException(badInputMessage);
        }

        assert matcher.groupCount() == 2 : "Todo regex group count should be 2";
        String description = matcher.group(1).trim();
        String byStr = matcher.group(2).trim();

        if (description.isEmpty() || byStr.isEmpty()) {
            throw new DogException(badInputMessage);
        }

        LocalDate dueDate = DateUtils.parse(byStr);
        assert dueDate != null : "parsed due date (by) LocalDate should not be null";
        return new Deadline(description, dueDate);
    }

    /**
     * Returns a string representation of the deadline task for saving to file.
     *
     * @return the deadline in save format.
     */
    @Override
    public String toSaveFormat() {
        return String.format(TASK_ICON + " | %s | %s | %s", getStatusIcon(), dueDate.toString(), description);
    }

    /**
     * Returns a string representation of the deadline task for display.
     *
     * @return the deadline in display format.
     */
    @Override
    public String toString() {
        return "[" + TASK_ICON + "]" + super.toString() + " (by: " + DateUtils.format(this.dueDate) + ")";
    }
}
