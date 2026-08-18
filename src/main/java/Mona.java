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
        Task[] tasks = new Task[MAXIMUM_TASKS];
        int taskCount = 0;

        printFormatted(BANNER + "\nHello! I'm Mona.\nWhat can I do for you?");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Mona > ");
            String userInput = scanner.nextLine();

            if (userInput.startsWith("mark ")) {
                markTask(tasks, taskCount, userInput, true);
            } else if (userInput.startsWith("unmark ")) {
                markTask(tasks, taskCount, userInput, false);
            } else if (userInput.equals("bye")) {
                printFormatted("Bye. Hope to see you again soon!");
                break;
            } else if (userInput.equals("list")) {
                printTasks(tasks, taskCount);
            } else {
                tasks[taskCount] = new Task(userInput);
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

    private static void printTasks(Task[] tasks, int taskCount) {
        StringBuilder taskList = new StringBuilder("Here are the tasks in your list:");
        for (int index = 0; index < taskCount; index++) {
            taskList.append(System.lineSeparator())
                    .append(index + 1)
                    .append(".")
                    .append(tasks[index]);
        }
        printFormatted(taskList.toString());
    }

    private static void markTask(Task[] tasks, int taskCount, String userInput, boolean shouldMarkAsDone) {
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(userInput.substring(userInput.indexOf(' ') + 1));
        } catch (NumberFormatException exception) {
            printFormatted("Please enter a valid task number.");
            return;
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            printFormatted("Please enter a valid task number.");
            return;
        }

        Task task = tasks[taskNumber - 1];

        if (shouldMarkAsDone) {
            task.markAsDone();
            printFormatted("Nice! I've marked this task as done:\n  " + task);
        } else {
            task.markAsNotDone();
            printFormatted("OK, I've marked this task as not done yet:\n  " + task);
        }
    }
}
