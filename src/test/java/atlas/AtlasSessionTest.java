package atlas;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import atlas.client.ClientList;
import atlas.storage.Storage;
import atlas.task.TaskList;

/**
 * Tests the command loop from the outside: which replies a command produces,
 * and what it leaves in the data file.
 *
 * The UI harness covers the same commands, but it matches each expected line as
 * a substring of the whole session output, so it cannot assert that a reply is
 * absent, cannot check the order in which checks run, and cannot read the data
 * file. These tests cover exactly those three things for the priority command.
 */
public class AtlasSessionTest {

    /** Records the replies a session produces so a test can inspect them. */
    private static final class Recorder implements AtlasSession.Output {
        private final List<String> messages = new ArrayList<>();
        private final List<String> errors = new ArrayList<>();

        @Override
        public void speak(String message) {
            messages.add(message);
        }

        @Override
        public void speakError(String message) {
            messages.add(message);
            errors.add(message);
        }

        @Override
        public void print(String text) {
            messages.add(text);
        }

        /**
         * Returns whether any recorded reply contains the given text.
         *
         * @param text text to look for.
         * @return {@code true} when some reply contains it.
         */
        private boolean contains(String text) {
            return messages.stream().anyMatch(message -> message.contains(text));
        }

        /**
         * Returns whether any reply that was reported as an error contains the
         * given text.
         *
         * @param text text to look for.
         * @return {@code true} when some error contains it.
         */
        private boolean hasError(String text) {
            return errors.stream().anyMatch(message -> message.contains(text));
        }
    }

    /** One session, writing to a data file of its own. */
    private static final class Fixture {
        private final Path dataFile;
        private final TaskList tasks = new TaskList();
        private final Recorder recorder = new Recorder();
        private final AtlasSession session;

        Fixture(Path dataFile) {
            this.dataFile = dataFile;
            this.session = new AtlasSession(new Storage(dataFile.toString()), tasks, new ClientList(), recorder);
        }

        /**
         * Feeds each command line to the session, in order.
         *
         * @param lines command lines to run.
         */
        private void run(String... lines) {
            for (String line : lines) {
                session.respond(line);
            }
        }

        /**
         * Returns the current contents of the data file.
         *
         * @return the stored text, or an empty string when nothing was saved.
         * @throws IOException if the file cannot be read.
         */
        private String storedText() throws IOException {
            return Files.exists(dataFile) ? Files.readString(dataFile) : "";
        }
    }

    @Test
    void rankingATaskWritesTheLevelToDiskImmediately(@TempDir Path tempDir) throws IOException {
        Fixture fixture = new Fixture(tempDir.resolve("atlas.txt"));

        fixture.run("todo buy milk", "priority 1 high");

        assertEquals("T | 0 | buy milk | high", fixture.storedText().strip());
    }

    @Test
    void clearingARankWritesTheClearedStateToDiskImmediately(@TempDir Path tempDir) throws IOException {
        Fixture fixture = new Fixture(tempDir.resolve("atlas.txt"));

        fixture.run("todo buy milk", "priority 1 high", "priority 1 none");

        assertEquals("T | 0 | buy milk", fixture.storedText().strip());
    }

    @Test
    void aBadTaskNumberIsReportedBeforeABadLevel(@TempDir Path tempDir) {
        Fixture fixture = new Fixture(tempDir.resolve("atlas.txt"));
        fixture.run("todo buy milk");

        fixture.run("priority 99 urgent");

        assertTrue(fixture.recorder.contains("No such task in the pantheon."));
        assertFalse(fixture.recorder.contains("The Fates know only"));
        assertNull(fixture.tasks.get(0).getPriority());
    }

    @Test
    void anUpperCaseLevelIsRejectedAndChangesNothing(@TempDir Path tempDir) {
        Fixture fixture = new Fixture(tempDir.resolve("atlas.txt"));
        fixture.run("todo buy milk");

        fixture.run("priority 1 HIGH");

        assertTrue(fixture.recorder.contains("The Fates know only"));
        assertNull(fixture.tasks.get(0).getPriority());
    }

    @Test
    void aBareCommandAsksForANumberAndATabSeparatesTheLevel(@TempDir Path tempDir) throws IOException {
        Fixture fixture = new Fixture(tempDir.resolve("atlas.txt"));

        fixture.run("todo buy milk", "priority ", "priority 1\thigh");

        assertTrue(fixture.recorder.contains("Which labour shall I rank?"));
        assertEquals("T | 0 | buy milk | high", fixture.storedText().strip());
    }

    @Test
    void reportsARejectedCommandAsAnErrorAndChangesNothing(@TempDir Path tempDir) throws IOException {
        Fixture fixture = new Fixture(tempDir.resolve("atlas.txt"));
        fixture.run("todo buy milk");

        fixture.run("event ritual /from /to 4pm", "priority 1 URGENT");

        assertTrue(fixture.recorder.hasError("Even Icarus launched from somewhere."));
        assertTrue(fixture.recorder.hasError("The Fates know only"));
        assertTrue(fixture.recorder.errors.stream().noneMatch(message -> message.contains("Got it")));
        assertEquals(1, fixture.tasks.size());
        assertEquals("T | 0 | buy milk", fixture.storedText().strip());
    }

    @Test
    void acceptsACommandThatStartsWithWhitespace(@TempDir Path tempDir) {
        Fixture fixture = new Fixture(tempDir.resolve("atlas.txt"));

        fixture.run("  todo buy milk", "\tlist");

        assertEquals(1, fixture.tasks.size());
        assertEquals("buy milk", fixture.tasks.get(0).getDescription());
        assertTrue(fixture.recorder.contains("1.[T][ ] buy milk"));
    }

    @Test
    void keepsARejectedEventOutOfTheFileEntirely(@TempDir Path tempDir) throws IOException {
        Fixture fixture = new Fixture(tempDir.resolve("atlas.txt"));
        fixture.run("todo first");

        fixture.run("event clash /from 2pm /to 2pm");

        assertTrue(fixture.recorder.hasError("Time flows one way, mortal:"));
        assertEquals(1, fixture.tasks.size());
        assertEquals("T | 0 | first", fixture.storedText().strip());
    }
}
