package kotha;

import kotha.tasks.Task;
import kotha.tasks.TaskList;

/**
 * A chatbot that echoes commands until the user exits.
 */
public class Kotha {
    private static final Storage storage = new Storage("data/kotha.txt");
    private static final TaskList listOfTasks = new TaskList(storage.loadTasks());

    private static final Ui ui = new Ui();
    private static final Parser parser = new Parser();

    /**
     * Starts the chatbot and processes commands from standard input.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        ui.printWelcomeMessage();

        while (true) {
            String command = ui.readCommand();
            try {
                if (processCommand(command)) {
                    break;
                }
            } catch (KothaException e) {
                ui.printErrorMessage(e.getMessage());
            }
        }
    }

    /** Processes one command and returns whether the application should exit. */
    private static boolean processCommand(String command) throws KothaException {
        Parser.CommandType commandType = parser.parse(command);
        switch (commandType) {
            case BYE:
                ui.printGoodbye();
                return true;
            case LIST:
                ui.printTaskList(listOfTasks.asList());
                break;
            case FIND:
                findTasks(command);
                break;
            case MARK:
                changeTaskStatus(command, true);
                break;
            case UNMARK:
                changeTaskStatus(command, false);
                break;
            case TODO:
                addTask(Task.createTodo(command));
                break;
            case DEADLINE:
                addTask(Task.createDeadline(command));
                break;
            case EVENT:
                addTask(Task.createEvent(command));
                break;
            case DELETE:
                deleteTask(command);
                break;
            default:
                throw new KothaException("Your Majesty, I do not recognise that command.");
        }
        return false;
    }

    /** Searches task descriptions for the keyword supplied after {@code find}. */
    private static void findTasks(String command) throws KothaException {
        String keyword = command.substring("find".length()).trim();
        if (keyword.isEmpty()) {
            throw new KothaException("Your Majesty, provide a keyword to find.");
        }
        ui.printSearchResults(listOfTasks.find(keyword));
    }

    /** Adds a task, reports the change, and saves the updated list. */
    private static void addTask(Task task) {
        listOfTasks.add(task);
        task.printAddText();
        ui.printTaskCount(listOfTasks.size());
        storage.saveTasks(listOfTasks.asList());
    }

    /** Deletes the task identified by a one-based task number. */
    private static void deleteTask(String command) throws KothaException {
        String taskNumberText = command.substring("delete".length()).trim();
        if (!taskNumberText.matches("\\d+")) {
            throw new KothaException("Your Majesty, I cannot delete task number " + taskNumberText
                    + ".\nPray provide an integer.");
        }
        int taskIndex = Integer.parseInt(taskNumberText) - 1;
        if (taskIndex < 0 || taskIndex >= listOfTasks.size()) {
            throw new KothaException("Your Majesty, that task lies beyond the bounds of your list.");
        }
        listOfTasks.get(taskIndex).printRemoveText();
        listOfTasks.remove(taskIndex);
        ui.printTaskCount(listOfTasks.size());
        storage.saveTasks(listOfTasks.asList());
    }

    /** Marks or unmarks the task identified by a one-based task number. */
    private static void changeTaskStatus(String command, boolean isDone) throws KothaException {
        String commandWord = isDone ? "mark" : "unmark";
        String taskNumberText = command.substring(commandWord.length()).trim();
        if (!taskNumberText.matches("\\d+")) {
            throw new KothaException(
                    "Your Majesty, pray provide a valid task number to " + commandWord + ".");
        }
        int taskIndex;
        try {
            taskIndex = Integer.parseInt(taskNumberText) - 1;
        } catch (NumberFormatException exception) {
            throw new KothaException(
                    "Your Majesty, pray provide a valid task number to " + commandWord + ".");
        }
        if (taskIndex < 0 || taskIndex >= listOfTasks.size()) {
            throw new KothaException("Your Majesty, that task number does not exist.");
        }
        if (isDone) {
            listOfTasks.get(taskIndex).markAsDone();
        } else {
            listOfTasks.get(taskIndex).markAsNotDone();
        }
        storage.saveTasks(listOfTasks.asList());
    }
}
