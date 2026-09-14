package morgan.ui;

/**
 * Handles user interactions for the chatbot application.
 * Responsible for formatting messages, error alerts, and capturing response output for the GUI.
 */
public class Ui {
    private static final String CHATBOT_NAME = "Morgan";
    private final StringBuilder responseBuffer = new StringBuilder();

    /**
     * Constructs a Ui instance.
     */
    public Ui() {
    }

    /**
     * Appends message lines to the response buffer.
     *
     * @param messages The lines of messages to append.
     */
    public void showToUser(String... messages) {
        for (String m : messages) {
            responseBuffer.append(m).append("\n");
        }
    }

    /**
     * Returns the accumulated response text and clears the internal buffer.
     *
     * @return The response string collected since the last flush.
     */
    public String flushResponse() {
        String result = responseBuffer.toString().trim();
        responseBuffer.setLength(0);
        return result;
    }

    /**
     * Displays the exit message.
     */
    public void showGoodbye() {
        showToUser("Meow~ Bye bye, human! Don't forget to feed me~");
    }

    /**
     * Displays the loading error.
     */
    public void showLoadingError() {
        showToUser("Meow! Unable to load save file! Starting with empty task list.");
    }
}
