package whimsybot.ui;

import java.util.Scanner;

import whimsybot.Personality;

/** Handles Whimsy Bot's interaction with the user through the console. */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    /** Displays the application's welcome message. */
    public void showWelcome() {
        System.out.println(SEPARATOR);
        System.out.println(" _       ___     _                         \n"
                + "| |     / / |__ (_)_ __ ___  ___ _   _     \n"
                + "| | /| / /| '_ \\| | '_ ` _ \\/ __| | | |    \n"
                + "| |/ |/ / | | | | | | | | | \\__ \\ |_| |    \n"
                + "|__/|__/  |_| |_|_|_| |_| |_|___/\\__, |    \n"
                + "                                  |___/     \n"
                + " ____        _   \n"
                + "| __ )  ___ | |_ \n"
                + "|  _ \\ / _ \\| __|\n"
                + "| |_) | (_) | |_ \n"
                + "|____/ \\___/ \\__|\n");
        show(Personality.welcomeGreeting(), Personality.welcomePrompt(), Personality.introduction());
        showLine();
    }

    /** Returns whether another command is available. */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /** Reads the next command. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays the standard divider. */
    public void showLine() {
        System.out.println(SEPARATOR);
    }

    /** Displays one or more messages, each on its own line.
     *
     * @param messages the messages to display
     */
    public void show(String... messages) {
        for (String message : messages) {
            System.out.println(message);
        }
    }
}
