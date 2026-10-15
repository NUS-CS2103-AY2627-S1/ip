package nexus;

import java.util.List;
import java.util.Scanner;

/** Handles user input and console output for Nexus, the calm mission-control assistant. */
public class Ui {
    /** Separator displayed between sections of console output. */
    private static final String LINE = "____________________________________________________________";
    private final Scanner scanner = new Scanner(System.in);

    /** Shows the welcome message. */
    public void showWelcome() {
        System.out.println("NEXUS");
        System.out.println("Nexus online. Your quiet mission-control companion is ready.");
        System.out.println("Give me a task, and I’ll keep it in your flight plan.");
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

    /** Displays the separator line. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Displays the goodbye message. */
    public void showGoodbye() {
        showLine();
        System.out.println("Nexus signing off. Keep your trajectory steady!");
        showLine();
    }

    /** Displays all tasks. */
    public void showTasks(TaskList tasks) {
        System.out.println("Flight plan telemetry:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    /** Displays tasks matching a search keyword, or a message when none match. */
    public void showMatchingTasks(List<Task> matchingTasks) {
        System.out.println("Matching signals from your flight plan:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println((i + 1) + "." + matchingTasks.get(i));
        }
    }

    /** Closes the input source. */
    public void close() {
        scanner.close();
    }
}
