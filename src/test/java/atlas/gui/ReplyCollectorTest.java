package atlas.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import atlas.AtlasSession;
import atlas.client.ClientList;
import atlas.storage.Storage;
import atlas.task.TaskList;

/**
 * Pins the error flag the GUI reads to decide whether a reply belongs in an
 * ordinary bubble or an error bubble.
 *
 * The GUI itself cannot be tested here, because JavaFX needs a display that the
 * test runner does not have. The decision the GUI depends on is therefore kept
 * in a plain class with no JavaFX imports, and tested instead.
 */
public class ReplyCollectorTest {

    @TempDir
    Path temporaryDirectory;

    /**
     * Runs each command in order through a fresh session, keeping the reply of
     * the last command.
     *
     * @param lines command lines to run in order.
     * @return the collector holding the last command's reply.
     */
    private ReplyCollector lastReplyOf(String... lines) {
        ReplyCollector collector = new ReplyCollector();
        AtlasSession session = new AtlasSession(
                new Storage(temporaryDirectory.resolve("atlas.txt").toString()),
                new TaskList(), new ClientList(), collector);
        for (String line : lines) {
            collector.clear();
            session.respond(line);
        }
        return collector;
    }

    @Test
    void marksARejectedCommandAsAnError() {
        ReplyCollector collector = lastReplyOf("mark 99");
        assertTrue(collector.isError());
        assertTrue(collector.getReply().contains("No such task in the pantheon."));
    }

    @Test
    void doesNotMarkOrdinaryOutputAsAnError() {
        ReplyCollector collector = lastReplyOf("todo buy milk");
        assertFalse(collector.isError());
    }

    @Test
    void forgetsTheErrorFlagWhenTheNextCommandSucceeds() {
        ReplyCollector collector = lastReplyOf("mark 99", "todo buy milk");
        assertFalse(collector.isError());
    }

    @Test
    void keepsTheErrorFlagForAPrintedReply() {
        ReplyCollector collector = lastReplyOf("todo buy milk", "list");
        assertFalse(collector.isError());
        assertTrue(collector.getReply().contains("buy milk"));
    }
}
