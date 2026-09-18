package sophon.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sophon.Sophon;

public class MainWindowTest extends ApplicationTest {
    @TempDir
    private Path temporaryDirectory;

    private VBox dialogContainer;
    private TextField userInput;
    private Button sendButton;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
        Parent root = loader.load();
        MainWindow mainWindow = loader.getController();
        mainWindow.setSophon(new Sophon(temporaryDirectory.toString(), "tasks.txt"), stage);

        stage.setScene(new Scene(root));
        stage.show();
        dialogContainer = lookup("#dialogContainer").queryAs(VBox.class);
        userInput = lookup("#userInput").queryAs(TextField.class);
        sendButton = lookup("#sendButton").queryAs(Button.class);
    }

    @Test
    public void start_displaysGreeting() {
        Set<String> messages = getDisplayedMessages();
        Label greeting = (Label) dialogContainer.lookup(".message");
        greeting.getScene().getRoot().applyCss();
        greeting.getScene().getRoot().layout();

        assertEquals(1, dialogContainer.getChildren().size());
        assertTrue(messages.stream().anyMatch(message -> message.contains("Hi. I'm Sophon.")));
        assertTrue(greeting.getText().contains("What do you wish to communicate?"));
        assertEquals(Region.USE_PREF_SIZE, greeting.getMinHeight());
        assertEquals(300.0, greeting.getMaxHeight());
        assertTrue(greeting.getHeight() >= greeting.prefHeight(greeting.getWidth()));
    }

    @Test
    public void sendTodo_displaysUserInputAndResponse() {
        submitWithButton("todo read book");

        Set<String> messages = getDisplayedMessages();
        assertEquals(3, dialogContainer.getChildren().size());
        assertTrue(messages.contains("todo read book"));
        assertTrue(messages.stream().anyMatch(message -> message.contains("Recorded. A new task")));
    }

    @Test
    public void sendBlankInput_doesNotAddDialog() {
        submitWithButton("   ");

        assertEquals(1, dialogContainer.getChildren().size());
    }

    @Test
    public void pressEnter_submitsInput() {
        interact(() -> {
            userInput.setText("todo read book");
            userInput.fireEvent(new ActionEvent());
        });
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(3, dialogContainer.getChildren().size());
        assertTrue(getDisplayedMessages().contains("todo read book"));
    }

    @Test
    public void sendBye_disablesInputAndButton() {
        submitWithButton("bye");

        assertTrue(userInput.isDisabled());
        assertTrue(sendButton.isDisabled());
        assertTrue(getDisplayedMessages().stream().anyMatch(message -> message.contains("Until we meet again.")));
    }

    @Test
    public void sendTodo_createsStyledDialogsWithAvatarsAndSenderLabels() {
        submitWithButton("todo read book");

        DialogBox userDialog = (DialogBox) dialogContainer.getChildren().get(1);
        DialogBox sophonDialog = (DialogBox) dialogContainer.getChildren().get(2);

        assertTrue(userDialog.getStyleClass().contains("user-dialog"));
        assertTrue(sophonDialog.getStyleClass().contains("sophon-dialog"));
        assertEquals("YOU · TRANSMISSION", ((Label) userDialog.lookup(".sender")).getText());
        assertEquals("SOPHON · RESPONSE", ((Label) sophonDialog.lookup(".sender")).getText());
        assertNotNull(((ImageView) userDialog.lookup(".image-view")).getImage());
        assertNotNull(((ImageView) sophonDialog.lookup(".image-view")).getImage());
        assertTrue(userDialog.getChildren().get(0) instanceof VBox);
        assertTrue(userDialog.getChildren().get(1) instanceof ImageView);
        assertTrue(sophonDialog.getChildren().get(0) instanceof ImageView);
        assertTrue(sophonDialog.getChildren().get(1) instanceof VBox);
    }

    private void submitWithButton(String text) {
        interact(() -> {
            userInput.setText(text);
            sendButton.fire();
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    private Set<String> getDisplayedMessages() {
        return lookup(".label").queryAllAs(Label.class).stream()
                .map(Label::getText)
                .collect(Collectors.toSet());
    }
}
