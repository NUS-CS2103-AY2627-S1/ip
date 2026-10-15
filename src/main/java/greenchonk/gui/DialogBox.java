package greenchonk.gui;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

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
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/**
 * Represents one message and avatar in the chat history.
 */
public class DialogBox extends HBox {
    private static final String USER_AVATAR_TEXT = "YOU";
    private static final double AVATAR_RADIUS = 20.0;
    private static final double MINIMUM_BUBBLE_WIDTH = 220.0;
    private static final double BUBBLE_WIDTH_RATIO = 0.76;
    private static final double NON_BUBBLE_WIDTH = 66.0;
    private static final Image GREEN_CHONK_IMAGE = new Image(Objects.requireNonNull(
            DialogBox.class.getResourceAsStream("/images/green-chonk-transparent.png")));

    @FXML
    private Label dialog;

    @FXML
    private StackPane avatar;

    @FXML
    private ImageView avatarImage;

    @FXML
    private Label avatarText;

    private DialogBox(String text) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog layout.", exception);
        }
        dialog.setText(text);
        dialog.maxWidthProperty().bind(Bindings.max(MINIMUM_BUBBLE_WIDTH,
                widthProperty().multiply(BUBBLE_WIDTH_RATIO).subtract(NON_BUBBLE_WIDTH)));
        avatarImage.setClip(new Circle(AVATAR_RADIUS, AVATAR_RADIUS, AVATAR_RADIUS));
    }

    /**
     * Creates a right-aligned dialog for a command entered by the user.
     *
     * @param text the command to display.
     * @return the configured user dialog.
     */
    public static DialogBox getUserDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.avatarText.setText(USER_AVATAR_TEXT);
        dialogBox.avatarText.setManaged(true);
        dialogBox.avatarText.setVisible(true);
        dialogBox.avatar.getStyleClass().add("user-avatar");
        dialogBox.dialog.getStyleClass().add("user-bubble");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for Green Chonk's response.
     *
     * @param text the response to display.
     * @return the configured Green Chonk dialog.
     */
    public static DialogBox getGreenChonkDialog(String text) {
        DialogBox dialogBox = new DialogBox(text);
        dialogBox.avatarImage.setImage(GREEN_CHONK_IMAGE);
        dialogBox.avatarImage.setAccessibleText("Green Chonk mascot");
        dialogBox.avatarImage.setManaged(true);
        dialogBox.avatarImage.setVisible(true);
        dialogBox.avatar.getStyleClass().add("green-chonk-avatar");
        dialogBox.dialog.getStyleClass().add("green-chonk-bubble");
        dialogBox.flip();
        return dialogBox;
    }

    /**
     * Creates a welcoming Green Chonk dialog with a distinct introductory style.
     *
     * @param text the welcome message to display.
     * @return the configured welcome dialog.
     */
    public static DialogBox getWelcomeDialog(String text) {
        DialogBox dialogBox = getGreenChonkDialog(text);
        dialogBox.dialog.getStyleClass().add("welcome-bubble");
        return dialogBox;
    }

    /**
     * Creates a left-aligned dialog for an invalid command or failed operation.
     *
     * @param text the error response to display.
     * @return the configured error dialog.
     */
    public static DialogBox getErrorDialog(String text) {
        DialogBox dialogBox = getGreenChonkDialog(text);
        dialogBox.dialog.getStyleClass().add("error-bubble");
        return dialogBox;
    }

    /**
     * Places the avatar before the message and aligns the row to the bottom-left.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.BOTTOM_LEFT);
    }
}
