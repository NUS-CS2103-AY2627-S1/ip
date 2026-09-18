package computa.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import computa.task.Task;
import computa.util.DateTimeParser;

/** Handles all text displayed by Computa. */
public class Ui {
    /** Separates the greeting and each command response in the console output. */
    private static final String SEPARATOR = "____________________________________________________________";
    /** Receives each line produced by the chatbot. */
    private final Consumer<String> output;

    /** Creates a UI that writes output to the console. */
    public Ui() {
        this(System.out::println);
    }

    /**
     * Creates a UI that sends output to the supplied receiver.
     *
     * @param output receiver for each complete line of output.
     */
    public Ui(Consumer<String> output) {
        this.output = output;
    }

    /** Sends one complete chatbot message to the configured output receiver. */
    private void printLine(String line) {
        output.accept(line);
    }

    /** Joins related lines into one message so GUI output is not needlessly fragmented. */
    private void printMessage(String... lines) {
        printLine(String.join("\n", lines));
    }

    /** Prints Computa's greeting. */
    public void showGreeting() {
        showSeparator();
        printMessage("                         COMPUTA",
                "Konnichiwassup! °˖✧◝(⁰▿⁰)◜✧˖°",
                "I'm your personal Computa ｡:ﾟ(｡ﹷ ‸ ﹷ ✿)",
                "What can I do for you?");
        showSeparator();
    }

    /** Prints the separator between command responses. */
    public void showSeparator() {
        printLine(SEPARATOR);
    }

    /** Prints Computa's farewell. */
    public void showFarewell() {
        printLine("Noooo don't go!!! Hmph. Fine... Hope to see you again soon!");
    }

    /** Prints all tasks and their completion status. */
    public void showTasks(ArrayList<Task> tasks) {
        ArrayList<String> taskMessageLines = new ArrayList<>();
        taskMessageLines.add("We've got so much to do (⋟﹏⋞)");
        taskMessageLines.add("I guess I'll have to spend more time with you "
                + "(⁄ ⁄>⁄ ▽ ⁄<⁄ ⁄)");
        for (int i = 0; i < tasks.size(); i++) {
            taskMessageLines.add((i + 1) + "." + tasks.get(i));
        }
        printLine(String.join("\n", taskMessageLines));
    }

    /** Prints the acknowledgement and the task list after sorting. */
    public void showSortedTasks(ArrayList<Task> tasks) {
        printLine("Tasks sorted by date.");
        showTasks(tasks);
    }

    /** Prints tasks whose descriptions contain the supplied keyword, ignoring case. */
    public void showTasksContaining(ArrayList<Task> tasks, String keyword) {
        printLine("Goshujin-Sama, can't you do this yourself?");
        printLine("Hmph. I guess I have no choice.");

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Integer> matchingTaskIndexes = IntStream.range(0, tasks.size())
                .filter(index -> tasks.get(index).getDescription().toLowerCase(Locale.ROOT)
                        .contains(normalizedKeyword))
                .boxed()
                .collect(Collectors.toList());
        matchingTaskIndexes.forEach(index -> printLine((index + 1) + "." + tasks.get(index)));
        if (matchingTaskIndexes.isEmpty()) {
            printLine("No matching tasks found.");
        }
    }

    /** Prints deadlines and events that occur on the requested date. */
    public void showTasksOnDate(ArrayList<Task> tasks, LocalDate date) {
        printLine("Tasks on " + DateTimeParser.formatDateForDisplay(date) + ":");
        List<Task> matchingTasks = tasks.stream()
                .filter(task -> task.occursOn(date))
                .collect(Collectors.toList());
        IntStream.range(0, matchingTasks.size())
                .forEach(index -> printLine((index + 1) + "." + matchingTasks.get(index)));
        if (matchingTasks.isEmpty()) {
            printLine("No deadlines or events found.");
        }
    }

    /** Prints the acknowledgement shown after a task is added. */
    public void showAddedTask(Task task, int taskCount) {
        printLine("More work? Don't overwork yourself, Goshujin-Sama ໒( ⇀ ‸ ↼ )७");
        printLine("  " + task);
        printLine("Now you have " + taskCount + " tasks in the list. (⋟﹏⋞)");
        printLine("(.づ◡﹏◡)づ. When will we get some alone time together?");
    }

    /** Prints the acknowledgement shown after marking or unmarking a task. */
    public void showStatusUpdate(Task task, boolean isMark) {
        if (isMark) {
            printLine("Yatta! (ᗒᗨᗕ) I knew you could do it (✧ᴗ✧✿) \n");
        } else {
            printLine("Gambare, Goshujin-Sama ! ˚‧º·( 。ᗒ ‸ ◕✿) \n");
        }
        printLine("  [" + task.getStatusIcon() + "] "
                + task.getDisplayDescription());
    }

    /** Prints the acknowledgement shown after deleting a task. */
    public void showDeletedTask(Task deletedTask, int taskCount) {
        printLine("Goshujin-Sama, you don't want to do this with me anymore? "
                + "(๑˃̣̣̥⌓˂̣̣̥)");
        printLine("  " + deletedTask);
        printLine("Now you have " + taskCount + " tasks in the list.");
        printLine("Not that I want to hang out with you anyway. (๑•́ ₃ •̀๑)");
    }

    /** Prints a user-input error without terminating the session. */
    public void showError(String message) {
        printLine(message);
    }
}
