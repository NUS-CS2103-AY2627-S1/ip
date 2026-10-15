package nerrad;

import java.io.IOException;
import java.util.Scanner;

import nerrad.loan.Loan;
import nerrad.loan.LoanList;
import nerrad.parser.Parser;
import nerrad.storage.Storage;
import nerrad.task.Task;
import nerrad.task.TaskList;
import nerrad.ui.Ui;

/**
 * Coordinates Nerrad's user interface, command parser, storage, and task list.
 */
public class Nerrad {
    /** User interface used to read commands and display responses. */
    private final Ui ui;

    /** Parser used to interpret task details and task numbers. */
    private final Parser parser;

    /** Storage used to load and save this chatbot's task data. */
    private final Storage storage;

    /** Tasks managed during this chatbot session. */
    private final TaskList tasks;

    /** Loans managed during this chatbot session. */
    private final LoanList loans;

    /** Whether startup failed because saved tasks could not be loaded. */
    private final boolean hasLoadingError;

    /**
     * Creates a Nerrad chatbot that stores its tasks at the given path.
     *
     * @param filePath Path of the task data file.
     */
    public Nerrad(String filePath) {
        this.ui = new Ui();
        this.parser = new Parser();
        this.storage = new Storage(filePath);

        TaskList loadedTasks;
        LoanList loadedLoans;
        boolean loadingFailed = false;
        try {
            loadedTasks = new TaskList(storage.loadTasks());
            loadedLoans = new LoanList(storage.loadLoans());
        } catch (IOException exception) {
            loadedTasks = new TaskList();
            loadedLoans = new LoanList();
            loadingFailed = true;
        }
        this.tasks = loadedTasks;
        this.loans = loadedLoans;
        this.hasLoadingError = loadingFailed;
    }

