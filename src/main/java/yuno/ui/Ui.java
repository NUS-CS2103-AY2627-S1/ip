package yuno.ui;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import yuno.exception.InvalidTaskNumberException;
import yuno.task.Task;
import yuno.task.TaskList;

/**
 * Reads commands from the user and displays chatbot messages and task information.
 */
public class Ui {
    /** Separates sections of chatbot output. */
    private static final String DIVIDER = "_".repeat(50);
    /** Reads commands entered through standard input. */
    private final Scanner scanner = new Scanner(System.in);
    /** Most recent response produced by Yuno. */
    private String latestResponse = "";

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
    }

    /**
     * Displays the chatbot banner and greeting.
     */
    public void printGreeting() {
        String banner = """
                __   __ _   _ _   _  ___
                \\ \\ / /| | | | \\ | |/ _ \\
                 \\ V / | | | |  \\| | | | |
                  | |  | |_| | |\\  | |_| |
                  |_|   \\___/|_| \\_|\\___/
                """;
        System.out.println(DIVIDER);
        printLines(banner, "I'm Yuno.", "Can we just get this over quickly?");
    }

    /**
     * Displays the farewell message.
     */
    public void printBye() {
        printLines("Finally! Bye. I'm leaving!");
    }

    /**
     * Displays confirmation that the specified task was added.
     *
     * @param task Added task to display.
     */
    public void printAddTask(Task task) {
        printLines("Added:", task.toString(), "Just another task you won't finish.");
    }

    /**
     * Displays every task in the specified task list.
     *
     * @param taskList Task list to display.
     * @throws InvalidTaskNumberException If a task cannot be retrieved from the task list.
     */
    public void printList(TaskList taskList) throws InvalidTaskNumberException {
        StringBuilder output = new StringBuilder();
        if (taskList.getCount() == 0) {
            output.append("Wow, not even a single task? You are so lazy.\n");
        } else {
            output.append("Wow. Look at how slow you are at completing these tasks.\n");
            for (int i = 0; i < taskList.getCount(); i++) {
                output.append(i + 1);
                output.append(". ").append(taskList.getTask(i + 1)).append("\n");
            }
        }
        printLines(output.toString());
    }

    /**
     * Displays every available command and its expected format.
     */
    public void printHelp() {
        printLines("""
            You forgot the commands already? Fine. Pay attention this time.

            Dates accept yyyy-MM-dd or yyyy-MM-dd HHmm.

            TASKS
              todo <description>
                Adds a task without a date.

              deadline <description> /by <yyyy-MM-dd HHmm>
                Adds a task that must be completed by a particular time.

              event <description> /from <yyyy-MM-dd HHmm> /to <yyyy-MM-dd HHmm>
                Adds an event with a start and end time.

            MANAGE
              list
                Shows all your stored responsibilities.

              mark <task number>
                Marks a task as complete. Miracles do happen.

              unmark <task number>
                Marks a task as incomplete again. Of course.

              delete <task number>
                Removes a task.

              clear
                Removes every task.

            SEARCH AND SORT
              find <text>
                Finds tasks containing the given text.

              find /date <yyyy-MM-dd>
                Finds tasks relevant to the given date.

              sort
                Sorts dated tasks from earliest to latest.

              sort /order <asc|desc>
                Sorts dated tasks in the chosen order.

            OTHER
              help
                Shows this list, since apparently you need it.

              bye
                Exits Yuno. Finally.
            """);
    }

    /**
     * Displays tasks that are relevant for a requested date.
     *
     * @param matchingTasks Tasks relevant for the requested date.
     */
    public void printTasksForDate(List<Task> matchingTasks) {
        StringBuilder output = new StringBuilder();
        if (matchingTasks.isEmpty()) {
            output.append(
                    "You have nothing. What I see is just someone being lazy on this particular date.\n");
        } else {
            output.append("You are not even capable to finish all these in one go.\n");
            output.append(formatTasks(matchingTasks));
        }
        printLines(output.toString());
    }

    /**
     * Displays tasks whose descriptions contain requested text.
     *
     * @param matchingTasks Tasks with descriptions containing the requested text.
     */
    public void printTasksForName(List<Task> matchingTasks) {
        StringBuilder output = new StringBuilder();
        if (matchingTasks.isEmpty()) {
            output.append("You have nothing. You must be really happy since you are so lazy.\n");
        } else {
            output.append("So many task sharing a word. Could you be repeating task to feel accomplished?\n");
            output.append(formatTasks(matchingTasks));
        }
        printLines(output.toString());
    }

    /**
     * Formats the specified tasks as a bulleted list.
     *
     * @param tasks Tasks to format.
     * @return Display-ready bulleted task list.
     */
    private String formatTasks(List<Task> tasks) {
        return tasks.stream()
                .map(task -> "- " + task)
                .collect(Collectors.joining("\n", "", "\n"));
    }

    /**
     * Displays confirmation that the specified task was marked as completed.
     *
     * @param task Task that was marked as completed.
     */
    public void printMarkTask(Task task) {
        printLines(
                "You actually completed a task? Bet it's the only task you'll ever complete.",
                task.toString());
    }

    /**
     * Displays confirmation that the specified task was marked as incomplete.
     *
     * @param task Task that was marked as incomplete.
     */
    public void printUnmarkTask(Task task) {
        printLines("Wow! So you lied about completing it? Typical behavior from you.", task.toString());
    }

    /**
     * Displays confirmation that the specified task was deleted.
     *
     * @param task Deleted task to display.
     */
    public void printDeleteTask(Task task) {
        printLines("Wow! Did you give up, or did you actually finish it?", task.toString());
    }

    /**
     * Displays confirmation that every task was removed.
     */
    public void printTasksCleared() {
        printLines("Finally. Now that everything is gone, can I go now?");
    }

    /**
     * Displays the specified exception message.
     *
     * @param message Exception message to display.
     */
    public void printException(String message) {
        printLines(message);
    }

    /**
     * Returns the next command entered by the user.
     *
     * @return Command entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the supplied lines and stores them as the latest response.
     *
     * @param lines Lines to display in the response.
     */
    private void printLines(String... lines) {
        latestResponse = String.join("\n", lines);
        System.out.print(latestResponse);

        if (!latestResponse.endsWith("\n")) {
            System.out.println();
        }

        System.out.println(DIVIDER);
    }

    /**
     * Returns the most recent response produced by Yuno.
     *
     * @return Most recent Yuno response.
     */
    public String getResponse() {
        return latestResponse;
    }
}
