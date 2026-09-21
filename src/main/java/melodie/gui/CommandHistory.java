package melodie.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Stores submitted commands and tracks navigation through them.
 */
final class CommandHistory {
    private final List<String> commands = new ArrayList<>();
    private int currentIndex;
    private String draft = "";

    /**
     * Adds a submitted command and resets navigation to the newest position.
     *
     * @param command Submitted command.
     */
    void record(String command) {
        this.commands.add(command);
        this.currentIndex = this.commands.size();
        this.draft = "";
    }

    /**
     * Returns the previous command, preserving unfinished input before navigation starts.
     *
     * @param currentInput Text currently in the input field.
     * @return Previous command, or an empty value when already at the oldest command.
     */
    Optional<String> getPreviousCommand(String currentInput) {
        if (this.currentIndex == 0) {
            return Optional.empty();
        }

        if (this.currentIndex == this.commands.size()) {
            this.draft = currentInput;
        }
        this.currentIndex--;
        return Optional.of(this.commands.get(this.currentIndex));
    }

    /**
     * Returns the next command, or restores the unfinished input after the newest command.
     *
     * @return Next command or draft, or an empty value when navigation cannot continue.
     */
    Optional<String> getNextCommand() {
        if (this.currentIndex >= this.commands.size()) {
            return Optional.empty();
        }

        this.currentIndex++;
        if (this.currentIndex == this.commands.size()) {
            return Optional.of(this.draft);
        }
        return Optional.of(this.commands.get(this.currentIndex));
    }

    /**
     * Reports whether an older command is currently being displayed.
     *
     * @return {@code true} while navigating submitted commands.
     */
    boolean isBrowsing() {
        return this.currentIndex < this.commands.size();
    }

    /**
     * Stops navigation after the user edits a recalled command.
     */
    void stopBrowsing() {
        this.currentIndex = this.commands.size();
        this.draft = "";
    }
}
