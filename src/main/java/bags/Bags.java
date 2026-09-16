package bags;

import bags.exception.BagsException;
import bags.parser.Command;
import bags.parser.Parser;
import bags.storage.Storage;
import bags.task.Deadline;
import bags.task.Event;
import bags.task.Task;
import bags.task.TaskList;
import bags.task.TaskType;
import bags.task.Todo;

/**
 * Main chatbot class for the Bags task management application.
 *
 * <p>Coordinates command parsing, task management, and storage components.</p>
 */
public class Bags {

    private static final String DEFAULT_STORAGE_PATH = "./data/Bags.txt";

    private final Storage storage;
    private final Parser parser;
    private TaskList tasks;
    private String startupMessage;

    private boolean isAddingTaskMode;
    private boolean isEchoMode;
    private boolean isEditingMode;
    private String editingCommand;

    /**
     * Creates a Bags application and loads existing tasks from storage.
     */
    public Bags() {
        this(DEFAULT_STORAGE_PATH);
    }

    /**
     * Creates a Bags application that stores tasks at the supplied path.
     *
     * @param storagePath location of the task save file
     */
    Bags(String storagePath) {
        storage = new Storage(storagePath);
        parser = new Parser();

        try {
            tasks = new TaskList(storage.loadTasks(parser));
            if (storage.isSaveFileMissing()) {
                startupMessage = "No saved task file was found. Starting a new session. Please wait for a moment!";
            }
        } catch (BagsException e) {
            tasks = new TaskList();
            startupMessage = "Unable to load saved tasks. Starting a new session. "
                    + "Please hold on for a while!" + e.getMessage();
        }

    }

    /**
     * Processes a command entered by the user.
     *
     * @param input the command entered by the user
     * @return the response that should be displayed to the user
     * @throws BagsException if the command is invalid
     */
    public String processCommand(String input) throws BagsException {
        if (input == null) {
            throw new BagsException("📭 Your message seems to have slipped out of the bag—"
                    + "try typing a command.");
        }

        String trimmedInput = input.trim();

        if (isEchoMode) {
            return processEchoMode(trimmedInput);
        }

        if (trimmedInput.isEmpty()) {
            throw new BagsException("📭 Your message seems to have slipped out of the bag—"
                    + "try typing a command.");
        }

        if (isAddingTaskMode) {
            return processAddingTaskMode(trimmedInput);
        }

        if (isEditingMode) {
            return processEditingTaskMode(trimmedInput);
        }

        if (parser.parseTaskType(trimmedInput) != null) {
            throw new BagsException(
                    "Please enter task-entry mode first by typing add task.");
        }

        Command command = parser.parseCommand(trimmedInput);
        assert command != null : "Parsed command must not be null";

        String response = executeCommand(command, trimmedInput);
        assert response != null : "Command execution response should not be null";

        return response;
    }

    /**
     * Returns the startup alert for a save-file problem, if one occurred.
     */
    public String getStartupMessage() {
        return startupMessage;
    }

    private String executeCommand(Command command, String input) throws BagsException {
        assert command != null : "Command to execute cannot be null";
        assert !input.isEmpty() : "Input string to executeCommand should not be empty";

        if (command == Command.ADD_TASK) {
            return startAddingTask();
        } else if (command == Command.LIST) {
            return listItems();
        } else if (command == Command.MARK) {
            return markDone(input);
        } else if (command == Command.UNMARK) {
            return unmarkDone(input);
        } else if (command == Command.ECHO) {
            return startEchoMode();
        } else if (command == Command.DELETE) {
            return deleteTask(input);
        } else if (command == Command.SEARCH) {
            return searchTasks(input);
        } else if (command == Command.EDIT) {
            return startEditingTask(input);
        } else if (command == Command.BYE) {
            saveTasks();
            return "See you soon! :)";
        }

        throw new BagsException("🤔 I don't know what you mean. Try again?");
    }

    private String startAddingTask() {
        isAddingTaskMode = true;
        return """
                📥 What would you like me to pack into your bag?

                Enter a task using one of these formats:
                • todo <task name>
                • deadline <task name> /by YYYY-MM-DD HH:MM
                • event <task name> /from YYYY-MM-DD HH:MM /to YYYY-MM-DD HH:MM

                Type exit to cancel.
                """;
    }

    private String startEchoMode() {
        isEchoMode = true;
        return "🗣 From now on, I'll echo what you put in the bag. To exit, enter exit.";
    }

    private String startEditingTask(String input) throws BagsException {
        Task task = tasks.getTask(input);

        isEditingMode = true;
        editingCommand = input;

        return """
                ✍️ Let's repack this task:
                %s

                Enter a replacement task using the same task type:
                • todo <task name>
                • deadline <task name> /by YYYY-MM-DD HH:MM
                • event <task name> /from YYYY-MM-DD HH:MM /to YYYY-MM-DD HH:MM

                Type exit to cancel.
                """.formatted(task);
    }

