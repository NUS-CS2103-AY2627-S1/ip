package aravii;

import java.nio.file.Path;

/**
 * Runs the Arav II command-line task manager.
 */
public final class AravII {
    private AravII() {
    }

    /**
     * Starts the chatbot and processes commands until bye or end of input.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        ChatSession session = new ChatSession(new Storage(Path.of("data", "aravii.txt")));
        new Ui(System.in, System.out).run(session);
    }
}
