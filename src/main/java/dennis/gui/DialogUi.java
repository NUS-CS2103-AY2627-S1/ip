package dennis.gui;

import dennis.task.Task;
import dennis.ui.Ui;

/**
 * A {@link Ui} that collects Dennis's replies into a string instead of
 * printing them, so a graphical front end can show them in a dialog bubble.
 *
 * <p>Every {@code show*} method the commands call is overridden to append to
 * an internal buffer. {@link #drain()} returns the text built up since the
 * last call and clears the buffer, ready for the next command. The console
 * {@link Ui} is left completely untouched; this class only adds a second way
 * to receive its output. The wording here mirrors {@code Ui}'s console
 * messages, so the two should be kept in step.</p>
 */
public class DialogUi extends Ui {
    /** Non-breaking space, used to keep a task's prefix on one line. */
    private static final String NBSP = " ";

    /** Collects the lines of the reply currently being built. */
    private final StringBuilder buffer = new StringBuilder();

    /** Creates a capturing UI with an empty buffer. */
    public DialogUi() {
    }

    /**
     * Returns everything appended since the last call and clears the buffer.
     *
     * @return the accumulated reply text, with no trailing line separator
     */
    public String drain() {
        String text = buffer.toString();
        buffer.setLength(0);
        return text;
    }

    /**
     * Appends the given lines to the reply buffer, inserting a line separator
     * before each line that is not the first in the buffer.
     *
     * @param lines the text to add, in order
     */
    private void append(String... lines) {
        for (String line : lines) {
            if (buffer.length() > 0) {
                buffer.append(System.lineSeparator());
            }
            buffer.append(line);
        }
    }

    /**
     * Renders a task the way the commands display it ({@code "  " + task}),
     * but with the leading indent and the {@code [type][status]} box joined
     * by non-breaking spaces and glued to the description. That keeps a long,
     * space-less description on the same line as the box, wrapping character
     * by character from there, instead of being pushed onto a line of its own.
     *
     * @param task the task to render
     * @return the bubble form of the task line
     */
    private static String taskLine(Task task) {
        String rendered = task.toString();
        int afterBox = rendered.indexOf("] ");
        if (afterBox < 0) {
            return "  " + rendered;
        }

        String box = rendered.substring(0, afterBox + 1).replace(" ", NBSP);
        String description = rendered.substring(afterBox + 2);
        return NBSP + NBSP + box + NBSP + description;
    }

    @Override
    public void showMessage(String... messages) {
        append(messages);
    }

    @Override
    public void showLine() {
        // The console divider rule has no place in a chat bubble.
    }

    @Override
    public void showError(String message) {
        append("HISS!! " + message);
    }

    @Override
    public void showGoodbye() {
        append("Time for a cat-nap. Bye!");
    }

    @Override
    public void showAddedTask(Task task, int taskCount) {
        append("Purrfect, I've added this task:",
                taskLine(task),
                "Meow you have " + taskCount + " tasks!");
    }

    @Override
    public void showRemovedTask(Task task, int taskCount) {
        append("Scratched this task off the list:",
                taskLine(task),
                "Meow there are " + taskCount + " tasks left!");
    }

    @Override
    public void showMarkedTask(Task task) {
        append("Paw-sitively marked as done:", taskLine(task));
    }

    @Override
    public void showUnmarkedTask(Task task) {
        append("Un-fur-tunately, back on the list:", taskLine(task));
    }
}
