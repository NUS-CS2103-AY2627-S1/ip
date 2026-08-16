import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * A command-line chatbot that stores ToDos, deadlines, and events.
 */
public class Bibi {
    private static final String DIVIDER = "____________________________________________________________";

    /**
     * Starts Bibi and processes commands until the user enters {@code bye}.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        List<Task> tasks = new ArrayList<>();

        printWelcomeMessage();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("You: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("bye")) {
                    System.out.println("Bibi: Goodbye! Till next time...");
                    break;
                }

                processCommand(input, tasks);
            }
        }
    }

    /**
     * Displays the initial banner and a summary of Bibi's supported commands.
     */
    private static void printWelcomeMessage() {
        String banner = "B B B B    i    b b b    i\n"
                + "B       B       b       b\n"
                + "B B B B   iii   b b b b  iii\n"
                + "B       B  i    b       b  i\n"
                + "B B B B  iii   b b b b  iii\n";
        System.out.println(banner);
        System.out.println("Bibi: Enter todo <description>, deadline <description> /by <time>,");
        System.out.println("or event <description> /from <start> /to <end>.");
        System.out.println("Bibi: Type list, mark <number>, unmark <number>, or bye.");
    }

    /**
     * Routes one user command to the appropriate task operation.
     *
     * @param input the user's complete command
     * @param tasks the task list to update or display
     */
    private static void processCommand(String input, List<Task> tasks) {
        String command = input.toLowerCase(Locale.ROOT);

        if (input.isEmpty()) {
            System.out.println("Bibi: Please enter a command.");
        } else if (command.equals("list")) {
            printTasks(tasks);
        } else if (command.equals("todo") || command.startsWith("todo ")) {
            addTodo(input.substring(4).trim(), tasks);
        } else if (command.equals("deadline") || command.startsWith("deadline ")) {
            addDeadline(input.substring(8).trim(), tasks);
        } else if (command.equals("event") || command.startsWith("event ")) {
            addEvent(input.substring(5).trim(), tasks);
        } else if (command.startsWith("mark ")) {
            markTask(input.substring(5).trim(), tasks);
        } else if (command.startsWith("unmark ")) {
            unmarkTask(input.substring(7).trim(), tasks);
        } else {
            System.out.println("Bibi: I don't understand that command.");
        }
    }

    /**
     * Creates a ToDo from its description.
     *
     * @param description the ToDo description
     * @param tasks the task list to update
     */
    private static void addTodo(String description, List<Task> tasks) {
        if (description.isEmpty()) {
            System.out.println("Bibi: Use todo followed by a description.");
            return;
        }

        addTask(new Todo(description), tasks);
    }

    /**
     * Creates a deadline from text in the form {@code description /by time}.
     *
     * @param deadlineText the deadline text after the {@code deadline} command
     * @param tasks the task list to update
     */
    private static void addDeadline(String deadlineText, List<Task> tasks) {
        String normalizedText = deadlineText.toLowerCase(Locale.ROOT);
        int byIndex = normalizedText.indexOf(" /by ");

        if (byIndex <= 0) {
            System.out.println("Bibi: Use deadline <description> /by <time>.");
            return;
        }

        String description = deadlineText.substring(0, byIndex).trim();
        String by = deadlineText.substring(byIndex + 5).trim();
        if (description.isEmpty() || by.isEmpty()) {
            System.out.println("Bibi: A deadline needs both a description and a /by time.");
            return;
        }

        addTask(new Deadline(description, by), tasks);
    }

    /**
     * Creates an event from text in the form
     * {@code description /from start time /to end time}.
     *
     * @param eventText the event text after the {@code event} command
     * @param tasks the task list to update
     */
    private static void addEvent(String eventText, List<Task> tasks) {
        String normalizedText = eventText.toLowerCase(Locale.ROOT);
        int fromIndex = normalizedText.indexOf(" /from ");
        int toIndex = normalizedText.indexOf(" /to ");

        if (fromIndex <= 0 || toIndex <= fromIndex + 7) {
            System.out.println("Bibi: Use event <description> /from <start> /to <end>.");
            return;
        }

        String description = eventText.substring(0, fromIndex).trim();
        String from = eventText.substring(fromIndex + 7, toIndex).trim();
        String to = eventText.substring(toIndex + 5).trim();
        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            System.out.println("Bibi: An event needs a description, /from time, and /to time.");
            return;
        }

        addTask(new Event(description, from, to), tasks);
    }

    /**
     * Stores a newly created task and confirms the addition.
     *
     * @param task the new task
     * @param tasks the task list to update
     */
    private static void addTask(Task task, List<Task> tasks) {
        tasks.add(task);
        System.out.println(DIVIDER);
        System.out.println("Bibi: Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /**
     * Prints every task with its one-based task number and completion status.
     *
     * @param tasks tasks to print
     */
    private static void printTasks(List<Task> tasks) {
        System.out.println(DIVIDER);
        if (tasks.isEmpty()) {
            System.out.println("Bibi: Your task list is empty.");
        } else {
            System.out.println("Bibi: Here are the tasks in your list:");
            for (int index = 0; index < tasks.size(); index++) {
                System.out.println((index + 1) + ". " + tasks.get(index));
            }
        }
        System.out.println(DIVIDER);
    }

    /**
     * Marks the requested one-based task number complete when it exists.
     *
     * @param numberText the task number supplied after {@code mark}
     * @param tasks the current task list
     */
    private static void markTask(String numberText, List<Task> tasks) {
        Task task = findTask(numberText, tasks, "mark");
        if (task != null) {
            task.markComplete();
            System.out.println("Bibi: Marked task " + numberText + " as complete.");
        }
    }

    /**
     * Marks the requested one-based task number incomplete when it exists.
     *
     * @param numberText the task number supplied after {@code unmark}
     * @param tasks the current task list
     */
    private static void unmarkTask(String numberText, List<Task> tasks) {
        Task task = findTask(numberText, tasks, "unmark");
        if (task != null) {
            task.markIncomplete();
            System.out.println("Bibi: Unmarked task " + numberText + ", now incomplete.");
        }
    }

    /**
     * Finds a task using a one-based task number and reports invalid input.
     *
     * @param numberText the requested task number
     * @param tasks the current task list
     * @param command the command used for the error message
     * @return the matching task, or {@code null} when no valid task was selected
     */
    private static Task findTask(String numberText, List<Task> tasks, String command) {
        try {
            int taskNumber = Integer.parseInt(numberText);
            int taskIndex = taskNumber - 1;
            if (taskIndex < 0 || taskIndex >= tasks.size()) {
                System.out.println("Bibi: That task number does not exist.");
                return null;
            }
            return tasks.get(taskIndex);
        } catch (NumberFormatException exception) {
            System.out.println("Bibi: Use " + command + " followed by a task number, for example: "
                    + command + " 2");
            return null;
        }
    }
}
