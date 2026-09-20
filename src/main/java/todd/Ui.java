package todd;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

/** Reads console input and formats responses shared by Todd's interfaces. */
public class Ui {
    private static final String LINE = "____________________________________________________________";
    private static final String BANNER = "   ______          __     __\n"
            + "  /_  __/___  ____/ /____/ /\n"
            + "   / / / __ \\/ __  / __  / \n"
            + "  / / / /_/ / /_/ / /_/ /  \n"
            + " /_/  \\____/\\__,_/\\__,_/   \n";

    private final Scanner scanner;

    /** Creates a console UI that reads commands from standard input. */
    public Ui() {
        scanner = new Scanner(System.in);
    }

    /** Returns the next command entered by the user. */
    public String readCommand() {
        return scanner.nextLine();
    }

    /** Displays an already formatted message on the console. */
    public void show(String message) {
        System.out.println(message);
    }

    /** Returns Todd's welcome message. */
    public String formatWelcome() {
        return BANNER + "Hello There! I'm Todd, a NPC Chatbot\nWhat can I do for you today?";
    }

    /** Returns the welcome text without the console's ASCII art. */
    public String formatGuiWelcome() {
        return formatWelcome().substring(BANNER.length());
    }

    /** Removes only the outer console separators; the GUI supplies its own message borders. */
    public static String formatForGui(String message) {
        String prefix = LINE + "\n";
        String suffix = "\n" + LINE;
        if (message.startsWith(prefix) && message.endsWith(suffix)) {
            return message.substring(prefix.length(), message.length() - suffix.length());
        }
        return message;
    }

    /** Returns Todd's goodbye message. */
    public String formatGoodbye() {
        return "Nooo ok bye atb on making it out alive";
    }

    /** Returns a concise guide to the commands Todd recognizes. */
    public String formatHelp() {
        return surround(
                "Here's what you can ask me to do:",
                "",
                "ADD TASKS",
                "todo <description>",
                "Add a task. Example: todo read notes",
                "",
                "deadline <description> /by <date> [HHmm]",
                "Add a task with a due date.",
                "Example: deadline submit report /by tomorrow 1800",
                "",
                "event <description> /from <date> [HHmm] /to <date> [HHmm]",
                "Add an event with a start and end.",
                "Example: event study /from today 1400 /to today 1600",
                "",
                "VIEW & FIND",
                "list — Show all your tasks and their numbers.",
                "find <keyword> — Search task descriptions. Example: find notes",
                "on <date> — Show deadlines and events on a date. Example: on tomorrow",
                "reminders — Show unfinished deadlines and events for today and the next 6 days.",
                "",
                "UPDATE TASKS",
                "mark <task number> — Mark a task done. Example: mark 1",
                "unmark <task number> — Mark a task not done. Example: unmark 1",
                "delete <task number> — Remove a task. Example: delete 1",
                "Use the task numbers from list.",
                "",
                "CHAT & HELP",
                "hi or hello — Say hi to Todd.",
                "help — Show this guide.",
                "bye — Say goodbye.",
                "",
                "HOW TO READ THE FORMATS",
                "Replace <description>, <date>, and <task number> with your own details.",
                "Don't type the < > or [ ] brackets. [HHmm] means the time is optional.",
                "Dates: yyyy-MM-dd, today, tomorrow, or a weekday such as Mon.",
                "Time: use 24-hour HHmm, e.g. 1430 for 2:30pm.");
    }

    /** Returns an error that prevented saved tasks from being loaded. */
    public String formatLoadingError(String message) {
        return surround(message, "Todd could not load your list. Task changes are disabled to protect your saved data.",
                "Fix the file or its access permissions, then restart Todd.");
    }

    /** Returns a user-facing command error. */
    public String formatError(String message) {
        return surround(message);
    }

    /** Returns all tasks, or an empty-list message when there are none. */
    public String formatTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            return surround("I think the list is empty gang, there's no way you have nothing to do.");
        }

        StringBuilder content = new StringBuilder("Here's the current survival plan:");
        appendNumberedTasks(content, tasks);
        return surround(content.toString());
    }

    /** Returns confirmation that the specified task was marked. */
    public String formatMarked(Task task) {
        return surround("Okayy one more step to making it out alive.", "   " + task);
    }

    /** Returns confirmation that the specified task was unmarked. */
    public String formatUnmarked(Task task) {
        return surround("It's okay, I believe in you!", "   " + task);
    }

    /** Returns the added task and the updated total number of tasks. */
    public String formatAdded(Task task, int totalTasks) {
        return surround(
                "Another side quest? Okay, added:",
                "   " + task,
                "Now you have " + totalTasks + " tasks in the list.");
    }

    /** Returns the deleted task and the updated total number of tasks. */
    public String formatDeleted(Task task, int totalTasks) {
        return surround(
                "Quest abandoned. I saw nothing.",
                "   " + task,
                "Now you have " + totalTasks + " tasks in the list.");
    }

    /** Returns tasks selected by a date search together with their original list numbers. */
    public String formatTasksOnDate(LocalDate date, List<Task> tasks, List<Integer> taskNumbers) {
        StringBuilder content = new StringBuilder("Deadlines and events on ")
                .append(DateTimeUtil.format(date)).append(":");
        if (taskNumbers.isEmpty()) {
            content.append("\nYou have no deadlines or events on that date.");
        } else {
            for (int taskNumber : taskNumbers) {
                content.append("\n ").append(taskNumber).append(".").append(tasks.get(taskNumber - 1));
            }
        }
        return surround(content.toString());
    }

    /** Returns search matches with their original task numbers for mark, unmark, and delete. */
    public String formatMatchingTasks(List<Task> tasks, List<Integer> taskNumbers) {
        if (taskNumbers.isEmpty()) {
            return surround("Couldn't find it gang. Try something else.");
        }

        StringBuilder content = new StringBuilder("Found these lurking in your quest log:");
        for (int taskNumber : taskNumbers) {
            content.append("\n ").append(taskNumber).append(".").append(tasks.get(taskNumber - 1));
        }
        return surround(content.toString());
    }

    /** Returns incomplete deadlines and events occurring in the reminder window. */
    public String formatReminders(LocalDate startDate, int numberOfDays,
                                  List<Task> tasks, List<Integer> taskNumbers) {
        if (taskNumbers.isEmpty()) {
            return surround("Nothing coming up. Its peaceful out here.");
        }
        LocalDate endDate = startDate.plusDays(numberOfDays - 1L);
        StringBuilder content = new StringBuilder("These deadlines are getting a little too close for comfort:\n")
                .append("Deadlines and events from ")
                .append(DateTimeUtil.format(startDate))
                .append(" to ")
                .append(DateTimeUtil.format(endDate))
                .append(" (both dates included):");
        for (int taskNumber : taskNumbers) {
            content.append("\n ").append(taskNumber).append(".").append(tasks.get(taskNumber - 1));
        }
        return surround(content.toString());
    }

    private void appendNumberedTasks(StringBuilder content, List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            content.append("\n ").append(i + 1).append(".").append(tasks.get(i));
        }
    }

    /** Wraps any number of response lines between visual separators. */
    private String surround(String... lines) {
        return LINE + "\n" + String.join("\n", lines) + "\n" + LINE;
    }
}
