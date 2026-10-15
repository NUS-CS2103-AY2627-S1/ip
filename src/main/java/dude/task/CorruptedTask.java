package dude.task;

/**
 * A placeholder for a task record that could not be read from storage.
 */
public class CorruptedTask extends Task {
    private static final String DESCRIPTION_PREFIX = "[Corrupted: ";
    private static final String DESCRIPTION_SUFFIX = "]";

    private final String rawContent;

    /**
     * Creates a visible placeholder for one malformed storage record.
     */
    public CorruptedTask() {
        this("record");
    }

    /**
     * Creates a visible placeholder containing the malformed source record.
     *
     * @param rawContent Original record that could not be parsed.
     */
    public CorruptedTask(String rawContent) {
        super(DESCRIPTION_PREFIX + rawContent + DESCRIPTION_SUFFIX);
        this.rawContent = rawContent;
    }

    /**
     * Returns the malformed source record.
     *
     * @return Original raw record.
     */
    public String getRawContent() {
        return rawContent;
    }

    /**
     * Returns the formatted corrupted-record representation.
     *
     * @return Corrupted-task marker followed by the raw record.
     */
    @Override
    public String toString() {
        return String.format("[C]%s", super.toString());
    }
}
