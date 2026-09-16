package eve;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import eve.command.CommandWord;
import eve.task.Task;

/**
 * Deals with all interactions with the user: reading command lines from
 * standard input, and printing every message the chatbot shows (the
 * greeting, task confirmations, error messages, etc.). Keeping this in one
 * class means the exact wording/formatting of the chatbot's output lives in
 * a single place, separate from the logic that decides what to say.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String STAR_LINE = "*".repeat(21);
    private static final String BANNER = STAR_LINE + "\n"
            + "***     E V E     ***\n"
            + STAR_LINE;

    private final Scanner scanner = new Scanner(System.in);

    /** Prints the banner and greeting, and points the user at the {@code help} command. */
    public void showWelcome() {
        List<String> lines = new ArrayList<>();
        lines.add(BANNER);
        lines.add("");
        lines.add("HEYYY! I'm Eve!");
        lines.add("I'm SO ready to help you crush your to-do list today! What's first?");
        lines.add("");
        lines.add("(Type help anytime to see everything I can do!)");
        showLines(lines);
    }

    /**
     * Returns Eve's greeting as one plain-text block (no banner or console
     * dividers), for the GUI to show as its first chat message.
     *
     * @return the greeting.
     */
    public String getWelcomeMessage() {
        return "HEYYY! I'm Eve!\nI'm SO ready to help you crush your to-do list today! "
                + "(Type help anytime to see everything I can do!)";
    }

    /** Prints the full list of available commands, e.g. in response to the {@code help} command. */
    public void showHelp() {
        List<String> lines = new ArrayList<>();
        lines.add("Here's everything I can do:");
        lines.add("");
        lines.addAll(Arrays.stream(CommandWord.values())
                .flatMap(commandWord -> Stream.of(
                        "• " + commandWord.getUsage(),
                        "   " + commandWord.getDescription(),
                        ""))
                .toList());
        showLines(lines.subList(0, lines.size() - 1));
    }

    /**
     * Reads one line of user input.
     *
     * @return the full line the user typed, unmodified.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the farewell message shown when the user exits with "bye". */
    public void showGoodbye() {
        System.out.println("Byeee! Go crush it out there -- see you again soon!");
        System.out.println(LINE);
    }

    /**
     * Prints every task in the list, numbered from 1.
     *
     * @param tasks the tasks to print, in order.
     */
    public void showTaskList(List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        lines.add("Here's everything on your list:");
        lines.addAll(numberedLines(tasks));
        showLines(lines);
    }

    /**
     * Prints the tasks whose description matched a search keyword, or a
     * "no matches" message if none did.
     *
     * @param matches the matching tasks, in list order.
     */
    public void showMatchingTasks(List<Task> matches) {
        List<String> lines = new ArrayList<>();
        if (matches.isEmpty()) {
            lines.add("Hmm, no matches in your list -- but don't stop now!");
        } else {
            lines.add("Found these matches for you:");
            lines.addAll(numberedLines(matches));
        }
        showLines(lines);
    }

    /**
     * Prints the tasks that occur on a given date, or a "no tasks" message
     * if none do.
     *
     * @param date the date that was queried.
     * @param matches the tasks that occur on {@code date}, in list order.
     */
    public void showTasksOnDate(LocalDate date, List<Task> matches) {
        List<String> lines = new ArrayList<>();
        if (matches.isEmpty()) {
            lines.add("Nothing going on " + date.format(Task.DISPLAY_FORMAT) + " -- nice and clear!");
        } else {
            lines.add("Here's what's happening on " + date.format(Task.DISPLAY_FORMAT) + ":");
            lines.addAll(numberedLines(matches));
        }
        showLines(lines);
    }

    /**
     * Prints every dated task (deadlines and events) in chronological
     * order, or a "nothing scheduled" message if there are none.
     *
     * @param scheduledTasks the dated tasks, earliest first.
     */
    public void showSchedule(List<Task> scheduledTasks) {
        List<String> lines = new ArrayList<>();
        if (scheduledTasks.isEmpty()) {
            lines.add("Your schedule's wide open -- blank canvas energy!");
        } else {
            lines.add("Here's your schedule, all lined up:");
            lines.addAll(numberedLines(scheduledTasks));
        }
        showLines(lines);
    }

    /** Prints confirmation that a task was marked as done. */
    public void showTaskMarked(Task task) {
        showLines("YESSS! Marked as done:", "  " + task);
    }

    /** Prints confirmation that a task was marked as not done. */
    public void showTaskUnmarked(Task task) {
        showLines("Got it, back on the list:", "  " + task);
    }

    /**
     * Prints confirmation that a task was added.
     *
     * @param task the task that was added.
     * @param taskCount how many tasks are in the list after adding it.
     */
    public void showTaskAdded(Task task, int taskCount) {
        showLines("Added it, let's gooo:", "  " + task,
                "That's " + taskCount + " tasks -- you're basically unstoppable!");
    }

    /**
     * Prints confirmation that a task was removed.
     *
     * @param task the task that was removed.
     * @param taskCount how many tasks remain in the list after removing it.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        showLines("Poof, gone! Removed:", "  " + task, "Down to " + taskCount + " tasks -- look at you go!");
    }

    /** Prints an error message, e.g. from a caught {@link EveException}. */
    public void showError(String message) {
        showLines(message);
    }

    /**
     * Prints a warning that the saved task list could not be loaded, and
     * that the chatbot is starting with an empty list instead.
     *
     * @param message detail of what went wrong, e.g. an I/O error message.
     */
    public void showLoadingError(String message) {
        showLines(message + " No worries, starting fresh!");
    }

    /**
     * Prints one or more message lines wrapped between two divider lines --
     * the shape shared by every show method above that has a fixed, known
     * number of lines to print (as opposed to one line per task, which
     * varies at runtime; see the {@link #showLines(List)} overload for that
     * case).
     *
     * @param lines the message lines to print, in order.
     */
    private void showLines(String... lines) {
        System.out.println(LINE);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(LINE);
    }

    /**
     * Prints message lines built up at runtime (e.g. one per task) wrapped
     * between two divider lines, by delegating to {@link #showLines(String...)}.
     *
     * @param lines the message lines to print, in order.
     */
    private void showLines(List<String> lines) {
        showLines(lines.toArray(new String[0]));
    }

    /**
     * Numbers each task from 1 (e.g. "1.[T][ ] read book"), based on its
     * position in {@code tasks}. Shared by every show method above that
     * lists tasks, since they all number their tasks the same way.
     *
     * @param tasks the tasks to number, in order.
     */
    private static List<String> numberedLines(List<Task> tasks) {
        return IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + "." + tasks.get(i))
                .toList();
    }
}
