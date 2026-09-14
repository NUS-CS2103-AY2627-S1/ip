package morgan.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import morgan.Morgan;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final long EXIT_DELAY_MILLIS = 1500;
    private static final String USER_IMAGE_PATH = "/images/User.png";
    private static final String MORGAN_IMAGE_PATH = "/images/Morgan.png";

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Morgan morgan;

    private final Image userImage = new Image(getClass().getResourceAsStream(USER_IMAGE_PATH));
    private final Image morganImage = new Image(getClass().getResourceAsStream(MORGAN_IMAGE_PATH));

    /**
     * Initializes the main window.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        dialogContainer.prefWidthProperty().bind(scrollPane.widthProperty());
    }

    /** Injects the Morgan instance */
    public void setMorgan(Morgan m) {
        morgan = m;
        dialogContainer.getChildren().add(
                DialogBox.getMorganDialog("Meow~ I'm Morgan. What can I do for you?", morganImage)
        );
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Morgan's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.trim().isEmpty()) {
            return;
        }

        String response = morgan.getResponse(input);
        String commandType = morgan.getCommandType();

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMorganDialog(response, morganImage, commandType)
        );
        userInput.clear();

        if (morgan.isExit()) {
            disableInputAndScheduleExit();
        }
    }

    /**
     * Prevents further input and schedules the application to exit after displaying the goodbye message.
     */
    private void disableInputAndScheduleExit() {
        userInput.setDisable(true);
        sendButton.setDisable(true);

        Thread exitThread = new Thread(this::exitAfterDelay);
        exitThread.start();
    }

    /**
     * Waits briefly before closing the application so the user can read the goodbye message.
     */
    private void exitAfterDelay() {
        try {
            Thread.sleep(EXIT_DELAY_MILLIS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Platform.exit();
    }
}
