package bob;

import java.util.Locale;

/**
 * Represents the semantic type of a chatbot response for presentation purposes.
 */
public enum ResponseType {
    /** Confirms that a task was added. */
    ADD,
    /** Ends the current chatbot session. */
    BYE,
    /** Confirms that a task was deleted. */
    DELETE,
    /** Reports a recoverable command-processing error. */
    ERROR,
    /** Displays tasks matching a search keyword. */
    FIND,
    /** Presents general information that is not produced by a command. */
    INFO,
    /** Displays the complete task list. */
    LIST,
    /** Confirms that a task was marked as done. */
    MARK,
    /** Confirms that a task was marked as not done. */
    UNMARK;

    /**
     * Returns the CSS class used to style this response type.
     *
     * @return response-specific CSS class.
     */
    public String getStyleClass() {
        return "response-" + name().toLowerCase(Locale.ROOT);
    }
}
