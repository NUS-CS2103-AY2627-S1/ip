package sheppy;

import java.util.Scanner;

/** Handles Sheppy's interaction with the user. */
public class Ui {
    /** The greeting shared by the console and graphical interfaces. */
    public static final String WELCOME_MESSAGE = "Baa-hello! I'm Sheppy, your woolly little helper.\n"
            + "What shall we graze on today?";

    /** Reads commands from standard input. */
    private final Scanner scanner = new Scanner(System.in);

    /** Creates a user-interface handler connected to standard input. */
    public Ui() {
    }

    /** Displays Sheppy's welcome messages. */
    public void showWelcome() {
        String banner = "        __\n"
                + "       (oo)\n"
                + "  +---/----\\-----------------------+\n"
                + "  |        S H E P P Y             |\n"
                + "  |      your woolly helper        |\n"
                + "  +--------------------------------+\n";
        System.out.println(banner);
        System.out.println(WELCOME_MESSAGE);
    }

    /**
     * Checks whether another command is available.
     *
     * @return whether another command can be read
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return the next command line
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays a response from Sheppy.
     *
     * @param response the response to display
     */
    public void showResponse(String response) {
        System.out.println(response);
    }
}
