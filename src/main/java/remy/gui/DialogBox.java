package remy.gui;

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
import javafx.scene.shape.Rectangle;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    /** Portion of the dialog box width available to its message label. */
    private static final double DIALOG_LABEL_WIDTH_RATIO = 0.72;

    /** Corner radius of each profile picture in logical pixels. */
    private static final double PROFILE_PICTURE_CORNER_RADIUS = 10.0;

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
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog layout.", e);
        }

        assert dialog != null : "FXML loader must inject the dialog label";
        assert displayPicture != null : "FXML loader must inject the display picture";

        dialog.setText(text);
        dialog.maxWidthProperty()
                .bind(widthProperty()
                        .multiply(DIALOG_LABEL_WIDTH_RATIO));
        configureDisplayPicture(image);
    }

    /** Configures the profile picture to fill a rounded square without distorting its image. */
    private void configureDisplayPicture(Image image) {
        double cornerArcDiameter = PROFILE_PICTURE_CORNER_RADIUS * 2;
        Rectangle clip = new Rectangle(displayPicture.getFitWidth(), displayPicture.getFitHeight());
        clip.setArcWidth(cornerArcDiameter);
        clip.setArcHeight(cornerArcDiameter);

        displayPicture.setImage(image);
        displayPicture.setViewport(calculateSquareViewport(image.getWidth(), image.getHeight()));
        displayPicture.setClip(clip);
    }

    /**
     * Returns a centered square viewport that fits within the given image dimensions.
     *
     * @param imageWidth width of the source image
     * @param imageHeight height of the source image
     * @return centered square viewport for the source image
     */
    static Rectangle2D calculateSquareViewport(double imageWidth, double imageHeight) {
        double cropSize = Math.min(imageWidth, imageHeight);
        double cropX = (imageWidth - cropSize) / 2;
        double cropY = (imageHeight - cropSize) / 2;

        return new Rectangle2D(cropX, cropY, cropSize, cropSize);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(getChildren());
        Collections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a right-aligned dialog spoken by the user.
     *
     * @param text message to display
     * @param image profile image to display
     * @return user dialog box
     */
    public static DialogBox getUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);

        dialogBox.dialog
                .getStyleClass()
                .add("user-bubble");

        dialogBox.displayPicture
                .setAccessibleText("User");

        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog spoken by Remy.
     *
     * @param text message to display
     * @param image profile image to display
     * @return Remy dialog box
     */
    public static DialogBox getRemyDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);

        dialogBox.dialog
                .getStyleClass()
                .add("remy-bubble");

        dialogBox.displayPicture
                .setAccessibleText("Remy");

        dialogBox.flip();
        return dialogBox;
    }
}
