package herta;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Maintains bounded command history independently of JavaFX controls.
 */
final class CommandHistory {
    private final int maximumEntries;
    private final List<String> commands = new ArrayList<>();
    private int navigationIndex;
    private String draft = "";

    CommandHistory(int maximumEntries) {
        if (maximumEntries <= 0) {
            throw new IllegalArgumentException("Command history capacity must be positive.");
        }
        this.maximumEntries = maximumEntries;
    }

    /** Records a non-blank command unless it repeats the newest history entry. */
    void record(String command) {
        boolean isBlankCommand = command.isBlank();
        boolean hasPreviousCommand = !commands.isEmpty();
        boolean isDuplicateCommand = hasPreviousCommand
                && command.equals(commands.getLast());
        if (!isBlankCommand && !isDuplicateCommand) {
            commands.add(command);
            removeExcessCommands();
        }
        resetNavigation();
    }

    /** Moves to the previous command and captures the current draft at the newest position. */
    Optional<String> moveToPreviousCommand(String currentDraft) {
        if (commands.isEmpty()) {
            return Optional.empty();
        }
        if (navigationIndex == commands.size()) {
            draft = currentDraft;
        }
        navigationIndex = Math.max(0, navigationIndex - 1);
        return Optional.of(commands.get(navigationIndex));
    }

    /** Moves to the next command or the draft after the newest command. */
    Optional<String> moveToNextCommand() {
        if (navigationIndex >= commands.size()) {
            return Optional.empty();
        }
        navigationIndex++;
        return navigationIndex == commands.size()
                ? Optional.of(draft) : Optional.of(commands.get(navigationIndex));
    }

    /** Removes the oldest entries when the configured capacity is exceeded. */
    private void removeExcessCommands() {
        if (commands.size() > maximumEntries) {
            commands.removeFirst();
        }
    }

    /** Returns navigation to the position after the newest command. */
    private void resetNavigation() {
        navigationIndex = commands.size();
        draft = "";
    }
}
