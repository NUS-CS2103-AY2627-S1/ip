package plana.command;

import java.util.Locale;

/**
 * Represents a command that Plana can process.
 */
public enum CommandType {
    /** Exits the application. */
    BYE("bye"),

    /** Displays the help message. */
    HELP("help"),

    /** Displays all tasks. */
    LIST("list"),

    /** Displays deadlines and events occurring on a date. */
    ON("on"),

    /** Finds tasks whose descriptions contain a keyword. */
    FIND("find"),

    /** Deletes a task. */
    DELETE("delete"),

    /** Marks a task as completed. */
    MARK("mark"),

    /** Marks a task as not completed. */
    UNMARK("unmark"),

    /** Manages client records. */
    CLIENT("client"),

    /** Adds a deadline task. */
    DEADLINE("deadline"),

    /** Adds an event task. */
    EVENT("event"),

    /** Adds a ToDo task. */
    TODO("todo"),

    /** Represents input that does not match a known command. */
    UNKNOWN("");

    private final String commandText;

    /**
     * Creates a command type with its command-line representation.
     *
     * @param commandText the command text used for this command.
     */
    CommandType(String commandText) {
        this.commandText = commandText;
    }

    /**
     * Converts raw user input into a command type.
     *
     * @param input the complete command entered by the user.
     * @return the matching command type, or {@link #UNKNOWN} if none matches
     */
    public static CommandType parseInput(String input) {
        if (input == null) {
            return UNKNOWN;
        }

        String trimmedInput = input.trim();
        if (trimmedInput.equals("?")) {
            return HELP;
        }
        if (trimmedInput.equalsIgnoreCase("please help me")) {
            return HELP;
        }
        String[] commandAndArguments = trimmedInput.split("\\s+", 2);
        if (commandAndArguments.length == 0 || commandAndArguments[0].isEmpty()) {
            return UNKNOWN;
        }
        String commandText = commandAndArguments[0].toLowerCase(Locale.ROOT);
        for (CommandType commandType : values()) {
            if (commandType == UNKNOWN) {
                continue;
            }
            if (commandText.equals(commandType.commandText)) {
                return commandType;
            }
        }
        return UNKNOWN;
    }

    /**
     * Returns the command text used in user-facing guidance.
     *
     * @return the command text
     */
    public String getCommandText() {
        return commandText;
    }
}
