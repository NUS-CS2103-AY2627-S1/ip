package tuesday.ui;

/**
 * Stores the default strings used by the application.
 */
public class Strings {
    /** Links users to Tuesday's command reference. */
    public static final String USER_GUIDE_URL = "https://soy046.github.io/ip/";
    /** Prompts for a command when no input was supplied. */
    public static final String EMPTY_COMMAND = "Please enter a command, Sir!";
    /** Explains when duplicate choices are available. */
    public static final String DUPLICATE_CHOICE_UNAVAILABLE =
            "Sir, use \"new\" or \"old\" only when I ask which duplicate task to keep.\n"
            + "\"new\" keeps the proposed task; \"old\" keeps the existing task.";
    /** Describes every accepted input date/time format. */
    public static final String DATE_TIME_FORMAT_HELP =
            "Use YYYY-MM-DD, HH:mm (24-hour), or YYYY-MM-DD HH:mm.\n"
            + "Use leading zeros and one space between the date and time.";
    /** Shows the deadline command structure. */
    public static final String DEADLINE_SYNTAX = "Format: deadline DESCRIPTION /by WHEN";
    /** Shows the event command structure. */
    public static final String EVENT_SYNTAX = "Format: event DESCRIPTION /from START /to END";
    /** Supplies a valid deadline command users can adapt. */
    public static final String DEADLINE_EXAMPLE = "Example: deadline work /by 2026-09-20 18:00";
    /** Supplies a valid event command users can adapt. */
    public static final String EVENT_EXAMPLE = "Example: event meeting /from 14:00 /to 15:00";
    /** Displays a save failure after the original task state has been restored. */
    public static final String SAVE_FAILURE = "Unable to save your changes. Your tasks have not been changed.";
    /** Displays the farewell shown when the application exits. */
    public static final String FAREWELL = "Bye. Hope to see you again soon!";
    /** Displays the valid answers while waiting for the user to resolve a duplicate task. */
    public static final String DUPLICATE_CHOICE_INSTRUCTION =
            "Please reply with \"new\" to keep the new task or \"old\" to keep the existing task.";
    /** Displays the confirmation for marking a task. */
    public static final String MARK = "Nice! I've marked this task as done:";
    /** Displays the confirmation for unmarking a task. */
    public static final String UNMARK = "OK, I've marked this task as not done yet:";
    /** Displays the heading for the task list. */
    public static final String SHOW_LIST = "Here are the tasks in your list:";
}
