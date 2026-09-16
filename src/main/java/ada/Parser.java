package ada;

/**
 * Parses raw user‑input strings and converts them into structured {@code Parser.CommandInfo} objects.
 * Detects command type, extracts arguments and throws {@code AdaException} for format‑violating input.
 */
public class Parser {

    /**
     * Data‑holding inner class that stores parsed result of one user command.
     * Contains command name and corresponding extracted argument fields.
     */
    public static class CommandInfo {
        private String command;
        private int taskNum;
        private String description;
        private String by;
        private String from;
        private String to;
        private String keyword;

        /**
         * Creates a CommandInfo holding only the command name.
         * Other fields remain uninitialized and should be set manually if needed.
         *
         * @param command name of the recognized user command
         */
        public CommandInfo(String command) {
            assert command != null : "A parsed command must have a command name";
            this.command = command;
        }

        public String getCommand() {
            return this.command;
        }

        public int getTaskNum() {
            return this.taskNum;
        }

        public String getDescription() {
            return this.description;
        }

        public String getBy() {
            return this.by;
        }

        public String getFrom() {
            return this.from;
        }

        public String getTo() {
            return this.to;
        }

        public String getKeyword() {
            return this.keyword;
        }
    }

    /**
     * Parses one line of user input into a {@code CommandInfo} object.
     *
     * The method identifies the command and delegates detailed validation
     * to the corresponding helper method.
     *
     * @param input raw user input
     * @return parsed command information
     * @throws AdaException if the command format is invalid
     */
    public CommandInfo parse(String input) throws AdaException {
        assert input != null
                : "The parser must receive a command string";

        if (input.equals("bye")) {
            return new CommandInfo("bye");
        }

        String command = getCommandWord(input);

        switch (command) {
            case "list":
                return parseNoArgumentCommand(input, "list");
            case "sort":
                return parseNoArgumentCommand(input, "sort");
            case "mark":
                return parseTaskNumberCommand(input, "mark", 4);
            case "unmark":
                return parseTaskNumberCommand(input, "unmark", 6);
            case "todo":
                return parseTodo(input);
            case "deadline":
                return parseDeadline(input);
            case "event":
                return parseEvent(input);
            case "delete":
                return parseTaskNumberCommand(input, "delete", 6);
            case "find":
                return parseFind(input);
            default:
                return parseKnownCommandPrefix(input);
        }
    }

    /**
     * Handles inputs that begin with a known command but do not contain
     * the correct spacing or argument format.
     *
     * For example, inputs such as "mark1" or "todoSomething"
     * should produce a format error instead of being treated as unknown
     * commands.
     *
     * @param input raw user input
     * @return parsed command information
     * @throws AdaException if the input begins with a known command
     *                      but has an invalid format
     */
    private CommandInfo parseKnownCommandPrefix(
            String input) throws AdaException {
        if (input.startsWith("list")) {
            return parseNoArgumentCommand(input, "list");
        }

        if (input.startsWith("sort")) {
            return parseNoArgumentCommand(input, "sort");
        }

        if (input.startsWith("mark")) {
            return parseTaskNumberCommand(input, "mark", 4);
        }

        if (input.startsWith("unmark")) {
            return parseTaskNumberCommand(input, "unmark", 6);
        }

        if (input.startsWith("todo")) {
            return parseTodo(input);
        }

        if (input.startsWith("deadline")) {
            return parseDeadline(input);
        }

        if (input.startsWith("event")) {
            return parseEvent(input);
        }

        if (input.startsWith("delete")) {
            return parseTaskNumberCommand(input, "delete", 6);
        }

        if (input.startsWith("find")) {
            return parseFind(input);
        }

        return new CommandInfo("unknown");
    }

    /**
     * Extracts the first word from the input as the command name.
     *
     * @param input raw user input
     * @return first word of the input, or an empty string for blank input
     */
    private String getCommandWord(String input) {
        String trimmedInput = input.trim();

        if (trimmedInput.isEmpty()) {
            return "";
        }

        String[] parts = trimmedInput.split("\\s+", 2);
        return parts[0];
    }

    /**
     * Parses a command that does not accept arguments.
     *
     * @param input raw user input
     * @param command expected command name
     * @return command information
     * @throws AdaException if extra arguments are provided
     */
    private CommandInfo parseNoArgumentCommand(
            String input,
            String command) throws AdaException {
        String extra = input.substring(command.length()).trim();

        if (!extra.isEmpty()) {
            throw new AdaException(
                    "Invalid format. Please use: " + command);
        }

        return new CommandInfo(command);
    }

    /**
     * Parses a todo command.
     *
     * @param input raw todo command
     * @return parsed todo command information
     * @throws AdaException if the description is missing or malformed
     */
    private CommandInfo parseTodo(String input) throws AdaException {
        String description = input.substring(4).trim();

        if (description.isEmpty()) {
            throw new AdaException(
                    "Todo description can not be empty. "
                            + "Please input task content.");
        }

        if (input.charAt(4) != ' ') {
            throw new AdaException(
                    "Wrong format. Use exactly one space: todo <>");
        }

        CommandInfo commandInfo = new CommandInfo("todo");
        commandInfo.description = description;
        return commandInfo;
    }

