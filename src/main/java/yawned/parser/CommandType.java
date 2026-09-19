package yawned.parser;

import java.util.List;

/**
 * Represents a command word accepted by Yawned.
 */
public enum CommandType {
    TODO("todo", true, "t"),
    DEADLINE("deadline", true, "d"),
    EVENT("event", true, "e"),
    LIST("list", false, "l"),
    MARK("mark", true, "m"),
    UNMARK("unmark", true, "u"),
    DELETE("delete", true, "del"),
    FIND("find", true, "f"),
    ALIAS("alias", true),
    UNKNOWN("", false);

    private final String word;
    private final boolean acceptsArguments;
    private final String[] aliases;

    CommandType(String word, boolean acceptsArguments, String... aliases) {
        this.word = word;
        this.acceptsArguments = acceptsArguments;
        this.aliases = aliases;
    }

    /**
     * Returns the command word represented by this type.
     *
     * @return Command word.
     */
    public String getWord() {
        return word;
    }

    /**
     * Returns whether this command accepts arguments.
     *
     * @return Whether arguments are valid for this command.
     */
    public boolean acceptsArguments() {
        return acceptsArguments;
    }

    /**
     * Returns this command's built-in shortcuts.
     *
     * @return Built-in shortcuts in their display order.
     */
    public List<String> getAliases() {
        return List.of(aliases);
    }

    /**
     * Identifies the command type represented by the user's full input.
     *
     * @param input Full user input.
     * @return Matching command type, or {@link #UNKNOWN}.
     */
    public static CommandType fromInput(String input) {
        for (CommandType commandType : values()) {
            if (commandType.matchesInput(input)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns the task command represented by a canonical alias target word.
     *
     * @param word Canonical command word.
     * @return Matching task command, or {@link #UNKNOWN} if the word is not a valid target.
     */
    public static CommandType fromAliasTarget(String word) {
        return switch (word) {
            case "todo" -> TODO;
            case "deadline" -> DEADLINE;
            case "event" -> EVENT;
            case "list" -> LIST;
            case "mark" -> MARK;
            case "unmark" -> UNMARK;
            case "delete" -> DELETE;
            case "find" -> FIND;
            default -> UNKNOWN;
        };
    }

    /**
     * Returns whether the supplied name cannot be used as a custom alias.
     *
     * @param name Alias name to check.
     * @return Whether the name is reserved by the command syntax.
     */
    public static boolean isReservedAliasName(String name) {
        if (name.equalsIgnoreCase(ALIAS.word) || name.equalsIgnoreCase("remove")) {
            return true;
        }
        for (CommandType commandType : values()) {
            if (name.equalsIgnoreCase(commandType.word) || commandType.isAlias(name)) {
                return true;
            }
        }
        return false;
    }

    /** Returns whether the input begins with this command's canonical word or alias. */
    private boolean matchesInput(String input) {
        if (this == UNKNOWN) {
            return false;
        }
        int firstSpaceIndex = input.indexOf(' ');
        boolean hasArguments = firstSpaceIndex >= 0;
        if (hasArguments && !acceptsArguments) {
            return false;
        }
        String typedWord = hasArguments ? input.substring(0, firstSpaceIndex) : input;
        return typedWord.equals(word) || isAlias(typedWord);
    }

    /** Returns whether the supplied word is one of this command's aliases. */
    private boolean isAlias(String typedWord) {
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(typedWord)) {
                return true;
            }
        }
        return false;
    }
}
