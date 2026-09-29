package tasktracker.ui;

import java.util.List;

/**
 * Container for all user-facing UI messages, application branding elements,
 * and command format error templates.
 * <p>
 * Centralizes static message strings across the application to ensure uniform
 * user feedback and simplify UI text maintenance.
 */
public class Message {

    // --- Raw Command Syntax (private helpers) --- //
    public static final String SYNTAX_INDEX = "mark <index> | unmark <index> | delete <index>";
    public static final String SYNTAX_TODO = "todo <description>";
    public static final String SYNTAX_DEADLINE = "deadline <description> /by <due DT>";
    public static final String SYNTAX_EVENT = "event <description> /from <start DT> /to <end DT>";
    public static final String SYNTAX_FIXED = "fixed <description> /needs <duration>";
    public static final String SYNTAX_FIND = "find <keyword>";
    public static final String SYNTAX_DATE_TIME = "yyyy-MM-dd HHmm(24-H clock)";
    public static final String SYNTAX_VALID_RANGE = "Index range: ";
    public static final String SYNTAX_FORMAT = "Format: ";

    // --- Branding Elements --- //
    public static final String INDENT_4 = "    ";
    public static final String DIVIDER = "_".repeat(99);
    // Define the TaskTracker banner logo using external tool from manytools
    // Gemini AI was used to generate the specific formatting for the banner
    public static final String BANNER = "  ______           __                 \n"
            + " /_  ______ ______/ /__                \n"
            + "  / / / __ `/ ___/ //_/                \n"
            + " / / / /_/ (__  / ,<                   \n"
            + "/_______,_/____/_/|_|   __            \n"
            + " /_  ___________ ______/ /_____  _____\n"
            + "  / / / ___/ __ `/ ___/ //_/ _ \\/ ___/\n"
            + " / / /  / /_/ / /__/ ,< /  __/ /    \n"
            + "/_/ /_/   \\__,_/\\___/_/|_|\\___/_/     \n";

    public static final String MSG_WELCOME = "Hello there! I'm TaskTracker.\n"
            + "I am a chatbot for you to track your tasks.\n"
            + "What can I do for you today?\n"
            + "Type 'help' to see available commands.\n";

    public static final String MSG_GOODBYE = "Baiiiiiii!!! Cya soon!\n";

    public static final String MSG_HELP = "Here are the available commands:\n"
            + "  - list : Views all tasks\n"
            + "  - " + SYNTAX_TODO + " : Adds a todo task\n"
            + "  - " + SYNTAX_DEADLINE + " : Adds a deadline task\n"
            + "  - " + SYNTAX_EVENT + " : Adds an event task\n"
            + "  - " + SYNTAX_FIXED + " : Adds a fixed duration task\n"
            + "  - mark <index> : Marks a task as completed\n"
            + "  - unmark <index> : Marks a task as not completed\n"
            + "  - delete <index> : Deletes a task from the list\n"
            + "  - undo : Restores the most recently deleted task\n"
            + "  - " + SYNTAX_FIND + " : Finds tasks by keyword\n"
            + "  - bye : Exits the program\n"
            + "  - Date and Time (DT) Format: " + SYNTAX_DATE_TIME + "\n";

    // --- Task Success Responses --- //
    public static final String MSG_TASK_ADDED = "Got it. I've added this task:\n";
    public static final String MSG_TASK_REMOVED = "Noted. I've removed this task:\n";
    public static final String MSG_TASK_MARKED = "Well done on completing this task! Marked as done! :D\n";
    public static final String MSG_TASK_UNMARKED = "Awwh... marked this as undone. Try finish soon ya? :P\n";
    public static final String MSG_FIND_MATCHING = "Here are the matching tasks in your list:\n";

    // --- User Input Formats --- //
    public static final String DATE_TIME_FORMAT = SYNTAX_FORMAT + SYNTAX_DATE_TIME + "\n";
    public static final String INDEX_FORMAT = SYNTAX_FORMAT + SYNTAX_INDEX + "\n";
    public static final String TODO_FORMAT = SYNTAX_FORMAT + SYNTAX_TODO + "\n";
    public static final String DEADLINE_FORMAT = SYNTAX_FORMAT + SYNTAX_DEADLINE + "\n" + SYNTAX_DATE_TIME + "\n";
    public static final String EVENT_FORMAT = SYNTAX_FORMAT + SYNTAX_EVENT + "\n" + SYNTAX_DATE_TIME + "\n";
    public static final String FIXED_FORMAT = SYNTAX_FORMAT + SYNTAX_FIXED + "\n";
    public static final String FIND_FORMAT = SYNTAX_FORMAT + SYNTAX_FIND + "\n";

    // --- Error Messages --- //
    public static final String ERR_UNKNOWN_COMMAND = "OOPS!! I'm sowwyyy :ccc\n"
            + "I don't know what that command means ;(\n"
            + "Type 'help' to see available commands.\n";
    public static final String ERR_EMPTY_FIND = "OOPS!! find keyword cannot be empty.\n" + FIND_FORMAT;
    public static final String ERR_NO_MATCHING_TASKS = "No matching tasks found containing: ";

