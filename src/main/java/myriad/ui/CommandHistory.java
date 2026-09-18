package myriad.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// AI-assisted: most of this file was written with Claude Code (Claude Opus),
// used by Ryan Chua, who reviewed and adapted the output.
/**
 * Remembers the commands the user has sent, so that they can be recalled
 * one at a time, newest first, like the history in a command-line shell.
 *
 * A cursor marks the entry currently recalled. It rests one step past the
 * newest entry whenever the user is not browsing, which is where a fresh
 * line is typed. This class uses no JavaFX, so it can be tested without a
 * running GUI; {@link MainWindow} connects it to the arrow keys.
 */
public class CommandHistory {

    /** Commands sent so far, oldest first. */
    private final List<String> commands = new ArrayList<>();

    /**
     * Index of the command currently recalled, or {@code commands.size()}
     * when none is, meaning the user is on a fresh line.
     */
    private int cursor = 0;

    /**
     * Records a command the user has just sent, and ends any browsing so
     * that the next recall starts again from the newest command. A blank
     * line, or a repeat of the command just before it, is not recorded, so
     * that stepping back through the history never shows the same line twice
     * in a row.
     *
     * @param command the line the user sent.
     */
    public void add(String command) {
        boolean isRepeat = !commands.isEmpty() && commands.get(commands.size() - 1).equals(command);
        if (!command.isBlank() && !isRepeat) {
            commands.add(command);
        }
        cursor = commands.size();
    }

    /**
     * Steps back to the next older command and returns it. At the oldest
     * command the cursor stays put, so pressing again keeps showing it.
     *
     * @return the older command, or empty if no command has been sent yet.
     */
    public Optional<String> getPrevious() {
        if (commands.isEmpty()) {
            return Optional.empty();
        }
        if (cursor > 0) {
            cursor--;
        }
        return Optional.of(commands.get(cursor));
    }

    /**
     * Steps forward to the next newer command and returns it. Stepping past
     * the newest command returns an empty line, which returns the user to a
     * fresh line to type on.
     *
     * @return the newer command, an empty string past the newest one, or
     *         empty if the user is not browsing the history.
     */
    public Optional<String> getNext() {
        if (cursor >= commands.size()) {
            return Optional.empty();
        }
        cursor++;
        if (cursor == commands.size()) {
            return Optional.of("");
        }
        return Optional.of(commands.get(cursor));
    }
}
