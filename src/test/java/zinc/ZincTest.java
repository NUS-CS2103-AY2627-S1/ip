package zinc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Tests Zinc's terminal command loop. */
public class ZincTest {
    private static final Path TASK_STORAGE_FILE = Path.of("data", "zincTasks.txt");
    private static final Path CONTACT_STORAGE_FILE = Path.of("data", "zincContacts.txt");

    private static final String CORRUPTED_TASK_DATA_WARNING =
            "NOTE: Your saved task data has been corrupted. Zinc started with an empty task list.\n";

    private static final String CORRUPTED_CONTACT_DATA_WARNING =
            "NOTE: Your saved contact data has been corrupted. Zinc started with an empty contact list.\n";

    @BeforeEach
    @AfterEach
    public void clearStorage() throws Exception {
        Files.deleteIfExists(TASK_STORAGE_FILE);
        Files.deleteIfExists(CONTACT_STORAGE_FILE);
    }

    @Test
    public void main_multipleCommandsBeforeBye_processesEveryCommand() {
        InputStream originalInput = System.in;
        PrintStream originalOutput = System.out;
        String commands = "todo First\ntodo Second\nlist\nbye\n";
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        System.setIn(new ByteArrayInputStream(commands.getBytes(StandardCharsets.UTF_8)));
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
        try {
            Zinc.main(new String[0]);
        } finally {
            System.setIn(originalInput);
            System.setOut(originalOutput);
        }

        String terminalOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(terminalOutput.contains("2. [T][ ] Second"));
        assertTrue(terminalOutput.contains("Goodbye."));
    }

    @Test
    public void processCommand_duplicateTask_returnsWarningWithoutAddingTask() {
        Zinc zinc = new Zinc();

        zinc.processCommand("todo Tune guitar");
        String response = zinc.processCommand("todo Tune guitar");

        assertTrue(response.contains("That task already exists in the database. Type \"list\" to see your tasks."));
        assertTrue(zinc.processCommand("list").contains("1. [T][ ] Tune guitar"));
        assertFalse(zinc.processCommand("list").contains("2. [T][ ] Tune guitar"));
    }

    @Test
    public void guiResponses_defaultUi_returnPersonalityMessages() {
        Zinc zinc = new Zinc();

        assertTrue(List.of(
                "Hello, I'm Zinc. Ready to rock your tasks?",
                "Zinc is plugged in! What shall we tackle?",
                "Hello! Let's tune up your day together.").contains(zinc.getGreetingResponse()));
        assertTrue(List.of(
                "What's next on the setlist?",
                "Ready for another riff?",
                "What shall we rock through next?").contains(zinc.getConversationPromptResponse()));
        assertTrue(List.of(
                "Goodbye. Keep rocking!",
                "Goodbye. Until the next encore!",
                "Goodbye. The stage is yours!").contains(zinc.getGoodbyeResponse()));
    }

    @Test
    public void getGreetingResponse_corruptedTaskData_includesTaskWarning() throws Exception {
        writeCorruptedData(TASK_STORAGE_FILE);

        Zinc zinc = new Zinc();

        assertTrue(zinc.getGreetingResponse().endsWith("\n" + CORRUPTED_TASK_DATA_WARNING));
    }

    @Test
    public void getGreetingResponse_corruptedContactData_includesContactWarning() throws Exception {
        writeCorruptedData(CONTACT_STORAGE_FILE);

        Zinc zinc = new Zinc();

        assertTrue(zinc.getGreetingResponse().endsWith("\n" + CORRUPTED_CONTACT_DATA_WARNING));
    }

    @Test
    public void getGreetingResponse_bothDataFilesCorrupted_separatesBothWarnings() throws Exception {
        writeCorruptedData(TASK_STORAGE_FILE);
        writeCorruptedData(CONTACT_STORAGE_FILE);

        Zinc zinc = new Zinc();

        assertTrue(zinc.getGreetingResponse().endsWith(CORRUPTED_TASK_DATA_WARNING
                + CORRUPTED_CONTACT_DATA_WARNING));
    }

    /** Writes invalid content to a storage file used by a startup test. */
    private void writeCorruptedData(Path storageFile) throws Exception {
        Files.createDirectories(storageFile.getParent());
        Files.writeString(storageFile, "corrupted data");
    }
}
