package walter.gui;

import java.io.IOException;
import java.util.Collections;

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
import walter.command.CommandCategory;

/**
 * Reusable FXML-backed message bubble for one side of a Walter conversation.
 */
public class DialogBox extends HBox {
    private static final double MAX_BUBBLE_WIDTH_RATIO = 0.72;
    private static final double MAX_READABLE_BUBBLE_WIDTH = 620.0;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    /**
     * Loads one dialog bubble and assigns its message text.
     *
     * @param text Message displayed in the bubble.
     * @param image Avatar displayed beside the bubble.
     */
    private DialogBox(String text, Image image) {
        FXMLLoader loader = new FXMLLoader(DialogBox.class.getResource("/view/DialogBox.fxml"));
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Walter could not load a dialog box.", exception);
        }
        dialog.setText(text);
        dialog.maxWidthProperty().bind(Bindings.min(
                widthProperty().multiply(MAX_BUBBLE_WIDTH_RATIO),
                MAX_READABLE_BUBBLE_WIDTH));
        displayPicture.setImage(image);
    }

    /**
     * Creates a right-aligned user message.
     *
     * @param text User input to display.
     * @param image User avatar to display.
     * @return Styled user dialog.
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Creates a left-aligned Walter response.
     *
     * @param text Walter output to display.
     * @param image Walter avatar to display.
     * @return Styled Walter dialog.
     */
    public static DialogBox getWalterDialog(String text, Image image) {
        return getWalterDialog(text, image, CommandCategory.NORMAL);
    }

    /**
     * Creates a left-aligned Walter response with a category-specific visual accent.
     *
     * @param text Walter output to display.
     * @param image Walter avatar to display.
     * @param category Command category that produced the output.
     * @return Styled Walter dialog.
     */
    public static DialogBox getWalterDialog(String text, Image image, CommandCategory category) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        dialogBox.dialog.getStyleClass().add("reply-label");
        dialogBox.applyCategoryStyle(category);
        return dialogBox;
    }

    /**
     * Flips the FXML child order so Walter's avatar appears left of the reply bubble.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Adds the CSS class corresponding to a response category.
     *
     * @param category Category associated with the response.
     */
    private void applyCategoryStyle(CommandCategory category) {
        switch (category) {
            case ADD:
                dialog.getStyleClass().add("add-label");
                break;
            case STATE_CHANGE:
                dialog.getStyleClass().add("state-change-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("delete-label");
                break;
            case ERROR:
                dialog.getStyleClass().add("error-label");
                break;
            case NORMAL:
                break;
            default:
                break;
        }
    }
}
