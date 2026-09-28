package kotha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class KothaEngineCommandTest {
    @TempDir
    Path temporaryDirectory;
    private KothaEngine engine;

    @BeforeEach
    void setUp() {
        engine = new KothaEngine(temporaryDirectory.resolve("tasks.txt").toString(), () -> true);
    }

    @Test
    void commands_addListFindAndPersistTasks() {
        assertTrue(engine.processCommand("todo read book").contains("read book"));
        assertTrue(engine.processCommand("deadline submit report /by 16/05/2027 1159").contains("submit report"));
        assertTrue(engine.processCommand("event team meeting /from 16/5/2027 0900 /to 16/5/2027 1000")
                .contains("team meeting"));
        assertTrue(engine.processCommand("constraint return book /after exam").contains("return book"));
        assertTrue(engine.processCommand("list").contains("1. [T][ ] read book"));
        assertTrue(engine.processCommand("find REPORT").contains("submit report"));

        KothaEngine reloaded = new KothaEngine(temporaryDirectory.resolve("tasks.txt").toString(), () -> true);
        assertTrue(reloaded.processCommand("list").contains("return book"));
    }

    @Test
    void markUnmarkAndDelete_updateTheStoredList() {
        engine.processCommand("todo read book");

        assertTrue(engine.processCommand("mark 1").contains("[X]"));
        assertTrue(engine.processCommand("unmark 1").contains("[ ]"));
        assertTrue(engine.processCommand("delete 1").contains("read book"));
        assertTrue(engine.processCommand("list").contains("ledger standeth empty"));
    }

    @Test
    void invalidCommands_returnPersonaSpecificErrors() {
        assertTrue(engine.processCommand("find").contains("grant me a word"));
        assertTrue(engine.processCommand("todo").contains("speak the duty"));
        assertTrue(engine.processCommand("mark nope").contains("valid number"));
        assertTrue(engine.processCommand("delete 1").contains("no task bearing that number"));
        assertTrue(engine.processCommand("deadline task /by not-a-date").contains("write the date"));
        assertTrue(engine.processCommand("event meeting /from 2/1/2027 /to 1/1/2027")
                .contains("ending hour must follow"));
        assertTrue(engine.processCommand("constraint task /after exam").contains("task"));
    }

    @Test
    void duplicateDescriptions_areRejectedCaseInsensitively() {
        engine.processCommand("todo Read book");

        String response = engine.processCommand("todo read BOOK");

        assertTrue(response.contains("already graces"));
        assertEquals("royal", engine.getLastResponseStyle());
    }

    @Test
    void byeAndEmptyList_haveExpectedResponses() {
        assertTrue(engine.processCommand("bye").contains("Fare thee well"));
        assertTrue(engine.processCommand("list").contains("ledger standeth empty"));
    }
}
