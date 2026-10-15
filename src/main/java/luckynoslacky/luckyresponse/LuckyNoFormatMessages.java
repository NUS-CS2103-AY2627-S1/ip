package luckynoslacky.luckyresponse;

import luckynoslacky.luckyparser.LuckyNoParser;

/**
 * Provides command syntax descriptions and invalid-format responses.
 */
public final class LuckyNoFormatMessages {
    private static final String TODO_FORMAT = "<description>";
    private static final String DEADLINE_FORMAT =
            "<description> /by <date/time>.";
    private static final String EVENT_FORMAT =
            "<description> /from <start date/time> /to <end date/time>.";
    private static final String FIND_FORMAT = "[<description>] [/on <date>]";
    private static final String SNOOZE_BY_FORMAT =
            "<taskNumber> [/by <duration>]";
    private static final String SNOOZE_TO_FORMAT =
            "<taskNumber> [/to <end date/time>]";
    private static final String RESCHED_DEADLINE_FORMAT =
            "<taskNumber> /to <date/time>";
    private static final String RESCHED_EVENT_FORMAT =
            "<taskNumber> /from <start date/time> /to <end date/time>";

    private LuckyNoFormatMessages() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Returns a message for an invalid command format.
     *
     * @param commandName command whose format is invalid
     * @param validFormats one or more valid formats for the command; when no
     *     formats are supplied, the command's default format is used
     * @return formatted error message
     */
    public static String invalidFormatMessage(
            LuckyNoParser.CommandName commandName,
            String... validFormats) {
        if (commandName == null) {
            throw new IllegalArgumentException("Command name cannot be null.");
        }

        String[] formats = validFormats.length == 0
                ? new String[]{getDefaultFormat(commandName)}
                : validFormats;
        String format = String.join(" or ", formats);

        return String.join("\n",
                "Eh HELLO you know how to type command one anot? ",
                "Lai lai let me teach you: `"
                        + commandName.getInputName() + " " + format + "`");
    }

    /**
     * Returns the expected ToDo command format.
     *
     * @return ToDo format
     */
    public static String todoFormat() {
        return TODO_FORMAT;
    }

    /**
     * Returns the expected deadline command format.
     *
     * @return deadline format
     */
    public static String deadlineFormat() {
        return DEADLINE_FORMAT;
    }

    /**
     * Returns the expected event command format.
     *
     * @return event format
     */
    public static String eventFormat() {
        return EVENT_FORMAT;
    }

    /**
     * Returns the expected find command format.
     *
     * @return find format
     */
    public static String findFormat() {
        return FIND_FORMAT;
    }

    /**
     * Returns the duration-based snooze format.
     *
     * @return duration-based snooze format
     */
    public static String snoozeByFormat() {
        return SNOOZE_BY_FORMAT;
    }

    /**
     * Returns the explicit-ending-time snooze format.
     *
     * @return explicit-ending-time snooze format
     */
    public static String snoozeToFormat() {
        return SNOOZE_TO_FORMAT;
    }

    /**
     * Returns the deadline rescheduling format.
     *
     * @return deadline rescheduling format
     */
    public static String reschedDeadlineFormat() {
        return RESCHED_DEADLINE_FORMAT;
    }

    /**
     * Returns the event rescheduling format.
     *
     * @return event rescheduling format
     */
    public static String reschedEventFormat() {
        return RESCHED_EVENT_FORMAT;
    }

    /**
     * Returns the default format for a command that supports format errors.
     *
     * @param commandName command whose default format is needed
     * @return default command format
     */
    private static String getDefaultFormat(LuckyNoParser.CommandName commandName) {
        return switch (commandName) {
            case TODO -> TODO_FORMAT;
            case DEADLINE -> DEADLINE_FORMAT;
            case EVENT -> EVENT_FORMAT;
            case FIND -> FIND_FORMAT;
            case SNOOZE -> SNOOZE_BY_FORMAT + "` or `snooze " + SNOOZE_TO_FORMAT;
            case RESCHED -> RESCHED_EVENT_FORMAT;
            default -> {
                assert false : "No format is defined for command: " + commandName;
                throw new IllegalArgumentException(
                        "No format is defined for this command.");
            }
        };
    }
}
