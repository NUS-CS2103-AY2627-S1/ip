package panda.ui;

/**
 * Handles Panda's console input/output presentation.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";

    /** Displays Panda's welcome message. */
    public void showWelcome() {
        showMessages(DIVIDER, "PANDA", "Hello! I'm Panda.", "What can I do for you?", DIVIDER);
    }

    /** Displays the divider used between chatbot turns. */
    public void showDivider() {
        System.out.println(DIVIDER);
    }

    /** Displays Panda's goodbye illustration and message. */
    public void showBye() {
        showMessages("    ( ) ( ) ( )", "      \\ | /", "       \\|/", "     .-----.",
            "    /       \\", "   |   o o   |", "    \\_______/", "Bye. Hope to see you again soon!");
        showDivider();
    }

    private void showMessages(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }
}
