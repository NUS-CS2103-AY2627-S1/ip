package peter;

import java.time.LocalDate;
import java.util.List;

import peter.command.Command;
import peter.exception.PeterException;
import peter.parser.Parser;
import peter.storage.Storage;
import peter.storage.Storage.LoadResult;
import peter.task.Task;
import peter.task.TaskList;
import peter.ui.Ui;

/**
 * The main class of the chatbot.
 */
public class Peter {
    private static final String WELCOME_MESSAGE = "Welcome aboard—I'm Peter, your task navigator."
            + "\nWhat shall we chart today?";

    private final Storage storage;
    private TaskList tasks;
    private final Ui ui;
    private String loadingWarning;

    /**
     * Constructs a chatbot.
     *
     * @param filePath The path to the storage file.
     */
    public Peter(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            LoadResult loadResult = storage.load();
            tasks = new TaskList(loadResult.tasks());
            if (loadResult.skippedEntries() > 0) {
                loadingWarning = "[Log warning] I skipped " + loadResult.skippedEntries()
                        + " damaged or duplicate saved entries; the rest loaded safely.";
            }
        } catch (PeterException e) {
            loadingWarning = "[Log warning] " + e.getMessage() + " Starting with a clear map.";
            tasks = new TaskList();
        }
    }

    /**
     * Runs the chatbot's command loop.
     */
    public void run() {
        if (loadingWarning != null) {
            ui.showError(loadingWarning);
        }
        ui.showWelcome();
        boolean isExit = false;

        while (!isExit) {
            String fullCommand = ui.readCommand();
            if (fullCommand.isEmpty()) {
                continue;
            }

            ui.showLine();
            Response response = getResponseResult(fullCommand);
            ui.showMessage(response.message());
            isExit = Command.fromString(fullCommand) == Command.BYE && !response.isError();
            ui.showLine();
        }
        ui.close();
    }

    /**
     * Generates Peter's response to one command for use by both the GUI and text UI.
     *
     * @param fullCommand Raw command entered by the user.
     * @return Peter's response text.
     */
    public String getResponse(String fullCommand) {
        return getResponseResult(fullCommand).message();
    }

    /**
     * Generates a response together with presentation metadata for the GUI.
     *
     * @param fullCommand Raw command entered by the user.
     * @return Response text and whether it represents an input error.
     */
    public Response getResponseResult(String fullCommand) {
        try {
            Command command = Command.fromString(fullCommand);
            String message = switch (command) {
                case BYE -> {
                    Parser.requireNoArguments(fullCommand, "bye");
                    yield "Journey paused. Your course is saved—see you on the next leg!";
                }
                case LIST -> {
                    Parser.requireNoArguments(fullCommand, "list");
                    yield handleList();
                }
                case MARK -> handleMark(fullCommand);
                case UNMARK -> handleUnmark(fullCommand);
                case TODO -> handleTodo(fullCommand);
                case DEADLINE -> handleDeadline(fullCommand);
                case EVENT -> handleEvent(fullCommand);
                case DELETE -> handleDelete(fullCommand);
                case VIEW -> handleView(fullCommand);
                case FIND -> handleFind(fullCommand);
                case SORT -> {
                    Parser.requireNoArguments(fullCommand, "sort");
                    yield handleSort();
                }
                default -> throw new PeterException(
                        "That route isn't on my map yet. Try a command such as list, todo, or deadline.");
            };
            return new Response(message, false);
        } catch (PeterException e) {
            return new Response(e.getMessage(), true);
        } catch (RuntimeException e) {
            return new Response("I hit an unexpected obstacle. Please check the command and try again.", true);
        }
    }

    /**
     * Returns the greeting shown when the GUI starts.
     *
     * @return Peter's welcome message.
     */
    public String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /**
     * Returns a recoverable storage warning encountered during startup.
     *
     * @return Warning text, or null when every saved entry loaded successfully.
     */
    public String getLoadingWarning() {
        return loadingWarning;
    }

    private String handleList() {
        if (tasks.isEmpty()) {
            return "The route is clear—there are no tasks on the map.";
        }
        List<Task> currentTasks = tasks.getTasks();
        StringBuilder response = new StringBuilder("Here's the course we've charted:");
        for (int i = 0; i < currentTasks.size(); i++) {
            response.append(System.lineSeparator()).append(i + 1).append('.').append(currentTasks.get(i));
        }
        return response.toString();
    }

    private String handleMark(String input) throws PeterException {
        int index = Parser.parseIndex(input);
        boolean wasDone = tasks.get(index).isDone();
        Task task = tasks.mark(index);
        try {
            storage.save(tasks);
        } catch (PeterException e) {
            if (!wasDone) {
                task.markAsUndone();
            }
            throw e;
        }
        return "Milestone reached! I've marked this task complete:\n  " + task;
    }

    private String handleUnmark(String input) throws PeterException {
        int index = Parser.parseIndex(input);
        boolean wasDone = tasks.get(index).isDone();
        Task task = tasks.unmark(index);
        try {
            storage.save(tasks);
        } catch (PeterException e) {
            if (wasDone) {
                task.markAsDone();
            }
            throw e;
        }
        return "Course adjusted. This task is back on the route:\n  " + task;
    }

    private String handleTodo(String input) throws PeterException {
        Task task = Parser.parseTodo(input);
        addAndSave(task);
        return getTaskAddedMessage(task);
    }

    private String handleDeadline(String input) throws PeterException {
        Task task = Parser.parseDeadline(input);
        addAndSave(task);
        return getTaskAddedMessage(task);
    }

    private String handleEvent(String input) throws PeterException {
        Task task = Parser.parseEvent(input);
        addAndSave(task);
        return getTaskAddedMessage(task);
    }

    private String handleDelete(String input) throws PeterException {
        int index = Parser.parseIndex(input);
        TaskList updatedTasks = new TaskList(tasks.getTasks());
        Task removedTask = updatedTasks.delete(index);
        storage.save(updatedTasks);
        tasks = updatedTasks;
        return "Route updated. I've removed this task:\n  " + removedTask
                + "\nThere are now " + tasks.size() + " tasks on the map.";
    }

    private void addAndSave(Task task) throws PeterException {
        TaskList updatedTasks = new TaskList(tasks.getTasks());
        updatedTasks.add(task);
        storage.save(updatedTasks);
        tasks = updatedTasks;
    }

    /**
     * Handles the find command by searching tasks matching the keyword.
     *
     * @param fullCommand Raw command input string.
     * @throws PeterException If keyword is missing.
     */
    private String handleFind(String fullCommand) throws PeterException {
        String keyword = Parser.parseFindKeyword(fullCommand);
        List<Task> matchingTasks = tasks.findTasks(keyword);
        return formatTasks(matchingTasks, "I searched the map, but found no tasks matching that keyword.",
                "These tasks match your search:");
    }

    private String getTaskAddedMessage(Task task) {
        return "Waypoint charted! I've added this task:\n  " + task
                + "\nThere are now " + tasks.size() + " tasks on the map.";
    }

    /**
     * Handles the view command to list all tasks occurring on a specific date.
     *
     * @param fullCommand Raw command string entered by the user (e.g., "view 2019-12-02").
     * @throws PeterException If the date format is invalid or parameter is missing.
     */
    private String handleView(String fullCommand) throws PeterException {
        LocalDate date = Parser.parseViewDate(fullCommand);
        List<Task> matchingTasks = tasks.getTasksOnDate(date);
        return formatTasks(matchingTasks, "No tasks are charted for that date.",
                "Here's the route for that date:");
    }

    /**
     * Sorts deadlines chronologically and persists the reordered task list.
     *
     * @return A message containing the sorted task list.
     */
    private String handleSort() throws PeterException {
        TaskList updatedTasks = new TaskList(tasks.getTasks());
        updatedTasks.sortByDeadline();
        storage.save(updatedTasks);
        tasks = updatedTasks;
        return formatTasks(tasks.getTasks(), "The route is clear—there are no deadlines to arrange.",
                "Course arranged! Deadlines now run chronologically:");
    }

    private String formatTasks(List<Task> matchingTasks, String emptyMessage, String heading) {
        if (matchingTasks.isEmpty()) {
            return emptyMessage;
        }
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < matchingTasks.size(); i++) {
            response.append(System.lineSeparator()).append(i + 1).append('.').append(matchingTasks.get(i));
        }
        return response.toString();
    }

    /**
     * A command response and the information needed to present it appropriately.
     *
     * @param message Text to display.
     * @param isError Whether the response describes invalid user input.
     */
    public record Response(String message, boolean isError) {
    }

    public static void main(String[] args) {
        new Peter("data/peter.txt").run();
    }
}
