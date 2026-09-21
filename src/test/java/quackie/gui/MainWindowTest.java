package quackie.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import quackie.Quackie;
import quackie.storage.Storage;

/**
 * Tests the JavaFX window using its real FXML controls.
 */
class MainWindowTest {
    @TempDir
    private Path temporaryDirectory;

    /** Starts the JavaFX toolkit before creating controls. */
    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        CountDownLatch startupLatch = new CountDownLatch(1);
        Platform.startup(startupLatch::countDown);
        assertTrue(startupLatch.await(10, TimeUnit.SECONDS));
    }

    /** Stops the JavaFX toolkit after all GUI tests. */
    @AfterAll
    static void stopJavaFx() {
        Platform.exit();
    }

    /** Verifies command submission, responses, validation, and session termination. */
    @Test
    void handlesBasicChatbotCommands() throws Exception {
        runOnJavaFxThread(() -> {
            FXMLLoader loader = new FXMLLoader(MainWindowTest.class.getResource("/view/MainWindow.fxml"));
            BorderPane root = loader.load();
            MainWindow controller = loader.getController();
            Quackie quackie = new Quackie(new Storage(temporaryDirectory.resolve("quackie.txt")));
            controller.setQuackie(quackie);

            TextField userInput = (TextField) loader.getNamespace().get("userInput");
            Button sendButton = (Button) loader.getNamespace().get("sendButton");
            VBox dialogContainer = (VBox) loader.getNamespace().get("dialogContainer");

            DialogBox welcomeDialog = (DialogBox) dialogContainer.getChildren().getFirst();
            Label welcomeMessage = (Label) welcomeDialog.lookup("#dialog");
            Label quackieAvatar = (Label) welcomeDialog.lookup("#avatar");
            assertTrue(welcomeMessage.getText().startsWith("Quack! I'm Quackie"));
            assertEquals("🦆", quackieAvatar.getText());

            userInput.setText("todo read book");
            sendButton.fire();
            assertEquals(3, dialogContainer.getChildren().size());
            assertTrue(getLastResponse(dialogContainer).contains("I've added this task"));

            userInput.setText("list");
            sendButton.fire();
            assertTrue(getLastResponse(dialogContainer).contains("1.[T][ ] read book"));

            userInput.setText("");
            sendButton.fire();
            assertEquals("OOPS!!! Please enter a command.", getLastResponse(dialogContainer));
            assertTrue(getLastDialog(dialogContainer).lookup(".error-bubble") != null);
            Label errorAvatar = (Label) getLastDialog(dialogContainer).lookup("#avatar");
            assertEquals("!", errorAvatar.getText());

            userInput.setText("bye");
            sendButton.fire();
            assertEquals("Bye. Hope to see you again soon!", getLastResponse(dialogContainer));
            assertTrue(userInput.isDisabled());
            assertTrue(sendButton.isDisabled());
            return null;
        });
    }

    /** Returns the text in the latest chatbot dialog. */
    private static String getLastResponse(VBox dialogContainer) {
        DialogBox dialogBox = getLastDialog(dialogContainer);
        Label response = (Label) dialogBox.lookup("#dialog");
        return response.getText();
    }

    /** Returns the latest chatbot dialog. */
    private static DialogBox getLastDialog(VBox dialogContainer) {
        int lastIndex = dialogContainer.getChildren().size() - 1;
        return (DialogBox) dialogContainer.getChildren().get(lastIndex);
    }

    /** Runs a test action on the JavaFX application thread. */
    private static <T> T runOnJavaFxThread(Callable<T> action) throws Exception {
        FutureTask<T> task = new FutureTask<>(action);
        Platform.runLater(task);
        return task.get(10, TimeUnit.SECONDS);
    }
}
