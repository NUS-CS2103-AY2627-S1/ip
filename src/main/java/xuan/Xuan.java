package xuan;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import xuan.exception.XuanException;
import xuan.parser.Parser;
import xuan.storage.Storage;
import xuan.task.Deadline;
import xuan.task.Event;
import xuan.task.Task;
import xuan.task.TaskList;
import xuan.task.Todo;
import xuan.ui.Ui;

/**
 * Runs the Xuan chatbot application.
 */
public class Xuan {

    private final Ui ui;
    private final Parser parser;
    private final Storage storage;
    private final TaskList taskList;

    /**
     * Creates a Xuan chatbot and loads saved tasks from storage.
     */
    public Xuan() {
        ui = new Ui();
        parser = new Parser();
        storage = new Storage("./data/xuan.txt");

        TaskList loadedTaskList;

        try {
            loadedTaskList = new TaskList(storage.loadTasks());
        } catch (FileNotFoundException e) {
            loadedTaskList = new TaskList(new ArrayList<>());
        }

        taskList = loadedTaskList;
    }

    /**
     * Finds tasks whose descriptions contain the specified keyword.
     *
     * @param input the full find command entered by the user
     * @return the message containing the matching tasks
     * @throws XuanException if the search keyword is missing
     */
    private String handleFind(String input) throws XuanException {
        String keyword = parser.getFindKeyword(input);
        ArrayList<Task> matchingTasks = taskList.findTasks(keyword);

        return ui.getMatchingTasksMessage(matchingTasks);
    }

    /**
     * Finds deadlines that occur on the specified date.
     *
     * @param input the full finddate command entered by the user
     * @return the message containing deadlines on the specified date
     * @throws XuanException if the date is missing or invalid
     */
    private String handleFindDate(String input) throws XuanException {
        LocalDate targetDate = parser.getFindDate(input);
        ArrayList<Deadline> deadlines =
                taskList.findDeadlinesOnDate(targetDate);

        return ui.getDeadlinesOnDateMessage(targetDate, deadlines);
    }

    /**
     * Marks the specified task as done and saves the updated task list.
     *
     * @param input the full mark command entered by the user
     * @return the message describing the marked task
     * @throws XuanException if the task number is missing or invalid
     * @throws IOException if the updated task list cannot be saved
     */
    private String handleMark(String input) throws XuanException, IOException {
        if (!input.startsWith("mark ")
                || input.substring(5).trim().isEmpty()) {
            throw new XuanException(
                    "Please specify the task number to mark.");
        }

        int taskNumber = parser.getTaskNumber(input, 5);

        if (taskNumber < 1 || taskNumber > taskList.size()) {
            throw new XuanException(
                    "That task number does not exist.");
        }

        Task task = taskList.get(taskNumber - 1);
        task.markAsDone();
        storage.saveTasks(taskList.getTasks());

        return ui.getMarkedTaskMessage(task);
    }

    /**
     * Marks the specified task as not done and saves the updated task list.
     *
     * @param input the full unmark command entered by the user
     * @return the message describing the unmarked task
     * @throws XuanException if the task number is missing or invalid
     * @throws IOException if the updated task list cannot be saved
     */
    private String handleUnmark(String input)
            throws XuanException, IOException {
        if (!input.startsWith("unmark ")
                || input.substring(7).trim().isEmpty()) {
            throw new XuanException(
                    "Please specify the task number to unmark.");
        }

        int taskNumber = parser.getTaskNumber(input, 7);

        if (taskNumber < 1 || taskNumber > taskList.size()) {
            throw new XuanException(
                    "That task number does not exist.");
        }

        Task task = taskList.get(taskNumber - 1);
        task.markAsNotDone();
        storage.saveTasks(taskList.getTasks());

        return ui.getUnmarkedTaskMessage(task);
    }

    /**
     * Deletes the specified task and saves the updated task list.
     *
     * @param input the full delete command entered by the user
     * @return the message describing the deleted task
     * @throws XuanException if the task number is missing or invalid
     * @throws IOException if the updated task list cannot be saved
     */
    private String handleDelete(String input)
            throws XuanException, IOException {
        if (!input.startsWith("delete ")
                || input.substring(7).trim().isEmpty()) {
            throw new XuanException(
                    "Please specify the task number to delete.");
        }

        int taskNumber = parser.getTaskNumber(input, 7);

        if (taskNumber < 1 || taskNumber > taskList.size()) {
            throw new XuanException(
                    "That task number does not exist.");
        }

        Task deletedTask = taskList.delete(taskNumber - 1);
        storage.saveTasks(taskList.getTasks());

        return ui.getDeletedTaskMessage(
                deletedTask, taskList.size());
    }

