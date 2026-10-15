package xian;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Represents the backend of the Xian task management application.
 * Xian allows users to add, list, mark, unmark, delete, and find tasks
 * via commands, with automatic saving to and loading from disk.
 */
public class Xian {

    private final Storage storage;
    private final Ui ui;
    private TaskList tasks;
    /** Stores a warning for the GUI when saved task data cannot be loaded. */
    private String startupWarning = "";

    /**
     * Creates a Xian instance, initializing the UI and loading
     * previously saved tasks from the given file path.
     * If loading fails, starts with an empty task list instead and records a
     * warning for the user interface.
     *
     * @param filePath the path to the file used for saving/loading tasks.
     */
    public Xian(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);

        try {
            tasks = storage.load();
        } catch (IOException exception) {
            startupWarning = "I couldn't read your saved tasks, so I started with an empty list. "
                    + "Please check that the data file is accessible.";
            System.out.println("Error loading saved tasks");
            tasks = new TaskList();
        } catch (XianException exception) {
            startupWarning = "Your saved task data looks invalid, so I started with an empty list. "
                    + "Please check the data file before continuing.";
            System.out.println(exception.getMessage());
            tasks = new TaskList();
        }
    }

    /**
     * Returns the formatted welcome message for the user interface.
     *
     * @return The formatted welcome response.
     */
    public String getWelcomeMessage() {
        return ui.formatWelcomeMessage();
    }

    /**
     * Returns a warning produced while loading saved tasks, if any.
     *
     * @return the startup warning, or an empty string when loading succeeded.
     */
    public String getStartupWarning() {
        return startupWarning;
    }

    /**
     * Parses and validates the task index supplied to a task-modifying command.
     *
     * @param remainder the arguments containing the task index.
     * @param action the action to include in the validation error message.
     * @return the validated one-based task index.
     * @throws XianException if the task index is invalid.
     */
    private int parseAndValidateTaskIndex(String remainder, String action) throws XianException {
        int index;

        try {
            index = Parser.parseIndex(remainder);
        } catch (NumberFormatException exception) {
            throw new XianException("Please enter a whole-number task index to " + action + ".");
        }

        if (index < 1 || index > tasks.getSize()) {
            throw new XianException("Please enter a task number from 1 to "
                    + tasks.getSize() + " to " + action + ".");
        }

        return index;
    }

    /**
     * Handles the "mark" command by marking the specified task as done
     * and saving the updated task list.
     *
     * @param remainder the arguments containing the task index to mark.
     * @throws IOException if the updated task list cannot be saved.
     * @throws XianException if the given index is invalid.
     */
    private String handleMark(String remainder) throws IOException, XianException {
        int index = parseAndValidateTaskIndex(remainder, "mark");
        Task task = tasks.get(index - 1);
        task.mark();
        storage.save(tasks);

        return ui.formatTaskMark(task);
    }

    /**
     * Handles the "unmark" command by marking the specified task as not done
     * and saving the updated task list.
     *
     * @param remainder the arguments containing the task index to unmark.
     * @throws IOException if the updated task list cannot be saved.
     * @throws XianException if the given index is invalid.
     */
    private String handleUnmark(String remainder) throws IOException, XianException {
        int index = parseAndValidateTaskIndex(remainder, "unmark");
        Task task = tasks.get(index - 1);
        task.unmark();
        storage.save(tasks);

        return ui.formatTaskUnmark(task);
    }

    /**
     * Handles the "delete" command by removing the specified task
     * and saving the updated task list.
     *
     * @param remainder the arguments containing the task index to delete.
     * @throws IOException if the updated task list cannot be saved.
     * @throws XianException if the given index is invalid.
     */
    private String handleDelete(String remainder) throws IOException, XianException {
        int index = parseAndValidateTaskIndex(remainder, "delete");
        Task task = tasks.delete(index);
        storage.save(tasks);

        return ui.formatTaskDelete(task, tasks);
    }

    /**
     * Handles the "todo", "deadline", and "event" commands by creating
     * the corresponding task, adding it to the task list, and saving
     * the updated task list.
     *
     * @param command the type of task to add ("todo", "deadline", or "event").
     * @param remainder the arguments describing the task to create.
     * @throws IOException if the updated task list cannot be saved.
     * @throws XianException if the arguments are not in the expected format.
     */
    private String handleAddTask(String command, String remainder) throws IOException, XianException {
        Task task = switch (command) {
            case "todo" -> Parser.parseTodo(remainder);
            case "deadline" -> Parser.parseDeadline(remainder);
            case "event" -> Parser.parseEvent(remainder);
            default -> throw new XianException("Please enter a valid action :( ");
        };
        tasks.add(task);
        storage.save(tasks);
        return ui.formatTaskAdded(task, tasks);
    }

    /**
     * Handles the "find" command by returning a formatted response containing
     * tasks matching the keyword.
     *
     * @param remainder The keyword to search for in task descriptions.
     */
    private String handleFind(String remainder) {
        TaskList matchingTasks = tasks.find(remainder);
        return ui.formatMatchingTasks(matchingTasks);
    }

    /**
     * Updates one field of an existing task and saves the updated task list.
     *
     * @param remainder the task index, field, and new value.
     * @return the formatted task-update response.
     * @throws IOException if the updated task list cannot be saved.
     * @throws XianException if the update arguments or task type are invalid.
     */
    private String handleUpdate(String remainder) throws IOException, XianException {
        String[] updateParts = Parser.parseUpdate(remainder);
        int index = parseAndValidateTaskIndex(updateParts[0], "update");
        Task task = tasks.get(index - 1);
        String field = updateParts[1];
        String value = updateParts[2];

        switch (field) {
            case "desc" -> {
                Parser.validateTaskDescription(value);
                task.setDescription(value);
            }
            case "by" -> {
                if (!(task instanceof Deadline deadline)) {
                    throw new XianException("The 'by' field can only be updated for a deadline task :( ");
                }
                deadline.setByDate(Parser.parseDateTime(value));
            }
            case "from" -> {
                if (!(task instanceof Event event)) {
                    throw new XianException("The 'from' field can only be updated for an event task :( ");
                }
                LocalDateTime newFrom = Parser.parseDateTime(value);
                Parser.validateEventTimeRange(newFrom, event.getTo());
                event.setFrom(newFrom);
            }
            case "to" -> {
                if (!(task instanceof Event event)) {
                    throw new XianException("The 'to' field can only be updated for an event task :( ");
                }
                LocalDateTime newTo = Parser.parseDateTime(value);
                Parser.validateEventTimeRange(event.getFrom(), newTo);
                event.setTo(newTo);
            }
            default -> throw new XianException("Please specify a valid field to update :( ");
        }

        storage.save(tasks);
        return ui.formatTaskUpdated(task);
    }

    /**
     * Executes a user command and returns the formatted response.
     *
     * @param input The user command to execute.
     * @return The formatted response for the command.
     * @throws XianException If the command or its arguments are invalid.
     * @throws IOException If task data cannot be saved.
     */
    public String executeCommand(String input) throws XianException, IOException {
        assert tasks != null : "Task list must be initialized before executing commands";

        String normalizedInput = Parser.normalizeInput(input);

        if (normalizedInput.equals("list")) {
            return ui.formatTaskList(tasks);
        } else {
            String command = Parser.getCommandWord(normalizedInput);

            if (command.equals("list")) {
                throw new XianException("The list command does not take any arguments.");
            }

            if (!command.equals("find") && !command.equals("mark")
                    && !command.equals("unmark") && !command.equals("delete")
                    && !command.equals("update") && !command.equals("todo")
                    && !command.equals("deadline") && !command.equals("event")) {
                throw new XianException("I don't recognize '" + command
                        + "'. Try 'list' or another supported command.");
            }

            String remainder = Parser.getArguments(normalizedInput);

            return switch (command) {
                case "find" -> handleFind(remainder);
                case "mark" -> handleMark(remainder);
                case "unmark" -> handleUnmark(remainder);
                case "delete" -> handleDelete(remainder);
                case "update" -> handleUpdate(remainder);
                default -> handleAddTask(command, remainder);
            };
        }
    }

}
