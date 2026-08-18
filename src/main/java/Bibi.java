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
        TaskList tasks = new TaskList();

        printWelcomeMessage();
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("You: ");
                String input = scanner.nextLine().trim();

                if (input.equalsIgnoreCase("bye")) {
                    System.out.println("Bibi: Goodbye! Till next time...");
                    break;
                }

                try {
                    processCommand(input, tasks);
                } catch (BibiException exception) {
                    System.out.println("Bibi: " + exception.getMessage());
                }
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

    private static void printHelp() {
        System.out.println(DIVIDER);
        System.out.println("Bibi: Here are the commands I understand:");
        System.out.println("  todo <description>");
        System.out.println("  deadline <description> /by <time>");
        System.out.println("  event <description> /from <start> /to <end>");
        System.out.println("  list");
        System.out.println("  mark <number>");
        System.out.println("  unmark <number>");
        System.out.println("  remove <number>");
        System.out.println("  help");
        System.out.println("  bye");
        System.out.println(DIVIDER);
    }

    /**
     * Routes one user command to the appropriate task operation.
     *
     * @param input the user's complete command
     * @param tasks the task list to update or display
     * @throws BibiException if the command is empty, unknown, or malformed
     */
    private static void processCommand(String input, TaskList tasks) throws BibiException {
        String command = input.toLowerCase(Locale.ROOT); //Local.root uses user's local settings to determine case conversion

        if (input.isEmpty()) {
            throw new BibiException("Please enter a command.");
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
        } else if (command.startsWith("remove ")) {
            removeTask(input.substring(7).trim(), tasks);
        } else if (command.startsWith("help ")) {
            printHelp();
        } else {
            throw new BibiException("I don't understand that command. "
                    + "Try todo, deadline, event, list, mark, unmark, or bye.");
        }
    }

    /**
     * Creates a ToDo from its description.
     *
     * @param description the ToDo description
     * @param tasks the task list to update
     * @throws BibiException if the description is missing
     */
    private static void addTodo(String description, TaskList tasks) throws BibiException {
        addTask(new Todo(description), tasks);
    }

    /**
     * Creates a deadline from text in the form {@code description /by time}.
     *
     * @param deadlineText the deadline text after the {@code deadline} command
     * @param tasks the task list to update
     * @throws BibiException if the deadline is missing required information
     */
    private static void addDeadline(String deadlineText, TaskList tasks) throws BibiException {
        String normalizedText = deadlineText.toLowerCase(Locale.ROOT);
        int byIndex = normalizedText.indexOf(" /by ");

        if (byIndex < 0) {
            throw new BibiException("Use deadline <description> /by <time>.");
        }

        String description = deadlineText.substring(0, byIndex).trim();
        String by = deadlineText.substring(byIndex + 5).trim();
        addTask(new Deadline(description, by), tasks);
    }

    /**
     * Creates an event from text in the form
     * {@code description /from start time /to end time}.
     *
     * @param eventText the event text after the {@code event} command
     * @param tasks the task list to update
     * @throws BibiException if the event is missing required information
     */
    private static void addEvent(String eventText, TaskList tasks) throws BibiException {
        String normalizedText = eventText.toLowerCase(Locale.ROOT);
        int fromIndex = normalizedText.indexOf(" /from ");
        int toIndex = normalizedText.indexOf(" /to ");

        if (fromIndex < 0 || toIndex < fromIndex + 7) {
            throw new BibiException("Use event <description> /from <start> /to <end>.");
        }

        String description = eventText.substring(0, fromIndex).trim();
        String from = eventText.substring(fromIndex + 7, toIndex).trim();
        String to = eventText.substring(toIndex + 5).trim();
        addTask(new Event(description, from, to), tasks);
    }

    /**
     * Stores a newly created task and confirms the addition.
     *
     * @param task the new task
     * @param tasks the task list to update
     */
    private static void addTask(Task task, TaskList tasks) {
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
    private static void printTasks(TaskList tasks) throws BibiException {
        System.out.println(DIVIDER);
        if (tasks.isEmpty()) {
            System.out.println("Bibi: Your task list is empty.");
        } else {
            System.out.println("Bibi: Here are the tasks in your list:");
            for (int taskNumber = 1; taskNumber <= tasks.size(); taskNumber++) {
                System.out.println(taskNumber + ". " + tasks.get(taskNumber));
            }
        }
        System.out.println(DIVIDER);
    }

    /**
     * Marks the requested one-based task number complete when it exists.
     *
     * @param numberText the task number supplied after {@code mark}
     * @param tasks the current task list
     * @throws BibiException if the supplied task number is invalid
     */
    private static void markTask(String numberText, TaskList tasks) throws BibiException {
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
     * @throws BibiException if the supplied task number is invalid
     */
    private static void unmarkTask(String numberText, TaskList tasks) throws BibiException {
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
     * @return the matching task
     * @throws BibiException if the task number is not a valid list entry
     */
    private static Task findTask(String numberText, TaskList tasks, String command)
            throws BibiException {
        try {
            return tasks.get(Integer.parseInt(numberText));
        } catch (NumberFormatException exception) {
            throw new BibiException("Use " + command + " followed by a task number, for example: "
                    + command + " 2");
        }
    }

    private static void removeTask(String index, TaskList tasks) throws BibiException {
        try {
            tasks.remove(Integer.parseInt(index));
            System.out.println("Bibi: Task " + index + " removed.");
        } catch (NumberFormatException exception) {
            throw new BibiException("Use remove followed by a task number, for example: remove 2");
        }
    }
}
