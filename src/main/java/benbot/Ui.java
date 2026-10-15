package benbot;

import java.io.IOException;
import java.util.Scanner;

/** Handles BenBot's command-line interaction with the user. */
class Ui {
    /** Message shown when BenBot cannot save data to its storage file. */
    static final String STORAGE_ERROR_MESSAGE =
            "ERROR: Unable to save data. Check that the data folder is writable, then try again.";

    /** The greeting and command summary shared by the terminal and graphical interfaces. */
    private static final String WELCOME_MESSAGE = String.join(System.lineSeparator(),
            "Hello! I'm BenBot.",
            "I can help you manage tasks, contacts, notes, and expenses.",
            "",
            "Tasks:",
            "  todo DESCRIPTION",
            "  deadline DESCRIPTION /by DATE [TIME]",
            "  event DESCRIPTION /from START /to END",
            "  list",
            "  find KEYWORD",
            "  mark TASK_NUMBER",
            "  unmark TASK_NUMBER",
            "  delete TASK_NUMBER",
            "",
            "Contacts:",
            "  contact NAME /phone PHONE /email EMAIL",
            "  contacts",
            "  delete-contact CONTACT_NUMBER",
            "",
            "Notes:",
            "  note TEXT",
            "  notes",
            "  delete-note NOTE_NUMBER",
            "",
            "Expenses (list includes the total):",
            "  expense DESCRIPTION /amount AMOUNT",
            "  expenses",
            "  delete-expense EXPENSE_NUMBER",
            "",
            "Use lowercase commands and replace the uppercase placeholders.",
            "Leading, trailing, and repeated spaces are ignored.",
            "Each list has its own numbers, starting at 1.",
            "Dates: 15/9/2026; optional times: 1800 (24-hour HHmm).",
            "START and END each accept a date with an optional time; END must be after START.",
            "Exact duplicate tasks are rejected.",
            "Amounts use digits and up to two decimal places, without a currency symbol.",
            "Type bye to save all your data and exit.",
            "What would you like to do?");

    /** Reads commands entered through the standard input stream. */
    private final Scanner scanner;

    /** Stores tasks when the user exits the application. */
    private final TaskDataStore taskDataStore;

    /** Creates a user interface that reads commands from standard input. */
    public Ui() {
        this(new Scanner(System.in), new TaskDataStore());
    }

    /**
     * Creates a user interface with the supplied input source and data store.
     *
     * @param scanner reads commands entered by the user.
     * @param taskDataStore stores tasks when the user exits.
     */
    Ui(Scanner scanner, TaskDataStore taskDataStore) {
        this.scanner = scanner;
        this.taskDataStore = taskDataStore;
    }

    /** Displays BenBot's welcome banner, available commands, and initial prompt. */
    public void welcome() {
        String banner = " ____              ____        _   \n"
                + "| __ )  ___ _ __  | __ )  ___ | |_ \n"
                + "|  _ \\ / _ \\ '_ \\ |  _ \\ / _ \\| __|\n"
                + "| |_) |  __/ | | || |_) | (_) | |_ \n"
                + "|____/ \\___|_| |_||____/ \\___/ \\__|\n";

        System.out.println(banner);
        System.out.println(getWelcomeMessage());
        System.out.println(BenBot.DIVIDER);
    }

    /** Returns the greeting and command summary shared by BenBot's interfaces. */
    static String getWelcomeMessage() {
        return WELCOME_MESSAGE;
    }

    /**
     * Reads and processes commands until input ends or a {@code bye} command saves successfully.
     *
     * @param taskLoader processes the entered commands.
     * @param tasks stores the current tasks.
     * @param taskCount holds the current number of tasks.
     */
    public void run(TaskLoader taskLoader, Task[] tasks, int[] taskCount) {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            System.out.println(BenBot.DIVIDER);
            boolean exitRequested = taskLoader.addTask(line, tasks, taskCount, false);
            String response = taskLoader.getLastResponse();
            if (exitRequested) {
                String storageError = storeData(taskLoader, tasks, taskCount[0]);
                if (storageError.isEmpty()) {
                    System.out.println(response);
                    break;
                }
                System.out.println(storageError);
                System.out.println(BenBot.SAVE_RETRY_MESSAGE);
            } else {
                System.out.println(response);
            }
        }
    }

    /**
     * Stores the current tasks and extension entities and returns an error message if storage fails.
     * Returning the message lets both text and graphical interfaces display it appropriately.
     */
    String storeData(TaskLoader taskLoader, Task[] tasks, int taskCount) {
        try {
            taskDataStore.store(tasks, taskCount, taskLoader.getExtensionStorageCommands());
            return "";
        } catch (IOException ignored) {
            return STORAGE_ERROR_MESSAGE;
        }
    }
}