    /**
     * Starts the chatbot and processes commands until the user enters bye.
     */
    public void run() {
        if (hasLoadingError) {
            ui.showLoadingError();
            return;
        }

        ui.showWelcome();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                String input = ui.readCommand(scanner);
                if (input == null) {
                    break;
                }

                ui.showResponse(getResponse(input));
                if (isExitCommand(input)) {
                    break;
                }
            }
        }
    }

    /**
     * Returns the chatbot's reply after processing a user command.
     *
     * @param input Command entered by the user.
     * @return Reply to display to the user.
     */
    public String getResponse(String input) {
        if (hasLoadingError) {
            return ui.getLoadingErrorMessage();
        }

        if (isExitCommand(input)) {
            return ui.getGoodbyeMessage();
        }

        if (input.equals("list")) {
            return ui.getTaskListMessage(tasks.getTasks());
        }
        if (input.equals("loans")) {
            return ui.getLoanListMessage(loans.getLoans());
        }

        try {
            if (input.equals("find") || input.startsWith("find ")) {
                String keyword = input.substring(4).trim();
                if (keyword.isEmpty()) {
                    throw new NerradException("Please provide a keyword to find.");
                }
                return ui.getMatchingTasksMessage(tasks.findTasks(keyword));
            }

            if (input.equals("settle-loan") || input.startsWith("settle-loan ")) {
                int loanIndex = parser.parseLoanIndex(input.substring(11), loans.size());
                settleLoan(loanIndex);
                return ui.getLoanSettledMessage(loans.get(loanIndex));
            }

            if (input.equals("loan") || input.startsWith("loan ")) {
                Loan newLoan = parser.parseLoan(input);
                addLoan(newLoan);
                return ui.getLoanAddedMessage(newLoan, loans.size());
            }

            if (input.equals("mark") || input.startsWith("mark ")) {
                int taskIndex = parser.parseTaskIndex(input.substring(4), tasks.size(), "mark");
                setTaskDone(taskIndex, true);
                return ui.getTaskMarkedMessage(tasks.get(taskIndex));
            }

            if (input.equals("unmark") || input.startsWith("unmark ")) {
                int taskIndex = parser.parseTaskIndex(input.substring(6), tasks.size(), "unmark");
                setTaskDone(taskIndex, false);
                return ui.getTaskUnmarkedMessage(tasks.get(taskIndex));
            }

            if (input.equals("delete") || input.startsWith("delete ")) {
                int taskIndex = parser.parseTaskIndex(input.substring(6), tasks.size(), "delete");
                Task deletedTask = deleteTask(taskIndex);
                return ui.getTaskDeletedMessage(deletedTask, tasks.size());
            }

            Task newTask = parser.parseTask(input);
            addTask(newTask);
            return ui.getTaskAddedMessage(newTask, tasks.size());
        } catch (NerradException exception) {
            return ui.getErrorMessage(exception.getMessage());
        }
    }

    /**
     * Returns Nerrad's greeting for graphical user interfaces.
     *
     * @return Startup greeting, or a loading error when saved data is invalid.
     */
    public String getWelcomeMessage() {
        return hasLoadingError ? ui.getLoadingErrorMessage() : ui.getWelcomeMessage();
    }

    /**
     * Returns a compact overview of tasks and loans for the graphical interface.
     *
     * @return Task, completion, and loan counts.
     */
    public String getDashboardSummary() {
        return ui.getDashboardSummary(tasks.size(), tasks.getCompletedCount(), loans.size());
    }

    /**
     * Returns whether a command asks Nerrad to exit.
     *
     * @param input Command entered by the user.
     * @return Whether the command is {@code bye}.
     */
    public boolean isExitCommand(String input) {
        return input.equals("bye");
    }

    /**
     * Returns whether Nerrad could not load its saved tasks at startup.
     *
     * @return Whether task loading failed.
     */
    public boolean hasLoadingError() {
        return hasLoadingError;
    }

    /**
     * Starts Nerrad using the normal project-relative data file.
     *
     * @param args Command-line arguments; not used.
     */
    public static void main(String[] args) {
        new Nerrad("data/nerrad.txt").run();
    }

    /**
     * Saves the current task list and converts file-writing failures into a chatbot error.
     *
     * @throws NerradException If the task list cannot be saved.
     */
    private void saveTasks() throws NerradException {
        try {
            storage.saveTasks(tasks.getTasks());
        } catch (IOException exception) {
            throw new NerradException("I could not save your tasks.");
        }
    }

    /**
     * Adds a task only if the changed list can be saved successfully.
     *
     * @param newTask Task to add.
     * @throws NerradException If the changed list cannot be saved.
     */
    private void addTask(Task newTask) throws NerradException {
        tasks.add(newTask);
        try {
            saveTasks();
        } catch (NerradException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
    }

    /**
     * Changes a task's completion status only if the changed list can be saved.
     *
     * @param taskIndex Index of the task to change.
     * @param shouldBeDone Desired completion status.
     * @throws NerradException If the changed list cannot be saved.
     */
    private void setTaskDone(int taskIndex, boolean shouldBeDone) throws NerradException {
        boolean wasDone = tasks.get(taskIndex).isDone();
        tasks.setDone(taskIndex, shouldBeDone);

        try {
            saveTasks();
        } catch (NerradException exception) {
            tasks.setDone(taskIndex, wasDone);
            throw exception;
        }
    }

    /**
     * Deletes a task only if the changed list can be saved successfully.
     *
     * @param taskIndex Index of the task to delete.
     * @return Deleted task.
     * @throws NerradException If the changed list cannot be saved.
     */
    private Task deleteTask(int taskIndex) throws NerradException {
        Task deletedTask = tasks.remove(taskIndex);
        try {
            saveTasks();
            return deletedTask;
        } catch (NerradException exception) {
            tasks.add(taskIndex, deletedTask);
            throw exception;
        }
    }

    /**
     * Saves the current loan list and converts file-writing failures into a chatbot error.
     *
     * @throws NerradException If the loan list cannot be saved.
     */
    private void saveLoans() throws NerradException {
        try {
            storage.saveLoans(loans.getLoans());
        } catch (IOException exception) {
            throw new NerradException("I could not save your loans.");
        }
    }

    /**
     * Adds a loan only if the changed list can be saved successfully.
     *
     * @param newLoan Loan to add.
     * @throws NerradException If the changed list cannot be saved.
     */
    private void addLoan(Loan newLoan) throws NerradException {
        loans.add(newLoan);
        try {
            saveLoans();
        } catch (NerradException exception) {
            loans.remove(loans.size() - 1);
            throw exception;
        }
    }

    /**
     * Marks a loan as settled only if the changed list can be saved successfully.
     *
     * @param loanIndex Index of the loan to settle.
     * @throws NerradException If the changed list cannot be saved.
     */
    private void settleLoan(int loanIndex) throws NerradException {
        Loan loan = loans.get(loanIndex);
        boolean wasSettled = loan.isSettled();
        loan.markAsSettled();
        try {
            saveLoans();
        } catch (NerradException exception) {
            if (!wasSettled) {
                loan.markAsOutstanding();
            }
            throw exception;
        }
    }
}
