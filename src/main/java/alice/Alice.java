package alice;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import alice.exception.AliceException;
import alice.parser.Command;
import alice.parser.Parser;
import alice.storage.Storage;
import alice.task.Task;
import alice.task.TaskList;
import alice.task.ToDo;
import alice.ui.Ui;

/**
 * Runs the Alice bot.
 */
public class Alice {
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT = DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final String UNKNOWN_COMMAND_ERROR = "Oh dear — I didn’t quite understand that command.";
    private static final String TODO_USAGE = "todo read book";
    private static final String DEADLINE_USAGE = "deadline submit report /by 2026-10-01";
    private static final String EVENT_USAGE = "event meeting /from 2026-10-01 1400 /to 2026-10-01 1500";

    private final Storage storage;
    private final TaskList tasks;
    private final Parser parser;

    /**
     * Creates an Alice bot with its saved task list.
     */
    public Alice() {
        storage = new Storage();
        tasks = new TaskList(storage.load());
        parser = new Parser();
    }

    /**
     * Processes a command from either the graphical or console user interface.
     *
     * @param userInput The command entered by the user.
     * @return Alice's response to the command.
     */
    public String getResponse(String userInput) {
        return getResponseResult(userInput).message();
    }

    /**
     * Processes a command and identifies whether its response describes an error.
     *
     * @param userInput The command entered by the user.
     * @return The response text together with its error status.
     */
    public Response getResponseResult(String userInput) {
        Command command = Command.fromCommandWord(parser.getCommandWord(userInput));
        try {
            String message = switch (command) {
                case BYE -> getByeResponse();
                case LIST -> getTaskListResponse();
                case MARK, UNMARK -> getTaskStatusResponse(userInput, command);
                case REPEAT -> getRepeatResponse(userInput);
                case TODO, DEADLINE, EVENT -> getAddTaskResponse(userInput);
                case DATE -> getDateResponse(parser.parseDate(userInput));
                case FIND -> getFindResponse(parser.parseKeyword(userInput));
                case DELETE -> getDeleteResponse(userInput);
                case UNKNOWN -> throw new AliceException("I don't understand that command.");
            };
            return new Response(message, false);
        } catch (AliceException e) {
            return new Response(formatErrorResponse(e.getMessage(), command), true);
        }
    }

    /**
     * Adds a useful example to errors caused by incomplete task-creation commands.
     *
     * @param message The original explanation of the error.
     * @param command The command that caused the error.
     * @return A friendly error response, with usage guidance where useful.
     */
    private String formatErrorResponse(String message, Command command) {
        return switch (command) {
            case UNKNOWN -> UNKNOWN_COMMAND_ERROR;
            case TODO -> message.equals("The description of a todo cannot be empty.")
                    ? formatErrorWithUsage(message, TODO_USAGE)
                    : message;
            case DEADLINE -> formatErrorWithUsage(message, DEADLINE_USAGE);
            case EVENT -> message.equals("An event cannot end before it starts.")
                    ? message
                    : formatErrorWithUsage(message, EVENT_USAGE);
            default -> message;
        };
    }

    /**
     * Formats an input error with a command example.
     *
     * @param message The original explanation of the error.
     * @param usage The example command that fixes the error.
     * @return The formatted error response.
     */
    private String formatErrorWithUsage(String message, String usage) {
        String sentenceFragment = Character.toLowerCase(message.charAt(0)) + message.substring(1);
        return "Oh dear — " + sentenceFragment + "\nTry: " + usage;
    }

    /**
     * Represents text returned after processing a command.
     *
     * @param message The text to display to the user.
     * @param isError Whether the command could not be completed.
     */
    public record Response(String message, boolean isError) {
    }

    /** Creates the response for the {@code bye} command. */
    private String getByeResponse() {
        return "See you soon. Don’t be late!";
    }

    /** Creates the response for a {@code mark} or {@code unmark} command. */
    private String getTaskStatusResponse(String userInput, Command command) throws AliceException {
        Task task = tasks.get(getTaskIndex(userInput, command));
        boolean isMarking = command == Command.MARK;
        if (task.isDone() == isMarking) {
            throw new AliceException(isMarking
                    ? "This task is already marked as done."
                    : "This task is already marked as not done.");
        }

        if (isMarking) {
            if (task.isRecurring()) {
                Task nextOccurrence = task.createNextOccurrence(LocalDateTime.now());
                task.markAsDone();
                tasks.add(nextOccurrence);
                storage.save(tasks.asList());
                return "Wonderful! That task is complete:\n  " + task
                        + "\nThe next occurrence is ready:\n  " + nextOccurrence;
            }
            task.markAsDone();
        } else {
            if (task.isRecurring()) {
                throw new AliceException("Completed recurring tasks cannot be unmarked.");
            }
            task.unmarkAsDone();
        }
        storage.save(tasks.asList());

        return isMarking
                ? "Wonderful! That task is complete:\n  " + task
                : "All right — this task is not done just yet:\n  " + task;
    }

