package doe;

import java.time.format.DateTimeParseException;
import java.util.List;


/**
 * Entry point for the doe chatbot application.
 */

public class Doe {
    private enum GuiState {
        MAIN, TODO_MENU, ADD_TYPE, ADD_DESCRIPTION, ADD_DATE, REMOVE, MARK, UNMARK, FIND
    }

    private Storage storage;
    private TaskList tasks;
    private UserInterface ui;
    private GuiState guiState = GuiState.MAIN;
    private Parser.TaskMenu pendingTaskType;
    private String pendingDescription;
    private boolean lastResponseWasWarning;

    /**
     * Initialises the required chatbot components such as UI, storage, and tasks.
     *
     * @param filePath The path to the next file used for storing tasks.
     */
    public Doe(String filePath) {
        ui = new UserInterface();
        storage = new Storage(filePath);
        tasks = new TaskList(storage.load());
    }

    /**
     * Returns the first message shown when the graphical interface opens.
     *
     * @return Doe's greeting and the available main-menu commands.
     */
    public String getWelcomeMessage() {
        return "hello! i'm doe :).\nwhat can i do for you?\n"
                + "1. neigh\n2. meow\n3. list\n4. todo\n\ntype \"bye\" to exit";
    }

    /**
     * Returns a recoverable startup problem separately from the normal welcome message.
     *
     * @return A user-facing warning with recovery guidance, or an empty string when loading succeeded.
     */
    public String getStartupWarning() {
        return storage.getLoadWarning();
    }

    /**
     * Processes one GUI submission while retaining the same multi-step menus as the console interface.
     * The GUI controller only presents this returned text; task parsing, mutation, and storage remain here.
     *
     * @param input One line submitted in the graphical interface.
     * @return Doe's response and, when needed, the next prompt.
     */
    public String getResponse(String input) {
        lastResponseWasWarning = false;
        try {
            return processResponse(input);
        } catch (IllegalArgumentException exception) {
            return "error: " + exception.getMessage();
        } catch (java.io.UncheckedIOException | IllegalStateException | SecurityException exception) {
            guiState = GuiState.MAIN;
            lastResponseWasWarning = true;
            if (!storage.getLoadWarning().isEmpty()) {
                return storage.getLoadWarning();
            }
            return "Doe could not save your latest change.\n"
                    + "The change is still in memory, but it has not been saved and will be lost when Doe closes.\n"
                    + "How to fix: check that the task-file folder is writable and has enough free space, "
                    + "then try again.\n"
                    + "Technical details: " + exception.getMessage();
        }
    }

    /** Returns whether the most recent GUI response is a warning that needs a notice bubble. */
    public boolean wasLastResponseWarning() {
        return lastResponseWasWarning;
    }

    /** Dispatches input while retaining the current prompt after validation failures. */
    private String processResponse(String input) {
        String cleanedInput = input.strip();
        switch (guiState) {
            case MAIN:
                return respondToMainMenu(cleanedInput);
            case TODO_MENU:
                return respondToTodoMenu(cleanedInput);
            case ADD_TYPE:
                return respondToAddType(cleanedInput);
            case ADD_DESCRIPTION:
                return acceptTaskDescription(cleanedInput);
            case ADD_DATE:
                return acceptTaskDate(cleanedInput);
            case REMOVE:
                return removeTask(cleanedInput);
            case MARK:
                return changeTaskStatus(cleanedInput, true);
            case UNMARK:
                return changeTaskStatus(cleanedInput, false);
            case FIND:
                guiState = GuiState.MAIN;
                return matchingTasksText(tasks.findTasks(cleanedInput)) + "\n\n" + getWelcomeMessage();
            default:
                throw new IllegalStateException("Unknown GUI state: " + guiState);
        }
    }

    private String respondToMainMenu(String input) {
        switch (Parser.MainMenu.fromString(input)) {
            case NEIGH:
                return ">> EURHGGGHHHH!! <<";
            case MEOW:
                return ">> MEOW! (=^･ω･^=)";
            case LIST:
                return "roles and responsibilities\n1. survive nus cs\n2. get a few internships\n"
                        + "3. work at mcdonalds\n4. retire as a manager (hopefully)";
            case TODO:
                guiState = GuiState.TODO_MENU;
                return todoMenuText();
            case BYE:
                return "bye. hope to see you again soon!";
            case UNKNOWN:
            default:
                return "horh\n\n" + getWelcomeMessage();
        }
    }

