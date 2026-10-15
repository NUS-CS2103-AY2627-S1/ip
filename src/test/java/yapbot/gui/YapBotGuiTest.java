package yapbot.gui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import yapbot.storage.Storage;

public class YapBotGuiTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    public void redirectStorageToTempFile() {
        Storage.setTargetPathForTesting(tempDir.resolve("yapBot.txt"));
    }

    @AfterEach
    public void restoreRealStoragePath() {
        Storage.resetTargetPathForTesting();
    }

    @Test
    public void getResponse_todoCommand_returnsAddedConfirmation() {
        YapBotGui yapBotGui = new YapBotGui();

        String response = yapBotGui.getResponse("todo read book");

        assertTrue(response.contains("Great, I've added read book to the list"));
        assertFalse(yapBotGui.isExit());
    }

    @Test
    public void getResponse_listCommand_returnsPreviouslyAddedTasks() {
        YapBotGui yapBotGui = new YapBotGui();
        yapBotGui.getResponse("todo read book");

        String response = yapBotGui.getResponse("list");

        assertTrue(response.contains("1.[T][ ]read book"));
    }

    @Test
    public void getResponse_findCommand_returnsMatchingTasksOnly() {
        YapBotGui yapBotGui = new YapBotGui();
        yapBotGui.getResponse("todo read book");
        yapBotGui.getResponse("todo write code");

        String response = yapBotGui.getResponse("find book");

        assertTrue(response.contains("read book"));
        assertFalse(response.contains("write code"));
    }

    @Test
    public void getResponse_unrecognizedCommand_returnsErrorMessage() {
        YapBotGui yapBotGui = new YapBotGui();

        String response = yapBotGui.getResponse("gibberish");

        assertTrue(response.contains("I'm sorry, but I don't know what that means."));
        assertFalse(yapBotGui.isExit());
    }

    @Test
    public void getResponse_byeCommand_returnsGoodbyeAndSetsExit() {
        YapBotGui yapBotGui = new YapBotGui();

        String response = yapBotGui.getResponse("bye");

        assertTrue(response.contains("Nice work today! See you soon!"));
        assertTrue(yapBotGui.isExit());
    }

    @Test
    public void isExit_beforeAnyCommand_returnsFalse() {
        YapBotGui yapBotGui = new YapBotGui();

        assertFalse(yapBotGui.isExit());
    }

    @Test
    public void getGreeting_matchesConsoleWelcomeWording() {
        YapBotGui yapBotGui = new YapBotGui();

        String greeting = yapBotGui.getGreeting();

        assertTrue(greeting.contains("Hey! I'm YapBot"));
        assertTrue(greeting.contains("What are we tackling today?"));
    }
}
