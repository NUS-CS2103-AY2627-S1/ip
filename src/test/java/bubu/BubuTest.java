package bubu;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import bubu.storage.Storage;

/** Tests Bubu's command orchestration and response state. */
class BubuTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void getWelcomeMessage_newBubu_returnsWelcomeText() {
        Bubu bubu = createBubu();

        String response = bubu.getWelcomeMessage();

        assertTrue(response.contains("Hello! I'm BUBU!"));
    }

    @Test
    void getResponse_unknownCommand_marksResponseAsError() {
        Bubu bubu = createBubu();

        String response = bubu.getResponse("archive");

        assertTrue(bubu.wasLastResponseAnError());
        assertTrue(response.contains("don't know"));
    }

    @Test
    void getResponse_bye_marksResponseAsExit() {
        Bubu bubu = createBubu();

        String response = bubu.getResponse("bye");

        assertTrue(response.contains("Bye"));
        assertTrue(bubu.wasLastResponseAnExit());
        assertFalse(bubu.wasLastResponseAnError());
    }

    @Test
    void getResponse_validTodo_clearsErrorStateAndAddsTask() {
        Bubu bubu = createBubu();

        String response = bubu.getResponse("todo read notes");

        assertFalse(bubu.wasLastResponseAnError());
        assertTrue(response.contains("read notes"));
    }

    @Test
    void getResponse_duplicateTodo_returnsError() {
        Bubu bubu = createBubu();
        bubu.getResponse("todo read notes");

        String response = bubu.getResponse("todo read notes");

        assertTrue(bubu.wasLastResponseAnError());
        assertTrue(response.contains("identical task"));
    }

    @Test
    void getResponse_markUnmarkAndDelete_updatesTaskList() {
        Bubu bubu = createBubu();
        bubu.getResponse("todo read notes");

        String markResponse = bubu.getResponse("mark 1");
        String unmarkResponse = bubu.getResponse("unmark 1");
        String deleteResponse = bubu.getResponse("delete 1");
        String listResponse = bubu.getResponse("list");

        assertTrue(markResponse.contains("marked"));
        assertTrue(unmarkResponse.contains("not done"));
        assertTrue(deleteResponse.contains("removed"));
        assertTrue(listResponse.contains("task list is empty"));
    }

    @Test
    void getResponse_listWithThreeTasks_includesAllTasks() {
        Bubu bubu = createBubu();
        bubu.getResponse("todo first task");
        bubu.getResponse("todo second task");
        bubu.getResponse("todo third task");

        String response = bubu.getResponse("list");

        assertTrue(response.contains("1. [T][ ] first task"));
        assertTrue(response.contains("2. [T][ ] second task"));
        assertTrue(response.contains("3. [T][ ] third task"));
    }

    private Bubu createBubu() {
        return new Bubu(new Storage(temporaryDirectory.resolve("bubu.txt")));
    }
}
