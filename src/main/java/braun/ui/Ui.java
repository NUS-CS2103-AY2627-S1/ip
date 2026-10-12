package braun.ui;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import braun.task.Task;

/**
 * Handles all user interactions and console presentation for the Braun chatbot.
 * Responsible for printing the talk-show host persona greetings, broadcast lines,
 * divider lines, task status updates, and reading user input commands from standard input.
 */
public class Ui {

    /** Standard divider line used to frame broadcast message blocks. */
    public static final String DIVIDER = "    ____________________________________________________________";

    /** Standard indentation whitespace prefix for console lines. */
    public static final String INDENT = "     ";

    private static final String BANNER = ""
            + "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n"
            + "    ⠀⠀⠀⠀⠀⠀⠀⢠⣀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⡄⠀⠀⠀⠀⠀⠀⠀\n"
            + "    ⠀⠀⠀⠀⠀⠀⠀⠀⠉⠻⣦⣄⠀⠀⠀⠀⠀⠀⣠⣴⠟⠉⠀⠀⠀⠀⠀⠀⠀⠀\n"
            + "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⣠⣴⣶⣶⣦⣄⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n"
            + "    ⠀⠀⠀⣀⣀⣀⣀⣀⣀⣀⣀⣀⣉⣉⣉⣉⣉⣉⣀⣀⣀⣀⣀⣀⣀⣀⣀⠀⠀⠀\n"
            + "    ⠀⠀⢸⣿⠟⠛⣛⣛⣛⣛⣛⣛⣛⣛⣛⣛⣛⣛⠛⠛⠛⠛⢿⡿⠛⠿⣿⡇⠀⠀\n"
            + "    ⠀⠀⢸⡏⢠⣾⣿⣿⣿⣿⣿⠿⠛⠋⠉⠁⠀⠀⠀⠀⠀⠀⢸⣇⠀⠀⣽⡇⠀⠀\n"
            + "    ⠀⠀⢸⡇⢸⣿⣿⣿⠟⠋⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⣿⣿⡇⠀⠀\n"
            + "    ⠀⠀⢸⡇⢸⣿⡿⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⣿⣿⣿⡇⠀⠀\n"
            + "    ⠀⠀⢸⡇⢸⡟⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⡿⠿⠿⣿⡇⠀⠀\n"
            + "    ⠀⠀⢸⡇⠸⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⡷⠶⠶⣾⡇⠀⠀\n"
            + "    ⠀⠀⢸⡇⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⡷⠶⠶⢾⡇⠀⠀\n"
            + "    ⠀⠀⢸⣿⣄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣼⣷⣶⣶⣿⡇⠀⠀\n"
            + "    ⠀⠀⠘⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠃⠀⠀\n"
            + "    ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀\n";

    /** Random generator and pool of general broadcast catchphrases used when no keyword matches */
    private static final Random RANDOM = new Random();
    private static final String[] GENERAL_REMARKS = {
        "*bzzzt* Added to the broadcast schedule! The audience is already whispering...",
        "*kzzzt* Splendid choice, dear viewer! Another intriguing entry for tonight.",
        "*static hiss* Recorded on tape! Stay tuned, folks!",
        "*chime* An exciting development! Let's see how this unfolds on air.",
        "*applause track* The studio audience is leaning in with bated breath!",
        "*screen flickers* Ooh, this smells like a juicy scenario for the show!",
        "*bzzzt* Logging this entry into tonight's special feature!"
    };

    private final Scanner scanner;

    /**
     * Constructs a new {@code Ui} instance reading input from {@link System#in}.
     */
    public Ui() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Displays a divider line to frame broadcast message blocks.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Greets the user with Braun's signature CRT TV-headed talk-show host persona banner.
     */
    public void showWelcome() {
        System.out.println(DIVIDER);
        System.out.print(BANNER);
        System.out.println(INDENT + "*kzzzt... bzzzt!*");
        System.out.println(INDENT + "Good evening, dear guest! I'm Braun, host of the Late-Night Show.");
        System.out.println(INDENT + "What can I do for you?");
        System.out.println(DIVIDER);
    }

    /**
     * Displays Braun's theatrical signoff broadcast message upon program exit.
     */
    public void showGoodbye() {
        System.out.println(DIVIDER);
        System.out.println(INDENT + "*bzzzt* That's a wrap for today's broadcast!");
        System.out.println(INDENT + "Bye. Hope to see you again soon!");
        System.out.println(DIVIDER);
    }

    /**
     * Returns Braun's theatrical broadcast welcome greeting for display in the GUI.
     *
     * @return opening welcome greeting string.
     */
    public String getWelcomeMessage() {
        return "*kzzzt... bzzzt!*\n"
                + "Good evening, dear guest! I'm Braun, host of the Late-Night Show.\n"
                + "What can I do for you?";
    }

    /**
     * Returns Braun's theatrical signoff broadcast message upon program exit.
     *
     * @return farewell signoff message string.
     */
    public String getGoodbyeMessage() {
        return "*bzzzt* That's a wrap for today's broadcast!\n"
                + "Bye. Hope to see you again soon!";
    }

