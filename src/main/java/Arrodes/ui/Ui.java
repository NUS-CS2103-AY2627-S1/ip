package arrodes.ui;

/** Handles console input and output for Arrodes. */
public interface Ui {
    /**
     * Reads one complete command from the user.
     *
     * @return command entered by the user
     */
    public String readUserInput();
    /**
     * Displays an arbitrary application message.
     * @param message text to print
     */
    public void showMessage(String message);
    /** Displays the startup banner and greeting. */
    public void showOnLoadMessage();

    /** Displays the standard separator line. */
    public void showSeparator();

    /** Displays the application farewell. */
    public void showOnExitMessage();
}
