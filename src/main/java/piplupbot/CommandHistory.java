package piplupbot;

import java.util.ArrayList;
import java.util.List;

// ACKNOWLEDGEMENTS: This Java file was written with the help of Claude.

/**
 * Represents the commands sent so far in the window, so that the user can bring
 * one back with the Up and Down keys and send it again, as in a terminal.
 *
 * <p>Picture the commands as a column, oldest at the top, with one more slot
 * below the newest: the <em>draft</em>, which holds whatever the user had typed
 * before they started moving through the history. Moving older from the draft
 * saves what is in the box as the draft first, so moving back down to it gives
 * those words back instead of wiping them. Moving never goes above the oldest
 * command or below the draft.</p>
 *
 * <p>This class knows nothing about JavaFX. {@link GuiUi} passes it the text in
 * the box and shows what it returns, which is what lets it be tested without a
 * window.</p>
 */
public class CommandHistory {
    /** The commands sent so far, oldest first. */
    private final List<String> commands = new ArrayList<>();

    /**
     * Which command is selected, as its index in {@link #commands}. When it
     * equals the number of commands, it is one past the newest: the draft.
     */
    private int selectedIndex = 0;

    /** What the user had typed before moving away from the draft slot. */
    private String draft = "";

    /**
     * Adds a command the user has just sent, as the newest, and goes back to an
     * empty draft below it, so the next Up brings back this command.
     *
     * @param command The command, as it was sent.
     */
    public void add(String command) {
        commands.add(command);
        selectedIndex = commands.size();
        draft = "";
    }

    /**
     * Selects the command before the selected one, unless the oldest is
     * already selected.
     *
     * @param currentText What is in the input box now. It is kept as the draft
     *                    if the draft is what is selected.
     */
    public void moveToOlder(String currentText) {
        saveDraftIfSelected(currentText);
        if (selectedIndex > 0) {
            selectedIndex--;
        }
    }

    /**
     * Selects the command after the selected one, or the draft after the
     * newest command. If the draft is already selected, nothing moves.
     *
     * @param currentText What is in the input box now. It is kept as the draft
     *                    if the draft is what is selected.
     */
    public void moveToNewer(String currentText) {
        // Saving here too matters: without it, pressing Down while typing a new
        // command would bring back an older draft and lose what was typed since.
        saveDraftIfSelected(currentText);
        if (selectedIndex < commands.size()) {
            selectedIndex++;
        }
    }

    /**
     * Returns what the input box should show: the selected command, or the
     * draft if no command is selected.
     */
    public String getSelected() {
        if (selectedIndex == commands.size()) {
            return draft;
        }
        return commands.get(selectedIndex);
    }

    /**
     * Keeps the text in the box as the draft, if the draft is what is selected.
     * Text in the box while an older command is selected is not kept: it is
     * that command, perhaps edited, and moving away drops the edit.
     */
    private void saveDraftIfSelected(String currentText) {
        if (selectedIndex == commands.size()) {
            draft = currentText;
        }
    }
}
