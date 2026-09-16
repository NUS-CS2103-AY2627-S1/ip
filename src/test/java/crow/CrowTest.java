package crow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import crow.exception.CrowException;
import crow.storage.Storage;
import crow.task.Task;
import crow.ui.Ui;

class CrowTest {
    @TempDir
    Path tempDirectory;

    @Test
    void getResponse_taskCommands_updateAndPersistTasks() {
        Path filePath = tempDirectory.resolve("crow.txt");
        Crow crow = new Crow(filePath);

        assertTrue(crow.getResponse("todo read book").contains("[T][ ] read book"));
        assertTrue(crow.getResponse("deadline return book /by 2/12/2019 1800")
                .contains("[D][ ] return book"));
        assertTrue(crow.getResponse("mark 1").contains("[T][X] read book"));
        assertTrue(crow.getResponse("find book").contains("1.[T][X] read book"));
        assertTrue(crow.getResponse("delete 2").contains("return book"));

        Crow reloadedCrow = new Crow(filePath);
        String reloadedList = reloadedCrow.getResponse("list");
        assertTrue(reloadedList.contains("1.[T][X] read book"));
        assertFalse(reloadedList.contains("return book"));
    }

    @Test
    void getResponse_invalidCommand_returnsError() {
        Crow crow = new Crow(tempDirectory.resolve("crow.txt"));

        assertEquals("Error: Unknown command.", crow.getResponse("blah"));
        assertEquals("Error: Todo description cannot be empty.", crow.getResponse("todo"));
        assertEquals("Error: list does not accept parameters.", crow.getResponse("list extra"));
        assertEquals("Error: bye does not accept parameters.", crow.getResponse("bye now"));
    }

    @Test
    void getResponse_duplicateTask_returnsErrorWithoutAddingTask() {
        Crow crow = new Crow(tempDirectory.resolve("crow.txt"));
        crow.getResponse("todo read book");

        assertEquals("Error: This task already exists.", crow.getResponse("todo READ   BOOK"));
        assertTrue(crow.getResponse("list").contains("1.[T][ ] read book"));
        assertFalse(crow.getResponse("list").contains("2.[T]"));
    }

    @Test
    void getResponse_listCommand_automaticallyGroupsSortsAndPersistsTasks() {
        Path filePath = tempDirectory.resolve("crow.txt");
        Crow crow = new Crow(filePath);
        crow.getResponse("event dinner /from 6/9/2026 1900 /to 6/9/2026 2100");
        crow.getResponse("deadline report /by 5/9/2026 1800");
        crow.getResponse("todo visit zoo");
        crow.getResponse("todo borrow book");
        crow.getResponse("event meeting /from 5/9/2026 1000 /to 5/9/2026 1100");
        crow.getResponse("deadline assignment /by 4/9/2026 1800");

        String sortedResponse = crow.getResponse("list");

        assertTrue(sortedResponse.contains("ToDos:\n1.[T][ ] borrow book"));
        assertTrue(sortedResponse.contains("1.[T][ ] borrow book"));
        assertTrue(sortedResponse.contains("2.[T][ ] visit zoo"));
        assertTrue(sortedResponse.contains("--------------------\nDeadlines:"));
        assertTrue(sortedResponse.contains("3.[D][ ] assignment"));
        assertTrue(sortedResponse.contains("4.[D][ ] report"));
        assertTrue(sortedResponse.contains("--------------------\nEvents:"));
        assertTrue(sortedResponse.contains("5.[E][ ] meeting"));
        assertTrue(sortedResponse.contains("6.[E][ ] dinner"));

        String reloadedList = new Crow(filePath).getResponse("list");
        assertTrue(reloadedList.contains("1.[T][ ] borrow book"));
        assertTrue(reloadedList.contains("6.[E][ ] dinner"));
    }

    @Test
    void run_completeWorkflow_updatesOutputAndStorage() throws CrowException {
        Path filePath = tempDirectory.resolve("data").resolve("crow.txt");
        String input = "todo read book\n"
                + "deadline return book /by 2/12/2019 1800\n"
                + "event meeting /from 3/12/2019 1400 /to 3/12/2019 1600\n"
                + "mark 1\n"
                + "unmark 1\n"
                + "find book\n"
                + "delete 2\n"
                + "list\n"
                + "blah\n"
                + "bye\n";
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = createUi(input, outputBytes);

        new Crow(filePath, ui).run();

        String output = outputBytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("[T][ ] read book"));
        assertTrue(output.contains("[D][ ] return book (by: Dec 02 2019 6:00PM)"));
        assertTrue(output.contains("[E][ ] meeting (from: Dec 03 2019 2:00PM"));
        assertTrue(output.contains("Noted. I've removed this task:"));
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("Error: Unknown command."));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));

        List<Task> savedTasks = new Storage(filePath).load();
        assertEquals(2, savedTasks.size());
        assertEquals("read book", savedTasks.get(0).getDescription());
        assertEquals("meeting", savedTasks.get(1).getDescription());
    }

    @Test
    void run_existingTasks_loadsAndListsTasks() throws IOException {
        Path filePath = tempDirectory.resolve("crow.txt");
        Files.writeString(filePath, "T | 1 | saved task", StandardCharsets.UTF_8);
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = createUi("list\nbye\n", outputBytes);

        new Crow(filePath, ui).run();

        assertTrue(outputBytes.toString(StandardCharsets.UTF_8).contains("1.[T][X] saved task"));
    }

    @Test
    void run_invalidSavedData_reportsErrorAndStartsEmpty() throws IOException {
        Path filePath = tempDirectory.resolve("crow.txt");
        Files.writeString(filePath, "invalid data", StandardCharsets.UTF_8);
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = createUi("list\nbye\n", outputBytes);

        new Crow(filePath, ui).run();

        String output = outputBytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Error: Invalid task type in data file."));
        assertTrue(output.contains("Here are the tasks in your list:"));
    }

    private Ui createUi(String input, ByteArrayOutputStream outputBytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        PrintStream outputStream = new PrintStream(outputBytes, true, StandardCharsets.UTF_8);
        return new Ui(inputStream, outputStream);
    }
}
