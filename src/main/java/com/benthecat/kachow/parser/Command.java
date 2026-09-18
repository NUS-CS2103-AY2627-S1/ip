package com.benthecat.kachow.parser;

import java.util.Arrays;

import com.benthecat.kachow.exception.KachowException;

/**
 * Identifies every command accepted by Kachow and associates it with its user-facing keyword.
 */
public enum Command {
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    LIST("list"),
    FIND("find"),
    ON("on"),
    MARK("mark"),
    UNMARK("unmark"),
    EDIT("edit"),
    DELETE("delete"),
    BYE("bye");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the user-facing keyword for this command.
     *
     * @return Command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Converts a user-entered keyword into its corresponding command.
     *
     * @param keyword Command keyword entered by the user.
     * @return Matching command.
     * @throws KachowException If the keyword does not identify a supported command.
     */
    public static Command fromKeyword(String keyword) throws KachowException {
        return Arrays.stream(values())
                .filter(command -> command.keyword.equals(keyword))
                .findFirst()
                .orElseThrow(() -> new KachowException(
                        "That command took a wrong turn. Try " + getSupportedKeywordsText() + "."));
    }

    /**
     * Formats all supported command keywords as a readable list in enum declaration order.
     *
     * @return Supported keywords separated by commas and {@code or} before the final keyword.
     */
    private static String getSupportedKeywordsText() {
        Command[] commands = values();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < commands.length; i++) {
            if (i > 0) {
                text.append(i == commands.length - 1 ? ", or " : ", ");
            }
            text.append(commands[i].keyword);
        }
        return text.toString();
    }
}
