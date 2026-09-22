package nico;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Represents one message displayed in the conversation.
 */
public class DialogBox extends HBox {
    private static final double MAX_BUBBLE_WIDTH_RATIO = 0.72;
    private static final int BUBBLE_HORIZONTAL_INSET = 32;
    private static final double PROFILE_IMAGE_SIZE = 42;
    private static final double PROFILE_FRAME_SIZE = 48;
    private static final double PROFILE_CROP_X_RATIO = 0.22;
    private static final double PROFILE_CROP_Y_RATIO = 0.14;
    private static final double PROFILE_CROP_SIZE_RATIO = 0.50;
    private static final Image NICO_PROFILE_IMAGE = new Image(
            DialogBox.class.getResourceAsStream("/images/andremsdesign-robot-8449206.jpg"));

    /**
     * Creates a dialog box containing the supplied message.
     */
    public DialogBox(String message) {
        Label text = new Label(message);
        text.setWrapText(true);
        text.maxWidthProperty().bind(widthProperty().multiply(MAX_BUBBLE_WIDTH_RATIO)
                .subtract(BUBBLE_HORIZONTAL_INSET));
        text.getStyleClass().add("message-bubble");
        getStyleClass().add("dialog");
        this.getChildren().add(text);
    }

    /**
     * Adds Nico's profile picture and aligns the dialog box on the left.
     */
    private void showAsNicoDialog() {
        getChildren().add(0, createNicoProfilePicture());
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a circular crop that keeps Nico's face recognizable at chat-avatar size.
     */
    private StackPane createNicoProfilePicture() {
        double cropSize = NICO_PROFILE_IMAGE.getWidth() * PROFILE_CROP_SIZE_RATIO;
        ImageView profilePicture = new ImageView(NICO_PROFILE_IMAGE);
        profilePicture.setFitWidth(PROFILE_IMAGE_SIZE);
        profilePicture.setFitHeight(PROFILE_IMAGE_SIZE);
        profilePicture.setViewport(new Rectangle2D(
                NICO_PROFILE_IMAGE.getWidth() * PROFILE_CROP_X_RATIO,
                NICO_PROFILE_IMAGE.getHeight() * PROFILE_CROP_Y_RATIO,
                cropSize,
                cropSize));
        profilePicture.setClip(new Circle(
                PROFILE_IMAGE_SIZE / 2,
                PROFILE_IMAGE_SIZE / 2,
                PROFILE_IMAGE_SIZE / 2));
        profilePicture.setAccessibleText("Nico's profile picture");

        StackPane profileFrame = new StackPane(profilePicture);
        profileFrame.setMinSize(PROFILE_FRAME_SIZE, PROFILE_FRAME_SIZE);
        profileFrame.setPrefSize(PROFILE_FRAME_SIZE, PROFILE_FRAME_SIZE);
        profileFrame.setMaxSize(PROFILE_FRAME_SIZE, PROFILE_FRAME_SIZE);
        profileFrame.getStyleClass().add("nico-profile-frame");
        return profileFrame;
    }

    /**
     * Creates a right-aligned dialog box for a user message.
     */
    public static DialogBox getUserDialog(String message) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        dialogBox.getStyleClass().add("user-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog box for a Nico message.
     */
    public static DialogBox getNicoDialog(String message) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.showAsNicoDialog();
        dialogBox.getStyleClass().add("nico-dialog");
        return dialogBox;
    }

    /**
     * Creates a left-aligned, visually distinct dialog box for an error message.
     */
    public static DialogBox getErrorDialog(String message) {
        DialogBox dialogBox = new DialogBox(message);
        dialogBox.showAsNicoDialog();
        dialogBox.getStyleClass().add("error-dialog");
        return dialogBox;
    }
}
