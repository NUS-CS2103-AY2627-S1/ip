package choo.gui;

import java.net.URL;

import choo.Choo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controls the main CHOO chat window defined in FXML.
 */
public class MainWindow extends AnchorPane {
    private static final Image USER_IMAGE = loadImage("/images/UserAvatar.png");
    private static final Image CHOO_IMAGE = loadImage("/images/ChooAvatar.png");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;

    private Choo choo;

    /**
     * Configures automatic scrolling after FXML fields are injected.
     */
    @FXML
    public void initialize() {
        this.scrollPane.vvalueProperty().bind(this.dialogContainer.heightProperty());
    }

    /**
     * Supplies the command engine used to answer messages.
     *
     * @param choo CHOO command engine.
     */
    public void setChoo(Choo choo) {
        this.choo = choo;
        this.dialogContainer.getChildren().add(
                DialogBox.getChooDialog(
                        "All aboard! I'm CHOO, your task conductor. "
                                + "Tell me what needs to stay on track.",
                        CHOO_IMAGE));
    }

    /**
     * Sends the current text-field content and displays both sides of the exchange.
     */
    @FXML
    private void handleUserInput() {
        String input = this.userInput.getText().trim();
        if (input.isEmpty() || this.choo == null) {
            return;
        }

        String response = this.choo.getResponse(input);
        DialogBox responseDialog = response.startsWith("OOPS!!!")
                ? DialogBox.getErrorDialog(response, CHOO_IMAGE)
                : DialogBox.getChooDialog(response, CHOO_IMAGE);
        this.dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, USER_IMAGE),
                responseDialog);
        this.userInput.clear();
        if (input.equals("bye")) {
            Platform.exit();
        }
    }

    private static Image loadImage(String resourcePath) {
        URL imageUrl = MainWindow.class.getResource(resourcePath);
        if (imageUrl == null) {
            throw new IllegalStateException("Unable to load GUI image: " + resourcePath);
        }
        return new Image(imageUrl.toExternalForm());
    }
}
