package zinc.ui;

import java.util.ArrayList;
import java.util.List;

/** Stores submitted commands and supports navigating through them. */
public class CommandHistory {
    /** The commands submitted during the current application session. */
    private final List<String> commands = new ArrayList<>();

    /** The index currently selected while navigating, or {@code -1} for the draft. */
    private int selectedIndex = -1;

    /** The input text that was present when history navigation began. */
    private String draft = "";

    /** Adds a command to the end of the history and returns to the draft position. */
    public void addCommand(String command) {
        assert command != null : "Command must not be null";
        commands.add(command);
        resetNavigation();
    }

    /** Returns the previous command, or the current input when no previous command exists. */
    public String navigateUp(String currentInput) {
        assert currentInput != null : "Current input must not be null";

        if (commands.isEmpty()) {
            return currentInput;
        }

        if (selectedIndex == -1) {
            draft = currentInput;
            selectedIndex = commands.size() - 1;
        } else if (selectedIndex > 0) {
            selectedIndex--;
        }

        return commands.get(selectedIndex);
    }

    /** Returns the next command, or the current input after the newest command. */
    public String navigateDown(String currentInput) {
        assert currentInput != null : "Current input must not be null";

        if (selectedIndex == -1) {
            return currentInput;
        }

        if (selectedIndex < commands.size() - 1) {
            selectedIndex++;
            return commands.get(selectedIndex);
        }

        String restoredDraft = draft;
        selectedIndex = -1;
        return restoredDraft;
    }

    /** Returns navigation to the unsent draft position. */
    public void resetNavigation() {
        selectedIndex = -1;
        draft = "";
    }
}
