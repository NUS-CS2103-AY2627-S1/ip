package zikiai.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import zikiai.Zikiai;

/**
 * Connects the tutorial's chat controls to a single Zikiai session.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Zikiai zikiai;
    private final Image userImage = new Image(MainWindow.class.getResourceAsStream("/images/DaUser.png"));
    private final Image zikiaiImage = new Image(MainWindow.class.getResourceAsStream("/images/DaDuke.png"));

    /**
     * Keeps new messages visible after JavaFX lays them out.
     */
    @FXML
    private void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldHeight, newHeight) ->
                Platform.runLater(() -> scrollPane.setVvalue(1.0)));
    }

    /**
     * Connects a chatbot and displays its greeting or loading error.
     *
     * @param zikiai chatbot session for this window.
     */
    public void setZikiai(Zikiai zikiai) {
        this.zikiai = zikiai;
        dialogContainer.getChildren().add(DialogBox.getZikiaiDialog(zikiai.getWelcome(), zikiaiImage));
        updateInputState();
    }

    /**
     * Sends one command, appends both messages, and clears the input.
     * Empty submissions are ignored. After bye, the farewell stays visible.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank() || !zikiai.canAcceptCommands()) {
            return;
        }
        String response = zikiai.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getZikiaiDialog(response, zikiaiImage));
        userInput.clear();
        updateInputState();
        userInput.requestFocus();
    }

    /**
     * Prevents further input after a loading error or the bye command.
     */
    private void updateInputState() {
        boolean isDisabled = !zikiai.canAcceptCommands();
        userInput.setDisable(isDisabled);
        sendButton.setDisable(isDisabled);
    }
}
