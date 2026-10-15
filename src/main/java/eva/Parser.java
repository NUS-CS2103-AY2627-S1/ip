package eva;

/**
 * Parses user input into commands that can be executed by Eva.
 */
public class Parser {

    /**
     * Represents the supported command types.
     */
    public enum CommandType {
        BYE,
        LIST,
        MARK,
        UNMARK,
        DELETE,
        TODO,
        DEADLINE,
        EVENT,
        FIND,
        SORT,
        AI
    }

    /**
     * Parses the specified user input.
     *
     * @param input User command to parse.
     * @return Parsed representation of the command.
     * @throws EvaException If the command format is invalid or unknown.
     */
    public static ParsedCommand parse(String input) throws EvaException {
        if (input == null || input.isBlank()) {
            throw new EvaException("Please enter a command.");
        }

        input = input.strip().replaceAll("\\s+", " ");

        if (input.equals("bye")) {
            return new ParsedCommand(CommandType.BYE);
        }

        if (input.equals("list")) {
            return new ParsedCommand(CommandType.LIST);
        }

        if (input.equals("sort")) {
            return new ParsedCommand(CommandType.SORT);
        }

        if (input.equals("mark") || input.startsWith("mark ")) {
            return parseTaskNumber(input, "mark", CommandType.MARK);
        }

        if (input.equals("unmark") || input.startsWith("unmark ")) {
            return parseTaskNumber(input, "unmark", CommandType.UNMARK);
        }

        if (input.equals("delete") || input.startsWith("delete ")) {
            return parseTaskNumber(input, "delete", CommandType.DELETE);
        }

        if (input.equals("todo") || input.startsWith("todo ")) {
            return parseTodo(input);
        }

        if (input.equals("deadline") || input.startsWith("deadline ")) {
            return parseDeadline(input);
        }

        if (input.equals("event") || input.startsWith("event ")) {
            return parseEvent(input);
        }

        if (input.equals("find") || input.startsWith("find ")) {
            return parseFind(input);
        }

        if (input.equals("@ai") || input.startsWith("@ai ")) {
            return parseAi(input);
        }

        throw new EvaException(
                "I'm sorry, but I don't know what that means.");
    }

    private static ParsedCommand parseTaskNumber(
            String input, String commandWord, CommandType commandType)
            throws EvaException {
        if (!input.matches(commandWord + " \\d+")) {
            throw new EvaException(
                    "Please specify a valid task number to "
                            + commandWord + ".");
        }

        try {
            int taskNumber = Integer.parseInt(
                    input.substring(commandWord.length() + 1));
            return new ParsedCommand(commandType, taskNumber);
        } catch (NumberFormatException e) {
            throw new EvaException(
                    "Please specify a valid task number to "
                            + commandWord + ".");
        }
    }

    private static ParsedCommand parseTodo(String input)
            throws EvaException {
        if (!input.startsWith("todo ")
                || input.substring(4).trim().isEmpty()) {
            throw new EvaException(
                    "The description of a todo cannot be empty.");
        }

        String description = input.substring(5).trim();
        return new ParsedCommand(CommandType.TODO, description);
    }

    private static ParsedCommand parseDeadline(String input)
            throws EvaException {
        if (!input.startsWith("deadline ")
                || input.substring(8).trim().isEmpty()) {
            throw new EvaException(
                    "The description of a deadline cannot be empty.");
        }

        String content = input.substring(9).trim();
        int byIndex = content.indexOf(" /by ");

        if (byIndex == -1) {
            throw new EvaException("A deadline must contain /by.");
        }

        String description = content.substring(0, byIndex).trim();
        String by = content.substring(byIndex + 5).trim();

        if (description.isEmpty() || by.isEmpty()) {
            throw new EvaException(
                    "A deadline needs both a description and /by time.");
        }

        return new ParsedCommand(CommandType.DEADLINE, description, by);
    }

    private static ParsedCommand parseEvent(String input)
            throws EvaException {
        if (!input.startsWith("event ")
                || input.substring(5).trim().isEmpty()) {
            throw new EvaException(
                    "The description of an event cannot be empty.");
        }

        String content = input.substring(6).trim();
        int fromIndex = content.indexOf(" /from ");
        int toIndex = content.indexOf(" /to ");

        if (fromIndex == -1
                || toIndex == -1
                || toIndex <= fromIndex) {
            throw new EvaException(
                    "An event must contain /from and /to.");
        }

        String description = content.substring(0, fromIndex).trim();
        String from = content.substring(fromIndex + 7, toIndex).trim();
        String to = content.substring(toIndex + 5).trim();

        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new EvaException(
                    "An event needs a description, /from time, "
                            + "and /to time.");
        }

        return new ParsedCommand(
                CommandType.EVENT, description, from, to);
    }

    private static ParsedCommand parseFind(String input)
            throws EvaException {
        if (!input.startsWith("find ")
                || input.substring(4).trim().isEmpty()) {
            throw new EvaException(
                    "Please specify a keyword to find.");
        }

        String keyword = input.substring(5).trim();
        return new ParsedCommand(CommandType.FIND, keyword);
    }

    private static ParsedCommand parseAi(String input)
            throws EvaException {
        if (!input.startsWith("@ai ")
                || input.substring(3).trim().isEmpty()) {
            throw new EvaException(
                    "Please enter a question after @ai.");
        }

        String question = input.substring(4).trim();
        return new ParsedCommand(CommandType.AI, question);
    }

    /**
     * Stores the type and arguments of a parsed command.
     */
    public static class ParsedCommand {
        private final CommandType type;
        private final int taskNumber;
        private final String[] values;

        private ParsedCommand(CommandType type) {
            this(type, 0);
        }

        private ParsedCommand(CommandType type, int taskNumber) {
            this.type = type;
            this.taskNumber = taskNumber;
            this.values = new String[0];
        }

        private ParsedCommand(CommandType type, String... values) {
            this.type = type;
            this.taskNumber = 0;
            this.values = values;
        }

        /**
         * Returns the type of this command.
         *
         * @return Command type.
         */
        public CommandType getType() {
            return type;
        }

        /**
         * Returns the task number supplied with this command.
         *
         * @return Task number.
         */
        public int getTaskNumber() {
            return taskNumber;
        }

        /**
         * Returns a command argument at the specified position.
         *
         * @param index Position of the command argument.
         * @return Command argument at the specified position.
         */
        public String getValue(int index) {
            return values[index];
        }
    }
}
