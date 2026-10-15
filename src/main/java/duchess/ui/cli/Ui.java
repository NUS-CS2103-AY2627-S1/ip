package duchess.ui.cli;

import java.util.Scanner;

/**
 * Listens for commands by the user, and displays
 * strings to the user.
 */
class Ui {
    private final String displayName;
    private final String banner;
    private final String separator;
    private final Scanner input;

    /**
     * Creates a new Ui instance.
     * @param name The NAME of the chatbot utilising this instance.
     * @param banner The BANNER of the chatbot utilising this instance.
     * @param separator The design of the borders of the displayed output.
     */
    Ui(String name, String banner, String separator) {
        this.displayName = name;
        this.banner = banner;
        this.separator = separator;

        this.input = new Scanner(System.in);
    }

    /**
     * Listens for a user command.
     * @return A string representing the user input.
     */
    public String listen() {
        return input.nextLine().toLowerCase();
    }

    /**
     * Displays the message to the user.
     */
    public void say(String message) {
        System.out.println(this.separator);
        System.out.println(message);
        System.out.println(this.separator);
    }

    /**
     * Displays the first message to the user.
     */
    public void greet() {
        say(this.banner
                + "Hello! I am " + this.displayName + ". \n"
                + "What can I do for you?");
    }

    /**
     * Displays the final message to the user before exiting.
     */
    public void exit() {
        say("Bye! Hope to see you again soon!");
    }
}
