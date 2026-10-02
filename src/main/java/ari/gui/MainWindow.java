package ari.gui;

import ari.Ari;
import ari.CommandResult;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls the main chat window and passes user commands to Ari.
 */
public class MainWindow extends AnchorPane {
    private final Image userImage = new Image(
            this.getClass().getResourceAsStream("/images/User.png")
    );
    private final Image ariImage = new Image(
            this.getClass().getResourceAsStream("/images/Ari.png")
    );

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Ari ari;

    /**
     * Configures scrolling and input behavior after the FXML fields are loaded.
     */
    @FXML
    public void initialize() {
        this.scrollPane.vvalueProperty().bind(this.dialogContainer.heightProperty());
        this.sendButton.disableProperty().bind(Bindings.createBooleanBinding(() -> this.userInput.getText().isBlank(),
                this.userInput.textProperty()));
    }

    /**
     * Supplies the task manager used to answer commands and shows its greeting.
     *
     * @param ari Task manager backing this window.
     */
    public void setAri(Ari ari) {
        this.ari = ari;
        String welcomeMessage = String.format(
                "Hola, I'm Ari!%nNeed any help?%n%n%s",
                this.ari.start()
        );
        this.dialogContainer.getChildren().add(
                DialogBox.getAriDialog(welcomeMessage, this.ariImage, this.ari.isStorageProtected())
        );
        Platform.runLater(this.userInput::requestFocus);
    }

    /**
     * Displays the user's command and Ari's response, then clears the input field.
     */
    @FXML
    private void handleUserInput() {
        String input = this.userInput.getText().strip();
        if (input.isEmpty()) {
            return;
        }

        CommandResult result = this.ari.processCommand(input);
        this.dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, this.userImage),
                DialogBox.getAriDialog(result.message().stripTrailing(), this.ariImage, result.isError())
        );
        this.userInput.clear();
        this.userInput.requestFocus();
        if (result.shouldExit()) {
            Platform.exit();
        }
    }
}
