package aster.ui;

import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import aster.task.Task;
import aster.task.TaskStatistics;

/**
 * Handles everything the user reads and types.
 *
 * <p>Every line Aster says and every line it reads passes through here, so the
 * wording and the layout of the conversation are decided in one place. Where each line
 * goes is decided when the interface is created: the text interface prints it to
 * standard output, while a graphical interface can collect the lines of one reply to
 * show them together.
 */
public class Ui {
    private static final String DIVIDER =
            "____________________________________________________________";

    private final Scanner scanner;
    private final Consumer<String> lineWriter;

    /**
     * Creates a user interface that reads from standard input and writes to standard
     * output.
     */
    public Ui() {
        // A lambda rather than a method reference, so standard output is looked up for
        // every line, exactly as it was when each line was printed directly.
        this(new Scanner(System.in), line -> System.out.println(line));
    }

    /**
     * Creates a user interface that reads nothing and hands every line it would show to
     * the given writer.
     *
     * <p>This lets a caller gather what Aster says in reply to one message without
     * reading from the keyboard or writing to the screen.
     *
     * @param lineWriter receives each line, without a line ending, in the order shown.
     */
    public Ui(Consumer<String> lineWriter) {
        this(new Scanner(""), lineWriter);
    }

    /**
     * Creates a user interface with the given source of commands and destination of
     * lines.
     *
     * @param scanner the source of the command lines the user types.
     * @param lineWriter receives each line shown to the user.
     */
    private Ui(Scanner scanner, Consumer<String> lineWriter) {
        this.scanner = scanner;
        this.lineWriter = lineWriter;
    }

    /**
     * Returns whether the user has typed another command.
     *
     * <p>This is also how the conversation ends cleanly when the input stops before
     * the user types {@code bye}.
     *
     * @return {@code true} if another line can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Returns the next command line, without the spaces around it.
     *
     * @return the line the user typed, trimmed.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Shows the line that separates one exchange from the next.
     */
    public void showLine() {
        print(DIVIDER);
    }

    /**
     * Greets the user at the start of the conversation, framed as an exchange of its own.
     */
    public void showWelcome() {
        showLine();
        showGreeting();
        showLine();
    }

    /**
     * Shows the lines that greet the user, without any framing.
     */
    public void showGreeting() {
        print("Hello! I'm Aster.");
        print("I'm a simple chatbot, and I'm glad you're here.");
        print("What can I do for you?");
    }

    /**
     * Says goodbye at the end of the conversation, framed as an exchange of its own.
     */
    public void showGoodbye() {
        showLine();
        showFarewell();
        showLine();
    }

    /**
     * Shows the line that ends the conversation, without any framing.
     */
    public void showFarewell() {
        print("Goodbye for now. Take care!");
    }

    /**
     * Reports that the saved tasks could not be read, framed on its own because no
     * conversation follows it.
     *
     * @param message the explanation to show the user.
     */
    public void showLoadingError(String message) {
        showLine();
        print(message);
        showLine();
    }

    /**
     * Reports that a command could not be carried out.
     *
     * @param message the explanation to show the user.
     */
    public void showError(String message) {
        print(message);
    }

    /**
     * Reports a task that has just been added, and how many tasks there are now.
     *
     * @param task the task that was added.
     * @param taskCount the number of tasks in the list afterwards.
     */
    public void showAdded(Task task, int taskCount) {
        print("Got it. I've added this task:");
        print("  " + task);
        showCount(taskCount);
    }

    /**
     * Reports a task that has just been removed, and how many tasks are left.
     *
     * @param task the task that was removed.
     * @param taskCount the number of tasks in the list afterwards.
     */
    public void showRemoved(Task task, int taskCount) {
        print("Noted. I've removed this task:");
        print("  " + task);
        showCount(taskCount);
    }

