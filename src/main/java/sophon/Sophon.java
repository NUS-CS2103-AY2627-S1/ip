package sophon;

import java.io.IOException;

import sophon.exception.SophonException;
import sophon.logic.Command;
import sophon.logic.Parser;
import sophon.model.Task;
import sophon.model.TaskList;
import sophon.storage.Storage;
import sophon.ui.Ui;

/**
 * Entry point for the Sophon chatbot.
 */
public class Sophon {
    private final Storage storage;
    private final TaskList tasks;
    private final Ui ui;
    private final String startupWarning;

    /**
     * Creates a Sophon chatbot that saves tasks at the given path.
     *
     * @param first first part of the save file path.
     * @param more remaining parts of the save file path.
     */
    public Sophon(String first, String... more) {
        ui = new Ui();
        storage = new Storage(first, more);

        TaskList loadedTasks;
        String warning = null;
        try {
            loadedTasks = storage.loadTasks();
        } catch (IOException e) {
            warning = "I could not read the saved tasks.";
            loadedTasks = new TaskList();
        } catch (SophonException e) {
            warning = e.getMessage();
            loadedTasks = new TaskList();
        }

        tasks = loadedTasks;
        startupWarning = warning;
    }

    private String markTask(int taskIndex) throws SophonException, IOException {
        if (!tasks.isValidIndex(taskIndex)) {
            throw new SophonException("No task exists at that number.");
        }

        tasks.get(taskIndex).markAsDone();
        storage.saveTasks(tasks);
        return "Acknowledged. This task is now complete:\n"
                + "  " + tasks.get(taskIndex);
    }

    private String unmarkTask(int taskIndex) throws SophonException, IOException {
        if (!tasks.isValidIndex(taskIndex)) {
            throw new SophonException("No task exists at that number.");
        }

        tasks.get(taskIndex).markAsNotDone();
        storage.saveTasks(tasks);
        return "Reverted. This task is once again incomplete:\n"
                + "  " + tasks.get(taskIndex);
    }

    private String deleteTask(int taskIndex) throws SophonException, IOException {
        if (!tasks.isValidIndex(taskIndex)) {
            throw new SophonException("No task exists at that number.");
        }

        Task removedTask = tasks.remove(taskIndex);
        storage.saveTasks(tasks);
        return "Removed. This task is no longer under observation:\n"
                + "  " + removedTask + "\n"
                + tasks.size() + " tasks remain under observation.";
    }

    /**
     * Generates a response for the user's chat message.
     *
     * @param input user input.
     * @return response to show in the chat window.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input.trim());

            switch (command.getType()) {
                case BYE:
                    return ui.getByeMessage();
                case LIST:
                    return ui.getTaskList(tasks);
                case FIND:
                    return ui.getMatchingTasks(tasks.find(command.getKeyword()));
                case ADD_TODO:
                    return addTask(command.getTask(), "task");
                case ADD_DEADLINE:
                    return addTask(command.getTask(), "deadline");
                case ADD_EVENT:
                    return addTask(command.getTask(), "event");
                case MARK:
                    return markTask(command.getTaskIndex());
                case UNMARK:
                    return unmarkTask(command.getTaskIndex());
                case DELETE:
                    return deleteTask(command.getTaskIndex());
                case UNKNOWN:
                default:
                    return "Your message has been observed.\n"
                            + "Its meaning, however, remains unknown.";
            }
        } catch (SophonException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "I could not save the task list.";
        }
    }

    private String addTask(Task task, String taskKind) throws IOException {
        tasks.add(task);
        storage.saveTasks(tasks);
        return "Recorded. A new " + taskKind + " has entered observation:\n"
                + "  " + task + "\n"
                + tasks.size() + " tasks are currently under observation.";
    }

    public String getGreeting() {
        return ui.getGreeting(startupWarning);
    }
}
