package lloyd.gui;

import java.util.ArrayList;
import java.util.List;

/** Stores submitted commands and supports moving through them in order. */
final class CommandHistory {
    private final List<String> commands = new ArrayList<>();

    private int currentIndex;
    private String draft = "";

    /** Adds a submitted command and returns navigation to the newest position. */
    void add(String command) {
        commands.add(command);
        currentIndex = commands.size();
        draft = "";
    }

    /**
     * Returns the previous command, preserving the unfinished input when navigation starts.
     *
     * @param currentInput Text in the input field before moving backward.
     * @return Previous command, or the current text when there is no history.
     */
    String previous(String currentInput) {
        if (commands.isEmpty()) {
            return currentInput;
        }

        if (currentIndex == commands.size()) {
            draft = currentInput;
        }
        if (currentIndex > 0) {
            currentIndex--;
        }
        return commands.get(currentIndex);
    }

    /**
     * Returns the next command, or the preserved unfinished input after the newest command.
     *
     * @param currentInput Text in the input field before moving forward.
     * @return Next command or the preserved unfinished input.
     */
    String next(String currentInput) {
        if (commands.isEmpty()) {
            return currentInput;
        }

        if (currentIndex < commands.size() - 1) {
            currentIndex++;
            return commands.get(currentIndex);
        }
        if (currentIndex == commands.size() - 1) {
            currentIndex++;
        }
        return draft;
    }
}
