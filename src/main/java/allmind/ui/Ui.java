package allmind.ui;

import java.util.Scanner;

/** Handles console input and output for the ALLMIND chatbot. */
public class Ui {
    private static final String HORIZONTAL_LINE = "_".repeat(100);
    private final Scanner scanner;

    /** Creates a UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Prints the welcome banner shown when the application starts. */
    public void showWelcome() {
        String banner =
                "                                                                                      \n" +
                        "                                                                                      \n" +
                        "      db      `7MMF'      `7MMF'      `7MMM.     ,MMF'`7MMF'`7MN.   `7MF'`7MM\"\"\"Yb.   \n" +
                        "     ;MM:       MM          MM          MMMb    dPMM    MM    MMN.    M    MM    `Yb. \n" +
                        "    ,V^MM.      MM          MM          M YM   ,M MM    MM    M YMb   M    MM     `Mb \n" +
                        "   ,M  `MM      MM          MM          M  Mb  M' MM    MM    M  `MN. M    MM      MM \n" +
                        "   AbmmmqMA     MM      ,   MM      ,   M  YM.P'  MM    MM    M   `MM.M    MM     ,MP \n" +
                        "  A'     VML    MM     ,M   MM     ,M   M  `YM'   MM    MM    M     YMM    MM    ,dP' \n" +
                        ".AMA.   .AMMA..JMMmmmmMMM .JMMmmmmMMM .JML. `'  .JMML..JMML..JML.    YM  .JMMmmmdP'   \n" +
                        "                                                                                      \n" +
                        "                                                                                      ";
        String welcomeMessage = "This is ALLMIND, a primitive chatbot system. \n"
                + "Welcome back, User";
        showLine();
        System.out.println(banner);
        System.out.println(welcomeMessage);
        showLine();
    }

    /** Prints the goodbye message shown when the user exits. */
    public void showGoodbye() {
        showLine();
        System.out.println("It appears that will be all. Goodbye.");
        showLine();
    }

    /** Prints a separator between interactions. */
    public void showLine() {
        System.out.println(HORIZONTAL_LINE);
    }

    /** Reads the next command, or returns {@code null} when input ends. */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /** Reports that the task file could not be read. */
    public void showLoadingError() {
        System.out.println("The task file could not be read.");
    }

    /** Reports that the task file could not be saved. */
    public void showSavingError() {
        System.out.println("The task file could not be saved.");
    }
}
