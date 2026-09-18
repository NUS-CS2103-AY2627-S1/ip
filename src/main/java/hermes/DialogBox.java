package hermes;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/**
 * Represents a dialog box consisting of a circular picture of the speaker
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private Circle displayPicture;

    private DialogBox(String text, Image image) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setFill(createCroppedPattern(image));
    }

    /**
     * Returns a fill that shows the middle of an image, cropping whichever side is longer.
     *
     * <p>An image pattern stretches to the bounds of the shape it fills, so a
     * landscape photo placed in a circle as is would be squashed. Sizing the
     * pattern to the image's own proportions and centering it keeps the photo
     * undistorted, and the circle trims the overhang.
     *
     * @param image the picture to show.
     * @return a pattern that covers a circle without distorting the image.
     */
    static ImagePattern createCroppedPattern(Image image) {
        double aspectRatio = image.getWidth() / image.getHeight();
        double width = Math.max(aspectRatio, 1);
        double height = Math.max(1 / aspectRatio, 1);
        return new ImagePattern(image, (1 - width) / 2, (1 - height) / 2, width, height, true);
    }

    /**
     * Flips the dialog box such that the picture is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Returns a dialog box holding what the user typed.
     *
     * @param text the line the user entered.
     * @param image the user's picture.
     * @return a dialog box with the picture on the right.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a dialog box holding a reply from Hermes.
     *
     * @param text the reply to show.
     * @param image Hermes's picture.
     * @return a dialog box flipped so the picture sits on the left.
     */
    public static DialogBox getHermesDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Returns a reply from Hermes styled to stand out as a problem report.
     *
     * @param text the explanation of what went wrong.
     * @param image Hermes's picture.
     * @return a dialog box laid out like any other reply but highlighted in red.
     */
    public static DialogBox getErrorDialog(String text, Image image) {
        DialogBox dialogBox = getHermesDialog(text, image);
        dialogBox.dialog.getStyleClass().add("error-label");
        return dialogBox;
    }
}
