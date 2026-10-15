package luckynoslacky.luckyui.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import luckynoslacky.CommandResult;
import luckynoslacky.luckyresponse.LuckyNoQuips;

/** Tests observable command interactions and failure handling in the main window. */
@ExtendWith(ApplicationExtension.class)
class MainWindowInteractionTest extends MainWindowTestSupport {
    /** Loads the production FXML window before each test. */
    @Start
    void start(Stage stage) throws IOException {
        startWindow(stage);
    }

    /** Verifies that the window displays a user input and chatbot reply. */
    @Test
    void mainWindow_unknownCommand_displaysUserAndWarningMessages(FxRobot robot) {
        TextField inputField = robot.lookup("#userInput").query();
        robot.clickOn(inputField).write("unknown").push(KeyCode.ENTER);

        VBox dialogueContainer = robot.lookup("#dialogContainer").query();
        assertEquals(3, dialogueContainer.getChildren().size());
        DialogueBox userDialogue =
                (DialogueBox) dialogueContainer.getChildren().get(1);
        DialogueBox chatbotDialogue =
                (DialogueBox) dialogueContainer.getChildren().get(2);
        assertEquals("unknown", getMessageText(userDialogue, 0));
        assertEquals(
                LuckyNoQuips.unknownCommandMessage(),
                getMessageText(chatbotDialogue, 1));
        assertTrue(userDialogue.getStyleClass().contains(
                DialogueBox.USER_DIALOGUE_STYLE));
        assertTrue(chatbotDialogue.getStyleClass().contains(
                DialogueBox.WARNING_DIALOGUE_STYLE));
    }

    /** Verifies that the input control remains enabled after a non-exit command. */
    @Test
    void mainWindow_unknownCommand_keepsInputEnabled(FxRobot robot) {
        TextField inputField = robot.lookup("#userInput").query();
        robot.clickOn(inputField).write("unknown").push(KeyCode.ENTER);

        assertFalse(inputField.isDisabled());
        assertTrue(inputField.getText().isEmpty());
    }

    /** Verifies that clicking Send submits the command like pressing Enter. */
    @Test
    void mainWindow_sendButton_submitsCommand(FxRobot robot) {
        robot.clickOn("#userInput")
                .write("unknown")
                .clickOn("#sendButton");

        VBox dialogueContainer = robot.lookup("#dialogContainer").query();
        assertEquals(3, dialogueContainer.getChildren().size());
    }

    /** Verifies that the main controls expose the drafted accessible labels. */
    @Test
    void mainWindow_controls_exposeAccessibleLabels() {
        TextField inputField = lookupTextField();
        Button sendButton = lookupButton();
        ScrollPane conversationHistory = lookupScrollPane();

        assertEquals(
                "Lai tell me what you want me to do, then click Send",
                inputField.getAccessibleText());
        assertEquals(
                "You can also press Enter to send the command without using the mouse.",
                inputField.getAccessibleHelp());
        assertEquals("Send command", sendButton.getAccessibleText());
        assertEquals(
                "Sends the command currently entered in the command field.",
                sendButton.getAccessibleHelp());
        assertEquals(
                "Here you see the whole chat history! Just use your arrow "
                        + "or page keys to scroll up scroll down can liao",
                conversationHistory.getAccessibleText());
    }

    /** Verifies that the command field receives focus when the window opens. */
    @Test
    void mainWindow_startApplication_focusesCommandInput(FxRobot robot) {
        robot.interact(() -> assertTrue(lookupTextField().isFocused()));
    }

    /** Verifies that Tab follows the specified forward focus order. */
    @Test
    void mainWindow_tabTraversal_movesForwardThroughControls(FxRobot robot) {
        TextField inputField = lookupTextField();
        Button sendButton = lookupButton();
        ScrollPane conversationHistory = lookupScrollPane();

        robot.clickOn(inputField).push(KeyCode.TAB);
        assertTrue(sendButton.isFocused());

        robot.push(KeyCode.TAB);
        assertTrue(conversationHistory.isFocused());

        robot.push(KeyCode.TAB);
        assertTrue(inputField.isFocused());
    }

