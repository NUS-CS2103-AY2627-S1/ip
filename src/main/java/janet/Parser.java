package janet;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Map;

/**
 * Parses user commands and maps them to their corresponding task operations.
 */
public class Parser {

    protected static final String RESET_WORD = "reset";
    private static final int COMMAND_INDEX = 0;
    private static final String ARG_SEPARATOR = "\\s+";
    private static final String EXIT_WORD = "bye";
    private final TaskList taskList;

    private final Map<
            String,
            CommandHandler> commandMap = Map.of(
            "list", this::handleListTasksCommand,
            "find", this::handleFindTasksCommand,
            "mark", this::handleMarkTaskCommand,
            "todo", this::handleAddTodoCommand,
            "deadline", this::handleAddDeadlineCommand,
            "event", this::handleAddEventCommand,
            "delete", this::handleDeleteTaskCommand,
            "sort", this::handleSetTaskListComparatorCommand
    );

    private final Map<String, Comparator<Task>> listComparators = Map.of(
            "default", TaskList.DEFAULT_COMPARATOR,
            "label", TaskList.LABEL_COMPARATOR
    );

    /**
     * Creates a parser for the specified task list.
     *
     * @param taskList Task list to be modified by parsed commands.
     */
    public Parser(TaskList taskList) {
        this.taskList = taskList;
    }

    public static String formatString(String currLine) {
        return currLine.trim();
    }

    public static boolean isExitCommand(String currLine) {
        return currLine.equals(Parser.EXIT_WORD);
    }

    public static boolean isResetCommand(String currLine) {
        return currLine.equals(Parser.RESET_WORD);
    }

    /**
     * Processes a command entered by the user and returns the corresponding result.
     *
     * @param currLine Command and arguments to process.
     * @return Result of processing the command.
     * @throws JanetException If the command is not recognized or cannot be processed.
     */
    public TaskList.CommandResult processCommand(String currLine) throws JanetException {
        String command = this.getCommand(currLine);
        String argsLine = this.getArgsLine(command, currLine); // remaining string

        if (!this.commandMap.containsKey(command)) {
            throw new JanetException("Unrecognised command!");
        }

        CommandHandler handler = this.commandMap.get(command);
        assert handler != null : "Every registered command must have a handler";
        return handler.handle(argsLine);
    }

    private String getCommand(String currLine) {
        return currLine.split(Parser.ARG_SEPARATOR)[Parser.COMMAND_INDEX].trim();
    }

    private String getArgsLine(String command, String currLine) {
        return currLine.substring(command.length()).trim();
    }

    private TaskList.CommandResult handleListTasksCommand(String argsLine) {
        return this.taskList.listTasks();
    }

    private TaskList.CommandResult handleFindTasksCommand(String argsLine) {
        return this.taskList.findTasks(argsLine);
    }

    private TaskList.CommandResult handleMarkTaskCommand(String argsLine) throws JanetException {
        try {
            return this.taskList.markTask(Integer.parseInt(argsLine));
        } catch (NumberFormatException e) {
            throw new JanetException("Mark should contain integer!");
        }
    }

    private TaskList.CommandResult handleAddTodoCommand(String argsLine) throws JanetException {
        return this.handleAddTaskCommand(new Todo(false, argsLine));
    }

    private TaskList.CommandResult handleAddDeadlineCommand(String argsLine) throws JanetException {
        // find what index the /by is located in the line, if exists
        int deadlineIndex = argsLine.indexOf(Deadline.DEADLINE_SEP);
        if (deadlineIndex == -1) {
            throw new JanetException("Deadline not found!");
        }

        // receive the actual string parameters for the command based on index
        String taskLabel = argsLine.substring(0, deadlineIndex).trim();
        String deadline = argsLine.substring(deadlineIndex + Deadline.DEADLINE_SEP.length()).trim();
        if (deadline.isEmpty()) {
            throw new JanetException(String.format("Invalid deadline arguments! Deadline: %s\n", deadline));
        }

        // convert to Date format
        try {
            LocalDate deadlineDate = LocalDate.parse(deadline);
            return this.handleAddTaskCommand(new Deadline(false, taskLabel, deadlineDate));
        } catch (DateTimeParseException e) {
            throw new JanetException("Invalid date format");
        }
    }

    private TaskList.CommandResult handleAddEventCommand(String argsLine) throws JanetException {
        // find what index the /from and /to is located in the line, if exists
        int fromIndex = this.getIndexFromStringAndSep(argsLine, Event.EVENT_FROM_SEP);
        int toIndex = this.getIndexFromStringAndSep(argsLine, Event.EVENT_TO_SEP);

        // receive the actual strings parameters for the command based on index
        String taskLabel = this.getArgFromIndex(argsLine, 0, fromIndex);
        String from = this.getArgFromIndex(argsLine, fromIndex + Event.EVENT_FROM_SEP.length(), toIndex);
        String to = this.getArgFromIndex(argsLine, toIndex + Event.EVENT_TO_SEP.length(), argsLine.length());

        // convert to Date format
        try {
            LocalDate fromDate = LocalDate.parse(from);
            LocalDate toDate = LocalDate.parse(to);
            return this.handleAddTaskCommand(new Event(false, taskLabel, fromDate, toDate));
        } catch (DateTimeParseException e) {
            throw new JanetException("Invalid date format");
        }
    }

    private int getIndexFromStringAndSep(String argsLine, String sep) throws JanetException {
        int index = argsLine.indexOf(sep);
        if (index == -1) {
            throw new JanetException(String.format("Argument not found when parsing [%s]: %s", argsLine, sep));
        }
        return index;
    }

    private String getArgFromIndex(String argsLine, int startIndex, int toIndex) throws JanetException {
        try {
            String arg = argsLine.substring(startIndex, toIndex).trim();
            if (arg.isEmpty()) {
                throw new JanetException(String.format("Argument is empty when parsing [%s]: %s", argsLine, arg));
            }
            return arg;
        } catch (IndexOutOfBoundsException e) {
            throw new JanetException(
                    String.format(
                            "Arguments are in an invalid order when parsing [%s]",
                            argsLine
                    )
            );
        }
    }

    private TaskList.CommandResult handleAddTaskCommand(Task task) throws JanetException {
        assert task != null : "Private parser helpers only receive constructed tasks";
        return this.taskList.addTask(task);
    }

    private TaskList.CommandResult handleDeleteTaskCommand(String argsLine) throws JanetException {
        try {
            return this.taskList.deleteTask(Integer.parseInt(argsLine));
        } catch (NumberFormatException e) {
            throw new JanetException("Delete should contain integer!");
        }
    }

    private TaskList.CommandResult handleSetTaskListComparatorCommand(String argsLine) throws JanetException {
        if (!this.listComparators.containsKey(argsLine)) {
            throw new JanetException(String.format("Unknown sort command: %s", argsLine));
        }
        Comparator<Task> comparator = this.listComparators.get(argsLine);
        return this.taskList.setTaskListComparator(comparator);
    }
}
