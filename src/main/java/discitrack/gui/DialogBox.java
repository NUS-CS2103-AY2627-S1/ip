package discitrack.gui;

import java.io.IOException;
import java.util.Objects;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Displays one message and identifies whether it came from the user or DisciTrack.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 50.0;
    private static final Image DISCITRACK_IMAGE = new Image(Objects.requireNonNull(
            DialogBox.class.getResourceAsStream("/images/discitrack-coach.png"),
            "DisciTrack coach image could not be found."));

    @FXML
    private Label dialog;

    @FXML
    private Label displayPicture;

    private DialogBox(String text) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout.", e);
        }

        dialog.setText(text);
    }

    /**
     * Creates a dialog box for a message entered by the user.
     *
     * @param text the user's message.
     * @return a user-styled dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.displayPicture.setText("YOU");
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.getChildren().setAll(dialogBox.dialog, dialogBox.displayPicture);
        return dialogBox;
    }

    /**
     * Creates a dialog box for a response from DisciTrack.
     *
     * @param text DisciTrack's response.
     * @return a DisciTrack-styled dialog box.
     */
    public static DialogBox getDisciTrackDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        ImageView coachImage = new ImageView(DISCITRACK_IMAGE);
        coachImage.setFitHeight(AVATAR_SIZE);
        coachImage.setFitWidth(AVATAR_SIZE);
        coachImage.setPreserveRatio(true);
        coachImage.setSmooth(true);
        coachImage.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));

        dialogBox.displayPicture.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        dialogBox.displayPicture.setGraphic(coachImage);
        dialogBox.getStyleClass().add("discitrack-dialog");
        return dialogBox;
    }
}
