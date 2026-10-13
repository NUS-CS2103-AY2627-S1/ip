package duke;

/**
 * Represents a validated command that can be handled by any Wangsa interface.
 *
 * <p>The parser produces one of the nested command shapes below. This gives the
 * application layer typed input instead of making every interface split raw strings
 * and repeat validation logic.</p>
 */
public sealed interface Command permits Command.Simple, Command.TaskNumber, Command.Search,
        Command.AddTask, Command.AiQuestion {
    /**
     * Returns the action requested by this command.
     *
     * @return Command action.
     */
    Parser.CommandType type();

    /**
     * A command that has no additional arguments.
     *
     * @param type Command action.
     */
    record Simple(Parser.CommandType type) implements Command {
        /**
         * Ensures this shape is used only for argument-free commands.
         */
        public Simple {
            if (type != Parser.CommandType.BYE && type != Parser.CommandType.LIST
                    && type != Parser.CommandType.SORT && type != Parser.CommandType.HELP) {
                throw new IllegalArgumentException("Simple commands cannot carry arguments");
            }
        }
    }

    /**
     * A command that targets one task by its one-based display number.
     *
     * @param type Command action.
     * @param taskNumber One-based task number.
     */
    record TaskNumber(Parser.CommandType type, int taskNumber) implements Command {
        /**
         * Ensures this shape is used only for commands that target a task number.
         */
        public TaskNumber {
            if (type != Parser.CommandType.MARK && type != Parser.CommandType.UNMARK
                    && type != Parser.CommandType.DELETE) {
                throw new IllegalArgumentException("Task-number commands require a task action");
            }
        }
    }

    /**
     * A command that searches task descriptions.
     *
     * @param keyword Validated search keyword.
     */
    record Search(String keyword) implements Command {
        /**
         * Returns the search action represented by this command.
         */
        @Override
        public Parser.CommandType type() {
            return Parser.CommandType.FIND;
        }
    }

    /**
     * A read-only question about Wangsa's commands.
     *
     * @param question Validated question text.
     */
    record AiQuestion(String question) implements Command {
        /**
         * Returns the AI-help action represented by this command.
         */
        @Override
        public Parser.CommandType type() {
            return Parser.CommandType.AI;
        }
    }

    /**
     * A command that adds a fully constructed task.
     *
     * @param task Validated task to add.
     */
    record AddTask(Task task) implements Command {
        /**
         * Returns the add-task action represented by this command.
         */
        @Override
        public Parser.CommandType type() {
            return Parser.CommandType.ADD_TASK;
        }
    }
}
