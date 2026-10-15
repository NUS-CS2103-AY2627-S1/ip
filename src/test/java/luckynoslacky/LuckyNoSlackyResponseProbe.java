package luckynoslacky;

/**
 * Invokes the chatbot facade from an isolated subprocess test.
 */
public final class LuckyNoSlackyResponseProbe {
    private LuckyNoSlackyResponseProbe() {
        // Prevent instantiation of this test helper.
    }

    /**
     * Prints one chatbot response and its exit flag.
     *
     * @param args command words to pass to the chatbot
     */
    public static void main(String[] args) {
        String userInput = String.join(" ", args);
        CommandResult response = new LuckyNoSlacky()
                .getResponse(userInput);
        System.out.println(response.message());
        System.out.println("shouldExit=" + response.shouldExit());
    }
}
