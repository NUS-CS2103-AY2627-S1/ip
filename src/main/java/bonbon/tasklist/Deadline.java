package bonbon.tasklist;

import bonbon.parser.Parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Deadline is a type of task with a due date.
 * The due date is represented in <code>datetime</code>.
 */
public class Deadline extends Task {
    private LocalDateTime date;

    /**
     * Creates a Deadline.
     *
     * @param name the name of the deadline.
     * @param date the date of the deadline, written in format 'yyyy-MM-dd HHmm'.
     */
    public Deadline(String name, LocalDateTime date) {
        super(name);
        this.date = date;
    }

    /**
     * Returns <code>String</code> representation of the deadline.
     *
     * @return <code>String</code> representation of the deadline.
     */
    @Override
    public String toString() {
        return String.format("[D]%s (DUE BY: %s)", super.toString(), date.format(OUTPUT_FORMATTER));
    }

    /**
     * Returns the string representation of the deadline task formatted for storage persistence.
     *
     * @return The formatted string suitable for saving to a file.
     */
    @Override
    public String toFileFormat() {
        return String.format("D | %s | %s", super.toFileFormat(), date.format(Parser.INPUT_FORMATTER));
    }
}