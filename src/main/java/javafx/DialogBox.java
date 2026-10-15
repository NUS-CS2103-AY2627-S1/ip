package javafx;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/** Represents one message and the display picture of its speaker. */
public class DialogBox extends HBox {
    private static final double ASSISTANT_MESSAGE_WIDTH_RATIO = 0.84;
    private static final double USER_MESSAGE_WIDTH_RATIO = 0.72;

    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load a dialog box.", exception);
        }

        dialog.setText(text);
        if (image != null) {
            displayPicture.setImage(image);
            cropDisplayPicture(image);
        }
    }

    /** Crops the display picture to a centered circle suitable for a chat avatar. */
    private void cropDisplayPicture(Image image) {
        double cropSize = Math.min(image.getWidth(), image.getHeight());
        double cropX = (image.getWidth() - cropSize) / 2;
        double cropY = (image.getHeight() - cropSize) / 2;
        displayPicture.setViewport(new Rectangle2D(cropX, cropY, cropSize, cropSize));
        displayPicture.setPreserveRatio(false);

        double radius = displayPicture.getFitWidth() / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /** Styles this dialog as a compact command entered by the user. */
    private void styleAsUserMessage() {
        getStyleClass().add("user-dialog-box");
        dialog.maxWidthProperty().bind(widthProperty().multiply(USER_MESSAGE_WIDTH_RATIO));
    }

    /** Flips and styles this dialog so that CBT's picture appears on the left. */
    private void styleAsAssistantMessage() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("assistant-dialog-box");
        dialog.getStyleClass().add("reply-label");
        dialog.maxWidthProperty().bind(widthProperty().multiply(ASSISTANT_MESSAGE_WIDTH_RATIO));
    }

    /** Adds attention-grabbing styling to an assistant response caused by invalid input. */
    private void styleAsErrorMessage() {
        getStyleClass().add("error-dialog-box");
        dialog.getStyleClass().add("error-label");
    }

    /**
     * Creates a dialog box for a message from the user.
     *
     * @param text message to display.
     * @param image user's display picture.
     * @return dialog box aligned to the right.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.styleAsUserMessage();
        return dialogBox;
    }

    /**
     * Creates a dialog box for a reply from CBT.
     *
     * @param text message to display.
     * @param image CBT's display picture.
     * @return dialog box aligned to the left.
     */
    public static DialogBox getCbtDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.styleAsAssistantMessage();
        return dialogBox;
    }

    /**
     * Creates a visually prominent error reply from CBT.
     *
     * @param text correction guidance to display.
     * @param image CBT's display picture.
     * @return error dialog aligned to the left.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = getCbtDialog(text, image);
        dialogBox.styleAsErrorMessage();
        return dialogBox;
    }
}
