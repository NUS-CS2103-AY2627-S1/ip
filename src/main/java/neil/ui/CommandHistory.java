package neil.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Stores a bounded history of submitted commands for terminal-style navigation.
 */
final class CommandHistory {
    private static final int MAX_COMMAND_COUNT = 10;

    private final List<String> commands = new ArrayList<>();
    private int currentIndex = -1;
    private String draft = "";

    /**
     * Adds a nonblank command and resets navigation to the newest position.
     *
     * @param command submitted command.
     */
    void add(String command) {
        assert command != null : "A command must not be null";

        if (command.isBlank()) {
            return;
        }
        if (commands.size() == MAX_COMMAND_COUNT) {
            commands.remove(0);
        }

        commands.add(command);
        currentIndex = -1;
        draft = "";
    }

    /**
     * Returns the previous command, stopping at the oldest command.
     *
     * @param currentInput text entered before navigating through history.
     * @return previous command, or an empty result when there is no history.
     */
    Optional<String> getPrevious(String currentInput) {
        assert currentInput != null : "Current input must not be null";

        if (commands.isEmpty()) {
            return Optional.empty();
        }

        if (currentIndex < 0) {
            draft = currentInput;
            currentIndex = commands.size() - 1;
        } else if (currentIndex > 0) {
            currentIndex--;
        }
        return Optional.of(commands.get(currentIndex));
    }

    /**
     * Returns the next command, followed by the text entered before navigation.
     *
     * @return next command or draft, or an empty result when navigation has not started.
     */
    Optional<String> getNext() {
        if (commands.isEmpty() || currentIndex < 0) {
            return Optional.empty();
        }

        if (currentIndex < commands.size() - 1) {
            currentIndex++;
            return Optional.of(commands.get(currentIndex));
        }

        currentIndex = -1;
        return Optional.of(draft);
    }
}
