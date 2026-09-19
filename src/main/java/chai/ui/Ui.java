package chai.ui;

import java.util.Scanner;

import chai.Chai;

/** Handles all console input and output for Chai. */
public class Ui {
    /** Divider used to separate Chai's responses. */
    private static final String SEPARATOR = "____________________________________________________________";

    /** Reads commands entered by the user. */
    private final Scanner scanner;

    /** Creates a console UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /**
     * Runs the console interaction loop for the given Chai session.
     *
     * @param chai Chai session that processes commands.
     */
    public void run(Chai chai) {
        showMessages(SEPARATOR, chai.getWelcomeMessage(), SEPARATOR);

        boolean isRunning = true;
        while (isRunning && scanner.hasNextLine()) {
            String command = scanner.nextLine();
            String response = chai.getResponse(command);
            isRunning = !command.trim().equalsIgnoreCase("bye");
            showMessages(SEPARATOR, response, SEPARATOR);
        }
    }

    /** Displays any number of messages in order in the console. */
    private void showMessages(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }
}