    /**
     * Adds a todo task and saves the updated task list.
     *
     * @param input the full todo command entered by the user
     * @return the message describing the added task
     * @throws XuanException if the task description is invalid
     * @throws IOException if the updated task list cannot be saved
     */
    private String handleTodo(String input)
            throws XuanException, IOException {
        String description = parser.getDescription(input, 4);

        Task task = new Todo(description);
        taskList.add(task);
        storage.saveTasks(taskList.getTasks());

        return ui.getAddedTaskMessage(task, taskList.size());
    }

    /**
     * Adds a deadline task and saves the updated task list.
     *
     * @param input the full deadline command entered by the user
     * @return the message describing the added task
     * @throws XuanException if the deadline command is invalid
     * @throws IOException if the updated task list cannot be saved
     */
    private String handleDeadline(String input)
            throws XuanException, IOException {
        String description = parser.getDeadlineDescription(input);
        LocalDate by = parser.getDeadlineDate(input);

        Task task = new Deadline(description, by);
        taskList.add(task);
        storage.saveTasks(taskList.getTasks());

        return ui.getAddedTaskMessage(task, taskList.size());
    }

    /**
     * Adds an event task and saves the updated task list.
     *
     * @param input the full event command entered by the user
     * @return the message describing the added task
     * @throws XuanException if the event command is invalid
     * @throws IOException if the updated task list cannot be saved
     */
    private String handleEvent(String input)
            throws XuanException, IOException {
        String description = parser.getEventDescription(input);
        String from = parser.getEventFrom(input);
        String to = parser.getEventTo(input);

        Task task = new Event(description, from, to);
        taskList.add(task);
        storage.saveTasks(taskList.getTasks());

        return ui.getAddedTaskMessage(task, taskList.size());
    }

    /**
     * Starts the command-line version of the Xuan chatbot.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        Xuan xuan = new Xuan();
        xuan.run();
    }

    /**
     * Returns Xuan's greeting message.
     *
     * @return Xuan's greeting message
     */
    public String getGreeting() {
        return ui.getGreetingMessage();
    }

    /**
     * Runs the command-line interaction loop.
     */
    private void run() {
        ui.showBanner();
        ui.showGreeting();

        while (true) {
            String input = ui.readCommand();
            String response = getResponse(input);

            ui.showMessage(response);

            if (input.trim().equals("bye")) {
                break;
            }
        }

        ui.close();
    }

    /**
     * Returns the help message if the help command is valid.
     *
     * @param input the full help command entered by the user
     * @return the help message
     * @throws XuanException if the help command contains arguments
     */
    private String handleHelp(String input) throws XuanException {
        if (!input.trim().equals("help")) {
            throw new XuanException(
                    "The help command does not take any arguments.");
        }

        return ui.getHelpMessage();
    }

    /**
     * Returns Xuan's response to the given user input.
     *
     * @param input the user input
     * @return Xuan's response
     */
    public String getResponse(String input) {
        try {
            String command = parser.getCommandWord(input);
            if (command.equals("bye")) {
                parser.validateNoArguments(input, "bye");
                return ui.getByeMessage();
            } else if (command.equals("help")) {
                return handleHelp(input);
            } else if (command.equals("list")) {
                parser.validateNoArguments(input, "list");
                return ui.getTaskListMessage(taskList);
            } else if (command.equals("find")) {
                return handleFind(input);
            } else if (command.equals("finddate")) {
                return handleFindDate(input);
            } else if (command.equals("mark")) {
                return handleMark(input);
            } else if (command.equals("unmark")) {
                return handleUnmark(input);
            } else if (command.equals("delete")) {
                return handleDelete(input);
            } else if (command.equals("todo")) {
                return handleTodo(input);
            } else if (command.equals("deadline")) {
                return handleDeadline(input);
            } else if (command.equals("event")) {
                return handleEvent(input);
            } else {
                throw new XuanException(
                        "Sorry, I don't understand that command.");
            }
        } catch (XuanException e) {
            return ui.getErrorMessage(e.getMessage());
        } catch (IOException e) {
            return ui.getErrorMessage(
                    "Sorry, I couldn't save the tasks.");
        }
    }
}