    /** Verifies that Shift+Tab follows the specified reverse focus order. */
    @Test
    void mainWindow_tabTraversal_movesBackwardThroughControls(FxRobot robot) {
        TextField inputField = lookupTextField();
        Button sendButton = lookupButton();
        ScrollPane conversationHistory = lookupScrollPane();

        robot.clickOn(inputField).push(KeyCode.TAB);
        robot.push(KeyCode.TAB);
        assertTrue(conversationHistory.isFocused());

        robot.press(KeyCode.SHIFT)
                .push(KeyCode.TAB)
                .release(KeyCode.SHIFT);
        assertTrue(sendButton.isFocused());

        robot.press(KeyCode.SHIFT)
                .push(KeyCode.TAB)
                .release(KeyCode.SHIFT);
        assertTrue(inputField.isFocused());
    }

    /** Verifies that a Send-button submission restores focus to the input. */
    @Test
    void mainWindow_sendButtonSubmission_returnsFocusToCommandInput(FxRobot robot) {
        robot.clickOn("#userInput")
                .write("unknown")
                .clickOn("#sendButton");

        assertTrue(lookupTextField().isFocused());
    }

    /** Verifies that submitting blank input leaves the conversation unchanged. */
    @Test
    void mainWindow_blankInput_doesNotAddDialogue(FxRobot robot) {
        VBox dialogueContainer = robot.lookup("#dialogContainer").query();

        robot.clickOn("#sendButton");

        assertEquals(1, dialogueContainer.getChildren().size());
    }

    /** Verifies that a parser error is displayed as a chatbot message. */
    @Test
    void mainWindow_parseErrorResponse_displaysExactReply(FxRobot robot) {
        StubChatbot chatbot = new StubChatbot(
                false,
                new CommandResult("exact parse error", false));
        showWindowWithChatbot(robot, chatbot);

        robot.clickOn("#userInput").write("trigger parse error")
                .push(KeyCode.ENTER);

        VBox dialogueContainer = robot.lookup("#dialogContainer").query();
        DialogueBox chatbotDialogue = (DialogueBox) dialogueContainer.getChildren()
                .get(dialogueContainer.getChildren().size() - 1);
        assertEquals("exact parse error", getMessageText(chatbotDialogue, 1));
        assertFalse(robot.lookup("#userInput").query().isDisabled());
    }

    /** Verifies that a save error is displayed as a chatbot message. */
    @Test
    void mainWindow_saveErrorResponse_displaysExactReply(FxRobot robot) {
        StubChatbot chatbot = new StubChatbot(
                false,
                new CommandResult(
                        LuckyNoQuips.saveErrorMessage(), false));
        showWindowWithChatbot(robot, chatbot);

        robot.clickOn("#userInput").write("trigger save error")
                .push(KeyCode.ENTER);

        VBox dialogueContainer = robot.lookup("#dialogContainer").query();
        DialogueBox chatbotDialogue = (DialogueBox) dialogueContainer.getChildren()
                .get(dialogueContainer.getChildren().size() - 1);
        assertEquals(LuckyNoQuips.saveErrorMessage(),
                getMessageText(chatbotDialogue, 1));
        assertFalse(robot.lookup("#userInput").query().isDisabled());
    }

    /** Verifies that a startup load error is displayed after the greeting. */
    @Test
    void mainWindow_loadErrorChatbot_displaysExactStartupError(FxRobot robot) {
        showWindowWithChatbot(robot, new StubChatbot(
                true,
                new CommandResult("unused response", false)));

        VBox dialogueContainer = robot.lookup("#dialogContainer").query();
        DialogueBox chatbotDialogue = (DialogueBox) dialogueContainer.getChildren()
                .get(dialogueContainer.getChildren().size() - 1);
        assertEquals(LuckyNoQuips.loadErrorMessage(),
                getMessageText(chatbotDialogue, 1));
    }

