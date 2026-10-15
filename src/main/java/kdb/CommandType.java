package kdb;

import java.util.Locale;

/** Represents the commands understood by Kdb. */
public enum CommandType {
    BYE, LIST, MARK, UNMARK, DELETE, TODO, DEADLINE, EVENT, FIND, UNKNOWN;

    /**
     * Maps a raw command word to a command type.
     *
     * @param word command text entered by the user
     * @return the matching command, or {@code UNKNOWN} when there is no match
     */
    public static CommandType fromWord(String word) {
        if (word == null || word.isBlank()) {
            return UNKNOWN;
        }
        try {
            return CommandType.valueOf(word.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
