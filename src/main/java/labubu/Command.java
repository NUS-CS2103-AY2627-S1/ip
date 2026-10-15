package labubu;

/**
 * Represents the command flows supported by the parser.
 */
enum Command {
    EXIT(true, "bye", "exit", "quit"),
    TASK_UPDATE(false, "mark", "unmark", "delete"),
    FIND(false, "find"),
    LIST(true, "list"),
    TODO(false, "todo"),
    DEADLINE(false, "deadline"),
    EVENT(false, "event"),
    HELP(true, "help"),
    UNKNOWN(false);

    private final boolean isExactInput;
    private final String[] keywords;

    Command(boolean exactInput, String... keywords) {
        this.isExactInput = exactInput;
        this.keywords = keywords;
    }

    /**
     * Identifies the command flow for the given input.
     *
     * @param userInput Complete trimmed user input.
     * @param tokens User input split into whitespace-separated tokens.
     * @return Command flow represented by the input.
     */
    static Command identify(String userInput, String[] tokens) {
        for (Command command : values()) {
            if (command != UNKNOWN && command.matches(userInput, tokens)) {
                return command;
            }
        }

        return UNKNOWN;
    }

    private boolean matches(String userInput, String[] tokens) {
        String input = isExactInput ? userInput : tokens[0];
        for (String keyword : keywords) {
            if (input.equalsIgnoreCase(keyword)) {
                return true;
            }
        }
        return false;
    }
}
