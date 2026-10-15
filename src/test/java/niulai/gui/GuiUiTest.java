package niulai.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import niulai.NiuLai;
import niulai.model.Todo;

/** Tests the JavaFX interface's output adapter and chatbot integration boundary. */
class GuiUiTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void consumeOutput_removesConsoleFormatting() {
        GuiUi ui = new GuiUi();

        ui.showTaskAdded(new Todo("read book"), 1);

        assertEquals(
                "Got it. I've added this task:\n[T][ ] read book\nNow you have 1 tasks in the list.",
                ui.consumeOutput()
        );
        assertEquals("", ui.consumeOutput());
    }

    /** Verifies that recovery warnings identify every skipped storage line. */
    @Test
    void showRecoveryWarning_multipleIssues_reportsStableLineList() {
        GuiUi ui = new GuiUi();

        ui.showRecoveryWarning(List.of(2, 4));

        assertEquals(
                "Warning: I skipped invalid task data on lines 2, 4.\n"
                        + "The original file will be backed up before your next saved change.",
                ui.consumeOutput()
        );
    }

    @Test
    void processCommand_reusesExistingChatbotLogic() {
        GuiUi ui = new GuiUi();
        Path dataFile = temporaryDirectory.resolve("niulai.txt");
        NiuLai chatbot = new NiuLai(dataFile.toString(), ui);

        chatbot.startSession();
        ui.consumeOutput();

        assertTrue(!chatbot.processCommand("todo read book"));
        assertTrue(ui.consumeOutput().contains("I've added this task"));
        assertTrue(!chatbot.processCommand("list"));
        assertTrue(ui.consumeOutput().contains("1.[T][ ] read book"));
    }

    /** Verifies that keyword search uses the keyword mode of a find command. */
    @Test
    void processCommand_keywordFind_displaysMatchingTasks() {
        GuiUi ui = new GuiUi();
        Path dataFile = temporaryDirectory.resolve("niulai.txt");
        NiuLai chatbot = new NiuLai(dataFile.toString(), ui);

        chatbot.startSession();
        ui.consumeOutput();
        chatbot.processCommand("todo read book");
        ui.consumeOutput();

        assertTrue(!chatbot.processCommand("find read"));
        assertTrue(ui.consumeOutput().contains("1.[T][ ] read book"));
    }

    /** Verifies that undo removes the task added by the most recent mutating command. */
    @Test
    void processCommand_undoAfterAdd_removesAddedTask() {
        GuiUi ui = new GuiUi();
        Path dataFile = temporaryDirectory.resolve("niulai.txt");
        NiuLai chatbot = new NiuLai(dataFile.toString(), ui);

        chatbot.startSession();
        ui.consumeOutput();
        chatbot.processCommand("todo read book");
        ui.consumeOutput();

        chatbot.processCommand("undo");
        assertTrue(ui.consumeOutput().contains("undone"));
        chatbot.processCommand("list");

        assertTrue(!ui.consumeOutput().contains("read book"));
    }

    /** Verifies that undo reverses only the latest command and does not support a second undo. */
    @Test
    void processCommand_undoAfterMark_restoresPendingStatusOnce() {
        GuiUi ui = new GuiUi();
        Path dataFile = temporaryDirectory.resolve("niulai.txt");
        NiuLai chatbot = new NiuLai(dataFile.toString(), ui);

        chatbot.startSession();
        ui.consumeOutput();
        chatbot.processCommand("todo read book");
        ui.consumeOutput();
        chatbot.processCommand("mark 1");
        ui.consumeOutput();

        chatbot.processCommand("undo");
        assertTrue(ui.consumeOutput().contains("undone"));
        chatbot.processCommand("list");
        assertTrue(ui.consumeOutput().contains("1.[T][ ] read book"));

        chatbot.processCommand("undo");
        assertTrue(ui.consumeOutput().contains("nothing to undo"));
    }

    /** Verifies that undo restores a task deleted by the most recent mutating command. */
    @Test
    void processCommand_undoAfterDelete_restoresDeletedTask() {
        GuiUi ui = new GuiUi();
        Path dataFile = temporaryDirectory.resolve("niulai.txt");
        NiuLai chatbot = new NiuLai(dataFile.toString(), ui);

        chatbot.startSession();
        ui.consumeOutput();
        chatbot.processCommand("todo read book");
        ui.consumeOutput();
        chatbot.processCommand("todo write report");
        ui.consumeOutput();
        chatbot.processCommand("delete 1");
        ui.consumeOutput();

        chatbot.processCommand("undo");
        assertTrue(ui.consumeOutput().contains("undone"));
        chatbot.processCommand("list");
        String output = ui.consumeOutput();

        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("2.[T][ ] write report"));
    }

    /** Verifies that a read-only command leaves the most recent mutating command undoable. */
    @Test
    void processCommand_listDoesNotReplaceUndoTarget() {
        GuiUi ui = new GuiUi();
        Path dataFile = temporaryDirectory.resolve("niulai.txt");
        NiuLai chatbot = new NiuLai(dataFile.toString(), ui);

        chatbot.startSession();
        ui.consumeOutput();
        chatbot.processCommand("todo read book");
        ui.consumeOutput();
        chatbot.processCommand("list");
        ui.consumeOutput();

        chatbot.processCommand("undo");
        assertTrue(ui.consumeOutput().contains("undone"));
        chatbot.processCommand("list");

        assertTrue(!ui.consumeOutput().contains("read book"));
    }

    /** Verifies that undo without a successful mutating command produces a helpful error. */
    @Test
    void processCommand_undoWithoutHistory_showsError() {
        GuiUi ui = new GuiUi();
        Path dataFile = temporaryDirectory.resolve("niulai.txt");
        NiuLai chatbot = new NiuLai(dataFile.toString(), ui);

        chatbot.startSession();
        ui.consumeOutput();

        chatbot.processCommand("undo");

        assertTrue(ui.consumeOutput().contains("nothing to undo"));
    }
}
