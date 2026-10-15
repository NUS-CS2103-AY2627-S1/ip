package henry.gui;

import java.io.IOException;

import henry.parser.CommandType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * Represents one chat message together with the speaker's avatar.
 */
public class DialogBox extends HBox {
    private static final double HENRY_DIALOG_HORIZONTAL_SPACE = 52.0;
    private static final double USER_DIALOG_WIDTH_RATIO = 0.78;

    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(
                MainWindow.class.getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load the dialog box layout.", e);
        }

        dialog.setText(text);
        configureDisplayPicture(image);
    }

    /**
     * Crops an image from its center and clips it into a circular avatar.
     *
     * @param image image to display.
     */
    private void configureDisplayPicture(Image image) {
        double cropSize = Math.min(image.getWidth(), image.getHeight());
        double cropX = (image.getWidth() - cropSize) / 2;
        double cropY = (image.getHeight() - cropSize) / 2;
        double radius = Math.min(displayPicture.getFitWidth(), displayPicture.getFitHeight()) / 2;

        displayPicture.setImage(image);
        displayPicture.setViewport(new Rectangle2D(cropX, cropY, cropSize, cropSize));
        displayPicture.setPreserveRatio(false);
        displayPicture.setClip(new Circle(radius, radius, radius));
    }

    /**
     * Creates a dialog displayed on the user's side of the conversation.
     *
     * @param text message text.
     * @param image user's avatar.
     * @return user dialog box.
     */
    public static DialogBox createUserDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.maxWidthProperty().bind(
                dialogBox.widthProperty().multiply(USER_DIALOG_WIDTH_RATIO));
        return dialogBox;
    }

    /**
     * Creates a dialog displayed on Henry's side of the conversation.
     *
     * @param text message text.
     * @param image Henry's avatar.
     * @param commandType command type used to style the response.
     * @return Henry dialog box.
     */
    public static DialogBox createHenryDialog(String text, Image image, CommandType commandType) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.dialog.maxWidthProperty().bind(
                dialogBox.widthProperty().subtract(HENRY_DIALOG_HORIZONTAL_SPACE));
        dialogBox.flip();
        dialogBox.changeDialogStyle(text, commandType);
        return dialogBox;
    }

    private void flip() {
        getChildren().setAll(displayPicture, dialog);
        setAlignment(Pos.TOP_LEFT);
        dialog.getStyleClass().add("reply-label");
    }

    private void changeDialogStyle(String text, CommandType commandType) {
        switch (commandType) {
            case TODO:
            case DEADLINE:
            case EVENT:
                dialog.getStyleClass().add("add-label");
                break;
            case MARK:
            case UNMARK:
                dialog.getStyleClass().add("marked-label");
                break;
            case DELETE:
                dialog.getStyleClass().add("negative-label");
                break;
            case FIND:
                String styleClass = text.startsWith("I found these matching tasks:")
                        ? "find-label"
                        : "negative-label";
                dialog.getStyleClass().add(styleClass);
                break;
            default:
                break;
        }
    }
}
