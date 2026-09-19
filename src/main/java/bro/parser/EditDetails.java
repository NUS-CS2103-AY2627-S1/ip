package bro.parser;

/**
 * Encapsulates the parsed arguments for an edit command.
 *
 * @param index       The 0-based index of the task to edit.
 * @param description The new description, or null if unchanged.
 * @param by          The new deadline string, or null if unchanged.
 * @param from        The new event start string, or null if unchanged.
 * @param to          The new event end string, or null if unchanged.
 */
public record EditDetails(
        int index,
        String description,
        String by,
        String from,
        String to
) {
    /**
     * Checks if a new description was specified.
     *
     * @return True if description was specified, false otherwise.
     */
    public boolean hasDescription() {
        return description != null;
    }

    /**
     * Checks if a new deadline was specified.
     *
     * @return True if deadline was specified, false otherwise.
     */
    public boolean hasBy() {
        return by != null;
    }

    /**
     * Checks if a new start time was specified.
     *
     * @return True if start was specified, false otherwise.
     */
    public boolean hasFrom() {
        return from != null;
    }

    /**
     * Checks if a new end time was specified.
     *
     * @return True if to was specified, false otherwise.
     */
    public boolean hasTo() {
        return to != null;
    }
}