    /**
     * Displays an error message wrapped within broadcast dividers.
     *
     * @param message error text to present to the user.
     */
    public void showError(String message) {
        System.out.println(DIVIDER);
        System.out.println(INDENT + message);
        System.out.println(DIVIDER);
    }

    /**
     * Displays a multi-line response enclosed within broadcast dividers and standard indentation.
     *
     * @param response the response text to format and display.
     */
    public void showResponse(String response) {
        System.out.println(DIVIDER);
        for (String line : response.split("\n")) {
            System.out.println(INDENT + line);
        }
        System.out.println(DIVIDER);
    }

    /**
     * Displays a fallback warning when stored broadcast logs fail to load.
     */
    public void showLoadingError() {
        System.out.println(DIVIDER);
        System.out.println(INDENT + "*static* Warning: Could not load broadcast log. Initializing empty schedule.");
        System.out.println(DIVIDER);
    }

    /**
     * Reads the next line of command input from the user.
     *
     * @return the raw input string, or an empty string if no line is available.
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : "";
    }

    /**
     * Checks if more input lines are available from the input stream.
     *
     * @return {@code true} if an input line is present; {@code false} otherwise.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Closes the underlying input scanner.
     */
    public void close() {
        scanner.close();
    }

    /**
     * Displays all tasks currently in the broadcast list.
     *
     * @param tasks list of tasks to display.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println(DIVIDER);
        System.out.println(INDENT + "Here are the tasks in your list:");
        IntStream.range(0, tasks.size())
                .forEach(i -> System.out.println(INDENT + (i + 1) + "." + tasks.get(i)));
        System.out.println(DIVIDER);
    }

    /**
     * Displays confirmation when a task is successfully added to the broadcast schedule,
     * including contextual lore remarks or randomized broadcast catchphrases.
     *
     * @param task the newly added task.
     * @param totalCount total number of tasks in the schedule.
     * @param description original description used for lore remark matching.
     */
    public void showAddedTask(Task task, int totalCount, String description) {
        System.out.println(DIVIDER);
        System.out.println(INDENT + "Got it. I've added this task:");
        System.out.println(INDENT + "  " + task);
        System.out.println(INDENT + "Now you have " + totalCount + " tasks in the list.");
        System.out.println(INDENT + getRemarkForTask(description));
        System.out.println(DIVIDER);
    }

    /**
     * Displays confirmation when a task is marked as completed.
     *
     * @param task the marked task.
     */
    public void showMarkedTask(Task task) {
        System.out.println(DIVIDER);
        System.out.println(INDENT + "Nice! I've marked this task as done:");
        System.out.println(INDENT + "  " + task);
        System.out.println(DIVIDER);
    }

    /**
     * Displays confirmation when a task is marked as not completed.
     *
     * @param task the unmarked task.
     */
    public void showUnmarkedTask(Task task) {
        System.out.println(DIVIDER);
        System.out.println(INDENT + "OK, I've marked this task as not done yet:");
        System.out.println(INDENT + "  " + task);
        System.out.println(DIVIDER);
    }

    /**
     * Displays confirmation when a task is removed from the broadcast schedule.
     *
     * @param task the removed task.
     * @param remainingCount total tasks remaining.
     */
    public void showDeletedTask(Task task, int remainingCount) {
        System.out.println(DIVIDER);
        System.out.println(INDENT + "Noted. I've removed this task:");
        System.out.println(INDENT + "  " + task);
        System.out.println(INDENT + "Now you have " + remainingCount + " tasks in the list.");
        System.out.println(DIVIDER);
    }

    /**
     * Displays tasks scheduled for a specific date, or a message indicating none were found.
     *
     * @param formattedDate human-readable formatted date string.
     * @param matchingTasks list of tasks occurring on that date.
     */
    public void showTasksOnDate(String formattedDate, ArrayList<Task> matchingTasks) {
        System.out.println(DIVIDER);
        if (matchingTasks.isEmpty()) {
            System.out.println(INDENT + "*static* No broadcast tasks scheduled for " + formattedDate + ".");
        } else {
            System.out.println(INDENT + "Here are the tasks scheduled for " + formattedDate + ":");
            IntStream.range(0, matchingTasks.size())
                    .forEach(i -> System.out.println(INDENT + (i + 1) + "." + matchingTasks.get(i)));
        }
        System.out.println(DIVIDER);
    }

    /**
     * Displays tasks whose descriptions match a search keyword, or a message indicating none were found.
     *
     * @param keyword the search keyword or phrase.
     * @param matchingTasks list of tasks matching the keyword.
     */
    public void showMatchingTasks(String keyword, ArrayList<Task> matchingTasks) {
        System.out.println(DIVIDER);
        if (matchingTasks.isEmpty()) {
            System.out.println(INDENT + "*static* No matching broadcast tasks found for: " + keyword);
        } else {
            System.out.println(INDENT + "Here are the matching tasks in your list:");
            IntStream.range(0, matchingTasks.size())
                    .forEach(i -> System.out.println(INDENT + (i + 1) + "." + matchingTasks.get(i)));
        }
        System.out.println(DIVIDER);
    }

