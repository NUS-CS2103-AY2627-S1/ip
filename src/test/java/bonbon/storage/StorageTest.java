package bonbon.storage;

import bonbon.exception.BonBonException;
import bonbon.tasklist.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StorageTest {

    @TempDir
    Path tempDir;

    private Path testFilePath;
    private TaskList taskList;

    @BeforeEach
    public void setUp() {
        testFilePath = tempDir.resolve("test_bonbon.txt");
        taskList = new TaskList(10);
    }

    // writeFile Tests

    @Test
    public void writeFile_tasksInList_createsFileAndWritesFormattedData() throws IOException, BonBonException {
        taskList.addToDo("read book");
        Storage.writeFile(testFilePath, taskList);

        assertTrue(Files.exists(testFilePath));
        List<String> lines = Files.readAllLines(testFilePath);
        assertEquals(1, lines.size());
        assertEquals("T | 0 | read book", lines.get(0));
    }

    @Test
    public void writeFile_multipleTasks_overwritesWithCurrentListState() throws IOException, BonBonException {
        taskList.addToDo("buy groceries");

        LocalDateTime deadlineDate = LocalDateTime.of(2026, 9, 1, 18, 0);
        taskList.addDeadline("return book", deadlineDate);

        LocalDateTime eventStart = LocalDateTime.of(2026, 9, 1, 14, 0);
        LocalDateTime eventEnd = LocalDateTime.of(2026, 9, 1, 16, 0);
        taskList.addEvent("project meeting", eventStart, eventEnd, "Auditorium 2");

        Storage.writeFile(testFilePath, taskList);

        List<String> lines = Files.readAllLines(testFilePath);
        assertEquals(3, lines.size());
        assertEquals("T | 0 | buy groceries", lines.get(0));
        assertEquals("D | 0 | return book | 01/09/2026 1800", lines.get(1));
        assertEquals("E | 0 | project meeting | 01/09/2026 1400 | 01/09/2026 1600 | Auditorium 2", lines.get(2));
    }

    // loadFile Tests

    @Test
    public void loadFile_nonExistentFile_printsMessageAndDoesNotThrow() {
        Path nonExistentPath = tempDir.resolve("does_not_exist.txt");

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));

        try {
            Storage.loadFile(nonExistentPath, taskList);
            String consoleOutput = outputStream.toString();
            assertTrue(consoleOutput.contains("No saved data found!"));
            assertEquals(0, taskList.getCurrSize());
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    public void loadFile_validTasksInFile_populatesTaskList() throws Exception {
        Files.writeString(testFilePath,
                "T | 0 | read book\n"
                        + "D | 0 | assignment | 01/09/2026 1800\n"
                        + "E | 0 | team dinner | 15/10/2026 1900 | 15/10/2026 2100 | Food Court\n");

        Storage.loadFile(testFilePath, taskList);

        assertEquals(3, taskList.getCurrSize());
        assertEquals("read book", taskList.getTask(0).getName());
        assertEquals("assignment", taskList.getTask(1).getName());
        assertEquals("team dinner", taskList.getTask(2).getName());
    }

    @Test
    public void loadFile_markedTaskInFile_loadsTaskAsDone() throws Exception {
        Files.writeString(testFilePath, "T | 1 | submit paper\n");

        Storage.loadFile(testFilePath, taskList);

        assertEquals(1, taskList.getCurrSize());
        assertTrue(taskList.getTask(0).isDone());
    }
}