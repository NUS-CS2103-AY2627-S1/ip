package joebiden.ui;

import java.util.Scanner;
/**
 * Handles user input and output for the chatbot.
 */
public class Ui {

    private final Scanner scanner;

    public Ui() {
        scanner = new Scanner(System.in);
    }

    public String readInput() {
        return scanner.nextLine();
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public void close() {
        scanner.close();
    }

}
