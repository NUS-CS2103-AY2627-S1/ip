package yachiyo.ui;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import yachiyo.exception.ErrorCategory;

/**
 * Displays a chat message beside its sender's profile image.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String message, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(
                DialogBox.class.getResource("/view/DialogBox.fxml"),
                "Dialog box FXML resource not found."
        ));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box FXML.", e);
        }

        assert dialog != null && displayPicture != null
                : "Dialog-box controls must be injected from FXML";

        dialog.setText(message);
        displayPicture.setImage(image);
        cropProfileImageToCircle(image);
    }

    /**
     * Center-crops the profile image to a square and clips it into a circle.
     *
     * @param image profile image to crop.
     */
    private void cropProfileImageToCircle(Image image) {
        double cropSize = Math.min(image.getWidth(), image.getHeight());
        double cropX = (image.getWidth() - cropSize) / 2;
        double cropY = (image.getHeight() - cropSize) / 2;
        displayPicture.setViewport(new Rectangle2D(cropX, cropY, cropSize, cropSize));

        double radius = Math.min(displayPicture.getFitWidth(), displayPicture.getFitHeight()) / 2;
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Creates a user dialog with its profile image on the right.
     *
     * @param message message to display.
     * @param image profile image to display.
     * @return user dialog box.
     */
    public static DialogBox getUserDialog(String message, Image image) {
        return new DialogBox(message, image);
    }

    /**
     * Creates a Yachiyo dialog with its profile image on the left.
     *
     * @param message message to display.
     * @param image profile image to display.
     * @return Yachiyo dialog box.
     */
    public static DialogBox getYachiyoDialog(String message, Image image) {
        DialogBox dialogBox = new DialogBox(message, image);
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a Yachiyo error dialog formatted according to its category.
     *
     * @param message explanation of the error.
     * @param image profile image to display.
     * @param category category that determines the error format.
     * @return formatted Yachiyo error dialog box.
     */
    public static DialogBox getYachiyoErrorDialog(String message, Image image,
            ErrorCategory category) {
        assert category != null : "Error category must not be null";

        DialogBox dialogBox = getYachiyoDialog(message, image);
        dialogBox.applyErrorFormat(category);
        return dialogBox;
    }

    /**
     * Adds a visible category heading and the corresponding style to an error message.
     *
     * @param category category that determines the error format.
     */
    private void applyErrorFormat(ErrorCategory category) {
        String heading;
        String styleClass;
        switch (category) {
            case WARNING:
                heading = "Check command";
                styleClass = "warning-label";
                break;
            case INVALID_OPERATION:
                heading = "Cannot complete command";
                styleClass = "invalid-operation-label";
                break;
            case SYSTEM_ERROR:
                heading = "Storage error";
                styleClass = "system-error-label";
                break;
            default:
                throw new AssertionError("Unexpected error category: " + category);
        }

        Label errorHeading = new Label(heading);
        errorHeading.getStyleClass().add("error-heading");
        dialog.setContentDisplay(ContentDisplay.TOP);
        dialog.setGraphic(errorHeading);
        dialog.setGraphicTextGap(5.0);
        dialog.getStyleClass().add(styleClass);
    }

    /**
     * Flips the dialog box so its profile image appears on the left.
     */
    private void flip() {
        assert getChildren().size() == 2
                : "Dialog box must contain exactly a message and profile image";

        setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        dialog.getStyleClass().add("reply-label");
    }
}