    private String respondToTodoMenu(String input) {
        switch (Parser.TodoMenu.fromString(input)) {
            case ADD:
                guiState = GuiState.ADD_TYPE;
                return "what type of task would you like to add?\n1. todo\n2. deadline\n3. event\n4. exit";
            case REMOVE:
                guiState = GuiState.REMOVE;
                return "what you want to remove?\n\n" + taskListText(tasks.getTasks());
            case SORT:
                sortTasks();
                guiState = GuiState.MAIN;
                return UserInterface.sortedTodoListText(tasks.getTasks()) + "\n\n" + getWelcomeMessage();
            case VIEW:
                guiState = GuiState.MAIN;
                return taskListText(tasks.getTasks()) + "\n\n" + getWelcomeMessage();
            case MARK:
                guiState = GuiState.MARK;
                return "to mark a task as done, enter its number from the list.\n\n"
                        + taskListText(tasks.getTasks());
            case UNMARK:
                guiState = GuiState.UNMARK;
                return "to mark a task as not done, enter its number from the list.\n\n"
                        + taskListText(tasks.getTasks());
            case FIND:
                guiState = GuiState.FIND;
                return "enter a keyword to search for:";
            case EXIT:
                guiState = GuiState.MAIN;
                return getWelcomeMessage();
            case UNKNOWN:
            default:
                return "that option is incorrect. please choose one of the todo options below.\n\n" + todoMenuText();
        }
    }

    private String respondToAddType(String input) {
        pendingTaskType = Parser.TaskMenu.fromString(input);
        switch (pendingTaskType) {
            case TODO:
            case DEADLINE:
            case EVENT:
                guiState = GuiState.ADD_DESCRIPTION;
                return "what you want to add?";
            case EXIT:
                guiState = GuiState.TODO_MENU;
                return todoMenuText();
            case UNKNOWN:
            default:
                return "that option is incorrect. please choose 1, 2, 3, todo, deadline, or event.";
        }
    }

    private String acceptTaskDescription(String input) {
        assert pendingTaskType == Parser.TaskMenu.TODO
                || pendingTaskType == Parser.TaskMenu.DEADLINE
                || pendingTaskType == Parser.TaskMenu.EVENT
                : "A task type must be selected before its description is accepted";
        if (containsStorageDelimiter(input)) {
            return "error: input cannot contain '|' character.\n\nwhat you want to add?";
        }
        pendingDescription = Task.validateDescription(input);
        if (pendingTaskType == Parser.TaskMenu.TODO) {
            tasks.addTask(new Todo(pendingDescription));
            return finishAddingTask();
        }

        guiState = GuiState.ADD_DATE;
        if (pendingTaskType == Parser.TaskMenu.DEADLINE) {
            return "input deadline in format dd-mm-yyyy hhmm (e.g. 17-07-2004 1800):";
        }
        return "input event time in format dd-mm-yyyy hhmm (e.g. 17-07-2004 1100):";
    }

    private String acceptTaskDate(String input) {
        assert pendingTaskType == Parser.TaskMenu.DEADLINE || pendingTaskType == Parser.TaskMenu.EVENT
                : "Only dated task types may request a date";
        assert pendingDescription != null : "A task description must be accepted before its date";
        if (containsStorageDelimiter(input)) {
            return "error: input cannot contain '|' character.";
        }
        try {
            if (pendingTaskType == Parser.TaskMenu.DEADLINE) {
                tasks.addTask(new Deadline(pendingDescription, input));
            } else {
                tasks.addTask(new Event(pendingDescription, input));
            }
            return finishAddingTask();
        } catch (DateTimeParseException exception) {
            return "invalid date format. please use dd-mm-yyyy hhmm (e.g. 17-07-2004 1800).";
        }
    }

    private String finishAddingTask() {
        assert pendingTaskType != null : "A task type must remain available until the add operation finishes";
        assert pendingDescription != null : "A task description must remain available until the add operation finishes";
        storage.save(tasks.getTasks());
        guiState = GuiState.MAIN;
        pendingTaskType = null;
        pendingDescription = null;
        return "item saved successfully!\n\n" + getWelcomeMessage();
    }

    private String removeTask(String input) {
        int index = tasks.findTaskIndex(input);
        guiState = GuiState.MAIN;
        if (index < 0) {
            return "could not find a task with that name or number. please try again.\n\n"
                    + getWelcomeMessage();
        }
        assert index < tasks.size() : "A successful task lookup must return an existing index";
        Task removedTask = tasks.removeTask(index);
        storage.save(tasks.getTasks());
        return "item removed successfully!\n" + removedTask + "\n\n" + getWelcomeMessage();
    }

