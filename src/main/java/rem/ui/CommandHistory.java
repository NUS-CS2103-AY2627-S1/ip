package rem.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Recalls submitted commands while keeping the unfinished input available after the newest entry.
 */
public class CommandHistory {
    private final List<String> commands = new ArrayList<>();
    private int position;
    private String draft = "";

    /**
     * Records a submission and resets navigation, ignoring blanks and consecutive duplicates.
     *
     * @param command Submitted input.
     */
    public void add(String command) {
        if (command.isBlank()) {
            return;
        }
        if (commands.isEmpty() || !commands.getLast().equals(command)) {
            commands.add(command);
        }
        position = commands.size();
        draft = "";
    }

    /**
     * Recalls an older command, saving the current draft when navigation begins.
     *
     * @param currentInput Current contents of the command field.
     * @return Older command, or unchanged input if no commands exist.
     */
    public String previous(String currentInput) {
        if (commands.isEmpty()) {
            return currentInput;
        }
        if (position == commands.size()) {
            draft = currentInput;
        }
        position = Math.max(0, position - 1);
        return commands.get(position);
    }

    /**
     * Recalls a newer command or restores the unfinished draft past the newest entry.
     *
     * @param currentInput Current contents of the command field.
     * @return Newer command, saved draft, or unchanged input when not browsing history.
     */
    public String next(String currentInput) {
        if (position == commands.size()) {
            return currentInput;
        }
        position++;
        return position == commands.size() ? draft : commands.get(position);
    }
}
