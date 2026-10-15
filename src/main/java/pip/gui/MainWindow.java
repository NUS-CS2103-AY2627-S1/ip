package pip.gui;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import pip.Pip;

/** Controller for Pip's main JavaFX window. */
public class MainWindow {
    @FXML private HBox header;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    @FXML private Button sendButton;
    private Pip pip;

    /** Creates the controller; FXMLLoader injects controls before calling initialize. */
    public MainWindow() {
    }

    /** Configures input bindings after FXML injection and requests focus once the scene is ready. */
    @FXML
    public void initialize() {
        header.getChildren().add(0, DialogBox.createPipAvatar());
        sendButton.disableProperty().bind(Bindings.createBooleanBinding(
                () -> userInput.getText().isBlank(), userInput.textProperty()));
        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Connects the command processor and adds the opening greeting.
     * Called once by the application after loading the FXML, before accepting input.
     *
     * @param pip The command processor used for this window's conversation.
     */
    public void setPip(Pip pip) {
        this.pip = pip;
        dialogContainer.getChildren().add(DialogBox.getPipDialog(
                "Hey there! I'm Pip, your chipmunk task buddy.\n"
                        + "Small paws. Big plans. Let's chip away at your tasks!\n\n"
                        + "Try gather read a book, stash, or sniff book.", false));
        if (!pip.getStartupWarning().isEmpty()) {
            dialogContainer.getChildren().add(DialogBox.getPipDialog(pip.getStartupWarning(), true));
        }
    }

    /**
     * Processes a nonblank command and appends both messages with the response's error status.
     * Restores input focus and scrolls after layout so the latest reply is visible.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = pip.getResponse(input);
        dialogContainer.getChildren().addAll(DialogBox.getUserDialog(input),
                DialogBox.getPipDialog(response, pip.isResponseError()));
        userInput.clear();
        userInput.requestFocus();
        // The new rows must be laid out before the scroll position can reach their bottom edge.
        Platform.runLater(() -> {
            scrollPane.applyCss();
            scrollPane.layout();
            scrollPane.setVvalue(1.0);
        });
    }
}
