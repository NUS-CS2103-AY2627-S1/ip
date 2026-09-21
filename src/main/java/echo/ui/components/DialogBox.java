package echo.ui.components;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;

/**
 * Represents a responsive message containing an avatar and styled text.
 */
public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 52;
    private static final double MESSAGE_CHROME_WIDTH = 76;
    private static final double MINIMUM_MESSAGE_WIDTH = 120;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;
    @FXML
    private VBox messageContainer;
    @FXML
    private Label statusLabel;

    private DialogBox(String text, Image image, ObservableValue<Bounds> viewportBounds,
                      double widthRatio) {
        try {
            URL layout = MainWindow.class.getResource("/view/DialogBox.fxml");
            if (layout == null) {
                throw new IllegalStateException("The dialog-box layout is missing.");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(layout);
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load dialog box layout.", exception);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
        configureAvatar(image);
        dialog.maxWidthProperty().bind(Bindings.createDoubleBinding(() -> Math.max(
                        MINIMUM_MESSAGE_WIDTH,
                        viewportBounds.getValue().getWidth() * widthRatio - MESSAGE_CHROME_WIDTH),
                viewportBounds));
    }

    /** Crops the supplied image to a square and clips it to a circle. */
    private void configureAvatar(Image image) {
        double squareSize = Math.min(image.getWidth(), image.getHeight());
        double xOffset = (image.getWidth() - squareSize) / 2;
        double yOffset = (image.getHeight() - squareSize) / 2;
        displayPicture.setViewport(new Rectangle2D(xOffset, yOffset, squareSize, squareSize));
        displayPicture.setClip(new Circle(AVATAR_SIZE / 2, AVATAR_SIZE / 2, AVATAR_SIZE / 2));
    }

    /** Places the avatar on the left of the message. */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a right-aligned user dialog.
     *
     * @param text Message text.
     * @param image User avatar.
     * @param viewportBounds Bounds used to size the message responsively.
     * @return User dialog.
     */
    public static DialogBox getUserDialog(String text, Image image,
                                          ObservableValue<Bounds> viewportBounds) {
        DialogBox dialogBox = new DialogBox(text, image, viewportBounds, 0.70);
        dialogBox.getStyleClass().add("user-dialog");
        dialogBox.messageContainer.getStyleClass().add("user-bubble");
        return dialogBox;
    }

    /**
     * Creates a left-aligned Echo dialog.
     *
     * @param text Message text.
     * @param image Echo avatar.
     * @param viewportBounds Bounds used to size the message responsively.
     * @return Echo dialog.
     */
    public static DialogBox getEchoDialog(String text, Image image,
                                          ObservableValue<Bounds> viewportBounds) {
        DialogBox dialogBox = new DialogBox(text, image, viewportBounds, 0.85);
        dialogBox.flip();
        dialogBox.getStyleClass().add("echo-dialog");
        dialogBox.messageContainer.getStyleClass().add("echo-bubble");
        return dialogBox;
    }

    /**
     * Creates a left-aligned error dialog.
     *
     * @param text Error text.
     * @param image Echo avatar.
     * @param viewportBounds Bounds used to size the message responsively.
     * @return Error dialog.
     */
    public static DialogBox getErrorDialog(String text, Image image,
                                           ObservableValue<Bounds> viewportBounds) {
        DialogBox dialogBox = new DialogBox(text, image, viewportBounds, 0.85);
        dialogBox.flip();
        dialogBox.getStyleClass().add("echo-dialog");
        dialogBox.messageContainer.getStyleClass().add("error-bubble");
        dialogBox.statusLabel.setManaged(true);
        dialogBox.statusLabel.setVisible(true);
        return dialogBox;
    }
}

