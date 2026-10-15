package waddles;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Deals with all interactions with the user: reading input and
 * displaying messages.
 *
 * <p>Each message is built by a {@code format...} method that returns the
 * message as a String. The CLI-facing {@code show...} methods print that
 * String to the console, while the GUI (see {@link MainWindow}) calls the
 * {@code format...} methods directly so the exact same wording appears in
 * both interfaces without duplicating it.
 */
public class Ui {
    private static final DateTimeFormatter SCHEDULE_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d yyyy");

    private final Scanner scanner;

    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Formats the tasks that matched a find/search command.
     *
     * @param matches The matching tasks to display.
     * @return The formatted message.
     */
    public String formatFoundTasks(List<Task> matches) {
        if (matches.isEmpty()) {
            return "Oink? Nothing in the trough matches that.";
        }
        return "Found these snuffled up from your trough:\n" + formatNumbered(matches);
    }

    /**
     * Numbers a list of tasks from 1, one per line, e.g. "1.[T][X] read book".
     *
     * @param tasks The tasks to number.
     * @return The numbered tasks joined with newlines.
     */
    private String formatNumbered(List<Task> tasks) {
        return IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + "." + tasks.get(i))
                .collect(Collectors.joining("\n"));
    }

    /**
     * Displays the welcome banner and greeting shown at startup.
     */
    public void showWelcome() {
        String logo = " (\\_/)\n"
                + "( •,• )\n"
                + "( \")_(\")  WADDLES";
        showLine();
        System.out.println(logo);
        System.out.println(formatWelcomeMessage());
        showLine();
    }

    /**
     * Formats the greeting shown at startup.
     *
     * @return The formatted message.
     */
    public String formatWelcomeMessage() {
        return "Oink! I'm Waddles, your sparkly little trough-keeper. ✨\n"
                + "What shall we add to the trough today?";
    }

    public void showLine() {
        System.out.println("___________________________________________________________<3");
    }

    /**
     * Displays the farewell message shown when the user exits.
     */
    public void showGoodbye() {
        showLine();
        System.out.println(formatGoodbyeMessage());
        showLine();
    }

    /**
     * Formats the farewell message shown when the user exits.
     *
     * @return The formatted message.
     */
    public String formatGoodbyeMessage() {
        return "Byeee! Waddles is off to roll in the mud. ✨ Come back soon!";
    }

    /**
     * Reads a single line of input from the user.
     *
     * @return The line of input entered by the user.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    public void showError(String message) {
        System.out.println(message);
    }

    public void showLoadingError() {
        System.out.println(formatLoadingError());
    }

    /**
     * Formats the warning shown when saved data could not be read.
     *
     * @return The formatted message.
     */
    public String formatLoadingError() {
        return "Oink? Waddles couldn't sniff out your saved data, so we're starting with an empty trough.";
    }

    public void showSavingError() {
        System.out.println(formatSavingError());
    }

    /**
     * Formats the warning shown when tasks could not be saved to disk.
     *
     * @return The formatted message.
     */
    public String formatSavingError() {
        return "Uh-oh, Waddles tripped in the mud and couldn't save your trough to disk!";
    }

    /**
     * Displays a confirmation that a task was added.
     *
     * @param task The task that was added.
     * @param size The new total number of tasks.
     */
    public void showAdded(Task task, int size) {
        System.out.println(formatAdded(task, size));
    }

    /**
     * Formats a confirmation that a task was added.
     *
     * @param task The task that was added.
     * @param size The new total number of tasks.
     * @return The formatted message.
     */
    public String formatAdded(Task task, int size) {
        return "Oink-cellent! I've tossed this into the trough:\n  " + task
                + "\nThat's " + size + " " + taskWord(size) + " waiting to be gobbled up. ✨";
    }

    /**
     * Returns "task" or "tasks" depending on the count, so confirmation
     * messages read naturally whether there is one task or several.
     *
     * @param count The number of tasks.
     * @return "task" if count is 1, otherwise "tasks".
     */
    private String taskWord(int count) {
        return count == 1 ? "task" : "tasks";
    }

    public void showMarked(Task task) {
        System.out.println(formatMarked(task));
    }

    /**
     * Formats a confirmation that a task was marked as done.
     *
     * @param task The task that was marked.
     * @return The formatted message.
     */
    public String formatMarked(Task task) {
        return "Yay! Crossed off and oink-credibly satisfying. ✨\n  " + task;
    }

    /**
     * Formats a confirmation that a recurring task's occurrence was completed
     * and rescheduled to its next occurrence.
     *
     * @param task The task that was rescheduled.
     * @return The formatted message.
     */
    public String formatRecurred(Task task) {
        return "Yay! Done and dusted — Waddles already booked the next round for you. ✨\n  " + task;
    }

    public void showUnmarked(Task task) {
        System.out.println(formatUnmarked(task));
    }

    /**
     * Formats a confirmation that a task was marked as not done.
     *
     * @param task The task that was unmarked.
     * @return The formatted message.
     */
    public String formatUnmarked(Task task) {
        return "Alrighty, back into the trough it goes — not done yet:\n  " + task;
    }

    /**
     * Displays a confirmation that a task was removed.
     *
     * @param task The task that was removed.
     * @param size The new total number of tasks.
     */
    public void showDeleted(Task task, int size) {
        System.out.println(formatDeleted(task, size));
    }

    /**
     * Formats a confirmation that a task was removed.
     *
     * @param task The task that was removed.
     * @param size The new total number of tasks.
     * @return The formatted message.
     */
    public String formatDeleted(Task task, int size) {
        return "Noted! I've swept this one out of the sty:\n  " + task
                + "\nThat leaves " + size + " " + taskWord(size) + " in the trough. ✨";
    }

    /**
     * Formats the tasks scheduled on a given date, for the "schedule" command.
     *
     * @param date The date the schedule was requested for.
     * @param tasksOnDate The tasks occurring on that date, in original order.
     * @return The formatted message.
     */
    public String formatSchedule(LocalDate date, List<Task> tasksOnDate) {
        String header = "Here's the trough lineup for " + date.format(SCHEDULE_DATE_FORMAT) + ":";
        if (tasksOnDate.isEmpty()) {
            return header + "\nNothing scheduled — a whole day free to roll in the mud! ✨";
        }
        StringBuilder sb = new StringBuilder(header);
        for (int i = 0; i < tasksOnDate.size(); i++) {
            sb.append("\n").append(i + 1).append(".").append(tasksOnDate.get(i));
        }
        return sb.toString();
    }

    /**
     * Displays every task in the given list, numbered from 1.
     *
     * @param tasks The task list to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println(formatTaskList(tasks));
    }

    /**
     * Formats every task in the given list, numbered from 1.
     *
     * @param tasks The task list to display.
     * @return The formatted message.
     */
    public String formatTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            return "Your trough is empty — nothing to snort about yet!";
        }
        return formatNumbered(tasks.getAll());
    }
}
