package xian;

import java.io.IOException;
import java.time.format.DateTimeParseException;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Represents the main JavaFX window used to interact with Xian.
 */
public class MainWindow extends AnchorPane {

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private Xian xian;

    private final Image userImage =
            new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));

    private final Image xianImage =
            new Image(this.getClass().getResourceAsStream("/images/DaXian.png"));

    /**
     * Initializes the dialog container's scrolling behavior.
     */
    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Provides the Xian backend used by this controller.
     * Displays the welcome message and any startup warning, then focuses the
     * input field after setup.
     *
     * @param xian The Xian backend instance.
     */
    public void setXian(Xian xian) {
        this.xian = xian;

        dialogContainer.getChildren().add(
                DialogBox.getXianDialog(
                        xian.getWelcomeMessage(),
                        xianImage,
                        ""));

        if (!xian.getStartupWarning().isBlank()) {
            dialogContainer.getChildren().add(
                    DialogBox.getXianDialog(
                            xian.getStartupWarning(),
                            xianImage,
                            "error"));
        }

        Platform.runLater(userInput::requestFocus);
    }

    /**
     * Handles a command submitted through the input field or Send button.
     * Displays errors in a distinct response style and keeps invalid input in
     * the field so that the user can correct it.
     */
    @FXML
    private void handleUserInput() {
        assert xian != null : "Xian backend must be set before handling input";

        String input = userInput.getText();

        if (input.isBlank()) {
            dialogContainer.getChildren().add(
                    DialogBox.getXianDialog(
                            "Hmm, please enter a command before sending it.",
                            xianImage,
                            "error"));
            return;
        }

        if (input.trim().equalsIgnoreCase("bye")) {
            Platform.exit();
            return;
        }

        dialogContainer.getChildren().add(
                DialogBox.getUserDialog(input, userImage));

        try {
            String response = xian.executeCommand(input);
            dialogContainer.getChildren().add(
                    DialogBox.getXianDialog(response, xianImage, ""));
        } catch (XianException | IOException | NumberFormatException
                 | DateTimeParseException exception) {
            dialogContainer.getChildren().add(
                    DialogBox.getXianDialog(
                            "Hmm, I couldn't process that command:\n"
                                    + exception.getMessage(),
                            xianImage,
                            "error"));
            return;
        }

        userInput.clear();
    }
}
