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
     * Parses one line of user input into a {@code CommandInfo}.
     * Validates input syntax and extracts relevant command arguments.
     *
     * @param input raw text line entered by the user
     * @return structured parsed command information
     * @throws AdaException when input format is invalid or required arguments are missing
     */
    public CommandInfo parse(String input) throws AdaException {
        assert input != null : "The parser must receive a command string";

        if (input.equals("bye")) {
            return new CommandInfo("bye");
        }

        if (input.startsWith("list")) {
            String extra = input.substring(4).trim();

            if (!extra.isEmpty()) {
                throw new AdaException(
                        "Invalid format. Please use: list");
            }

            return new CommandInfo("list");
        }

        if (input.startsWith("sort")) {
            String extra = input.substring(4).trim();

            if (!extra.isEmpty()) {
                throw new AdaException(
                        "Invalid format. Please use: sort");
            }

            return new CommandInfo("sort");
        }

        if (input.startsWith("mark")) {
            return parseTaskNumberCommand(input, "mark", 4);
        }

        if (input.startsWith("unmark")) {
            return parseTaskNumberCommand(input, "unmark", 6);
        }

        if (input.startsWith("todo")) {
            String desc = input.substring(4).trim();
            if (desc.isEmpty()) {
                throw new AdaException("Todo description can not be empty. Please input task content.");
            }
            if (input.charAt(4) != ' ') {
                throw new AdaException("Wrong format. Use exactly one space: todo <>");
            }
            CommandInfo ci = new CommandInfo("todo");
            ci.description = desc;
            return ci;
        }

        if (input.startsWith("deadline")) {
            String content = input.substring(8).trim();
            if (content.isEmpty()) {
                throw new AdaException("Deadline can not be empty. Please input task content.");
            }
            if (input.charAt(8) != ' ') {
                throw new AdaException("Wrong format. Use exactly one space: deadline <>");
            }
            int byIdx = content.indexOf(" /by");
            if (byIdx == -1) {
                throw new AdaException("Missing marker '/by'. Please follow format: deadline xxx /by xxx.");
            }
            String desc = content.substring(0, byIdx);
            if (desc.isEmpty()) {
                throw new AdaException("Deadline description can not be empty. Please input task description.");
            }
            String by = content.substring(byIdx + 4).trim();
            if (by.isEmpty()) {
                throw new AdaException("Deadline time can not be empty, enter 'not know' is also valid.");
            }
            CommandInfo ci = new CommandInfo("deadline");
            ci.description = desc;
            ci.by = by;
            return ci;
        }

        if (input.startsWith("event")) {
            String content = input.substring(5).trim();
            if (content.isEmpty()) {
                throw new AdaException("Event can not be empty. Please input task content.");
            }
            if (input.charAt(5) != ' ') {
                throw new AdaException("Wrong format. Use exactly one space: event <>");
            }
            int fromIdx = content.indexOf(" /from");
            int toIdx = content.indexOf(" /to");
            if (fromIdx == -1) {
                throw new AdaException("Missing marker '/from'. Format: event xxx /from xxx /to xxx");
            }
            if (toIdx == -1) {
                throw new AdaException("Missing marker '/to'. Format: event xxx /from xxx /to xxx");
            }
            if (fromIdx > toIdx) {
                throw new AdaException("Wrong format.Marker order should be: event xxx /from xxx /to xxx");
            }
            String desc = content.substring(0, fromIdx);
            if (desc.isEmpty()) {
                throw new AdaException("Event description can not be empty. Please input task description.");
            }
            String from = content.substring(fromIdx + 6, toIdx).trim();
            if (from.isEmpty()) {
                throw new AdaException("Starting time can not be empty, enter 'not know' is also valid.");
            }
            String to = content.substring(toIdx + 4).trim();
            if (to.isEmpty()) {
                throw new AdaException("Ending time can not be empty, enter 'not know' is also valid.");
            }
            CommandInfo ci = new CommandInfo("event");
            ci.description = desc;
            ci.from = from;
            ci.to = to;
            return ci;
        }

        if (input.startsWith("delete")) {
            return parseTaskNumberCommand(input, "delete", 6);
        }

        if (input.startsWith("find")) {
            String trimmedInput = input.trim();
            if (trimmedInput.length() == 4) {
                throw new AdaException("Please provide a keyword to find");
            }
            if (trimmedInput.charAt(4) != ' ') {
                throw new AdaException("Invalid format for find command. Usage: find KEYWORD");
            }
            String rest = trimmedInput.substring(4).trim();
            if (rest.isEmpty()) {
                throw new AdaException("Please provide a keyword to find");
            }
            CommandInfo ci = new CommandInfo("find");
            ci.keyword = rest;
            return ci;
        }
        return new CommandInfo("unknown");
    }

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
