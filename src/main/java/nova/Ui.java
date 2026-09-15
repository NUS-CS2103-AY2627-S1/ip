package nova;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles everything the user sees.
 * The get...Message methods build plain response text with no framing, used
 * both by the console (wrapped in a divider by printMessage) and by the GUI
 * (shown as a dialog bubble, which is its own visual frame). Keeping the
 * wording here means the console and the GUI never say something different
 * for the same event.
 */
public class Ui {
    private static final String DIVIDER = "_".repeat(60);

    /** Name the chatbot introduces itself with. */
    private static final String NAME = "Nova";

    /** ASCII-art logo shown once at startup, console only. */
    private static final String BANNER = " _   _                      \n"
            + "| \\ | |  ___           __ _ \n"
            + "|  \\| | / _ \\ __   __ / _` |\n"
            + "| |\\  || (_) |\\ \\ / /| (_| |\n"
            + "|_| \\_| \\___/  \\ V /  \\__,_|\n"
            + "                \\_/         ";

    private static final String GREETING =
            String.format("Hello! I'm %s.\nWhat can I do for you?", NAME);

    private static final String FAREWELL = "Bye. Hope to see you again soon!";

    /**
     * Shows the startup banner and greeting as one block. Console only; the
     * GUI shows getGreetingMessage() as its first dialog bubble instead,
     * since ASCII art has no equivalent in a chat bubble.
     */
    void showWelcome() {
        printMessage(BANNER + "\n" + GREETING);
    }

    /**
     * Prints one block of output, framed by a divider above and below.
     * Routing every message through here means no caller can produce a
     * half-framed block.
     *
     * @param message text to show, may span several lines.
     */
    void printMessage(String message) {
        System.out.println(DIVIDER);
        System.out.println(message);
        System.out.println(DIVIDER);
    }

    /**
     * Returns the greeting shown when the chatbot starts, with no banner.
     *
     * @return the greeting text.
     */
    String getGreetingMessage() {
        return GREETING;
    }

    /**
     * Returns the farewell shown when the user says goodbye or input ends.
     *
     * @return the farewell text.
     */
    String getFarewellMessage() {
        return FAREWELL;
    }

    /**
     * Returns the text reporting that a task was added to the list.
     *
     * @param task      the task just added.
     * @param taskCount how many tasks the list now holds.
     * @return the report text.
     */
    String getTaskAddedMessage(Task task, int taskCount) {
        // Called straight after adding, so the list cannot be empty. A zero
        // here would mean the add silently failed, and the user would be told
        // "Now you have 0 tasks" right after successfully adding one.
        assert taskCount >= 1 : "reporting an add with a task count of " + taskCount;

        return "Got it. I've added this task:\n  " + task + "\n"
                + String.format("Now you have %d task%s in the list.",
                taskCount, taskCount == 1 ? "" : "s");
    }

    /**
     * Returns the text reporting that a task was deleted from the list.
     *
     * @param task      the task just removed.
     * @param taskCount how many tasks remain.
     * @return the report text.
     */
    String getTaskRemovedMessage(Task task, int taskCount) {
        // Deleting the last task legitimately leaves zero, but a negative
        // count could only come from arithmetic on the size going wrong.
        assert taskCount >= 0 : "reporting a delete with a task count of " + taskCount;

        return "Noted, I've removed this task:\n  " + task + "\n"
                + String.format("Now you have %d task%s in the list.",
                taskCount, taskCount == 1 ? "" : "s");
    }

    /**
     * Returns the text refusing an event that clashes with existing ones.
     * Every clashing event is listed so the user can see what to reschedule
     * without having to run "list" and compare times by eye.
     *
     * @param clashes the events the new one overlaps, never empty.
     * @return the refusal text, ending with how to override it.
     */
    String getClashMessage(List<Event> clashes) {
        ArrayList<String> lines = new ArrayList<>();
        lines.add("That clashes with:");

        for (Event clash : clashes) {
            lines.add("  " + clash);
        }

        lines.add("Re-enter with " + Parser.FORCE_MARKER + " to add it anyway.");
        return String.join("\n", lines);
    }

    /**
     * Returns the notice shown at startup when the saved list already holds
     * events that overlap. A count rather than a list, to keep startup brief.
     *
     * @param pairCount how many pairs of events clash, at least one.
     * @return the notice text.
     */
    String getClashSummary(int pairCount) {
        return String.format("%d pair%s of events in your list clash.",
                pairCount, pairCount == 1 ? "" : "s");
    }

    /**
     * Returns the text reporting that a task's done status changed.
     *
     * @param task     the task that was updated.
     * @param isMarked true if it was marked done, false if it was un-marked.
     * @return the report text.
     */
    String getTaskMarkedMessage(Task task, boolean isMarked) {
        String heading = isMarked
                ? "Nice! I've marked this task as done:"
                : "OK, I've marked this task as not done yet:";
        return heading + "\n  " + task;
    }
}
