package jimbo.ui;

import jimbo.JimboException;

import java.util.Scanner;

/**
 * Handles UI related things.
 */
public class Ui {
    private static final Scanner SCANNER = new Scanner(System.in);

    /**
     * Prints a line to console.
     */
    public void printSeparator() {
        System.out.println("--------------------");
    }

    /**
     * Says hi to the user.
     */
    public void greet() {
        System.out.println("hi i'm jimbo");
        System.out.println("nice to meet you");
        printSeparator();
    }

    /**
     * Says bye to the user.
     */
    public void sayBye() {
        System.out.println("bye bye");
    }

    public void echo(String toEcho) {
        System.out.println(toEcho);
        printSeparator();
    }

    /**
     * Gets String input from the user.
     * @return User input.
     */
    public String getInput() {
        System.out.print("> ");
        return SCANNER.nextLine();
    }

    /**
     * Prints Jimbo's response to console.
     *
     * @param response Jimbo's response.
     */
    public void printResponse(String response) {
        System.out.println(response);
        printSeparator();
    }

    /**
     * Prints an error message to console.
     *
     * @param error The error.
     */
    public void printError(JimboException error) {
        System.out.println("uh oh you got an error: ");
        System.out.println(error.getMessage());
        printSeparator();
    }
}
