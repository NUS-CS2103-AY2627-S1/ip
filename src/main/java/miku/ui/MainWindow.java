package miku.ui;

import java.net.URL;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import miku.Miku;
import miku.MikuResponse;

/** Controls the main JavaFX chat window for Miku. */
public class MainWindow {
    private static final Duration FAREWELL_DURATION = Duration.seconds(1);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private final Image userImage = loadImage("/images/DaUser.jpg");
    private final Image mikuImage = loadImage("/images/DaMiku.jpg");
    private Miku miku;
    private Runnable exitHandler = () -> { };

    /** Configures automatic scrolling after FXML has injected the chat controls. */
    @FXML
    public void initialize() {
        assert scrollPane != null && dialogContainer != null && userInput != null && sendButton != null
                : "Main window FXML must inject all chat controls.";
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));
        Platform.runLater(userInput::requestFocus);
    }

    /** Supplies Miku's application logic and displays its opening messages. */
    public void setMiku(Miku miku) {
        this.miku = Objects.requireNonNull(miku);
        for (MikuResponse response : miku.getStartupResponses()) {
            dialogContainer.getChildren().add(DialogBox.getMikuDialog(response.text(), response.isError(), mikuImage));
        }
    }

    /** Supplies the action that runs after Miku's farewell has been shown. */
    public void setExitHandler(Runnable exitHandler) {
        this.exitHandler = Objects.requireNonNull(exitHandler);
    }

    /** Adds user input and Miku's response to the conversation before clearing the input field. */
    @FXML
    private void handleUserInput() {
        assert miku != null : "Main window must receive Miku before handling input.";
        String input = userInput.getText();
        if (!input.isBlank()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        }
        MikuResponse response = miku.processCommand(input);
        dialogContainer.getChildren().add(DialogBox.getMikuDialog(response.text(), response.isError(), mikuImage));
        userInput.clear();
        if (miku.isExitRequested()) {
            closeAfterFarewell();
        } else {
            userInput.requestFocus();
        }
    }

    /** Disables new messages and executes the exit action after the farewell is visible. */
    private void closeAfterFarewell() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition farewellPause = new PauseTransition(FAREWELL_DURATION);
        farewellPause.setOnFinished(event -> exitHandler.run());
        farewellPause.play();
    }

    /** Loads an optional avatar without preventing chat messages when the image is unavailable. */
    private Image loadImage(String resourcePath) {
        URL imageResource = MainWindow.class.getResource(resourcePath);
        if (imageResource == null) {
            return null;
        }
        return new Image(imageResource.toExternalForm());
    }
}
