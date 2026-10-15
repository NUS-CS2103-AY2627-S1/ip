package charliek.command;

import java.util.Optional;

/**
 * Describes the command keywords recognized by the parser.
 */
public enum CommandType {
    /**
     * Exits the application.
     */
    BYE("bye", false, "bye", "Exit CharlieK.", "bye"),

    /**
     * Displays all tasks, optionally sorted by time.
     */
    LIST("list", true, "list [time]",
            "Display tasks, optionally in chronological order.", "list time"),

    /**
     * Marks a task as done.
     */
    MARK("mark", true, "mark <number>", "Mark a task as complete.", "mark 1"),

    /**
     * Marks a task as not done.
     */
    UNMARK("unmark", true, "unmark <number>", "Mark a task as incomplete.", "unmark 1"),

    /**
     * Deletes a task.
     */
    DELETE("delete", true, "delete <number>", "Delete a task.", "delete 1"),

    /**
     * Adds a to-do task.
     */
    TODO("todo", true, "todo <description>", "Add an undated to-do task.", "todo buy milk"),

    /**
     * Adds a deadline task.
     */
    DEADLINE("deadline", true, "deadline <description> /by <date/time>",
            "Add a task with a deadline.", "deadline submit report /by 2/12/2019"),

    /**
     * Adds an event task.
     */
    EVENT("event", true, "event <description> /from <date/time> /to <date/time>",
            "Add an event task.",
            "event project meeting /from 2026-08-06 2pm /to 2026-08-06 4pm"),

    /**
     * Finds tasks matching a keyword.
     */
    FIND("find", true, "find <keyword>", "Find tasks containing a keyword.", "find book"),

    /**
     * Shows all commands or detailed usage for one command.
     */
    HELP("help", true, "help [command]", "Show all commands or detailed usage for one command.", "help list");

    /**
     * The text the user types for this command.
     */
    private final String keyword;

    /**
     * Whether this command may be followed by an argument.
     */
    private final boolean canAcceptArguments;

    /**
     * The usage syntax shown to users.
     */
    private final String usage;

    /**
     * The brief explanation shown to users.
     */
    private final String description;

    /**
     * An example input for this command.
     */
    private final String example;

    /**
     * Creates a command definition.
     *
     * @param keyword the command keyword.
     * @param canAcceptArguments whether the command accepts an argument.
     * @param usage the usage syntax shown to users.
     * @param description the brief explanation shown to users.
     * @param example an example input for this command.
     */
    CommandType(String keyword, boolean canAcceptArguments, String usage, String description, String example) {
        this.keyword = keyword;
        this.canAcceptArguments = canAcceptArguments;
        this.usage = usage;
        this.description = description;
        this.example = example;
    }

    /**
     * Finds the command represented by a complete input line.
     *
     * @param input the input line entered by the user.
     * @return the matching command, or an empty result for an unknown command.
     */
    public static Optional<CommandType> getCommandFromInput(String input) {
        if (input == null || !hasValidSpacing(input)) {
            return Optional.empty();
        }
        for (CommandType command : values()) {
            boolean isExactMatch = input.equals(command.keyword);
            boolean isArgumentMatch = command.canAcceptArguments
                    && input.startsWith(command.keyword + " ");
            if (isExactMatch || isArgumentMatch) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    /**
     * Checks the whitespace that separates a command from its arguments.
     */
    private static boolean hasValidSpacing(String input) {
        return !input.isEmpty()
                && input.equals(input.trim())
                && input.chars().noneMatch(character -> (Character.isWhitespace(character)
                        || Character.isSpaceChar(character)) && character != ' ')
                && input.chars().noneMatch(character -> character < 32 || character == 127)
                && !input.contains("  ");
    }

    /**
     * Finds the command represented by one command keyword.
     *
     * @param keyword the command keyword to find.
     * @return the matching command, or an empty result when no command matches.
     */
    public static Optional<CommandType> getCommandFromKeyword(String keyword) {
        if (keyword == null) {
            return Optional.empty();
        }
        for (CommandType command : values()) {
            if (command.keyword.equals(keyword)) {
                return Optional.of(command);
            }
        }
        return Optional.empty();
    }

    /**
     * Returns the command keyword users type.
     *
     * @return the command keyword.
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns the usage syntax for this command.
     *
     * @return the usage syntax.
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Returns the brief description for this command.
     *
     * @return the command description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns an example input for this command.
     *
     * @return an example input.
     */
    public String getExample() {
        return example;
    }

    /**
     * Extracts the text after this command's keyword.
     *
     * @param input the complete input line.
     * @return the text after the keyword and its separating space.
     */
    public String getArgumentFromInput(String input) {
        if (input == null) {
            return "";
        }
        int argumentStart = keyword.length();
        if (input.length() > argumentStart && input.charAt(argumentStart) == ' ') {
            argumentStart++;
        }
        return input.substring(argumentStart);
    }
}

