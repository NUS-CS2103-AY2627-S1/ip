package anniechat.ui;

import java.util.Scanner;

import anniechat.logic.CommandHandler;

/** Handles input from and output to the command-line user. */
public class Ui {
    private final Scanner scanner;

    /** Creates a user-interface object that reads from standard input. */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /** Displays the welcome message. */
    public void showWelcome() {
        System.out.println(CommandHandler.WELCOME_MESSAGE + "\n");
    }

    /**
     * Reads one command from the user.
     *
     * @return command entered by the user.
     */
    public String readCommand() {
        return this.scanner.nextLine();
    }

    /**
     * Displays a response produced by the command handler.
     *
     * @param response response to display.
     */
    public void showResponse(String response) {
        System.out.println(response);
    }
}
