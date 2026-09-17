package glennon;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import glennon.task.Task;

/**
 * Handles all interaction with the user, reading typed commands and printing
 * Glennon's responses.
 */
public class Ui {
    /** Horizontal rule printed around each response. */
    private static final String DIVIDER = "_".repeat(60);

    /** Format used in headings for date-filter results. */
    private static final DateTimeFormatter DATE_DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM d uuuu", Locale.ENGLISH);

    /** Glennon title art displayed when a session starts. */
    private static final String BANNER = """
            +==========================================================+
            |                                                          |
            |             ________                                     |
            |            / ____/ /__  ____  ____  ____  ____           |
            |           / / __/ / _ \\/ __ \\/ __ \\/ __ \\/ __ \\          |
            |          / /_/ / /  __/ / / / / / / /_/ / / / /          |
            |          \\____/_/\\___/_/ /_/_/ /_/\\____/_/ /_/           |
            |                                                          |
            +==========================================================+
            """;

    /** Receives formatted responses for either the console or a GUI buffer. */
    private final PrintWriter output;

    /** Reads commands from standard input. */
    private final Scanner scanner;

    /**
     * Records errors separately from text so GUI styling does not depend on wording.
     */
    private boolean hasError;

    /**
     * Creates a user interface that reads commands from standard input.
     */
    public Ui() {
        this(new PrintWriter(System.out, true));
    }

    /**
     * Creates an interface that sends responses to the supplied writer.
     *
     * @param output destination for formatted responses.
     */
    public Ui(PrintWriter output) {
        this.output = output;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays the banner and greeting shown when Glennon starts.
     */
    public void showWelcome() {
        output.println(DIVIDER);
        output.print(BANNER);
        printLines("Hey there! Glennon online.", "What's the mission?", DIVIDER);
    }

    /**
     * Checks whether the user has entered another command.
     *
     * @return true while input remains available.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command typed by the user.
     *
     * @return the raw input line, without interpretation.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the divider that separates one response from the next.
     */
    public void showDivider() {
        output.println(DIVIDER);
    }

    /**
     * Displays the message shown before Glennon exits.
     */
    public void showGoodbye() {
        output.println("Signing off. Catch you on the next mission!");
    }

    /**
     * Displays every stored mission with its one-based position.
     *
     * @param missions missions to display.
     */
    public void showMissionList(List<Task> missions) {
        output.println("Mission log:");
        showNumberedMissions(missions);
    }

    /**
     * Confirms that missions were sorted and displays their new order.
     *
     * @param missions missions in chronological order.
     */
    public void showSortedMissions(List<Task> missions) {
        output.println("Mission log sorted chronologically:");
        showNumberedMissions(missions);
    }

    /**
     * Displays missions with one-based positions.
     *
     * @param missions missions to display.
     */
    private void showNumberedMissions(List<Task> missions) {
        for (int i = 0; i < missions.size(); i++) {
            output.println((i + 1) + ". " + missions.get(i));
        }
    }

    /**
     * Displays matching missions with their current positions in the full log.
     *
     * @param missions matching missions in their original order.
     * @param allMissions all missions in their current log order.
     */
    public void showMatchingMissions(List<Task> missions, List<Task> allMissions) {
        output.println("Matching missions located:");
        for (Task mission : missions) {
            output.println((allMissions.indexOf(mission) + 1) + ". " + mission);
        }
    }

    /**
     * Displays scheduled missions with their current positions in the full log.
     *
     * @param date date selected by the user.
     * @param missions missions scheduled on that date.
     * @param allMissions all missions in their current log order.
     */
    public void showScheduledMissions(LocalDate date, List<Task> missions, List<Task> allMissions) {
        output.println("Missions on " + date.format(DATE_DISPLAY_FORMAT) + ":");
        for (Task mission : missions) {
            output.println((allMissions.indexOf(mission) + 1) + ". " + mission);
        }
    }

    /**
     * Confirms that a mission was added.
     *
     * @param mission mission that was added.
     * @param missionCount number of missions now stored.
     */
    public void showMissionAdded(Task mission, int missionCount) {
        output.println("Mission added: " + mission);
        showMissionCount(missionCount);
    }

    /**
     * Confirms that a mission was removed.
     *
     * @param mission mission that was removed.
     * @param missionCount number of missions now stored.
     */
    public void showMissionRemoved(Task mission, int missionCount) {
        printLines("Mission removed:", "  " + mission);
        showMissionCount(missionCount);
    }

    /**
     * Confirms that a mission's completion status changed.
     *
     * @param mission mission whose status changed.
     * @param isComplete true when the mission was marked complete.
     */
    public void showMissionStatusChanged(Task mission, boolean isComplete) {
        printLines(isComplete
                ? "Mission marked complete:"
                : "Mission marked incomplete:", "  " + mission);
    }

    /**
     * Displays an error raised while carrying out a command.
     *
     * @param message explanation shown to the user.
     */
    public void showError(String message) {
        hasError = true;
        printLines("Mission control alert!", message);
    }

    /**
     * Returns whether this interface has displayed an error.
     *
     * @return true after an error has been displayed.
     */
    public boolean hasError() {
        return hasError;
    }

    /**
     * Releases the input source once Glennon has finished.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Prints each supplied line in order.
     *
     * @param lines lines to display.
     */
    private void printLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }

    /**
     * Displays the mission total using the correct singular or plural noun.
     *
     * @param missionCount number of missions currently stored.
     */
    private void showMissionCount(int missionCount) {
        String missionLabel = missionCount == 1 ? "mission" : "missions";
        output.println("Mission log now has " + missionCount + " " + missionLabel + ".");
    }
}
