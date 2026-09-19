package gary.contact;

import java.util.Locale;

/**
 * Represents an operation supported by the contact manager.
 */
public enum ContactCommandType {
    /** Adds a contact. */
    ADD("add"),
    /** Lists all contacts. */
    LIST("list"),
    /** Finds contacts containing a keyword. */
    FIND("find"),
    /** Deletes a contact. */
    DELETE("delete"),
    /** Represents an unrecognized contact operation. */
    UNKNOWN("");

    private final String keyword;

    ContactCommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the contact operation represented by an input command.
     *
     * @param input Full contact command entered by the user.
     * @return Matching operation, or {@link #UNKNOWN} if none matches.
     */
    public static ContactCommandType from(String input) {
        assert input != null : "Contact input should not be null";

        String[] commandParts = input.strip().split("\\s+", 3);
        if (commandParts.length < 2) {
            return UNKNOWN;
        }

        String keyword = commandParts[1].toLowerCase(Locale.ENGLISH);
        for (ContactCommandType commandType : values()) {
            if (commandType.keyword.equals(keyword)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }
}
