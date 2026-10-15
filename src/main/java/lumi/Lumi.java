package lumi;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import lumi.command.CommandType;
import lumi.exception.LumiException;
import lumi.parser.Parser;
import lumi.storage.Storage;
import lumi.task.Deadline;
import lumi.task.Event;
import lumi.task.Task;
import lumi.ui.Ui;

/**
 * Runs the Lumi chatbot and generates replies for its graphical interface.
 */
public final class Lumi {
    /** Portable path to the task data file, relative to the project root. */
    private static final Path DATA_FILE = Path.of("data", "lumi.txt");

    /** Farewell returned when the user ends the current Lumi session. */
    private static final String GOODBYE_MESSAGE =
            " Until next time. Your tasks are safe here.";

    /** Storage used to load and save the current task list. */
    private final Storage storage;

    /** Tasks available to both the console and graphical interfaces. */
    private final ArrayList<Task> tasks = new ArrayList<>();

    /** Warning to show after startup when some stored data could not be loaded. */
    private final String startupMessage;

    /** Whether this Lumi session is still accepting commands. */
    private boolean isRunning = true;

    /** Creates a Lumi chatbot backed by the default task data file. */
    public Lumi() {
        this(DATA_FILE);
    }

    /**
     * Creates a Lumi chatbot backed by a particular task data file.
     * This overload lets tests use isolated files without changing user data.
     *
     * @param dataFile Path of the task data file.
     */
    Lumi(Path dataFile) {
        storage = new Storage(dataFile);
        String loadMessage = "";
        try {
            Storage.LoadResult loadResult = storage.load();
            assert loadResult.tasks() != null : "Storage must return a task list";
            assert loadResult.skippedLineCount() >= 0
                    : "Storage cannot report a negative number of skipped lines";
            tasks.addAll(loadResult.tasks());
            if (loadResult.skippedLineCount() > 0) {
                loadMessage = "I found " + loadResult.skippedLineCount()
                        + " unreadable line(s) in your saved tasks and skipped them.";
            }
        } catch (IOException error) {
            loadMessage = "I couldn't open your saved tasks, so we're starting with a clear map.";
        }
        startupMessage = loadMessage;
    }

    /**
     * Returns any warning produced while loading saved tasks.
     *
     * @return A loading warning, or an empty string when loading succeeded.
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    /**
     * Reports whether the chatbot is still accepting commands.
     *
     * @return True until the user enters {@code bye}.
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Executes a command entered through either interface and generates its response.
     *
     * @param input Complete command entered by the user.
     * @return Lumi's response to the command.
     */
    public String getResponse(String input) {
        String command = input == null ? "" : input.trim();
        try {
            CommandType commandType = Parser.parseCommandType(command);
            if (commandType == CommandType.BYE) {
                isRunning = false;
                return GOODBYE_MESSAGE;
            }

            if (commandType == CommandType.LIST) {
                return formatNumberedTasks("Here's your current constellation:", tasks);
            }
            if (commandType == CommandType.FIND) {
                String keyword = Parser.parseFindKeyword(command);
                List<Task> matchingTasks = tasks.stream()
                        .filter(task -> task.hasDescriptionContaining(keyword))
                        .toList();
                return formatNumberedTasks("These tasks match your signal:", matchingTasks);
            }
            if (commandType == CommandType.MARK) {
                int taskIndex = getTaskIndex(command, commandType);
                Task task = tasks.get(taskIndex);
                task.markAsDone();
                return saveTasks(" A little brighter. This task is complete:\n   " + task);
            }
            if (commandType == CommandType.UNMARK) {
                int taskIndex = getTaskIndex(command, commandType);
                Task task = tasks.get(taskIndex);
                task.markAsNotDone();
                return saveTasks(" Back in orbit. This task is active again:\n   " + task);
            }
            if (commandType == CommandType.SNOOZE) {
                return snoozeTask(command);
            }
            if (commandType == CommandType.DELETE) {
                int taskIndex = getTaskIndex(command, commandType);
                int previousTaskCount = tasks.size();
                Task removedTask = tasks.remove(taskIndex);
                assert tasks.size() == previousTaskCount - 1
                        : "Deleting one task must reduce the task count by one";
                String response = " Cleared from the map. I've removed this task:\n   " + removedTask
                        + formatTaskCount();
                return saveTasks(response);
            }
            if (commandType == CommandType.TODO
                    || commandType == CommandType.DEADLINE
                    || commandType == CommandType.EVENT) {
                Task newTask = Parser.parseTask(command, commandType);
                int previousTaskCount = tasks.size();
                tasks.add(newTask);
                assert tasks.size() == previousTaskCount + 1
                        : "Adding one task must increase the task count by one";
                String response = " It's on the map. I've added this task:\n   " + newTask
                        + formatTaskCount();
                return saveTasks(response);
            }
            throw new LumiException("I don't recognize that command.");
        } catch (LumiException error) {
            return " " + error.getMessage();
        }
    }

