package luckynoslacky.luckyparser;

import java.util.Arrays;
import java.util.Optional;

import luckynoslacky.luckycommand.LuckyNoByeCommand;
import luckynoslacky.luckycommand.LuckyNoCommand;
import luckynoslacky.luckycommand.LuckyNoDeleteCommand;
import luckynoslacky.luckycommand.LuckyNoListCommand;
import luckynoslacky.luckycommand.LuckyNoMarkCommand;
import luckynoslacky.luckycommand.LuckyNoTaskCommand;
import luckynoslacky.luckyexception.LuckyNoInputException;
import luckynoslacky.luckytask.TaskMaster;

/**
 * Parses user input into commands and reports invalid input consistently.
 *
 * <p>The parser coordinates command-specific collaborators while keeping the
 * public parsing API used by the chatbot unchanged.</p>
 */
public class LuckyNoParser {
    private final TaskMaster taskMaster;
    private final TaskCommandParser taskCommandParser;
    private final ScheduledCommandParser scheduledCommandParser;

    /** Creates a parser using the current system clock and a default task master. */
    public LuckyNoParser() {
        this(new DateTimeParser(), new TaskMaster());
    }

    /**
     * Creates a parser using the supplied date/time parser.
     *
     * @param dateTimeParser parser used to resolve date/time arguments
     */
    public LuckyNoParser(DateTimeParser dateTimeParser) {
        this(dateTimeParser, new TaskMaster());
    }

    /**
     * Creates a parser using the system clock and supplied task master.
     *
     * @param taskMaster task master attached to parsed commands
     */
    public LuckyNoParser(TaskMaster taskMaster) {
        this(new DateTimeParser(), taskMaster);
    }

    /**
     * Creates a parser with explicit date/time and task-list dependencies.
     *
     * @param dateTimeParser parser used to resolve date/time arguments
     * @param taskMaster task master attached to parsed commands
     */
    public LuckyNoParser(DateTimeParser dateTimeParser, TaskMaster taskMaster) {
        if (dateTimeParser == null) {
            throw new IllegalArgumentException("Date-time parser cannot be null.");
        }
        if (taskMaster == null) {
            throw new IllegalArgumentException("Task master cannot be null.");
        }
        DateTimePrefixParser dateTimePrefixParser = new DateTimePrefixParser(dateTimeParser);
        this.taskMaster = taskMaster;
        this.taskCommandParser = new TaskCommandParser(
                dateTimeParser, dateTimePrefixParser);
        this.scheduledCommandParser = new ScheduledCommandParser(
                dateTimeParser, dateTimePrefixParser);
    }

    /**
     * Represents a command name that can be entered by the user.
     */
    public enum CommandName {
        /** Exit command. */
        BYE("bye"),
        /** List command. */
        LIST("list"),
        /** ToDo creation command. */
        TODO("todo"),
        /** Deadline creation command. */
        DEADLINE("deadline"),
        /** Event creation command. */
        EVENT("event"),
        /** Mark-done command. */
        MARK("mark"),
        /** Mark-undone command. */
        UNMARK("unmark"),
        /** Task-deletion command. */
        DELETE("delete"),
        /** Date-search command. */
        FIND("find"),
        /** Task-snoozing command. */
        SNOOZE("snooze"),
        /** Task-rescheduling command. */
        RESCHED("resched");

        private final String inputName;

        CommandName(String inputName) {
            this.inputName = inputName;
        }

        /**
         * Returns the command spelling accepted from the user.
         *
         * @return user-facing command name
         */
        public String getInputName() {
            return inputName;
        }

        /**
         * Finds the command represented by a user-provided token.
         *
         * @param commandToken command token
         * @return matching command, or an empty Optional if there is no match
         */
        public static Optional<CommandName> fromCommandToken(String commandToken) {
            return Arrays.stream(values())
                    .filter(commandName -> commandName.inputName.equalsIgnoreCase(commandToken))
                    .findFirst();
        }
    }

