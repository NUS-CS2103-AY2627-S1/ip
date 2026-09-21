package topaz.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import topaz.Topaz;

/**
 * Verifies message layouts using real FXML and CSS on the JavaFX thread.
 */
class DialogBoxTest {
    @TempDir
    Path temporaryDirectory;

    @BeforeAll
    static void startToolkit() throws Exception {
        FutureTask<Void> startup = new FutureTask<>(() -> null);
        Platform.startup(startup);
        startup.get(10, TimeUnit.SECONDS);
        Platform.setImplicitExit(false);
    }

    @Test
    void mainWindow_enterAndSend_handleErrorsAndEndSession() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            String previous = System.getProperty("topaz.dataFile");
            try {
                System.setProperty("topaz.dataFile", temporaryDirectory.resolve("gui.txt").toString());
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
                AnchorPane root = loader.load();
                new Scene(root, 400, 600);
                loader.<MainWindow>getController().setTopaz(new Topaz());
                root.applyCss();
                root.layout();
                TextField input = (TextField) root.lookup("#userInput");
                Button send = (Button) root.lookup("#sendButton");
                VBox messages = (VBox) root.lookup("#dialogContainer");
                assertEquals(1, messages.getChildren().size());

                input.setText("todo read book");
                input.fireEvent(new ActionEvent());
                assertEquals(3, messages.getChildren().size());
                assertEquals("", input.getText());
                input.setText("mark 99");
                send.fire();
                assertTrue(messages.getChildren().getLast().lookup(".error-dialog") != null);
                input.setText("list");
                send.fire();
                assertTrue(messages.getChildren().getLast().lookup(".error-dialog") == null);
                Label response = (Label) messages.getChildren().getLast().lookup(".message-text");
                assertTrue(response.getText().contains("1.[T][ ] read book"));
                assertTrue(new Topaz().getResponse("list").contains("read book"));
                input.setText("bye");
                send.fire();
                assertTrue(input.isDisabled());
                assertTrue(send.isDisabled());
            } finally {
                if (previous == null) {
                    System.clearProperty("topaz.dataFile");
                } else {
                    System.setProperty("topaz.dataFile", previous);
                }
            }
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    @Test
    void dialogs_longMessages_wrapWithinConversation() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            String text = "A long task description with several words ".repeat(20);
            DialogBox user = DialogBox.getUserDialog(text);
            DialogBox response = DialogBox.getTopazDialog(text);
            VBox conversation = new VBox(user, response);
            new Scene(conversation, 400, 600);
            conversation.applyCss();
            conversation.layout();

            Label userHeading = (Label) user.lookup(".message-heading");
            Label responseHeading = (Label) response.lookup(".message-heading");
            assertFalse(userHeading.isManaged());
            assertEquals("GRONK!", responseHeading.getText());
            assertTrue(responseHeading.isVisible());
            assertTrue(user.lookup(".user-dialog").getBoundsInParent().getWidth() <= 320);
            assertTrue(response.lookup(".gronk-dialog").getBoundsInParent().getWidth() > 320);
            for (DialogBox box : new DialogBox[] {user, response}) {
                Label message = (Label) box.lookup(".message-text");
                assertEquals(text, message.getText());
                assertTrue(message.isWrapText());
                assertTrue(message.getHeight() > 50);
                assertTrue(message.getWidth() <= 400);
            }
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    @Test
    void errorDialog_failureThenNormalResponse_hasDistinctAppearance() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            DialogBox error = DialogBox.getErrorDialog("Unknown command. Try list.");
            DialogBox normal = DialogBox.getTopazDialog("Here are your tasks.");
            VBox conversation = new VBox(error, normal);
            new Scene(conversation, 400, 600);
            conversation.applyCss();
            conversation.layout();

            Label heading = (Label) error.lookup(".message-heading");
            Label message = (Label) error.lookup(".message-text");
            assertEquals("GRONK!", heading.getText());
            assertEquals(javafx.scene.paint.Color.web("#237a35"), heading.getTextFill());
            assertEquals(26, heading.getFont().getSize());
            Label errorHeading = (Label) error.lookup(".error-heading");
            assertTrue(errorHeading.isVisible());
            assertTrue(errorHeading.getText().contains("ERROR"));
            assertEquals("Unknown command. Try list.", message.getText());
            assertEquals(javafx.scene.paint.Color.web("#7a271a"), message.getTextFill());
            assertTrue(error.lookup(".error-dialog") != null);
            assertTrue(normal.lookup(".error-dialog") == null);
            assertEquals("GRONK!", ((Label) normal.lookup(".message-heading")).getText());
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

    @Test
    void response_battleCry_movesOnlyLeadingCryToHeading() throws Exception {
        FutureTask<Void> check = new FutureTask<>(() -> {
            String body = "Gronk grabbed a new task:\n   [T][ ] shout GRONK!";
            String text = "GRONK!" + System.lineSeparator() + body;
            DialogBox response = DialogBox.getTopazDialog(text);
            DialogBox user = DialogBox.getUserDialog(text);
            VBox conversation = new VBox(response, user);
            new Scene(conversation, 400, 600);
            conversation.applyCss();
            conversation.layout();

            Label heading = (Label) response.lookup(".message-heading");
            assertEquals("GRONK!", heading.getText());
            assertEquals(26, heading.getFont().getSize());
            assertEquals(javafx.scene.paint.Color.web("#237a35"), heading.getTextFill());
            assertEquals(body, ((Label) response.lookup(".message-text")).getText());
            assertEquals(text, ((Label) user.lookup(".message-text")).getText());
            assertFalse(response.lookup(".error-heading").isManaged());
            return null;
        });
        Platform.runLater(check);
        check.get(10, TimeUnit.SECONDS);
    }

}
