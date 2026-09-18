package herta.ui;

/**
 * Collects command output so that it can be returned to a graphical user interface.
 */
public class ResponseCollector implements UiOutput {
    private final StringBuilder output = new StringBuilder();

    /**
     * Adds a message to the collected response.
     *
     * @param message the message to collect.
     */
    @Override
    public void showMessage(String message) {
        append(message);
    }

    /**
     * Adds Herta's goodbye message to the collected response.
     */
    @Override
    public void showGoodbye() {
        append(GOODBYE_MESSAGE);
    }

    /**
     * Returns all collected messages separated by line breaks.
     *
     * @return the collected response.
     */
    public String getOutput() {
        return output.toString();
    }

    private void append(String message) {
        if (!output.isEmpty()) {
            output.append(System.lineSeparator());
        }
        output.append(message);
    }
}
