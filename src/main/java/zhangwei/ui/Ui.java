package zhangwei.ui;

import java.util.List;
import java.util.Scanner;

import zhangwei.command.CommandType;
import zhangwei.task.SortCriterion;
import zhangwei.task.Task;
import zhangwei.task.TaskList;

/**
 * Deals with everything the user sees and types: it reads commands from the
 * console and prints the chatbot's replies.
 *
 * <p>
 * Collecting the console work here means the rest of the program never
 * calls {@code System.out} directly, so the wording of a message can be
 * changed, or the whole console replaced with a window, without touching the
 * classes that decide what to say.
 *
 * <p>
 * Every line is written twice: once to the console, and once to an internal
 * buffer that {@link #drainOutput()} hands over. The console version of the
 * chatbot ignores the buffer, while the graphical version reads it instead of
 * the console, so both front ends get the same words from the same code.
 */
public class Ui {

    /** The chatbot's name, shown in the greeting and the window title. */
    public static final String NAME = "Wei the Panda";

    /**
     * A small panda drawn in ASCII art, shown once at startup.
     *
     * <p>
     * Each backslash in the art must be written as \\ in a Java string
     * literal, because \ starts an escape sequence.
     */
    private static final String BANNER = " (@)___(@)\n"
            + " ( o   o )    " + NAME + "\n"
            + "  \\  v  /     keeper of your bamboo grove\n"
            + "   `---'\n";

    private final Scanner scanner = new Scanner(System.in);

    /** Holds a copy of everything said since the last {@link #drainOutput()}. */
    private final StringBuilder buffer = new StringBuilder();

    /** Creates a Ui that reads from and writes to the console. */
    public Ui() {
    }

    /** Prints the banner and the greeting shown when the chatbot starts. */
    public void showWelcome() {
        say(BANNER);
        showGreeting();
    }

    /**
     * Prints the greeting alone, without the banner.
     *
     * <p>
     * The banner is drawn with spaces and slashes, so it only lines up in a
     * fixed-width console; a window shows this shorter greeting instead.
     */
    public void showGreeting() {
        say("*munch munch* Oh, hello! I'm " + NAME + ".");
        say("What shall we plant in your task grove today?");
    }

    /**
     * Reads one line of input from the console.
     *
     * <p>
     * If the input has run out (the user pressed Ctrl-D, or piped-in input
     * ended without a bye), the bye command is returned instead, so the
     * session ends with the usual farewell rather than a crash.
     *
     * @return the next line the user types, without the line separator, or
     *     the bye keyword if there is no more input.
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : CommandType.BYE.getKeyword();
    }

    /** Prints the farewell shown just before the chatbot exits. */
    public void showGoodbye() {
        say("Bye. Time for my bamboo nap. Come back soon!");
    }

    /**
     * Prints a message the chatbot wants the user to read, such as a
     * complaint about the save file.
     *
     * @param message the text to show.
     */
    public void showMessage(String message) {
        say(message);
    }

    /**
     * Prints something that went wrong, in words the user can act on.
     *
     * <p>
     * Separate from {@link #showMessage} so that errors can later be
     * highlighted (a prefix, a colour, a different stream) in one place.
     *
     * @param message the explanation of what went wrong.
     */
    public void showError(String message) {
        say(message);
    }

    /**
     * Confirms that a task was added, and reports the new task count.
     *
     * @param task      the task that was just added.
     * @param taskCount how many tasks the list holds now.
     */
    public void showTaskAdded(Task task, int taskCount) {
        say("Got it. I've planted this task in your grove:");
        showTask(task);
        showTaskCount(taskCount);
    }

    /**
     * Confirms that a task was removed, and reports the new task count.
     *
     * @param task      the task that was just removed.
     * @param taskCount how many tasks the list holds now.
     */
    public void showTaskRemoved(Task task, int taskCount) {
        say("Noted. I've chewed this task away:");
        showTask(task);
        showTaskCount(taskCount);
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task the task that was just marked.
     */
    public void showTaskMarked(Task task) {
        say("Nice! This task is done, have a bamboo shoot:");
        showTask(task);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task the task that was just unmarked.
     */
    public void showTaskUnmarked(Task task) {
        say("OK, no rush. This task is not done yet:");
        showTask(task);
    }

    /**
     * Prints every stored task, numbered from 1, with its done status.
     *
     * <p>
     * An empty list gets its own sentence instead of a heading with nothing
     * under it, which would read as though something went wrong.
     *
     * @param tasks the task list to show.
     */
    public void showTaskList(TaskList tasks) {
        if (tasks.isEmpty()) {
            say("Your grove is empty. Plant a task with todo, deadline or event.");
            return;
        }

        say("Here are the tasks growing in your grove:");
        for (int i = 1; i <= tasks.size(); i++) {
            say(i + "." + tasks.get(i));
        }
    }

    /**
     * Confirms the order the list was just put into, then shows it, so the
     * user can read off the numbers the next command will use.
     *
     * @param criterion the order the list was sorted into.
     * @param tasks the task list, already sorted.
     */
    public void showTasksSorted(SortCriterion criterion, TaskList tasks) {
        say("Sorted your tasks by " + criterion.getKeyword() + ", neat as a row of bamboo.");
        showTaskList(tasks);
    }

    /**
     * Prints the tasks that matched a search, numbered from 1 among the
     * matches rather than by their position in the full list.
     *
     * <p>
     * An empty result gets its own sentence instead of a heading with
     * nothing under it, which would read as though something went wrong.
     *
     * @param matches the matching tasks, in the order they appear in the list.
     */
    public void showMatchingTasks(List<Task> matches) {
        if (matches.isEmpty()) {
            say("I sniffed around, but no tasks in your grove match.");
            return;
        }

        say("Here are the matching tasks I found in your grove:");
        for (int i = 0; i < matches.size(); i++) {
            say((i + 1) + "." + matches.get(i));
        }
    }

    /**
     * Returns everything said since this method was last called, and forgets
     * it, so that the next caller only sees the reply to the next command.
     *
     * <p>
     * The console front end has already printed this text and throws the
     * copy away; the graphical front end shows the copy in a dialog bubble.
     *
     * @return the buffered text, with the trailing line separator removed.
     */
    public String drainOutput() {
        String output = buffer.toString().strip();
        clearOutput();
        assert buffer.isEmpty()
                : "Draining must empty the buffer, or each reply would repeat the ones before it.";
        return output;
    }

    /**
     * Forgets everything said since the last drain, without handing it over.
     *
     * <p>
     * The console front end has already put those words on the screen, so
     * it has nothing to do with the copy kept for the graphical one.
     */
    public void clearOutput() {
        buffer.setLength(0);
    }

    /**
     * Prints the given task, indented, as it appears to the user.
     *
     * @param task the task to show.
     */
    private void showTask(Task task) {
        say("  " + task);
    }

    /**
     * Reports how many tasks are stored, after adding or removing one.
     *
     * @param taskCount how many tasks the list holds now.
     */
    private void showTaskCount(int taskCount) {
        String noun = taskCount == 1 ? "task" : "tasks";
        say("Now you have " + taskCount + " " + noun + " in your grove.");
    }

    /**
     * Says one line, to the console and to the buffer.
     *
     * <p>
     * Every message in this class goes through here, so a front end that
     * cannot use the console only has to read the buffer.
     *
     * @param message the line to say.
     */
    private void say(String message) {
        System.out.println(message);
        buffer.append(message).append(System.lineSeparator());
    }

    /** Releases the console once the chatbot has finished with it. */
    public void close() {
        scanner.close();
    }
}
