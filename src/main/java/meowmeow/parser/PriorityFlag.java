package meowmeow.parser;

import meowmeow.MeowmeowException;
import meowmeow.task.TaskPriority;

/**
 * The result of pulling an optional "/p" or "/priority" flag out of a
 * command's arguments: the priority it specified (or
 * {@link TaskPriority#NONE} if there was no flag) and the text left once the
 * flag and its value are removed.
 *
 * <p>The flag may appear anywhere in the arguments - before, after, or
 * between other markers like "/by" - because {@link #extractFrom} matches the
 * marker as a whole whitespace-delimited token, so "/p" can never be mistaken
 * for the start of "/priority", and a date like "2/12/2019" is never
 * mistaken for a marker at all. Kept separate from {@link Parser} because the
 * scan returns two results (a priority and a remainder) rather than one, and
 * is worth testing on its own.
 */
final class PriorityFlag {

    private static final String SHORT_MARKER = "/p";
    private static final String LONG_MARKER = "/priority";

    private final TaskPriority priority;
    private final String remainingArguments;

    private PriorityFlag(TaskPriority priority, String remainingArguments) {
        this.priority = priority;
        this.remainingArguments = remainingArguments;
    }

    /** Returns the priority the flag specified, or {@link TaskPriority#NONE} if there was no flag. */
    TaskPriority priority() {
        return priority;
    }

    /** Returns the arguments with the flag and its value removed, extra whitespace collapsed. */
    String remainingArguments() {
        return remainingArguments;
    }

    /**
     * Pulls a "/p" or "/priority" flag out of {@code arguments}, wherever it
     * appears.
     *
     * @param arguments the text after a "todo", "deadline" or "event" keyword.
     * @return the priority found (or {@link TaskPriority#NONE}) and the
     *     remaining arguments with the flag removed.
     * @throws MeowmeowException if more than one priority flag is present, a
     *     flag has no value after it, or its value is not a priority level
     *     Meowmeow recognises.
     */
    static PriorityFlag extractFrom(String arguments) throws MeowmeowException {
        int markerStart = nextMarkerStart(arguments, 0);
        if (markerStart < 0) {
            return new PriorityFlag(TaskPriority.NONE, arguments);
        }

        int markerEnd = markerStart + markerAt(arguments, markerStart).length();
        int valueStart = skipWhitespace(arguments, markerEnd);
        int valueEnd = endOfWord(arguments, valueStart);
        String value = arguments.substring(valueStart, valueEnd);
        if (value.isEmpty() || value.startsWith("/")) {
            if (value.equalsIgnoreCase(SHORT_MARKER) || value.equalsIgnoreCase(LONG_MARKER)) {
                throw new MeowmeowException(
                        " One priority per task, please.",
                        " I found more than one /p or /priority flag.");
            }
            throw new MeowmeowException(
                    " Tell me which priority to set, e.g. \"/p high\".",
                    " Levels: " + TaskPriority.acceptedLevels() + ".");
        }
        if (nextMarkerStart(arguments, valueEnd) >= 0) {
            throw new MeowmeowException(
                    " One priority per task, please.",
                    " I found more than one /p or /priority flag.");
        }

        TaskPriority priority = parseLevel(value);
        String before = arguments.substring(0, markerStart).trim();
        String after = arguments.substring(valueEnd).trim();
        String remainder = before.isEmpty() ? after : after.isEmpty() ? before : before + " " + after;
        return new PriorityFlag(priority, remainder);
    }

    /**
     * Parses a single priority level token, e.g. "high" or "l".
     *
     * @param token the level text a user typed.
     * @return the matching priority.
     * @throws MeowmeowException if the token is not a level Meowmeow recognises.
     */
    static TaskPriority parseLevel(String token) throws MeowmeowException {
        TaskPriority priority = TaskPriority.fromInput(token);
        if (priority == null) {
            throw new MeowmeowException(
                    " That's not a priority I know.",
                    " Levels: " + TaskPriority.acceptedLevels() + ".");
        }
        return priority;
    }

    /**
     * Returns the index of the next "/p" or "/priority" marker at or after
     * {@code fromIndex}, or -1 if there is none. A marker must start at the
     * beginning of {@code text} or right after whitespace, so a slash
     * embedded in other text (e.g. a date like "2/12/2019") is never
     * mistaken for one.
     */
    private static int nextMarkerStart(String text, int fromIndex) {
        for (int i = fromIndex; i < text.length(); i++) {
            if (text.charAt(i) != '/' || (i > 0 && !Character.isWhitespace(text.charAt(i - 1)))) {
                continue;
            }
            String marker = markerAt(text, i);
            if (marker.equalsIgnoreCase(LONG_MARKER) || marker.equalsIgnoreCase(SHORT_MARKER)) {
                return i;
            }
        }
        return -1;
    }

    /** Returns the whitespace-delimited token starting at {@code start}. */
    private static String markerAt(String text, int start) {
        return text.substring(start, endOfWord(text, start));
    }

    /** Returns the index of the first whitespace character at or after {@code from}, or the text's length. */
    private static int endOfWord(String text, int from) {
        int end = from;
        while (end < text.length() && !Character.isWhitespace(text.charAt(end))) {
            end++;
        }
        return end;
    }

    /** Returns the index of the first non-whitespace character at or after {@code from}, or the text's length. */
    private static int skipWhitespace(String text, int from) {
        int start = from;
        while (start < text.length() && Character.isWhitespace(text.charAt(start))) {
            start++;
        }
        return start;
    }
}
