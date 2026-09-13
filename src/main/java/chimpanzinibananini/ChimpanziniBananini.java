package chimpanzinibananini;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Clock;
import java.time.LocalDateTime;

import chimpanzinibananini.exception.ChimpanziniBananiniException;
import chimpanzinibananini.parser.Parser;
import chimpanzinibananini.storage.Storage;
import chimpanzinibananini.task.Task;
import chimpanzinibananini.task.TaskList;
import chimpanzinibananini.ui.Ui;

/**
 * Coordinates the components of the ChimpanziniBananini task chatbot.
 */
public class ChimpanziniBananini {
    public static final Path DATA_FILE = Path.of("data", "duke.txt");

    private final Ui ui;
    private final Storage storage;
    private final Clock clock;
    private TaskList tasks;
    private String loadingError;

    /**
     * Creates a chatbot that stores its tasks at the given path.
     */
    public ChimpanziniBananini(Path filePath) {
        this(filePath, Clock.systemDefaultZone());
    }

    /**
     * Creates a chatbot with the given storage path and clock for reminder time comparisons.
     */
    public ChimpanziniBananini(Path filePath, Clock clock) {
        this.clock = clock;
        ui = new Ui();
        storage = new Storage(filePath);
        try {
            tasks = storage.loadTasks();
        } catch (IOException e) {
            tasks = new TaskList();
            loadingError = e.getMessage();
        }
    }

    /**
     * Reads and processes commands until the input ends or the user says bye.
     */
    public void run() {
        ui.showWelcome();
        try {
            tasks = storage.loadTasks();
            loadingError = null;
        } catch (IOException e) {
            ui.showLoadingError(e.getMessage());
            ui.close();
            return;
        }
        try (ui) {
            while (ui.hasNextCommand()) {
                String input = ui.readCommand();
                String response = getResponse(input);
                ui.showResponse(response);
                if (isBye(input)) {
                    break;
                }
            }
        }
    }

    /**
     * Processes one command and returns the chatbot's response.
     */
    public String getResponse(String input) {
        if (loadingError != null) {
            return "I couldn't load the task file: " + loadingError;
        }
        try {
            return execute(Parser.parse(input));
        } catch (ChimpanziniBananiniException e) {
            return e.getMessage();
        } catch (IOException e) {
            return "I couldn't save your tasks: " + e.getMessage();
        }
    }

    /** Executes one parsed command and saves the list after successful changes. */
    private String execute(Parser.ParsedCommand command) throws ChimpanziniBananiniException, IOException {
        return switch (command.type()) {
        case LIST -> formatTaskList(tasks);
        case REMINDERS -> {
            LocalDateTime now = LocalDateTime.now(clock);
            TaskList upcomingDeadlines = new TaskList(tasks.getUpcomingDeadlines(now));
            yield upcomingDeadlines.size() == 0
                    ? "You have no upcoming deadlines in the next 7 days."
                    : "Here are your upcoming deadlines:\n" + formatTaskList(upcomingDeadlines);
        }
        case MARK -> {
            boolean wasDone = command.taskNumber() >= 1 && command.taskNumber() <= tasks.size()
                    && tasks.get(command.taskNumber() - 1).isDone();
            Task task = tasks.mark(command.taskNumber());
            try {
                storage.saveTasks(tasks);
            } catch (IOException e) {
                if (!wasDone) {
                    task.markAsNotDone();
                }
                throw e;
            }
            yield "Nice! I've marked this task as done:\n  " + task;
        }
        case ADD -> {
            TaskList updatedTasks = new TaskList(tasks.asList());
            updatedTasks.add(command.task());
            storage.saveTasks(updatedTasks);
            tasks = updatedTasks;
            yield "Got it. I've added this task:\n  " + command.task()
                    + "\nNow you have " + tasks.size() + " tasks in the list.";
        }
        case DELETE -> {
            TaskList updatedTasks = new TaskList(tasks.asList());
            Task removedTask = updatedTasks.delete(command.taskNumber());
            storage.saveTasks(updatedTasks);
            tasks = updatedTasks;
            yield "Noted. I've removed this task:\n  " + removedTask
                    + "\nNow you have " + tasks.size() + " tasks in the list.";
        }
        case FIND -> formatFoundTasks(command);
        case BYE -> "Tung tung tung sahur... I'm outta here 💀";
        };
    }

    /** Formats all tasks using their one-based task numbers. */
    private String formatTaskList(TaskList taskList) {
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < taskList.size(); i++) {
            if (i > 0) {
                response.append('\n');
            }
            response.append(i + 1).append(". ").append(taskList.get(i));
        }
        return response.isEmpty() ? "Your task list is empty." : response.toString();
    }

    /** Formats tasks matching a find command. */
    private String formatFoundTasks(Parser.ParsedCommand command) {
        TaskList matchingTasks = new TaskList(tasks.find(command.keyword()));
        String formattedTasks = formatTaskList(matchingTasks);
        return "Here are the matching tasks in your list:\n" + formattedTasks;
    }

    /** Returns whether an input is a valid bye command. */
    private boolean isBye(String input) {
        try {
            return Parser.parse(input).type() == Parser.CommandType.BYE;
        } catch (ChimpanziniBananiniException e) {
            return false;
        }
    }

    /**
     * Starts the chatbot using its default data file.
     */
    public static void main(String[] args) {
        new ChimpanziniBananini(DATA_FILE).run();
    }
}
