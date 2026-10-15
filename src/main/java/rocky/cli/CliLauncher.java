package rocky.cli;

import rocky.TaskList;
import rocky.parser.Parser;
import rocky.storage.Storage;
import rocky.task.Task;
import rocky.ui.Ui;

/** Starts Rocky's original console interface. */
public class CliLauncher {
    private static final Storage STORAGE = new Storage("./data/rocky.txt");

    /** Starts the console chatbot and processes commands until it exits. */
    public static void main(String[] args) {
        Ui ui = new Ui();
        ui.showWelcome();

        TaskList tasks = new TaskList(STORAGE.load());
        Parser parser = new Parser();
        while (ui.hasNextLine()) {
            Parser.Command command = parser.parse(ui.readLine());
            switch (command.getType()) {
            case BYE:
                ui.showBye();
                return;
            case LIST:
                ui.showTasks(tasks);
                break;
            case SORT:
                sortTasks(tasks);
                ui.showSortedTasks(tasks);
                break;
            case FIND:
                ui.showMatchingTasks(tasks.find(command.getArgument()));
                break;
            case MARK:
                updateTaskStatus(command.getArgument(), "mark", true, tasks, ui);
                break;
            case UNMARK:
                updateTaskStatus(command.getArgument(), "unmark", false, tasks, ui);
                break;
            case DELETE:
                deleteTask(command.getArgument(), tasks, ui);
                break;
            case ADD:
                addTask(command.getTask(), tasks, ui);
                break;
            case MISSING_DESCRIPTION:
            case ERROR:
                ui.showError(command);
                break;
            default:
                break;
            }
        }
        ui.showBye();
    }

    /** Updates a task's completion state and persists the updated task list. */
    private static void updateTaskStatus(String taskNumberText, String command, boolean completed,
                                         TaskList tasks, Ui ui) {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            int taskIndex = taskNumber - 1;
            if (taskIndex >= 0 && taskIndex < tasks.size()) {
                Task task = tasks.get(taskIndex);
                if (completed) {
                    task.markAsDone();
                } else {
                    task.markAsNotDone();
                }
                STORAGE.save(tasks.asList());
                ui.showTaskStatus(task, completed);
            } else {
                ui.showTaskNotFound();
            }
        } catch (NumberFormatException exception) {
            ui.showInvalidTaskNumber(command);
        }
    }

    /** Deletes the selected task and persists the updated task list. */
    private static void deleteTask(String taskNumberText, TaskList tasks, Ui ui) {
        try {
            int taskNumber = Integer.parseInt(taskNumberText);
            int taskIndex = taskNumber - 1;
            if (taskIndex >= 0 && taskIndex < tasks.size()) {
                Task task = tasks.get(taskIndex);
                ui.showTaskDeleted(task);
                tasks.delete(taskIndex);
                STORAGE.save(tasks.asList());
            } else {
                ui.showTaskNotFound();
            }
        } catch (NumberFormatException exception) {
            ui.showInvalidTaskNumber("delete");
        }
    }

    /** Adds a task, displays confirmation, and persists the updated task list. */
    private static void addTask(Task task, TaskList tasks, Ui ui) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
        STORAGE.save(tasks.asList());
    }

    /** Sorts and persists the task list. */
    private static void sortTasks(TaskList tasks) {
        tasks.sortByDate();
        STORAGE.save(tasks.asList());
    }
}
