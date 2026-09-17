package panda.gui;

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
import javafx.scene.layout.HBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import panda.ui.Ui;

/**
 * Represents a dialog box containing a speaker image and message text.
 */
public class DialogBox extends HBox {
    private static final String STYLE_APPLICATION_ERROR_DIALOG = "application-error-dialog";
    private static final String STYLE_PANDA_DIALOG = "panda-dialog";
    private static final String STYLE_PARSING_ERROR_DIALOG = "parsing-error-dialog";

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
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load the dialog box layout.", exception);
        }

        dialog.setText(text);
        displayPicture.setFill(new ImagePattern(image));
        constrainDialogWidth();
    }

    /**
     * Creates a dialog box for a message from the user.
     *
     * @param text Message to display.
     * @param image User image to display.
     * @return Dialog box for the user's message.
     */
    public static DialogBox createUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add("user-dialog");
        dialogBox.alignMessageWithUserImage();
        return dialogBox;
    }

    /**
     * Creates a correctly styled dialog box for a typed response from Panda.
     *
     * @param message Typed message to display.
     * @param image Panda image to display.
     * @return Dialog box for Panda's response.
     */
    public static DialogBox createPandaDialog(Ui.UiMessage message, Image image) {
        String styleClass = switch (message.type()) {
            case NORMAL, EXIT_CONFIRMATION -> STYLE_PANDA_DIALOG;
            case PARSING_ERROR -> STYLE_PARSING_ERROR_DIALOG;
            case APPLICATION_ERROR -> STYLE_APPLICATION_ERROR_DIALOG;
        };
        return createStyledPandaDialog(message.text(), image, styleClass);
    }

    /**
     * Creates a left-aligned Panda dialog with the requested visual style.
     *
     * @param text Message to display.
     * @param image Panda image to display.
     * @param styleClass CSS class applied to the message label.
     * @return Styled dialog box for Panda's message.
     */
    private static DialogBox createStyledPandaDialog(String text, Image image, String styleClass) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.getStyleClass().add(styleClass);
        dialogBox.flip();
        dialogBox.alignMessageWithPandaImage();
        return dialogBox;
    }

    /**
     * Expands the message within the space left after reserving the avatar and row spacing.
     */
    private void constrainDialogWidth() {
        double reservedWidth = displayPicture.getRadius() * 2
                + getSpacing()
                + getPadding().getLeft()
                + getPadding().getRight();
        dialog.maxWidthProperty().bind(Bindings.max(0,
                widthProperty().subtract(reservedWidth)));
    }

    /**
     * Right-aligns a user message beside the user image.
     */
    private void alignMessageWithUserImage() {
        dialog.setAlignment(Pos.TOP_RIGHT);
        dialog.setTextAlignment(TextAlignment.RIGHT);
    }

    /**
     * Left-aligns a Panda message beside the Panda image.
     */
    private void alignMessageWithPandaImage() {
        dialog.setAlignment(Pos.TOP_LEFT);
        dialog.setTextAlignment(TextAlignment.LEFT);
    }

    /**
     * Places the speaker image on the left and the message on the right.
     */
    private void flip() {
        ObservableList<Node> children = FXCollections.observableArrayList(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
        setAlignment(Pos.TOP_LEFT);
    }
}
