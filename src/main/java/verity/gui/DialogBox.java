package verity.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Represents one compact user command or Verity response.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 99.0;
    private static final double USER_MAXIMUM_WIDTH = 520.0;
    private static final double USER_WIDTH_RATIO = 0.75;
    private static final double VERITY_MAXIMUM_WIDTH = 680.0;
    private static final double VERITY_WIDTH_RATIO = 0.92;
    private static final Image VERITY_AVATAR = loadVerityAvatar();
    private static final Image VERITY_ERROR_AVATAR = loadAvatar(
            "/images/verity_red.jpeg", "Missing Verity error avatar resource.");

    @FXML
    private Region leadingSpacer;
    @FXML
    private VBox messageContainer;
    @FXML
    private Label speakerLabel;
    @FXML
    private Label dialog;
    @FXML
    private Region trailingSpacer;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text) {
        URL fxmlUrl = Objects.requireNonNull(
                MainWindow.class.getResource("/view/DialogBox.fxml"),
                "Missing DialogBox.fxml resource.");
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
            dialog.setText(text);
        } catch (IOException | RuntimeException exception) {
            throw new IllegalStateException(
                    "Could not load DialogBox.fxml.", exception);
        }
    }

    /**
     * Returns a right-aligned command without an avatar or speaker label.
     *
     * @param text User command.
     * @return User command dialog box.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.configureUserDialog();
        return dialogBox;
    }

    /**
     * Returns a left-aligned Verity response with semantic error styling.
     *
     * @param text Verity response.
     * @param isError Whether the response reports an error.
     * @return Verity response dialog box.
     */
    public static DialogBox getVerityDialog(String text, boolean isError) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.configureVerityDialog(isError);
        return dialogBox;
    }

    private void configureUserDialog() {
        setAlignment(Pos.TOP_RIGHT);
        getStyleClass().add("user-dialog");
        leadingSpacer.setManaged(true);
        leadingSpacer.setVisible(true);
        trailingSpacer.setManaged(false);
        trailingSpacer.setVisible(false);
        speakerLabel.setManaged(false);
        speakerLabel.setVisible(false);
        displayPicture.setManaged(false);
        displayPicture.setVisible(false);
        messageContainer.getStyleClass().add("user-message-container");
        dialog.getStyleClass().add("user-message");
        bindMaximumMessageWidth(USER_WIDTH_RATIO, USER_MAXIMUM_WIDTH);
    }

    private void configureVerityDialog(boolean isError) {
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("verity-dialog");
        leadingSpacer.setManaged(false);
        leadingSpacer.setVisible(false);
        trailingSpacer.setManaged(true);
        trailingSpacer.setVisible(true);
        configureAvatar(isError ? VERITY_ERROR_AVATAR : VERITY_AVATAR);
        messageContainer.getStyleClass().add("verity-message-container");
        dialog.getStyleClass().add("verity-message");
        bindMaximumMessageWidth(VERITY_WIDTH_RATIO, VERITY_MAXIMUM_WIDTH);

        if (isError) {
            speakerLabel.setText("VERITY · ACTION NEEDED");
            messageContainer.getStyleClass().add("error-message-container");
            speakerLabel.getStyleClass().add("error-speaker");
            dialog.getStyleClass().add("error-message");
        }
    }

    private void bindMaximumMessageWidth(double widthRatio, double maximumWidth) {
        messageContainer.maxWidthProperty().bind(Bindings.createDoubleBinding(
                () -> Math.min(getWidth() * widthRatio, maximumWidth),
                widthProperty()));
    }

    private void configureAvatar(Image avatar) {
        displayPicture.setManaged(true);
        displayPicture.setVisible(true);
        displayPicture.setImage(avatar);
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);
        displayPicture.setPreserveRatio(true);
        displayPicture.setViewport(getCenteredSquareViewport(avatar));
        displayPicture.getStyleClass().add("verity-avatar");
    }

    private static Image loadVerityAvatar() {
        return loadAvatar(
                "/images/verity_bot.png", "Missing Verity avatar resource.");
    }

    private static Image loadAvatar(String resourcePath, String errorMessage) {
        InputStream imageStream = Objects.requireNonNull(
                DialogBox.class.getResourceAsStream(resourcePath), errorMessage);
        Image image = new Image(imageStream);
        if (image.isError()) {
            throw new IllegalStateException(
                    "Invalid avatar resource " + resourcePath + ".",
                    image.getException());
        }
        return image;
    }

    private static Rectangle2D getCenteredSquareViewport(Image image) {
        double cropSize = Math.min(image.getWidth(), image.getHeight());
        double horizontalOffset = (image.getWidth() - cropSize) / 2;
        double verticalOffset = (image.getHeight() - cropSize) / 2;
        return new Rectangle2D(
                horizontalOffset, verticalOffset, cropSize, cropSize);
    }
}
