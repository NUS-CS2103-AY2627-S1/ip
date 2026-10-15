package dukey.command;

/**
 * Represents the supported user commands.
 */
public enum CommandWord {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    DELETE("delete"),
    FIND("find"),
    ON("on"),
    STATS("stats"),
    UNKNOWN("");

    private final String word;

    CommandWord(String word) {
        this.word = word;
    }

    public String getWord() {
        return word;
    }
}
