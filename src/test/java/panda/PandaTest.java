package panda;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests complete command workflows through Panda's public application boundary.
 */
class PandaTest {
    @TempDir
    private Path temporaryDirectory;

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private InputStream originalInput;
    private PrintStream originalOutput;

    @Test
    void getDefaultDataFilePath_returnsFileInsideDataDirectory() {
        assertEquals(Path.of("data", "info.txt"), Panda.getDefaultDataFilePath());
    }

    @BeforeEach
    void redirectStreams() {
        originalInput = System.in;
        originalOutput = System.out;
        System.setOut(new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    @AfterEach
    void restoreStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    void isExitCommand_exactByeOnly_returnsTrue() {
        Panda panda = createPanda("tasks.txt");

        assertTrue(panda.isExitCommand("bye"));
        assertFalse(panda.isExitCommand("bye "));
        assertFalse(panda.isExitCommand("Bye"));
        assertFalse(panda.isExitCommand("bye now"));
    }

    @Test
    void getResponse_completeWorkflow_updatesOutputAndStorage() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Panda panda = new Panda(dataFile.toString());

        assertTrue(panda.getResponse("todo read book").contains("[T][ ] read book"));
        assertTrue(panda.getResponse("deadline submit report /by 2026-09-15 18:00")
                .contains("[D][ ] submit report (by: Sep 15 2026 18:00)"));
        assertTrue(panda.getResponse(
                "event camp /from 2026-09-16 09:00 /to 2026-09-17 17:00")
                .contains("[E][ ] camp (from: Sep 16 2026 09:00 to: Sep 17 2026 17:00)"));
        assertTrue(panda.getResponse("mark 2").contains("[X] submit report"));
        assertTrue(panda.getResponse("unmark 2").contains("[ ] submit report"));
        assertTrue(panda.getResponse("update 1 /name read Java book")
                .contains("[T][ ] read Java book"));
        assertTrue(panda.getResponse(
                "update 2 /by 2026-09-20 20:00 /name submit final report")
                .contains("submit final report (by: Sep 20 2026 20:00)"));
        assertTrue(panda.getResponse(
                "update 3 /from 2026-09-18 10:00 /to 2026-09-19 12:00 /name camp two")
                .contains("camp two (from: Sep 18 2026 10:00 to: Sep 19 2026 12:00)"));

        String filteredList = panda.getResponse("list 2026-09-19");
        assertFalse(filteredList.contains("read Java book"));
        assertFalse(filteredList.contains("submit final report"));
        assertTrue(filteredList.contains("3.[E][ ] camp two"));
        assertTrue(panda.getResponse("find FINAL").contains("2.[D][ ] submit final report"));
        assertTrue(panda.getResponse("delete 1").contains("Now you have 2 tasks"));

        assertEquals(String.join(System.lineSeparator(),
                "D | 0 | submit final report | 2026-09-20 20:00",
                "E | 0 | camp two | 2026-09-18 10:00 | 2026-09-19 12:00"),
                Files.readString(dataFile, StandardCharsets.UTF_8));
    }

    @Test
    void getResponse_invalidCommands_returnsFocusedErrorsAndContinues() {
        Panda panda = createPanda("tasks.txt");

        assertEquals("OOPS!!! I'm bamboo-zled; I don't know what that means :-(",
                panda.getResponse("dance"));
        assertEquals("OOPS!!! This panda needs a valid task number after mark.",
                panda.getResponse("mark bamboo"));
        assertEquals("OOPS!!! This panda cannot find task 1 in the bamboo stack.",
                panda.getResponse("delete 1"));
        assertTrue(panda.getResponse("todo still works").contains("still works"));
    }

    @Test
    void getResponse_bye_returnsGoodbyeMessage() {
        Panda panda = createPanda("tasks.txt");

        assertEquals("Bye. Hope to see you again soon!", panda.getResponse("bye"));
    }

    @Test
    void getResponse_invalidUpdates_doNotPartiallyChangeOrSaveTask() throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Panda panda = new Panda(dataFile.toString());
        panda.getResponse("deadline report /by 2026-09-14 10:00");
        String originalData = Files.readString(dataFile, StandardCharsets.UTF_8);

        assertEquals("OOPS!!! This panda needs a valid date and time in yyyy-MM-dd HH:mm format.",
                panda.getResponse("update 1 /by invalid /name changed"));
        assertEquals(originalData, Files.readString(dataFile, StandardCharsets.UTF_8));
        assertTrue(panda.getResponse("list").contains(
                "report (by: Sep 14 2026 10:00)"));
        assertFalse(panda.getResponse("list").contains("changed"));

        assertEquals("OOPS!!! This panda cannot apply that timing update to this task type.",
                panda.getResponse(
                        "update 1 /from 2026-09-14 10:00 /to 2026-09-14 11:00"));
    }