    private String changeTaskStatus(String input, boolean markAsDone) {
        guiState = GuiState.MAIN;
        if (tasks.size() == 0) {
            return "there are no tasks to mark yet. add a task first.\n\n" + getWelcomeMessage();
        }

        int index;
        try {
            index = Integer.parseInt(input) - 1;
        } catch (NumberFormatException exception) {
            index = -1;
        }
        if (index < 0 || index >= tasks.size()) {
            return "that task number is incorrect. please enter a number from 1 to " + tasks.size()
                    + ".\n\n" + getWelcomeMessage();
        }

        assert index >= 0 && index < tasks.size() : "A validated task number must map to an existing task";
        Task task = tasks.getTask(index);
        if (markAsDone) {
            task.markAsDone();
        } else {
            task.markAsNotDone();
        }
        storage.save(tasks.getTasks());
        String message = markAsDone ? "nice! i've marked this task as done:\n[x] "
                : "ok i've marked this task as not done yet:\n[] ";
        return message + task.getDescription() + "\n\n" + taskListText(tasks.getTasks())
                + "\n\n" + getWelcomeMessage();
    }

    private static boolean containsStorageDelimiter(String input) {
        return input.contains("|");
    }

    private static String todoMenuText() {
        return "modify todo list\n1. add\n2. remove\n3. view\n4. sort\n5. mark\n6. unmark\n7. find\n8. exit";
    }

    private static String taskListText(List<Task> taskItems) {
        StringBuilder result = new StringBuilder("here are the tasks in your list:");
        for (int i = 0; i < taskItems.size(); i++) {
            result.append('\n').append(i + 1).append(". ").append(taskItems.get(i));
        }
        return result.toString();
    }

