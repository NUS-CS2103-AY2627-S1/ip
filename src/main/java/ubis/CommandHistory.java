package ubis;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores session commands and restores the unfinished draft after browsing history.
 */
public class CommandHistory {
    private final List<String> commands = new ArrayList<>();
    private int position;
    private String draft = "";

    /**
     * Records a submitted command and resets navigation to the draft position.
     */
    public void add(String command) {
        commands.add(command);
        position = commands.size();
        draft = "";
    }

    /**
     * Returns an older command, saving the current draft when browsing begins.
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
     * Returns a newer command, or restores the draft after the newest command.
     */
    public String next(String currentInput) {
        if (position == commands.size()) {
            return currentInput;
        }
        position++;
        return position == commands.size() ? draft : commands.get(position);
    }
}
