package echo;

import java.util.Scanner;
// Codex made class
/**
 * Provides a console entry point for executing UI test-plan transcripts
 * against Echo's GUI-facing API.
 */
public class ConsoleEchoAdapter {
    /**
     * Starts Echo, processes one input line at a time, and prints each reply.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        Echo echo = new Echo();
        System.out.println(echo.start());
        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                System.out.println(echo.getResponse(scanner.nextLine()));
                if (echo.isExit()) {
                    return;
                }
            }
        }
    }
}
