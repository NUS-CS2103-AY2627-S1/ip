package luckynoslacky.luckyresponse;

/**
 * Provides non-task-specific messages shown by the application.
 */
public final class LuckyNoQuips {
    private static final String INVALID_DURATION_FORMAT =
            "Eh can you be more specific anot, what do you mean by \"%s\" sia?";

    private LuckyNoQuips() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Returns the message for a missing task number.
     *
     * @return missing task number message
     */
    public static String missingTaskNumberMessage() {
        return "Eh which task you talking about har? Can say clearly anot.";
    }

    /**
     * Returns the message for an invalid task number entered by the user.
     *
     * @return invalid task number message
     */
    public static String invalidTaskNumberMessage() {
        return "You siao ah how to spin this task from thin air?";
    }

    /**
     * Returns the message for extra arguments after a command.
     *
     * @param commandName command that received extra arguments
     * @return extra-arguments message
     */
    public static String extraArgumentsMessage(String commandName) {
        return "Why you so losor! Leave the "
                + commandName + " command to do its own thing lah";
    }

    /**
     * Returns the message for a blank input line.
     *
     * @return missing command message
     */
    public static String missingCommandMessage() {
        return "Eh you mute issit?? Just say what you want lah!";
    }

    /**
     * Returns the message for a task command without a description.
     *
     * @return missing task description message
     */
    public static String missingTaskDescriptionMessage() {
        return "You don't tell me what to do how I know what to do???";
    }

    /**
     * Returns the message for an unrecognised command.
     *
     * @return unknown command message
     */
    public static String unknownCommandMessage() {
        return "What talking you? "
                + "I only understand todo, deadline, event, list, mark, unmark, "
                + "delete, find, snooze, resched, or bye, ok?";
    }

    /**
     * Returns the message shown when saved tasks cannot be loaded.
     *
     * @return task-loading error message
     */
    public static String loadErrorMessage() {
        return "Eh you so free ah, no tasks were loaded! "
                + "If you think this is salah, check your task data file.";
    }

    /**
     * Returns the message shown when tasks cannot be saved.
     *
     * @return task-saving error message
     */
    public static String saveErrorMessage() {
        return "Honggan la your system abit rabs ah, I cannot save your task";
    }

    /**
     * Returns the message shown when the task list is full.
     *
     * @return task-capacity error message
     */
    public static String taskLimitMessage() {
        return "Eh your task list too full already lah! Finish or delete "
                + "some tasks before adding more.";
    }

    /**
     * Returns the message shown when application configuration is invalid.
     *
     * @return configuration error message
     */
    public static String configurationErrorMessage() {
        return "Eh your configured clock not making sense lah! Remove or fix "
                + "`luckynoslacky.fixedNow` and try again.";
    }

    /**
     * Returns the message shown for an invalid date or time.
     *
     * @return invalid date/time message
     */
    public static String invalidDateTimeMessage() {
        return "Eh mr smart alec you tell me your calendar and clock got tell you "
                + "time like this one meh?";
    }

    /**
     * Returns the message shown for unsupported decimal calendar durations.
     *
     * @return decimal calendar duration message
     */
    public static String decimalCalendarDurationMessage() {
        return "Paiseh bro... i cannot settle decimal values for years and months yet...";
    }

    /**
     * Returns a message for an unrecognised snooze duration.
     *
     * @param durationText duration text supplied by the user
     * @return message asking the user to clarify the duration
     */
    public static String invalidDurationMessage(String durationText) {
        return String.format(INVALID_DURATION_FORMAT, durationText);
    }

    /**
     * Returns the message shown when a snooze duration exceeds the supported date range.
     *
     * @return snooze-duration overflow message
     */
    public static String snoozeOverflowMessage() {
        return "Siao ah delay so long, by that time your great grandson also die already la! "
                + "Can be more reasonable anot!";
    }

    /**
     * Returns the message shown when a task's time order is impossible.
     *
     * @return time-order error message
     */
    public static String timeTravelMessage() {
        return "you think you time travelling issit? check your date and time properly hor!";
    }

    /**
     * Returns the message shown when a ToDo is used with a timed-task command.
     *
     * @param commandName snooze or reschedule command name
     * @return ToDo rejection message
     */
    public static String cannotScheduleTodoMessage(String commandName) {
        if (commandName == null || commandName.isBlank()) {
            throw new IllegalArgumentException("Command name cannot be blank.");
        }
        return "Eh mr blur sotong this task don't even have time for you to "
                + commandName + " la";
    }

    /**
     * Returns the chatbot banner.
     *
     * @return banner text
     */
    public static String banner() {
        return String.join("\n",
                "     .--\"\"\"\"\"--.",
                "   /  /^\\   /^\\  \\",
                "  |  .---------.  |",
                "  |  | | | | | |  |",
                "   \\ '---------' /",
                "     '-._____.-'",
                "    [NO SLACKING]",
                "  LuckyNoSlacky is here to help!");
    }

    /**
     * Returns the chatbot greeting.
     *
     * @return greeting message
     */
    public static String greeting() {
        return "Limpeh is LuckyNoSlacky, and I will confirm "
                + "make sure you're lucky and not slacky!";
    }

    /**
     * Returns the chatbot goodbye message.
     *
     * @return goodbye message
     */
    public static String goodbye() {
        return "Huh so fast zao ah, rest well ah!";
    }
}
