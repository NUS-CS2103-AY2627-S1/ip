package bonbon.ui;

import bonbon.tasklist.Task;
import bonbon.tasklist.TaskList;
import bonbon.command.Command;

import java.util.Scanner;

/**
 * Manages user interface interactions, including reading inputs and formatting console outputs.
 */
public class Ui {

    /**
     * Displays the application banner and greeting message to the console.
     */
    public static void greet() {
        String banner =
                " ______                ______               \n" +
                        " | ___ \\               | ___ \\              \n" +
                        " | |_/ / ___  _ __     | |_/ / ___  _ __    \n" +
                        " | ___ \\/ _ \\| '_ \\    | ___ \\/ _ \\| '_ \\   \n" +
                        " | |_/ / (_) | | | |   | |_/ / (_) | | | |  \n" +
                        " \\____/ \\___/|_| |_|   \\____/ \\___/|_| |_|  \n";
        System.out.println(banner);
        System.out.println("Hi! I'm BonBon");
        System.out.println("What're we doing today?\n");
    }

    /**
     * Displays the farewell message to the user upon exiting the application.
     */
    public static void exit() {
        System.out.println("Bye bye!!!!");
    }

    /**
     * Reads and returns the next line of input entered by the user from the console.
     *
     * @return The raw user input string.
     */
    public static String getInput() {
        System.out.println();
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        return input;
    }

    /**
     * Displays the list of available commands and their corresponding syntax to the console.
     */
    public static void printCommands() {
        System.out.println(Command.toStringCommands());
    }

    /**
     * Displays a confirmation message indicating that a task was successfully added.
     *
     * @param t The task that was added to the task list.
     */
    public static void printTaskAdd(Task t) {
        String s = String.format("Task '%s' has been added!", t.toString());
        System.out.println(s);
    }
}