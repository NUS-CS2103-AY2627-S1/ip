package janet;

/**
 * Handles displaying messages and other user interface output for Janet.
 */
public class Ui {
    private static final String MESSAGE_GREETING = """
            Hi! I'm Janet, your friendly informational assistant.
            Let me know what you need!
            """;
    private static final String MESSAGE_GOODBYE = "Bye! Take it sleazy!";

    /**
     * Displays the greeting message when Janet starts.
     */
    public void showGreeting() {
        System.out.printf("%s\n", Ui.MESSAGE_GREETING);
    }


    /**
     * Displays a message to the user.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message to the user.
     */
    public void showError(String errorMessage) {
        System.out.println(errorMessage);
    }

    /**
     * Displays the goodbye message when Janet exits.
     */
    public void showGoodbye() {
        System.out.printf(Ui.MESSAGE_GOODBYE);
    }
}