    /**
     * Parses one line of chatbot input.
     *
     * @param userInput raw user input
     * @param taskCount number of tasks currently stored
     * @return parsed command
     * @throws LuckyNoInputException if the input is invalid
     */
    public LuckyNoCommand parseCommand(String userInput, int taskCount)
            throws LuckyNoInputException {
        return parseCommand(userInput, taskCount, taskMaster);
    }

    /**
     * Parses input using the task master's current task count.
     *
     * @param userInput raw user input
     * @return parsed command
     * @throws LuckyNoInputException if the input is invalid
     */
    public LuckyNoCommand parseCommand(String userInput)
            throws LuckyNoInputException {
        return parseCommand(userInput, taskMaster.getTaskCount(), taskMaster);
    }

    /**
     * Parses a command with an explicit task count and command dependency.
     *
     * @param userInput raw user input
     * @param taskCount current number of tasks
     * @param commandTaskMaster task master attached to the command
     * @return executable command
     * @throws LuckyNoInputException if the command arguments are invalid
     */
    private LuckyNoCommand parseCommand(
            String userInput,
            int taskCount,
            TaskMaster commandTaskMaster)
            throws LuckyNoInputException {
        CommandArgumentParser.ParsedInput parsedInput =
                CommandArgumentParser.parseInput(userInput);
        CommandName commandName = CommandArgumentParser.parseCommandName(
                parsedInput.commandToken());
        return createCommand(
                commandName,
                parsedInput.arguments(),
                taskCount,
                commandTaskMaster);
    }

    /**
     * Builds the executable command represented by parsed input.
     *
     * @param commandName recognized command name
     * @param commandArguments command arguments
     * @param taskCount current number of tasks
     * @param commandTaskMaster task master attached to the command
     * @return executable command
     * @throws LuckyNoInputException if the command arguments are invalid
     */
    private LuckyNoCommand createCommand(
            CommandName commandName,
            String commandArguments,
            int taskCount,
            TaskMaster commandTaskMaster)
            throws LuckyNoInputException {
        switch (commandName) {
            case BYE:
                CommandArgumentParser.checkNoArguments(
                        commandArguments, CommandName.BYE.getInputName());
                return new LuckyNoByeCommand();
            case LIST:
                CommandArgumentParser.checkNoArguments(
                        commandArguments, CommandName.LIST.getInputName());
                return new LuckyNoListCommand(commandTaskMaster);
            case TODO:
                return new LuckyNoTaskCommand(
                        taskCommandParser.parseTodo(commandArguments), commandTaskMaster);
            case DEADLINE:
                return new LuckyNoTaskCommand(
                        taskCommandParser.parseDeadline(commandArguments), commandTaskMaster);
            case EVENT:
                return new LuckyNoTaskCommand(
                        taskCommandParser.parseEvent(commandArguments), commandTaskMaster);
            case MARK:
                return new LuckyNoMarkCommand(
                        CommandArgumentParser.parseTaskNumber(commandArguments, taskCount),
                        true,
                        commandTaskMaster);
            case UNMARK:
                return new LuckyNoMarkCommand(
                        CommandArgumentParser.parseTaskNumber(commandArguments, taskCount),
                        false,
                        commandTaskMaster);
            case DELETE:
                return new LuckyNoDeleteCommand(
                        CommandArgumentParser.parseTaskNumber(commandArguments, taskCount),
                        commandTaskMaster);
            case FIND:
                return taskCommandParser.parseFind(commandArguments, commandTaskMaster);
            case SNOOZE:
                return scheduledCommandParser.parseSnooze(
                        commandArguments, taskCount, commandTaskMaster);
            case RESCHED:
                return scheduledCommandParser.parseResched(
                        commandArguments, taskCount, commandTaskMaster);
            default:
                assert false : "Unhandled command name: " + commandName;
                throw new IllegalStateException("Unhandled command name.");
        }
    }
}