    /**
     * Parses a deadline command.
     *
     * @param input raw deadline command
     * @return parsed deadline command information
     * @throws AdaException if the deadline format is invalid
     */
    private CommandInfo parseDeadline(String input)
            throws AdaException {
        String content = input.substring(8).trim();

        if (content.isEmpty()) {
            throw new AdaException(
                    "Deadline can not be empty. "
                            + "Please input task content.");
        }

        if (input.charAt(8) != ' ') {
            throw new AdaException(
                    "Wrong format. Use exactly one space: deadline <>");
        }

        int byIndex = content.indexOf(" /by");

        if (byIndex == -1) {
            throw new AdaException(
                    "Missing marker '/by'. "
                            + "Please follow format: "
                            + "deadline xxx /by xxx.");
        }

        String description = content.substring(0, byIndex);

        if (description.isEmpty()) {
            throw new AdaException(
                    "Deadline description can not be empty. "
                            + "Please input task description.");
        }

        String by = content.substring(byIndex + 4).trim();

        if (by.isEmpty()) {
            throw new AdaException(
                    "Deadline time can not be empty.");
        }

        CommandInfo commandInfo = new CommandInfo("deadline");
        commandInfo.description = description;
        commandInfo.by = by;
        return commandInfo;
    }

    /**
     * Parses an event command.
     *
     * @param input raw event command
     * @return parsed event command information
     * @throws AdaException if the event format is invalid
     */
    private CommandInfo parseEvent(String input)
            throws AdaException {
        String content = input.substring(5).trim();

        if (content.isEmpty()) {
            throw new AdaException(
                    "Event can not be empty. "
                            + "Please input task content.");
        }

        if (input.charAt(5) != ' ') {
            throw new AdaException(
                    "Wrong format. Use exactly one space: event <>");
        }

        int fromIndex = content.indexOf(" /from");
        int toIndex = content.indexOf(" /to");

        if (fromIndex == -1) {
            throw new AdaException(
                    "Missing marker '/from'. "
                            + "Format: event xxx /from xxx /to xxx");
        }

        if (toIndex == -1) {
            throw new AdaException(
                    "Missing marker '/to'. "
                            + "Format: event xxx /from xxx /to xxx");
        }

        if (fromIndex > toIndex) {
            throw new AdaException(
                    "Wrong format. Marker order should be: "
                            + "event xxx /from xxx /to xxx");
        }

        String description = content.substring(0, fromIndex);

        if (description.isEmpty()) {
            throw new AdaException(
                    "Event description can not be empty. "
                            + "Please input task description.");
        }

        String from = content
                .substring(fromIndex + 6, toIndex)
                .trim();

        if (from.isEmpty()) {
            throw new AdaException(
                    "Starting time can not be empty.");
        }

        String to = content.substring(toIndex + 4).trim();

        if (to.isEmpty()) {
            throw new AdaException(
                    "Ending time can not be empty.");
        }

        CommandInfo commandInfo = new CommandInfo("event");
        commandInfo.description = description;
        commandInfo.from = from;
        commandInfo.to = to;
        return commandInfo;
    }

    /**
     * Parses a find command.
     *
     * @param input raw find command
     * @return parsed find command information
     * @throws AdaException if the keyword is missing
     */
    private CommandInfo parseFind(String input)
            throws AdaException {
        String trimmedInput = input.trim();

        if (trimmedInput.length() == 4) {
            throw new AdaException(
                    "Please provide a keyword to find");
        }

        if (trimmedInput.charAt(4) != ' ') {
            throw new AdaException(
                    "Invalid format for find command. "
                            + "Usage: find KEYWORD");
        }

        String keyword = trimmedInput.substring(4).trim();

        if (keyword.isEmpty()) {
            throw new AdaException(
                    "Please provide a keyword to find");
        }

        CommandInfo commandInfo = new CommandInfo("find");
        commandInfo.keyword = keyword;
        return commandInfo;
    }

    /**
     * Parses commands that require a task number.
     *
     * @param input raw user input
     * @param command command name
     * @param commandLength length of the command name
     * @return parsed task-number command information
     * @throws AdaException if the task number is missing or invalid
     */
    private CommandInfo parseTaskNumberCommand(
            String input,
            String command,
            int commandLength) throws AdaException {
        String after = input.substring(commandLength).trim();
        if (after.isEmpty()) {
            throw new AdaException("Please provide a task number.");
        }
        if (input.charAt(commandLength) != ' ') {
            throw new AdaException(
                    "Wrong format. Use exactly one space: "
                            + command + " <>");
        }
        try {
            CommandInfo result = new CommandInfo(command);
            result.taskNum = Integer.parseInt(after);
            return result;
        } catch (NumberFormatException e) {
            throw new AdaException("Please input a valid integer number.");
        }
    }

}