    // List error methods
    public static final String ERR_TASK_LIST_EMPTY = "No task added yet, please add a task first!\n";
    public static final String ERR_TASK_LIST_FULL = "OOPS!! Task list full. Please remove any task first!\n";
    public static final String ERR_MISSING_INDEX = "OOPS!! specify a task index number.\n" + INDEX_FORMAT;
    public static final String ERR_INVALID_INDEX = "OOPS!! task number must be a valid integer.\n" + INDEX_FORMAT;
    public static final String ERR_INVALID_DATE_TIME =
            "OOPS!! date and time must follow the correct format.\n" + DATE_TIME_FORMAT;
    public static final String ERR_NO_DELIMITER =
            "OOPS!! Task descriptions cannot contain '|'.\n";

    // Todo error methods
    public static final String ERR_EMPTY_TODO =
            "OOPS!! todo description cannot be empty.\n" + TODO_FORMAT;

    // Deadline error methods
    public static final String ERR_EMPTY_DEADLINE =
            "OOPS!! deadline description cannot be empty.\n" + DEADLINE_FORMAT;
    public static final String ERR_MISSING_BY =
            "OOPS!! specify deadline description and date using '/by'.\n" + DEADLINE_FORMAT;

    // Event error methods
    public static final String ERR_EMPTY_EVENT =
            "OOPS!! event description cannot be empty.\n" + EVENT_FORMAT;
    public static final String ERR_MISSING_FROM =
            "OOPS!! Please specify a valid start time using '/from'.\n" + EVENT_FORMAT;
    public static final String ERR_MISSING_TO =
            "OOPS!! Please specify a valid end time using '/to'.\n" + EVENT_FORMAT;
    public static final String ERR_OUT_OF_ORDER =
            "OOPS!! '/to' cannot be before '/from' in ordering.\n" + EVENT_FORMAT;
    public static final String ERR_EVENT_CHRONOLOGY =
            "OOPS!! Event start time cannot be after the end time.\n" + EVENT_FORMAT;

    // Fixed error methods
    public static final String ERR_EMPTY_FIXED =
            "The description of a fixed-duration task cannot be empty." + FIXED_FORMAT;
    public static final String ERR_MISSING_NEEDS =
            "A fixed-duration task must specify a duration using ' /needs '." + FIXED_FORMAT;
    public static final String ERR_EMPTY_DURATION =
            "The duration cannot be empty." + FIXED_FORMAT;

    // File error methods
    public static final String ERR_FILE_SAVE = "Failed to save tasks: ";
    public static final String ERR_FILE_LOAD = "Failed to load tasks: ";
    public static final String ERR_FILE_CORRUPT = "Corrupted line in file: ";
    public static final String ERR_FILE_DEADLINE = "Corrupted Deadline entry: ";
    public static final String ERR_FILE_EVENT = "Corrupted Event entry: ";
    public static final String ERR_FILE_FIXED = "Corrupted fixed-duration task in save file: ";
    public static final String ERR_FILE_UNKNOWN = "Unknown task type in file: ";

    // Undo method strings
    public static final String TIP_UNDO =
            "Accidentally deleted this task? Type 'undo' to restore this task immediately!!";
    public static final String ERR_NO_UNDO_TASK =
            "There is no recently deleted task to undo!";
    public static final String MSG_TASK_RESTORED =
            "Yayyy!! Brought it right back from the void for you! :D";

    // --- Private Constructor --- //

    private Message() {
        // Prevent instantiation
    }

    // --- List Total Methods (with variables) --- //

    /**
     * Generates a message indicating the current total number of tasks.
     *
     * @param count Current total number of tasks in the list.
     * @return Formatted string showing task count.
     */
    public static String getMsgTaskCount(int count) {
        return "Now you have " + count + " task" + (count == 1 ? "" : "s") + " in the list.\n";
    }

    /**
     * Generates an error message indicating that a provided task index is out of bounds.
     *
     * @param count Current total number of tasks in the list.
     * @return Formatted out-of-bounds error message.
     */
    public static String getErrOutOfBounds(int count) {
        return "OOPS!! Task index out of bounds.\n" + INDEX_FORMAT + SYNTAX_VALID_RANGE
                + (count == 0 ? "No tasks available" : "1-" + count) + "\n";
    }

    /**
     * Constructs a warning message detailing corrupted storage records that were bypassed during load.
     *
     * @param skippedLines List of raw lines with line numbers that failed parsing.
     * @return Formatted warning dialogue for the user.
     */
    public static String getLoadWarningMessage(List<String> skippedLines) {
        StringBuilder sb = new StringBuilder();
        sb.append("A mortal corruption was detected in the archives!! :< \nPress F to pay respects.\n");
        sb.append("The following entries could not be resurrected and were skipped:\n\n");
        for (String entry : skippedLines) {
            sb.append("  • ").append(entry).append("\n");
        }
        sb.append("\nHow to resolve this:\n");
        sb.append("1. To recover data: Close the app and edit 'data/tasks.txt' to supply missing fields.\n");
        sb.append("2. To discard corrupted lines: Add, edit or delete any task to overwrite the save file.\n");
        sb.append("\nYou may right click the message to copy out corrupted lines and paste them somewhere else."
                + " They cannot be recovered once the data.txt file is overwritten!!\n");
        return sb.toString().trim();
    }
}
