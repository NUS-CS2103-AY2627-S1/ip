package hermes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import hermes.ui.Ui;

/**
 * Tests what Hermes hands back to the window for each line of input.
 *
 * <p>The no-argument constructor and {@link Hermes#main} are left untested: both
 * use the real data/Hermes.txt, which a test must never read or overwrite.
 */
public class HermesTest {

    @TempDir
    Path tempDir;

    private Hermes newHermes() {
        return new Hermes(tempDir.resolve("Hermes.txt").toString());
    }

    @Test
    public void getResponse_ordinaryCommand_replyWithNeitherFlag() {
        Response response = newHermes().getResponse("todo read");

        assertEquals("""
                It is recorded. I have set down this task:
                  [T][ ] read
                Thy scroll now holds 1 task.
                """, response.text());
        assertFalse(response.isExit());
        assertFalse(response.isError());
    }

    @Test
    public void getResponse_bye_flaggedAsExit() {
        Response response = newHermes().getResponse("bye");

        assertTrue(response.isExit());
        assertFalse(response.isError());
    }

    @Test
    public void getResponse_inputThatCannotBeParsed_flaggedAsErrorWithExplanation() {
        Response response = newHermes().getResponse("todo");

        assertEquals("A todo must have a description, for instance: todo borrow book", response.text());
        assertTrue(response.isError());
        assertFalse(response.isExit());
    }

    @Test
    public void getResponse_commandThatFails_flaggedAsErrorWithExplanation() {
        Response response = newHermes().getResponse("delete 1");

        assertEquals("Alas, no task numbered 1 is to be found.", response.text());
        assertTrue(response.isError());
    }

    @Test
    public void getResponse_unrecognisedWord_notTreatedAsError() {
        // An unknown word is an ordinary reply rather than an exception.
        Response response = newHermes().getResponse("dance");

        assertFalse(response.isError());
    }

    @Test
    public void getResponse_severalLines_stateCarriesBetweenThem() {
        Hermes hermes = newHermes();
        hermes.getResponse("todo read");
        hermes.getResponse("mark 1");

        assertEquals("1. [T][X] read\n", hermes.getResponse("list").text());
    }

    @Test
    public void describeSkippedLines_everyLineReadable_saysNothing() throws IOException {
        Files.write(tempDir.resolve("Hermes.txt"), List.of("T | 0 | read"));

        assertEquals("", newHermes().describeSkippedLines());
    }

    @Test
    public void describeSkippedLines_unreadableLines_reportsHowMany() throws IOException {
        Files.write(tempDir.resolve("Hermes.txt"), List.of("rubbish", "T | 0 | read", "T | 0"));

        assertTrue(newHermes().describeSkippedLines()
                .startsWith("Error: Hermes could not read 2 lines in your task file, data/Hermes.txt."));
    }

    @Test
    public void describeUnreadableFile_fileOpens_saysNothing() throws IOException {
        Files.write(tempDir.resolve("Hermes.txt"), List.of("T | 0 | read"));

        assertEquals("", newHermes().describeUnreadableFile());
    }

    @Test
    public void describeUnreadableFile_fileCannotBeOpened_explainsHowToFixIt() throws IOException {
        Files.createDirectory(tempDir.resolve("Hermes.txt"));

        assertEquals(Ui.formatUnreadableError("data/Hermes.txt"), newHermes().describeUnreadableFile());
    }
}
