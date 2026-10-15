package ff15.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import ff15.Storage;
import ff15.Ui;
import ff15.ai.AiAssistant;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/** Tests how {@code @ai} shows the AI's answer. The AI is a lambda returning a fixed reply. */
public class AiAskCommandTest {

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

    @Test
    public void execute_answerOverSeveralLines_showsEachLineAndSkipsBlankOnes() throws Exception {
        AiAssistant assistant = new AiAssistant((systemPrompt, userPrompt) -> "First.\n\nSecond.");

        new AiAskCommand(assistant, "q").execute(new TaskList(), new ContactList(), ui, storage);

        String[] lines = ui.drainTranscript().split(System.lineSeparator());
        assertEquals(2, lines.length);
        assertEquals("First.", lines[0]);
        assertEquals("Second.", lines[1]);
    }
}
