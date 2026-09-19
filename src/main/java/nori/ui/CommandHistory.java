package nori.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Remembers the commands entered this session so an earlier one can be recalled.
 *
 * Browsing has a position of its own, which sits one past the newest command
 * when nobody is browsing. Stepping back walks towards the oldest command and
 * stops there; stepping forward walks towards the newest and then off the end,
 * which returns the empty line the user was typing on before they started
 * looking back.
 */
final class CommandHistory {
    /** The commands entered this session, oldest first. */
    private final List<String> commands = new ArrayList<>();

    /** Where browsing currently sits; the number of commands means "not browsing". */
    private int browseIndex;

    /** Creates an empty history. */
    CommandHistory() {
    }

    /**
     * Records a command the user has just entered, and stops browsing.
     *
     * A command identical to the one before it is not recorded twice, because
     * walking back through a run of the same command to reach what came before
     * it is work the history exists to save.
     *
     * @param command the command the user entered.
     */
    void add(String command) {
        assert command != null : "A command is recorded only after the window has read one.";

        boolean isRepeatOfNewest = !commands.isEmpty()
                && commands.get(commands.size() - 1).equals(command);
        if (!isRepeatOfNewest) {
            commands.add(command);
        }
        browseIndex = commands.size();
    }

    /**
     * Steps back towards the oldest command, and stops at it.
     *
     * @return the command to show, or empty when nothing has been entered yet.
     */
    Optional<String> recallEarlier() {
        if (commands.isEmpty()) {
            return Optional.empty();
        }
        if (browseIndex == 0) {
            return Optional.of(commands.get(0));
        }

        browseIndex--;
        return Optional.of(commands.get(browseIndex));
    }

    /**
     * Steps forward towards the newest command, and then off the end of it.
     *
     * @return the command to show, the empty line past the newest, or empty
     *         when browsing is already past the newest.
     */
    Optional<String> recallLater() {
        if (browseIndex >= commands.size()) {
            return Optional.empty();
        }

        browseIndex++;
        boolean isPastNewest = browseIndex == commands.size();
        return Optional.of(isPastNewest ? "" : commands.get(browseIndex));
    }
}
