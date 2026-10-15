package piplupbot;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a {@link Ui} for tests. It keeps each reply instead of showing it, so a test
 * can check exactly what was said, in what order, and how many times --
 * including that nothing was said at all.
 *
 * <p>It writes only {@link Ui#show}. The greeting, the goodbye, lists and errors
 * come with the interface, just as they do for the two real faces, so an error
 * arrives here as an ordinary reply, one line after another.</p>
 *
 * <p>The tests of several classes, in more than one package, use it. That is
 * why it is public and has a file of its own, rather than a copy nested inside
 * each test class.</p>
 */
public class RecordingUi implements Ui {
    /** Each reply so far, oldest first, its lines joined by line breaks. */
    private final List<String> replies = new ArrayList<>();

    @Override
    public void show(String... lines) {
        replies.add(String.join("\n", lines));
    }

    /**
     * Returns every reply so far, oldest first.
     *
     * @return A copy of the replies, each with its lines joined by line breaks.
     */
    public List<String> getReplies() {
        return List.copyOf(replies);
    }

    /**
     * Forgets every reply so far, so that a test can build up a list through the
     * bot and then look only at what the line under test makes it say.
     */
    public void clearReplies() {
        replies.clear();
    }
}