    private String matchingTasksText(List<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return "no matching tasks found.";
        }
        StringBuilder result = new StringBuilder("Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            result.append('\n').append(tasks.getTasks().indexOf(matchingTasks.get(i)) + 1)
                    .append(". ").append(matchingTasks.get(i));
        }
        return result.toString();
    }

    /**
     * Starts the main logical loop of doe bot to continuously read and execute commands.
     */
    public void run() {
        if (!storage.getLoadWarning().isEmpty()) {
            System.out.println("warning: " + storage.getLoadWarning());
        }
        try {
            runConsole();
        } catch (java.util.NoSuchElementException exception) {
            ui.printBye();
        } finally {
            ui.closeScanner();
        }
    }

    /** Runs console commands, reporting recoverable input and persistence errors at the menu boundary. */
    private void runConsole() {
        ui.printBanner(); // starting point

        // main loop
        while (true) {
            String input = ui.readCommand();

            Parser.MainMenu command = Parser.MainMenu.fromString(input); // parser interprets input using enums
            switch (command) {
                case NEIGH:
                    ui.printNeigh();
                    break;
                case MEOW:
                    ui.printMeow();
                    break;
                case LIST:
                    ui.printList();
                    break;
                case TODO:
                    try {
                        runTodoMenu();
                    } catch (IllegalArgumentException exception) {
                        System.out.println("error: " + exception.getMessage());
                        ui.printBanner();
                    } catch (java.io.UncheckedIOException | IllegalStateException | SecurityException exception) {
                        System.out.println("changes are in memory only and have not been saved. "
                                + exception.getMessage());
                        ui.printBanner();
                    }
                    break;
                case BYE:
                    ui.printBye();
                    ui.closeScanner();
                    return; // terminates application by exiting run

                case UNKNOWN:
                default:
                    UserInterface.printUnexpectedInputMessage("horh");
            }
        }
    }

    /**
     * Runs the console todo menu until an action returns the user to the main menu.
     */
    private void runTodoMenu() {
        while (true) {
            ui.printTodoMenu();
            Parser.TodoMenu command = Parser.TodoMenu.fromString(ui.readCommand());

            switch (command) {
                case ADD:
                    if (addTaskFromConsole()) {
                        saveTasksAndReturnToMainMenu();
                        return;
                    }
                    break;
                case REMOVE:
                    removeTaskFromConsole();
                    return;
                case SORT:
                    sortTasks();
                    ui.printSortedTodoList(tasks.getTasks());
                    ui.printBanner();
                    return;
                case VIEW:
                    ui.printTodoList(tasks.getTasks());
                    ui.printBanner();
                    return;
                case MARK:
                    if (changeTaskStatusFromConsole(true)) {
                        return;
                    }
                    break;
                case UNMARK:
                    if (changeTaskStatusFromConsole(false)) {
                        return;
                    }
                    break;
                case FIND:
                    findTasksFromConsole();
                    return;
                case EXIT:
                    ui.printBanner();
                    return;
                case UNKNOWN:
                    UserInterface.printUnexpectedInputMessage("\n|"
                            + "that option is incorrect. please choose one of the todo options below.");
                    break;
                default:
                    throw new IllegalStateException("Unknown todo menu command: " + command);
            }
        }
    }

    /**
     * Reads task details from the console and adds a valid task.
     *
     * @return true if a task was added, or false if the user exited the add menu
     */
    private boolean addTaskFromConsole() {
        while (true) {
            ui.printAddMenu();
            Parser.TaskMenu type = Parser.TaskMenu.fromString(ui.readCommand());

            switch (type) {
                case TODO:
                    if (addTodoFromConsole()) {
                        return true;
                    }
                    break;
                case DEADLINE:
                case EVENT:
                    if (addDatedTaskFromConsole(type)) {
                        return true;
                    }
                    break;
                case EXIT:
                    return false;
                case UNKNOWN:
                    UserInterface.printUnexpectedInputMessage("\n"
                            + "that option is incorrect. "
                            + "please choose 1, 2, 3, todo, deadline, or event.");
                    break;
                default:
                    throw new IllegalStateException("Unknown task menu command: " + type);
            }
        }
    }

    private boolean addTodoFromConsole() {
        ui.printAddPrompt();
        String description = Task.validateDescription(ui.readCommand());
        if (UserInterface.preventCorrupt(description)) {
            return false;
        }
        tasks.addTask(new Todo(description));
        return true;
    }

    private boolean addDatedTaskFromConsole(Parser.TaskMenu type) {
        ui.printAddPrompt();
        String description = Task.validateDescription(ui.readCommand());
        if (UserInterface.preventCorrupt(description)) {
            return false;
        }

        if (type == Parser.TaskMenu.DEADLINE) {
            ui.printDeadlineDate();
        } else {
            ui.printEventDate();
        }
        String date = ui.readCommand();
        if (UserInterface.preventCorrupt(date)) {
            return false;
        }

        try {
            Task task = type == Parser.TaskMenu.DEADLINE
                    ? new Deadline(description, date)
                    : new Event(description, date);
            tasks.addTask(task);
            return true;
        } catch (DateTimeParseException exception) {
            UserInterface.printUnexpectedInputMessage("\n"
                    + "invalid date format. please use dd-mm-yyyy hhmm "
                    + "(e.g. 17-07-2004 1800).\n");
            return false;
        }
    }

    private void removeTaskFromConsole() {
        ui.printRemovePrompt();
        ui.printTodoList(tasks.getTasks());
        int index = tasks.findTaskIndex(ui.readCommand());

        if (index >= 0) {
            tasks.removeTask(index);
            storage.save(tasks.getTasks());
            ui.printRemoved();
        } else {
            UserInterface.printUnexpectedInputMessage(
                    "could not find a task with that name or number. please try again.");
        }
        ui.printBanner();
    }

    private boolean changeTaskStatusFromConsole(boolean markAsDone) {
        if (markAsDone) {
            ui.printMarkPrompt();
        } else {
            ui.printUnmarkPrompt();
        }
        ui.printTodoList(tasks.getTasks());

        int index = ui.getTaskIndex(tasks.size());
        if (index < 0) {
            return false;
        }

        Task task = tasks.getTask(index);
        if (markAsDone) {
            task.markAsDone();
            System.out.println("____________________________________________________________");
            System.out.println("nice! i've marked this task as done:");
            System.out.println("[x] " + task.getDescription());
        } else {
            task.markAsNotDone();
            System.out.println("____________________________________________________________");
            System.out.println("ok i've marked this task as not done yet:");
            System.out.println("[] " + task.getDescription());
        }
        System.out.println("____________________________________________________________");
        ui.printTodoList(tasks.getTasks());
        storage.save(tasks.getTasks());
        ui.printBanner();
        return true;
    }

    private void findTasksFromConsole() {
        ui.printFindPrompt();
        ui.printMatchingTasks(tasks.findTasks(ui.readCommand()), tasks.getTasks());
        ui.printBanner();
    }

    /** Reorders the backing list and saves it so task numbers survive a restart. */
    private void sortTasks() {
        tasks.sortByDate();
        storage.save(tasks.getTasks());
    }

    private void saveTasksAndReturnToMainMenu() {
        storage.save(tasks.getTasks());
        ui.printSaved();
        ui.printBanner();
    }

    /***
     * Starts the doe chatbot application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // initialises doe with target file and starts the main loop
        new Doe("todo.txt").run();
    }
}
