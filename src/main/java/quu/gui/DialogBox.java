package quu.gui;

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
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import quu.CommandType;

/**
 * Represents a message containing the speaker's picture and text.
 */
public class DialogBox extends HBox {
    /**
     * Fraction of the row's width a bubble may occupy. Capping the bubble relative to the
     * row, rather than at a fixed number of pixels, keeps the text re-wrapping as the
     * window is resized.
     */
    private static final double BUBBLE_WIDTH_RATIO = 0.72;

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
            throw new IllegalStateException("Unable to load the dialog box layout", e);
        }

        assert dialog != null && displayPicture != null
                : "DialogBox.fxml declares an fx:id for both of its children";

        dialog.setText(text);
        dialog.maxWidthProperty().bind(widthProperty().multiply(BUBBLE_WIDTH_RATIO));
        displayPicture.setImage(image);
        clipToCircle(displayPicture);
    }

    /**
     * Returns a dialog box for something the user typed.
     *
     * @param text the user's input
     * @param image the user's avatar
     * @return the dialog box
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a dialog box for Quu's reply, colored for the command category.
     *
     * @param text the reply
     * @param image Quu's avatar
     * @param commandType the category reported by {@code Quu.getCommandType()}
     * @return the dialog box
     */
    public static DialogBox getQuuDialog(String text, Image image, CommandType commandType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.changeDialogStyle(commandType);
        return dialogBox;
    }

    /**
     * Clips an avatar to a circle, so that the square source images read as profile
     * pictures rather than as pasted-in squares.
     *
     * @param imageView the avatar to clip
     */
    private static void clipToCircle(ImageView imageView) {
        double radius = imageView.getFitWidth() / 2;
        assert radius > 0 : "DialogBox.fxml gives the avatar a fixed fitWidth";
        imageView.setClip(new Circle(radius, radius, radius));
    }

    /** Flips the dialog box so that the picture is on the left. */
    private void flip() {
        ObservableList<Node> reversedChildren = FXCollections.observableArrayList(getChildren());
        Collections.reverse(reversedChildren);
        getChildren().setAll(reversedChildren);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    /**
     * Adds the style class for the given command category.
     *
     * <p>The switch is an expression over every constant of {@link CommandType}, so adding
     * a category without giving it a style here will not compile.
     *
     * @param commandType the category reported by {@code Quu.getCommandType()}
     */
    private void changeDialogStyle(CommandType commandType) {
        String styleClass = switch (commandType) {
            case ADD -> "add-label";
            case MARK -> "marked-label";
            case UNMARK -> "unmarked-label";
            case DELETE -> "delete-label";
            case UPDATE -> "update-label";
            case LIST -> "list-label";
            case FIND -> "find-label";
            case ERROR -> "error-label";
            case NONE -> null; // the opening greeting keeps the default styling
        };
        if (styleClass != null) {
            dialog.getStyleClass().add(styleClass);
        }
    }
}
