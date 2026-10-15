package luckynoslacky.luckyui.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Tests layout regions, scrolling, and responsive sizing in the main window. */
@ExtendWith(ApplicationExtension.class)
class MainWindowLayoutTest extends MainWindowTestSupport {
    private static final int SCROLL_COMMAND_COUNT = 20;

    /** Loads the production FXML window before each test. */
    @Start
    void start(Stage stage) throws IOException {
        startWindow(stage);
    }

    /** Verifies that the layout exposes stable visual integration regions. */
    @Test
    void mainWindow_layoutRegions_exposeStableIntegrationHooks(FxRobot robot) {
        VBox brandHeaderSlot = robot.lookup("#brandHeaderSlot").query();
        ScrollPane conversationRegion = robot.lookup("#scrollPane").query();
        HBox commandRow = robot.lookup("#commandRow").query();

        assertTrue(brandHeaderSlot.getStyleClass().contains(
                "brand-header-slot"));
        assertTrue(brandHeaderSlot.isManaged());
        assertTrue(brandHeaderSlot.isVisible());
        assertNotNull(robot.lookup("#brandTitle").query());
        assertTrue(conversationRegion.getStyleClass().contains(
                "conversation-region"));
        StackPane background = robot.lookup("#personalityBackground").query();
        assertTrue(background.getStyleClass().contains("personality-background"));
        assertTrue(background.getMinHeight()
                >= conversationRegion.getViewportBounds().getHeight());
        assertTrue(lookupTextField().getStyleClass().contains(
                "personality-input-field"));
        assertTrue(lookupButton().getStyleClass().contains(
                "personality-send-button"));
        assertTrue(commandRow.getStyleClass().contains("command-row"));
    }

    /** Verifies that a long conversation scrolls to its newest message. */
    @Test
    void mainWindow_manyMessages_scrollsToLatestDialogue(FxRobot robot) {
        showWindowWithChatbot(robot, new ScrollingChatbot());
        TextField inputField = robot.lookup("#userInput").query();

        for (int commandNumber = 0;
             commandNumber < SCROLL_COMMAND_COUNT;
             commandNumber++) {
            robot.clickOn(inputField).write("scroll")
                    .push(KeyCode.ENTER);
        }
        WaitForAsyncUtils.waitForFxEvents();

        ScrollPane scrollPane = robot.lookup("#scrollPane").query();
        VBox dialogueContainer = robot.lookup("#dialogContainer").query();
        assertEquals(1 + 2 * SCROLL_COMMAND_COUNT,
                dialogueContainer.getChildren().size());
        assertTrue(scrollPane.getVvalue()
                >= scrollPane.getVmax() - 0.01);
    }

    /** Verifies that widening the window expands input while preserving the button. */
    @Test
    void mainWindow_widenedWindow_expandsInputAndPreservesButtonWidth(
            FxRobot robot) {
        TextField inputField = robot.lookup("#userInput").query();
        Button sendButton = robot.lookup("#sendButton").query();

        robot.interact(() -> {
            stage.setWidth(400.0);
            stage.setHeight(600.0);
        });
        WaitForAsyncUtils.waitForFxEvents();
        double initialInputWidth = inputField.getWidth();
        double initialButtonWidth = sendButton.getWidth();

        robot.interact(() -> stage.setWidth(800.0));
        WaitForAsyncUtils.waitForFxEvents();

        assertTrue(inputField.getWidth() > initialInputWidth + 100.0);
        assertEquals(initialButtonWidth, sendButton.getWidth(), 2.0);
        assertNodeWithinScene(robot.lookup("#commandRow").query(),
                stage.getScene());
    }

    /** Verifies that controls remain inside the scene at minimum dimensions. */
    @Test
    void mainWindow_minimumWindow_keepsControlsWithinScene(FxRobot robot) {
        robot.interact(() -> {
            stage.setWidth(320.0);
            stage.setHeight(480.0);
        });
        WaitForAsyncUtils.waitForFxEvents();

        HBox inputRow = robot.lookup("#commandRow").query();
        TextField inputField = robot.lookup("#userInput").query();
        Button sendButton = robot.lookup("#sendButton").query();
        Scene scene = stage.getScene();
        assertNodeWithinScene(inputRow, scene);
        assertNodeWithinScene(inputField, scene);
        assertNodeWithinScene(sendButton, scene);
        assertTrue(inputField.getWidth() > 0.0);
        assertTrue(sendButton.getWidth() > 0.0);
    }
}
