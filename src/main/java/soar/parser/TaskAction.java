package soar.parser;

/**
 * Describes the flight-themed action used when a task-number command is incomplete.
 */
public enum TaskAction {
    COMPLETE_FLIGHT("has completed its flight"),
    RETURN_TO_FLIGHT_PATH("should return to the flight path"),
    SHOT_DOWN("should be shot down"),
    UPDATED("should be updated");

    /** Phrase inserted into the missing-task-number error message. */
    private final String description;

    /**
     * Creates a task action with its user-facing description.
     *
     * @param description Phrase describing the action.
     */
    TaskAction(String description) {
        this.description = description;
    }

    /**
     * Returns the phrase used to describe this action.
     *
     * @return user-facing action description
     */
    public String getDescription() {
        return description;
    }
}
