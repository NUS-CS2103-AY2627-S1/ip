package nova;

import java.util.ArrayList;
import java.util.List;

/**
 * Keeps the commands the user has typed so that the input field can recall them
 * with the up and down arrow keys, the way a terminal does.
 */
public class CommandHistory {
    private static final int MAX_ENTRIES = 100;

    private final List<String> entries = new ArrayList<>();
    private int cursor;

    /**
     * Adds a command to the history, ignoring blank input and an immediate
     * repeat of the last command.
     *
     * @param command the command the user submitted
     */
    public void add(String command) {
        String trimmed = command.strip();
        if (!trimmed.isEmpty() && (entries.isEmpty() || !entries.get(entries.size() - 1).equals(trimmed))) {
            entries.add(trimmed);
            if (entries.size() > MAX_ENTRIES) {
                entries.remove(0);
            }
        }
        cursor = entries.size();
    }

    /**
     * Returns the previous command, moving back through the history.
     *
     * @return the command to show, or the oldest one when already at the start
     */
    public String previous() {
        if (entries.isEmpty()) {
            return "";
        }
        cursor = Math.max(0, cursor - 1);
        return entries.get(cursor);
    }

    /**
     * Returns the next command, moving forward through the history. Moving past
     * the newest command clears the input field.
     *
     * @return the command to show, or an empty string at the end of the history
     */
    public String next() {
        if (entries.isEmpty()) {
            return "";
        }
        cursor = Math.min(entries.size(), cursor + 1);
        return cursor == entries.size() ? "" : entries.get(cursor);
    }
}
