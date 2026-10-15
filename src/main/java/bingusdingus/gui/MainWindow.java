package bingusdingus.gui;

import bingusdingus.BingusDingus;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/** Displays the graphical user interface for Bingus Dingus. */
public class MainWindow extends BorderPane {
    private static final Duration GOODBYE_DISPLAY_DURATION = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private BingusDingus bingusDingus;

    private final Image bingusDingusImage =
            new Image(this.getClass().getResourceAsStream("/images/BingusDingus.jpg"));

    /** Initializes the dialog scroll position binding after the FXML view is loaded. */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        userInput.textProperty().addListener((observable, oldValue, newValue) ->
                sendButton.setDisable(newValue == null || newValue.isBlank()));
        sendButton.setDisable(true);
        userInput.requestFocus();
    }

    /**
     * Sets the Bingus Dingus instance used to process user commands.
     *
     * @param bingusDingus the application instance to use.
     */
    public void setBingusDingus(BingusDingus bingusDingus) {
        this.bingusDingus = bingusDingus;
        dialogContainer.getChildren().add(
                DialogBox.getBingusDingusDialog(bingusDingus.getResponse("help"), bingusDingusImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing
     * Bingus Dingus's reply, then appends them to the dialog container.
     * Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input == null || input.isBlank()) {
            return;
        }

        String response = bingusDingus.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                DialogBox.getBingusDingusDialog(response, bingusDingusImage)
        );
        userInput.clear();

        if (bingusDingus.isByeCommand(input)) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition goodbyeDelay = new PauseTransition(GOODBYE_DISPLAY_DURATION);
            goodbyeDelay.setOnFinished(event -> Platform.exit());
            goodbyeDelay.play();
            return;
        }

        userInput.requestFocus();
    }
}
