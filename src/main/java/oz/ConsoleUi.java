package oz;

import java.util.Scanner;

/**
 * Runs the interactive console interface for Oz.
 */
public class ConsoleUi {
    /** Visual divider placed around console messages. */
    private static final String DIVIDER = "____________________________________________________________\n";

    /** Oz banner displayed when the console starts. */
    private static final String BANNER = """
              ___    ____\s
             / _ \\  |_  /
            | | | |   / /\s
            | |_| |  / /_\s
             \\___/  /____|
            """;

    /** Greeting displayed when the console starts. */
    private static final String GREETING = DIVIDER
            + BANNER
            + "Greetings! *oink* What tasks are on today's agenda?\n";

    /** Farewell displayed when the console closes. */
    private static final String FAREWELL = DIVIDER
            + "  Farewell! Back to my contraptions. *oink*\n"
            + DIVIDER;

    /** Application controller that processes console commands. */
    private final Oz oz;

    /**
     * Constructs a console interface for the supplied Oz controller.
     *
     * @param oz Application controller that processes user commands.
     */
    public ConsoleUi(Oz oz) {
        assert oz != null : "The console requires an Oz controller";
        this.oz = oz;
    }

    /**
     * Reads and processes commands until input ends or Oz receives an exit command.
     */
    public void run() {
        System.out.print(GREETING);

        try (Scanner scanner = new Scanner(System.in)) {
            while (scanner.hasNextLine()) {
                String fullCommand = scanner.nextLine().trim();
                CommandResult result = this.oz.getResponse(fullCommand);
                if (this.oz.isExit()) {
                    break;
                }
                System.out.print(DIVIDER + result.message() + "\n" + DIVIDER);
            }
        }

        System.out.print(FAREWELL);
    }
}
