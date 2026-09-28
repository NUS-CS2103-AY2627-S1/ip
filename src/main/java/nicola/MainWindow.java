package nicola;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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

    private Nicola nicola;

    private Image nicolaImage = new Image(this.getClass().getResourceAsStream("/images/nicola.jpg"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /** Injects the Duke instance */
    public void setNicola(Nicola nicola) {
        this.nicola = nicola;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();

        if (input.isBlank()) {
            return;
        }

        String response = nicola.getResponse(input);
        String commandType = nicola.getCommandType();

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getNicolaDialog(
                        response,
                        nicolaImage,
                        commandType)
        );
        userInput.clear();

        if (input.equalsIgnoreCase("bye")) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
