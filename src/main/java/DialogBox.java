import java.io.IOException;
import java.util.Collections;

import javafx.beans.binding.Bindings;
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
 * Displays one user or Loki message with a compact speaker label.
 */
public class DialogBox extends HBox {
    private static final double MINIMUM_MESSAGE_WIDTH = 160.0;
    private static final double MAXIMUM_MESSAGE_WIDTH = 520.0;
    private static final double MESSAGE_RESERVED_WIDTH = 72.0;

    @FXML
    private Label text;
    @FXML
    private Label speaker;
    @FXML
    private ImageView displayPicture;

    /**
     * Creates a dialog box using the reusable FXML message layout.
     *
     * @param message the message to display.
     * @param speakerName the short name of the message sender.
     * @param image the optional sender icon.
     */
    public DialogBox(String message, String speakerName, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box", exception);
        }

        text.setText(message);
        speaker.setText(speakerName);
        displayPicture.setImage(image);
        if (image == null) {
            displayPicture.setManaged(false);
            displayPicture.setVisible(false);
        } else {
            displayPicture.setClip(new Circle(14.0, 14.0, 14.0));
        }
        text.maxWidthProperty().bind(Bindings.createDoubleBinding(this::getMessageMaxWidth, widthProperty()));
        getStyleClass().add("dialog-box");
    }

    /**
     * Creates a dialog box aligned as a user message.
     *
     * @param message the user's message.
     * @return the user dialog box.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message, "YOU", null);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a dialog box aligned as a Loki response.
     *
     * @param message Loki's response.
     * @param image Loki's icon.
     * @return the Loki dialog box.
     */
    public static DialogBox getLokiDialog(String message, Image image) {
        return getLokiDialog(message, image, false);
    }

    /**
     * Creates a Loki response dialog and optionally marks it as an error.
     *
     * @param message Loki's response.
     * @param image Loki's icon.
     * @param isError whether the response represents an invalid command.
     * @return the Loki dialog box.
     */
    public static DialogBox getLokiDialog(String message, Image image, boolean isError) {
        DialogBox dialogBox = new DialogBox(message, "LOKI", image);
        dialogBox.getStyleClass().add("loki-dialog");
        if (isError) {
            dialogBox.getStyleClass().add("error-dialog");
        }
        return dialogBox;
    }

    /**
     * Flips this dialog box so the speaker label appears on the right.
     */
    private void flip() {
        setAlignment(Pos.TOP_RIGHT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
    }

    /**
     * Returns the maximum message width for the current dialog width.
     *
     * @return the responsive maximum width for the message text.
     */
    private double getMessageMaxWidth() {
        return Math.max(MINIMUM_MESSAGE_WIDTH,
                Math.min(MAXIMUM_MESSAGE_WIDTH, getWidth() - MESSAGE_RESERVED_WIDTH));
    }
}
