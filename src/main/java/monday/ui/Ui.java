package monday.ui;

import java.util.Scanner;

import monday.task.TaskList;

/**
 * Handles all input from and output to the user.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private final Scanner scanner;
    private String response;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Return's Monday's most recently stored response.
     *
     * @return the latest response text.
     */
    public String getResponse() {
        return this.response;
    }

    /**
     * Displays the welcome message when Monday starts.
     */
    public void showWelcome() {
        String banner = LINE + "\n"
                + " __  __   ___   _   _  ____      _    __   __\n"
                + "|  \\/  | / _ \\ | \\ | ||  _ \\    / \\   \\ \\ / /\n"
                + "| |\\/| || | | ||  \\| || | | |  / _ \\   \\ V / \n"
                + "| |  | || |_| || |\\  || |_| | / ___ \\   | |  \n"
                + "|_|  |_| \\___/ |_| \\_||____/ /_/   \\_\\  |_|  \n"
                + "Hello! My name is Monday.\n"
                + "How can I help you today?\n"
                + LINE;
        System.out.println(banner);

        String bannerForGui = "Hello! My name is Monday.\n How can I help you today?";
        this.response = bannerForGui;
    }

    /**
     * Reads one command entered by the user.
     *
     * @return the command entered by the user.
     */
    public String readCommand() {
        return this.scanner.nextLine();
    }

    /**
     * Displays a message between separator lines.
     *
     * @param responses the message to display.
     */
    public void showResponse(String... responses) {
        String response = String.join("\n", responses);

        System.out.println(LINE);
        System.out.println(response);
        System.out.println(LINE);

        this.response = response;
    }

    /**
     * Displays all tasks in the task list.
     *
     * @param tasks the tasks to display.
     */
    public void showTaskList(TaskList tasks) {
        StringBuilder response = new StringBuilder("Here are the tasks in your list:");
        for (int index = 0; index < tasks.size(); index++) {
            response.append("\n").append(index + 1).append(".").append(tasks.get(index));
        }
        showResponse(response.toString());
    }

    /**
     * Displays tasks whose descriptions matched a search keyword.
     *
     * @param tasks matching tasks to display
     */
    public void showMatchingTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            showResponse("No matching tasks found.");
            return;
        }

        StringBuilder response = new StringBuilder("Here are the matching tasks in your list:");

        for (int index = 0; index < tasks.size(); index++) {
            response.append("\n").append(index + 1).append(".").append(tasks.get(index));
        }

        showResponse(response.toString());
    }
}
