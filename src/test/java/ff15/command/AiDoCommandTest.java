package ff15.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ff15.FF15Exception;
import ff15.Storage;
import ff15.Ui;
import ff15.ai.AiAssistant;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/**
 * Tests that {@code @do} only ever suggests a command, and only one FF15 can run.
 * The AI is a lambda returning a fixed reply.
 */
public class AiDoCommandTest {

    @TempDir
    Path tempDir;

    private PrintStream realOut;
    private Ui ui;
    private Storage storage;

    @BeforeEach
    public void setUp() {
        realOut = System.out;
        System.setOut(new PrintStream(OutputStream.nullOutputStream()));
        ui = new Ui();
        storage = new Storage(tempDir.resolve("t.txt").toString(), tempDir.resolve("c.txt").toString());
    }

    @AfterEach
    public void tearDown() {
        System.setOut(realOut);
    }

    /** Returns a command whose AI always replies {@code reply}. */
    private AiDoCommand commandWhoseAiReplies(String reply) {
        return new AiDoCommand(new AiAssistant((systemPrompt, userPrompt) -> reply), "the request");
    }

    /** Runs {@code command} against empty lists. */
    private void execute(AiDoCommand command) throws Exception {
        command.execute(new TaskList(), new ContactList(), ui, storage);
    }

    @Test
    public void execute_validSuggestion_showsItAndOffersItWithoutRunningIt() throws Exception {
        TaskList tasks = new TaskList();

        commandWhoseAiReplies("todo read book").execute(tasks, new ContactList(), ui, storage);

        assertTrue(ui.drainTranscript().contains("  todo read book"));
        assertEquals("todo read book", ui.takeSuggestion());
        assertEquals(0, tasks.size());
    }

    @Test
    public void execute_suggestionTheParserRejects_throwsExceptionAndOffersNothing() {
        AiDoCommand command = commandWhoseAiReplies("deadline read book");

        assertThrows(FF15Exception.class, () -> execute(command));
        assertEquals("", ui.takeSuggestion());
    }

    @Test
    public void execute_suggestionIsAnotherAiCommand_throwsException() {
        for (String reply : new String[] {"@ai what can you do?", "@do add a task"}) {
            AiDoCommand command = commandWhoseAiReplies(reply);
            assertThrows(FF15Exception.class, () -> execute(command), reply);
        }
        assertEquals("", ui.takeSuggestion());
    }

    @Test
    public void takeSuggestion_calledAgain_hasNothingLeft() throws Exception {
        execute(commandWhoseAiReplies("list"));

        assertEquals("list", ui.takeSuggestion());
        assertEquals("", ui.takeSuggestion());
    }
}
