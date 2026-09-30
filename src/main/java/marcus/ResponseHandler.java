package marcus;

/**
 * Receives responses produced while Marcus processes a command.
 */
public interface ResponseHandler {
    /**
     * Displays an ordinary response message.
     *
     * @param message response text.
     */
    void showMessage(String message);

    /**
     * Displays an error response.
     *
     * @param message error text.
     */
    void showError(String message);

    /**
     * Displays a formatted task list.
     *
     * @param taskList formatted task-list text.
     */
    void showTaskList(String taskList);

    /** Displays Marcus's goodbye response. */
    void showGoodbye();
}
