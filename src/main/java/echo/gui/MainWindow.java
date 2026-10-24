package echo.gui;

import echo.Echo;
import echo.parser.Parser;
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
 * Controller for {@code MainWindow.fxml}: turns each Enter/Send into one
 * round trip through {@link Echo#getResponse(String)}, and renders the
 * result as a pair of chat bubbles.
 */
public class MainWindow extends AnchorPane {
    /** How long to show Echo's farewell bubble before the window closes. */
    private static final Duration EXIT_DELAY = Duration.seconds(1.2);

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Echo echo;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.jpeg"));
    private final Image echoImage = new Image(this.getClass().getResourceAsStream("/images/echo.jpeg"));

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Injects the Echo instance this window talks to, and shows Echo's
     * opening greeting as the first bubble.
     */
    public void setEcho(Echo echo) {
        this.echo = echo;
        dialogContainer.getChildren().add(
                DialogBox.getEchoDialog("Hello! I'm Echo. Toss a task my way and I'll make sure "
                        + "it comes back around when you need it.", echoImage, ""));
    }

    /**
     * Sends the text field's contents to Echo, shows the exchange as a
     * pair of dialog boxes, then clears the field. A blank input is
     * ignored rather than sent, matching what pressing Enter on an
     * empty console line does. Typing {@code bye} closes the window
     * shortly after showing Echo's farewell, the same as the console UI.
     */
    @FXML
    private void handleUserInput() {
        // Main.start() always calls setEcho() right after loading the FXML
        // and before showing the stage, so by the time a user can type into
        // this window and fire this handler, echo must already be set.
        assert echo != null : "echo should be injected via setEcho() before the window is shown";

        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }

        String commandWord = Parser.getCommandWord(input.trim());
        String response = echo.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getEchoDialog(response, echoImage, commandWord));
        userInput.clear();

        if (input.trim().equals("bye")) {
            PauseTransition delay = new PauseTransition(EXIT_DELAY);
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }
}
