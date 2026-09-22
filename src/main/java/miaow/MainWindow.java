package miaow;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import java.net.URL;
import miaow.gui.DialogBox;

import java.io.InputStream;
import java.util.Objects;


/**
 * Mainwindow
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

    private Miaow miaow;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/Miaowuser.png"));
    private Image miaowImage = new Image(this.getClass().getResourceAsStream("/images/Miaowoiia.png"));
    private final AudioClip miaowSound = loadSound("/sounds/cat-meow.mp3");
    private final AudioClip angrySound = loadSound("/sounds/cat-angry.mp3");

    @FXML
    public void initialize() {
        dialogContainer.setFillWidth(true);
        dialogContainer.setSpacing(8);
        dialogContainer.setAlignment(Pos.TOP_LEFT);

        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(false);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        dialogContainer.minHeightProperty().bind(
                Bindings.createDoubleBinding(
                        () -> scrollPane.getViewportBounds().getHeight(),
                        scrollPane.viewportBoundsProperty()
                )
        );
    }

    /**
     * loads a sound
     * @param resourcePath
     * @return Audioclip of the sound
     */
    private static AudioClip loadSound(String resourcePath) {
        URL soundUrl = MainWindow.class.getResource(resourcePath);

        if (soundUrl == null) {
            return null;
        }

        return new AudioClip(soundUrl.toExternalForm());
    }

    private boolean isErrorResponse(String response) {
        return response.startsWith("Error:")
                || response.startsWith("Invalid")
                || response.startsWith("Please")
                || response.startsWith("Sorry");
    }

    /** Injects the Duke instance */
    public void setMiaow(Miaow miaow) {
        this.miaow = miaow;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Duke's reply and then appends them to
     * the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert userInput != null : "User input field must be injected";
        assert dialogContainer != null : "Dialog container must be injected";
        assert miaow != null : "Miaow must be injected before handling input";
        //System.out.println("HANDLE USER INPUT CALLED");

        String input = userInput.getText().trim();
        System.out.println(input);

        if (input.isEmpty()) {
            return;
        }

        String response = miaow.getResponse(input);

        if (isErrorResponse(response)) {
            if (angrySound != null) {
                angrySound.play();
            }
        } else {
            if (miaowSound != null) {
                miaowSound.play();
            }
        }

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMiaowDialog(response, miaowImage)
        );

        userInput.clear();

        Platform.runLater(() -> scrollPane.setVvalue(1.0));

        if (miaow.isExitCommand(input)) {
            Platform.exit();
        }
    }

    private static Image loadImage(String resourcePath) {
        InputStream imageStream = Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath);
        return new Image(imageStream);
    }
}