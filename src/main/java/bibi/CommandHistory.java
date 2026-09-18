package bibi;

import java.util.ArrayList;
import java.util.List;

/**
 * Remembers commands for one session while keeping an unfinished draft separate.
 *
 * <p>History only supplies text for editing. Executing a recalled command remains the
 * input field's job, so browsing cannot accidentally change a task.
 */
public class CommandHistory {
    private final List<String> commands = new ArrayList<>();
    private int position;
    private String draftText = "";

    /**
     * Creates an empty history for a new window session.
     */
    public CommandHistory() {
    }

    /**
     * Records a submitted command and returns browsing to the newest position.
     *
     * @param commandText the submitted text, preserved as typed
     */
    public void add(String commandText) {
        if (commandText.isBlank()) {
            return;
        }
        commands.add(commandText);
        position = commands.size();
        draftText = "";
    }

    /**
     * Returns the previous command, saving the draft when browsing first starts.
     *
     * @param currentText the input field's current contents
     * @return an older command, or the unchanged input when history is empty
     */
    public String recallPrevious(String currentText) {
        if (commands.isEmpty()) {
            return currentText;
        }
        if (position == commands.size()) {
            draftText = currentText;
        }
        if (position > 0) {
            position--;
        }
        return commands.get(position);
    }

    /**
     * Returns the next command or restores the draft after the newest command.
     *
     * @param currentText the input field's current contents
     * @return newer history text, the saved draft, or unchanged input when not browsing
     */
    public String recallNext(String currentText) {
        if (position == commands.size()) {
            return currentText;
        }
        position++;
        return position == commands.size() ? draftText : commands.get(position);
    }
}
