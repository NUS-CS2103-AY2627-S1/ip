package nova.ui;

import java.util.Scanner;

/**
 * Handles all interactions between NOVA and the user,
 * including reading commands and displaying messages.
 */
public class UI {

    private final Scanner scanner;
    private final String line =
            "____________________________________________________________";

    /**
     * Creates a UI object that reads input from the console.
     */
    public UI() {
        scanner = new Scanner(System.in);
    }

    /**
     * Displays NOVA's welcome message and available command formats.
     */
    public void showWelcome() {
        String banner = " _   _  _____     ___    \n"
                + "| \\ | |/ _ \\ \\   / / \\   \n"
                + "|  \\| | | | \\ \\ / / _ \\  \n"
                + "| |\\  | |_| |\\ V / ___ \\ \n"
                + "|_| \\_|\\___/  \\_/_/   \\_\\\n";

        System.out.println(line);
        System.out.println(banner);
        System.out.println("Hi there Big Boss! I am your assistant NOVA");
        System.out.println("Your wish is my command!");

        System.out.println(line);
        System.out.println("Type your Command Below");
        System.out.println(line);

        System.out.println("deadline [-description-] /by d/M/yyyy HHmm");
        System.out.println("event [-description-] /from d/M/yyyy HHmm /to d/M/yyyy HHmm");
        System.out.println("todo [-description-]");

        System.out.println(line);
        System.out.println("Or");
        System.out.println("Type -bye- to exit");
        System.out.println("Or");
        System.out.println("Type -list- to see your history of commands");

        System.out.println(line);
        System.out.println("Type -mark [task_number]- to mark tasks");
        System.out.println("Or");
        System.out.println("Type -unmark [task_number]- to unmark previously marked tasks");
        System.out.println(" ");

        System.out.println(line);
        System.out.println("Type -delete [task_number]- to remove tasks");
        System.out.println("find [-keyword-]");
        System.out.println(line);
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the full command entered by the user
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays NOVA's goodbye message.
     */
    public void showGoodbye() {
        System.out.println(line);
        System.out.println("Bye Boss. Hope to see you again soon!");
        System.out.println(line);
    }

    /**
     * Displays a general message to the user.
     *
     * @param message message to display
     */
    public void showMessage(String message) {
        System.out.println(line);
        System.out.println(message);
        System.out.println(line);
    }

    /**
     * Displays an error message to the user.
     *
     * @param message error message to display
     */
    public void showError(String message) {
        System.out.println(line);
        System.out.println(message);
        System.out.println(line);
    }

    /**
     * Closes the input scanner used by the UI.
     */
    public void close() {
        scanner.close();
    }
}
