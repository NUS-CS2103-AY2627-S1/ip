package ui;

import java.io.InputStream;

import friedberg.CommandResult;
import friedberg.Friedberg;
import friedberg.ResponseType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

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

    private Friedberg friedberg;

    private final Image userImage = loadImageOrPlaceholder("/images/DaUser.png");
    private final Image friedbergImage = loadImageOrPlaceholder("/images/DaDuke.png");

    /**
     * Keeps the newest dialog visible as the conversation grows.
     */
    @FXML
    public void initialize() {
        assert scrollPane != null : "MainWindow.fxml must inject scrollPane";
        assert dialogContainer != null : "MainWindow.fxml must inject dialogContainer";
        assert userInput != null : "MainWindow.fxml must inject userInput";
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Loads an avatar, using a transparent placeholder when the optional resource is absent.
     *
     * @param path absolute classpath location of the image
     * @return avatar or transparent placeholder
     */
    private Image loadImageOrPlaceholder(String path) {
        InputStream imageStream = this.getClass().getResourceAsStream(path);
        if (imageStream == null) {
            return new WritableImage(1, 1);
        }
        return new Image(imageStream);
    }

    /**
     * Injects the Friedberg instance used to process GUI input.
     *
     * @param friedberg shared Friedberg backend for this GUI session
     */
    public void setFriedberg(Friedberg friedberg) {
        this.friedberg = friedberg;
        dialogContainer.getChildren().add(DialogBox.getFriedbergDialog(friedberg.getGreeting(), friedbergImage,
                ResponseType.DEFAULT));
    }

    /**
     * Creates dialog boxes for the user's input and Friedberg's reply.
     * Clears input and exits JavaFX when the bye command is processed.
     */
    @FXML
    private void handleUserInput() {
        assert Platform.isFxApplicationThread()
                : "GUI input must be handled on the JavaFX Application Thread";
        assert friedberg != null : "The backend must be injected before accepting GUI input";
        String input = userInput.getText();
        CommandResult result = friedberg.processInput(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getFriedbergDialog(result.message(), friedbergImage, result.responseType())
        );
        userInput.clear();

        if (result.shouldExit()) {
            Platform.exit();
        }
    }
}
