package clover.parser;

/**
 * Holds task arguments after an optional tutoree association has been separated.
 */
public class TaskArguments {
    private final String taskArguments;
    private final String tutoreeName;

    /**
     * Creates parsed task arguments with an optional tutoree name.
     */
    public TaskArguments(String taskArguments, String tutoreeName) {
        this.taskArguments = taskArguments;
        this.tutoreeName = tutoreeName;
    }

    public String getTaskArguments() {
        return taskArguments;
    }

    /** Returns the tutoree name, or {@code null} when no {@code /for} marker was supplied. */
    public String getTutoreeName() {
        return tutoreeName;
    }
}
