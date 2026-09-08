package crack;

import java.util.List;
import java.util.Scanner;

import crack.task.Task;

/**
 * Everything Crack says to the user, and how it hears back.
 * Keeping the wording in one place means the rest of the code never prints anything itself.
 */
public class Ui {
    /** Line drawn between messages. */
    private static final String DIVIDER = "_".repeat(60);

    private static final String BANNER = "  ____                _    \n"
            + " / ___|_ __ __ _  ___| | __\n"
            + "| |   | '__/ _` |/ __| |/ /\n"
            + "| |___| | | (_| | (__|   < \n"
            + " \\____|_|  \\__,_|\\___|_|\\_\\";

    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prints one line of output.
     * The GUI overrides this to collect the same wording instead of printing it.
     *
     * @param line The line to show.
     */
    protected void print(String line) {
        System.out.println(line);
    }

    /**
     * Greets the user, banner and all.
     */
    public void showWelcome() {
        showLine();
        print(BANNER);
        showGreeting();
        showLine();
    }

    /**
     * Says hello without the banner, which needs a fixed width font to line up.
     */
    public void showGreeting() {
        print("Ayo, Crack pulled up.");
        print("What we locking in today gng?");
    }

    /**
     * Returns whether there is another line of input waiting.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads one line of input, trimmed.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Draws the line that separates one message from the next.
     */
    public void showLine() {
        print(DIVIDER);
    }

    /**
     * Tells the user why something did not work.
     *
     * @param message What went wrong.
     */
    public void showError(String message) {
        print(message);
    }

    /**
     * Signs off.
     */
    public void showGoodbye() {
        print("Aight bet, I'm finna fade. Don't get cooked.");
    }

    /**
     * Confirms that a task was added.
     *
     * @param task The task that went in.
     * @param count How many tasks there are now.
     */
    public void showAdded(Task task, int count) {
        print("Bet, ts on the list now:");
        print("  " + task);
        showCount(count);
    }

    /**
     * Confirms that a task was removed.
     *
     * @param task The task that was dropped.
     * @param count How many tasks are left.
     */
    public void showRemoved(Task task, int count) {
        print("Say less, ts gone:");
        print("  " + task);
        showCount(count);
    }

    /**
     * Confirms that a task is now done.
     *
     * @param task The task that was marked.
     */
    public void showMarked(Task task) {
        print("Ayo that's a W. Ts done:");
        print("  " + task);
    }

    /**
     * Confirms that a task is back on the pile.
     *
     * @param task The task that was unmarked.
     */
    public void showUnmarked(Task task) {
        print("Aight, ts back on the pile:");
        print("  " + task);
    }

    /**
     * Lists every command Crack knows, its short forms and what it does.
     */
    public void showHelp() {
        print("Aight, here's the whole playbook:");
        for (Command command : Command.values()) {
            print("  " + command.getUsage() + formatAliases(command) + " - " + command.getSummary());
        }
        print("Dates go 2/12/2020 1500 or 2019-10-15. Time's optional.");
    }

    /**
     * Returns a command's short forms in brackets, or nothing when it has none.
     *
     * @param command The command being listed.
     * @return The bracketed short forms, ready to sit after the usage.
     */
    private static String formatAliases(Command command) {
        List<String> aliases = command.getAliases();
        return aliases.isEmpty() ? "" : " (" + String.join(", ", aliases) + ")";
    }

    /**
     * Prints the whole list, numbered from one.
     *
     * @param tasks The list to print.
     */
    public void showList(TaskList tasks) {
        showNumbered(tasks.getTasks(), "Peep what you got on deck:", "List's dry gng. You free rn.");
    }

    /**
     * Prints whatever lands on one day.
     *
     * @param day The day being asked about, already written out for display.
     * @param matches The tasks falling on that day, possibly none.
     */
    public void showTasksOn(String day, List<Task> matches) {
        if (matches.isEmpty()) {
            print("Ain't nothing on " + day + ". You free that day fr.");
            return;
        }
        print("Here's what's cooking on " + day + ":");
        for (Task task : matches) {
            print("  " + task);
        }
    }

    /**
     * Prints the tasks that matched a search, numbered from one.
     *
     * @param matches The tasks whose descriptions contained the keyword.
     */
    public void showFound(List<Task> matches) {
        showNumbered(matches, "Aight, peep what I dug up:", "Nah, nothing matching that gng.");
    }

    /**
     * Prints tasks numbered from one, or says so when there are none.
     *
     * @param tasks The tasks to print.
     * @param heading What to say above the list.
     * @param emptyMessage What to say instead when the list is empty.
     */
    private void showNumbered(List<Task> tasks, String heading, String emptyMessage) {
        if (tasks.isEmpty()) {
            print(emptyMessage);
            return;
        }
        print(heading);
        for (int i = 0; i < tasks.size(); i++) {
            print((i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Says how many tasks are left.
     *
     * @param count The number of tasks in the list.
     */
    private void showCount(int count) {
        if (count == 0) {
            print("List's clear. You goated fr.");
            return;
        }
        print("That's " + count + (count == 1 ? " thing" : " things") + " on deck now.");
    }
}
