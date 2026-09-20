package larper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class LarperTest {
    private static final String TODAY_PROPERTY = "larper.today";

    @TempDir
    private Path tempDir;

    @BeforeEach
    public void setUp() {
        System.setProperty(TODAY_PROPERTY, "2026-08-23");
    }

    @AfterEach
    public void tearDown() {
        System.clearProperty(TODAY_PROPERTY);
    }

    @Test
    public void getWelcomeMessage_newLarper_containsGreetingAndPrompt() {
        Larper larper = new Larper(tempDir.resolve("larperdata.txt"));

        assertEquals(" _\n"
                + "| |       __ _   _ __   _ __     ___   _ __\n"
                + "| |      / _` | | '__| | '_ \\   / _ \\ | '__|\n"
                + "| |___  | (_| | | |    | |_) | |  __/ | |\n"
                + "|_____|  \\__,_| |_|    | .__/   \\___| |_|\n"
                + "                       |_|\n"
                + "Larper has entered the chat.\n\n"
                + " Productivity expert (allegedly).\n"
                + " Let's get productive. Or at least look productive.\n\n"
                + " Drop a command.", larper.getWelcomeMessage());
    }

    @Test
    public void getResponse_addListExit_returnsExpectedMessages() {
        Larper larper = new Larper(tempDir.resolve("larperdata.txt"));

        LarperResponse addResponse = larper.getResponse("todo read book");
        LarperResponse listResponse = larper.getResponse("list");
        LarperResponse exitResponse = larper.getResponse("exit");

        assertFalse(addResponse.isExit());
        assertEquals(" Say less. Adding it to the agenda so we can feel productive:\n"
                + " [T][ ] read book\n"
                + " Agenda now has 1 public commitment.", addResponse.getMessage());
        assertFalse(listResponse.isExit());
        assertEquals(" The current agenda (very serious):\n"
                + " 1. [T][ ] read book", listResponse.getMessage());
        assertTrue(exitResponse.isExit());
        assertEquals(" Aight, I'm clocking out. Continue larping productivity without me.",
                exitResponse.getMessage());
    }

    @Test
    public void getTaskSummaries_afterTaskChanges_returnsCurrentTasks() {
        Larper larper = new Larper(tempDir.resolve("larperdata.txt"));

        larper.getResponse("todo read book");
        larper.getResponse("deadline submit report /by 2026-08-23 no time");
        larper.getResponse("mark 2");
        larper.getResponse("delete 1");

        assertEquals(List.of("[D][X] submit report (by: Aug 23 2026)"), larper.getTaskSummaries());
    }

    @Test
    public void getResponse_invalidCommand_returnsErrorResponse() {
        Larper larper = new Larper(tempDir.resolve("larperdata.txt"));

        LarperResponse response = larper.getResponse("hello");

        assertFalse(response.isExit());
        assertTrue(response.isError());
    }

    @Test
    public void getResponse_help_returnsCommandGuide() {
        Larper larper = new Larper(tempDir.resolve("larperdata.txt"));

        LarperResponse response = larper.getResponse("help");

        assertFalse(response.isExit());
        assertFalse(response.isError());
        assertTrue(response.getMessage().contains("todo DESCRIPTION"));
        assertTrue(response.getMessage().contains("delete NUMBER"));
    }

    @Test
    public void getResponse_misspelledCommand_returnsSuggestionError() {
        Larper larper = new Larper(tempDir.resolve("larperdata.txt"));

        LarperResponse response = larper.getResponse("delet 2");

        assertFalse(response.isExit());
        assertTrue(response.isError());
        assertEquals(" Minor misinformation detected.\n"
                + " Unknown command: `delet`\n"
                + " Did you mean `delete 2`?\n\n"
                + " Type `help` before freelancing syntax.", response.getMessage());
    }

    @Test
    public void getResponse_invalidTaskNumber_returnsErrorResponse() {
        Larper larper = new Larper(tempDir.resolve("larperdata.txt"));

        larper.getResponse("todo read book");
        LarperResponse response = larper.getResponse("mark 999");

        assertFalse(response.isExit());
        assertTrue(response.isError());
        assertEquals(" That task literally does not exist.", response.getMessage());
    }

    @Test
    public void getResponse_findFlow_returnsPromptAndMatches() {
        Larper larper = new Larper(tempDir.resolve("larperdata.txt"));

        larper.getResponse("todo read book");
        LarperResponse promptResponse = larper.getResponse("find");
        LarperResponse findResponse = larper.getResponse("READ BOOK");

        assertFalse(promptResponse.isExit());
        assertEquals(" Drop the keywords. I'll find the receipts.", promptResponse.getMessage());
        assertFalse(findResponse.isExit());
        assertEquals(" Caught in 4K. Here's what I found:\n"
                + " [T][ ] read book (task no: 1)", findResponse.getMessage());
    }

    @Test
    public void getResponse_tagUntagAndFindTag_updatesAndSearchesTasks() {
        Larper larper = new Larper(tempDir.resolve("larperdata.txt"));

        larper.getResponse("todo prepare slides");
        LarperResponse tagResponse = larper.getResponse("tag 1 #SCHOOL urgent");
        LarperResponse findResponse = larper.getResponse("find tag school");
        larper.getResponse("mark 1");
        LarperResponse untagResponse = larper.getResponse("untag 1 #school");

        assertEquals(" Labeled task 1. Personal brand curated:\n"
                + " [T][ ] prepare slides [#school] [#urgent]", tagResponse.getMessage());
        assertEquals(" Caught in 4K. Here's what I found:\n"
                + " [T][ ] prepare slides [#school] [#urgent] (task no: 1)", findResponse.getMessage());
        assertEquals(" Rebranded task 1. Removed that label:\n"
                + " [T][X] prepare slides [#urgent]", untagResponse.getMessage());
    }

    @Test
    public void getWelcomeMessage_malformedSavedLine_reportsSkippedLine() throws Exception {
        Path dataFile = tempDir.resolve("larperdata.txt");
        Files.writeString(dataFile, """
                T | 0 | read book
                not valid
                """);

        Larper larper = new Larper(dataFile);

        assertTrue(larper.getWelcomeMessage().contains("Larper skipped saved data line(s): 2."));
    }

    @Test
    public void getResponse_addWhenSaveFails_taskListUnchanged() {
        Larper larper = new Larper(tempDir);

        LarperResponse response = larper.getResponse("todo read book");

        assertTrue(response.isError());
        assertTrue(larper.getTaskSummaries().isEmpty());
    }

    @Test
    public void getResponse_markWhenSaveFails_taskStatusUnchanged() throws Exception {
        Path dataFile = tempDir.resolve("larperdata.txt");
        Files.writeString(dataFile, "T | 0 | read book\n");
        Larper larper = new Larper(dataFile);
        Files.delete(dataFile);
        Files.createDirectory(dataFile);

        LarperResponse response = larper.getResponse("mark 1");

        assertTrue(response.isError());
        assertEquals(List.of("[T][ ] read book"), larper.getTaskSummaries());
    }
}