    /**
     * Runs the chatbot and processes user input until the user enters {@code bye}.
     *
     * @param args Command-line arguments; not used by this application.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();
        Lumi lumi = new Lumi();
        if (!lumi.getStartupMessage().isEmpty()) {
            ui.showResponse(lumi.getStartupMessage());
        }
        ui.showDivider();

        while (lumi.isRunning() && ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showDivider();
            ui.showResponse(lumi.getResponse(command));
            ui.showDivider();
        }
    }

    /**
     * Formats tasks with the one-based numbers shown in Lumi responses.
     *
     * @param heading Explanation shown above the tasks.
     * @param tasksToDisplay Tasks to include in the response.
     * @return Heading followed by the numbered tasks.
     */
    private String formatNumberedTasks(String heading, List<Task> tasksToDisplay) {
        assert heading != null && !heading.isBlank()
                : "A numbered task response must have a heading";
        assert tasksToDisplay != null : "A numbered task response must have a task list";
        StringBuilder response = new StringBuilder(" ").append(heading);
        for (int i = 0; i < tasksToDisplay.size(); i++) {
            response.append("\n ").append(i + 1).append(".").append(tasksToDisplay.get(i));
        }
        return response.toString();
    }

    /**
     * Parses a task number and verifies the parser's index postcondition.
     *
     * @param command Complete mark, unmark, or delete command.
     * @param commandType Type of task command.
     * @return A valid zero-based index into the current task list.
     * @throws LumiException If the task number is missing, invalid, or out of range.
     */
    private int getTaskIndex(String command, CommandType commandType) throws LumiException {
        int taskIndex = Parser.parseTaskIndex(command, commandType, tasks.size());
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Parser must return an index within the current task list";
        return taskIndex;
    }

    /** Reschedules a deadline or event and saves its updated schedule. */
    private String snoozeTask(String command) throws LumiException {
        Parser.SnoozeRequest request = Parser.parseSnoozeRequest(command, tasks.size());
        Task task = tasks.get(request.taskIndex());
        if (task instanceof Deadline deadline) {
            deadline.reschedule(request.newDateTime());
        } else if (task instanceof Event event) {
            event.reschedule(request.newDateTime());
        } else {
            throw new LumiException("only deadlines and events can be snoozed.");
        }
        return saveTasks(" Orbit adjusted. I've rescheduled this task:\n   " + task);
    }

    /**
     * Saves the current tasks and appends a warning if saving fails.
     *
     * @param response Successful command response.
     * @return The response, followed by a saving warning when needed.
     */
    private String saveTasks(String response) {
        try {
            storage.save(tasks);
            return response;
        } catch (IOException error) {
            return response + "\n " + LumiException.ERROR_PREFIX
                    + "I couldn't save the latest task changes.";
        }
    }

    /** Formats the task count with correct singular or plural grammar. */
    private String formatTaskCount() {
        String taskWord = tasks.size() == 1 ? "task" : "tasks";
        return "\n Your map now holds " + tasks.size() + " " + taskWord + ".";
    }
}
