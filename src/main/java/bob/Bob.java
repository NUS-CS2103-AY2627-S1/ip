package bob;

import bob.exception.BobException;

/**
 * Entry point for the Bob chatbot application.
 */
public class Bob {
    private static final String DEFAULT_FILE_PATH = "data/bob.txt";

    /**
     * Starts the chatbot.
     *
     * @param args Command-line arguments, currently unused.
     */
    public static void main(String[] args) {
        try {
            ChatBot chatBot = new ChatBot(DEFAULT_FILE_PATH);
            chatBot.run();
        } catch (BobException exception) {
            System.out.println(exception.getMessage());
        }

    }
}
