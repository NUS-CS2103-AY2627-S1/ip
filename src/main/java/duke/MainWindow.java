package duke;

import java.io.IOException;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * The main GUI window: an AnchorPane loaded from FXML that hosts the chat and its background.
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

    private Duke duke;

    private final Image userImage = new Image(Objects.requireNonNull(
            this.getClass().getResourceAsStream("/images/DaUser.png")));
    private final Image dukeImage = new Image(Objects.requireNonNull(
            this.getClass().getResourceAsStream("/images/DaDucke.png")));

    /**
     * Loads the window layout from FXML, making this object the displayed root.
     */
    public MainWindow() {
        FXMLLoader loader = new FXMLLoader(MainWindow.class.getResource("/view/MainWindow.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the window after its FXML has loaded: binds the scroll pane to
     * auto-scroll to the newest message and applies the background image.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());

        Image background = new Image(Objects.requireNonNull(
                MainWindow.class.getResource("/images/pond_background.jpg")).toExternalForm());
        BackgroundSize backgroundSize = new BackgroundSize(
                BackgroundSize.AUTO, BackgroundSize.AUTO,
                false, false,
                false, true); // cover: fills the window and rescales as it resizes
        BackgroundImage backgroundImage = new BackgroundImage(
                background,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                backgroundSize);
        setBackground(new Background(backgroundImage));
    }

    /**
     * Injects the Duke instance and shows the welcome greeting.
     *
     * @param d the chatbot backing this window
     */
    public void setDuke(Duke d) {
        duke = d;
        showBanner();
        dialogContainer.getChildren().add(
                DialogBox.getDukeDialog(duke.getWelcome(), dukeImage));
    }

    /**
     * Shows the ASCII-art banner in a monospaced label so its characters align.
     */
    private void showBanner() {
        Label banner = new Label(Banner.getBanner());
        banner.setStyle("-fx-font-family: 'Monospaced'; -fx-font-size: 11px;");
        banner.setWrapText(false);
        banner.setMaxWidth(Double.MAX_VALUE);
        banner.setAlignment(Pos.CENTER);
        dialogContainer.getChildren().add(banner);
    }

    /**
     * Shows the user's input and Ducke's response as dialog boxes, then clears the input.
     * Closes the window a moment after a bye command.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = duke.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getDukeDialog(response, dukeImage)
        );
        userInput.clear();
        if (duke.isExit()) {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> Platform.exit());
            pause.play();
        }
    }
}
