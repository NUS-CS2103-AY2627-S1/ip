package korvus.bot;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.function.Consumer;

import korvus.Config;
import korvus.storage.Storage;
import korvus.storage.StorageConflictException;
import korvus.storage.StorageParser;
import korvus.tasks.InvalidTaskException;
import korvus.tasks.Tasklist;
import korvus.ui.UI;
import korvus.utils.CommandParser;
import korvus.utils.DateTimeParser;

/**
 * The main class containing the logic for Korvus.
 */
public class Korvus {
    protected UI ui;
    protected Config config;
    protected boolean isActive;

    private CommandParser parser;
    private HashMap<String, Consumer<String>> commandMap;
    private DateTimeParser dateTimeParser;
    private Tasklist tasklist;
    private StorageParser<Tasklist> tasklistParser;
    private Storage storage;

    /**
     * Returns an instance of the Korvus object, with the provided parameters.
     *
     * @param ui UI tagged to the Korvus instance.
     * @param storagePath Relative path to the directory with files for Korvus to use.
     */
    public Korvus(UI ui, String storagePath) {
        this.ui = ui;
        this.storage = new Storage(storagePath);
        this.parser = new CommandParser();
        this.commandMap = generateCommandMapping();
    }

    /**
     * Starts the Korvus bot.
     */
    public void initialise() {
        if (isActive) {
            throw new RuntimeException("Already Running!");
        } else {
            isActive = true;
        }

        this.loadFromConfigs();
        this.createStorageParsers();

        this.tasklist = new Tasklist(this.dateTimeParser);

        this.readFromStorage();
        this.greet();
    }

    /**
     * Loads configuration information from file system.
     */
    private void loadFromConfigs() {
        ui.say("Loading config file...");
        try {
            this.config = this.storage.readConfigFile();
            ui.say("Loaded config file!");
        } catch (FileNotFoundException e) {
            this.config = Config.generateNewConfig();
            ui.say("Failed to find config file, using default configurations~");
        }

        // Reading config file
        this.dateTimeParser = new DateTimeParser(
                DateTimeFormatter.ofPattern(this.config.getValue("datetime_format"))
        );
    }

    /**
     * Initialises the StorageParsers to be used.
     */
    private void createStorageParsers() {
        try {
            this.tasklistParser = new StorageParser<Tasklist>(
                    this.storage,
                    this.config.getValue("tasklist_file_path")
            );
        } catch (StorageConflictException e) {
            ui.say(String.format("""
                    Warning: Failed to connect parser to tasklist!
                    Tasklist will be empty and cannot be saved to storage.
                    Error: %s""", e.getMessage()));
        }
    }

    /**
     * Reads data from file system
     */
    private void readFromStorage() {
        try {
            boolean hasErrors = this.tasklist.readFromParser(this.tasklistParser);
            if (hasErrors) {
                ui.say("Warning: Failed to read some tasks! Tasklist may be missing tasks.");
            }
        } catch (IOException e) {
            ui.say("Warning: Failed tasklist file! Tasklist will be empty.");
        }
        ui.divider();
    }

    /**
     * Reads a message from the user and processes it.
     *
     * @param msg Message that the user inputted.
     */
    public void processUserMessage(String msg) {
        String[] cmd = parser.parse(msg);
        String cmdName = cmd[0];
        String cmdArgs = cmd[1];

        commandMap.get(cmdName).accept(cmdArgs);
    }

    /**
     * Writes a greeting to the UI.
     */
    private void greet() {
        assert isActive = true;

        ui.divider();
        ui.say("Nice to meet you!");
        ui.say("I am caw-lled Korvus, your personal chatbot for keeping track of shiny things.");
        ui.say("To get a list of cawmands, tweet 'help'!");
    }

    /**
     * Writes a list of functions that the bot can do, with detailed explanations
     */
    private void help(String input) {
        assert isActive = true;

        //CHECKSTYLE.OFF: Regexp
        ui.say("Here are a list of cawmands!\nFor any invalid cawmands, I will simply parrot them back~\n");
        ui.say("list[s], task[s] - View your tasks.");
        ui.say("""
            add task <task> - Adds a task with name <task>.
            Use the flags -t for a ToDo, -d for a Deadline and -e for an Event.
             -t <task> : Adds a ToDo Task.
             -d <task> // <deadline> : Adds a Deadline Task with an (optional) deadline.
             -e <task> // <start> // <end> : Adds an Event Task with (optional) duration.""");
        ui.say("add todo <task> - Adds a ToDo Task.");
        ui.say("add deadline <task> // <deadline>"
                + " - Adds a Deadline Task with an (optional) deadline.");
        ui.say("add event <task> // <start> // <end>"
                + " - Adds an Event Task with (optional) duration.");
        ui.say("""
            do task <q_task> - Marks task with info <q_task> as done.
            
            <q_task> is first assumed to be the task id, but if invalid then assumed to be task name.
            If there are duplicate tasks with the same name, it will only use the first one.""");
        ui.say("""
            undo task <q_task> - Marks task with info <q_task> as not done.
            
            <q_task> is first assumed to be the task id, but if invalid then assumed to be task name.
            If there are duplicate tasks with the same name, it will only use the first one.""");
        ui.say("""
            delete task <q_task> - Removes task with info <q_task> from the tasklist.
            
            <q_task> is first assumed to be the task id, but if invalid then assumed to be task name.
            If there are duplicate tasks with the same name, it will only use the first one.""");
        ui.say("""
            find task <f_task> - Finds all tasks with <f_task> as part of the name from the tasklist.
            
            <f_task> can be any part of the name, but cannot be separated in any ways.
            There may be regex that you may be able to apply...""");
        ui.say("undo last - Undoes the previous action. Note that this is limited to do, undo, add, delete.");
        ui.say("bye - Closes the program (goodbye...)");
        ui.say("help - Hi there! I'm here to help!");
        //CHECKSTYLE.ON: Regexp
    }

