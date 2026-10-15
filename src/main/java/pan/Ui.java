package pan;

import java.util.Scanner;

/**
 * Builds every line of text PanPan shows the user, and (for the console version)
 * reads the commands they type.
 *
 * <p>Each {@code format*} / {@code get*} method returns its message as a
 * {@code String} instead of printing it. The console loop in {@link Pan#run()}
 * prints those strings; the JavaFX {@link Main} window puts the same strings in
 * chat bubbles. Keeping the wording in one class is what lets both front ends
 * share it.
 */
public class Ui {

    /** Divider printed between responses so the console transcript stays readable. */
    private static final String LINE =
            "____________________________________________________________";

    /** The single shared input source for a console session. */
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Returns whether the console still has a line to give.
     *
     * <p>False once input runs out - the user pressed Ctrl+Z (Windows) or
     * Ctrl+D (Unix), or a piped file ended. Without this check
     * {@link #readCommand()} throws {@code NoSuchElementException} and the
     * app dies with a stack trace instead of saying goodbye.
     */
    public boolean hasCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command line the user types (console only). */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Prints the horizontal divider (console only). */
    public void showLine() {
        System.out.println(LINE);
    }

    /**
     * Prints the ASCII banner and the greeting shown once at console startup.
     *
     * @param greeting the greeting text, which the caller may have extended
     *     with warnings about the save file.
     */
    public void showWelcome(String greeting) {
        String banner = " ____      _     _   _   ____      _     _   _ \n"
                + "|  _ \\    / \\   | \\ | | |  _ \\    / \\   | \\ | |\n"
                + "| |_) |  / _ \\  |  \\| | | |_) |  / _ \\  |  \\| |\n"
                + "|  __/  / ___ \\ | |\\  | |  __/  / ___ \\ | |\\  |\n"
                + "|_|    /_/   \\_\\|_| \\_| |_|    /_/   \\_\\|_| \\_|\n";
        showLine();
        System.out.println(banner);
        System.out.println(greeting);
        showLine();
    }

    /**
     * Returns the opening greeting without the ASCII banner, so it fits in a
     * GUI chat bubble.
     */
    public String getWelcome() {
        return " Heyyy hihi~ (๑>ᴗ<๑) It's meeeee, PanPan!!\n"
                + " PanPan is SUPER happy you're here today, teehee~\n"
                + " What can PanPan do for you todayy??";
    }

    /** Returns the farewell shown when the user types {@code bye}. */
    public String getGoodbye() {
        return " Byeee Byeee! PanPan will stay cute for you in the meantime! Mwah mwah~ (˘▾˘~)";
    }

    /** Returns the warning shown when the task list could not be written to disk. */
    public String getSaveError() {
        return " PanPan couldn't save your tasks... sorryyy! (っ- ‸ - ς)";
    }

    /** Returns the warning shown when an existing save file could not be read. */
    public String getLoadError() {
        return " PanPan couldn't read the save file... starting fresh! (｡•́︿•̀｡)";
    }

    /**
     * Returns the warning shown when some save-file lines were unreadable and
     * had to be left out.
     *
     * @param count how many lines were skipped.
     */
    public String getSkippedLinesWarning(int count) {
        return " PanPan skipped " + count + " save line(s) it couldn't understand~ (；一_一)";
    }

    /** Returns the whole task list as text, numbered from 1. */
    public String formatList(TaskList tasks) {
        StringBuilder message = new StringBuilder(" Ooh ooh, here's what PanPan dug up for you~ "
                + "PanPan's list-finding skills are Pan-tastic, teehee!!:");
        for (int i = 0; i < tasks.size(); i++) {
            message.append("\n ").append(i + 1).append(".").append(tasks.get(i));
        }
        return message.toString();
    }

    /**
     * Returns the tasks that matched a {@code find} command as text, numbered
     * from 1, or a friendly note when nothing matched.
     *
     * @param matches the sub-list of tasks whose description contained the keyword.
     */
    public String formatFound(TaskList matches) {
        if (matches.isEmpty()) {
            return " Awww, PanPan looked hard but found no matching tasks~ (｡•́︿•̀｡)";
        }
        StringBuilder message = new StringBuilder(" Oooh, PanPan found these ones "
                + "hiding in your list~ (⁎˃ᴗ˂⁎)");
        for (int i = 0; i < matches.size(); i++) {
            message.append("\n ").append(i + 1).append(".").append(matches.get(i));
        }
        return message.toString();
    }

    /** Returns the confirmation for a newly added {@code todo}, {@code deadline} or {@code event}. */
    public String formatAdded(Task task) {
        return " PanPan added this to your list! " + task
                + "\n PanPan will watch and make sure you do it!";
    }

    /** Returns the confirmation that a task was marked done. */
    public String formatMarked(Task task) {
        return " Yayyy!! PanPan marked this task as done, Pan-tastic job!!\n   " + task;
    }

    /** Returns the confirmation that a task was marked not done. */
    public String formatUnmarked(Task task) {
        return " Awww not done yet? PanPan unmarked this task already... "
                + "PanPan thinks you can do better!\n   " + task;
    }

    /**
     * Returns the confirmation that a task was deleted.
     *
     * @param removed   the task that was removed.
     * @param remaining how many tasks are left afterwards.
     */
    public String formatDeleted(Task removed, int remaining) {
        return " Okayyy, PanPan waved byebye to this task and removed it from the list~ (｡•̀ᴗ-)✧"
                + "\n   " + removed
                + "\n PanPan is now keeping " + remaining + " tasks safe for you!";
    }

    /** Returns the confirmation that one of a task's details was changed. */
    public String formatUpdated(Task task) {
        return " Ooooh, PanPan changed just that bit! Everything else stays the same~ (◕ᴗ◕✿)"
                + "\n   " + task;
    }

    /** Closes the input source at the end of a console session. */
    public void close() {
        scanner.close();
    }
}