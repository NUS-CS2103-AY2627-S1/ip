package ayre.storage;

import java.util.Arrays;
import java.util.List;

import ayre.commands.Command;
import ayre.commands.DeadlineCommand;
import ayre.commands.EventCommand;
import ayre.commands.TodoCommand;
import ayre.enums.CommandType;
import ayre.exceptions.InvalidCommandException;
import ayre.exceptions.TaskLogCorruptedException;
import ayre.tasks.Deadline;
import ayre.tasks.Event;
import ayre.tasks.Task;
import ayre.tasks.Todo;

/**
 * Log file parser to support the Storage class.
 * Primarily handles creating a Task out of whatever line is read out of the log file, and throws a relevant
 * exception if the process fails at any point.
 */
public class LogParser {
    private static final int COMMAND_FLAG_INDEX = 0;
    private static final int NAME_INDEX = 2;
    private static final int FIRST_DATE_INDEX = 3;
    private static final int SECOND_DATE_INDEX = 4;
    private static final int MIN_TOKENS = 3;

    /**
     * Creates and returns a new Task based on input arguments.
     * Package private to facilitate JUnit testing for the exception thrown.
     *
     * @param index Identifies the line of the save file in the exception in case log corruption is detected.
     * @param logArgs Tokenized arguments taken from parsing the line.
     * @return A new Task reconstructed via the logArgs.
     * @throws TaskLogCorruptedException If save file data does not match parsing expectations.
     */
    public static Task constructNewTask(int index, String ...logArgs) throws TaskLogCorruptedException {
        return switch (logArgs[COMMAND_FLAG_INDEX]) {
            case "T" -> {
                validateLog(CommandType.TODO, new TodoCommand(null), logArgs, index);
                assert logArgs.length == 3 : "Number of log arguments should match log specifications (3)";
                yield new Todo(logArgs[NAME_INDEX]);
            }
            case "D" -> {
                validateLog(CommandType.DEADLINE, new DeadlineCommand(null), logArgs, index);
                assert logArgs.length == 4 : "Number of log arguments should match log specifications (4)";
                yield new Deadline(logArgs[NAME_INDEX], logArgs[FIRST_DATE_INDEX]);
            }
            case "E" -> {
                validateLog(CommandType.EVENT, new EventCommand(null), logArgs, index);
                assert logArgs.length == 5 : "Number of log arguments should match log specifications (5)";
                yield new Event(logArgs[NAME_INDEX], logArgs[FIRST_DATE_INDEX], logArgs[SECOND_DATE_INDEX]);
            }
            default -> {
                throw new TaskLogCorruptedException("Dropped log line at index " + index + ": Data corrupted");
            }
        };
    }

    /**
     * Checks the correctness of the arguments in a line of the save file.
     *
     * @param cmdType The CommandType to check the number of required arguments.
     * @param command A temporary Command used for argument validation.
     * @param logArgs A tokenized array holding the arguments present in the save file line.
     * @param index The index of the line in its file.
     * @throws TaskLogCorruptedException If validation checks for number of arguments or correctness fail.
     */
    private static void validateLog(CommandType cmdType, Command command, String[] logArgs, int index)
            throws TaskLogCorruptedException {
        if (logArgs.length < MIN_TOKENS) { // expected save file log format has minimum 3 tokens for a todo
            throw new TaskLogCorruptedException("Dropped log line at index " + index + ": Missing data detected");
        }
        // turn the arguments from NAME_INDEX onwards into a list
        List<String> cmdArgs = Arrays.asList(Arrays.copyOfRange(logArgs, NAME_INDEX, logArgs.length));
        if (cmdArgs.size() != cmdType.getNumArgs()) {
            throw new TaskLogCorruptedException("Dropped log line at index " + index + ": Missing data detected");
        }
        try {
            command.validate(cmdArgs); // correctness check for the arguments
        } catch (InvalidCommandException e) {
            throw new TaskLogCorruptedException("Dropped log line at index " + index + ": Data corrupted");
        }
    }
}
