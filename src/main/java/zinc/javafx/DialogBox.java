package zinc.javafx;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    /** The style applied to messages written by the user. */
    private static final String USER_MESSAGE_STYLE = "user-message";

    /** The style applied to messages written by Zinc. */
    private static final String ZINC_MESSAGE_STYLE = "zinc-message";

    /** The label containing the message text. */
    @FXML
    private Label messageLabel;

    /** The image identifying the message's speaker. */
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing the supplied message and speaker image.
     *
     * @param text The message to display.
     * @param image The image representing the speaker.
     */
    private DialogBox(String text, Image image, String messageStyle) {
        try {
            URL layoutUrl = Objects.requireNonNull(MainWindow.class.getResource("/view/DialogBox.fxml"),
                    "Dialog-box layout is missing");
            FXMLLoader fxmlLoader = new FXMLLoader(layoutUrl);
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog-box layout", exception);
        }

        messageLabel.setText(text);
        messageLabel.getStyleClass().add(messageStyle);
        displayPicture.setImage(image);
    }

    /**
     * Places Zinc's image on the left and its message on the right.
     */
    private void alignForZinc() {
        ObservableList<Node> dialogComponents = FXCollections.observableArrayList(getChildren());
        Collections.reverse(dialogComponents);
        getChildren().setAll(dialogComponents);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box aligned for user messages.
     *
     * @param text The message text.
     * @param image The speaker image.
     * @return The user-aligned dialog box.
     */
    public static DialogBox createUserDialog(String text, Image image) {
        return new DialogBox(text, image, USER_MESSAGE_STYLE);
    }

    /**
     * Creates a dialog box aligned for Zinc messages.
     *
     * @param text The message text.
     * @param image The speaker image.
     * @return The Zinc-aligned dialog box.
     */
    public static DialogBox createZincDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image, ZINC_MESSAGE_STYLE);
        dialogBox.alignForZinc();
        return dialogBox;
    }
}