    /**
     * Stops the bot.
     *
     * @param forced If null, forces the bot to stop regardless whether its data can be saved to Storage.
     */
    private void goodbye(String forced) {
        assert isActive = true;

        ui.say("Saving session information to disk...");

        // Only runs when user says bye
        try {
            if (tasklistParser != null) {
                tasklistParser.writeStorage(tasklist);
            }
            storage.saveConfigFile(config);
        } catch (IOException e) {
            if (forced != null) {
                ui.say(String.format("Failed to save some files!\n%s", e.getMessage()));
                ui.say("Aborting exit... If you want to force exit, tweet \"goodbye -f\".");
                return;
            }
        }
        isActive = false;

        ui.say("Goodbye! Eagle to see you again!");
    }

    /**
     * Adds a task to the bot.
     *
     * @param task String containing data of Task to be saved.
     */
    private void addTask(String task) {
        assert isActive = true;

        try {
            String newTask = tasklist.addTask(task);
            ui.say(String.format("Added task:\n%d. %s", tasklist.getSize(), newTask));
            ui.divider();
        } catch (InvalidTaskException e) {
            ui.say("An error occurred while creating task!");
            ui.say(e.getMessage());
            ui.divider();
        }
    }

    /**
     * Marks a task in the bot as done.
     *
     * @param sTask String containing data of Task to be marked as done.
     */
    private void doTask(String sTask) {
        assert isActive = true;

        try {
            String taskString;
            if (sTask.matches("\\d+") && Integer.parseInt(sTask) - 1 < tasklist.getSize()) {
                taskString = tasklist.doTask(Integer.parseInt(sTask) - 1);
            } else {
                taskString = tasklist.doTask(sTask);
            }
            ui.say(String.format("Success! Task has been marked done!\n%s", taskString));
        } catch (InvalidTaskException e) {
            ui.say(String.format("Oh no... Failed to mark task: %s", e.getMessage()));
        }
    }

    /**
     * Marks a task in the bot as not done.
     *
     * @param sTask String containing data of Task to be marked as not done.
     */
    private void undoTask(String sTask) {
        assert isActive = true;

        try {
            String taskString;
            if (sTask.matches("\\d+") && Integer.parseInt(sTask) - 1 < tasklist.getSize()) {
                taskString = tasklist.undoTask(Integer.parseInt(sTask) - 1);
            } else {
                taskString = tasklist.undoTask(sTask);
            }
            ui.say(String.format("Success! Task has been unmarked!\n%s", taskString));
        } catch (InvalidTaskException e) {
            ui.say(String.format("Oh no... Failed to unmark task: %s", e.getMessage()));
        }
    }

    /**
     * Deletes a task in the bot.
     *
     * @param sTask String containing data of Task to be deleted.
     */
    private void deleteTask(String sTask) {
        assert isActive = true;

        try {
            String taskString;
            if (sTask.matches("\\d+") && Integer.parseInt(sTask) - 1 < tasklist.getSize()) {
                taskString = tasklist.deleteTask(Integer.parseInt(sTask) - 1);
            } else {
                taskString = tasklist.deleteTask(sTask);
            }
            ui.say(String.format("""
                    Success! Task (%s) has been deleted!
                    Take note that the other tasks may have new indexes now.
                    Do tweet "list" or "task" to view your updated tasklist.""", taskString));
        } catch (InvalidTaskException e) {
            ui.say(String.format("Oh no... Failed to delete task: %s", e.getMessage()));
        }
    }

    /**
     * Finds tasks in the bot given a query.
     *
     * @param input String containing keyword to search.
     */
    private void findTasks(String input) {
        assert isActive = true;

        String output = tasklist.findTask(input);
        if (output.isEmpty()) {
            ui.say("No task matches your query: " + input);
        } else {
            ui.say("There are task(s) matching your query!");
            ui.say(output);
        }
    }

    /**
     * Undoes the previous task command.
     */
    private void undoAction(String input) {
        assert isActive = true;

        try {
            String output = tasklist.undoTaskOperation();

            ui.say(String.format("Sucessfully undid action for:\n%s", output));
        } catch (InvalidTaskException e) {
            ui.say(e.getMessage());
            ui.say("Failed to undo task operation :(");
        }
    }

    /**
     * Prints all the tasks in the bot.
     */
    private void printTasks(String input) {
        assert isActive = true;

        if (tasklist.getSize() == 0) {
            ui.say("You have no tasks! Caw-ngratulations!");
            return;
        }
        ui.say("Here are your tasks!");
        ui.say(tasklist.toString());
    }

    /**
     * Returns a HashMap of certain keywords to functions.
     * This allows the bot to offload user input parsing into another file.
     *
     * @return HashMap containing mappings of String to Consumers in the bot.
     */
    private HashMap<String, Consumer<String>> generateCommandMapping() {
        HashMap<String, Consumer<String>> commandMap = new HashMap<>();
        commandMap.put("bye", this::goodbye);
        commandMap.put("help", this::help);
        commandMap.put("list", this::printTasks);
        commandMap.put("add", this::addTask);
        commandMap.put("do", this::doTask);
        commandMap.put("undo", this::undoTask);
        commandMap.put("del", this::deleteTask);
        commandMap.put("find", this::findTasks);
        commandMap.put("backtrack", this::undoAction);
        commandMap.put("echo", this.ui::say);

        return commandMap;
    }
}
