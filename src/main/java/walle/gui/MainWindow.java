package walle.gui;

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
import walle.Response;
import walle.WALLE;

/**
 * Controller for the main GUI window.
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

    private WALLE walle;

    private final Image userImage = new Image(this.getClass().getResourceAsStream("/images/user.png"));
    private final Image walleImage = new Image(this.getClass().getResourceAsStream("/images/walle.png"));

    /**
     * Scrolls the scroll pane to the bottom whenever new dialog is added. This uses a
     * listener rather than a direct binding, since binding vvalueProperty would lock it
     * permanently and block the user from scrolling manually (e.g. with the mouse wheel).
     */
    @FXML
    public void initialize() {
        dialogContainer.heightProperty().addListener((observable, oldValue, newValue) -> scrollPane.setVvalue(1.0));
    }

    /**
     * Injects the WALLE instance this window talks to, and shows its greeting.
     *
     * @param walle the WALLE instance to use.
     */
    public void setWalle(WALLE walle) {
        this.walle = walle;
        dialogContainer.getChildren().add(DialogBox.getWalleDialog(walle.getGreeting(), walleImage));
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing WALLE's
     * reply, then appends them to the dialog container. Clears user input after processing.
     * If the response is WALLE's farewell (a {@code bye} command), closes the app shortly
     * after so the user has time to read it.
     */
    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        Response response = walle.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getWalleDialog(response.text(), walleImage, response.isError()));
        userInput.clear();

        if (response.isExit()) {
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(event -> Platform.exit());
            delay.play();
        }
    }

}
