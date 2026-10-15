package windy.ui;

import java.io.IOException;
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
import javafx.scene.shape.Circle;

/**
 * Displays one message together with its speaker's profile picture.
 */
public class DialogBox extends HBox {

    private static final double PROFILE_PICTURE_RADIUS = 22.0;
    private static final double MESSAGE_WIDTH_RESERVE = 76.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box containing the given message and profile picture.
     *
     * @param message message to display.
     * @param image profile picture to display.
     */
    private DialogBox(String message, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(
                DialogBox.class.getResource("/view/DialogBox.fxml"),
                "Dialog box FXML resource not found"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load dialog box FXML", exception);
        }

        assert dialog != null : "Dialog label should be injected by FXML";
        assert displayPicture != null : "Display picture should be injected by FXML";
        dialog.setText(message);
        displayPicture.setImage(image);
        displayPicture.setClip(new Circle(PROFILE_PICTURE_RADIUS, PROFILE_PICTURE_RADIUS,
                PROFILE_PICTURE_RADIUS));
        dialog.maxWidthProperty().bind(widthProperty().subtract(MESSAGE_WIDTH_RESERVE));
    }

    /**
     * Creates a right-aligned dialog for a user message.
     *
     * @param message message to display.
     * @param image user's profile picture.
     * @return dialog box for the user.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.dialog.getStyleClass().add("user-label");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for a Windy response.
     *
     * @param message message to display.
     * @param image Windy's profile picture.
     * @return dialog box for Windy.
     */
    public static DialogBox getWindyDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a left-aligned welcome dialog using an ASCII-compatible font.
     *
     * @param message welcome message to display.
     * @param image Windy's profile picture.
     * @return welcome dialog box for Windy.
     */
    public static DialogBox getWelcomeDialog(String message, Image image) {
        DialogBox dialogBox = getWindyDialog(message, image);
        dialogBox.dialog.getStyleClass().add("welcome-label");
        return dialogBox;
    }

    /**
     * Places the profile picture on the left and the message on the right.
     */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        FXCollections.reverse(children);
        getChildren().setAll(children);
        dialog.getStyleClass().add("reply-label");
    }
}
