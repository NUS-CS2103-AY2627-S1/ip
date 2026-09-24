package aravii;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Owns console input and output, keeping presentation out of task and storage logic.
 */
public class Ui {
    private final Scanner scanner;

    private final PrintStream output;

    /**
     * Creates a text interface using the supplied streams.
     *
     * @param input The stream of user commands.
     * @param output The stream for chatbot responses.
     */
    public Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Displays the ASCII logo and processes commands until bye or end of input.
     *
     * @param session The shared command and persistence coordinator.
     */
    public void run(ChatSession session) {
        output.println("____________________________________________________________\n"
                + "     _    ____      _       __      __\n"
                + "    / \\  |  _ \\    / \\      \\ \\    / /\n"
                + "   / _ \\ | |_) |  / _ \\      \\ \\  / / \n"
                + "  / ___ \\|  _ <  / ___ \\      \\ \\/ /  \n"
                + " /_/   \\_\\_| \\_\\/_/   \\_\\      \\__/   \n"
                + "             ( II )\n"
                + "Hello! I'm Arav (II).\nWhat can I do for you?\n"
                + "Type help to see the available commands.\n"
                + "____________________________________________________________");
        if (!session.getStartupError().isEmpty()) {
            output.println(session.getStartupError());
        }
        while (!session.shouldExit() && scanner.hasNextLine()) {
            output.println(session.respond(scanner.nextLine()));
        }
        if (!session.shouldExit()) {
            output.println(session.respond("bye"));
        }
        output.println("____________________________________________________________");
    }
}