    @Test
    void constructor_savedTasksExist_loadsThemForLaterSession() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Panda firstSession = new Panda(dataFile.toString());
        firstSession.getResponse("todo persisted task");
        firstSession.getResponse("mark 1");

        Panda secondSession = new Panda(dataFile.toString());
        String response = secondSession.getResponse("list");

        assertTrue(response.contains("1.[T][X] persisted task"));
    }

    @Test
    void getResponse_saveFailure_reportsErrorButKeepsInMemoryChange() throws IOException {
        Path blockedParent = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(blockedParent, "blocking file", StandardCharsets.UTF_8);
        Panda panda = new Panda(blockedParent.resolve("tasks.txt").toString());

        String response = panda.getResponse("todo retained task");

        assertEquals("OOPS!!! This panda could not save its bamboo archive.", response);
        assertTrue(panda.getResponse("list").contains("1.[T][ ] retained task"));
    }

    @Test
    void main_commandsThenEndOfInput_printsWelcomeResponsesAndGoodbye() {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        System.setIn(new ByteArrayInputStream(
                "todo command line task\nlist\n".getBytes(StandardCharsets.UTF_8)));

        Panda.main(new String[]{dataFile.toString()});

        String consoleOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(consoleOutput.contains("Hello! I'm Panda."));
        assertTrue(consoleOutput.contains("Got it. I've added this task:"));
        assertTrue(consoleOutput.contains("1.[T][ ] command line task"));
        assertTrue(consoleOutput.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    void run_malformedStoredRecord_printsLoadingErrorAndLoadsValidRecord()
            throws IOException {
        Path dataFile = temporaryDirectory.resolve("tasks.txt");
        Files.writeString(dataFile, String.join(System.lineSeparator(),
                "bad record",
                "T | 0 | valid task"), StandardCharsets.UTF_8);
        System.setIn(new ByteArrayInputStream("list\nbye\n".getBytes(StandardCharsets.UTF_8)));

        new Panda(dataFile.toString()).run();

        String consoleOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(consoleOutput.contains("Line 1 has no task description."));
        assertTrue(consoleOutput.contains("1.[T][ ] valid task"));
        assertTrue(consoleOutput.contains("Bye. Hope to see you again soon!"));
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void run_unreadableDataPath_reportsLoadingErrorAndStartsWithEmptyList()
            throws IOException {
        Path dataDirectory = temporaryDirectory.resolve("data-directory");
        Files.createDirectory(dataDirectory);
        System.setIn(new ByteArrayInputStream("list\nbye\n".getBytes(StandardCharsets.UTF_8)));

        new Panda(dataDirectory.toString()).run();

        String consoleOutput = output.toString(StandardCharsets.UTF_8);
        assertTrue(consoleOutput.contains(
                "OOPS!!! This panda cannot read its bamboo archive at " + dataDirectory + "."));
        assertTrue(consoleOutput.contains("Here are the tasks in your list:"));
        assertFalse(consoleOutput.contains("1.["));
    }

    private Panda createPanda(String fileName) {
        return new Panda(temporaryDirectory.resolve(fileName).toString());
    }
}