    /** Creates the response for a {@code repeat} command. */
    private String getRepeatResponse(String userInput) throws AliceException {
        Parser.RepeatDetails repeatDetails = parser.parseRepeatCommand(userInput, tasks.size());
        Task task = tasks.get(repeatDetails.taskNumber() - 1);
        if (task instanceof ToDo) {
            throw new AliceException("Todos cannot recur.");
        }
        if (task.isDone()) {
            throw new AliceException("Completed tasks' recurring status cannot be changed.");
        }
        if (repeatDetails.recurrence() == null) {
            if (!task.isRecurring()) {
                throw new AliceException("This task is not recurring.");
            }
            task.clearRecurrence();
            storage.save(tasks.asList());
            return "The clock has stopped for this task:\n  " + task;
        }

        if (repeatDetails.recurrence() == task.getRecurrence()) {
            throw new AliceException("This task already repeats "
                    + repeatDetails.recurrence().getDisplayName() + ".");
        }
        boolean wasRecurring = task.isRecurring();
        task.setRecurrence(repeatDetails.recurrence());
        storage.save(tasks.asList());
        return (wasRecurring ? "A small adjustment: this task will now repeat " : "Got it. This task will now repeat ")
                + repeatDetails.recurrence().getDisplayName() + ":\n  " + task;
    }

    /** Creates the response for a task-creation command. */
    private String getAddTaskResponse(String userInput) throws AliceException {
        Task task = parser.parseTask(userInput);
        tasks.add(task);
        storage.save(tasks.asList());
        return "Got it. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /** Creates the response for the {@code delete} command. */
    private String getDeleteResponse(String userInput) throws AliceException {
        Task deletedTask = tasks.remove(getTaskIndex(userInput, Command.DELETE));
        storage.save(tasks.asList());
        return "Noted. I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.";
    }

    /**
     * Parses a command's task number and converts it to a zero-based task index.
     *
     * @param userInput The complete command entered by the user.
     * @param command The command containing the task number.
     * @return The zero-based index of the selected task.
     * @throws AliceException If the command does not identify an existing task.
     */
    private int getTaskIndex(String userInput, Command command) throws AliceException {
        return parser.parseTaskNumber(userInput, command, tasks.size()) - 1;
    }

    /**
     * Creates the response for the {@code list} command.
     *
     * @return The formatted task list.
     */
    private String getTaskListResponse() {
        if (tasks.size() == 0) {
            return "Nothing on your list just yet. Shall we add something?";
        }
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            response.append("\n  ").append(index + 1).append('.').append(tasks.get(index));
        }
        return response.toString();
    }

    /**
     * Creates the response for the {@code date} command.
     *
     * @param date The date to search for.
     * @return The formatted matching tasks.
     */
    private String getDateResponse(LocalDate date) {
        List<Task> matchingTasks = tasks.getTasksOnDate(date);
        if (matchingTasks.isEmpty()) {
            return "No tasks occur on " + date.format(DATE_DISPLAY_FORMAT) + ".";
        }
        String heading = "Here are the tasks occurring on " + date.format(DATE_DISPLAY_FORMAT) + ":";
        return formatTaskListResponse(heading, matchingTasks);
    }

    /**
     * Creates the response for the {@code find} command.
     *
     * @param keyword The keyword to search for.
     * @return The formatted matching tasks.
     */
    private String getFindResponse(String keyword) {
        List<Task> matchingTasks = tasks.getTasksWithKeyword(keyword);
        if (matchingTasks.isEmpty()) {
            return "No matching tasks found.";
        }
        return formatTaskListResponse("Here are the matching tasks in your list:", matchingTasks);
    }

    /**
     * Formats tasks with their positions in the complete task list.
     *
     * @param heading The heading to show before the tasks.
     * @param tasksToDisplay The tasks to format.
     * @return The formatted task list response.
     */
    private String formatTaskListResponse(String heading, List<Task> tasksToDisplay) {
        StringBuilder response = new StringBuilder(heading);
        for (Task task : tasksToDisplay) {
            response.append("\n  ").append(tasks.indexOf(task) + 1).append('.').append(task);
        }
        return response.toString();
    }

    /**
     * Starts Alice and processes commands until the user enters {@code bye}.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Alice alice = new Alice();
        while (true) {
            String userInput = ui.readCommand();
            ui.showSeparator();
            if (userInput.equals(Command.BYE.getCommandWord())) {
                ui.showResponse(alice.getResponse(userInput));
                break;
            }
            ui.showResponse(alice.getResponse(userInput));
        }
    }
}
