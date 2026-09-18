package littledaisy;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LittleDaisyTest {
    @TempDir
    Path tempDirectory;

    @Test
    void getResponse_addListFindAndError_responsesReflectCommands() {
        LittleDaisy littleDaisy = new LittleDaisy(tempDirectory.resolve("tasks.txt"));

        assertTrue(littleDaisy.getResponse("todo Finish GUI").contains("Finish GUI"));
        assertTrue(littleDaisy.getResponse("list").contains("1.[T][ ] Finish GUI"));
        assertTrue(littleDaisy.getResponse("find gui").contains("Finish GUI"));
        assertTrue(littleDaisy.getResponse("dance").startsWith("OOPS!!!"));
    }

    @Test
    void getResponse_savedTask_newInstanceLoadsTask() {
        Path dataFile = tempDirectory.resolve("tasks.txt");
        LittleDaisy firstInstance = new LittleDaisy(dataFile);
        firstInstance.getResponse("deadline submit /by 2026-09-04");

        LittleDaisy reloadedInstance = new LittleDaisy(dataFile);

        assertTrue(reloadedInstance.getResponse("list").contains("submit (by: Sep 4 2026)"));
    }

    @Test
    void getResponse_markUnmarkAndDelete_responsesReflectChanges() {
        LittleDaisy littleDaisy = new LittleDaisy(tempDirectory.resolve("tasks.txt"));
        littleDaisy.getResponse("todo Finish GUI");

        assertTrue(littleDaisy.getResponse("mark 1").contains("[T][X] Finish GUI"));
        assertTrue(littleDaisy.getResponse("unmark 1").contains("[T][ ] Finish GUI"));
        assertTrue(littleDaisy.getResponse("delete 1").contains("Now you have 0 tasks"));
    }

    @Test
    void getResponse_help_commandReferenceReturned() {
        LittleDaisy littleDaisy = new LittleDaisy(tempDirectory.resolve("tasks.txt"));

        String response = littleDaisy.getResponse("help");

        assertTrue(response.contains("todo <description>"));
        assertTrue(response.contains("deadline <description> /by <yyyy-MM-dd>"));
        assertTrue(response.contains("bye"));
    }

    @Test
    void getResponse_duplicateTask_errorReturnedAndListUnchanged() {
        LittleDaisy littleDaisy = new LittleDaisy(tempDirectory.resolve("tasks.txt"));
        littleDaisy.getResponse("todo Read book");

        String response = littleDaisy.getResponse("todo read BOOK");

        assertTrue(response.startsWith("OOPS!!!"));
        assertTrue(response.contains("already in your list"));
        assertTrue(littleDaisy.getResponse("list").contains("1.[T][ ] Read book"));
    }
}
