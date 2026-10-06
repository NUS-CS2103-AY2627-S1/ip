package moon;

import java.util.Scanner;

/**
 * Handles Moon's interactions with the user through the console.
 */
public class Ui {
    /** The line used to separate Moon's messages. */
    private static final String DIVIDER = "____________________________________________________________";

    /** Reads commands entered by the user. */
    private final Scanner scanner;

    /** Creates a UI that reads from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Displays Moon's welcome message. */
    public void showWelcome() {
        showLine();
        System.out.println("Yo, I'm Moon 🌙 — your task sidekick.");
        System.out.println("Let's get this list sorted. What are we doing?");
        showLine();
    }

    /** Displays the separator line used in Moon's responses. */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Reads and trims the next command.
     *
     * @return the next command, or {@code null} when input has ended
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine().trim() : null;
    }

    /** Displays an error encountered while loading saved tasks. */
    public void showLoadingError() {
        System.out.println(" Oof! I couldn't load your task list. Starting fresh for now.");
    }

    /** Displays an error raised while processing a command. */
    public void showError(String message) {
        System.out.println(" Oof! That didn't hit: " + message);
    }

    /** Displays the error used when saving a task list fails. */
    public void showSavingError() {
        System.out.println(" Oof! The save got a little scuffed.");
    }

    /** Displays Moon's farewell message. */
    public void showGoodbye() {
        System.out.println("Aight, catch you later ✌️");
    }
}
