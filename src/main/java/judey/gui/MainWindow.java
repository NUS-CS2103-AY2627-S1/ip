package judey.gui;

import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import javafx.util.Duration;

import judey.Judey;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Judey judey;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/UserExplorer.png"));
    private Image judeyImage = new Image(this.getClass().getResourceAsStream("/images/DaJudey.png"));

    @FXML
    public void initialize() {
        scrollPane.setVvalue(0.0);
    }

    /** Injects the Judey instance used to process commands from this window. */
    public void setJudey(Judey j) {
        judey = j;
        dialogContainer.getChildren().add(
                DialogBox.getBannerDialog(judey.getWelcomeMessage(), judeyImage)
        );
        scrollPane.setVvalue(0.0);
    }

    /** Creates the user and Judey response dialogs for a submitted command. */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = judey.getResponse(input);
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        if (input.trim().equals("help")) {
            dialogContainer.getChildren().add(DialogBox.getHelpDialog(response, judeyImage));
        } else if (DialogBox.isErrorResponse(response)) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(response, judeyImage));
        } else {
            dialogContainer.getChildren().add(DialogBox.getJudeyDialog(response, judeyImage));
        }
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
        userInput.clear();

        if (input.trim().equals("bye")) {
            scheduleWindowClose();
        }
    }

    /** Disables further input and closes the GUI after the farewell remains visible briefly. */
    private void scheduleWindowClose() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition farewellDelay = new PauseTransition(Duration.seconds(1.5));
        farewellDelay.setOnFinished(event -> {
            Scene scene = userInput.getScene();
            if (scene != null) {
                Window window = scene.getWindow();
                if (window != null) {
                    window.hide();
                }
            }
        });
        farewellDelay.play();
    }
}
