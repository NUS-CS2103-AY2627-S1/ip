import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * A simple command-line chatbot that stores and manages tasks.
 */
public class Bibi {
    /**
     * Starts Bibi and processes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        String banner = "B B B B    i    b b b    i\n"
                + "B       B       b       b\n"
                + "B B B B   iii   b b b b  iii\n"
                + "B       B  i    b       b  i\n"
                + "B B B B  iii   b b b b  iii\n"
                + "Greetings Comrade! I'm Bibi, your ever-present bot friend.\n"
                + "Enter a task to add it, type list to view tasks, mark <number> to complete one, "
                + "or bye to exit.";
        List<Task> tasks = new ArrayList<>();

        System.out.println(banner);

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("You: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("bye")) {
                    System.out.println("Bibi: Goodbye! Till next time...");
                    break;
                } else if (input.equalsIgnoreCase("list")) {
                    printTasks(tasks);
                } else if (input.toLowerCase().startsWith("mark ")) {
                    markTask(input.substring(5).trim(), tasks);
                } else if (input.toLowerCase().startsWith("unmark ")) {
                    unmarkTask(input.substring(7).trim(), tasks);
                } 
                else if (input.isEmpty()) {
                    System.out.println("Bibi: Please enter a task or command.");
                } else {
                    tasks.add(new Task(input));
                    System.out.println("Bibi: Added: " + input);
                }
            }
        }
    }

    /**
     * Prints every task with its one-based task number and completion status.
     *
     * @param tasks tasks to print
     */
    private static void printTasks(List<Task> tasks) {
        if (tasks.isEmpty()) {
            System.out.println("Bibi: Your task list is empty.");
            return;
        }

        for (int index = 0; index < tasks.size(); index++) {
            System.out.println((index + 1) + ". " + tasks.get(index));
        }
    }

    /**
     * Marks the requested one-based task number complete when it exists.
     *
     * @param numberText the task number supplied after {@code mark}
     * @param tasks the current task list
     */
    private static void markTask(String numberText, List<Task> tasks) {
        try {
            int taskNumber = Integer.parseInt(numberText);
            int taskIndex = taskNumber - 1;

            if (taskIndex < 0 || taskIndex >= tasks.size()) {
                System.out.println("Bibi: That task number does not exist.");
                return;
            }

            Task task = tasks.get(taskIndex);
            task.markComplete();
            System.out.println("Bibi: Marked task " + taskNumber + " as complete.");
        } catch (NumberFormatException exception) {
            System.out.println("Bibi: Use unmark followed by a task number, for example: unmark 2");
        }
    }

    private static void unmarkTask(String numberText, List<Task> tasks) {
        try {
            int taskNumber = Integer.parseInt(numberText);
            int taskIndex = taskNumber - 1;

            if (taskIndex < 0 || taskIndex >= tasks.size()) {
                System.out.println("Bibi: That task number does not exist.");
                return;
            }

            Task task = tasks.get(taskIndex);
            task.markIncomplete();
            System.out.println("Bibi: Unmarked task " + taskNumber + ", now incomplete.");
        } catch (NumberFormatException exception) {
            System.out.println("Bibi: Use mark followed by a task number, for example: mark 2");
        }
    }
}
