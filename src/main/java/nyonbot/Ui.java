package nyonbot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Scanner;

/**
 * Handles all input and output for the NyonBot application.
 */
public class Ui {
    private static Ui instance = null;
    private static Scanner scanner = new Scanner(System.in);

    private Ui() {
    }

    public static synchronized Ui getInstance() {
        if (instance == null) {
            instance = new Ui();
        }
        return instance;
    }

    /**
     * Displays the welcome banner.
     */
    public void welcome() {
        String welcome = ResourceLoader.readTextFile("static/welcome.txt");
        System.out.println(welcome);
    }

    /**
     * Displays the goodbye banner.
     */
    public void goodbye() {
        String out = ResourceLoader.readTextFile("static/goodbye.txt");
        System.out.println(out);
    }

    /**
     * Displays the title banner.
     */
    public void banner() {
        String banner = ResourceLoader.readTextFile("static/ascii-banner.txt");
        System.out.println(banner);
    }

    /**
     * Reads one command from the standard input.
     *
     * @return the command entered by the user.
     */
    public String readCommand() {

        String userInput = scanner.nextLine();

        return userInput;
    }

    /**
     * Prints a message to the standard output.
     *
     * @param out the message to be printed.
     */
    public void showOutput(String out) {
        System.out.println(out);
    }

    /**
     * Formats a date to the display pattern {@code dd MMM yyyy HHmm}.
     *
     * @param dateTime <code>DateTime</code> to be formatted.
     * @return the formatted date string.
     */
    public static String showDate(LocalDateTime dateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy HHmm", Locale.US);
        return dateTime.format(dtf);
    }
}