    private String processAddingTaskMode(String input) throws BagsException {
        assert isAddingTaskMode : "Should only process adding task mode when flag is true";
        if (input.equals("exit")) {
            isAddingTaskMode = false;
            return "Okay, I've closed the bag for now.";
        }

        TaskType taskType = parser.parseTaskType(input);
        if (taskType == null) {
            throw new BagsException("I can't pack that task type. Enter a todo, deadline, or event task, "
                    + "or type exit to cancel.");
        }

        return addTask(input);
    }

    private String processEditingTaskMode(String input) throws BagsException {
        assert isEditingMode : "Should only process editing task mode when flag is true";
        assert editingCommand != null : "Edit command must exist while editing a task";

        if (input.equals("exit")) {
            isEditingMode = false;
            editingCommand = null;
            return "Okay, I've closed the bag without changing the task.";
        }

        TaskType taskType = parser.parseTaskType(input);
        if (taskType == null) {
            throw new BagsException("Hmm, I need a todo, deadline, or event task to repack. Enter exit to cancel.");
        }

        Task replacementTask = createTask(taskType, input);
        Task updatedTask = tasks.edit(editingCommand, replacementTask);

        isEditingMode = false;
        editingCommand = null;
        saveTasks();

        return "✅ All repacked! Here's the updated task:\n" + updatedTask;
    }

    private String processEchoMode(String input) throws BagsException {
        assert isEchoMode : "Should only process echo mode when flag is true";
        if (input.equals("exit")) {
            isEchoMode = false;
            return "Okay, I've closed the bag for now.";
        }
        if (input.isEmpty()) {
            throw new BagsException(" I can't echo silence. Try putting a word in the bag!");
        }
        return input;
    }

    private String addTask(String input) throws BagsException {
        assert input != null && !input.isEmpty() : "Task input to add must not be null or empty";

        int initialSize = tasks.getSize();
        TaskType type = parser.parseTaskType(input);
        assert type != null : "Parsed Task type must not be null";

        Task task = createTask(type, input);

        tasks.add(task);
        assert tasks.getSize() == initialSize + 1 : "TaskList size should increase by 1 after addition";

        saveTasks();

        return "📥 Safely packed into your bag:\n"
                + task
                + "\n🎒 Your bag now holds "
                + tasks.getSize()
                + (tasks.getSize() == 1 ? " item.\n" : " items.\n")
                + "\nEnter another task or enter exit to leave editing mode.";
    }

    private Task createTask(TaskType type, String input) throws BagsException {
        assert type != null : "Task type cannot be null when creating task";
        assert input != null && !input.isEmpty() : "Input string cannot be empty when creating task";

        if (type == TaskType.TODO) {
            return Todo.createTask(input);
        } else if (type == TaskType.DEADLINE) {
            return Deadline.createTask(input);
        } else if (type == TaskType.EVENT) {
            return Event.createTask(input);
        }

        throw new BagsException("⚠️ I can only pack todo, deadline, or event tasks.");
    }

    private String listItems() throws BagsException {
        if (tasks.isEmpty()) {
            throw new BagsException("📭 Your bag is empty. Wanna add a task?");
        }
        return tasks.toString();
    }

    private String markDone(String input) throws BagsException {
        Task task = tasks.markDone(input);
        assert task != null : "Task returned after markDone should not be null";
        saveTasks();
        return "✅ Nice work! I've marked this task as done and tied it up in your bag:\n" + task;
    }

    private String unmarkDone(String input) throws BagsException {
        Task task = tasks.markUndone(input);
        assert task != null : "Task returned after markUndone should not be null";
        saveTasks();
        return "❌ No worries! I've marked this task as undone and taken it back out for you:\n" + task;
    }

    private String deleteTask(String input) throws BagsException {
        int initialSize = tasks.getSize();
        Task task = tasks.delete(input);

        assert task != null : "Deleted task object should not be null";
        assert tasks.getSize() == initialSize - 1 : "TaskList size should decrease by 1 after deletion";

        saveTasks();
        return "🗑 Done! I've removed this from your bag:\n"
                + task
                + "\n🎒 Your bag now holds "
                + tasks.getSize()
                + (tasks.getSize() == 1 ? " item." : " items.");
    }

    private String searchTasks(String input) throws BagsException {
        assert input != null : "Search input should not be null";
        return tasks.search(input);
    }

    /**
     * Saves all current tasks to the storage file.
     */
    private void saveTasks() throws BagsException {
        assert storage != null : "Storage component must exist to save tasks";
        assert tasks != null : "TaskList must exist to retrieve records for saving";
        storage.saveRecords(tasks.saveRecords());
    }
}
