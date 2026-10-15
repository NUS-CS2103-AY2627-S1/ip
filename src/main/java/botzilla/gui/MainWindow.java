package botzilla.gui;

import botzilla.Botzilla;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controller for the main GUI window.
 */
public class MainWindow extends AnchorPane {
    private static final int GOODBYE_DELAY_SECONDS = 3;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Botzilla botzilla;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    private final Image botzillaImage = new Image(this.getClass().getResourceAsStream("/images/DaBot.png"));

    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Botzilla instance this window talks to, and displays
     * its opening greeting as the first dialog box.
     *
     * @param b the Botzilla instance to use for generating responses
     */
    public void setBotzilla(Botzilla b) {
        botzilla = b;
        dialogContainer.getChildren().add(DialogBox.getBotzillaDialog(botzilla.getGreeting(), botzillaImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other
     * containing Botzilla's reply, and appends them to the dialog
     * container. Clears the user input field afterwards.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        String response = botzilla.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBotzillaDialog(response, botzillaImage)
        );
        userInput.clear();

        if (botzilla.isExit(input)) {
            closeAfterDelay();
        }
    }

    /**
     * Leaves the goodbye message on screen for a few seconds, then closes
     * the application.
     */
    private void closeAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(GOODBYE_DELAY_SECONDS));
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }
}
