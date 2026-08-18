import java.util.Scanner;

/**
 * Provides a command-line task manager named Mona.
 */
public class Mona {
    private static final int MAXIMUM_TASKS = 100;
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String TODO_COMMAND = "todo ";
    private static final String DEADLINE_COMMAND = "deadline ";
    private static final String EVENT_COMMAND = "event ";
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_START_SEPARATOR = " /from ";
    private static final String EVENT_END_SEPARATOR = " /to ";
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

        printFormatted(BANNER
                + "\n✨ Hello, I'm Mona.\nThe constellations lie reflected in the water tonight. "
                + "What fate shall we divine?");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Mona > ");
            String userInput = scanner.nextLine();

            if (userInput.startsWith("mark ")) {
                markTask(tasks, taskCount, userInput, true);
            } else if (userInput.startsWith("unmark ")) {
                markTask(tasks, taskCount, userInput, false);
            } else if (userInput.equals("bye")) {
                printFormatted("✨ Farewell. May the stars guide you until we meet again.");
                break;
            } else if (userInput.equals("list")) {
                printTasks(tasks, taskCount);
            } else if (userInput.startsWith(TODO_COMMAND)) {
                String description = userInput.substring(TODO_COMMAND.length());
                taskCount = addTask(tasks, taskCount, new Todo(description));
            } else if (userInput.startsWith(DEADLINE_COMMAND)) {
                taskCount = addDeadline(tasks, taskCount, userInput);
            } else if (userInput.startsWith(EVENT_COMMAND)) {
                taskCount = addEvent(tasks, taskCount, userInput);
            } else {
                printFormatted("❌ That is not written in the stars I can read. Try a todo, deadline, or event.");
            }
        }
    }

    private static int addTask(Task[] tasks, int taskCount, Task task) {
        if (taskCount >= tasks.length) {
            printFormatted("❌ The list of fates overflows. No more tasks can surface tonight.");
            return taskCount;
        }

        tasks[taskCount] = task;
        int updatedTaskCount = taskCount + 1;
        printFormatted("✅ Your fate is rewritten. I've added this task:\n  " + task
                + "\nNow you have " + updatedTaskCount + " tasks in the list.");
        return updatedTaskCount;
    }

    private static int addDeadline(Task[] tasks, int taskCount, String userInput) {
        int separatorIndex = userInput.indexOf(DEADLINE_SEPARATOR, DEADLINE_COMMAND.length());
        if (separatorIndex < 0) {
            printFormatted("❌ Even the stars need a fixed point. Specify the deadline using /by.");
            return taskCount;
        }

        String description = userInput.substring(DEADLINE_COMMAND.length(), separatorIndex);
        String deadline = userInput.substring(separatorIndex + DEADLINE_SEPARATOR.length());
        return addTask(tasks, taskCount, new Deadline(description, deadline));
    }

    private static int addEvent(Task[] tasks, int taskCount, String userInput) {
        int startSeparatorIndex = userInput.indexOf(EVENT_START_SEPARATOR, EVENT_COMMAND.length());
        int endSeparatorIndex = userInput.indexOf(EVENT_END_SEPARATOR,
                startSeparatorIndex + EVENT_START_SEPARATOR.length());
        if (startSeparatorIndex < 0 || endSeparatorIndex < 0) {
            printFormatted("❌ Fate needs both a dawn and a dusk. Specify the event using /from and /to.");
            return taskCount;
        }

        String description = userInput.substring(EVENT_COMMAND.length(), startSeparatorIndex);
        String start = userInput.substring(startSeparatorIndex + EVENT_START_SEPARATOR.length(), endSeparatorIndex);
        String end = userInput.substring(endSeparatorIndex + EVENT_END_SEPARATOR.length());
        return addTask(tasks, taskCount, new Event(description, start, end));
    }

    private static void printFormatted(String text) {
        System.out.println(SEPARATOR);
        System.out.println(text);
        System.out.println(SEPARATOR);
    }

    private static void printTasks(Task[] tasks, int taskCount) {
        StringBuilder taskList = new StringBuilder("✨ Here is what the stars reveal:");
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
            printFormatted("❌ No such fate is written in the constellations. Please enter a valid task number.");
            return;
        }

        if (taskNumber < 1 || taskNumber > taskCount) {
            printFormatted("❌ No such fate is written in the constellations. Please enter a valid task number.");
            return;
        }

        Task task = tasks[taskNumber - 1];

        if (shouldMarkAsDone) {
            task.markAsDone();
            printFormatted("✅ The stars align. I've marked this task as done:\n  " + task);
        } else {
            task.markAsNotDone();
            printFormatted("❌ The constellation fades. I've marked this task as not done yet:\n  " + task);
        }
    }
}
