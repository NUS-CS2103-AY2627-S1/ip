import java.util.Scanner;

/**
 * Provides a command-line task manager named Mona.
 */
public class Mona {
    private static final int MAXIMUM_TASKS = 100;
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BANNER = " __  __  ___  _   _    _ \n"
            + "|  \\/  |/ _ \\| \\ | |  / \\\n"
            + "| |\\/| | | | |  \\| | / _ \\\n"
            + "| |  | | |_| | |\\  |/ ___ \\\n"
            + "|_|  |_|\\___/|_| \\_/_/   \\_\\\n";

    /**
     * Starts Mona and processes task commands until the user exits.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        String[] tasks = new String[MAXIMUM_TASKS];
        int taskCount = 0;
        boolean shouldExit = false;

        printFormatted(BANNER + "\nHello! I'm Mona.\nWhat can I do for you?");

        Scanner scanner = new Scanner(System.in);
        while (!shouldExit) {
            System.out.print("Mona > ");
            String userInput = scanner.nextLine();

            switch (userInput) {
            case "bye":
                printFormatted("Bye. Hope to see you again soon!");
                shouldExit = true;
                break;
            case "list":
                printTasks(tasks, taskCount);
                break;
            default:
                tasks[taskCount] = userInput;
                taskCount++;
                printFormatted("added: " + userInput);
            }
        }
    }

    private static void printFormatted(String text) {
        System.out.println(SEPARATOR);
        System.out.println(text);
        System.out.println(SEPARATOR);
    }

    private static void printTasks(String[] tasks, int taskCount) {
        StringBuilder taskList = new StringBuilder();
        for (int index = 0; index < taskCount; index++) {
            if (index > 0) {
                taskList.append(System.lineSeparator());
            }
            taskList.append(index + 1)
                    .append(". ")
                    .append(tasks[index]);
        }
        printFormatted(taskList.toString());
    }
}
