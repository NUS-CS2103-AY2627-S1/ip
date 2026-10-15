package min.ui;

/**
 * Stores the text Min shows to the user.
 *
 * <p>Collecting the wording in one place keeps Min's voice consistent and means a change
 * of phrasing touches this class alone rather than every class that talks to the user.
 * Messages that embed a task, a note, or a count are built by the methods below; the rest
 * are plain constants.
 *
 * <p>Min's voice is relaxed and lightly funny, but errors stay helpful first and funny
 * second: every error below still tells the user exactly what to type.
 *
 * <p>Item text is passed in as an already-formatted {@code String} so that this class does
 * not depend on the task and note packages.
 */
public final class Messages {
    // Greeting and farewell.
    public static final String WELCOME = "Hi, I'm Min.\nTasks, notes, whatever - I got you."
            + "\nNew here? Try 'help'.";
    public static final String GOODBYE = "Later! Go touch some grass.";
    public static final String INPUT_PROMPT = "Type a command, or 'help'";
    public static final String HELP = "Here's what I know:\n"
            + "   todo <description>\n"
            + "   deadline <description> /by yyyy-mm-dd\n"
            + "   event <description> /from <time> /to <time>\n"
            + "   note <text>\n"
            + "   list, listtasks, listnotes\n"
            + "   find <keyword>\n"
            + "   mark <number>, unmark <number>, delete <number>\n"
            + "   deletenote <number>\n"
            + "   help, bye\n"
            + "Numbers match the last list you printed.";

    // Headings shown above a numbered list of items.
    public static final String TASK_LIST_HEADING = "Your tasks, in all their glory:";
    public static final String NOTE_LIST_HEADING = "And the notes you jotted down:";
    public static final String MATCHING_TASK_HEADING = "Tasks that match:";
    public static final String MATCHING_NOTE_HEADING = "Notes that match:";

    // Shown in place of a heading when a list has nothing to show.
    public static final String EMPTY_TASK_LIST = "No tasks. Great!";
    public static final String EMPTY_NOTE_LIST = "No notes. A blank canvas.";
    public static final String EMPTY_MATCHING_TASKS = "No tasks match that.";
    public static final String EMPTY_MATCHING_NOTES = "No notes match that either.";

    // Errors caused by invalid command input.
    public static final String INVALID_TODO = "A todo needs words. Try: todo <description>.";
    public static final String INVALID_DEADLINE =
            "That deadline's missing something. Try: deadline <description> /by yyyy-mm-dd.";
    public static final String INVALID_DEADLINE_DATE = "That date's not clicking. Use yyyy-mm-dd.";
    public static final String INVALID_EVENT =
            "Events need a start and an end. Try: event <description> /from <time> /to <time>.";
    public static final String INVALID_FIND = "Find what, exactly? Give me a keyword.";
    public static final String INVALID_NOTE = "An empty note is just paper. Try: note <text>.";
    public static final String INVALID_NOTE_TEXT =
            "No \" | \" in notes, sorry - that's my filing system.";
    public static final String INVALID_TASK_TEXT =
            "No \" | \" in task details - that's my filing system.";

    // Errors caused by unreadable or unwritable saved data.
    public static final String SAVE_DATA_ERROR =
            "Couldn't save that. My disk and I aren't on speaking terms right now.";
    public static final String LOAD_DATA_ERROR = "Couldn't open your saved data.";
    public static final String INVALID_SAVED_DEADLINE_DATE =
            "Saved deadline dates need to be yyyy-mm-dd. Fix data/min.txt.";

    // Text shown when Min cannot start.
    public static final String APP_NAME = "Min";
    public static final String STARTUP_ERROR_HEADER = "Min's having a moment";
    public static final String STARTUP_ERROR = "Min couldn't open your saved data.";
    public static final String UNEXPECTED_STARTUP_ERROR =
            "Min tripped over something unexpected and couldn't start.";

    /** Prevents instantiation of this constants holder. */
    private Messages() {
    }

    /**
     * Returns the error shown when the input is not a command Min knows.
     *
     * @param commandWords Every command word Min accepts, separated by commas.
     */
    public static String formatInvalidCommand(String commandWords) {
        return "Hmm, don't know that one. I speak: " + commandWords + ".";
    }

