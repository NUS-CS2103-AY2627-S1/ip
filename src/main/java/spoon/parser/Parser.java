package spoon.parser;

import java.time.LocalDate;

import spoon.command.Command;
import spoon.exception.*;
import spoon.task.Deadline;
import spoon.task.Event;
import spoon.task.Task;
import spoon.task.TaskList;
import spoon.task.ToDo;
import spoon.util.DateFormat;

/**
 * Handles the input parsing of Spoon.
 */
public class Parser {
    // Methods
    /**
     * Parses the inputArray from user input.
     *
     * @param input user input.
     * @return inputArray parsed from user input.
     * @throws InvalidArgumentException if the special character '|' is used
     *              (delimiter used in file storage).
     */
    public static String[] parseInput(String input) throws InvalidArgumentException {
        // User input should at worst be an empty string, never null
        assert input != null : "String 'input' is null";
        if (input.contains("|")) {
            throw new InvalidArgumentException("Task descriptions cannot contain the '|' character.");
        }
        return input.trim().split("\\s+", 2);
    }

    /**
     * Parses the command from user input.
     *
     * @param input user input.
     * @return command parsed from user input.
     */
    public static Command parseCommand(String input) throws InvalidArgumentException {
        return Command.fromString(parseInput(input)[0]);
    }

    /**
     * Checks for exceptions in MARK, UNMARK and DELETE commands.
     *
     * @param input user input.
     * @return index of task with respect to the list of tasks.
     * @throws MissingArgumentException if argument is missing.
     * @throws InvalidFormatException if argument is not a number.
     * @throws IndexOutOfRangeException if argument is not within bounds.
     */
    public static int checkEdit(String input, TaskList tasks) throws SpoonException {
        String[] inputArray = parseInput(input);
        Command command = Command.fromString(inputArray[0]);

        if (inputArray.length < 2 || inputArray[1].isBlank()) {
            throw new MissingArgumentException(command.toString().toLowerCase(), "index");
        }
        int index;

        // Check that argument is a number
        try {
            index = Integer.parseInt(inputArray[1]);
        } catch (NumberFormatException e) {
            throw new InvalidFormatException("number");
        }

        // Check that argument is within bounds
        if (index < 1 || index > tasks.size()) {
            throw new IndexOutOfRangeException(1, tasks.size());
        }

        return index - 1;
    }

    /**
     * Checks for exceptions in ON and BY commands.
     *
     * @param input user input.
     * @return date for task filtering.
     * @throws MissingArgumentException if argument for task filtering is missing.
     * @throws InvalidArgumentException if the special character '|' is used
     *              (delimiter used in file storage).
     */
    public static LocalDate checkDate(String input) throws SpoonException {
        String[] inputArray = parseInput(input);
        Command command = Command.fromString(inputArray[0]);

        if (inputArray.length < 2 || inputArray[1].isBlank()) {
            throw new MissingArgumentException(command.toString().toLowerCase(), "date");
        }

        return DateFormat.parse(inputArray[1]).dateTime().toLocalDate();
    }

    /**
     * Checks for exceptions in the FIND command.
     *
     * @param input user input.
     * @return word to search for.
     * @throws MissingArgumentException if the keyword argument is missing.
     */
    public static String[] checkFind(String input) throws SpoonException {
        String[] inputArray = parseInput(input);
        if (inputArray.length < 2 || inputArray[1].isBlank()) {
            throw new MissingArgumentException("find", "keyword(s)");
        }
        String[] words = inputArray[1].trim().split("\\s*,\\s*");
        // Words should at worst be an array with a empty string as an element, never of length 0
        assert words.length > 0 : "Array 'words' does not have at least one valid search token";
        return words;
    }

