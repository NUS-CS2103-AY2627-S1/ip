import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

/**
 * Friday chatbot entry point.
 * Coordinates Parser, Ui, TaskList, and Storage without interpreting command syntax itself.
 */
public class Friday {
    private static final String INVALID_TASK_NUMBER_MESSAGE = "Sir, The task number is invalid.";
    private static final Path DATA_FILE = Path.of("data", "friday.txt");

    /** Loads saved tasks and processes commands, saving after each task-list change. */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(DATA_FILE);
        TaskList tasks = new TaskList();
        boolean savingEnabled = true;

        ui.showWelcome();
        try {
            tasks = new TaskList(storage.load());
        } catch (IOException | SecurityException e) {
            savingEnabled = false;
            ui.showLoadingError();
        }
        ui.showLine();

        while (ui.hasNextCommand()) {
            String command = ui.readCommand();
            ui.showLine();
            try {
                boolean changed = false;
                switch (Parser.parseCommandType(command)) {
                case BYE -> {
                    ui.showGoodbye();
                    return;
                }
                case HELLO -> ui.showGreeting();
                case THANKS -> ui.showThanks();
                case HELP -> ui.showHelp();
                case TODO, DEADLINE, EVENT -> {
                    Task task = Parser.parseTask(command);
                    tasks.add(task);
                    ui.showTaskAdded(task, tasks.size());
                    changed = true;
                }
                case ON -> listTasksOn(tasks, Parser.parseDate(command), ui);
                case LIST -> {
                    ui.showTaskListHeader();
                    for (int taskNumber = 1; taskNumber <= tasks.size(); taskNumber++) {
                        ui.showNumberedTask(taskNumber, tasks.get(taskNumber));
                    }
                }
                case DELETE -> {
                    int taskNumber = requireExistingTaskNumber(command, tasks);
                    Task removedTask = tasks.delete(taskNumber);
                    ui.showTaskDeleted(removedTask, tasks.size());
                    changed = true;
                }
                case MARK -> {
                    int taskNumber = requireExistingTaskNumber(command, tasks);
                    changed = tasks.mark(taskNumber);
                    ui.showTaskMarked(tasks.get(taskNumber));
                }
                case UNMARK -> {
                    int taskNumber = requireExistingTaskNumber(command, tasks);
                    changed = tasks.unmark(taskNumber);
                    if (changed) {
                        ui.showTaskUnmarked(tasks.get(taskNumber));
                    } else {
                        ui.showAlreadyUnmarked();
                    }
                }
                }
                if (changed) {
                    saveTasks(storage, tasks, savingEnabled, ui);
                }
            } catch (IllegalArgumentException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /** Checks parsed task numbers against the current list; the parser does not depend on list state. */
    private static int requireExistingTaskNumber(String command, TaskList tasks) {
        int taskNumber = Parser.parseTaskNumber(command);
        if (!tasks.isValidTaskNumber(taskNumber)) {
            throw new IllegalArgumentException(INVALID_TASK_NUMBER_MESSAGE);
        }
        return taskNumber;
    }

    /** Shows matching dated tasks using their original list numbers, without modifying or saving the list. */
    private static void listTasksOn(TaskList tasks, LocalDate date, Ui ui) {
        ui.showDateHeader(date);
        List<Integer> matches = tasks.findTaskNumbersOn(date);
        if (matches.isEmpty()) {
            ui.showNoTasksOnDate();
        }
        for (int taskNumber : matches) {
            ui.showNumberedTask(taskNumber, tasks.get(taskNumber));
        }
    }

    /** Reports storage failures without terminating the command loop or discarding session tasks. */
    private static void saveTasks(Storage storage, TaskList tasks, boolean savingEnabled, Ui ui) {
        if (!savingEnabled) {
            ui.showSavingDisabled();
            return;
        }
        try {
            storage.save(tasks.toList());
        } catch (IOException | SecurityException e) {
            ui.showSavingError();
        }
    }
}
