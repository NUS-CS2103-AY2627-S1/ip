package piplupbot;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link CommandHistory}, which lets the Up and Down keys bring back
 * earlier commands in the window.
 *
 * <p>Everything here is about the two ends of the history, where an
 * off-by-one would show: the oldest command, and the draft slot below the
 * newest. The keys themselves need a window and are checked by hand.</p>
 */
public class CommandHistoryTest {

    /**
     * With nothing sent yet, Up must leave what the user has typed alone. A
     * history that forgot to keep the draft would wipe the box instead.
     */
    @Test
    public void moveToOlder_emptyHistory_keepsTypedText() {
        CommandHistory history = new CommandHistory();

        history.moveToOlder("tod");

        assertEquals("tod", history.getSelected());
    }

    @Test
    public void moveToOlder_pastOldest_staysAtOldest() {
        CommandHistory history = new CommandHistory();
        history.add("todo a");
        history.add("todo b");

        history.moveToOlder("");
        history.moveToOlder("");
        history.moveToOlder("");

        assertEquals("todo a", history.getSelected());
    }

    @Test
    public void moveToNewer_afterMovingOlder_walksBackInOrder() {
        CommandHistory history = new CommandHistory();
        history.add("todo a");
        history.add("todo b");
        history.add("todo c");
        history.moveToOlder("");
        history.moveToOlder("");
        assertEquals("todo b", history.getSelected());

        history.moveToNewer("todo b");

        assertEquals("todo c", history.getSelected());
    }

    /**
     * Moving down past the newest command gives back what was typed before
     * moving up, as a terminal does, rather than an empty box.
     */
    @Test
    public void moveToNewer_pastNewest_restoresTypedText() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.moveToOlder("deadline ret");
        assertEquals("list", history.getSelected());

        history.moveToNewer("list");

        assertEquals("deadline ret", history.getSelected());
    }

    /**
     * Guards the draft being saved on Down as well as Up. If it were saved only
     * on Up, pressing Down while typing would bring back the older draft
     * ("todo") and lose the words typed since.
     */
    @Test
    public void moveToNewer_draftAlreadySelected_keepsLatestTypedText() {
        CommandHistory history = new CommandHistory();
        history.add("list");
        history.moveToOlder("todo");
        history.moveToNewer("list");

        history.moveToNewer("todo read book");

        assertEquals("todo read book", history.getSelected());
    }

    /**
     * After a command is sent, Up must start again from that newest command,
     * not from wherever the user had moved to before sending it.
     */
    @Test
    public void add_afterMovingOlder_upStartsFromNewestAgain() {
        CommandHistory history = new CommandHistory();
        history.add("todo a");
        history.add("todo b");
        history.moveToOlder("");
        history.moveToOlder("");

        history.add("todo c");
        history.moveToOlder("");

        assertEquals("todo c", history.getSelected());
    }

    /** The words typed before moving up were sent, so they are no longer a draft. */
    @Test
    public void add_afterDraftSaved_leavesEmptyDraft() {
        CommandHistory history = new CommandHistory();
        history.moveToOlder("todo a");

        history.add("todo a");

        assertEquals("", history.getSelected());
    }
}
