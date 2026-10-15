package wodan.gui;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import wodan.Wodan;

/**
 * Controller for the main chat window.
 */
public class MainWindow extends VBox {
    private static final String INPUT_ERROR_STYLE = "command-field-error";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;
    @FXML
    private ImageView headerPortrait;

    private Wodan wodan;

    private final Image wodanImage = new Image(
            this.getClass().getResourceAsStream("/images/wodan.png"));

    /**
     * Binds the scroll pane to the dialog list so the latest reply stays in view.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        headerPortrait.setImage(wodanImage);
        double radius = headerPortrait.getFitWidth() / 2;
        headerPortrait.setClip(new Circle(radius, radius, radius));
        Platform.runLater(() -> userInput.requestFocus());
    }

    /**
     * Connects this window to {@code wodan} and shows the greeting.
     *
     * @param wodan Chatbot that produces replies.
     */
    public void setWodan(Wodan wodan) {
        this.wodan = wodan;
        dialogContainer.getChildren().add(DialogBox.getWodanDialog(wodan.getGreeting()));
        String loadMessage = wodan.getLoadMessage();
        if (loadMessage != null) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(loadMessage));
        }
    }

    /**
     * Shows the typed command and Wodan's reply, then clears the input field.
     * Closes the window shortly after a {@code bye} command.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        String response = wodan.getResponse(input);
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input));
        if (wodan.wasError()) {
            dialogContainer.getChildren().add(DialogBox.getErrorDialog(response));
            setInputErrorState(true);
        } else {
            dialogContainer.getChildren().add(DialogBox.getWodanDialog(response));
            setInputErrorState(false);
        }
        userInput.clear();

        if (wodan.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition delay = new PauseTransition(Duration.seconds(1.2));
            delay.setOnFinished(event -> {
                Stage stage = (Stage) userInput.getScene().getWindow();
                stage.close();
            });
            delay.play();
        }
    }

    private void setInputErrorState(boolean isError) {
        userInput.getStyleClass().remove(INPUT_ERROR_STYLE);
        if (isError) {
            userInput.getStyleClass().add(INPUT_ERROR_STYLE);
        }
    }
}
