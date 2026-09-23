package sallman;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import sallman.task.Task;
import sallman.task.TaskDate;


/**
 * Everything the user sees and types.
 * <p>
 * Keeping the wording here means the rest of the app decides what happened,
 * not how to phrase it, and the console is the only thing that has to change
 * if the chatbot is later given a different kind of interface.
 */
public class Ui {

    /** Horizontal rule that separates the chatbot's replies from the user's input. */
    private static final String DIVIDER =
            "____________________________________________________________";

    /**
     * ASCII-art banner spelling "saLLMan", shown once when the chatbot starts.
     * Each backslash in the art is escaped as {@code \}, so the string looks
     * wider in the source than it does on screen.
     */
    private static final String BANNER =
              "              _      _      __  __\n"
            + " ___    __ _ | |    | |    |  \\/  |  __ _  _ __\n"
            + "/ __|  / _` || |    | |    | |\\/| | / _` || '_ \\\n"
            + "\\__ \\ | (_| || |___ | |___ | |  | || (_| || | | |\n"
            + "|___/  \\__,_||_____||_____||_|  |_| \\__,_||_| |_|";

    /** Reads the user's commands from the console. */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Whether replies are printed to the console as well as collected.
     * <p>
     * The console front end prints as it goes; the GUI collects instead and
     * asks for the text when the command is finished.
     */
    private final boolean isPrintingToConsole;

    /** Replies said since the last {@link #drainText()}, without decoration. */
    private final StringBuilder saidText = new StringBuilder();

    /** Creates a user interface that prints to the console. */
    public Ui() {
        this(true);
    }

    /**
     * Creates a user interface, optionally silent.
     *
     * @param isPrintingToConsole false to collect replies without printing them
     */
    public Ui(boolean isPrintingToConsole) {
        this.isPrintingToConsole = isPrintingToConsole;
    }

    /**
     * Returns everything said since this was last called, and forgets it.
     * <p>
     * The text has no dividers or indentation: those belong to the console,
     * whereas a GUI puts each reply in a box of its own.
     *
     * @return the replies since the last call, one line each
     */
    public String drainText() {
        String text = saidText.toString().strip();
        saidText.setLength(0);
        return text;
    }

    /**
     * Prints the given lines as one chatbot reply, wrapped between horizontal
     * rules so replies stand out from what the user typed.
     *
     * @param lines lines of the reply, printed in order
     */
    public void say(String... lines) {
        if (isPrintingToConsole) {
            System.out.println("    " + DIVIDER);
            for (String line : lines) {
                System.out.println("     " + line);
            }
            System.out.println("    " + DIVIDER);
            System.out.println();
        }
        for (String line : lines) {
            saidText.append(line).append(System.lineSeparator());
        }
    }

    /**
     * Shows the banner and greeting.
     *
     * @param name the name the chatbot introduces itself with
     */
    public void showWelcome(String name) {
        if (isPrintingToConsole) {
            System.out.println(BANNER);
        }
        say("Hello! I'm " + name + ", your Large Language (task) Manager.",
                "How can I assist you with your tasks today?");
    }

    /** Shows the parting message. */
    public void showGoodbye() {
        say("Thank you for chatting with me! I hope this was helpful. Goodbye!");
    }

    /**
     * Reports a problem with what the user asked for.
     *
     * @param e the problem, carrying its explanation and any suggested fix
     */
    public void showError(SallmanException e) {
        say(e.toLines());
    }

    /**
     * Warns about saved lines that could not be read, naming each one so a
     * hand-edited data file can be corrected.
     *
     * @param skippedLines descriptions of the skipped lines, possibly empty
     */
    public void showSkippedLines(List<String> skippedLines) {
        if (skippedLines.isEmpty()) {
            return;
        }
        boolean isOneLine = skippedLines.size() == 1;
        List<String> replyLines = new ArrayList<>();
        replyLines.add("I noticed " + skippedLines.size()
                + (isOneLine ? " unreadable line" : " unreadable lines")
                + " in your saved data, so I skipped " + (isOneLine ? "it:" : "them:"));
        replyLines.addAll(skippedLines);
        replyLines.add("Everything else loaded perfectly! I'll drop the unreadable "
                + (isOneLine ? "line" : "lines"));
        replyLines.add("the next time your list changes.");
        say(replyLines.toArray(new String[0]));
    }

    /**
     * Confirms that a task was added, and reports the new total.
     *
     * @param task      the task just added
     * @param taskCount number of tasks now in the list
     */
    public void showAdded(Task task, int taskCount) {
        say("Certainly! What a wonderful task. I've added it:", "  " + task,
                taskCountSummary(taskCount));
    }

    /**
     * Confirms that a task was removed, and reports the new total.
     *
     * @param task      the task just removed
     * @param taskCount number of tasks left in the list
     */
    public void showRemoved(Task task, int taskCount) {
        say("Of course! I've removed this task:", "  " + task, taskCountSummary(taskCount));
    }