    /**
     * Returns the confirmation shown after a task is added.
     *
     * @param taskText The added task as it is displayed.
     * @param taskCount The number of tasks now in the list.
     */
    public static String formatAddedTask(String taskText, int taskCount) {
        return "Bet. Added to the pile:\n"
                + "   " + taskText + "\n"
                + "That's " + plural(taskCount, "task")
                + " now. No pressure. (Okay, maybe a little pressure.)";
    }

    /**
     * Returns the confirmation shown after a note is added.
     *
     * @param noteText The added note as it is displayed.
     * @param noteCount The number of notes now in the list.
     */
    public static String formatAddedNote(String noteText, int noteCount) {
        return "Cool, wrote it down:\n"
                + "   " + noteText + "\n"
                + "That's " + plural(noteCount, "note") + ". Your brain thanks you.";
    }

    /**
     * Returns the confirmation shown after a task is marked as done.
     *
     * @param taskText The marked task as it is displayed.
     */
    public static String formatMarkedTask(String taskText) {
        return "W. Nice, one down:\n"
                + "   " + taskText;
    }

    /**
     * Returns the confirmation shown after a task is marked as not done.
     *
     * @param taskText The unmarked task as it is displayed.
     */
    public static String formatUnmarkedTask(String taskText) {
        return "Back on the pile it goes. Happens:\n"
                + "   " + taskText;
    }

    /**
     * Returns the confirmation shown after a task is deleted.
     *
     * @param taskText The deleted task as it is displayed.
     * @param taskCount The number of tasks left in the list.
     */
    public static String formatRemovedTask(String taskText, int taskCount) {
        return "Poof. Gone:\n"
                + "   " + taskText + "\n"
                + plural(taskCount, "task") + " left. Lighter already.";
    }

    /**
     * Returns the confirmation shown after a note is deleted.
     *
     * @param noteText The deleted note as it is displayed.
     * @param noteCount The number of notes left in the list.
     */
    public static String formatRemovedNote(String noteText, int noteCount) {
        return "Tossed it:\n"
                + "   " + noteText + "\n"
                + plural(noteCount, "note") + " left.";
    }

    /**
     * Returns the error shown when saved task data cannot be understood.
     *
     * @param details The reason the saved data was rejected.
     */
    public static String formatCorruptedTaskData(String details) {
        return "Your saved tasks look scrambled. Fix or delete data/min.txt. Details: " + details;
    }

    /**
     * Returns the error shown when saved note data cannot be understood.
     *
     * @param details The reason the saved data was rejected.
     */
    public static String formatCorruptedNoteData(String details) {
        return "Your saved notes look scrambled. Fix or delete data/notes.txt. Details: " + details;
    }

    /**
     * Returns the error shown when a command needs an item number but none was given.
     *
     * @param itemName The singular name of the item, such as "task".
     * @param action The action the number was needed for, such as "mark".
     */
    public static String formatMissingIndex(String itemName, String action) {
        return "Which " + itemName + "? Give me a number to " + action + ".";
    }

    /**
     * Returns the error shown when a command needs an item but the list is empty.
     *
     * @param itemName The singular name of the item, such as "task".
     * @param action The action that could not be performed, such as "mark".
     */
    public static String formatNoItems(String itemName, String action) {
        return "No " + itemName + "s to " + action + ". Nothing to do here.";
    }

    /**
     * Returns the error shown when an item number falls outside the list.
     *
     * @param itemName The singular name of the item, such as "task".
     * @param itemCount The number of items the number may refer to.
     */
    public static String formatIndexOutOfRange(String itemName, int itemCount) {
        return "Pick a " + itemName + " number between 1 and " + itemCount + ".";
    }

    /**
     * Returns the error shown when an item number is not a whole number.
     *
     * @param itemName The singular name of the item, such as "task".
     */
    public static String formatIndexNotANumber(String itemName) {
        return "That " + itemName + " number needs to be a whole number. I'm not that clever.";
    }

    /**
     * Returns the count followed by the singular or plural form of the word.
     *
     * @param count The number of items.
     * @param singular The singular form of the item name, such as "task".
     */
    private static String plural(int count, String singular) {
        return count + " " + singular + (count == 1 ? "" : "s");
    }
}
