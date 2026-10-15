package ff15;

import java.util.List;
import java.util.Scanner;

import ff15.contact.Contact;
import ff15.task.Event;
import ff15.task.Task;
import ff15.task.Todo;

/**
 * Deals with everything the user sees and types: reading commands from the
 * keyboard and printing the chatbot's replies.
 *
 * <p>Gathering it here means the rest of the program never touches
 * {@code System.in} or {@code System.out}, so the wording, the indentation, and
 * the divider lines can all be changed without touching any task logic.
 *
 * <p>Replies are printed in blocks: a divider line, the message lines, then a
 * closing divider and a blank line separating the block from the next one.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    private static final String BANNER = " _____ _____ _  ____  \n"
            + "|  ___|  ___/ |/ ___| \n"
            + "| |_  | |_  | |\\___ \\ \n"
            + "|  _| |  _| | | ___) |\n"
            + "|_|   |_|   |_||____/ \n";

    private final Scanner scanner;

    /** What has been shown since the last drain, for callers that display it themselves. */
    private final StringBuilder transcript = new StringBuilder();

    /** The command last suggested to the user, or an empty string if none has been since the last take. */
    private String suggestion = "";

    /** Creates a Ui that reads from standard input and writes to standard output. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Reads the next line the user types. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Opens a block of output with a divider line. */
    public void startBlock() {
        printDivider();
    }

    /** Closes a block of output, leaving a blank line before the next one. */
    public void endBlock() {
        printDivider();
        System.out.println();
    }

    /**
     * Prints the banner and greeting. The caller frames it with
     * {@link #startBlock()} and {@link #endBlock()}, so that anything else
     * belonging to startup - such as a loading error - lands in the same block.
     */
    public void showWelcome() {
        System.out.println(BANNER);
        showMessage("Hi. I'm FF15. Assistant Regional Manager. "
                        + "...Assistant TO the Regional Manager. Which is you.",
                "What can I do for you, boss?");
    }

    /** Reports that the save file could not be read, and that the session starts empty. */
    public void showLoadingError(String reason) {
        showError("Couldn't read your saved tasks: " + reason);
        showMessage("Starting you off with an empty list. Call the IT guy, what's his name?");
    }

    /** Reports that the contacts file could not be read, and that the session starts empty. */
    public void showContactLoadingError(String reason) {
        showError("Couldn't read your saved contacts: " + reason);
        showMessage("Starting you off with an empty contact list. Call the IT guy, what's his name?");
    }

    /**
     * Reports the lines of {@code fileName} that could not be read and were left
     * out, one per line, then reassures that the rest loaded. Prints nothing when
     * nothing was skipped, which is the normal case.
     */
    public void showSkippedLines(String fileName, List<String> skipped) {
        if (skipped.isEmpty()) {
            return;
        }
        boolean isOne = skipped.size() == 1;
        String count = isOne ? "1 line" : skipped.size() + " lines";
        String them = isOne ? "it" : "them";
        showError("I couldn't read " + count + " in " + fileName + ", so I skipped " + them + ":");
        for (String problem : skipped) {
            showMessage("  " + problem);
        }
        showMessage("The rest loaded fine. Call the IT guy, what's his name?");
    }

    /** Closes the final block. No blank line follows it, since the program is ending. */
    public void endFinalBlock() {
        printDivider();
    }

    /** Prints the farewell message. */
    public void showFarewell() {
        showMessage("See ya tomorrow, boss.");
    }

    /**
     * Prints one line of the chatbot's reply per message given, and keeps a copy
     * of each for {@link #drainTranscript()}. Replies that run to several lines
     * are one call rather than one call per line.
     */
    public void showMessage(String... messages) {
        // Varargs makes showMessage() with no arguments legal, which would show nothing.
        assert messages.length > 0 : "showMessage was called with nothing to show";
        for (String message : messages) {
            System.out.println("     " + message);
            transcript.append(message).append(System.lineSeparator());
        }
    }

    /**
     * Returns everything shown since this was last called, and forgets it. Only
     * the message text is kept, without the indentation and divider lines the
     * console session frames it with, so the words can be put straight into a
     * dialog box. The console session never calls this.
     */
    public String drainTranscript() {
        String shown = transcript.toString().strip();
        transcript.setLength(0);
        return shown;
    }

    /**
     * Shows a command the AI came up with for the user to check, and keeps it for
     * {@link #takeSuggestion()}, so the window can put it in the text box ready to
     * send.
     */
    public void showSuggestion(String command) {
        showMessage("Here's what I'd type:", "  " + command, "Send it to go ahead, or change it first.");
        suggestion = command;
    }

    /**
     * Returns the command suggested since this was last called, or an empty string
     * if there was none, and forgets it. The console session never calls this.
     */
    public String takeSuggestion() {
        String taken = suggestion;
        suggestion = "";
        return taken;
    }

    /** Prints something that went wrong, tagged so it stands out from ordinary replies. */
    public void showError(String message) {
        showMessage("No. GOD. NO. " + message);
    }

    /** Prints a heading followed by the tasks under it, numbered from 1. */
    public void showTaskList(String header, List<Task> tasks) {
        showMessage(header);
        for (int i = 0; i < tasks.size(); i++) {
            showMessage((i + 1) + "." + tasks.get(i));
        }
    }

    /** Prints a heading followed by the single task it refers to. */
    public void showTask(String header, Task task) {
        showMessage(header, "  " + task);
    }

    /**
     * Confirms a newly added task and how many tasks there are now. Michael
     * cannot hear "todo" without saying it, and cannot hear of an event without
     * asking; a deadline gets a plain acknowledgement, since he does not rate them.
     */
    public void showTaskAdded(Task task, int taskCount) {
        String header;
        if (task instanceof Todo) {
            header = "That's what she said. Also, added:";
        } else if (task instanceof Event) {
            header = "Am I invited? ...I'm invited. Added:";
        } else {
            header = "Got it. Added:";
        }
        showTask(header, task);
        showTaskCount(taskCount);
    }

    /** Confirms a deleted task and how many tasks are left. */
    public void showTaskRemoved(Task task, int taskCount) {
        showTask("Gone. Like Toby, if I had my way. Removed:", task);
        showTaskCount(taskCount);
    }

    /** Prints a heading followed by the contacts under it, numbered from 1. */
    public void showContactList(String header, List<Contact> contacts) {
        showMessage(header);
        for (int i = 0; i < contacts.size(); i++) {
            showMessage((i + 1) + "." + contacts.get(i));
        }
    }

    /** Confirms a newly added contact and how many contacts there are now. */
    public void showContactAdded(Contact contact, int contactCount) {
        showMessage("New friend. I'm friends with everyone. Added:", "  " + contact);
        showContactCount(contactCount);
    }

    /** Confirms a deleted contact and how many contacts are left. */
    public void showContactRemoved(Contact contact, int contactCount) {
        showMessage("Dead to me. Removed:", "  " + contact);
        showContactCount(contactCount);
    }

    private void showTaskCount(int taskCount) {
        showMessage("Now you have " + taskCount + " tasks in the list.");
    }

    private void showContactCount(int contactCount) {
        showMessage(contactCount + " contacts. I know everyone. Everyone knows me.");
    }

    private void printDivider() {
        System.out.println("    " + DIVIDER);
    }
}
