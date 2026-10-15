package nutrisoy.ui;

/**
 * Contains the text and display status of a response produced for the graphical interface.
 */
public class GuiResponse {
    private final String message;
    private final boolean error;
    private final boolean exit;

    /**
     * Creates a graphical response.
     *
     * @param message response text to display
     * @param error whether the response describes an error
     */
    public GuiResponse(String message, boolean error) {
        this(message, error, false);
    }

    /**
     * Creates a response including whether the application may safely exit.
     *
     * @param message response text
     * @param error whether processing failed
     * @param exit whether a successfully processed command requests exit
     */
    public GuiResponse(String message, boolean error, boolean exit) {
        assert message != null : "GUI response message must not be null";
        this.message = message;
        this.error = error;
        this.exit = exit && !error;
    }

    /**
     * Returns the response text.
     *
     * @return response text
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether the response represents an error.
     *
     * @return true when the response is an error
     */
    public boolean isError() {
        return error;
    }

    /**
     * Returns whether this response permits closing the application.
     *
     * @return true after a successful exit command
     */
    public boolean isExit() {
        return exit;
    }
}