    /**
     * Reports a task that has just been marked as done.
     *
     * @param task the task that was marked.
     */
    public void showMarked(Task task) {
        print("Nice! I've marked this task as done:");
        print("  " + task);
    }

    /**
     * Reports a task that has just been marked as not done.
     *
     * @param task the task that was marked.
     */
    public void showUnmarked(Task task) {
        print("Alright, I've marked this task as not done yet:");
        print("  " + task);
    }

    /**
     * Reports a task that was already done when marking it was asked for.
     *
     * <p>This is not an error: the task is in the state that was asked for, so it is
     * shown as it stands rather than refused.
     *
     * @param task the task that was already done.
     */
    public void showAlreadyMarked(Task task) {
        print("This task is already marked as done:");
        print("  " + task);
    }

    /**
     * Reports a task that was already not done when unmarking it was asked for.
     *
     * @param task the task that was already not done.
     */
    public void showAlreadyUnmarked(Task task) {
        print("This task is already marked as not done:");
        print("  " + task);
    }

    /**
     * Shows the tasks in the order they were added.
     *
     * <p>An empty list shows nothing at all. The numbering shown to the user starts
     * at one, so it is offset from the position in the list.
     *
     * @param tasks the tasks to show.
     */
    public void showTasks(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            print((i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Shows the tasks that matched a search, or says that none did.
     *
     * <p>The heading names what is being shown, so a search that happens to match
     * every task cannot be mistaken for the whole list. The matches are numbered
     * from one across themselves, so these numbers are not the ones {@code mark},
     * {@code unmark} and {@code delete} take.
     *
     * @param tasks the matching tasks, in the order they are held.
     */
    public void showFound(List<Task> tasks) {
        if (tasks.isEmpty()) {
            print("No tasks match that keyword.");
            return;
        }
        print("Here are the matching tasks in your list:");
        showTasks(tasks);
    }

    /**
     * Shows how many tasks there are, how many are done, and how many of each type.
     *
     * <p>With no tasks there is nothing to count, so a single line says so instead of a
     * list of zeros and a percentage of nothing.
     *
     * @param statistics the statistics to show.
     */
    public void showStatistics(TaskStatistics statistics) {
        int total = statistics.total();
        if (total == 0) {
            print("You have no tasks yet, so there are no statistics to show.");
            return;
        }
        print("Here are your task statistics:");
        print("Total: " + total + " " + getTaskNoun(total));
        print("Completed: " + statistics.completed() + " (" + statistics.completionPercent() + "%)");
        print("Not completed: " + statistics.notCompleted());
        print("Todos: " + statistics.todos() + ", Deadlines: " + statistics.deadlines()
                + ", Events: " + statistics.events());
    }

    /**
     * Says that there are no tasks to show.
     *
     * <p>{@link #showTasks(List)} shows nothing for an empty list, which suits the text
     * interface, where the dividers around the exchange still mark the reply. A
     * graphical interface has no such dividers, so it says this instead of showing an
     * empty reply. The text interface never calls it.
     */
    public void showEmptyList() {
        print("Your list is empty.");
    }

    /**
     * Shows how many tasks the list holds, as shown after adding or removing one.
     *
     * @param taskCount the number of tasks in the list.
     */
    private void showCount(int taskCount) {
        print("Now you have " + taskCount + " " + getTaskNoun(taskCount)
                + " in the list.");
    }

    /**
     * Hands one line to wherever this interface sends what it shows.
     *
     * @param line the line to show, without a line ending.
     */
    private void print(String line) {
        lineWriter.accept(line);
    }

    /**
     * Returns the singular or plural form of {@code task} for a count.
     *
     * <p>Kept here rather than shared, so that no class outside the user interface has
     * to depend on it for its wording.
     *
     * @param count the number of tasks.
     * @return {@code "task"} if the count is one, otherwise {@code "tasks"}.
     */
    private static String getTaskNoun(int count) {
        return count == 1 ? "task" : "tasks";
    }
}
