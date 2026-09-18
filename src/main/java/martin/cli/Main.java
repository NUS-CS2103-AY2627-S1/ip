package martin.cli;

import martin.core.Martin;
import martin.exception.MartinException;

/**
 * Entry point for launching the Martin chatbot application.
 */
public class Main {
    /**
     * Launches the Martin application.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        try {
            new Martin().run();
        } catch (MartinException exception) {
            System.out.println("Martin could not start: " + exception.getMessage());
        }
    }
}
