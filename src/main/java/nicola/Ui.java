package nicola;

import java.util.Scanner;

/**
 * handles the communications between the user and the chatbot.
 */
public class Ui {
    private static final String LINE = "_______^_^___________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    public void showWelcome() {
        System.out.println("Buongiorno. I'm Nicola Francesca.");
        System.out.println("Allow me to organize your affairs.");
        System.out.println(LINE);
    }

    public String readCommand() {
        return scanner.nextLine().trim();
    }

    public void showLine() {
        System.out.println(LINE);
    }

    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    public void showGoodbye() {
        System.out.println("Bye. I'll miss you.");
        System.out.println(LINE);
    }
}
