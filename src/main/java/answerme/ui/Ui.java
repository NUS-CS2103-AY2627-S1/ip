package answerme.ui;

import java.util.List;
import java.util.Scanner;

import answerme.task.Task;
import answerme.task.TaskList;

/**
 * Handles the user's interaction with the AnswerMe chatbot.
 */
public class Ui {
    private static final String BOT_NAME = "AnswerMe";
    private static final String HORIZONTAL_LINE = "____________________________________________________________";
    private static final String ERROR_PREFIX = "Oh no! ";

    private final Scanner scanner = new Scanner(System.in);
    private String latestResponse;
    private ResponseType latestResponseType;
    private final boolean isCliInstance;
    private String loadingErrorMessage;
    private String loadingWarningMessage;

    /**
     * Constructs the user interface for either the command-line or graphical
     * application mode.
     *
     * @param isCliInstance whether this user interface is used by the
     *                      command-line application.
     */
    public Ui(boolean isCliInstance) {
        this.isCliInstance = isCliInstance;
    }

    /**
     * Displays the chatbot's welcome message and banner.
     */
    public void showWelcome() {
        String banner = """
                      >>                                                        >=>       >=>          \s
                     >>=>                                                       >> >=>   >>=>          \s
                    >> >=>     >==>>==>   >===>  >=>      >=>   >==>    >> >==> >=> >=> > >=>   >==>   \s
                   >=>  >=>     >=>  >=> >=>      >=>  >  >=> >>   >=>   >=>    >=>  >=>  >=> >>   >=> \s
                  >=====>>=>    >=>  >=>   >==>   >=> >>  >=> >>===>>=>  >=>    >=>   >>  >=> >>===>>=>\s
                 >=>      >=>   >=>  >=>     >=>  >=>>  >=>=> >>         >=>    >=>       >=> >>       \s
                >=>        >=> >==>  >=> >=> >=> >==>    >==>  >====>   >==>    >=>       >=>  >====>  \s
                """;
        System.out.println(banner);
        System.out.println(getShortWelcome());
        System.out.println(HORIZONTAL_LINE);
    }

    /**
     * Reads a line of input from the user.
     *
     * @return The input entered by the user.
     */
    public String readUserInput() {
        if (!scanner.hasNextLine()) {
            return "bye";
        }
        return scanner.nextLine();
    }

    /**
     * Displays the tasks in the given task list.
     *
     * @param taskList The task list to display.
     * @param heading The message to display if task list is not empty.
     * @param emptyMessage The message to display if task list is empty.
     */
    public void listTasks(TaskList taskList, String heading, String emptyMessage) {
        if (taskList.isEmpty()) {
            showMessage(emptyMessage);
            return;
        }
        showMessage(heading + "\n" + taskList);
    }

    /**
     * Records the latest message/response and displays it in command-line
     * mode.
     *
     * @param message The message to display.
     */
    public void showMessage(String message) {
        showTypedMessage(message, ResponseType.NORMAL);
    }

    /**
     * Records and displays a successful response.
     *
     * @param message The success message to display.
     */
    public void showSuccessMessage(String message) {
        showTypedMessage(message, ResponseType.SUCCESS);
    }

    /**
     * Records and displays an error response.
     *
     * @param message The error message to display.
     */
    public void showErrorMessage(String message) {
        showTypedMessage(message, ResponseType.ERROR);
    }

    private void showTypedMessage(String message, ResponseType responseType) {
        latestResponse = message;
        latestResponseType = responseType;

        if (isCliInstance) {
            printMessageToConsole(message);
        }
    }

    private void printMessageToConsole(String message) {
        System.out.println("\t" + HORIZONTAL_LINE);
        for (String line : message.split("\n")) {
            System.out.println("\t" + line);
        }
        System.out.println("\t" + HORIZONTAL_LINE + "\n");
    }

    /**
     * Displays the reason saved tasks could not be loaded from Storage.
     *
     * @param reason The reason task loading failed.
     */
    public void showLoadingError(String reason) {
        loadingErrorMessage = "Issue loading saved tasks.\n" + reason
                + "\nPlease repair the data file and then restart the application.";
        showErrorMessage(loadingErrorMessage);
    }

    /**
     * Displays warnings for malformed tasks that were skipped while loading.
     *
     * @param warnings The warnings encountered while loading saved tasks.
     */
    public void showLoadingWarning(List<String> warnings) {
        loadingWarningMessage =
                "Some saved tasks could not be loaded and were skipped:\n- "
                        + String.join("\n- ", warnings)
                        + "\n\nThe remaining tasks were loaded successfully. "
                        + "Skipped entries will be removed the next time tasks are saved.";
        showErrorMessage(loadingWarningMessage);
    }

    /**
     * Displays an informational message that a task has been added.
     *
     * @param task The added task.
     * @param taskCount The number of tasks currently in the list.
     */
    public void printAddNewItem(Task task, int taskCount) {
        showSuccessMessage("Got it. I have added this task:\n"
                + task + "\nYou now have " + taskCount + " task(s) in the list.");
    }

    /**
     * Displays an informational message that a task has been deleted.
     *
     * @param task The deleted task.
     * @param taskCount The number of tasks currently in the list.
     */
    public void printDeleteItem(Task task, int taskCount) {
        showSuccessMessage("Noted. I will remove this task:\n"
                + task + "\nYou now have " + taskCount + " task(s) in the list.");
    }

    /**
     * Displays the duplicate tasks that were removed and the remaining
     * task count.
     *
     * @param duplicateTasks The list of tasks removed as duplicates.
     * @param taskCount The number of tasks remaining in the list.
     */
    public void printDedupItem(TaskList duplicateTasks, int taskCount) {
        showSuccessMessage("Removed the following duplicate task(s):\n"
                + duplicateTasks + "You now have " + taskCount + " task(s) in the list.");
    }

    /**
     * Returns a shortened version of the welcome message tailored for
     * graphical interfaces.
     *
     * @return A shortened version of the welcome message.
     */
    public String getShortWelcome() {
        return "Hello! I'm " + BOT_NAME + ", your personal assistant bot.\n"
                + "What can I do for you today?";
    }

    /**
     * Displays an ordinary command error.
     *
     * @param reason The reason the command failed.
     */
    public void showError(String reason) {
        showErrorMessage(ERROR_PREFIX + reason);
    }

    // accessors
    public String getLatestResponse() {
        return latestResponse;
    }

    public ResponseType getLatestResponseType() {
        return latestResponseType;
    }

    public String getLoadingErrorMessage() {
        return loadingErrorMessage;
    }

    public String getLoadingWarningMessage() {
        return loadingWarningMessage;
    }
}