    /**
     * Confirms that a task was marked done.
     *
     * @param task the task, already updated
     */
    public void showMarked(Task task) {
        say("Great job! I've marked this task as done:", "  " + task);
    }

    /**
     * Confirms that a task was marked not done.
     *
     * @param task the task, already updated
     */
    public void showUnmarked(Task task) {
        say("No problem! I've marked this task as not done yet:", "  " + task);
    }

    /**
     * Confirms a change to a task's tags, or says there was nothing to change.
     *
     * @param task         the task, already updated
     * @param isAddingTags true if tags were being attached rather than removed
     * @param hasChanged   false when the task already had, or already lacked,
     *                     every tag named
     */
    public void showTagged(Task task, boolean isAddingTags, boolean hasChanged) {
        if (!hasChanged) {
            say(isAddingTags
                            ? "It looks like that task already has every tag you named:"
                            : "It looks like that task has none of the tags you named:",
                    "  " + task);
            return;
        }
        say(isAddingTags ? "Absolutely! I've tagged this task:" : "Sure thing! I've removed those tags:",
                "  " + task);
    }

    /**
     * Confirms that the last change was undone.
     *
     * @param taskCount number of tasks in the restored list
     */
    public void showUndone(int taskCount) {
        say("I apologise for any confusion! I've put your list back the way it was.",
                taskCountSummary(taskCount));
    }

    /**
     * Shows the task list in its new order.
     *
     * @param tasks     the tasks, already sorted
     * @param orderName what they were sorted by, named as the user typed it
     */
    public void showSorted(TaskList tasks, String orderName) {
        sayNumbered("Here's a carefully sorted overview of your list, by " + orderName + ":",
                tasks.asList());
    }

    /**
     * Shows the whole task list, numbered from 1.
     *
     * @param tasks the tasks to show
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.size() == 0) {
            // A heading with nothing under it reads as if the command half-worked.
            say("Great question! Your list is currently empty.",
                    "Would you like me to help you add a task? Try: todo read book");
            return;
        }
        sayNumbered("Great question! Here are the tasks in your list:", tasks.asList());
    }

    /**
     * Shows a heading followed by tasks numbered from 1.
     * <p>
     * The lines are gathered into an array and handed to {@link #say(String...)}
     * as its varargs argument, which is what lets one reply hold a heading and
     * however many tasks there happen to be.
     *
     * @param heading the line introducing the tasks
     * @param items   the tasks to number, in the order to show them
     */
    private void sayNumbered(String heading, List<Task> items) {
        String[] lines = Stream.concat(
                        Stream.of(heading),
                        IntStream.range(0, items.size())
                                .mapToObj(i -> (i + 1) + "." + items.get(i)))
                .toArray(String[]::new);
        say(lines);
    }

    /**
     * Shows a heading followed by some of the tasks, each numbered by its place
     * in the whole list.
     * <p>
     * Those are the numbers mark, delete and tag act on, so a task found by a
     * search can be changed using the number it is shown with.
     *
     * @param heading the line introducing the tasks
     * @param tasks   the whole list
     * @param matches the tasks to show, all of them in that list
     */
    private void sayWithListNumbers(String heading, TaskList tasks, List<Task> matches) {
        String[] lines = Stream.concat(
                        Stream.of(heading),
                        matches.stream().map(task -> (tasks.indexOf(task) + 1) + "." + task))
                .toArray(String[]::new);
        say(lines);
    }

    /**
     * Shows the tasks matching a search, or says there were none.
     *
     * @param tasks   the whole list, whose positions number the matches
     * @param matches the matching tasks, possibly empty
     * @param keyword the text that was searched for
     */
    public void showMatchingTasks(TaskList tasks, List<Task> matches, String keyword) {
        if (matches.isEmpty()) {
            say("I searched thoroughly, but no tasks match \"" + keyword + "\".");
            return;
        }
        sayWithListNumbers("I found some tasks that match \"" + keyword + "\":", tasks, matches);
    }

    /**
     * Shows the tasks falling on one date, or says there are none.
     *
     * @param tasks   the whole list, whose positions number the matches
     * @param matches the tasks on that date, possibly empty
     * @param date    the date being asked about
     */
    public void showTasksOn(TaskList tasks, List<Task> matches, LocalDate date) {
        if (matches.isEmpty()) {
            say("Great news! You have nothing on " + TaskDate.format(date) + ".");
            return;
        }
        sayWithListNumbers("Here is everything you have on " + TaskDate.format(date) + ":", tasks, matches);
    }

    /**
     * Reports how many tasks are in the list, shown after adding or removing one.
     *
     * @param taskCount number of tasks now in the list
     * @return a sentence naming the total, with "task" pluralized to match
     */
    private static String taskCountSummary(int taskCount) {
        return "You now have " + taskCount + (taskCount == 1 ? " task" : " tasks")
                + " in your list.";
    }

    /**
     * Returns whether there is another command to read.
     *
     * @return true while input remains, false once it ends
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command, without surrounding spaces.
     *
     * @return what the user typed
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Releases the console input. */
    public void close() {
        scanner.close();
    }
}
