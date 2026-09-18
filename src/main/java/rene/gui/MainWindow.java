package rene.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import rene.Rene;

/**
 * Controls the main JavaFX window and forwards user commands to Rene.
 */
public class MainWindow extends AnchorPane {
    /** The proportion of the window width that a Rene card may occupy. */
    private static final double CARD_WIDTH_FRACTION = 0.72;
    /** Keeps cards from getting wider than they need to be on large windows. */
    private static final double CARD_MAX_WIDTH_PX = 560;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Rene rene;

    /**
     * Keeps the conversation scrolled to its newest dialog and clamps card
     * widths so the content responds when the window is resized. The clamp is
     * bound to the conversation container's width, so it also runs on the
     * initial layout.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.widthProperty().addListener((observable, oldWidth, newWidth) -> clampCardWidths());
    }

    /**
     * Supplies the application logic used to process commands.
     *
     * @param rene the Rene application instance.
     */
    public void setRene(Rene rene) {
        this.rene = rene;
        dialogContainer.getChildren().add(DialogBox.getReneDialog(rene.getWelcomeMessage()));
        userInput.requestFocus();
    }

    /**
     * Adds the user's command and Rene's response to the conversation.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String response = rene.getResponse(input);
        DialogBox responseBox = response.startsWith(" Apologies")
                ? DialogBox.getErrorDialog(response)
                : DialogBox.getReneDialog(response);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input),
                responseBox);
        clampCardWidths();
        userInput.clear();
        if (input.strip().equals("bye")) {
            Platform.exit();
        }
    }

    /**
     * Restricts each dialog card to a width relative to the window so long
     * responses wrap gracefully instead of forcing the window wider. Skips
     * clamping while the container width has not been laid out yet.
     */
    private void clampCardWidths() {
        double containerWidth = dialogContainer.getWidth();
        if (containerWidth <= 10) {
            return;
        }
        double maxWidth = Math.min(containerWidth * CARD_WIDTH_FRACTION, CARD_MAX_WIDTH_PX);
        for (var child : dialogContainer.getChildren()) {
            if (child instanceof DialogBox dialogBox) {
                dialogBox.setCardMaxWidth(maxWidth);
            }
        }
    }
}
