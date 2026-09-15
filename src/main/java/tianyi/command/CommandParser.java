package tianyi.command;

import java.time.format.DateTimeParseException;

import tianyi.TianyiException;
import tianyi.task.TaskList;
import tianyi.task.TaskTime;

/**
 * Parses user input into commands with appropriately typed arguments.
 */
public class CommandParser {
    private static final int INPUT_PART_INDEX_COMMAND = 0;
    private static final int INPUT_PART_INDEX_ARGUMENT = 1;
    private static final int INPUT_PART_COUNT_EXPECTED = 2;

    private static final int TASK_NUMBER_FIRST = 1;

    private final TaskParser taskParser;

    /**
     * Creates a parser for supported Tianyi commands.
     */
    public CommandParser() {
        taskParser = new TaskParser();
    }

    /**
     * Parses one line of user input into an executable command.
     *
     * @param input Complete line entered by the user.
     * @param tasks Current task list, used to validate task indices.
     * @return Command represented by the input.
     * @throws TianyiException If the command or any argument is invalid.
     */
    public Command parse(String input, TaskList tasks)
            throws TianyiException {
        String[] inputParts = input.trim().split("\\s+", INPUT_PART_COUNT_EXPECTED);

        CommandType type = CommandType.from(inputParts[INPUT_PART_INDEX_COMMAND]);
        String argument = inputParts.length == INPUT_PART_COUNT_EXPECTED
                ? inputParts[INPUT_PART_INDEX_ARGUMENT].trim()
                : "";

        return switch (type) {
            case TODO, DEADLINE, EVENT -> new AddCommand(taskParser.parse(type, argument));
            case MARK -> new MarkCommand(parseIndex(type, argument, tasks));
            case UNMARK -> new UnmarkCommand(parseIndex(type, argument, tasks));
            case DELETE -> new DeleteCommand(parseIndex(type, argument, tasks));
            case LIST -> new ListCommand(parseListDate(type, argument));
            case FIND -> new FindCommand(parseFindKeyword(type, argument));
            case HELP -> {
                validateNoArgument(type, argument);
                yield new HelpCommand();
            }
            case BYE -> {
                validateNoArgument(type, argument);
                yield new ExitCommand();
            }
        };
    }

    /**
     * Converts a user-facing task number into a validated zero-based index.
     *
     * @param type Command type that supplies the example usage.
     * @param argument Task number supplied by the user.
     * @param tasks Current task list, used to validate the task number.
     * @return Validated zero-based task index.
     * @throws TianyiException If the task number is missing or invalid.
     */
    private int parseIndex(CommandType type, String argument, TaskList tasks)
            throws TianyiException {
        if (argument.isBlank()) {
            throw new TianyiException("Please specify a task number.\n"
                    + "Try: " + type.getExample());
        }

        if (tasks.isEmpty()) {
            throw new TianyiException("There is no task in your list.\n"
                    + "Please add a task.\n"
                    + "Try: " + CommandType.TODO.getExample());
        }

        int taskNumber;

        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException e) {
            throw new TianyiException(argument + " is not a valid task number.\n"
                    + "Try: " + type.getExample());
        }

        if (taskNumber < TASK_NUMBER_FIRST || taskNumber > tasks.size()) {
            throw new TianyiException(
                    "Task number " + taskNumber + " does not exist.\n"
                            + "Please enter a number from " + TASK_NUMBER_FIRST + " to " + tasks.size() + ".\n"
                            + "Try: " + type.getExample());
        }

        int index = taskNumber - TASK_NUMBER_FIRST;

        assert index >= 0 && index < tasks.size()
                : "Parsed task index should be within the task list";

        return index;
    }

    /**
     * Parses the optional date accepted by the list command.
     *
     * @param type Command type that supplies error context and example usage.
     * @param argument Optional date supplied by the user.
     * @return Parsed date, or {@code null} when no date is supplied.
     * @throws TianyiException If the date is invalid or includes a time.
     */
    private TaskTime parseListDate(CommandType type, String argument)
            throws TianyiException {
        if (argument.isBlank()) {
            return null;
        }

        try {
            TaskTime time = new TaskTime(argument);

            if (time.hasTime()) {
                throw new TianyiException("Invalid " + type + " date.\n"
                        + "Please use d-M-yyyy.\n"
                        + "Try: " + type.getExample());
            }

            return time;
        } catch (DateTimeParseException e) {
            throw new TianyiException("Invalid " + type + " date.\n"
                    + "Please use d-M-yyyy.\n"
                    + "Try: " + type.getExample());
        }
    }

    /**
     * Validates and returns the keyword supplied to a find command.
     *
     * @param type Command type that supplies error context and example usage.
     * @param argument Keyword supplied by the user.
     * @return Validated keyword.
     * @throws TianyiException If the keyword is blank.
     */
    private String parseFindKeyword(CommandType type, String argument)
            throws TianyiException {
        if (argument.isBlank()) {
            throw new TianyiException(
                    "The keyword of " + type + " cannot be empty.\n"
                            + "Try: " + type.getExample()
            );
        }

        return argument;
    }

    /**
     * Rejects unexpected arguments for commands that accept none.
     *
     * @param type Command type that supplies error context and example usage.
     * @param argument Unexpected argument supplied by the user.
     * @throws TianyiException If an argument is present.
     */
    private void validateNoArgument(CommandType type, String argument)
            throws TianyiException {
        if (!argument.isBlank()) {
            throw new TianyiException(type + " does not accept any arguments.\n"
                    + "Try: " + type.getExample());
        }
    }
}
