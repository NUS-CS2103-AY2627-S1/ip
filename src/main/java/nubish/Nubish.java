package nubish;

import java.io.IOException;
import java.util.Scanner;

import nubish.utils.NubishException;
import nubish.utils.Parser;
import nubish.utils.Storage;
import nubish.utils.TaskList;
import nubish.utils.UI;

/**
 * Entry point and coordinator for the Nubish task manager application.
 */
public class Nubish {
    private static final String DEFAULT_FILEPATH = "./nubish.txt";

    private final Storage storage;
    private final TaskList taskList;
    private final UI ui;
    private final Parser parser;
    private boolean hasLoadedTasks;

    /**
     * Creates a Nubish application that saves and loads tasks from the given file.
     *
     * @param filepath path to the save file used by storage
     */
    public Nubish(String filepath) {
        this.ui = new UI();
        this.taskList = new TaskList();
        this.storage = new Storage(filepath, this.taskList);
        this.parser = new Parser(this.storage, this.ui, this.taskList);
    }

    /**
     * Creates a Nubish application using the default save file.
     */
    public Nubish() {
        this(DEFAULT_FILEPATH);
    }

    /**
     * Starts the command loop, loads saved tasks, and saves tasks before exiting.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);

        loadTasks();
        ui.greet();
        ui.printReminders(taskList.remind().toString());
        String input = scanner.nextLine();

        while (parser.parse(input)) {
            input = scanner.nextLine();
        }

        saveTasks();
    }

    /**
     * Loads saved tasks and returns the initial greeting.
     *
     * @return greeting message
     */
    public String start() {
        loadTasks();
        if (ui.isLastResponseError()) {
            return ui.getLastResponse() + "\n\n" + ui.getGreeting();
        }
        return ui.getGreeting();
    }

    /**
     * Pulls out reminders if there are any.
     *
     * @return greeting message
     */
    public String remind() {
        return ui.getReminders(taskList.remind().toString());
    }

    /**
     * Returns the current formatted task list.
     *
     * @return formatted task list
     */
    public String getTaskList() {
        return taskList.toString();
    }

    /**
     * Handles one user command and returns the response produced by Nubish.
     *
     * @param input user command
     * @return response produced after parsing the command
     */
    public String getResponse(String input) {
        boolean shouldContinue = parser.parse(input);
        if (!shouldContinue) {
            saveTasks();
        }
        return ui.getLastResponse();
    }

    /**
     * Returns whether the latest response should be shown as an error.
     *
     * @return true if the latest response is an error response
     */
    public boolean isLastResponseError() {
        return ui.isLastResponseError();
    }

    /**
     * Saves the current task list to disk.
     */
    public void save() {
        saveTasks();
    }

    /**
     * Launches the Nubish application.
     *
     * @param args command line arguments, currently unused
     */
    public static void main(String[] args) {
        new Nubish(DEFAULT_FILEPATH).run();
    }

    private void loadTasks() {
        if (hasLoadedTasks) {
            return;
        }

        try {
            storage.load();
            hasLoadedTasks = true;
        } catch (IOException | NubishException e) {
            hasLoadedTasks = true;
            ui.showError(String.format("OOPs seems like there was an error loading your data: %s", e.getMessage()));
        }
    }

    private void saveTasks() {
        try {
            storage.save();
        } catch (IOException e) {
            System.out.printf("OOPs seems like there was an error saving your data: %s%n", e.getMessage());
        }
    }
}
