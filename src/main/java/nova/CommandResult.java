package nova;

/**
 * The outcome of one user command: the text to show the user, and what kind of
 * reply it is. The GUI uses the kind to pick the styling and the marker of a
 * reply, so an error looks different from a confirmation.
 *
 * @param text the reply text
 * @param kind whether the reply reports a problem, a change or plain information
 */
public record CommandResult(String text, Kind kind) {
    /** The kinds of reply Nova can give. */
    public enum Kind {
        INFO, SUCCESS, ERROR
    }

    /**
     * Creates a reply that only passes information on, such as a list.
     *
     * @param text the reply text
     * @return an informational result
     */
    public static CommandResult info(String text) {
        return new CommandResult(text, Kind.INFO);
    }

    /**
     * Creates a reply that confirms something changed, such as an added task.
     *
     * @param text the reply text
     * @return a successful result
     */
    public static CommandResult success(String text) {
        return new CommandResult(text, Kind.SUCCESS);
    }

    /**
     * Creates a reply that reports a problem the user should notice.
     *
     * @param text the reply text
     * @return a failed result
     */
    public static CommandResult error(String text) {
        return new CommandResult(text, Kind.ERROR);
    }

    /**
     * Returns whether this reply reports a problem.
     *
     * @return true for an error result
     */
    public boolean isError() {
        return this.kind == Kind.ERROR;
    }
}