    /** Verifies that a load failure still leaves the GUI usable. */
    @Test
    void mainWindow_loadFailure_remainsInteractive(FxRobot robot) {
        showWindowWithChatbot(robot, new LoadFailingChatbot());

        TextField inputField = robot.lookup("#userInput").query();
        Button sendButton = robot.lookup("#sendButton").query();
        VBox dialogueContainer = robot.lookup("#dialogContainer").query();

        assertFalse(inputField.isDisabled());
        assertFalse(sendButton.isDisabled());
        assertEquals(2, dialogueContainer.getChildren().size());

        robot.clickOn(inputField).write("list")
                .push(KeyCode.ENTER);

        assertEquals(4, dialogueContainer.getChildren().size());
    }

    /** Verifies that a capacity error remains recoverable in the GUI. */
    @Test
    void mainWindow_taskLimit_keepsControlsEnabled(FxRobot robot) {
        showWindowWithChatbot(robot, new CapacityFullChatbot());

        TextField inputField = robot.lookup("#userInput").query();
        Button sendButton = robot.lookup("#sendButton").query();
        VBox dialogueContainer = robot.lookup("#dialogContainer").query();

        robot.clickOn(inputField).write("todo overflow")
                .push(KeyCode.ENTER);

        assertFalse(inputField.isDisabled());
        assertFalse(sendButton.isDisabled());
        assertEquals(3, dialogueContainer.getChildren().size());
        DialogueBox chatbotDialogue =
                (DialogueBox) dialogueContainer.getChildren().get(2);
        assertEquals(
                LuckyNoQuips.taskLimitMessage(),
                getMessageText(chatbotDialogue, 1));
    }

    /** Verifies that goodbye is visible before the delayed exit is triggered. */
    @Test
    void mainWindow_byeCommand_displaysGoodbyeBeforeDelayedExit(FxRobot robot) {
        RecordingExitScheduler scheduler = new RecordingExitScheduler();
        AtomicBoolean exitTriggered = new AtomicBoolean();
        Runnable exitAction = () -> exitTriggered.set(true);
        showWindowWithChatbot(robot, new GoodbyeChatbot(), scheduler, exitAction);

        robot.clickOn("#userInput").write("bye")
                .push(KeyCode.ENTER);

        VBox dialogueContainer = robot.lookup("#dialogContainer").query();
        DialogueBox chatbotDialogue =
                (DialogueBox) dialogueContainer.getChildren().get(2);
        TextField inputField = robot.lookup("#userInput").query();
        Button sendButton = robot.lookup("#sendButton").query();
        assertEquals(3, dialogueContainer.getChildren().size());
        assertEquals(
                LuckyNoQuips.goodbye(),
                getMessageText(chatbotDialogue, 1));
        assertTrue(stage.isShowing());
        assertTrue(inputField.isDisabled());
        assertTrue(sendButton.isDisabled());
        assertEquals(1, scheduler.getScheduleCount());
        assertEquals(1500.0, scheduler.getDelay().toMillis());
        assertFalse(exitTriggered.get());

        robot.interact(scheduler::trigger);
        assertTrue(exitTriggered.get());
    }

    /** Verifies that exit dependencies cannot be missing. */
    @Test
    void construct_nullExitDependencies_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->
                new MainWindow(null, () -> { }));
        assertThrows(IllegalArgumentException.class, () ->
                new MainWindow((delay, action) -> { }, null));
    }

    /** Verifies that the main window rejects a missing chatbot dependency. */
    @Test
    void setChatbot_nullChatbot_throwsIllegalArgumentException() {
        MainWindow controller = new MainWindow();

        assertThrows(IllegalArgumentException.class, () ->
                controller.setChatbot(null));
    }
}
