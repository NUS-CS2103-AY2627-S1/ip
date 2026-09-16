package zucc.ui;

/**
 * Defines how Zucc presents responses independently of a particular interface toolkit.
 */
public abstract class Ui {
    /** Welcome message shared by every user interface. */
    protected static final String GREETING =
            "Hello! I'm Zucc.\nReady to keep tabs on your tasks—and only your tasks.";

    /**
     * Shows the welcome message for a newly started session.
     */
    public void showGreeting() {
        showMessage(GREETING);
    }

    /**
     * Shows one response from Zucc.
     *
     * @param message response to show.
     */
    public abstract void showMessage(String message);

    /**
     * Shows an error that the user can recover from.
     *
     * @param message explanation of the error and how to recover from it.
     */
    public abstract void showError(String message);
}