    /**
     * Determines the appropriate talk-show remark for an added task description.
     * Scans for thematic keywords to provide lore-specific dialogue,
     * or falls back to a random general broadcast remark.
     *
     * @param taskDescription description text of the task.
     * @return a thematic or randomized talk-show host remark.
     */
    public String getRemarkForTask(String taskDescription) {
        String lower = taskDescription.toLowerCase();

        if (lower.contains("ghost") || lower.contains("anomaly")
                || lower.contains("monster") || lower.contains("entity")) {
            return "*screen flickers* A new anomaly! Daydream Inc. will want this documented.";
        } else if (lower.contains("work") || lower.contains("deadline")
                || lower.contains("report") || lower.contains("salary")) {
            return "*chime* Even trapped in a ghost story, we still gotta work, don't we?";
        } else if (lower.contains("explore") || lower.contains("investigate") || lower.contains("record")) {
            return "*static hiss* Watch your step, Field Explorer! Duly noted in the Dark Exploration Records.";
        } else if (lower.contains("rabbit") || lower.contains("doll") || lower.contains("plush")) {
            return "*bzzzt* Reminds me of a certain charming pink rabbit doll, doesn't it?";
        } else if (lower.contains("ticket") || lower.contains("wish") || lower.contains("survive")) {
            return "*applause track* Keep racking up those points for your Wish Ticket home!";
        } else if (lower.contains("soleum") || lower.contains("roe deer") || lower.contains("kim")) {
            return "*screen flashes bright* Ah, our favorite explorer! Keep your wits sharp!";
        }

        return getRandomGeneralRemark();
    }

    /**
     * Selects a random catchphrase from Braun's general broadcast remark pool.
     *
     * @return a random talk-show host remark string.
     */
    public String getRandomGeneralRemark() {
        return GENERAL_REMARKS[RANDOM.nextInt(GENERAL_REMARKS.length)];
    }

    /**
     * Formats the list of scheduled broadcast tasks as a multi-line string.
     *
     * @param tasks list of tasks to format.
     * @return formatted task list string.
     */
    public String formatTaskList(ArrayList<Task> tasks) {
        String entries = IntStream.range(0, tasks.size())
                .mapToObj(i -> (i + 1) + "." + tasks.get(i))
                .collect(Collectors.joining("\n"));
        return entries.isEmpty()
                ? "Here are the tasks in your list:"
                : "Here are the tasks in your list:\n" + entries;
    }

    /**
     * Formats confirmation when a task is successfully added to the broadcast schedule.
     *
     * @param task the newly added task.
     * @param totalCount total number of tasks in the schedule.
     * @param description original description used for lore remark matching.
     * @return formatted addition confirmation string.
     */
    public String formatAddedTask(Task task, int totalCount, String description) {
        return "Got it. I've added this task:\n"
                + "  " + task + "\n"
                + "Now you have " + totalCount + " tasks in the list.\n"
                + getRemarkForTask(description);
    }

    /**
     * Formats confirmation when a task is marked as completed.
     *
     * @param task the marked task.
     * @return formatted completion confirmation string.
     */
    public String formatMarkedTask(Task task) {
        return "Nice! I've marked this task as done:\n  " + task;
    }

    /**
     * Formats confirmation when a task is marked as not completed.
     *
     * @param task the unmarked task.
     * @return formatted unmark confirmation string.
     */
    public String formatUnmarkedTask(Task task) {
        return "OK, I've marked this task as not done yet:\n  " + task;
    }

    /**
     * Formats confirmation when a task is removed from the broadcast schedule.
     *
     * @param task the removed task.
     * @param remainingCount total tasks remaining.
     * @return formatted deletion confirmation string.
     */
    public String formatDeletedTask(Task task, int remainingCount) {
        return "Noted. I've removed this task:\n"
                + "  " + task + "\n"
                + "Now you have " + remainingCount + " tasks in the list.";
    }

    /**
     * Formats tasks scheduled for a specific date, or a message indicating none were found.
     *
     * @param formattedDate human-readable formatted date string.
     * @param matchingTasks list of tasks occurring on that date.
     * @return formatted date query result string.
     */
    public String formatTasksOnDate(String formattedDate, ArrayList<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return "*static* No broadcast tasks scheduled for " + formattedDate + ".";
        }
        String entries = IntStream.range(0, matchingTasks.size())
                .mapToObj(i -> (i + 1) + "." + matchingTasks.get(i))
                .collect(Collectors.joining("\n"));
        return "Here are the tasks scheduled for " + formattedDate + ":\n" + entries;
    }

    /**
     * Formats tasks whose descriptions match a search keyword, or a message indicating none were found.
     *
     * @param keyword the search keyword or phrase.
     * @param matchingTasks list of tasks matching the keyword.
     * @return formatted search query result string.
     */
    public String formatMatchingTasks(String keyword, ArrayList<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            return "*static* No matching broadcast tasks found for: " + keyword;
        }
        String entries = IntStream.range(0, matchingTasks.size())
                .mapToObj(i -> (i + 1) + "." + matchingTasks.get(i))
                .collect(Collectors.joining("\n"));
        return "Here are the matching tasks in your list:\n" + entries;
    }
}