    /**
     * Checks for exceptions in adding todos, deadlines and events.
     *
     * @param input user input.
     * @return task initialized with command.
     * @throws MissingArgumentException if argument(s) for task initialization are missing.
     * @throws InvalidFormatException if argument(s) for task initialization are in the wrong datetime format.
     * @throws InvalidArgumentException if arguments for task initialization are invalid
     *     (i.e. end date before start date).
     * @throws FatalErrorException as a placeholder (should never happen).
     */
    public static Task checkAdd(String input) throws SpoonException {
        String[] inputArray = parseInput(input);
        Command command = Command.fromString(inputArray[0]);
        String commandString = inputArray[0].toLowerCase();

        if (inputArray.length < 2 || inputArray[1].isBlank()) {
            throw new MissingArgumentException(commandString, "description");
        }
        switch (command) {
            // Add todos command
            case TODO: {
                return new ToDo(inputArray[1]);
            }

            // Add deadlines command
            case DEADLINE: {
                return parseDeadline(inputArray, commandString);
            }

            // Add events command
            case EVENT: {
                return parseEvent(inputArray, commandString);
            }

            // Default: placeholder value, should never happen
            default: {
                // checkAdd is only called with commands To-Do, Deadline and Event,
                // and therefore should never reach the default case
                assert false : "Execution reached default case in checkAdd";
                throw new FatalErrorException();
            }
        }
    }

    /**
     * Checks for exceptions in adding deadlines.
     *
     * @param inputArray user input split into command and argument.
     * @param commandString command converted to string.
     * @return deadline initialized with command.
     * @throws MissingArgumentException if argument(s) for deadline initialization are missing.
     * @throws DuplicateArgumentException if there are duplicated arguments for deadline initialization.
     * @throws InvalidFormatException if argument(s) for deadline initialization are in the wrong datetime format.
     * @throws InvalidArgumentException if arguments for deadline initialization are invalid
     *     (i.e. end date before start date).
     */
    private static Deadline parseDeadline(String[] inputArray, String commandString) throws SpoonException {
        if (inputArray[1].startsWith("/by")) {
            throw new MissingArgumentException(commandString, "description");
        }
        String[] deadlineArray = inputArray[1].split("\\s+/by\\s+");
        if (deadlineArray.length < 2 || deadlineArray[1].isBlank()) {
            throw new MissingArgumentException(commandString, "deadline (starting with /by)");
        } else if (deadlineArray.length > 2) {
            throw new DuplicateArgumentException("by");
        }
        return new Deadline(deadlineArray[0], deadlineArray[1]);
    }

    /**
     * Checks for exceptions in adding events.
     *
     * @param inputArray user input split into command and argument.
     * @param commandString command converted to string.
     * @return event initialized with command.
     * @throws MissingArgumentException if argument(s) for event initialization are missing.
     * @throws DuplicateArgumentException if there are duplicated arguments for event initialization.
     * @throws InvalidFormatException if argument(s) for event initialization are in the wrong datetime format.
     * @throws InvalidArgumentException if arguments for event initialization are invalid
     *     (i.e. end date before start date).
     */
    private static Event parseEvent(String[] inputArray, String commandString) throws SpoonException {
        if (inputArray[1].startsWith("/from") || inputArray[1].startsWith("/to")) {
            throw new MissingArgumentException(commandString, "description");
        }
        String[] eventArray = inputArray[1].split("\\s+/from\\s+");
        if (eventArray.length < 2 || eventArray[1].isBlank() || eventArray[1].matches("(?i)^/to.*")) {
            throw new MissingArgumentException(commandString, "start date (starting with /from)");
        } else if (eventArray.length > 2) {
            throw new DuplicateArgumentException("from");
        }
        String[] eventArgsArray = eventArray[1].split("\\s+/to\\s+");
        if (eventArgsArray.length < 2 || eventArgsArray[1].isBlank()) {
            throw new MissingArgumentException(commandString, "end date (starting with /to)");
        } else if (eventArgsArray.length > 2) {
            throw new DuplicateArgumentException("to");
        }
        return new Event(eventArray[0], eventArgsArray[0], eventArgsArray[1]);
    }
}
